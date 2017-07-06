package util;

import faceAPI.CheckingPhoto;
/*
 * 处理嘉宾到访时摄像头传输的帧
 */
public class DealWithPhotoStream {
    public String dealWithPhotoStream(String photoStr) throws Exception{
    	String gname="";
    	gname=cp.doesThePersonExist(photoStr, 1);
    	if("0".endsWith(gname))
    		gname="0";//该嘉宾不存在
    	else if(lastGuest.equals("gname"))
    		gname="1";//该嘉宾消息已被推送
    	else
    		lastGuest=gname;
    	return gname;
    }
    private String lastGuest="";
    private CheckingPhoto cp=new CheckingPhoto();
}
