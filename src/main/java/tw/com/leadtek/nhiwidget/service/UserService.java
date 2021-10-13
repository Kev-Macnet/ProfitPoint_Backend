/**
 * Created on 2021/5/3.
 */
package tw.com.leadtek.nhiwidget.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tw.com.leadtek.nhiwidget.dao.DEPARTMENTDao;
import tw.com.leadtek.nhiwidget.dao.USERDao;
import tw.com.leadtek.nhiwidget.dao.USER_DEPARTMENTDao;
import tw.com.leadtek.nhiwidget.model.rdb.DEPARTMENT;
import tw.com.leadtek.nhiwidget.model.rdb.USER;
import tw.com.leadtek.nhiwidget.model.rdb.USER_DEPARTMENT;
import tw.com.leadtek.nhiwidget.payload.UserRequest;

/**
 * 處理用戶帳號、部門及權限
 * 
 * @author kenlai
 *
 */
@Service
public class UserService {

  private Logger logger = LogManager.getLogger();

  @Autowired
  private USERDao userDao;

  @Autowired
  private DEPARTMENTDao departmentDao;

  @Autowired
  private USER_DEPARTMENTDao userDepartmentDao;

  @Autowired
  private PasswordEncoder encoder;
  
  @Autowired
  private EMailService emailService;
  
  private HashMap<Long, DEPARTMENT> departments;
  
  private void retrieveData() {
    HashMap<Long, DEPARTMENT> newDepartments = new HashMap<Long, DEPARTMENT>();
    List<DEPARTMENT> departmentList = departmentDao.findAll();
    for (DEPARTMENT department : departmentList) {
      newDepartments.put(department.getId(), department);
    }
    departments = newDepartments;
  }

  public USER newUser(UserRequest ur) {
    String[] departments = null;
    if (ur.getDepartments() != null && ur.getDepartments().length() > 0) {
      departments = ur.getDepartments().split(",");
    }
    USER user = findUser(ur.getUsername());
    if (user != null) {
      return null;
    }
    if (ur.getDisplayName() == null) {
      ur.setDisplayName(ur.getUsername());
    }
    user = ur.convertToUSER();
    user.setPassword(encoder.encode(user.getPassword()));
    user.setCreateAt(new Date());
    user.setUpdateAt(new Date());
    user = userDao.save(user);
    saveUserDepartment(user.getId(), departments);
    return user;
  }

  public String updateUser(UserRequest ur) {
    Optional<USER> optional = userDao.findById(ur.getId());
    if (optional == null) {
      return "帳號不存在";
    }
    USER existUser = optional.get();
    existUser.setDisplayName(ur.getDisplayName());
    existUser.setEmail(ur.getEmail());
    existUser.setRole(ur.getRole());
    existUser.setStatus(ur.getStatus());
    existUser.setUpdateAt(new Date());

    String[] departments = null;
    if (ur.getDepartments() != null && ur.getDepartments().length() > 0) {
      departments = ur.getDepartments().split(",");
    }
    existUser.setUpdateAt(new Date());
    existUser = userDao.save(existUser);
    saveUserDepartment(existUser.getId(), departments);
    return null;
  }

  public void saveUserDepartment(Long userId, String[] departments) {
    if (departments == null || departments.length == 0 || userId == null) {
      return;
    }
    List<USER_DEPARTMENT> udList = userDepartmentDao.findByUserIdOrderByDepartmentId(userId);
    if (udList == null || udList.size() == 0) {
      for (String department : departments) {
        DEPARTMENT dep = findDepartment(department);
        if (dep != null) {
          USER_DEPARTMENT ud = new USER_DEPARTMENT();
          ud.setUserId(userId);
          ud.setDepartmentId(dep.getId());
          userDepartmentDao.save(ud);
        }
      }
    } else {
      List<Long> readyToSaveDepartment = new ArrayList<Long>();
      for (String department : departments) {
        DEPARTMENT dep = findDepartment(department);
        if (dep != null) {
          readyToSaveDepartment.add(dep.getId());
        }
      }

      for (USER_DEPARTMENT ud : udList) {
        boolean isFound = false;
        for (Long departmentId : readyToSaveDepartment) {
          if (departmentId.longValue() == ud.getDepartmentId().longValue()) {
            isFound = true;
            readyToSaveDepartment.remove(departmentId);
            break;
          }
        }
        if (!isFound) {
          userDepartmentDao.delete(ud);
        }
      }
      for (Long departmentId : readyToSaveDepartment) {
        USER_DEPARTMENT ud = new USER_DEPARTMENT();
        ud.setDepartmentId(departmentId);
        ud.setUserId(userId);
        userDepartmentDao.save(ud);
      }
    }
  }

