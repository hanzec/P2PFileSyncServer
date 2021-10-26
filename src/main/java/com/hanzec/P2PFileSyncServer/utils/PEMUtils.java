package com.hanzec.P2PFileSyncServer.utils;


import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import java.io.*;

public class PEMUtils {
    public static byte[] ConvertPKCS7ToPEMBytes(CMSSignedData pkcs7Certificate) throws IOException {
        // generate buffer
        ByteArrayOutputStream arrayBuffer = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(arrayBuffer));

        ConvertPKCS7ToPEMToWriter(pkcs7Certificate, writer);

        return arrayBuffer.toByteArray();
    }

    public static void ConvertPKCS7ToPEMToWriter(CMSSignedData pkcs7Certificate, Writer writer) throws IOException {
        //writer to PEM format
        JcaPEMWriter pemWriter = new JcaPEMWriter(writer);
        pemWriter.writeObject(pkcs7Certificate.toASN1Structure());
        pemWriter.flush();
        pemWriter.close();
    }

}
