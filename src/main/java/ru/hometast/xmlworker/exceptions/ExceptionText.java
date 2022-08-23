package ru.hometast.xmlworker.exceptions;

public enum ExceptionText {
    FILESNOTEXISTS (1,"Files are not exists in directory"),
    RECORDEXISTS (2,"Record is already exists"),
    RECORDNOTEXISTS (3,"Record is not exists"),
    FILENOTVALID (4,"File not valid"),
    SOMETHINGWRONG (5,"Oops, something wrong"),
    UNKNOWNFILE (6,"Unknown File"),
    RECORDISNOTVALID (7,"Record is not valid"),
    FILEISALREADYSAVED (8,"File is already saved"),
    RECORDNOTSAVED (9,"Unsuccessful save, file not valid"),
    EMPTYORWRONG(10, "File name is empty or not end with .xsd for xsd file and .xml for xml file");

    private String textexception;
    private int    code;

    ExceptionText( int code,String textexception) {
        this.code = code;
        this.textexception = textexception;
    }

    public int getCode() {
        return code;
    }

    public String getTextexception() {
        return textexception;
    }
}
