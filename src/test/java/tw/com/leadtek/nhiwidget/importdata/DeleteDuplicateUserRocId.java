/**
 * Created on 2022/3/7.
 */
package tw.com.leadtek.nhiwidget.importdata;

import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import tw.com.leadtek.nhiwidget.NHIWidget;
import tw.com.leadtek.nhiwidget.dao.USERDao;
import tw.com.leadtek.nhiwidget.model.rdb.USER;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class DeleteDuplicateUserRocId {

  @Autowired
  private USERDao userDao;
  
  @Test
  public void deleteDuplicateUserRocId() {
    List<USER> users = userDao.findAllByOrderByRocId();
    HashMap<String, String> existUser = new HashMap<String, String>();
    for (USER user : users) {
      if (user.getRocId() == null) {
        continue;
      }
      if (existUser.get(user.getRocId()) == null) {
        existUser.put(user.getRocId(), "");
      } else {
        userDao.deleteById(user.getId());
      }
    }
  }
}
