package faceAPI;

public class Configuration {
	public String getUrl1() {
		return url1;
	}

	public String getUrl2() {
		return url2;
	}

	public String getUrl3() {
		return url3;
	}

	public String getUrl4() {
		return url4;
	}

	public String getUrl5() {
		return url5;
	}

	public String getUrl6() {
		return url6;
	}

	public String getUrl7() {
		return url7;
	}

	public String getUrl8() {
		return url8;
	}

	public String getAppID() {
		return appID;
	}

	public String getAppKey() {
		return appKey;
	}

	// 检测图片(Image)中的人脸(Face)的位置和相应的人脸属性
	private String url1 = "http://api.eyekey.com/face/Check/checking";
	// 计算两个Face的相似度，分值百分制
	private String url2 = "http://api.eyekey.com/face/Match/match_compare";
	// 创建一个People
	private String url3 = "http://api.eyekey.com/People/people_create";
	// 创建一个crowd
	private String url4 = "http://api.eyekey.com/Crowd/crowd_create";
	// 将一个people加入到一个crowd中
	private String url5 = "http://api.eyekey.com/Crowd/crowd_add";
	// 指定一个人脸Face列表，在一个Crowd中查询出人脸列表中最相似的People
	private String url6 = "http://api.eyekey.com/face/Match/match_identify";
	// 将一个或一组Face加入到一个People中。注意， 同一个Face只能被加入到一个People中
	private String url7 = "http://api.eyekey.com/People/people_add";
	// 删除People中的一个或多个Face
	private String url8 = "http://api.eyekey.com/People/people_remove";

	private String appID = "efb84878045e4ee79994f1ed232ff17f";
	private String appKey = "6f8d0d8879d2402aadc804e30810269e";
}
