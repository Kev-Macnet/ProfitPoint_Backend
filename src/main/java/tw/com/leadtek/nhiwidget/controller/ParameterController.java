/**
 * Created on 2021/5/19.
 */
package tw.com.leadtek.nhiwidget.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.PointsValue;
import tw.com.leadtek.nhiwidget.service.ParametersService;

@Api(tags = "參數設定相關API", value = "參數設定相關API")
@CrossOrigin(origins = "*", maxAge = 36000)
@RestController
// @CrossOrigin(value = "http://localhost:8080")
@RequestMapping(value = "/p", produces = "application/json; charset=utf-8")
public class ParameterController extends BaseController {

  @Autowired
  private ParametersService parameterService;

  @ApiOperation(value = "取得是否使用西醫、牙醫總額度支配點數設定", notes = "取得是否使用西醫、牙醫總額度支配點數設定")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/pointsStatus")
  public ResponseEntity<Map<String, Object>> getPointsSetting(
      @ApiParam(name = "perPage", value = "每頁顯示筆數",
          example = "20") @RequestParam(required = false) Integer perPage,
      @ApiParam(name = "page", value = "第幾頁，第一頁值為0", example = "0") @RequestParam(required = false,
          defaultValue = "0") Integer page) {
    int perPageInt =
        (perPage == null) ? parameterService.getIntParameter(ParametersService.PAGE_COUNT)
            : perPage.intValue();
    return ResponseEntity.ok(parameterService.getPointsStatus(perPageInt, page.intValue()));
  }

  @ApiOperation(value = "修改支配總點數設定", notes = "修改支配總點數設定")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PutMapping("/pointsStatus")
  public ResponseEntity<BaseResponse> updatePointsSetting(
      @ApiParam(name = "startDate", value = "生效日",
      example = "2021/05/01") @RequestParam(required = true) String startDate,
      @ApiParam(name = "wmStatus", value = "西醫是否計算總點數，true/false",
      example = "true") @RequestParam(required = true) Boolean wmStatus,
      @ApiParam(name = "dentistStatus", value = "牙醫是否計算總點數，true/false",
      example = "false") @RequestParam(required = true) Boolean dentistStatus
      ) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    try {
      Date sDate = sdf.parse(startDate);
      return returnAPIResult(parameterService.updatePointsStatus(sDate, wmStatus, dentistStatus));
    } catch (ParseException e) {
      BaseResponse br = new BaseResponse();
      br.setMessage(e.getLocalizedMessage());
      return ResponseEntity.badRequest().body(br);
    }
    
  }

  @ApiOperation(value = "取得支配總點數", notes = "取得支配總點數")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/pointsValue")
  public ResponseEntity<PointsValue> getPointsValue(@ApiParam(name = "startDate", value = "生效日",
      example = "2021/05/01") @RequestParam(required = true) String startDate) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    PointsValue result = null;
    try {
      Date sDate = sdf.parse(startDate);
      result = parameterService.getPointsValue(sDate);
      if (result == null) {
        return ResponseEntity.badRequest().body(null);
      }
      return ResponseEntity.ok(result);
    } catch (ParseException e) {
      return ResponseEntity.badRequest().body(result);
    }
  }

  @ApiOperation(value = "修改支配總點數", notes = "修改支配總點數")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PutMapping("/pointsValue")
  public ResponseEntity<BaseResponse> updatePointsValue(@RequestBody PointsValue pv) {
    return returnAPIResult(parameterService.updatePointsValue(pv));
  }
  
  @ApiOperation(value = "新增支配總點數", notes = "修改支配總點數")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PostMapping("/pointsValue")
  public ResponseEntity<BaseResponse> newPointsValue(@RequestBody PointsValue pv) {
    return returnAPIResult(parameterService.newPointsValue(pv));
  }

  @ApiOperation(value = "取得參數值", notes = "取得參數值")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/value")
  public ResponseEntity<Map<String, Object>> getValue(
      @ApiParam(name = "name", value = "參數值名稱，如抽件數(SAMPLING),標準給付額(SPR)", example = "SAMPLING") @RequestParam(required = true) String name,
      @ApiParam(name = "perPage", value = "每頁顯示筆數",
          example = "20") @RequestParam(required = false) Integer perPage,
      @ApiParam(name = "page", value = "第幾頁，第一頁值為0", example = "0") @RequestParam(required = false,
          defaultValue = "0") Integer page) {
    int perPageInt =
        (perPage == null) ? parameterService.getIntParameter(ParametersService.PAGE_COUNT)
            : perPage.intValue();
    return ResponseEntity.ok(parameterService.getParameterValue(name, perPageInt, page.intValue()));
  }
  
  @ApiOperation(value = "新增支配總點數", notes = "修改支配總點數")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "資料不存在")})
  @PostMapping("/value")
  public ResponseEntity<BaseResponse> newValue(
      @ApiParam(name = "name", value = "參數值名稱，如抽件數(SAMPLING),標準給付額(SPR)", example = "SAMPLING") @RequestParam(required = true) String name,
      @ApiParam(name = "value", value = "參數值", example = "1234") @RequestParam(required = true) String value,
      @ApiParam(name = "startDate", value = "生效日",
      example = "2021/05/01") @RequestParam(required = true) Date startDate,
      @ApiParam(name = "endDate", value = "生效日",
      example = "2021/12/31") @RequestParam(required = true) Date endDate
      ) {
    return returnAPIResult(parameterService.newValue(name, value, startDate, endDate));
  }
}
