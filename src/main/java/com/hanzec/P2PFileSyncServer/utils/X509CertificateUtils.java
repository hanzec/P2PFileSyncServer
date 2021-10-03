package com.hanzec.P2PFileSyncServer.utils;


import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
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
import org.bouncycastle.util.encoders.Base64;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class X509CertificateUtils {
    private static final String BC_PROVIDER = "BC";

    public static X509Certificate generateSelfSignedX509Certificate(int yearsFromToday, int monthsFromToday, int daysFromToday, String subject, KeyPair keyPair, ContentSigner signer) throws CertificateException, NoSuchAlgorithmException, OperatorCreationException, CertIOException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, daysFromToday);
        calendar.add(Calendar.YEAR, yearsFromToday);
        calendar.add(Calendar.MONTH, monthsFromToday);
        return generateSelfSignedX509Certificate(new Date(), calendar.getTime(), subject,keyPair, signer);
    }

    public static X509Certificate generateSelfSignedX509Certificate(Date notAfter, Date notBefore, String subject, KeyPair keyPair, ContentSigner signer) throws NoSuchAlgorithmException, CertIOException, CertificateException, OperatorCreationException {
        BigInteger rootSerialNum = new BigInteger(Long.toString(new SecureRandom().nextLong()));

        // Issued By and Issued To same for root certificate
        X500Name rootCertIssuer = new X500Name(subject);
        X509v3CertificateBuilder rootCertBuilder = new JcaX509v3CertificateBuilder(rootCertIssuer,
                                                                                   rootSerialNum,
                                                                                   notAfter, notBefore,
                                                                                   rootCertIssuer, keyPair.getPublic());
        // Add Extensions
        // A BasicConstraint to mark root certificate as CA certificate
        JcaX509ExtensionUtils rootCertExtUtils = new JcaX509ExtensionUtils();
        rootCertBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
        rootCertBuilder.addExtension(Extension.subjectKeyIdentifier, false, rootCertExtUtils.createSubjectKeyIdentifier(keyPair.getPublic()));

        return new JcaX509CertificateConverter().setProvider(BC_PROVIDER).getCertificate(rootCertBuilder.build(signer));
    }

    public static X509Certificate generateClientX509Certificate(int yearsFromToday, int monthsFromToday, int daysFromToday, String subject, KeyPair keyPair, X509Certificate rootCert,  String ipAddress, String machineID ,ContentSigner rootPrivateKey) throws CertificateException, NoSuchAlgorithmException, SignatureException, OperatorCreationException, NoSuchProviderException, CertIOException {
        return generateSignedX509Certificate(yearsFromToday, monthsFromToday, daysFromToday, subject, keyPair, rootCert, rootPrivateKey, ipAddress, machineID, new KeyUsage(KeyUsage.digitalSignature|KeyUsage.encipherOnly|KeyUsage.decipherOnly));
    }

    public static X509Certificate generateSubRootX509Certificate(int yearsFromToday, int monthsFromToday, int daysFromToday, String subject, KeyPair keyPair, X509Certificate rootCert, ContentSigner rootPrivateKey) throws CertificateException, NoSuchAlgorithmException, SignatureException, OperatorCreationException, NoSuchProviderException, CertIOException {
        return generateSignedX509Certificate(yearsFromToday, monthsFromToday, daysFromToday, subject, keyPair, rootCert, rootPrivateKey, null, null, new KeyUsage(KeyUsage.keyCertSign));
    }

    /**
     *  Helper function for easier generate X509Certificate which will automatically generate signed certificate available from today
     *
     * @param yearsFromToday number of available years from today
     * @param monthsFromToday number of available months from today
     * @param daysFromToday number of available days from today
     * @param subject the subject of certificates
     * @param keyPair the generated private/public keypair
     * @param rootCert the certificate which will sign the current generated certificate
     * @param rootPrivateKey the private key of certificate which will sign the current generated certificate
     * @return the final generated X509Certificate, note that will NULL if generating FAILED
     * @throws NoSuchAlgorithmException if no such algorithm
     * @throws CertIOException if certificate not exist
     * @throws CertificateException if certificate error
     * @throws OperatorCreationException if cannot create
     * @throws SignatureException if signature error
     * @throws NoSuchProviderException if no such provider
     */
    public static X509Certificate generateSignedX509Certificate(int yearsFromToday, int monthsFromToday, int daysFromToday, String subject, KeyPair keyPair, X509Certificate rootCert, ContentSigner rootPrivateKey, String ipAddress, String machineID, KeyUsage keyUsage) throws CertificateException, NoSuchAlgorithmException, OperatorCreationException, CertIOException, SignatureException, NoSuchProviderException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, daysFromToday);
        calendar.add(Calendar.YEAR, yearsFromToday);
        calendar.add(Calendar.MONTH, monthsFromToday);
        return generateSignedX509Certificate(new Date(), calendar.getTime(), subject, keyPair, rootCert, rootPrivateKey, ipAddress, machineID, keyUsage);
    }

    /**
     *  Methods for generating new X509Certificate
     *
     * @apiNote if one of the @param machineID or @param ipAddress is null then both fields will not insert to final certificates
     * @param notAfter available dates for certificates
     * @param notBefore expire dates for certificates
     * @param subject the subject of certificates
     * @param keyPair the generated private/public keypair
     * @param rootCert the certificate which will sign the current generated certificate
     * @param csrContentSigner the private key of certificate which will sign the current generated certificate
     * @param ipAddress the ip address which will put to GeneralName.iPAddress under subjectAlternativeName section
     * @param machineID the machine id which will put to GeneralName.uniformResourceIdentifier under subjectAlternativeName section
     * @param keyUsage list of usage which will used for this certificates
     * @return the final generated X509Certificate, note that will NULL if generating FAILED
     * @throws NoSuchAlgorithmException if no such algorithm
     * @throws CertIOException if certificate not exist
     * @throws CertificateException if certificate error
     * @throws OperatorCreationException if cannot create
     * @throws SignatureException if signature error
     * @throws NoSuchProviderException if no such provider
     */
    public static X509Certificate generateSignedX509Certificate(Date notAfter, Date notBefore,
                                                                String subject, KeyPair keyPair,
                                                                X509Certificate rootCert, ContentSigner csrContentSigner,
                                                                String ipAddress, String machineID, KeyUsage keyUsage) throws NoSuchAlgorithmException, CertIOException, CertificateException, OperatorCreationException, SignatureException, NoSuchProviderException {
        X500Name issuedCertSubject = new X500Name(subject);
        BigInteger issuedCertSerialNum = new BigInteger(Long.toString(new SecureRandom().nextLong()));

        PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(issuedCertSubject, keyPair.getPublic());

        // Sign the new KeyPair with the root cert Private Key
        PKCS10CertificationRequest csr = p10Builder.build(csrContentSigner);

        // Use the Signed KeyPair and CSR to generate an issued Certificate
        // Here serial number is randomly generated. In general, CAs use
        // a sequence to generate Serial number and avoid collisions
        X500Name issuerSubject = new X500Name(rootCert.getIssuerX500Principal().getName());
        X509v3CertificateBuilder issuedCertBuilder = new X509v3CertificateBuilder(issuerSubject, issuedCertSerialNum, notBefore, notAfter, csr.getSubject(), csr.getSubjectPublicKeyInfo());

        JcaX509ExtensionUtils issuedCertExtUtils = new JcaX509ExtensionUtils();

        // Use BasicConstraints to say that this Cert is not a CA
        issuedCertBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(false));

        // Add Issuer cert identifier as Extension
        issuedCertBuilder.addExtension(Extension.subjectKeyIdentifier, false,
                issuedCertExtUtils.createSubjectKeyIdentifier(csr.getSubjectPublicKeyInfo()));
        issuedCertBuilder.addExtension(Extension.authorityKeyIdentifier, false, issuedCertExtUtils.createAuthorityKeyIdentifier(rootCert));

        // Add intended key usage extension if needed
        issuedCertBuilder.addExtension(Extension.keyUsage, false, keyUsage);

        // Add DNS name is cert is to used for SSL
        if(ipAddress != null && machineID != null)
            issuedCertBuilder.addExtension(Extension.subjectAlternativeName, false, new DERSequence(new ASN1Encodable[] {
                    new GeneralName(GeneralName.iPAddress, ipAddress),
                    new GeneralName(GeneralName.uniformResourceIdentifier, machineID),
            }));

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

    public static byte[] writeCertAsBase64Encoded(X509Certificate certificate) throws CertificateEncodingException {
        String sb = "-----BEGIN CERTIFICATE-----\n" +
                    Base64.toBase64String(certificate.getEncoded()) +
                    "-----END CERTIFICATE-----\n";
        return sb.getBytes(StandardCharsets.UTF_8);
    }
}
