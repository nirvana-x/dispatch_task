package cn.itrigger.config;

import cn.itrigger.dao.TaskBeanDao;
import cn.itrigger.enumeration.TaskBeanEnableEnum;
import cn.itrigger.model.TaskBean;
import cn.itrigger.utils.HttpUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

/**
 * @author
 * @Description 定时任务配置类
 * @Date 2019/5/14 18:47
 **/
@Component
@EnableScheduling
public class TaskConfig implements SchedulingConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(TaskConfig.class);

    private static final int TIMEOUT = 5 * 1000;
    private static final String LOCK_PREFIX = "lock_";


    private Map<String, ScheduledFuture<?>> futures = new HashMap<>();

    @Autowired
    private TaskBeanDao taskBeanDao;

    @Autowired
    private RedisLockHelper redisLockHelper;


    @Autowired
    @Qualifier("threadPoolTaskScheduler")
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    /**
     * 模拟 task 配置，实际执⾏行行是从 t_task_config 表读取
     */
    private List<TaskBean> taskConfigs = new ArrayList<>();

    /**
     * 定时获取数据库最新的定时任务
     */
    @PostConstruct
    public void init() {
        taskConfigs = taskBeanDao.findByEnable(TaskBeanEnableEnum.ENABLE_STATUS.getType());
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        for (TaskBean taskBean : taskConfigs) {
            ScheduledFuture future =
                    threadPoolTaskScheduler.schedule(new TaskThread(taskBean), new
                            CronTrigger(taskBean.getCron()));
            futures.put(taskBean.getTaskCode(), future);
        }
    }

    public class TaskThread extends Thread {


        private TaskBean taskBean;

        public TaskThread(TaskBean taskBean) {
            this.taskBean = taskBean;
        }

        public TaskBean getTaskBean() {
            return taskBean;
        }

        public void setTaskBean(TaskBean taskBean) {
            this.taskBean = taskBean;
        }

        @Override
        public void run() {
            String lock = LOCK_PREFIX + taskBean.getTaskCode();
            long time = System.currentTimeMillis() + TIMEOUT;
            try {
                if (!redisLockHelper.lock(lock, String.valueOf(time))) {
                    logger.info("【定时任务未获取到锁【{}】】", lock);
                }
                //获取锁成功
                logger.info("【定时任务获取锁{}成功】，任务编码：{},任务名称：{}", lock, taskBean.getTaskCode(), taskBean.getTaskName());
                //调用业务模块进行处理
                logger.info("【执行定时任务【编码:{}】开始】 任务名称：{}", taskBean.getTaskCode(), taskBean.getTaskName());
                //2.2 合法性校验.
                if (StringUtils.isEmpty(taskBean.getCron())) {
                    logger.info("【定时任务【编码:{}】执行计划为空】,任务名称：{}", taskBean.getTaskCode(), taskBean.getTaskName());
                } else {
                    taskBean.setLastExecuteTime(new Date());
                    taskBeanDao.saveAndFlush(taskBean);
                    String postResult = HttpUtils.postHttp(taskBean.getUrl(), null, 30000);
                    logger.info("任务【编码：{}】调用业务层返回：{}", taskBean.getTaskCode(), postResult);
                }
            } catch (Exception e) {
                logger.error("【调用业务处理层报错】,任务编码：{},任务名称：{}", taskBean.getTaskCode(),
                        taskBean.getTaskName());
                logger.error(e.getMessage(), e);
            } finally {
                redisLockHelper.unlock(lock, String.valueOf(time));
                logger.info("【执行定时任务【编码:{}】结束】 任务名称：{}", taskBean.getTaskCode(), taskBean.getTaskName());
                logger.info("【定时任务释放锁【{}】成功】,任务编码：{},任务名称:{}", lock, taskBean.getTaskCode(),
                        taskBean.getTaskName());
            }
        }

    }

    public Map<String, ScheduledFuture<?>> getFutures() {
        return futures;
    }

    /**
     * 新增或者更新定时任务
     *
     * @param taskBean
     */
    public void saveOrUpdateTaskConfigs(TaskBean taskBean) {
        taskConfigs.clear();
        if (MapUtils.isNotEmpty(futures)) {
            if (futures.containsKey(taskBean.getTaskCode())) {
                for (Map.Entry<String, ScheduledFuture<?>> entry : futures.entrySet()) {
                    ScheduledFuture<?> future = entry.getValue();
                    if (future.isDone()) {
                        //表明定时任务已经执行完了
                        entry.getValue().cancel(false);
                        taskConfigs.add(taskBean);
                    }
                }
            } else {
                taskConfigs.add(taskBean);
            }
        } else {
            taskConfigs.add(taskBean);
        }
        configureTasks(new ScheduledTaskRegistrar());
    }

    /**
     * 暂停定时任务
     *
     * @param taskBean
     */
    public void cancelTask(TaskBean taskBean) {
        if (MapUtils.isNotEmpty(futures)) {
            for (Map.Entry<String, ScheduledFuture<?>> entry : futures.entrySet()) {
                if (taskBean.getTaskCode().equals(entry.getKey())) {
                    entry.getValue().cancel(false);
                }
            }
        }
    }

    /**
     * 启用定时任务
     *
     * @param taskBean
     */
    public void enableTask(TaskBean taskBean) {
        taskConfigs.clear();
        taskConfigs.add(taskBean);
        configureTasks(new ScheduledTaskRegistrar());
    }

}
