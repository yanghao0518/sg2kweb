package com.sg2k.controller;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.sg2k.consts.ErrorCode;
import com.sg2k.exception.DSException;

public class BaseController
{
	private Logger LOG = Logger.getLogger(BaseController.class);

	public JSONObject getSuccess()
	{
		return getSuccess(null);
	}

	public JSONObject getSuccess(Object data)
	{
		JSONObject json = new JSONObject();
		json.put("code", ErrorCode.SUCCESS);
		json.put("data", data);
		return json;
	}

	public JSONObject getFailure(int code, String msg)
	{
		JSONObject json = new JSONObject();
		json.put("code", code);
		json.put("msg", msg);
		return json;
	}

	public JSONObject getFailure(String msg)
	{
		return getFailure(ErrorCode.COMMON_ERROR_CODE, msg);
	}

	protected JSONObject getExceptionFailure(Exception e)
	{
		LOG.error("错误->"+e.getMessage(), e);
		
		if (e instanceof IllegalArgumentException) {
			return getFailure(ErrorCode.COMMON_ERROR_CODE, e.getMessage());
		}

		if (e instanceof DSException) {
			DSException dse = (DSException) e;
			return getFailure(dse.getCode(), dse.getMessage());
		}
		return getFailure(ErrorCode.DB_ERROR, "操作数据库异常");
	}

}
