package androidServiceServlet;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import util.Employee;
import util.SessionListener;

public class AModifyEmployeeInfoServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public AModifyEmployeeInfoServlet() {
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
		HttpSession session = request.getSession(false);
		boolean validate = SessionListener.getInstance().verifySession(request);
		if (validate) {
			Employee emp = new Employee();
			// String userTel=(String) session.getAttribute("etel");
			// String userPhoto=(String) session.getAttribute("ephoto");
			// if(userTel!=null&&userPhoto!=null){
			try {
				// String loginBool=emp.checkuser(userTel, userPhoto);
				// if(loginBool.equals("0")){
				String temp = request.getParameter("tip");
				System.out.println("tip:" + temp);
				if (temp != null) {
					String[] modifyTypeList = temp.split(";");
					String[] modifyContentList = new String[modifyTypeList.length];
					String tip = "";
					//String eid = request.getParameter("eid");
					String eid=(String) session.getAttribute("eid");
					emp.getC().setAutoCommit(false);
					int i = 0;
					for (i = 0; i < modifyTypeList.length; i++) {
						modifyContentList[i] = request
								.getParameter(modifyTypeList[i]);
						// System.out.println(modifyTypeList[i]+":"+modifyContentList[i]);
						tip = emp.modifyEmployeeInfo(eid, modifyTypeList[i],
								modifyContentList[i]);
						if (!tip.equals("0")) {
							tip += "" + (i + 1);
							emp.getC().rollback();
							break;
						}
					}
					if (tip.equals("0") && i == modifyTypeList.length)
						emp.getC().commit();
					response.setCharacterEncoding("UTF-8");
					response.setContentType("text/html; charset=utf-8");
					PrintWriter out = null;
					out = response.getWriter();
					out.append(tip);
					System.out.println("tip(modifyEmployeeInfo):" + tip);
					emp.getC().setAutoCommit(true);
					out.flush();
					out.close();
				}
				// }
				// else
				// System.out.println("No Legitimate(2)");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
