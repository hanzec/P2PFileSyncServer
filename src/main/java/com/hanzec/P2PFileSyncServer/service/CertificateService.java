package com.hanzec.P2PFileSyncServer.service;

import com.hanzec.P2PFileSyncServer.config.params.CACertificateConfigParams;
import com.hanzec.P2PFileSyncServer.config.params.ClientCertificateConfigParams;
import com.hanzec.P2PFileSyncServer.config.params.GeneralCertificateConfigParams;
import com.hanzec.P2PFileSyncServer.config.params.TrueStoreConfigParams;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.ClientAccount;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.UserAccount;
import com.hanzec.P2PFileSyncServer.utils.PKCS12CertificateUtils;
import com.hanzec.P2PFileSyncServer.utils.X509CertificateUtils;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.PKCS12Util;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.pkcs.PKCS12PfxPdu;
import org.bouncycastle.pkcs.PKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.bc.BcPKCS12PBEOutputEncryptorBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS12SafeBagBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bouncycastle.asn1.isismtt.ocsp.RequestedCertificate.certificate;

@Service
public class CertificateService {
    private final ContentSigner urlSigner;
    private final ContentSigner clientSigner;
    private final X509Certificate[] rootCertificate;
    private final X509Certificate[] urlSignCertificate;
    private final X509Certificate[] clientSignCertificate;
    private final ClientCertificateConfigParams clientCertificateConfigParams;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    CertificateService(TrueStoreConfigParams params,
                       CACertificateConfigParams caCertificateConfigParams,
                       ClientCertificateConfigParams clientCertificateConfigParams,
                       GeneralCertificateConfigParams generalCertificateConfigParams) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException, OperatorCreationException, UnrecoverableEntryException, SignatureException, IOException {
        Path truthStoragePath = Paths.get(params.getTrustStorePath());
        this.clientCertificateConfigParams = clientCertificateConfigParams;

        Security.addProvider(new BouncyCastleProvider());
        KeyStore keys = KeyStore.getInstance("BKS", "BC");
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

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(caCertificateConfigParams.getAlgorithm());
        keyPairGenerator.initialize(caCertificateConfigParams.getPrivateKeySize());

        ContentSigner rootSigner;
        boolean regenerateFlag = true;

        // check root certificate
        if (keys.isKeyEntry(caCertificateConfigParams.getRootCertificateSubject())) {
            var rootCertificatePair = (KeyStore.PrivateKeyEntry) keys.getEntry(
                    caCertificateConfigParams.getRootCertificateSubject(), password);

            rootSigner = new JcaContentSignerBuilder(generalCertificateConfigParams.getSingedAlgorithm())
                    .setProvider("BC").build(rootCertificatePair.getPrivateKey());
            rootCertificate = (X509Certificate[]) rootCertificatePair.getCertificateChain();
        } else {
            logger.warn("Failed looking for [" + caCertificateConfigParams.getRootCertificateSubject() + "], will generate instead!");

            regenerateFlag = false;
            KeyPair keypair = keyPairGenerator.generateKeyPair();

            rootSigner = new JcaContentSignerBuilder(generalCertificateConfigParams.getSingedAlgorithm())
                    .setProvider("BC").build(keypair.getPrivate());
            rootCertificate = new X509Certificate[]{X509CertificateUtils.generateSelfSignedX509Certificate(
                    caCertificateConfigParams.getExpireYears(), caCertificateConfigParams.getExpireMonths(),
                    caCertificateConfigParams.getExpireDays(), "CN=" + caCertificateConfigParams.getRootCertificateSubject(), keypair, rootSigner)};

            keys.setEntry(
                    caCertificateConfigParams.getRootCertificateSubject(),
                    new KeyStore.PrivateKeyEntry(keypair.getPrivate(), rootCertificate), password);
        }
        logger.debug(caCertificateConfigParams.getRootCertificateSubject() + ": \n " + getRootCertificate().toString());


        // get certificate for sign url
        if (keys.isKeyEntry(caCertificateConfigParams.getUrlSignCertificateSubject()) && regenerateFlag) {
            var urlCertificatePair = (KeyStore.PrivateKeyEntry) keys.getEntry(
                    caCertificateConfigParams.getUrlSignCertificateSubject(), password);

            urlSigner = new JcaContentSignerBuilder(generalCertificateConfigParams.getSingedAlgorithm())
                    .setProvider("BC").build(urlCertificatePair.getPrivateKey());
            urlSignCertificate = (X509Certificate[]) urlCertificatePair.getCertificateChain();
        }else{
            logger.warn("Failed looking for [ " + caCertificateConfigParams.getUrlSignCertificateSubject() + " ], will generate instead!");

            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            urlSigner = new JcaContentSignerBuilder(generalCertificateConfigParams.getSingedAlgorithm())
                    .setProvider("BC").build(keyPair.getPrivate());
            urlSignCertificate = new X509Certificate[]{
                    X509CertificateUtils.generateSubRootX509Certificate(
                            caCertificateConfigParams.getExpireYears(), caCertificateConfigParams.getExpireMonths(),
                            caCertificateConfigParams.getExpireDays(), "CN=" + caCertificateConfigParams.getUrlSignCertificateSubject(),
                            keyPair, getRootCertificate(), rootSigner),
                    getRootCertificate()};
            keys.setEntry(
                    caCertificateConfigParams.getUrlSignCertificateSubject(),
                    new KeyStore.PrivateKeyEntry(keyPair.getPrivate(), urlSignCertificate), password);
        }
        logger.debug(caCertificateConfigParams.getUrlSignCertificateSubject() + ": \n " + getUrlSignCertificate().toString());

        // get certificate for sign client cert
        if(keys.isKeyEntry(caCertificateConfigParams.getClientSignCertificateSubject()) && regenerateFlag){
            var clientSignCertificatePair = (KeyStore.PrivateKeyEntry) keys.getEntry(
                    caCertificateConfigParams.getClientSignCertificateSubject(), password);

            clientSigner = new JcaContentSignerBuilder(generalCertificateConfigParams.getSingedAlgorithm())
                    .setProvider("BC").build(clientSignCertificatePair.getPrivateKey());
            clientSignCertificate = (X509Certificate[]) clientSignCertificatePair.getCertificateChain();
        }else{
            logger.warn("Failed locking for client sign certificate, will generate instead!");

            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            clientSigner = new JcaContentSignerBuilder(generalCertificateConfigParams.getSingedAlgorithm())
                    .setProvider("BC").build(keyPair.getPrivate());
            clientSignCertificate = new X509Certificate[]{
                    X509CertificateUtils.generateSubRootX509Certificate(
                            caCertificateConfigParams.getExpireYears(), caCertificateConfigParams.getExpireMonths(),
                            caCertificateConfigParams.getExpireDays(), "CN=" + caCertificateConfigParams.getClientSignCertificateSubject(),
                            keyPair, getRootCertificate(), rootSigner),
                    getRootCertificate()};
            keys.setEntry(
                    caCertificateConfigParams.getClientSignCertificateSubject(),
                    new KeyStore.PrivateKeyEntry(keyPair.getPrivate(), clientSignCertificate), password);
        }
        logger.debug(caCertificateConfigParams.getClientSignCertificateSubject() + ": \n " + getClientSignCertificate().toString());

        // saving keystore file to disk
        keys.store(new FileOutputStream(truthStoragePath.toAbsolutePath().toString()), params.getTrustStorePassword().toCharArray());
    }


