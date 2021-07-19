/**
 * Created on 2021/1/15.
 */
package tw.com.leadtek.nhiwidget.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.ResponseId;

public class BaseController {

  protected Logger logger = LogManager.getLogger();
  
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
}
