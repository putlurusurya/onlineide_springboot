package com.project.onlineide.models;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class CodeFile {
    @Id
    private String id;
    private String lang;
    private String filePath;
    private Date submittedAt;
    private Date startedAt;
    private Date completedAt;
    private String output;
    private String status;
}
