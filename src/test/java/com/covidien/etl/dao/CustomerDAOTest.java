package com.covidien.etl.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.covidien.etl.dao.helper.CustomerDAOSQLHelper;
import com.covidien.etl.dbstore.DBConnection;
import com.covidien.etl.dbstore.DBUtiltityFunctions;
import com.covidien.etl.model.Customer;

public class CustomerDAOTest {
    CustomerDAO cDao;
    List<Customer> list = new ArrayList<Customer>();;

    @Before
    public void setUp()
        throws Exception {
        Connection con = DBConnection.getInstance().getConnection();
        DBUtiltityFunctions.init(con);
        CustomerDAOSQLHelper.init(con);
        cDao = new CustomerDAO();
        cDao.init(con);
        Customer cus1 = new Customer();
        cus1.setCustomerId("US-1269421");
        cus1.setName("Aspen Womens Center1");
        cus1.setPhone("(801) 356-0712");
        cus1.setFax("");
        cus1.setLastChangeDate("2013-06-14 01:00:00");
        cus1.setBatchNumber(1);
        cus1.setIsDeleted(0);
        list.add(cus1);
        Customer cus2 = new Customer();
        cus2.setCustomerId("US-1269421");
        cus2.setName("Aspen Womens Center1");
        cus2.setPhone("(801) 356-0712");
        cus2.setFax("(801) 356-0713");
        cus2.setLastChangeDate("2013-06-15 01:00:00");
        cus2.setBatchNumber(1);
        cus2.setIsDeleted(0);
        list.add(cus2);
        Customer cus3 = new Customer();
        cus3.setCustomerId("US-1269421");
        cus3.setName("Aspen Womens Cente1r");
        cus3.setPhone("(801) 356-0712");
        cus3.setFax("(801) 356-0713");
        cus3.setLastChangeDate("2013-06-16 01:00:00");
        cus3.setBatchNumber(1);
        cus3.setIsDeleted(0);
        list.add(cus3);
        Customer cus4 = new Customer();
        cus4.setCustomerId("US-2269421");
        cus4.setName("Aspen Womens Cente2r");
        cus4.setPhone("(801) 356-0712");
        cus4.setFax("(801) 356-0713");
        cus4.setLastChangeDate("2013-06-17 01:00:00");
        cus4.setBatchNumber(1);
        cus4.setIsDeleted(0);
        list.add(cus3);

        Customer cus5 = new Customer();
        cus5.setCustomerId("US-3269421");
        cus5.setName("Aspen Womens Cente2r");
        cus5.setPhone("(801) 356-0712");
        cus5.setFax("(801) 356-0713");
        cus5.setLastChangeDate("2013-06-17 01:00:00");
        cus5.setBatchNumber(1);
        cus5.setIsDeleted(0);
        list.add(cus5);

        Customer cus6 = new Customer();
        cus6.setCustomerId("");
        cus6.setName("Aspen Womens Cente2r");
        cus6.setPhone("(801) 356-0712");
        cus6.setFax("(801) 356-0713");
        cus6.setLastChangeDate("2013-06-17 01:00:00");
        cus6.setBatchNumber(1);
        cus6.setIsDeleted(0);
        list.add(cus6);

        Customer cus7 = new Customer();
        cus7.setCustomerId("US-3269421");
        cus7.setName("");
        cus7.setPhone("(801) 356-0712");
        cus7.setFax("(801) 356-0713");
        cus7.setLastChangeDate("2013-06-17 01:00:00");
        cus7.setBatchNumber(1);
        cus7.setIsDeleted(0);
        list.add(cus7);

        Customer cus8 = new Customer();
        cus8.setCustomerId("US-4269421");
        cus8.setName("Aspen Womens Cente2r");
        cus8.setPhone("(801) 356-0712");
        cus8.setFax("(801) 356-0713");
        cus8.setLastChangeDate("2013-0 01:00:00");
        cus8.setBatchNumber(1);
        cus8.setIsDeleted(0);
        list.add(cus8);
    }

    @Test
    public void test() {
        cDao.process(list);
    }

}
