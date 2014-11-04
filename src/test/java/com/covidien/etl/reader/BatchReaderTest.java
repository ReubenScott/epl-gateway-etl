package com.covidien.etl.reader;

import java.util.List;
import org.junit.Test;
import com.covidien.etl.csvreader.BatchReader;
import com.covidien.etl.model.Batch;

public class BatchReaderTest {

	@Test
	public void testReader() {
		System.out.println("ok");
		BatchReader reader = new BatchReader();
		List<Batch> list = reader.readCSVFile();
		for (Batch batch : list) {
			System.out.println(batch);
		}
	}
}
