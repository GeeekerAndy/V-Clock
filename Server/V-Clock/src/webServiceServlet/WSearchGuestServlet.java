package webServiceServlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import util.*;

public class WSearchGuestServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public WSearchGuestServlet() {
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
		boolean validate=SessionListener.getInstance().verifySession(request);
		if(validate){
			String tip = request.getParameter("tip");
			System.out.println("tip(SearchGuestServlet):" + tip);
			Guest guest = new Guest();
			GuestList guestList = new GuestList();
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			if (tip.equals("0")) {// 某工作人员对应的邀请名单中的嘉宾列表
				//String eid = request.getParameter("eid");
				String eid=(String) session.getAttribute("eid");
				System.out.println("session:"+eid);
				JSONArray temp = guestList.searchGuestList(eid);
				JSONObject searchGuestList = new JSONObject();
				if (temp == null)
					searchGuestList.put("tip", "2");
				else {
					searchGuestList.put("tip", "0");
					searchGuestList.put("GuestList", temp);
				}
				out.append(searchGuestList.toString());
				// System.out.println("searchGuestList:"+searchGuestList.toString());
			} else if (tip.equals("1")) {// 某搜索对应的嘉宾列表
				String gname = request.getParameter("gname");

				JSONArray temp = guest.searchGuestForWeb(gname);

				JSONObject searchGuest = new JSONObject();
				if (temp == null)
					searchGuest.put("tip", "2");
				else {
					searchGuest.put("tip", "0");
					searchGuest.put("Guest", temp);
				}
				out.append(searchGuest.toString());
				// System.out.println("searchGuest:"+searchGuest.toString());
			} else {// 某搜索对应对应的嘉宾
				String gname = request.getParameter("gname");
				if (null != guest.searchOneGuest(gname)) {
					JSONObject searchOneGuest = guest.searchOneGuest(gname);

					out.append(searchOneGuest.toString());
				}
			}
			out.flush();
			out.close();
		}
		// Employee emp=new Employee();
		// String userTel=(String) session.getAttribute("etel");
		// String userPhoto=(String) session.getAttribute("ephoto");
		// if(userTel!=null&&userPhoto!=null){
		// try {
		// String loginBool=emp.checkuser(userTel, userPhoto);
		// if(loginBool.equals("0")){
		
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
