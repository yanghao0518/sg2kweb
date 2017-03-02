/**
 * 
 */
var baseRequest = function baseRequest(url, success, error) {
	this.url = url;
	this.success = success;
	this.error = error;
}
baseRequest.prototype.send = function(data) {
	var me = this;
	if(!data){
		data = {};
	}
	$.ajax({
		url : me.url,
		data : data,
		type : 'post',
		dataType : 'json',
		async : false,
		success : function(result) {
			if(me.success){
				me.success(result);
			}else{
				console.info(result);
			}
			
		},
		complete : function() {
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			if(me.error){
				me.error(textStatus);
			}else{
				alert('请求错误!');
			}
		}
	});
}