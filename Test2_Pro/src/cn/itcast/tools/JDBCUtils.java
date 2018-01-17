package cn.itcast.tools;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class JDBCUtils {
	private static ComboPooledDataSource ds = new ComboPooledDataSource();
	//公有，静态成员方法：public static DataSource getDataSource()此方法返回C3P0连接池对象
	public static DataSource getDataSource(){
		return ds;
	}
	//方法返回通过C3P0连接池获取的Connection对象
	public static Connection getConnection() throws SQLException{
		return ds.getConnection();
	}
}
