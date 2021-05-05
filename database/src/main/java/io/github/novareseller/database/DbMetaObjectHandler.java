package io.github.novareseller.database;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import io.github.novareseller.database.support.DbConstant;
import io.github.novareseller.security.context.LoginUser;
import io.github.novareseller.security.context.SecurityContext;
import io.github.novareseller.tool.utils.SystemClock;
import org.apache.ibatis.reflection.MetaObject;

/**
 * unified processing of system fields
 *
 * @author: Bowen huang
 * @date: 2020/06/21
 */
public class DbMetaObjectHandler implements MetaObjectHandler {


    @Override
    public void insertFill(MetaObject metaObject) {
        long currentTimeMillis = SystemClock.now();
        LoginUser loginUser = SecurityContext.getLoginUser();
        this.setFieldValByName("crtUser", loginUser.getUid(), metaObject);
        this.setFieldValByName("crtTime", currentTimeMillis, metaObject);
        this.setFieldValByName("uptUser", loginUser.getUid(), metaObject);
        this.setFieldValByName("uptTime", currentTimeMillis, metaObject);
        this.setFieldValByName("tenantId", loginUser.getTenantId(), metaObject);
        this.setFieldValByName("delFlag", DbConstant.DB_NOT_DELETED, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LoginUser loginUser = SecurityContext.getLoginUser();
        this.setFieldValByName("uptUser", loginUser.getUid(), metaObject);
        this.setFieldValByName("uptTime", System.currentTimeMillis(), metaObject);
    }
}
