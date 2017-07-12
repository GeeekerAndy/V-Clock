package controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import util.Employee;

public class DisplayEmployeeInfoServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public DisplayEmployeeInfoServlet() {
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
		response.setCharacterEncoding("UTF-8");
		HttpSession session=request.getSession();
		Employee emp=new Employee();
		String userTel=(String) session.getAttribute("etel");
		String userPhoto=(String) session.getAttribute("ephoto");
		if(userTel!=null&&userPhoto!=null){
			try {
				String loginBool=emp.checkuser(userTel, userPhoto);
				if(loginBool.equals("0")){
					String eid=request.getParameter("eid");
					System.out.println("输入eid:"+eid);
					JSONObject json=emp.displayEmployeeInfo(eid);
					PrintWriter out = response.getWriter();
					out.append(json.toString());
					//System.out.println(json.toString());
					out.flush();
					out.close();
				}
				else
					System.out.println("No Legitimate(2)");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			System.out.println("No Legitimate(1)");
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
