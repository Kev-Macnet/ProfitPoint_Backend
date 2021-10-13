/**
 * Created on 2021/1/15.
 */
package tw.com.leadtek.nhiwidget.controller;

import javax.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.ResponseId;
import tw.com.leadtek.nhiwidget.service.ParametersService;

public class BaseController {

  @Autowired
  protected ParametersService parametersService;

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
}
