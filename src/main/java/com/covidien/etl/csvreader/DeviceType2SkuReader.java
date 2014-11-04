package com.covidien.etl.csvreader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.prefs.CsvPreference.Builder;

import com.covidien.etl.common.Constant;
import com.covidien.etl.common.PropertyReader;
import com.covidien.etl.model.DeviceType2Sku;
import com.covidien.etl.worker.Worker;

/**
 * @ClassName: DeviceType2SkuReader
 * @Description:
 */
public class DeviceType2SkuReader implements Reader<DeviceType2Sku> {
    /**
     * Define static log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(DeviceType2SkuReader.class);

    /**
     * @Title: getSku
     * @Description:
     * @return CellProcessor[]
     * @throws
     */
    private static CellProcessor[] getSku() {

        final CellProcessor[] processors = new CellProcessor[] {
                new NotNull(new Trim()), new NotNull(new Trim()), new NotNull(new Trim()) };

        return processors;
    }

    @Override
    public final List<DeviceType2Sku> readCSVFile(Worker<DeviceType2Sku> worker) {
        List<DeviceType2Sku> list = new ArrayList<DeviceType2Sku>();
        Properties properties = PropertyReader.getInstance().read(
                Constant.getConfigFilePath() + "csvPathConfig.properties");
        String fullName = Constant.getConfigFilePath() + properties.getProperty("SKUInitDataFileName");
        ICsvBeanReader beanReader = null;
        try {
            if (!new File(fullName).exists()) {
                return null;
            }

            CsvPreference preference = new Builder('"', Constant.CSV_DELIMTIER_PIPE, "\n").build();
            beanReader = new CsvBeanReader(new FileReader(fullName), preference);

            // the header elements are used to map the values to the bean
            // (names
            // must match)
            final String[] header = beanReader.getHeader(true);
            final CellProcessor[] processors = getSku();
            DeviceType2Sku sku;
            while ((sku = beanReader.read(DeviceType2Sku.class, header, processors)) != null) {
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

    @Override
    public final Map<String, Long> getCSVinfo() {
        return null;
    }

}
