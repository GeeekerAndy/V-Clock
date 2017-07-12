package util;

import java.io.ByteArrayInputStream;


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.alibaba.fastjson.JSON;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;

import database.Connect;

import util.GenerateImage;


import net.sf.json.JSONObject;

import faceAPI.*;
public class Employee implements objects.Employees{
	//数据库连接相关属性
	private Connect conn=new Connect();
	private Connection c;
	private PreparedStatement pstmt;
    //对象和接口
	//private Objects.Employees employee;
	private CheckingPhoto checking;
	private ImageCoding imagecode;
	private GenerateImage gi;
	private AddtoCrowd atc;
	private RecognizeFace recognize;
	//内部属性
	private static int eidnumber=1;//工作人员创建eid
	private JSONObject json;
	
	public Connection getC() {
		return c;
	}
	public void setC(Connection c) {
		this.c = c;
	}
	public Employee(){ 
		c=conn.con();
		checking=new CheckingPhoto();
		imagecode=new ImageCoding();
		json=new JSONObject();
		gi=new GenerateImage();
		atc=new AddtoCrowd();
		recognize=new RecognizeFace();
		getLastEid();
	}
	
	/**
	 * 获取数据库中的最大eid作为赋予新id的基础
	 */
	public void getLastEid(){
		String sql="select count(eid) from employee";
		try {
			pstmt=c.prepareStatement(sql);
			conn.setRs(pstmt.executeQuery());
			if(conn.getRs().next()){
				eidnumber=conn.getRs().getInt("count(eid)")+1;
			}
			else
				eidnumber=1;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	/**
	 * 判断工作人员信息合法性
	 * type表示需要判断合法性的属性是哪一个：判断etel,判断ename,判断eid
	 */
	public boolean codeLegitimate(String type,String content){
		if(content==null)
			return false;
		String allNumber="^[0-9_]+$";//纯数字正则表达式
		String existNumber=".*\\d+.*";//包含数字正则表达式
		Pattern ifAllNumber=Pattern.compile(allNumber);
		Pattern ifExistNumber=Pattern.compile(existNumber);
		if(type.equals("etel")){
			Matcher m1=ifAllNumber.matcher(content);
			boolean etelbool=m1.matches();
			if(content.length()<12&&etelbool){
				return true;
			}
			else
				return false;
		}
		else if(type.equals("ename")){
			Matcher m2=ifExistNumber.matcher(content);
			boolean enameBool=m2.matches();
			if(content.length()<20&&!enameBool){
				return true;
			}
			else
				return false;
		}
		else if(type.equals("eid")){
			Matcher m3=ifAllNumber.matcher(content);
			boolean eidBool=m3.matches();
			if(content.length()<5&&eidBool){
				return true;
			}
			else
				return false;
		}
		else if(type.equals("ephoto")||type.equals("esex"))
			return true;
		else
			return false;

	}

	
	/**
	 * 查询数据库中是否存在某手机号
	 * 返回值意义：0（有此手机号），1（无此手机号），2（数据错误）
	 */
	public String checkphoNumber(String etel){
		String sql="select  *  from Employee where etel=?";
		try {
			pstmt=c.prepareStatement(sql);
			pstmt.setString(1, etel);			
			conn.setRs(pstmt.executeQuery());
			if(conn.getRs().next())
				return "0";						
			else
				return "1";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "2";
	}
	
	/**
	 * 查询数据库中是否存在某手机号账户
	 * 返回值意义：0（有此手机号账户），1（无此手机号账户），2（数据错误），3（手机号与人脸不匹配）
	 * @throws Exception 
	 */
	public String checkuser(String etel,String ephoto) throws Exception{
		String sql="select  ephoto  from Employee where etel=?";
		String path;
		try {
			pstmt=c.prepareStatement(sql);
			pstmt.setString(1, etel);
			conn.setRs(pstmt.executeQuery());
			if(conn.getRs().next()){
				path=conn.getRs().getString("ephoto");
				System.out.println(path+"1");
				boolean bool=checking.isTheSamePerson(ephoto,path);
				if(bool){
					return "0";
				}
				else
					return "3";
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
	 * 工作人员登录
	 * 返回值意义：工作人员编号（登录成功）,"1"（无此工作人员）,"2"（数据错误），3（手机号与人脸不匹配）
	 * 此处将工作人员的信息赋值了，建议得到这个工作人员编号存进session
	 * @throws Exception 
	 */
	public String login(String etel,String ephoto) throws Exception{
		//employee=new Objects.Employees();
		if(!codeLegitimate("etel",etel))
			return "2";
		String tip=checkuser(etel,ephoto);
		//System.out.println("tip:"+tip);
		String eid;
		if(tip.equals("1"))
			return "1";
		else if(tip.equals("2"))
			return "2";
		else if(tip.equals("3"))
			return "3";
		else{
		try {
			pstmt=c.prepareStatement("select  *  from Employee where etel=?");
			pstmt.setString(1, etel);
			conn.setRs(pstmt.executeQuery());
			if(conn.getRs().next()){
				eid=conn.getRs().getString("eid");
				return eid;
			}
			else
				return "1";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		return "2";

	}

	/**
	 * 工作人员注册
	 * 返回值意义：0（注册成功）.1（此手机号或工作人员已存在），2（数据错误）
	 * @throws Exception 
	 */
	public String register(String ename,String esex,String etel,String ephoto) throws Exception{
		if(!codeLegitimate("etel",etel)||!codeLegitimate("ename",ename)){
			System.out.println("codeLegitimate fail!!!");
			return "2";
		}
		String tip=checking.doesThePersonExist(ephoto,2);
		if(tip.equals("1"))
			return "2";
		else if(!tip.equals("0"))
			return "1";
		tip=checkphoNumber(etel);
		String eid;
		if(tip.equals("0"))
			return "1";
		if(tip.equals("2")){
			System.out.println("checkphoNumber fail!!!");
			return "2";
		}
		else{
			String sql="insert into Employee(eid,ename,esex,etel,ephoto) "+"values(?,?,?,?,?)";	
			eid=(eidnumber++)+"";
			while(eid.length()<4){
					eid="0"+eid;
			}
			try {
				//在本地服务器上存储照片 并返回存储路径
				String imgFilePath=gi.generateImg(eid, ephoto, 2);
				//将employee加入crowd
				pstmt=c.prepareStatement(sql);
				pstmt.setString(1, eid);
				pstmt.setString(2, ename);
				pstmt.setString(3, esex);
				pstmt.setString(4, etel);
				pstmt.setString(5,imgFilePath);
				pstmt.executeUpdate();
				boolean atcAddBool=atc.add(ephoto, eid,2);
				if(!atcAddBool)
					return "2";
				return "0";
			} catch (SQLException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
		System.out.println("end fail!!!");
		return "2";
	}
	
	/**
	 * 工作人员信息修改
	 * 输入信息：type=ename（更改姓名）\esex（更改性别）\etel（更改手机号）\ephoto（更改照片）
	 * 返回值意义：0（修改成功），1（此修改不被允许），2（数据错误）
	 * @throws Exception 
	 */
	public String modifyEmployeeInfo(String eid,String type,String content) throws Exception{
		try{
			if(!codeLegitimate(type,content))
				return "2";
			String path;
		if(type.equals("ephoto")){
			//先判断要更新的照片是不是属于该工作人员本人
			String sql="select * from Employee where eid=?";
			pstmt=c.prepareStatement(sql);
			pstmt.setString(1, eid);	
			conn.setRs(pstmt.executeQuery());
			if(conn.getRs().next()){
				path=conn.getRs().getString("ephoto");
				boolean bool=checking.isTheSamePerson(content,path);
				if(!bool)
					return "1";
			}
			else
				return "1";
			//在本地服务器上存储照片 并返回存储路径（由于每个工作人员对应一个本地路径，更改照片不需要再更新数据库）
			String imgFilePath=gi.generateImg(eid, content, 2);
			//将employee的新脸加入crowd
			recognize.removeOldFaceFromPerson(eid);
			String face_id=recognize.computeFaceID(content);
			if(face_id==null)//判断输入的新图片是否合法
				return "2";
			recognize.addNewFacetoPerson(eid, face_id);
			return "0";
		}
		else if(type.equals("etel")){
			pstmt=c.prepareStatement("select * from Employee where etel=? and eid<>?");
			pstmt.setString(1, content);
			pstmt.setString(2, eid);
			conn.setRs(pstmt.executeQuery());
			if(conn.getRs().next()){
				return "1";
			}
			PreparedStatement pstmts=c.prepareStatement("update Employee set etel=? where eid=?");
			pstmts.setString(1, content);
			pstmts.setString(2, eid);
			pstmts.executeUpdate();
			return "0";
		}
		else{
			pstmt=c.prepareStatement("update Employee set "+type+"=? where eid=?");
			pstmt.setString(1, content);
			pstmt.setString(2, eid);
			pstmt.executeUpdate();
			return "0";
		}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "2";
	}



	/**
	 * 工作人员信息查看
	 * 
	 * 返回值意义：json对象（正常查看），null（数据错误)
	 */
	public JSONObject displayEmployeeInfo(String eid){
		if(!codeLegitimate("eid",eid)){
			System.out.println("codeLegitimate fail!!!");
			return null;
		}
		String sql="select * from Employee where eid=?";
		String temp,path;
		json.put("tip", "0");
		try {
			pstmt=c.prepareStatement(sql);
			pstmt.setString(1, eid);
			conn.setRs(pstmt.executeQuery());
			if(conn.getRs().next()){
				for(int i=0;i<emessage.length-1;i++){
					temp=conn.getRs().getString(emessage[i]);
					json.put(emessage[i], temp);
				}
				path=conn.getRs().getString("ephoto");
				String ephoto=imagecode.GetImageStr(path);
				//System.out.println("ephoto:"+ephoto);
				json.put(emessage[emessage.length-1], ephoto);
				return json;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		json=new JSONObject();
		json.put("tip", "2");
		return json;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Employee e=new Employee();
		//e.insert("张三", "男", "18247965198", "D:\\1.jpg");
		System.out.println(e.eidnumber);

	}

}