  public USER findUser(String username) {
    Optional<USER> user = userDao.findByUsername(username);
    return user.orElse(null);
  }

  public String deleteUser(Long id) {
    Optional<USER> optional = userDao.findById(id);
    if (optional == null) {
      return "帳號不存在";
    }
    USER existUser = optional.get();
    try {
      userDao.deleteById(existUser.getId());
      userDepartmentDao.deleteByUserId(existUser.getId());
    } catch (Exception e) {
      e.printStackTrace();
      return "帳號刪除有誤";
    }
    return null;
  }
  
  public String login(String username, String password) {
    if (username == null || username.length() == 0 || password == null || password.length() == 0) {
      return "帳號密碼有誤";
    }
    System.out.println("login username:" + username);
    Optional<USER> optional = userDao.findByUsername(username);
    if (optional == null) {
      return "帳號不存在";
    }
    USER existUser = optional.orElse(new USER());
    if (encoder.matches(password, existUser.getPassword())){
      return null;
    }
    return "密碼有誤";
  }
  
  public String updateDepartment(DEPARTMENT department) {
    Optional<DEPARTMENT> optional = departmentDao.findById(department.getId());
    if (optional == null) {
      return "帳號不存在";
    }
    DEPARTMENT existDepartment = optional.get();
    try {
      existDepartment.setCode(department.getCode());
      existDepartment.setName(department.getName());
      existDepartment.setNhCode(department.getNhCode());
      existDepartment.setNhName(department.getNhName());
      existDepartment.setNote(department.getNote());
      existDepartment.setParentId(department.getParentId());
      existDepartment.setStatus(department.getStatus());
      existDepartment.setUpdateAt(new Date());
      departmentDao.save(existDepartment);
    } catch (Exception e) {
      e.printStackTrace();
      return "更新刪除有誤";
    }
    return null;
  }

  public boolean changePassword(String username, String password) {
    USER existUser = findUser(username);
    if (existUser == null) {
      return false;
    }
    existUser.setPassword(encoder.encode(password));
    userDao.save(existUser);
    return true;
  }

  public boolean updateUser(USER user, String[] departments) {
    USER existUser = null;
    if (user.getId() == null) {
      existUser = findUser(user.getUsername());
    } else {
      Optional<USER> optUser = userDao.findById(user.getId());
      existUser = optUser.orElse(null);
    }
    if (existUser == null) {
      return false;
    }
    existUser.setDisplayName(user.getDisplayName());
    existUser.setEmail(user.getEmail());
    existUser.setStatus(user.getStatus());
    existUser.setUsername(user.getUsername());
    existUser.setUpdateAt(new Date());
    existUser = userDao.save(user);
    saveUserDepartment(existUser.getId(), departments);
    return true;
  }

  public DEPARTMENT newDepartment(DEPARTMENT department) {
    DEPARTMENT existDepartment = findDepartment(department.getName());
    if (existDepartment != null) {
      return null;
    }
    department.setUpdateAt(new Date());
    DEPARTMENT result = departmentDao.save(department);
    departments.put(result.getId(), result);
    return result;
  }

  public DEPARTMENT findDepartment(String name) {
    Optional<DEPARTMENT> department = departmentDao.findByName(name);
    return department.orElse(null);
  }

  public String deleteDepartment(Long id) {
    Optional<DEPARTMENT> existDepartment = departmentDao.findById(id); 
    if (existDepartment == null) {
      return "部門id不存在";
    }
    departmentDao.deleteById(id);
    return null;
  }

