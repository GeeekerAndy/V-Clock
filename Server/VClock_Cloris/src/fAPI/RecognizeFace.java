package fAPI;

import java.net.URLEncoder;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RecognizeFace {
	public RecognizeFace() {
		ic = new ImageCoding();
		hgp = new HttpGetandPost();
		conf = new Configuration();
	}
    //计算图片信息
	public String computeFaceInfo(String imgFilePath) throws Exception {
		String imgStr = ic.GetImageStr(imgFilePath);
		// 设置参数
		String param = "app_id=" + URLEncoder.encode(conf.getAppID(), "utf-8")
				+ "&" + "app_key="
				+ URLEncoder.encode(conf.getAppKey(), "utf-8") + "&" + "img="
				+ URLEncoder.encode(imgStr, "utf-8");
		return hgp.sendPost(conf.getUrl1(), param);
	}

	// 提取face_id
	public String getFaceID(String imgFilePath) throws Exception {
		JSONObject object = JSONObject.fromObject(computeFaceInfo(imgFilePath));
		String faceContent = object.getString("face");
		JSONArray faceArray = JSONArray.fromObject(faceContent);
		JSONObject face0 = JSONObject.fromObject(faceArray.get(0).toString());
		return face0.getString("face_id");
	}
    public String compareOnewithAnother(String imgFilePath1,String imgFilePath2) throws Exception{
    	String result;
    	//计算两张图片的face_id
    	String fid1=getFaceID(imgFilePath1);
    	String fid2=getFaceID(imgFilePath2);
    	//设置参数
    	String param= "app_id=" + URLEncoder.encode(conf.getAppID(), "utf-8")
				+ "&" + "app_key="
				+ URLEncoder.encode(conf.getAppKey(), "utf-8") + "&" + "face_id1="
				+ URLEncoder.encode(fid1, "utf-8")+"&"+"face_id2="+URLEncoder.encode(fid2, "utf-8");
    	result=hgp.sendGet(conf.getUrl2(), param);   	
    	return result;	
    }
    public float getSimilarityBetweenTwoImages(String imgFilePath1,String imgFilePath2) throws Exception{
    	float similarity=0;
    	JSONObject object=JSONObject.fromObject(compareOnewithAnother(imgFilePath1,imgFilePath2));
    	similarity=Float.parseFloat(object.getString("similarity"));
    	return similarity;
    }
	private ImageCoding ic;
	private HttpGetandPost hgp;
	private Configuration conf;

	public static void main(String[] args) throws Exception {
		String imgFilePath1 = "D:\\1.jpg";
		RecognizeFace rf = new RecognizeFace();
		//System.out.println(rf.getFaceID(imgFilePath1));
		
		String imgFilePath2="D:\\2.jpg";
		System.out.println(rf.getSimilarityBetweenTwoImages(imgFilePath1, imgFilePath2));

	}

}
