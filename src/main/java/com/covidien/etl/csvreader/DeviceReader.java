package com.covidien.etl.csvreader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.prefs.CsvPreference.Builder;

import com.covidien.etl.common.PropertyReader;
import com.covidien.etl.model.Device;

/**
 * @ClassName: DeviceReader
 * @Description:
 */
public class DeviceReader implements Reader<Device> {
    /**
     * Define static log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(DeviceReader.class);
    /**
     * @Title: getDevice
     * @Description:
     * @return CellProcessor[]
     * @throws
     */
    private static CellProcessor[] getDevice() {

        final CellProcessor[] processors = new CellProcessor[] {
                new Optional(), // LOCATION_ID
                new Optional(), // CUSTOMER_ID
                new Optional(), // MAINTENANCE_EXPIRATION_DATE
                new NotNull(), // SERIAL_NUMBER
                new NotNull(), // SKU
                new NotNull(), // SOURCE_SYSTEM
                new Optional(), // INSTALL_COUNTRY_CODE
                new Optional(), // LAST_CHANGE_DATE
                new Optional(), // INSTALLATION_DATE
                new Optional(), // ACTUAL_SHIP_DATE
                new Optional(new ParseInt()), // is_deleted
                new NotNull(new ParseInt()) // batch_number

        };

        return processors;
    }
    /**
     * path.
     */
    private String path;
    @Override
    public final List<Device> readCSVFile() {
        if (path == null) {
            Properties properties = PropertyReader.getInstance().read(
                    "csvPathConfig.properties");
            path = properties.getProperty("DevicePath");

        }

        List<Device> list = new ArrayList<Device>();

        ICsvBeanReader beanReader = null;
        try {
            if (!new File(path).exists()) {
                return list;
            }
            CsvPreference preference = new Builder('"', 124, "\n").build();
            beanReader = new CsvBeanReader(new FileReader(path), preference);

            // the header elements are used to map the values to the bean (names
            // must match)
            final String[] header = beanReader.getHeader(true);
            final CellProcessor[] processors = getDevice();

            Device device;
            while ((device = beanReader.read(Device.class, header, processors)) != null) {
                list.add(device);
            }

        } catch (Exception e) {
            LOGGER.error("Exception:", e);
        } finally {
            if (beanReader != null) {
                try {
                    beanReader.close();
                } catch (IOException e) {
                    LOGGER.error("Exception:", e);
                }
            }
        }
        return list;
    }
}
