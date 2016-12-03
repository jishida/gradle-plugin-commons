package com.github.jishida.gradle.commons.util;

import com.github.jishida.gradle.commons.function.Proc;
import org.gradle.internal.impldep.org.apache.http.client.HttpResponseException;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;

import static com.github.jishida.gradle.commons.util.EnvironmentUtils.getJavaVersion;
import static com.github.jishida.gradle.commons.util.IOUtils.*;
import static java.lang.String.format;

public final class NetUtils {
    private static class InsecureX509TrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static int BUFFER_SIZE = 8192;
    private static SSLContext _insecureSSLContext;
    private static HostnameVerifier _insecureHostnameVerifier;

    static {
        final int[] javaVersion = getJavaVersion();
        if (javaVersion == null ||
                javaVersion[0] < 1 ||
                (javaVersion[0] == 1 && javaVersion[1] < 7) ||
                (javaVersion[0] == 1 && javaVersion[1] == 7 && javaVersion[2] == 0 && javaVersion[3] < 111) ||
                (javaVersion[0] == 1 && javaVersion[1] == 8 && javaVersion[2] == 0 && javaVersion[3] < 101)
                ) {
            try {
                final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

                withInputStream(new File(System.getProperty("java.home"), "lib/security/cacerts"), new Proc<InputStream>() {
                    @Override
                    public void invoke(InputStream it) {
                        try {
                            keyStore.load(it, "changeit".toCharArray());
                        } catch (RuntimeException e) {
                            throw e;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                withCloseable(NetUtils.class.getResourceAsStream("DSTRootCAX3.der"), new Proc<InputStream>() {
                    @Override
                    public void invoke(InputStream it) {
                        try {
                            final Certificate certificate = certificateFactory.generateCertificate(it);
                            keyStore.setCertificateEntry("DSTRootCAX3", certificate);
                        } catch (RuntimeException e) {
                            throw e;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);
                final SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
                SSLContext.setDefault(sslContext);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String findFileName(final URL url, final String defaultName) {
        try {
            if (url.getHost().equals("sourceforge.net")) {
                final Matcher matcher = Regex.sourceForgeFilePath.matcher(url.getPath());
                if (matcher.matches()) {
                    return URLDecoder.decode(matcher.group(1), "UTF-8");
                }
            } else {
                final Matcher matcher = Regex.urlFilePath.matcher(url.getPath());
                if (matcher.matches()) {
                    return URLDecoder.decode(matcher.group(1), "UTF-8");
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return defaultName;
    }

    public static void downloadFile(final URL url, final File file) throws HttpResponseException, IOException {
        downloadFile(url, file);
    }

    public static void downloadFile(final URL url, final File file, final boolean insecure) throws HttpResponseException, IOException {
        Checker.checkNull(url, "url");
        Checker.checkNull(file, "file");
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if (connection instanceof HttpsURLConnection && insecure) {
            final HttpsURLConnection https = (HttpsURLConnection) connection;
            https.setHostnameVerifier(getInsecureHostnameVerifier());
            https.setSSLSocketFactory(getInsecureSSLContext().getSocketFactory());
        }
        connection.setInstanceFollowRedirects(false);
        connection.connect();
        switch (connection.getResponseCode()) {
            case 301:
            case 302:
            case 303:
                downloadFile(new URL(connection.getHeaderField("Location")), file, insecure);
                break;
            case 200:
                try {
                    withCloseable(connection.getInputStream(), new Proc<InputStream>() {
                        @Override
                        public void invoke(final InputStream inputStream) {
                            try {
                                withOutputStream(file, false, new Proc<OutputStream>() {
                                    @Override
                                    public void invoke(final OutputStream outputStream) {
                                        try {
                                            byte[] buffer = new byte[BUFFER_SIZE];
                                            while (true) {
                                                final int count = inputStream.read(buffer);
                                                if (count == -1) break;
                                                outputStream.write(buffer);
                                            }
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                });
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                } catch (RuntimeException e) {
                    if (e.getCause() instanceof IOException) {
                        throw (IOException) e.getCause();
                    }
                    throw e;
                }
                break;
            default:
                final int code = connection.getResponseCode();
                throw new HttpResponseException(code, format("response code `%d` is unexpected.", code));
        }
    }

    private static SSLContext getInsecureSSLContext() {
        if (_insecureSSLContext == null) {
            try {
                final TrustManager[] trustManagers = new TrustManager[0];
                trustManagers[0] = new InsecureX509TrustManager();
                final SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustManagers, null);
                _insecureSSLContext = sslContext;
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return _insecureSSLContext;
    }

    private static HostnameVerifier getInsecureHostnameVerifier() {
        if (_insecureHostnameVerifier == null) {
            _insecureHostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            };
        }
        return _insecureHostnameVerifier;
    }
}
