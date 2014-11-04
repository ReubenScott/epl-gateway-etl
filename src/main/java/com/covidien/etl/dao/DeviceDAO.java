package com.covidien.etl.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.covidien.etl.common.DeviceValidateResult;
import com.covidien.etl.common.EtlException;
import com.covidien.etl.common.EtlType;
import com.covidien.etl.dao.helper.DeviceDAOSQLHelper;
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
    private final EtlLogger etlloger = EtlLoggerFactory.getLogger();
    /**
     * Define a variable to store device type.
     */
    private String deviceType = null;
    /**
     * Define a variable to store xref devicePsId.
     */
    private String devicePsId = null;
    /**
     * Define a sku and sn validator.
     */
    private DeviceValidator validator = DeviceValidator.getInstance();
    /**
     * Define a database connection.
     */
    private Connection con = null;
    /**
     * serivce type nid.
     */
    private long serviceTypeNid = -1;
    /**
     * person nid.
     */
    private long personNid = -1;
    /**
     * etl user nid.
     */
    private long etlAdmin = -1;
    /**
     * count the execute record.
     */
    private long count = 0L;
    /**
     * Define a save point.
     */
    private static volatile Savepoint savepoint = null;
    /**
     * deviceInstallationNid.
     */
    private long deviceInstallationNid = -1;
    /**
     * deviceTypeNid.
     */
    private long deviceTypeNid = -1;
    /**
     * nodeRevisions.
     */
    private Map<String, String> nodeRevisions = new HashMap<String, String>();
    /**
     * node.
     */
    private Map<String, String> node = new HashMap<String, String>();
    /**
     * deviceStr.
     */
    private Map<String, String> deviceStr = new HashMap<String, String>();
    /**
     * contentFieldDeviceType.
     */
    private String contentFieldDeviceType = null;
    /**
     * deviceInstallation.
     */
    private Map<String, String> deviceInstallation = new HashMap<String, String>();
    /**
     * contentFieldDevice.
     */
    private String contentFieldDevice = null;
    /**
     * deviceServiceHistory.
     */
    private Map<String, String> deviceServiceHistory = new HashMap<String, String>();

    @Override
    public final void process(List<Device> list) {

        try {
            con = getDbConnection().getConnection();
            con.setAutoCommit(false);
            serviceTypeNid = DBUtiltityFunctions.getServiceTypeNid("Device Registration");
            personNid = DBUtiltityFunctions.getPersonNid(super.getEtlUser());
            etlAdmin = DBUtiltityFunctions.getUserId(super.getEtlUser());
        } catch (SQLException e) {
            LOGGER.error("Exception:", e);
            return;
        }

        for (Device device : list) {
            count++;
            if (count >= 100 * 3) {
                try {
                    con.commit();
                } catch (SQLException e) {
                    LOGGER.error("Exception: When commit", e);
                }
                count = 0L;
            }
            // ----validate sn and sku--
            boolean isSNError = false;
            String errorType = validateSkuAndSN(device);
            if (errorType.equals("SKUError")) {
                continue;
            } else if (errorType.equals("SNError")) {
                isSNError = true;
            }

            try {
                deviceType = getDeviceType(device.getSku(), device.getSourceSystem());
            } catch (SQLException e) {
                LOGGER.error("Exception:", e);
            }
            devicePsId = device.getSerialNumber() + ":" + deviceType;
            long nid = -1;
            try {
                nid = super.getXrefIdByPsId(EtlType.Device, devicePsId);
            } catch (SQLException e1) {
                LOGGER.error("Exception:", e1);
            }

            //check device in node table but not in xref table.
            if (nid == -1) {
                nid = getDeviceNidFromNode(device.getSerialNumber(), devicePsId);
            }

            if (device.getIsDeleted() == 1) {
                try {
                    if (isSNError) {
                        etlloger.failDelete(EtlType.Device, device);
                        continue;
                    }
                    if (nid == -1) {
                        throw new Exception("Device doesn't exist!");
                    }
                    deleteDevice(nid);
                    super.deleteXrefByPsId(EtlType.Device, devicePsId);
                    con.releaseSavepoint(savepoint);
                    savepoint = con.setSavepoint();
                    //                    con.commit();
                    etlloger.successDelete(EtlType.Device, device);
                } catch (Exception e) {
                    try {
                        if (savepoint != null) {
                            con.rollback(savepoint);
                        } else {
                            con.rollback();
                        }
                    } catch (SQLException e1) {
                        LOGGER.error("Exception:", e1);
                    }
                    LOGGER.error("Exception:", e);
                    device.setException(e.getMessage());
                    etlloger.failDelete(EtlType.Device, device);
                }
                continue;
            }

            if (nid != -1) {
                if (isSNError) {
                    etlloger.failUpdate(EtlType.Device, device);
                    continue;
                }

                try {
                    if (!shouldChange(EtlType.Device, devicePsId, formateDate(device.getLastChangeDate()))) {
                        continue;
                    }

                    LOGGER.info("update device:" + device.getSerialNumber());
                    this.updateBySerialNumber(nid, device);
                    con.releaseSavepoint(savepoint);
                    savepoint = con.setSavepoint();
                    //                    con.commit();
                    etlloger.successUpdate(EtlType.Device, device);
                } catch (Exception e) {
                    try {
                        if (savepoint != null) {
                            con.rollback(savepoint);
                        } else {
                            con.rollback();
                        }
                    } catch (SQLException e1) {
                        LOGGER.error("Exception:", e1);
                    }
                    LOGGER.error("Exception:", e);
                    device.setException(e.getMessage());
                    etlloger.failUpdate(EtlType.Device, device);
                }
                continue;
            }

            if (isSNError) {
                etlloger.failInsert(EtlType.Device, device);
                continue;
            }

            //check the format of Last Change Date.
            try {
                super.formateDate(device.getLastChangeDate());
                super.formateDate(device.getMaintenanceExpirationDate());
            } catch (Exception e) {
                LOGGER.error("Exception:", e);
                device.setException(e.getMessage());
                etlloger.failInsert(EtlType.Device, device);
                continue;
            }
            try {
                processBody(device);
            } catch (Exception e) {
                LOGGER.error("Exception:", e);
                device.setException(e.getMessage());
                etlloger.failInsert(EtlType.Device, device);
                continue;
            }
        }
        list.clear();
        list = null;
        cleanCache();
        try {
            con.commit();
            //            con.close();
        } catch (SQLException e) {
            LOGGER.error("Exception:", e);
        }
    }

    /**
     * @Title: processBody
     * @param device
     *        device
     * @throws SQLException
     *         SQLException
     */
    private void processBody(final Device device)
        throws SQLException {
        deviceInstallationNid = -1;
        deviceTypeNid = -1;
        contentFieldDeviceType = null;
        contentFieldDevice = null;
        try {
            if (deviceType == null) {
                throw new EtlException("Couldn't find deviceType. Sku:" + device.getSku() + ";Source system:"
                        + device.getSourceSystem());
            }
            deviceTypeNid = DBUtiltityFunctions.getDeviceTypeNid(deviceType);
            if (deviceTypeNid == -1) {
                throw new EtlException("Couldn't find deviceType Nid. Sku:" + device.getSku() + ";Source system:"
                        + device.getSourceSystem());
            }

            long customerAcctNid = getCustomerAcctNid(device);
            if (customerAcctNid == -1) {
                throw new EtlException("No customer found for device:" + device.getSerialNumber());
            }

            long locationNid = getLocationNid(device);
            if (locationNid == -1) {
                throw new EtlException("No location found for device:" + device.getSerialNumber());
            }

            String title = device.getSerialNumber();
            if (title == null || title == "") {
                throw new EtlException("SerialNumber cannot be empty!");
            }
            long countryNid = getCountryNid(device);
            if (countryNid == -1) {
                throw new EtlException("Install Country Code is invalid!");
            }
            nodeRevisions.clear();
            node.clear();
            deviceStr.clear();
            nodeRevisions.put("uid", String.valueOf(etlAdmin));
            nodeRevisions.put("title", device.getSerialNumber());

            node.put("uid", String.valueOf(etlAdmin));
            node.put("title", device.getSerialNumber());
            node.put("type", "device");

            deviceStr.put("expirationDate", device.getMaintenanceExpirationDate());
            deviceStr.put("serialNumber", device.getSerialNumber());
            deviceStr.put("customerAcctNid", String.valueOf(customerAcctNid));

            contentFieldDeviceType = String.valueOf(deviceTypeNid);
            long deviceNid = this.insert(nodeRevisions, node, deviceStr, contentFieldDeviceType, null, null, null);

            nodeRevisions.clear();
            node.clear();
            deviceInstallation.clear();

            nodeRevisions.put("uid", String.valueOf(etlAdmin));
            nodeRevisions.put("title", "Device Installation");

            node.put("uid", String.valueOf(etlAdmin));
            node.put("title", "Device Installation");
            node.put("type", "device_installation");

            deviceInstallation.put("countryNid", String.valueOf(countryNid));
            deviceInstallation.put("locationNid", String.valueOf(locationNid));

            contentFieldDevice = String.valueOf(deviceNid);

            deviceInstallationNid = this.insert(nodeRevisions, node, null, null, deviceInstallation,
                    contentFieldDevice, null);
            nodeRevisions.clear();
            node.clear();
            deviceServiceHistory.clear();

            nodeRevisions.put("uid", String.valueOf(etlAdmin));
            nodeRevisions.put("title", "Device Service");

            node.put("uid", String.valueOf(etlAdmin));
            node.put("title", "Device Service");
            node.put("type", "device_service_history");

            deviceServiceHistory.put("installationNid", String.valueOf(deviceInstallationNid));
            deviceServiceHistory.put("serviceTypeNid", String.valueOf(serviceTypeNid));
            deviceServiceHistory.put("servicePersonNid", String.valueOf(personNid));

            this.insert(nodeRevisions, node, null, null, null, contentFieldDevice, deviceServiceHistory);

            super.insertXref(EtlType.Device, deviceNid, devicePsId, device.getLastChangeDate());
            con.releaseSavepoint(savepoint);
            savepoint = con.setSavepoint();
            //            con.commit();
            LOGGER.info("inserted device is:" + devicePsId);
            etlloger.successInsert(EtlType.Device, device);
        } catch (Exception e) {
            LOGGER.error("Exception:", e);
            device.setException(e.getMessage());
            etlloger.failInsert(EtlType.Device, device);
        }
    }

    /**
     * @Title: validateSkuAndSN
     * @Description:
     * @param device
     *        device
     * @return String
     */
    private String validateSkuAndSN(final Device device) {
        String error = "";
        DeviceValidateResult validateResult = validator.validate(device);
        switch (validateResult) {
        case SKUError:
            LOGGER.error("Device SKU validate failed. The device is :" + device.getSerialNumber() + ","
                    + device.getSku());
            device.setException("SKU Validation Error! SKU:" + device.getSku());
            etlloger.failValidate(EtlType.Device, DeviceValidateResult.SKUError, device);
            error = "SKUError";
            break;
        case SNError:
            LOGGER.error("Device SN validate failed. The device is :" + device.getSerialNumber() + ","
                    + device.getSku());
            device.setException("SN Validation Error! SN:" + device.getSerialNumber());
            etlloger.failValidate(EtlType.Device, DeviceValidateResult.SNError, device);
            error = "SNError";
            break;
        default:
            break;
        }
        validator.rebuild();
        return error;
    }

    /**
     * @Title: deleteDevice
     * @param nid
     *        nid
     * @throws Exception
     *         Exception
     */
    private void deleteDevice(final long nid)
        throws Exception {
        super.setExpired(nid);
    }

    /**
     * @Title: getCountryNid
     * @param device
     *        device
     * @return long
     * @throws SQLException
     *         SQLException
     */
    private long getCountryNid(final Device device)
        throws SQLException {
        long countryNid = -1;
        if (device.getInstallCountryCode() != null && device.getInstallCountryCode() != "") {
            countryNid = DBUtiltityFunctions.getCountryNid(device.getInstallCountryCode());
        }
        return countryNid;
    }

    /**
     * @Title: getLocationNid
     * @param device
     *        device
     * @return long
     */
    private long getLocationNid(final Device device) {
        long locationNid = -1;
        try {
            locationNid = DBUtiltityFunctions.getPostalAddressRefNid(device.getLocationId());
        } catch (SQLException e) {
            LOGGER.error("Exception:", e);
        }
        return locationNid;
    }

    /**
     * @Title: getCustomerNid
     * @param device
     *        device
     * @return long
     */
    private long getCustomerAcctNid(final Device device) {

        long customerNid = -1;
        if (device.getCustomerId() == null || device.getCustomerId() == "") {
            long unknownCustomer = -1;
            try {
                unknownCustomer = DBUtiltityFunctions.getunknownCustomerNid();
            } catch (SQLException e) {
                LOGGER.error("Exception:", e);
            }
            customerNid = unknownCustomer;
        } else {
            try {
                customerNid = DBUtiltityFunctions.getCustomerAcctNid(device.getCustomerId());
            } catch (SQLException e) {
                LOGGER.error("Exception:", e);
            }
        }
        return customerNid;
    }

    /**
     * get device owner nid by device serial numner.
     * 
     * @Title: getDeviceOwnerNid
     * @param serialNumber
     *        device serial number.
     * @return owner person nid.
     * @throws SQLException
     *         SQLException
     */
    private long getDeviceOwnerNid(final String serialNumber)
        throws SQLException {
        long ownerNid = -1;
        PreparedStatement pstmt = DeviceDAOSQLHelper.getPstmtN();
        pstmt.setString(1, serialNumber);
        ResultSet rs3 = pstmt.executeQuery();
        if (rs3.next()) {
            ownerNid = rs3.getLong("field_device_owner_nid");
        }
        return ownerNid;

    }

    /**
     * get the privilege of last modified person for installation.
     * 
     * @Title: getModifiedPrivilegeOfInstallation
     * @param currentDeviceNid
     *        device nid.
     * @return modified privilege value.
     * @throws SQLException
     *         SQLException
     */
    private int getModifiedPrivilegeOfInstallation(final long currentDeviceNid)
        throws SQLException {
        int privilegeVal = -1;

        PreparedStatement pstmt = DeviceDAOSQLHelper.getPstmtL();
        pstmt.setLong(1, currentDeviceNid);
        ResultSet rs3 = pstmt.executeQuery();
        if (rs3.next()) {
            privilegeVal = rs3.getInt("modified_privilege");
        }
        return privilegeVal;
    }

    /**
     * get the privilege of last modified person for device owner.
     * 
     * @Title: getModifiedPrivilegeOfDeviceOwner
     * @param currentDeviceNid
     *        device nid.
     * @return modified privilege value.
     * @throws SQLException
     *         SQLException
     */
    private int getModifiedPrivilegeOfDeviceOwner(final long currentDeviceNid)
        throws SQLException {
        int privilegeVal = -1;

        PreparedStatement pstmt = DeviceDAOSQLHelper.getPstmtO();
        pstmt.setLong(1, currentDeviceNid);
        ResultSet rs3 = pstmt.executeQuery();
        if (rs3.next()) {
            privilegeVal = rs3.getInt("modified_owner_privilege");
        }
        return privilegeVal;
    }

    /**
     * @Title: getDeviceType
     * @param sku
     *        sku
     * @param sourceSystem
     *        sourceSystem
     * @throws SQLException
     *         SQLException
     * @return Device Type
     */
    private String getDeviceType(final String sku, final String sourceSystem)
        throws SQLException {
        PreparedStatement pstmt = null;
        String deviceTypeT = null;
        pstmt = DeviceDAOSQLHelper.getPstmtA();
        pstmt.setString(1, sku);
        pstmt.setString(2, sourceSystem);
        ResultSet rs3 = pstmt.executeQuery();
        if (rs3.next()) {
            deviceTypeT = rs3.getString("title");
        }
        rs3.close();
        return deviceTypeT;
    }

    /**
     * @Title: updateBySerialNumber
     * @param currentDeviceNid
     *        currentDeviceNid
     * @param device
     *        device
     * @throws Exception
     *         Exception
     */
    private void updateBySerialNumber(final long currentDeviceNid, final Device device)
        throws Exception {
        PreparedStatement pstmt = null;
        String maintainaceExpireDate = device.getMaintenanceExpirationDate();
        long countryNid = getCountryNid(device);
        long locationNid = getLocationNid(device);
        long newCustomerAcctNid = getCustomerAcctNid(device);
        long currentOwnerNid = getDeviceOwnerNid(device.getSerialNumber());
        long unknowCustomerNid = DBUtiltityFunctions.getunknownCustomerNid();
        int modifiedOwnerPrivilege = getModifiedPrivilegeOfDeviceOwner(currentDeviceNid);
        if (modifiedOwnerPrivilege >= 2 && unknowCustomerNid != currentOwnerNid) {
            newCustomerAcctNid = currentOwnerNid;
        }
        if (countryNid == -1) {
            throw new Exception("INSTALL_COUNTRY_CODE doesn't exist!");
        }
        if (locationNid == -1) {
            throw new Exception("LOCATION_ID doesn't exist!");
        }
        if (newCustomerAcctNid == -1) {
            throw new Exception("CUSTOMER_ID doesn't exist!");
        }
        pstmt = DeviceDAOSQLHelper.getPstmtB();
        pstmt.setLong(1, newCustomerAcctNid);
        pstmt.setString(2, maintainaceExpireDate);
        pstmt.setLong(3, currentDeviceNid);
        pstmt.executeUpdate();

        //update location.
        pstmt = DeviceDAOSQLHelper.getPstmtM();
        pstmt.setLong(1, locationNid);
        pstmt.setLong(2, currentDeviceNid);
        pstmt.executeUpdate();

        int modifiedPrivilege = getModifiedPrivilegeOfInstallation(currentDeviceNid);
        //country code change will be done by etl 
        //only if modifiedPrivilege less than 2(means is created by etl).
        if (modifiedPrivilege <= 2) {
            pstmt = DeviceDAOSQLHelper.getPstmtC();
            pstmt.setLong(1, countryNid);
            pstmt.setLong(2, 2);
            pstmt.setLong(3, currentDeviceNid);
            pstmt.executeUpdate();
        }

    }

    /**
     * @Title: getDeviceNidFromNode
     * @param deviceSerialNumbner
     *        deviceSerialNumbner
     * @param psId
     *        xref psId
     * @return device nid
     */
    private long getDeviceNidFromNode(String deviceSerialNumbner, String psId) {
        long nid = -1L;
        PreparedStatement pstmt = null;
        ResultSet result = null;
        pstmt = DeviceDAOSQLHelper.getPstmtK();
        try {
            pstmt.setString(1, deviceSerialNumbner);
            result = pstmt.executeQuery();
            if (result.next()) {
                nid = result.getLong("nid");
            }
            if (nid != -1) {
                insertXref(EtlType.Device, nid, psId, "1980-00-00 00:00:00");
            }
        } catch (Exception e) {
            LOGGER.error("Exception:", e);
        }
        return nid;
    }

    /**
     * @Title: insert
     * @param nodeRevisionsI
     *        nodeRevisions
     * @param nodeI
     *        node
     * @param deviceStrI
     *        deviceStr
     * @param contentFieldDeviceTypeI
     *        contentFieldDeviceType
     * @param deviceInstallationI
     *        deviceInstallation
     * @param contentFieldDeviceI
     *        contentFieldDevice
     * @param deviceServiceHistoryI
     *        deviceServiceHistory
     * @throws Exception
     *         Exception
     * @return long
     */
    private long insert(final Map<String, String> nodeRevisionsI, final Map<String, String> nodeI,
            final Map<String, String> deviceStrI, final String contentFieldDeviceTypeI,
            final Map<String, String> deviceInstallationI, final String contentFieldDeviceI,
            final Map<String, String> deviceServiceHistoryI)
        throws Exception {

        long nid = -1;
        long vid = -1;
        PreparedStatement pstmt = null;
        try {
            pstmt = DeviceDAOSQLHelper.getPstmtD();
            pstmt.setString(1, nodeRevisionsI.get("uid"));
            pstmt.setString(2, nodeRevisionsI.get("title"));
            vid = insertWithReturnKey(pstmt);

            pstmt = DeviceDAOSQLHelper.getPstmtE();
            pstmt.setLong(1, vid);
            pstmt.setString(2, nodeI.get("type"));
            pstmt.setString(3, nodeI.get("title"));
            pstmt.setString(4, nodeI.get("uid"));
            nid = insertWithReturnKey(pstmt);
            updateNidWithVid(vid, nid);
            super.updateNidWithVid(vid, nid);

            if (deviceStrI != null && deviceStrI.size() > 0) {
                pstmt = DeviceDAOSQLHelper.getPstmtF();
                pstmt.setLong(1, nid);
                pstmt.setLong(2, vid);
                pstmt.setString(3, deviceStrI.get("serialNumber"));
                pstmt.setString(4, deviceStrI.get("customerAcctNid"));
                pstmt.setString(5, deviceStrI.get("expirationDate"));
                pstmt.executeUpdate();
                super.addValidRecord2ExpirationTbl(nid, vid);
            }
            if (contentFieldDeviceTypeI != null && contentFieldDeviceTypeI.length() > 0) {
                pstmt = DeviceDAOSQLHelper.getPstmtG();
                pstmt.setLong(1, nid);
                pstmt.setLong(2, vid);
                pstmt.setLong(3, Long.valueOf(contentFieldDeviceTypeI).longValue());
                pstmt.executeUpdate();
            }
            if (deviceInstallationI != null && deviceInstallationI.size() > 0) {
                pstmt = DeviceDAOSQLHelper.getPstmtH();
                pstmt.setLong(1, nid);
                pstmt.setLong(2, vid);
                pstmt.setLong(3, Long.valueOf(deviceInstallationI.get("countryNid")).longValue());
                pstmt.setLong(4, Long.valueOf(deviceInstallationI.get("locationNid")).longValue());
                pstmt.executeUpdate();
            }
            if (contentFieldDeviceI != null && contentFieldDeviceI.length() > 0) {
                pstmt = DeviceDAOSQLHelper.getPstmtI();
                pstmt.setLong(1, nid);
                pstmt.setLong(2, vid);
                pstmt.setLong(3, Long.valueOf(contentFieldDeviceI).longValue());
                pstmt.executeUpdate();
            }

            // stmt.addBatch("insert into content_field_facility_pk (nid, vid) values ("+nid+","
            // + contentFieldFacility.substring(0, contentFieldFacility
            // .length() - 1));

            if (deviceServiceHistoryI != null && deviceServiceHistoryI.size() > 0) {
                pstmt = DeviceDAOSQLHelper.getPstmtJ();
                pstmt.setLong(1, nid);
                pstmt.setLong(2, vid);
                pstmt.setLong(3, Long.valueOf(deviceServiceHistoryI.get("installationNid")).longValue());
                pstmt.setLong(4, Long.valueOf(deviceServiceHistoryI.get("serviceTypeNid")).longValue());
                pstmt.setLong(5, Long.valueOf(deviceServiceHistoryI.get("servicePersonNid")).longValue());
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            if (pstmt != null) {
                LOGGER.error(pstmt.toString());
            }
            LOGGER.error("nid is: " + nid + "; vid is:" + vid);
            try {
                if (savepoint != null) {
                    con.rollback(savepoint);
                } else {
                    con.rollback();
                }
                LOGGER.info("Exception happened, data rollback!!!!!!");
            } catch (SQLException e1) {
                LOGGER.error("Exception:", e1);
            }
            throw e;
        }
        return nid;
    }
}
