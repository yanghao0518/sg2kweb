var initForm = function($form,config){
	$form.form({
		url :config.url,
		onBeforeLoad : function(param){
			//$.messager.progress();
		},
		onLoadSuccess : function(data) {
			//加载完成之后才构建
			if(config.onLoadSuccessCallBack){
				config.onLoadSuccessCallBack(data);
			}
		},
		onLoadError : function(data) {
			//加载完成之后才构建
			layer.alert("加载内容失败,请重新刷新页面/联系管理员");
		},
		onSubmit : function(param) {
			if (!$(this).valid()) {
				// new AlertDismiss("提示","提交的选项中,有错误内容,请检查");
			     return false;
			}
		},
		success : function(data) {	
			var jd = eval('(' + data + ')');
			if(!!!jd){
				layer.alert("提示","未知异常");
				return false;
			}
			if(jd.state!=200){
				layer.alert(jd.message||'操作失败!');
				return false;
			}
			if(jd.data && jd.data.alertDismiss == 'no'){
				//页面不弹出操作成功提示
				if(config.submitSuccessCallback){
    				config.submitSuccessCallback(jd.data);
    			}
			}else{
				if(config.alertDismiss && config.alertDismiss == 'yes'){
					if(config.submitSuccessCallback){
	    				config.submitSuccessCallback(jd.data);
	    			}
					return false;
				}
				
				var tip = config.tip||'操作成功!';
				layer.alert(tip, {
	        	    skin: 'layui-layer-lan' //样式类名
	        	    ,closeBtn: 0
	        	}, function(index){
	        		if(config.submitSuccessCallback){
	    				config.submitSuccessCallback(jd.data);
	    				layer.close(index);
	    			}
	        	});
			}
			
		}
	});
	
};

//直接load无法处理时间格式转化,采用ajax获取到对象,然后回调function自行处理完成后再load入form
var loadFormPre = function(url,options){
	$.ajax({
	      url: url,
		  type:'POST',
	      dataType: 'json',
		  timeout:10000,
	      success: function(data) {
	    	if (typeof options.loadFormCallback === 'function') {
	        	options.loadFormCallback(data.data);
	        }
	      },
		  error: function(XMLHttpRequest, textStatus, errorThrown) {
			
		  }
	});
}

