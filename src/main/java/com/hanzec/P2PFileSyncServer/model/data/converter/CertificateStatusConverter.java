package com.hanzec.P2PFileSyncServer.model.data.converter;

import com.hanzec.P2PFileSyncServer.constant.IAccountType;
import com.hanzec.P2PFileSyncServer.constant.ICertificateStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CertificateStatusConverter implements AttributeConverter<ICertificateStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(ICertificateStatus attribute) {
        return switch(attribute){
            case GOOD -> 1;
            case REVOKED -> 2;
            case UNKNOWN -> 3;
        };
    }

    @Override
    public ICertificateStatus convertToEntityAttribute(Integer dbData) {
        return switch(dbData){
            case 1 -> ICertificateStatus.GOOD;
            case 2 -> ICertificateStatus.REVOKED;
            case 4 -> ICertificateStatus.UNKNOWN;
            default -> null;
        };
    }
}
