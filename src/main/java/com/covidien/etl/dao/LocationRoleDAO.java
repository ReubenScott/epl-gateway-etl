package com.covidien.etl.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.covidien.etl.common.EtlException;
import com.covidien.etl.common.EtlType;
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
    private static final Logger LOGGER = Logger
            .getLogger(LocationRoleDAO.class);
    /**
     * Define a etl log instance.
     */
    private static final EtlLogger ETLLOGGER = EtlLoggerFactory.getLogger();
    @Override
    public final void process(final List<LocationRole> list) {

        LOGGER.info("begin to deal with LocationRole objects.");
        LOGGER.info("LocationRole size is :" + list.size());
        Connection con = null;
        Statement stmt = null;

        long etlAdmin = -1;
        try {
            con = getDbConnection().getConnection();
            stmt = con.createStatement();
            etlAdmin = DBUtiltityFunctions.getUserId(stmt, super.getEtlUser());
        } catch (SQLException e) {
            LOGGER.error("exception:", e);
        }

        long postalAddressRefNid = -1;
        long partyPostalNid = -1;

        HashMap<String, Long> locationTypeMap = new HashMap<String, Long>();
        HashMap<String, Long> partyIDMap = new HashMap<String, Long>();

        for (LocationRole locationRole : list) {
            String locationRoleId = locationRole.getCUSTOMER_ID() + ":"
                    + locationRole.getLOCATION_ID();

            long nid = -1;

            try {
                nid = super.getXrefIdByPsId(stmt, EtlType.LocationRole,
                        locationRoleId);
            } catch (SQLException e1) {
                LOGGER.error("Exception:", e1);
            }

            if (nid != -1) {
                if (locationRole.getIS_DELETED() == 1) {
                    try {
                        deleteLocationRole(con, nid);
                        super.deleteXrefByPsId(con, EtlType.LocationRole,
                                locationRoleId);
                        LOGGER.info("Location Role:" + locationRoleId
                                + " is deleted!");
                        ETLLOGGER.successDelete(EtlType.LocationRole,
                                locationRole);
                    } catch (Exception e) {
                        LOGGER.error("Exception:", e);
                        locationRole.setException(e.getMessage());
                        ETLLOGGER
                                .failDelete(EtlType.LocationRole, locationRole);
                    }
                    continue;
                }

                try {
                    if (!shouldChange(
                            stmt,
                            EtlType.LocationRole,
                            locationRole.getCUSTOMER_ID() + ":"
                                    + locationRole.getLOCATION_ID(),
                            formateDate(locationRole.getLAST_CHANGE_DATE()))) {
                        LOGGER.info("Skip LocationRole:"
                                + locationRole.getLOCATION_ID() + ";"
                                + locationRole.getCUSTOMER_ID());
                    }
                } catch (Exception e) {
                    LOGGER.error("Exception:", e);
                }
                // ETLLOGGER.failUpdate(EtlType.LocationRole, locationRole);
                continue;
            }

            StringBuilder nodeRevisions = new StringBuilder();
            StringBuilder node = new StringBuilder();
            StringBuilder contentTypePartyPostalAddress = new StringBuilder();
            StringBuilder contentAddressType = new StringBuilder();

            long addressTypeNid = -1;

            try {
                addressTypeNid = DBUtiltityFunctions.getAddressTypeNid(stmt,
                        locationRole.getLOCATION_ROLE());

                if (addressTypeNid == -1) {
                    nodeRevisions.append(etlAdmin
                            + ",'"
                            + locationRole.getLOCATION_ROLE().replace("'",
                                    "\\'") + "','','','',unix_timestamp(),0),");
                    node.append("'address_type','','"
                            + locationRole.getLOCATION_ROLE().replace("'",
                                    "\\'") + "'," + etlAdmin
                            + ",'1',unix_timestamp(),unix_timestamp(),0,0,0),");
                    contentAddressType.append("'"
                            + locationRole.getLOCATION_ROLE() + "'),");
                    addressTypeNid = insert(stmt, nodeRevisions, node,
                            contentAddressType, null);

                    nodeRevisions = new StringBuilder();
                    node = new StringBuilder();
                    contentAddressType = new StringBuilder();
                } else {
                    locationTypeMap.put(locationRole.getLOCATION_ROLE(),
                            addressTypeNid);
                }

                nodeRevisions.append(etlAdmin + ",'"
                        + locationRole.getLOCATION_ID().replace("'", "\\'")
                        + "','','','',unix_timestamp(),0),");
                node.append("'party_postal_address','','"
                        + locationRole.getLOCATION_ID().replace("'", "\\'")
                        + "'," + etlAdmin
                        + ",'1',unix_timestamp(),unix_timestamp(),0,0,0),");

                postalAddressRefNid = DBUtiltityFunctions
                        .getPostalAddressRefNid(stmt,
                                locationRole.getLOCATION_ID());

                if (partyIDMap.containsKey(locationRole.getCUSTOMER_ID())) {
                    partyPostalNid = partyIDMap.get(locationRole
                            .getCUSTOMER_ID());
                } else {

                    partyPostalNid = DBUtiltityFunctions.getPartyAddressNid(
                            stmt, locationRole.getCUSTOMER_ID());

                    partyIDMap.put(locationRole.getCUSTOMER_ID(),
                            partyPostalNid);
                }
                // partyPostalNid = DBUtiltityFunctions.getPartyAddressNid(stmt,
                // locationRole.getCUSTOMER_ID());
                if (postalAddressRefNid == -1) {
                    throw new EtlException("Coudn't find location:"
                            + locationRole.getLOCATION_ID());
                }
                if (partyPostalNid == -1) {
                    throw new EtlException("Coudn't find customer:"
                            + locationRole.getCUSTOMER_ID());
                }

                if (postalAddressRefNid != -1 && partyPostalNid != -1) {
                    contentTypePartyPostalAddress
                            .append(partyPostalNid + "," + addressTypeNid + ","
                                    + postalAddressRefNid + "),");
                    LOGGER.info("Location Role is inserted:"
                            + locationRole.getCUSTOMER_ID() + ":"
                            + locationRole.getLOCATION_ID());
                    long locationRoleNid = insert(stmt, nodeRevisions, node,
                            contentAddressType, contentTypePartyPostalAddress);
                    super.insertXref(con, EtlType.LocationRole,
                            locationRoleNid, locationRoleId,
                            locationRole.getLAST_CHANGE_DATE());
                    ETLLOGGER.successInsert(EtlType.LocationRole, locationRole);
                }
            } catch (Exception e) {
                LOGGER.error("Exception:", e);
                locationRole.setException(e.getMessage());
                ETLLOGGER.failInsert(EtlType.LocationRole, locationRole);
            }

        }

        try {
            stmt.close();
        } catch (SQLException e) {
            LOGGER.error("Exception:", e);
        }

    }
    /**
     * @Title: getExistLocationRoleNid
     * @Description:
     * @param con
     * con
     * @param locationRole
     * locationRole
     * @return long
     */
    public final long getExistLocationRoleNid(final Connection con,
            final LocationRole locationRole) {
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            long partyPostalNid = DBUtiltityFunctions.getPartyAddressNid(stmt,
                    locationRole.getCUSTOMER_ID());
            // long addressTypeNid = DBUtiltityFunctions.getAddressTypeNid(stmt,
            // locationRole.getLOCATION_ROLE());
            long postalAddressRefNid = DBUtiltityFunctions
                    .getPostalAddressRefNid(stmt, locationRole.getLOCATION_ID());

            if (partyPostalNid == -1 || postalAddressRefNid == -1) {
                return -1;
            }

            ResultSet result = stmt
                    .executeQuery("select nid from content_type_party_postal_address where field_party_postal_address_nid ="
                            + partyPostalNid
                            // + " and field_postal_address_type_nid="
                            // + addressTypeNid
                            + " and field_party_postal_address_ref_nid="
                            + postalAddressRefNid);
            while (result.next()) {
                return result.getLong(1);
            }
        } catch (Exception e) {
            LOGGER.error("Exception:", e);
        } finally {
            try {
                stmt.close();
            } catch (SQLException e) {
                LOGGER.error("Exception:", e);
            }
        }
        return -1;
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
     * @param contentAddressType
     * contentAddressType
     * @param contentTypePartyPostalAddress
     * contentTypePartyPostalAddress
     * @return long
     * @throws Exception
     */
    private long insert(final Statement stmt,
            final StringBuilder nodeRevisions, final StringBuilder node,
            final StringBuilder contentAddressType,
            final StringBuilder contentTypePartyPostalAddress) throws Exception {

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

        if (contentTypePartyPostalAddress != null
                && contentTypePartyPostalAddress.length() > 0) {
            stmt.executeUpdate("insert into content_type_party_postal_address (nid, vid, field_party_postal_address_nid, "
                    + "field_postal_address_type_nid, field_party_postal_address_ref_nid) values ("
                    + nid
                    + ","
                    + vid
                    + ","
                    + contentTypePartyPostalAddress.substring(0,
                            contentTypePartyPostalAddress.length() - 1));
        }
        if (contentAddressType != null && contentAddressType.length() > 0) {
            stmt.executeUpdate("insert into content_type_address_type (nid, vid, field_address_type_name_value) values ("
                    + nid
                    + ","
                    + vid
                    + ","
                    + contentAddressType.substring(0,
                            contentAddressType.length() - 1));
        }

        return nid;
    }
    /**
     * @Title: deleteLocationRole
     * @Description:
     * @param con
     * con
     * @param nid
     * nid
     * @throws Exception
     */
    private void deleteLocationRole(final Connection con, final long nid)
            throws Exception {
        super.setExpired(con, nid);
        // Statement stmt = null;
        // try {
        // stmt = con.createStatement();
        // stmt.executeUpdate("update content_field_expiration_datetime expiry inner join node node on node.vid = expiry.vid set expiry.field_expiration_datetime_value = NOW() where "
        // + nid + " and node.type='party_postal_address'");
        // } catch (Exception e) {
        // throw e;
        // } finally {
        // stmt.close();
        // }
    }
}
