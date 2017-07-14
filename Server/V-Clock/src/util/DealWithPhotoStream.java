package util;



import java.util.Timer;
import java.util.TimerTask;

import faceAPI.CheckingPhoto;
/*
 * 处理嘉宾到访时摄像头传输的帧
 */
public class DealWithPhotoStream {
    public String dealWithPhotoStream(String photoStr) throws Exception{
    	String gname="";
    	gname=cp.doesThePersonExist(photoStr, 1);
    	if("0".endsWith(gname)){
    		gname="0";//该嘉宾不存在
    		change=false;
    	}else if(lastGuest.equals("gname")){
    		change=false;//该嘉宾已被推送
    	}else if(!lastGuest.equals(gname)){
    		lastGuest=gname;//gname失效，该嘉宾再次被推送
    		change=true;
    	}else{
    		change=false;	
    	}
    	if(change){//开启计时器
    		startTimer();
    	}
    	return gname;
    }
    public void startTimer(){//定时重置gname
       TimerTask task=new TimerTask(){
    	   public void run(){
    		   lastGuest="";
    	   }
       };
       timer.schedule(task, 100000L,100000L);
    }
    public boolean getChange(){
    	return change;
    }

    private Timer timer = new Timer();
    private String lastGuest="";
    private boolean change=false;
    private CheckingPhoto cp=new CheckingPhoto();
}
