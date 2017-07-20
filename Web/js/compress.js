function compressImage(url, ratio, quality, callback){
	var canvas = document.createElement('canvas');
	var context = canvas.getContext('2d');
	var img = document.createElement('img');
	img.src = url;
	img.onload = function(){
		canvas.width = img.width * ratio;
		canvas.height = img.height * ratio;
		context.drawImage(img,0,0,canvas.width,canvas.height);
		callback(canvas.toDataURL('image/jpeg', quality));
	};
}
