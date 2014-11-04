package com.covidien.csvreader.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.HashMap;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import com.covidien.csvreader.model.LocationRole;
import com.covidien.dbstore.DBConnection;
import com.covidien.dbstore.DBSetup;
import com.covidien.dbstore.DBUtiltityFunctions;

public class LocationRoleReader {

	//private static final String LOCATIONROLE_CSV_FILEPATH = "C:\\Users\\Prakash\\Desktop\\Projects\\Covidien\\ETL\\GATEWAY_EXTRACT_V3\\LOCATION_ROLE.csv";

	private static CellProcessor[] getLocationRole() {

		final CellProcessor[] processors = new CellProcessor[] {
				new NotNull(), // CUSTOMER_ID
				new NotNull(), // LOCATION_ID
				new Optional(), // LOCATION_ROLE
				new Optional() // LAST_PUBLISH_DATE
		};

		return processors;
	}

	boolean fileCopied = false;
	//	StringBuffer buffer = null;

	StringBuffer nodeRevisions = null;
	StringBuffer node = null;
	StringBuffer contentTypePartyPostalAddress = null;
	StringBuffer contentAddressType = null;
	StringBuffer contentFieldActivation = null;
	StringBuffer contentFieldExpiration = null;
	StringBuffer contentFieldActivationUTC = null;
	StringBuffer contentFieldExpirationUTC = null;

	StringBuffer locationRoleResponse = null;

	private DBConnection dbConnection;

	public LocationRoleReader(String host, String dbName, 
			String user, String password, String port) {
		//	buffer = new StringBuffer();
		dbConnection = new DBConnection(dbName, host, password, user, port);

		init();
	}

	private void init() {
		nodeRevisions = new StringBuffer();
		node = new StringBuffer();
		contentTypePartyPostalAddress = new StringBuffer();
		contentAddressType = new StringBuffer();
		contentFieldActivation = new StringBuffer();
		contentFieldExpiration = new StringBuffer();
		contentFieldActivationUTC = new StringBuffer();
		contentFieldExpirationUTC = new StringBuffer();
		locationRoleResponse= new StringBuffer();
	}

