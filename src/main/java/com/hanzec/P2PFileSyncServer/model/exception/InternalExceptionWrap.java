package com.hanzec.P2PFileSyncServer.model.exception;

public class InternalExceptionWrap extends Exception{
    private final Exception internalException;

    public InternalExceptionWrap(String message, Exception internalException){
        super(message);
        this.internalException = internalException;
    }

    protected Exception getInternalException(){
        return internalException;
    }
}
