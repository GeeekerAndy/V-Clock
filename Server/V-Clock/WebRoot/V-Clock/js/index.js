$(function() {
	$("#pic-select").click(function(){
		$("#inputImage").click();
	});
	
        $("#pic").click(function(){
			$("#doc-modal-1").modal({ width: '600px' });
			setLoadPic($("#pic"));
        });

	$("#gpic").click(function() {
		$("#gphoto").click(); //隐藏了input:file样式后，点击头像就可以本地上传
		$("#gphoto").on("change", function() {
			var objUrl = getObjectURL(this.files[0]); //获取图片的路径，该路径不是图片在本地的路径
			if(objUrl) {
				$("#gpic").attr("src", objUrl); //将图片路径存入src中，显示出图片
			}
		});
	});

	$("#register-submit").click(function() {
		var ename = $("#ename").val();
		if(!ename) {
			alert("请输入姓名");
			return;
		}
		var esex = $("#esex").val();
		if(!esex || (esex != "男" && esex != "女")) {
			alert("请选择性别");
			return;
		}
		var etel = $("#etel").val();
		if(!etel) {
			alert("请输入手机号");
			return;
		}

		var imgData = $("#pic").attr("src");
		if(imgData.indexOf(",")>=0) {
			compressImage(imgData, 0.5, 0.7, function(ephoto) {
					var position = ephoto.indexOf(",");
					var ephotoV = ephoto.substring(position + 1);

					console.log(ephotoV)

					$.ajax({
						type: "post",
						url: "http://121.250.222.75:8080/V-Clock/servlet/WRegisterServlet",
						async: true,
						dataType: "json",
						data: {
							ename: ename,
							esex: esex,
							etel: etel,
							ephoto: ephotoV
						},
						xhrFields: {			                       
							withCredentials: true			                   
						},
						success: function(result) {
							//result是返回值
							console.log(result);
							if(result == 0) {
								alert("注册成功");
								$("#myModal").modal("hide");
								window.location.reload();
							} else if(result == 1) {
								alert("此工作人员已存在");
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

	var photoboothInit = false;

	$('#loginModal').on('shown.bs.modal', function() {
		openDevice();
	});

	$('#loginModal').on('hide.bs.modal', function() {
		closeDevice();
	});
});

function submit(image) {
	var logetel = $("#logetel").val();
	if(!logetel) {
		alert("请输入手机号");
		return;
	}
	$.ajax({
		type: "post",
		url: "http://121.250.222.75:8080/V-Clock/servlet/WLoginServlet",
		timeout: 10000,
		async: true,
		xhrFields: {			                       
			withCredentials: true			                   
		},
		data: {
			etel: logetel,
			ephoto: image
		},
		error: function(jqXHR, textStatus, errorThrown) {
			if(textStatus == "timeout") {
				alert("连接超时,请重新登录");
			} 
		},
		success: function(result) { //result是返回值
			console.log(result);
			if(result.length == 4) { //跳转
				window.location.href = "guestsList.html"
			} else if(result == 1) {
				alert("无此员工")
			} else if(result == 2) {
				alert("请检查人脸是否被正确捕捉或手机号是否有不合法字符")
			}else if(result == 3) {
				alert("手机号与人脸不匹配")
			}
		}

	});
}

$("#login").click(function() {
	var logetel = $("#logetel").val();
	if(!logetel) {
		alert("请输入手机号");
	}else if(logetel.length<11){
		alert("请输入11位合法手机号");
	}else if(isNaN(logetel)){
		alert("含有不合法字符");
	}
	else 
	{
		$("#login-btn").click();
	}
});

$("#add").click(function() {
	$('#addModal').modal('show')
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