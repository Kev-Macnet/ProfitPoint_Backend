/**
 * Created on 2021/1/15.
 */
package tw.com.leadtek.nhiwidget.redis;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import tw.com.leadtek.nhiwidget.NHIWidget;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class TestRedis {

  @Autowired
  private StringRedisTemplate srt;
  
  @Autowired
  private RedisTemplate<String, Object> redisTemplate;
  
  public final static String USER = "USER:";

  @Ignore
  @Test
  public void testSet() {
    // 新增一組 key/value
    srt.opsForValue().set("test-string-value", "Hello Redis");
  }
 
  @Test
  public void testHash() {
    String username = "leadtek";
    String key = USER + username;
    Object loginTime = redisTemplate.opsForHash().get(key, "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsZWFkdGVrIiwicm9sZSI6IkEiLCJleHAiOjE2MzQ4OTIzOTh9.CFsqcCa25ri2puf0nvwFLsfA0tBv_hIBMV0v7zbYRuw");
    if (loginTime == null) {
      System.out.println("sets is null");
    } else {
      long usedTime = System.currentTimeMillis() - Long.parseLong((String) loginTime);
      System.out.println("used time(" + username + "):" + usedTime);
    }
  }
}
