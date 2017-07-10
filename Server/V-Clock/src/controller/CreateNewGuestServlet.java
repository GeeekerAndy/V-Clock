package controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import faceAPI.RecognizeFace;

import objects.Guests;
import util.Guest;

public class CreateNewGuestServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public CreateNewGuestServlet() {
		super();

		guests = new Guests();
		rf =new RecognizeFace();
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
		doPost(request,response);
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
        String  imgIsValid="";
		String glist[] = new String[guests.gmessage.length];
		for (int i = 0; i < guests.gmessage.length; i++) {
//		for (int i = 0; i < guests.gmessage.length-1; i++) {
			glist[i] = request.getParameter(guests.gmessage[i]);
			if(i!=guests.gmessage.length-2)
				System.out.println(guests.gmessage[i]+":"+glist[i]);
		}
//		HttpSession session=request.getSession();
//		glist[guests.gmessage.length-1]=(String) session.getAttribute("eid");
		try {
			imgIsValid=rf.computeFaceID(glist[4]);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		PrintWriter out = response.getWriter();
		if(imgIsValid!=null){
			Guest guest = new Guest();
			String tip="";
			try {
				tip = guest.createNewGuest(glist[0], glist[1], glist[2], glist[3],
						glist[4], glist[5]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			out.append(tip);
			System.out.println("tip(create guest):"+tip);
		}else{
			out.append("2");
		}
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

	private Guests guests;
	private RecognizeFace rf;
}
