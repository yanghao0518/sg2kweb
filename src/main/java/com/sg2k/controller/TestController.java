package com.sg2k.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

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
import com.sgck.dtu.analysis.common.SystemConsts;
import com.sgck.dtu.analysis.utiils.HexUtils;

@Controller
@RequestMapping("/Test")
public class TestController extends BaseController
{
	// 上一次传感器配置
	private JSONObject lastSensorConfig;
	
	//上一次下传给网关的配置
	//{wg1:{name:xx,sensors:[]},wg2:{name:xx,sensors:[]},wg3:{name:xx,sensors:[]}}
	private JSONObject lastWgSensorsConfig;
	
	
	//第一次来到传感器设置页面获取上一次保存信息
	@RequestMapping(value = "getFirstSensorConfig.do")
	@ResponseBody
	public Object getFirstSensorConfig(HttpServletRequest request)
	{
		synchronized (this) {
			try {
				return getSuccess(this.lastSensorConfig);
			} catch (Exception e) {
				return getExceptionFailure(e);
			}
		}

	}

	// 保存传感器配置信息
	@RequestMapping(value = "saveSensorConfig.do")
	@ResponseBody
	public Object saveSensorConfig(String jsonStr, HttpServletRequest request)
	{
		synchronized (this) {
			try {
				Assert.notNull(jsonStr, "参数不能为空!");
				JSONObject json = JSONObject.parseObject(jsonStr);
				this.lastSensorConfig = json;
				return getSuccess();
			} catch (Exception e) {
				return getExceptionFailure(e);
			}
		}

	}
	
	
	//第一次来到网关下发页面获取上一次保存信息
		@RequestMapping(value = "getFirstWgSensorsConfig.do")
		@ResponseBody
		public Object getFirstWgSensorsConfig(HttpServletRequest request)
		{
			synchronized (this) {
				try {
					return getSuccess(this.lastWgSensorsConfig);
				} catch (Exception e) {
					return getExceptionFailure(e);
				}
			}

		}

		

	// 保存配置
	@RequestMapping(value = "saveZwConfig.do")
	@ResponseBody
	public Object saveZwConfig(String jsonStr, HttpServletRequest request)
	{
		try {
			Assert.notNull(jsonStr, "参数不能为空!");
			JSONObject json = JSONObject.parseObject(jsonStr);
			this.lastWgSensorsConfig = json;
			return getSuccess();
		} catch (Exception e) {
			return getExceptionFailure(e);
		}
	}

	// 下发组网配置
	@RequestMapping(value = "sendZwConfig.do")
	@ResponseBody
	public Object sendZwConfig(String jsonStr, HttpServletRequest request)
	{
		try {
			Assert.notNull(jsonStr, "参数不能为空!");
			JSONObject json = JSONObject.parseObject(jsonStr);
			Set<String> keys = json.keySet();
			//{wg1:{Gateway_Id:xx,Sensor_Ids:[]},wg2:{name:xx,sensors:[]},wg3:{name:xx,sensors:[]}}
			for(String key:keys){
				JSONObject one = json.getJSONObject(key);
				String Gateway_Id = one.getString("Gateway_Id");
				String Sensor_Ids = one.getString("Sensor_Ids");
				List<String> SensorIds = JUtil.splitToListString(Sensor_Ids);
				ConfigManager.getInstance().dealSendGatewayIdAndSensorIdRef(Gateway_Id, SensorIds);
			}
			return getSuccess();
		} catch (Exception e) {
			return getExceptionFailure(e);
		}
	}

	// 获取实时上传的组网信息
	@RequestMapping(value = "getZwInfo.do")
	@ResponseBody
	public Object getZwInfo(HttpServletRequest request)
	{
		try {
			SensorVo vo = DataCache.getInstance().getUploadZwInfo();
			if (vo.getSensor_Id() == null) {
				return null;
			}
			JSONObject rtn = new JSONObject();
			rtn.put("Sensor_Id", HexUtils.getHexString(vo.getSensor_Id()));
			rtn.put("Gateway_Id", HexUtils.getHexString(vo.getGateway_Id()));
			rtn.put("riss", HexUtils.getHexString(vo.getRiss()));
			rtn.put("package", vo.getConfig().get(SystemConsts.DATAPACKAGESIGN));
			return getSuccess(rtn);
		} catch (Exception e) {
			return getExceptionFailure(e);
		}
	}

	@RequestMapping(value = "getZwInfoTest.do")
	@ResponseBody
	public Object getZwInfoTest(HttpServletRequest request)
	{
		try {
			JSONObject rtn = new JSONObject();
			rtn.put("Sensor_Id", HexUtils.getHexString(new Random().nextInt(1000)));
			rtn.put("Gateway_Id", HexUtils.getHexString(new Random().nextInt(10000)));
			rtn.put("riss", HexUtils.getHexString(new Random().nextInt(100)));
			List<Byte> lists = new ArrayList<Byte>();
			for (int i = 0; i < 30; i++) {
				lists.add((byte) (new Random().nextInt(127)));
			}
			rtn.put("package", lists);
			return getSuccess(rtn);
		} catch (Exception e) {
			return getExceptionFailure(e);
		}
	}

	// 获取实时特征值
	@RequestMapping(value = "getPubCharacter.do")
	@ResponseBody
	public Object getPubCharacter(HttpServletRequest request)
	{
		try {
			JSONObject json = DataCache.getInstance().getPubCharacter();
			if (null != json) {
				// 网关ID和传感器ID需要转化成16进制
				String Sensor_Id = HexUtils.getHexString(json.getIntValue("Sensor_Id"));
				String Gateway_Id = HexUtils.getHexString((Integer) json.get("Gateway_Id"));
				json.put("Sensor_Id", Sensor_Id);
				json.put("Gateway_Id", Gateway_Id);

			}
			return getSuccess(json);
		} catch (Exception e) {
			return getExceptionFailure(e);
		}
	}

	// 发送传感器配置信息
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

	// 根据选项获取不同的特征值
	@RequestMapping(value = "getCharacterByCondition.do")
	@ResponseBody
	public Object getCharacterByCondition(String type, String Sensor_Id, HttpServletRequest request)
	{
		try {
			List<Number> list = DataCache.getInstance().getCharacterList(Sensor_Id, type);
			return getSuccess(list);
		} catch (Exception e) {
			return getExceptionFailure(e);
		}
	}

}
