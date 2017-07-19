$("#gpicV").click(function(){
	$("#doc-modal-1").modal({ width: '600px' });
	setLoadPic($("#gpicV"));
});

$("#mpic").click(function(){
	$("#doc-modal-1").modal({ width: '600px' });
	setLoadPic($("#mpic"));
});

$("#mgpic").click(function(){
	$("#doc-modal-1").modal({ width: '600px' });
	setLoadPic($("#mgpic"));
});

$("#add-submit").click(function() {
	var gname = $("#gnameV").val();
	if(!gname) {
		alert("请输入姓名");
		return;
	}
	var gsex = $("#gsexV").val();
	if(!gsex || (gsex != "男" && gsex != "女")) {
		alert("请选择性别");
		return;
	}
	var gtel = $("#gtelV").val();
	if(!gtel) {
		alert("请输入手机号");
		return;
	}
	var gcompany = $("#gcompanyV").val();
	if(!gcompany) {
		alert("请输入嘉宾单位");
		return;
	}

	var imgFile = $("#gpicV").attr("src");
	if(imgFile.indexOf(",")>=0) {
			compressImage(imgFile, 0.5, 0.7, function(gphoto) {
				var position = gphoto.indexOf(",");
				var gphotoV = gphoto.substring(position + 1);
				
				console.log(gphoto)

				$.ajax({
					type: "post",
					url: "http://121.250.222.75:8080/V-Clock/servlet/WCreateNewGuestServlet",
					async: true,
					xhrFields: {                       
						withCredentials: true                   
					},
					dataType: "json",
					data: {
						gname: gname,
						gsex: gsex,
						gtel: gtel,
						gcompany: gcompany,
						gphoto: gphotoV,
						//regid: '0002',
					},
					success: function(result) { //result是返回值
						if(result == 0) {
							alert("新建成功");
							$.ajax({
								type: "post",
								url:"http://121.250.222.75:8080/V-Clock/servlet/WAddtoGuestListServlet",
								data:{
									gname: gname,
									//eid: '0002'
								},
								xhrFields: {			                       
									withCredentials: true			                   
								},
								dataType:"json",
								success:function(result) {
									if(result == 0) {
										alert("添加成功");
									}
									if(result == 1) {
										alert("此嘉宾已在该工作人员的邀请名单中");
									}
									if(result == 2) {
										alert("数据错误");
									}
									$('#addModal').modal('hide');
									window.location.reload();
								}
							});
						} else if(result == 1) {
							alert("此嘉宾已存在");
						} else if(result == 2) {
							alert("数据错误");
						}
					}
				});
			});
	} else {
		alert("请选择头像");
	}
});

$("#add").click(function() {
	$('#addModal-btn').click();
});

$("#addtoListconfirm-submit").click(function() {
	var deleteGname=$("#mgname").val();
	if(("data:image/jpeg;base64," +_gphoto)!=$("#mpic").attr("src") || _gsex!=$("#msex").val() || _gtel!=$("#mtel").val() || _gcompany!=$("#mcompany").val()){
		$('#modifyconfirm-btn').click();
	}else{
		$.ajax({
			type: "post",
			url:"http://121.250.222.75:8080/V-Clock/servlet/WAddtoGuestListServlet", 
			data:{
				//eid:"0002",
				gname: $("#mname").val()
			},
			xhrFields: {			                       
				withCredentials: true			                   
			},
			success:function(result) {
				console.log(result);
	
				if(result == 0) {
					alert("添加成功");
				} else if(result == 1) {
					alert("该嘉宾已在您的邀请名单当中");
				} else if(result == 2) {
					alert("数据错误")
				}
				$('#searchModal').modal("hide");
				//这里刷新
				window.location.reload();
			},
			dataType:"json"
		});
	}
})

var enableModify = function (){
	$("#mpic").attr("disabled", false);
	$("#mtel").attr("readonly", false);
	$("#mcompany").attr("readonly", false);
	$("#msex").attr("disabled", false);
	
	$("#modify").html("保存修改");
	$("#modify").unbind("click").click(saveModify);
};

