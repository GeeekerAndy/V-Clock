var resp = false;

// 打开摄像头
function openDevice() {
	var data = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<param>\n    <device>\n        <width>640</width>\n        <height>480</height>\n        <rotate>0</rotate>\n        <flip>0</flip>\n        <capidx>0</capidx>\n        <audio>1</audio>\n        <step>3000</step>\n        <time>15</time>\n    </device>\n    <image>\n        <eye>0</eye>\n        <mouth>0</mouth>\n        <headup>18</headup>\n        <headdown>18</headdown>\n        <headleft>18</headleft>\n        <headright>18</headright>\n        <pupilmin>1</pupilmin>\n        <pupilmax>256</pupilmax>\n        <clarity>15</clarity>\n    </image>\n</param>";
    stdfcectl.InitParam(data); // 设置参数，可以在任何需要的地方调用，将覆盖原来的值，立即生效
    stdfcectl.OpenCapture(); // 打开摄像头
    resp = false;
    getFace();
}

// 获取图像
function getFace() {
	setTimeout("timeout()",10000);
    stdfcectl.GetFace();
}

// 关闭摄像头
function closeDevice() {
    stdfcectl.CloseCapture();
}

function timeout(){
	if(resp)
		return;
	alert("请检查手机号是否正确并确保摄像头打开时对准自己的面部");
	window.location.reload();
}
