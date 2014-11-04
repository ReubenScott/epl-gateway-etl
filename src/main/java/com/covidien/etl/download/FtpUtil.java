package com.covidien.etl.download;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.covidien.etl.common.Constant;
import com.covidien.etl.common.EtlFileHelper;
import com.covidien.etl.common.PropertyReader;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * @ClassName: FtpUtil
 * @Description:
 */
public class FtpUtil {
    /**
     * logSuffix.
     */
    public static final String LOG_SUFFIX = ".txt";
    /**
     * Define a log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(FtpUtil.class);
    /**
     * username.
     */
    private String username;
    /**
     * password.
     */
    private String password;
    /**
     * host.
     */
    private String host;
    /**
     * port.
     */
    private int port;
    /**
     * ftpDir.
     */
    private String ftpDir;
    /**
     * tempDir.
     */
    private String tempDir;
    /**
     * sourceDir.
     */
    private String sourceDir;
    /**
     * batchFiles.
     */
    private String[] batchFiles;
    /**
     * dataFiles.
     */
    private String[] dataFiles;
    /**
     * sftp.
     */
    private ChannelSftp sftp;
    /**
     * sshSession.
     */
    private Session sshSession = null;

    /**
     * @Title: FtpUtil
     * @Description:
     */
    public FtpUtil() {
        Properties properties = PropertyReader.getInstance().read(
                Constant.getConfigFilePath() + "downloadConf.properties");
        this.username = properties.getProperty("ftp.username");
        this.password = properties.getProperty("ftp.password");
        this.host = properties.getProperty("ftp.host");
        this.ftpDir = properties.getProperty("ftp.dir");
        this.tempDir = properties.getProperty("target.dir");
        this.port = Integer.parseInt(properties.getProperty("ftp.port"));
        this.batchFiles = properties.getProperty("download.batchfile").split(",");
        this.dataFiles = properties.getProperty("download.datafile").split(",");
        this.sourceDir = properties.getProperty("source.dir");
        initFtpConnection();

    }

    /**
     * @Title: initFtpConnection
     * @Description:
     * @return boolean
     */
    private boolean initFtpConnection() {
        sftp = getFtpConnection(host, port, username, password);
        return true;
    }

    /**
     * @Title: getFtpConnection
     * @Description:
     * @param ftpHost
     *        ftpHost
     * @param ftpPort
     *        ftpPort
     * @param ftpUsername
     *        ftpUsername
     * @param ftpPassword
     *        ftpPassword
     * @return ChannelSftp
     */
    public final ChannelSftp getFtpConnection(final String ftpHost, final int ftpPort, final String ftpUsername,
            final String ftpPassword) {
        ChannelSftp channelSftp = null;
        try {
            JSch jsch = new JSch();
            Session ftpSession = jsch.getSession(ftpUsername, ftpHost, ftpPort);

            LOGGER.info("ssh Session created! host:" + ftpHost + ";port:" + ftpPort);
            ftpSession.setPassword(ftpPassword);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            ftpSession.setConfig(sshConfig);
            ftpSession.connect();

            LOGGER.info("Session connected.");
            LOGGER.info("Opening Channel.");
            Channel channel = ftpSession.openChannel("sftp");
            this.sshSession = ftpSession;
            channel.connect();
            channelSftp = (ChannelSftp) channel;
            System.out.println("Connected to " + ftpHost + ".");
        } catch (Exception e) {
            LOGGER.error("Exception:", e);
        }
        return channelSftp;
    }

