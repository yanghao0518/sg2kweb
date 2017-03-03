package com.sg2k.core.task;



public class SendConfigTask extends SGTask {

	public SendConfigTask(String taskId, Object content, TaskCallback taskCallback) {
		super(taskId, SGTaskType.SEND_CONFIG_TASK, content, 0, null, taskCallback);
	}

}
