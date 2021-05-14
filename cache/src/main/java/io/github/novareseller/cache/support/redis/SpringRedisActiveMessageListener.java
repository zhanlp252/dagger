package io.github.novareseller.cache.support.redis;

import net.oschina.j2cache.cluster.ClusterPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * 监听二缓key失效，主动清除本地缓存
 * 
 * @author zhangsaizz
 *
 */
public class SpringRedisActiveMessageListener implements MessageListener {

	private String namespace;

	private ClusterPolicy clusterPolicy;

	private static final String COLON = ":";
	
	SpringRedisActiveMessageListener(ClusterPolicy clusterPolicy, String namespace) {
		this.clusterPolicy = clusterPolicy;
		this.namespace = namespace;
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		String key = message.toString();
		if (key == null) {
			return;
		}
		if (key.startsWith(namespace + COLON)) {
			String[] k = key.replaceFirst(namespace + COLON, "").split(COLON, 2);
			if(k.length != 2) {
				return;
			}
			clusterPolicy.evict(k[0], k[1]);
		}

	}

}
