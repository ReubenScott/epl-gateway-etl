package com.covidien.etl.csvreader;

import java.util.List;

/**
 * @ClassName: Reader
 * @Description:
 * @param <T>
 */
public interface Reader<T> {
    /**
     * @Title: readCSVFile
     * @Description:
     * @return List<T>
     */
    List<T> readCSVFile();
}
