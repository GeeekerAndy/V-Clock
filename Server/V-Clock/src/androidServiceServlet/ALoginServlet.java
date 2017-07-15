package androidServiceServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import util.Employee;
import util.SessionListener;

import net.sf.json.JSONObject;

public class ALoginServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public ALoginServlet() {
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
		String etel=request.getParameter("etel");
		String ephoto=request.getParameter("ephoto");	
		System.out.println("login:"+etel);
		Employee emp=new Employee();
		String eid;
		try {
			eid = emp.login(etel, ephoto);
			response.setCharacterEncoding("UTF-8");
			System.out.println("**********************");
			PrintWriter out=null;
			out=response.getWriter();
			out.append(eid);
			System.out.println("eid(login):"+eid);
			
			if(eid.length()==4){
				HttpSession session=request.getSession();
				if(session.isNew()){
					session.setAttribute("eid", eid);
					SessionListener.getInstance().addToDB(session);
				}
				//session.setAttribute("eid", eid);
				
				
				//System.out.println(session.getId()+"--------");
				//session.setAttribute("etel", etel);
				//session.setAttribute("ephoto", ephoto);
			}
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
