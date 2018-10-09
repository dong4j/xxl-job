- [社区交流](http://www.xuxueli.com/page/community.html)
- [Gitter](https://gitter.im/xuxueli/xxl-job)


## 项目组维护版本 1.9.2.up

# 新增功能

1. 添加任务时, 通过下拉框选取存在的 job, 避免手写
2. 优化日志
3. 使用线程池代替手动创建线程
4. 使用项目规范重构代替

以前的版本是 1.9.1 需要新增一个字段

```sql
ALTER TABLE xxl_job_qrtz_trigger_log ADD executor_sharding_param varchar(20) DEFAULT null NULL COMMENT '执行器任务分片参数, 格式 1/2';
ALTER TABLE xxl_job_qrtz_trigger_log
  MODIFY COLUMN executor_sharding_param varchar(20) DEFAULT null COMMENT '执行器任务分片参数, 格式 1/2' AFTER executor_param;
```