package com.covidien.etl.dbstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @ClassName: DBUtiltityFunctions
 * @Description:
 */
public final class DBUtiltityFunctions {
    /**
     * @Title: DBUtiltityFunctions
     */
    private DBUtiltityFunctions() {
    }

    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtA = null;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtB = null;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtC = null;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtD = null;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtE = null;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtF = null;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtG = null;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtH = null;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtJ = null;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtK = null;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtL = null;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtM = null;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtN = null;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtO = null;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtP = null;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtQ = null;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtR = null;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtS = null;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtT = null;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtU = null;
    /**
     * Define a PreparedStatement object.
     */
    private static PreparedStatement pstmtV = null;

    /**
     * @Title: DBUtiltityFunctions
     * @param con
     *        database connection.
     * @throws SQLException
     *         SQLException
     */
    public static void init(Connection con)
        throws SQLException {
        pstmtA = con.prepareStatement("SELECT MAX(nid)+1 AS nid FROM node");
        pstmtB = con.prepareStatement("SELECT MAX(vid)+1 AS vid FROM node");
        pstmtC = con.prepareStatement("select nid from node where title='Customer'");
        pstmtD = con.prepareStatement("select nid from content_type_bu_customer "
                + "where field_bu_customer_account_number_value=?");

        pstmtE = con.prepareStatement("select nid from node where title = 'Unknown' and type='bu_customer'");

        pstmtF = con.prepareStatement("select nid from node where type='devicetype' and title=?");
        pstmtG = con
                .prepareStatement("select nid from content_type_country where field_iso_3166_2lcode_value=? limit 0,1");
        pstmtH = con.prepareStatement("select nid from node where title=? and type='postal_address'");
        pstmtJ = con.prepareStatement("select nid from node where type='address_type' and title=?");
        pstmtK = con.prepareStatement("select field_customer_party_pk_nid from content_type_bu_customer "
                + "where field_bu_customer_account_number_value=?");
        pstmtL = con.prepareStatement("select nid from node where title=? and type='device_service_type'");
        pstmtM = con.prepareStatement("select nid from node where title=? and type='person'");
        pstmtN = con.prepareStatement("select uid from users where mail=?");
        pstmtO = con.prepareStatement("select title from node join content_type_sku on "
                + "content_type_sku.field_device_type_pk_nid=node.nid where "
                + "content_type_sku.field_sku_id_value=? " + " and content_type_sku.field_source_system_value=?");
        pstmtP = con.prepareStatement("select field_serial_number_regex_value from content_type_devicetype "
                + "join node on node.nid=content_type_devicetype.nid where title=?");
        pstmtQ = con.prepareStatement("select content_type_device.nid from content_type_device "
                + "join content_field_device_type on content_field_device_type.nid=content_type_device.nid "
                + "join node on node.nid=content_field_device_type.field_device_type_nid "
                + "where field_device_serial_number_value=? and node.title=?");
        pstmtR = con.prepareStatement("select field_device_serial_number_value from content_type_device "
                + "join content_field_device_type on content_field_device_type.nid=content_type_device.nid "
                + "join node on node.nid=content_field_device_type.field_device_type_nid where node.title=?");
        pstmtS = con.prepareStatement("select count(nid) as count from customer_responce_status");
        pstmtT = con.prepareStatement("select count(nid) as count from location_responce_status");
        pstmtU = con.prepareStatement("select count(nid) as count from location_role_responce_status");
        pstmtV = con.prepareStatement("select field_covidien_employee_value from content_type_person "
                + "where field_company_name_nid=?");
    }

    /**
     * @Title: getLatestNid
     * @return long
     * @throws SQLException
     *         SQLException
     */
    public static long getLatestNid()
        throws SQLException {
        long nid = 0;
        ResultSet rs1 = pstmtA.executeQuery();
        if (rs1.next()) {
            nid = rs1.getLong("nid");
        }
        rs1.close();
        return nid;
    }

    /**
     * @Title: getLatestVid
     * @return long
     * @throws SQLException
     *         SQLException
     */
    public static long getLatestVid()
        throws SQLException {
        long vid = 0;
        ResultSet rs2 = pstmtB.executeQuery();
        if (rs2.next()) {
            vid = rs2.getInt("vid");
        }
        rs2.close();
        return vid;
    }

    /**
     * @Title: getCustomerTypeNid
     * @return long
     * @throws SQLException
     *         SQLException
     */
    public static long getCustomerTypeNid()
        throws SQLException {
        long customerTypeNid = -1;
        ResultSet rs3 = pstmtC.executeQuery();
        if (rs3.next()) {
            customerTypeNid = rs3.getInt("nid");
        }
        rs3.close();
        return customerTypeNid;
    }

