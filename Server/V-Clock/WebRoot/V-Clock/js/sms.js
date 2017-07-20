var pc;
var currentPage = 1;
// 显示下一页
function next() {
    if (show_last_page_alert())
        return;
    currentPage++;
    show_current();
}

//显示前一页
function pre() {
    if (show_first_page_alert())
        return;
    currentPage--;
    show_current();
}

//显示第一页
function first() {
    if (show_first_page_alert())
        return;
    currentPage = 1;
    show_current();
}

//显示最后一页
function last() {
    if (show_last_page_alert())
        return;
    currentPage = pc;
    show_current();
}
//重置显示的内容


//判断是否已经是第一页
function show_first_page_alert() {
    if (currentPage == 1) {
        alert("Already the first page.");
        return true;
    }
    return false;
}

//判断是否已经是最后一页
function show_last_page_alert() {
    if (currentPage == pc) {
        alert("Already the last page.");
        return true;
    }
    return false;
}

//在页面上显示总共有几页
function show_total_page_num() {
    document.getElementById("total_num").innerHTML = pc;
}

//显示当前页数
function show_current_page_num() {
    document.getElementById("current_index").innerHTML = currentPage;
}

//显示
function show_current() {

    //reset();
    show();
    show_current_page_num();
    show_total_page_num() ;
}

//初始化
//show_current();
//show_total_page_num();
function show(){
//		$.post(
//	"http://121.250.222.75:8080/V-Clock/servlet/WDisplayVisitingRecordServlet", {
//	//	eid: '0002',
//		page: currentPage,
//	},
//
//	function(result) {
//		if(result.tip == '2') {
//			//alert(result.tip);
//			alert("数据错误！");
//		} else {
//			pc = result.allPageCount;
//
//				document.getElementById("total_num").innerHTML = pc;
//	
//
//			var obj = result.VisitingRecord;
//	
//			var $right = $("#msg-right");
//			$.each(obj, function(infoIndex, info) {
//				var strHtml = ""; //存储数据的变量
//				var strHtml1 = ""; //存储数据的变量
//				strHtml += "姓名：" + info["gname"] + "                    ";
//				strHtml += "到达时间：" + info["arrivingdate"] + "<br>";
//				strHtml1 += info["gphoto"];
//				var div = $("<div class='feature-center ' style='background-color: #FFFFFF;'>" +
//					"<nobr class='author'><img align='left' style='width: 60px;height:60px;margin-right: 30px;'src='data:image/jpeg;base64," + strHtml1 + "'> </nobr>" +
//					"<div id='divframe'>" +
//					"<div class='loadTitle'></div>" +
//					"<div>" + strHtml + "</div>" +
//					"</div>" +
//
//					"</div>");
//
//					div.appendTo($right);
//		
//			});
//
//		}
//	}, "json");
$.ajax({
	type:"post",
	url:"http://121.250.222.75:8080/V-Clock/servlet/WDisplayVisitingRecordServlet",
	async:true,
	dataType: "json",
	xhrFields: {			                       
			withCredentials: true			                   
		},
		data:{
			page: currentPage
		},
		success:function(result) {
		if(result.tip == '2') {
			//alert(result.tip);
			alert("数据错误！");
		} else {
			pc = result.allPageCount;

				document.getElementById("total_num").innerHTML = pc;
	

			var obj = result.VisitingRecord;
	
			var $right = $("#msg-right");
			$.each(obj, function(infoIndex, info) {
				var strHtml = ""; //存储数据的变量
				var strHtml1 = ""; //存储数据的变量
				strHtml += "姓名：" + info["gname"] + "                    ";
				strHtml += "到达时间：" + info["arrivingdate"] + "<br>";
				strHtml1 += info["gphoto"];
				var div = $("<div class='feature-center ' style='background-color: #FFFFFF;'>" +
					"<nobr class='author'><img align='left' style='width: 60px;height:60px;margin-right: 30px;'src='data:image/jpeg;base64," + strHtml1 + "'> </nobr>" +
					"<div id='divframe'>" +
					"<div class='loadTitle'></div>" +
					"<div>" + strHtml + "</div>" +
					"</div>" +

					"</div>");

					div.appendTo($right);
		
			});

		}
	}
});
}
