package io.github.novareseller.cache;


import io.github.novareseller.cache.bean.TestBean;
import io.github.novareseller.cache.autoconfigure.J2CacheAutoConfiguration;
import io.github.novareseller.cache.autoconfigure.J2CacheSpringCacheAutoConfiguration;
import io.github.novareseller.cache.autoconfigure.J2CacheSpringRedisAutoConfiguration;
import io.github.novareseller.cache.support.J2CacheCacheManger;
import io.github.novareseller.cache.support.util.SpringUtil;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import static org.assertj.core.api.Assertions.assertThat;




@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
		J2CacheAutoConfiguration.class,
		J2CacheSpringCacheAutoConfiguration.class,
		J2CacheSpringRedisAutoConfiguration.class })
@ActiveProfiles(value = {"L1cache-close-springcache"})
public class UseL1CacheCloseSpringCacheTests extends BaseTester{


	@Autowired
	private CacheChannel cacheChannel;


	/**
	 * 正确用例：spring不会加载J2CacheCacheManger对象(不会集成spring cache)
	 */
	@Test(expected = NoSuchBeanDefinitionException.class)
	public void testCloseSpringCache() {
		SpringUtil.getBean(J2CacheCacheManger.class);
	}


	@Test
	public void testCache() {
		TestBean cacheBefore = getBean(3);
		cacheChannel.set("test3s", "key1", cacheBefore);

		CacheObject cacheObject = cacheChannel.get("test3s", "key1");
		TestBean cacheAfter = (TestBean) cacheObject.rawValue();

		//测试对象hash与属性值相等
		assertThat(cacheBefore.equals(cacheAfter)).isTrue();
		assertThat(cacheBefore.getName().equals(cacheAfter.getName())).isTrue();
		assertThat(cacheBefore.getAge().equals(cacheAfter.getAge())).isTrue();
		//缓存级别正确性
		assertThat(cacheObject.getLevel()).isEqualTo(CacheObject.LEVEL_1);
	}

	@Test
	public void testLRU() {
		TestBean threeAge = getBean(3);
		cacheChannel.set("test3s", "threeAge", threeAge);

		TestBean fourAge = getBean(4);
		cacheChannel.set("test3s", "fourAge", fourAge);

		TestBean fiveAge = getBean(5);
		cacheChannel.set("test3s", "fiveAge", fiveAge);

		sleep(500);

		//当超过缓存容量大小时,会默认使用Least Recently Used策略进行清除
		//see https://github.com/ben-manes/caffeine/wiki/Efficiency
		assertThat(cacheChannel.keys("test3s")).hasSize(2);
	}


	@Test
	public void testExpire() {
		//自动过期
		TestBean threeAge = getBean(3);
		cacheChannel.set("test3s", "threeAge", threeAge);
		sleep(3500);

		assertThat(cacheChannel.get("test3s", "threeAge").getValue()).isEqualTo(null);


		//手动过期
		TestBean fourAge = getBean(4);
		cacheChannel.set("test3s", "fourAge", fourAge);

		cacheChannel.evict("test3s","fourAge");
		assertThat(cacheChannel.get("test3s", "fourAge").getValue()).isEqualTo(null);
	}

	@Test
	public void testForeach() {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		for (int i = 0; i < 1000_000; i++) {
			cacheChannel.set("test3s", String.valueOf(i), getBean(i));
		}
		stopWatch.stop();
		assertThat(stopWatch.getTotalTimeMillis()).isLessThanOrEqualTo(2000);
		assertThat(cacheChannel.get("test3s", "fourAge").getValue()).isEqualTo(null);
	}


}