    /**
     * @Title: isFileModified
     * @Description:
     * @return boolean
     * @throws SftpException
     *         SftpException
     */
    @SuppressWarnings("unchecked")
    public final boolean isFileModified()
        throws SftpException {

        if (!isDataFileMatchWithBatchFile(dataFiles, batchFiles)) {
            return false;
        }

        LOGGER.info("tempDir is :" + tempDir);
        Map<String, String> currentMap = new HashMap<String, String>();
        Map<String, String> previousBatchRunTimeMap = new HashMap<String, String>();
        String[] currentFiles = new String[batchFiles.length * 2];
        for (int i = 0; i < batchFiles.length; i++) {
            currentFiles[i] = batchFiles[i];
        }

        for (int i = 0; i < dataFiles.length; i++) {
            currentFiles[i + batchFiles.length] = dataFiles[i];
        }

        for (String fileName : currentFiles) {
            String fullName = tempDir + fileName + LOG_SUFFIX;
            File file = new File(fullName);
            if (!file.exists()) {
                LOGGER.info("file doesn't exist:" + fullName);
                break;
            }
            List<String> contents = EtlFileHelper.getInstance().read(fullName);
            if (contents.size() > 0) {
                currentMap.put(fileName, contents.get(0));
            }
            if (contents.size() > 1) {
                previousBatchRunTimeMap.put(fileName, contents.get(1));
            }
        }

        Map<String, String> modifiedMap = new HashMap<String, String>();
        Vector<LsEntry> files = sftp.ls(ftpDir);
        StringBuffer sftpDataFiels = new StringBuffer();
        StringBuffer sftpBatchFiels = new StringBuffer();
        for (LsEntry file : files) {
            String fileName = file.getFilename();
            if (".".equals(fileName) || "..".equals(fileName)
                    || (!contains(batchFiles, fileName) && !contains(dataFiles, fileName))) {
                continue;
            }
            String fullName = tempDir + fileName + LOG_SUFFIX;
            String mTime = file.getAttrs().getMtimeString();
            modifiedMap.put(fileName, mTime);
            EtlFileHelper.getInstance().write(fullName, mTime, false);
            if (contains(batchFiles, fileName)) {
                if (sftpBatchFiels.length() > 0) {
                    sftpBatchFiels.append("," + fileName);
                } else {
                    sftpBatchFiels.append(fileName);
                }
                String batchRuntime = previousBatchRunTimeMap.get(fileName);
                if (null != batchRuntime && batchRuntime != "") {
                    EtlFileHelper.getInstance().write(fullName, "\n" + batchRuntime, true);
                }
            } else if (contains(dataFiles, fileName)) {
                if (sftpDataFiels.length() > 0) {
                    sftpDataFiels.append("," + fileName);
                } else {
                    sftpDataFiels.append(fileName);
                }
            }
        }

        String[] sftpDataFielsArr = sftpDataFiels.toString().split(",");
        String[] sftpBatchFielsArr = sftpBatchFiels.toString().split(",");
        if (sftpDataFielsArr.length != dataFiles.length || sftpBatchFielsArr.length != batchFiles.length) {
            LOGGER.error("The SFTP total file count not match the file count in the downloadConf.properties");
            return false;
        }

        if (currentMap.size() != currentFiles.length) {
            LOGGER.info("Hasn't been stored previous mtime!");
            return true;
        }
        boolean sameTimestamp = true;
        for (String fileName : currentFiles) {
            try {
                if (formateDate(currentMap.get(fileName)) < formateDate(modifiedMap.get(fileName))) {
                    sameTimestamp = false;
                }
            } catch (Exception e) {
                sameTimestamp = false;
                LOGGER.error("Exception happend when formating unix file timestamp!", e);
            }
        }
        if (sameTimestamp) {
            LOGGER.error("The SFTP file is same timestamp with previous file!");
            return false;
        }

        LOGGER.info("public schema has been changed!");
        return true;
    }

    /**
     * @Title: contains
     * @Description:
     * @param strs
     *        strs
     * @param ele
     *        ele
     * @return boolean
     */
    private boolean contains(final String[] strs, final String ele) {
        for (String str : strs) {
            if (str.equals(ele)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @Title: download
     * @Description:
     */
    public final void download() {
        for (String fileName : batchFiles) {
            download(ftpDir + fileName, sourceDir + fileName);
        }

        for (String fileName : dataFiles) {
            download(ftpDir + fileName, sourceDir + fileName);
        }
    }

    /**
     * @Title: download
     * @Description:
     * @param downloadFile
     *        downloadFile
     * @param saveFile
     *        saveFile
     */
    private void download(final String downloadFile, final String saveFile) {
        try {
            LOGGER.info("download File:" + downloadFile);
            LOGGER.info("target file:" + saveFile);
            sftp.get(downloadFile, saveFile);
        } catch (Exception e) {
            LOGGER.error("Exception:", e);
        }
    }

    /**
     * @Title: disconnect
     * @Description:
     */
    public final void disconnect() {
        if (this.sftp != null) {
            this.sftp.disconnect();
        }
        if (this.sshSession != null && this.sshSession.isConnected()) {
            this.sshSession.disconnect();
        }
    }

    /**
     * @Title: getSession
     * @Description:
     * @return Session
     */
    public final Session getSession() {
        return this.sshSession;
    }

    /**
     * @Title: isDataFileMatchWithBatchFile
     * @Description:
     * @param dataFile
     *        dataFile
     * @param batchFile
     *        batchFile
     * @return boolean
     */
    private boolean isDataFileMatchWithBatchFile(final String[] dataFile, final String[] batchFile) {
        String[] dataF = new String[dataFile.length];
        String[] batchF = new String[batchFile.length];
        if (dataFile.length != batchFile.length) {
            LOGGER.error("batchFiles and dataFiles number doesn't match! dataFiles' total number:" + dataFile.length
                    + "; batchFiles' total number:" + batchFile.length);
            return false;
        } else {
            for (int i = 0; i < dataFile.length; i++) {
                dataF[i] = dataFile[i].replace("DATA", "");
            }
            for (int i = 0; i < batchFile.length; i++) {
                batchF[i] = batchFile[i].replace("BATCH", "");
            }
            for (String str : dataF) {
                Arrays.sort(batchF);
                if (Arrays.binarySearch(batchF, str) < 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * To convert unix file timestamp to numeric.
     * 
     * @Title: formateDate
     * @param strDate
     *        date string
     * @return long
     * @throws Exception
     *         Exception
     */
    private long formateDate(String strDate)
        throws Exception {
        if (strDate == null || strDate == "") {
            return 0;
        }
        DateFormat df = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
        Date date = df.parse(strDate);
        return date.getTime();
    }

}
