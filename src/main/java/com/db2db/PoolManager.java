package com.db2db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.tomcat.jdbc.pool.DataSource;

public class PoolManager {

	DataSource  ds = null;
	static PoolManager pool;
	public PoolManager() {
		try {
			
			ds = new DataSource();
			ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
			ds.setUrl("jdbc:mysql://" + System.getenv("dbhost") + ":3306/db2db");
			ds.setUsername(System.getenv("dbuser"));
			ds.setPassword(System.getenv("dbpassword"));
			ds.setInitialSize(1);
			ds.setMaxActive(5);
			ds.setMaxIdle(2);
			ds.setMinIdle(1);
			ds.setTestOnBorrow(true);
//			Properties dbProperties = new Properties();
//	        
//			dbProperties.setProperty("useSSL", "true");
//			dbProperties.setProperty("verifyServerCertificate", "true");
//			dbProperties.setProperty("requireSSL", "false");
//			ds.setDbProperties(dbProperties);
			
			
			}catch(Exception e) {
				e.printStackTrace();
			}
	}
	
	public static PoolManager getInstance() {
		if(pool == null) {
			pool = new PoolManager();
		}
		return pool;
	}
	
	public Connection getConnection() throws SQLException {
		return ds.getConnection();
	}
	
	public static void main(String[] args) {
		try {
			PoolManager.getInstance().getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		String host = "mysqlb.mysql.database.azure.com:3306";
//        String database = "db2db";
//        String user = "myadmin@mysqlb";
//        String password = "Azurep@ssw0rd";
//
//        // check that the driver is installed
//        try
//        {
//            Class.forName("com.mysql.jdbc.Driver");
//        }
//        catch (ClassNotFoundException e)
//        {
//            e.printStackTrace();
//        }
//
//        System.out.println("MySQL JDBC driver detected in library path.");
//
//        Connection connection = null;
//
//        // Initialize connection object
//        try
//        {
//            String url = String.format("jdbc:mysql://%s/%s", host, database);
//
//            // Set connection properties.
//            Properties properties = new Properties();
//            properties.setProperty("user", user);
//            properties.setProperty("password", password);
////            properties.setProperty("useSSL", "true");
////            properties.setProperty("verifyServerCertificate", "true");
////            properties.setProperty("requireSSL", "false");
//
//            // get connection
//            connection = DriverManager.getConnection(url, properties);
//        }
//        catch (SQLException e)
//        {
//           e.printStackTrace();// throw new SQLException("Failed to create connection to database.", e);
//        }
	}
}