    /**
     * @Title: getCustomerAcctNid
     * @param customerId
     *        customerId
     * @return long
     * @throws SQLException
     *         SQLException
     */
    public static long getCustomerAcctNid(final String customerId)
        throws SQLException {
        long customerNid = -1;

        pstmtD.setString(1, customerId);
        ResultSet rs3 = pstmtD.executeQuery();
        if (rs3.next()) {
            customerNid = rs3.getInt("nid");
        }
        rs3.close();
        return customerNid;
    }

    /**
     * @Title: getunknownCustomerNid
     * @return long
     * @throws SQLException
     *         SQLException
     */
    public static long getunknownCustomerNid()
        throws SQLException {
        long customerNid = -1;

        ResultSet rs3 = pstmtE.executeQuery();
        if (rs3.next()) {
            customerNid = rs3.getInt("nid");
        }
        rs3.close();
        return customerNid;
    }

    /**
     * @Title: getDeviceTypeNid
     * @param deviceType
     *        deviceType
     * @return long
     * @throws SQLException
     *         SQLException
     */
    public static long getDeviceTypeNid(final String deviceType)
        throws SQLException {
        long deviceTypeNid = -1;
        pstmtF.setString(1, deviceType);
        ResultSet rs3 = pstmtF.executeQuery();
        if (rs3.next()) {
            deviceTypeNid = rs3.getInt("nid");
        }
        rs3.close();
        return deviceTypeNid;
    }

    /**
     * @Title: getCountryNid
     * @param countryName
     *        countryName
     * @return long
     * @throws SQLException
     *         SQLException
     */
    public static long getCountryNid(final String countryName)
        throws SQLException {
        long countryNid = -1;
        pstmtG.setString(1, countryName);

        ResultSet rs3 = pstmtG.executeQuery();
        if (!rs3.isClosed()) {
            if (rs3.next()) {
                countryNid = rs3.getLong("nid");
            }
        }
        rs3.close();
        return countryNid;
    }

    /**
     * @Title: getPostalAddressRefNid
     * @param postalAddressId
     *        postalAddressId
     * @return long
     * @throws SQLException
     *         SQLException
     */
    public static long getPostalAddressRefNid(final String postalAddressId)
        throws SQLException {
        long postalAddressNid = -1;

        pstmtH.setString(1, postalAddressId);
        ResultSet rs3 = pstmtH.executeQuery();
        if (rs3.next()) {
            postalAddressNid = rs3.getInt("nid");
        }
        rs3.close();
        return postalAddressNid;
    }

    /**
     * @Title: getAddressTypeNid
     * @param locationRole
     *        locationRole
     * @return long
     * @throws SQLException
     *         SQLException
     */
    public static long getAddressTypeNid(final String locationRole)
        throws SQLException {
        long addressTypeNid = -1;
        pstmtJ.setString(1, locationRole);
        ResultSet rs3 = pstmtJ.executeQuery();
        if (rs3.next()) {
            addressTypeNid = rs3.getInt("nid");
        }
        rs3.close();
        return addressTypeNid;
    }

    /**
     * @Title: getPartyNid
     * @param customerId
     *        customerId
     * @return long
     * @throws SQLException
     *         SQLException
     */
    public static long getPartyNid(final String customerId)
        throws SQLException {
        long partyAddressNid = -1;
        pstmtK.setString(1, customerId);
        ResultSet rs3 = pstmtK.executeQuery();
        if (rs3.next()) {
            partyAddressNid = rs3.getInt("field_customer_party_pk_nid");
        }
        rs3.close();
        return partyAddressNid;
    }

    /**
     * @Title: getServiceTypeNid
     * @param serviceType
     *        serviceType
     * @return long
     * @throws SQLException
     *         SQLException
     */
    public static long getServiceTypeNid(final String serviceType)
        throws SQLException {
        long serviceTypeNid = -1;
        pstmtL.setString(1, serviceType);
        ResultSet rs3 = pstmtL.executeQuery();
        if (rs3.next()) {
            serviceTypeNid = rs3.getInt("nid");
        }
        rs3.close();
        return serviceTypeNid;
    }

    /**
     * @Title: getPersonNid
     * @param personName
     *        personName
     * @return long
     * @throws SQLException
     *         SQLException
     */
    public static long getPersonNid(final String personName)
        throws SQLException {
        long personNid = 0;
        pstmtM.setString(1, personName);
        ResultSet rs3 = pstmtM.executeQuery();
        if (rs3.next()) {
            personNid = rs3.getInt("nid");
        }
        rs3.close();
        return personNid;
    }

