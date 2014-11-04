package com.covidien.etl.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import com.covidien.etl.common.Constant;
import com.covidien.etl.common.PropertyReader;

/**
 * @ClassName: FileCheck
 * @Description:
 */
public class FileCheck {
    /**
     * Define a log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(FileCheck.class);
    /**
     * batchFile.
     */
    private String[] batchFile;
    /**
     * dataFile.
     */
    private String[] dataFile;
    /**
     * targetDir.
     */
    private String targetDir;
    /**
     * sourceDir.
     */
    private String sourceDir;
    /**
     * mapBatchMD5.
     */
    private Map<String, String> mapBatchMD5 = null;

    /**
     * @Title: FileCheck
     * @Description:
     */
    public FileCheck() {
        Properties properties = PropertyReader.getInstance().read(
                Constant.getConfigFilePath() + "downloadConf.properties");
        batchFile = properties.getProperty("download.batchfile").split(",");
        dataFile = properties.getProperty("download.datafile").split(",");
        targetDir = properties.getProperty("target.dir");
        sourceDir = properties.getProperty("source.dir");
    }

    /**
     * @Title: getFileMD5String
     * @Description:
     * @param file
     *        file
     * @return String
     * @throws IOException
     *         IOException
     */
    private String getFileMD5String(final File file)
        throws IOException {
        InputStream in = new FileInputStream(file);
        String value = DigestUtils.md5Hex(in);
        in.close();
        return value;
    }

    /**
     * @Title: getExistMD5
     * @Description:
     * @return Map<String, String>
     */
    private Map<String, String> getExistMD5() {
        Map<String, String> map = new HashMap<String, String>();
        String md5 = null;

        for (String fileName : batchFile) {
            String fullName = targetDir + "/" + fileName + ".md5";
            File file = new File(fullName);
            if (!file.exists()) {
                return map;
            }

            try {
                Scanner scanner = new Scanner(file);
                if (scanner.hasNext()) {
                    md5 = scanner.useDelimiter("\\Z").next();
                    map.put(fileName, md5);
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                LOGGER.error("Exception:", e);
            }
        }

        return map;

    }

    /**
     * @Title: writeFile
     * @Description: write md5 value of batch file into checksum file.
     * @param fileName
     *        fileName
     * @param content
     *        content
     */
    private void writeFile(final String fileName, final String content) {
        File file = new File(fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(content.getBytes());
        } catch (Exception e) {
            LOGGER.error("Exception:", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOGGER.error("Exception:", e);
                }
            }
        }
    }

    /**
     * @Title: saveMD5
     * @Description: Write md5 of batch file into checksum file.
     */
    public final void saveMD5() {
        for (String fileName : batchFile) {
            String fullName = sourceDir + "/" + fileName + ".md5";
            writeFile(fullName, mapBatchMD5.get(fileName));

        }
    }

    /**
     * @Title: checkDataFile
     * @Description:
     * @return boolean
     */
    public final boolean checkDataFile() {
        boolean canJobRun = false;
        int count = 0;
        for (int i = 0; i < batchFile.length; i++) {
            String fullNameBatch = sourceDir + "/" + batchFile[i];
            String fullNameData = sourceDir + "/" + dataFile[i];
            try {
                Map<String, String> mapBatch = this.readBatchFile(fullNameBatch);
                Map<String, String> mapData = this.readDataFile(fullNameData);
                if (mapBatch.get("BATCH_NUMBER").equals(mapData.get("BATCH_NUMBER"))
                        && mapBatch.get("ROWS_SENT").equals(mapData.get("ROWS_SENT"))) {
                    count++;
                }
            } catch (Exception e) {
                LOGGER.error("Exception:", e);
            }
        }
        if (count > 0 && count == batchFile.length) {
            canJobRun = true;
        }
        return canJobRun;
    }

    /**
     * @Title: checkBatchFile
     * @Description: verify whether all 4 batch files are changed, if all 4
     *               file's md5 value are different with the md5 value stored in
     *               checksum files, then return true.
     * @return boolean
     * @throws IOException
     *         IOException
     */
    public final boolean checkBatchFile()
        throws IOException {
        boolean canDownload = false;
        Map<String, String> mapExistMD5 = getExistMD5();
        mapBatchMD5 = getBatchFileMD5();
        if (mapExistMD5.size() < batchFile.length) {

            canDownload = true;
        } else {
            int count = 0;
            for (String fileName : batchFile) {
                String md5Exist = mapExistMD5.get(fileName);
                String md5Batch = mapBatchMD5.get(fileName);
                if (md5Exist != null && !md5Exist.equals(md5Batch)) {
                    count++;
                } else {
                    break;
                }

            }
            if (count > 0 && count == batchFile.length) {
                canDownload = true;
            }
        }
        return canDownload;
    }

    /**
     * @Title: getBatchFileMD5
     * @Description:
     * @return Map<String, String>
     * @throws IOException
     *         IOException
     */
    public final Map<String, String> getBatchFileMD5()
        throws IOException {
        Map<String, String> map = new HashMap<String, String>();

        for (String fileName : batchFile) {
            String fullName = sourceDir + "/" + fileName;
            File file = new File(fullName);
            if (file.exists()) {
                String md5 = getFileMD5String(file);
                map.put(fileName, md5);
            }
        }
        return map;
    }

    /**
     * @Title: readBatchFile
     * @Description:
     * @param filename
     *        filename
     * @return Map<String, String>
     * @throws IOException
     *         IOException
     */
    public final Map<String, String> readBatchFile(final String filename)
        throws IOException {
        Map<String, String> map = new HashMap<String, String>();
        LineNumberReader reader = new LineNumberReader(new FileReader(filename));
        reader.setLineNumber(2);
        String line = reader.readLine();
        line = reader.readLine();

        String[] items = line.split("\\|");
        map.put("BATCH_NUMBER", items[1]);
        map.put("ROWS_SENT", items[3]);

        reader.close();
        return map;
    }

    /**
     * @Title: readDataFile
     * @Description: Count lines for data file.
     * @param filename
     *        filename
     * @return Map<String, String>
     * @throws IOException
     *         IOException
     */
    private Map<String, String> readDataFile(final String filename)
        throws IOException {
        Map<String, String> map = new HashMap<String, String>();
        LineNumberReader reader = new LineNumberReader(new FileReader(filename));
        int cnt = 0;
        String line = "";
        while ((line = reader.readLine()) != null) {
            if (reader.getLineNumber() == 2) {
                String[] items = line.split("\\|");
                map.put("BATCH_NUMBER", items[items.length - 1]);

            }
        }
        cnt = reader.getLineNumber();
        reader.close();
        map.put("ROWS_SENT", String.valueOf(cnt - 1));
        return map;
    }

    /**
     * @Title: isFileChanged
     * @Description:
     * @return boolean
     * @throws IOException
     *         IOException
     */
    public final boolean isFileChanged()
        throws IOException {
        Map<String, String> mapExistMD5 = getExistMD5();
        Map<String, String> mapCurrentMD5 = this.getBatchFileMD5();
        if (mapExistMD5.size() == 0) {
            for (String batch : batchFile) {
                this.writeFile(this.targetDir + "/" + batch + ".md5", mapCurrentMD5.get(batch));
            }
            return true;
        }

        boolean result = this.checkBatchFile();
        for (String batch : batchFile) {
            this.writeFile(this.targetDir + "/" + batch + ".md5", mapCurrentMD5.get(batch));
        }

        return result;
    }

}
