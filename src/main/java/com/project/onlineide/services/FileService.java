package com.project.onlineide.services;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.project.onlineide.models.CodeFile;
import com.project.onlineide.repositories.FileRepository;

@Service
public class FileService {
    
    private static final String CODES_DIRECTORY = "codes";
    private static final String OUTPUT_PATH = "outputs";

    private FileRepository repository;

    public FileService(FileRepository repository) {
        this.repository = repository;
    }
    public String createFile(String lang, String code){

        CodeFile codeFile=new CodeFile();
        try {
            repository.save(codeFile);
            String filePath = generateFile(lang, code, codeFile.getId());
            codeFile.setLang(lang);
            codeFile.setSubmittedAt(new Date());
            codeFile.setFilePath(filePath);
            codeFile.setStatus("PENDING");
            repository.save(codeFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return codeFile.getId();
    }
    private String generateFile(String lang, String content, String jobId) throws IOException {
        String fileName = jobId + "." + lang;
        String outFileName = jobId + ".out";
        String filePath;
        String outFilePath;

        String tempPath = Paths.get(CODES_DIRECTORY, lang).toString();
        Files.createDirectories(Paths.get(tempPath));
        String outTempPath = Paths.get(OUTPUT_PATH, lang).toString();
        Files.createDirectories(Paths.get(outTempPath));
        filePath = Paths.get(tempPath, fileName).toString();
        outFilePath = Paths.get(outTempPath, outFileName).toString();

        System.out.println(filePath);
        Files.write(Paths.get(filePath), content.getBytes());
        Files.write(Paths.get(outFilePath),"".getBytes());
        return filePath;
    }
    public String readFile(String outPath) throws IOException{
        String outString= Files.readString(Paths.get(outPath));
        return outString;
    }

    @Async
    public String executeCode(String fileIdString) throws IOException, InterruptedException {

        if(fileIdString==null){
            throw new IOException(" file ID not provides");
        }

        Optional<CodeFile> currCodeFile=repository.findById(fileIdString);
        

        String filePath=currCodeFile.orElse(null).getFilePath();
        String lang=currCodeFile.orElse(null).getLang();
        String jobId = Paths.get(filePath).getFileName().toString().split("\\.")[0];
        String outPath = Paths.get(OUTPUT_PATH, lang + "/" + jobId + ".out").toString();
        System.out.println(outPath);
        System.out.println(currCodeFile.orElse(null).getId());

        ProcessBuilder processBuilder;
        if ("c".equals(lang)) {
            processBuilder = new ProcessBuilder("g++", filePath, "-o", outPath, "&&", "cd", OUTPUT_PATH + "/" + lang, "&&", "./" + jobId + ".out");
        } 
        else if("java".equals(lang)){
            processBuilder = new ProcessBuilder("java", filePath);
        }
        else {
            // Handle other languages accordingly
            throw new UnsupportedOperationException("Language not supported: " + lang);
        }

        File outFile = new File(outPath);
        processBuilder.redirectOutput(outFile);
        processBuilder.redirectError(outFile);
        processBuilder.start();

        currCodeFile.orElse(null).setCompletedAt(new Date());
        currCodeFile.orElse(null).setStatus("COMPLETED");
        repository.save(currCodeFile.orElse(null));

        
        return outPath;
    }

    public String getStatus(String fileIdString){
        Optional<CodeFile> currCodeFile=repository.findById(fileIdString);

        return currCodeFile.orElse(null).getStatus();
    }

    
}
