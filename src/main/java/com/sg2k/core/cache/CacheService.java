package com.sg2k.core.cache;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;

public class CacheService<T> {
	private Cache<String, T> cache;
	private CacheItemLoader<T> itemLoader;

	/**
	 * 创建一个自动加载缓存，缓存的对象没有最大数目限制且永不过期
	 * 
	 * @param loader
	 *            对象自动加载类
	 */
	public CacheService(CacheItemLoader<T> loader) {
		this.itemLoader = loader;
		this.cache = CacheBuilder.newBuilder().build(new CacheLoader<String, T>() {
			@Override
			public T load(String key) throws Exception {
				return itemLoader.load(key);
			}
		});
	}

	/**
	 * 创建一个自动加载缓存，可以指定缓存的对象的最大数目及对象的过期时间
	 * 
	 * @param loader
	 *            对象自动加载类
	 * @param maximum
	 *            缓存的对象的最大数目
	 * @param expiredTimeInSec
	 *            缓存对象的过期时间，以秒为单位
	 */
	public CacheService(CacheItemLoader<T> loader, int maximum, int expiredTimeInSec) {
		this.itemLoader = loader;
		if (maximum <= 0) {
			maximum = 200;
		}
		if (expiredTimeInSec <= 0) {
			expiredTimeInSec = 3600;
		}
		this.cache = CacheBuilder.newBuilder().maximumSize(maximum).expireAfterAccess(expiredTimeInSec, TimeUnit.SECONDS).build(new CacheLoader<String, T>() {
			@Override
			public T load(String key) throws Exception {
				return itemLoader.load(key);
			}
		});
	}

	/**
	 * 创建一个自动加载缓存,可以指定对象的过期时间
	 * 
	 * @param loader
	 *            对象自动加载类
	 * @param expiredTimeInSec
	 *            缓存对象的过期时间，以秒为单位
	 */
	public CacheService(CacheItemLoader<T> loader, int expiredTimeInSec) {
		this.itemLoader = loader;
		if (expiredTimeInSec <= 0) {
			expiredTimeInSec = 3600;
		}
		this.cache = CacheBuilder.newBuilder().expireAfterAccess(expiredTimeInSec, TimeUnit.SECONDS).build(new CacheLoader<String, T>() {
			@Override
			public T load(String key) throws Exception {
				return itemLoader.load(key);
			}
		});
	}

	/**
	 * 创建一个手动加载缓存，缓存的对象没有最大数目限制且永不过期
	 */
	public CacheService() {
		this.cache = CacheBuilder.newBuilder().build();
	}

	/**
	 * 创建一个手动加载缓存，缓存的对象没有最大数目限制且永不过期,并创健监听
	 */
	public CacheService(RemovalListener<String, T> listener) {
		this.cache = CacheBuilder.newBuilder().removalListener(listener).build();
	}

	/**
	 * 创建一个手动加载缓存，可以指定缓存的对象的最大数目及对象的过期时间
	 * 
	 * @param maximum
	 *            缓存的对象的最大数目
	 * @param expiredTimeInSec
	 *            缓存对象的过期时间，以秒为单位
	 */
	public CacheService(int maximum, int expiredTimeInSec) {
		if (maximum <= 0) {
			maximum = 200;
		}
		if (expiredTimeInSec <= 0) {
			expiredTimeInSec = 3600;
		}
		this.cache = CacheBuilder.newBuilder().maximumSize(maximum).expireAfterAccess(expiredTimeInSec, TimeUnit.SECONDS).build();
	}

	/**
	 * 创建一个手动加载缓存，可以指定缓存对象的过期时间
	 * 
	 * @param expiredTimeInSec
	 *            缓存对象的过期时间，以秒为单位
	 */
	public CacheService(int expiredTimeInSec) {
		if (expiredTimeInSec <= 0) {
			expiredTimeInSec = 3600;
		}
		this.cache = CacheBuilder.newBuilder().expireAfterAccess(expiredTimeInSec, TimeUnit.SECONDS).build();
	}

	/**
	 * 该函数只是用于自动加载的cache(loadingCache)， 如果是非自动加载的缓存，请使用
	 * {@code get(String key,Callable<T> caller)} 函数
	 * 
	 * @param key
	 *            对象唯一标示
	 * @return
	 */
	public T get(String key) throws Exception {
		if (cache instanceof LoadingCache) {
			return ((LoadingCache<String, T>) cache).get(key);
		} else {
			throw new Exception("caller must NOT be null for not-auto-loading cache!");
		}
	}

	/**
	 * 如果是自动加载的cache(loadingCache)，会忽略caller参数 如果是非自动加载的缓存，必须指定caller对象
	 * 
	 * @param key
	 *            对象唯一标示
	 * @return
	 */
	public T get(String key, Callable<T> caller) throws Exception {
		if (cache instanceof LoadingCache) {
			return ((LoadingCache<String, T>) cache).get(key);
		} else {
			if (caller != null) {
				return cache.get(key, caller);
			} else {
				throw new Exception("caller must NOT be null not-auto-loading cache!");
			}
		}
	}

	public T getIfPresent(String key) {
		return cache.getIfPresent(key);
	}

	public void put(String key, T value) {
		if (key == null) {
			return;
		}
		cache.put(key, value);
	}

	public void putAll(Map<String, T> map) {
		if (map == null) {
			return;
		}
		cache.putAll(map);
	}

	public void remove(String key) {
		try {
			cache.invalidate(key);
		} catch (Exception e) {
		}
	}

	public void cleanAll() {
		try {
			cache.invalidateAll();
		} catch (Exception e) {
			System.out.println("clean all guava faiulre!!");
		}
	}

	public long size() {
		return cache.size();
	}

	public ConcurrentMap<String, T> asMap() {
		return cache.asMap();
	}

	public boolean containsKey(String key) {
		if (key == null) {
			return false;
		}
		return this.asMap().containsKey(key);
	}
}
