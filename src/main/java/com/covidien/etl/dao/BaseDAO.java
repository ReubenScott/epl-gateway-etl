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
            Connection con = dbConnection.getConnection();
            con.setAutoCommit(false);
            createXREFTable(con);
            createIndexForXref(con);
        } catch (SQLException e) {
            LOGGER.error("Exception:" + e);
        }
    }

    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtA;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtC;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtD;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtE;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtF;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtG;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtH;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtI;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtJ;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtK;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtL;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtM;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtN;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtO;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtP;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtQ;

    /**
     * Before using function in this class, must initial this class by init
     * function.
     * 
     * @Title: init
     * @param conn
     *        database connection
     * @throws SQLException
     *         SQLException
     */
    public final void init(Connection conn)
        throws SQLException {
        pstmtA = conn.prepareStatement("select ps_last_change_time from xref where type=? and ps_id=?");
        pstmtC = conn.prepareStatement("update node_revisions set nid=? where vid=?");
        pstmtD = conn.prepareStatement("select nid from xref where type=? and ps_id=?");
        pstmtE = conn
                .prepareStatement("insert into xref(nid,ps_id,type,ps_last_change_time,created_time,updated_time)values(?,?,?,?,"
                        + "UNIX_TIMESTAMP(NOW()),UNIX_TIMESTAMP(NOW()))");
        pstmtF = conn.prepareStatement("insert into xref_history (select * from xref where ps_id=?)");
        pstmtG = conn.prepareStatement("delete from xref where ps_id=?");
        pstmtH = conn
                .prepareStatement("update xref set ps_last_change_time=?,updated_time=UNIX_TIMESTAMP(NOW()) where type=? and ps_id=?");
        pstmtI = conn
                .prepareStatement("update content_field_expiration_datetime set field_expiration_datetime_value=now() where nid=?");
        pstmtJ = conn.prepareStatement("select vid from node where nid=?");
        pstmtK = conn
                .prepareStatement("insert into content_field_expiration_datetime(nid,vid,field_expiration_datetime_value)"
                        + "values(?,?,now())");
        pstmtL = conn.prepareStatement("insert into content_field_expiration_datetime(nid,vid) values(?,?)");
        pstmtM = conn.prepareStatement("select nid from content_field_expiration_datetime where nid=? and vid=?");
        pstmtN = conn.prepareStatement("update node set title=? where nid=?");
        pstmtO = conn.prepareStatement(
                "insert into node_revisions (nid,uid,title,timestamp,format,body,teaser,log) values "
                        + "(?,?,?,unix_timestamp(),0,'','','')", Statement.RETURN_GENERATED_KEYS);
        pstmtP = conn.prepareStatement("update node set vid=? where nid=?");
        pstmtQ = conn.prepareStatement("delete from cache_content");

    }

    /**
     * @Title: getEtlUser
     * @Description:
     * @return String
     */
    public final String getEtlUser() {
        return etlUSER;
    }

    /**
     * @Title: process
     * @Description:
     * @param list
     *        list
     */
    public abstract void process(List<T> list);

    /**
     * @Title: formateDate
     * @Description:
     * @param strDate
     *        strDate
     * @throws ParseException
     *         ParseException
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
     * @param type
     *        type
     * @param psId
     *        psId
     * @param date
     *        date
     * @return boolean
     * @throws SQLException
     *         SQLException
     */
    public final boolean shouldChange(final EtlType type, final String psId, final long date)
        throws SQLException {
        ResultSet result = null;
        try {
            pstmtA.setInt(1, type.getValue());
            pstmtA.setString(2, psId);
            result = pstmtA.executeQuery();
            while (result.next()) {
                long updateTime = result.getLong("ps_last_change_time");
                if (updateTime < date) {
                    updateXrefByPsId(type, date, psId);
                    return true;
                }
                return false;
            }
            result.close();
        } catch (SQLException e) {
            throw e;
        }
        return false;
    }

    /**
     * @Title: createXREFTable
     * @Description:
     * @param con
     *        con
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
     * create index for xref table.
     * 
     * @Title: createIndexForXref
     * @param con
     *        database connection
     */
    private void createIndexForXref(final Connection con) {
        long count = 0;
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            ResultSet result = stmt
                    .executeQuery("select count(1) from information_schema.statistics where table_name = 'xref' "
                            + "and index_name in ('xref_type_ps_id','xref_type_nid','xref_ps_id')");

            while (result.next()) {
                count = result.getLong(1);
            }
            if (count < 1) {
                stmt.executeUpdate(DBSetup.TABLE_XREF_INDEX_1);
                stmt.executeUpdate(DBSetup.TABLE_XREF_INDEX_2);
                stmt.executeUpdate(DBSetup.TABLE_XREF_INDEX_3);
                con.commit();
            }
            result.close();
        } catch (Exception e) {
            LOGGER.error("Exception", e);
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
     */
    public final void cleanCache() {
        try {
            pstmtQ.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("exception:", e);
        }
    }

    /**
     * @Title: insertWithReturnKey
     * @param pstmt
     *        pstmt
     * @return long
     */
    public final long insertWithReturnKey(final PreparedStatement pstmt) {
        long key = -1;
        ResultSet result = null;
        try {
            //pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.executeUpdate();
            result = pstmt.getGeneratedKeys();
            while (result.next()) {
                key = result.getLong(1);
            }
            result.close();
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }
        return key;
    }

    /**
     * @Title: updateNidWithVid
     * @Description:
     * @param vid
     *        vid
     * @param nid
     *        nid
     * @throws Exception
     *         Exception
     */
    public final void updateNidWithVid(final long vid, final long nid)
        throws Exception {

        try {
            pstmtC.setLong(1, nid);
            pstmtC.setLong(2, vid);
            pstmtC.executeUpdate();
        } catch (Exception e) {
            LOGGER.error("Exception:", e);
            throw e;
        }
    }

    /**
     * @Title: getXrefIdByPsId
     * @param type
     *        type
     * @param psId
     *        psId
     * @throws SQLException
     *         SQLException
     * @return long
     */
    public final long getXrefIdByPsId(final EtlType type, final String psId)
        throws SQLException {
        long nid = -1;
        pstmtD.setInt(1, type.getValue());
        pstmtD.setString(2, psId);
        ResultSet result = pstmtD.executeQuery();

        while (result.next()) {
            nid = result.getLong(1);
        }
        result.close();
        return nid;
    }

    /**
     * @Title: insertXref
     * @Description:
     * @param type
     *        type
     * @param nid
     *        nid
     * @param psId
     *        psId
     * @param psLastChangeTime
     *        psLastChangeTime
     * @throws Exception
     *         Exception
     */
    public final void insertXref(final EtlType type, final long nid, final String psId, final String psLastChangeTime)
        throws Exception {

        pstmtE.setLong(1, nid);
        pstmtE.setString(2, psId);
        pstmtE.setInt(3, type.getValue());
        pstmtE.setLong(4, formateDate(psLastChangeTime));
        pstmtE.executeUpdate();
    }

    /**
     * @Title: deleteXrefByPsId
     * @Description:
     * @param type
     *        type
     * @param psId
     *        psId
     * @throws SQLException
     *         SQLException
     */
    public final void deleteXrefByPsId(final EtlType type, final String psId)
        throws SQLException {
        pstmtF.setString(1, psId);
        pstmtF.executeUpdate();
        pstmtG.setString(1, psId);
        pstmtG.executeUpdate();
    }

    /**
     * @Title: updateXrefByPsId
     * @Description:
     * @param type
     *        type
     * @param psUpdateTime
     *        psUpdateTime
     * @param psId
     *        psId
     * @throws SQLException
     *         SQLException
     */
    public final void updateXrefByPsId(final EtlType type, final long psUpdateTime, final String psId)
        throws SQLException {
        pstmtH.setLong(1, psUpdateTime);
        pstmtH.setInt(2, type.getValue());
        pstmtH.setString(3, psId);
        pstmtH.executeUpdate();
    }

    /**
     * @Title: setExpired
     * @Description:
     * @param nid
     *        nid
     * @throws Exception
     *         Exception
     */
    public final void setExpired(final long nid)
        throws Exception {
        if (hasDataInExpired(nid)) {
            updateExpired(nid);
        } else {
            insertExpired(nid);
        }
        LOGGER.info(nid + " is set to expired!");
    }

    /**
     * @Title: updateExpired
     * @Description:
     * @param nid
     *        nid
     * @throws SQLException
     *         SQLException
     */
    private void updateExpired(final long nid)
        throws SQLException {
        pstmtI.setLong(1, nid);
        pstmtI.executeUpdate();
    }

    /**
     * @Title: insertExpired
     * @Description:
     * @param nid
     *        nid
     * @throws Exception
     *         Exception
     */
    private void insertExpired(final long nid)
        throws Exception {
        long vid = -1;
        pstmtJ.setLong(1, nid);
        ResultSet result = pstmtJ.executeQuery();
        while (result.next()) {
            vid = result.getLong(1);
            break;
        }
        result.close();
        pstmtK.setLong(1, nid);
        pstmtK.setLong(2, vid);
        pstmtK.executeUpdate();
    }

    /**
     * add record to content_field_expiration_datetime table when new record
     * created.
     * 
     * @Title: addValidRecord2ExpirationTbl
     * @param nid
     *        node nid.
     * @param vid
     *        node vid.
     * @throws Exception
     *         sql exception.
     */
    public final void addValidRecord2ExpirationTbl(final long nid, final long vid)
        throws Exception {
        pstmtL.setLong(1, nid);
        pstmtL.setLong(2, vid);
        pstmtL.executeUpdate();
    }

    /**
     * @Title: hasDataInExpired
     * @param nid
     *        nid
     * @throws SQLException
     *         SQLException
     * @return boolean
     */
    private boolean hasDataInExpired(final long nid)
        throws SQLException {
        long vid = -1;
        pstmtJ.setLong(1, nid);
        ResultSet result = pstmtJ.executeQuery();
        while (result.next()) {
            vid = result.getLong(1);
            break;
        }
        pstmtM.setLong(1, nid);
        pstmtM.setLong(2, vid);
        result = pstmtM.executeQuery();
        while (result.next()) {
            return true;
        }
        result.close();
        return false;
    }

    /**
     * @Title: updateNodeByNid
     * @param nid
     *        nid
     * @param title
     *        title
     * @throws SQLException
     *         SQLException
     */
    public final void updateNodeByNid(final long nid, final String title)
        throws SQLException {
        pstmtN.setString(1, title);
        pstmtN.setLong(2, nid);
        pstmtN.executeUpdate();
    }

    /**
     * Update title and updte node version id, and set previous vid to expiry,
     * and update new vid to related nid.
     * 
     * @Title: updateNodeRevisionsByNid
     * @param uid
     *        User id.
     * @param nid
     *        nid.
     * @param title
     *        title.
     * @throws Exception
     *         Exception
     */
    public final void updateNodeRevisionsByNid(final long uid, final long nid, final String title)
        throws Exception {
        long vid = 0;
        pstmtO.setLong(1, nid);
        pstmtO.setLong(2, uid);
        pstmtO.setString(3, title);
        vid = insertWithReturnKey(pstmtO);
        setExpired(nid);
        pstmtP.setLong(1, vid);
        pstmtP.setLong(2, nid);
        pstmtP.executeUpdate();
    }
}
