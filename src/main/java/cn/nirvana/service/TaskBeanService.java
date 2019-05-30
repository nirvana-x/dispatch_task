package cn.nirvana.service;

import cn.nirvana.dto.TaskBeanDTO;
import cn.nirvana.dto.TaskSearchDTO;
import cn.nirvana.model.TaskBean;
import org.springframework.data.domain.Page;

/**
 * @author
 * @Description ${TODO}
 * @Date 2019/5/15 20:42
 **/
public interface TaskBeanService {

    /**
     * 分页查询定时任务列表
     *
     * @return
     */
    Page<TaskBeanDTO> findPage(TaskSearchDTO taskSearchDTO);

    /**
     * 更新定时任务
     *
     * @param taskBeanDTO
     */
    void saveOrUpdateTaskBean(TaskBeanDTO taskBeanDTO);

    /**
     * 根据Id获取定时任务详情
     *
     * @param taskId
     * @return
     */
    TaskBean findById(Long taskId);

    /**
     * 禁用定时任务
     *
     * @param taskId
     * @return
     */
    boolean cancelTask(Long taskId);

    /**
     * 根据任务Id获取任务详情
     *
     * @param id
     * @return
     */
    TaskBeanDTO findDetailById(Long id);

    /**
     * 启用定时任务
     * @param id
     * @return
     */
    boolean enableTask(Long id);

    /**
     * 刷新定时任务
     * @param id
     */
    boolean refreshTask(Long id);

    /**
     * 刷新所有的定时任务
     */
    void refreshAllTask();
}
