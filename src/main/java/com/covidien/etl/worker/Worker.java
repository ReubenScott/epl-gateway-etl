package com.covidien.etl.worker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.covidien.etl.common.Constant;
import com.covidien.etl.common.PropertyReader;
import com.covidien.etl.csvreader.Reader;
import com.covidien.etl.dao.BaseDAO;
import com.covidien.etl.job.EtlJobFactory;
import com.covidien.etl.log.EtlLoggerFactory;

/**
 * @ClassName: Worker
 * @Description:
 * @param <T>
 */
public class Worker<T> {
    /**
     * Define a log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(Worker.class);
    /**
     * Worker.
     */
    @SuppressWarnings("rawtypes")
    private Worker nextWorker;
    /**
     * reader.
     */
    private Reader<T> reader;
    /**
     * BaseDAO.
     */
    private BaseDAO<T> dao;

    /**
     * @Title: setDAO
     * @Description:
     * @param dao
     *        dao
     */
    public final void setDao(final BaseDAO<T> dao) {
        this.dao = dao;
    }

    /**
     * @Title: setReader
     * @Description:
     * @param reader
     *        reader
     */
    public final void setReader(final Reader<T> reader) {
        this.reader = reader;
    }

    /**
     * @Title: work
     * @Description:
     */
    public final void work() {
        reader.readCSVFile(this);
        if (nextWorker != null) {
            nextWorker.work();
        } else {
            workerSummary();
        }
    }

    /**
     * @Title: setNextWorker
     * @Description:
     * @param worker
     *        worker
     */
    @SuppressWarnings("rawtypes")
    public final void setNextWorker(final Worker worker) {
        this.nextWorker = worker;
    }

