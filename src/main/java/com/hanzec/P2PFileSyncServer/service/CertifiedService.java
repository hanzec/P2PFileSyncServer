package com.hanzec.P2PFileSyncServer.service;

import com.hanzec.P2PFileSyncServer.config.params.TrueStoreConfigParams;
import com.hanzec.P2PFileSyncServer.utils.X509CertificateUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Service
public class CertifiedService {
    private final KeyStore keys;
    private final PrivateKey urlSignPrivateKey;
    private final PrivateKey clientSignPrivateKey;
    private final X509Certificate urlSignCertificate;
    private final X509Certificate clientSignCertificate;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    CertifiedService(TrueStoreConfigParams params) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException, OperatorCreationException, UnrecoverableEntryException, SignatureException, IOException {
        Path truthStoragePath = Paths.get(params.getTrustStorePath());

        Security.addProvider(new BouncyCastleProvider());
        keys = KeyStore.getInstance("BKS", "BC");
        var password = new KeyStore.PasswordProtection(params.getTrustStorePassword().toCharArray());

        // if key store file not existed
        try {
            keys.load(new FileInputStream(truthStoragePath.toAbsolutePath().toString()), params.getTrustStorePassword().toCharArray());
            logger.info("loading certificate from [" + truthStoragePath.toAbsolutePath() + "]");
        } catch (IOException e) {
            // if keyStore not found then need to do load first
            keys.load(null, null);

            // if folders not existed
            if(!Files.exists(truthStoragePath)){
                Files.createDirectories(truthStoragePath.getParent());
                logger.warn("truth store folder not found in [" + truthStoragePath.toAbsolutePath() + "], will create instead!");
            }
        }

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(4096);

        boolean regenerateFlag = true;
        PrivateKey rootPrivate;
        X509Certificate rootCertificate;

        // check root certificate
        if (keys.isKeyEntry("ROOT_CERTIFICATE")) {
            var rootCertificatePair = (KeyStore.PrivateKeyEntry) keys.getEntry("ROOT_CERTIFICATE", password);
            rootPrivate = rootCertificatePair.getPrivateKey();
            rootCertificate = (X509Certificate) rootCertificatePair.getCertificate();
            logger.debug("ROOT Certificate: \n " + rootCertificate.toString());
        } else {
            logger.warn("Failed looking for root certificate, will generate instead!");
            regenerateFlag = false;
            KeyPair keypair = keyPairGenerator.generateKeyPair();

            rootPrivate = keypair.getPrivate();
            rootCertificate = X509CertificateUtils.generateSelfSignedX509Certificate(
                    10, 0, 0, "CN=P2P_FILE_SYNC_ROOT_CERTIFICATE", keypair);
            keys.setEntry(
                    "ROOT_CERTIFICATE",
                    new KeyStore.PrivateKeyEntry(keypair.getPrivate(), new X509Certificate[]{rootCertificate}), password);
        }

        // get certificate for sign url
        if (keys.isKeyEntry("URL_CERTIFICATE") && regenerateFlag) {
            var urlCertificatePair = (KeyStore.PrivateKeyEntry) keys.getEntry("URL_CERTIFICATE", password);
            urlSignPrivateKey = urlCertificatePair.getPrivateKey();
            urlSignCertificate = (X509Certificate) urlCertificatePair.getCertificate();
            logger.debug("URL Sign Certificate: \n " + urlSignCertificate.toString());
        }else{
            logger.warn("Failed looking for url sign certificate, will generate instead!");

            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            urlSignPrivateKey = keyPair.getPrivate();
            urlSignCertificate = X509CertificateUtils.generateSignedX509Certificate(
                    10, 0, 0, "CN=P2P_FILE_SYNC_URL_SIGN_CERTIFICATE", keyPair, rootCertificate, rootPrivate);
            keys.setEntry("URL_CERTIFICATE", new KeyStore.PrivateKeyEntry(keyPair.getPrivate(), new X509Certificate[]{rootCertificate}), password);
        }

        // get certificate for sign client cert
        if(keys.isKeyEntry("CLIENT_CERTIFICATE") && regenerateFlag){
            var clientSignCertificatePair = (KeyStore.PrivateKeyEntry) keys.getEntry("CLIENT_CERTIFICATE", password);
            clientSignPrivateKey = clientSignCertificatePair.getPrivateKey();
            clientSignCertificate = (X509Certificate) clientSignCertificatePair.getCertificate();
            logger.debug("Client Sign Certificate: \n " + clientSignCertificate.toString());
        }else{
            logger.warn("Failed locking for client sign certificate, will generate instead!");

            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            clientSignPrivateKey = keyPair.getPrivate();
            clientSignCertificate = X509CertificateUtils.generateSignedX509Certificate(
                    10, 0, 0, "CN=P2P_FILE_SYNC_CLIENT_SIGN_CERTIFICATE", keyPair, rootCertificate, rootPrivate);
            keys.setEntry("CLIENT_CERTIFICATE", new KeyStore.PrivateKeyEntry(keyPair.getPrivate(), new X509Certificate[]{rootCertificate}), password);
        }
        keys.store(new FileOutputStream(truthStoragePath.toAbsolutePath().toString()), params.getTrustStorePassword().toCharArray());
    }
}
