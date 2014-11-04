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
public final class LocationDAOSQLHelper {
    /**
     * @Title: LocationDAOSQLHelper
     */
    private LocationDAOSQLHelper() {
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
     * This class will initial PreparedStatement object.
     * 
     * @Title: CustomerDAOSQLInit
     * @param conn
     *        database connection.
     * @throws SQLException
     *         SQLException
     */
    //    public LocationDAOSQLInit(Connection conn) throws SQLException {
    public static void init(Connection conn)
        throws SQLException {
        pstmtA = conn.prepareStatement(
                "insert into node_revisions (nid,uid,title,timestamp,format,body,teaser,log) values "
                        + "(0,?,?,unix_timestamp(),0,'','','')", Statement.RETURN_GENERATED_KEYS);
        pstmtB = conn.prepareStatement(
                "insert into node (vid,type,language,title,uid,status,created,changed,comment,promote,"
                        + "translate) values (?,?,'',?,?,'1',unix_timestamp(),unix_timestamp(),0,0,0)",
                Statement.RETURN_GENERATED_KEYS);

        pstmtC = conn
                .prepareStatement("insert into content_type_postal_address (nid, vid, field_postal_address_line1_value,"
                        + "field_postal_address_city_value, field_state_province_value, field_postal_code_value, "
                        + "field_postal_code_country_nid) values (?,?,?,?,?,?,?)");

        pstmtD = conn.prepareStatement("insert into content_type_country (nid, vid, field_iso_3166_2lcode_value) "
                + "values (?,?,?)");

        pstmtE = conn.prepareStatement("insert into content_field_is_active (nid, vid) values (?,?)");

        pstmtF = conn.prepareStatement("insert into content_field_sort_sequence (nid, vid) values (?,?)");

        pstmtG = conn.prepareStatement("select c.field_location_id_nid from content_type_device a left join "
                + "content_field_expiration_datetime d ON a.nid = d.nid and a.vid = d.vid "
                + "and d.field_expiration_datetime_value is null, content_field_device_pk b, "
                + "content_type_device_installation c left join content_field_expiration_datetime e "
                + "ON c.nid = e.nid and c.vid = e.vid and e.field_expiration_datetime_value is null "
                + "where a.nid = b.field_device_pk_nid and b.nid = c.nid and c.field_location_id_nid =?");

        pstmtH = conn.prepareStatement("select ps_id from xref where type=1 and nid=?");

        pstmtI = conn.prepareStatement("select nid from xref where type=2 and ps_id like ?");

        pstmtJ = conn
                .prepareStatement("select a.field_party_postal_address_ref_nid from content_type_party_postal_address a "
                        + "left join content_field_expiration_datetime b on a.nid = b.nid and a.vid = b.vid "
                        + "and b.field_expiration_datetime_value is null, node left join "
                        + "content_field_expiration_datetime c on node.nid = c.nid and node.vid = c.vid "
                        + "and c.field_expiration_datetime_value is null "
                        + "where node.nid = a.field_party_postal_address_nid and a.field_party_postal_address_ref_nid=?");

        pstmtK = conn.prepareStatement("update content_type_postal_address set field_postal_address_line1_value=?"
                + ",field_postal_address_city_value=? ,field_state_province_value=? ,field_postal_code_value=?"
                + ",field_postal_code_country_nid=? where nid=?");

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

}
