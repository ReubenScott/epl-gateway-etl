/**
 * @Title: DBConnectionTest.java
 * @Package com.covidien.etl.dbstore
 * @Description:
 * @author tony.zhang2
 * @date 2013-12-17
 * @version V2.0
 */
package com.covidien.etl.dbstore;

import java.sql.SQLException;

import org.junit.Test;

/**
 * @ClassName: DBConnectionTest
 * @Description:
 */
public class DBConnectionTest {

    @Test
    public void test() {
        DBConnection dbCon = DBConnection.getInstance();
        try {
            if (dbCon.getConnection().isValid(10)) {
                System.out.println("##### DB connection testing is passed!");
            } else {
                System.err.println("##### DB connection testing is failed!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
