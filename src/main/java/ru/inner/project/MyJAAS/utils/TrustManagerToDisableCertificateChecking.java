package ru.inner.project.MyJAAS.utils;

import lombok.experimental.UtilityClass;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

@UtilityClass
public class TrustManagerToDisableCertificateChecking { // to use only for internal requests
    public static TrustManager[] createTrustManager() { // Create a trust manager that trusts all certificates
        return new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };
    }

    // Create an SSL context with the trust manager
    public static SSLContext createSSLContext() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null,
                TrustManagerToDisableCertificateChecking.createTrustManager(),
                new java.security.SecureRandom()
        );
        return sslContext;
    }
}
