/**
 * Created on 2021/1/15.
 */
package tw.com.leadtek.nhiwidget.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import tw.com.leadtek.nhiwidget.model.JsonSuggestion;
import tw.com.leadtek.nhiwidget.model.redis.CodeBaseLongId;
import tw.com.leadtek.nhiwidget.model.redis.OrderCode;

/**
 * redis相關操作
 * 
 * @author Ken Lai
 */
@Service
public class RedisService {

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  private final static int MAX = 500;

  private String key = "ICD10-data";
  private String indexKey = "ICD10-index:";

  public List<JsonSuggestion> query(String cat, String q) {
    ObjectMapper mapper = new ObjectMapper();
    ZSetOperations<String, Object> zsetOp =
        (ZSetOperations<String, Object>) redisTemplate.opsForZSet();

    String[] ss = q.split(" ");
    HashOperations<String, String, String> hashOp = redisTemplate.opsForHash();
    Set<String> rangeSet = (Set<String>) (Set<?>) zsetOp.range(indexKey + ss[0], 0, -1);
    List<String> values = hashOp.multiGet(key, rangeSet);
    List<JsonSuggestion> result = new ArrayList<JsonSuggestion>();
    int count = 0;
    for (String string : values) {
      try {
        JsonSuggestion json = null;
        String lowerCase = string.toLowerCase();
        if (lowerCase.indexOf(ss[0]) < 0) {
          continue;
        }
        if (ss.length > 1) {
          boolean needContinue = false;
          for (int i = 1; i < ss.length; i++) {
            if (lowerCase.indexOf(ss[i]) < 0) {
              needContinue = true;
              break;
            }
          }
          if (needContinue) {
            continue;
          }
        }
        if (string.indexOf("\"p\"") > 0) {
          OrderCode oc = mapper.readValue(string, OrderCode.class);
          if (cat != null && !cat.toUpperCase().equals(oc.getCategory())) {
            continue;
          }

          // 將支付點數放在 DescEn 欄位
          oc.setDescEn(String.valueOf(oc.getP()));
          json = new JsonSuggestion(oc);
        } else {
          CodeBaseLongId cb = mapper.readValue(string, CodeBaseLongId.class);
          if (cat != null && !cat.toUpperCase().equals(cb.getCategory())) {
            continue;
          }
          json = new JsonSuggestion(cb);
        }
        // System.out.println("name=" + cb.getCode() + "," + cb.getDesc() + "," + cb.getDescEn());

        // System.out.println(string);
        // addBold(json, q);
        // System.out.println("name=" + json.getId() + "," + json.getLabel() + "," +
        // json.getValue());
        result.add(json);
        count++;
        if (count >= MAX) {
          break;
        }
      } catch (JsonMappingException e) {
        e.printStackTrace();
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    }

    return result;
  }

  private void addBold(JsonSuggestion json, String q) {
    if (json.getId().toLowerCase().indexOf(q.toLowerCase()) > -1) {
      System.out.println("id include " + q);
      json.setId(addBold(json.getId(), q));
    } else if (json.getLabel().toLowerCase().indexOf(q.toLowerCase()) > -1) {
      System.out.println("label include " + q);
      json.setLabel(addBold(json.getLabel(), q));
    }
  }

  private String addBold(String s, String q) {
    StringBuffer sb = new StringBuffer(s);
    int index = s.toLowerCase().indexOf(q.toLowerCase());
    sb.insert(index + q.length(), "</b>");
    sb.insert(index, "<b>");
    System.out.println("after bold:" + sb.toString());
    return sb.toString();
  }

  /**
   * 將 code 依2, 3, 4... 個字元塞到 redis,以達到 search suggestion功能.
   * 
   * @param op
   * @param prefix
   * @param index
   * @param code
   * @return
   */
  public int addIndexToRedisIndex(String prefix, String index, String code) {
    ZSetOperations<String, Object> op = redisTemplate.opsForZSet();
    int result = 0;
    for (int i = 2; i <= code.length(); i++) {
      String key = prefix + ":" + code.substring(0, i).toLowerCase();
      Long oldCount = op.zCard(key);
      if (oldCount == null || oldCount.longValue() == 0) {
        System.out.println("add " + key + " index=" + index);
        op.add(key, index, 0.0);
        result++;
      }
    }
    return result;
  }

  public void removeIndexToRedisIndex(String prefix, String name, int removeId) {
    ZSetOperations<String, Object> op = redisTemplate.opsForZSet();
    for (int i = 2; i <= name.length(); i++) {
      String key = name.substring(0, i);
      Set<Object> set = op.range(prefix + ":" + key, 0, -1);
      for (Object object : set) {
        if (Integer.parseInt((String) object) == removeId) {
          op.remove(prefix + ":" + key, object);
        }
      }
    }
  }

  public int getMaxId() {
    String key = "ICD10-data";
    HashOperations<String, String, String> hashOp = redisTemplate.opsForHash();
    // Set<Object> rangeSet = zsetOp.range(indexKey + searchValue, 0, -1);
    Set<String> fields = hashOp.keys(key);
    int result = -1;
    for (String field : fields) {
      int id = Integer.parseInt(field);
      if (id > result) {
        result = id;
      }
    }
    return result;
  }
  
  public void putHash(String key, String name, String value) {
    redisTemplate.opsForHash().put(key, name, value);
  }
  
  public void deleteHash(String key, String name) {
    redisTemplate.opsForHash().delete(key, name);
  }
}
