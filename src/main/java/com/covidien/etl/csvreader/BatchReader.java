package com.covidien.etl.csvreader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.covidien.etl.model.Batch;

/**
 * @ClassName: BatchReader
 * @Description:
 */
public class BatchReader implements Reader<Batch> {
    /**
     * Define static log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(BatchReader.class);
    /**
     * @Title: getBatch
     * @Description:
     * @return CellProcessor[]
     * @throws
     */
    private static CellProcessor[] getBatch() {

        final CellProcessor[] processors = new CellProcessor[] {
                new NotNull(), // SOURCE_NAME
                new NotNull(new ParseInt()), // BATCH_NUMBER
                new NotNull(), // BATCH_RUN_TIMESTAMP
                new Optional(new ParseInt()), // ROWS_SENT
                new Optional() // OBJECT_NAME
        };

        return processors;
    }
    @Override
    public final List<Batch> readCSVFile() {

        Properties properties = PropertyReader.getInstance().read(
                "csvPathConfig.properties");

        List<String> paths = Arrays
                .asList("BatchCustomerPath", "BatchLocationPath",
                        "BatchLocationRolePath", "BatchDevicePath");
        List<Batch> list = new ArrayList<Batch>();

        for (String pathName : paths) {
            String path = properties.getProperty(pathName);
            LOGGER.info("batch file is :" + path);
            ICsvBeanReader beanReader = null;
            try {
                File file = new File(path);
                if (!file.exists()) {
                    return null;
                }

                CsvPreference preference = new Builder('"', 124, "\n").build();
                beanReader = new CsvBeanReader(new FileReader(path), preference);

                // the header elements are used to map the values to the bean
                // (names
                // must match)
                final String[] header = beanReader.getHeader(true);
                final CellProcessor[] processors = getBatch();
                Batch batch;
                while ((batch = beanReader
                        .read(Batch.class, header, processors)) != null) {
                    batch.setFileName(file.getName());
                    list.add(batch);
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
        }
        return list;
    }

}
