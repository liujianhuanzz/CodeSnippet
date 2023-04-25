package cn.hhspace.kerberos;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2023/4/23 13:49
 * @Descriptions: 自定义认证时的Configuration， 通常可以采用jaas方式传入配置文件
 */
public class MyConfiguration extends Configuration {

    private String keytabFile;
    private String keytabPrincipal;

    public MyConfiguration(String keytabFile, String keytabPrincipal) {
        this.keytabFile = keytabFile;
        this.keytabPrincipal = keytabPrincipal;
    }

    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
        Map<String, String> KEYTAB_KERBEROS_OPTIONS = new HashMap();
        KEYTAB_KERBEROS_OPTIONS.put("useKeyTab", "true");
        KEYTAB_KERBEROS_OPTIONS.put("storeKey", "true");
        KEYTAB_KERBEROS_OPTIONS.put("useTicketCache", "false");
        KEYTAB_KERBEROS_OPTIONS.put("keyTab", keytabFile);
        KEYTAB_KERBEROS_OPTIONS.put("principal", keytabPrincipal);

        AppConfigurationEntry KEYTAB_KERBEROS_LOGIN = new AppConfigurationEntry("com.sun.security.auth.module.Krb5LoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, KEYTAB_KERBEROS_OPTIONS);
        return new AppConfigurationEntry[]{KEYTAB_KERBEROS_LOGIN};
    }
}
