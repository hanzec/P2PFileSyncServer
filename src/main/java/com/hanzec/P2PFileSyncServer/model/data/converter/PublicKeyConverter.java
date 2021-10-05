package com.hanzec.P2PFileSyncServer.model.data.converter;

import com.hanzec.P2PFileSyncServer.config.params.ClientCertificateConfigParams;
import lombok.SneakyThrows;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.springframework.beans.factory.annotation.Value;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

@Converter
public class PublicKeyConverter implements AttributeConverter<PublicKey, String> {
    private final ClientCertificateConfigParams clientCertificateConfigParams;

    public PublicKeyConverter(ClientCertificateConfigParams clientCertificateConfigParams){
        this.clientCertificateConfigParams = clientCertificateConfigParams;
    }

    @Override
    public String convertToDatabaseColumn(PublicKey attribute) {
        return Base64.encodeBase64String(attribute.getEncoded());
    }

    @Override
    @SneakyThrows
    public PublicKey convertToEntityAttribute(String dbData) {
        KeyFactory fact = KeyFactory.getInstance(clientCertificateConfigParams.getAlgorithm(), "BC");
        return fact.generatePublic(new X509EncodedKeySpec(Base64.decodeBase64(dbData)));
    }
}