    /**
     * @Title: getUserId
     * @param userName
     *        userName
     * @return long
     * @throws SQLException
     *         SQLException
     */
    public static long getUserId(final String userName)
        throws SQLException {
        long userid = 0;
        pstmtN.setString(1, userName);
        ResultSet rs3 = pstmtN.executeQuery();
        if (rs3.next()) {
            userid = rs3.getInt("uid");
        }
        rs3.close();
        return userid;
    }

    /**
     * @Title: getDeviceType
     * @param sku
     *        sku
     * @param sourceSystem
     *        sourceSystem
     * @return String
     * @throws SQLException
     *         SQLException
     */
    public static String getDeviceType(final String sku, final String sourceSystem)
        throws SQLException {
        String deviceType = null;
        pstmtO.setString(1, sku);
        pstmtO.setString(2, sourceSystem);
        ResultSet rs3 = pstmtO.executeQuery();
        if (rs3.next()) {
            deviceType = rs3.getString("title");
        }
        rs3.close();
        return deviceType;

    }

    /**
     * @Title: getSerialNumberValidation
     * @param deviceType
     *        deviceType
     * @return String
     * @throws SQLException
     *         SQLException
     */
    public static String getSerialNumberValidation(final String deviceType)
        throws SQLException {
        String serialNumberRegex = null;
        pstmtP.setString(1, deviceType);
        ResultSet rs3 = pstmtP.executeQuery();
        if (rs3.next()) {
            serialNumberRegex = rs3.getString("field_serial_number_regex_value");
        }
        rs3.close();
        return serialNumberRegex;
    }

    /**
     * @Title: checkDuplicateSerialNumber
     * @param deviceType
     *        deviceType
     * @param serialNumber
     *        serialNumber
     * @return boolean
     * @throws SQLException
     *         SQLException
     */
    public static boolean checkDuplicateSerialNumber(final String deviceType, final String serialNumber)
        throws SQLException {
        pstmtQ.setString(1, serialNumber);
        pstmtQ.setString(2, deviceType);
        ResultSet rs3 = pstmtQ.executeQuery();
        if (rs3 != null && rs3.next() && rs3.getInt("nid") > 0) {
            return true;
        }
        rs3.close();
        return false;
    }

    /**
     * @Title: getDeviceTypeSerialNumbers
     * @param deviceType
     *        deviceType
     * @return ArrayList<String>
     * @throws SQLException
     *         SQLException
     */
    public static ArrayList<String> getDeviceTypeSerialNumbers(final String deviceType)
        throws SQLException {
        pstmtR.setString(1, deviceType);
        ResultSet rs3 = pstmtR.executeQuery();
        ArrayList<String> serialNumbers = new ArrayList<String>();
        if (rs3 != null) {
            while (rs3.next()) {
                serialNumbers.add(rs3.getString("field_device_serial_number_value"));
            }
        }
        rs3.close();
        return serialNumbers;
    }

    /**
     * @Title: checkAnyCustomerRecordAddedByETL
     * @return boolean
     * @throws SQLException
     *         SQLException
     */
    public static boolean checkAnyCustomerRecordAddedByETL()
        throws SQLException {

        ResultSet rs3 = pstmtS.executeQuery();
        if (rs3 != null && rs3.next() && rs3.getInt("count") > 0) {
            return true;
        }
        rs3.close();
        return false;
    }

    /**
     * @Title: checkAnyLocationRecordAddedByETL
     * @return boolean
     * @throws SQLException
     *         SQLException
     */
    public static boolean checkAnyLocationRecordAddedByETL()
        throws SQLException {
        ResultSet rs3 = pstmtT.executeQuery();
        if (rs3 != null && rs3.next() && rs3.getInt("count") > 0) {
            return true;
        }
        rs3.close();
        return false;
    }

    /**
     * @Title: checkAnyLocationRoleRecordAddedByETL
     * @return boolean
     * @throws SQLException
     *         SQLException
     */
    public static boolean checkAnyLocationRoleRecordAddedByETL()
        throws SQLException {
        ResultSet rs3 = pstmtU.executeQuery();
        if (rs3 != null && rs3.next() && rs3.getInt("count") > 0) {
            return true;
        }
        rs3.close();
        return false;
    }

    /**
     * get user type.
     * 
     * @Title: checkAnyLocationRoleRecordAddedByETL
     * @param personNid
     *        person nid of user.
     * @return if it's covidien user return 'Yes' else return 'No'.
     * @throws SQLException
     *         SQLException
     */
    public static String getUserType(final long personNid)
        throws SQLException {
        String userType = "Yes";
        pstmtV.setLong(1, personNid);
        ResultSet rs3 = pstmtV.executeQuery();
        if (rs3.next()) {
            userType = rs3.getString("field_covidien_employee_value");
        }
        rs3.close();
        return userType;
    }
}
