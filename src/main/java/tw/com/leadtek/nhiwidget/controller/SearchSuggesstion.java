/**
 * Created on 2021/1/15.
 */
package tw.com.leadtek.nhiwidget.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import tw.com.leadtek.nhiwidget.model.JsonSuggestion;
import tw.com.leadtek.nhiwidget.service.RedisService;

@Api(tags = "搜尋建議 API", value = "搜尋建議 API")
@RestController
// @CrossOrigin(value = "http://localhost:8080")
@RequestMapping(produces = "application/json; charset=utf-8") // reponse 有中文才不會亂碼
public class SearchSuggesstion extends BaseController {

  @Autowired
  private RedisService rs;

  @ApiOperation(value = "取得搜尋建議", notes = "取得搜尋建議")
  @GetMapping("/nhixml/suggestions")
  public ResponseEntity<?> suggestions(
      @ApiParam(name = "term", value = "搜尋關鍵字，若有多個用空格區隔，如\"diabetes mellitus without\"", example = "l97.") @RequestParam(required = true) String term,
      @ApiParam(name = "類別", value = "有 ICD10-PCS(處置碼), ICD10-CM(診斷碼)", example = "ICD10-CM") @RequestParam(required = false) String cat) {
    //logger.info("search:" + term + ", cat=" + cat);
    List<JsonSuggestion> result = rs.query(cat, term.toLowerCase());
    return ResponseEntity.ok(result);
    // if (cfService.doesCfNameExist(request.getName())) {
    // return returnAPIResult("表單 " + request.getName() + " 已存在");
    // }
    //
    // String errorMessage = cfService.addCF(request);
    // if (errorMessage != null && errorMessage.length() > 0) {
    // logger.info("/cf:" + errorMessage);
    // }
    // return returnAPIResult(errorMessage);
  }

}
