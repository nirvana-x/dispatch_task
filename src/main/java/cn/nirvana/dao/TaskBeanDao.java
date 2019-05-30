package cn.nirvana.dao;

import cn.nirvana.model.TaskBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author
 * @Description ${TODO}
 * @Date 2019/5/15 15:00
 **/
@Repository
public interface TaskBeanDao extends JpaRepository<TaskBean, Long>, JpaSpecificationExecutor<TaskBean> {

    /**
     * 获取所有已启用的任务
     * @param enable
     * @return
     */
    List<TaskBean> findByEnable(Integer enable);

    /**
     * 根据任务编码获取定时任务
     * @param taskCode
     * @return
     */
    TaskBean findByTaskCode(String taskCode);


}
