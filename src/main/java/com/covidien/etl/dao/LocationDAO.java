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
import java.util.Properties;

import org.apache.log4j.Logger;

import com.covidien.etl.common.Constant;
import com.covidien.etl.common.EtlException;
import com.covidien.etl.common.EtlType;
import com.covidien.etl.common.PropertyReader;
import com.covidien.etl.dao.helper.LocationDAOSQLHelper;
import com.covidien.etl.dbstore.DBUtiltityFunctions;
import com.covidien.etl.log.EtlLogger;
import com.covidien.etl.log.EtlLoggerFactory;
import com.covidien.etl.model.Location;

/**
 * @ClassName: LocationDAO
 * @Description:
 */
public class LocationDAO extends BaseDAO<Location> {
    /**
     * Define a log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(LocationDAO.class);
    /**
     * Define a etl log instance.
     */
    private final EtlLogger etlloger = EtlLoggerFactory.getLogger();
    /**
     * Define a country code map.
     */
    private HashMap<String, String> countryCodeMap = new HashMap<String, String>();
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
     * contentTypePostalAddress.
     */
    private Map<String, String> contentTypePostalAddress = new HashMap<String, String>();

    /**
     * @Title: LocationDAO
     * @Description:
     */
    public LocationDAO() {
        super();
        Properties properties = PropertyReader.getInstance().read(
                Constant.getConfigFilePath() + "ISOCountryCode.properties");
        for (String key : properties.stringPropertyNames()) {
            countryCodeMap.put(key, properties.getProperty(key));
        }
    }

