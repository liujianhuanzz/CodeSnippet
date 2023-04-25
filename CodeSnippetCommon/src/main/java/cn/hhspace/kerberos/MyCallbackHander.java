package cn.hhspace.kerberos;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.RealmCallback;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2023/4/25 10:11
 * @Descriptions: 自定义认证时的CallbackHandler，采用keytab的话就不需要了
 */
public class MyCallbackHander implements CallbackHandler {

    @Override
    public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
        for (Callback cb : callbacks) {
            if (cb instanceof NameCallback) {
                NameCallback nc = (NameCallback) cb;
                nc.setName(nc.getDefaultName());
            } else {
                if (cb instanceof PasswordCallback) {
                    throw new  UnsupportedCallbackException(cb, "Not Support PasswordCallback");
                }

                if (cb instanceof RealmCallback) {
                    RealmCallback rc = (RealmCallback)cb;
                    rc.setText(rc.getDefaultText());
                } else {
                    if (!(cb instanceof AuthorizeCallback)) {
                        throw new UnsupportedCallbackException(cb, "Unrecognized SASL ClientCallback");
                    }

                    AuthorizeCallback ac = (AuthorizeCallback) cb;
                    String authId = ac.getAuthenticationID();
                    String authzId = ac.getAuthorizationID();
                    ac.setAuthorized(authId.equals(authzId));
                    if (ac.isAuthorized()) {
                        ac.setAuthorizedID(authzId);
                    }
                }
            }
        }
    }
}
