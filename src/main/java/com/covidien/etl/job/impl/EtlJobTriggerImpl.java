package com.covidien.etl.job.impl;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.covidien.etl.common.Constant;
import com.covidien.etl.common.EtlFileHelper;
import com.covidien.etl.common.PropertyReader;
import com.covidien.etl.csvreader.BatchReader;
import com.covidien.etl.csvreader.CustomerReader;
import com.covidien.etl.csvreader.DeviceReader;
import com.covidien.etl.csvreader.LocationReader;
import com.covidien.etl.csvreader.LocationRoleReader;
import com.covidien.etl.download.FileCheck;
import com.covidien.etl.download.FtpUtil;
import com.covidien.etl.job.EtlJob;
import com.covidien.etl.job.EtlJobFactory;
import com.covidien.etl.job.EtlJobTrigger;
import com.covidien.etl.model.Batch;
import com.jcraft.jsch.SftpException;

/**
 * @ClassName: EtlJobTriggerImpl
 * @Description:
 */
public class EtlJobTriggerImpl implements EtlJobTrigger {
    /**
     * Define a log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(EtlJobTriggerImpl.class);

    @Override
    public final void trigger() {
        LOGGER.info("Monitoring public schema changes...");
        if (shouldDownload() && shouldStart()) {
            LOGGER.info("Etl job is triggered!");
            EtlJob etlJob = EtlJobFactory.createEtlJob();
            LOGGER.info("Start etl job:" + EtlJobFactory.getCurrentJobId());
            etlJob.start();
            LOGGER.info("Etl job ends.");
        } else {
            LOGGER.error("No data changes. Etl job isn't triggered!");
        }
    }

    /**
     * @Title: shouldDownload
     * @Description:
     * @return boolean
     */
    private boolean shouldDownload() {
        FtpUtil ftpUtil = new FtpUtil();
        try {
            if (ftpUtil.isFileModified()) {
                LOGGER.info("Remote public schema's modified time has been changed!");
                ftpUtil.download();
                LOGGER.info("Download remote public schema successfully!");
                return true;
            }
        } catch (SftpException e) {
            LOGGER.error("Exception:", e);
        } finally {
            ftpUtil.disconnect();
        }
        LOGGER.error("No update from public schema!");
        return false;
    }

