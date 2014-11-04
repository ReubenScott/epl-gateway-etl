package com.covidien.csvreader.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import com.covidien.csvreader.model.Device;
import com.covidien.dbstore.DBConnection;
import com.covidien.dbstore.DBSetup;
import com.covidien.dbstore.DBUtiltityFunctions;

public class DeviceReader {
	
	
	static class DeviceTypeKey {
		
		public DeviceTypeKey(String sku, String sourceSystem, String serailNumberValidation) {
			this.sku = sku;
			this.sourceSystem = sourceSystem;
			this.serailNumberValidation = serailNumberValidation;
		}
		
		String sku;
		
		String sourceSystem;
		
		String serailNumberValidation;

		public String getSku() {
			return sku;
		}

		public void setSku(String sku) {
			this.sku = sku;
		}

		public String getSourceSystem() {
			return sourceSystem;
		}

		public void setSourceSystem(String sourceSystem) {
			this.sourceSystem = sourceSystem;
		}

		public String getSerailNumberValidation() {
			return serailNumberValidation;
		}

		public void setSerailNumberValidation(String serailNumberValidation) {
			this.serailNumberValidation = serailNumberValidation;
		}
	}

	private static CellProcessor[] getDevice() {

		final CellProcessor[] processors = new CellProcessor[] {
				new NotNull(), //SOURCE_SYSTEM 
				new NotNull(), //SKU
				new NotNull(), //SERIAL_NUMBER
				new Optional(), //NAME
				new Optional(), //DESCRIPTION
				new Optional(), //MAINTENANCE_EXPIRATION_DATE
				new Optional(), //INSTALL_COUNTRY_CODE
				new Optional(), //CUSTOMER_ID
				new NotNull(), //LAST_PUBLISH_DATE
				new Optional() //LOCATION_ID
		};

		return processors;
	}
	
	private static CellProcessor[] getWriteDevice() {

		final CellProcessor[] processors = new CellProcessor[] {
				new NotNull(), //SOURCE_SYSTEM 
				new NotNull(), //SKU
				new NotNull(), //SERIAL_NUMBER
				new Optional(), //MAINTENANCE_EXPIRATION_DATE
				new Optional(), //CUSTOMER_ID
				new NotNull(), //LAST_PUBLISH_DATE
				new Optional() //LOCATION_ID
		};

		return processors;
	}

	static HashMap<String, ArrayList<DeviceTypeKey>> deviceTypeSKUMap = new HashMap<String, ArrayList<DeviceTypeKey>>();
	
	ArrayList<DeviceTypeKey> emptySkus = new ArrayList<DeviceReader.DeviceTypeKey>();

	boolean fileCopied = false;

	//StringBuffer buffer = null;

	StringBuffer nodeRevisions = null;
	StringBuffer node = null;
	StringBuffer deviceStr = null;
	StringBuffer contentFieldActivation = null;
	StringBuffer contentFieldExpiration = null;
	StringBuffer contentFieldActivationUTC = null;
	StringBuffer contentFieldExpirationUTC = null;
	StringBuffer contentFieldDeviceType = null;

	StringBuffer deviceInstallation = null;
	StringBuffer contentFieldDevice = null;
	StringBuffer contentFieldFacility = null;

	StringBuffer deviceServiceHistory = null;

	StringBuffer deviceResponse = null;

	HashMap<String, String> countryCodeMap = new HashMap<String, String>();

	private DBConnection dbConnection;

	public DeviceReader(String host, String dbName, 
			String user, String password, String port) {
		init();
		dbConnection = new DBConnection(dbName, host, password, user, port);
	}

