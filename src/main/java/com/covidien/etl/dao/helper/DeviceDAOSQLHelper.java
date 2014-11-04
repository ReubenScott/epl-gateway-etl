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
public final class DeviceDAOSQLHelper {
    /**
     * @Title: DeviceDAOSQLHelper
     */
    private DeviceDAOSQLHelper() {
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
        pstmtA = conn.prepareStatement("select title from node join content_type_sku on "
                + "content_type_sku.field_device_type_pk_nid=node.nid where content_type_sku.field_sku_id_value=? "
                + "and content_type_sku.field_source_system_value=?");

        pstmtB = conn.prepareStatement("update content_type_device set field_device_owner_nid=?"
                + ", field_maintance_expiration_date_value=? where nid=?");
        pstmtC = conn.prepareStatement("update content_type_device_installation set field_device_country_nid=?, "
                + "modified_privilege = ? where nid = (select a.nid from content_field_device_pk a, node b "
                + "where a.field_device_pk_nid=? and a.nid = b.nid and b.type='device_installation')");
        pstmtD = conn.prepareStatement(
                "insert into node_revisions (nid,uid,title,timestamp,format,body,teaser,log) values "
                        + "(0,?,?,unix_timestamp(),0,'','','')", Statement.RETURN_GENERATED_KEYS);
        pstmtE = conn.prepareStatement(
                "insert into node (vid,type,language,title,uid,status,created,changed,comment,promote,"
                        + "translate) values (?,?,'',?,?,'1',unix_timestamp(),unix_timestamp(),0,0,0)",
                Statement.RETURN_GENERATED_KEYS);

        pstmtF = conn.prepareStatement("insert into content_type_device (nid, vid, field_device_serial_number_value, "
                + "field_device_is_active_value, field_device_owner_nid, field_maintance_expiration_date_value)"
                + " values (?,?,?,1,?,?)");

        pstmtG = conn
                .prepareStatement("insert into content_field_device_type (nid, vid, field_device_type_nid) values (?,?,?)");

        pstmtH = conn
                .prepareStatement("insert into content_type_device_installation (nid, vid, field_device_country_nid,"
                        + " field_location_id_nid) values(?,?,?,?)");

        pstmtI = conn
                .prepareStatement("insert into content_field_device_pk (nid, vid, field_device_pk_nid) values (?,?,?)");

        pstmtJ = conn.prepareStatement("insert into content_type_device_service_history (nid, vid, "
                + "field_device_installation_pk_nid, field_device_service_type_nid, field_service_person_pk_nid, "
                + "field_service_note_value, field_service_datetime_value) values "
                + "(?,?,?,?,?,'Device Registered',current_timestamp())");

        pstmtK = conn.prepareStatement("select a.nid from node a left join content_field_expiration_datetime b "
                + " on a.nid = b.nid and a.vid = b.vid where a.title=? and a.type='device' "
                + "and b.field_expiration_datetime_value is null ");

        pstmtL = conn.prepareStatement("select modified_privilege from content_type_device_installation "
                + "where nid = (select a.nid from content_field_device_pk a, node b "
                + "where a.field_device_pk_nid=? and a.nid = b.nid and b.type='device_installation')");

        pstmtM = conn.prepareStatement("update content_type_device_installation set field_location_id_nid=? "
                + "where nid = (select a.nid from content_field_device_pk a, node b "
                + "where a.field_device_pk_nid=? and a.nid = b.nid and b.type='device_installation')");

        pstmtN = conn.prepareStatement("select a.field_device_owner_nid from content_type_device a "
                + "join node n on a.nid = n.nid and a.vid = n.vid "
                + "left join content_field_expiration_datetime b on a.nid = b.nid and a.vid = b.vid "
                + "where b.field_expiration_datetime_value is null and n.title=? and n.type='device' ");

        pstmtO = conn.prepareStatement("select modified_owner_privilege from content_type_device where nid=?");
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

}
