package com.example.sumpdata;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfiguration extends CachingConfigurerSupport {

    @Value("${spring.cache.caffeine.spec}")
    private String caffeineSpec;

    @Value("${spring.cache.caffeine.enabled:true}")
    private boolean caffeineEnabled;

    @Bean
    public CacheManager cacheManager() {
        // Default cache manager is caffeine for this implementation.
        // Possible improvement would be to make configurable via prop to switch like the default implementation
        // by reading spring.cache.type prop.
        return caffeineCacheManager();
    }

    @Bean
    public CacheManager caffeineCacheManager() {
        if (!caffeineEnabled) {
            return disabledCacheManager();
        }
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("controller");
        Caffeine<Object, Object> caffeine = Caffeine.from(caffeineSpec);
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }

    @Value("${spring.cache.redis.time-to-live:10000}")
    private long redisTTL;

    @Value("${spring.cache.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.cache.redis.port:6379}")
    private int redisPort;

    @Value("${spring.cache.redis.enabled:true}")
    private boolean redisEnabled;


    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisHost, redisPort);

        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        if (!redisEnabled) {
            return disabledCacheManager();
        }
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMillis(redisTTL));
        RedisCacheManager cacheManager = RedisCacheManager.builder(connectionFactory)
                .withCacheConfiguration("dataaccess", config).build();
        return cacheManager;
    }

    @Bean
    public CacheManager disabledCacheManager() {
        return new NoOpCacheManager();
    }

}
