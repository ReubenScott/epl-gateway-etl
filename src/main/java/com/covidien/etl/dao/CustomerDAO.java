package com.covidien.etl.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;

import com.covidien.etl.common.EtlException;
import com.covidien.etl.common.EtlType;
import com.covidien.etl.dbstore.DBUtiltityFunctions;
import com.covidien.etl.log.EtlLogger;
import com.covidien.etl.log.EtlLoggerFactory;
import com.covidien.etl.model.Customer;

/**
 * @ClassName: CustomerDAO
 * @Description:
 */
public class CustomerDAO extends BaseDAO<Customer> {
    /**
     * Define a log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(CustomerDAO.class);
    /**
     * Define a etl log instance.
     */
    private static final EtlLogger ETLLOGGER = EtlLoggerFactory.getLogger();
    @Override
    public final void process(final List<Customer> list) {
        LOGGER.info("begin to deal with Customer objects.");
        LOGGER.info("Customer size is :" + list.size());
        Long customertypeNid = -1L;
        Long etlAdmin = -1L;
        Connection con = null;
        Statement stmt = null;
        try {
            con = getDbConnection().getConnection();
            stmt = con.createStatement();
            customertypeNid = DBUtiltityFunctions.getCustomerTypeNid(stmt);
            etlAdmin = DBUtiltityFunctions.getUserId(stmt, super.getEtlUser());
        } catch (Exception e) {
            LOGGER.error("exception:", e);
            return;
        }

        for (Customer customer : list) {
            long nid = -1;
            try {
                nid = super.getXrefIdByPsId(stmt, EtlType.Customer,
                        customer.getCUSTOMER_ID());
            } catch (SQLException e1) {
                LOGGER.error("Exception:", e1);
            }
            // delete customer data
            if (customer.getIS_DELETED() == 1) {
                try {
                    if (nid == -1) {
                        throw new Exception("Customer doesn't exist!");
                    }
                    deleteCustomerByNid(con, nid);
                    super.deleteXrefByPsId(con, EtlType.Customer,
                            customer.getCUSTOMER_ID());
                    con.commit();
                    ETLLOGGER.successDelete(EtlType.Customer, customer);
                } catch (Exception e) {
                    try {
                        con.rollback();
                    } catch (SQLException e1) {
                        LOGGER.error("Exception:", e);
                    }
                    LOGGER.error("Exception:", e);
                    customer.setException(e.getMessage());
                    ETLLOGGER.failDelete(EtlType.Customer, customer);
                }
                continue;
            }
            // update customer data
            if (nid != -1) {
                try {
                    // process update case
                    if (!shouldChange(stmt, EtlType.Customer,
                            customer.getCUSTOMER_ID(),
                            super.formateDate(customer.getLAST_CHANGE_DATE()))) {
                        continue;
                    }
                    LOGGER.info("A customer is updated, id:"
                            + customer.getCUSTOMER_ID() + ";name:"
                            + customer.getNAME());

                    long customerNid = -1;

                    customerNid = this.getCustomerNid(stmt, customer.getNAME());
                    LOGGER.info("begin to update customer, id:"
                            + customer.getCUSTOMER_ID() + ",name:"
                            + customer.getNAME());

                    if (customerNid != -1) {
                        LOGGER.info("customerNid:" + customerNid + " exists.");

                        this.updateRelBuCustomer2Customer(stmt,
                                customer.getCUSTOMER_ID(), customerNid);
                    } else {

                        long customerPartyNid = getCustomerPartyPKNid(stmt,
                                customer.getCUSTOMER_ID());
                        if (customerPartyNid != -1) {
                            updateNodeByNid(con, customerPartyNid,
                                    customer.getNAME());
                        } else {
                            LOGGER.info("create a new customerId:"
                                    + customer.getCUSTOMER_ID() + ".");
                            long latestNid = this.customerNameInserts(stmt,
                                    customer, customertypeNid, etlAdmin);
                            this.updateRelBuCustomer2Customer(stmt,
                                    customer.getCUSTOMER_ID(), latestNid);
                        }
                    }

                    if (customer.getPHONE() != null) {
                        partyVoiceAddress(stmt, nid, customer, "phone",
                                etlAdmin);
                    }
                    if (customer.getFAX() != null) {
                        partyVoiceAddress(stmt, nid, customer, "fax", etlAdmin);
                    }
                    cleanCache(con);
                    con.commit();
                    ETLLOGGER.successUpdate(EtlType.Customer, customer);
                } catch (Exception e) {
                    try {
                        con.rollback();
                    } catch (SQLException e1) {
                        LOGGER.error("Exception:", e);
                    }
                    LOGGER.error("Exception:", e);
                    customer.setException(e.getMessage());
                    ETLLOGGER.failUpdate(EtlType.Customer, customer);
                }
                continue;
            }

            // insert customer data
            try {
                long customerNid = customerNameInserts(stmt, customer,
                        customertypeNid, etlAdmin);

                customerIdInserts(stmt, etlAdmin, customerNid, customer);

                // Phone Information
                if (customer.getPHONE() != null) {
                    partyVoiceAddress(stmt, customerNid, etlAdmin, "phone",
                            customer);
                }
                // Fax Information
                if (customer.getFAX() != null) {
                    partyVoiceAddress(stmt, customerNid, etlAdmin, "fax",
                            customer);
                }

                super.insertXref(con, EtlType.Customer, customerNid,
                        customer.getCUSTOMER_ID(),
                        customer.getLAST_CHANGE_DATE());
                con.commit();
                LOGGER.info("A customer is inserted,name:"
                        + customer.getCUSTOMER_ID() + ":" + customer.getNAME()
                        + ";nid:" + nid);
                ETLLOGGER.successInsert(EtlType.Customer, customer);

            } catch (Exception e) {
                LOGGER.error("Exception:", e);
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    LOGGER.error("Exception:", e);
                }
                customer.setException(e.getMessage());
                ETLLOGGER.failInsert(EtlType.Customer, customer);

            }

        }
        try {
            stmt.close();
        } catch (SQLException e) {
            LOGGER.error("Exception:", e);
        }
    }
    /**
     * @Title: getCustomerPartyPKNid
     * @Description:
     * @param stmt
     * stmt
     * @param customerId
     * customerId
     * @throws Exception
     * @return long
     */
    private long getCustomerPartyPKNid(final Statement stmt,
            final String customerId) throws Exception {
        long nid = -1;
        ResultSet result = stmt
                .executeQuery("select field_customer_party_pk_nid from content_type_bu_customer where field_bu_customer_account_number_value='"
                        + customerId + " '");
        while (result.next()) {
            nid = result.getLong(1);
            break;
        }
        result.close();
        return nid;
    }
    /**
     * @Title: deleteCustomerByNid
     * @Description:
     * @param con
     * con
     * @param nid
     * nid
     * @throws Exception
     */
    private void deleteCustomerByNid(final Connection con, final long nid)
            throws Exception {
        Statement stmt = con.createStatement();
        ResultSet result = stmt
                .executeQuery("SELECT nid FROM content_type_bu_customer WHERE field_customer_party_pk_nid="
                        + nid
                        + " and field_customer_party_pk_nid IN (SELECT field_device_owner_nid FROM content_type_device where nid not in (select nid from xref_history where type=3))");
        while (result.next()) {
            throw new EtlException(
                    "Can't delete customer record because it has some devices!");
        }
        super.setExpired(con, nid);
    }
    /**
     * @Title: updateRelBuCustomer2Customer
     * @Description:
     * @param stmt
     * stmt
     * @param customerId
     * customerId
     * @param customerNid
     * customerNid
     * @throws Exception
     */
    private void updateRelBuCustomer2Customer(final Statement stmt,
            final String customerId, final long customerNid) throws Exception {

        stmt.executeUpdate("update content_type_bu_customer set field_customer_party_pk_nid="
                + customerNid
                + " where field_bu_customer_account_number_value='"
                + customerId + "'");

    }
    /**
     * @Title: getCustomerNid
     * @Description:
     * @param stmt
     * stmt
     * @param customerName
     * customerName
     * @throws SQLException
     * @return long
     */
    private long
            getCustomerNid(final Statement stmt, final String customerName)
                    throws SQLException {
        long nid = -1;
        if (customerName == null) {
            return -1;
        }
        ResultSet result = stmt
                .executeQuery("select nid from node where title='"
                        + customerName + "' and type='bu_customer' limit 0,1");

        while (result.next()) {
            nid = result.getLong("nid");
            break;
        }

        result.close();
        return nid;

    }
    /**
     * @Title: partyVoiceAddress
     * @Description:
     * @param stmt
     * stmt
     * @param nid
     * nid
     * @param customer
     * customer
     * @param type
     * type
     * @param etlAdmin
     * etlAdmin
     * @throws Exception
     */
    private void partyVoiceAddress(final Statement stmt, final long nid,
            final Customer customer, final String type, final long etlAdmin)
            throws Exception {

        if (type.equals("phone")) {
            if (isExistPartyVoiceAddress(stmt, nid, type)) {
                stmt.executeUpdate("update content_type_party_voice_address set field_voice_address_value='"
                        + customer.getPHONE().replace("'", "\\\'")
                        + "' where field_voice_party_pk_nid="
                        + nid
                        + " and field_voice_type_value='phone'");
            } else {

                this.partyVoiceAddress(stmt, nid, etlAdmin, type, customer);
            }
        } else if (type.equals("fax")) {
            if (isExistPartyVoiceAddress(stmt, nid, type)) {
                stmt.executeUpdate("update content_type_party_voice_address set field_voice_address_value='"// field_voice_type_value
                        + customer.getPHONE().replace("'", "\\\'")
                        + "' where field_voice_party_pk_nid="
                        + nid
                        + " and field_voice_type_value='fax'");
            } else {
                this.partyVoiceAddress(stmt, nid, etlAdmin, type, customer);
            }
        }

    }
    /**
     * @Title: isExistPartyVoiceAddress
     * @Description:
     * @param stmt
     * stmt
     * @param nid
     * nid
     * @param type
     * type
     * @throws Exception
     * @return boolean
     */
    private boolean isExistPartyVoiceAddress(final Statement stmt,
            final long nid, final String type) throws Exception {
        boolean isExist = false;
        ResultSet result = stmt
                .executeQuery("select field_voice_address_value from content_type_party_voice_address where field_voice_type_value='"
                        + type + "' and field_voice_party_pk_nid=" + nid);
        while (result.next()) {
            isExist = true;
            break;
        }
        result.close();
        return isExist;
    }
    /**
     * @Title: customerNameInserts
     * @Description:
     * @param stmt
     * stmt
     * @param customer
     * customer
     * @param customertypeNid
     * customertypeNid
     * @param etlAdmin
     * etlAdmin
     * @throws Exception
     * @return long
     */
    private long customerNameInserts(final Statement stmt,
            final Customer customer, final long customertypeNid,
            final long etlAdmin) throws Exception {
        StringBuilder nodeRevisions = new StringBuilder();
        StringBuilder node = new StringBuilder();
        StringBuilder contentTypeParty = new StringBuilder();

        nodeRevisions.append(etlAdmin + ",'"
                + customer.getNAME().replace("'", "\\'")
                + "','','','',unix_timestamp(),0),");
        node.append(" 'party','','" + customer.getNAME().replace("'", "\\'")
                + "'," + etlAdmin
                + ",'1',unix_timestamp(),unix_timestamp(),0,0,0),");
        contentTypeParty.append(customertypeNid + ")  ");

        return this.insert(stmt, node, nodeRevisions, contentTypeParty, null,
                null);
    }
    /**
     * @Title: customerIdInserts
     * @Description:
     * @param stmt
     * stmt
     * @param etlAdmin
     * etlAdmin
     * @param customerNid
     * customerNid
     * @param customer
     * customer
     * @throws Exception
     */
    private void customerIdInserts(final Statement stmt, final long etlAdmin,
            final long customerNid, final Customer customer) throws Exception {
        StringBuilder nodeRevisions = new StringBuilder();
        StringBuilder node = new StringBuilder();
        StringBuilder contentTypeBuCustomer = new StringBuilder();

        nodeRevisions.append(etlAdmin + ",'" + customer.getCUSTOMER_ID()
                + "','','','',unix_timestamp(),0),");
        node.append("'bu_customer','','" + customer.getCUSTOMER_ID() + "',"
                + etlAdmin + ",'1',unix_timestamp(),unix_timestamp(),0,0,0),");
        contentTypeBuCustomer.append("'" + customer.getCUSTOMER_ID() + "','"
                + customerNid + "'),");

        this.insert(stmt, node, nodeRevisions, null, contentTypeBuCustomer,
                null);
    }
    /**
     * @Title: partyVoiceAddress
     * @Description:
     * @param stmt
     * stmt
     * @param cusomerNid
     * cusomerNid
     * @param etlAdmin
     * etlAdmin
     * @param type
     * type
     * @param customer
     * customer
     * @throws Exception
     */
    private void partyVoiceAddress(final Statement stmt, final long cusomerNid,
            final long etlAdmin, final String type, final Customer customer)
            throws Exception {
        StringBuilder nodeRevisions = new StringBuilder();
        StringBuilder node = new StringBuilder();
        StringBuilder contentTypePartyVoiceAddress = new StringBuilder();

        nodeRevisions.append(etlAdmin + ",'"
                + customer.getNAME().replace("'", "\\\'")
                + "','','','',unix_timestamp(),0),");
        node.append("'party_voice_address','','"
                + customer.getNAME().replace("'", "\\\'") + "'," + etlAdmin
                + ",'1',unix_timestamp(),unix_timestamp(),0,0,0),");

        if (type.equals("phone")) {
            contentTypePartyVoiceAddress.append("'" + cusomerNid
                    + "','phone','" + customer.getPHONE() + "'),");
        } else if (type.equals("fax")) {
            contentTypePartyVoiceAddress.append("'" + cusomerNid + "','fax','"
                    + customer.getFAX() + "'),");
        }
        this.insert(stmt, node, nodeRevisions, null, null,
                contentTypePartyVoiceAddress);
    }
    /**
     * @Title: insertExpireField
     * @Description:
     * @param stmt
     * stmt
     * @param nid
     * nid
     * @param vid
     * vid
     * @throws SQLException
     */
    private void insertExpireField(final Statement stmt, final long nid,
            final long vid) throws SQLException {
        ResultSet result = stmt
                .executeQuery("select vid from content_field_expiration_datetime where nid="
                        + nid);
        if (result.next()) {
            stmt.executeUpdate("update content_field_expiration_datetime set field_expiration_datetime_value = null where nid= "
                    + nid);
        } else {
            stmt.executeUpdate("insert into content_field_expiration_datetime ( vid , nid ) values ("
                    + vid + "," + nid + ")");
        }
    }
    /**
     * @Title: insert
     * @Description:
     * @param stmt
     * stmt
     * @param node
     * node
     * @param nodeRevisions
     * nodeRevisions
     * @param contentTypeParty
     * contentTypeParty
     * @param contentTypeBuCustomer
     * contentTypeBuCustomer
     * @param contentTypePartyVoiceAddress
     * contentTypePartyVoiceAddress
     * @throws Exception
     * @return long
     */
    private long insert(final Statement stmt, final StringBuilder node,
            final StringBuilder nodeRevisions,
            final StringBuilder contentTypeParty,
            final StringBuilder contentTypeBuCustomer,
            final StringBuilder contentTypePartyVoiceAddress) throws Exception {

        long vid = 0;
        long nid = 0;
        try {

            if (nodeRevisions != null) {
                vid = insertWithReturnKey(
                        stmt.getConnection(),
                        "insert into node_revisions (nid,uid,title,body,teaser,log,timestamp,format) values (0,"
                                + nodeRevisions.substring(0,
                                        nodeRevisions.length() - 1));
            }
            if (node != null) {
                nid = insertWithReturnKey(
                        stmt.getConnection(),
                        "insert into node (vid,type,language,title,uid,status,created,changed,comment,promote,"
                                + "translate) values ("
                                + vid
                                + ","
                                + node.substring(0, node.length() - 1));
                updateNidWithVid(stmt.getConnection(), vid, nid);
            }
            if (contentTypeParty != null) {
                stmt.executeUpdate("insert into content_type_party (vid,nid,field_party_type_nid) values ("
                        + vid
                        + ","
                        + nid
                        + ","
                        + contentTypeParty.substring(0,
                                contentTypeParty.length() - 1));
                // insert data to expire field
                insertExpireField(stmt, nid, vid);

            }
            if (contentTypeBuCustomer != null) {
                stmt.executeUpdate("insert into content_type_bu_customer (vid,nid,field_bu_customer_account_number_value,"
                        + "field_customer_party_pk_nid) values ("
                        + vid
                        + ","
                        + nid
                        + ","
                        + contentTypeBuCustomer.substring(0,
                                contentTypeBuCustomer.length() - 1));
                // insert data to expire field
                insertExpireField(stmt, nid, vid);

            }
            if (contentTypePartyVoiceAddress != null
                    && contentTypePartyVoiceAddress.length() != 0) {
                stmt.executeUpdate("insert into content_type_party_voice_address (vid,nid,field_voice_party_pk_nid, "
                        + "field_voice_type_value, field_voice_address_value) values ("
                        + vid
                        + ","
                        + nid
                        + ","
                        + contentTypePartyVoiceAddress.substring(0,
                                contentTypePartyVoiceAddress.length() - 1));
            }

        } catch (Exception ex) {
            LOGGER.error("Exception:", ex);
            throw ex;
        }
        return nid;

    }
}
