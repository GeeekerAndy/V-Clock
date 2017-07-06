package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
			if(content.length()<5&&eidBool){
				return true;
			}
			else
				return false;
		}
		else
			return false;

	}
	
	/**
	 * 展示到访记录
	 * 返回值：jsonArray对象（到访记录获取成功），null（数据错误）
	 */
	public JSONArray displayVisitingRecord(String eid){
		if(!codeLegitimate("eid",eid))
			return null;
		String sql="select * from Visitingrecord eid=? order by arrivingdate desc";
		String gname,path,gphoto,arrivingdate;
		JSONArray jsonArray=new JSONArray();
		Date temp;
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			pstmt=c.prepareStatement(sql);
			pstmt.setString(1, eid);
			conn.setRs(pstmt.executeQuery());
			while(conn.getRs().next()){
				gname=conn.getRs().getString("gname");
				temp=new Date(conn.getRs().getTimestamp("arrivingdate").getTime());
				arrivingdate=df.format(temp);
				sql="select gphoto from guest where game=?";
				pstmt=c.prepareStatement(sql);
				pstmt.setString(1, gname);
				conn.setRs(pstmt.executeQuery());
				if(conn.getRs().next()){
					json=new JSONObject();
					path=conn.getRs().getString("gphoto");
					gphoto=imagecode.GetImageStr(path);
					json.put("gname", gname);
					json.put("arrivingdate", arrivingdate);
					json.put("gphoto", gphoto);
					jsonArray.add(json);
				}
			}
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

	}

}