    /**
     * @Title: shouldStart
     * @Description:
     * @return boolean
     */
    private boolean shouldStart() {

        FileCheck fileCheck = new FileCheck();
        boolean isFileChanged;
        try {
            isFileChanged = fileCheck.isFileChanged();
        } catch (IOException e) {
            LOGGER.error("Exception:", e);
            return false;
        }
        LOGGER.info("Checking batch files's MD5 value");
        if (!isFileChanged) {
            LOGGER.error("No file changed");
            return false;
        }
        LOGGER.info("Batch files' MD5 value changed");

        BatchReader batchReader = new BatchReader();
        CustomerReader customerReader = new CustomerReader();
        LocationReader locationReader = new LocationReader();
        LocationRoleReader locationRoleReader = new LocationRoleReader();
        DeviceReader deviceReader = new DeviceReader();

        List<Batch> batchList = batchReader.readCSVFile(null);
        if (batchList.size() != 4) {
            LOGGER.error("Batch list is not 4!");
            return false;
        }
        Map<String, Long> customerInfoMap = customerReader.getCSVinfo();
        Map<String, Long> locationInfoMap = locationReader.getCSVinfo();
        Map<String, Long> locationRoleInfoMap = locationRoleReader.getCSVinfo();
        Map<String, Long> deviceInfoMap = deviceReader.getCSVinfo();

        Batch customerBatch = batchList.get(0);
        Batch locationBatch = batchList.get(1);
        Batch locationRoleBatch = batchList.get(2);
        Batch deviceBatch = batchList.get(3);

        if (batchList.size() != 4 || customerInfoMap.get(Constant.CSV_SIZE_KEY) == 0
                || locationInfoMap.get(Constant.CSV_SIZE_KEY) == 0
                || locationRoleInfoMap.get(Constant.CSV_SIZE_KEY) == 0 || deviceInfoMap.get(Constant.CSV_SIZE_KEY) == 0) {
            LOGGER.error("Batch info error or no data found!");
            return false;
        }

        if (customerInfoMap.get(Constant.CSV_SIZE_KEY) != customerBatch.getRowsSent()
                || locationInfoMap.get(Constant.CSV_SIZE_KEY) != locationBatch.getRowsSent()
                || locationRoleInfoMap.get(Constant.CSV_SIZE_KEY) != locationRoleBatch.getRowsSent()
                || deviceInfoMap.get(Constant.CSV_SIZE_KEY) != deviceBatch.getRowsSent()) {
            LOGGER.error("customer:" + customerInfoMap.get(Constant.CSV_SIZE_KEY) + ";" + customerBatch.getRowsSent()
                    + ";location:" + locationInfoMap.get(Constant.CSV_SIZE_KEY) + ";" + locationBatch.getRowsSent()
                    + ";locationRole:" + locationRoleInfoMap.get(Constant.CSV_SIZE_KEY) + ";"
                    + locationRoleBatch.getRowsSent() + ";device:" + deviceInfoMap.get(Constant.CSV_SIZE_KEY) + ";"
                    + deviceBatch.getRowsSent());
            LOGGER.error("Files are in transmit status!");
            return false;
        }

        if (customerInfoMap.get(Constant.CSV_BATCH_NUMBER_KEY) != customerBatch.getBatchNumber()
                || locationInfoMap.get(Constant.CSV_BATCH_NUMBER_KEY) != locationBatch.getBatchNumber()
                || locationRoleInfoMap.get(Constant.CSV_BATCH_NUMBER_KEY) != locationRoleBatch.getBatchNumber()
                || deviceInfoMap.get(Constant.CSV_BATCH_NUMBER_KEY) != deviceBatch.getBatchNumber()) {
            LOGGER.error("Batch number is not match!");
            return false;
        }
        //check batch run time and record the batch run time.
        HashMap<String, String> preBatchRunTimeMap = readPreBatchRunTime();
        if (preBatchRunTimeMap.size() == 4) {
            try {
                if (formateDate(customerBatch.getBatchRunTimestamp()) <= formateDate(preBatchRunTimeMap
                        .get(customerBatch.getFileName()))
                        || formateDate(locationBatch.getBatchRunTimestamp()) <= formateDate(preBatchRunTimeMap
                                .get(locationBatch.getFileName()))
                        || formateDate(locationRoleBatch.getBatchRunTimestamp()) <= formateDate(preBatchRunTimeMap
                                .get(locationRoleBatch.getFileName()))
                        || formateDate(deviceBatch.getBatchRunTimestamp()) <= formateDate(preBatchRunTimeMap
                                .get(deviceBatch.getFileName()))) {
                    LOGGER.error("Current batch run time must be greater than previous batch run time!");
                    return false;
                } else {
                    LOGGER.info("Current batch run time greater than previous batch run time, record current batch run time. ");
                    recordBatchRunTime(customerBatch);
                    recordBatchRunTime(locationBatch);
                    recordBatchRunTime(locationRoleBatch);
                    recordBatchRunTime(deviceBatch);
                }
            } catch (ParseException e) {
                LOGGER.error("Exception: ", e);
                return false;
            }
        } else if (preBatchRunTimeMap.size() == 0) {
            LOGGER.info("Previous batch run time is null.");
            recordBatchRunTime(customerBatch);
            recordBatchRunTime(locationBatch);
            recordBatchRunTime(locationRoleBatch);
            recordBatchRunTime(deviceBatch);
        } else {
            // Batch run time count not match the batch files count.
            LOGGER.error("Batch run time count not match the batch files count.");
            return false;
        }

        return true;
    }

    /**
     * @Title: formateDate
     * @Description:
     * @param strDate
     *        strDate
     * @return long
     * @throws ParseException
     *         ParseException
     */
    private long formateDate(final String strDate)
        throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = df.parse(strDate);
        return date.getTime();
    }

    /**
     * @Title: readPreBatchRunTime
     * @Description:
     * @return HashMap<String, String>
     */
    private HashMap<String, String> readPreBatchRunTime() {
        HashMap<String, String> preBatchRunTimeMap = new HashMap<String, String>();
        Properties properties = PropertyReader.getInstance().read(
                Constant.getConfigFilePath() + "downloadConf.properties");
        String tempDir = properties.getProperty("target.dir");
        String[] batchFiles = properties.getProperty("download.batchfile").split(",");

        for (String fileName : batchFiles) {
            String fullName = tempDir + fileName + FtpUtil.LOG_SUFFIX;
            File file = new File(fullName);
            if (!file.exists()) {
                LOGGER.info("file doesn't exist:" + fullName);
                break;
            }
            List<String> contents = EtlFileHelper.getInstance().read(fullName);
            if (contents.size() > 1) {
                LOGGER.info("The batch run time of file " + fileName + " is: " + contents.get(1));
                preBatchRunTimeMap.put(fileName, contents.get(1));
            }
        }

        return preBatchRunTimeMap;
    }

    /**
     * @Title: recordBatchRunTim
     * @Description:
     * @param batchFile
     *        batchFile
     */
    private void recordBatchRunTime(final Batch batchFile) {
        Properties properties = PropertyReader.getInstance().read(
                Constant.getConfigFilePath() + "downloadConf.properties");
        String tempDir = properties.getProperty("target.dir");
        String fileName = batchFile.getFileName();
        String fullName = tempDir + fileName + FtpUtil.LOG_SUFFIX;
        List<String> contents = EtlFileHelper.getInstance().read(fullName);
        if (contents.size() > 0) {
            EtlFileHelper.getInstance().write(fullName, contents.get(0), false);
        }
        EtlFileHelper.getInstance().write(fullName, "\n" + batchFile.getBatchRunTimestamp(), true);
    }
}
