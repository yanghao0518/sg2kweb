package com.sg2k.core.cache;

public interface CacheItemLoader<T> {
	public T load(String key) throws Exception;
}
