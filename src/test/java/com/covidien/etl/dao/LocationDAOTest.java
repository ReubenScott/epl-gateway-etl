package com.covidien.etl.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.covidien.etl.dao.helper.LocationDAOSQLHelper;
import com.covidien.etl.dbstore.DBConnection;
import com.covidien.etl.dbstore.DBUtiltityFunctions;
import com.covidien.etl.model.Location;

public class LocationDAOTest {
    LocationDAO lDao;
    List<Location> list = new ArrayList<Location>();

    @Before
    public void setUp()
        throws Exception {
        Connection con = DBConnection.getInstance().getConnection();
        DBUtiltityFunctions.init(con);
        LocationDAOSQLHelper.init(con);
        lDao = new LocationDAO();
        lDao.init(con);
        Location location1 = new Location();
        location1.setLocationId("E1-947140");
        location1.setAddressLine1("7435 W TALCOTT AVE");
        location1.setAddressModifier1("");
        location1.setAddressModifier2("");
        location1.setAddressModifier3("");
        location1.setAddressModifier4("");
        location1.setCity("CHICAGO");
        location1.setStateProvince("IL");
        location1.setPostalCode("60631-3707");
        location1.setCountryCode("US");
        location1.setLastChangeDate("2013-03-22 01:00:00");
        location1.setBatchNumber(1);
        location1.setIsDeleted(0);
        list.add(location1);

        Location location2 = new Location();
        location2.setLocationId("E1-947140");
        location2.setAddressLine1("7435 W TALCOTT AVE");
        location2.setAddressModifier1("dsa");
        location2.setAddressModifier2("ds");
        location2.setAddressModifier3("ds");
        location2.setAddressModifier4("dd");
        location2.setCity("CHICAGO");
        location2.setStateProvince("IL");
        location2.setPostalCode("60631-3707");
        location2.setCountryCode("US");
        location2.setLastChangeDate("2013-03-23 01:00:00");
        location2.setBatchNumber(1);
        location2.setIsDeleted(0);
        list.add(location2);

        Location location3 = new Location();
        location3.setLocationId("E1-947140");
        location3.setAddressLine1("7435 W TALCOTT AVE");
        location3.setAddressModifier1("");
        location3.setAddressModifier2("");
        location3.setAddressModifier3("");
        location3.setAddressModifier4("");
        location3.setCity("CHICAGO");
        location3.setStateProvince("IL");
        location3.setPostalCode("60631-3707");
        location3.setCountryCode("US");
        location3.setLastChangeDate("2013-03-23 01:00:00");
        location3.setBatchNumber(1);
        location3.setIsDeleted(1);
        list.add(location3);

        Location location4 = new Location();
        location4.setLocationId("E1-947141");
        location4.setAddressLine1("7435 W TALCOTT AVE");
        location4.setAddressModifier1("");
        location4.setAddressModifier2("");
        location4.setAddressModifier3("");
        location4.setAddressModifier4("");
        location4.setCity("CHICAGO");
        location4.setStateProvince("IL");
        location4.setPostalCode("60631-3707");
        location4.setCountryCode("US");
        location4.setLastChangeDate("2013-03-23 01:00:00");
        location4.setBatchNumber(1);
        location4.setIsDeleted(0);
        list.add(location4);

        Location location5 = new Location();
        location5.setLocationId("E1-847141");
        location5.setAddressLine1("");
        location5.setAddressModifier1("");
        location5.setAddressModifier2("");
        location5.setAddressModifier3("");
        location5.setAddressModifier4("");
        location5.setCity("CHICAGO");
        location5.setStateProvince("IL");
        location5.setPostalCode("60631-3707");
        location5.setCountryCode("US");
        location5.setLastChangeDate("2013-03-23 01:00:00");
        location5.setBatchNumber(1);
        location5.setIsDeleted(0);
        list.add(location5);

        Location location6 = new Location();
        location6.setLocationId("");
        location6.setAddressLine1("7435 W TALCOTT AVE");
        location6.setAddressModifier1("");
        location6.setAddressModifier2("");
        location6.setAddressModifier3("");
        location6.setAddressModifier4("");
        location6.setCity("CHICAGO");
        location6.setStateProvince("IL");
        location6.setPostalCode("60631-3707");
        location6.setCountryCode("US");
        location6.setLastChangeDate("2013-03-23 01:00:00");
        location6.setBatchNumber(1);
        location6.setIsDeleted(0);
        list.add(location4);

        Location location7 = new Location();
        location7.setLocationId("E1-847142");
        location7.setAddressLine1("7435 W TALCOTT AVE");
        location7.setAddressModifier1("");
        location7.setAddressModifier2("");
        location7.setAddressModifier3("");
        location7.setAddressModifier4("");
        location7.setCity("CHICAGO");
        location7.setStateProvince("ABC");
        location7.setPostalCode("60631-3707");
        location7.setCountryCode("USAAA");
        location7.setLastChangeDate("2013-03-23 01:00:00");
        location7.setBatchNumber(1);
        location7.setIsDeleted(0);
        list.add(location7);

        Location location8 = new Location();
        location8.setLocationId("E1-847143");
        location8.setAddressLine1("7435 W TALCOTT AVE");
        location8.setAddressModifier1("");
        location8.setAddressModifier2("");
        location8.setAddressModifier3("");
        location8.setAddressModifier4("");
        location8.setCity("CHICAGO");
        location8.setStateProvince("ABC");
        location8.setPostalCode("60631-3707");
        location8.setCountryCode("USAAA");
        location8.setLastChangeDate("2013-23 01:00:00");
        location8.setBatchNumber(1);
        location8.setIsDeleted(0);
        list.add(location8);
    }

    @Test
    public void test() {
        lDao.process(list);
    }

}
