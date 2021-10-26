package com.hanzec.P2PFileSyncServer.service;

import com.hanzec.P2PFileSyncServer.config.params.CACertificateConfigParams;
import com.hanzec.P2PFileSyncServer.config.params.ClientCertificateConfigParams;
import com.hanzec.P2PFileSyncServer.config.params.GeneralCertificateConfigParams;
import com.hanzec.P2PFileSyncServer.config.params.TrueStoreConfigParams;
import com.hanzec.P2PFileSyncServer.model.data.certificate.ClientCertificate;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.ClientAccount;
import com.hanzec.P2PFileSyncServer.model.exception.certificate.CertificateGenerateException;
import com.hanzec.P2PFileSyncServer.repository.certificate.ClientCertificateRepository;
import com.hanzec.P2PFileSyncServer.utils.ByteArrayUtil;
import com.hanzec.P2PFileSyncServer.utils.PKCS12CertificateUtils;
import com.hanzec.P2PFileSyncServer.utils.X509CertificateUtils;
import io.lettuce.core.StrAlgoArgs;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.pkcs.PKCS12PfxPdu;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.util.ByteUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriBuilder;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import static com.hanzec.P2PFileSyncServer.utils.ByteArrayUtil.ByteArrayToHex;


@Service
public class CertificateService {
    private final ContentSigner urlSigner;
    private final ContentVerifier urlVerifier;
    private final ContentSigner clientSigner;
    private final X509Certificate[] rootCertificate;
    private final X509Certificate[] urlSignCertificate;
    private final X509Certificate[] clientSignCertificate;
    private final ClientCertificateRepository clientCertificateRepository;
    private final ClientCertificateConfigParams clientCertificateConfigParams;
    private final GeneralCertificateConfigParams generalCertificateConfigParams;
    private final Logger logger = LoggerFactory.getLogger(CertificateService.class);

    CertificateService(TrueStoreConfigParams params,
                       CACertificateConfigParams caCertificateConfigParams,
                       ClientCertificateRepository clientCertificateRepository,
                       ClientCertificateConfigParams clientCertificateConfigParams,
                       GeneralCertificateConfigParams generalCertificateConfigParams) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException, OperatorCreationException, UnrecoverableEntryException, SignatureException, IOException, InvalidKeyException {
        Path truthStoragePath = Paths.get(params.getTrustStorePath());
        this.clientCertificateRepository = clientCertificateRepository;
        this.clientCertificateConfigParams = clientCertificateConfigParams;
        this.generalCertificateConfigParams = generalCertificateConfigParams;

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

            rootSigner = new JcaContentSignerBuilder(caCertificateConfigParams.getSingedAlgorithm())
                    .setProvider("BC").build(rootCertificatePair.getPrivateKey());
            rootCertificate = (X509Certificate[]) rootCertificatePair.getCertificateChain();
        } else {
            logger.warn("Failed looking for [" + caCertificateConfigParams.getRootCertificateSubject() + "], will generate instead!");

            regenerateFlag = false;

            // generate key pair
            KeyPair keypair = keyPairGenerator.generateKeyPair();

            // generate x509 issuer
            X500NameBuilder nameBuilder = new X500NameBuilder();
            nameBuilder.addRDN(BCStyle.C, generalCertificateConfigParams.getCountryCode());
            nameBuilder.addRDN(BCStyle.O, generalCertificateConfigParams.getOrigination());
            nameBuilder.addRDN(BCStyle.OU, caCertificateConfigParams.getRootCertificateSubject());

            rootSigner = new JcaContentSignerBuilder(caCertificateConfigParams.getSingedAlgorithm())
                    .setProvider("BC").build(keypair.getPrivate());
            rootCertificate = new X509Certificate[]{X509CertificateUtils.generateRootCertificate(
                    caCertificateConfigParams.getExpireYears(), caCertificateConfigParams.getExpireMonths(),
                    caCertificateConfigParams.getExpireDays(), nameBuilder.build(), keypair, rootSigner)};

            keys.setEntry(
                    caCertificateConfigParams.getRootCertificateSubject(),
                    new KeyStore.PrivateKeyEntry(keypair.getPrivate(), rootCertificate), password);
        }
        logger.debug(caCertificateConfigParams.getRootCertificateSubject() + ": \n " + getRootCertificate().toString());


