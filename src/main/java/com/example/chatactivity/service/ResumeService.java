package com.example.chatactivity.service;

import com.example.chatactivity.entity.Applicant;
import org.springframework.stereotype.Component;

@Component
public class ResumeService {

    public void storeResume(Applicant applicant) {
        System.out.println("Storing resume ...");
        System.out.printf("Name: %s, Email: %s, Phone: %s \n", applicant.getName(), applicant.getEmail(), applicant.getPhoneNumber());
    }

}
