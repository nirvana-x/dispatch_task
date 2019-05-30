package cn.nirvana.service.impl;

import cn.nirvana.config.TaskConfig;
import cn.nirvana.dao.TaskBeanDao;
import cn.nirvana.dto.TaskBeanDTO;
import cn.nirvana.dto.TaskSearchDTO;
import cn.nirvana.enumeration.TaskBeanEnableEnum;
import cn.nirvana.exception.EISException;
import cn.nirvana.model.TaskBean;
import cn.nirvana.service.TaskBeanService;
import cn.nirvana.utils.ConvertUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
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
        Page<TaskBean> page = taskBeanDao.findAll(specification, new PageRequest(taskSearchDTO.getPageNo()-1,
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
        if (Objects.isNull(taskBean.getCreateTime())){
            taskBean.setCreateTime(new Date());
        }
        taskBean.setUpdateTime(new Date());
        taskBeanDao.saveAndFlush(taskBean);
        if (TaskBeanEnableEnum.ENABLE_STATUS.getType().equals(taskBean.getEnable())) {
            taskConfig.saveOrUpdateTaskConfigs(taskBean);
        }
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
            taskBeanDao.saveAndFlush(taskBean);
            taskConfig.cancelTask(taskBean);
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
            taskBeanDao.saveAndFlush(taskBean);
            taskConfig.enableTask(taskBean);
            return true;
        }
    }

    @Override
    public boolean refreshTask(Long id) {
        TaskBean taskBean = taskBeanDao.findById(id).orElse(null);
        if (null == taskBean){
            logger.info("【定时任务刷新失败】，未查询到任务详情数据，任务ID：{}",id);
            return false;
        }
        taskConfig.saveOrUpdateTaskConfigs(taskBean);
        return true;
    }

    @Override
    public void refreshAllTask() {
        List<TaskBean> taskBeanList = taskBeanDao.findByEnable(TaskBeanEnableEnum.ENABLE_STATUS.getType());

    }
}
