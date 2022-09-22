/**
 * Created on 2021/11/4.
 */
package tw.com.leadtek.nhiwidget.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

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
import tw.com.leadtek.nhiwidget.annotation.LogDefender;
import tw.com.leadtek.nhiwidget.constant.LogType;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.report.AchievementQuarter;
import tw.com.leadtek.nhiwidget.payload.report.AchievementWeekly;
import tw.com.leadtek.nhiwidget.payload.report.DRGMonthlyPayload;
import tw.com.leadtek.nhiwidget.payload.report.DRGMonthlySectionPayload;
import tw.com.leadtek.nhiwidget.payload.report.DeductedPayloadResponse;
import tw.com.leadtek.nhiwidget.payload.report.HealthCareCost;
import tw.com.leadtek.nhiwidget.payload.report.PeriodPointPayload;
import tw.com.leadtek.nhiwidget.payload.report.PeriodPointWeeklyPayload;
import tw.com.leadtek.nhiwidget.payload.report.PointMRPayload;
import tw.com.leadtek.nhiwidget.payload.report.VisitsVarietyPayload;
import tw.com.leadtek.nhiwidget.service.HealthCareCostService;
import tw.com.leadtek.nhiwidget.service.ReportExportService;
import tw.com.leadtek.nhiwidget.service.ReportService;

@Api(tags = "快速報告相關API", value = "快速報告相關API")
@CrossOrigin(origins = "*", maxAge = 36000)
@RestController
// @CrossOrigin(value = "http://localhost:8080")
@RequestMapping(value = "/report", produces = "application/json; charset=utf-8")
public class ReportController extends BaseController {

	@Autowired
	private ReportService reportService;

	@Autowired
	private HealthCareCostService healthCareCostService;

	@Autowired
	private ReportExportService reportExportService;

