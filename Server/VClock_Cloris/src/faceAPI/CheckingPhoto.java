package faceAPI;

import net.sf.json.JSONObject;

public class CheckingPhoto {
	public CheckingPhoto() {
		rf = new RecognizeFace();
	}

	/*
	 * 输入参数:两张照片的字符数组字符串 返回值：两张照片同属于一个人 true 两张照片不属于同一个人 false
	 */
	public boolean isTheSamePerson(String photoFilePath1, String photoFilePath2)
			throws Exception {
		float similarity = rf.compareOnewithAnother(photoFilePath1,
				photoFilePath2);
		if (similarity >= 80)
			return true;
		else
			return false;

	}

	/*
	 * 输入参数：照片的存储位置 返回值：若照片能在图库找到一张同属与一个人的照片 person_name 若不能找到 null
	 */
	public String doesThePersonExist(String photoFilePath) throws Exception {
		String name = "";
		String face_id = rf.computeFaceID(photoFilePath);
		JSONObject similarPeople = rf.identifyPeopleInCrowd(face_id);
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
