package com.covidien.etl.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.covidien.etl.common.EtlType;
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
    private static final EtlLogger ETLLOGGER = EtlLoggerFactory.getLogger();
    /**
     * Define a country code map.
     */
    private HashMap<String, String> countryCodeMap = new HashMap<String, String>();
    /**
     * @Title: LocationDAO
     * @Description:
     */
    public LocationDAO() {
        super();
        countryCodeMap.put("US", "United States");
    }
    @Override
    public final void process(final List<Location> list) {
        LOGGER.info("begin to deal with Location objects.");
        LOGGER.info("Location size is :" + list.size());
        long etlAdmin = -1;
        Statement stmt = null;
        Connection con = null;
        try {
            con = getDbConnection().getConnection();
            stmt = con.createStatement();
            etlAdmin = DBUtiltityFunctions.getUserId(stmt, super.getEtlUser());

        } catch (SQLException e) {
            LOGGER.error("exception:", e);
        }

        long countryNid = -1;

        for (Location location : list) {

            long nid = -1;
            try {
                nid = super.getXrefIdByPsId(stmt, EtlType.Location,
                        location.getLOCATION_ID());
            } catch (SQLException e1) {
                LOGGER.error("Exception:", e1);
            }

            StringBuilder nodeRevisions = new StringBuilder();
            StringBuilder node = new StringBuilder();
            StringBuilder contentTypePostalAddress = new StringBuilder();

            try {
                countryNid = getCountryId(stmt, location, etlAdmin);
            } catch (Exception e1) {
                LOGGER.error("Exception:", e1);
            }

            if (nid != -1) {
                // process delete case
                if (location.getIS_DELETED() == 1) {
                    try {
                        deleteLocationByNid(con, nid);
                        super.deleteXrefByPsId(con, EtlType.Location,
                                location.getLOCATION_ID());
                        con.commit();
                        ETLLOGGER.successDelete(EtlType.Location, location);
                    } catch (Exception e) {
                        try {
                            con.rollback();
                        } catch (SQLException e1) {
                            LOGGER.error("Exception:", e1);
                        }
                        LOGGER.error("Exception:", e);
                        location.setException(e.getMessage());
                        ETLLOGGER.failDelete(EtlType.Location, location);
                    }
                    continue;
                }

                // process update case
                try {
                    if (!shouldChange(stmt, EtlType.Location,
                            location.getLOCATION_ID(),
                            formateDate(location.getLAST_CHANGE_DATE()))) {
                        continue;
                    }

                    LOGGER.info("update location,LOCATION_ID is :"
                            + location.getLOCATION_ID());
                    contentTypePostalAddress
                            .append("update content_type_postal_address set ")
                            .append("field_postal_address_line1_value='")
                            .append(location.getADDRESS_LINE1())
                            .append("',field_postal_address_city_value='")
                            .append(location.getCITY())
                            .append("',field_state_province_value='")
                            .append(location.getSTATE_PROVINCE())
                            .append("',field_postal_code_value='")
                            .append(location.getPOSTAL_CODE())
                            .append("',field_postal_code_country_nid=")
                            .append(countryNid);

                    contentTypePostalAddress.append(" where nid=").append(nid);

                    updateLocation(stmt, contentTypePostalAddress);
                    contentTypePostalAddress = new StringBuilder();
                    cleanCache(con);
                    con.commit();
                    ETLLOGGER.successUpdate(EtlType.Location, location);
                } catch (Exception e) {
                    try {
                        con.rollback();
                    } catch (SQLException e1) {
                        LOGGER.error("Exception:", e1);
                    }
                    LOGGER.error("Exception:", e);
                    location.setException(e.getMessage());
                    ETLLOGGER.failUpdate(EtlType.Location, location);
                }
                continue;
            }

            LOGGER.info("insert location,LOCATION_ID is :"
                    + location.getLOCATION_ID());

            nodeRevisions.append(etlAdmin + ",'"
                    + location.getLOCATION_ID().replace("'", "\\'")
                    + "','','','',unix_timestamp(),0),");
            node.append("'postal_address','','"
                    + location.getLOCATION_ID().replace("'", "\\'") + "',"
                    + etlAdmin
                    + ",'1',unix_timestamp(),unix_timestamp(),0,0,0),");

            if (location.getADDRESS_LINE1() != null) {
                contentTypePostalAddress.append("'"
                        + location.getADDRESS_LINE1() + "',");
            } else {
                contentTypePostalAddress.append("'',");
            }

            if (location.getCITY() != null) {
                contentTypePostalAddress.append("'" + location.getCITY()
                        + "','" + location.getSTATE_PROVINCE() + "','"
                        + location.getPOSTAL_CODE() + "'," + countryNid + "),");
            } else {
                contentTypePostalAddress.append("','"
                        + location.getSTATE_PROVINCE() + "','"
                        + location.getPOSTAL_CODE() + "'," + countryNid + "),");
            }

            try {

                long locationNid = insert(stmt, nodeRevisions, node,
                        contentTypePostalAddress, null);
                super.insertXref(con, EtlType.Location, locationNid,
                        location.getLOCATION_ID(),
                        location.getLAST_CHANGE_DATE());
                con.commit();
                ETLLOGGER.successInsert(EtlType.Location, location);
            } catch (Exception e) {
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    LOGGER.error("Exception:", e1);
                }
                LOGGER.error("Exception:", e);
                location.setException(e.getMessage());
                ETLLOGGER.failInsert(EtlType.Location, location);
            }

        }
        try {
            stmt.close();
        } catch (SQLException e) {
            LOGGER.error("Exception:", e);
        }
    }
    /**
     * @Title: deleteLocationByNid
     * @Description:
     * @param con
     * con
     * @param nid
     * nid
     * @throws Exception
     */
    private void deleteLocationByNid(final Connection con, final long nid)
            throws Exception {
        super.setExpired(con, nid);
    }
    /**
     * @Title: getCountryId
     * @Description:
     * @param stmt
     * stmt
     * @param location
     * location
     * @param etlAdmin
     * etlAdmin
     * @throws Exception
     * @return long
     */
    private long getCountryId(final Statement stmt, final Location location,
            final long etlAdmin) throws Exception {
        long countryNid = -1;
        try {
            countryNid = DBUtiltityFunctions.getCountryNid(stmt,
                    location.getCOUNTRY_CODE());
        } catch (SQLException e) {
            LOGGER.error("exception:", e);
        }

        if (location.getCOUNTRY_CODE() != null && countryNid == 0) {
            try {
                insertCountry(stmt, location.getCOUNTRY_CODE(), etlAdmin);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return countryNid;
    }
    /**
     * @Title: insertCountry
     * @Description:
     * @param stmt
     * stmt
     * @param countryCode
     * countryCode
     * @param etlAdmin
     * etlAdmin
     * @throws Exception
     */
    private void insertCountry(final Statement stmt, final String countryCode,
            final long etlAdmin) throws Exception {
        StringBuilder nodeRevisions = new StringBuilder();
        StringBuilder node = new StringBuilder();
        StringBuilder contentTyepCountry = new StringBuilder();
        nodeRevisions.append(etlAdmin + ",'" + countryCodeMap.get(countryCode)
                + "','','','',unix_timestamp(),0),");
        node.append("'postal_address','','" + countryCodeMap.get(countryCode)
                + "'," + etlAdmin
                + ",'1',unix_timestamp(),unix_timestamp(),0,0,0),");
        contentTyepCountry.append("'" + countryCode + "'),");
        insert(stmt, nodeRevisions, node, null, contentTyepCountry);
    }
    /**
     * @Title: updateLocation
     * @Description:
     * @param stmt
     * stmt
     * @param contentTypePostalAddress
     * contentTypePostalAddress
     * @throws Exception
     */
    private void updateLocation(final Statement stmt,
            final StringBuilder contentTypePostalAddress) throws Exception {

        LOGGER.info("contentTypePostalAddress is :"
                + contentTypePostalAddress.toString());
        stmt.executeUpdate(contentTypePostalAddress.toString());

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
     * @param contentTypePostalAddress
     * contentTypePostalAddress
     * @param contentTyepCountry
     * contentTyepCountry
     * @return long
     * @throws Exception
     */
    private long insert(final Statement stmt,
            final StringBuilder nodeRevisions, final StringBuilder node,
            final StringBuilder contentTypePostalAddress,
            final StringBuilder contentTyepCountry) throws Exception {
        long nid = -1;

        long vid = super
                .insertWithReturnKey(
                        stmt.getConnection(),
                        "insert into node_revisions (nid,uid,title,body,teaser,log,timestamp,format) values (0,"
                                + nodeRevisions.substring(0,
                                        nodeRevisions.length() - 1));
        nid = insertWithReturnKey(
                stmt.getConnection(),
                "insert into node (vid,type,language,title,uid,status,created,changed,comment,promote,"
                        + "translate) values ("
                        + vid
                        + ","
                        + node.substring(0, node.length() - 1));
        updateNidWithVid(stmt.getConnection(), vid, nid);

        super.updateNidWithVid(stmt.getConnection(), vid, nid);

        stmt.executeUpdate("insert into content_type_postal_address (nid, vid, field_postal_address_line1_value,"
                + "field_postal_address_city_value, field_state_province_value, field_postal_code_value, field_postal_code_country_nid) values ("
                + nid
                + ","
                + vid
                + ","
                + contentTypePostalAddress.substring(0,
                        contentTypePostalAddress.length() - 1));
        if (contentTyepCountry != null && contentTyepCountry.length() > 0) {
            stmt.executeUpdate("insert into content_type_country (nid, vid, field_iso_3166_2lcode_value) values ("
                    + nid
                    + ","
                    + vid
                    + ","
                    + contentTyepCountry.substring(0,
                            contentTyepCountry.length() - 1));
            stmt.executeUpdate("insert into content_field_is_active (nid, vid) values ("
                    + nid + "," + vid + ")");
            stmt.executeUpdate("insert into content_field_sort_sequence (nid, vid) values ("
                    + nid + "," + vid + ")");
        }

        return nid;
    }

}
