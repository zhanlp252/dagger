package io.github.lovelybowen.database.support;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.BeanUtils;
import io.github.lovelybowen.tool.text.StringUtil;
import io.github.lovelybowen.tool.utils.NumberUtil;


import java.util.Map;

/**
 * @author: Bowen huang
 * @date: 2020/06/21
 */
public class Condition {

    /**
     * 转化成mybatis plus中的Page
     *
     * @param query 查询条件
     * @return IPage
     */
    public static <T> IPage<T> getPage(Query query) {
        Page<T> page = new Page<>(NumberUtil.toInt(query.getPage(), 1), NumberUtil.toInt(query.getLimit(), 10));
        page.setAsc(StringUtil.toStrArray(",", SqlKeyword.filter(query.getAscs())));
        page.setDesc(StringUtil.toStrArray(",", SqlKeyword.filter(query.getDescs())));
        return page;
    }

    /**
     * 获取mybatis plus中的QueryWrapper
     *
     * @param entity 实体
     * @param <T>    类型
     * @return QueryWrapper
     */
    public static <T> QueryWrapper<T> getQueryWrapper(T entity) {
        return new QueryWrapper<>(entity);
    }

    /**
     * 获取mybatis plus中的QueryWrapper
     *
     * @param query 查询条件
     * @param clazz 实体类
     * @param <T>   类型
     * @return QueryWrapper
     */
    public static <T> QueryWrapper<T> getQueryWrapper(Map<String, Object> query, Class<T> clazz) {
        Map<String, Object> exclude =
                ImmutableMap.of("page", "page",
                        "limit", "limit",
                        "ascs", "ascs",
                        "descs", "descs"
                );
        return getQueryWrapper(query, exclude, clazz);
    }

    /**
     * 获取mybatis plus中的QueryWrapper
     *
     * @param query   查询条件
     * @param exclude 排除的查询条件
     * @param clazz   实体类
     * @param <T>     类型
     * @return QueryWrapper
     */
    public static <T> QueryWrapper<T> getQueryWrapper(Map<String, Object> query, Map<String, Object> exclude, Class<T> clazz) {
        exclude.forEach((k, v) -> query.remove(k));
        QueryWrapper<T> qw = new QueryWrapper<>();
        qw.setEntity(BeanUtils.instantiateClass(clazz));
        SqlKeyword.buildCondition(query, qw);
        return qw;
    }
}
