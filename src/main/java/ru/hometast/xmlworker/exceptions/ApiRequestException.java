package ru.hometast.xmlworker.exceptions;

public class ApiRequestException  extends RuntimeException{

    int errorCode;

    public ApiRequestException(String message,int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
