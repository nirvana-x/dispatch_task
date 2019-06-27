package cn.nirvana.controller;

import cn.nirvana.common.ResponseDSL;
import cn.nirvana.common.SingleResponse;
import cn.nirvana.dto.TaskBeanDTO;
import cn.nirvana.dto.TaskSearchDTO;
import cn.nirvana.service.TaskBeanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * @author
 * @Description 定时任务调度控制器
 * @Date 2019/5/15 20:27
 **/
@RestController
@RequestMapping("/v1/oms/task")
@Api(value = "定时任务调度控制器")
public class TaskBeanController {

    private static final Logger logger = LoggerFactory.getLogger(TaskBeanController.class);

    @Autowired
    private TaskBeanService taskBeanService;

    @PostMapping("/list")
    @ApiOperation(value = "获取定时任务列表", notes = "获取定时任务列表")
    public SingleResponse<Page<TaskBeanDTO>> list(@RequestBody TaskSearchDTO taskSearchDTO) {
        Page<TaskBeanDTO> page = taskBeanService.findPage(taskSearchDTO);
        return ResponseDSL.singleResponseOk(page).build();
    }

    @PostMapping("/add")
    public SingleResponse<Object> add(@RequestBody TaskBeanDTO taskBeanDTO) {
        taskBeanService.saveOrUpdateTaskBean(taskBeanDTO);
        return ResponseDSL.responseNoDataOk().build();
    }

    @PutMapping("/edit")
    @ApiOperation(value = "编辑定时任务", notes = "编辑定时任务")
    public SingleResponse<Object> edit(@RequestBody TaskBeanDTO taskBeanDTO) {
        taskBeanService.saveOrUpdateTaskBean(taskBeanDTO);
        return ResponseDSL.responseNoDataOk().build();
    }

    @PutMapping("/enable/{id}")
    @ApiOperation(value = "启用定时任务",notes = "启用定时任务")
    public SingleResponse<Object> enable(@ApiParam(value = "定时任务Id",required = true) @PathVariable("id") Long id){
        taskBeanService.enableTask(id);
        return ResponseDSL.responseNoDataOk().build();
    }

    @PutMapping("/cancel/{id}")
    @ApiOperation(value = "取消定时任务", notes = "取消定时任务")
    public SingleResponse<Object> cancel(@ApiParam(value = "定时任务ID", required = true) @PathVariable("id") Long id) {
        taskBeanService.cancelTask(id);
        return ResponseDSL.responseNoDataOk().build();
    }

    @GetMapping("/detail/{id}")
    @ApiOperation(value = "获取定时任务详情", notes = "获取定时任务详情")
    public SingleResponse<TaskBeanDTO> detail(@ApiParam(value = "定时任务Id", required = true) @PathVariable("id") Long id) {
        TaskBeanDTO taskBeanDTO = taskBeanService.findDetailById(id);
        return ResponseDSL.singleResponseOk(taskBeanDTO).build();
    }

    @PutMapping("/refresh/{id}")
    @ApiOperation(value = "刷新定时任务",notes = "刷新定时任务")
    public SingleResponse<Object> refresh(@ApiParam(value = "定时任务ID",required = true) @PathVariable("id") Long id){
        taskBeanService.refreshTask(id);
        return ResponseDSL.responseNoDataOk().build();
    }

    @GetMapping("/call-back")
    @ApiOperation(value = "定时任务回调处理",notes = "定时任务回调处理")
    public SingleResponse executeCallBack(@ApiParam(value = "任务编码",required = true) @RequestParam("taskCode") String taskCode){
        logger.info("调度回调，任务编码：{}",taskCode);
        try {
            taskBeanService.updateTaskBeanByCallBack(taskCode);
            return ResponseDSL.responseNoDataOk().build();
        } catch (Exception e) {
            logger.error("调度回调处理失败，任务编码：{}",taskCode);
            logger.error(e.getMessage(),e);
            return ResponseDSL.responseError().build();
        }
    }

}
