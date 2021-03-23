package io.github.lovelybowen.database.base;

import lombok.Data;

/**
 * @author: Bowen huang
 * @date: 2020/07/31
 */
@Data
public class TenantEntity extends BaseEntity<TenantEntity> {

    private static final long serialVersionUID = 7393111163846132019L;

    private Long tenantId;

}
