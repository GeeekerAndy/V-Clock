package util;

import faceAPI.CheckingPhoto;
/*
 * 处理嘉宾到访时摄像头传输的帧
 */
public class DealWithPhotoStream {
    public String dealWithPhotoStream(String photoStr) throws Exception{
    	System.out.println("拿到一张图片！");
    	String gname="";
    	gname=cp.doesThePersonExist(photoStr, 1);
    	System.out.println("----"+gname+"--------");
    	if("0".endsWith(gname)){
    		gname="0";//该嘉宾不存在
    		change=false;
    	}else if(lastGuest.equals("gname")){
    		gname="1";//该嘉宾消息已被推送
    		change=false;
    	}else{
    		lastGuest=gname;
    		change=true;
    	}
    	return gname;
    }
    public boolean getChange(){
    	return change;
    }
    private String lastGuest="";
    private boolean change=false;
    private CheckingPhoto cp=new CheckingPhoto();
}
