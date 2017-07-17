package androidServiceServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import database.Connect;

import util.Employee;
import util.Guest;
import util.SessionListener;

public class AModifyGuestInfoServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public AModifyGuestInfoServlet() {
		super();
		guest = new Guest();

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
		String result = "", loginBool = "";
		HttpSession session = request.getSession(false);

		Employee emp = new Employee();
		// String userTel=(String) session.getAttribute("etel");
		// String userPhoto=(String) session.getAttribute("ephoto");
		// if(userTel!=null&&userPhoto!=null){
		// try {
		// loginBool=emp.checkuser(userTel, userPhoto);
		// } catch (Exception e2) {
		// // TODO Auto-generated catch block
		// e2.printStackTrace();
		// }
		// if(loginBool.equals("0")){
		try {
			String informationType = request.getParameter("tip");
			System.out.println("informationType:" + informationType);
			String[] infoList = informationType.split(";");
			// String[] temp = informationType.split(";");
			// String[] infoList=new String[temp.length];
			// infoList[0]=(String) session.getAttribute("eid");
			// for(int i=1;i<infoList.length;i++){
			// infoList[i]=temp[i-1];
			// }
			String gname = request.getParameter("gname");
			System.out.println("gname(ModifyGuestInfo):" + gname);
			guest.getC().setAutoCommit(false);
			for (int i = 0; i < infoList.length; i++) {
				String info = request.getParameter(infoList[i]);
				result += guest.modifyInfo(gname, info, infoList[i]);
			}
			int res=Integer.parseInt(result);
			if(res>0)
				guest.getC().rollback();
			guest.getC().commit();
			out.append(result);
			// guest.getC().setAutoCommit(false);
		} catch (Exception e) {
			try {
				guest.getC().rollback();
				out.append(result);
				// guest.getC().setAutoCommit(true);
			} catch (SQLException e1) {
				e1.printStackTrace();
				out.append(result);
			}
		}
		System.out.println(result);
		out.flush();
		out.close();
		// }

		// else
		// System.out.println("No Legitimate(2)");
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

	private Guest guest;

}