var saveModify = function(){
	$("#mpic").attr("disabled", true);
	$("#mtel").attr("readonly", true);
	$("#mcompany").attr("readonly", true);
	$("#msex").attr("disabled", true);
	
	$("#modify").html("正在保存...");
	$("#modify").attr("disabled", true);
	
	var callback = function(result) {
				if(result == 0) {	
					_gsex = $("#msex").val();
					_gtel = $("#mtel").val();
					_gcompany = $("#mcompany").val();
					_gphoto = data.gphoto;
					alert("修改成功");
				} else {
					$("#msex").val(_gsex);
					$("#mtel").val(_gtel);
					$("#mcompany").val(_gcompany);
					$("#mpic").attr("src", "data:image/jpeg;base64," + _gphoto);
					alert("修改失败");
				}
		$("#modify").html("修改嘉宾信息");
		$("#modify").attr("disabled", false);
		$("#modify").unbind("click").click(enableModify);
	};

	var data = {
		tip:"",
		//regid:"0002",
		gname: $("#mname").val()
	};
	if(_gsex != $("#msex").val()) {
		data.tip += "gsex;";
		data.gsex = $("#msex").val();
	}
	if(_gtel != $("#mtel").val()) {
		data.tip += "gtel;";
		data.gtel = $("#mtel").val();
	}
	if(_gcompany != $("#mcompany").val()) {
		data.tip += "gcompany;";
		data.gcompany = $("#mcompany").val();
	}
	var imgFile = $("#mpic").attr("src");
	if(("data:image/jpeg;base64," + _gphoto) != imgFile) {
		data.tip += "gphoto;";

		var gphoto = imgFile;

			var position = gphoto.indexOf(",");
			var gphotoV = gphoto.substring(position + 1);
			data.gphoto = gphotoV;
			$.ajax({
				type: "post",
				url:"http://121.250.222.75:8080/V-Clock/servlet/WModifyGuestInfoServlet", //这里修改嘉宾信息的路径
				data:data,
				xhrFields: {			                       
					withCredentials: true			                   
				},
				success:callback,
				
			});
	} else {
		$.ajax({
			type: "post",
			url:"http://121.250.222.75:8080/V-Clock/servlet/WModifyGuestInfoServlet", //这里修改嘉宾信息的路径
			data:data,
			xhrFields: {			                       
				withCredentials: true			                   
			},
			success:callback,
			
		});
	}
};

$("#modify").click(enableModify);

var _gphoto, _gsex, _gtel, _gcompany;

$('#nomodify-submit').click(function() {
	$.ajax({
		type: "post",
		url:"http://121.250.222.75:8080/V-Clock/servlet/WAddtoGuestListServlet", 
		data:{
			//eid:"0002",
			gname: $("#mname").val()
		},
		xhrFields: {			                       
			withCredentials: true			                   
		},
		success:function(result) {
			console.log(result);

			if(result == 0) {
				alert("添加成功");
			} else if(result == 1) {
				alert("该嘉宾已在您的邀请名单当中");
			} else if(result == 2) {
				alert("数据错误")
			}
			$("#modifyconfirm").modal("hide");
			$('#searchModal').modal("hide");
			//这里刷新
			window.location.reload();
		},
		dataType:"json"
	});
})

