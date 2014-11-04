package com.covidien.etl.common;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @ClassName: PropertyReader
 * @Description:
 */
public class PropertyReader {
    /**
     * Define the static property reader.
     */
    private static PropertyReader reader = new PropertyReader();
    /**
     * Define the static log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(PropertyReader.class);
    /**
     * @Title: getInstance
     * @Description:
     * @return PropertyReader
     * @throws
     */
    public static PropertyReader getInstance() {
        return reader;
    }
    /**
     * @Title: read
     * @Description:
     * @param propertyPath
     * propertyPath
     * @return Properties
     */
    public final Properties read(final String propertyPath) {
        InputStream inputStream = this.getClass().getClassLoader()
                .getResourceAsStream(propertyPath);
        Properties p = new Properties();
        try {
            p.load(inputStream);
            inputStream.close();
        } catch (Exception e) {
            LOGGER.error("exception:", e);
        }
        return p;
    }
}
