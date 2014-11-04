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
public final class LocationRoleDAOSQLHelper {
    /**
     * @Title: LocationRoleDAOSQLHelper
     */
    private LocationRoleDAOSQLHelper() {
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
        pstmtA = conn.prepareStatement("update content_type_party_postal_address set field_postal_address_type_nid=?"
                + " where nid=?");

        pstmtB = conn.prepareStatement(
                "insert into node_revisions (nid,uid,title,timestamp,format,body,teaser,log) values "
                        + "(0,?,?,unix_timestamp(),0,'','','')", Statement.RETURN_GENERATED_KEYS);
        pstmtC = conn.prepareStatement(
                "insert into node (vid,type,language,title,uid,status,created,changed,comment,promote,"
                        + "translate) values (?,?,'',?,?,'1',unix_timestamp(),unix_timestamp(),0,0,0)",
                Statement.RETURN_GENERATED_KEYS);
        pstmtD = conn.prepareStatement("insert into content_type_party_postal_address (nid, vid, "
                + "field_party_postal_address_nid, "
                + "field_postal_address_type_nid, field_party_postal_address_ref_nid) values (?,?,?,?,?)");

        pstmtE = conn
                .prepareStatement("insert into content_type_address_type (nid, vid, field_address_type_name_value) "
                        + "values (?,?,?)");

        pstmtF = conn.prepareStatement("select nid from content_type_party_postal_address "
                + "where field_party_postal_address_nid =? and field_party_postal_address_ref_nid=?");

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

}
