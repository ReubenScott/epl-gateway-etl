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
import com.covidien.etl.model.SNRule;

/**
 * @ClassName: SNRuleReader
 * @Description:
 */
public class SNRuleReader implements Reader<SNRule> {
    /**
     * Define static log instance.
     */
    private static final Logger LOGGER = Logger.getLogger(SNRuleReader.class);
    /**
     * @Title: getSnRule
     * @Description:
     * @return CellProcessor[]
     * @throws
     */
    private static CellProcessor[] getSnRule() {

        final CellProcessor[] processors = new CellProcessor[] {
                new NotNull(),
                new NotNull() };

        return processors;
    }
    @Override
    public final List<SNRule> readCSVFile() {
        List<SNRule> list = new ArrayList<SNRule>();
        Properties properties = PropertyReader.getInstance().read(
                "csvPathConfig.properties");
        String snInitPath = properties.getProperty("SNInitPath");

        ICsvBeanReader beanReader = null;
        try {
            if (!new File(snInitPath).exists()) {
                return null;
            }

            CsvPreference preference = new Builder('"', 44, "\n").build();
            beanReader = new CsvBeanReader(new FileReader(snInitPath),
                    preference);

            // the header elements are used to map the values to the bean
            // (names
            // must match)
            final String[] header = beanReader.getHeader(true);
            final CellProcessor[] processors = getSnRule();
            SNRule sn;
            while ((sn = beanReader.read(SNRule.class, header, processors)) != null) {
                list.add(sn);
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
