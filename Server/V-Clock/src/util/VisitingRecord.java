package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import database.Connect;

public class VisitingRecord {
	//数据库操作
	private Connect conn;
	private Connection c;
	private PreparedStatement pstmt;
	//对象和接口
	private ImageCoding imagecode;
	//内部属性
	private JSONObject json;
	public VisitingRecord(){
		conn = new Connect();
		c=conn.con();
		imagecode=new ImageCoding();
		json=new JSONObject();
	}
	
	/**
	 * 判断输入信息合法性
	 * type表示需要判断合法性的属性是哪一个：判断eid
	 */
	public boolean codeLegitimate(String type,String content){
		String allNumber="^[0-9_]+$";//纯数字正则表达式
		Pattern ifAllNumber=Pattern.compile(allNumber);
		if(type.equals("eid")){
			Matcher m=ifAllNumber.matcher(content);
			boolean eidBool=m.matches();
			if(content.length()==4&&eidBool){
				return true;
			}
			else
				return false;
		}
		else if(type.equals("page")){
			Matcher m=ifAllNumber.matcher(content);
			boolean pageBool=m.matches();
			if(pageBool){
				return true;
			}
			else
				return false;
		}
		else
			return false;

	}
	
	/**
	 * 计算到访记录页数
	 * 返回值(int):页数（计算成功），-1（数据错误）
	 */
	public int pageCount(String eid){
		if(!codeLegitimate("eid",eid))
			return -1;
		String sql="select count(*) from Visitingrecord where eid=?";
		try {
			pstmt=c.prepareStatement(sql);
			pstmt.setString(1, eid);
			conn.setRs(pstmt.executeQuery());
			if(conn.getRs().next()){
				int count=conn.getRs().getInt("count(*)");
				int extra=count%18;
				if(extra!=0)
					return (count/18)+1;
				else
					return count/18;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * 展示到访记录
	 * 返回值：jsonArray对象（到访记录获取成功），null（数据错误）
	 */
	public JSONArray displayVisitingRecord(String eid,String page){
		if(!codeLegitimate("eid",eid)||!codeLegitimate("page",page))
			return null;
		int pagenumber=Integer.parseInt(page);
		int startNumber=(pagenumber-1)*18;
		System.out.println("startNumber:"+startNumber);
		String sql="select * from Visitingrecord where eid=? order by arrivingdate desc limit ?,18";
		String gname,path,gphoto,arrivingdate;
		JSONArray jsonArray=new JSONArray();
		Date temp;
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			pstmt=c.prepareStatement(sql);
			pstmt.setString(1, eid);
			pstmt.setInt(2, startNumber);
			conn.setRs(pstmt.executeQuery());
			ResultSet result;
			JSONObject jsons=new JSONObject();
			while(conn.getRs().next()){
				gname=conn.getRs().getString("gname");
				eid=conn.getRs().getString("eid");
				//System.out.println("gname:"+gname);
				temp=new Date(conn.getRs().getTimestamp("arrivingdate").getTime());
				arrivingdate=df.format(temp);
				sql="select gphoto from guest where gname=?";
				PreparedStatement pstmts=c.prepareStatement(sql);
				pstmts.setString(1, gname);
				result=pstmts.executeQuery();
				if(result.next()){
					json=new JSONObject();
					path=result.getString("gphoto");
					gphoto=imagecode.GetImageStr(path);
					json.put("gname", gname);
					json.put("arrivingdate", arrivingdate);
					json.put("eid", eid);
					json.put("gphoto", gphoto);
					//System.out.println(json.toString());
					jsonArray.add(json);
				}
			}	
			//jsons.put("VisitingRecord", jsonArray);
			//System.out.println("jsons:"+jsons);
			return jsonArray;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		VisitingRecord vr=new VisitingRecord();
		//vr.displayVisitingRecord("0000");
		System.out.println(vr.pageCount("0001"));
		

	}

}
