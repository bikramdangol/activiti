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
public class BuyACarProcessRestController {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ApplicantRepository applicantRepository;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/start-buy-process", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void startBuyCarProcess() {
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
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("buyACar", variables);

        // First, the 'parent approval' should be active
        Task task = taskService.createTaskQuery()                                           
                .processInstanceId(processInstance.getId())
                .taskCandidateGroup("parent")
                .singleResult();
        //Assert.assertEquals("Telephone interview", task.getName());

        // Completing the parent approval
        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables.put("parentApproved", true);
        taskService.complete(task.getId(), taskVariables);

        //Finance approval

        task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskCandidateGroup("finance")
                .singleResult();
        //Assert.assertEquals("Telephone interview", task.getName());

        // Completing the parent approval
        taskVariables = new HashMap<String, Object>();
        taskVariables.put("financeApproved", true);
        taskService.complete(task.getId(), taskVariables);


        //Buy a car

        task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskCandidateGroup("car")
                .singleResult();
        //Assert.assertEquals("Telephone interview", task.getName());

        // Completing the parent approval
        taskVariables = new HashMap<String, Object>();
        taskVariables.put("car", true);
        taskService.complete(task.getId(), taskVariables);

        //Buy a car

//        task = taskService.createTaskQuery()
//                .processInstanceId(processInstance.getId())
//                .taskCandidateGroup("say")
//                .singleResult();
//        //Assert.assertEquals("Telephone interview", task.getName());
//
//        // Completing the parent approval
//        taskVariables = new HashMap<String, Object>();
//        taskVariables.put("say", "No");
//        taskService.complete(task.getId(), taskVariables);

        // Verify email
//        Assert.assertEquals(1, wiser.getMessages().size());

        // Verify process completed
        long count = historyService.createHistoricProcessInstanceQuery().finished().count();
//        Assert.assertEquals(1, count);
    }

}