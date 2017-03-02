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

var baseCheckbox = function baseCheckbox(ctype,byteSize){
	this.ctype = ctype;
	this.byteSize = byteSize;
}

baseCheckbox.prototype.buzero = function(str,size){
	if(size > 0){
		for(var j=0;j<size;j++){
			str + = '0';
		}
	}
	return str;
}

baseCheckbox.prototype.getValue = function(){
	var types = $('input[ctype="'+this.ctype+'"]');
	var str = ''; 
	var i = 1;
	$.each(types,function(index,demo){
		if(demo.checked){
			str = str + '1';
		}else{
			str = str + '0';
		}
		i++;
	});
	str = this.buzero(str,(this.byteSize - i));
	return str;
};

baseCheckbox.prototype.getValueBy10 = function(){
	var str = this.getValue(); 
	return parseInt(str,2);
};

baseCheckbox.prototype.setValueBy10 = function(value){
	value =  value.toString(2);
	var types = $('input[ctype="'+this.ctype+'"]');
	$.each(types,function(index,demo){
		var x = value.charAt(index);
		if(x == '0'){
			demo.checked = false;
		}else{
			demo.checked = true;
		}
		
	});
};

baseCheckbox.prototype.setValue = function(value){
	var types = $('input[ctype="'+this.ctype+'"]');
	$.each(types,function(index,demo){
		var x = value.charAt(index);
		if(x == '0'){
			demo.checked = false;
		}else{
			demo.checked = true;
		}
		
	});
};

var baseForm = function baseForm(id){
	this.id = id;
	this.$form = $('#'+id);
}

baseForm.prototype.getSendData = function(){
	var array = this.$form.serializeArray();
	var obj = {};
	for (var arr in array) {
		var objName = array[arr]['name'];
		var objValue = array[arr]['value'];
		if(!objValue || objValue.length < 1) continue;
		if(obj[objName] != undefined){
			obj[objName] = obj[objName] + ',' + objValue;
		}else{
			obj[objName] = objValue;
		}
	}
	return obj;
};
baseForm.prototype.save = function(url,call){
	var request = new baseRequest(url,call);
	var formdata = this.getSendData();
	request.send({'formdata':JSON.stringify(formdata)});
};
baseForm.prototype.load = function(url,beforLoad){
	var request = new baseRequest(url,function(result){
		if(result && result.code == 200 && result.data){
			this.loadData(result.data);
			if(beforLoad){
				beforLoad(result.data);
			}
		}else{
			layer.msg('加载数据失败!');
		}
	});
	request.send({});
};

baseForm.prototype.loadData = function(obj){
	var key,value,tagName,type,arr;
	for(x in obj){
		key = x;
		value = obj[x];
		
		$("[name='"+key+"'],[name='"+key+"[]']").each(function(){
			tagName = $(this)[0].tagName;
			type = $(this).attr('type');
			if(tagName=='INPUT'){
				if(type=='radio'){
					$(this).attr('checked',$(this).val()==value);
				}else if(type=='checkbox'){
					arr = value.split(',');
					for(var i =0;i<arr.length;i++){
						if($(this).val()==arr[i]){
							$(this).attr('checked',true);
							break;
						}
					}
				}else{
					$(this).val(value);
				}
			}else if(tagName=='SELECT' || tagName=='TEXTAREA'){
				$(this).val(value);
			}
			
		});
	}
}
