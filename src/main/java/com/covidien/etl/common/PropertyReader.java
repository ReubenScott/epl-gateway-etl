package com.covidien.etl.common;

import java.io.FileInputStream;
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
     * @Title: readResource
     * @Description:
     * @param resourceName
     *        The resource property file which be included in jar package.
     * @return Properties
     */
    public final Properties readResource(final String resourceName) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(resourceName);
        Properties p = new Properties();
        try {
            p.load(inputStream);
            inputStream.close();
        } catch (Exception e) {
            LOGGER.error("exception:", e);
        }
        return p;

    }

    /**
     * @Title: read
     * @Description:
     * @param propertyPath
     *        propertyPath
     * @return Properties
     */
    public final Properties read(final String propertyPath) {
        Properties p = null;
        try {
            InputStream inputStream = new FileInputStream(propertyPath);
            p = new Properties();

            p.load(inputStream);
            inputStream.close();
        } catch (Exception e) {
            LOGGER.error("exception:", e);
        }
        return p;
    }
}
