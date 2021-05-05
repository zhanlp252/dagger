package io.github.novareseller.security.context;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * @author: Bowen huang
 * @date: 2021/04/28
 */
public class SecurityContext {

    static TransmittableThreadLocal<LoginUser> loginUserThreadLocal = new TransmittableThreadLocal<LoginUser>(){
        @Override
        protected LoginUser initialValue() {
            return new LoginUser();
        }
    };


    /**
     *
     * @param loginUser
     */
    public static void setLoginUser(LoginUser loginUser) {
        loginUserThreadLocal.set(loginUser);
    }

    /**
     *
     */
    public static void clear() {
        loginUserThreadLocal.remove();
    }

    /**
     *
     * @return
     */
    public static LoginUser getLoginUser() {
        return loginUserThreadLocal.get();
    }

}
