package io.github.novareseller.boot;


import io.github.novareseller.boot.config.WebMvcConfig;
import io.github.novareseller.boot.security.TestClientInfoHolder;
import io.github.novareseller.boot.utils.SpringUtils;
import io.github.novareseller.cache.autoconfigure.J2CacheAutoConfiguration;
import io.github.novareseller.cache.autoconfigure.J2CacheSpringCacheAutoConfiguration;
import io.github.novareseller.cache.autoconfigure.J2CacheSpringRedisAutoConfiguration;
import io.github.novareseller.security.model.ClientInfo;
import io.github.novareseller.security.spi.ClientInfoHolder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
		WebMvcConfig.class
})
//@ActiveProfiles(value = {"L1cache-open-springcache"})
public class Tests extends BaseTester{

//	@Autowired
//	ClientInfoHolder clientInfoHolder;

	@Test
	public void testClientInfoHolder() {

		ClientInfoHolder bean = SpringUtils.getBean(ClientInfoHolder.class);

		//ClientInfo clientInfo = clientInfoHolder.findClientInfo(1000);
		System.out.println(bean);
	}










}
