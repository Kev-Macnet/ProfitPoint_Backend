package tw.com.leadtek.nhiwidget.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
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
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.report.AchievementQuarter;
import tw.com.leadtek.nhiwidget.service.DbReportExportService;
import tw.com.leadtek.nhiwidget.service.DbReportService;

@Api(tags = "資料庫統計匯出API", value = "資料庫統計匯出API")
@CrossOrigin(origins = "*", maxAge = 36000)
@RestController
// @CrossOrigin(value = "http://localhost:8080")
@RequestMapping(value = "/dbReport", produces = "application/json; charset=utf-8")
public class DbReportController extends BaseController {

	@Autowired
	private DbReportService dbService;
	@Autowired
	private DbReportExportService dbExportService;

	@ApiOperation(value = "取得hello", notes = "取得hello")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/hello")
	public ResponseEntity<BaseResponse> getHello() {
		return ResponseEntity.ok(dbService.test());
	}

	@ApiOperation(value = "取得達成率與超額數", notes = "取得達成率與超額數")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/achievementRateAndExcess")
	public ResponseEntity<AchievementQuarter> getAchievementRateAndExcess(
			@ApiParam(name = "year", value = "西元年，若為多筆資料，用空格隔開", example = "2021 2021 2021") @RequestParam(required = true) String year,
			@ApiParam(name = "quarter",value = "月份，若為多筆資料，用空格隔開", example = "1 2 3") @RequestParam(required = true) String quarter,
			@ApiParam(name = "isLastM",value = "上個月同條件相比", example = "false") @RequestParam(required = true) boolean isLastM,
			@ApiParam(name = "isLastY",value = "去年同期時段相比", example = "false") @RequestParam(required = true) boolean isLastY) {

		return ResponseEntity.ok(dbService.getAchievementAndExcess(year, quarter, isLastM, isLastY));
	}

	@ApiOperation(value = "取得達成率與超額數-匯出", notes = "取得達成率與超額數-匯出")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/achievementRateAndExcessExport")
	public ResponseEntity<AchievementQuarter> getAchievementRateAndExcessExport(
			@ApiParam(name = "year", value = "西元年，若為多筆資料，用空格隔開", example = "2021 2021 2021") @RequestParam(required = true) String year,
			@ApiParam(name = "quarter",value = "月份，若為多筆資料，用空格隔開", example = "1 2 3") @RequestParam(required = true) String quarter,
			@ApiParam(name = "isLastM",value = "上個月同條件相比", example = "false") @RequestParam(required = true) boolean isLastM,
			@ApiParam(name = "isLastY",value = "去年同期時段相比", example = "false") @RequestParam(required = true) boolean isLastY,
			HttpServletResponse response) throws IOException {

		dbExportService.getAchievementAndExcessExport(year, quarter, isLastM, isLastY, response);
		return null;
	}

	@ApiOperation(value = "取得DRG案件數分佈佔率與定額、實際點數", notes = "取得DRG案件數分佈佔率與定額、實際點數")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/drgQueryCondition")
	public ResponseEntity<Map<String,Object>> getDrgQueryCondition(
			@ApiParam(name = "dateType", value = "日期類型: 0=年月帶入，1=日期區間", example = "0") @RequestParam(required = true) String dateType,
			@ApiParam(name = "year", value = "西元年，若為多筆資料，用空格隔開，dateType=0時必填", example = "2021 2021 2021") @RequestParam(required = true) String year,
			@ApiParam(name = "month", value = "月份，若為多筆資料，用空格隔開，dateType=0時必填", example = "1 2 3") @RequestParam(required = true) String month,
			@ApiParam(name = "betweenSDate", value = "起始日，格式為yyyy-MM-dd，dateType=1時必填", example = "2020-06-01") @RequestParam(required = false) String betweenSDate,
			@ApiParam(name = "betweenEDate", value = "迄日，格式為yyyy-MM-dd，dateType=1時必填", example = "2020-06-30") @RequestParam(required = false) String betweenEDate,
			@ApiParam(name = "sections", value = "顯示區間，若為多筆資料，用空格隔開", example = "A B1 B2 C") @RequestParam(required = true) String sections,
			@ApiParam(name = "drgCodes", value = "指定DRG代碼，若為多筆資料，用空格隔開", example = "") @RequestParam(required = false) String drgCodes,
			@ApiParam(name = "dataFormats", value = "就醫類別，若為多筆資料，用空格隔開，為all totalop op em ip", example = "") @RequestParam(required = false) String dataFormats,
			@ApiParam(name = "funcTypes", value = "科別，若為多筆資料，用空格隔開，05 06", example = "") @RequestParam(required = false) String funcTypes,
			@ApiParam(name = "medNames", value = "醫護姓名，若為多筆資料，用空格隔開，R A ", example = "") @RequestParam(required = false) String medNames,
			@ApiParam(name = "icdcms", value = "病歷編號，若為多筆資料，用空格隔開，Z01.411 Z01.412 ", example = "") @RequestParam(required = false) String icdcms,
			@ApiParam(name = "medLogCodes", value = "就醫紀錄編號，若為多筆資料，用空格隔開，Z01.411 Z01.412 ", example = "") @RequestParam(required = false) String medLogCodes,
			@ApiParam(name = "applMin", value = "單筆申報點數(最小)", example = "1") @RequestParam(required = false) int applMin,
			@ApiParam(name = "applMax", value = "單筆申報點數(最大)", example = "1") @RequestParam(required = false) int applMax,
			@ApiParam(name = "icdAll", value = "不分區ICD碼，若為多筆資料，用空格隔開，Z01.411 Z01.412 ", example = "") @RequestParam(required = false) String icdAll,
			@ApiParam(name = "payCode", value = "支付標準代碼", example = "") @RequestParam(required = false) String payCode,
			@ApiParam(name = "inhCode", value = "院內碼", example = "") @RequestParam(required = false) String inhCode,
			@ApiParam(name = "isShowDRGList", value = "DRG項目列出", example = "false") @RequestParam(required = false) boolean isShowDRGList,
			@ApiParam(name = "isLastM", value = "上個月同條件相比", example = "false") @RequestParam(required = false) boolean isLastM,
			@ApiParam(name = "isLastY", value = "去年同期時段相比", example = "false") @RequestParam(required = false) boolean isLastY)
			throws ParseException {
//		DrgQueryConditionPayload result = new DrgQueryConditionPayload();
		Map<String,Object> result = new HashMap<String,Object>();

//		if(dateType == null) {
//			result.setResult(BaseResponse.ERROR);
//			result.setMessage("dateType為必填");
//			return ResponseEntity.badRequest().body(result);
//		}
		if (dateType == "0") {
			if (year.isEmpty() || month.isEmpty()) {
				result.put("result",BaseResponse.ERROR);
				result.put("message","dateType為0時，西元年或月為必填");
				return ResponseEntity.badRequest().body(result);
			}
		} else {
			isLastM = false;
			if (betweenSDate.isEmpty() || betweenEDate.isEmpty()) {
				result.put("result",BaseResponse.ERROR);
				result.put("message","dateType為1時，日期區間起迄日為必填");
				return ResponseEntity.badRequest().body(result);
			}
		}
		if (isShowDRGList) {
			drgCodes = "";
			isLastM = false;
			isLastY = false;
		}
		return ResponseEntity.ok(dbService.getDrgQueryCondition(dateType, year, month, betweenSDate, betweenEDate,
				sections, drgCodes, dataFormats, funcTypes, medNames, icdcms, medLogCodes, applMin, applMax, icdAll,
				payCode, inhCode, isShowDRGList, isLastM, isLastY));
	}

}
