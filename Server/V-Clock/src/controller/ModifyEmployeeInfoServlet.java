package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.Employee;


public class ModifyEmployeeInfoServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public ModifyEmployeeInfoServlet() {
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
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String temp=request.getParameter("tip");
		String[] modifyTypeList=temp.split(";");
		String[] modifyContentList=new String[modifyTypeList.length];
		Employee emp=new Employee();
		String tip="",eid;
		eid=request.getParameter("eid");
		try {
			Connection c=emp.getC();
			c.setAutoCommit(false);
			int i=0;
		for(i=0;i<modifyTypeList.length;i++){
			modifyContentList[i]=request.getParameter(modifyTypeList[i]);
			tip=emp.modifyEmployeeInfo(eid, modifyTypeList[i], modifyContentList[i]);
			if(!tip.equals("0")){
				tip+=""+(i+1);
				c.rollback();
				break;
			}
		}
		if(tip.equals("0")&&i==modifyTypeList.length)
			c.commit();
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out=null;
		out = response.getWriter();
		out.write(tip);
		out.flush();
		out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
