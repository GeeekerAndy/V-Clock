package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import database.Connect;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import objects.Guests;

public class GuestList {
	private Connect conn=new Connect();
	private Connection c;
	private PreparedStatement pstmt;
	//内部属性
	private JSONObject json;
	private ImageCoding imagecode;
	private Guests guest;
	
	public GuestList(){
		json=new JSONObject();
		imagecode=new ImageCoding();
		guest=new Guests();
		c=conn.con();
	}
	
	/**
	 * 判断输入信息合法性
	 * type表示需要判断合法性的属性是哪一个：判断gname,判断eid
	 */
	public boolean codeLegitimate(String type,String content){
		String allNumber="^[0-9_]+$";//纯数字正则表达式
		String existNumber="^[\u4e00-\u9fa5a-zA-Z·]+$";//姓名正则表达式
		Pattern ifAllNumber=Pattern.compile(allNumber);
		Pattern ifExistNumber=Pattern.compile(existNumber);
		if(type.equals("gname")){
			Matcher m1=ifExistNumber.matcher(content);
			boolean gnameBool=m1.matches();
			if(content.length()<=20&&gnameBool){
				return true;
			}
			else
				return false;
		}
		else if(type.equals("eid")){
			Matcher m2=ifAllNumber.matcher(content);
			boolean eidBool=m2.matches();
			if(content.length()==4&&eidBool){
				return true;
			}
			else
				return false;
		}
		else
			return false;

	}
	
	/**
	 * 添加嘉宾至邀请名单
	 * 返回值：0（添加成功），1（此嘉宾已在该工作人员邀请名单中），null（数据错误）
	 */
	public String addToGuestList(String gname,String eid){
		System.out.println(gname+"%%%"+eid);
		if(!codeLegitimate("gname",gname)||!codeLegitimate("eid",eid))
			return "2";
		//System.out.println("%%%%%%%%%%%");
		String sql="select * from guestlist where gname=? and eid=?";
		try {
			pstmt=c.prepareStatement(sql);
			pstmt.setString(1, gname);
			pstmt.setString(2, eid);
			conn.setRs(pstmt.executeQuery());
			if(conn.getRs().next()){
				return "1";
			}
			else{
				sql="insert into guestlist(gname,eid) "+"values(?,?)";	
				PreparedStatement pstmts=c.prepareStatement(sql);
				pstmts.setString(1, gname);
				pstmts.setString(2, eid);
				if(pstmts.executeUpdate()==1)
					return "0";
				else
					return "2";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "2";
	}
	
	/**
	 * 从邀请名单删除嘉宾
	 * 返回值：0（添加成功），1（此嘉宾已在该工作人员邀请名单中），null（数据错误）
	 */
	public String deleteFromGuestList(String gname,String eid){
		String sql="select * from guestlist where gname=? and eid=?";
		try {
			pstmt=c.prepareStatement(sql);
			pstmt.setString(1, gname);
			pstmt.setString(2, eid);
			conn.setRs(pstmt.executeQuery());
			if(conn.getRs().next()){
				sql="delete from guestlist where gname=? and eid=?";
				PreparedStatement pstmts=c.prepareStatement(sql);
				pstmts.setString(1, gname);
				pstmts.setString(2, eid);
				pstmts.executeUpdate();
				return "0";
			}
			else
				return "1";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "2";
	}
	
	/**
	 * 获得对应工作人员的邀请名单
	 * 返回值：返回值：jsonArray对象（该工作人员负责的嘉宾信息获取成功），null（数据错误）
	 */
	public JSONArray searchGuestList(String eid){
		if(!codeLegitimate("eid",eid))
			return null;
		String sql="select * from guestlist where eid=?";
		String gname,path,gphoto,gtel;
		JSONArray jsonArray=new JSONArray();
		JSONObject jsons=new JSONObject();
		ResultSet rs;
		try {
			pstmt=c.prepareStatement(sql);
			pstmt.setString(1, eid);
			conn.setRs(pstmt.executeQuery());
			while(conn.getRs().next()){
				gname=conn.getRs().getString("gname");
				sql="select * from guest where gname=?";
				PreparedStatement pstmts=c.prepareStatement(sql);
				pstmts.setString(1, gname);
				rs=pstmts.executeQuery();
				if(rs.next()){
					json=new JSONObject();
					gtel=rs.getString("gtel");
					path=rs.getString("gphoto");
					gphoto=imagecode.GetImageStr(path);
					json.put("gname", gname);
					json.put("gtel", gtel);
					json.put("gphoto", gphoto);
					jsonArray.add(json);
				}
			}
			//jsons.put("GuestList", jsonArray);
			//System.out.println("jsons:"+jsons.toString());
			return jsonArray;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

}
