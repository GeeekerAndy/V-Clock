package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import objects.PushServletService;

import database.Connect;

public class SessionListener implements HttpSessionListener{
	private Connect conn;
	private Connection c;
	private PreparedStatement pstmt;
	private static SessionListener instance = new SessionListener();

	public SessionListener() {
		conn = new Connect();
		c = conn.con();
	}
	public static SessionListener getInstance() {
		return instance;
	}
	public boolean verifySession(HttpServletRequest req) {
		// 判断该会话是否在服务器已注册
		HttpSession session = req.getSession();
		System.out.println("sessionID:"+session.getId());
		if (session.isNew()) {
			System.out.println("session is new");
			return false;
		} else {
			String sessionID = session.getId();
			String eid = (String) session.getAttribute("eid");
			String sql = "select * from session where sessionid=? and clientid=?";
            System.out.println("eid in session:"+eid);
			try {
				pstmt = c.prepareStatement(sql);
				pstmt.setString(1, sessionID);
				pstmt.setString(2, eid);
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					return true;
				} else {
					return false;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	public void sessionCreated(HttpSessionEvent event) {
		String sessionID = event.getSession().getId();
		//System.out.println("SessionID:"+sessionID);
		
	}
		
    public void addToDB(HttpSession session){
    	String sql = "insert into session (sessionid,clientid) values (?,?)";
		try {
			pstmt = c.prepareStatement(sql);
			pstmt.setString(1, session.getId());
			pstmt.setString(2, (String) session.getAttribute("eid"));
			pstmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
    }
	public void sessionDestroyed(HttpSessionEvent event) {
		/*
		 * 触发sessionDestroyed监听事件
		 * 1.执行session.invalidate()方法
		 * 2.客户端较长时间没有访问服务器 限定为30分钟
		 */
		System.out.println("delete session!");
        String sessionID=event.getSession().getId();
        String sql = "delete from session where sessionid=? ";

		try {
			pstmt = c.prepareStatement(sql);
			pstmt.setString(1, sessionID);
			pstmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
  


}
