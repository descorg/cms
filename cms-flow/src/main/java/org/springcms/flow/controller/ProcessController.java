package org.springcms.flow.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.springcms.core.mybatis.response.R;
import org.springcms.flow.vo.ProcessDefinitionVO;
import org.springcms.flow.vo.ProcessDetailVO;
import org.springcms.flow.vo.ProcessVO;
import org.springcms.flow.vo.TaskVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/process")
@Api(value = "ProcessController", tags = "流程")
public class ProcessController {

    @Autowired
    private IdentityService identityService;
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private HistoryService historyService;

    @GetMapping("/deploy")
    @ApiOperation(value = "已部署的流程")
    @ApiOperationSupport(order = 1)
    public R<List<ProcessDefinitionVO>> getDeployList() {
        List<ProcessDefinitionVO> processDefinitionList = new ArrayList<>();
        repositoryService.createDeploymentQuery().list();
        List<ProcessDefinition> models = repositoryService.createProcessDefinitionQuery().list();
        for (ProcessDefinition definition : models) {
            ProcessDefinitionVO processDefinition = new ProcessDefinitionVO();
            BeanUtils.copyProperties(definition, processDefinition);
            processDefinition.setImages(getImages(definition.getId(), null, null));
            processDefinitionList.add(processDefinition);
        }

        return R.data(processDefinitionList, "ok");
    }

    @PostMapping("/start")
    @ApiOperation(value = "发起流程")
    @ApiOperationSupport(order = 2)
    public R<String> start(@RequestBody ProcessVO process) {
        String userId = "123456789";
        try {
            //设置发起人
            identityService.setAuthenticatedUserId(userId);

            Map<String, Object> map = JSON.parseObject(JSON.toJSONString(process));
            map.put("taskUser", userId);
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(process.getCategory(), map);

            String processInstanceId = processInstance.getId();

            //设置第一个节点处理人
            Task task = processEngine.getTaskService().createTaskQuery().processInstanceId(processInstanceId).singleResult();
            taskService.setAssignee(task.getId(), process.getAssignee());

            return R.data(processInstanceId, "ok");
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    @GetMapping("/stop/{processInstanceId}")
    @ApiOperation(value = "终止流程")
    @ApiOperationSupport(order = 3)
    public R<Boolean> stop(@PathVariable String processInstanceId) {
        runtimeService.suspendProcessInstanceById(processInstanceId);
        return R.success("ok");
    }

    @PostMapping("/activate/{processInstanceId}")
    @ApiOperation(value = "激活流程")
    @ApiOperationSupport(order = 4)
    public R<Boolean> activate(@PathVariable String processInstanceId) {
        try {
            runtimeService.activateProcessInstanceById(processInstanceId);
            return R.data(Boolean.TRUE);
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    @GetMapping("/taskByUserId/{userId}")
    @ApiOperation(value = "待办流程-根据人查询")
    @ApiOperationSupport(order = 5)
    public R<List<TaskVO>> taskByUserId(@PathVariable String userId) {
        List<TaskVO> taskList = new ArrayList<>();
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().desc().list();
        for (Task task : tasks) {
            TaskVO taskVO = new TaskVO();
            BeanUtils.copyProperties(task, taskVO);

            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            taskVO.setProcessDefinitionKey(pi.getProcessDefinitionKey());
            taskVO.setProcessDefinitionName(pi.getProcessDefinitionName());

            taskList.add(taskVO);
        }

        return R.data(taskList, "ok");
    }

    @GetMapping("/taskByGroup/{group}")
    @ApiOperation(value = "待办流程-根据组查询")
    @ApiOperationSupport(order = 6)
    public R<List<TaskVO>> taskByGroup(@PathVariable String group) {
        List<TaskVO> taskList = new ArrayList<>();
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(group).orderByTaskCreateTime().desc().list();

        for (Task task : tasks) {
            TaskVO taskVO = new TaskVO();
            BeanUtils.copyProperties(task, taskVO);

            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            taskVO.setProcessDefinitionKey(pi.getProcessDefinitionKey());
            taskVO.setProcessDefinitionName(pi.getProcessDefinitionName());

            taskList.add(taskVO);
        }

        return R.data(taskList, "ok");
    }

    @PostMapping("/apply/{taskId}")
    @ApiOperation(value = "通过")
    @ApiOperationSupport(order = 7)
    public R<Boolean> apply(@PathVariable String taskId, @RequestParam String descption) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return R.fail("流程不存在");
        } else {
            try {
                Map<String, Object> map = new HashMap<>();
                map.put("comment", descption);
                map.put("checkResult", "通过");
                taskService.complete(taskId, map);
                return R.success("ok");
            } catch (Exception e) {
                return R.fail(e.getMessage());
            }
        }
    }

    @PostMapping("/reject/{taskId}")
    @ApiOperation(value = "驳回")
    @ApiOperationSupport(order = 8)
    public R<Boolean> reject(@PathVariable String taskId, @RequestParam String descption) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return R.fail("流程不存在");
        } else {
            try {
                Map<String, Object> map = new HashMap<>();
                map.put("comment", descption);
                map.put("checkResult", "驳回");
                taskService.complete(taskId, map);
                return R.success("ok");
            } catch (Exception e) {
                return R.fail(e.getMessage());
            }
        }
    }

