package io.github.novareseller.cache;


import io.github.novareseller.cache.bean.TestBean;
import io.github.novareseller.cache.service.CacheAnnotationService;
import io.github.novareseller.cache.autoconfigure.J2CacheAutoConfiguration;
import io.github.novareseller.cache.autoconfigure.J2CacheSpringCacheAutoConfiguration;
import io.github.novareseller.cache.autoconfigure.J2CacheSpringRedisAutoConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
		CacheAnnotationService.class,

		J2CacheAutoConfiguration.class,
		J2CacheSpringCacheAutoConfiguration.class,
		J2CacheSpringRedisAutoConfiguration.class })
@ActiveProfiles(value = {"L1cache-open-springcache"})
public class UseL1CacheOpenSpringCacheTests extends BaseTester{


	@Autowired
	private CacheAnnotationService cacheAnnotationService;



	@Test
	public void testCache() {
		TestBean bean = getBean(3);
		cacheAnnotationService.testCache(bean);

		TestBean cacheBean = cacheAnnotationService.getCache(bean);

		assertThat(cacheBean).isNotNull();
		assertThat(cacheBean.getAge()).isEqualTo(3);
	}



	@Test
	public void testPutCache() {
		TestBean bean = getBean(3);
		cacheAnnotationService.testCache(bean);

		bean.setName("tom");

		cacheAnnotationService.putCache(bean);

		TestBean cacheBean = cacheAnnotationService.getCache(bean);
		assertThat(cacheBean.getName()).isEqualTo("tom");
	}

	@Test
	public void testPutCacheException() {
		TestBean bean = getBean(3);
		bean.setName("tom");
		cacheAnnotationService.testCache(bean);

		TestBean cacheBean = cacheAnnotationService.getCache(bean);
		assertThat(cacheBean.getName()).contains("tom");


		try {
			cacheAnnotationService.putCacheException(getBean(3));
		} catch (Exception e) {
		}


		TestBean cacheBean2 = cacheAnnotationService.getCache(getBean(3));
		assertThat(cacheBean2.getName()).contains("tom");
	}


	@Test
	public void evict() {
		TestBean bean = getBean(3);
		cacheAnnotationService.testCache(bean);

		TestBean cacheBean = cacheAnnotationService.getCache(bean);
		assertThat(cacheBean.getAge()).isEqualTo(3);

		cacheAnnotationService.evict(3);

		TestBean cacheBean2 = cacheAnnotationService.getCache(getBean(3));
		assertThat(cacheBean2).isEqualTo(null);
	}



}
