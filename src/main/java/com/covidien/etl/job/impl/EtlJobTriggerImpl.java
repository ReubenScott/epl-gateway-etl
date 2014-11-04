package com.covidien.etl.job.impl;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

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
import com.covidien.etl.model.Customer;
import com.covidien.etl.model.Device;
import com.covidien.etl.model.Location;
import com.covidien.etl.model.LocationRole;
import com.jcraft.jsch.SftpException;

/**
 * @ClassName: EtlJobTriggerImpl
 * @Description:
 */
public class EtlJobTriggerImpl implements EtlJobTrigger {
    /**
     * Define a log instance.
     */
    private static final Logger LOGGER = Logger
            .getLogger(EtlJobTriggerImpl.class);
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
            LOGGER.info("No data changes. Etl job isn't triggered!");
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
        LOGGER.info("No update from public schema!");
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
            LOGGER.info("No file changed");
            return false;
        }
        LOGGER.info("Batch files' MD5 value changed");
        BatchReader batchReader = new BatchReader();
        CustomerReader customerReader = new CustomerReader();
        LocationReader locationReader = new LocationReader();
        LocationRoleReader locationRoleReader = new LocationRoleReader();
        DeviceReader deviceReader = new DeviceReader();

        List<Batch> batchList = batchReader.readCSVFile();
        if (batchList.size() != 4) {
            LOGGER.info("Batch list is not 4!");
            return false;
        }
        List<Customer> customerList = customerReader.readCSVFile();
        List<Location> locationList = locationReader.readCSVFile();
        List<LocationRole> locationRoleList = locationRoleReader.readCSVFile();
        List<Device> deviceList = deviceReader.readCSVFile();

        Batch customerBatch = batchList.get(0);
        Batch locationBatch = batchList.get(1);
        Batch locationRoleBatch = batchList.get(2);
        Batch deviceBatch = batchList.get(3);

        if (batchList.size() != 4 || customerList.size() == 0
                || locationList.size() == 0 || locationRoleList.size() == 0
                || deviceList.size() == 0) {
            LOGGER.info("Batch info error or no data found!");
            return false;
        }

        if (customerList.size() != customerBatch.getROWS_SENT()
                || locationList.size() != locationBatch.getROWS_SENT()
                || locationRoleList.size() != locationRoleBatch.getROWS_SENT()
                || deviceList.size() != deviceBatch.getROWS_SENT()) {
            LOGGER.info("customer:" + customerList.size() + ";"
                    + customerBatch.getROWS_SENT() + ";location:"
                    + locationList.size() + ";" + locationBatch.getROWS_SENT()
                    + ";locationRole:" + locationRoleList.size() + ";"
                    + locationRoleBatch.getROWS_SENT() + ";device:"
                    + deviceList.size() + ";" + deviceBatch.getROWS_SENT());
            LOGGER.info("Files are in transmit status!");
            return false;
        }

        if (customerList.get(0).getBATCH_NUMBER() != customerBatch
                .getBATCH_NUMBER()
                || locationList.get(0).getBATCH_NUMBER() != locationBatch
                        .getBATCH_NUMBER()
                || locationRoleList.get(0).getBATCH_NUMBER() != locationRoleBatch
                        .getBATCH_NUMBER()
                || deviceList.get(0).getBATCH_NUMBER() != deviceBatch
                        .getBATCH_NUMBER()) {
            LOGGER.info("Batch number is not match!");
            return false;
        }

        HashMap<String, String> preBatchRunTimeMap = readPreBatchRunTime();
        if (preBatchRunTimeMap.size() == 4) {
            try {
                if (formateDate(customerBatch.getBATCH_RUN_TIMESTAMP()) <= formateDate(preBatchRunTimeMap
                        .get(customerBatch.getFileName()))
                        || formateDate(locationBatch.getBATCH_RUN_TIMESTAMP()) <= formateDate(preBatchRunTimeMap
                                .get(locationBatch.getFileName()))
                        || formateDate(locationRoleBatch
                                .getBATCH_RUN_TIMESTAMP()) <= formateDate(preBatchRunTimeMap
                                .get(locationRoleBatch.getFileName()))
                        || formateDate(deviceBatch.getBATCH_RUN_TIMESTAMP()) <= formateDate(preBatchRunTimeMap
                                .get(deviceBatch.getFileName()))) {
                    LOGGER.info("Current batch run tiem must be greater then previoue batch run time!");
                    return false;
                } else {
                    recordBatchRunTim(customerBatch);
                    recordBatchRunTim(locationBatch);
                    recordBatchRunTim(locationRoleBatch);
                    recordBatchRunTim(deviceBatch);
                }
            } catch (ParseException e) {
                LOGGER.info("Exception: " + e);
            }
        } else if (preBatchRunTimeMap.size() == 0) {
            recordBatchRunTim(customerBatch);
            recordBatchRunTim(locationBatch);
            recordBatchRunTim(locationRoleBatch);
            recordBatchRunTim(deviceBatch);
        } else {
            // Batch run time count not match the batch files count.
            return false;
        }

        return true;
    }
    /**
     * @Title: formateDate
     * @Description:
     * @param strDate
     * strDate
     * @return long
     * @throws ParseException
     */
    private long formateDate(final String strDate) throws ParseException {
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
                "downloadConf.properties");
        String tempDir = properties.getProperty("target.dir");
        String[] batchFiles = properties.getProperty("download.batchfile")
                .split(",");

        for (String fileName : batchFiles) {
            String fullName = tempDir + fileName + FtpUtil.LOG_SUFFIX;
            File file = new File(fullName);
            if (!file.exists()) {
                LOGGER.info("file doesn't exist:" + fullName);
                break;
            }
            List<String> contents = EtlFileHelper.getInstance().read(fullName);
            if (contents.size() > 1) {
                LOGGER.info("The batch run time of file " + fileName + " is: "
                        + contents.get(1));
                preBatchRunTimeMap.put(fileName, contents.get(1));
            }
        }

        return preBatchRunTimeMap;
    }
    /**
     * @Title: recordBatchRunTim
     * @Description:
     * @param batchFile
     * batchFile
     */
    private void recordBatchRunTim(final Batch batchFile) {
        Properties properties = PropertyReader.getInstance().read(
                "downloadConf.properties");
        String tempDir = properties.getProperty("target.dir");
        String fullName = tempDir + batchFile.getFileName()
                + FtpUtil.LOG_SUFFIX;
        EtlFileHelper.getInstance().write(fullName,
                "\n" + batchFile.getBATCH_RUN_TIMESTAMP(), true);
    }
}
