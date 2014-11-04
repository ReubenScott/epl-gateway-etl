/**
 * @Title: DBUtiltityFunctionsTest.java
 * @Package com.covidien.etl.dbstore
 * @author tony.zhang2
 * @date 2014-2-21
 * @version V2.0
 */
package com.covidien.etl.dbstore;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

/**
 * @ClassName: DBUtiltityFunctionsTest
 */
public class DBUtiltityFunctionsTest {

    @Test
    public void test() {
        Connection con = null;
        try {
            con = DBConnection.getInstance().getConnection();
            DBUtiltityFunctions.init(con);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            DBUtiltityFunctions.checkAnyCustomerRecordAddedByETL();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            DBUtiltityFunctions.checkAnyLocationRoleRecordAddedByETL();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            DBUtiltityFunctions.checkDuplicateSerialNumber("SCD 700", "dsadas");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            DBUtiltityFunctions.getDeviceType("2950", "E1");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            DBUtiltityFunctions.getDeviceTypeSerialNumbers("SCD 700");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            DBUtiltityFunctions.getSerialNumberValidation("SCD 700");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            DBUtiltityFunctions.getunknownCustomerNid();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
