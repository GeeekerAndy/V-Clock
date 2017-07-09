package objects;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import util.GetHttpMessage;

public class PushServletService {
	// private static GetHttpMessage ghm=new GetHttpMessage();
	// 异步Servlet上下文队列.
	private final Map<User, AsyncContext> ASYNC_CONTEXT_MAP = new ConcurrentHashMap<User, AsyncContext>();

	// 消息队列.
	private final BlockingQueue<JSONObject> TEXT_MESSAGE_QUEUE = new LinkedBlockingQueue<JSONObject>();

	// 单一实例.
	private static PushServletService instance = new PushServletService();

	// 构造函数，创建发送消息的异步线程.
	private PushServletService() {
		new Thread(this.notifierRunnable).start();// 线程发发消息给多个用户
	}

	// 单一实例.
	public static PushServletService getInstance() {
		return instance;
	}

	/**
	 * 
	 * 注册异步Servlet上下文.
	 * 
	 * @param asyncContext
	 *            异步Servlet上下文.
	 */
	public void addAsyncContext(final AsyncContext asyncContext) {
		HttpServletRequest req = (HttpServletRequest) asyncContext.getRequest();
		User user = new User();
		// user.setEid((String)req.getSession().getAttribute("eid"));
		user.setEid(req.getParameter("eid"));
		// user.setEid("0000");
		user.setMobile(GetHttpMessage.check(req.getHeader("USER-AGENT")));
		System.out.println(req.getParameter("eid") + "****"
				+ req.getHeader("USER-AGENT"));
		// System.out.println(ASYNC_CONTEXT_MAP.size()+"---------");

		if (null != user) {
			System.out.println("客户端注册成功！");
			ASYNC_CONTEXT_MAP.put(user, asyncContext);

		}
	}

	/**
	 * 
	 * 删除异步Servlet上下文.
	 * 
	 * @param asyncContext
	 *            异步Servlet上下文.
	 * @throws IOException
	 */
	public void removeAsyncContext(final AsyncContext asyncContext)
			throws IOException {

		HttpServletRequest req = (HttpServletRequest) asyncContext.getRequest();
		User user = new User();
		// user.setEid((String)req.getSession().getAttribute("eid"));
		user.setEid(req.getParameter("eid"));
		// user.setEid(req.getParameter("eid"));
		user.setMobile(GetHttpMessage.check(req.getHeader("USER-AGENT")));
		if (null != user) {
			System.out.println("与客户端交互结束！");
			ASYNC_CONTEXT_MAP.remove(user);
			// JSONObject obj=new JSONObject();
			// obj.put("info","connect success");
			// write(asyncContext,obj);
			// PrintWriter w=asyncContext.getResponse().getWriter();
			// w.write("connect success");
			// w.flush();
			// w.close();
		}

	}

	/**
	 * 
	 * 发送消息到异步线程，最终输出到http response 流 .
	 * 
	 * @param text
	 *            发送给客户端的消息.
	 * 
	 */
	public void putMessage(final String eid, final String gname,
			final String arrivingDate) throws IllegalStateException {

		try {

			String jsonStr = "{\"eid\":" + eid + ",\"gname\":" + gname
					+ ",\"arrivingDate\":" + arrivingDate + "}";
			JSONObject message = JSONObject.fromObject(jsonStr);
			TEXT_MESSAGE_QUEUE.add(message);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

	}

	// public void putMessage(final TextMessage tm) {
	// try {
	// TEXT_MESSAGE_QUEUE.add(tm);
	// } catch (Exception ex) {
	// throw new RuntimeException(ex);
	// }
	// }
	//
	public boolean pushMessage(final JSONObject m, User user) {
		boolean result = false;
		AsyncContext ac = ASYNC_CONTEXT_MAP.get(user);
		try {
			if (null != ac) {
				System.out.println("push message!");
				System.out.println(m.toString());
				write(ac, m);
				result = true;
			}
		} catch (Exception e) {
			ASYNC_CONTEXT_MAP.remove(user);
		}

		return result;
	}

	/**
	 * 
	 * 异步线程，当消息队列中被放入数据，将释放take方法的阻塞，将数据发送到http response流上. 该方法暂时没用，用于并发测试
	 */
	private Runnable notifierRunnable = new Runnable() {

		public void run() {

			boolean done = false;
			while (!done) {
				try {
					final JSONObject message = TEXT_MESSAGE_QUEUE.take();// 当消息队列没有数据时候，线程执行到这里就会被阻塞
					System.out.println(message.getString("eid") + "?????????");
					int id = Integer.parseInt(message.getString("eid"));
					String eid = message.getString("eid");
					if (id < 10)
						eid = "000" + id;
					else if (id < 100)
						eid = "00" + id;
					else
						eid = "0" + id;
					for (Entry<User, AsyncContext> entry : ASYNC_CONTEXT_MAP
							.entrySet()) {
						//System.out.println(entry.getKey().getEid());
						if (eid.equals(entry.getKey().getEid())) {

							pushMessage(message, entry.getKey());
						}
					}
					Thread.sleep(1000);// 暂停100ms，停止的这段时间让用户有足够时间连接到服务器

				} catch (InterruptedException iex) {
					done = true;
				}

			}
		}
	};

	private void write(AsyncContext ac, JSONObject text) throws IOException {
		PrintWriter acWriter = ac.getResponse().getWriter();
		String t = text.toString();
		// System.out.println(t);
		//判断请求方，确定返回对象格式json和jsonp
		if(GetHttpMessage.check(((HttpServletRequest) ac.getRequest()).getHeader("USER-AGENT"))){
			//返回json对象
			acWriter.write(t);
		}else{
			//返回jsonp格式
			String jsonp=ac.getRequest().getParameter("callback");
			acWriter.write(jsonp+"("+t+")");
		}
		acWriter.flush();

		acWriter.close();

		ac.complete();

	}

}

class User {
	private boolean isMobile;
	private String eid = "0004";

	public boolean isMobile() {
		return isMobile;
	}

	public void setMobile(boolean isMobile) {
		this.isMobile = isMobile;
	}

	public String getEid() {
		return eid;
	}

	public void setEid(String eid) {
		this.eid = eid;
	}
}
