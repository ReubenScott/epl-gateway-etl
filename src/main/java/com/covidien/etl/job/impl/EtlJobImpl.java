package com.covidien.etl.job.impl;

import java.util.Calendar;

import com.covidien.etl.job.EtlJob;
import com.covidien.etl.worker.EtlWorker;

/**
 * @ClassName: EtlJobImpl
 * @Description:
 */
public class EtlJobImpl implements EtlJob {
    /**
     * jobName.
     */
    private String jobName;
    /**
     * @Title: EtlJobImpl
     * @Description:
     */
    public EtlJobImpl() {
        createJobName();
    }
    @Override
    public final boolean start() {
        try {
            EtlWorker worker = new EtlWorker();
            worker.work();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    /**
     * @Title: createJobName
     * @Description:
     */
    private void createJobName() {
        this.jobName = "etl" + Calendar.getInstance().getTimeInMillis();
    }
    @Override
    public final String getJobName() {
        return this.jobName;
    }
}
