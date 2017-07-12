package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import objects.*;

import net.sf.json.*;

import database.Connect;
import faceAPI.*;

public class Guest {
	private Connect conn;
	private Connection c;
	private PreparedStatement pstmt;
    //对象和接口
	private AddtoCrowd atc;
	private GenerateImage gi;
	private RecognizeFace rf;
	//内部属性
	private JSONObject json;
	private ImageCoding imagecode;
	private CheckingPhoto checking;
	private Guests guest;
	
	public Connection getC() {
		return c;
	}
	public Guest() {
		conn = new Connect();
		c=conn.con();
		atc = new AddtoCrowd();
		gi = new GenerateImage();
		rf=new RecognizeFace();
		json=new JSONObject();
		imagecode=new ImageCoding();
		checking=new CheckingPhoto();
		guest=new Guests();

	}

	/**
	 * 判断嘉宾和工作人员信息合法性
	 * type表示需要判断合法性的属性是哪一个：判断gname,判断gtel,判断reid
	 */
	public boolean codeLegitimate(String type,String content){
		String allNumber="^[0-9_]+$";//纯数字正则表达式
		String existNumber=".*\\d+.*";//包含数字正则表达式
		Pattern ifAllNumber=Pattern.compile(allNumber);
		Pattern ifExistNumber=Pattern.compile(existNumber);
		if(type.equals("gtel")){
			Matcher m1=ifAllNumber.matcher(content);
			boolean etelbool=m1.matches();
			if(content.length()<12&&etelbool){
				return true;
			}
			else
				return false;
		}
		else if(type.equals("gname")){
			Matcher m2=ifExistNumber.matcher(content);
			boolean enameBool=m2.matches();
			if(content.length()<20&&!enameBool){
				return true;
			}
			else
				return false;
		}
		else if(type.equals("regid")){
			Matcher m3=ifAllNumber.matcher(content);
			boolean eidBool=m3.matches();
			if(content.length()<5&&eidBool){
				return true;
			}
			else
				return false;
		}
		else if(type.equals("gphoto")||type.equals("gsex")||type.equals("gcompany"))
			return true;
		else
			return false;

	}
	
	
	public String createNewGuest(String gname, String gsex, String gtel,
			String gcompany, String gphoto, String regid) {
		// 返回值： 0(新建嘉宾成功),1(该嘉宾已存在),2(数据错误)
		if(!codeLegitimate("gname",gname)||!codeLegitimate("gtel",gtel)||!codeLegitimate("regid",regid))
			return "2";
		ResultSet rs;
		boolean success;
		// 根据嘉宾姓名和照片，判断该嘉宾是否已存在
		String sql1 = "select * from guest where gname=?";
		try {
			//String gphototip=checking.doesThePersonExist(gphoto,1);
			pstmt = c.prepareStatement(sql1);
			pstmt.setString(1, gname);
			rs = pstmt.executeQuery();
			//if (rs.next()||(!gphototip.equals("0")&&!gphototip.equals("1"))) {
			if (rs.next()) {
				// 该嘉宾已存在
				System.out.println("guest exist!!");
				return "1";
			} else {
				// 若该嘉宾不存在，在数据库中新建记录
				String sql2 = "insert into guest(gname,gsex,gtel,gcompany,gphoto,regid) values(?,?,?,?,?,?)";
				PreparedStatement pstmts= c.prepareStatement(sql2);
				pstmts.setString(1, gname);
				pstmts.setString(2, gsex);
				pstmts.setString(3, gtel);
				pstmts.setString(4, gcompany);
				pstmts.setString(5, gi.generateImg(gname, gphoto, 1));// 在服务器本地生成该嘉宾的照片,存储照片路径
				pstmts.setString(6, regid);
				if(pstmts.executeUpdate()==1)
					success=true;
				else
					success=false;
				System.out.println("success:"+success);
//				// 在crowd of guest中加入该嘉宾
//				atc.add(gphoto, gname, 1);
//				return "0";
				if (success) {
					// 在crowd of guest中加入该嘉宾
					atc.add(gphoto, gname, 1);
					System.out.println("crowd of guest:"+gname);
					return "0";
				} else {
					return "2";
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			return "2";
		}

	}

	public String modifyInfo(String gname, String information, String infoType) throws Exception {

		// // 返回值： 0(修改信息成功),1(修改信息失败)
		// infoType： 1 注册人员编号regid 2嘉宾手机号 gtel 3嘉宾公司 gcompany 4嘉宾照片 gphoto
		if(!codeLegitimate("gname",gname)||!codeLegitimate(infoType,information))
			return "1";
		boolean success;
		String sql="";
		sql = "update guest set "+infoType+"=? where gname=? ";
		pstmt=c.prepareStatement(sql);
		if("gphoto".equals(infoType)){
				pstmt.setString(1, gi.generateImg(gname, information, 1));//这里能够自动覆盖原路径图片
				pstmt.setString(2, gname);
		}else{
				pstmt.setString(1, information);
				pstmt.setString(2, gname);
		}
		if(pstmt.executeUpdate()==1)
			success=true;
		else
			success=false;
		if(success){
			if("gphoto".equals(infoType)){
				//先删除该people原绑定的face
				rf.removeOldFaceFromPerson(gname);
				//将新的face-id绑定至该people
				String face_id=rf.computeFaceID(information);
				rf.addNewFacetoPerson(gname, face_id);
			}
			return "0";
		}else{
			return "1";
		}	
	}

	/**
	 * 搜索嘉宾，包括开始时没有检索信息的全部嘉宾搜索，和异步嘉宾姓名搜索的部分嘉宾搜索
	 * gnamePart：搜索值，若是全部嘉宾搜索则为空字符串（注意不是gamePart=null而是gamePart=""）
	 * 返回值：包含所需所有嘉宾的信息的jsonArray（检索成功）；null（数据错误）
	 */
	public JSONArray searchGuest(String gnamePart){
		if(!codeLegitimate("gname",gnamePart))
			return null;
		String sql;
		String gname,path,gphoto;
		JSONArray jsonArray=new JSONArray();
		//JSONObject jsons=new JSONObject();
		sql="select gname,gphoto from Guest where gname regexp '^"+gnamePart+"'";
		try {
			pstmt=c.prepareStatement(sql);
			conn.setRs(pstmt.executeQuery());
			while(conn.getRs().next()){
				json=new JSONObject();
				gname=conn.getRs().getString("gname");
				path=conn.getRs().getString("gphoto");
				gphoto=imagecode.GetImageStr(path);
				json.put("gname", gname);
				json.put("gphoto", gphoto);
				jsonArray.add(json);
			}
			//jsons.put("Guest", jsonArray);
			return jsonArray;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 搜索某个嘉宾的信息
	 * 返回值：包含所需嘉宾的信息的json（搜索完成）；null（数据错误）
	 */
	public JSONObject searchOneGuest(String gname){
		if(!codeLegitimate("gname",gname))
			return null;
		String sql="select * from Guest where gname=?";
		String temp,path;
		int gLength=guest.gmessage.length;
		json.put("tip", "0");
		try {
			pstmt=c.prepareStatement(sql);
			pstmt.setString(1, gname);
			conn.setRs(pstmt.executeQuery());
			if(conn.getRs().next()){
				for(int i=0;i<gLength;i++){
					if(i!=gLength-2){
						temp=conn.getRs().getString(guest.gmessage[i]);
						json.put(guest.gmessage[i], temp);
					}
					else{
						path=conn.getRs().getString(guest.gmessage[i]);
						String gphoto=imagecode.GetImageStr(path);
						json.put(guest.gmessage[i], gphoto);
					}
				}			
			}
			return json;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		json=new JSONObject();
		json.put("tip", "2");
		return json;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Guest g=new Guest();
		//e.insert("张三", "男", "18247965198", "D:\\1.jpg");
		g.searchGuest("J");

	}
	

}
