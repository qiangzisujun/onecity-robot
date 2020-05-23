package com.tangchao.shop.beans;

import java.util.List;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/10/28 11:42
 */
public interface TreeEntity<Entity> {

    /**
     * 获取主键
     * @return Long
     */
    Long findId();

    /**
     * 父级Id
     * @return Long
     */
    Long findPid();

    /**
     * 排序值
     * @return Integer
     */
    Integer findSort();

    /**
     * 设置子节点
     * @param entityList 子节点列表
     */
    void setSubList(List<Entity> entityList);
}
