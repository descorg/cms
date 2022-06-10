package org.springcms.flow.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springcms.core.mybatis.response.R;
import org.springcms.flow.vo.ProcessVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/process")
@Api(value = "ProcessController", tags = "流程")
public class ProcessController {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @PostMapping("")
    @ApiOperation(value = "发起流程")
    public R<String> start(@RequestBody ProcessVO process) {
        String userId = "123456789";
        try {
            Map<String, Object> map = JSON.parseObject(JSON.toJSONString(process));
            map.put("taskUser", userId);
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(process.getCategory(), map);
            return R.data(processInstance.getId(), "ok");
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    @GetMapping("")
    @ApiOperation(value = "待办流程")
    public R<List<Task>> await() {
        String userId = "123456789";
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().desc().list();
        return R.data(tasks, "ok");
    }

    @PostMapping("/apply/{taskId}")
    @ApiOperation(value = "通过")
    public R<Boolean> apply(@PathVariable String taskId, @RequestParam(name = "说明") String descption) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return R.fail("流程不存在");
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("comment", descption);
            map.put("checkResult", "通过");
            taskService.complete(taskId, map);
            return R.success("ok");
        }
    }

    @PostMapping("/reject/{taskId}")
    @ApiOperation(value = "驳回")
    public R<Boolean> reject(@PathVariable String taskId, @RequestParam(name = "说明") String descption) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return R.fail("流程不存在");
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("comment", descption);
            map.put("checkResult", "驳回");
            taskService.complete(taskId, map);
            return R.success("ok");
        }
    }
}