    protected X509Certificate getRootCertificate(){ return rootCertificate[0];}

    protected X509Certificate getUrlSignCertificate(){ return urlSignCertificate[0];}

    protected X509Certificate getClientSignCertificate(){ return clientSignCertificate[0];}


    // todo do not set RDN for new certificate
    public PKCS12PfxPdu generateNewClientCertificate(ClientAccount clientAccount) throws NoSuchAlgorithmException, CertificateException, SignatureException, OperatorCreationException, NoSuchProviderException, IOException, PKCSException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                clientCertificateConfigParams.getAlgorithm());
        keyPairGenerator.initialize(clientCertificateConfigParams.getPrivateKeySize());

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        X509Certificate[] chain = new X509Certificate[]{
                X509CertificateUtils.generateClientX509Certificate(
                        clientCertificateConfigParams.getExpireYears(), clientCertificateConfigParams.getExpireMonths(),
                        clientCertificateConfigParams.getExpireDays(), "CN=" + clientCertificateConfigParams.getSubjectPrefix() + clientAccount.getMachineID(),
                        keyPair, getRootCertificate(), clientAccount.getIpAddress(), clientAccount.getMachineID(), clientSigner),
                clientSignCertificate[0], clientSignCertificate[1]};

        return PKCS12CertificateUtils.generatePKCS12Certificate(chain, keyPair, null);
    }

    public CMSSignedData getClientSignPublicCertificate() throws CertificateEncodingException, CMSException, OperatorCreationException {
        CMSSignedDataGenerator generator = new CMSSignedDataGenerator();

        generator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(
                new JcaDigestCalculatorProviderBuilder()
                .setProvider("BC").build())
                .build(clientSigner, getClientSignCertificate()));
        generator.addCertificates(new JcaCertStore(Arrays.asList(clientSignCertificate)));

        return generator.generate( new CMSProcessableByteArray(getClientSignCertificate().getEncoded()), true);
    }
}
