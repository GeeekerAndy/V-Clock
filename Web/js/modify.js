$(function() {
	$("#pic").click(function(){
		$("#doc-modal-1").modal({ width: '600px' });
		setLoadPic($("#pic"));
	});

	var photoboothInit = false;

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

$("#modify").click(function() {
	$("#eid5").attr("readonly", false);
	$("#ename5").attr("readonly", false);
	$("#esex5").attr("disabled", false);
	$("#etel5").attr("readonly", false);
});

$("#register-submit").click(function() {
	var ephoto = $("#pic").attr("src");
	if(!ephoto) {
		alert("请选择照片");
		return;
	}

	var imgFile = $("#pic").attr("src");
	if(imgFile.indexOf(",")>=0) {
		compressImage(imgFile, 0.5, 0.7, function(ephoto) {
			var position = ephoto.indexOf(",");
			var ephotoV = ephoto.substring(position + 1);

			$.ajax({
				type: "post",
				url: "http://121.250.222.75:8080/V-Clock/servlet/WModifyEmployeeInfoServlet",
				async: true,
				dataType: "json",
				xhrFields: {                       
					withCredentials: true                   
				},
				data: {
					tip: 'ephoto',
					ephoto: ephotoV
				},
				success: function(result) {
					if(result == '0') {
						alert("修改成功！");
						window.location.href = "PersonalInfo.html"; //
					} else if(result=='11'||result=='21'||result=='31'){
						alert("此修改不被允许!");
					}
					else{
						alert("数据错误!");
					}
				}
			});
		});
	}
});