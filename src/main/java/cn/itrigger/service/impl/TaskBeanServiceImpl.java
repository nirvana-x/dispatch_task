package cn.itrigger.service.impl;

import cn.itrigger.config.TaskConfig;
import cn.itrigger.dao.TaskBeanDao;
import cn.itrigger.dto.TaskBeanDTO;
import cn.itrigger.dto.TaskSearchDTO;
import cn.itrigger.enums.TaskBeanEnableEnum;
import cn.itrigger.exception.EISException;
import cn.itrigger.model.TaskBean;
import cn.itrigger.service.TaskBeanService;
import cn.itrigger.utils.ConvertUtil;
import cn.itrigger.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author
 * @Description 定时任务业务处理实现类
 * @Date 2019/5/15 20:53
 **/
@Service
public class TaskBeanServiceImpl implements TaskBeanService {

    private static final Logger logger = LoggerFactory.getLogger(TaskBeanServiceImpl.class);

    @Autowired
    private TaskBeanDao taskBeanDao;

    @Autowired
    private TaskConfig taskConfig;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public Page<TaskBeanDTO> findPage(TaskSearchDTO taskSearchDTO) {
        List<Predicate> predicates = new ArrayList<>();
        Specification<TaskBean> specification = (root, criteriaQuery, criteriaBuilder) -> {
            if (StringUtils.isNotEmpty(taskSearchDTO.getTaskName())) {
                predicates.add(criteriaBuilder.like(root.get("taskName"), "%" + taskSearchDTO.getTaskName() + "%"));
            }
            if (Objects.nonNull(taskSearchDTO.getEnable())) {
                predicates.add(criteriaBuilder.equal(root.get("enable"), taskSearchDTO.getEnable()));
            }
            return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
        };
        Page<TaskBean> page = taskBeanDao.findAll(specification, new PageRequest(taskSearchDTO.getPageNo() - 1,
                taskSearchDTO.getPageSize()));
        Page<TaskBeanDTO> pageDto = ConvertUtil.pageEntity2DTO(page, TaskBeanDTO.class);
        return pageDto;
    }

    @Override
    public void saveOrUpdateTaskBean(TaskBeanDTO taskBeanDTO) {
        TaskBean taskBean;
        if (Objects.isNull(taskBeanDTO.getId())) {
            taskBean = new TaskBean();
        } else {
            taskBean = taskBeanDao.findById(taskBeanDTO.getId()).orElse(null);
        }
        BeanUtils.copyProperties(taskBeanDTO, taskBean);
        if (Objects.isNull(taskBean.getCreateTime())) {
            taskBean.setCreateTime(new Date());
        }
        taskBean.setUpdateTime(new Date());
        TaskBean newTaskBean = taskBeanDao.saveAndFlush(taskBean);
        //Redis发布消息通知，通知其他实例变动
        logger.info("【Redis发布消息通知，任务更新或者保存，通知其他实例变动】，任务名称：{}", newTaskBean.getTaskName());
        redisTemplate.convertAndSend(taskBean.getTaskCode(), newTaskBean);
    }

    @Override
    public TaskBean findById(Long taskId) {
        return taskBeanDao.findById(taskId).orElse(null);
    }

    @Override
    public boolean cancelTask(Long taskId) {
        TaskBean taskBean = taskBeanDao.findById(taskId).orElse(null);
        if (null == taskBean) {
            return false;
        } else {
            taskBean.setEnable(TaskBeanEnableEnum.DISABLE_STATUS.getType());
            taskBean.setUpdateTime(new Date());
            TaskBean newTaskBean = taskBeanDao.saveAndFlush(taskBean);
            //Redis发布消息通知，通知其他实例变动
            logger.info("【Redis发布消息通知，暂停定时任务，通知其他实例变动】，任务名称：{}", newTaskBean.getTaskName());
            redisTemplate.convertAndSend(taskBean.getTaskCode(), newTaskBean);
            return true;
        }
    }

    @Override
    public TaskBeanDTO findDetailById(Long id) {
        TaskBeanDTO taskBeanDTO = new TaskBeanDTO();
        TaskBean taskBean = taskBeanDao.findById(id).orElse(null);
        if (null == taskBean) {
            logger.info("【查询定时任务详情失败】,未查询到定时任务：{} 的明细", id);
            throw new EISException("未查询到定时任务明细");
        }
        BeanUtils.copyProperties(taskBean, taskBeanDTO);
        return taskBeanDTO;
    }

    @Override
    public boolean enableTask(Long id) {
        TaskBean taskBean = taskBeanDao.findById(id).orElse(null);
        if (null == taskBean) {
            return false;
        } else {
            taskBean.setEnable(TaskBeanEnableEnum.ENABLE_STATUS.getType());
            taskBean.setUpdateTime(new Date());
            TaskBean newTaskBean = taskBeanDao.saveAndFlush(taskBean);
            //Redis发布消息通知，通知其他实例变动
            logger.info("【Redis发布消息通知，启用定时任务，通知其他实例变动】，任务名称：{}", newTaskBean.getTaskName());
            redisTemplate.convertAndSend(taskBean.getTaskCode(), newTaskBean);
            return true;
        }
    }

    @Override
    public boolean refreshTask(Long id) {
        TaskBean taskBean = taskBeanDao.findById(id).orElse(null);
        if (null == taskBean) {
            logger.info("【定时任务刷新失败】，未查询到任务详情数据，任务ID：{}", id);
            return false;
        }
        //Redis发布消息通知，通知其他实例变动
        logger.info("【Redis发布消息通知，刷新定时任务，通知其他实例变动】，任务名称：{}", taskBean.getTaskName());
        redisTemplate.convertAndSend(taskBean.getTaskCode(), taskBean);
        return true;
    }

    @Override
    public void refreshAllTask() {
        List<TaskBean> taskBeanList = taskBeanDao.findByEnable(TaskBeanEnableEnum.ENABLE_STATUS.getType());

    }

    @Override
    public void updateTaskBeanByCallBack(String taskCode) {
        TaskBean taskBean = taskBeanDao.findByTaskCode(taskCode);
        if (taskBean == null){
            logger.info("【回调未查询到任务信息】，任务编码：{}",taskCode);
            throw new EISException("【回调未查询到任务信息】");
        }
        taskBean.setLastExecuteTime(new Date());
        taskBeanDao.saveAndFlush(taskBean);
    }

    @Override
    public List<TaskBean> findByLastExecuteTimeException(Integer intervalTime) {
        //TODO 查询已启用的且最后一次执行时间与当前系统时间 大于 间隔时间的定时任务
        Date intervalTimePreDay = DateUtils.getIntervalTimePreDay(intervalTime);
        List<TaskBean> taskBeans =
                taskBeanDao.findByLastExecuteTimeBeforeAndEnable(intervalTimePreDay,
                        TaskBeanEnableEnum.ENABLE_STATUS.getType());
        return taskBeans;
    }
}