    @GetMapping("/detail/{processInstanceId}")
    @ApiOperation(value = "流程图")
    @ApiOperationSupport(order = 9)
    public R<ProcessDetailVO> detail(@PathVariable String processInstanceId) {
        ProcessDetailVO detail = new ProcessDetailVO();
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (pi == null) {
            return R.fail("Process does not exist");
        }

        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();

        String instanceId = task.getProcessInstanceId();
        //查询正在执行的执行对象表
        List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(instanceId).list();

        List<String> activityIds = new ArrayList<>();
        List<String> flows = new ArrayList<>();
        for (Execution exe : executions) {
            List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
            activityIds.addAll(ids);
        }

        String image = getImages(pi.getProcessDefinitionId(), activityIds, flows);
        if (image == null || image.isEmpty()) {
            return R.fail("Failed to view flowchart");
        } else {
            BeanUtils.copyProperties(pi, detail);
            detail.setImages(image);
            return R.data(detail, "ok");
        }
    }

    @DeleteMapping("/deleteDeployment/{deploymentId}")
    @ApiOperation(value = "删除部署")
    @ApiOperationSupport(order = 10)
    public R<Boolean> delete(@PathVariable String deploymentId) {
        repositoryService.deleteDeployment(deploymentId, true);
        return R.success("ok");
    }

    @GetMapping("/launch/{userId}")
    @ApiOperation(value = "我发起的")
    public R<List> launch(@PathVariable String userId, @RequestParam Integer page, @RequestParam Integer size) {
        List historicProcessInstanceList = historyService.createHistoricProcessInstanceQuery()
                .startedBy(userId)
                .orderByProcessInstanceStartTime().desc()
                .listPage((page - 1) * size, size);
        return R.data(historicProcessInstanceList, "ok");
    }

    @GetMapping("/conduct/{userId}")
    @ApiOperation(value = "我办理的")
    public R conduct(@PathVariable String userId, @RequestParam Integer page, @RequestParam Integer size) {
        List historicProcessInstanceList = historyService.createHistoricActivityInstanceQuery()
                // 我审批的
                .taskAssignee(userId)
                // 按照结束时间倒序
                .orderByHistoricActivityInstanceEndTime().desc()
                // 已结束的（其实就是判断有没有结束时间）
                .finished()
                // 分页
                .listPage((page - 1) * size, size);

        return R.data(historicProcessInstanceList, "ok");
    }

    /**
     * 获取流程图
     * @param definitionId
     * @param activityIds
     * @param flows
     * @return
     */
    private String getImages(String definitionId, List<String> activityIds, List<String> flows) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(definitionId);
        ProcessEngineConfiguration engconf = processEngine.getProcessEngineConfiguration();
        ProcessDiagramGenerator diagramGenerator = engconf.getProcessDiagramGenerator();
        InputStream in;

        if (activityIds == null || flows == null) {
            in = diagramGenerator.generateDiagram(bpmnModel, "png", engconf.getActivityFontName(), engconf.getLabelFontName(), engconf.getAnnotationFontName(), engconf.getClassLoader(), 1.0, true);
        } else {
            in = diagramGenerator.generateDiagram(bpmnModel, "png", activityIds, flows, engconf.getActivityFontName(), engconf.getLabelFontName(), engconf.getAnnotationFontName(), engconf.getClassLoader(), 1.0, true);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int legth = 0;
        try {
            while ((legth = in.read(buf)) != -1) {
                out.write(buf, 0, legth);
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            String data = Base64.getEncoder().encodeToString(out.toByteArray());
            return String.format("data:image/png;base64,%s", data);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