    @Override
    public final void process(List<Location> list) {
        long etlAdmin = -1;
        try {
            con = getDbConnection().getConnection();
            con.setAutoCommit(false);
            etlAdmin = DBUtiltityFunctions.getUserId(super.getEtlUser());
        } catch (SQLException e) {
            LOGGER.error("exception:", e);
            return;
        }

        long countryNid = -1;

        for (Location location : list) {
            count++;
            if (count >= 100 * 3) {
                try {
                    con.commit();
                } catch (SQLException e) {
                    LOGGER.error("Exception: When commit", e);
                }
                count = 0L;
            }

            long nid = -1;
            try {
                nid = super.getXrefIdByPsId(EtlType.Location, location.getLocationId());
            } catch (SQLException e1) {
                LOGGER.error("Exception:", e1);
            }

            try {
                countryNid = getCountryId(location, etlAdmin);
            } catch (Exception e1) {
                LOGGER.error("Exception:", e1);
            }
            // process delete case
            if (location.getIsDeleted() == 1) {
                try {
                    if (nid == -1) {
                        throw new Exception("Location doesn't exist!");
                    }
                    deleteLocationByNid(nid);
                    super.deleteXrefByPsId(EtlType.Location, location.getLocationId());
                    con.releaseSavepoint(savepoint);
                    savepoint = con.setSavepoint();
                    //con.commit();
                    etlloger.successDelete(EtlType.Location, location);
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
                    location.setException(e.getMessage());
                    etlloger.failDelete(EtlType.Location, location);
                }
                continue;
            }

            if (nid != -1) {
                // process update case
                try {
                    if (!shouldChange(EtlType.Location, location.getLocationId(),
                            formateDate(location.getLastChangeDate()))) {
                        continue;
                    }
                    if (countryNid == -1) {
                        LOGGER.error("Location " + location.getLocationId() + ": Country code is invalid!"
                                + "Country Code is: " + location.getCountryCode());
                        location.setException("Country code is invalid!");
                        etlloger.failUpdate(EtlType.Location, location);
                        continue;
                    }
                    LOGGER.info("update location,LOCATION_ID is :" + location.getLocationId());
                    contentTypePostalAddress.clear();
                    contentTypePostalAddress.put("addressLine1Value", location.getAddressLine1());
                    contentTypePostalAddress.put("cityValue", location.getCity());
                    contentTypePostalAddress.put("provinceValue", location.getStateProvince());
                    contentTypePostalAddress.put("postalCodeValue", location.getPostalCode());
                    contentTypePostalAddress.put("countryCodeNid", String.valueOf(countryNid));
                    contentTypePostalAddress.put("nid", String.valueOf(nid));

                    updateLocation(contentTypePostalAddress);
                    contentTypePostalAddress.clear();
                    con.releaseSavepoint(savepoint);
                    savepoint = con.setSavepoint();
                    //con.commit();
                    etlloger.successUpdate(EtlType.Location, location);
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
                    location.setException(e.getMessage());
                    etlloger.failUpdate(EtlType.Location, location);
                }
                continue;
            }

            //check the format of Last Change Date.
            try {
                super.formateDate(location.getLastChangeDate());
            } catch (ParseException e) {
                LOGGER.error("Exception:", e);
                location.setException(e.getMessage());
                etlloger.failInsert(EtlType.Location, location);
                continue;
            }
            if (countryNid == -1) {
                LOGGER.error("Location " + location.getLocationId() + ": Country code is invalid!"
                        + " Country Code is: " + location.getCountryCode());
                location.setException("Country code is invalid!");
                etlloger.failInsert(EtlType.Location, location);
                continue;
            }
            nodeRevisions.clear();
            node.clear();
            nodeRevisions.put("uid", String.valueOf(etlAdmin));
            nodeRevisions.put("title", location.getLocationId());
            node.put("uid", String.valueOf(etlAdmin));
            node.put("title", location.getLocationId());
            node.put("type", "postal_address");

            if (location.getAddressLine1() == null) {
                location.setAddressLine1("");
            }
            if (location.getCity() == null) {
                location.setCity("");
            }
            if (location.getStateProvince() == null) {
                location.setStateProvince("");
            }
            if (location.getPostalCode() == null) {
                location.setPostalCode("");
            }
            contentTypePostalAddress.clear();
            contentTypePostalAddress.put("addressLine1Value", location.getAddressLine1());
            contentTypePostalAddress.put("cityValue", location.getCity());
            contentTypePostalAddress.put("provinceValue", location.getStateProvince());
            contentTypePostalAddress.put("postalCodeValue", location.getPostalCode());
            contentTypePostalAddress.put("countryCodeNid", String.valueOf(countryNid));

            try {

                long locationNid = insert(nodeRevisions, node, contentTypePostalAddress, null);
                super.insertXref(EtlType.Location, locationNid, location.getLocationId(), location.getLastChangeDate());
                con.releaseSavepoint(savepoint);
                savepoint = con.setSavepoint();
                //con.commit();
                LOGGER.info("inserted location is :" + location.getLocationId());
                etlloger.successInsert(EtlType.Location, location);
            } catch (Exception e) {
                LOGGER.error("Exception:", e);
                location.setException(e.getMessage());
                etlloger.failInsert(EtlType.Location, location);
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
     * @Title: deleteLocationByNid
     * @param nid
     *        nid
     * @throws Exception
     *         Exception
     */
    private void deleteLocationByNid(final long nid)
        throws Exception {
        PreparedStatement pstmt = null;
        pstmt = LocationDAOSQLHelper.getPstmtG();
        pstmt.setLong(1, nid);
        ResultSet result = pstmt.executeQuery();
        while (result.next()) {
            throw new EtlException("Can't delete location record because it reference some devices!");
        }

        String loctionId = "$";
        pstmt = LocationDAOSQLHelper.getPstmtH();
        pstmt.setLong(1, nid);
        result = pstmt.executeQuery();
        while (result.next()) {
            loctionId = result.getString(1);
        }

        loctionId = "%" + loctionId;
        pstmt = LocationDAOSQLHelper.getPstmtI();
        pstmt.setString(1, loctionId);
        result = pstmt.executeQuery();
        while (result.next()) {
            throw new EtlException("Can't delete location record because it reference some location role!");
        }

        pstmt = LocationDAOSQLHelper.getPstmtJ();
        pstmt.setLong(1, nid);
        result = pstmt.executeQuery();
        while (result.next()) {
            throw new EtlException("Can't delete location record because it reference some costomer postal address!");
        }
        result.close();
        super.setExpired(nid);
    }

    /**
     * @Title: getCountryId
     * @param location
     *        location
     * @param etlAdmin
     *        etlAdmin
     * @throws Exception
     *         Exception
     * @return long
     */
    private long getCountryId(final Location location, final long etlAdmin)
        throws Exception {
        long countryNid = -1;
        if (location.getCountryCode() != null && location.getCountryCode() != "") {
            try {
                countryNid = DBUtiltityFunctions.getCountryNid(location.getCountryCode());
            } catch (SQLException e) {
                LOGGER.error("exception:", e);
            }
            if (location.getCountryCode() != null && location.getCountryCode() != "" && countryNid <= 0) {
                try {
                    insertCountry(location.getCountryCode(), etlAdmin);
                } catch (SQLException e) {
                    LOGGER.error("exception:", e);
                }
            }
        }
        return countryNid;

    }

    /**
     * @Title: insertCountry
     * @param countryCode
     *        countryCode
     * @param etlAdmin
     *        etlAdmin
     * @throws Exception
     *         Exception
     */
    private void insertCountry(final String countryCode, final long etlAdmin)
        throws Exception {
        nodeRevisions.clear();
        node.clear();
        String countryName = countryCodeMap.get(countryCode);
        if (countryName == null || countryName == "") {
            throw new EtlException("Country is invalid! Country code is : " + countryCode);
        }
        nodeRevisions.put("uid", String.valueOf(etlAdmin));
        nodeRevisions.put("title", countryCodeMap.get(countryCode));
        node.put("uid", String.valueOf(etlAdmin));
        node.put("title", countryCodeMap.get(countryCode));
        node.put("type", "country");

        insert(nodeRevisions, node, null, countryCode);
    }

    /**
     * @Title: updateLocation
     * @param contentTypePostalAddressI
     *        contentTypePostalAddressI
     * @throws Exception
     *         Exception
     */
    private void updateLocation(final Map<String, String> contentTypePostalAddressI)
        throws Exception {
        PreparedStatement pstmt = null;
        pstmt = LocationDAOSQLHelper.getPstmtK();
        pstmt.setString(1, contentTypePostalAddressI.get("addressLine1Value"));
        pstmt.setString(2, contentTypePostalAddressI.get("cityValue"));
        pstmt.setString(3, contentTypePostalAddressI.get("provinceValue"));
        pstmt.setString(4, contentTypePostalAddressI.get("postalCodeValue"));
        pstmt.setString(5, contentTypePostalAddressI.get("countryCodeNid"));
        pstmt.setLong(6, Long.valueOf(contentTypePostalAddressI.get("nid")).longValue());
        pstmt.executeUpdate();
    }

    /**
     * @Title: insert
     * @param nodeRevisionsI
     *        nodeRevisions
     * @param nodeI
     *        node
     * @param contentTypePostalAddressI
     *        contentTypePostalAddress
     * @param contentTyepCountry
     *        contentTyepCountry
     * @return long
     * @throws Exception
     *         Exception
     */
    private long insert(final Map<String, String> nodeRevisionsI, final Map<String, String> nodeI,
            final Map<String, String> contentTypePostalAddressI, final String contentTyepCountry)
        throws Exception {
        long nid = -1;
        long vid = -1;
        PreparedStatement pstmt = null;
        try {
            pstmt = LocationDAOSQLHelper.getPstmtA();
            pstmt.setString(1, nodeRevisionsI.get("uid"));
            pstmt.setString(2, nodeRevisionsI.get("title"));
            vid = super.insertWithReturnKey(pstmt);

            pstmt = LocationDAOSQLHelper.getPstmtB();
            pstmt.setLong(1, vid);
            pstmt.setString(2, nodeI.get("type"));
            pstmt.setString(3, nodeI.get("title"));
            pstmt.setString(4, nodeI.get("uid"));
            nid = super.insertWithReturnKey(pstmt);
            updateNidWithVid(vid, nid);

            if (contentTypePostalAddressI != null && contentTypePostalAddressI.size() > 0) {
                pstmt = LocationDAOSQLHelper.getPstmtC();
                pstmt.setLong(1, nid);
                pstmt.setLong(2, vid);
                pstmt.setString(3, contentTypePostalAddressI.get("addressLine1Value"));
                pstmt.setString(4, contentTypePostalAddressI.get("cityValue"));
                pstmt.setString(5, contentTypePostalAddressI.get("provinceValue"));
                pstmt.setString(6, contentTypePostalAddressI.get("postalCodeValue"));
                pstmt.setString(7, contentTypePostalAddressI.get("countryCodeNid"));
                pstmt.executeUpdate();
            }

            if (contentTyepCountry != null && contentTyepCountry.length() > 0) {
                pstmt = LocationDAOSQLHelper.getPstmtD();
                pstmt.setLong(1, nid);
                pstmt.setLong(2, vid);
                pstmt.setString(3, contentTyepCountry);
                pstmt.executeUpdate();

                pstmt = LocationDAOSQLHelper.getPstmtE();
                pstmt.setLong(1, nid);
                pstmt.setLong(2, vid);
                pstmt.executeUpdate();

                pstmt = LocationDAOSQLHelper.getPstmtF();
                pstmt.setLong(1, nid);
                pstmt.setLong(2, vid);
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
