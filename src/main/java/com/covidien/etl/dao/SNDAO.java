package com.covidien.etl.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import com.covidien.etl.dbstore.DBConnection;
import com.covidien.etl.model.Device;

/**
 * @ClassName: SNDAO
 * @Description:
 */
public class SNDAO {
    /**
     * Define a db connection.
     */
    private DBConnection dbConnection;
    /**
     * @Title: getDbConnection
     * @Description:
     * @return DBConnection
     */
    public final DBConnection getDbConnection() {
        return dbConnection;
    }
    /**
     * Define a log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(SNDAO.class);
    /**
     * @Title: SNDAO
     * @Description:
     */
    public SNDAO() {
        dbConnection = DBConnection.getInstance();
        try {
            dbConnection.getConnection().setAutoCommit(false);
        } catch (SQLException e) {
            LOGGER.error("Exception:" + e);
        }
    }
    /**
     * @Title: getRuleByDevice
     * @Description:
     * @param device
     * device
     * @return String
     * @throws SQLException
     */
    public final String getRuleByDevice(final Device device)
            throws SQLException {

        String sku = device.getSKU();
        String source = device.getSOURCE_SYSTEM();
        Connection con = dbConnection.getConnection();
        Statement stmt = con.createStatement();
        ResultSet result = stmt
                .executeQuery("select field_serial_number_regex_value from content_type_devicetype where nid in (select sku.nid from content_type_sku sku where sku.field_sku_id_value ='"
                        + sku
                        + "' and  sku.field_source_system_value ='"
                        + source + "')");

        while (result.next()) {
            return result.getString(1);
        }
        return null;
    }

}
