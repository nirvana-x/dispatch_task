package cn.itrigger.listener;

import cn.itrigger.config.TaskConfig;
import cn.itrigger.dao.TaskBeanDao;
import cn.itrigger.enums.TaskBeanEnableEnum;
import cn.itrigger.model.TaskBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author
 * @Description redis 消息处理器
 * @Date 2019/6/4 14:25
 **/
@Component
public class MessageReceive {

    private static final Logger logger = LoggerFactory.getLogger(MessageReceive.class);

    @Autowired
    private TaskConfig taskConfig;

    @Autowired
    private TaskBeanDao taskBeanDao;

    /**
     * 接收消息的方法
     */
    public void receiveMessage(String message) {
        logger.info("【Redis 消息订阅】,{}",message);
        JSONArray object = JSON.parseArray(message);
        JSONObject jsonObject = object.getJSONObject(1);
        TaskBean taskBean = taskBeanDao.findById(jsonObject.getLong("id")).orElse(null);
        logger.info("【匹配定时任务为】,{}",JSONObject.toJSONString(taskBean));
        if (taskBean.getEnable().equals(TaskBeanEnableEnum.DISABLE_STATUS.getType())){
            //说明定时任务是未启用(暂停定时任务)
            taskConfig.cancelTask(taskBean);
        }else{
            //说明定时任务是新增或者修改
            taskConfig.saveOrUpdateTaskConfigs(taskBean);
        }
    }
}
