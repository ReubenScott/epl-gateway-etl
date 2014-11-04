package com.covidien.etl.job;

import org.junit.Before;
import org.junit.Test;

public class EtlMainTest {
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws Exception {
		String[] args = new String[]{"--skuInit", "--snInit"};
		EtlMain.main(args);
	}

}