$('#modify-submit').click(function() {
	var callback = function(result) {
		$.ajax({
			type: "post",
			url:"http://121.250.222.75:8080/V-Clock/servlet/WAddtoGuestListServlet", 
			data:{
				//eid:"0002",
				gname: $("#mname").val()
			},
			xhrFields: {			                       
				withCredentials: true			                   
			},
			success:function(result) {

				if(result == 0) {
					alert("修改并添加成功");
				} else if(result == 1) {
					alert("该嘉宾已在您的邀请名单当中");
				} else if(result == 2) {
					alert("数据错误")
				}

				$("#modifyconfirm").modal("hide");
				$('#searchModal').modal("hide");
				window.location.reload();

			},
			dataType:"json"
		});
	};

	var data = {
		tip:"",
		//regid:"0002",
		gname: $("#mname").val()
	};
	if(_gsex != $("#msex").val()) {
		data.tip += "gsex;";
		data.gsex = $("#msex").val();
	}
	if(_gtel != $("#mtel").val()) {
		data.tip += "gtel;";
		data.gtel = $("#mtel").val();
	}
	if(_gcompany != $("#mcompany").val()) {
		data.tip += "gcompany;";
		data.gcompany = $("#mcompany").val();
	}
	var imgFile = $("#mpic").attr("src");
	if(("data:image/jpeg;base64," + _gphoto) != imgFile) {
		data.tip += "gphoto;";

		var gphoto = imgFile;

			var position = gphoto.indexOf(",");
			var gphotoV = gphoto.substring(position + 1);
			data.gphoto = gphotoV;
			$.ajax({
				type: "post",
				url:"http://121.250.222.75:8080/V-Clock/servlet/WModifyGuestInfoServlet", //这里修改嘉宾信息的路径
				data:data,
				xhrFields: {			                       
					withCredentials: true			                   
				},
				success:callback,
				dataType:"json"
			});
	} else {
		$.ajax({
			type: "post",
			url:"http://121.250.222.75:8080/V-Clock/servlet/WModifyGuestInfoServlet", //这里修改嘉宾信息的路径
			data:data,
			xhrFields: {			                       
				withCredentials: true			                   
			},
			success:callback,
			dataType:"json"
		});
	}
});

//var _deleteGname;
//
//function deleteGuest(gname) {
//	_deleteGname = gname;
//	$("#delete").modal('show');
//}

function modifyGuestListItem(gname) {
	$("#mpic").attr("disabled", true);
	$("#mname").attr("readonly", true);
	$("#mtel").attr("readonly", true);
	$("#mcompany").attr("readonly", true);
	$("#msex").attr("disabled", true);
	$("#modify").html("修改嘉宾信息");
	$("#modify").attr("disabled", false);
	$("#modify").unbind("click").click(enableModify);
	$('#searchModal-btn').click();
	$.ajax({
		type: "post",
		url:"http://121.250.222.75:8080/V-Clock/servlet/WSearchGuestServlet", 
		data:{
			tip: '2',
			gname: gname,
		},
		xhrFields: {			                       
			withCredentials: true			                   
		},
		success:function(result) {
			_gphoto = result.gphoto;
			_gsex = result.gsex;
			_gtel = result.gtel;
			_gcompany = result.gcompany;

			$("#mpic").attr("src", "data:image/jpeg;base64," + result.gphoto);
			$("#msex").val(result.gsex);
			$("#mname").val(result.gname);
			$("#mtel").val(result.gtel);
			$("#mcompany").val(result.gcompany);

		}, 
		dataType:"json"
	});
}

$("#search-submit").click(function() {
	var gname = $("#gsearch").val();
	if(!gname) {
		alert("请输入嘉宾姓名");
	} else {
		showSearchModal(gname);
	}
});

$("#gsearch").on("input", function() {
	$("#search-list").empty();

	var gname = $("#gsearch").val();
	if(!gname) {

		return;
	} else {

		$.ajax({
			type: "post",
			url:"http://121.250.222.75:8080/V-Clock/servlet/WSearchGuestServlet", 
			data:{
				tip: '1',
				gname: gname,
			},
			xhrFields: {			                       
				withCredentials: true			                   
			},
			success:function(result) {
				if($("#gsearch").val() != gname)
					return;
				var obj = result.Guest;
				if(obj.length == undefined || obj.length == 0) {
					return;
				}
				var search_list = $("#search-list");
				$.each(obj, function(infoIndex, info) {
					var li = $(
						'<li class="list-group-item " style="font-size: 1.2em;">' +
						"<img onclick='showSearchModal(\"" + info.gname + "\")' style='height: 40px; width: 40px;border-radius:50% ;margin:5px auto;margin-right: 30px;margin-left: 20px;' src='data:image/jpeg;base64," + info.gphoto + "'>" +
						info.gname +
						"</li>");
					li.appendTo(search_list);
				});

			}, 
			dataType:"json"
		});
	}
});

