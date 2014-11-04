package com.covidien.etl.reader;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class CustomerReaderTest {

//	private CustomerReader reader;

	@Before
	public void setup() {
		// reader = new CustomerReader("10.243.235.88", "covidiendb", "root",
		// "password", "3306");
		// reader = new CustomerReader("192.168.1.108", "covidiendb", "root",
		// "password", "3306");
	}

	@Test
	public void testReadCustomerCSVFile() throws Exception {
		String path = this.getClass().getClassLoader().getResource("")
				.getPath()
				+ "/csvFiles/CUSTOMER.csv";
		System.out.println("path is :" + path);
		// reader.readCustomerCSVFile(path);
	}

	@Test
	public void testList() {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < 100; i++) {
			list.add(i);
		}
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		int size = 15;
		for (int i = 0; i < 100 / 15 + 1; i++) {
			int total = size * (i + 1);
			if (total >= 100) {
				total = 100;
			}
			List<Integer> temp = list.subList(size * i, total);
			result.add(temp);
		}

		for (int i = 0; i < result.size(); i++) {
			System.out.println("index:" + i);
			for (int j = 0; j < result.get(i).size(); j++) {
				System.out.print(result.get(i).get(j) + "  ");
			}
			System.out.println();
		}
	}

}
