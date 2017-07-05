package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;

import database.Connect;
import faceAPI.AddtoCrowd;
import faceAPI.RecognizeFace;

public class Guest {
	public Guest() {
		conn = new Connect();
		atc = new AddtoCrowd();
		gi = new GenerateImage();
		rf=new RecognizeFace();

	}

	public String createNewGuest(String gname, String gsex, String gtel,
			String gcompany, String gphoto, String reid) {
		// 返回值：0：新建嘉宾失败 1：新建嘉宾成功
		ResultSet rs;
		boolean success;
		// 根据嘉宾姓名，判断该嘉宾是否已存在
		String sql1 = "select * from guset where gname=" + gname;
		try {
			pstmt = c.prepareStatement(sql1);
			rs = pstmt.executeQuery();
			if (rs != null) {
				// 该嘉宾已存在
				return "0";
			} else {
				// 若该嘉宾不存在，在数据库中新建记录
				String sql2 = "insert into guest(gname,gsex,gtel,gcompany,gphoto,rgid) values(?,?,?,?,?,?)";
				pstmt = c.prepareStatement(sql2);
				pstmt.setString(1, gname);
				pstmt.setString(2, gsex);
				pstmt.setString(3, gtel);
				pstmt.setString(4, gcompany);
				pstmt.setString(5, gi.generateImg(gname, gphoto, 1));// 在服务器本地生成该嘉宾的照片,存储照片路径
				pstmt.setString(6, reid);
				success = pstmt.execute();
				if (success) {
					// 在crowd of guest中加入该嘉宾
					atc.add(gphoto, gname, 1);
					return "1";
				} else {
					return "0";
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}

	}

	public String modifyInfo(String gname, String information, String infoType) throws Exception {
		// // 返回值：0：修改信息失败 1：修改信息成功
		// infoType： 1 注册人员编号rgid 2嘉宾手机号 gtel 3嘉宾公司 gcompany 4嘉宾照片 gphoto
		boolean success;
		String sql="";
		sql = "update guest set "+infoType+"=? where gname= " + gname;
		pstmt=c.prepareStatement(sql);
		if("gphoto".equals(infoType)){
				pstmt.setString(1, gi.generateImg(gname, information, 1));//这里能够自动覆盖原路径图片
		}else{
				pstmt.setString(1, information);
		}
		success=pstmt.execute();
		if(success){
			if("gphoto".equals(infoType)){
				//先删除该people原绑定的face
				rf.removeOldFaceFromPerson(gname);
				//将新的face-id绑定至该people
				String face_id=rf.computeFaceID(information);
				rf.addNewFacetoPerson(gname, face_id);
			}
			return "1";
		}else{
			return "0";
		}	
	}
	public Connection getC() {
		return c;
	}
	private Connect conn;
	private Connection c;


	private PreparedStatement pstmt;

	private AddtoCrowd atc;
	private GenerateImage gi;
	private RecognizeFace rf;
}