function showSearchModal(gname) {
	$("#mpic").attr("disabled", true);
	$("#mname").attr("readonly", true);
	$("#mtel").attr("readonly", true);
	$("#mcompany").attr("readonly", true);
	$("#msex").attr("disabled", true);
	$("#modify").html("修改嘉宾信息");
	$("#modify").attr("disabled", false);
	$("#modify").unbind("click").click(enableModify);

	$.ajax({
		type: "post",
		url:"http://121.250.222.75:8080/V-Clock/servlet/WSearchGuestServlet", 
		data:{
			tip: '2',
			gname: gname,
		},
		xhrFields: {			                       
			withCredentials: true			                   
		},
		success:function(result) {
			_gphoto = result.gphoto;
			_gsex = result.gsex;
			_gtel = result.gtel;
			_gcompany = result.gcompany;
			if(!_gphoto && !_gsex && !_gtel && !_gcompany) {
				alert("无此嘉宾，请直接添加嘉宾");
			} else {
				$('#searchModal-btn').click();
				$("#mpic").attr("src", "data:image/jpeg;base64," + result.gphoto);
				$("#msex").val(result.gsex);
				$("#mname").val(result.gname);
				$("#mtel").val(result.gtel);
				$("#mcompany").val(result.gcompany);
			}

		}, 
		dataType:"json"
	});

}

var MGphoto;
var MGsex;
var MGtel;
var MGcompany;

function showMyGuestModal(gname) {

	$.ajax({
		type: "post",
		url:"http://121.250.222.75:8080/V-Clock/servlet/WSearchGuestServlet", 
		data:{
			tip: '2',
			gname: gname,
		},
		xhrFields: {			                       
			withCredentials: true			                   
		},
		success:function(result) {
			MGphoto= result.gphoto;
			MGsex = result.gsex;
			MGtel = result.gtel;
			MGcompany = result.gcompany;

			$('#modMyguest-btn').click();
				$("#mgpic").attr("src", "data:image/jpeg;base64," + result.gphoto);
				$("#mgsex").val(result.gsex);
				$("#mgname").val(result.gname);
				$("#mgtel").val(result.gtel);
				$("#mgcompany").val(result.gcompany);
			
			

		}, 
		dataType:"json"
	});

}

$("#modMyguest-sumbit").click(function(){
	var callback = function(result){
				if(result == 0) {
					alert("修改成功");
					window.location.reload();
				} else if(result == 1) {
					alert("您未修改信息或修改失败");
					$("#mgpic").attr("src",("data:image/jpeg;base64," + MGphoto));
					$("#mgsex").val(MGsex);
					$("#mgtel").val(MGtel);
					$("#mgcompany").val(MGcompany);
				} 
	

	};

	var data = {
		tip:"",
		//regid:"0002",
		gname: $("#mgname").val()
	};
	if(MGsex != $("#mgsex").val()) {
		data.tip += "gsex;";
		data.gsex = $("#mgsex").val();
	}
	if(MGtel != $("#mgtel").val()) {
		data.tip += "gtel;";
		data.gtel = $("#mgtel").val();
	}
	if(MGcompany != $("#mgcompany").val()) {
		data.tip += "gcompany;";
		data.gcompany = $("#mgcompany").val();
	}
	var imgFile = $("#mgpic").attr("src");
	if(("data:image/jpeg;base64," + MGphoto) != imgFile) {
		data.tip += "gphoto;";

		var gphoto = imgFile;

			var position = gphoto.indexOf(",");
			var gphotoV = gphoto.substring(position + 1);
			data.gphoto = gphotoV;
			$.ajax({
				type: "post",
				url:"http://121.250.222.75:8080/V-Clock/servlet/WModifyGuestInfoServlet", //这里修改嘉宾信息的路径
				data:data,
				xhrFields: {			                       
					withCredentials: true			                   
				},
				success:callback,
				dataType:"json"
			});
	} else {
		$.ajax({
			type: "post",
			url:"http://121.250.222.75:8080/V-Clock/servlet/WModifyGuestInfoServlet", //这里修改嘉宾信息的路径
			data:data,
			xhrFields: {			                       
				withCredentials: true			                   
			},
			success:callback,
			dataType:"json"
		});
	}
});


