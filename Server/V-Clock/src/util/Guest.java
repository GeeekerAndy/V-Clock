package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import database.Connect;
import faceAPI.AddtoCrowd;

public class Guest {
    private Connect conn;
    private Connection c;
	private PreparedStatement pstmt;
	//对象和接口
	private AddtoCrowd atc;
	private GenerateImage gi;
	
	public Guest(){
		conn=new Connect();
		atc=new AddtoCrowd();
		gi=new GenerateImage();
		
	}
	public String createNewGuest(String gname,String gsex,String gtel,String gcompany,String gphoto,String regid){
		//返回值：0：新建嘉宾失败 1：新建嘉宾成功
		ResultSet rs;
		boolean success;
		//1.根据嘉宾姓名，判断该嘉宾是否已存在	
		String sql1="select * from guset where gname="+gname;
		try{
			pstmt=c.prepareStatement(sql1);
			rs=pstmt.executeQuery();
			if(rs!=null){
				//该嘉宾已存在
				return "0";
			}else{
				//若该嘉宾不存在，在数据库中新建记录
				String sql2="insert into guest(gname,gsex,gtel,gcompany,gphoto,rgid) values(?,?,?,?,?,?)";
				pstmt=c.prepareStatement(sql2);
				pstmt.setString(1, gname);
				pstmt.setString(2, gsex);
				pstmt.setString(3, gtel);
				pstmt.setString(4, gcompany);
				pstmt.setString(5, gphoto);
				pstmt.setString(6, regid);
				success=pstmt.execute();
				if(success){
					//在crowd of guest中加入该嘉宾
					atc.add(gphoto, gname, 1);
					//在服务器本地生成该嘉宾的照片
					gi.generateImg(gname, gphoto, 1);		
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "2";
		//  判断嘉宾信息的合法性  gsex（男，女）gtel（11位）
		//3.在crowd of guest中加入该嘉宾（基于2）
		//4.在服务器本地生成该嘉宾的照片（基于2）
		
	}
    public String modifyInfo(String gname,String information,int infoType){
    	//infoType： 1 注册人员编号rgid 2嘉宾手机号 gtel 3嘉宾公司 gcompany 4嘉宾照片 gphoto 
    	switch(infoType){
    	case 1:
    		break;
    	
    	}
		return "2";
    }

}
