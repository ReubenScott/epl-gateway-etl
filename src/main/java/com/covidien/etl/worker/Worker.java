package com.covidien.etl.worker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.covidien.etl.common.Constant;
import com.covidien.etl.common.PropertyReader;
import com.covidien.etl.csvreader.Reader;
import com.covidien.etl.dao.BaseDAO;
import com.covidien.etl.job.EtlJobFactory;
import com.covidien.etl.log.EtlLoggerFactory;
import com.covidien.etl.thread.DAOThread;

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
     * GROUPSIZE.
     */
    private static final int GROUPSIZE = 3500;
    /**
     * @Title: setDAO
     * @Description:
     * @param dao
     * dao
     */
    public final void setDAO(final BaseDAO<T> dao) {
        this.dao = dao;
    }
    /**
     * @Title: setReader
     * @Description:
     * @param reader
     * reader
     */
    public final void setReader(final Reader<T> reader) {
        this.reader = reader;
    }
    /**
     * @Title: work
     * @Description:
     */
    @SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
    public final void work() {

        List<Thread> threadList = new ArrayList<Thread>();
        List<T> list = reader.readCSVFile();

        for (int i = 0; i < list.size() / GROUPSIZE + 1; i++) {
            int groupTatal = GROUPSIZE * (i + 1);
            if (groupTatal >= list.size()) {
                groupTatal = list.size();
            }
            List<T> currentList = list.subList(i * GROUPSIZE, groupTatal);
            Thread thread = new Thread(new DAOThread(dao, currentList));
            thread.start();
            threadList.add(thread);
        }

        LOGGER.info("Thread size is :" + threadList.size());

        LOGGER.info("All etl threads are started");

        boolean isFinished = false;
        while (!isFinished) {
            isFinished = true;
            for (Thread thread : threadList) {
                if (State.TERMINATED != thread.getState()) {
                    isFinished = false;
                }
                try {
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException e) {
                    LOGGER.error("exception:", e);
                }
            }
        }

        LOGGER.info("Etl job is completed.");

        if (nextWorker != null) {
            nextWorker.work();
        } else {

            int customerSuccessInsert = getTotalCount(Constant.CUSTOMER_SUCCESS_INSERT);
            int customerSuccessUpdate = getTotalCount(Constant.CUSTOMER_SUCCESS_UPDATE);
            int customerSuccessDelete = getTotalCount(Constant.CUSTOMER_SUCCESS_DELETE);
            int customerFailInsert = getTotalCount(Constant.CUSTOMER_FAIL_INSERT);
            int customerFailUpdate = getTotalCount(Constant.CUSTOMER_FAIL_UPDATE);
            int customerFailDelete = getTotalCount(Constant.CUSTOMER_FAIL_DELETE);

            int locationSuccessInsert = getTotalCount(Constant.LOCATION_SUCCESS_INSERT);
            int locationSuccessUpdate = getTotalCount(Constant.LOCATION_SUCCESS_UPDATE);
            int locationSuccessDelete = getTotalCount(Constant.LOCATION_SUCCESS_DELETE);
            int locationFailInsert = getTotalCount(Constant.LOCATION_FAIL_INSERT);
            int locationFailUpdate = getTotalCount(Constant.LOCATION_FAIL_UPDATE);
            int locationFailDelete = getTotalCount(Constant.LOCATION_FAIL_DELETE);

            int locationRoleSuccessInsert = getTotalCount(Constant.LOCATION_ROLE_SUCCESS_INSERT);
            int locationRoleSuccessUpdate = getTotalCount(Constant.LOCATION_ROLE_SUCCESS_UPDATE);
            int locationRoleSuccessDelete = getTotalCount(Constant.LOCATION_ROLE_SUCCESS_DELETE);
            int locationRoleFailInsert = getTotalCount(Constant.LOCATION_ROLE_FAIL_INSERT);
            int locationRoleFailUpdate = getTotalCount(Constant.LOCATION_ROLE_FAIL_UPDATE);
            int locationRoleFailDelete = getTotalCount(Constant.LOCATION_ROLE_FAIL_DELETE);

            int deviceSuccessInsert = getTotalCount(Constant.DEVICE_SUCCESS_INSERT);
            int deviceSuccessUpdate = getTotalCount(Constant.DEVICE_SUCCESS_UPDATE);
            int deviceSuccessDelete = getTotalCount(Constant.DEVICE_SUCCESS_DELETE);
            int deviceFailInsert = getTotalCount(Constant.DEVICE_FAIL_INSERT);
            int deviceFailUpdate = getTotalCount(Constant.DEVICE_FAIL_UPDATE);
            int deviceFailDelete = getTotalCount(Constant.DEVICE_FAIL_DELETE);

            int customerTotal = customerSuccessInsert + customerSuccessUpdate
                    + customerSuccessDelete + customerFailInsert
                    + customerFailUpdate + customerFailDelete;
            int locationTotal = locationSuccessInsert + locationSuccessUpdate
                    + locationSuccessDelete + locationFailInsert
                    + locationFailUpdate + locationFailDelete;
            int locationRoleTotal = locationRoleSuccessInsert
                    + locationRoleSuccessUpdate + locationRoleSuccessDelete
                    + locationRoleFailInsert + locationRoleFailUpdate
                    + locationRoleFailDelete;
            int deviceTotal = deviceSuccessInsert + deviceSuccessUpdate
                    + deviceSuccessDelete + deviceFailInsert + deviceFailUpdate
                    + deviceFailDelete;

            int total = customerTotal + locationTotal + locationRoleTotal
                    + deviceTotal;

            LOGGER.info("This job processed :" + customerSuccessInsert
                    + " records");

            String jobId = EtlJobFactory.getCurrentJobId();
            String tdStyle = "style='border:1px solid silver;font-size:12px;color:#2F4F4F;text-align:center;width:150px;font-family:Tahoma,Arial,sans-serif;'";
            StringBuilder sb = new StringBuilder(
                    "<html><head><style>p{font-family:Tahoma,Arial,sans-serif;}</style></head><body><p>There're "
                            + total + " records processed.<p>");
            sb.append("<table style='border-collapse:collapse;'><tr><td "
                    + tdStyle + " >&nbsp;</td><td " + tdStyle
                    + " >Customer</td><td " + tdStyle + " >Location</td><td "
                    + tdStyle + " >LocationRole</td><td " + tdStyle
                    + " >Device</td></tr>");
            sb.append("<tr><td " + tdStyle + " >Successfully Insert</td><td "
                    + tdStyle + " >" + customerSuccessInsert + "</td><td "
                    + tdStyle + " >" + locationSuccessInsert + "</td><td "
                    + tdStyle + " >" + locationRoleSuccessInsert + "</td><td "
                    + tdStyle + " >" + deviceSuccessInsert + "</td></tr>");
            sb.append("<tr><td " + tdStyle + " >Successfully Update</td><td "
                    + tdStyle + " >" + customerSuccessUpdate + "</td><td "
                    + tdStyle + " >" + locationSuccessUpdate + "</td><td "
                    + tdStyle + " >" + locationRoleSuccessUpdate + "</td><td "
                    + tdStyle + " >" + deviceSuccessUpdate + "</td></tr>");
            sb.append("<tr><td " + tdStyle + " >Successfully Delete</td><td "
                    + tdStyle + " >" + customerSuccessDelete + "</td><td "
                    + tdStyle + " >" + locationSuccessDelete + "</td><td "
                    + tdStyle + " >" + locationRoleSuccessDelete + "</td><td "
                    + tdStyle + " >" + deviceSuccessDelete + "</td></tr>");
            sb.append("<tr><td " + tdStyle + " >Failed Insert</td><td "
                    + tdStyle + " >" + customerFailInsert + "</td><td "
                    + tdStyle + " >" + locationFailInsert + "</td><td "
                    + tdStyle + " >" + locationRoleFailInsert + "</td><td "
                    + tdStyle + " >" + deviceFailInsert + "</td></tr>");
            sb.append("<tr><td " + tdStyle + " >Failed Update</td><td "
                    + tdStyle + " >" + customerFailUpdate + "</td><td "
                    + tdStyle + " >" + locationFailUpdate + "</td><td "
                    + tdStyle + " >" + locationRoleFailUpdate + "</td><td "
                    + tdStyle + " >" + deviceFailUpdate + "</td></tr>");
            sb.append("<tr><td " + tdStyle + " >Failed Delete</td><td "
                    + tdStyle + " >" + customerFailDelete + "</td><td "
                    + tdStyle + " >" + locationFailDelete + "</td><td "
                    + tdStyle + " >" + locationRoleFailDelete + "</td><td "
                    + tdStyle + " >" + deviceFailDelete + "</td></tr>");
            sb.append("<tr><td "
                    + tdStyle
                    + " >Total</td><td "
                    + tdStyle
                    + " >"
                    + customerTotal
                    + "</td><td "
                    + tdStyle
                    + " >"
                    + locationTotal
                    + "</td><td "
                    + tdStyle
                    + " >"
                    + locationRoleTotal
                    + "</td><td style='border:1px solid silver;font-size:12px;color:#2F4F4F;text-align:center;width:150px;font-family:Tahoma,Arial,sans-serif;' >"
                    + deviceTotal
                    + "</td><td style='display:none'></td></tr><table>");

            String content = sb.toString();
            EtlLoggerFactory.getLogger().sendEmail(
                    "ETL Job:" + jobId + " is completed.", content);
            LOGGER.info("Email is sent out:" + content);
        }
    }
    /**
     * @Title: setNextWorker
     * @Description:
     * @param worker
     * worker
     */
    @SuppressWarnings("rawtypes")
    public final void setNextWorker(final Worker worker) {
        this.nextWorker = worker;
    }
    /**
     * @Title: getTotalCount
     * @Description:
     * @param type
     * type
     * @return int
     */
    private int getTotalCount(final String type) {
        Properties properties = PropertyReader.getInstance().read(
                "log.properties");
        String path = properties.getProperty("log.path");
        String jobId = EtlJobFactory.getCurrentJobId();
        String fileName = jobId + "_" + type + ".CSV";
        File file = new File(path + fileName);
        if (!file.exists()) {
            return 0;
        }
        FileReader reader = null;
        BufferedReader in = null;
        int lineNumber = -1;
        try {
            reader = new FileReader(path + fileName);

            in = new BufferedReader(reader);

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
                reader.close();
            } catch (IOException e) {
                LOGGER.error("Exception:", e);
            }
        }
        return lineNumber > 0 ? lineNumber : 0;
    }
}
