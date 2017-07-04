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
		if (similarity >= 80)
			return true;
		else
			return false;

	}

	/*
	 * 判断当前获取的图片所属的人是否已存在记录中，并返回该人的姓名
	 */
	public String doesThePersonExist(String photoStr,int peopleType) throws Exception {
		String name = "";
		String face_id = rf.computeFaceID(photoStr);
		JSONObject similarPeople = rf.identifyPeopleInCrowd(face_id,peopleType);
		name = similarPeople.getString("person_name");
		float similarity = Float.parseFloat(similarPeople
				.getString("similarity"));
		if (similarity >= 80)
			return name;
		else
			return null;
	}

	private RecognizeFace rf;
	// public static void main(String[] args) throws Exception {
	// String imgFilePath1 = "D:\\1.jpg";
	// String imgFilePath2 = "D:\\2.jpg";
	// CheckingPhoto cp=new CheckingPhoto();
	// System.out.println(cp.doesThePersonExist(imgFilePath1));
	//
	// }
}
