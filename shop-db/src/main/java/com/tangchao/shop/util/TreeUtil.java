package com.tangchao.shop.util;

import com.tangchao.shop.beans.TreeEntity;

import java.util.*;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/10/29 15:56
 */
public final class TreeUtil {

    public static <Entity extends TreeEntity> List<Entity> tree(List<Entity> entityList){
        //  父级Id去重
        Map<Long,TreeEntity> map = new HashMap<>();
        for (TreeEntity entity : entityList){
            map.put(entity.findPid(),entity);
        }
        //  排序
        Collections.sort(entityList,new Comparator<Entity>(){
            @Override
            public int compare(Entity entity1, Entity entity2) {
                return entity1.findSort() - entity2.findSort();
            }
        });
        //  获取父级Id列表
        List<Long> pidList = getPidList(map.keySet(),entityList);
        List<Entity> list = new ArrayList<>(pidList.size());
        for (Long pid : pidList){
            List<Entity> entities = getSubList(pid,entityList);
            list.addAll(entities);
        }
        return list;
    }

    /**
     * 递归获取子节点
     * @param pid 父级Id
     * @param entityList 实体类列表
     * @return List<Entity>
     */
    @SuppressWarnings("unchecked")
    private static <Entity extends TreeEntity> List<Entity> getSubList(long pid, List<Entity> entityList){
        List<Entity> list = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i ++){
            Entity entity = entityList.get(i);
            if (entity.findPid() == pid){
                List<Entity> subList = getSubList(entity.findId(),entityList);
                entity.setSubList(subList);
                list.add(entity);
            }
        }
        return list;
    }

    /**
     * 获取最顶级的Pid
     * @param pidSet 去重后的pid列表
     * @param entityList 实体类列表
     * @return List<Long>
     */
    private static <Entity extends TreeEntity> List<Long> getPidList(Set<Long> pidSet,List<Entity> entityList){
        List<Long> pidList = new ArrayList<>();
        for (long pid : pidSet){
            boolean isFind = false;
            for (Entity entity : entityList){
                //  结束循环
                if (entity.findId() == pid){
                    isFind = true;
                    break;
                }
            }
            if (!isFind){
                pidList.add(pid);
            }
        }
        return pidList;
    }

}