    /**
     * Count the csv log file.
     * 
     * @Title: getTotalCount
     * @param type
     *        CSV log type.
     * @return count.
     */
    private long getTotalCount(final String type) {
        Properties properties = PropertyReader.getInstance().read(Constant.getConfigFilePath() + "log.properties");
        String path = properties.getProperty("log.path");
        String jobId = EtlJobFactory.getCurrentJobId();
        String fileName = jobId + "_" + type + ".CSV";
        File file = new File(path + fileName);
        if (!file.exists()) {
            return 0;
        }
        FileReader fileReader = null;
        BufferedReader in = null;
        long lineNumber = -1;
        try {
            fileReader = new FileReader(path + fileName);

            in = new BufferedReader(fileReader);

            String lineValue;
            while ((lineValue = in.readLine()) != null) {
                if (lineValue.replace("\r", "").replace("\n", "").length() > 0) {
                    lineNumber++;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception:", e);
        } finally {
            try {
                in.close();
                fileReader.close();
            } catch (IOException e) {
                LOGGER.error("Exception:", e);
            }
        }
        if (lineNumber > 0) {
            return lineNumber;
        } else {
            return 0;
        }
    }

    /**
     * Import csv data to database.
     * 
     * @Title: importData
     * @param list
     *        The etl model data in it.
     */
    public final void importData(List<T> list) {
        dao.process(list);
    }

    /**
     * After all worker complete, will audit the workers and send email.
     * 
     * @Title: workerSummary
     */
    private void workerSummary() {
        long customerSuccessInsert = getTotalCount(Constant.CUSTOMER_SUCCESS_INSERT);
        long customerSuccessUpdate = getTotalCount(Constant.CUSTOMER_SUCCESS_UPDATE);
        long customerSuccessDelete = getTotalCount(Constant.CUSTOMER_SUCCESS_DELETE);
        long customerFailInsert = getTotalCount(Constant.CUSTOMER_FAIL_INSERT);
        long customerFailUpdate = getTotalCount(Constant.CUSTOMER_FAIL_UPDATE);
        long customerFailDelete = getTotalCount(Constant.CUSTOMER_FAIL_DELETE);

        long locationSuccessInsert = getTotalCount(Constant.LOCATION_SUCCESS_INSERT);
        long locationSuccessUpdate = getTotalCount(Constant.LOCATION_SUCCESS_UPDATE);
        long locationSuccessDelete = getTotalCount(Constant.LOCATION_SUCCESS_DELETE);
        long locationFailInsert = getTotalCount(Constant.LOCATION_FAIL_INSERT);
        long locationFailUpdate = getTotalCount(Constant.LOCATION_FAIL_UPDATE);
        long locationFailDelete = getTotalCount(Constant.LOCATION_FAIL_DELETE);

        long locationRoleSuccessInsert = getTotalCount(Constant.LOCATION_ROLE_SUCCESS_INSERT);
        long locationRoleSuccessUpdate = getTotalCount(Constant.LOCATION_ROLE_SUCCESS_UPDATE);
        long locationRoleSuccessDelete = getTotalCount(Constant.LOCATION_ROLE_SUCCESS_DELETE);
        long locationRoleFailInsert = getTotalCount(Constant.LOCATION_ROLE_FAIL_INSERT);
        long locationRoleFailUpdate = getTotalCount(Constant.LOCATION_ROLE_FAIL_UPDATE);
        long locationRoleFailDelete = getTotalCount(Constant.LOCATION_ROLE_FAIL_DELETE);

        long deviceSuccessInsert = getTotalCount(Constant.DEVICE_SUCCESS_INSERT);
        long deviceSuccessUpdate = getTotalCount(Constant.DEVICE_SUCCESS_UPDATE);
        long deviceSuccessDelete = getTotalCount(Constant.DEVICE_SUCCESS_DELETE);
        long deviceFailInsert = getTotalCount(Constant.DEVICE_FAIL_INSERT);
        long deviceFailUpdate = getTotalCount(Constant.DEVICE_FAIL_UPDATE);
        long deviceFailDelete = getTotalCount(Constant.DEVICE_FAIL_DELETE);

        long customerTotal = customerSuccessInsert + customerSuccessUpdate + customerSuccessDelete + customerFailInsert
                + customerFailUpdate + customerFailDelete;
        long locationTotal = locationSuccessInsert + locationSuccessUpdate + locationSuccessDelete + locationFailInsert
                + locationFailUpdate + locationFailDelete;
        long locationRoleTotal = locationRoleSuccessInsert + locationRoleSuccessUpdate + locationRoleSuccessDelete
                + locationRoleFailInsert + locationRoleFailUpdate + locationRoleFailDelete;
        long deviceTotal = deviceSuccessInsert + deviceSuccessUpdate + deviceSuccessDelete + deviceFailInsert
                + deviceFailUpdate + deviceFailDelete;

        long total = customerTotal + locationTotal + locationRoleTotal + deviceTotal;

        LOGGER.info("This job processed :" + total + " records");

        String jobId = EtlJobFactory.getCurrentJobId();
        String tdStyle = "style='border:1px solid silver;font-size:12px;color:#2F4F4F;text-align:center;"
                + "width:150px;font-family:Tahoma,Arial,sans-serif;'";
        StringBuilder sb = new StringBuilder(
                "<html><head><style>p{font-family:Tahoma,Arial,sans-serif;}</style></head><body><p>There're " + total
                        + " records processed.<p>");
        sb.append("<table style='border-collapse:collapse;'><tr><td " + tdStyle + " >&nbsp;</td><td " + tdStyle
                + " >Customer</td><td " + tdStyle + " >Location</td><td " + tdStyle + " >LocationRole</td><td "
                + tdStyle + " >Device</td></tr>");
        sb.append("<tr><td " + tdStyle + " >Successfully Insert</td><td " + tdStyle + " >" + customerSuccessInsert
                + "</td><td " + tdStyle + " >" + locationSuccessInsert + "</td><td " + tdStyle + " >"
                + locationRoleSuccessInsert + "</td><td " + tdStyle + " >" + deviceSuccessInsert + "</td></tr>");
        sb.append("<tr><td " + tdStyle + " >Successfully Update</td><td " + tdStyle + " >" + customerSuccessUpdate
                + "</td><td " + tdStyle + " >" + locationSuccessUpdate + "</td><td " + tdStyle + " >"
                + locationRoleSuccessUpdate + "</td><td " + tdStyle + " >" + deviceSuccessUpdate + "</td></tr>");
        sb.append("<tr><td " + tdStyle + " >Successfully Delete</td><td " + tdStyle + " >" + customerSuccessDelete
                + "</td><td " + tdStyle + " >" + locationSuccessDelete + "</td><td " + tdStyle + " >"
                + locationRoleSuccessDelete + "</td><td " + tdStyle + " >" + deviceSuccessDelete + "</td></tr>");
        sb.append("<tr><td " + tdStyle + " >Failed Insert</td><td " + tdStyle + " >" + customerFailInsert + "</td><td "
                + tdStyle + " >" + locationFailInsert + "</td><td " + tdStyle + " >" + locationRoleFailInsert
                + "</td><td " + tdStyle + " >" + deviceFailInsert + "</td></tr>");
        sb.append("<tr><td " + tdStyle + " >Failed Update</td><td " + tdStyle + " >" + customerFailUpdate + "</td><td "
                + tdStyle + " >" + locationFailUpdate + "</td><td " + tdStyle + " >" + locationRoleFailUpdate
                + "</td><td " + tdStyle + " >" + deviceFailUpdate + "</td></tr>");
        sb.append("<tr><td " + tdStyle + " >Failed Delete</td><td " + tdStyle + " >" + customerFailDelete + "</td><td "
                + tdStyle + " >" + locationFailDelete + "</td><td " + tdStyle + " >" + locationRoleFailDelete
                + "</td><td " + tdStyle + " >" + deviceFailDelete + "</td></tr>");
        sb.append("<tr><td " + tdStyle + " >Total</td><td " + tdStyle + " >" + customerTotal + "</td><td " + tdStyle
                + " >" + locationTotal + "</td><td " + tdStyle + " >" + locationRoleTotal
                + "</td><td style='border:1px solid silver;font-size:12px;color:#2F4F4F;text-align:center;"
                + "width:150px;font-family:Tahoma,Arial,sans-serif;' >" + deviceTotal
                + "</td><td style='display:none'></td></tr></table>");
        sb.append("<p>More information please look into server log files.<p>");
        String content = sb.toString();
        EtlLoggerFactory.getLogger().sendEmail("ETL Job:" + jobId + " is completed.", content);
        LOGGER.info("Email is sent out:" + content);
    }

}
