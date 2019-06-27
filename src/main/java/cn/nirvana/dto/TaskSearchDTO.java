package cn.nirvana.dto;

/**
 * @author
 * @Description ${TODO}
 * @Date 2019/5/15 20:49
 **/
public class TaskSearchDTO extends TaskBeanDTO {

    private Integer pageNo;
    private Integer pageSize;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
