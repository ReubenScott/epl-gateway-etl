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
import com.covidien.etl.dao.helper.CustomerDAOSQLHelper;
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
     * contentTypeBuCustomer.
     */
    private Map<String, String> contentTypeBuCustomer = new HashMap<String, String>();
    /**
     * contentTypePartyVoiceAddress.
     */
    private Map<String, String> contentTypePartyVoiceAddress = new HashMap<String, String>();

    @Override
    public final void process(List<Customer> list) {
        Long customertypeNid = -1L;
        Long etlAdmin = -1L;
        try {
            con = getDbConnection().getConnection();
            con.setAutoCommit(false);
            customertypeNid = DBUtiltityFunctions.getCustomerTypeNid();
            etlAdmin = DBUtiltityFunctions.getUserId(super.getEtlUser());
        } catch (Exception e) {
            LOGGER.error("Failed to process Customer list, list start at: " + list.get(0).getCustomerId()
                    + ";list end at: " + list.get(list.size() - 1).getCustomerId() + ", List size: " + list.size());
            LOGGER.error("exception:", e);
            return;
        }

        for (Customer customer : list) {
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
                nid = super.getXrefIdByPsId(EtlType.Customer, customer.getCustomerId());
            } catch (SQLException e1) {
                LOGGER.error("Exception:", e1);
            }
            // delete customer data
            if (customer.getIsDeleted() == 1) {
                try {
                    if (nid == -1) {
                        throw new Exception("Customer doesn't exist!");
                    }
                    deleteCustomerByNid(nid);
                    super.deleteXrefByPsId(EtlType.Customer, customer.getCustomerId());
                    con.releaseSavepoint(savepoint);
                    savepoint = con.setSavepoint();
                    //con.commit();
                    etlloger.successDelete(EtlType.Customer, customer);
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
                    customer.setException(e.getMessage());
                    etlloger.failDelete(EtlType.Customer, customer);
                }
                continue;
            }
            // update customer data
            if (nid != -1) {
                try {
                    // process update case
                    if (!shouldChange(EtlType.Customer, customer.getCustomerId(),
                            super.formateDate(customer.getLastChangeDate()))) {
                        continue;
                    }
                    LOGGER.info("Update customer is, id: " + customer.getCustomerId() + ";name:" + customer.getName());

                    this.updateCustomerName(nid, customer.getName());

                    if (customer.getPhone() != null) {
                        partyVoiceAddressUpdate(nid, customer, "phone", etlAdmin);
                    }
                    if (customer.getFax() != null) {
                        partyVoiceAddressUpdate(nid, customer, "fax", etlAdmin);
                    }
                    con.releaseSavepoint(savepoint);
                    savepoint = con.setSavepoint();
                    //con.commit();
                    etlloger.successUpdate(EtlType.Customer, customer);
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
                    customer.setException(e.getMessage());
                    etlloger.failUpdate(EtlType.Customer, customer);
                }
                continue;
            }

            // insert customer data
            try {
                //check the format of Last Change Date.
                try {
                    super.formateDate(customer.getLastChangeDate());
                } catch (ParseException e) {
                    LOGGER.error("Exception:", e);
                    customer.setException(e.getMessage());
                    etlloger.failInsert(EtlType.Customer, customer);
                    continue;
                }
                long partyNid = customerNameInserts(customer, customertypeNid, etlAdmin);

                long customerAcctNid = customerIdInserts(etlAdmin, partyNid, customer);

                // Phone Information
                if (customer.getPhone() != null) {
                    partyVoiceAddressInsert(customerAcctNid, etlAdmin, "phone", customer);
                }
                // Fax Information
                if (customer.getFax() != null) {
                    partyVoiceAddressInsert(customerAcctNid, etlAdmin, "fax", customer);
                }

                super.insertXref(EtlType.Customer, customerAcctNid, customer.getCustomerId(),
                        customer.getLastChangeDate());
                con.releaseSavepoint(savepoint);
                savepoint = con.setSavepoint();
                //con.commit();
                LOGGER.info("Inserted customer is:" + customer.getCustomerId() + ":" + customer.getName() + ";nid:"
                        + customerAcctNid);
                etlloger.successInsert(EtlType.Customer, customer);

            } catch (Exception e) {
                LOGGER.error("Exception:", e);
                customer.setException(e.getMessage());
                etlloger.failInsert(EtlType.Customer, customer);

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
     * @Title: deleteCustomerByNid
     * @param nid
     *        account number nid(bu_customer).
     * @throws Exception
     *         Exception
     */
    private void deleteCustomerByNid(final long nid)
        throws Exception {
        PreparedStatement pstmt = null;
        pstmt = CustomerDAOSQLHelper.getPstmtA();
        pstmt.setLong(1, nid);
        ResultSet result = pstmt.executeQuery();
        if (result.next()) {
            throw new EtlException("Can't delete customer record because it has some devices!");
        }
        String customerId = "$";
        pstmt = CustomerDAOSQLHelper.getPstmtB();
        pstmt.setLong(1, nid);
        result = pstmt.executeQuery();
        if (result.next()) {
            customerId = result.getString(1);
        }
        pstmt = CustomerDAOSQLHelper.getPstmtC();
        pstmt.setString(1, customerId + ":%");
        result = pstmt.executeQuery();
        if (result.next()) {
            throw new EtlException("Can't delete customer record because it reference some location role!");
        }
        pstmt = CustomerDAOSQLHelper.getPstmtD();
        pstmt.setLong(1, nid);
        result = pstmt.executeQuery();
        if (result.next()) {
            throw new EtlException("Can't delete customer record because it reference some users!");
        }
        long partyNid = -1;
        pstmt = CustomerDAOSQLHelper.getPstmtE();
        pstmt.setLong(1, nid);
        result = pstmt.executeQuery();
        while (result.next()) {
            partyNid = result.getLong(1);
        }
        result.close();
        super.setExpired(nid);
        super.setExpired(partyNid);
    }

    /**
     * @Title: updateCustomerName
     * @param acctNumNid
     *        account number nid.
     * @param customerName
     *        customerName
     * @throws Exception
     *         Exception
     */
    private void updateCustomerName(final long acctNumNid, final String customerName)
        throws Exception {
        PreparedStatement pstmt = null;
        //update party.
        ResultSet result = null;
        long partyNid = -1;
        pstmt = CustomerDAOSQLHelper.getPstmtE();
        pstmt.setLong(1, acctNumNid);
        result = pstmt.executeQuery();
        if (result.next()) {
            partyNid = result.getLong(1);
        }
        pstmt = CustomerDAOSQLHelper.getPstmtG();
        pstmt.setString(1, customerName);
        pstmt.setLong(2, partyNid);
        pstmt.executeUpdate();

    }

    /**
     * @Title: partyVoiceAddressUpdate
     * @param nid
     *        nid
     * @param customer
     *        customer
     * @param type
     *        type
     * @param etlAdmin
     *        etlAdmin
     * @throws Exception
     *         Exception
     */
    private void partyVoiceAddressUpdate(final long nid, final Customer customer, final String type, final long etlAdmin)
        throws Exception {

        if (isExistPartyVoiceAddress(nid, type)) {
            PreparedStatement pstmt = null;
            pstmt = CustomerDAOSQLHelper.getPstmtR();
            if (type.equals("phone")) {
                pstmt.setString(1, customer.getPhone());
            } else if (type.equals("fax")) {
                pstmt.setString(1, customer.getFax());
            }
            pstmt.setLong(2, nid);
            pstmt.setString(3, type);
            pstmt.executeUpdate();
        } else {
            this.partyVoiceAddressInsert(nid, etlAdmin, type, customer);
        }

    }

    /**
     * @Title: isExistPartyVoiceAddress
     * @param nid
     *        nid
     * @param type
     *        type
     * @throws Exception
     *         Exception
     * @return boolean
     */
    private boolean isExistPartyVoiceAddress(final long nid, final String type)
        throws Exception {
        boolean isExist = false;
        PreparedStatement pstmt = null;
        pstmt = CustomerDAOSQLHelper.getPstmtQ();
        pstmt.setString(1, type);
        pstmt.setLong(2, nid);
        ResultSet result = pstmt.executeQuery();
        while (result.next()) {
            isExist = true;
            break;
        }
        result.close();
        return isExist;
    }

    /**
     * @Title: customerNameInserts
     * @param customer
     *        customer
     * @param customertypeNid
     *        customertypeNid
     * @param etlAdmin
     *        etlAdmin
     * @throws Exception
     *         Exception
     * @return long
     */
    private long customerNameInserts(final Customer customer, final long customertypeNid, final long etlAdmin)
        throws Exception {
        nodeRevisions.clear();
        node.clear();
        String title = customer.getName();
        if (title == null || title == "") {
            throw new EtlException("Costomer Name cannot be empty!");
        }
        nodeRevisions.put("uid", String.valueOf(etlAdmin));
        nodeRevisions.put("title", title);
        node.put("uid", String.valueOf(etlAdmin));
        node.put("title", title);
        node.put("type", "party");

        return this.insert(node, nodeRevisions, String.valueOf(customertypeNid), null, null);
    }

    /**
     * @Title: customerIdInserts
     * @param etlAdmin
     *        etlAdmin
     * @param customerNid
     *        customerNid
     * @param customer
     *        customer
     * @throws Exception
     *         Exception
     * @return long
     */
    private long customerIdInserts(final long etlAdmin, final long customerNid, final Customer customer)
        throws Exception {
        nodeRevisions.clear();
        node.clear();
        contentTypeBuCustomer.clear();
        String title = customer.getCustomerId();
        if (title == null || title == "") {
            throw new EtlException("Costomer Id cannot be empty!");
        }
        nodeRevisions.put("uid", String.valueOf(etlAdmin));
        nodeRevisions.put("title", title);
        node.put("uid", String.valueOf(etlAdmin));
        node.put("title", title);
        node.put("type", "bu_customer");
        contentTypeBuCustomer.put("customerNumber", customer.getCustomerId());
        contentTypeBuCustomer.put("customerNid", String.valueOf(customerNid));

        return this.insert(node, nodeRevisions, null, contentTypeBuCustomer, null);
    }

    /**
     * @Title: partyVoiceAddress
     * @param cusomerNid
     *        cusomerNid
     * @param etlAdmin
     *        etlAdmin
     * @param type
     *        type
     * @param customer
     *        customer
     * @throws Exception
     *         Exception
     */
    private void partyVoiceAddressInsert(final long cusomerNid, final long etlAdmin, final String type,
            final Customer customer)
        throws Exception {

        nodeRevisions.clear();
        node.clear();
        contentTypePartyVoiceAddress.clear();
        String title = customer.getName();
        if (title == null || title == "") {
            throw new EtlException("Costomer Name cannot be empty!");
        }
        nodeRevisions.put("uid", String.valueOf(etlAdmin));
        nodeRevisions.put("title", title);
        node.put("uid", String.valueOf(etlAdmin));
        node.put("title", title);
        node.put("type", "party_voice_address");
        contentTypePartyVoiceAddress.put("type", type);
        contentTypePartyVoiceAddress.put("cusomerNid", String.valueOf(cusomerNid));

        if (type.equals("phone")) {
            contentTypePartyVoiceAddress.put("addressValue", customer.getPhone());
        } else if (type.equals("fax")) {
            contentTypePartyVoiceAddress.put("addressValue", customer.getFax());
        }
        this.insert(node, nodeRevisions, null, null, contentTypePartyVoiceAddress);
    }

    /**
     * @Title: insertExpireField
     * @param nid
     *        nid
     * @param vid
     *        vid
     * @throws SQLException
     *         SQLException
     */
    private void insertExpireField(final long nid, final long vid)
        throws SQLException {
        PreparedStatement pstmt = null;
        pstmt = CustomerDAOSQLHelper.getPstmtL();
        pstmt.setLong(1, nid);
        ResultSet result = pstmt.executeQuery();
        if (result.next()) {
            pstmt = CustomerDAOSQLHelper.getPstmtM();
            pstmt.setLong(1, nid);
            pstmt.executeUpdate();
        } else {
            pstmt = CustomerDAOSQLHelper.getPstmtN();
            pstmt.setLong(1, vid);
            pstmt.setLong(2, nid);
            pstmt.executeUpdate();
        }
        result.close();
    }

    /**
     * @Title: insert
     * @param nodeI
     *        node
     * @param nodeRevisionsI
     *        nodeRevisions
     * @param contentTypeParty
     *        contentTypeParty
     * @param contentTypeBuCustomerI
     *        contentTypeBuCustomer
     * @param contentTypePartyVoiceAddressI
     *        contentTypePartyVoiceAddress
     * @throws Exception
     *         Exception
     * @return long
     */
    private long insert(final Map<String, String> nodeI, final Map<String, String> nodeRevisionsI,
            final String contentTypeParty, final Map<String, String> contentTypeBuCustomerI,
            final Map<String, String> contentTypePartyVoiceAddressI)
        throws Exception {

        long vid = 0;
        long nid = 0;
        PreparedStatement pstmt = null;
        try {
            if (nodeRevisionsI != null && nodeRevisionsI.size() > 0) {
                pstmt = CustomerDAOSQLHelper.getPstmtI();
                pstmt.setString(1, nodeRevisionsI.get("uid"));
                pstmt.setString(2, nodeRevisionsI.get("title"));
                vid = insertWithReturnKey(pstmt);
            }
            if (nodeI != null && nodeI.size() > 0) {
                pstmt = CustomerDAOSQLHelper.getPstmtJ();
                pstmt.setLong(1, vid);
                pstmt.setString(2, nodeI.get("type"));
                pstmt.setString(3, nodeI.get("title"));
                pstmt.setString(4, nodeI.get("uid"));
                nid = insertWithReturnKey(pstmt);
                updateNidWithVid(vid, nid);
            }
            if (contentTypeParty != null && contentTypeParty.length() > 0) {
                pstmt = CustomerDAOSQLHelper.getPstmtK();
                pstmt.setLong(1, vid);
                pstmt.setLong(2, nid);
                pstmt.setLong(3, Long.valueOf(contentTypeParty).longValue());
                pstmt.executeUpdate();
                // insert data to expire field
                insertExpireField(nid, vid);

            }
            if (contentTypeBuCustomerI != null && contentTypeBuCustomerI.size() > 0) {
                pstmt = CustomerDAOSQLHelper.getPstmtO();
                pstmt.setLong(1, vid);
                pstmt.setLong(2, nid);
                pstmt.setString(3, contentTypeBuCustomerI.get("customerNumber"));
                pstmt.setString(4, contentTypeBuCustomerI.get("customerNid"));
                pstmt.executeUpdate();
                // insert data to expire field
                insertExpireField(nid, vid);

            }
            if (contentTypePartyVoiceAddressI != null && contentTypePartyVoiceAddressI.size() != 0) {
                pstmt = CustomerDAOSQLHelper.getPstmtP();
                pstmt.setLong(1, vid);
                pstmt.setLong(2, nid);
                pstmt.setString(3, contentTypePartyVoiceAddressI.get("customerNid"));
                pstmt.setString(4, contentTypePartyVoiceAddressI.get("type"));
                pstmt.setString(5, contentTypePartyVoiceAddressI.get("addressValue"));
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
