package com.covidien.etl.csvreader;

import org.junit.Before;
import org.junit.Test;

import com.covidien.etl.csvreader.CustomerReader;
import com.covidien.etl.csvreader.DeviceReader;
import com.covidien.etl.csvreader.LocationReader;
import com.covidien.etl.csvreader.LocationRoleReader;
import com.covidien.etl.download.FtpUtil;

public class CsvReaderTest {

    @Before
    public void setUp() {
        FtpUtil ftpUtil = new FtpUtil();
        ftpUtil.download();
    }

    @Test
    public void test() {
        CustomerReader c = new CustomerReader();
        c.getCSVinfo();
        DeviceReader d = new DeviceReader();
        d.getCSVinfo();
        LocationReader l = new LocationReader();
        l.getCSVinfo();
        LocationRoleReader r = new LocationRoleReader();
        r.getCSVinfo();
    }

}
