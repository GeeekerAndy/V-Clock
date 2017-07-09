package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import database.Connect;

import util.DealWithPhotoStream;

public class PrepareForPushServlet extends HttpServlet {
	private DealWithPhotoStream dealps = new DealWithPhotoStream();
	private Connect conn = new Connect();
	private Connection c;

	/**
	 * Constructor of the object.
	 */
	public PrepareForPushServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//
//		response.setHeader("Access-Control-Allow-Origin", "*");
//		request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
//		response.setCharacterEncoding("UTF-8");
//		response.setContentType("text/html;charset=UTF-8");
//
//		try {
//			// 接收来自摄像头的照片流
//			String gname = dealps.dealWithPhotoStream(request
//					.getParameter("imgStr"));
//			// 处理嘉宾重复到达的情况
//			if (!dealps.getChange()) {
//				gname = "";
//			} else {
//				PreparedStatement pstmt;
//				ResultSet rs;
//				// 在GuestList中找出负责接待的员工编号
//				String sql1 = "select eid from guestlist where gname=?";
//				pstmt = c.prepareStatement(sql1);
//				pstmt.setString(1, gname);
//				rs = pstmt.executeQuery();
//				while (rs.next()) {
//					// 向visitingrecord插入嘉宾到访记录
//					String eid = rs.getString("eid");
//					String sql2 = "insert into visitingrecord(gname,eid) values(?,?)";
//					pstmt = c.prepareStatement(sql2);
//					pstmt.setString(1, gname);
//					pstmt.setString(2, eid);
//					pstmt.execute();
//					// 在visitingrecord找出嘉宾到访的时间
//					String sql3 = "select arrivingdate from visitingrecord where gname=? and eid =?";
//					pstmt = c.prepareStatement(sql3);
//					pstmt.setString(1, gname);
//					pstmt.setString(2, eid);
//					rs = pstmt.executeQuery();
//					String arrivingDate;
//					if (rs.next()) {
//						arrivingDate = rs.getString("arrivingdate");
//						// 设置传给PushMessageServlet的request内容
//						HttpServletRequest requestToServlet = null;
//						HttpServletResponse responseFromServlet = null;
//						requestToServlet.setAttribute("gname", gname);
//						requestToServlet.setAttribute("arrivingdate",
//								arrivingDate);
//						requestToServlet.setAttribute("eid", eid);
//						requestToServlet.setAttribute("origin",
//								"PrepareForPushServlet");
//						// 将嘉宾到访记录传至PushMessageServlet进行处理
//						request.getRequestDispatcher(
//								"/servlet/PushMessageServlet").forward(
//								requestToServlet, responseFromServlet);
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		PrintWriter out = response.getWriter();
//		out.write("已接收嘉宾到达信息，开始进行转发！");
//		out.flush();
//		out.close();

	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setHeader("Access-Control-Allow-Origin", "*");
		request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");

		try {
			// 接收来自摄像头的照片流
			String gname = dealps.dealWithPhotoStream(request
					.getParameter("imgStr"));
			// 处理嘉宾重复到达的情况
			if (!dealps.getChange()) {
				gname = "";
			} else {
				PreparedStatement pstmt;
				ResultSet rs;
				// 在GuestList中找出负责接待的员工编号
				String sql1 = "select eid from guestlist where gname=?";
				pstmt = c.prepareStatement(sql1);
				pstmt.setString(1, gname);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					// 向visitingrecord插入嘉宾到访记录
					String eid = rs.getString("eid");
					String sql2 = "insert into visitingrecord(gname,eid) values(?,?)";
					pstmt = c.prepareStatement(sql2);
					pstmt.setString(1, gname);
					pstmt.setString(2, eid);
					pstmt.execute();
					// 在visitingrecord找出嘉宾到访的时间
					String sql3 = "select arrivingdate from visitingrecord where gname=? and eid =?";
					pstmt = c.prepareStatement(sql3);
					pstmt.setString(1, gname);
					pstmt.setString(2, eid);
					rs = pstmt.executeQuery();
					String arrivingDate;
					if (rs.next()) {
						arrivingDate = rs.getString("arrivingdate");
						// 设置传给PushMessageServlet的request内容
						HttpServletRequest requestToServlet = null;
						HttpServletResponse responseFromServlet = null;
						requestToServlet.setAttribute("gname", gname);
						requestToServlet.setAttribute("arrivingdate",
								arrivingDate);
						requestToServlet.setAttribute("eid", eid);
						requestToServlet.setAttribute("origin",
								"PrepareForPushServlet");
						// 将嘉宾到访记录传至PushMessageServlet进行处理
						request.getRequestDispatcher(
								"/servlet/PushMessageServlet").forward(
								requestToServlet, responseFromServlet);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		PrintWriter out = response.getWriter();
		out.write("已接收嘉宾到达信息，开始进行转发！");
		out.flush();
		out.close();

	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
