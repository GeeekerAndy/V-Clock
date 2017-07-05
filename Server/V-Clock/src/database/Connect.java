package database;
import java.sql.*;

public class Connect {
	Connection conn = null;
	private static Statement stat;
	private static ResultSet rs;

	public Connection con() {
		String url = "jdbc:mysql://121.250.222.75:3306/facerecognition";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, "root", "Wuhuabaren53");
			stat = conn.createStatement();

		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("��������");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}
	public static Statement getStat() {
		return stat;
	}
	public static void setStat(Statement stat) {
		Connect.stat = stat;
	}
	public void databaseclose() {
		try {
			conn.close();
			stat.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("��ݿ�رճ���");
			e.printStackTrace();
		}
	}



	/**
	 * @param args
	 */

	public static ResultSet getRs() {
		return rs;
	}
	public static void setRs(ResultSet rs) {
		Connect.rs = rs;
	}

}
