/**
 * Created on 2021/1/15.
 */
package tw.com.leadtek.nhiwidget.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RedisConfig {
  @Bean
  @ConditionalOnMissingBean(name = "redisTemplate")
  public RedisTemplate<String, Object> redisTemplate(
          RedisConnectionFactory redisConnectionFactory) {

      Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
      ObjectMapper om = new ObjectMapper();
      om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
      //om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
      jackson2JsonRedisSerializer.setObjectMapper(om);

      RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
      template.setConnectionFactory(redisConnectionFactory);
      template.setKeySerializer(jackson2JsonRedisSerializer);
      template.setValueSerializer(jackson2JsonRedisSerializer);
      template.setHashKeySerializer(jackson2JsonRedisSerializer);
      template.setHashValueSerializer(jackson2JsonRedisSerializer);
      template.afterPropertiesSet();
      
      // 若不加 Serializer，存到redis會是 "\"B55.\"" 而非 "B55."
      RedisSerializer<String> stringSerializer = new StringRedisSerializer();
      template.setKeySerializer(stringSerializer);
      template.setValueSerializer(stringSerializer);
      template.setHashKeySerializer(stringSerializer);
      template.setHashValueSerializer(stringSerializer);
      return template;
  }

  @Bean
  @ConditionalOnMissingBean(StringRedisTemplate.class)
  public StringRedisTemplate stringRedisTemplate(
          RedisConnectionFactory redisConnectionFactory) {
      StringRedisTemplate template = new StringRedisTemplate();
      template.setConnectionFactory(redisConnectionFactory);
      return template;
  }
}