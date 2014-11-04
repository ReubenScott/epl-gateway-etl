package com.covidien.etl.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.covidien.etl.common.EtlException;
import com.covidien.etl.common.EtlType;
import com.covidien.etl.dao.helper.LocationRoleDAOSQLHelper;
import com.covidien.etl.dbstore.DBUtiltityFunctions;
import com.covidien.etl.log.EtlLogger;
import com.covidien.etl.log.EtlLoggerFactory;
import com.covidien.etl.model.LocationRole;

/**
 * @ClassName: LocationRoleDAO
 * @Description:
 */
public class LocationRoleDAO extends BaseDAO<LocationRole> {
    /**
     * Define a log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(LocationRoleDAO.class);
    /**
     * Define a etl log instance.
     */
    private final EtlLogger etlloger = EtlLoggerFactory.getLogger();

    /**
     * Define a database connection.
     */
    private Connection con = null;
    /**
     * count the execute record.
     */
    private long count = 0L;
    /**
     * Define a save point.
     */
    private static volatile Savepoint savepoint = null;
    /**
     * nodeRevisions.
     */
    private Map<String, String> nodeRevisions = new HashMap<String, String>();
    /**
     * node.
     */
    private Map<String, String> node = new HashMap<String, String>();
    /**
     * contentTypePartyPostalAddress.
     */
    private Map<String, String> contentTypePartyPostalAddress = new HashMap<String, String>();

