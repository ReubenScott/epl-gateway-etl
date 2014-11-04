package com.covidien.etl.thread;

import java.util.List;

import com.covidien.etl.dao.BaseDAO;

/**
 * @ClassName: DAOThread
 * @Description:
 * @param <T>
 */
public class DAOThread<T> implements Runnable {
    /**
     * BaseDAO.
     */
    private BaseDAO<T> dao;
    /**
     * List.
     */
    private List<T> list;
    /**
     * @Title: DAOThread
     * @Description:
     * @param dao
     * dao
     * @param list
     * list
     */
    public DAOThread(final BaseDAO<T> dao, final List<T> list) {
        this.dao = dao;
        this.list = list;
    }
    @Override
    public final void run() {
        dao.process(list);
    }
}
