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
		//ת�˽��
		double money = 6000000;
		QueryRunner qr = new QueryRunner();
		//��ѯҪת���˵���Ϣ��װ���и�account������
		String sql = "select *from account where cardid = ?";
		Account ac = qr.query(conn, sql, new BeanHandler<Account>(Account.class), "6212999999999");
		//���ת���˵����
		double balance = ac.getBalance();
	
		//�ж�����Ƿ����
		if(balance < money){
			System.out.println("����");
			System.exit(0);
		}
		//������һ������
		conn.setAutoCommit(false);
		//ִ��ת�����
		Object[] arr1 = {money,"6212999999999"};
		int i = qr.update(conn, "update account set balance = balance - ? where cardid = ?", arr1);
		//�ж�ת���˻�������
		Account a1 = qr.query(conn,sql,new BeanHandler<Account>(Account.class),"6212999999999");
		if(a1.getBalance() > 15000000){
			double rate = 2.8;
			qr.update(conn, "update account set moneyRate = ? where cardid = ?", rate,"6212999999999");
		}else{
			double rate = 2.5;
			qr.update(conn, "update account set moneyRate = ? where cardid = ?", rate,"6212999999999");

		}
		
		//ִ��ת�����
		Object[] arr3 = {money,"6212888888888"};
		int k = qr.update(conn, "update account set balance = balance + ? where cardid = ?", arr3);
		
		//�ж�ת���˻�������
		Account a2 = qr.query(conn,sql,new BeanHandler<Account>(Account.class),"6212888888888");
		if(a2.getBalance() > 15000000){
			double rate = 2.8;
			qr.update(conn, "update account set moneyRate = ? where cardid = ?", rate,"6212888888888");
		}else{
			double rate = 2.5;
			qr.update(conn, "update account set moneyRate = ? where cardid = ?", rate,"6212888888888");

		}
		//�ж��Ƿ񶼴���0   ������֤��ת�˳ɹ�
		if(i > 0 && k > 0){
			System.out.println("ת�˳ɹ�");
			//�ύ����
			conn.commit();
			Connection conn2 = JDBCUtils.getConnection();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//��ȡ��ǰʱ�����ַ�����ʽ
			String datetime = sdf.format(new Date());
			//�����ڶ�������
			conn2.setAutoCommit(false);
			//��ת������Ϣ����transaction��
			Object[] arr4 = {"6212999999999","ת��",money,datetime};
			int a = qr.update(conn2,"insert into transaction values(null,?,?,?,?)", arr4);
			//��ת������Ϣ����transaction��
			Object[] arr5 = {"6212888888888","ת��",money,datetime};
			int b = qr.update(conn2,"insert into transaction values(null,?,?,?,?)", arr5);
			//�ж��Ƿ񶼴���0
			if(a > 0 && b > 0){
				//���ɹ��ύ����
				conn2.commit();
			}else{
				//����ع�����
				conn2.rollback();
			}
		}else{
			conn.rollback();
		}
	}
}
