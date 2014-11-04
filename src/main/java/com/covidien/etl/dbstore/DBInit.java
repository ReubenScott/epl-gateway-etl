package com.covidien.etl.dbstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.covidien.etl.csvreader.DeviceType2SkuReader;
import com.covidien.etl.csvreader.SNRuleReader;
import com.covidien.etl.model.DeviceType2Sku;
import com.covidien.etl.model.SNRule;

/**
 * @ClassName: DBInit
 * @Description:
 */
public class DBInit {
    /**
     * Define a log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(DBInit.class);
    /**
     * Connection.
     */
    private Connection conn;
    /**
     * @Title: DBInit
     * @Description:
     * @throws Exception
     */
    public DBInit() throws Exception {
        this.conn = DBConnection.getInstance().getConnection();
        this.conn.setAutoCommit(false);
    }
    /**
     * @Title: insertWithReturnKey
     * @Description:
     * @param con
     * con
     * @param sql
     * sql
     * @return String
     */
    public final String insertWithReturnKey(final Connection con,
            final String sql) {
        String key = null;
        ResultSet result = null;
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.executeUpdate();
            result = pstmt.getGeneratedKeys();
            while (result.next()) {
                key = result.getString(1);
            }
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                result.close();
                pstmt.close();
            } catch (Exception e) {
                LOGGER.error("Exception", e);
            }
        }
        return key;
    }
    /**
     * @Title: queryWithReturnSet
     * @Description:
     * @param conn
     * conn
     * @param sql
     * sql
     * @return Set<String>
     * @throws SQLException
     */
    public final Set<String> queryWithReturnSet(final Connection conn,
            final String sql) throws SQLException {

        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery(sql);
        Set<String> returnSet = new HashSet<String>();
        while (result.next()) {
            returnSet.add(result.getString(1));
        }
        result.close();
        stmt.close();

        return returnSet;

    }
    /**
     * @Title: query
     * @Description:
     * @param conn
     * conn
     * @param sql
     * sql
     * @return String
     * @throws SQLException
     */
    public final String query(final Connection conn, final String sql)
            throws SQLException {
        String returnValue = null;
        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery(sql);
        while (result.next()) {
            returnValue = result.getString(1);
            break;
        }
        result.close();
        stmt.close();
        return returnValue;
    }
    /**
     * @Title: update
     * @Description:
     * @param conn
     * conn
     * @param sql
     * sql
     * @throws Exception
     */
    public final void update(final Connection conn, final String sql)
            throws Exception {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
    }
    /**
     * @Title: snInit
     * @Description:
     * @throws Exception
     */
    public final void snInit() throws Exception {
        SNRuleReader reader = new SNRuleReader();
        List<SNRule> list = reader.readCSVFile();

        Set<String> nidSet = queryWithReturnSet(conn,
                "select nid from content_type_devicetype");

        for (SNRule item : list) {

            String deviceType = item.getDeviceType();
            String ruleRegx = item.getSnRegx();

            String nodeNid = query(conn,
                    "select nid from node where type='devicetype' and title='"
                            + deviceType + "'");
            String nodeVid;

            if (nodeNid != null) {

                if (nidSet.contains(nodeNid)) {
                    update(conn,
                            "update content_type_devicetype set field_serial_number_regex_value='"
                                    + ruleRegx + "' where nid = '" + nodeNid
                                    + "'");
                } else {
                    insertWithReturnKey(
                            conn,
                            "insert into content_type_devicetype (nid,vid,field_serial_number_regex_value)values('"
                                    + nodeNid
                                    + "','"
                                    + nodeNid
                                    + "','"
                                    + ruleRegx + "')");
                }

            } else {
                nodeVid = insertWithReturnKey(
                        conn,
                        "insert into node_revisions (title,uid,body,teaser,log,timestamp,format)values('"
                                + deviceType
                                + "',0,'','','',UNIX_TIMESTAMP(NOW()),0)");
                nodeNid = insertWithReturnKey(conn,
                        "insert into node (title,type)values('" + deviceType
                                + "','devicetype')");
                update(conn, "update node set vid='" + nodeVid
                        + "' where nid='" + nodeNid + "'");
                update(conn, "update node_revisions set nid='" + nodeNid
                        + "' where vid='" + nodeVid + "'");
                insertWithReturnKey(
                        conn,
                        "insert into content_type_devicetype (nid,vid,field_serial_number_regex_value)values('"
                                + nodeNid
                                + "','"
                                + nodeVid
                                + "','"
                                + ruleRegx
                                + "')");
            }
        }
        conn.commit();
    }
    /**
     * @Title: skuInit
     * @Description:
     * @throws Exception
     */
    public final void skuInit() throws Exception {
        DeviceType2SkuReader reader = new DeviceType2SkuReader();
        List<DeviceType2Sku> list = reader.readCSVFile();
        for (DeviceType2Sku item : list) {

            String deviceType = item.getDeviceType();
            String sourceSystem = item.getSourceSystem();
            String[] skus = item.getSkus().split(";");
            String nodeVid;

            String deviceTypeNid = query(
                    conn,
                    "select a.nid from content_type_devicetype a,node b where a.nid=b.nid and b.title='"
                            + deviceType + "'");

            if (deviceTypeNid == null) {
                String nodeNid = query(conn,
                        "select nid from node a where a.title='" + deviceType
                                + "' and type='devicetype'");

                if (nodeNid == null) {
                    nodeVid = insertWithReturnKey(
                            conn,
                            "insert into node_revisions (title,uid,body,teaser,log,timestamp,format)values('"
                                    + deviceType
                                    + "',0,'','','',UNIX_TIMESTAMP(NOW()),0)");
                    nodeNid = insertWithReturnKey(conn,
                            "insert into node (title,type)values('"
                                    + deviceType + "','devicetype')");
                    update(conn, "update node set vid='" + nodeVid
                            + "' where nid='" + nodeNid + "'");
                    update(conn, "update node_revisions set nid='" + nodeNid
                            + "' where vid='" + nodeVid + "'");
                    insertWithReturnKey(
                            conn,
                            "insert into content_type_devicetype (nid,vid,field_serial_number_regex_value)values('"
                                    + nodeNid + "','" + nodeVid + "','')");
                    deviceTypeNid = nodeNid;

                }

            }

            for (String sku : skus) {

                String nodeNid = query(conn,
                        "select nid from content_type_sku a where a.field_sku_id_value='"
                                + sku + "' and field_source_system_value='"
                                + sourceSystem + "'");

                if (nodeNid == null) {

                    nodeNid = query(conn,
                            "select nid from node a where a.title='" + sku
                                    + "' and type='sku'");

                    if (nodeNid == null) {
                        nodeVid = insertWithReturnKey(
                                conn,
                                "insert into node_revisions (title,uid,body,teaser,log,timestamp,format)values('"
                                        + sku
                                        + "',0,'','','',UNIX_TIMESTAMP(NOW()),0)");
                        nodeNid = insertWithReturnKey(conn,
                                "insert into node (title,type)values('" + sku
                                        + "','sku')");
                        update(conn, "update node set vid='" + nodeVid
                                + "' where nid='" + nodeNid + "'");
                        update(conn, "update node_revisions set nid='"
                                + nodeNid + "' where vid='" + nodeVid + "'");
                    } else {
                        nodeVid = query(conn,
                                "select vid from node a where a.title='" + sku
                                        + "' and type='sku'");
                    }

                    update(conn,
                            "insert into content_type_sku(nid,vid,field_sku_id_value,field_source_system_value,field_device_type_pk_nid)values("
                                    + nodeNid
                                    + ","
                                    + nodeVid
                                    + ",'"
                                    + sku
                                    + "','"
                                    + sourceSystem
                                    + "',"
                                    + deviceTypeNid + ")");
                }

            }
        }
        conn.commit();
    }

}