        // get certificate for sign url
        if (keys.isKeyEntry(caCertificateConfigParams.getUrlSignCertificateSubject()) && regenerateFlag) {
            var urlCertificatePair = (KeyStore.PrivateKeyEntry) keys.getEntry(
                    caCertificateConfigParams.getUrlSignCertificateSubject(), password);

            urlSigner = new JcaContentSignerBuilder(caCertificateConfigParams.getSingedAlgorithm())
                    .setProvider("BC").build(urlCertificatePair.getPrivateKey());
            urlVerifier = new JcaContentVerifierProviderBuilder()
                    .setProvider("BC").build(urlCertificatePair.getCertificate().getPublicKey()).get(urlSigner.getAlgorithmIdentifier());
            urlSignCertificate = (X509Certificate[]) urlCertificatePair.getCertificateChain();
        }else{
            logger.warn("Failed looking for [" + caCertificateConfigParams.getUrlSignCertificateSubject() + "], will generate instead!");

            // generate key pair
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // generate x509 issuer
            X500NameBuilder nameBuilder = new X500NameBuilder();
            nameBuilder.addRDN(BCStyle.C, generalCertificateConfigParams.getCountryCode());
            nameBuilder.addRDN(BCStyle.O, generalCertificateConfigParams.getOrigination());
            nameBuilder.addRDN(BCStyle.OU, caCertificateConfigParams.getUrlSignCertificateSubject());

            urlSigner = new JcaContentSignerBuilder(caCertificateConfigParams.getSingedAlgorithm())
                    .setProvider("BC").build(keyPair.getPrivate());
            urlVerifier = new JcaContentVerifierProviderBuilder()
                    .setProvider("BC").build(keyPair.getPublic()).get(urlSigner.getAlgorithmIdentifier());
            urlSignCertificate = new X509Certificate[]{
                    X509CertificateUtils.generateIntermediateCertificate(
                            caCertificateConfigParams.getExpireYears(), caCertificateConfigParams.getExpireMonths(),
                            caCertificateConfigParams.getExpireDays(), nameBuilder.build(), keyPair.getPublic(), rootCertificate[0], rootSigner),
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

            clientSigner = new JcaContentSignerBuilder(caCertificateConfigParams.getSingedAlgorithm())
                    .setProvider("BC").build(clientSignCertificatePair.getPrivateKey());
            clientSignCertificate = (X509Certificate[]) clientSignCertificatePair.getCertificateChain();
        }else{
            logger.warn("Failed locking for [" + caCertificateConfigParams.getClientSignCertificateSubject() + "], will generate instead!");

            // generate key pair
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // generate x509 issuer
            X500NameBuilder nameBuilder = new X500NameBuilder();
            nameBuilder.addRDN(BCStyle.C, generalCertificateConfigParams.getCountryCode());
            nameBuilder.addRDN(BCStyle.O, generalCertificateConfigParams.getOrigination());
            nameBuilder.addRDN(BCStyle.OU, caCertificateConfigParams.getClientSignCertificateSubject());

            clientSigner = new JcaContentSignerBuilder(caCertificateConfigParams.getSingedAlgorithm())
                    .setProvider("BC").build(keyPair.getPrivate());
            clientSignCertificate = new X509Certificate[]{
                    X509CertificateUtils.generateIntermediateCertificate(
                            caCertificateConfigParams.getExpireYears(), caCertificateConfigParams.getExpireMonths(),
                            caCertificateConfigParams.getExpireDays(), nameBuilder.build(), keyPair.getPublic(), rootCertificate[0], rootSigner),
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

    public String signUrl(String url) throws IOException {
        urlSigner.getOutputStream().write(url.getBytes(StandardCharsets.UTF_8));
        return ByteArrayToHex(urlSigner.getSignature());
    }

    public boolean verifyUrl(String url, String sign) throws IOException {
        urlVerifier.getOutputStream().write(url.getBytes(StandardCharsets.UTF_8));
        return urlVerifier.verify(ByteArrayUtil.hexStringToByteArray(sign));
    }

    // todo do not set RDN for new certificate
    public PKCS12PfxPdu generateNewClientCertificate(ClientAccount clientAccount) throws CertificateGenerateException {
        PKCS12PfxPdu ret;
        X509Certificate[] chain;
        KeyPairGenerator keyPairGenerator;

        try{
            keyPairGenerator = KeyPairGenerator.getInstance(
                    clientCertificateConfigParams.getAlgorithm());

            if(Objects.equals(clientCertificateConfigParams.getAlgorithm(), "RSA")){
                keyPairGenerator.initialize(clientCertificateConfigParams.getPrivateKeySize());
            } else if (Objects.equals(clientCertificateConfigParams.getAlgorithm(), "ECDSA")){
                ECParameterSpec ecSpec = ECNamedCurveTable
                        .getParameterSpec(clientCertificateConfigParams.getEcCurveTable());
                keyPairGenerator.initialize(ecSpec, new SecureRandom());
            }

            // generate key pair
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // generate x509 issuer
            X500NameBuilder subjectBuilder = new X500NameBuilder();
            subjectBuilder.addRDN(BCStyle.C, generalCertificateConfigParams.getCountryCode());
            subjectBuilder.addRDN(BCStyle.O, generalCertificateConfigParams.getOrigination());
            subjectBuilder.addRDN(BCStyle.CN, clientCertificateConfigParams.getSubjectPrefix() + clientAccount.getMachineID());

            chain = new X509Certificate[]{
                    X509CertificateUtils.generateClientX509Certificate(
                            clientCertificateConfigParams.getExpireYears(), clientCertificateConfigParams.getExpireMonths(),
                            clientCertificateConfigParams.getExpireDays(), subjectBuilder.build(),keyPair.getPublic(),
                            getClientSignCertificate(), clientAccount.getIpAddress(), clientAccount.getMachineID(), clientSigner),
                    clientSignCertificate[0], clientSignCertificate[1]};

            ret = PKCS12CertificateUtils.generatePKCS12Certificate(chain, keyPair, null);
        } catch (NoSuchAlgorithmException | CertificateException | SignatureException | OperatorCreationException | NoSuchProviderException | IOException | PKCSException | InvalidKeyException | InvalidAlgorithmParameterException e){
            e.printStackTrace();
            throw new CertificateGenerateException(e);
        }

        // update client certificate database
        clientCertificateRepository.save(new ClientCertificate(chain[0]));

        return ret;
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
