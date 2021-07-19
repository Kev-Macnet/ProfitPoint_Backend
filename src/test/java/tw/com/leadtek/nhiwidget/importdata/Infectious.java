/**
 * Created on 2021/4/19.
 */
package tw.com.leadtek.nhiwidget.importdata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import tw.com.leadtek.nhiwidget.NHIWidget;
import tw.com.leadtek.nhiwidget.model.redis.OrderCode;

/**
 * 處理法定傳染病
 * @author kenlai
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class Infectious {
  
  @Autowired
  private RedisTemplate<String, Object> redis;

  @Test
  public void getICDDescription() {
    HashSet<String> codes = new HashSet<String>();
    System.out.println("getICDDescription");
    try {
      BufferedReader br = new BufferedReader(new FileReader(new File("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\法定傳染病\\icd.txt")));
      BufferedWriter bw =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\資料匯入用\\法定傳染病\\icd_desc.txt"), "BIG5"));
      String s = null;
      while ((s = br.readLine()) != null) {
        if (codes.contains(s.trim())) {
          System.out.println(s + " duplicate");
          continue;
        }
        codes.add(s.trim());
        codes.add(s.trim());
        OrderCode oc = search(s.trim().toLowerCase(), "ICD10-CM");
        if (oc != null) {
          //System.out.println(oc.getCode() + ":" + oc.getDescEn() + "(" + oc.getDesc() +")");
          bw.write(oc.getCode());
          bw.write('|');
          bw.write(oc.getDesc());
          bw.write('|');
          bw.write(oc.getDescEn());       
          bw.newLine();
        } else {
          bw.write(s.trim());
          bw.write('|');
          bw.write('|');
          bw.newLine();
        }
      }
      br.close();
      bw.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public OrderCode search(String searchKey, String category) {
    String key = "ICD10-data";
    String indexKey = "ICD10-index:";

    ObjectMapper mapper = new ObjectMapper();
    ZSetOperations<String, Object> zsetOp = (ZSetOperations<String, Object>) redis.opsForZSet();
    HashOperations<String, String, String> hashOp = redis.opsForHash();
    Set<Object> rangeSet = zsetOp.range(indexKey + searchKey, 0, -1);

    boolean isFound = false;
    for (Object object : rangeSet) {
      // 找到 ICD10-data 的 index
      String s = hashOp.get(key, object);
      //System.out.println(s);

      try {
        //if (s.indexOf("\"p\"") > 0) {
          OrderCode oc =  mapper.readValue(s, OrderCode.class);
          if (oc.getCode().toLowerCase().equals(searchKey) && oc.getCategory().equals(category)) {
            return oc;
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
    return null;
  }
}
