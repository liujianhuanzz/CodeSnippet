package cn.hhspace.kerberos;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2023/4/23 10:37
 * @Descriptions: 封装认证过程，参考Hadoop实现方式
 */

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * User and group information for Hadoop.
 * This class wraps around a JAAS Subject and provides methods to determine the
 * user's username and groups. It supports both the Windows, Unix and Kerberos
 * login modules.
 */
public class UserGroupInformation {

    private final Subject subject;

    private static UserGroupInformation loginUser = null;

    private static String keytabPrincipal = null;
    private static String keytabFile = null;

    public UserGroupInformation(Subject subject) {
        this.subject = subject;
    }

    public static synchronized UserGroupInformation loginUserFromKeytab(String user, String path) throws IOException {
            keytabFile = path;
            keytabPrincipal = user;
            Subject subject = new Subject();

            try {
                //也可以传参CallbackHandler
                //LoginContext login = new LoginContext("bm-to-hdfs", subject, new MyCallbackHander(), new MyConfiguration(keytabFile, keytabPrincipal));
                LoginContext login = new LoginContext("xxxx", subject, null, new MyConfiguration(keytabFile, keytabPrincipal));
                login.login();
                loginUser = new UserGroupInformation(login.getSubject());
                return loginUser;
            } catch (LoginException e) {
                throw new IOException("Login failure for " + user + " from keytab " + path + ": " + e, e);
            }
    }


    public <T> T doAs(PrivilegedExceptionAction<T> action) throws IOException, InterruptedException {
        try {
            return Subject.doAs(subject, action);
        } catch (PrivilegedActionException pae) {
            Throwable cause = pae.getCause();

            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof InterruptedException) {
                throw (InterruptedException) cause;
            } else {
                throw new RuntimeException(cause);
            }
        }
    }
}

