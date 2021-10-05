package com.hanzec.P2PFileSyncServer.model.data.certificate;

import com.hanzec.P2PFileSyncServer.constant.ICertificateStatus;
import com.hanzec.P2PFileSyncServer.model.data.converter.CertificateStatusConverter;
import com.hanzec.P2PFileSyncServer.model.data.converter.PublicKeyConverter;
import com.hanzec.P2PFileSyncServer.utils.ByteArrayUtil;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.Date;

@Entity
@Table(
        name = "CLIENT_CERTIFICATE",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "PUBLIC_KEY")
        })
public class ClientCertificate implements Serializable {
    @Id
    @Column(name = "ID")
    private byte[] id;

    @Getter
    @Column(name = "STATUS")
    @Convert(converter = CertificateStatusConverter.class)
    private ICertificateStatus status;

    @Getter
    @Column(name = "PUBLIC_KEY")
    @Convert(converter = PublicKeyConverter.class)
    private PublicKey publicKey;

    @Getter
    @Column(name = "EXPIRE_TIME", columnDefinition="TIMESTAMP")
    private Date expireTime;

    @Getter
    @Column(name = "AVALIABLE_TIME", columnDefinition="TIMESTAMP")
    private Date avaliableTime;

    @Getter
    @CreationTimestamp
    @Column(name = "CREATE_TIME", columnDefinition="TIMESTAMP")
    private ZonedDateTime createTime;

    @Getter
    @UpdateTimestamp
    @Column(name = "LAST_MODIFY_TIME", columnDefinition="TIMESTAMP")
    private ZonedDateTime lastModifyTime;

    public BigInteger getId() {
        return new BigInteger(this.id);
    }

    public ClientCertificate(){
        this.status = ICertificateStatus.UNKNOWN;
    }

    public ClientCertificate(X509Certificate certificate){
        this.status = ICertificateStatus.GOOD;
        this.publicKey = certificate.getPublicKey();
        this.expireTime = certificate.getNotAfter();
        this.avaliableTime = certificate.getNotBefore();
        this.id = certificate.getSerialNumber().toByteArray();
    }
}
