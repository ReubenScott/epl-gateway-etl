package com.covidien.etl.job;

import com.covidien.etl.job.impl.EtlJobImpl;

/**
 * @ClassName: EtlJobFactory
 * @Description:
 */
public class EtlJobFactory {
    /**
     * etlJob.
     */
    private static EtlJob etlJob;
    /**
     * @Title: createEtlJob
     * @Description:
     * @return EtlJob
     */
    public static EtlJob createEtlJob() {
        etlJob = new EtlJobImpl();
        return etlJob;
    }
    /**
     * @Title: getCurrentJobId
     * @Description:
     * @return Strings
     */
    public static String getCurrentJobId() {
        if (etlJob == null) {
            return null;
        }
        return etlJob.getJobName();
    }
}
