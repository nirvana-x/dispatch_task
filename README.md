# dispatch_task
## 任务调度服务，含分布式锁
## 说明：
  ### 1.该服务可用于定时任务的调度，动态配置定时任务执行计划；随改随生效；
  ### 2.单独的服务，适用于任何项目；针对多实例，添加了锁机制；