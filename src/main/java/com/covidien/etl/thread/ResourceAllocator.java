package com.covidien.etl.thread;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.covidien.etl.dbstore.DBConnection;
import com.covidien.etl.dbstore.DBUtiltityFunctions;

/**
 * @ClassName: ResourceAllocator
 * @Description:
 */
public final class ResourceAllocator {
    /**
     * nid.
     */
    private long nid;
    /**
     * vid.
     */
    private long vid;
    /**
     * ResourceAllocator.
     */
    private static ResourceAllocator allocator = new ResourceAllocator();
    /**
     * @Title: getInstance
     * @Description:
     * @return ResourceAllocator
     */
    public static ResourceAllocator getInstance() {
        return allocator;
    }
    /**
     * @Title: ResourceAllocator
     * @Description:
     */
    private ResourceAllocator() {
        Connection con = null;
        Statement stmt = null;
        DBConnection dbConnection = DBConnection.getInstance();
        try {
            con = dbConnection.getConnection();
            con.setAutoCommit(false);
            stmt = con.createStatement();

            nid = DBUtiltityFunctions.getLatestNid(stmt);
            vid = DBUtiltityFunctions.getLatestVid(stmt);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * @Title: allocateNid
     * @Description:
     * @param groupSize
     * groupSize
     * @param groupNumber
     * groupNumber
     * @return List<List<Long>>
     */
    public List<List<Long>> allocateNid(final int groupSize,
            final int groupNumber) {
        long index = nid;
        List<List<Long>> result = new ArrayList<List<Long>>();
        for (int i = 0; i < groupNumber; i++) {
            List<Long> list = new ArrayList<Long>();
            for (int j = 0; j < groupSize; j++) {
                for (int k = 0; k < 5; k++) {
                    list.add(index++);
                }
            }
            result.add(list);
        }
        return result;
    }
    /**
     * @Title: allocateVid
     * @Description:
     * @param groupSize
     * groupSize
     * @param groupNumber
     * groupNumber
     * @return List<List<Long>>
     */
    public List<List<Long>> allocateVid(final int groupSize,
            final int groupNumber) {
        long index = vid;
        List<List<Long>> result = new ArrayList<List<Long>>();
        for (int i = 0; i < groupNumber; i++) {
            List<Long> list = new ArrayList<Long>();
            for (int j = 0; j < groupSize; j++) {
                for (int k = 0; k < 6; k++) {
                    list.add(index++);
                }
            }
            result.add(list);
        }
        return result;
    }
}
