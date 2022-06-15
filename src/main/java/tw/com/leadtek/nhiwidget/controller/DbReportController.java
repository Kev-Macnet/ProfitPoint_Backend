package tw.com.leadtek.nhiwidget.controller;

import java.io.IOException;

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
			@ApiParam(value = "西元年，若為多筆資料，用空格隔開", example = "2021 2021 2021") @RequestParam(required = true) String year,
			@ApiParam(value = "月份，若為多筆資料，用空格隔開", example = "1 2 3") @RequestParam(required = true) String quarter,
			@ApiParam(value = "上個月同條件相比", example = "false") @RequestParam(required = true) boolean isLastM,
			@ApiParam(value = "去年同期時段相比", example = "false") @RequestParam(required = true) boolean isLastY) {

		return ResponseEntity.ok(dbService.getAchievementAndExcess(year, quarter, isLastM, isLastY));
	}

	@ApiOperation(value = "取得達成率與超額數-匯出", notes = "取得達成率與超額數-匯出")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/achievementRateAndExcessExport")
	public ResponseEntity<AchievementQuarter> getAchievementRateAndExcessExport(
			@ApiParam(value = "西元年，若為多筆資料，用空格隔開", example = "2021 2021 2021") @RequestParam(required = true) String year,
			@ApiParam(value = "月份，若為多筆資料，用空格隔開", example = "1 2 3") @RequestParam(required = true) String quarter,
			@ApiParam(value = "上個月同條件相比", example = "false") @RequestParam(required = true) boolean isLastM,
			@ApiParam(value = "去年同期時段相比", example = "false") @RequestParam(required = true) boolean isLastY,
			HttpServletResponse response) throws IOException {
		
		dbExportService.getAchievementAndExcessExport(year, quarter, isLastM, isLastY, response);
		return null;
	}

}
