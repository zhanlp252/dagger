package io.github.lovelybowen.database.support;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: Bowen huang
 * @date: 2020/06/21
 */
@Data
@Accessors(chain = true)
public class Query {

    /**
     * 当前页
     */
    private Integer page;

    /**
     * 每页的数量
     */
    private Integer limit;

    /**
     * 排序的字段名
     */
    private String ascs;

    /**
     * 排序方式
     */
    private String descs;


}
