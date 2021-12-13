package com.hanzec.P2PFileSyncServer.utils;


import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.encoders.Base64;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class X509CertificateUtils {

    public static X509Certificate generateSelfSignedX509Certificate(Date notAfter, Date notBefore, X500Name issuer, KeyPair keyPair, ContentSigner signer) throws NoSuchAlgorithmException, CertIOException, CertificateException, OperatorCreationException, SignatureException, InvalidKeyException, NoSuchProviderException {
        BigInteger rootSerialNum = new BigInteger(Long.toString(new SecureRandom().nextLong()));

        // Issued By and Issued To same for root certificate
        X509v3CertificateBuilder rootCertBuilder = new JcaX509v3CertificateBuilder(
                issuer, rootSerialNum, notAfter, notBefore, issuer, keyPair.getPublic());

        // A BasicConstraint to mark root certificate as CA certificate
        JcaX509ExtensionUtils rootCertExtUtils = new JcaX509ExtensionUtils();
        rootCertBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
        rootCertBuilder.addExtension(Extension.subjectKeyIdentifier, false, rootCertExtUtils.createSubjectKeyIdentifier(keyPair.getPublic()));

        // generate the final certificates
        X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(rootCertBuilder.build(signer));

        // verify the generated certificate
        cert.checkValidity(new Date());
        cert.verify(keyPair.getPublic());

        return cert;
    }

    /**
     * Methods for generating new X509Certificate
     *
     * @param notAfter  available dates for certificates
     * @param notBefore expire dates for certificates
     * @param subject   the subject of certificates
     * @param pubKey    the generated private/public keypair
     * @param caCert    the certificate which will sign the current generated certificate
     * @param caSigner  the private key of certificate which will sign the current generated certificate
     * @param ipAddress the ip address which will put to GeneralName.iPAddress under subjectAlternativeName section
     * @param machineID the machine id which will put to GeneralName.uniformResourceIdentifier under subjectAlternativeName section
     * @param keyUsage  list of usage which will used for this certificates
     * @return the final generated X509Certificate, note that will NULL if generating FAILED
     * @throws NoSuchAlgorithmException if no such algorithm
     * @throws CertIOException          if certificate not exist
     * @throws CertificateException     if certificate error
     * @throws SignatureException       if signature error
     * @throws NoSuchProviderException  if no such provider
     * @apiNote if one of the @param machineID or @param ipAddress is null then both fields will not insert to final certificates
     */
    public static X509Certificate generateSignedX509Certificate(Date notAfter, Date notBefore, X500Name subject,
                                                                PublicKey pubKey, X509Certificate caCert,
                                                                ContentSigner caSigner, KeyUsage keyUsage,
                                                                String ipAddress, String machineID) throws NoSuchAlgorithmException, CertIOException, CertificateException, SignatureException, InvalidKeyException, NoSuchProviderException {
        BigInteger rootSerialNum = new BigInteger(Long.toString(new SecureRandom().nextLong()));

        //
        // create the certificate - version 3
        //
        X509v3CertificateBuilder issuedCertBuilder = new JcaX509v3CertificateBuilder(caCert, rootSerialNum, notAfter, notBefore, subject, pubKey);

        //
        // extensions
        //
        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();

        // Add intended key usage extension if needed
        issuedCertBuilder.addExtension(Extension.keyUsage, false, keyUsage);

        // Use BasicConstraints to say that this Cert is not a CA
        issuedCertBuilder.addExtension(Extension.basicConstraints, true,
                new BasicConstraints(keyUsage.hasUsages(KeyUsage.keyCertSign)));

        // Add Issuer cert identifier as Extension
        issuedCertBuilder.addExtension(Extension.subjectKeyIdentifier, false,
                extUtils.createSubjectKeyIdentifier(pubKey));

        issuedCertBuilder.addExtension(Extension.authorityKeyIdentifier, false,
                extUtils.createAuthorityKeyIdentifier(caCert));

        // Add DNS name is cert is to used for SSL
        if (ipAddress != null && machineID != null) {
            ArrayList<ASN1Encodable> generalNames = new ArrayList<>();
            var ipAddressList = ipAddress.split(",");

            for (var ip : ipAddressList) {
                generalNames.add(new GeneralName(GeneralName.iPAddress, ip));
            }

            generalNames.add(new GeneralName(GeneralName.uniformResourceIdentifier, machineID));
            issuedCertBuilder.addExtension(Extension.subjectAlternativeName, false, new DERSequence(generalNames.toArray(new ASN1Encodable[0])));
        }


        X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(issuedCertBuilder.build(caSigner));

        cert.checkValidity(new Date());

        cert.verify(caCert.getPublicKey());

        return cert;
    }

    public static X509Certificate generateRootCertificate(
            int yearsFromToday, int monthsFromToday, int daysFromToday, X500Name issuer, KeyPair keyPair, ContentSigner signer) throws CertificateException, NoSuchAlgorithmException, OperatorCreationException, CertIOException, SignatureException, InvalidKeyException, NoSuchProviderException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, daysFromToday);
        calendar.add(Calendar.YEAR, yearsFromToday);
        calendar.add(Calendar.MONTH, monthsFromToday);
        return generateSelfSignedX509Certificate(new Date(), calendar.getTime(), issuer, keyPair, signer);
    }


    public static X509Certificate generateIntermediateCertificate(int yearsFromToday, int monthsFromToday, int daysFromToday, X500Name subject, PublicKey keyPair, X509Certificate rootCert, ContentSigner rootPrivateKey) throws CertificateException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, CertIOException, InvalidKeyException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, daysFromToday);
        calendar.add(Calendar.YEAR, yearsFromToday);
        calendar.add(Calendar.MONTH, monthsFromToday);
        return generateSignedX509Certificate(new Date(), calendar.getTime(), subject, keyPair, rootCert, rootPrivateKey, new KeyUsage(KeyUsage.keyCertSign), null, null);
    }

    public static X509Certificate generateClientX509Certificate(int yearsFromToday, int monthsFromToday, int daysFromToday, X500Name subject, PublicKey keyPair, X509Certificate rootCert, String ipAddress, String machineID, ContentSigner rootPrivateKey) throws CertificateException, NoSuchAlgorithmException, SignatureException, OperatorCreationException, NoSuchProviderException, CertIOException, InvalidKeyException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, daysFromToday);
        calendar.add(Calendar.YEAR, yearsFromToday);
        calendar.add(Calendar.MONTH, monthsFromToday);
        return generateSignedX509Certificate(new Date(), calendar.getTime(), subject, keyPair, rootCert, rootPrivateKey, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.encipherOnly | KeyUsage.decipherOnly), ipAddress, machineID);
    }


    public static byte[] writeCertAsBase64Encoded(X509Certificate certificate) throws CertificateEncodingException {
        String sb = "-----BEGIN CERTIFICATE-----\n" +
                Base64.toBase64String(certificate.getEncoded()) +
                "-----END CERTIFICATE-----\n";
        return sb.getBytes(StandardCharsets.UTF_8);
    }
}
