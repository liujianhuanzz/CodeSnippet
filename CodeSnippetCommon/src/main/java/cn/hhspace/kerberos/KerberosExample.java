package cn.hhspace.kerberos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PrivilegedExceptionAction;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2023/4/23 10:37
 * @Descriptions: 调用Kerberos认证的API接口示例
 */

public class KerberosExample {

    public static void main(String[] args) throws Exception {
        boolean isOpenValidations = true;

        if (!isOpenValidations) {
            //没有开kerberos认证的接口
            String url = "xxxxxx";
            HttpURLConnection conn = getHttpURLConnection(url, "GET");
            String result = getResponseContent(conn);
            System.out.println(result);
        } else {
            String principal = "xxxxxx";
            String keytab = "xxxxxx";

            //System.setProperty("java.security.krb5.conf", "/etc/krb5.conf");
            //System.setProperty("sun.security.krb5.debug", "true");


            UserGroupInformation ugi = UserGroupInformation.loginUserFromKeytab(principal, keytab);

            ugi.doAs(new PrivilegedExceptionAction<String>() {
                @Override
                public String run() {
                    String url = "xxxxx";

                    HttpURLConnection conn = getHttpURLConnection(url, "GET");
                    String result = getResponseContent(conn);
                    System.out.println(result);
                    return null;
                }
            });
        }

    }

    public static HttpURLConnection getHttpURLConnection(String uri, String method) {
        try {
            URL url = new URL(uri);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            conn.setRequestMethod(method);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(20000);
            return conn;
        } catch (IOException e) {
            System.out.println("http处理异常:"+e);
            return null;
        }

    }

    public static String getResponseContent(HttpURLConnection conn){

        //读取接口返回的内容
        try {
            int statusCode = conn.getResponseCode();
            System.out.println(statusCode);
            InputStream is;
            if (statusCode >= 200 && statusCode < 300) {
                is = conn.getInputStream();
            } else {
                is = conn.getErrorStream();
            }
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            StringBuilder buffer = new StringBuilder();
            String line = null;
            while((line = br.readLine()) != null){
                buffer.append(line);
            }

            return buffer.toString();

        } catch (IOException e) {
            return "";
        }finally {
            conn.disconnect();
        }

    }
}
