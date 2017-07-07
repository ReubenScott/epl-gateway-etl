package com.kindustry.etl.job;

/**
 * <p>
 * 需要进行任务调度的工作内容
 * </p>
 */
public interface EtlJob {
  
  public void work();
}