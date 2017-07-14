package faceAPI;

import net.sf.json.JSONObject;

public class CheckingPhoto {
	public CheckingPhoto() {
		rf = new RecognizeFace();
	}

	/*
	 * 判断当前获取的图片是否与对应数据库中存储图片同属于一个人
	 */
	public boolean isTheSamePerson(String photoStr1, String photoFilePath2)
			throws Exception {
		float similarity = rf.compareOnewithAnother(photoStr1,
				photoFilePath2);
		System.out.println(similarity);
		if (similarity >= 80)
			return true;
		else
			return false;

	}

	/*
	 * 判断当前获取的图片所属的人是否已存在记录中，并返回该人的姓名
	 * 0:people不存在 1:图片无效，无法识别人脸 name：该人已存在，返回其姓名
	 */
	public String doesThePersonExist(String photoStr,int peopleType) throws Exception {
		String name = "";
		
		String face_id = rf.computeFaceID(photoStr);
		if(face_id!=null){
			JSONObject similarPeople = rf.identifyPeopleInCrowd(face_id,peopleType);
			//System.out.println(similarPeople);
			if(similarPeople!=null){
				name = similarPeople.getString("person_name");
				float similarity = Float.parseFloat(similarPeople
						.getString("similarity"));
				if (similarity >= 80)
					return name;
				else
					return "0";
			}else{
				return "0";
			}
		}else{
			return "1";
		}
		
		
	}

	private RecognizeFace rf;
//	 public static void main(String[] args) throws Exception {
//	 String imgFilePath1 = "D:\\1.jpg";
//	 String imgFilePath2 = "D:\\2.jpg";
//	 CheckingPhoto cp=new CheckingPhoto();
//	 ImageCoding ic=new ImageCoding();
//	 String s=ic.GetImageStr(imgFilePath1);
//	 System.out.println(cp.doesThePersonExist(s,1));
//	
//	 }
}
