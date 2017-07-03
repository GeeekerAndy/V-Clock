package faceAPI;

import java.net.URLEncoder;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RecognizeFace {
	public RecognizeFace() {
		ic = new ImageCoding();
		hgp = new HttpGetandPost();
		conf = new Configuration();
	}

	/*
	 * EyeKey 人脸识别接口调用： 检测checking/匹配match_compare match_identify/
	 * 人people_create/人群 crowd_create
	 */
	
	// 计算图片的face_id
	public String computeFaceID(String imgFilePath) throws Exception {
		String result = "";
		String imgStr = ic.GetImageStr(imgFilePath);
		// 设置参数
		String param = "app_id=" + URLEncoder.encode(conf.getAppID(), "utf-8")
				+ "&" + "app_key="
				+ URLEncoder.encode(conf.getAppKey(), "utf-8") + "&" + "img="
				+ URLEncoder.encode(imgStr, "utf-8");
		result = hgp.sendPost(conf.getUrl1(), param);
		return getFaceID(result);
	}

	// 通过两张图片的face_id，计算他们的similarity
	public float compareOnewithAnother(String imgFilePath1, String imgFilePath2)
			throws Exception {
		String result;
		String fid1 = computeFaceID(imgFilePath1);
		String fid2 = computeFaceID(imgFilePath2);
		// 设置参数
		String param = "app_id=" + URLEncoder.encode(conf.getAppID(), "utf-8")
				+ "&" + "app_key="
				+ URLEncoder.encode(conf.getAppKey(), "utf-8") + "&"
				+ "face_id1=" + URLEncoder.encode(fid1, "utf-8") + "&"
				+ "face_id2=" + URLEncoder.encode(fid2, "utf-8");
		result = hgp.sendGet(conf.getUrl2(), param);

		return getSimilarityBetweenTwoImages(result);
	}

	// 创建一个people对象,自动加入crowd“wuhuabaren”
	public String createOnePeople(String face_id, String people_name)
			throws Exception {
		String result = "";
		String param = "app_id=" + URLEncoder.encode(conf.getAppID(), "utf-8")
				+ "&" + "app_key="
				+ URLEncoder.encode(conf.getAppKey(), "utf-8") + "&"
				+ "people_name=" + URLEncoder.encode(people_name, "utf-8")
				+ "&" + "face_id=" + URLEncoder.encode(face_id, "utf-8") + "&"
				+ "crowd_name=" + URLEncoder.encode(crowdName, "utf-8");
		// System.out.println(param);
		result = hgp.sendGet(conf.getUrl3(), param);
		return result;
	}

	// 创建一个crowd对象(已创建)
	// 重复执行应返回:{"added_people":0,"message":"crowd_name 已存??,"res_code":"1037"}
	public String createOneCrowd() throws Exception {
		String result = "";
		String param = "app_id=" + URLEncoder.encode(conf.getAppID(), "utf-8")
				+ "&" + "app_key="
				+ URLEncoder.encode(conf.getAppKey(), "utf-8") + "&"
				+ "crowd_name=" + URLEncoder.encode(crowdName, "utf-8");

		result = hgp.sendGet(conf.getUrl4(), param);
		return result;
	}

	// 在crowd“wuhuabaren”中找出与目标图片最相似的person信息
	public JSONObject identifyPeopleInCrowd(String face_id) throws Exception {
		String result = "";
		String param = "app_id=" + URLEncoder.encode(conf.getAppID(), "utf-8")
				+ "&" + "app_key="
				+ URLEncoder.encode(conf.getAppKey(), "utf-8") + "&"
				+ "face_id=" + URLEncoder.encode(face_id, "utf-8") + "&"
				+ "crowd_name=" + URLEncoder.encode(crowdName, "utf-8");
		result = hgp.sendGet(conf.getUrl6(), param);

		return getTheMostSimilar(result);
	}

	
	/*
	 * 对EyeKey 人脸识别接口返回数据进行处理，找出目标值
	 */
	
	// 提取一张图片的face_id
	public String getFaceID(String result)  {
		JSONObject object = JSONObject.fromObject(result);
		String faceContent = object.getString("face");
		JSONArray faceArray = JSONArray.fromObject(faceContent);
		JSONObject face0 = JSONObject.fromObject(faceArray.get(0).toString());
		return face0.getString("face_id");
	}

	// 提取两张图片的similarity
	public float getSimilarityBetweenTwoImages(String result)  {
		float similarity = 0;
		JSONObject object = JSONObject.fromObject(result);
		similarity = Float.parseFloat(object.getString("similarity"));
		return similarity;
	}
    
	//提取与目标照片最相似的person信息
	public JSONObject getTheMostSimilar(String result) {
		JSONObject object1 = JSONObject.fromObject(result);
		String faceContent = object1.getString("face");
		JSONArray array1 = JSONArray.fromObject(faceContent);
		JSONObject object2 = JSONObject.fromObject(array1.get(0).toString());
		String resultContent = object2.getString("result");
		// System.out.println(resultContent);
		JSONArray array2 = JSONArray.fromObject(resultContent);
		JSONObject object3 = JSONObject.fromObject(array2.get(0).toString());
		//name = object3.getString("person_name");
		return object3;
	}

	private ImageCoding ic;
	private HttpGetandPost hgp;
	private Configuration conf;
	private String crowdName = "wuhuabaren";

//	public static void main(String[] args) throws Exception {
//		String imgFilePath1 = "D:\\1.jpg";
//		RecognizeFace rf = new RecognizeFace();
//		// System.out.println(rf.computeFaceID(imgFilePath1));
//
//		String imgFilePath2 = "D:\\2.jpg";
//		System.out
//				.println(rf.compareOnewithAnother(imgFilePath1, imgFilePath2));
//		// String fid = rf.getFaceID(imgFilePath1);
//		// System.out.println(rf.createOnePeople(fid, "w3"));
//		// System.out.println(rf.identifyPeopleInCrowd(fid));
//		// System.out.println(rf.createOneCrowd());
//
//	}

}
