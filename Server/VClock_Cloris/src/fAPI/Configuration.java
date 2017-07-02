package fAPI;

public class Configuration {
	public String getUrl1() {
		return url1;
	}
	public String getUrl2() {
		return url2;
	}
	public String getAppID() {
		return appID;
	}

	public String getAppKey() {
		return appKey;
	}
    // 检测图片(Image)中的人脸(Face)的位置和相应的人脸属性，包括多人脸检测
	private String url1 = "http://api.eyekey.com/face/Check/checking";
	//计算两个Face的相似度，分值百分制
	private String url2="http://api.eyekey.com/face/Match/match_compare";
	// 创建people （face_id）
	private String url3="http://api.eyekey.com/People/people_create";
	
	private String appID = "efb84878045e4ee79994f1ed232ff17f";
	private String appKey = "6f8d0d8879d2402aadc804e30810269e";
}