	private void init() {
		//buffer = new StringBuffer();
		nodeRevisions = new StringBuffer();
		node = new StringBuffer();
		deviceStr = new StringBuffer();
		contentFieldActivation = new StringBuffer();
		contentFieldExpiration = new StringBuffer();
		contentFieldActivationUTC = new StringBuffer();
		contentFieldExpirationUTC = new StringBuffer();
		contentFieldDeviceType = new StringBuffer();

		deviceInstallation = new StringBuffer();
		contentFieldDevice = new StringBuffer();
		contentFieldFacility = new StringBuffer();

		deviceServiceHistory = new StringBuffer();

		deviceResponse = new StringBuffer();

		countryCodeMap.put("US", "United States");
	}
	/**
	 * Check the sku column exists .
	 * will be removed in next phase .
	 * @return boolean .
	 */
	private boolean checkColumnExists(){
		Connection con = null;
		try {
			con = dbConnection.getConnection();
			PreparedStatement stmt = null;
			ResultSet result = null;
			stmt = con.prepareStatement(DBSetup.checkTableDRS);
			if (stmt.execute()) {
				result = stmt.getResultSet();
				if (result != null) {
					while(result.next()) {
						if(result.getString("field").equals("sku")){
							return true;
						}
					}
				}
			}
						
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * Device CSV File Reader Function.
	 */
	public void readDeviceCSVFile(String path) {

		ICsvBeanReader beanReader = null;
		Connection con = null;
		try {
			if (!new File(path).exists()) {
				return;
			}
			beanReader = new CsvBeanReader(new FileReader(path), CsvPreference.STANDARD_PREFERENCE);

			// the header elements are used to map the values to the bean (names must match)
			final String[] header = beanReader.getHeader(true);
			final CellProcessor[] processors = getDevice();

			Device device;
			con = dbConnection.getConnection();
			con.setAutoCommit(false);
			Statement stmt = con.createStatement();
			//String sqlQuery = "INSERT INTO device (source_system,sku,serial_number,name,description,maintanance_expiration_date,install_country_code," +
			//"customer_id,last_publish_date,location_id,status) values ";
			stmt.executeUpdate(DBSetup.table4);
			if(!checkColumnExists()){
				System.out.println(DBSetup.table4);
				con.commit();
				try {
					stmt.executeUpdate(DBSetup.alter1);
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
				
				con.commit();
			}
			int loopCount = 0;
			int totalCount =0;

			long nid = DBUtiltityFunctions.getLatestNid(stmt);
			long vid = DBUtiltityFunctions.getLatestVid(stmt);
			String deviceType = null;

			long deviceInstallationNid;
			long serviceTypeNid = DBUtiltityFunctions.getServiceTypeNid(stmt, "Device Registration");
			long personNid = DBUtiltityFunctions.getPersonNid(stmt, "GWetl.admin@covidien.com");
			long etlAdmin = DBUtiltityFunctions.getUserId(stmt, "GWetl.admin@covidien.com");
			HashMap<String, Long> deviceTypeMapNid = new HashMap<String, Long>();
			HashMap<String, Long> customerMapNid = new HashMap<String, Long>();
			HashMap<String, Long> countryCodeNid = new HashMap<String, Long>();
			HashMap<String, Long> locationMapNid = new HashMap<String, Long>();
			long unknownCustomer = DBUtiltityFunctions.getunknownCustomerNid(stmt);
			String serialNumber = null;
			
			
			//Will Commented out later.
			String directory = path.substring(0, path.lastIndexOf(File.separator));
			final String[] writeHeader = new String[]{"SOURCE_SYSTEM","SKU","SERIAL_NUMBER","MAINTENANCE_EXPIRATION_DATE",
					"CUSTOMER_ID","LAST_PUBLISH_DATE","LOCATION_ID"};
			CsvBeanWriter invalidSerialNumber = new CsvBeanWriter(new FileWriter(directory + File.separator + "invalidDeviceSerial.csv"), 
					CsvPreference.STANDARD_PREFERENCE);
			invalidSerialNumber.writeHeader(writeHeader);
			
			CsvBeanWriter duplicateSerialNumber = new CsvBeanWriter(new FileWriter(directory + File.separator + "duplicateDeviceSerial.csv"), 
					CsvPreference.STANDARD_PREFERENCE);
			duplicateSerialNumber.writeHeader(writeHeader);
			final CellProcessor[] writeProcessors = getWriteDevice();
			
			int invalidDevice = 0;
			int duplicateDevice = 0;
			int validDevice = 0;
			HashMap<String, ArrayList<String>> deviceTypeSerial = new HashMap<String, ArrayList<String>>();
			
			while( (device = beanReader.read(Device.class, header, processors)) != null ) {
				totalCount++;

				deviceType = getDeviceType(stmt, device.getSKU(), device.getSOURCE_SYSTEM());
				if(deviceType == null) {
					continue;
				}
				
				serialNumber = deviceTypeSKUMap.get(deviceType).get(0).getSerailNumberValidation();

				if(serialNumber != null && !device.getSERIAL_NUMBER().matches(serialNumber)) {
					invalidSerialNumber.write(device, writeHeader, writeProcessors);
					invalidSerialNumber.flush();
					invalidDevice++;
					continue;
				}
				
				if(!deviceTypeSerial.containsKey(deviceType)) {
					deviceTypeSerial.put(deviceType, DBUtiltityFunctions.getDeviceTypeSerialNumbers(stmt, deviceType));
				}
				
				if(deviceTypeSerial.get(deviceType).contains(device.getSERIAL_NUMBER())) {
					duplicateSerialNumber.write(device, writeHeader, writeProcessors);
					duplicateSerialNumber.flush();
					duplicateDevice++;
					continue;
				}
				
				deviceTypeSerial.get(deviceType).add(device.getSERIAL_NUMBER());
				
				validDevice++;
				long deviceTypeNid = 0;
				if(deviceTypeMapNid.containsKey(deviceType)) {
					deviceTypeNid = deviceTypeMapNid.get(deviceType);
				} else {
					deviceTypeNid = DBUtiltityFunctions.getDeviceTypeNid(stmt, deviceType);
					deviceTypeMapNid.put(deviceType, deviceTypeNid);
				}
				
				long customerNid = 0;
				if(device.getCUSTOMER_ID()==null) {
					customerNid = unknownCustomer;
				} else if(customerMapNid.containsKey(device.getCUSTOMER_ID())) {
					customerNid = customerMapNid.get(device.getCUSTOMER_ID());
				} else {
					customerNid = DBUtiltityFunctions.getCustomerNid(stmt, device.getCUSTOMER_ID());
					customerMapNid.put(device.getCUSTOMER_ID(), customerNid);
				}

//				nid++;
//				vid++;

				long deviceNid = nid;

				nodeRevisions.append("(" + nid + "," + etlAdmin + ",'" +device.getSERIAL_NUMBER().replace("'","\\'") + "','','','',unix_timestamp(),0),");
				node.append("("+vid+",'device','','" + device.getSERIAL_NUMBER().replace("'","\\'") + "'," + etlAdmin + ",'1',unix_timestamp(),unix_timestamp(),0,0,0),");
				if(device.getMAINTENANCE_EXPIRATION_DATE() == null || device.getMAINTENANCE_EXPIRATION_DATE().equals("null")) {
					deviceStr.append("(" +  nid + "," + vid + ",'" + device.getSERIAL_NUMBER() + "',1," + customerNid + ",NULL),");
				} else {
					deviceStr.append("(" +  nid + "," + vid + ",'" + device.getSERIAL_NUMBER() + "',1," + customerNid + ",'" + device.getMAINTENANCE_EXPIRATION_DATE() + "'),");
				}
				contentFieldActivation.append("(" + nid + "," + vid + "),");
				contentFieldExpiration.append("(" + nid + "," + vid + "),");
				contentFieldActivationUTC.append("(" + nid + "," + vid + "),");
				contentFieldExpirationUTC.append("(" + nid + "," + vid + "),");
				contentFieldDeviceType.append("(" + nid + "," + vid + "," + deviceTypeNid + "),");

				nid++;
				vid++;

				nodeRevisions.append("(" + nid + "," + etlAdmin + ",'"+ "Device Installation','','','',unix_timestamp(),0),");
				node.append("("+vid+",'device_installation','','Device Installation" + "'," + etlAdmin + ",'1',unix_timestamp(),unix_timestamp(),0,0,0),");
				long countryNid;

				if(countryCodeNid.containsKey(device.getINSTALL_COUNTRY_CODE())) {
					countryNid = countryCodeNid.get(device.getINSTALL_COUNTRY_CODE());
				} else {
					countryNid = DBUtiltityFunctions.getCountryNid(stmt, countryCodeMap.get(device.getINSTALL_COUNTRY_CODE()));
					countryCodeNid.put(device.getINSTALL_COUNTRY_CODE(), countryNid);
				}
				long locationNid;
				if(locationMapNid.containsKey(device.getLOCATION_ID())) {
					locationNid = locationMapNid.get(device.getLOCATION_ID());
				} else {
					locationNid = DBUtiltityFunctions.getPostalAddressRefNid(stmt, device.getLOCATION_ID());
					locationMapNid.put(device.getLOCATION_ID(), locationNid);
				}

				deviceInstallationNid = nid;
				deviceInstallation.append("(" + nid + "," + vid + "," + countryNid + "," + locationNid + "),");
				contentFieldDevice.append("(" + nid + "," + vid + "," + deviceNid + "),");
				contentFieldFacility.append("(" + nid + "," + vid + "),");
				contentFieldActivation.append("(" + nid + "," + vid + "),");
				contentFieldExpiration.append("(" + nid + "," + vid + "),");
				contentFieldActivationUTC.append("(" + nid + "," + vid + "),");
				contentFieldExpirationUTC.append("(" + nid + "," + vid + "),");

				nid++;
				vid++;

				nodeRevisions.append("(" + nid + "," + etlAdmin + ",'" + "Device Service','','','',unix_timestamp(),0),");
				node.append("("+vid+",'device_service_history','','Device Service" + "'," + etlAdmin + ",'1',unix_timestamp(),unix_timestamp(),0,0,0),");

				deviceServiceHistory.append("(" + nid + "," + vid + "," + deviceInstallationNid + ","
						+ serviceTypeNid + "," + personNid + ",'Device Registered',current_timestamp()),");
				contentFieldDevice.append("(" + nid + "," + vid + "," + deviceNid + "),");
				//appendForStageingDB(device);

				deviceResponse.append("(" + deviceNid + ",'" + device.getSERIAL_NUMBER() + "','" + 
						device.getSKU() + "','PASS'),");

				nid++;
				vid++;
				
				loopCount++;
				if(loopCount == 1000) {
					//	stmt.executeUpdate(sqlQuery + buffer.substring(0, buffer.length()-1));
					//	buffer = new StringBuffer();
					batchInserts(con);
					loopCount = 0;
					init();
				}
			}
			if(loopCount > 0) {
				//	stmt.executeUpdate(sqlQuery + buffer.substring(0, buffer.length()-1));
				batchInserts(con);
				totalCount = totalCount + loopCount;
				System.out.println("Total records : " + totalCount);
				System.out.println("Valid records : " + validDevice);
				System.out.println("Invalid records :" + invalidDevice);
				System.out.println("Duplication records :" + duplicateDevice);
				init();
			} 
			moveFile(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if( beanReader != null ) {
				try {
					beanReader.close();
					// delete the original file.
					if(fileCopied) {
						new File(path).delete();						
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void moveFile(String path) throws IOException {
		File filePath = new File(path);
		File successFolder = new File(path.substring(0, path.lastIndexOf(File.separator)) + File.separator + "success");
		
		if(!successFolder.exists()) {
			successFolder.mkdir();
		}
		
		InputStream in = new FileInputStream(new File(path));
		OutputStream out = new FileOutputStream(new File(successFolder.getAbsolutePath() + File.separator + filePath.getName()));
		
		byte[] buffer = new byte[10240];
		int length;
	    //copy the file content in bytes 
	    while ((length = in.read(buffer)) > 0){
	    	out.write(buffer, 0, length);
	    }
	    fileCopied = true;

	    in.close();
	    out.close();
	}

	/*private void appendForStageingDB(Device device) {
		buffer.append(" ('"+ device.getSOURCE_SYSTEM() +"' , ");
		buffer.append(" '"+ device.getSKU() +"' , ");  				
		if( device.getSERIAL_NUMBER()==null || device.getSERIAL_NUMBER().equals("null") ) {
			buffer.append("'',");
		} else {
			buffer.append(" '"+ device.getSERIAL_NUMBER() +"' , ");  					
		}
		if( device.getNAME()==null || device.getNAME().equals("null") ) {
			buffer.append("'',");
		} else {
			buffer.append(" '"+ device.getNAME().replace("'", "\\'") +"' , ");  					
		}
		if( device.getDESCRIPTION()==null || device.getDESCRIPTION().equals("null") ) {
			buffer.append("'',");
		} else {
			buffer.append(" '"+ device.getDESCRIPTION().replace("'", "\\'") +"' , ");  					
		}
		if( device.getMAINTENANCE_EXPIRATION_DATE()==null || device.getMAINTENANCE_EXPIRATION_DATE().equals("null") ) {
			buffer.append("'0000.00.00',");
		} else {
			buffer.append(" '"+ device.getMAINTENANCE_EXPIRATION_DATE() +"' , ");  					
		}
		if( device.getINSTALL_COUNTRY_CODE()==null || device.getINSTALL_COUNTRY_CODE().equals("null") ) {
			buffer.append("'',");
		} else {
			buffer.append(" '"+ device.getINSTALL_COUNTRY_CODE().replace("'", "\\'") +"' , ");  					
		}
		buffer.append(" '"+ device.getCUSTOMER_ID() +"' , ");
		buffer.append(" '"+ device.getLAST_PUBLISH_DATE() +"' , ");
		buffer.append(" '"+ device.getLOCATION_ID() +"' , ");
		buffer.append("0),");
	}*/

	private void batchInserts(Connection con) {
		Savepoint save = null;
		Statement stmt = null;
		try {
			save = con.setSavepoint();
			stmt = con.createStatement();
			stmt.addBatch("insert into node_revisions (nid,uid,title,body,teaser,log,timestamp,format) values " 
					+ nodeRevisions.substring(0, nodeRevisions.length()-1));
			stmt.addBatch("insert into node (vid,type,language,title,uid,status,created,changed,comment,promote," +
					"translate) values " + node.substring(0, node.length()-1));
			stmt.addBatch("insert into content_type_device (nid, vid, field_device_serial_number_value, " +
					"field_device_is_active_value, field_device_owner_nid, field_maintance_expiration_date_value) values " +
					deviceStr.substring(0, deviceStr.length()-1));
			stmt.addBatch("insert into content_field_device_type (nid, vid, field_device_type_nid) values " +
					contentFieldDeviceType.substring(0, contentFieldDeviceType.length()-1));
			stmt.addBatch("insert into content_type_device_installation (nid, vid, field_device_country_nid, field_location_id_nid) values" +
					deviceInstallation.substring(0, deviceInstallation.length()-1));
			stmt.addBatch("insert into content_field_device_pk (nid, vid, field_device_pk_nid) values " +
					contentFieldDevice.substring(0, contentFieldDevice.length()-1));
			stmt.addBatch("insert into content_field_facility_pk (nid, vid) values " +
					contentFieldFacility.substring(0, contentFieldFacility.length()-1));
			stmt.addBatch("insert into content_field_activation_datetime (nid, vid) " +
					"values " + contentFieldActivation.substring(0, contentFieldActivation.length()-1));
			stmt.addBatch("insert into content_field_expiration_datetime (nid, vid) " +
					"values " + contentFieldExpiration.substring(0, contentFieldExpiration.length()-1));
			stmt.addBatch("insert into content_field_activation_utc_offset (nid, vid) " +
					"values " + contentFieldActivationUTC.substring(0, contentFieldActivationUTC.length()-1));
			stmt.addBatch("insert into content_field_expiration_utc_offset (nid, vid) " +
					"values " + contentFieldExpirationUTC.substring(0, contentFieldExpirationUTC.length()-1));
			stmt.addBatch("insert into content_type_device_service_history (nid, vid, field_device_installation_pk_nid, " +
					"field_device_service_type_nid, field_service_person_pk_nid, field_service_note_value, field_service_datetime_value) values " +
					deviceServiceHistory.substring(0, deviceServiceHistory.length()-1));
			stmt.addBatch("insert into device_responce_status (nid, serial_no, sku, status) values " +
					deviceResponse.substring(0, deviceResponse.length()-1));
			stmt.executeBatch();
			con.commit();
			con.releaseSavepoint(save);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(save != null) {
				try {
					con.rollback(save);
					con.commit();
					con.releaseSavepoint(save);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				
			}			
		} finally {
			
		}
	}

	private String getDeviceType(Statement stmt, String sku, String sourceSystem) throws SQLException {
		String deviceType = null;
		String serialKeyValidation = null;
		for(DeviceTypeKey key : emptySkus) {
			if(key.getSku().equals(sku) && key.getSourceSystem().equals(sourceSystem)) {
				return deviceType;
			}
		}
		Set<String> deviceTypes = deviceTypeSKUMap.keySet();
		for(String type : deviceTypes) {
			if(deviceType == null) {
				for(DeviceTypeKey deviceTypeKey : deviceTypeSKUMap.get(type)) {
					if(deviceTypeKey.getSku().equals(sku) && deviceTypeKey.getSourceSystem().equals(sourceSystem)) {
						deviceType = type;
						break;
					}
				}				
			} else {
				break;
			}
		}
		if(deviceType==null) {
			deviceType = DBUtiltityFunctions.getDeviceType(stmt, sku, sourceSystem);
			if(deviceType!=null && deviceType.equals("SCD 700")) {
				serialKeyValidation = DBUtiltityFunctions.getSerialNumberValidation(stmt, deviceType);				
			}
			if(deviceType != null) {
				if(!deviceTypeSKUMap.containsKey(deviceType)) {
					deviceTypeSKUMap.put(deviceType, new ArrayList<DeviceReader.DeviceTypeKey>());
				}
				deviceTypeSKUMap.get(deviceType).add(new DeviceTypeKey(sku, sourceSystem, serialKeyValidation));
			} else {
				emptySkus.add(new DeviceTypeKey(sku, sourceSystem, serialKeyValidation));
			}
		}
		return deviceType;
	}

	public static void main(String[] args) {
		System.out.println(System.currentTimeMillis());
		DeviceReader deviceReader = new DeviceReader("172.16.1.132", "covidien_dev_910_etl", "covidiendbuser","C0vidi3nDrp","3306");
		deviceReader.readDeviceCSVFile("F:\\Projects\\Covidien\\ETL\\GATEWAY_EXTRACT_V4\\GATEWAY_EXTRACT_V4\\success1\\Testcase1\\DEVICE.csv");
		System.out.println(System.currentTimeMillis());
	}
}
