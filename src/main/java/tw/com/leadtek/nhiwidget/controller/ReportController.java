/**
 * Created on 2021/11/4.
 */
package tw.com.leadtek.nhiwidget.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.report.DRGMonthlyPayload;
import tw.com.leadtek.nhiwidget.payload.report.DRGMonthlySectionPayload;
import tw.com.leadtek.nhiwidget.payload.report.PeriodPointPayload;
import tw.com.leadtek.nhiwidget.payload.report.PeriodPointWeeklyPayload;
import tw.com.leadtek.nhiwidget.payload.report.PointMRPayload;
import tw.com.leadtek.nhiwidget.service.ReportService;

@Api(tags = "快速報告相關API", value = "快速報告相關API")
@CrossOrigin(origins = "*", maxAge = 36000)
@RestController
// @CrossOrigin(value = "http://localhost:8080")
@RequestMapping(value = "/report", produces = "application/json; charset=utf-8") 
public class ReportController extends BaseController {

  @Autowired
  private ReportService reportService;
  
  @ApiOperation(value = "取得健保點數月報表", notes = "取得健保點數月報表")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/monthlyPoint")
  public ResponseEntity<PointMRPayload> getMonthlyPoint(@ApiParam(name = "year", value = "西元年",
      example = "2021") @RequestParam(required = true) Integer year,
      @ApiParam(name = "month", value = "月份",
      example = "3") @RequestParam(required = true) Integer month) {
    if (year < 2015 || year > 2030) {
      PointMRPayload result = new PointMRPayload();
      result.setResult(BaseResponse.ERROR);
      result.setMessage("年份超過範圍");
      return ResponseEntity.badRequest().body(result);
    }
    if (month < 1 || month > 12) {
      PointMRPayload result = new PointMRPayload();
      result.setResult(BaseResponse.ERROR);
      result.setMessage("月份超過範圍");
      return ResponseEntity.badRequest().body(result);
    }
    return ResponseEntity.ok(reportService.getMonthlyReport(year, month));
  }
  
  @ApiOperation(value = "取得費用業務-點數", notes = "取得費用業務-點數")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/periodPoint")
  public ResponseEntity<PeriodPointPayload> getPeriodPoint(
      @ApiParam(name = "sdate", value = "起始日期", example = "2021/01/01") 
      @RequestParam(required = false) String sdate,
      @ApiParam(name = "edate", value = "結束日期", example = "2021/01/11") 
      @RequestParam(required = false) String edate ){
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    Date startDate = null;
    Date endDate = null;
    try {
      startDate = sdf.parse(sdate);
      endDate = sdf.parse(edate);
    } catch (ParseException e) {
      PeriodPointPayload result = new PeriodPointPayload();
      result.setResult(BaseResponse.ERROR);
      result.setMessage("日期格式不正確");
      return ResponseEntity.badRequest().body(result);
    }
    return ResponseEntity.ok(reportService.getPeriodPoint(startDate, endDate));
  }
  
  @ApiOperation(value = "取得費用業務-每周趨勢資料", notes = "取得費用業務-每周趨勢資料")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/periodPointWeekly")
  public ResponseEntity<PeriodPointWeeklyPayload> getPeriodPointWeekly(
      @ApiParam(name = "edate", value = "結束日期", example = "2021/01/11") 
      @RequestParam(required = false) String edate){
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    Date endDate = null;
    try {
      endDate = sdf.parse(edate);
    } catch (ParseException e) {
      PeriodPointWeeklyPayload result = new PeriodPointWeeklyPayload();
      result.setResult(BaseResponse.ERROR);
      result.setMessage("日期格式不正確");
      return ResponseEntity.badRequest().body(result);
    }
    return ResponseEntity.ok(reportService.getPeroidPointWeekly(endDate));
  }
  
  @ApiOperation(value = "取得DRG分配比例月報表", notes = "取得DRG分配比例月報表")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/drgMonthly")
  public ResponseEntity<DRGMonthlyPayload> getDrgMonthly(@ApiParam(name = "year", value = "西元年",
      example = "2021") @RequestParam(required = true) Integer year,
      @ApiParam(name = "month", value = "月份",
      example = "3") @RequestParam(required = true) Integer month) {
    if (year < 2015 || year > 2030) {
      DRGMonthlyPayload result = new DRGMonthlyPayload();
      result.setResult(BaseResponse.ERROR);
      result.setMessage("年份超過範圍");
      return ResponseEntity.badRequest().body(result);
    }
    if (month < 1 || month > 12) {
      DRGMonthlyPayload result = new DRGMonthlyPayload();
      result.setResult(BaseResponse.ERROR);
      result.setMessage("月份超過範圍");
      return ResponseEntity.badRequest().body(result);
    }
    DRGMonthlyPayload result = reportService.getDrgMonthly(year, month);
    if (result == null) {
      result = new DRGMonthlyPayload();
      result.setResult(BaseResponse.ERROR);
      result.setMessage("無資料");
      return ResponseEntity.badRequest().body(result);
    }
    return ResponseEntity.ok(result);
  }
  
  @ApiOperation(value = "取得DRG各科分配比例月報表", notes = "取得DRG分配比例月報表")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/drgMonthlyAllFuncType")
  public ResponseEntity<DRGMonthlyPayload> getDrgMonthlyAllFuncType(@ApiParam(name = "year", value = "西元年",
      example = "2021") @RequestParam(required = true) Integer year,
      @ApiParam(name = "month", value = "月份",
      example = "3") @RequestParam(required = true) Integer month) {
    if (year < 2015 || year > 2030) {
      DRGMonthlyPayload result = new DRGMonthlyPayload();
      result.setResult(BaseResponse.ERROR);
      result.setMessage("年份超過範圍");
      return ResponseEntity.badRequest().body(result);
    }
    if (month < 1 || month > 12) {
      DRGMonthlyPayload result = new DRGMonthlyPayload();
      result.setResult(BaseResponse.ERROR);
      result.setMessage("月份超過範圍");
      return ResponseEntity.badRequest().body(result);
    }
    return ResponseEntity.ok(reportService.getDrgMonthlyAllFuncType(year, month));
  }
  
  @ApiOperation(value = "取得DRG各區分配比例月報表", notes = "取得DRG各區分配比例月報表")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/drgMonthlySection")
  public ResponseEntity<DRGMonthlySectionPayload> getDrgMonthlySection(@ApiParam(name = "year", value = "西元年",
      example = "2021") @RequestParam(required = true) Integer year,
      @ApiParam(name = "month", value = "月份",
      example = "3") @RequestParam(required = true) Integer month) {
    if (year < 2015 || year > 2030) {
      DRGMonthlySectionPayload result = new DRGMonthlySectionPayload();
      result.setResult(BaseResponse.ERROR);
      result.setMessage("年份超過範圍");
      return ResponseEntity.badRequest().body(result);
    }
    if (month < 1 || month > 12) {
      DRGMonthlySectionPayload result = new DRGMonthlySectionPayload();
      result.setResult(BaseResponse.ERROR);
      result.setMessage("月份超過範圍");
      return ResponseEntity.badRequest().body(result);
    }
    return ResponseEntity.ok(reportService.getDrgMonthlySection(year, month));
  }
  
}
