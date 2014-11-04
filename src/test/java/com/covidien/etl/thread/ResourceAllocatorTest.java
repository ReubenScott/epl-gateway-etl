package com.covidien.etl.thread;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import com.covidien.etl.dbstore.DBConnection;
import com.covidien.etl.dbstore.DBUtiltityFunctions;

public class ResourceAllocatorTest {

    @Before
    public void setUp()
        throws Exception {
    }

    @Test
    public void test() {
        try {
            DBUtiltityFunctions.init(DBConnection.getInstance().getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ResourceAllocator r = ResourceAllocator.getInstance();
        r.allocateNid(1, 2);
        r.allocateVid(3, 4);
    }

}
