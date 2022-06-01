/**
 * Created on 2021/11/4.
 */
package tw.com.leadtek.nhiwidget.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import tw.com.leadtek.nhiwidget.payload.report.AchievementQuarter;
import tw.com.leadtek.nhiwidget.payload.report.AchievementWeekly;
import tw.com.leadtek.nhiwidget.payload.report.DRGMonthlyPayload;
import tw.com.leadtek.nhiwidget.payload.report.DRGMonthlySectionPayload;
import tw.com.leadtek.nhiwidget.payload.report.PeriodPointPayload;
import tw.com.leadtek.nhiwidget.payload.report.PeriodPointWeeklyPayload;
import tw.com.leadtek.nhiwidget.payload.report.PointMRPayload;
import tw.com.leadtek.nhiwidget.payload.report.VisitsVarietyPayload;
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
  
  @ApiOperation(value = "取得健保申報總額達成趨勢資料", notes = "取得健保申報總額達成趨勢資料")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/achievementRate")
  public ResponseEntity<AchievementWeekly> getAchievementRate(
      @ApiParam(value = "西元年", example = "2021") @RequestParam(required = true) String year,
      @ApiParam(value = "第幾週(week of year)", example = "16") @RequestParam(required = true) String week){
    
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, Integer.parseInt(year));
    cal.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(week));
    // 指定週的最後一天
    cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

    return ResponseEntity.ok(reportService.getAchievementWeekly(cal));
  }
  
  @ApiOperation(value = "取得健保總額累積達成率", notes = "取得健保申報總額達成趨勢資料")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/achievementRateQuarter")
  public ResponseEntity<AchievementQuarter> getAchievementRateQuarter(
      @ApiParam(value = "西元年，若為多筆資料，用空格隔開", example = "2021 2021 2021") @RequestParam(required = true) String year,
      @ApiParam(value = "季度，若為多筆資料，用空格隔開", example = "Q1 Q2 Q3") @RequestParam(required = true) String quarter){
    
    return ResponseEntity.ok(reportService.getAchievementQuarter(year, quarter));
  }
  
  @ApiOperation(value = "取得門急診/住院/出院人次變化", notes = "取得門急診/住院/出院人次變化")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
  @GetMapping("/visitsVariety")
  public ResponseEntity<VisitsVarietyPayload> getVisitsVariety(
      @ApiParam(name = "sdate", value = "開始日期", example = "2021/01/01") 
      @RequestParam(required = false) String sdate, 
      @ApiParam(name = "edate", value = "結束日期", example = "2021/01/11") 
      @RequestParam(required = false) String edate,
      @ApiParam(value = "西元年", example = "2021") @RequestParam(required = false) String year,
      @ApiParam(value = "第幾週(week of year)", example = "16") @RequestParam(required = false) String week){
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    java.sql.Date startDate = null;
    java.sql.Date endDate = null;
    try {
      startDate = new java.sql.Date(sdf.parse(sdate).getTime());
      endDate = new java.sql.Date(sdf.parse(edate).getTime());
    } catch (ParseException e) {
      VisitsVarietyPayload result = new VisitsVarietyPayload();
      result.setResult(BaseResponse.ERROR);
      result.setMessage("日期格式不正確");
      return ResponseEntity.badRequest().body(result);
    }
    return ResponseEntity.ok(reportService.getVisitsVariety(startDate, endDate, year, week));
  }

}