    @Override
    public final void process(List<LocationRole> list) {

        long etlAdmin = -1;
        try {
            con = getDbConnection().getConnection();
            con.setAutoCommit(false);
            etlAdmin = DBUtiltityFunctions.getUserId(super.getEtlUser());
        } catch (SQLException e) {
            LOGGER.error("exception:", e);
            return;
        }

        long postalAddressRefNid = -1;
        long customerAcctNid = -1;

        HashMap<String, Long> partyIDMap = new HashMap<String, Long>();

        for (LocationRole locationRole : list) {
            count++;
            if (count >= 100 * 3) {
                try {
                    con.commit();
                } catch (SQLException e) {
                    LOGGER.error("Exception: When commit", e);
                }
                count = 0L;
            }
            String locationRoleKey = locationRole.getCustomerId() + ":" + locationRole.getLocationId();

            long nid = -1;
            long addressTypeNid = -1;
            long existLocationRoleNid = -1;
            try {
                nid = super.getXrefIdByPsId(EtlType.LocationRole, locationRoleKey);
            } catch (SQLException e1) {
                LOGGER.error("Exception:", e1);
            }

            if (locationRole.getIsDeleted() == 1) {
                try {
                    if (nid == -1) {
                        throw new Exception("LocationRole doesn't exist!");
                    }
                    deleteLocationRole(nid);
                    super.deleteXrefByPsId(EtlType.LocationRole, locationRoleKey);
                    con.releaseSavepoint(savepoint);
                    savepoint = con.setSavepoint();
                    //con.commit();
                    LOGGER.info("Location Role:" + locationRoleKey + " is deleted!");
                    etlloger.successDelete(EtlType.LocationRole, locationRole);
                } catch (Exception e) {
                    try {
                        if (savepoint != null) {
                            con.rollback(savepoint);
                        } else {
                            con.rollback();
                        }
                    } catch (SQLException sql) {
                        LOGGER.error("Exception:", sql);
                    }
                    LOGGER.error("Exception:", e);
                    locationRole.setException(e.getMessage());
                    etlloger.failDelete(EtlType.LocationRole, locationRole);
                }
                continue;
            }

            if (nid != -1) {
                try {
                    if (!shouldChange(EtlType.LocationRole, locationRoleKey,
                            formateDate(locationRole.getLastChangeDate()))) {
                        continue;
                    }
                    LOGGER.info("Update Location Role is:" + locationRole.getCustomerId() + ":"
                            + locationRole.getLocationId() + ":" + locationRole.getLocationRole());
                    addressTypeNid = getOrCreateAddressTypeNid(etlAdmin, locationRole);
                    existLocationRoleNid = getExistLocationRoleNid(locationRole);
                    if (existLocationRoleNid != -1 && addressTypeNid != -1) {
                        updateLocationRole(addressTypeNid, existLocationRoleNid);
                        con.releaseSavepoint(savepoint);
                        savepoint = con.setSavepoint();
                        //con.commit();
                        etlloger.successUpdate(EtlType.LocationRole, locationRole);
                    } else {
                        LOGGER.info("existLocationRoleNid is: " + existLocationRoleNid + "; " + "addressTypeNid is: "
                                + addressTypeNid);
                    }
                } catch (Exception e) {
                    try {
                        if (savepoint != null) {
                            con.rollback(savepoint);
                        } else {
                            con.rollback();
                        }
                    } catch (SQLException sql) {
                        LOGGER.error("Exception:", sql);
                    }
                    LOGGER.error("Exception:", e);
                    locationRole.setException(e.getMessage());
                    etlloger.failUpdate(EtlType.LocationRole, locationRole);
                }
                continue;
            }

            //check the format of Last Change Date.
            try {
                super.formateDate(locationRole.getLastChangeDate());
            } catch (ParseException e) {
                LOGGER.error("Exception:", e);
                locationRole.setException(e.getMessage());
                etlloger.failInsert(EtlType.LocationRole, locationRole);
                continue;
            }

            String contentAddressType = null;

            try {
                addressTypeNid = getOrCreateAddressTypeNid(etlAdmin, locationRole);
                String title = locationRole.getLocationId();
                if (title == null || title == "") {
                    throw new EtlException("LocationId cannot be empty!");
                }
                nodeRevisions.clear();
                node.clear();
                nodeRevisions.put("uid", String.valueOf(etlAdmin));
                nodeRevisions.put("title", locationRole.getLocationId());
                node.put("uid", String.valueOf(etlAdmin));
                node.put("title", locationRole.getLocationId());
                node.put("type", "party_postal_address");

                postalAddressRefNid = DBUtiltityFunctions.getPostalAddressRefNid(locationRole.getLocationId());

                if (partyIDMap.containsKey(locationRole.getCustomerId())) {
                    customerAcctNid = partyIDMap.get(locationRole.getCustomerId());
                } else {

                    customerAcctNid = DBUtiltityFunctions.getCustomerAcctNid(locationRole.getCustomerId());

                    partyIDMap.put(locationRole.getCustomerId(), customerAcctNid);
                }
                // partyPostalNid = DBUtiltityFunctions.getPartyAddressNid(stmt,
                // locationRole.getCUSTOMER_ID());
                if (postalAddressRefNid == -1) {
                    throw new EtlException("Coudn't find location:" + locationRole.getLocationId());
                }
                if (customerAcctNid == -1) {
                    throw new EtlException("Coudn't find customer:" + locationRole.getCustomerId());
                }

                if (postalAddressRefNid != -1 && customerAcctNid != -1) {
                    contentTypePartyPostalAddress.clear();
                    contentTypePartyPostalAddress.put("postalAddressNid", String.valueOf(customerAcctNid));
                    contentTypePartyPostalAddress.put("addressTypeNid", String.valueOf(addressTypeNid));
                    contentTypePartyPostalAddress.put("addressRefNid", String.valueOf(postalAddressRefNid));

                    LOGGER.info("Location Role is inserted:" + locationRole.getCustomerId() + ":"
                            + locationRole.getLocationId());
                    long locationRoleNid = insert(nodeRevisions, node, contentAddressType,
                            contentTypePartyPostalAddress);
                    super.insertXref(EtlType.LocationRole, locationRoleNid, locationRoleKey,
                            locationRole.getLastChangeDate());
                    etlloger.successInsert(EtlType.LocationRole, locationRole);
                }
                con.releaseSavepoint(savepoint);
                savepoint = con.setSavepoint();
                //con.commit();
            } catch (Exception e) {
                LOGGER.error("Exception:", e);
                locationRole.setException(e.getMessage());
                etlloger.failInsert(EtlType.LocationRole, locationRole);
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
     * @Title: getExistLocationRoleNid
     * @param locationRole
     *        locationRole
     * @return long
     */
    public final long getExistLocationRoleNid(final LocationRole locationRole) {
        PreparedStatement pstmt = null;
        try {
            long acctNumNid = DBUtiltityFunctions.getCustomerAcctNid(locationRole.getCustomerId());
            // long addressTypeNid = DBUtiltityFunctions.getAddressTypeNid(stmt,
            // locationRole.getLOCATION_ROLE());
            long postalAddressRefNid = DBUtiltityFunctions.getPostalAddressRefNid(locationRole.getLocationId());

            if (acctNumNid == -1 || postalAddressRefNid == -1) {
                return -1;
            }
            pstmt = LocationRoleDAOSQLHelper.getPstmtF();
            pstmt.setLong(1, acctNumNid);
            pstmt.setLong(2, postalAddressRefNid);
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                return result.getLong(1);
            }
            result.close();
        } catch (Exception e) {
            LOGGER.error("Exception:", e);
        }
        return -1;
    }

    /**
     * @Title: insert
     * @param nodeRevisionsI
     *        nodeRevisions
     * @param nodeI
     *        node
     * @param contentAddressType
     *        contentAddressType
     * @param contentTypePartyPostalAddressI
     *        contentTypePartyPostalAddress
     * @return long
     * @throws Exception
     *         Exception
     */
    private long insert(final Map<String, String> nodeRevisionsI, final Map<String, String> nodeI,
            final String contentAddressType, final Map<String, String> contentTypePartyPostalAddressI)
        throws Exception {

        long nid = -1;
        long vid = -1;
        PreparedStatement pstmt = null;
        try {
            pstmt = LocationRoleDAOSQLHelper.getPstmtB();
            pstmt.setString(1, nodeRevisionsI.get("uid"));
            pstmt.setString(2, nodeRevisionsI.get("title"));
            vid = super.insertWithReturnKey(pstmt);

            pstmt = LocationRoleDAOSQLHelper.getPstmtC();
            pstmt.setLong(1, vid);
            pstmt.setString(2, nodeI.get("type"));
            pstmt.setString(3, nodeI.get("title"));
            pstmt.setString(4, nodeI.get("uid"));
            nid = super.insertWithReturnKey(pstmt);
            updateNidWithVid(vid, nid);

            if (contentTypePartyPostalAddressI != null && contentTypePartyPostalAddressI.size() > 0) {
                pstmt = LocationRoleDAOSQLHelper.getPstmtD();
                pstmt.setLong(1, nid);
                pstmt.setLong(2, vid);
                pstmt.setLong(3, Long.valueOf(contentTypePartyPostalAddressI.get("postalAddressNid")).longValue());
                pstmt.setLong(4, Long.valueOf(contentTypePartyPostalAddressI.get("addressTypeNid")).longValue());
                pstmt.setLong(5, Long.valueOf(contentTypePartyPostalAddressI.get("addressRefNid")).longValue());
                pstmt.executeUpdate();
            }
            if (contentAddressType != null && contentAddressType.length() > 0) {
                pstmt = LocationRoleDAOSQLHelper.getPstmtE();
                pstmt.setLong(1, nid);
                pstmt.setLong(2, vid);
                pstmt.setString(3, contentAddressType);
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

    /**
     * @Title: deleteLocationRole
     * @param nid
     *        nid
     * @throws Exception
     *         Exception
     */
    private void deleteLocationRole(final long nid)
        throws Exception {
        super.setExpired(nid);
        // Statement stmt = null;
        // try {
        // stmt = con.createStatement();
        // stmt.executeUpdate("update content_field_expiration_datetime expiry
        // inner join node node on node.vid = expiry.vid set
        // expiry.field_expiration_datetime_value = NOW() where "
        // + nid + " and node.type='party_postal_address'");
        // } catch (Exception e) {
        // throw e;
        // } finally {
        // stmt.close();
        // }
    }

    /**
     * @Title: getOrCreateAddressTypeNid
     * @param etlAdmin
     *        etlAdmin
     * @param locationRole
     *        locationRole
     * @return long
     * @throws Exception
     *         Exception
     */
    private long getOrCreateAddressTypeNid(long etlAdmin, LocationRole locationRole)
        throws Exception {
        nodeRevisions.clear();
        node.clear();
        String contentAddressType = null;

        long addressTypeNid = -1;
        addressTypeNid = DBUtiltityFunctions.getAddressTypeNid(locationRole.getLocationRole());

        if (addressTypeNid == -1) {
            String title = locationRole.getLocationRole();
            if (title == null || title == "") {
                throw new EtlException("LocationRole cannot be empty!");
            }
            nodeRevisions.put("uid", String.valueOf(etlAdmin));
            nodeRevisions.put("title", locationRole.getLocationRole());
            node.put("uid", String.valueOf(etlAdmin));
            node.put("title", locationRole.getLocationRole());
            node.put("type", "address_type");

            contentAddressType = locationRole.getLocationRole();
            addressTypeNid = insert(nodeRevisions, node, contentAddressType, null);
        }

        return addressTypeNid;
    }

    /**
     * @Title: updateLocationRole
     * @param addressTypeNid
     *        addressTypeNid
     * @param existLocationRoleNid
     *        addressTypeNid
     * @throws Exception
     *         Exception
     */
    private void updateLocationRole(final long addressTypeNid, final long existLocationRoleNid)
        throws Exception {
        PreparedStatement pstmt = null;
        pstmt = LocationRoleDAOSQLHelper.getPstmtA();
        pstmt.setLong(1, addressTypeNid);
        pstmt.setLong(2, existLocationRoleNid);
        pstmt.executeUpdate();
    }
}
