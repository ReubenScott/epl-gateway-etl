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
import com.covidien.etl.model.LocationRole;
import com.covidien.etl.util.EtlUtil;
import com.covidien.etl.worker.Worker;

/**
 * @ClassName: LocationRoleReader
 * @Description:
 */
public class LocationRoleReader implements Reader<LocationRole> {
    /**
     * Define static log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(LocationRoleReader.class);

    /**
     * @Title: getLocationRole
     * @Description:
     * @return CellProcessor[]
     * @throws
     */
    private static CellProcessor[] getLocationRole() {

        final CellProcessor[] processors = new CellProcessor[] {
                new NotNull(new Trim()), // CUSTOMER_ID
                new NotNull(new Trim()), // LOCATION_ID
                new Optional(new Trim()), // LOCATION_ROLE
                new Optional(new Trim()), // last_change_date
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
    public final List<LocationRole> readCSVFile(Worker<LocationRole> worker) {
        int initialCapacity = 1000 * 10 * 5;
        List<LocationRole> list = new ArrayList<LocationRole>(initialCapacity);
        ICsvBeanReader beanReader = null;
        if (path == null) {
            path = properties.getProperty("LocationRolePath");
        }
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
            final CellProcessor[] processors = getLocationRole();

            LocationRole locationRole;
            long index = 0;
            while ((locationRole = beanReader.read(LocationRole.class, header, processors)) != null) {
                if (index > 0 && index % (initialCapacity) == 0) {
                    worker.importData(list);
                    list = new ArrayList<LocationRole>(initialCapacity);
                }
                list.add(locationRole);
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
        Map<String, Long> csvInfo = new HashMap<String, Long>();
        ICsvBeanReader beanReader = null;
        if (path == null) {
            path = properties.getProperty("LocationRolePath");
        }
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
            final CellProcessor[] processors = getLocationRole();

            LocationRole locationRole;
            long batchNumber = 0;
            locationRole = beanReader.read(LocationRole.class, header, processors);
            if (locationRole != null) {
                batchNumber = locationRole.getBatchNumber();
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
