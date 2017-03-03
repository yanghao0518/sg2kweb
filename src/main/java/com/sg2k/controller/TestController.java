package com.sg2k.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.sg2k.core.cache.CacheService;
import com.sg2k.core.task.SendConfigTask;
import com.sg2k.core.task.TaskCallback;
import com.sg2k.core.task.TaskStatus;
import com.sg2k.utils.JUtil;
import com.sgck.dtu.analysis.cache.AckLastCache;
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

	// 上一次下传给网关的配置
	// {wg1:{name:xx,sensors:[]},wg2:{name:xx,sensors:[]},wg3:{name:xx,sensors:[]}}
	private JSONObject lastWgSensorsConfig;

	protected CacheService<SendConfigTask> taskCache = new CacheService<SendConfigTask>(1800);
	
	private TaskCallback callback = new TaskCallback()
	{

		@Override
		public void onOK(Object result)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onError(Object result)
		{
			// TODO Auto-generated method stub

		}
	};

	// 第一次来到传感器设置页面获取上一次保存信息
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
	public Object saveSensorConfig(String formdata, HttpServletRequest request)
	{
		synchronized (this) {
			try {
				Assert.notNull(formdata, "参数不能为空!");
				JSONObject json = JSONObject.parseObject(formdata);
				this.lastSensorConfig = json;
				return getSuccess();
			} catch (Exception e) {
				return getExceptionFailure(e);
			}
		}

	}

	// 发送传感器配置信息
	@RequestMapping(value = "sendSensorConfig.do")
	@ResponseBody
	public Object sendSensorConfig(String formdata, HttpServletRequest request)
	{
		try {
			Assert.notNull(formdata, "参数不能为空!");
			JSONObject json = JSONObject.parseObject(formdata);
			String Gateway_Id = json.getString("Gateway_Id");
			String Sensor_Id = json.getString("Sensor_Id");
			
			
			Assert.notNull(Gateway_Id, "网关ID不能为空!");
			Assert.notNull(Sensor_Id, "传感器ID不能为空!");
			
			Sensor_Id = HexUtils.unStringFromHex(Sensor_Id);
			
			Gateway_Id = HexUtils.unStringFromHex(Gateway_Id);
			
			json.put("Gateway_Id", Gateway_Id);
			
			json.put("Sensor_Id", Sensor_Id);
			

			SendConfigTask task = null;
			if (!taskCache.containsKey(Sensor_Id)) {
				JSONObject content = new JSONObject();
				task = new SendConfigTask(Sensor_Id, content, callback);
				taskCache.put(Sensor_Id, task);
			}

			ConfigManager.getInstance().dealSendConfigByHtml(Sensor_Id, Gateway_Id, json);

			return getSuccess(Sensor_Id);
		} catch (Exception e) {
			return getExceptionFailure(e);
		}
	}

	// 不停的获取数据
	@RequestMapping(value = "getTaskResult.do")
	@ResponseBody
	public Object getTaskResult(String taskId, HttpServletRequest request)
	{
		synchronized (this) {
			try {
				taskId =  HexUtils.unStringFromHex(taskId);
				if(!taskCache.containsKey(taskId)){
					throw new IllegalArgumentException("下发任务不存在!");
				}
				byte[] data = AckLastCache.getInstance().getPackages(taskId);
				JSONObject rtn = new JSONObject();
				if(null == data){
					rtn.put("status", TaskStatus.STATUS_RUNNING);
					return getSuccess(rtn);
				}
				taskCache.remove(taskId);
				rtn.put("status", TaskStatus.STATUS_FINISH);
				rtn.put("packageData", data);
				return getSuccess(rtn);
				
			} catch (Exception e) {
				return getExceptionFailure(e);
			}
		}

	}

	// 第一次来到网关下发页面获取上一次保存信息
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
			// {wg1:{Gateway_Id:xx,Sensor_Ids:[]},wg2:{name:xx,sensors:[]},wg3:{name:xx,sensors:[]}}
			for (String key : keys) {
				JSONObject one = json.getJSONObject(key);
				String Gateway_Id = one.getString("Gateway_Id");
				Gateway_Id = HexUtils.unStringFromHex(Gateway_Id);
				String Sensor_Ids = one.getString("Sensor_Ids");
				List<String> SensorIds = JUtil.splitToListString(Sensor_Ids,"0x","");
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
			rtn.put("Sensor_Id", HexUtils.toStringFromHex(vo.getSensor_Id()));
			rtn.put("Gateway_Id", HexUtils.toStringFromHex(vo.getGateway_Id()));
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
			JSONObject newjson = new JSONObject();
			JUtil.cloneJSONObject(json, newjson);
			
			if (null != json) {
				// 网关ID和传感器ID需要转化成16进制
				String Sensor_Id = HexUtils.getHexString(newjson.getIntValue("Sensor_Id"));
				String Gateway_Id = HexUtils.getHexString((Integer) newjson.get("Gateway_Id"));
				String Constant_Up = HexUtils.getHexString(newjson.getIntValue("Constant_Up"));
				newjson.put("Sensor_Id", Sensor_Id);
				newjson.put("Gateway_Id", Gateway_Id);
				newjson.put("Constant_Up", Constant_Up);

			}
			return getSuccess(newjson);
		} catch (Exception e) {
			return getExceptionFailure(e);
		}
	}
	
	//激活测试
	@RequestMapping(value = "getPubCharacterTest.do")
	@ResponseBody
	public Object getPubCharacterTest(HttpServletRequest request)
	{
		try {
			JSONObject json = new JSONObject();
			
			json.put("Constant_Up", 36589);
			json.put("Gateway_Id", 25502);
			json.put("Sensor_Id", 2502);
			json.put("Package_Type", 5);
			json.put("Version", 1);
			json.put("Package_length", 20);
			json.put("Package_Number", 257);
			json.put("command_properties", 1);
			json.put("Battery", 20);
			json.put("Temperature", 30);
			json.put("Character_Attribute", 2);
			json.put("Data_coefficient", 100);
			json.put("Data_x_Rms", 200);
			json.put("Data_x_PP", 100);
			json.put("Data_x_P", 50);
			
			json.put("Data_y_Rms", 400);
			json.put("Data_y_PP", 200);
			json.put("Data_y_P", 150);
			
			json.put("Data_z_Rms", 600);
			json.put("Data_z_PP", 500);
			json.put("Data_z_P", 250);
			
			json.put("BCC", 1);
			
			byte[] lists = new byte[]{1,3,43,89,16,5,6,78,2,11,90,22};
			json.put("datapackage", lists);
			
			 DataCache.getInstance().setPubCharacter(json);
			return getSuccess(json);
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
	
	// 获取测试SQL
	@RequestMapping(value = "getSql.do")
	@ResponseBody
	public Object getSql(String sql,HttpServletRequest request)
	{
		try {
			System.out.println(sql);
			return getSuccess();
		} catch (Exception e) {
			return getExceptionFailure(e);
		}
	}

}
