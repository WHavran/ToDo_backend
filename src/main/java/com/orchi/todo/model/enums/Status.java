package com.orchi.todo.model.enums;

public enum Status {
    CREATED("created"),
    IN_PROCESS("inProcess"),
    FINISHED("finished"),
    FAILED("failed");

    private final String label;

    Status(String label){
        this.label = label;
    }

    public String getLabel(){
        return label;
    }

}
