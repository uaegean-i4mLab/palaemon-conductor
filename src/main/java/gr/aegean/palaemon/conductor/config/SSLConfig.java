package gr.aegean.palaemon.conductor.config;

import gr.aegean.palaemon.conductor.utils.EnvUtils;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

//@Configuration
public class SSLConfig {
    private final char[] KEYSTORE_PASS = EnvUtils.getEnvVar("SSL_KEYSTORE_PASS","testpass").toCharArray();

    private final String certPathName =  EnvUtils.getEnvVar("SSL_ROOT_CERTIFICATE","/home/ni/code/java/palaemon-db-proxy/dfb.palaemon.itml.gr");
//            new File(
//            EnvUtils.getEnvVar("SSL_CERTIFICATE","/home/ni/code/java/palaemon-db-proxy/dfb.palaemon.itml.crt")).isFile() ?
//            "/home/ni/code/java/palaemon-db-proxy/dfb.palaemon.itml.crt" : "tls.crt";
    private final File keyStoreFile;

    public SSLConfig() throws Exception {
        keyStoreFile = this.generateKeyStoreFile();
    }

    Certificate generateCert() throws Exception {
        InputStream inStream = new FileInputStream(certPathName);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        return cf.generateCertificate(inStream);
    }



    File generateKeyStoreFile() throws Exception {
        String keyStoreName =  EnvUtils.getEnvVar("SSL_KEYSTORE_JKS","keystore_ssl.jks");
        File f = new File(keyStoreName);
        if (f.isFile()) return f;

        Certificate cert = this.generateCert();
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null);
        ks.setCertificateEntry("es-http-public", cert);
        ks.store(new FileOutputStream(keyStoreName), KEYSTORE_PASS);
        return new File(keyStoreName);
    }

    public SSLContext getSSLContext() throws Exception {
        SSLContextBuilder builder = SSLContexts.custom();
        builder.loadTrustMaterial(keyStoreFile, KEYSTORE_PASS, new TrustSelfSignedStrategy());
        return builder.build();
    }
}