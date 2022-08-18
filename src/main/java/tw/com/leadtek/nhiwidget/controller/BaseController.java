/**
 * Created on 2021/1/15.
 */
package tw.com.leadtek.nhiwidget.controller;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.ResponseId;
import tw.com.leadtek.nhiwidget.security.service.UserDetailsImpl;
import tw.com.leadtek.nhiwidget.service.ParametersService;

public class BaseController {

  @Autowired
  protected ParametersService parametersService;
  
  @Autowired
  protected HttpServletRequest httpServletReq;

  protected Logger logger = LogManager.getLogger();

  /**
   * 每頁顯示筆數
   */
  public static int DEFAULT_PAGE_COUNT = 20; 
  
  @PostConstruct
  public void init() {
    DEFAULT_PAGE_COUNT = parametersService.getIntParameter(ParametersService.PAGE_COUNT);
  }

  public ResponseEntity<BaseResponse> returnAPIResult(String result) {
    if (result == null) {
      return ResponseEntity.ok(new BaseResponse("success", null));
    }
    return ResponseEntity.badRequest().body(new BaseResponse("error", result));
  }

  public ResponseEntity<BaseResponse> returnIDResult(String id) {
    if (id == null) {
      return ResponseEntity.badRequest().body(new BaseResponse("error", null));
    }
    return ResponseEntity.ok(new ResponseId("success", null, id));
  }

  public static int countTotalPage(int count, int perPage) {
    int result = count / perPage;
    if (count % perPage > 0) {
      result++;
    }
    return result;
  }
  
  public UserDetailsImpl getUserDetails() {
    UserDetailsImpl result = null;
    try {
      Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      if (obj instanceof UserDetailsImpl) {
        result = (UserDetailsImpl) obj; 
      } else {
        return null;
      }
    } catch (Exception e) {
      logger.error("not valid user", e);
    }
    return result;
  }

  public Object checkUser(String menu, boolean isEditable) {
    UserDetailsImpl userDetails = getUserDetails();
    if (userDetails == null) {
      return new ResponseEntity<String>("未登入，無法取得資料", HttpStatus.UNAUTHORIZED);
    }
//    if (!checkUserAuthority(userDetails, menu, isEditable)) {
//      return new ResponseEntity<String>("無此權限", HttpStatus.FORBIDDEN);
//    }
    return userDetails;
  }
  
  /**
   * 比對orderBy欄位是否存在用
   * @param className
   * @param field
   * @return
   */
  public static boolean findDeclaredMethod(String className, String field) {
    try {
      Class<?> c = Class.forName(className);
      String functionName = field.substring(0, 1).toUpperCase() + field.substring(1);
      c.getDeclaredMethod("get" + functionName, (Class<?>[])new Class[]{});
      return true;
    } catch (ClassNotFoundException e) {
      //e.printStackTrace();
    } catch (NoSuchMethodException e) {
      //e.printStackTrace();
    } catch (SecurityException e) {
      //e.printStackTrace();
    }
    return false;
  }
  
  
//  public boolean checkUserAuthority(UserDetailsImpl userDetails, String menuName, boolean isEditable) {
//    List<LeftMenu> menus = userService.getUserMenus(userDetails.getId());
//    for (LeftMenu leftMenu : menus) {
//      if (leftMenu.getName().equals(menuName)) {
//        if (!leftMenu.getShowMenu()) {
//          return false;
//        }
//        if (!leftMenu.getViewable()) {
//          return false;
//        }
//        if (!leftMenu.getEditable() && isEditable) {
//          return false;
//        }
//        break;
//      }
//    }
//    return true;
//  }

}
