package com.hanzec.P2PFileSyncServer.utils;

import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.pkcs.*;
import org.bouncycastle.pkcs.bc.BcPKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.bc.BcPKCS12PBEOutputEncryptorBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS12SafeBagBuilder;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class PKCS12CertificateUtils {
    public static PKCS12PfxPdu generatePKCS12Certificate(X509Certificate[] chain, KeyPair keyPair, String password) throws NoSuchAlgorithmException, IOException, PKCSException {
        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
        PKCS12PfxPduBuilder pfxPduBuilder = new PKCS12PfxPduBuilder();

        for(int i = chain.length - 1 ; i >= 0 ; i--){
            PKCS12SafeBagBuilder bagBuilder = new JcaPKCS12SafeBagBuilder(chain[i])
                .addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName, new DERBMPString(chain[i].getSubjectX500Principal().getName()));

            // if loop to final certificate (first certificate in keychain) then will add its public key
            if( i == 0)
                bagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId, extUtils.createSubjectKeyIdentifier(keyPair.getPublic()));

            pfxPduBuilder.addData(bagBuilder.build());
        }

        // put private to bags
        PKCS12SafeBagBuilder keyBagBuilder;
        if(password != null){
            keyBagBuilder = new JcaPKCS12SafeBagBuilder(
                    keyPair.getPrivate(), new BcPKCS12PBEOutputEncryptorBuilder(
                        PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, new CBCBlockCipher(new DESedeEngine())).build(password.toCharArray()));
        }else{
            keyBagBuilder = new JcaPKCS12SafeBagBuilder(keyPair.getPrivate());
        }

        // set certificate friendly name
        keyBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName,
                new DERBMPString(chain[0].getSubjectX500Principal().getName()));

        // add public key to certificate
        keyBagBuilder.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId, extUtils.createSubjectKeyIdentifier(keyPair.getPublic()));

        pfxPduBuilder.addData(keyBagBuilder.build());

        if(password == null) {
            return pfxPduBuilder.build(new BcPKCS12MacCalculatorBuilder(), null);
        } else {
            return pfxPduBuilder.build(new BcPKCS12MacCalculatorBuilder(), password.toCharArray());
        }
    }
}
