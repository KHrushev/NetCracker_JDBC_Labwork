package com.hrushev.dao;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnectionPool {
    private static BasicDataSource ds = new BasicDataSource();

    static {
        ds.setUrl("jdbc:oracle:thin:@192.168.1.215:1521:XE");
        ds.setUsername("system");
        ds.setPassword("hellfire");
        ds.setMinIdle(5);
        ds.setMaxIdle(10);
        ds.setMaxOpenPreparedStatements(100);
        ds.setDriverClassName("oracle.jdbc.driver.OracleDriver");
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    private DBConnectionPool(){ }
}
