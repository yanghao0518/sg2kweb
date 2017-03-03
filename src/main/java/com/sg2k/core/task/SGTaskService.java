package com.sg2k.core.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alibaba.fastjson.JSONObject;
import com.sg2k.core.cache.CacheService;



/**
 * 任务服务类  管理所有继承了SGTask的任务类
 * 
 */
public class SGTaskService {
	/**
	 * 任务执行者
	 */
	public ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
	
	private int cacheTimeInSec = 600;	//默认任务都是缓存10分钟，超时丢失
	/**
	 * 任务缓存
	 */
	private CacheService<SGTask> taskCache;
	
	public SGTaskService(){
		if(null == taskCache){
			taskCache = new CacheService<SGTask>(cacheTimeInSec);
		}
	}
	/**
	 * 供spring注入设置属性
	 */
	public void setCacheTimeInSec(int cacheTimeInSec) {
		this.cacheTimeInSec = cacheTimeInSec;
	}

	public SGTask getTask(String taskId){
		SGTask task = taskCache.getIfPresent(taskId);
		if(null != task){
			if(task.isExpired()){
				task.setStatus(TaskStatus.STATUS_TIMEOUT);
			}
		}
		return task;
	}
	
	public void addTask(String taskId, SGTask task){
		taskCache.put(taskId, task);
		
		Runnable taskRunnable = task.getTaskCmd();
		if(null != task.getTaskCmd()){
			executorService.execute(taskRunnable);
		}
	}
	
	public void updateTask(String taskId, SGTask task){
		taskCache.put(taskId, task);
	}
	
	public void removeTask(String taskId){
		taskCache.remove(taskId);
	}
	
	public void cancelTask(String taskId){
		SGTask task = getTask(taskId);
		if(null != task){
			task.setStatus(TaskStatus.STATUS_CANCEL);
			updateTask(taskId, task);
		}
	}
	
	public void updateTaskResult(String taskId, JSONObject resultObj){
		SGTask task = getTask(taskId);
		if(null != task){
			task.update(resultObj);
			updateTask(taskId, task);
		}
	}

}
