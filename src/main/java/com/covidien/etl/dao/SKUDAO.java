package com.covidien.etl.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.covidien.etl.dbstore.DBConnection;
import com.covidien.etl.model.Device;

/**
 * @ClassName: SKUDAO
 * @Description:
 */
public final class SKUDAO {
    /**
     * Define a db connection.
     */
    private DBConnection dbConnection;

    /**
     * @Title: getDbConnection
     * @Description:
     * @return DBConnection
     */
    public DBConnection getDbConnection() {
        return dbConnection;
    }

    /**
     * Define a log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(SKUDAO.class);
    /**
     * Define a database connection.
     */
    private static Connection con = null;
    /**
     * Define a database connection.
     */
    private static PreparedStatement pstmt = null;
    /**
     * Define a map to store query result.
     */
    private static volatile Map<String, String> skuMap = new HashMap<String, String>();

    /**
     * @Title: SKUDAO
     */
    public SKUDAO() {
        dbConnection = DBConnection.getInstance();
        try {
            con = dbConnection.getConnection();
            con.setAutoCommit(false);
            pstmt = con.prepareStatement("select sku.nid from content_type_sku sku "
                    + "where sku.field_sku_id_value =? and  sku.field_source_system_value =?");
        } catch (SQLException e) {
            LOGGER.error("Exception:" + e);
        }
    }

    /**
     * @Title: isSKUExist
     * @Description:
     * @param device
     *        device
     * @return boolean
     * @throws SQLException
     *         SQLException
     */
    public boolean isSKUExist(final Device device)
        throws SQLException {
        String key = device.getSku() + ":" + device.getSourceSystem();
        if (skuMap.size() > 0 && skuMap.get(key) != null) {
            return true;
        } else {

            pstmt.setString(1, device.getSku());
            pstmt.setString(2, device.getSourceSystem());
            ResultSet result = pstmt.executeQuery();
            while (result.next()) {
                skuMap.put(key, result.getString(1));
                return true;
            }
            result.close();
        }
        return false;
    }
}
