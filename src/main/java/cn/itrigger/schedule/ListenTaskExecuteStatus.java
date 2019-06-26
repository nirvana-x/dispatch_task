package cn.itrigger.schedule;

import cn.itrigger.model.TaskBean;
import cn.itrigger.service.TaskBeanService;
import cn.itrigger.utils.MailUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.List;

/**
 * @author
 * @Description ${TODO}
 * @Date 2019/6/24 18:29
 **/
@Component
public class ListenTaskExecuteStatus {

    private static final Logger logger = LoggerFactory.getLogger(ListenTaskExecuteStatus.class);

    @Value("${task.monitor.intervalTime}")
    private Integer intervalTime;

    @Value("${mail.receiver}")
    private String receiver;

    @Value("${mail.enable}")
    private boolean enable;

    @Autowired
    private TaskBeanService taskBeanService;


    @Scheduled(fixedDelay = 60000)
    public void sendExceptionMessage() {
        StringBuffer emailMsg = new StringBuffer();
        emailMsg.append("调度任务执行异常，任务对应编码为：");
        List<TaskBean> taskBeanList = taskBeanService.findByLastExecuteTimeException(intervalTime);
        taskBeanList.forEach(taskBean -> emailMsg.append(taskBean.getTaskCode()).append("、"));
        //TODO 将这些定时任务统一发送到固定邮箱
        try {
            //邮件发送给你是否启用
            if (enable){
                MailUtils.sendMail(receiver,emailMsg.toString());
            }
        } catch (MessagingException e) {
            logger.error("【调度任务异常预警，发送邮件失败】");
            logger.error(e.getMessage(),e);
        }

    }
}
