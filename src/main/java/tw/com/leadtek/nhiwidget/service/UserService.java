/**
 * Created on 2021/5/3.
 */
package tw.com.leadtek.nhiwidget.service;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.constant.LogType;
import tw.com.leadtek.nhiwidget.constant.ROLE_TYPE;
import tw.com.leadtek.nhiwidget.dao.DEPARTMENTDao;
import tw.com.leadtek.nhiwidget.dao.USERDao;
import tw.com.leadtek.nhiwidget.dao.USER_DEPARTMENTDao;
import tw.com.leadtek.nhiwidget.model.rdb.DEPARTMENT;
import tw.com.leadtek.nhiwidget.model.rdb.USER;
import tw.com.leadtek.nhiwidget.model.rdb.USER_DEPARTMENT;
import tw.com.leadtek.nhiwidget.payload.UserRequest;
import tw.com.leadtek.nhiwidget.security.jwt.JwtUtils;
import tw.com.leadtek.nhiwidget.sql.LogDataDao;
import tw.com.leadtek.tools.DateTool;

/**
 * 處理用戶帳號、部門及權限
 * 
 * @author kenlai
 *
 */
@Service
public class UserService {

  private Logger logger = LogManager.getLogger();
  
  private final static String ALGORITHM = "AES";
  
  private final static Key KEY = new SecretKeySpec(new byte[]{'L', 'E', 'A', 'D', 'T', 'E', 'K', 'P', 'R', 'O', 'F', 'I', 'T', 'P', 'O', 'I'}, ALGORITHM);

  public final static String USER_PREFIX = "USER:";

  public final static String EDITING = "EDITING";

  /**
   * 存放用戶正在編輯的病歷id
   */
  public final static String MREDIT = "MREDIT:";

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

  @Autowired
  private RedisService redisService;

  @Autowired
  private LogDataDao logDataDao;

  @Autowired
  private JwtUtils jwtUtils;
  
  @Autowired
  private ReportService reportService;
  
  @Autowired
  private CodeTableService codeTableService; 

  @Value("${project.jwt.afk}")
  private long afkTime;
  
  private HashMap<Long, DEPARTMENT> departmentHash;

  @Autowired
  private HttpServletRequest httpServletReq;
  
  public void retrieveData() {
    HashMap<Long, DEPARTMENT> newDepartments = new HashMap<Long, DEPARTMENT>();
    List<DEPARTMENT> departmentList = departmentDao.findAll();
    for (DEPARTMENT department : departmentList) {
      newDepartments.put(department.getId(), department);
    }
    departmentHash = newDepartments;
  }

  public USER newUser(UserRequest ur) {
    String[] departments = null;
    if (ur.getDepartments() != null && ur.getDepartments().length() > 0) {
      departments = ur.getDepartments().split(",");
    }
    USER user = findUser(ur.getUsername());
    if (user != null) {
      List<USER_DEPARTMENT> udList = userDepartmentDao.findByUserIdOrderByDepartmentId(user.getId());
      if (departments != null) {
        for (String department : departments) {
          DEPARTMENT dep = findDepartment(department);
          boolean isNewDepartment = true;
          for (USER_DEPARTMENT ud : udList) {
            if (dep.getId().longValue() == ud.getDepartmentId()) {
              isNewDepartment = false;
              break;
            }
          }
          if (isNewDepartment) {
            USER_DEPARTMENT ud = new USER_DEPARTMENT();
            ud.setDepartmentId(dep.getId());
            ud.setUserId(user.getId());
            userDepartmentDao.save(ud);
          }
        }
      }
      boolean isDirty = false;
      if (user.getDisplayName() != null && !user.getDisplayName().equals(ur.getDisplayName())) {
        isDirty = true;
        user.setDisplayName(ur.getDisplayName());
      }
      if (user.getEmail() != null && !user.getEmail().equals(ur.getEmail())) {
        isDirty = true;
        user.setEmail(ur.getEmail());
      } else if (ur.getEmail() != null && !ur.getEmail().equals(user.getEmail())) {
        isDirty = true;
        user.setEmail(ur.getEmail());
      } 
      isDirty = isDirty | updatePassword(user, ur.getPassword());
      if (!isDirty) {
        return user;  
      }
    } else {
      if (ur.getDisplayName() == null) {
        ur.setDisplayName(ur.getUsername());
      }
      user = ur.convertToUSER();
      user.setInhId(user.getUsername());
      updatePassword(user, ur.getPassword());
    }
    user.setCreateAt(new Date());
    user.setUpdateAt(new Date());
    user = userDao.save(user);
    saveUserDepartment(user.getId(), departments);
    return user;
  }
  
