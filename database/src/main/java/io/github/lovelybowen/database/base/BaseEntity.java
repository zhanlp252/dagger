package io.github.lovelybowen.database.base;


import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;


/**
 * 基础实体类
 *
 * @author: Bowen huang
 * @date: 2020/06/21
 */
@Data
public class BaseEntity<T extends BaseEntity> extends Model {


    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long crtUser;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Long crtTime;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long uptUser;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long uptTime;

    /**
     * 状态[0:未删除,1:删除]
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer delFlag;
}
