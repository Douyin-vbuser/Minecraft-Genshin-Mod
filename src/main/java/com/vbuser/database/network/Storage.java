//本文件包含AI辅助生成内容
//AI注释服务提供商:DeepSeek

package com.vbuser.database.network;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Storage {
    /**
     * 用于在客户端存储select查询结果的并发映射<br>
     * K: 随机生成的唯一标记<br>
     * V: 查询返回的数据表格
     */
    public static ConcurrentMap<Double, String> select = new ConcurrentHashMap<>(10);
}