$("#deleteMyguest-submit").click(function() {
	var deleteGname=$("#mgname").val();
	if(("data:image/jpeg;base64," + MGphoto)!=$("#mgpic").attr("src") || MGsex!=$("#mgsex").val() || MGtel!=$("#mgtel").val() || MGcompany!=$("#mgcompany").val()){
		$("#delete-confirm-btn").click();
	}else{
		$.ajax({
			type: "post",
			url:"http://121.250.222.75:8080/V-Clock/servlet/WDeleteFromGuestListServlet", 
			data:{
				gname: deleteGname,
				//eid:'0002',				
			},
			xhrFields: {			                       
				withCredentials: true			                   
			},
			success:function(result) {
				if(result == 0) {
					alert("删除成功");
				} else if(result == 1) {
					alert("此嘉宾不在邀请名单中");
				} else if(result == 2) {
					alert("数据错误");
				}
				$('#delete').modal("hide");
				window.location.reload();
	
			},
			dataType:"json"
		});
	}
});

$("#nomodifyDelete-submit").click(function(){
	var deleteGname=$("#mgname").val();
		$.ajax({
			type: "post",
			url:"http://121.250.222.75:8080/V-Clock/servlet/WDeleteFromGuestListServlet", 
			data:{
				gname: deleteGname,
				//eid:'0002',				
			},
			xhrFields: {			                       
				withCredentials: true			                   
			},
			success:function(result) {
				if(result == 0) {
					alert("删除成功");
				} else if(result == 1) {
					alert("此嘉宾不在邀请名单中");
				} else if(result == 2) {
					alert("数据错误");
				}
				$('#delete').modal("hide");
				window.location.reload();
	
			},
			dataType:"json"
		});
});

$("#modifyDelete-submit").click(function(){
	var callback = function(result){
		var deleteGname=$("#mgname").val();
		$.ajax({
			type: "post",
			url:"http://121.250.222.75:8080/V-Clock/servlet/WDeleteFromGuestListServlet", 
			data:{
				gname: deleteGname,
				//eid:'0002',				
			},
			xhrFields: {			                       
				withCredentials: true			                   
			},
			success:function(result) {
				console.log(result);
				if(result == 0) {
					alert("删除成功");
				} else if(result == 1) {
					alert("此嘉宾不在邀请名单中");
				} else if(result == 2) {
					alert("数据错误");
				}
				$('#delete').modal("hide");
				window.location.reload();
	
			},
			dataType:"json"
		});
	};
	
	var data = {
		tip:"",
		//regid:"0002",
		gname: $("#mgname").val()
	};
	if(MGsex != $("#mgsex").val()) {
		data.tip += "gsex;";
		data.gsex = $("#mgsex").val();
	}
	if(MGtel != $("#mgtel").val()) {
		data.tip += "gtel;";
		data.gtel = $("#mgtel").val();
	}
	if(MGcompany != $("#mgcompany").val()) {
		data.tip += "gcompany;";
		data.gcompany = $("#mgcompany").val();
	}
	var imgFile = $("#mgpic").attr("src");
	if(("data:image/jpeg;base64," + MGphoto) != imgFile) {
		data.tip += "gphoto;";

		var gphoto = imgFile;

			var position = gphoto.indexOf(",");
			var gphotoV = gphoto.substring(position + 1);
			data.gphoto = gphotoV;
			$.ajax({
				type: "post",
				url:"http://121.250.222.75:8080/V-Clock/servlet/WModifyGuestInfoServlet", //这里修改嘉宾信息的路径
				data:data,
				xhrFields: {			                       
					withCredentials: true			                   
				},
				success:callback,
				dataType:"json"
			});
	} else {
		$.ajax({
			type: "post",
			url:"http://121.250.222.75:8080/V-Clock/servlet/WModifyGuestInfoServlet", //这里修改嘉宾信息的路径
			data:data,
			xhrFields: {			                       
				withCredentials: true			                   
			},
			success:callback,
			dataType:"json"
		});
	}
});

//建立一個可存取到該file的url
function getObjectURL(file) {
	var url = null;
	if(window.createObjectURL != undefined) { // basic
		url = window.createObjectURL(file);
	} else if(window.URL != undefined) { // mozilla(firefox)
		url = window.URL.createObjectURL(file);
	} else if(window.webkitURL != undefined) { // webkit or chrome
		url = window.webkitURL.createObjectURL(file);
	}
	return url;
}