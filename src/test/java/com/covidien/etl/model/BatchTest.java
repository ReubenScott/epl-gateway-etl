package com.covidien.etl.model;

import org.junit.Test;

public class BatchTest {

    @Test
    public void test() {
        Batch b = new Batch();
        b.setBatchNumber(1);
        b.setBatchRunTimestamp("fdsfds");
        b.setFileName("fdsfsd");
        b.setObjectName("fdsf");
        b.setRowsSent(123);
        b.setSourceName("E1");
        b.getBatchNumber();
        b.getBatchRunTimestamp();
        b.getFileName();
        b.getObjectName();
        b.getRowsSent();
        b.getSourceName();
    }

}