  //@PreAuthorize("hasAuthority('administrator')")
  public List<UserRequest> getAllUser(String funcType, String funcTypeC) {
    if (departments == null) {
      retrieveData();
    }
    List<UserRequest> result = new ArrayList<UserRequest>();

    List<USER> list = userDao.findAll();
    List<USER_DEPARTMENT> udList = userDepartmentDao.findAll();
    if ((funcTypeC != null && !"不分科".equals(funcTypeC)) || (funcType != null && !"00".equals(funcType))) {
      Long depId = null;
      if (funcType != null) {
        depId = getDepartmentIdByCode(funcType);
      } else if (funcTypeC != null) {
        depId = getDepartmentIdByName(funcTypeC);
      }
      if (depId < 0) {
        return result;
      }
      List<USER_DEPARTMENT> newUserDepartmentList = new ArrayList<USER_DEPARTMENT>();
      for (USER_DEPARTMENT ud : udList) {
        if (ud.getDepartmentId().longValue() == depId) {
          newUserDepartmentList.add(ud);
        }
      }
      udList = newUserDepartmentList;
    }
    for (USER user : list) {
      String departments = getDepartmentsByUserId(user.getId(), udList);
      if (departments.length() == 0) {
        continue;
      }
      UserRequest ur = new UserRequest(user);
      ur.setPassword(null);
      ur.setCreateAt(null);
      ur.setDepartments(departments);
      result.add(ur);
    }
    return result;
  }
  
  private Long getDepartmentIdByName(String name) {
    for (DEPARTMENT dep : departments.values()) {
      if (dep.getName().equals(name) || dep.getNhName().equals(name)) {
        return dep.getId();
      }
    }
    return -1L;
  }
  
  private Long getDepartmentIdByCode(String code) {
    for (DEPARTMENT dep : departments.values()) {
      if (dep.getNhCode().equals(code) || dep.getCode().equals(code)) {
        return dep.getId();
      }
    }
    return -1L;
  }
  
  public List<DEPARTMENT> getAllDepartment() {
    if (departments == null) {
      retrieveData();
    }
    return new ArrayList<DEPARTMENT>(departments.values());
  }
  
  private String getDepartmentsByUserId(Long id,  List<USER_DEPARTMENT> udList) {
    StringBuffer sb = new StringBuffer();
    for (USER_DEPARTMENT ud : udList) {
      if (ud.getUserId().longValue() == id) {
        sb.append(departments.get(ud.getDepartmentId()).getName());
        sb.append(",");
      }
    }
    if (sb.length() > 1) {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }
  
  public String forgetPassword(String username, String email) {
    USER existUser = findUser(username);
    if (existUser == null) {
      return "帳號不存在";
    }
    if (existUser.getEmail() != null && !existUser.getEmail().equals(email)) {
      return "email不正確";
    }
    String newPassword = generateCommonLangPassword();
    emailService.sendMail("忘記密碼-重設新密碼", existUser.getEmail(), "系統隨機產生密碼:" + newPassword);
    existUser.setPassword(encoder.encode(newPassword));
    existUser.setUpdateAt(new Date());
    userDao.save(existUser);
    return null;
  }
  
  public String generateCommonLangPassword() {
    String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
    String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
    String numbers = RandomStringUtils.randomNumeric(2);
    // String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
    String totalChars = RandomStringUtils.randomAlphanumeric(2);
    String combinedChars =
        upperCaseLetters.concat(lowerCaseLetters).concat(numbers).concat(totalChars);
    List<Character> pwdChars =
        combinedChars.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
    Collections.shuffle(pwdChars);
    String password = pwdChars.stream()
        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
    return password;
  }

  // public boolean newRole(ROLE role) {
  // ROLE existRole = findRole(role.getName());
  // if (existRole != null) {
  // return false;
  // }
  // roleDao.save(role);
  // return true;
  // }
  //
  // public ROLE findRole(String name) {
  // Optional<ROLE> role = roleDao.findByName(name);
  // return role.orElse(null);
  // }
  //
  // public boolean deleteRole(String name) {
  // ROLE existRole = findRole(name);
  // if (existRole == null) {
  // return false;
  // }
  // roleDao.deleteById(existRole.getId());
  // return true;
  // }

}