	/**
	 * LocationRole CSV File Reader Function.
	 */
	public void readLocationRoleCSVFile(String path) {

		ICsvBeanReader beanReader = null;
		try {
			if (!new File(path).exists()) {
				return;
			}
			beanReader = new CsvBeanReader(new FileReader(path), CsvPreference.STANDARD_PREFERENCE);

			// the header elements are used to map the values to the bean (names must match)
			final String[] header = beanReader.getHeader(true);
			final CellProcessor[] processors = getLocationRole();

			LocationRole locationRole;
			Connection con = dbConnection.getConnection();
			con.setAutoCommit(false);
			Statement stmt = con.createStatement();
			stmt.executeUpdate(DBSetup.table2);
			con.commit();
			
			if(DBUtiltityFunctions.checkAnyLocationRoleRecordAddedByETL(stmt)) {
				return ;
			}
			//String sqlQuery = "INSERT INTO location_role (customer_id, location_id, location_role, last_publish_date, status ) values ";

			int loopCount = 0;
			int totalCount =0;
			long nid = DBUtiltityFunctions.getLatestNid(stmt);
			long vid = DBUtiltityFunctions.getLatestVid(stmt);
			long etlAdmin = DBUtiltityFunctions.getUserId(stmt, "GWetl.admin@covidien.com");

			long postalAddressRefNid;

			long partyPostalNid;

			HashMap<String, Long> locationTypeMap = new HashMap<String, Long>();
			HashMap<String, Long> partyIDMap = new HashMap<String, Long>();
			while( (locationRole = beanReader.read(LocationRole.class, header, processors)) != null ) {
				long addressTypeNid = 0;;	
				//stagingDB(locationRole);
				if(locationTypeMap.containsKey(locationRole.getLOCATION_ROLE())) {
					addressTypeNid = locationTypeMap.get(locationRole.getLOCATION_ROLE());
				}
				if(addressTypeNid == 0) {
					addressTypeNid = DBUtiltityFunctions.getAddressTypeNid(stmt, locationRole.getLOCATION_ROLE());
				}


				if(addressTypeNid == 0) {
					nodeRevisions.append("(" + nid + "," + etlAdmin + ",'" +locationRole.getLOCATION_ROLE().replace("'","\\'") + "','','','',unix_timestamp(),0),");
					node.append("("+vid+",'address_type','','" + locationRole.getLOCATION_ROLE().replace("'","\\'") + "'," + etlAdmin + ",'1',unix_timestamp(),unix_timestamp(),0,0,0),");
					contentAddressType.append("(" + nid + "," + vid + ",'" + locationRole.getLOCATION_ROLE() + "'),");
					locationTypeMap.put(locationRole.getLOCATION_ROLE(), nid);
					addressTypeNid = nid;
					nid++;
					vid++;
				} else {
					locationTypeMap.put(locationRole.getLOCATION_ROLE(), addressTypeNid);
				}


				nodeRevisions.append("(" + nid + "," + etlAdmin + ",'" +locationRole.getLOCATION_ID().replace("'","\\'") + "','','','',unix_timestamp(),0),");
				node.append("("+vid+",'party_postal_address','','" + locationRole.getLOCATION_ID().replace("'","\\'") + "'," + etlAdmin + ",'1',unix_timestamp(),unix_timestamp(),0,0,0),");

				postalAddressRefNid = DBUtiltityFunctions.getPostalAddressRefNid(stmt, locationRole.getLOCATION_ID());

				if(partyIDMap.containsKey(locationRole.getCUSTOMER_ID())) {
					partyPostalNid = partyIDMap.get(locationRole.getCUSTOMER_ID());
				} else {
					partyPostalNid = DBUtiltityFunctions.getPartyAddressNid(stmt, locationRole.getCUSTOMER_ID());
					partyIDMap.put(locationRole.getCUSTOMER_ID(), partyPostalNid);
				}
				//partyPostalNid = DBUtiltityFunctions.getPartyAddressNid(stmt, locationRole.getCUSTOMER_ID());
				contentTypePartyPostalAddress.append("(" + nid + "," + vid + "," + 
						partyPostalNid + "," + addressTypeNid + "," + postalAddressRefNid + "),");
				contentFieldActivation.append("(" + nid + "," + vid + "),");
				contentFieldExpiration.append("(" + nid + "," + vid + "),");
				contentFieldActivationUTC.append("(" + nid + "," + vid + "),");
				contentFieldExpirationUTC.append("(" + nid + "," + vid + "),");

				locationRoleResponse.append("(" + nid + ",'" + locationRole.getLOCATION_ID() + "','PASS'),");
				
				nid++;
				vid++;

				loopCount++;
				if(loopCount == 1000) {
					//stmt.executeUpdate(sqlQuery + buffer.substring(0, buffer.length()-1));
					insertBatch(con);
					totalCount = totalCount + 1000;
					System.out.println("Insert Total : " + totalCount);
					//buffer = new StringBuffer();
					loopCount = 0;
					init();
				} 
			}
			if(loopCount > 0) {
				//stmt.executeUpdate(sqlQuery + buffer.substring(0, buffer.length()-1));
				insertBatch(con);
				totalCount = totalCount + loopCount;
				System.out.println("Insert Total : " + totalCount);
				init();
			} 
			moveFile(path);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
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

		in.close();
		out.close();
		
		fileCopied = true;
		
	}

	/*private void stagingDB(LocationRole locationRole) {
		buffer.append(" ('"+ locationRole.getCUSTOMER_ID() +"' , ");
		buffer.append(" '"+ locationRole.getLOCATION_ID() +"' , ");  				
		if( locationRole.getLOCATION_ROLE()==null || locationRole.getLOCATION_ROLE().equals("null") ) {
			buffer.append("'',");
		} else {
			buffer.append(" '"+ locationRole.getLOCATION_ROLE().replace("'", "\\'") +"' , ");  					
		}
		buffer.append(" '"+ locationRole.getLAST_PUBLISH_DATE() +"' , ");
		buffer.append("0),");
	}*/

	private void insertBatch(Connection con) {
		Savepoint save = null;
		Statement stmt = null;
		try {
			save = con.setSavepoint();
			stmt = con.createStatement();
			stmt.addBatch("insert into node_revisions (nid,uid,title,body,teaser,log,timestamp,format) values " 
					+ nodeRevisions.substring(0, nodeRevisions.length()-1));
			stmt.addBatch("insert into node (vid,type,language,title,uid,status,created,changed,comment,promote," +
					"translate) values " + node.substring(0, node.length()-1));
			stmt.addBatch("insert into content_type_party_postal_address (nid, vid, field_party_postal_address_nid, " +
					"field_postal_address_type_nid, field_party_postal_address_ref_nid) values " +
					contentTypePartyPostalAddress.substring(0, contentTypePartyPostalAddress.length()-1));
			if(contentAddressType.length() > 0) {
				stmt.addBatch("insert into content_type_address_type (nid, vid, field_address_type_name_value) values " +
						contentAddressType.substring(0, contentAddressType.length()-1));
			}
			stmt.addBatch("insert into content_field_activation_datetime (nid, vid) " +
					"values " + contentFieldActivation.substring(0, contentFieldActivation.length()-1));
			stmt.addBatch("insert into content_field_expiration_datetime (nid, vid) " +
					"values " + contentFieldExpiration.substring(0, contentFieldExpiration.length()-1));
			stmt.addBatch("insert into content_field_activation_utc_offset (nid, vid) " +
					"values " + contentFieldActivationUTC.substring(0, contentFieldActivationUTC.length()-1));
			stmt.addBatch("insert into content_field_expiration_utc_offset (nid, vid) " +
					"values " + contentFieldExpirationUTC.substring(0, contentFieldExpirationUTC.length()-1));

			stmt.addBatch("insert into location_role_responce_status (nid, location_id, status) values " +
					locationRoleResponse.substring(0, locationRoleResponse.length()-1));
			stmt.executeBatch();
			con.commit();
			con.releaseSavepoint(save);
		} catch (SQLException e) {
			e.printStackTrace();
			if(save != null) {
				try {
					con.rollback(save);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

	}

	public static void main(String[] args) {
		LocationRoleReader locataionRoleReader = new LocationRoleReader("172.16.1.132", "covidien_dev_910_etl", "covidiendbuser","C0vidi3nDrp","3306");
		locataionRoleReader.readLocationRoleCSVFile("F:\\Projects\\Covidien\\ETL\\GATEWAY_EXTRACT_V4\\GATEWAY_EXTRACT_V4\\success1\\Testcase1\\LOCATION_ROLE.csv");
	}
}
