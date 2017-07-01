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

	public String computeFaceInfo(String imgFilePath) throws Exception {
		String imgStr = ic.GetImageStr(imgFilePath);
		// 设置参数
		String param = "app_id=" + URLEncoder.encode(conf.getAppID(), "utf-8")
				+ "&" + "app_key="
				+ URLEncoder.encode(conf.getAppKey(), "utf-8") + "&" + "img="
				+ URLEncoder.encode(imgStr, "utf-8");
		return hgp.sendPost(conf.getUrl(), param);
	}

	// 提取face_id
	public String getFaceID(String imgFilePath) throws Exception {
		JSONObject object = JSONObject.fromObject(computeFaceInfo(imgFilePath));
		String faceContent = object.getString("face");
		JSONArray faceArray = JSONArray.fromObject(faceContent);
		JSONObject face0 = JSONObject.fromObject(faceArray.get(0).toString());
		return face0.getString("face_id");
	}
    
	private ImageCoding ic;
	private HttpGetandPost hgp;
	private Configuration conf;

	public static void main(String[] args) throws Exception {
		String imgFilePath = "D:\\1.jpg";
		RecognizeFace rf = new RecognizeFace();
		System.out.println(rf.getFaceID(imgFilePath));

	}

}
