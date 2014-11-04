/**
 * @Title: Test.java
 * @Package com.covidien.etl.common
 * @Description:
 * @author tony.zhang2
 * @date 2013-12-14
 * @version V2.0
 */
package com.covidien.etl.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @ClassName: EtlFileHelper
 * @Description:
 */
public class EtlFileHelper {
    /**
     * Static instance for class.
     */
    private static EtlFileHelper helper = new EtlFileHelper();
    /**
     * Static log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(EtlFileHelper.class);

    /**
     * @Title: getInstance
     * @Description:
     * @return EtlFileHelper
     * @throws
     */
    public static EtlFileHelper getInstance() {
        return helper;
    }

    /**
     * @Title: write
     * @Description:
     * @param path
     * path
     * @param content
     * content
     * @param appendFlg
     * appendFlg
     */
    public final void write(final String path, final String content,
            final boolean appendFlg) {
        FileWriter writer = null;
        try {
            if (appendFlg) {
                writer = new FileWriter(path, true);
            } else {
                writer = new FileWriter(path);
            }
            writer.write(content);
        } catch (IOException e) {
            LOGGER.error("Exception:", e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                LOGGER.error("Exception:", e);
            }
        }
    }
    /**
     * @Title: read
     * @Description:
     * @param path
     * path
     * @return List<String>
     * @throws
     */
    public final List<String> read(final String path) {
        List<String> result = new ArrayList<String>();
        File file = new File(path);
        if (!file.exists()) {
            return result;
        }

        Reader reader = null;
        BufferedReader in = null;
        try {
            reader = new FileReader(path);
            in = new BufferedReader(reader);

            String lineValue;
            while ((lineValue = in.readLine()) != null) {
                result.add(lineValue);
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

        return result;
    }
}
