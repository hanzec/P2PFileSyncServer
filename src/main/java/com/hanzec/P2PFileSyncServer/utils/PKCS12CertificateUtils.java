package com.hanzec.P2PFileSyncServer.utils;

import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.pkcs.*;
import org.bouncycastle.pkcs.bc.BcPKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.bc.BcPKCS12PBEOutputEncryptorBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEOutputEncryptorBuilder;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class PKCS12CertificateUtils {
    public static PKCS12PfxPdu generatePKCS12Certificate(X509Certificate[] chain, String password, KeyPair keyPair) throws NoSuchAlgorithmException, IOException, PKCSException, OperatorCreationException {
        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
        PKCS12PfxPduBuilder pfxPduBuilder = new PKCS12PfxPduBuilder();
        OutputEncryptor encryptor = new JcePKCSPBEOutputEncryptorBuilder(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC).setProvider("BC").build(password.toCharArray());
        // put private to bags
        PKCS12SafeBagBuilder keyBagBuilder = new JcaPKCS12SafeBagBuilder(
                keyPair.getPrivate(), new JcePKCSPBEOutputEncryptorBuilder(NISTObjectIdentifiers.id_aes256_CBC).setProvider("BC").build(password.toCharArray()));

        // set certificate friendly name
        keyBagBuilder.addBagAttribute(PKCS12SafeBag.friendlyNameAttribute,
                new DERBMPString(chain[0].getSubjectX500Principal().getName()));

        // add public key to certificate
        keyBagBuilder.addBagAttribute(PKCS12SafeBag.localKeyIdAttribute, extUtils.createSubjectKeyIdentifier(keyPair.getPublic()));

        pfxPduBuilder.addData(keyBagBuilder.build());

        for (int i = chain.length - 1; i >= 0; i--) {
            PKCS12SafeBagBuilder bagBuilder = new JcaPKCS12SafeBagBuilder(chain[i])
                    .addBagAttribute(PKCS12SafeBag.friendlyNameAttribute, new DERBMPString(chain[i].getSubjectX500Principal().getName()));

            // if loop to final certificate (first certificate in keychain) then will add its public key
            if (i == 0)
                bagBuilder.addBagAttribute(PKCS12SafeBag.localKeyIdAttribute, extUtils.createSubjectKeyIdentifier(chain[0].getPublicKey()));

            pfxPduBuilder.addEncryptedData(encryptor, bagBuilder.build());
        }


        return pfxPduBuilder.build(new JcePKCS12MacCalculatorBuilder(NISTObjectIdentifiers.id_sha256), password.toCharArray());
    }
}
