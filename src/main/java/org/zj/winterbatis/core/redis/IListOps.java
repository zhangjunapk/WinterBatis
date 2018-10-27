package org.zj.winterbatis.core.redis;

import java.util.List;

public interface IListOps<K,V> {
    List<V> get(K k);
    void add(K k,V v);
}
