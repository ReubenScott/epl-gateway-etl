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
import com.covidien.etl.model.Customer;

/**
 * @ClassName: CustomerReader
 * @Description:
 */
public class CustomerReader implements Reader<Customer> {
    /**
     * Define static log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(CustomerReader.class);
    /**
     * @Title: getCustomer
     * @Description:
     * @return CellProcessor[]
     * @throws
     */
    private static CellProcessor[] getCustomer() {

        final CellProcessor[] processors = new CellProcessor[] {
                new NotNull(), // customerID
                new NotNull(), // customerName
                new Optional(), // phone
                new Optional(), // fax
                new Optional(), // last_change_date
                new Optional(new ParseInt()), // distribution flag
                new Optional(new ParseInt()), // is_deleted
                new NotNull(new ParseInt()) // batch_number
        };

        return processors;
    }
    /**
     * path.
     */
    private String path = null;
    @Override
    public final List<Customer> readCSVFile() {

        if (path == null) {
            Properties properties = PropertyReader.getInstance().read(
                    "csvPathConfig.properties");
            path = properties.getProperty("CustomerPath");

        }

        List<Customer> list = new ArrayList<Customer>();
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
            final CellProcessor[] processors = getCustomer();
            Customer customer;
            while ((customer = beanReader.read(Customer.class, header,
                    processors)) != null) {
                list.add(customer);
            }
        } catch (Exception e) {
            LOGGER.error("exception:", e);
        } finally {
            try {
                beanReader.close();
            } catch (IOException e) {
                LOGGER.error("exception:", e);
            }
        }

        return list;
    }

}
