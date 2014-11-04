package com.covidien.etl.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;

import com.covidien.etl.common.DeviceValidateResult;
import com.covidien.etl.common.EtlException;
import com.covidien.etl.common.EtlType;
import com.covidien.etl.dbstore.DBUtiltityFunctions;
import com.covidien.etl.log.EtlLogger;
import com.covidien.etl.log.EtlLoggerFactory;
import com.covidien.etl.model.Device;
import com.covidien.etl.validate.DeviceValidator;

/**
 * @ClassName: DeviceDAO
 * @Description:
 */
public class DeviceDAO extends BaseDAO<Device> {
    /**
     * Define a log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(DeviceDAO.class);
    /**
     * Define a etl log instance.
     */
    private static final EtlLogger ETLLOGGER = EtlLoggerFactory.getLogger();
    @Override
    public final void process(final List<Device> list) {

        LOGGER.info("begin to deal with Device objects.");
        LOGGER.info("Device size is :" + list.size());
        Connection con = null;
        Statement stmt = null;

        try {
            con = getDbConnection().getConnection();
            stmt = con.createStatement();
        } catch (SQLException e) {
            LOGGER.error("Exception:", e);
        }

        for (Device device : list) {

            // ----validate sn and sku--
            boolean isSNError = false;
            String errorType = validateSkuAndSN(device);
            if (errorType.equals("SKUError")) {
                continue;
            } else if (errorType.equals("SNError")) {
                isSNError = true;
            }

            String devicePsId = device.getSERIAL_NUMBER() + ":"
                    + device.getSKU();
            long nid = -1;
            try {
                nid = super.getXrefIdByPsId(stmt, EtlType.Device, devicePsId);
            } catch (SQLException e1) {
                LOGGER.error("Exception:", e1);
            }

            if (nid != -1) {
                if (device.getIS_DELETED() == 1) {
                    if (isSNError) {
                        ETLLOGGER.failDelete(EtlType.Device, device);
                        continue;
                    }
                    try {
                        deleteDevice(con, nid);
                        super.deleteXrefByPsId(con, EtlType.Device, devicePsId);
                        con.commit();
                        ETLLOGGER.successDelete(EtlType.Device, device);
                    } catch (Exception e) {
                        try {
                            con.rollback();
                        } catch (SQLException e1) {
                            LOGGER.error("Exception:", e1);
                        }
                        LOGGER.error("Exception:", e);
                        device.setException(e.getMessage());
                        ETLLOGGER.failDelete(EtlType.Device, device);
                    }
                    continue;
                }
                if (isSNError) {
                    ETLLOGGER.failUpdate(EtlType.Device, device);
                    continue;
                }

                try {
                    if (!shouldChange(stmt, EtlType.Device,
                            device.getSERIAL_NUMBER() + ":" + device.getSKU(),
                            formateDate(device.getLAST_CHANGE_DATE()))) {
                        continue;
                    }

                    LOGGER.info("update device:" + device.getSERIAL_NUMBER());
                    this.updateBySerialNumber(stmt, nid, device);
                    this.cleanCache(con);
                    con.commit();
                    ETLLOGGER.successUpdate(EtlType.Device, device);
                } catch (Exception e) {
                    try {
                        con.rollback();
                    } catch (SQLException e1) {
                        LOGGER.error("Exception:", e1);
                    }
                    LOGGER.error("Exception:", e);
                    device.setException(e.getMessage());
                    ETLLOGGER.failUpdate(EtlType.Device, device);
                }
                continue;
            }

            if (isSNError) {
                ETLLOGGER.failInsert(EtlType.Device, device);
                continue;
            }
            processBody(con, device, devicePsId);
        }
        try {
            stmt.close();
        } catch (SQLException e) {
            LOGGER.error("Exception:", e);
        }
    }
    /**
     * @Title: processBody
     * @Description:
     * @param con
     * con
     * @param device
     * device
     * @param devicePsId
     * devicePsId
     */
    private void processBody(final Connection con, final Device device,
            final String devicePsId) {
        LOGGER.info("insert device:" + devicePsId);
        long deviceInstallationNid = -1;
        long serviceTypeNid = -1;
        long personNid = -1;
        long etlAdmin = -1;
        long deviceTypeNid = -1;
        StringBuilder nodeRevisions = new StringBuilder();
        StringBuilder node = new StringBuilder();
        StringBuilder deviceStr = new StringBuilder();
        StringBuilder contentFieldDeviceType = new StringBuilder();
        StringBuilder deviceInstallation = new StringBuilder();
        StringBuilder contentFieldDevice = new StringBuilder();
        StringBuilder deviceServiceHistory = new StringBuilder();
        String deviceType = null;
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            serviceTypeNid = DBUtiltityFunctions.getServiceTypeNid(stmt,
                    "Device Registration");
            personNid = DBUtiltityFunctions.getPersonNid(stmt,
                    super.getEtlUser());
            etlAdmin = DBUtiltityFunctions.getUserId(stmt, super.getEtlUser());
        } catch (SQLException e) {
            LOGGER.error("Exception:", e);
        }

