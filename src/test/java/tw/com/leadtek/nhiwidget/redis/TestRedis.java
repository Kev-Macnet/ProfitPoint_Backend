/**
 * Created on 2021/1/15.
 */
package tw.com.leadtek.nhiwidget.redis;

import java.util.List;
import java.util.Set;
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
import tw.com.leadtek.nhiwidget.model.JsonSuggestion;
import tw.com.leadtek.nhiwidget.payload.MRDetail;
import tw.com.leadtek.nhiwidget.service.RedisService;
import tw.com.leadtek.nhiwidget.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class TestRedis {

  @Autowired
  private StringRedisTemplate srt;
  
  @Autowired
  private RedisTemplate<String, Object> redisTemplate;
  
  @Autowired
  private RedisService redisService;
  
  public final static String USER = "USER:";

  @Ignore
  @Test
  public void testSet() {
    // 新增一組 key/value
    srt.opsForValue().set("test-string-value", "Hello Redis");
  }
 
  @Ignore
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
  
  @Ignore
  @Test
  public void testMREDIT() {
    String key = UserService.MREDIT + "1190188";
    Set<Object> sets = redisService.hkeys(key);
    if (sets != null && sets.size() > 0) {
      System.out.println("sets not null");
      //redisService.deleteHash(key, "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJrZW5sYWkiLCJ1aWQiOjQzMCwicm9sZSI6IkEiLCJleHAiOjE2NDAwMjE4MzN9.4w9mUvevaBKfYVxAr7bT_ecBm99VauvPT_6V8Cfu43Y");
    } else {
       System.out.println("未找到開始編輯 token");
    }
  }
  
  @Test
  public void testSuggestion() {
    String code = "A53.9";
    List<JsonSuggestion> queryList = redisService.query(null, code.toLowerCase(), false);
    if (queryList != null) {
      for (JsonSuggestion jsonSuggestion : queryList) {
        System.out.println(jsonSuggestion.getId() + ":" + jsonSuggestion.getValue());
        if (jsonSuggestion.getId().equals(code.toUpperCase())) {
          System.out.println(jsonSuggestion.getValue());
          break;
        }
      }
    }
  }
}
