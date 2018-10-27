package org.zj.winterbatis.core.redis;

public interface IValueOps<K,V> {
    V get(K k);
    void set(K k,V v);
}
