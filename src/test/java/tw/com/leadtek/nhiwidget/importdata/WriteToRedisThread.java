/**
 * Created on 2021/1/18.
 */
package tw.com.leadtek.nhiwidget.importdata;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import tw.com.leadtek.nhiwidget.model.redis.CodeBaseLongId;
import tw.com.leadtek.nhiwidget.model.redis.OrderCode;

public class WriteToRedisThread implements Runnable {

  private WriteToRedisThreadPool pool;

  private RedisTemplate<String, Object> redis;

  private CodeBaseLongId cb;

  private String key;

  private static final List<String> ignoreWords = Arrays.asList("other", "the", "to", "of", "and");

  private String searchKey;

  public WriteToRedisThread(RedisTemplate<String, Object> redis, WriteToRedisThreadPool pool) {
    this.redis = redis;
    this.pool = pool;
  }

  public CodeBaseLongId getCb() {
    return cb;
  }

  public void setCb(CodeBaseLongId cb) {
    this.cb = cb;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public WriteToRedisThreadPool getPool() {
    return pool;
  }

  public void setPool(WriteToRedisThreadPool pool) {
    this.pool = pool;
  }

  public String getSearchKey() {
    return searchKey;
  }

  public void setSearchKey(String searchKey) {
    this.searchKey = searchKey;
  }

  @Override
  public void run() {
    if (searchKey != null) {
      search(false);
      return;
    } else {
      // insert 前先搜尋是否已存在
      searchKey = cb.getCode();
      if (search(true)) {
        System.out.println("duplicate code:" + cb.getCode() +"," + cb.getCategory());
        pool.decrease();
        return;
      }
    }
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(Include.NON_NULL);
    ZSetOperations<String, Object> op = redis.opsForZSet();
    try {
      String json = objectMapper.writeValueAsString(cb);
      // 1. save to data
      redis.opsForHash().put(key + "-data", String.valueOf(cb.getId()), json);
      System.out.println("insert data:" + cb.getId());
      // 2. save code to index for search
      addIndexToRedisIndex(op, key + "-index", String.valueOf(cb.getId()), cb.getCode());

      if (cb.getDescEn() != null && cb.getDescEn().length() > 0) {
        String[] descList = cb.getDescEn().split(" ");
        for (String string : descList) {
          if (ignoreWords.contains(string.toLowerCase())) {
            continue;
          }
          String newKey = removeCharacter(string);
          if (newKey.length() < 2) {
            continue;
          }
          addIndexToRedisIndex(op, key + "-index", String.valueOf(cb.getId()), newKey);
        }
      }
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    pool.decrease();
  }

  public boolean search(boolean needReturn) {
    String key = "ICD10-data";
    String indexKey = "ICD10-index:";

    ObjectMapper mapper = new ObjectMapper();
    ZSetOperations<String, Object> zsetOp = (ZSetOperations<String, Object>) redis.opsForZSet();
    HashOperations<String, String, String> hashOp = redis.opsForHash();
    Set<Object> rangeSet = zsetOp.range(indexKey + searchKey, 0, -1);

    boolean isFound = false;
    for (Object object : rangeSet) {
      //System.out.println(object);
      String s = hashOp.get(key, object);
      // System.out.println(s);

      try {
        //if (s.indexOf("\"p\"") > 0) {
          OrderCode oc =  mapper.readValue(s, OrderCode.class);
          if (oc.getCode().equals(searchKey) && oc.getCategory().equals(cb.getCategory())) {
            isFound = true;
            if (needReturn) {
              return true;
            }
            break;
          }
//        } else {
//          CodeBaseLongId cbRedis = mapper.readValue(s, CodeBaseLongId.class);
//          // System.out.println("name=" + cb.getCode() + "," + cb.getDesc() + "," + cb.getDescEn());
//          if (cbRedis.getCode().equals(searchKey) && cbRedis.getDescEn().equals(cb.getDescEn())) {
//            isFound = true;
//            break;
//          }
//        }
      } catch (JsonMappingException e) {
        e.printStackTrace();
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
    }
    if (!isFound) {
      if (needReturn) {
        return false;
      }
      System.err.println("Not Found:" + searchKey);
      pool.addTotal();
    }
    pool.decrease();
    return isFound;
  }

  private int addIndexToRedisIndex(ZSetOperations<String, Object> op, String prefix, String index,
      String name) {
    int result = 0;
    for (int i = 2; i <= name.length(); i++) {
      String key = name.substring(0, i);
      op.add(prefix + ":" + key, index, 0.0);
      result++;
    }
    return result;
  }

  private String removeCharacter(String s) {
    StringBuffer sb = new StringBuffer(s);
    for (int i = s.length() - 1; i >= 0; i--) {
      if (s.charAt(i) == '{' || s.charAt(i) == '}' || s.charAt(i) == '(' || s.charAt(i) == ')'
          || s.charAt(i) == '[' || s.charAt(i) == ']' || s.charAt(i) == ',') {
        sb.deleteCharAt(i);
      }
    }
    return sb.toString();
  }

}
