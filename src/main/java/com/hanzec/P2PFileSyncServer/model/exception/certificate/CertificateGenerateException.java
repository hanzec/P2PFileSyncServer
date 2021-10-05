package com.hanzec.P2PFileSyncServer.model.exception.certificate;

import com.hanzec.P2PFileSyncServer.model.exception.InternalExceptionWrap;

public class CertificateGenerateException extends InternalExceptionWrap {
    public CertificateGenerateException(Exception internalException){
        super("Internal Error: Failed to create certificate", internalException);
    }
}
