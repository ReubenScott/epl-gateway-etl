package com.covidien.etl.model;

import org.junit.Test;

public class DeviceTypeKeyTest {

	@Test
	public void test() {
		DeviceTypeKey dtk = new DeviceTypeKey("fdsf", "E2", "dsadada");
		dtk.getSerailNumberValidation();
		dtk.getSku();
		dtk.getSourceSystem();
	}

}
