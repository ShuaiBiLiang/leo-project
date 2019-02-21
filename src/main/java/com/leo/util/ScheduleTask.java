package com.leo.util;


import org.thymeleaf.util.StringUtils;

import java.util.concurrent.ScheduledFuture;

public abstract class ScheduleTask implements Runnable {
	private  static final int MAX_PERIOD = 28800;
	private int currentCount = 0;
	private boolean isJudge;
//	private boolean isShutdown;


	private String taskId;
	private ScheduledFuture<?> future;

	public ScheduleTask(String taskId) {
		this.taskId = taskId;
		this.isJudge = true;
//		this.isShutdown = false;
	}

	public ScheduleTask(String taskId, boolean isJudge) {
		this.taskId = taskId;
		this.isJudge = isJudge;
//		this.isShutdown = false;
	}

	public String getTaskId() {
		return taskId;
	}

//	public void setShutDown() {
//		this.isShutdown = true;
//	}
	public void setFuture(ScheduledFuture<?> future) {
		this.future = future;
	}

	public boolean cancel() {
		if (future != null) {
			if (future.cancel(true)) {
				future = null;
			} else {
				return false;
			}
		}
		return true;
	}

	@Override
	public void run() {
		if (StringUtils.isEmpty(taskId)) {
			return;
		}

		if (isJudge) {
			if (currentCount < MAX_PERIOD) {
				currentCount++;
			} else {
				ExecutorPool.scheduleRateCancle(taskId);
				return;
			}
		}

		this.execute();

//		if (isShutdown) {
//			this.cancel();
//		}
	}

	protected abstract void execute();
}
