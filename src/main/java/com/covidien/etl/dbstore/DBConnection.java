package com.covidien.etl.dbstore;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.covidien.etl.common.Constant;
import com.covidien.etl.common.PropertyReader;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * @ClassName: DBConnection
 * @Description:
 */
public final class DBConnection {
    /**
     * Define a log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class);
    /**
     * Define a mysql datasource.
     */
    private static MysqlDataSource datasource;
    /**
     * dbName.
     */
    private String dbName;
    /**
     * host.
     */
    private String host;
    /**
     * user.
     */
    private String user;
    /**
     * password.
     */
    private String password;
    /**
     * port.
     */
    private String port;
    /**
     * dbConnection.
     */
    private static DBConnection dbConnection = new DBConnection();
    /**
     * connection.
     */
    private Connection connection;

    /**
     * @Title: getInstance
     * @Description:
     * @return DBConnection
     */
    public static DBConnection getInstance() {
        return dbConnection;
    }

    /**
     * @Title: DBConnection
     * @Description:
     */
    private DBConnection() {
        Properties p = PropertyReader.getInstance().read(Constant.getConfigFilePath() + "dbConfig.properties");

        this.dbName = p.getProperty("dbName");
        this.host = p.getProperty("host");
        this.port = p.getProperty("port");
        this.user = p.getProperty("user");
        this.password = p.getProperty("password");

        datasource = new MysqlDataSource();
        datasource.setDatabaseName(this.dbName);
        datasource.setUser(this.user);
        datasource.setPassword(this.password);
        datasource.setServerName(this.host);
        datasource.setPort(Integer.parseInt(this.port));
        LOGGER.info("trying to connect database " + this.host + ":" + this.port + "/" + this.dbName);
        try {
            connection = datasource.getConnection();
        } catch (SQLException e) {
            LOGGER.error("exception:", e);
        }
    }

    /**
     * @Title: reconnect
     * @Description:
     */
    public void reconnect() {
        dbConnection = new DBConnection();
    }

    /**
     * @Title: close
     * @Description:
     */
    public void close() {
        try {
            dbConnection.getConnection().close();
        } catch (SQLException e) {
            LOGGER.error("exception:", e);
        }
    }

    /**
     * @Title: getConnection
     * @Description: Create new datasource if there is one not already
     *               available.
     * @return Connection
     * @throws SQLException
     *         SQLException
     */
    public Connection getConnection()
        throws SQLException {
        if (connection.isClosed()) {
            reconnect();
        }
        return connection;
    }

}
