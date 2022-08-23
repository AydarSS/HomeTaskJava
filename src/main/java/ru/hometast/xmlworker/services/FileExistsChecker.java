package ru.hometast.xmlworker.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileExistsChecker {

    @Value("${filesrepository.path}")
    String filepath;


    public boolean fileexistcheck (String filename) {
        boolean isexist = false;
        isexist = Files.exists(Path.of(filepath+filename));
        return isexist;
    }

}
