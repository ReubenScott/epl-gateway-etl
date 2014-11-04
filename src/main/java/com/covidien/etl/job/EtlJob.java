package com.covidien.etl.job;

/**
 * @ClassName: EtlJob
 * @Description:
 */
public interface EtlJob {
    /**
     * @Title: start
     * @Description:
     * @return boolean
     */
    boolean start();
    /**
     * @Title: getJobName
     * @Description:
     * @return String
     */
    String getJobName();
}
