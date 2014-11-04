package com.covidien.etl.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.covidien.etl.dbstore.DBConnection;
import com.covidien.etl.model.Device;

/**
 * @ClassName: SNDAO
 * @Description:
 */
public final class SNDAO {
    /**
     * Define a db connection.
     */
    private DBConnection dbConnection;

    /**
     * @Title: getDbConnection
     * @Description:
     * @return DBConnection
     */
    public DBConnection getDbConnection() {
        return dbConnection;
    }

    /**
     * Define a log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(SNDAO.class);
    /**
     * Define a database connection.
     */
    private static Connection con = null;
    /**
     * Define a database connection.
     */
    private static PreparedStatement pstmt = null;
    /**
     * Define a map to store query result.
     */
    private static volatile Map<String, String> snRuleMap = new HashMap<String, String>();

    /**
     * @Title: SNDAO
     */
    public SNDAO() {
        dbConnection = DBConnection.getInstance();
        try {
            con = dbConnection.getConnection();
            con.setAutoCommit(false);
            pstmt = con.prepareStatement("select field_serial_number_regex_value from content_type_devicetype "
                    + "where nid in (select sku.field_device_type_pk_nid from content_type_sku sku "
                    + "where sku.field_sku_id_value =? and  sku.field_source_system_value =?)");
        } catch (SQLException e) {
            LOGGER.error("Exception:" + e);
        }
    }

    /**
     * @Title: getRuleByDevice
     * @param device
     *        device
     * @return String
     * @throws SQLException
     *         SQLException
     */
    public String getRuleByDevice(final Device device)
        throws SQLException {
        String key = device.getSku() + ":" + device.getSourceSystem();
        if (snRuleMap.size() > 0 && snRuleMap.get(key) != null) {
            return snRuleMap.get(key);
        } else {

            pstmt.setString(1, device.getSku());
            pstmt.setString(2, device.getSourceSystem());
            ResultSet result = pstmt.executeQuery();

            while (result.next()) {
                String rule = result.getString(1);
                snRuleMap.put(key, rule);
                return rule;
            }
            result.close();
        }
        return null;
    }
}
