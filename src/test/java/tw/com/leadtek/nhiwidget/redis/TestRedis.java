/**
 * Created on 2021/1/15.
 */
package tw.com.leadtek.nhiwidget.redis;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

  @Ignore
  @Test
  public void testSet() {
    // 新增一組 key/value
    srt.opsForValue().set("test-string-value", "Hello Redis");
  }
  
}
