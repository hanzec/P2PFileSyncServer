package com.hanzec.P2PFileSyncServer.model.data.converter;

import com.hanzec.P2PFileSyncServer.constant.IAccountType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class AccountTypeConverter implements AttributeConverter<IAccountType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(IAccountType attribute) {
        return switch(attribute){
            case USER_ACCOUNT -> 1;
            case CLIENT_ACCOUNT -> 2;
        };
    }

    @Override
    public IAccountType convertToEntityAttribute(Integer dbData) {
        return switch(dbData){
            case 1 -> IAccountType.USER_ACCOUNT;
            case 2 -> IAccountType.CLIENT_ACCOUNT;
            default -> null;
        };
    }
}