        try {
            deviceType = getDeviceType(stmt, device.getSKU(),
                    device.getSOURCE_SYSTEM());
            if (deviceType == null) {
                throw new EtlException("Couldn't find deviceType. Sku:"
                        + device.getSKU() + ";Source system:"
                        + device.getSOURCE_SYSTEM());
            }
            deviceTypeNid = DBUtiltityFunctions.getDeviceTypeNid(stmt,
                    deviceType);

            if (deviceTypeNid == 0) {
                throw new EtlException("Couldn't find deviceType Nid. Sku:"
                        + device.getSKU() + ";Source system:"
                        + device.getSOURCE_SYSTEM());
            }

            long customerNid = getCustomerNid(stmt, device);
            if (customerNid == -1) {
                throw new EtlException("No customer found for device:"
                        + device.getSERIAL_NUMBER());
            }
            nodeRevisions.append(etlAdmin + ",'"
                    + device.getSERIAL_NUMBER().replace("'", "\\'")
                    + "','','','',unix_timestamp(),0),");
            node.append("'device','','"
                    + device.getSERIAL_NUMBER().replace("'", "\\'") + "',"
                    + etlAdmin
                    + ",'1',unix_timestamp(),unix_timestamp(),0,0,0),");
            if (device.getMAINTENANCE_EXPIRATION_DATE() == null
                    || device.getMAINTENANCE_EXPIRATION_DATE().equals("null")) {
                deviceStr.append("'" + device.getSERIAL_NUMBER() + "',1,"
                        + customerNid + ",NULL),");
            } else {
                deviceStr.append("'" + device.getSERIAL_NUMBER() + "',1,"
                        + customerNid + ",'"
                        + device.getMAINTENANCE_EXPIRATION_DATE() + "'),");
            }

            contentFieldDeviceType.append(deviceTypeNid + ") ");
            LOGGER.info("deviceStr is :" + deviceStr);
            long deviceNid = this.insert(stmt, nodeRevisions, node, deviceStr,
                    contentFieldDeviceType, null, null, null);

            nodeRevisions = new StringBuilder();
            node = new StringBuilder();
            deviceStr = new StringBuilder();

            nodeRevisions.append(etlAdmin + ",'"
                    + "Device Installation','','','',unix_timestamp(),0),");
            node.append("'device_installation','','Device Installation" + "',"
                    + etlAdmin
                    + ",'1',unix_timestamp(),unix_timestamp(),0,0,0),");
            long countryNid = getCountryNid(stmt, device);

            long locationNid = getLocationNid(stmt, device);
            if (locationNid == -1) {
                throw new EtlException("No location found for device:"
                        + device.getSERIAL_NUMBER());
            }
            deviceInstallation.append(countryNid + "," + locationNid + "),");
            contentFieldDevice.append(deviceNid + ") ");

            deviceInstallationNid = this.insert(stmt, nodeRevisions, node,
                    null, null, deviceInstallation, contentFieldDevice,
                    deviceServiceHistory);

            nodeRevisions = new StringBuilder();
            node = new StringBuilder();

            nodeRevisions.append(etlAdmin + ",'"
                    + "Device Service','','','',unix_timestamp(),0),");
            node.append("'device_service_history','','Device Service" + "',"
                    + etlAdmin
                    + ",'1',unix_timestamp(),unix_timestamp(),0,0,0),");

            deviceServiceHistory.append(deviceInstallationNid + ","
                    + serviceTypeNid + "," + personNid
                    + ",'Device Registered',current_timestamp()),");

            LOGGER.info("contentFieldDeviceType is :" + contentFieldDeviceType);
            LOGGER.info("contentFieldDevice is :" + contentFieldDevice);

            locationNid = this.insert(stmt, nodeRevisions, node, deviceStr,
                    null, deviceInstallation, contentFieldDevice,
                    deviceServiceHistory);

            super.insertXref(con, EtlType.Device, deviceNid, devicePsId,
                    device.getLAST_CHANGE_DATE());
            con.commit();
            ETLLOGGER.successInsert(EtlType.Device, device);
        } catch (Exception e) {
            try {
                con.rollback();
            } catch (SQLException e1) {
                LOGGER.error("Exception:", e1);
            }
            LOGGER.error("Exception:", e);
            device.setException(e.getMessage());
            ETLLOGGER.failInsert(EtlType.Device, device);
        }
    }
    /**
     * @Title: validateSkuAndSN
     * @Description:
     * @param device
     * device
     * @return String
     */
    private String validateSkuAndSN(final Device device) {
        String error = "";
        DeviceValidateResult validateResult = DeviceValidator.getInstance()
                .validate(device);
        switch (validateResult) {
        case SKUError:
            LOGGER.error("Device SKU validate failed. The device is :"
                    + device.getSERIAL_NUMBER() + "," + device.getSKU());
            ETLLOGGER.failValidate(EtlType.Device,
                    DeviceValidateResult.SKUError, device);
            error = "SKUError";
        case SNError:
            LOGGER.error("Device SN validate failed. The device is :"
                    + device.getSERIAL_NUMBER() + "," + device.getSKU());
            ETLLOGGER.failValidate(EtlType.Device,
                    DeviceValidateResult.SNError, device);
            device.setException("SN Validation Error! SN:"
                    + device.getSERIAL_NUMBER());
            error = "SNError";
        default:
            break;
        }
        return error;
    }
    /**
     * @Title: deleteDevice
     * @Description:
     * @param con
     * con
     * @param nid
     * nid
     * @throws Exception
     */
    private void deleteDevice(final Connection con, final long nid)
            throws Exception {
        super.setExpired(con, nid);
    }
    /**
     * @Title: getCountryNid
     * @Description:
     * @param stmt
     * stmt
     * @param device
     * device
     * @return long
     */
    private long getCountryNid(final Statement stmt, final Device device) {
        long countryNid = -1;
        try {
            countryNid = DBUtiltityFunctions.getCountryNid(stmt,
                    device.getINSTALL_COUNTRY_CODE());
        } catch (SQLException e) {
            LOGGER.error("Exception:", e);
        }
        return countryNid;
    }
    /**
     * @Title: getLocationNid
     * @Description:
     * @param stmt
     * stmt
     * @param device
     * device
     * @return long
     */
    private long getLocationNid(final Statement stmt, final Device device) {
        long locationNid = -1;
        try {
            locationNid = DBUtiltityFunctions.getPostalAddressRefNid(stmt,
                    device.getLOCATION_ID());
        } catch (SQLException e) {
            LOGGER.error("Exception:", e);
        }
        return locationNid;
    }
    /**
     * @Title: getCustomerNid
     * @Description:
     * @param stmt
     * stmt
     * @param device
     * device
     * @return long
     */
    private long getCustomerNid(final Statement stmt, final Device device) {
        long unknownCustomer = -1;
        try {
            unknownCustomer = DBUtiltityFunctions.getunknownCustomerNid(stmt);
        } catch (SQLException e) {
            LOGGER.error("Exception:", e);
        }

        long customerNid = -1;
        if (device.getCUSTOMER_ID() == null) {
            customerNid = unknownCustomer;
        } else {
            try {
                customerNid = DBUtiltityFunctions.getCustomerNid(stmt,
                        device.getCUSTOMER_ID());
            } catch (SQLException e) {
                LOGGER.error("Exception:", e);
            }
        }
        return customerNid;
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
     * @throws SQLException
     * @return String
     */
    private String getDeviceType(final Statement stmt, final String sku,
            final String sourceSystem) throws SQLException {
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
     * @Title: updateBySerialNumber
     * @Description:
     * @param stmt
     * stmt
     * @param currentDeviceNid
     * currentDeviceNid
     * @param device
     * device
     * @throws Exception
     */
    private void updateBySerialNumber(final Statement stmt,
            final long currentDeviceNid, final Device device) throws Exception {

        String maintainaceExpireDate = device.getMAINTENANCE_EXPIRATION_DATE();

        long countryNid = getCountryNid(stmt, device);
        long locationNid = getLocationNid(stmt, device);
        long customerNid = getCustomerNid(stmt, device);

        stmt.executeUpdate("update content_type_device set field_device_owner_nid="
                + customerNid
                + ", field_maintance_expiration_date_value='"
                + maintainaceExpireDate + "' where nid=" + currentDeviceNid);

        stmt.executeUpdate("update content_type_device_installation set field_device_country_nid="
                + countryNid
                + ", field_location_id_nid="
                + locationNid
                + " where nid in (select nid from content_field_device_pk where field_device_pk_nid="
                + currentDeviceNid + ")");

    }
    /**
     * @Title: insert
     * @Description:
     * @param stmt
     * stmt
     * @param nodeRevisions
     * nodeRevisions
     * @param node
     * node
     * @param deviceStr
     * deviceStr
     * @param contentFieldDeviceType
     * contentFieldDeviceType
     * @param deviceInstallation
     * deviceInstallation
     * @param contentFieldDevice
     * contentFieldDevice
     * @param deviceServiceHistory
     * deviceServiceHistory
     * @throws Exception
     * @return long
     */
    private long insert(final Statement stmt,
            final StringBuilder nodeRevisions, final StringBuilder node,
            final StringBuilder deviceStr,
            final StringBuilder contentFieldDeviceType,
            final StringBuilder deviceInstallation,
            final StringBuilder contentFieldDevice,
            final StringBuilder deviceServiceHistory) throws Exception {

        long nid = -1;

        long vid = super
                .insertWithReturnKey(
                        stmt.getConnection(),
                        "insert into node_revisions (nid,uid,title,body,teaser,log,timestamp,format) values (0,"
                                + nodeRevisions.substring(0,
                                        nodeRevisions.length() - 1));
        nid = super
                .insertWithReturnKey(
                        stmt.getConnection(),
                        "insert into node (vid,type,language,title,uid,status,created,changed,comment,promote,"
                                + "translate) values ("
                                + vid
                                + ","
                                + node.substring(0, node.length() - 1));
        super.updateNidWithVid(stmt.getConnection(), vid, nid);

        if (deviceStr != null && deviceStr.length() > 0) {
            stmt.executeUpdate("insert into content_type_device (nid, vid, field_device_serial_number_value, "
                    + "field_device_is_active_value, field_device_owner_nid, field_maintance_expiration_date_value) values ("
                    + nid
                    + ","
                    + vid
                    + ","
                    + deviceStr.substring(0, deviceStr.length() - 1));
        }
        if (contentFieldDeviceType != null
                && contentFieldDeviceType.length() > 0) {
            stmt.executeUpdate("insert into content_field_device_type (nid, vid, field_device_type_nid) values ("
                    + nid
                    + ","
                    + vid
                    + ","
                    + contentFieldDeviceType.substring(0,
                            contentFieldDeviceType.length() - 1));
        }
        if (deviceInstallation != null && deviceInstallation.length() > 0) {
            stmt.executeUpdate("insert into content_type_device_installation (nid, vid, field_device_country_nid, field_location_id_nid) values("
                    + nid
                    + ","
                    + vid
                    + ","
                    + deviceInstallation.substring(0,
                            deviceInstallation.length() - 1));
        }
        if (contentFieldDevice != null && contentFieldDevice.length() > 0) {
            stmt.executeUpdate("insert into content_field_device_pk (nid, vid, field_device_pk_nid) values ("
                    + nid
                    + ","
                    + vid
                    + ","
                    + contentFieldDevice.substring(0,
                            contentFieldDevice.length() - 1));
        }

        // stmt.addBatch("insert into content_field_facility_pk (nid, vid) values ("+nid+","
        // + contentFieldFacility.substring(0, contentFieldFacility
        // .length() - 1));

        if (deviceServiceHistory != null && deviceServiceHistory.length() > 0) {
            stmt.executeUpdate("insert into content_type_device_service_history (nid, vid, field_device_installation_pk_nid, "
                    + "field_device_service_type_nid, field_service_person_pk_nid, field_service_note_value, field_service_datetime_value) values ("
                    + nid
                    + ","
                    + vid
                    + ","
                    + deviceServiceHistory.substring(0,
                            deviceServiceHistory.length() - 1));
        }

        return nid;
    }
}
