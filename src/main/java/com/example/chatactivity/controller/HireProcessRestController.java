package com.example.chatactivity.controller;

import com.example.chatactivity.entity.Applicant;
import com.example.chatactivity.repository.ApplicantRepository;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class HireProcessRestController {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ApplicantRepository applicantRepository;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/start-hire-process", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void startHireProcess() {
        Map<String, String> data = new HashMap<String, String>();
        data.put("name", "bikhp");
        data.put("email", "email@gmail.com");
        data.put("phone", "323-123-1233");

//
//        Applicant applicant = new Applicant(data.get("name"), data.get("email"), data.get("phoneNumber"));
//        applicantRepository.save(applicant);
//
//        Map<String, Object> vars = Collections.<String, Object>singletonMap("applicant", applicant);
//        runtimeService.startProcessInstanceByKey("hireProcess", vars);

        // Create test applicant
        Applicant applicant = new Applicant("Test2 no hire", "test2@mailinator.com", "303-405-4545                                                                                                                                           ");
        applicantRepository.save(applicant);

        // Start process instance
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("applicant", applicant);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("hireProcessWithJpa", variables);

        // First, the 'phone interview' should be active
        Task task = taskService.createTaskQuery()                                           
                .processInstanceId(processInstance.getId())
                .taskCandidateGroup("dev-managers")
                .singleResult();
        //Assert.assertEquals("Telephone interview", task.getName());

        // Completing the phone interview with success should trigger two new tasks
        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables.put("telephoneInterviewOutcome", true);
        taskService.complete(task.getId(), taskVariables);

        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .orderByTaskName().asc()
                .list();
//        Assert.assertEquals(2, tasks.size());
//        Assert.assertEquals("Financial negotiation", tasks.get(0).getName());
//        Assert.assertEquals("Tech interview", tasks.get(1).getName());

        // Completing both should wrap up the subprocess, send out the 'welcome mail' and end the process instance
        taskVariables = new HashMap<String, Object>();
        taskVariables.put("techOk", true);
        taskService.complete(tasks.get(1).getId(), taskVariables);

        taskVariables = new HashMap<String, Object>();
        taskVariables.put("financialOk", false);
        taskService.complete(tasks.get(0).getId(), taskVariables);

        // Verify email
//        Assert.assertEquals(1, wiser.getMessages().size());

        // Verify process completed
        long count = historyService.createHistoricProcessInstanceQuery().finished().count();
//        Assert.assertEquals(1, count);
    }

}