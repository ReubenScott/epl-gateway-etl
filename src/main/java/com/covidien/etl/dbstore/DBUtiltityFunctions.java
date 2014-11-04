package com.covidien.etl.dbstore;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * @ClassName: DBUtiltityFunctions
 * @Description:
 */
public class DBUtiltityFunctions {
    /**
     * @Title: getLatestNid
     * @Description:
     * @param stmt
     * stmt
     * @return long
     * @throws SQLException
     */
    public static final long getLatestNid(final Statement stmt)
            throws SQLException {
        long nid = 0;
        ResultSet rs1 = stmt.executeQuery("SELECT MAX(nid)+1 AS nid FROM node");
        if (rs1.next()) {
            nid = rs1.getLong("nid");
        }
        return nid;
    }
    /**
     * @Title: getLatestVid
     * @Description:
     * @param stmt
     * stmt
     * @return long
     * @throws SQLException
     */
    public static final long getLatestVid(final Statement stmt)
            throws SQLException {
        long vid = 0;
        ResultSet rs2 = stmt.executeQuery("SELECT MAX(vid)+1 AS vid FROM node");
        if (rs2.next()) {
            vid = rs2.getInt("vid");
        }
        return vid;
    }
    /**
     * @Title: getCustomerTypeNid
     * @Description:
     * @param stmt
     * stmt
     * @return long
     * @throws SQLException
     */
    public static final long getCustomerTypeNid(final Statement stmt)
            throws SQLException {
        long customerTypeNid = -1;
        ResultSet rs3 = stmt
                .executeQuery("select nid from node where title='Customer'");
        if (rs3.next()) {
            customerTypeNid = rs3.getInt("nid");
        }
        return customerTypeNid;
    }
    /**
     * @Title: getCustomerNid
     * @Description:
     * @param stmt
     * stmt
     * @param customerId
     * customerId
     * @return long
     * @throws SQLException
     */
    public static final long getCustomerNid(final Statement stmt,
            final String customerId) throws SQLException {
        long customerNid = -1;
        String sqlQuery = "select nid from content_type_party where nid in(select field_customer_party_pk_nid from content_type_bu_customer where field_bu_customer_account_number_value='"
                + customerId + "')";
        ResultSet rs3 = stmt.executeQuery(sqlQuery);
        if (rs3.next()) {
            customerNid = rs3.getInt("nid");
        }
        return customerNid;
    }
    /**
     * @Title: getunknownCustomerNid
     * @Description:
     * @param stmt
     * stmt
     * @return long
     * @throws SQLException
     */
    public static final long getunknownCustomerNid(final Statement stmt)
            throws SQLException {
        long customerNid = -1;
        String sqlQuery = "select node.nid,node.title from node "
                + "join content_type_party on content_type_party.vid=node.vid "
                + "join content_type_party_type on content_type_party_type.nid=content_type_party.field_party_type_nid "
                + "join node as node1 on node1.vid=content_type_party_type.vid and node1.title='customer' where node.title='Unknown'";
        ResultSet rs3 = stmt.executeQuery(sqlQuery);
        if (rs3.next()) {
            customerNid = rs3.getInt("nid");
        }
        return customerNid;
    }
    /**
     * @Title: getDeviceTypeNid
     * @Description:
     * @param stmt
     * stmt
     * @param deviceType
     * deviceType
     * @return long
     * @throws SQLException
     */
    public static final long getDeviceTypeNid(final Statement stmt,
            final String deviceType) throws SQLException {
        long deviceTypeNid = -1;
        String sqlQuery = "select nid from node where type='devicetype' and title='"
                + deviceType + "'";
        ResultSet rs3 = stmt.executeQuery(sqlQuery);
        if (rs3.next()) {
            deviceTypeNid = rs3.getInt("nid");
        }
        return deviceTypeNid;
    }
    /**
     * @Title: getCountryNid
     * @Description:
     * @param stmt
     * stmt
     * @param countryName
     * countryName
     * @return long
     * @throws SQLException
     */
    public static final long getCountryNid(final Statement stmt,
            final String countryName) throws SQLException {
        long countryNid = -1;
        String sqlQuery = "select nid from content_type_country where field_iso_3166_2lcode_value='"
                + countryName + "'";
        ResultSet rs3 = stmt.executeQuery(sqlQuery);
        if (rs3.next()) {
            countryNid = rs3.getLong("nid");
        }
        return countryNid;
    }
    /**
     * @Title: getPostalAddressRefNid
     * @Description:
     * @param stmt
     * stmt
     * @param postalAddressId
     * postalAddressId
     * @return long
     * @throws SQLException
     */
    public static final long getPostalAddressRefNid(final Statement stmt,
            final String postalAddressId) throws SQLException {
        long postalAddressNid = -1;
        String sqlQuery = "select nid from node where title='"
                + postalAddressId + "' and type='postal_address'";
        ResultSet rs3 = stmt.executeQuery(sqlQuery);
        if (rs3.next()) {
            postalAddressNid = rs3.getInt("nid");
        }
        return postalAddressNid;
    }
    /**
     * @Title: getAddressTypeNid
     * @Description:
     * @param stmt
     * stmt
     * @param locationRole
     * locationRole
     * @return long
     * @throws SQLException
     */
    public static final long getAddressTypeNid(final Statement stmt,
            final String locationRole) throws SQLException {
        long addressTypeNid = -1;
        String sqlQuery = "select nid from node where type='address_type' and title='"
                + locationRole + "'";
        ResultSet rs3 = stmt.executeQuery(sqlQuery);
        if (rs3.next()) {
            addressTypeNid = rs3.getInt("nid");
        }
        return addressTypeNid;
    }
    /**
     * @Title: getPartyAddressNid
     * @Description:
     * @param stmt
     * stmt
     * @param customerId
     * customerId
     * @return long
     * @throws SQLException
     */
    public static final long getPartyAddressNid(final Statement stmt,
            final String customerId) throws SQLException {
        long partyAddressNid = -1;
        String sqlQuery = "select nid from node where title='" + customerId
                + "'";
        ResultSet rs3 = stmt.executeQuery(sqlQuery);
        if (rs3.next()) {
            partyAddressNid = rs3.getInt("nid");
        }
        return partyAddressNid;
    }
    /**
     * @Title: getServiceTypeNid
     * @Description:
     * @param stmt
     * stmt
     * @param serviceType
     * serviceType
     * @return long
     * @throws SQLException
     */
    public static final long getServiceTypeNid(final Statement stmt,
            final String serviceType) throws SQLException {
        long serviceTypeNid = -1;
        String sqlQuery = "select nid from node where title='" + serviceType
                + "' and type='device_service_type'";
        ResultSet rs3 = stmt.executeQuery(sqlQuery);
        if (rs3.next()) {
            serviceTypeNid = rs3.getInt("nid");
        }
        return serviceTypeNid;
    }
    /**
     * @Title: getPersonNid
     * @Description:
     * @param stmt
     * stmt
     * @param personName
     * personName
     * @return long
     * @throws SQLException
     */
    public static final long getPersonNid(final Statement stmt,
            final String personName) throws SQLException {
        long personNid = 0;
        String sqlQuery = "select nid from node where title='" + personName
                + "' and type='person'";
        ResultSet rs3 = stmt.executeQuery(sqlQuery);
        if (rs3.next()) {
            personNid = rs3.getInt("nid");
        }
        return personNid;
    }
    /**
     * @Title: getUserId
     * @Description:
     * @param stmt
     * stmt
     * @param userName
     * userName
     * @return long
     * @throws SQLException
     */
    public static final long getUserId(final Statement stmt,
            final String userName) throws SQLException {
        long userid = 0;
        String sqlQuery = "select uid from users where mail='" + userName + "'";
        ResultSet rs3 = stmt.executeQuery(sqlQuery);
        if (rs3.next()) {
            userid = rs3.getInt("uid");
        }
        return userid;
    }
    /**
     * @Title: getDeviceType
     * @Description:
     * @param stmt
     * stmt
     * @param sku
     * sku
     * @param sourceSystem
     * sourceSystem
     * @return String
     * @throws SQLException
     */
    public static final String getDeviceType(final Statement stmt,
            final String sku, final String sourceSystem) throws SQLException {
        String sqlQuery = "select title from node join content_type_sku on "
                + "content_type_sku.field_device_type_pk_nid=node.nid where "
                + "content_type_sku.field_sku_id_value='" + sku + "'"
                + " and content_type_sku.field_source_system_value='"
                + sourceSystem + "'";
        String deviceType = null;
        ResultSet rs3 = stmt.executeQuery(sqlQuery);
        if (rs3.next()) {
            deviceType = rs3.getString("title");
        }
        return deviceType;

    }
    /**
     * @Title: getSerialNumberValidation
     * @Description:
     * @param stmt
     * stmt
     * @param deviceType
     * deviceType
     * @return String
     * @throws SQLException
     */
    public static final String getSerialNumberValidation(final Statement stmt,
            final String deviceType) throws SQLException {
        String sqlQuery = "select field_serial_number_regex_value from content_type_devicetype "
                + "join node on node.nid=content_type_devicetype.nid where title='"
                + deviceType + "'";
        String serialNumberRegex = null;
        ResultSet rs3 = stmt.executeQuery(sqlQuery);
        if (rs3.next()) {
            serialNumberRegex = rs3
                    .getString("field_serial_number_regex_value");
        }
        return serialNumberRegex;
    }
    /**
     * @Title: checkDuplicateSerialNumber
     * @Description:
     * @param stmt
     * stmt
     * @param deviceType
     * deviceType
     * @param serialNumber
     * serialNumber
     * @return boolean
     * @throws SQLException
     */
    public static final boolean checkDuplicateSerialNumber(
            final Statement stmt, final String deviceType,
            final String serialNumber) throws SQLException {
        String sqlQuery = " select content_type_device.nid from content_type_device "
                + "join content_field_device_type on content_field_device_type.nid=content_type_device.nid "
                + "join node on node.nid=content_field_device_type.field_device_type_nid "
                + "where field_device_serial_number_value='3fe26f78d014e26b' and node.title='PB980_Ventilator'";
        ResultSet rs3 = stmt.executeQuery(sqlQuery);
        if (rs3 != null && rs3.next() && rs3.getInt("nid") > 0) {
            return true;
        }
        return false;
    }
    /**
     * @Title: getDeviceTypeSerialNumbers
     * @Description:
     * @param stmt
     * stmt
     * @param deviceType
     * deviceType
     * @return ArrayList<String>
     * @throws SQLException
     */
    public static final ArrayList<String> getDeviceTypeSerialNumbers(
            final Statement stmt, final String deviceType) throws SQLException {
        String sqlQuery = "select field_device_serial_number_value from content_type_device "
                + "join content_field_device_type on content_field_device_type.nid=content_type_device.nid "
                + "join node on node.nid=content_field_device_type.field_device_type_nid "
                + "where node.title='" + deviceType + "'";
        ResultSet rs3 = stmt.executeQuery(sqlQuery);
        ArrayList<String> serialNumbers = new ArrayList<String>();
        if (rs3 != null) {
            while (rs3.next()) {
                serialNumbers.add(rs3
                        .getString("field_device_serial_number_value"));
            }
        }
        return serialNumbers;
    }
    /**
     * @Title: checkAnyCustomerRecordAddedByETL
     * @Description:
     * @param stmt
     * stmt
     * @return boolean
     * @throws SQLException
     */
    public static final boolean checkAnyCustomerRecordAddedByETL(
            final Statement stmt) throws SQLException {
        String sqlQuery = "select count(nid) as count from customer_responce_status";
        ResultSet rs3 = stmt.executeQuery(sqlQuery);
        if (rs3 != null && rs3.next() && rs3.getInt("count") > 0) {
            return true;
        }
        return false;
    }
    /**
     * @Title: checkAnyLocationRecordAddedByETL
     * @Description:
     * @param stmt
     * stmt
     * @return boolean
     * @throws SQLException
     */
    public static final boolean checkAnyLocationRecordAddedByETL(
            final Statement stmt) throws SQLException {
        String sqlQuery = "select count(nid) as count from location_responce_status";
        ResultSet rs3 = stmt.executeQuery(sqlQuery);
        if (rs3 != null && rs3.next() && rs3.getInt("count") > 0) {
            return true;
        }
        return false;
    }
    /**
     * @Title: checkAnyLocationRoleRecordAddedByETL
     * @Description:
     * @param stmt
     * stmt
     * @return boolean
     * @throws SQLException
     */
    public static final boolean checkAnyLocationRoleRecordAddedByETL(
            final Statement stmt) throws SQLException {
        String sqlQuery = "select count(nid) as count from location_role_responce_status";
        ResultSet rs3 = stmt.executeQuery(sqlQuery);
        if (rs3 != null && rs3.next() && rs3.getInt("count") > 0) {
            return true;
        }
        return false;
    }

}
