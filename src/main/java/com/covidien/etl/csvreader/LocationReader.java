package com.covidien.etl.csvreader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.prefs.CsvPreference.Builder;

import com.covidien.etl.common.PropertyReader;
import com.covidien.etl.model.Location;

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
                new NotNull(), // LOCATION_ID
                new Optional(), // ADDRESS_LINE1
                new Optional(), // ADDRESS_MODIFIER_1
                new Optional(), // ADDRESS_MODIFIER_2
                new Optional(), // ADDRESS_MODIFIER_3
                new Optional(), // ADDRESS_MODIFIER_4
                new Optional(), // CITY
                new Optional(), // STATE_PROVINCE
                new ConvertNullTo(""), // POSTAL_CODE
                new Optional(), // COUNTRY_CODE
                new Optional(), new Optional(new ParseInt()), // is_deleted
                new NotNull(new ParseInt()) // batch_number
        };

        return processors;
    }
    /**
     * path.
     */
    private String path = null;
    @Override
    public final List<Location> readCSVFile() {
        if (path == null) {
            Properties properties = PropertyReader.getInstance().read(
                    "csvPathConfig.properties");
            path = properties.getProperty("LocationPath");

        }

        List<Location> list = new ArrayList<Location>();

        ICsvBeanReader beanReader = null;
        try {
            if (!new File(path).exists()) {
                return null;
            }
            CsvPreference preference = new Builder('"', 124, "\n").build();
            beanReader = new CsvBeanReader(new FileReader(path), preference);

            // the header elements are used to map the values to the bean (names
            // must match)
            final String[] header = beanReader.getHeader(true);
            final CellProcessor[] processors = getLocation();

            Location location;

            while ((location = beanReader.read(Location.class, header,
                    processors)) != null) {
                list.add(location);
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
