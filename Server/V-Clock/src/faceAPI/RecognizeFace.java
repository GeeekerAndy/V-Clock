package faceAPI;

import java.io.UnsupportedEncodingException;
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
	 * EyeKey API接口调用：
	 * 检测 checking/匹配match_compare match_identify/
	 * 人 people_create people_add people_remove/人群 crowd_create
	 */
	
	// 计算face_id
	public String computeFaceID(String imgStr) throws Exception {
		String result = "";
		//String imgStr = ic.GetImageStr(imgFilePath);
		// 设置参数
		String param = "app_id=" + URLEncoder.encode(conf.getAppID(), "utf-8")
				+ "&" + "app_key="
				+ URLEncoder.encode(conf.getAppKey(), "utf-8") + "&" + "img="
				+ URLEncoder.encode(imgStr, "utf-8");
		result = hgp.sendPost(conf.getUrl1(), param);
		return getFaceID(result);
	}

	// ͨ计算相似度
	public float compareOnewithAnother(String imgStr1, String imgFilePath2)
			throws Exception {
		String result;
		String fid1 = computeFaceID(imgStr1);
		String fid2 = computeFaceID( ic.GetImageStr(imgFilePath2));
		if(fid1!=null&&fid2!=null){
			// 设置参数
			String param = "app_id=" + URLEncoder.encode(conf.getAppID(), "utf-8")
					+ "&" + "app_key="
					+ URLEncoder.encode(conf.getAppKey(), "utf-8") + "&"
					+ "face_id1=" + URLEncoder.encode(fid1, "utf-8") + "&"
					+ "face_id2=" + URLEncoder.encode(fid2, "utf-8");
			result = hgp.sendGet(conf.getUrl2(), param);

			return getSimilarityBetweenTwoImages(result);
		}else{
			return 0;
		}
	}

	// 根据peopleType在对应的crowd加入一个新建的people
	public String createOnePeople(String face_id, String people_name,int peopleType)
			throws Exception {
		String result = "";
		String crowdName="";
		if(1==peopleType){
			crowdName=crowdName1;
		}else{
			crowdName=crowdName2;
		}
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

	//创建两个crowd分别存储guest和employee，只须执行一次（测试时会显示已存在）
	public String createCrowd() throws Exception {
		String result1 = "";
		String result2 = "";
		String param1 = "app_id=" + URLEncoder.encode(conf.getAppID(), "utf-8")
				+ "&" + "app_key="
				+ URLEncoder.encode(conf.getAppKey(), "utf-8") + "&"
				+ "crowd_name=" + URLEncoder.encode(crowdName1, "utf-8");
		String param2 = "app_id=" + URLEncoder.encode(conf.getAppID(), "utf-8")
				+ "&" + "app_key="
				+ URLEncoder.encode(conf.getAppKey(), "utf-8") + "&"
				+ "crowd_name=" + URLEncoder.encode(crowdName2, "utf-8");
		result1 = hgp.sendGet(conf.getUrl4(), param1);
		result2 =hgp.sendGet(conf.getUrl4(), param2);
		
		return result1+"\n"+result2;
	}

	// 根据peopleType在对应的crowd中找出与目标图片最相似的people-name
	public JSONObject identifyPeopleInCrowd(String face_id,int peopleType) throws Exception {
		String crowdName="";
		if(1==peopleType){
			crowdName=crowdName1;
		}else{
			crowdName=crowdName2;
		}
		String result = "";
		String param = "app_id=" + URLEncoder.encode(conf.getAppID(), "utf-8")
				+ "&" + "app_key="
				+ URLEncoder.encode(conf.getAppKey(), "utf-8") + "&"
				+ "face_id=" + URLEncoder.encode(face_id, "utf-8") + "&"
				+ "crowd_name=" + URLEncoder.encode(crowdName, "utf-8");
		result = hgp.sendGet(conf.getUrl6(), param);
        System.out.println(result);
		return getTheMostSimilar(result);
	}

	//向指定的people中增加一张照片
	public String addNewFacetoPerson(String people_name,String face_id) throws Exception {
		String result="";
		String param= "app_id=" + URLEncoder.encode(conf.getAppID(), "utf-8")
				+ "&" + "app_key="
				+ URLEncoder.encode(conf.getAppKey(), "utf-8") + "&"
				+ "people_name=" + URLEncoder.encode(people_name, "utf-8")+ "&"
				+ "face_id=" + URLEncoder.encode(face_id, "utf-8") ;
		result=hgp.sendGet(conf.getUrl7(), param);
		return result;
		
	}
	//删除指定的people绑定的所有face_id
	public String removeOldFaceFromPerson(String people_name) throws Exception{
		String result="";
		String param= "app_id=" + URLEncoder.encode(conf.getAppID(), "utf-8")
				+ "&" + "app_key="
				+ URLEncoder.encode(conf.getAppKey(), "utf-8") + "&"
				+ "people_name=" + URLEncoder.encode(people_name, "utf-8")+ "&"
				+ "face_id=" + URLEncoder.encode("all", "utf-8") ;
		result=hgp.sendGet(conf.getUrl8(), param);
		return result;
	}
	/*
	 * 对EyeKey 接口返回数据进行处理ֵ
	 */
	
	// 提取face_id
	public String getFaceID(String result)  {	
		JSONObject object = JSONObject.fromObject(result);
		if(object.getString("res_code").equals("1067")){
			return null;
		}else{
			String faceContent = object.getString("face");
			JSONArray faceArray = JSONArray.fromObject(faceContent);
			JSONObject face0 = JSONObject.fromObject(faceArray.get(0).toString());
			return face0.getString("face_id");
		}
	}

	// 提取similarity
	public float getSimilarityBetweenTwoImages(String result)  {
		float similarity = 0;
		JSONObject object = JSONObject.fromObject(result);
		similarity = Float.parseFloat(object.getString("similarity"));
		return similarity;
	}
    
	//提取最相似的person信息
	public JSONObject getTheMostSimilar(String result) {
		JSONObject object1 = JSONObject.fromObject(result);
		String faceContent = object1.getString("face");
		JSONArray array1 = JSONArray.fromObject(faceContent);
		JSONObject object2 = JSONObject.fromObject(array1.get(0).toString());
		String resultContent = object2.getString("result");
		//System.out.println(resultContent+"****");
		JSONArray array2 = JSONArray.fromObject(resultContent);
		if(!array2.isEmpty()){	
			JSONObject object3 = JSONObject.fromObject(array2.get(0).toString());
			return object3;
		}else{
			return null;
		}
		// System.out.println(resultContent);
		
		//name = object3.getString("person_name");
		
	}

	private ImageCoding ic;
	private HttpGetandPost hgp;
	private Configuration conf;
	private String crowdName1 = "wuhuabaren_guest";
	private String crowdName2= "wuhuabaren_employee";


//	public static void main(String[] args) throws Exception {
//		String imgFilePath1 = "C:\\Users\\dell\\Desktop\\4.jpg";
//		RecognizeFace rf = new RecognizeFace();
//		//System.out.println(rf.createCrowd());
//		// System.out.println(rf.computeFaceID(imgFilePath1));
//		 CheckingPhoto cp=new CheckingPhoto();
//	     ImageCoding ic=new ImageCoding();
//	     String s=ic.GetImageStr(imgFilePath1);
//	     System.out.println(rf.computeFaceID(s));
//		//String imgFilePath2 = "D:\\2.jpg";
//		//System.out.println(rf.compareOnewithAnother(imgFilePath1, imgFilePath2));
//		// String fid = rf.getFaceID(imgFilePath1);
//		// System.out.println(rf.createOnePeople(fid, "w3"));
//		// System.out.println(rf.identifyPeopleInCrowd(fid));
//		// System.out.println(rf.createOneCrowd());
//
//	}


}
