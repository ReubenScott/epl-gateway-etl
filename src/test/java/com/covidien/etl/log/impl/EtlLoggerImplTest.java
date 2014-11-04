package com.covidien.etl.log.impl;

import org.junit.Before;
import org.junit.Test;

import com.covidien.etl.common.EtlType;
import com.covidien.etl.log.EtlLogger;
import com.covidien.etl.model.Customer;
import com.covidien.etl.model.Device;
import com.covidien.etl.model.Location;
import com.covidien.etl.model.LocationRole;

public class EtlLoggerImplTest {
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		EtlLogger logger = EtlLoggerImpl.getInstance();
		try {
			logger.successUpdate(EtlType.Customer, new Customer());
        } catch (Exception e) {
        }
		try {
			logger.log("test log");
        } catch (Exception e) {
        }
		try {
			logger.failDelete(EtlType.Location, new Location());
        } catch (Exception e) {
        }
		try {
			logger.failDelete(EtlType.LocationRole, new LocationRole());
        } catch (Exception e) {
        }
		try {
			logger.failDelete(EtlType.Device, new Device());
        } catch (Exception e) {
        }
	}

}
