package gov.nih.ncats.omics.warehouse.util;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import gov.nih.ncats.omics.warehouse.conf.DataSourceConf;

public class UtilityConnector {

	private static Connection conn;
	private static Properties appConf;
	private static DataSource dataSource;
	
	public UtilityConnector() {
		
	}
	
	public UtilityConnector(String projectConf) {
		appConf = new Properties(); 
		try {
			appConf.load(new FileReader(projectConf));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public static Connection getConnection() {
		return getConnection(null, null, null, null);
	}
	
	public static Connection getUtilConnection() {
		DataSourceConf conf = new DataSourceConf();
		dataSource = conf.dataSource("jdbc:oracle:thin:/OMICS_USR@oradev04.ncats.nih.gov:1529:OMICSDB", 
				"oracle.jdbc.OracleDriver", "OMICS_USR", "nc@tsom1csusr");
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  conn;
	}

	public static Connection getBasicConnection() {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:/OMICS_USR@oradev04.ncats.nih.gov:1529:OMICSDB", "OMICS_USR", "nc@tsom1csusr");
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}
	
	public static Connection getConnection(
			@Value("${spring.datasource.url}") String jdbcUrl,
			@Value("${spring.datasource.driver-class-name}") String driverName,
			@Value("${spring.datasource.username}") String userName,
			@Value("${spring.datasource.password}") String password
			) {
		DataSourceConf conf = new DataSourceConf();
		dataSource = conf.dataSource(jdbcUrl, driverName, userName, password);
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}
	
	
	public static Connection connect() {
		try {
			Class.forName(appConf.getProperty("db.default.driver").replaceAll("^\"|\"$", ""));
			conn = DriverManager.getConnection(appConf.getProperty("db.default.url").replaceAll("^\"|\"$", ""),appConf.getProperty("db.default.user").replaceAll("^\"|\"$", ""), appConf.getProperty("db.default.password").replaceAll("^\"|\"$", ""));
			conn.setAutoCommit(false);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return conn;
	}
	
	public void testConn() {
		
		System.out.println("db.default.driver: "+ appConf.getProperty("db.default.driver"));
		System.out.println("db.default.url: "+appConf.getProperty("db.default.url"));
		System.out.println("db.default.user: "+appConf.getProperty("db.default.user"));
		System.out.println("db.default.password: "+appConf.getProperty("db.default.password"));			
	}
	
	public static void main(String [] args) {
		UtilityConnector uc = new UtilityConnector("C:\\Users\\braistedjc\\eclipse-workspace\\Omics_Warehouse_Play\\conf\\application.conf");
		uc.testConn();
		
		try {
			Connection conn = uc.connect();
			conn.setAutoCommit(false);
			System.out.println("conn:"+conn.toString());
			if(!conn.isClosed())
				System.out.println("Conn open");
			conn.close();
			if(conn.isClosed())
				System.out.println("Conn closed");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
