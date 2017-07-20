package androidServiceServlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import util.*;

public class AAddtoGuestListServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */

	private GetHttpMessage ghm = new GetHttpMessage();

	public AAddtoGuestListServlet() {
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
		response.setHeader("Access-Control-Allow-Origin", "*");
		doPost(request, response);
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
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession(false);

		Employee emp = new Employee();
		// String userTel=(String) session.getAttribute("etel");
		// String userPhoto=(String) session.getAttribute("ephoto");
		// if(userTel!=null&&userPhoto!=null){
		// try {
		// String loginBool=emp.checkuser(userTel, userPhoto);
		// if(loginBool.equals("0")){
		String gname = request.getParameter("gname");
		String eid = request.getParameter("eid");
		// String eid = (String) session.getAttribute("eid");
		GuestList guestList = new GuestList();
		String tip = guestList.addToGuestList(gname, eid);
		response.setCharacterEncoding("UTF-8");
		out.write(tip);
		System.out.println("tip(AddtoGuestListServlet):" + tip);
		out.flush();
		out.close();
		// }

		// else
		// System.out.println("No Legitimate(2)");
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// else
		// System.out.println("No Legitimate(1)");
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
