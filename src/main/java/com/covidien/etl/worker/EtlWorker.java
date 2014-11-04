package com.covidien.etl.worker;

import java.sql.Connection;

import com.covidien.etl.csvreader.CustomerReader;
import com.covidien.etl.csvreader.DeviceReader;
import com.covidien.etl.csvreader.LocationReader;
import com.covidien.etl.csvreader.LocationRoleReader;
import com.covidien.etl.dao.CustomerDAO;
import com.covidien.etl.dao.DeviceDAO;
import com.covidien.etl.dao.InitialDAO;
import com.covidien.etl.dao.LocationDAO;
import com.covidien.etl.dao.LocationRoleDAO;
import com.covidien.etl.dao.helper.CustomerDAOSQLHelper;
import com.covidien.etl.dao.helper.DeviceDAOSQLHelper;
import com.covidien.etl.dao.helper.LocationDAOSQLHelper;
import com.covidien.etl.dao.helper.LocationRoleDAOSQLHelper;
import com.covidien.etl.dbstore.DBConnection;
import com.covidien.etl.dbstore.DBUtiltityFunctions;
import com.covidien.etl.model.Customer;
import com.covidien.etl.model.Device;
import com.covidien.etl.model.Location;
import com.covidien.etl.model.LocationRole;

/**
 * @ClassName: EtlWorker
 * @Description:
 */
public class EtlWorker {
    /**
     * Worker.
     */
    @SuppressWarnings("rawtypes")
    private Worker firstWorker;

    /**
     * @throws Exception
     *         Exception
     * @Title: EtlWorker
     * @Description:
     */
    public EtlWorker() throws Exception {
        Connection con = DBConnection.getInstance().getConnection();
        //Create xref table to database by BaseDao when first time run etl2.0.
        CustomerDAO cDao = new CustomerDAO();

        InitialDAO iDao = new InitialDAO();

        iDao.initialXref();

        cDao.init(con);
        DBUtiltityFunctions.init(con);
        Worker<Customer> customerWorker = new Worker<Customer>();
        customerWorker.setDao(cDao);
        customerWorker.setReader(new CustomerReader());
        CustomerDAOSQLHelper.init(con);

        Worker<Location> locationWorker = new Worker<Location>();
        locationWorker.setDao(new LocationDAO());
        locationWorker.setReader(new LocationReader());
        customerWorker.setNextWorker(locationWorker);
        LocationDAOSQLHelper.init(con);

        Worker<LocationRole> locationRoleWorker = new Worker<LocationRole>();
        locationRoleWorker.setDao(new LocationRoleDAO());
        locationRoleWorker.setReader(new LocationRoleReader());
        locationWorker.setNextWorker(locationRoleWorker);
        LocationRoleDAOSQLHelper.init(con);

        Worker<Device> deviceWorker = new Worker<Device>();
        deviceWorker.setDao(new DeviceDAO());
        deviceWorker.setReader(new DeviceReader());
        locationRoleWorker.setNextWorker(deviceWorker);
        DeviceDAOSQLHelper.init(con);

        this.firstWorker = customerWorker;
    }

    /**
     * @Title: work
     * @Description:
     */
    public final void work() {
        firstWorker.work();
    }
}
