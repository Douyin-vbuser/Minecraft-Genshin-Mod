package com.vbuser.database.network;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Storage {
    /**
     * select : 用于在客户端存储select的SQL语句获得的返回结果<br>
     * K : 随机数<br>
     * V : 表格源数据
     */
    public static ConcurrentMap<Double, String> select = new ConcurrentHashMap<>(10);
}