  /**
   * 
   * @param user
   * @param newPassword
   * @return true:密碼有變更，false:密碼無變更
   */
  private boolean updatePassword(USER user, String newPassword) {
    if (user.getEmail() != null) {
      if (newPassword == null || newPassword.length() == 0) {

        logger.info("sendEmail:" + user.getEmail());
        String randomPassword = generateCommonLangPassword();
        if (emailService.fromEmail == null || emailService.fromEmail.indexOf("@") < 0) {
          randomPassword = "test";
        }
        emailService.sendMail(
            "新增帳號" + user.getUsername() + "密碼", user.getEmail(), "系統產生密碼:" + randomPassword);

        user.setPassword(encoder.encode(randomPassword));
        user.setStatus(USER.STATUS_ACTIVE);
      } else {
        user.setPassword(encoder.encode(newPassword));
        user.setStatus(USER.STATUS_ACTIVE);
      }
      return true;
    }
    return false;
  }

  public String updateUser(UserRequest ur) {
    Optional<USER> optional = userDao.findById(ur.getId());
    if (!optional.isPresent()) {
      return "帳號不存在";
    }
    USER existUser = optional.get();
    existUser.setDisplayName(ur.getDisplayName());
    if (ur.getEmail() != null && ur.getEmail().indexOf("@") > 0) {
      existUser.setEmail(ur.getEmail());
    }
    existUser.setRole(ur.getRole());
    existUser.setStatus(ur.getStatus());
    existUser.setUpdateAt(new Date());
    if (ur.getRocId() != null && ur.getRocId().length() > 0) {
      existUser.setRocId(ur.getRocId());
    }
    String[] departments = null;
    if (ur.getDepartments() != null && ur.getDepartments().length() > 0) {
      departments = ur.getDepartments().split(",");
    }
    existUser.setUpdateAt(new Date());
    existUser = userDao.save(existUser);
    saveUserDepartment(existUser.getId(), departments);
    return null;
  }

