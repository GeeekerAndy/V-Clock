package util;


public class GenerateImage {
	public GenerateImage(){
		ic = new ImageCoding();
	}
	/*
	 * imgName: guest:name employee:eid
	 * imgType: 1:guest 2:employee
	 */
	public String generateImg(String imgName,String imgStr,int imgType) throws Exception{
		String imgFilePath="";
		if(1==imgType){
			imgFilePath=guestAddress+"\\"+imgName+".jpg";
		}else{
			imgFilePath=employeeAddress+"\\"+imgName+".jpg";
		}
	    ic.generateImg(imgStr, imgFilePath);
	    return imgFilePath;
	}
	private ImageCoding ic;
	private String guestAddress="D:\\V_Clockphoto\\guest";
	private String employeeAddress="D:\\V_Clockphoto\\employee";

}
