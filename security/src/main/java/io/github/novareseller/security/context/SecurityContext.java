package io.github.novareseller.security.context;

/**
 * @author: Bowen huang
 * @date: 2021/04/28
 */
public class SecurityContext {

    static ThreadLocal<LoginUser> loginUserThreadLocal = new InheritableThreadLocal<LoginUser>(){
        @Override
        public LoginUser get() {
            return new LoginUser();
        }
    };


    /**
     *
     * @param tenantId
     * @param uid
     */
    public static void setLoginUser(long tenantId, long uid) {
        LoginUser loginUser = new LoginUser();
        loginUser.setTenantId(tenantId);
        loginUser.setUid(uid);
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
