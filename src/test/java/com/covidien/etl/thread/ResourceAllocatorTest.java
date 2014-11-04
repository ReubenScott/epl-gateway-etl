package com.covidien.etl.thread;

import org.junit.Before;
import org.junit.Test;

public class ResourceAllocatorTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		ResourceAllocator r = ResourceAllocator.getInstance();
		r.allocateNid(1, 2);
		r.allocateVid(3, 4);
	}

}
