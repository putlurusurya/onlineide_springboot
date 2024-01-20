package com.project.onlineide.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


import com.project.onlineide.models.CodeFile;


@Repository
public interface FileRepository extends MongoRepository<CodeFile, String> {
}
