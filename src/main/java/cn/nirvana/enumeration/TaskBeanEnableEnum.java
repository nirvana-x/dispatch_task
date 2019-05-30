package cn.nirvana.enumeration;

/**
 * @author
 * @Description 定时任务是否启用枚举类
 * @Date 2019/5/16 16:47
 **/
public enum TaskBeanEnableEnum {

    ENABLE_STATUS(1,"启用"),
    DISABLE_STATUS(0,"未启用");

    private Integer type;
    private String status;

    TaskBeanEnableEnum(Integer type, String status) {
        this.type = type;
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }
}
