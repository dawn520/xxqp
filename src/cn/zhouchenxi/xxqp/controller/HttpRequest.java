package cn.zhouchenxi.xxqp.controller;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import static java.lang.System.in;

public class HttpRequest {
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    public static HashMap process(String requestUrl, String method, String para,String setCookie,int returnCookie) {
        HashMap remap = new HashMap<>();
        HttpURLConnection urlConnection = null;
        try {

            URL url = new URL(requestUrl);
            if (url.getProtocol().toLowerCase().equals("https")) {
                trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                urlConnection = https;
            } else {
                urlConnection = (HttpURLConnection) url.openConnection();
            }
            // 设置请求的方式
            urlConnection.setRequestMethod(method);
            // 设置请求的超时时间
            urlConnection.setReadTimeout(50000);
            urlConnection.setConnectTimeout(50000);
            // 遍历map输出为要传递的数据
            String data = para;
            // 设置请求的头
            urlConnection.setRequestProperty("Connection", "keep-alive");
            // 设置请求的头
            urlConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            // 设置请求的头
            urlConnection.setRequestProperty("Content-Length",
                    String.valueOf(data.getBytes().length));
            // 设置请求的头
            urlConnection
                    .setRequestProperty("User-Agent",
                            "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
            if(!setCookie.equals("")){
                urlConnection.setRequestProperty("Cookie", setCookie);
            }
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            //setDoInput的默认值就是true
            urlConnection.connect();
            //获取输出流
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            os.close();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                baos.close();
                // 返回字符串
                byte[] result = baos.toByteArray();
                if(returnCookie==1){
                    //取cookie
                    String sessionId = "";
                    String cookieVal = "";
                    String key = null;
                    for(int i = 1; (key = urlConnection.getHeaderFieldKey(i)) != null; i++){
                        if(key.equalsIgnoreCase("set-cookie")){
                            cookieVal = urlConnection.getHeaderField(i);
                            cookieVal = cookieVal.substring(0, cookieVal.indexOf(";"));
                            sessionId = sessionId + cookieVal + ";";
                        }
                    }
                    remap.put("cookie", sessionId);
                }
                // System.out.println(result.toString());
                remap.put("data", result);
                remap.put("state", 1);//返回状态为1，连接服务器成功
            } else {
                remap.put("state", 0);//返回状态为0，连接服务器失败
            }
        } catch (UnsupportedEncodingException e) {
            remap.put("state", 0);
            e.printStackTrace();
        } catch (MalformedURLException e) {
            remap.put("state", 0);
            e.printStackTrace();
        } catch (IOException e) {
            remap.put("state", 0);
            e.printStackTrace();
        } catch (Exception e) {
            remap.put("state", 0);
            e.printStackTrace();
        }finally {
            return remap;
        }
    }

//    public static HashMap processHttps(String requestUrl, String method, String para,String setCookie,int returnCookie) {
//        HashMap remap = new HashMap<>();
//        HttpsURLConnection urlConnection = null;
//        try {
//            //创建SSLContext对象，并使用我们指定的信任管理器初始化
//            TrustManager[] tms = {new X509TrustManager() {
//                public void checkClientTrusted(X509Certificate certificates[], String authType) throws CertificateException {
//                }
//
//                public void checkServerTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {
//                }
//
//                public X509Certificate[] getAcceptedIssuers() {
//                    return null;
//                }
//            }
//            };
//            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
//            sslContext.init(null, tms, new SecureRandom());
//            //从上述SSLContext对象中得到SSLSocketFactory对象
//            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
//
//
//            URL url = new URL(requestUrl);
//            HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
//            urlConnection = https;
//
//
//
//            // 设置请求的方式
//            urlConnection.setRequestMethod(method);
//            // 设置请求的超时时间
//            urlConnection.setReadTimeout(50000);
//            urlConnection.setConnectTimeout(50000);
//            // 遍历map输出为要传递的数据
//            String data = para;
//            // 设置请求的头
//            urlConnection.setRequestProperty("Connection", "keep-alive");
//            // 设置请求的头
//            urlConnection.setRequestProperty("Content-Type",
//                    "application/x-www-form-urlencoded");
//            // 设置请求的头
//            urlConnection.setRequestProperty("Content-Length",
//                    String.valueOf(data.getBytes().length));
//            // 设置请求的头
//            urlConnection
//                    .setRequestProperty("User-Agent",
//                            "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
//            if(!setCookie.equals("")){
//                urlConnection.setRequestProperty("Cookie", setCookie);
//            }
//            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
//            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
//            //setDoInput的默认值就是true
//            urlConnection.connect();
//            //获取输出流
//            OutputStream os = urlConnection.getOutputStream();
//            os.write(data.getBytes());
//            os.flush();
//            os.close();
//            if (urlConnection.getResponseCode() == 200) {
//
//                // 获取响应的输入流对象
//                InputStream is = urlConnection.getInputStream();
//                // 创建字节输出流对象
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                // 定义读取的长度
//                int len;
//                // 定义缓冲区
//                byte buffer[] = new byte[1024];
//                // 按照缓冲区的大小，循环读取
//                while ((len = is.read(buffer)) != -1) {
//                    // 根据读取的长度写入到os对象中
//                    baos.write(buffer, 0, len);
//                }
//                // 释放资源
//                is.close();
//                baos.close();
//                // 返回字符串
//                byte[] result = baos.toByteArray();
//                if(returnCookie==1){
//                    //取cookie
//                    String sessionId = "";
//                    String cookieVal = "";
//                    String key = null;
//                    for(int i = 1; (key = urlConnection.getHeaderFieldKey(i)) != null; i++){
//                        if(key.equalsIgnoreCase("set-cookie")){
//                            cookieVal = urlConnection.getHeaderField(i);
//                            cookieVal = cookieVal.substring(0, cookieVal.indexOf(";"));
//                            sessionId = sessionId + cookieVal + ";";
//                        }
//                    }
//                    remap.put("cookie", sessionId);
//                }
//                // System.out.println(result.toString());
//                remap.put("data", result);
//                remap.put("state", 1);//返回状态为1，连接服务器成功
//            } else {
//                remap.put("state", 0);//返回状态为0，连接服务器失败
//            }
//        } catch (UnsupportedEncodingException e) {
//            remap.put("state", 0);
//            e.printStackTrace();
//        } catch (MalformedURLException e) {
//            remap.put("state", 0);
//            e.printStackTrace();
//        } catch (IOException e) {
//            remap.put("state", 0);
//            e.printStackTrace();
//        } catch (Exception e) {
//            remap.put("state", 0);
//            e.printStackTrace();
//        }
//        return remap;
//    }




    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHosts() {

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType)  {

            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {

            }
        } };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private static void trust12306() {
//        TrustManager[] trustManagers = new TrustManager[]{
//                new X509TrustManager() {
//                    @Override
//                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
//
//                    }
//
//                    @Override
//                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
//
//                    }
//
//                    @Override
//                    public X509Certificate[] getAcceptedIssuers() {
//                        return new X509Certificate[0];
//                    }
//                }
//        };
//
//        try {
//            SSLContext sslContext = SSLContext.getInstance("SSL");
//            sslContext.init(null, trustManagers, new java.security.SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void printHttpsConnCert(HttpsURLConnection conn) {
//        if (conn == null) return;
//        try {
//            //连接所使用的密码程序
//            String cipherSuite = conn.getCipherSuite();
//            //服务器端的证书链
//            Certificate[] serverCertificates = conn.getServerCertificates();
//            System.out.println("密码程序:" + cipherSuite);
//
//            for (Certificate certificate : serverCertificates) {
//                String type = certificate.getType();
//                PublicKey publicKey = certificate.getPublicKey();
//                System.out.println("证书类型:" + type + "\n" + "公钥算法:" + publicKey.getAlgorithm()
//                        + "\n" + "公钥:" + publicKey.getFormat());
//            }
//        } catch (SSLPeerUnverifiedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void printHttpsConn(HttpsURLConnection conn) {
//        if (conn == null) return;
//        try {
//            BufferedReader br =
//                    new BufferedReader(
//                            new InputStreamReader(conn.getInputStream()));
//            String output = "";
//            while (br.readLine() != null) {
//                output += br.readLine();
//            }
//            br.close();
//            System.out.println(conn.getURL().getHost() + "的内容\n" + output);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static SSLContext trust123061() throws java.security.cert.CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException, KeyManagementException {
//        KeyStore trustStore  = null;
//        trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//        System.out.println(KeyStore.getDefaultType());
//        //密钥库的类型可以通过看keytool来查看
//        trustStore.load(in, "changeit".toCharArray());
//        //注册密匙库
//        System.out.println(trustStore.getType());
//        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//        tmf.init(trustStore);
//        // From https://www.washington.edu/itconnect/security/ca/load-der.crt
//        //InputStream caInput = new BufferedInputStream(new FileInputStream("load-der.crt"));
//
//        // Create an SSLContext that uses our TrustManager
//        SSLContext context = SSLContext.getInstance("SSL");
//        context.init(null, tmf.getTrustManagers(), null);
//        // Create an SSLContext that uses our TrustManager
//
//        //context=SSLContext.
//
//        context.init(null, tmf.getTrustManagers(), null);
//        return context;
//    }
}
