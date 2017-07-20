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
	public synchronized void addAsyncContext(final AsyncContext asyncContext) {
		// try {
		// Thread.sleep(100);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		HttpServletRequest req = (HttpServletRequest) asyncContext.getRequest();
		User user = new User();
		// user.setEid((String)req.getSession().getAttribute("eid"));
		// System.out.println((String)req.getSession().getAttribute("eid"));
		boolean isMobile = GetHttpMessage.check(req.getHeader("USER-AGENT"));
		if (isMobile) {
			user.setEid(req.getParameter("eid"));
			System.out.println("mobile eid:" + req.getParameter("eid"));
		} else {
			String eid = (String) req.getSession().getAttribute("eid");
			if (null != eid)
				user.setEid(eid);
		}
		if (null != user.getEid()) {
			user.setMobile(isMobile);
			for (Entry<User, AsyncContext> entry : ASYNC_CONTEXT_MAP.entrySet()) {
				//System.out.println(entry.getKey().getEid() + "---------"
						//+ user.getEid());
				if (entry.getKey().getEid().equals(user.getEid())
						&& entry.getKey().getMobile() == user.getMobile()) {
					ASYNC_CONTEXT_MAP.remove(entry.getKey());
				}
			}
			System.out.println("============" + ASYNC_CONTEXT_MAP.size()
					+ "==========");
			if (null != user) {
				System.out.println("****" + req.getHeader("USER-AGENT"));
				System.out.println("客户端注册成功！");
				ASYNC_CONTEXT_MAP.put(user, asyncContext);
			}
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
		boolean isMobile = GetHttpMessage.check(req.getHeader("USER-AGENT"));
		if (isMobile) {
			user.setEid(req.getParameter("eid"));
		} else {
			user.setEid((String) req.getSession().getAttribute("eid"));
		}
		user.setMobile(isMobile);
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
			System.out.println("get a new message!");
			String jsonStr = "{\'eid\': \'" + eid + "\',\'gname\':\'" + gname
					+ "\',\'arrivingDate\':\'" + arrivingDate + "\'}";
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
				// System.out.println("push message:"+m.toString());
				// System.out.println(m.toString());
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
					// System.out.println(message.getString("eid") +
					// "?????????");
					int id = Integer.parseInt(message.getString("eid"));
					String eid = message.getString("eid");
					if (id < 10)
						eid = "000" + id;
					else if (id < 100)
						eid = "00" + id;
					else
						eid = "0" + id;
					// System.out.println(eid+"+++++++++");
					// while(ASYNC_CONTEXT_MAP.isEmpty()){
					// //doNothing
					// }
					for (Entry<User, AsyncContext> entry : ASYNC_CONTEXT_MAP
							.entrySet()) {
						// try{
						HttpServletRequest req = (HttpServletRequest) entry
								.getValue().getRequest();
						if (eid.equals(entry.getKey().getEid())) {

							pushMessage(message, entry.getKey());
							ASYNC_CONTEXT_MAP.remove(entry.getKey());
							// Thread.sleep(100);
						}
						// }catch(Exception ex){
						// //这里是暂时的解决方法，连接释放后对象没有被成功从Map里移除，正式的解决方法可以重写User类的equals方法实现
						// //remove方法是使用equals方法判断键值是否相等的
						// }
					}
					Thread.sleep(1000);// 暂停100ms，停止的这段时间让用户有足够时间连接到服务器 1000 安卓

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
		// 判断请求方，确定返回对象格式json和jsonp
		if (GetHttpMessage.check(((HttpServletRequest) ac.getRequest())
				.getHeader("USER-AGENT"))) {
			// 返回json对象
			System.out.println(t);
			acWriter.write(t);
		} else {
			// 返回jsonp格式
			String jsonp = ac.getRequest().getParameter("callback");
			System.out.println(jsonp + "(" + t + ")");
			acWriter.write(jsonp + "(" + t + ")");
		}
		acWriter.flush();

		acWriter.close();

		ac.complete();

	}

}

class User {
	private boolean isMobile;
	private String eid;

	public boolean isMobile() {
		return isMobile;
	}

	public void setMobile(boolean isMobile) {
		this.isMobile = isMobile;
	}

	public boolean getMobile() {
		return isMobile;
	}

	public String getEid() {
		return eid;
	}

	public void setEid(String eid) {
		this.eid = eid;
	}

	// 重写equals方法
	@Override
	public boolean equals(Object obj) {
		if (this.eid.equals(((User) obj).eid))
			return true;
		else
			return false;
	}
}
