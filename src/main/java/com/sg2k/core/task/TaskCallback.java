package com.sg2k.core.task;

public interface TaskCallback {
	public void onError(Object result);

	public void onOK(Object result);
}
