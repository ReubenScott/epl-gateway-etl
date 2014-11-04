package com.covidien.etl.csvreader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.prefs.CsvPreference.Builder;

import com.covidien.etl.common.PropertyReader;
import com.covidien.etl.model.DeviceType2Sku;

/**
 * @ClassName: DeviceType2SkuReader
 * @Description:
 */
public class DeviceType2SkuReader implements Reader<DeviceType2Sku> {
    /**
     * Define static log instance.
     */
    private static final Logger LOGGER = Logger
            .getLogger(DeviceType2SkuReader.class);
    /**
     * @Title: getSku
     * @Description:
     * @return CellProcessor[]
     * @throws
     */
    private static CellProcessor[] getSku() {

        final CellProcessor[] processors = new CellProcessor[] {
                new NotNull(),
                new NotNull(),
                new NotNull() };

        return processors;
    }
    @Override
    public final List<DeviceType2Sku> readCSVFile() {
        List<DeviceType2Sku> list = new ArrayList<DeviceType2Sku>();
        Properties properties = PropertyReader.getInstance().read(
                "csvPathConfig.properties");
        String skuInitPath = properties.getProperty("SKUInitPath");

        ICsvBeanReader beanReader = null;
        try {
            if (!new File(skuInitPath).exists()) {
                return null;
            }

            CsvPreference preference = new Builder('"', 124, "\n").build();
            beanReader = new CsvBeanReader(new FileReader(skuInitPath),
                    preference);

            // the header elements are used to map the values to the bean
            // (names
            // must match)
            final String[] header = beanReader.getHeader(true);
            final CellProcessor[] processors = getSku();
            DeviceType2Sku sku;
            while ((sku = beanReader.read(DeviceType2Sku.class, header,
                    processors)) != null) {
                list.add(sku);
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
