package com.covidien.etl.worker;

import java.util.Date;

import org.junit.Test;

public class EtlWorkerTest {

	private EtlWorker worker;

	@Test
	public void testWork() {
		System.out.println("start:" + new Date());
		worker = new EtlWorker();
		worker.work();
		System.out.println("finished:" + new Date());
	}

	@Test
	public void testOld() {
		System.out.println("start:" + new Date());
		worker = new EtlWorker();
		worker.work();
		System.out.println("finished:" + new Date());
	}
}
