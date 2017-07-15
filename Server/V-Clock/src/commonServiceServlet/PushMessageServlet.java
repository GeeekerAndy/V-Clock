package commonServiceServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import objects.PushServletService;

import org.apache.commons.lang.StringUtils;
import org.omg.IOP.ServiceContext;


public class PushMessageServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public PushMessageServlet() {
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

			throws ServletException, IOException, IllegalStateException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");

		String timeoutStr = request.getParameter("timeout");
		long timeout;
		if (StringUtils.isNumeric(timeoutStr)) {
			timeout = Long.parseLong(timeoutStr);
		} else {
			timeout = 10 * 60 * 1000;
		}
		final HttpServletResponse finalResponse = response;
		final AsyncContext ac = request.startAsync(request, finalResponse);
		// 设置成长久链接
		ac.setTimeout(timeout);
		ac.addListener(new AsyncListener() {
			public void onComplete(AsyncEvent event) throws IOException {
				PushServletService.getInstance().removeAsyncContext(ac);
			}

			public void onTimeout(AsyncEvent event) throws IOException {
				PushServletService.getInstance().removeAsyncContext(ac);
				ac.complete();
			}

			public void onError(AsyncEvent event) throws IOException {
				PushServletService.getInstance().removeAsyncContext(ac);
				ac.complete();
			}

			public void onStartAsync(AsyncEvent event) throws IOException {
			}
		});
		PushServletService.getInstance().addAsyncContext(ac);
		System.out.println("---------------------------------------------");


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
		//接收来子PrepareForPushServlet的嘉宾信息
		response.setHeader("Access-Control-Allow-Origin", "*");
		request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");

		String timeoutStr = request.getParameter("timeout");
		long timeout;
		if (StringUtils.isNumeric(timeoutStr)) {
			timeout = Long.parseLong(timeoutStr);
		} else {
			timeout = 10 * 60 * 1000;
		}
		System.out.println("message arrive!");
		System.out.println("guestmessage:" + request.getParameter("eid")
				+ request.getParameter("gname")
				+ request.getParameter("arrivingDate"));
        String eid[]=request.getParameter("eid").split(";");
        String arrivingDate=request.getParameter("arrivingDate");
        String gname=request.getParameter("gname");
        //将嘉宾信息加入消息队列，等待发送
        for(int i=0;i<eid.length;i++){
        	PushServletService.getInstance().putMessage(
    				eid[i], gname,
    				arrivingDate);
        }
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
