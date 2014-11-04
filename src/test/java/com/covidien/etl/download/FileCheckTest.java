package com.covidien.etl.download;

import org.junit.Before;
import org.junit.Test;

public class FileCheckTest {
	FileCheck fCheck;
	@Before
	public void setUp() throws Exception {
		fCheck = new FileCheck();
	}

	@Test
	public void test(){
		try {
			fCheck.saveMD5();
        } catch (Exception e) {
        }
		try {
			fCheck.checkDataFile();
        } catch (Exception e) {
        }
		try {
			fCheck.checkBatchFile();
        } catch (Exception e) {
        }
		try {
			FileCheck.main(new String[]{});
        } catch (Exception e) {
        }
		try {
			fCheck.readBatchFile("CUSTOMER_BATCH.CSV");
        } catch (Exception e) {
        }
	}
}
