package com.covidien.etl.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.covidien.etl.dbstore.DBConnection;
import com.covidien.etl.model.Device;

/**
 * @ClassName: SKUDAO
 * @Description:
 */
public class SKUDAO {
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
    private static final Logger LOGGER = Logger.getLogger(SKUDAO.class);
    /**
     * @Title: SKUDAO
     * @Description:
     */
    public SKUDAO() {
        dbConnection = DBConnection.getInstance();
        try {
            dbConnection.getConnection().setAutoCommit(false);
        } catch (SQLException e) {
            LOGGER.error("Exception:" + e);
        }
    }
    /**
     * @Title: isSKUExist
     * @Description:
     * @param device
     * device
     * @return boolean
     * @throws SQLException
     */
    public final boolean isSKUExist(final Device device) throws SQLException {
        String sku = device.getSKU();
        String source = device.getSOURCE_SYSTEM();
        Connection con = dbConnection.getConnection();
        Statement stmt = con.createStatement();
        ResultSet result = stmt
                .executeQuery("select title from node where nid in (select sku.nid from content_type_sku sku where sku.field_sku_id_value ='"
                        + sku
                        + "' and  sku.field_source_system_value ='"
                        + source + "')");
        while (result.next()) {
            return true;
        }
        return false;
    }
}
