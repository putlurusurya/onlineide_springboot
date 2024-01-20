package com.project.onlineide.controller;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.project.onlineide.models.Code;
import com.project.onlineide.responses.ErrorResponse;
import com.project.onlineide.responses.ExecutionResponse;
import com.project.onlineide.services.FileService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;





@RestController
@RequestMapping("/")
public class OnlineIdeController {

    @Autowired
    private FileService fileService;

    @GetMapping("/ide")
    public String getMethodName(@RequestParam(name = "param", required = false, defaultValue = "default") String param) {
        return "hello";
    }
    

    @PostMapping("/run")
    public ResponseEntity<Object> runFile(@RequestBody Code code) throws IOException, InterruptedException {
        String codeBody = code.getCode();
        String lang = code.getLang();
        if(codeBody==null || codeBody.isEmpty()){
            return ResponseEntity.badRequest().body(new ErrorResponse("Code not found"));
        }
        String fileIdString = fileService.createFile(lang,codeBody);
        String outputPath = fileService.executeCode(fileIdString);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ExecutionResponse(true, outputPath, fileIdString));
    }

    @PostMapping("/status")
    private  ResponseEntity<Object> postMethodName(@RequestBody String fileIdString) {
        String status = fileService.getStatus(fileIdString);
        
        return ResponseEntity.status(HttpStatus.OK)
                    .body(new ExecutionResponse(true, status, fileIdString));
    }
    
    
    
    
}
