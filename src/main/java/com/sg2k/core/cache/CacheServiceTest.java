package com.sg2k.core.cache;

import java.lang.reflect.ParameterizedType;


public class CacheServiceTest {
	private String name;
	private String addr;
	private int	age;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
	public CacheServiceTest(String name,String addr,int age){
		this.name = name;
		this.addr = addr;
		this.age = age;
	}
	
	public static CacheServiceTest cacheLoader(String key){
		//return null;
		return new CacheServiceTest(key,"shenzhen",28);
	}
	

	public static void main(String[] args) throws Exception {
		CacheService<CacheServiceTest> cache = new CacheService<CacheServiceTest>(new CacheItemLoader<CacheServiceTest>(){
			@Override
			public CacheServiceTest load(String key) {
				return CacheServiceTest.cacheLoader(key);
			}
		});
		
		CacheServiceTest test = cache.get("19861010");
		System.out.println("name: " + test.getName());
		System.out.println("addr: " + test.getAddr());
		System.out.println("age: " + test.getAge());
	}
}
