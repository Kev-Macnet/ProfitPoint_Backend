/**
 * Created on 2021/8/11.
 */
package tw.com.leadtek.nhiwidget.importdata;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import tw.com.leadtek.nhiwidget.NHIWidget;
import tw.com.leadtek.nhiwidget.constant.ROLE_TYPE;
import tw.com.leadtek.nhiwidget.dao.DEPARTMENTDao;
import tw.com.leadtek.nhiwidget.dao.IP_DDao;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.OP_DDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dao.USERDao;
import tw.com.leadtek.nhiwidget.dao.USER_DEPARTMENTDao;
import tw.com.leadtek.nhiwidget.model.rdb.DEPARTMENT;
import tw.com.leadtek.nhiwidget.model.rdb.USER;
import tw.com.leadtek.nhiwidget.model.rdb.USER_DEPARTMENT;
import tw.com.leadtek.nhiwidget.service.CodeTableService;
import tw.com.leadtek.tools.StringUtility;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class CheckDepartmentAndDoctor {

  @Autowired
  private OP_PDao oppDao;
  
  @Autowired
  private OP_DDao opdDao;
  
  @Autowired
  private IP_PDao ippDao;
  
  @Autowired
  private IP_DDao ipdDao;
  
  @Autowired
  private USERDao userDao;
  
  @Autowired
  private USER_DEPARTMENTDao userDepartmentDao;
  
  @Autowired
  private DEPARTMENTDao departmentDao;
  
  @Autowired
  private CodeTableService codeTable;
  
  /**
   * 存放醫生ID, 部門名稱
   */
  private HashMap<String, String> doctors = new HashMap<String, String>();
  
  /**
   * 存放部門代碼，部門
   */
  private HashMap<String, DEPARTMENT> departments = new HashMap<String, DEPARTMENT>();
  
  /**
   * 存放醫生ID，醫生所屬部門連結
   */
  private HashMap<String, USER_DEPARTMENT> userDepartment = new HashMap<String, USER_DEPARTMENT>();
  
  @Test
  public void importDoctor() {
    importExistData();
    checkList(opdDao.findDepartmentAndDoctor());
    checkList(ipdDao.findDepartmentAndDoctor());
    checkList(oppDao.findDepartmentAndDoctor());
    checkList(ippDao.findDepartmentAndDoctor());
  }
  
  private void importExistData() {
    List<USER> userList = userDao.findAll();
    List<USER_DEPARTMENT> userDepartmentList = userDepartmentDao.findAll();
    List<DEPARTMENT> departmentList = departmentDao.findAll();
    for (DEPARTMENT department : departmentList) {
      departments.put(department.getNhCode(), department);
    }
    for (USER user : userList) {
      for (USER_DEPARTMENT ud : userDepartmentList) {
        if (ud.getUserId() == user.getId()) {
          doctors.put(user.getRocId(), getDepartmentName(ud.getDepartmentId(), departmentList));
          userDepartment.put(user.getRocId(), ud);
          break;
        }
      }
    }
  }
  
  private String getDepartmentName(long id, List<DEPARTMENT> departmentList) {
    for (DEPARTMENT department : departmentList) {
      if (department.getId() == id) {
        return department.getNhCode();
      }
    }
    return null;
  }
  
  private void checkList(List<Object[]> list) {
    List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
    for (Object[] obj : list) {
      String rocId = (String) obj[0];
      if (rocId.endsWith("*") || rocId.length() != 10) {
        continue;
      }
      rocId = StringUtility.maskString(rocId, StringUtility.MASK_MOBILE);
      DEPARTMENT department = departments.get((String) obj[1]);
      if (department == null) {
        department = new DEPARTMENT();
        department.setNhCode((String) obj[1]);
        department.setCode((String) obj[1]);
        department.setName(codeTable.getDesc("FUNC_TYPE", department.getCode()));
        department.setNhName(department.getName());
        department.setStatus(1);
        department.setUpdateAt(new Date());
        department = departmentDao.save(department);
        departments.put(department.getNhCode(), department);
      }
      if (doctors.get(rocId) == null) {
        // db 無此人
        USER user = new USER();
        user.setCreateAt(new Date());
        user.setUpdateAt(new Date());
        user.setRocId(rocId);
        user.setRole("E");
        user.setUsername(user.getRocId());
        user = userDao.save(user);
        
        USER_DEPARTMENT ud = new USER_DEPARTMENT();
        ud.setDepartmentId(department.getId());
        ud.setUserId(user.getId());
        userDepartmentDao.save(ud);
        doctors.put(user.getRocId(), department.getName());
      }
      
    }
  }
  
}
