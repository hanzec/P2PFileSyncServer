package com.hanzec.P2PFileSyncServer.service;


import com.hanzec.P2PFileSyncServer.config.params.TrueStoreConfigParams;
import com.sun.jarsigner.ContentSigner;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509CertificateStructure;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import javax.annotation.PreDestroy;
import java.io.*;
import java.math.BigInteger;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;


@Service
public class CertifiedService {
    private final KeyStore keys;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    CertifiedService(TrueStoreConfigParams params) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        keys = KeyStore.getInstance("JSK");
        Security.addProvider(new BouncyCastleProvider());

        try{
            keys.load(new FileInputStream(params.getKeyStorePath()), params.getTrustStorePassword().toCharArray());
        }catch (FileNotFoundException e){
            logger.warn("KeyStore file [" + params.getKeyStorePath() + "] not found, will generated instead!");
            keys.load(null,null);
            keys.
        }

    }

    /**
     * Function handles when CertifiedService are exited
     * @param params
     * @throws IOException when keystore cannot write current keystore to disk
     * @throws CertificateException
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException when algorithm can not save to keystore
     */
    @PreDestroy
    private void shutdownService(TrueStoreConfigParams params) throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException {
        keys.store(new FileOutputStream(params.getKeyStorePath()),params.getTrustStorePassword().toCharArray());
    }

    public Certificate issueNewCertificates() throws NoSuchAlgorithmException, IOException {
        AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder()
                .find("SHA1withRSA");
        AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder()
                .find(sigAlgId);

        AsymmetricKeyParameter foo = PrivateKeyFactory.createKey(caPrivate
                .getEncoded());
        SubjectPublicKeyInfo keyInfo = SubjectPublicKeyInfo.getInstance(pair
                .getPublic().getEncoded());

        PKCS10CertificationRequestHolder pk10Holder = new PKCS10CertificationRequestHolder(inputCSR);
        //in newer version of BC such as 1.51, this is
        //PKCS10CertificationRequest pk10Holder = new PKCS10CertificationRequest(inputCSR);

        X509v3CertificateBuilder myCertificateGenerator = new X509v3CertificateBuilder(
                new X500Name("CN=issuer"), new BigInteger("1"), new Date(
                System.currentTimeMillis()), new Date(
                System.currentTimeMillis() + 30 * 365 * 24 * 60 * 60
                        * 1000), pk10Holder.getSubject(), keyInfo);

        ContentSigner sigGen = new BcRSAContentSignerBuilder(sigAlgId, digAlgId)
                .build(foo);

        X509CertificateHolder holder = myCertificateGenerator.build(sigGen);
        Certificate eeX509CertificateStructure = holder.toASN1Structure();
        //in newer version of BC such as 1.51, this is
        //org.spongycastle.asn1.x509.Certificate eeX509CertificateStructure = holder.toASN1Structure();

        CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");

        // Read Certificate
        InputStream is1 = new ByteArrayInputStream(eeX509CertificateStructure.getEncoded());
        X509Certificate theCert = (X509Certificate) cf.generateCertificate(is1);
        is1.close();
        return theCert;
        //return null;
    }
}
