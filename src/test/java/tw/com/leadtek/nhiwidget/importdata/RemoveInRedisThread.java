/**
 * Created on 2021/1/18.
 */
package tw.com.leadtek.nhiwidget.importdata;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

public class RemoveInRedisThread implements Runnable {

  private RedisTemplate<String, Object> redis;

  private String key;

  public RemoveInRedisThread(RedisTemplate<String, Object> redis) {
    this.redis = redis;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @Override
  public void run() {
    ZSetOperations<String, Object> zsetOp = (ZSetOperations<String, Object>) redis.opsForZSet();
    zsetOp.removeRange(key, 0, -1);
  }

}
