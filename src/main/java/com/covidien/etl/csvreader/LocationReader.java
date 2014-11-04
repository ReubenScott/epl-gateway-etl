package com.covidien.etl.csvreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.prefs.CsvPreference.Builder;

import com.covidien.etl.common.Constant;
import com.covidien.etl.common.PropertyReader;
import com.covidien.etl.model.Location;
import com.covidien.etl.util.EtlUtil;
import com.covidien.etl.worker.Worker;

/**
 * @ClassName: LocationReader
 * @Description:
 */
public class LocationReader implements Reader<Location> {
    /**
     * Define static log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(LocationReader.class);

    /**
     * @Title: getLocation
     * @Description:
     * @return CellProcessor[]
     * @throws
     */
    private static CellProcessor[] getLocation() {

        final CellProcessor[] processors = new CellProcessor[] {
                new NotNull(new Trim()), // LOCATION_ID
                new Optional(new Trim()), // ADDRESS_LINE1
                new Optional(new Trim()), // ADDRESS_MODIFIER_1
                new Optional(new Trim()), // ADDRESS_MODIFIER_2
                new Optional(new Trim()), // ADDRESS_MODIFIER_3
                new Optional(new Trim()), // ADDRESS_MODIFIER_4
                new Optional(new Trim()), // CITY
                new Optional(new Trim()), // STATE_PROVINCE
                new ConvertNullTo(""), // POSTAL_CODE
                new Optional(new Trim()), // COUNTRY_CODE
                new Optional(new Trim()), //LAST_CHANGE_DATE
                new Optional(new ParseInt()), // is_deleted
                new NotNull(new ParseInt()) // batch_number
        };

        return processors;
    }

    /**
     * path.
     */
    private String path = null;
    /**
     * Define a properties object.
     */
    private Properties properties = PropertyReader.getInstance().read(
            Constant.getConfigFilePath() + "csvPathConfig.properties");

    @Override
    public final List<Location> readCSVFile(Worker<Location> worker) {
        if (path == null) {
            path = properties.getProperty("LocationPath");
        }
        int initialCapacity = 1000 * 10 * 5;
        List<Location> list = new ArrayList<Location>(initialCapacity);

        ICsvBeanReader beanReader = null;
        try {
            if (!new File(path).exists()) {
                return null;
            }
            CsvPreference preference = new Builder('"', Constant.CSV_DELIMTIER_PIPE, "\n").build();
            beanReader = new CsvBeanReader(new FileReader(path), preference);

            // the header elements are used to map the values to the bean (names
            // must match)
            final String[] header = beanReader.getHeader(true);
            EtlUtil.convertHeader(header);
            final CellProcessor[] processors = getLocation();

            Location location;
            long index = 0;
            while ((location = beanReader.read(Location.class, header, processors)) != null) {
                if (index > 0 && index % (initialCapacity) == 0) {
                    worker.importData(list);
                    list = new ArrayList<Location>(initialCapacity);
                }
                list.add(location);
                index++;
            }
            worker.importData(list);
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

        return null;
    }

    @Override
    public final Map<String, Long> getCSVinfo() {
        if (path == null) {
            path = properties.getProperty("LocationPath");
        }

        Map<String, Long> csvInfo = new HashMap<String, Long>();
        ICsvBeanReader beanReader = null;
        try {
            if (!new File(path).exists()) {
                return null;
            }
            CsvPreference preference = new Builder('"', Constant.CSV_DELIMTIER_PIPE, "\n").build();
            beanReader = new CsvBeanReader(new FileReader(path), preference);

            // the header elements are used to map the values to the bean (names
            // must match)
            final String[] header = beanReader.getHeader(true);
            EtlUtil.convertHeader(header);
            final CellProcessor[] processors = getLocation();

            Location location;
            long batchNumber = 0;
            location = beanReader.read(Location.class, header, processors);
            if (location != null) {
                batchNumber = location.getBatchNumber();
            }
            FileReader fileReader = null;
            BufferedReader in = null;
            long lineNumber = -1;
            try {
                fileReader = new FileReader(path);

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
            csvInfo.put(Constant.CSV_BATCH_NUMBER_KEY, batchNumber);
            csvInfo.put(Constant.CSV_SIZE_KEY, lineNumber);
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
        return csvInfo;
    }

}
