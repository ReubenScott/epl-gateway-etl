package com.covidien.etl.job;

import org.junit.Test;

public class EtlMainTest {

    @Test
    public void test()
        throws Exception {
        String[] args = new String[] {
                "--skuInit", "--snInit" };
        EtlMain.main(args);
    }

}
