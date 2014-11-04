package com.covidien.etl.csvreader;

import java.util.List;
import java.util.Map;

import com.covidien.etl.worker.Worker;

/**
 * @ClassName: Reader
 * @Description:
 * @param <T>
 */
public interface Reader<T> {
    /**
     * Read CSV and import data to database.
     * 
     * @Title: readCSVFile
     * @param worker
     *        The etl driver.
     * @return List<T>.
     */
    List<T> readCSVFile(Worker<T> worker);

    /**
     * Get the size and batch number of CSV file.
     * 
     * @Title: getCSVSize
     * @return CSV information.
     */
    Map<String, Long> getCSVinfo();
}
