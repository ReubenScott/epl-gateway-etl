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
import com.covidien.etl.model.LocationRole;

/**
 * @ClassName: LocationRoleReader
 * @Description:
 */
public class LocationRoleReader implements Reader<LocationRole> {
    /**
     * Define static log instance.
     */
    private static final Logger LOGGER = Logger
            .getLogger(LocationRoleReader.class);
    /**
     * @Title: getLocationRole
     * @Description:
     * @return CellProcessor[]
     * @throws
     */
    private static CellProcessor[] getLocationRole() {

        final CellProcessor[] processors = new CellProcessor[] {
                new NotNull(), // CUSTOMER_ID
                new NotNull(), // LOCATION_ID
                new Optional(), // LOCATION_ROLE
                new Optional(), // last_change_date
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
    public final List<LocationRole> readCSVFile() {
        List<LocationRole> result = new ArrayList<LocationRole>();
        ICsvBeanReader beanReader = null;
        if (path == null) {
            Properties properties = PropertyReader.getInstance().read(
                    "csvPathConfig.properties");
            path = properties.getProperty("LocationRolePath");

        }
        try {
            if (!new File(path).exists()) {
                return null;
            }
            CsvPreference preference = new Builder('"', 124, "\n").build();
            beanReader = new CsvBeanReader(new FileReader(path), preference);

            // the header elements are used to map the values to the bean (names
            // must match)
            final String[] header = beanReader.getHeader(true);
            final CellProcessor[] processors = getLocationRole();

            LocationRole locationRole;
            while ((locationRole = beanReader.read(LocationRole.class, header,
                    processors)) != null) {
                result.add(locationRole);
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
        return result;
    }
}
