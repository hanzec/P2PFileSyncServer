package com.hanzec.P2PFileSyncServer.utils;


import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

public class X509CertificateUtils {
    private static final String BC_PROVIDER = "BC";
    private static final String KEY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    public static X509Certificate generateSelfSignedX509Certificate(int yearsFromToday, int monthsFromToday, int daysFromToday, String subject, KeyPair keyPair) throws CertificateException, NoSuchAlgorithmException, OperatorCreationException, CertIOException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, daysFromToday);
        calendar.add(Calendar.YEAR, yearsFromToday);
        calendar.add(Calendar.MONTH, monthsFromToday);
        return generateSelfSignedX509Certificate(new Date(), calendar.getTime(), subject,keyPair);
    }

    public static X509Certificate generateSelfSignedX509Certificate(Date notAfter, Date notBefore, String subject, KeyPair keyPair) throws NoSuchAlgorithmException, CertIOException, CertificateException, OperatorCreationException {
        BigInteger rootSerialNum = new BigInteger(Long.toString(new SecureRandom().nextLong()));

        // Issued By and Issued To same for root certificate
        X500Name rootCertIssuer = new X500Name(subject);
        ContentSigner rootCertContentSigner = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).setProvider(BC_PROVIDER).build(keyPair.getPrivate());
        X509v3CertificateBuilder rootCertBuilder = new JcaX509v3CertificateBuilder(rootCertIssuer,
                                                                                   rootSerialNum,
                                                                                   notAfter, notBefore,
                                                                                   rootCertIssuer, keyPair.getPublic());
        // Add Extensions
        // A BasicConstraint to mark root certificate as CA certificate
        JcaX509ExtensionUtils rootCertExtUtils = new JcaX509ExtensionUtils();
        rootCertBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
        rootCertBuilder.addExtension(Extension.subjectKeyIdentifier, false, rootCertExtUtils.createSubjectKeyIdentifier(keyPair.getPublic()));

        return new JcaX509CertificateConverter().setProvider(BC_PROVIDER).getCertificate(rootCertBuilder.build(rootCertContentSigner));
    }

    public static X509Certificate generateSignedX509Certificate(int yearsFromToday, int monthsFromToday, int daysFromToday, String subject, KeyPair keyPair, X509Certificate rootCert, PrivateKey rootPrivateKey) throws CertificateException, NoSuchAlgorithmException, OperatorCreationException, CertIOException, SignatureException, NoSuchProviderException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, daysFromToday);
        calendar.add(Calendar.YEAR, yearsFromToday);
        calendar.add(Calendar.MONTH, monthsFromToday);
        return generateSignedX509Certificate(new Date(), calendar.getTime(), subject, keyPair, rootCert, rootPrivateKey);
    }

    public static X509Certificate generateSignedX509Certificate(Date notAfter, Date notBefore, String subject, KeyPair keyPair, X509Certificate rootCert, PrivateKey rootPrivateKey) throws NoSuchAlgorithmException, CertIOException, CertificateException, OperatorCreationException, SignatureException, NoSuchProviderException {
        X500Name issuedCertSubject = new X500Name(subject);
        BigInteger issuedCertSerialNum = new BigInteger(Long.toString(new SecureRandom().nextLong()));

        PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(issuedCertSubject, keyPair.getPublic());
        JcaContentSignerBuilder csrBuilder = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).setProvider(BC_PROVIDER);

        // Sign the new KeyPair with the root cert Private Key
        ContentSigner csrContentSigner = csrBuilder.build(rootPrivateKey);
        PKCS10CertificationRequest csr = p10Builder.build(csrContentSigner);

        // Use the Signed KeyPair and CSR to generate an issued Certificate
        // Here serial number is randomly generated. In general, CAs use
        // a sequence to generate Serial number and avoid collisions
        X500Name issuerSubject = new X500Name(rootCert.getIssuerX500Principal().getName());
        X509v3CertificateBuilder issuedCertBuilder = new X509v3CertificateBuilder(issuerSubject, issuedCertSerialNum, notBefore, notAfter, csr.getSubject(), csr.getSubjectPublicKeyInfo());

        JcaX509ExtensionUtils issuedCertExtUtils = new JcaX509ExtensionUtils();

        // Add Extensions
        // Use BasicConstraints to say that this Cert is not a CA
        issuedCertBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));

        // Add Issuer cert identifier as Extension
        issuedCertBuilder.addExtension(Extension.authorityKeyIdentifier, false, issuedCertExtUtils.createAuthorityKeyIdentifier(rootCert));
        issuedCertBuilder.addExtension(Extension.subjectKeyIdentifier, false, issuedCertExtUtils.createSubjectKeyIdentifier(csr.getSubjectPublicKeyInfo()));

        // Add intended key usage extension if needed
        issuedCertBuilder.addExtension(Extension.keyUsage, false, new KeyUsage(KeyUsage.keyEncipherment));

//        // Add DNS name is cert is to used for SSL
//        issuedCertBuilder.addExtension(Extension.subjectAlternativeName, false, new DERSequence(new ASN1Encodable[] {
//                new GeneralName(GeneralName.iPAddress, ipAddress)
//        }));

        X509CertificateHolder issuedCertHolder = issuedCertBuilder.build(csrContentSigner);
        X509Certificate issuedCert  = new JcaX509CertificateConverter().setProvider(BC_PROVIDER).getCertificate(issuedCertHolder);

        // Verify the issued cert signature against the root (issuer) cert
        try {
            issuedCert.verify(rootCert.getPublicKey(), BC_PROVIDER);
        } catch (InvalidKeyException e) {
            return null;
        }

        return issuedCert;
    }
}