	@ApiOperation(value = "取得健保點數月報表", notes = "取得健保點數月報表")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/monthlyPoint")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<PointMRPayload> getMonthlyPoint(
			@ApiParam(name = "year", value = "西元年", example = "2021") @RequestParam(required = true) Integer year,
			@ApiParam(name = "month", value = "月份", example = "3") @RequestParam(required = true) Integer month) {
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
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/periodPoint")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<PeriodPointPayload> getPeriodPoint(
			@ApiParam(name = "sdate", value = "起始日期", example = "2021/01/01") @RequestParam(required = false) String sdate,
			@ApiParam(name = "edate", value = "結束日期", example = "2021/01/11") @RequestParam(required = false) String edate) {
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

	@ApiOperation(value = "取得費用業務依照科別-點數", notes = "取得費用業務依照科別-點數")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/periodPointByFunctype")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<PeriodPointPayload> getPeriodPointByFunctype(
			@ApiParam(name = "sdate", value = "起始日期", example = "2021/01/01") @RequestParam(required = false) String sdate,
			@ApiParam(name = "edate", value = "結束日期", example = "2021/01/11") @RequestParam(required = false) String edate,
			@ApiParam(name = "funcType", value = "科別", example = "01") @RequestParam(required = false) String funcType) {
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
		if (funcType == null) {
			PeriodPointPayload result = new PeriodPointPayload();
			result.setResult(BaseResponse.ERROR);
			result.setMessage("科別為必填");
		}
		return ResponseEntity.ok(reportService.getPeriodPointByFunctype(startDate, endDate, funcType));
	}

	@ApiOperation(value = "取得費用業務依照科別-每周趨勢資料", notes = "取得費用業務依照科別-每周趨勢資料")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/periodPointWeeklyByFunctype")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<PeriodPointWeeklyPayload> getPeriodPointWeekly(
			@ApiParam(name = "edate", value = "結束日期", example = "2021/01/11") @RequestParam(required = false) String edate,
			@ApiParam(name = "funcType", value = "科別", example = "01") @RequestParam(required = false) String funcType) {

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
		if (funcType == null) {
			PeriodPointPayload result = new PeriodPointPayload();
			result.setResult(BaseResponse.ERROR);
			result.setMessage("科別為必填");
		}
		return ResponseEntity.ok(reportService.getPeroidPointWeeklyByFunctype(endDate, funcType));
	}

	@ApiOperation(value = "取得費用業務-每周趨勢資料", notes = "取得費用業務-每周趨勢資料")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/periodPointWeekly")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<PeriodPointWeeklyPayload> getPeriodPointWeeklyByFunctype(
			@ApiParam(name = "edate", value = "結束日期", example = "2021/01/11") @RequestParam(required = false) String edate) {

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
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/drgMonthly")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<DRGMonthlyPayload> getDrgMonthly(
			@ApiParam(name = "year", value = "西元年", example = "2021") @RequestParam(required = true) Integer year,
			@ApiParam(name = "month", value = "月份", example = "3") @RequestParam(required = true) Integer month) {
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
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/drgMonthlyAllFuncType")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<DRGMonthlyPayload> getDrgMonthlyAllFuncType(
			@ApiParam(name = "year", value = "西元年", example = "2021") @RequestParam(required = true) Integer year,
			@ApiParam(name = "month", value = "月份", example = "3") @RequestParam(required = true) Integer month) {
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
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/drgMonthlySection")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<DRGMonthlySectionPayload> getDrgMonthlySection(
			@ApiParam(name = "year", value = "西元年", example = "2021") @RequestParam(required = true) Integer year,
			@ApiParam(name = "month", value = "月份", example = "3") @RequestParam(required = true) Integer month) {
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
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/achievementRate")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<AchievementWeekly> getAchievementRate(
			@ApiParam(value = "西元年", example = "2021") @RequestParam(required = true) String year,
			@ApiParam(value = "第幾週(week of year)", example = "16") @RequestParam(required = true) String week) {

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
  @LogDefender(value = {LogType.SIGNIN})
  public ResponseEntity<AchievementQuarter> getAchievementRateQuarter(
      @ApiParam(value = "西元年，若為多筆資料，用空格隔開", example = "2021 2021 2021")
          @RequestParam(required = true)
          String year,
      @ApiParam(value = "季度，若為多筆資料，用空格隔開", example = "Q1 Q2 Q3") @RequestParam(required = true)
          String quarter) {

    if (quarter != null && quarter.length() != 2) {
      AchievementQuarter result = new AchievementQuarter();
      result.setMessage("季度不得為空");
      result.setResult(BaseResponse.ERROR);
      return ResponseEntity.badRequest().body(result);
    }
    return ResponseEntity.ok(reportService.getAchievementQuarter(year, quarter));
  }

	@ApiOperation(value = "取得門急診/住院/出院人次變化", notes = "取得門急診/住院/出院人次變化")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/visitsVariety")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<VisitsVarietyPayload> getVisitsVariety(
			@ApiParam(name = "sdate", value = "開始日期", example = "2021/01/01") @RequestParam(required = false) String sdate,
			@ApiParam(name = "edate", value = "結束日期", example = "2021/01/11") @RequestParam(required = false) String edate,
			@ApiParam(value = "西元年", example = "2021") @RequestParam(required = false) String year,
			@ApiParam(value = "第幾週(week of year)", example = "16") @RequestParam(required = false) String week) {

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

	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@ApiOperation(value = "取得門急診/住院/出院人次變化-匯出", notes = "取得門急診/住院/出院人次變化-匯出")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/visitsVarietyExport")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<VisitsVarietyPayload> getVisitsVarietyExport(
			@ApiParam(name = "sdate", value = "開始日期", example = "2021/01/01") @RequestParam(required = false) String sdate,
			@ApiParam(name = "edate", value = "結束日期", example = "2021/01/11") @RequestParam(required = false) String edate,
			@ApiParam(value = "西元年", example = "2021") @RequestParam(required = false) String year,
			@ApiParam(value = "第幾週(week of year)", example = "16") @RequestParam(required = false) String week,
			HttpServletResponse response) {

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
//			e.printStackTrace();
			return ResponseEntity.badRequest().body(result);
		}

		reportExportService.getVisitsVarietyExport(reportService.getVisitsVariety(startDate, endDate, year, week),
				response, year, week, sdate, edate);

		return null;
	}

	@ApiOperation(value = "取得單月各科健保申報量與人次報表", notes = "取得單月各科健保申報量與人次報表")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/monthlyPointApplCount")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<PointMRPayload> getMonthlyPointApplCount(
			@ApiParam(name = "year", value = "西元年", example = "2019") @RequestParam(required = true) Integer year,
			@ApiParam(name = "month", value = "月份", example = "3") @RequestParam(required = true) Integer month) {
		if (year < 2015 || year > 2099) {
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
		return ResponseEntity.ok(reportService.getMonthlyReportApplCount(year, month));
	}

	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@ApiOperation(value = "取得單月各科健保申報量與人次報表-匯出", notes = "取得單月各科健保申報量與人次報表-匯出")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/monthlyPointApplCountExport")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<BaseResponse> getMonthlyPointApplCountExport(
			@ApiParam(name = "year", value = "西元年", example = "2019") @RequestParam(required = true) Integer year,
			@ApiParam(name = "month", value = "月份", example = "3") @RequestParam(required = true) Integer month,
			HttpServletResponse response) throws IOException {
		if (year < 2015 || year > 2099) {
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
		reportExportService.getMonthlyReportApplCountExport(year, month, response);

		return null;
	}

	@ApiOperation(value = "健保藥費概況", notes = "健保藥費概況")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/healthCareCost")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<?> getHealthCareCost(
			@ApiParam(name = "syear", value = "開始年份", example = "2022") @RequestParam(required = false) String syear,
			@ApiParam(name = "season", value = "季度", example = "Q1") @RequestParam(required = false) String season) {

		List<HealthCareCost> results = new ArrayList<HealthCareCost>();

		if (syear.length() != 4 || season.length() == 0) {
			HealthCareCost healthCareCost = new HealthCareCost();
			healthCareCost.setResult(BaseResponse.ERROR);
			healthCareCost.setMessage("年份或季度格式不正確");
			results.add(healthCareCost);
			return ResponseEntity.badRequest().body(results);
		}

		results = healthCareCostService.getData(syear, season, results);
//		logger.info("健保藥費概況: {}", results.toString());

		if (results.size() == 1 && results.get(0).getResult().equals("error")
				&& results.get(0).getMessage().equals("季度格式不正確")) {
			return ResponseEntity.badRequest().body(results);
		}

		return ResponseEntity.ok(results);
	}

	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@ApiOperation(value = "取得健保點數月報表-匯出", notes = "取得健保點數月報表-匯出")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/monthlyPointExport")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<BaseResponse> getMonthlyPointExport(
			@ApiParam(name = "year", value = "西元年", example = "2021") @RequestParam(required = true) Integer year,
			@ApiParam(name = "month", value = "月份", example = "3") @RequestParam(required = true) Integer month,
			@ApiParam(name = "type", value = "慢籤額度顯示", example = "月初一次累加") @RequestParam(required = true) String type,
			HttpServletResponse response) throws IOException {
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
		reportExportService.getMonthlyReportExport(year, month, type, response);
		return null;
	}

	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@ApiOperation(value = "取得健保申報總額達成趨勢資料-匯出", notes = "取得健保申報總額達成趨勢資料-匯出")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/achievementRateExport")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<BaseResponse> getAchievementRateExport(
			@ApiParam(value = "西元年", example = "2021") @RequestParam(required = true) String year,
			@ApiParam(value = "第幾週(week of year)", example = "16") @RequestParam(required = true) String week,
			@ApiParam(name = "type", value = "慢籤額度顯示", example = "月初一次累加") @RequestParam(required = true) String type,
			HttpServletResponse response) throws IOException {

		reportExportService.getAchievementRateExport(year, week, type, response);
		return null;
	}

	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@ApiOperation(value = "取得健保總額累積達成率-匯出", notes = "取得健保申報總額達成趨勢資料-匯出")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/achievementRateQuarterExport")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<BaseResponse> getAchievementRateQuarterExport(
			@ApiParam(value = "西元年，若為多筆資料，用空格隔開", example = "2021 2021 2021") @RequestParam(required = true) String year,
			@ApiParam(value = "季度，若為多筆資料，用空格隔開", example = "Q1 Q2 Q3") @RequestParam(required = true) String quarter,
			HttpServletResponse response) throws IOException {

		reportExportService.getAchievementQuarterExport(year, quarter, response);
		return null;
	}

	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@ApiOperation(value = "取得DRG分配比例月報表-匯出", notes = "取得DRG分配比例月報表-匯出")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/drgMonthlyExport")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<BaseResponse> getDrgMonthlyExport(
			@ApiParam(name = "year", value = "西元年", example = "2021") @RequestParam(required = true) Integer year,
			@ApiParam(name = "month", value = "月份", example = "3") @RequestParam(required = true) Integer month,
			HttpServletResponse response) throws IOException {
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

		reportExportService.getDrgMonthlyExport(year, month, response);
		return null;
	}

	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@ApiOperation(value = "取得DRG各科分配比例月報表-匯出", notes = "取得DRG各科分配比例月報表-匯出")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/drgMonthlyAllFuncTypeExport")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<DRGMonthlyPayload> getDrgMonthlyAllFuncTypeExport(
			@ApiParam(name = "year", value = "西元年", example = "2021") @RequestParam(required = true) Integer year,
			@ApiParam(name = "month", value = "月份", example = "3") @RequestParam(required = true) Integer month,
			HttpServletResponse response) throws IOException {
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
		reportExportService.getDrgMonthlyAllFuncTypeExport(year, month, response);
		return null;
	}

	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@ApiOperation(value = "取得DRG各區分配比例月報表-匯出", notes = "取得DRG各區分配比例月報表-匯出")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/drgMonthlySectionExport")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<BaseResponse> getDrgMonthlySectionExport(
			@ApiParam(name = "year", value = "西元年", example = "2021") @RequestParam(required = true) Integer year,
			@ApiParam(name = "month", value = "月份", example = "3") @RequestParam(required = true) Integer month,
			HttpServletResponse response) throws IOException {
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
		reportExportService.getDrgMonthlySectionExport(year, month, response);
		return null;
	}

	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@ApiOperation(value = "健保藥費概況-匯出", notes = "健保藥費概況-匯出")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/healthCareCostExport")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<BaseResponse> getHealthCareCostExport(
			@ApiParam(name = "syear", value = "開始年份", example = "2022") @RequestParam(required = false) String syear,
			@ApiParam(name = "season", value = "季度", example = "Q1") @RequestParam(required = false) String season,
			HttpServletResponse response) {

		List<HealthCareCost> results = new ArrayList<HealthCareCost>();

		if (syear.length() != 4 || season.length() == 0) {
			HealthCareCost healthCareCost = new HealthCareCost();
			healthCareCost.setResult(BaseResponse.ERROR);
			healthCareCost.setMessage("年份或季度格式不正確");
			return ResponseEntity.badRequest().body(healthCareCost);
		}

		results = healthCareCostService.getData(syear, season, results);

		if (results.size() == 1 && results.get(0).getResult().equals("error")
				&& results.get(0).getMessage().equals("季度格式不正確")) {
			HealthCareCost healthCareCost = new HealthCareCost();
			healthCareCost.setResult(BaseResponse.ERROR);
			healthCareCost.setMessage("季度格式不正確");
			return ResponseEntity.badRequest().body(healthCareCost);
		}

		healthCareCostService.getDataExport(syear, season, results, response);

		return null;
	}

	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@ApiOperation(value = "取得費用業務-點數-匯出", notes = "取得費用業務-點數-匯出")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/periodPointExport")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<BaseResponse> getPeriodPointExport(
			@ApiParam(name = "sdate", value = "起始日期", example = "2021/01/01") @RequestParam(required = false) String sdate,
			@ApiParam(name = "edate", value = "結束日期", example = "2021/01/11") @RequestParam(required = false) String edate,
			@ApiParam(name = "funcType", value = "科別", example = "01") @RequestParam(required = false) String funcType,
			HttpServletResponse response) throws ParseException, IOException {
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
		reportExportService.getPeriodPointExport(sdate, edate, funcType, response);
		return null;
	}
	
	@ApiOperation(value = "取得核刪資訊", notes = "取得核刪資訊")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/deductedNote")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<DeductedPayloadResponse> getDeductedNote(
			@ApiParam(value = "西元年，若為多筆資料，用空格隔開", example = "2022 2022 2022") @RequestParam(required = true) String year,
			@ApiParam(value = "季度，若為多筆資料，用空格隔開", example = "Q1 Q2 Q3") @RequestParam(required = true) String quarter) {
		DeductedPayloadResponse res = new DeductedPayloadResponse();
		res.setData(reportService.getDeductedNote(year, quarter));
		return ResponseEntity.ok(res);

	}
	
	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@ApiOperation(value = "取得核刪資訊-匯出", notes = "取得核刪資訊-匯出")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/deductedNoteExport")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<BaseResponse> getDeductedNoteExport(
			@ApiParam(value = "西元年，若為多筆資料，用空格隔開", example = "2022 2022 2022") @RequestParam(required = true) String year,
			@ApiParam(value = "季度，若為多筆資料，用空格隔開", example = "Q1 Q2 Q3") @RequestParam(required = true) String quarter,
			HttpServletResponse response
			) throws IOException {
		reportExportService.getDeductedNoteExport(year, quarter, response);
		return null;

	}
}
