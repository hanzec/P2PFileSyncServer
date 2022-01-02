package com.hanzec.P2PFileSyncServer.controller;

import com.hanzec.P2PFileSyncServer.service.CertificateService;
import com.hanzec.P2PFileSyncServer.utils.PEMUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.OperatorCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateEncodingException;

@Controller
@Tag(name = "Static webpage controller", description = "Controller for static webpages")
public class StaticPageController {

    private final byte[] clientSignRootCertificate;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/test")
    String test(String request) {
        return request;
    }

    @RequestMapping("/testGet")
    String testGet() {
        return "I am the cySchedule server!";
    }

    @RequestMapping("/teapot")
    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    void teapot() {
    }

    public StaticPageController(CertificateService certificateService) throws IOException, CertificateEncodingException, OperatorCreationException, CMSException {
        Path client_path = Paths.get("config/client_sign_root.crt");
        if (Files.isRegularFile(client_path)) {
            clientSignRootCertificate = Files.readAllBytes(client_path);
            logger.info("loading client sign certificate from cached file [config/client_sign_root.crt]");
        } else {
            clientSignRootCertificate = PEMUtils
                    .ConvertPKCS7ToPEMBytes(certificateService.getClientSignPublicCertificate());
            Files.write(client_path, clientSignRootCertificate);
            logger.info("loading client sign certificate from keystore and write to [config/client_sign_root.crt]");
        }
    }

    @Operation(summary = "Request Server public certifies for signing new client")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/client_sign_root.crt", produces = MediaType.TEXT_PLAIN_VALUE)
    public void RequestCerts(HttpServletResponse response) throws CertificateEncodingException, OperatorCreationException, CMSException, IOException {
        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("client_sign_root.crt", StandardCharsets.UTF_8));
        response.addHeader("Content-Length", "" + clientSignRootCertificate.length);
        response.getOutputStream().write(clientSignRootCertificate);
        response.getOutputStream().flush();
    }


}
