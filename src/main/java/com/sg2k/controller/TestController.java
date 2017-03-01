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

@Controller
@RequestMapping("/Test")
public class TestController extends BaseController
{

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

}
