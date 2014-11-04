package com.covidien.etl.worker;

import com.covidien.etl.csvreader.CustomerReader;
import com.covidien.etl.csvreader.DeviceReader;
import com.covidien.etl.csvreader.LocationReader;
import com.covidien.etl.csvreader.LocationRoleReader;
import com.covidien.etl.dao.CustomerDAO;
import com.covidien.etl.dao.DeviceDAO;
import com.covidien.etl.dao.LocationDAO;
import com.covidien.etl.dao.LocationRoleDAO;
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
     * @Title: EtlWorker
     * @Description:
     */
    public EtlWorker() {
        Worker<Customer> customerWorker = new Worker<Customer>();
        customerWorker.setDAO(new CustomerDAO());
        customerWorker.setReader(new CustomerReader());

        Worker<Location> locationWorker = new Worker<Location>();
        locationWorker.setDAO(new LocationDAO());
        locationWorker.setReader(new LocationReader());
        customerWorker.setNextWorker(locationWorker);

        Worker<LocationRole> locationRoleWorker = new Worker<LocationRole>();
        locationRoleWorker.setDAO(new LocationRoleDAO());
        locationRoleWorker.setReader(new LocationRoleReader());
        locationWorker.setNextWorker(locationRoleWorker);

        Worker<Device> deviceWorker = new Worker<Device>();
        deviceWorker.setDAO(new DeviceDAO());
        deviceWorker.setReader(new DeviceReader());
        locationRoleWorker.setNextWorker(deviceWorker);

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
