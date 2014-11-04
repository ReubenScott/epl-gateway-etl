/**
 * @Title: CustomerDAOSQLHelper.java
 * @Package com.covidien.etl.dao.helper
 * @author tony.zhang2
 * @date 2014-2-8
 * @version V2.0
 */
package com.covidien.etl.dao.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @ClassName: CustomerDAOSQLHelper
 */
public final class CustomerDAOSQLHelper {
    /**
     * @Title: CustomerDAOSQLHelper
     */
    private CustomerDAOSQLHelper() {
    }

    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtA;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtB;
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
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtR;

    /**
     * This class will initial PreparedStatement object.
     * 
     * @Title: CustomerDAOSQLInit
     * @param conn
     *        database connection.
     * @throws SQLException
     *         SQLException
     */
    public static void init(Connection conn)
        throws SQLException {
        pstmtA = conn.prepareStatement("SELECT d.field_device_owner_nid FROM "
                + "content_type_device d left join content_field_expiration_datetime e "
                + "on d.nid=e.nid and d.vid=e.vid "
                + "where e.field_expiration_datetime_value is null and d.field_device_owner_nid=?");

        pstmtB = conn.prepareStatement("select ps_id from xref where type=0 and nid=?");
        pstmtC = conn.prepareStatement("select nid from xref where type=2 and ps_id like ?");
        pstmtD = conn.prepareStatement("select a.name from users a, node nodeUser left join "
                + "content_field_expiration_datetime d ON nodeUser.nid = d.nid and nodeUser.vid = d.vid, "
                + "content_type_person c where a.uid = nodeUser.uid "
                + "and d.field_expiration_datetime_value is null and nodeUser.type = 'party' "
                + "and a.name = nodeUser.title and nodeUser.nid = c.field_person_party_nid "
                + "and c.field_comp_account_no_nid = ?");
        pstmtE = conn.prepareStatement("SELECT field_customer_party_pk_nid FROM content_type_bu_customer WHERE nid=?");
        pstmtF = conn.prepareStatement("select nid from node where title=? and type='party' limit 0,1");
        pstmtG = conn.prepareStatement("update node set title=? where nid=?");
        pstmtH = conn.prepareStatement("select field_customer_party_pk_nid from content_type_bu_customer "
                + "where field_bu_customer_account_number_value=?");
        pstmtI = conn.prepareStatement(
                "insert into node_revisions (nid,uid,title,timestamp,format,body,teaser,log) values "
                        + "(0,?,?,unix_timestamp(),0,'','','')", Statement.RETURN_GENERATED_KEYS);
        pstmtJ = conn.prepareStatement(
                "insert into node (vid,type,language,title,uid,status,created,changed,comment,promote,"
                        + "translate) values (?,?,'',?,?,'1',unix_timestamp(),unix_timestamp(),0,0,0)",
                Statement.RETURN_GENERATED_KEYS);
        pstmtK = conn.prepareStatement("insert into content_type_party (vid,nid,field_party_type_nid) values (?,?,?)");
        pstmtL = conn.prepareStatement("select vid from content_field_expiration_datetime where nid=?");
        pstmtM = conn
                .prepareStatement("update content_field_expiration_datetime set field_expiration_datetime_value = null "
                        + "where nid= ?");
        pstmtN = conn.prepareStatement("insert into content_field_expiration_datetime ( vid , nid ) values (?,?)");
        pstmtO = conn.prepareStatement("insert into content_type_bu_customer (vid,nid,"
                + "field_bu_customer_account_number_value,field_customer_party_pk_nid) values (?,?,?,?)");
        pstmtP = conn
                .prepareStatement("insert into content_type_party_voice_address (vid,nid,field_voice_party_pk_nid, "
                        + "field_voice_type_value, field_voice_address_value) values (?,?,?,?,?)");
        pstmtQ = conn.prepareStatement("select field_voice_address_value from content_type_party_voice_address "
                + "where field_voice_type_value=? and field_voice_party_pk_nid=?");
        pstmtR = conn.prepareStatement("update content_type_party_voice_address set field_voice_address_value=? "
                + " where field_voice_party_pk_nid=? and field_voice_type_value=?");
    }

    /**
     * @Title: getPstmtA
     * @return PreparedStatement
     */
    public static PreparedStatement getPstmtA() {
        return pstmtA;
    }

    /**
     * @Title: getPstmtB
     * @return PreparedStatement
     */
    public static PreparedStatement getPstmtB() {
        return pstmtB;
    }

    /**
     * @Title: getPstmtC
     * @return PreparedStatement
     */
    public static PreparedStatement getPstmtC() {
        return pstmtC;
    }

    /**
     * @Title: getPstmtD
     * @return PreparedStatement
     */
    public static PreparedStatement getPstmtD() {
        return pstmtD;
    }

    /**
     * @Title: getPstmtE
     * @return PreparedStatement
     */
    public static PreparedStatement getPstmtE() {
        return pstmtE;
    }

    /**
     * @Title: getPstmtF
     * @return PreparedStatement
     */
    public static PreparedStatement getPstmtF() {
        return pstmtF;
    }

    /**
     * @Title: getPstmtG
     * @return PreparedStatement
     */
    public static PreparedStatement getPstmtG() {
        return pstmtG;
    }

    /**
     * @Title: getPstmtH
     * @return PreparedStatement
     */
    public static PreparedStatement getPstmtH() {
        return pstmtH;
    }

    /**
     * @Title: getPstmtI
     * @return PreparedStatement
     */
    public static PreparedStatement getPstmtI() {
        return pstmtI;
    }

    /**
     * @Title: getPstmtJ
     * @return PreparedStatement
     */
    public static PreparedStatement getPstmtJ() {
        return pstmtJ;
    }

    /**
     * @Title: getPstmtK
     * @return PreparedStatement
     */
    public static PreparedStatement getPstmtK() {
        return pstmtK;
    }

    /**
     * @Title: getPstmtL
     * @return PreparedStatement
     */
    public static PreparedStatement getPstmtL() {
        return pstmtL;
    }

    /**
     * @Title: getPstmtM
     * @return PreparedStatement
     */
    public static PreparedStatement getPstmtM() {
        return pstmtM;
    }

    /**
     * @Title: getPstmtN
     * @return PreparedStatement
     */
    public static PreparedStatement getPstmtN() {
        return pstmtN;
    }

    /**
     * @Title: getPstmtO
     * @return PreparedStatement
     */
    public static PreparedStatement getPstmtO() {
        return pstmtO;
    }

    /**
     * @Title: getPstmtP
     * @return PreparedStatement
     */
    public static PreparedStatement getPstmtP() {
        return pstmtP;
    }

    /**
     * @Title: getPstmtQ
     * @return PreparedStatement
     */
    public static PreparedStatement getPstmtQ() {
        return pstmtQ;
    }

    /**
     * @Title: getPstmtR
     * @return PreparedStatement
     */
    public static PreparedStatement getPstmtR() {
        return pstmtR;
    }

}