  /**
   * 儲存使用者部門
   * @param userId
   * @param departments String[] 部門名稱
   */
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
    List<USER> list = userDao.findByUsername(username);
    if (list != null && list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  public USER findUserById(long id) {
    Optional<USER> optional = userDao.findById(id);
    if (optional.isPresent()) {
      return optional.get();
    }
    return null;
  }
  
  public USER findUserByDisplayName(String displayName) {
    List<USER> list = userDao.findByDisplayName(displayName);
    if (list != null && list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  public String deleteUser(Long id) {
    Optional<USER> optional = userDao.findById(id);
    if (!optional.isPresent()) {
      return "帳號不存在";
    }
    try {
      userDao.deleteById(id);
      userDepartmentDao.removeByUserId(id);
    } catch (Exception e) {
    }
    return null;
  }

  public String login(String username, String password) {
    if (username == null || username.length() == 0 || password == null || password.length() == 0) {
      return "帳號密碼有誤";
    }
    List<USER> list = userDao.findByUsername(username);
    if (list == null || list.size() == 0) {
      return "帳號不存在";
    }
    USER existUser = list.get(0);
    if (encoder.matches(password, existUser.getPassword())) {
      return null;
    }
    return "密碼有誤";
  }

  public String updateDepartment(DEPARTMENT department) {
    Optional<DEPARTMENT> optional = departmentDao.findById(department.getId());
    if (!optional.isPresent()) {
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
      departmentHash.put(department.getId(), existDepartment);
    } catch (Exception e) {
      e.printStackTrace();
      return "更新帳號有誤";
    }
    return null;
  }

  public boolean changePassword(String username, String password) {
    USER existUser = findUser(username);
    if (existUser == null) {
      return false;
    }
    existUser.setPassword(encoder.encode(password));
    existUser.setStatus(USER.STATUS_ACTIVE);
    userDao.save(existUser);
    
    httpServletReq.setAttribute(LogType.ACTION_U.name()+"_PKS", Arrays.asList(new Long[]{existUser.getId()}));
    
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
    if (departmentHash == null) {
      retrieveData();
    }
    DEPARTMENT existDepartment = findDepartment(department.getName());
    if (existDepartment != null) {
      return null;
    }
    department.setUpdateAt(new Date());
    DEPARTMENT result = departmentDao.save(department);
    departmentHash.put(result.getId(), result);
    return result;
  }

  public DEPARTMENT findDepartment(String name) {
    List<DEPARTMENT> departments = departmentDao.findByName(name);
    if (departments != null && departments.size() > 0) {
      return departments.get(0);
    }
    return null;
  }
  
  public DEPARTMENT findDepartmentByName(String name) {
    if (departmentHash == null) {
      retrieveData();
    }
    for (DEPARTMENT department : departmentHash.values()) {
      if (department.getName().equals(name) && department.getNhCode() != null) {
        return department;
      }
    }
    return null;
  }

  public String deleteDepartment(Long id) {
    departmentHash.remove(id);
    Optional<DEPARTMENT> existDepartment = departmentDao.findById(id);
    if (!existDepartment.isPresent()) {
      return "部門id不存在";
    }
    departmentDao.deleteById(id);
    return null;
  }

  public List<UserRequest> getAllUser(String funcType, String funcTypeC, String rocId, String name,
      String role, String keyword) {
    if (departmentHash == null) {
      retrieveData();
    }
    List<UserRequest> result = new ArrayList<UserRequest>();

    List<USER> list = null;
    if (ROLE_TYPE.APPL.getRole().equals(role)) {
      list = userDao.findApplUser();
    } else if ("U".equals(role)) {
      list = userDao.findAccount();
    } else {
      list = userDao.findDoctor();
    }
    // 與查詢相關的部門id array
    List<Long> depId = getDepartmentIds(funcType, funcTypeC);
    List<USER_DEPARTMENT> udList = userDepartmentDao.findAll();
    
    List<USER_DEPARTMENT> newUserDepartmentList = new ArrayList<USER_DEPARTMENT>();
    for (USER_DEPARTMENT ud : udList) {
      for (Long id : depId) {
        if (ud.getDepartmentId().longValue() == id.longValue()) {
          newUserDepartmentList.add(ud);
        }
      }
    }
    udList = newUserDepartmentList;
    boolean isAllDepartment = "不分科".equals(funcTypeC) || "00".equals(funcType) || (funcTypeC == null && funcType == null);
    HashMap<String, String> departments = null;
    for (USER user : list) {
      departments = getDepartmentIdAndNameByUserId(user.getId(), udList);
      if (departments == null || departments.size() == 0) {
        if (!isAllDepartment) {
          // 該user不在指定的部門別
          continue;
        }
      }
      if (rocId != null && rocId.length() > 0) {
        if ((user.getInhId() == null || user.getInhId().toUpperCase().indexOf(rocId.toUpperCase()) < 0) && 
         (user.getRocId() == null || user.getRocId().indexOf(rocId.toUpperCase()) < 0)) {
          continue;
        }
      }
      if (name != null && name.length () > 0) {
        if (user.getDisplayName() == null
            || user.getDisplayName().indexOf(name.toUpperCase()) < 0) {
          continue;
        }
      }
      if (keyword != null && keyword.length() > 0) {
        if ((user.getInhId() == null || user.getInhId().toUpperCase().indexOf(keyword.toUpperCase()) < 0)
            && (user.getDisplayName() == null
                || user.getDisplayName().toUpperCase().indexOf(keyword.toUpperCase()) < 0)) {
          continue;
        }
      }

      UserRequest ur = new UserRequest(user);
      ur.setPassword(null);
      if (departments != null) {
        ur.setDepartments(collectionToString(departments.values(), ","));
        ur.setDepartmentId(collectionToString(departments.keySet(), ","));
      }
      result.add(ur);
    }
    return result;
  }

  public UserRequest getUserById(Long id) {
    if (departmentHash == null) {
      retrieveData();
    }

    Optional<USER> optional = userDao.findById(id);
    if (!optional.isPresent()) {
      UserRequest result = new UserRequest();
      result.setDisplayName("user ID:" + id + "不存在");
      return result;
    }
    USER user = optional.get();
    UserRequest result = new UserRequest(user);
    List<USER_DEPARTMENT> udList = userDepartmentDao.findByUserIdOrderByDepartmentId(id);
    String departments = getDepartmentsByUserId(id, udList);
    if (departments != null && departments.length() > 0) {
      result.setDepartments(departments);
    }
    result.setRocId(user.getRocId());
    result.setPassword(null);
    result.setCreateAt(null);
    return result;
  }

  private Long getDepartmentIdByName(String name) {
    for (DEPARTMENT dep : departmentHash.values()) {
      if ((dep.getName() != null && dep.getName().equals(name))
          || (dep.getNhName() != null && dep.getNhName().equals(name))) {
        return dep.getId();
      }
    }
    return -1L;
  }

  private Long getDepartmentIdByCode(String code) {
    for (DEPARTMENT dep : departmentHash.values()) {
      if ((dep.getNhCode() != null && dep.getNhCode().equals(code)) || dep.getCode().equals(code)) {
        return dep.getId();
      }
    }
    return -1L;
  }

  public List<DEPARTMENT> getAllDepartment(String code, String name, long loginTime) {
    if (departmentHash == null) {
      retrieveData();
    }
//    System.out.println(System.currentTimeMillis());
//    System.out.println(loginTime);
    List<DEPARTMENT> result = new ArrayList<DEPARTMENT>();
    List<DEPARTMENT> all = new ArrayList<DEPARTMENT>(departmentHash.values());
    if ((code == null || code.length() == 0) && (name == null || name.length() == 0)) {
      result = getAllFuncTypeInMR();
      if (result == null || result.size() ==0 || ((System.currentTimeMillis() - loginTime) < 2000)) {
        return result;
      }
      return all;
    }
    if (code != null && code.length() > 0) {
      for (DEPARTMENT department : all) {
        if (department.getCode().startsWith(code)) {
          result.add(department);
        }
      }
    }
    if (name != null && name.length() > 0) {
      for (DEPARTMENT department : all) {
        if (department.getName().startsWith(name)) {
          result.add(department);
        }
      }
    }
    return result;
  }
  
  /**
   * 取得病歷有的科別
   * @return
   */
  private List<DEPARTMENT> getAllFuncTypeInMR(){
    List<DEPARTMENT> result = new ArrayList<DEPARTMENT>();
    List<String> funcTypes = reportService.findAllFuncTypes(true);
    for (String string : funcTypes) {
      DEPARTMENT department = new DEPARTMENT();
      department.setCode(string);
      department.setName(codeTableService.getDesc("FUNC_TYPE", string));
      result.add(department);
    }
    return result;
  }

  private String getDepartmentsByUserId(Long id, List<USER_DEPARTMENT> udList) {
    StringBuffer sb = new StringBuffer();
    if (udList == null || udList.size() == 0) {
      return "";
    }
    for (USER_DEPARTMENT ud : udList) {
      if (ud.getUserId().longValue() == id) {
        DEPARTMENT dep = departmentHash.get(ud.getDepartmentId());
        if (dep == null) {
          continue;
        }
        sb.append(dep.getName());
        sb.append(",");
      }
    }
    if (sb.length() > 1) {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }

  private HashMap<String, String> getDepartmentIdAndNameByUserId(Long id,
      List<USER_DEPARTMENT> udList) {
    if (udList == null || udList.size() == 0) {
      return null;
    }
    HashMap<String, String> result = new HashMap<String, String>();
    for (USER_DEPARTMENT ud : udList) {
      if (ud.getUserId().longValue() == id) {
        DEPARTMENT dep = departmentHash.get(ud.getDepartmentId());
        if (dep == null) {
          continue;
        }
        result.put(dep.getCode(), dep.getName());
      }
    }
    return result;
  }

  private String collectionToString(Collection<String> set, String delimiter) {
    StringBuffer sb = new StringBuffer();
    for (String string : set) {
      sb.append(string);
      sb.append(delimiter);
    }
    if (sb.length() == 0) {
      return null;
    }
    if (sb.charAt(sb.length() - 1) == delimiter.charAt(delimiter.length() - 1)) {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }

  public String forgetPassword(String username, String email) {
    USER existUser = findUser(username);
    if (existUser == null) {
      return "帳號不存在";
    }
    if (existUser.getStatus() == 0) {
      return "帳號停用中，無法申請新密碼";
    }

    String newPassword = generateCommonLangPassword();
    emailService.sendMail("忘記密碼-重設新密碼", existUser.getEmail(), "系統隨機產生密碼:" + newPassword);
    existUser.setPassword(encoder.encode(newPassword));
    existUser.setUpdateAt(new Date());
    existUser.setStatus(1);
    userDao.save(existUser);
    
    httpServletReq.setAttribute(LogType.FORGOT_PASSWORD.name()+"_ID", existUser.getId());
    
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

  public void loginLog(String username, String jwt) {
    String key = USER_PREFIX + username;
    Set<Object> sets = redisService.hkeys(key);
    if (sets != null && sets.size() > 0) {
      deleteAndSaveUserLogout(key, sets);
    }
    redisService.putHash(key, jwt, String.valueOf(System.currentTimeMillis()));
    removeMrEditKey(username);
  }

  /**
   * 同一帳號，重複登入，需把之前尚在編輯病歷的記錄移除，以免無法再次編輯
   * 
   * @param username
   */
  public void removeMrEditKey(String username) {
    Set<String> mrEditKeys = redisService.keys(UserService.MREDIT + "*");
    for (String key : mrEditKeys) {
      Set<Object> names = redisService.hkeys(key);
      for (Object obj : names) {
        String jwt = (String) obj;
        String jwtUsername = jwtUtils.getUsernameFromToken(jwt);
        if (jwtUsername == null || jwtUsername.equals(username)) {
          redisService.deleteHash(key, jwt);
        }
      }
    }
  }

  public void deleteAndSaveUserLogout(String key, Set<Object> names) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    for (Object object : names) {
      String oldJWT = (String) object;
      Object lastUsedTime = redisService.hget(key, oldJWT);
      redisService.deleteHash(key, oldJWT);
      long lastUsedTimeL = Long.parseLong((String) lastUsedTime);
      if ((lastUsedTimeL + 60000) < System.currentTimeMillis()) {
        // 點完頁面後加1分鐘當做觀看時間
        lastUsedTimeL += 60000;
      }
      logDataDao.updateSignout(oldJWT, sdf.format(new Date(lastUsedTimeL)));
    }
  }

  public void logoutLog(String username, String jwt) {
    // 儲存 DB logout 時間由 logService.setLogout 處理
    String key = USER_PREFIX + username;
    Object loginTime = redisService.hget(key, jwt);
    if (loginTime != null) {
      redisService.deleteHash(key, jwt);
    }
  }

  public boolean updateUserAlive(String username, String jwt, boolean isEditing) {
    String key = USER_PREFIX + username;
    Set<Object> sets = redisService.hkeys(key);
    if (sets == null || sets.size() == 0) {
      // @TODO 暫時先放行
      return true;
      // return false;
    } else {
      Object usedTime = redisService.hget(key, jwt);
      if (usedTime == null) {
        return false;
      }
      // 存放當下的時間
      redisService.putHash(key, jwt, String.valueOf(System.currentTimeMillis()));
      if (!isEditing) {
        Object mrId = redisService.hget(key, EDITING);
        if (mrId != null) {
          redisService.deleteHash(key, EDITING);
          redisService.deleteHash(MREDIT + mrId);
        }
      }
    }
    return true;
  }

  /**
   * 檢查已登入 user 的token，是否超過30分鐘未使用，是則刪掉在 redis的 JWT並儲存 logout時間
   */
  public void checkLoginUser() {
    Set<String> users = redisService.keys("USER*");
    for (String key : users) {
      List<Object> lastUsedTime = redisService.values(key);
      for (Object time : lastUsedTime) {
        long lastUsedTimeL = Long.parseLong((String) time);
        if (System.currentTimeMillis() - lastUsedTimeL > afkTime) {
          deleteAndSaveUserLogout(key, redisService.hkeys(key));
        }
      }
    }

    Set<String> mr = redisService.keys("MREDIT*");
    for (String key : mr) {
      Set<Object> names = redisService.hkeys(key);
      for (Object name : names) {
        String value = (String) redisService.hget(key, (String) name);
        if (value.indexOf(',') > 0) {
          value = value.split(",")[0];
        }
        long lastUsedTimeL = Long.parseLong(value);
        if (System.currentTimeMillis() - lastUsedTimeL > afkTime) {
          redisService.deleteHash(key, (String) name);
        }
      }
    }
  }

  public long getUserIdByName(String name) {
    
    USER user = findUser(name);
    if (user != null) {
      return user.getId();
    }
    return -1;
  }

  public boolean needChangePassword(long userId) {
    USER user = userDao.findById(userId).orElse(null);
    if (user != null) {
      if (user.getStatus() == null) {
        return false;
      }
      return user.getStatus() == USER.STATUS_CHANGE_PASSWORD;
    }
    return false;
  }

  private List<Long> getDepartmentIds(String funcType, String funcTypeC) {
    if ("不分科".equals(funcTypeC) || "00".equals(funcType) || "0".equals(funcType)
        || (funcType == null && funcTypeC == null)) {
      return new ArrayList<Long>(departmentHash.keySet());
    }
    List<Long> result = new ArrayList<Long>();
    if (funcType != null) {
      String[] s = funcType.split(" ");
      for (String string : s) {
        Long id = getDepartmentIdByCode(string);
        if (id != null) {
          result.add(id);
        }
      }
    } else if (funcTypeC != null) {
      String[] s = funcTypeC.split(" ");
      for (String string : s) {
        Long id = getDepartmentIdByName(string);
        if (id != null) {
          result.add(id);
        }
      }
    }
    return result;
  }
  
  public long getUserCount() {
    return userDao.count();
  }
  
  public String encrypt(String plainText) {
    String encryptedValue = null;

    try {
      Cipher chiper = Cipher.getInstance(ALGORITHM);
      chiper.init(Cipher.ENCRYPT_MODE, KEY);
      byte[] encVal = chiper.doFinal(plainText.getBytes());
      encryptedValue = new String(Base64.encodeBase64(encVal));
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    return encryptedValue;
  }

  public String decrypt(String encryptedText) {
    String decryptedValue = null;
    try {
      Cipher chiper = Cipher.getInstance(ALGORITHM);
      chiper.init(Cipher.DECRYPT_MODE, KEY);
      byte[] decordedValue = Base64.decodeBase64(encryptedText.getBytes());
      byte[] decValue = chiper.doFinal(decordedValue);
      decryptedValue = new String(decValue);

    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return decryptedValue;
  }

  /**
   * 檢查使用期限是否到期
   * @param encoded
   * @return null:未過期， != null 到期，回傳起訖日
   */
  public String checkExpire(String encoded) {
    if (encoded == null) {
      return "";
    }
    String decoded = decrypt(encoded);
    if (decoded == null) {
      return "";
    }
    USER leadtek =  findUser("leadtek");
    if (leadtek != null) {
      if (System.currentTimeMillis() > leadtek.getCreateAt().getTime() + Long.parseLong(decoded) * 86400000) {
        // 到期
        SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
        StringBuffer sb = new StringBuffer();
        sb.append(sdf.format(leadtek.getCreateAt()));
        sb.append(" - ");
        Date lastDate = new Date(leadtek.getCreateAt().getTime() + Long.parseLong(decoded) * 86400000);
        sb.append(sdf.format(lastDate));
        return sb.toString();
      }
    }
    return null;
  }
  
  public void initialLeadtek() {
    UserRequest user = new UserRequest();
    user.setDepartments("ADM");
    user.setPassword(encoder.encode("test"));
    user.setRocId("leadtek");
    user.setDisplayName("leadtek");
    user.setUsername("leadtek");
    user.setRole("A");
    user.setStatus(USER.STATUS_ACTIVE);
    newUser(user);
  }
}
