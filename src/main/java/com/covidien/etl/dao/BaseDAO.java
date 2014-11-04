package com.covidien.etl.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.covidien.etl.common.EtlType;
import com.covidien.etl.dbstore.DBConnection;
import com.covidien.etl.dbstore.DBSetup;

/**
 * @ClassName: BaseDAO
 * @Description:
 * @param <T>
 */
public abstract class BaseDAO<T> {
    /**
     * Define a db connection.
     */
    private DBConnection dbConnection;
    /**
     * @Title: getDbConnection
     * @Description:
     * @return DBConnection
     * @throws
     */
    public final DBConnection getDbConnection() {
        return dbConnection;
    }
    /**
     * Define a log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(BaseDAO.class);
    /**
     * Define the mail sender.
     */
    private final String etlUSER = "etl.admin@covidien.com";
    /**
     * @Title: BaseDAO
     * @Description:
     */
    public BaseDAO() {
        dbConnection = DBConnection.getInstance();
        try {
            dbConnection.getConnection().setAutoCommit(false);
            createXREFTable(dbConnection.getConnection());
        } catch (SQLException e) {
            LOGGER.error("Exception:" + e);
        }
    }
    /**
     * @Title: getEtlUser
     * @Description:
     * @return String
     * @throws
     */
    public final String getEtlUser() {
        return etlUSER;
    }
    /**
     * @Title: process
     * @Description:
     * @param list
     * list
     * @throws
     */
    public abstract void process(List<T> list);
    /**
     * @Title: formateDate
     * @Description:
     * @param strDate
     * strDate
     * @throws ParseException
     * @return long
     */
    protected final long formateDate(final String strDate)
            throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = df.parse(strDate);
        return date.getTime();
    }
    /**
     * @Title: shouldChange
     * @Description:
     * @param stmt
     * stmt
     * @param type
     * type
     * @param psId
     * psId
     * @param date
     * date
     * @return boolean
     * @throws SQLException
     */
    public final boolean shouldChange(final Statement stmt, final EtlType type,
            final String psId, final long date) throws SQLException {
        ResultSet result = null;
        try {
            result = stmt
                    .executeQuery("select ps_last_change_time from xref where type="
                            + type.getValue() + " and ps_id='" + psId + "'");
            while (result.next()) {
                long updateTime = result.getLong("ps_last_change_time");
                if (updateTime < date) {
                    stmt.executeUpdate("update xref set ps_last_change_time="
                            + date + ",updated_time=UNIX_TIMESTAMP(NOW())"
                            + " where type=" + type.getValue() + " and ps_id='"
                            + psId + "'");
                    return true;
                }
                return false;
            }

        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                result.close();
            } catch (SQLException e) {
                throw e;
            }
        }
        return false;
    }
    /**
     * @Title: createXREFTable
     * @Description:
     * @param con
     * con
     * @throws
     */
    private void createXREFTable(final Connection con) {

        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(DBSetup.TABLE_XREF);
            stmt.executeUpdate(DBSetup.TABLE_XREF_HISTORY);
            con.commit();
        } catch (SQLException e) {
            LOGGER.error("exception:", e);
        } finally {
            try {
                stmt.close();
            } catch (SQLException e) {
                LOGGER.error("exception:", e);
            }
        }
    }
    /**
     * @Title: cleanCache
     * @Description:
     * @param con
     * con
     * @throws
     */
    public final void cleanCache(final Connection con) {
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate("delete from cache_content");
            con.commit();
        } catch (SQLException e) {
            LOGGER.error("exception:", e);
        } finally {
            try {
                stmt.close();
            } catch (SQLException e) {
                LOGGER.error("exception:", e);
            }
        }
    }
    /**
     * @Title: insertWithReturnKey
     * @Description:
     * @param con
     * con
     * @param sql
     * sql
     * @return long
     * @throws
     */
    public final long
            insertWithReturnKey(final Connection con, final String sql) {
        long key = -1;
        ResultSet result = null;
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.executeUpdate();
            result = pstmt.getGeneratedKeys();
            while (result.next()) {
                key = result.getLong(1);
            }
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                result.close();
                pstmt.close();
            } catch (Exception e) {
                LOGGER.error("exception:", e);
            }
        }
        return key;
    }
    /**
     * @Title: updateNidWithVid
     * @Description:
     * @param con
     * con
     * @param vid
     * vid
     * @param nid
     * nid
     * @throws Exception
     */
    public final void updateNidWithVid(final Connection con, final long vid,
            final long nid) throws Exception {

        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement("update node_revisions set nid=" + nid
                    + " where vid=" + vid);
            pstmt.executeUpdate();
            con.commit();
        } catch (Exception e) {
            LOGGER.error("Exception:", e);
            throw e;
        } finally {
            try {
                pstmt.close();
            } catch (Exception e) {
                LOGGER.error("exception:", e);
            }
        }
    }
    /**
     * @Title: getXrefIdByPsId
     * @Description:
     * @param stmt
     * stmt
     * @param type
     * type
     * @param psId
     * psId
     * @throws SQLException
     * @return long
     */
    public final long getXrefIdByPsId(final Statement stmt, final EtlType type,
            final String psId) throws SQLException {
        long nid = -1;
        ResultSet result = stmt.executeQuery("select nid from xref where type="
                + type.getValue() + " and ps_id='" + psId + "'");

        while (result.next()) {
            nid = result.getLong(1);
        }
        result.close();
        return nid;
    }
    /**
     * @Title: insertXref
     * @Description:
     * @param con
     * con
     * @param type
     * type
     * @param nid
     * nid
     * @param psId
     * psId
     * @param psLastChangeTime
     * psLastChangeTime
     * @throws Exception
     */
    public final void insertXref(final Connection con, final EtlType type,
            final long nid, final String psId, final String psLastChangeTime)
            throws Exception {

        Statement stmt = con.createStatement();
        System.out
                .println("sql is :"
                        + "insert into xref(nid,ps_id,type,ps_last_change_time,created_time,updated_time)values("
                        + nid + ",'" + psId + "'," + type.getValue() + ","
                        + formateDate(psLastChangeTime)
                        + ",UNIX_TIMESTAMP(NOW()),UNIX_TIMESTAMP(NOW()))");
        stmt.executeUpdate("insert into xref(nid,ps_id,type,ps_last_change_time,created_time,updated_time)values("
                + nid
                + ",'"
                + psId
                + "',"
                + type.getValue()
                + ","
                + formateDate(psLastChangeTime)
                + ",UNIX_TIMESTAMP(NOW()),UNIX_TIMESTAMP(NOW()))");
    }
    /**
     * @Title: deleteXrefByPsId
     * @Description:
     * @param con
     * con
     * @param type
     * type
     * @param psId
     * psId
     * @throws SQLException
     */
    public final void deleteXrefByPsId(final Connection con,
            final EtlType type, final String psId) throws SQLException {
        Statement stmt = con.createStatement();
        stmt.executeUpdate("insert into xref_history (select * from xref where ps_id='"
                + psId + "')");
        stmt.executeUpdate("delete from xref where ps_id='" + psId + "'");
    }
    /**
     * @Title: updateXrefByPsId
     * @Description:
     * @param con
     * con
     * @param type
     * type
     * @param psUpdateTime
     * psUpdateTime
     * @param psId
     * psId
     * @throws SQLException
     */
    public final void updateXrefByPsId(final Connection con,
            final EtlType type, final long psUpdateTime, final String psId)
            throws SQLException {
        Statement stmt = con.createStatement();
        stmt.executeUpdate("update xref set status=0 where ps_id='" + psId
                + "'");
    }
    /**
     * @Title: setExpired
     * @Description:
     * @param con
     * con
     * @param nid
     * nid
     * @throws Exception
     */
    public final void setExpired(final Connection con, final long nid)
            throws Exception {
        if (hasDataInExpired(con, nid)) {
            updateExpired(con, nid);
        } else {
            insertExpired(con, nid);
        }
        LOGGER.info(nid + " is set to expired!");
    }
    /**
     * @Title: updateExpired
     * @Description:
     * @param con
     * con
     * @param nid
     * nid
     * @throws SQLException
     */
    private void updateExpired(final Connection con, final long nid)
            throws SQLException {
        Statement stmt = con.createStatement();
        stmt.executeUpdate("update content_field_expiration_datetime set field_expiration_datetime_value=now() where nid="
                + nid);
        stmt.close();
    }
    /**
     * @Title: insertExpired
     * @Description:
     * @param con
     * con
     * @param nid
     * nid
     * @throws Exception
     */
    private void insertExpired(final Connection con, final long nid)
            throws Exception {
        Statement stmt = con.createStatement();
        long vid = -1;
        ResultSet result = stmt.executeQuery("select vid from node where nid="
                + nid);
        while (result.next()) {
            vid = result.getLong(1);
            break;
        }
        stmt.executeUpdate("insert into content_field_expiration_datetime(nid,vid,field_expiration_datetime_value)values("
                + nid + "," + vid + ",now())");
        stmt.close();
    }
    /**
     * @Title: hasDataInExpired
     * @Description:
     * @param con
     * con
     * @param nid
     * nid
     * @throws SQLException
     * @return boolean
     */
    private boolean hasDataInExpired(final Connection con, final long nid)
            throws SQLException {
        Statement stmt = con.createStatement();
        ResultSet result = stmt
                .executeQuery("select nid from content_field_expiration_datetime where nid="
                        + nid);
        while (result.next()) {
            stmt.close();
            return true;
        }
        stmt.close();
        return false;
    }
    /**
     * @Title: updateNodeByNid
     * @Description:
     * @param con
     * con
     * @param nid
     * nid
     * @param title
     * title
     * @throws SQLException
     */
    public final void updateNodeByNid(final Connection con, final long nid,
            final String title) throws SQLException {
        Statement stmt = con.createStatement();
        stmt.executeUpdate("update node set title='" + title + "' where nid="
                + nid);
        stmt.close();

    }
}
