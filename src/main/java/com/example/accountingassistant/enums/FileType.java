package com.example.accountingassistant.enums;

public enum FileType {
    SUPPORT_IN("supportIn\\"),

    SUPPORT_OUT("supportOut\\"),

    USER_IN("userIn\\")

    ;
    private String folderName;

    FileType(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }
}
