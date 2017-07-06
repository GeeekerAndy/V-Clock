package controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import util.*;

public class SearchGuestServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public SearchGuestServlet() {
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
		response.setHeader("Access-Control-Allow-Origin", "*");
		String tip=request.getParameter("tip");
		Guest guest=new Guest();
		GuestList guestList=new GuestList();
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		if(tip.equals("0")){//某工作人员对应的邀请名单中的嘉宾列表
			String eid=request.getParameter("eid");
			JSONArray searchGuestList=guestList.searchGuestList(eid);
			if(searchGuestList==null)
				out.write("2");
			else if(searchGuestList.size()==0)
				out.write("1");
			else
				out.append(searchGuestList.toString());
		}
		else if(tip.equals("1")){//某搜索对应的嘉宾列表
			String gname=request.getParameter("gname");
			JSONArray searchGuestArray=guest.searchGuest(gname);
			if(searchGuestArray==null){
				out.append("2");
			}
			else if(searchGuestArray.size()==0)
				out.append("1");
			else
				out.append(searchGuestArray.toString());
		}
		else{//某搜索对应对应的嘉宾
			String gname=request.getParameter("gname");
			JSONObject searchGuest=guest.searchOneGuest(gname);
			if(searchGuest!=null){
				out.append(searchGuest.toString());
			}
			else
				out.append("2");
		}
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
