package cn.itrigger.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author
 * @Description 定时任务实体
 * @Date 2019/5/14 18:53
 **/
@Entity
@Table(name = "t_task_server_config")
public class TaskBean implements Serializable {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * cron表达式
     */
    @Column(name = "cron")
    private String cron;

    /**
     * 是否启用 1.是 、0.否
     */
    @Column(name = "status")
    private Integer enable;

    /**
     * 上次执行时间
     */
    @Column(name = "last_run_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastExecuteTime;

    /**
     * 任务编码
     */
    @Column(name = "code")
    private String taskCode;

    /**
     * 任务名称
     */
    @Column(name = "name")
    private String taskName;

    /**
     * 调度Url
     */
    @Column(name = "url")
    private String url;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    public TaskBean() {
    }

    public TaskBean(Long id, String cron, Integer enable, String taskCode, String taskName) {
        this.id = id;
        this.cron = cron;
        this.enable = enable;
        this.taskCode = taskCode;
        this.taskName = taskName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Date getLastExecuteTime() {
        return lastExecuteTime;
    }

    public void setLastExecuteTime(Date lastExecuteTime) {
        this.lastExecuteTime = lastExecuteTime;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "TaskBean{" +
                "id=" + id +
                ", cron='" + cron + '\'' +
                ", enable=" + enable +
                ", lastExecuteTime=" + lastExecuteTime +
                ", taskCode='" + taskCode + '\'' +
                ", taskName='" + taskName + '\'' +
                '}';
    }
}
