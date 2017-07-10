package controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import util.VisitingRecord;

public class DisplayVisitingRecordServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public DisplayVisitingRecordServlet() {
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
		response.setHeader("Access-Control-Allow-Origin", "*");
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
		response.setHeader("Access-Control-Allow-Origin", "*");
//		HttpSession session=request.getSession();
//		String eid=(String) session.getAttribute("eid");
		String eid=request.getParameter("eid");
		System.out.println("eid:"+eid);
		VisitingRecord visitingRecord=new VisitingRecord();
		JSONArray temp=visitingRecord.displayVisitingRecord(eid);
		JSONObject json=new JSONObject();
		if(temp==null)
			json.put("tip", "2");
		else{
			json.put("tip", "0");
			json.put("VisitingRecord", temp);
		}
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();		
		//System.out.println(json.toString());
		out.append(json.toString());
		out.flush();
		out.close();
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
