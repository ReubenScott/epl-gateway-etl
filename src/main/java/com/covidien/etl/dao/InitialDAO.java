/**
 * @Title: InitialDAO.java
 * @Package com.covidien.etl.dao
 * @author tony.zhang2
 * @date 2014-2-28
 * @version V2.0
 */
package com.covidien.etl.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.covidien.etl.dbstore.DBConnection;

/**
 * @ClassName: InitialDAO
 */
public class InitialDAO {
    /**
     * Define a log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(CustomerDAO.class);
    /**
     * Define a database connection.
     */
    private Connection con = null;
    /**
     * Defien a Statement object.
     */
    private Statement stmt = null;

    /**
     * The function will extract the exist customer,location,locationRole,device
     * from database at first time run etl, and insert to xref table.
     * 
     * @throws Exception
     *         Exception
     * @Title: initialXref
     */
    public final void initialXref()
        throws Exception {
        try {
            con = DBConnection.getInstance().getConnection();
            con.setAutoCommit(false);
            stmt = con.createStatement();
        } catch (Exception e) {
            LOGGER.error("Exception:", e);
            return;
        }

        long total = getXrefCount();
        //if xref table don't have records, then extracts data from database.
        if (total < 1) {
            try {
                LOGGER.info("Start to extract customer");
                extractCutomer();
                LOGGER.info("End to extract customer");

                LOGGER.info("Start to extract location");
                extractLocation();
                LOGGER.info("End to extract location");

                LOGGER.info("Start to extract location role");
                extractLocationRole();
                LOGGER.info("End to extract location role");

                LOGGER.info("Start to extract device");
                extractDevice();
                LOGGER.info("End to extract device");
            } catch (Exception e) {
                throw e;
            } finally {
                con.rollback();
            }
            con.commit();
        }

    }

    /**
     * get xref table total records.
     * 
     * @Title: getXrefCount
     * @return records count.
     * @throws SQLException
     *         SQLException
     */
    private long getXrefCount()
        throws SQLException {
        long total = 0;
        ResultSet result = stmt.executeQuery("select count(1) as total from xref");
        while (result.next()) {
            total = result.getLong("total");
            break;
        }
        result.close();
        return total;
    }

    /**
     * Extract customer from database and insert to xref table.
     * 
     * @Title: extractCutomer
     * @throws Exception
     *         Exception
     */
    private void extractCutomer()
        throws Exception {
        try {
            stmt.executeUpdate("insert into xref(nid,ps_id,type,ps_last_change_time,created_time,updated_time) "
                    + "select c.nid,c.field_bu_customer_account_number_value,0,0, "
                    + "UNIX_TIMESTAMP(NOW()),UNIX_TIMESTAMP(NOW()) from content_type_bu_customer c "
                    + "left join content_field_expiration_datetime d on c.nid=d.nid and c.vid=d.vid "
                    + "where d.field_expiration_datetime_value is null;");
        } catch (Exception e) {
            LOGGER.error("Exception: When extract customer from database", e);
            throw e;
        }
    }

    /**
     * Extract location from database and insert to xref table.
     * 
     * @Title: extractLocation
     * @throws Exception
     *         Exception
     */
    private void extractLocation()
        throws Exception {
        try {
            stmt.executeUpdate("insert into xref(nid,ps_id,type,ps_last_change_time,created_time,updated_time) "
                    + "select nid,title,1,0,UNIX_TIMESTAMP(NOW()),UNIX_TIMESTAMP(NOW()) "
                    + "from node where type='postal_address';");
        } catch (Exception e) {
            LOGGER.error("Exception: When extract location from database", e);
            throw e;
        }
    }

    /**
     * Extract location role from database and insert to xref table.
     * 
     * @Title: extractLocationRole
     * @throws Exception
     *         Exception
     */
    private void extractLocationRole()
        throws Exception {
        try {
            stmt.executeUpdate("insert into xref(nid,ps_id,type,ps_last_change_time,created_time,updated_time) "
                    + "select max(a.nid),concat(b.title,':',c.title) ,2,0,UNIX_TIMESTAMP(NOW()),UNIX_TIMESTAMP(NOW()) "
                    + "from content_type_party_postal_address a, node b, node c "
                    + "where a.field_party_postal_address_nid = b.nid "
                    + "and a.field_party_postal_address_ref_nid = c.nid group by b.title,c.title;");
        } catch (Exception e) {
            LOGGER.error("Exception: When extract location role from database", e);
            throw e;
        }
    }

    /**
     * Extract device from database and insert to xref table.
     * 
     * @Title: extractDevice
     * @throws Exception
     *         Exception
     */
    private void extractDevice()
        throws Exception {
        try {
            stmt.executeUpdate("insert into xref(nid,ps_id,type,ps_last_change_time,created_time,updated_time) "
                    + "select max(a.nid),concat(a.title,':',c.title),3,0,UNIX_TIMESTAMP(NOW()),UNIX_TIMESTAMP(NOW()) "
                    + "from node a, content_field_device_type b, node c where a.type='device' "
                    + "and a.nid = b.nid and b.field_device_type_nid = c.nid group by a.title,c.title;");
        } catch (Exception e) {
            LOGGER.error("Exception: When extract device from database", e);
            throw e;
        }
    }

}
