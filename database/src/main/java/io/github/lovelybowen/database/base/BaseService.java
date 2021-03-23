package io.github.lovelybowen.database.base;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 基础业务接口
 *
 * @param <T>
 * @author: Bowen huang
 * @date: 2020/06/21
 */
public interface BaseService<T> extends IService<T> {

    /**
     * 逻辑删除
     *
     * @param ids id集合(逗号分隔)
     * @return boolean
     */
    boolean deleteLogic(List<Long> ids);

}
