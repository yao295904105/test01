package cn.itcast.app;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import cn.itcast.domain.Account;
import cn.itcast.tools.JDBCUtils;

public class MainApp {
	public static void main(String[] args) throws Exception {
		Connection conn = JDBCUtils.getConnection();
		//转账金额
		double money = 6000000;
		QueryRunner qr = new QueryRunner();
		//查询要转出人的信息封装到有个account对象中
		String sql = "select *from account where cardid = ?";
		Account ac = qr.query(conn, sql, new BeanHandler<Account>(Account.class), "6212999999999");
		//查出转出人的余额
		double balance = ac.getBalance();
	
		//判断余额是否充足
		if(balance < money){
			System.out.println("余额不足");
			System.exit(0);
		}
		//开启第一个事务
		conn.setAutoCommit(false);
		//执行转出语句
		Object[] arr1 = {money,"6212999999999"};
		int i = qr.update(conn, "update account set balance = balance - ? where cardid = ?", arr1);
		//判断转出账户的利率
		Account a1 = qr.query(conn,sql,new BeanHandler<Account>(Account.class),"6212999999999");
		if(a1.getBalance() > 15000000){
			double rate = 2.8;
			qr.update(conn, "update account set moneyRate = ? where cardid = ?", rate,"6212999999999");
		}else{
			double rate = 2.5;
			qr.update(conn, "update account set moneyRate = ? where cardid = ?", rate,"6212999999999");

		}
		
		//执行转入语句
		Object[] arr3 = {money,"6212888888888"};
		int k = qr.update(conn, "update account set balance = balance + ? where cardid = ?", arr3);
		
		//判断转入账户的利率
		Account a2 = qr.query(conn,sql,new BeanHandler<Account>(Account.class),"6212888888888");
		if(a2.getBalance() > 15000000){
			double rate = 2.8;
			qr.update(conn, "update account set moneyRate = ? where cardid = ?", rate,"6212888888888");
		}else{
			double rate = 2.5;
			qr.update(conn, "update account set moneyRate = ? where cardid = ?", rate,"6212888888888");

		}
		//判断是否都大于0   都大于证明转账成功
		if(i > 0 && k > 0){
			System.out.println("转账成功");
			//提交事务
			conn.commit();
			Connection conn2 = JDBCUtils.getConnection();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//获取当前时间以字符串形式
			String datetime = sdf.format(new Date());
			//开启第二个事务
			conn2.setAutoCommit(false);
			//将转出人信息存入transaction中
			Object[] arr4 = {"6212999999999","转出",money,datetime};
			int a = qr.update(conn2,"insert into transaction values(null,?,?,?,?)", arr4);
			//将转入人信息出入transaction中
			Object[] arr5 = {"6212888888888","转入",money,datetime};
			int b = qr.update(conn2,"insert into transaction values(null,?,?,?,?)", arr5);
			//判断是否都大于0
			if(a > 0 && b > 0){
				//都成功提交事务
				conn2.commit();
			}else{
				//否则回滚事务
				conn2.rollback();
			}
		}else{
			conn.rollback();
		}
	}
}
