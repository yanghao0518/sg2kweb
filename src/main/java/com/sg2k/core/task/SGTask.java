package com.sg2k.core.task;

import com.alibaba.fastjson.JSONObject;

/**
 * 任务父类
 */
public class SGTask {
	protected String id;
	protected Object content;
	protected Object result;
	protected int status;
	protected int timeoutInSesc;
	protected int type = SGTaskType.BACKGROUND_TASK;
	protected long createTime;
	protected Runnable taskCmd = null; // 任务主体
	protected TaskCallback taskCallback = null; // 任务结果回调

	public SGTask(String id, int type, Object contentObj, int timeoutInSesc, Runnable taskCmd, TaskCallback taskCallback) {
		this.id = id;
		this.type = type;
		this.content = contentObj;
		this.timeoutInSesc = timeoutInSesc;
		this.taskCmd = taskCmd;
		this.taskCallback = taskCallback;

		this.status = TaskStatus.STATUS_PENDING;
		this.createTime = System.currentTimeMillis();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getTimeoutInSesc() {
		return timeoutInSesc;
	}

	public void setTimeoutInSesc(int timeoutInSesc) {
		this.timeoutInSesc = timeoutInSesc;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public Runnable getTaskCmd() {
		return taskCmd;
	}

	public void setTaskCmd(Runnable taskCmd) {
		this.taskCmd = taskCmd;
	}

	public TaskCallback getTaskCallback() {
		return taskCallback;
	}

	public void setTaskCallback(TaskCallback taskCallback) {
		this.taskCallback = taskCallback;
	}

	/**
	 * 更新任务信息（状态及结果）
	 */
	public void update(JSONObject taskResult) {
		status = ((Number) taskResult.get("status")).byteValue();
		result = taskResult.get("result");

		if (taskCallback != null) {
			if (status == TaskStatus.STATUS_FINISH) {
				taskCallback.onOK(result);
			} else {
				taskCallback.onError(result);
			}
		}
	}

	/**
	 * 任务是否超时
	 */
	public boolean isExpired() {
		if (timeoutInSesc <= 0) {
			return false;
		}

		return System.currentTimeMillis() - this.createTime > (timeoutInSesc) * 1000;// 加一个网络原因的延时
	}

	/**
	 * 重置任务的开始时间
	 */
	public void restetCTime() {
		this.createTime = System.currentTimeMillis();
	}

	public JSONObject getTaskResult() {
		JSONObject json = new JSONObject();
		json.put("status", getStatus());
		json.put("result", getResult());
		return json;
	}
}
