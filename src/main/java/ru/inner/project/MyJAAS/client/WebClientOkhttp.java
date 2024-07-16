package ru.inner.project.MyJAAS.client;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ru.inner.project.MyJAAS.utils.TrustManagerToDisableCertificateChecking;

import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Slf4j
public class WebClientOkhttp { // The client for inner queries (for redirect, forward request)
    private String resourceUrl;
    private String authToken;
    private OkHttpClient client;

    // Constructor (Create client who ignores web service certificate)
    public WebClientOkhttp(String resourceUrl, String authToken) throws NoSuchAlgorithmException, KeyManagementException {
        this.resourceUrl = resourceUrl;
        this.authToken = authToken;

        client = new OkHttpClient.Builder()
                .sslSocketFactory(TrustManagerToDisableCertificateChecking.createSSLContext().getSocketFactory(),
                        (X509TrustManager) TrustManagerToDisableCertificateChecking.createTrustManager()[0])
                .hostnameVerifier((hostname, session) -> true)
                .build();
    }

    // methods of endpoints
    public byte[] get() throws IOException {
        log.debug("Method get in class 'WebClientOkhttp', stared at " + LocalDateTime.now());
        Request request = new Request.Builder() // Create a GET request
                .url(resourceUrl)
                .get()
                .addHeader("Authorization", "Bearer " + authToken)
                .build();
        try (Response response = client.newCall(request).execute()) { // Execute the request and get the response
            if (response.isSuccessful()) { // Check if the response is successful
                assert response.body() != null;
                return response.body().bytes(); // Return the response body as a set of bytes
            } else {
                log.debug("Method get in class 'WebClientOkhttp', with request " + request + " ," +
                        "Error: " + response.code() + " " + response.message());
                return null;
            }
        }
    }
}
