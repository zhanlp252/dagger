package io.github.lovelybowen.tool.collection;


import io.github.lovelybowen.tool.utils.Validator;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * @Auther: bowen
 * @Date: 19-1-30
 * @Description: 集合工具类
 */
public class CollectionUtil {

    /**
     * 验证集合是否为空
     *
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null) || (collection.size() == 0);
    }

    /**
     * @param initialCapacity 初始化大小
     * @param <E>
     * @return 创建arrayList
     */
    public static <E> ArrayList<E> newArrayList(int initialCapacity) {
        return new ArrayList<E>(initialCapacity);
    }

    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<E>();
    }

    /**
     * 创建链表
     *
     * @param <E>
     * @return
     */
    public static <E> LinkedList<E> createLinkedList() {
        return new LinkedList<E>();
    }

    /**
     * 创建hashmap
     *
     * @param initialCapacity
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> HashMap<K, V> createHashMap(int initialCapacity) {
        return new HashMap<K, V>(initialCapacity);
    }


    /**
     * 数据转换
     *
     * @param fromList
     * @param function
     * @param <T>      转换元数据
     * @return
     */
    public static <F, T> List<T> transform(Collection<F> fromList, Function<? super F, ? extends T> function) {
        if (CollectionUtil.isEmpty(fromList)) {
            return Collections.emptyList();
        }

        List<T> result = new ArrayList<>(fromList.size());
        for (F f : fromList) {
            T t = function.apply(f);
            if (t == null) {
                continue;
            }
            result.add(t);
        }

        return result;
    }

    /**
     * 处理数据,无返回值
     *
     * @param fromList 数据来源
     * @param consumer 消费函数
     * @param <F>      //original way
     *                 if (Validator.isNullOrEmpty(ids)) {
     *                 return;
     *                 }
     *                 UserDecideNodeSnapshot userDecideNodeSnapshot = new UserDecideNodeSnapshot();
     *                 userDecideNodeSnapshot.setReadStatus(SysConstant.TRUE);
     *                 for (String id : ids) {
     *                 if (Validator.isNullOrEmpty(id)) {
     *                 continue;
     *                 }
     *                 userDecideNodeSnapshot.setId(id);
     *                 dao.updateByPrimaryKeySelective(userDecideNodeSnapshot);
     *                 }
     *                 <p>
     *                 <p>
     *                 //latest way
     *                 UserDecideNodeSnapshot userDecideNodeSnapshot = new UserDecideNodeSnapshot();
     *                 userDecideNodeSnapshot.setReadStatus(SysConstant.TRUE);
     *                 CollectionUtil.dispose(ids, id->{
     *                 userDecideNodeSnapshot.setId(id);
     *                 dao.updateByPrimaryKeySelective(userDecideNodeSnapshot);
     *                 });
     */
    public static <F> void dispose(Collection<F> fromList, Consumer<? super F> consumer) {
        if (CollectionUtil.isEmpty(fromList)) {
            return;
        }
        for (F f : fromList) {
            if (Validator.isNullOrEmpty(f)) {
                continue;
            }
            consumer.accept(f);
        }
    }


    /**
     * 筛选搜索数据
     *
     * @param formList  原始列表
     * @param condition 条件表达式
     * @param function  转换函数
     * @param <T>       原始数据
     * @param <R>       转换后的数据
     * @return R
     */
    public static <T, R> List<R> searchValueToList(Collection<T> formList, Predicate<? super T> condition, Function<? super T, ? extends R> function) {
        if (Validator.isNullOrEmpty(formList)) {
            return Collections.emptyList();
        }
        List result = new ArrayList<>();

        for (T originalElement : formList) {
            if (originalElement == null) {
                continue;
            }

            if (condition.test(originalElement)) {
                R r = function.apply(originalElement);
                result.add(r);
            }
        }
        return result;
    }

    /**
     * 对集合元素的一元运算
     *
     * @param fromList 数据源
     * @param function 运算函数
     * @param <F>      元素
     * @return 运算后的集合
     */
    public static <F> List<F> unaryTransform(Collection<F> fromList, UnaryOperator<F> function) {
        if (CollectionUtil.isEmpty(fromList)) {
            return Collections.emptyList();
        }

        List<F> result = new ArrayList<>(fromList.size());
        for (F f0 : fromList) {
            F f1 = function.apply(f0);
            if (f1 == null) {
                continue;
            }
            result.add(f1);
        }

        return result;
    }
}


