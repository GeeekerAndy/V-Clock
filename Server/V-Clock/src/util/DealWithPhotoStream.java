package util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import faceAPI.CheckingPhoto;
/*
 * 处理嘉宾到访时摄像头传输的帧
 */
public class DealWithPhotoStream {
    public String dealWithPhotoStream(String photoStr) throws Exception{
    	//startTimer();

//    	currentDate.setTime(new Date());
    	
    	//System.out.println(currentDate.getTime()+"*****"+lastDate.getTime());
//    	long timeOne=lastDate.getTimeInMillis();
//		long timeTwo=currentDate.getTimeInMillis();
//		long seconds=(timeTwo-timeOne)/(1000);//转化s
//    	Calendar calendar =Calendar.getInstance();
//    	int seconds=calendar.get(Calendar.SECOND);
//		System.out.println("****"+seconds+"****");
    	//System.out.println("拿到一张图片！");
    	String gname="";
    	gname=cp.doesThePersonExist(photoStr, 1);
    	//gname="张三";
    	//System.out.println("----"+gname+"--------"+lastGuest);
    	if("0".endsWith(gname)){
    		gname="0";//该嘉宾不存在
    		change=false;
    	}else if(lastGuest.equals("gname")){
    		change=false;
//    		if(seconds%10!=0){
//    			gname="1";//该嘉宾消息已被推送
//        		change=false;
//    		}else{
//    			change=true;//再次推送该嘉宾信息
//    		}
    	}else if(!lastGuest.equals(gname)){
    		lastGuest=gname;
    		//lastDate.setTime(new Date());
    		change=true;
    		//startTimer();
    	}else{
    		change=false;	
    		
    	}
    	if(change){
    		startTimer();
    	}
    	return gname;
    }
    public void startTimer(){
       //Timer timer =new Timer(); 
       TimerTask task=new TimerTask(){
    	   public void run(){
    		   lastGuest="";
    	   }
       };
       timer.schedule(task, 100000L,100000L);
//    	Runnable runnable= new Runnable(){
//    		public void run(){
//    			lastGuest="";
//    		}
//    	};
//    	ScheduledExecutorService service=Executors.newSingleThreadScheduledExecutor();
//    	service.scheduleAtFixedRate(runnable, 10, 120, TimeUnit.SECONDS);
    }
    public boolean getChange(){
    	return change;
    }
    //private String gname="";
    
//    private Calendar lastDate=Calendar.getInstance();
//    private Calendar currentDate=Calendar.getInstance();
    private Timer timer = new Timer();
    private String lastGuest="";
    private boolean change=false;
    private CheckingPhoto cp=new CheckingPhoto();
}
