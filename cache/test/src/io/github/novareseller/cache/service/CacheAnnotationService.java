package io.github.novareseller.cache.service;

import io.github.novareseller.cache.bean.TestBean;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CacheAnnotationService {


	@Cacheable(cacheNames="test3s",key = "#testBean.age")
	public TestBean testCache(TestBean testBean) {
		return testBean;
	}

	@Cacheable(cacheNames="test3s",key = "#testBean.age")
	public TestBean getCache(TestBean testBean) {
		return null;
	}

	@CachePut(cacheNames="test3s",key = "#testBean.age")
	public TestBean putCache(TestBean testBean) {

		return null;
	}

	@CachePut(cacheNames="test3s",key = "#testBean.age")
	public TestBean putCacheException(TestBean testBean) {

		throw new RuntimeException();
	}
	

	@CacheEvict(cacheNames={"test3s"}, key = "#age")
	public void evict(Integer age) {
		
	}
	

	
}
