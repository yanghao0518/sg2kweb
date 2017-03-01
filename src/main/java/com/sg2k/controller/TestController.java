package com.sg2k.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sg2k.utils.JUtil;
import com.sgck.dtu.analysis.cache.ConfigManager;
import com.sgck.dtu.analysis.cache.DataCache;
import com.sgck.dtu.analysis.cache.SensorVo;

@Controller
@RequestMapping("/Test")
public class TestController extends BaseController
{

	//保存配置
	@RequestMapping(value = "saveZwConfig.do")
	@ResponseBody
	public Object saveZwConfig(String jsonStr, HttpServletRequest request)
	{
		try {
			Assert.notNull(jsonStr, "参数不能为空!");
			JSONArray array = JSONArray.parseArray(jsonStr);
			int size = array.size();
			JSONObject json = null;
			for (int i = 0; i < size; i++) {
				json = array.getJSONObject(i);
				String Gateway_Id = json.getString("Gateway_Id");
				String Sensor_Ids = json.getString("Sensor_Ids");
				List<String> SensorIds = JUtil.splitToListString(Sensor_Ids);
				ConfigManager.getInstance().dealSaveGatewayIdAndSensorIdRef(Gateway_Id, SensorIds);
			}
			return getSuccess();
		} catch (Exception e) {
			return getExceptionFailure(e);
		}
	}
	
	//下发组网配置
	@RequestMapping(value = "sendZwConfig.do")
	@ResponseBody
	public Object sendZwConfig(String jsonStr, HttpServletRequest request)
	{
		try {
			Assert.notNull(jsonStr, "参数不能为空!");
			JSONArray array = JSONArray.parseArray(jsonStr);
			int size = array.size();
			JSONObject json = null;
			for (int i = 0; i < size; i++) {
				json = array.getJSONObject(i);
				String Gateway_Id = json.getString("Gateway_Id");
				String Sensor_Ids = json.getString("Sensor_Ids");
				List<String> SensorIds = JUtil.splitToListString(Sensor_Ids);
				ConfigManager.getInstance().dealSendGatewayIdAndSensorIdRef(Gateway_Id, SensorIds);
			}
			return getSuccess();
		} catch (Exception e) {
			return getExceptionFailure(e);
		}
	}
	
	//获取实时上传的组网信息
	@RequestMapping(value = "getZwInfo.do")
	@ResponseBody
	public Object getZwInfo(HttpServletRequest request)
	{
		try {
			SensorVo vo = DataCache.getInstance().getUploadZwInfo();
			return getSuccess(vo);
		} catch (Exception e) {
			return getExceptionFailure(e);
		}
	}
	
	//获取实时特征值
	@RequestMapping(value = "getPubCharacter.do")
	@ResponseBody
	public Object getPubCharacter(HttpServletRequest request)
	{
		try {
            JSONObject json = DataCache.getInstance().getPubCharacter();
            if(null != json){
            	//网关ID和传感器ID需要转化成16进制
            	String Sensor_Id = Integer.toHexString(json.getIntValue("Sensor_Id"));
            	String Gateway_Id = Integer.toHexString((Integer) json.get("Gateway_Id"));
            	json.put("Sensor_Id", Sensor_Id);
            	json.put("Gateway_Id", Gateway_Id);
            	
            }
			return getSuccess(json);
		} catch (Exception e) {
			return getExceptionFailure(e);
		}
	}
	
	//保存传感器配置信息
	@RequestMapping(value = "saveSensorConfig.do")
	@ResponseBody
	public Object saveSensorConfig(String jsonStr, HttpServletRequest request)
	{
		try {
			Assert.notNull(jsonStr, "参数不能为空!");
			JSONObject json = JSONObject.parseObject(jsonStr);
			ConfigManager.getInstance().dealSaveConfigByHtml(json.getString("Sensor_Id"), json);
			return getSuccess();
		} catch (Exception e) {
			return getExceptionFailure(e);
		}
	}
	
	//发送传感器配置信息
		@RequestMapping(value = "sendSensorConfig.do")
		@ResponseBody
		public Object sendSensorConfig(String jsonStr, HttpServletRequest request)
		{
			try {
				Assert.notNull(jsonStr, "参数不能为空!");
				JSONObject json = JSONObject.parseObject(jsonStr);
				ConfigManager.getInstance().dealSendConfigByHtml(json.getString("Sensor_Id"), json);
				return getSuccess();
			} catch (Exception e) {
				return getExceptionFailure(e);
			}
		}
	//根据选项获取不同的特征值
		@RequestMapping(value = "getCharacterByCondition.do")
		@ResponseBody
		public Object getCharacterByCondition(String type,String Sensor_Id,HttpServletRequest request)
		{
			try {
				List<Number> list = DataCache.getInstance().getCharacterList(Sensor_Id, type);
				return getSuccess(list);
			} catch (Exception e) {
				return getExceptionFailure(e);
			}
		}	

}
