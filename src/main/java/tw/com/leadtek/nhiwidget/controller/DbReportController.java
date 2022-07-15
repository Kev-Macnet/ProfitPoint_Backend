package tw.com.leadtek.nhiwidget.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import tw.com.leadtek.nhiwidget.payload.report.CaseStatusAndQuantity;
import tw.com.leadtek.nhiwidget.payload.report.DatabaseCalculateExportFactor;
import tw.com.leadtek.nhiwidget.payload.report.DeductedNoteQueryConditionResponse;
import tw.com.leadtek.nhiwidget.service.CaseStatusAndQuantityService;
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
	
	@Autowired
	private CaseStatusAndQuantityService caseStatusAndQuantityService;
	
	@ApiOperation(value = "資料庫統計匯出條件目錄", notes = "資料庫統計匯出條件目錄")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/databaseCalculateContents")
	public ResponseEntity<Map<String, Object>>getDatabaseCalculateContents(
	@ApiParam(name = "dateType", value = "日期類型: 0=年月帶入，1=日期區間", example = "0") @RequestParam(required = false) String dateType,
	@ApiParam(name = "exportType", value = "報表類型", example = "案件狀態與各別數量(可複選)") @RequestParam(required = false) String exportType,
	@ApiParam(name = "withLaborProtection", value = "健保狀態(單選) 健保(含勞保) or 健保(不含勞保)", example = "false") @RequestParam(required = false) Boolean withLaborProtection,
	@ApiParam(name = "classFee", value = "費用分類 多選用空格隔開", example = "門診診察費 品質支付服務 復健治療費") @RequestParam(required = false) String classFee,
	@ApiParam(name = "feeApply", value = "費用申報狀態(可複選) 多選用空格隔開", example = "自費 健保") @RequestParam(required = false) String feeApply,
	@ApiParam(name = "isShowSelfFeeList", value = "自費分項列出", example = "false") @RequestParam(required = false) Boolean isShowSelfFeeList,
	@ApiParam(name = "isShowPhysicalList", value = "就醫清單列出", example = "false") @RequestParam(required = false) Boolean isShowPhysicalList,
	@ApiParam(name = "caseStatus", value = "顯示案件狀態，若為多筆資料，用空格隔開", example = "無須變更 評估不調整 優化完成 待確認 待處理 疑問標示") @RequestParam(required = false) String caseStatus,
	@ApiParam(name = "year", value = "西元年，若為多筆資料，用空格隔開，dateType=0時必填", example = "2020 2021 2022") @RequestParam(required = false) String year,
	@ApiParam(name = "month", value = "月份，若為多筆資料，用空格隔開，dateType=0時必填", example = "1 2 3") @RequestParam(required = false) String month,
	@ApiParam(name = "betweenSDate", value = "起始日，格式為yyyy-MM-dd，dateType=1時必填", example = "2022-01-01") @RequestParam(required = false) String betweenSDate,
	@ApiParam(name = "betweenEDate", value = "迄日，格式為yyyy-MM-dd，dateType=1時必填", example = "2022-06-01") @RequestParam(required = false) String betweenEDate,
	@ApiParam(name = "sections", value = "顯示區間，若為多筆資料，用空格隔開", example = "A B1 B2 C") @RequestParam(required = false) String sections,
	@ApiParam(name = "drgCodes", value = "指定DRG代碼，若為多筆資料，用空格隔開", example = " ") @RequestParam(required = false) String drgCodes,
	@ApiParam(name = "dataFormats", value = "就醫類別，若為多筆資料，用空格隔開，為all totalop op em ip", example = "all totalop op em ip") @RequestParam(required = false) String dataFormats,
	@ApiParam(name = "funcTypes", value = "科別，若為多筆資料，用空格隔開，05 06", example = "05 06 AA") @RequestParam(required = false) String funcTypes,
	@ApiParam(name = "medNames", value = "醫護姓名，若為多筆資料，用空格隔開，R A ", example = "E121***289 F120***434") @RequestParam(required = false) String medNames,
	@ApiParam(name = "icdcms", value = "病歷編號，若為多筆資料，用空格隔開，Z01.411 Z01.412 ", example = "Z01.411 Z01.412") @RequestParam(required = false) String icdcms,
	@ApiParam(name = "medLogCodes", value = "就醫紀錄編號，若為多筆資料，用空格隔開，Z01.411 Z01.412 ", example = "Z01.411 Z01.412") @RequestParam(required = false) String medLogCodes,
	@ApiParam(name = "applMin", value = "單筆申報點數(最小)", example = "1") @RequestParam(required = false) Integer applMin,
	@ApiParam(name = "applMax", value = "單筆申報點數(最大)", example = "10") @RequestParam(required = false) Integer applMax,
	@ApiParam(name = "icdAll", value = "不分區ICD碼，若為多筆資料，用空格隔開，Z01.411 Z01.412 ", example = "Z01.411 Z01.412") @RequestParam(required = false) String icdAll,
	@ApiParam(name = "payCode", value = "支付標準代碼", example = " ") @RequestParam(required = false) String payCode,
	@ApiParam(name = "inhCode", value = "院內碼", example = " ") @RequestParam(required = false) String inhCode,
	@ApiParam(name = "isShowDRGList", value = "DRG項目列出", example = "false") @RequestParam(required = false) Boolean isShowDRGList,
	@ApiParam(name = "isLastM", value = "上個月同條件相比", example = "false") @RequestParam(required = false) Boolean isLastM,
	@ApiParam(name = "isLastY", value = "去年同期時段相比", example = "false") @RequestParam(required = false) Boolean isLastY){
		
		DatabaseCalculateExportFactor databaseCalculateExportFactor=dbService.getDatabaseCalculateContents(dateType,exportType,withLaborProtection,classFee,feeApply,isShowSelfFeeList,isShowPhysicalList,caseStatus,
				year,month,betweenSDate,betweenEDate,sections,drgCodes,dataFormats,funcTypes,medNames,icdcms, medLogCodes,applMin,applMax,icdAll,payCode,inhCode,isShowDRGList,
				isLastM,isLastY);
		
		Map<String, Object> result=new TreeMap<String, Object>();

		if(databaseCalculateExportFactor.getDateType()!=null && !databaseCalculateExportFactor.getDateType().equals("")) {
			result.put("日期類型: 0=年月帶入，1=日期區間", databaseCalculateExportFactor.getDateType());
		}
		if(databaseCalculateExportFactor.getExportName()!=null && !databaseCalculateExportFactor.getExportName().equals("")) {
			result.put("報表類型", databaseCalculateExportFactor.getExportName());
		}
		if(databaseCalculateExportFactor.getIsShowDRGList()!=null) {
			result.put("DRG項目列出", databaseCalculateExportFactor.getIsShowDRGList());
		}
	    if(databaseCalculateExportFactor.getSections()!=null && !databaseCalculateExportFactor.getSections().equals("")) {
	    	result.put("顯示區間(可複選)", databaseCalculateExportFactor.getSections());
	    }
	    if(databaseCalculateExportFactor.getDrgCodes()!=null && !databaseCalculateExportFactor.getDrgCodes().equals("")) {
	    	result.put("指定DRG代碼(可複選)", databaseCalculateExportFactor.getDrgCodes());
	    }
	    if(databaseCalculateExportFactor.getWithLaborProtection()!=null) {
	    	result.put("健保(是否含勞保)", databaseCalculateExportFactor.getWithLaborProtection());
	    }
	    if(databaseCalculateExportFactor.getClassFee()!=null && !databaseCalculateExportFactor.getClassFee().equals("")) {
	    	result.put("費用分類(可複選)", databaseCalculateExportFactor.getClassFee());
	    }
	    if(databaseCalculateExportFactor.getFeeApply()!=null && !databaseCalculateExportFactor.getFeeApply().equals("")) {
	    	result.put("費用申報狀態(可複選)", databaseCalculateExportFactor.getFeeApply());
	    }
		 if(databaseCalculateExportFactor.getIsShowSelfFeeList()!=null) {
		 	result.put("自費分項列出", databaseCalculateExportFactor.getIsShowSelfFeeList());
		 }
		if(databaseCalculateExportFactor.getIsShowPhysicalList()!=null) {
			result.put("就醫清單列出", databaseCalculateExportFactor.getIsShowPhysicalList());
		}
		if(databaseCalculateExportFactor.getCaseStatus()!=null && !databaseCalculateExportFactor.getCaseStatus().equals("")) {
			result.put("案件狀態(可複選)", databaseCalculateExportFactor.getCaseStatus());
		}
		if(databaseCalculateExportFactor.getYear()!=null && !databaseCalculateExportFactor.getYear().equals("")) {
			result.put("西元年(可複選)", databaseCalculateExportFactor.getYear());
		}
		if(databaseCalculateExportFactor.getMonth()!=null && !databaseCalculateExportFactor.getMonth().equals("")) {
			result.put("月份(可複選)", databaseCalculateExportFactor.getMonth());
		}
		if(databaseCalculateExportFactor.getBetweenSDate()!=null && !databaseCalculateExportFactor.getBetweenSDate().equals("")) {
			result.put("起始日", databaseCalculateExportFactor.getBetweenSDate());
		}
		if(databaseCalculateExportFactor.getBetweenEDate()!=null && !databaseCalculateExportFactor.getBetweenEDate().equals("")) {
			result.put("迄日", databaseCalculateExportFactor.getBetweenEDate());
		}
		if(databaseCalculateExportFactor.getDataFormats()!=null && !databaseCalculateExportFactor.getDataFormats().equals("")) {
			result.put("就醫類別(可複選)", databaseCalculateExportFactor.getDataFormats());
		}
		if(databaseCalculateExportFactor.getFuncTypes()!=null && !databaseCalculateExportFactor.getFuncTypes().equals("")) {
			result.put("科別(可複選)", databaseCalculateExportFactor.getFuncTypes());
		}
		if(databaseCalculateExportFactor.getMedNames()!=null && !databaseCalculateExportFactor.getMedNames().equals("")) {
			result.put("醫護姓名(可複選)", databaseCalculateExportFactor.getMedNames());
		}
		if(databaseCalculateExportFactor.getIcdcms()!=null && !databaseCalculateExportFactor.getIcdcms().equals("")) {
			result.put("病歷編號(可複選)", databaseCalculateExportFactor.getIcdcms());
		}
		 if(databaseCalculateExportFactor.getMedLogCodes()!=null && !databaseCalculateExportFactor.getMedLogCodes().equals("")) {
		 	result.put("就醫紀錄編號(可複選)", databaseCalculateExportFactor.getMedLogCodes());
		 }
		if(databaseCalculateExportFactor.getApplMin()!=null) {
			result.put("單筆申報點數(最小)", databaseCalculateExportFactor.getApplMin());
		}
		if(databaseCalculateExportFactor.getApplMax()!=null) {
			result.put("單筆申報點數(最大)", databaseCalculateExportFactor.getApplMax());
		}
		if(databaseCalculateExportFactor.getIcdAll()!=null && !databaseCalculateExportFactor.getIcdAll().equals("")) {
			result.put("不分區ICD碼(可複選)", databaseCalculateExportFactor.getIcdAll());
		}
		if(databaseCalculateExportFactor.getPayCode()!=null && !databaseCalculateExportFactor.getPayCode().equals("")) {
			result.put("支付標準代碼", databaseCalculateExportFactor.getPayCode());
		}
		if(databaseCalculateExportFactor.getInhCode()!=null && !databaseCalculateExportFactor.getInhCode().equals("")) {
			result.put("院內碼", databaseCalculateExportFactor.getInhCode());
		}
		if(databaseCalculateExportFactor.getIsLastM()!=null) {
			result.put("上個月同條件相比", databaseCalculateExportFactor.getIsLastM());
		}
		if(databaseCalculateExportFactor.getIsLastY()!=null) {
			result.put("去年同期時段相比", databaseCalculateExportFactor.getIsLastY());
		}
		
	  return ResponseEntity.ok(result);
	}
	
	@ApiOperation(value = "醫令項目與執行量", notes = "醫令項目與執行量")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/medicalOrder")
	public ResponseEntity<Map<String, Object>> getMedicalOrder(
			@ApiParam(name = "feeApply", value = "費用申報狀態(可複選)，多選用空格隔開，自費 健保", example = "自費 健保") @RequestParam(required = false) String feeApply,
			@ApiParam(name = "dateType", value = "日期類型: 0=年月帶入，1=日期區間", example = "0") @RequestParam(required = true) String dateType,
			@ApiParam(name = "year", value = "西元年，若為多筆資料，用空格隔開，dateType=0時必填", example = "2020 2021 2022") @RequestParam(required = true) String year,
			@ApiParam(name = "month", value = "月份，若為多筆資料，用空格隔開，dateType=0時必填", example = "1 2 3") @RequestParam(required = true) String month,
			@ApiParam(name = "betweenSDate", value = "起始日，格式為yyyy-MM-dd，dateType=1時必填", example = "2020-06-01") @RequestParam(required = false) String betweenSDate,
			@ApiParam(name = "betweenEDate", value = "迄日，格式為yyyy-MM-dd，dateType=1時必填", example = "2020-06-30") @RequestParam(required = false) String betweenEDate,
			@ApiParam(name = "dataFormats", value = "就醫類別，若為多筆資料，用空格隔開，為all totalop op em ip", example = "all") @RequestParam(required = false) String dataFormats,
			@ApiParam(name = "funcTypes", value = "科別，若為多筆資料，用空格隔開，05 06", example = "") @RequestParam(required = false) String funcTypes,
			@ApiParam(name = "medNames", value = "醫護姓名，若為多筆資料，用空格隔開，R A ", example = "") @RequestParam(required = false) String medNames,
			@ApiParam(name = "icdAll", value = "不分區ICD碼，若為多筆資料，用空格隔開，Z01.411 Z01.412 ", example = "") @RequestParam(required = false) String icdAll,
			@ApiParam(name = "payCode", value = "支付標準代碼", example = "") @RequestParam(required = false) String payCode,
			@ApiParam(name = "inhCode", value = "院內碼", example = "") @RequestParam(required = false) String inhCode,
			@ApiParam(name = "isLastM", value = "上個月同條件相比", example = "false") @RequestParam(required = false) boolean isLastM,
			@ApiParam(name = "isLastY", value = "去年同期時段同條件相比", example = "false") @RequestParam(required = false) boolean isLastY){
			
			Map<String, Object>result=new HashMap<String, Object>();
		
			if(feeApply.equals("") || feeApply==null) {
				result.put("result", BaseResponse.ERROR);
				result.put("message", "費用申報狀態不可為空");
				return ResponseEntity.badRequest().body(result);
			}
			
			if (dateType.equals("0")) {
				if (year.isEmpty() || month.isEmpty()) {
					result.put("result", BaseResponse.ERROR);
					result.put("message","dateType為0時，西元年或月為必填");
					return ResponseEntity.badRequest().body(result);
				}
			} 
			else {
				if (betweenSDate.isEmpty() || betweenEDate.isEmpty()) {
					result.put("result", BaseResponse.ERROR);
					result.put("message","dateType為1時，日期區間起迄日為必填");
					return ResponseEntity.badRequest().body(result);
				}
			}
			
			try {
				result=dbService.getMedicalOrder(feeApply,dateType,year,month,betweenSDate,betweenEDate,dataFormats,funcTypes,
						medNames,icdAll,payCode,inhCode,isLastM,isLastY);
			} catch (Exception e) {
				// TODO: handle exception
//				e.printStackTrace();
			}
			
			return ResponseEntity.ok(result);
	}
	
	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@ApiOperation(value = "醫令項目與執行量-匯出", notes = "醫令項目與執行量-匯出")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/medicalOrderExport")
	public ResponseEntity<BaseResponse> getMedicalOrderExport(
			@ApiParam(name = "feeApply", value = "費用申報狀態(可複選)，多選用空格隔開，自費 健保", example = "自費 健保") @RequestParam(required = false) String feeApply,
			@ApiParam(name = "dateType", value = "日期類型: 0=年月帶入，1=日期區間", example = "0") @RequestParam(required = true) String dateType,
			@ApiParam(name = "year", value = "西元年，若為多筆資料，用空格隔開，dateType=0時必填", example = "2020 2021 2022") @RequestParam(required = true) String year,
			@ApiParam(name = "month", value = "月份，若為多筆資料，用空格隔開，dateType=0時必填", example = "1 2 3") @RequestParam(required = true) String month,
			@ApiParam(name = "betweenSDate", value = "起始日，格式為yyyy-MM-dd，dateType=1時必填", example = "2020-06-01") @RequestParam(required = false) String betweenSDate,
			@ApiParam(name = "betweenEDate", value = "迄日，格式為yyyy-MM-dd，dateType=1時必填", example = "2020-06-30") @RequestParam(required = false) String betweenEDate,
			@ApiParam(name = "dataFormats", value = "就醫類別，若為多筆資料，用空格隔開，為all totalop op em ip", example = "all") @RequestParam(required = false) String dataFormats,
			@ApiParam(name = "funcTypes", value = "科別，若為多筆資料，用空格隔開，05 06", example = "") @RequestParam(required = false) String funcTypes,
			@ApiParam(name = "medNames", value = "醫護姓名，若為多筆資料，用空格隔開，R A ", example = "") @RequestParam(required = false) String medNames,
			@ApiParam(name = "icdAll", value = "不分區ICD碼，若為多筆資料，用空格隔開，Z01.411 Z01.412 ", example = "") @RequestParam(required = false) String icdAll,
			@ApiParam(name = "payCode", value = "支付標準代碼", example = "") @RequestParam(required = false) String payCode,
			@ApiParam(name = "inhCode", value = "院內碼", example = "") @RequestParam(required = false) String inhCode,
			@ApiParam(name = "isLastM", value = "上個月同條件相比", example = "false") @RequestParam(required = false) boolean isLastM,
			@ApiParam(name = "isLastY", value = "去年同期時段同條件相比", example = "false") @RequestParam(required = false) boolean isLastY,
			HttpServletResponse response){
			
			BaseResponse baseResponse=new BaseResponse();
		
			if(feeApply.equals("") || feeApply==null) {
				baseResponse.setResult(BaseResponse.ERROR);
				baseResponse.setMessage("費用申報狀態不可為空");
				return ResponseEntity.badRequest().body(baseResponse);
			}
			
			if (dateType.equals("0")) {
				if (year.isEmpty() || month.isEmpty()) {
					baseResponse.setResult(BaseResponse.ERROR);
					baseResponse.setMessage("dateType為0時，西元年或月為必填");
					return ResponseEntity.badRequest().body(baseResponse);
				}
			} 
			else {
				if (betweenSDate.isEmpty() || betweenEDate.isEmpty()) {
					baseResponse.setResult(BaseResponse.ERROR);
					baseResponse.setMessage("dateType為1時，日期區間起迄日為必填");
					return ResponseEntity.badRequest().body(baseResponse);
				}
			}
			
			try {
				Map<String, Object> result=dbService.getMedicalOrder(feeApply,dateType,year,month,betweenSDate,betweenEDate,dataFormats,funcTypes,
						medNames,icdAll,payCode,inhCode,isLastM,isLastY);
				
				dbExportService.getMedicalOrderExport(result,feeApply,dateType,year,month,betweenSDate,betweenEDate,dataFormats,funcTypes,
						medNames,icdAll,payCode,inhCode,isLastM,isLastY,response);
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("醫令項目與執行量匯出報表錯誤: {}",e);
//				e.printStackTrace();
			}
			
			return null;
	}
	
	@ApiOperation(value = "案件狀態與各別數量(可複選)", notes = "案件狀態與各別數量(可複選)")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/caseStatusAndQuantity")
	public ResponseEntity<?> getCaseStatusAndQuantity(
			@ApiParam(name = "status", value = "案件狀態與各別數量(可複選)", example = "無須變更 評估不調整 優化完成 待確認 待處理 疑問標示")
			@RequestParam(required = false) String status,
			@ApiParam(name = "physical", value = "是否包含列出就醫清單", example = "true")@RequestParam(required = false) boolean physical,
			@ApiParam(name = "startMonth", value = "開始月份", example = "2022/01") @RequestParam(required = false) String startMonth,
			@ApiParam(name = "endMonth", value = "結束月份", example = "2022/12") @RequestParam(required = false) String endMonth) {
		
		List<CaseStatusAndQuantity> results=new ArrayList<CaseStatusAndQuantity>();
		
		if(status.length()==0) {
			CaseStatusAndQuantity caseStatusAndQuantity=new CaseStatusAndQuantity();
			caseStatusAndQuantity.setResult(BaseResponse.ERROR);
			caseStatusAndQuantity.setMessage("無勾選案件狀態");
			results.add(caseStatusAndQuantity);
		    return ResponseEntity.badRequest().body(results);
		}
		
		if(startMonth!=null && endMonth!=null && !startMonth.equals("") && !endMonth.equals("") && !startMonth.equals("null") && !endMonth.equals("null")) {
				results=caseStatusAndQuantityService.getData(physical,status,startMonth,endMonth);
		}
		else {
			CaseStatusAndQuantity caseStatusAndQuantity=new CaseStatusAndQuantity();
			caseStatusAndQuantity.setResult(BaseResponse.ERROR);
			caseStatusAndQuantity.setMessage("資料格式不正確");
			results.add(caseStatusAndQuantity);
		    return ResponseEntity.badRequest().body(results);
		}
		
		return ResponseEntity.ok(results);
	}
	
	  @CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	  @ApiOperation(value = "案件狀態與各別數量(可複選)-匯出", notes = "案件狀態與各別數量(可複選)-匯出")
	  @ApiResponses({@ApiResponse(responseCode = "200", description = "成功")})
	  @GetMapping("/caseStatusAndQuantityExport")
	  public ResponseEntity<BaseResponse> getCaseStatusAndQuantityExport(
		 @ApiParam(name = "status", value = "案件狀態與各別數量(可複選)", example = "無須變更 評估不調整 優化完成 待確認 待處理 疑問標示")
		 @RequestParam(required = false) String status,
		 @ApiParam(name = "physical", value = "是否包含列出就醫清單", example = "true")@RequestParam(required = false) boolean physical,
		 @ApiParam(name = "startMonth", value = "開始月份", example = "2022/01") @RequestParam(required = false) String startMonth,
		 @ApiParam(name = "endMonth", value = "結束月份", example = "2022/12") @RequestParam(required = false) String endMonth,
	     HttpServletResponse response){
		  
			List<CaseStatusAndQuantity> results=new ArrayList<CaseStatusAndQuantity>();
			CaseStatusAndQuantity caseStatusAndQuantity=new CaseStatusAndQuantity();
			
			if(status.length()==0) {
				caseStatusAndQuantity.setResult(BaseResponse.ERROR);
				caseStatusAndQuantity.setMessage("無勾選案件狀態");
			    return ResponseEntity.badRequest().body(caseStatusAndQuantity);
			}
			
			if(startMonth!=null && endMonth!=null && !startMonth.equals("") && !endMonth.equals("") && !startMonth.equals("null") && !endMonth.equals("null")) {
					results=caseStatusAndQuantityService.getData(physical,status,startMonth,endMonth);
					caseStatusAndQuantityService.getDataExport(physical,results,startMonth,endMonth,response);
			}
			else {
				caseStatusAndQuantity.setResult(BaseResponse.ERROR);
				caseStatusAndQuantity.setMessage("資料格式不正確");
			    return ResponseEntity.badRequest().body(caseStatusAndQuantity);
			}
		  
		
			caseStatusAndQuantity.setResult(BaseResponse.SUCCESS);
			caseStatusAndQuantity.setMessage("");
		    return ResponseEntity.ok().body(caseStatusAndQuantity);
	  }

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
			@ApiParam(name = "quarter", value = "月份，若為多筆資料，用空格隔開", example = "1 2 3") @RequestParam(required = true) String quarter,
			@ApiParam(name = "isLastM", value = "上個月同條件相比", example = "false") @RequestParam(required = true) boolean isLastM,
			@ApiParam(name = "isLastY", value = "去年同期時段相比", example = "false") @RequestParam(required = true) boolean isLastY) {

		return ResponseEntity.ok(dbService.getAchievementAndExcess(year, quarter, isLastM, isLastY));
	}
	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@ApiOperation(value = "取得達成率與超額數-匯出", notes = "取得達成率與超額數-匯出")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/achievementRateAndExcessExport")
	public ResponseEntity<AchievementQuarter> getAchievementRateAndExcessExport(
			@ApiParam(name = "year", value = "西元年，若為多筆資料，用空格隔開", example = "2021 2021 2021") @RequestParam(required = true) String year,
			@ApiParam(name = "quarter", value = "月份，若為多筆資料，用空格隔開", example = "1 2 3") @RequestParam(required = true) String quarter,
			@ApiParam(name = "isLastM", value = "上個月同條件相比", example = "false") @RequestParam(required = true) boolean isLastM,
			@ApiParam(name = "isLastY", value = "去年同期時段相比", example = "false") @RequestParam(required = true) boolean isLastY,
			HttpServletResponse response) throws IOException {

		dbExportService.getAchievementAndExcessExport(year, quarter, isLastM, isLastY, response);
		return null;
	}

	@ApiOperation(value = "取得DRG案件數分佈佔率與定額、實際點數", notes = "取得DRG案件數分佈佔率與定額、實際點數")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/drgQueryCondition")
	public ResponseEntity<Map<String, Object>> getDrgQueryCondition(
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

		Map<String, Object> result = new HashMap<String, Object>();


		if (dateType.equals("0")) {
			if (year.isEmpty() || month.isEmpty()) {
				result.put("result", BaseResponse.ERROR);
				result.put("message", "dateType為0時，西元年或月為必填");
				return ResponseEntity.badRequest().body(result);
			}
		} else {
			isLastM = false;
			if (betweenSDate.isEmpty() || betweenEDate.isEmpty()) {
				result.put("result", BaseResponse.ERROR);
				result.put("message", "dateType為1時，日期區間起迄日為必填");
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
	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@ApiOperation(value = "取得DRG案件數分佈佔率與定額、實際點數-匯出", notes = "取得DRG案件數分佈佔率與定額、實際點數-匯出")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/drgQueryConditionExport")
	public ResponseEntity<Map<String, Object>> getDrgQueryConditionExport(
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
			@ApiParam(name = "isLastY", value = "去年同期時段相比", example = "false") @RequestParam(required = false) boolean isLastY,
			HttpServletResponse response) throws ParseException, IOException {
//		DrgQueryConditionPayload result = new DrgQueryConditionPayload();
		Map<String, Object> result = new HashMap<String, Object>();

//		if(dateType == null) {
//			result.setResult(BaseResponse.ERROR);
//			result.setMessage("dateType為必填");
//			return ResponseEntity.badRequest().body(result);
//		}
		if (dateType.equals("0")) {
			if (year.isEmpty() || month.isEmpty()) {
				result.put("result", BaseResponse.ERROR);
				result.put("message", "dateType為0時，西元年或月為必填");
				return ResponseEntity.badRequest().body(result);
			}
		} else {
			isLastM = false;
			if (betweenSDate.isEmpty() || betweenEDate.isEmpty()) {
				result.put("result", BaseResponse.ERROR);
				result.put("message", "dateType為1時，日期區間起迄日為必填");
				return ResponseEntity.badRequest().body(result);
			}
		}
		if (isShowDRGList) {
			drgCodes = "";
			isLastM = false;
			isLastY = false;
		}
		dbExportService.getDrgQueryConditionExport(dateType, year, month, betweenSDate, betweenEDate, sections,
				drgCodes, dataFormats, funcTypes, medNames, icdcms, medLogCodes, applMin, applMax, icdAll, payCode,
				inhCode, isShowDRGList, isLastM, isLastY, response);
		return null;
	}
	
	@ApiOperation(value = "取得自費項目清單", notes = "取得自費項目清單")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/ownExpenseQueryCondition")
	public ResponseEntity<Map<String, Object>> getOwnExpenseQueryCondition(
			@ApiParam(name = "betweenSDate", value = "起始日，格式為yyyy-MM-dd，dateType=1時必填", example = "2020-06-01") @RequestParam(required = true) String betweenSDate,
			@ApiParam(name = "betweenEDate", value = "迄日，格式為yyyy-MM-dd，dateType=1時必填", example = "2020-06-30") @RequestParam(required = true) String betweenEDate,
			@ApiParam(name = "dataFormats", value = "就醫類別，若為多筆資料，用空格隔開，為all totalop op em ip", example = "all") @RequestParam(required = true) String dataFormats,
			@ApiParam(name = "funcTypes", value = "科別，若為多筆資料，用空格隔開，05 06", example = "") @RequestParam(required = false) String funcTypes,
			@ApiParam(name = "medNames", value = "醫護姓名，若為多筆資料，用空格隔開，R A ", example = "") @RequestParam(required = false) String medNames,
			@ApiParam(name = "icdAll", value = "不分區ICD碼，若為多筆資料，用空格隔開，Z01.411 Z01.412 ", example = "") @RequestParam(required = false) String icdAll,
			@ApiParam(name = "payCode", value = "支付標準代碼", example = "") @RequestParam(required = false) String payCode,
			@ApiParam(name = "inhCode", value = "院內碼", example = "") @RequestParam(required = false) String inhCode,
			@ApiParam(name = "isLastY", value = "去年同期時段相比", example = "false") @RequestParam(required = false) boolean isLastY,
			@ApiParam(name = "isShowOwnExpense", value = "自費分項列出", example = "false") @RequestParam(required = false) boolean isShowOwnExpense)
			throws ParseException {

		Map<String, Object> result = new HashMap<String, Object>();
		
		if(betweenSDate == null || betweenSDate.isEmpty()) {
			result.put("result", BaseResponse.ERROR);
			result.put("message", "起始日必填");
			return ResponseEntity.badRequest().body(result);
		}
		
		if(betweenEDate == null || betweenEDate.isEmpty()) {
			result.put("result", BaseResponse.ERROR);
			result.put("message", "迄日必填");
			return ResponseEntity.badRequest().body(result);
		}
		
		if(dataFormats == null || dataFormats.isEmpty() ) {
			result.put("result", BaseResponse.ERROR);
			result.put("message", "就醫類別必填");
			return ResponseEntity.badRequest().body(result);
		}
		
		
		return ResponseEntity.ok(dbService.getOwnExpenseQueryCondition(betweenSDate, betweenEDate, dataFormats, funcTypes, medNames, icdAll, payCode, inhCode, isLastY, isShowOwnExpense));
	}
	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@ApiOperation(value = "取得自費項目清單-匯出", notes = "取得自費項目清單-匯出")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/ownExpenseQueryConditionExport")
	public ResponseEntity<BaseResponse> getOwnExpenseQueryConditionExport(
			@ApiParam(name = "betweenSDate", value = "起始日，格式為yyyy-MM-dd，dateType=1時必填", example = "2020-06-01") @RequestParam(required = true) String betweenSDate,
			@ApiParam(name = "betweenEDate", value = "迄日，格式為yyyy-MM-dd，dateType=1時必填", example = "2020-06-30") @RequestParam(required = true) String betweenEDate,
			@ApiParam(name = "dataFormats", value = "就醫類別，若為多筆資料，用空格隔開，為all totalop op em ip", example = "all") @RequestParam(required = true) String dataFormats,
			@ApiParam(name = "funcTypes", value = "科別，若為多筆資料，用空格隔開，05 06", example = "") @RequestParam(required = false) String funcTypes,
			@ApiParam(name = "medNames", value = "醫護姓名，若為多筆資料，用空格隔開，R A ", example = "") @RequestParam(required = false) String medNames,
			@ApiParam(name = "icdAll", value = "不分區ICD碼，若為多筆資料，用空格隔開，Z01.411 Z01.412 ", example = "") @RequestParam(required = false) String icdAll,
			@ApiParam(name = "payCode", value = "支付標準代碼", example = "") @RequestParam(required = false) String payCode,
			@ApiParam(name = "inhCode", value = "院內碼", example = "") @RequestParam(required = false) String inhCode,
			@ApiParam(name = "isLastY", value = "去年同期時段相比", example = "false") @RequestParam(required = false) boolean isLastY,
			@ApiParam(name = "isShowOwnExpense", value = "自費分項列出", example = "false") @RequestParam(required = false) boolean isShowOwnExpense,
			HttpServletResponse response
			)
			throws ParseException,IOException {
		BaseResponse result = new BaseResponse();
		
		
		if(betweenSDate == null || betweenSDate.isEmpty()) {
			result.setResult(BaseResponse.ERROR);
			result.setMessage("起始日必填");
			return ResponseEntity.badRequest().body(result);
		}
		
		if(betweenEDate == null || betweenEDate.isEmpty()) {
			result.setResult(BaseResponse.ERROR);
			result.setMessage("迄日必填");
			return ResponseEntity.badRequest().body(result);
		}
		
		if(dataFormats == null || dataFormats.isEmpty() ) {
			result.setResult(BaseResponse.ERROR);
			result.setMessage("就醫類別必填");
			return ResponseEntity.badRequest().body(result);
		}
		dbExportService.getOwnExpenseQueryConditionExport(betweenSDate, betweenEDate, dataFormats, funcTypes, medNames, icdAll, payCode, inhCode, isLastY, isShowOwnExpense, response);
		
		return null;
	}
	
	@ApiOperation(value = "取得申報分配佔率與點數、金額", notes = "取得申報分配佔率與點數、金額")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/achievePointQueryCondition")
	public ResponseEntity<Map<String, Object>> getAchievePointQueryCondition(
			@ApiParam(name = "nhiStatus", value = "健保狀態，1=含勞保、2＝不含勞保，必填", example = "1") @RequestParam(required = true) String nhiStatus,
			@ApiParam(name = "payCodeType", value = "費用分類，若為多筆資料，用空格隔開，", example = "1 2") @RequestParam(required = false) String payCodeType,
			@ApiParam(name = "year", value = "西元年，若為多筆資料，用空格隔開，必填", example = "2021 2021 2021") @RequestParam(required = true) String year,
			@ApiParam(name = "month", value = "月份，若為多筆資料，用空格隔開，必填", example = "1 2 3") @RequestParam(required = true) String month,
			@ApiParam(name = "dataFormats", value = "就醫類別，若為多筆資料，用空格隔開，為all totalop op em ip", example = "all") @RequestParam(required = false) String dataFormats,
			@ApiParam(name = "funcTypes", value = "科別，若為多筆資料，用空格隔開，05 06", example = "") @RequestParam(required = false) String funcTypes,
			@ApiParam(name = "medNames", value = "醫護姓名，若為多筆資料，用空格隔開，R A ", example = "") @RequestParam(required = false) String medNames,
			@ApiParam(name = "icdcms", value = "病歷編號，若為多筆資料，用空格隔開，Z01.411 Z01.412 ", example = "") @RequestParam(required = false) String icdcms,
			@ApiParam(name = "medLogCodes", value = "就醫紀錄編號，若為多筆資料，用空格隔開，Z01.411 Z01.412 ", example = "") @RequestParam(required = false) String medLogCodes,
			@ApiParam(name = "applMin", value = "單筆申報點數(最小)", example = "1") @RequestParam(required = false) Long applMin,
			@ApiParam(name = "applMax", value = "單筆申報點數(最大)", example = "1") @RequestParam(required = false) Long applMax,
			@ApiParam(name = "icdAll", value = "不分區ICD碼，若為多筆資料，用空格隔開，Z01.411 Z01.412 ", example = "") @RequestParam(required = false) String icdAll,
			@ApiParam(name = "payCode", value = "支付標準代碼", example = "") @RequestParam(required = false) String payCode,
			@ApiParam(name = "inhCode", value = "院內碼", example = "") @RequestParam(required = false) String inhCode,
			@ApiParam(name = "isLastM", value = "上個月同條件相比", example = "false") @RequestParam(required = false) boolean isLastM,
			@ApiParam(name = "isLastY", value = "去年同期時段相比", example = "false") @RequestParam(required = false) boolean isLastY)
			throws ParseException {

		Map<String, Object> result = new HashMap<String, Object>();


		
		return ResponseEntity.ok(dbService.getAchievePointQueryCondition(nhiStatus, payCodeType, year, month, dataFormats, funcTypes, medNames, icdcms, medLogCodes, applMin, applMax, icdAll, payCode, inhCode, isLastM, isLastY));
	}
	@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
	@ApiOperation(value = "取得申報分配佔率與點數、金額-匯出", notes = "取得申報分配佔率與點數、金額-匯出")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/achievePointQueryConditionExport")
	public ResponseEntity<BaseResponse> getAchievePointQueryConditionExport(
			@ApiParam(name = "nhiStatus", value = "健保狀態，1=含勞保、2＝不含勞保，必填", example = "1") @RequestParam(required = true) String nhiStatus,
			@ApiParam(name = "payCodeType", value = "費用分類，若為多筆資料，用空格隔開，", example = "1 2") @RequestParam(required = false) String payCodeType,
			@ApiParam(name = "year", value = "西元年，若為多筆資料，用空格隔開，必填", example = "2021 2021 2021") @RequestParam(required = true) String year,
			@ApiParam(name = "month", value = "月份，若為多筆資料，用空格隔開，必填", example = "1 2 3") @RequestParam(required = true) String month,
			@ApiParam(name = "dataFormats", value = "就醫類別，若為多筆資料，用空格隔開，為all totalop op em ip", example = "all") @RequestParam(required = false) String dataFormats,
			@ApiParam(name = "funcTypes", value = "科別，若為多筆資料，用空格隔開，05 06", example = "") @RequestParam(required = false) String funcTypes,
			@ApiParam(name = "medNames", value = "醫護姓名，若為多筆資料，用空格隔開，R A ", example = "") @RequestParam(required = false) String medNames,
			@ApiParam(name = "icdcms", value = "病歷編號，若為多筆資料，用空格隔開，Z01.411 Z01.412 ", example = "") @RequestParam(required = false) String icdcms,
			@ApiParam(name = "medLogCodes", value = "就醫紀錄編號，若為多筆資料，用空格隔開，Z01.411 Z01.412 ", example = "") @RequestParam(required = false) String medLogCodes,
			@ApiParam(name = "applMin", value = "單筆申報點數(最小)", example = "1") @RequestParam(required = false) Long applMin,
			@ApiParam(name = "applMax", value = "單筆申報點數(最大)", example = "1") @RequestParam(required = false) Long applMax,
			@ApiParam(name = "icdAll", value = "不分區ICD碼，若為多筆資料，用空格隔開，Z01.411 Z01.412 ", example = "") @RequestParam(required = false) String icdAll,
			@ApiParam(name = "payCode", value = "支付標準代碼", example = "") @RequestParam(required = false) String payCode,
			@ApiParam(name = "inhCode", value = "院內碼", example = "") @RequestParam(required = false) String inhCode,
			@ApiParam(name = "isLastM", value = "上個月同條件相比", example = "false") @RequestParam(required = false) boolean isLastM,
			@ApiParam(name = "isLastY", value = "去年同期時段相比", example = "false") @RequestParam(required = false) boolean isLastY,
			HttpServletResponse response
			)
			throws ParseException, IOException {

		Map<String, Object> result = new HashMap<String, Object>();
		dbExportService.getAchievePointQueryConditionExport(nhiStatus, payCodeType, year, month, dataFormats, funcTypes, medNames, icdcms, medLogCodes, applMin, applMax, icdAll, payCode, inhCode, isLastM, isLastY, response);

		
		return null;
	}
	
	@ApiOperation(value = "取得核刪資料", notes = "取得核刪資料")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "成功") })
	@GetMapping("/deductedNoteQueryCondition")
	public ResponseEntity<DeductedNoteQueryConditionResponse> getDeductedNoteQueryCondition(
			@ApiParam(name = "year", value = "西元年，若為多筆資料，用空格隔開，必填", example = "2022") @RequestParam(required = true) String year,
			@ApiParam(name = "month", value = "月份，若為多筆資料，用空格隔開，必填", example = "1") @RequestParam(required = true) String month,
			@ApiParam(name = "dataFormats", value = "就醫類別，若為多筆資料，用空格隔開，為all totalop op em ip", example = "all") @RequestParam(required = false) String dataFormats,
			@ApiParam(name = "funcTypes", value = "科別，若為多筆資料，用空格隔開，05 06", example = "") @RequestParam(required = false) String funcTypes,
			@ApiParam(name = "medNames", value = "醫護姓名，若為多筆資料，用空格隔開，R A ", example = "") @RequestParam(required = false) String medNames,
			@ApiParam(name = "icdAll", value = "不分區ICD碼，若為多筆資料，用空格隔開，Z01.411 Z01.412 ", example = "") @RequestParam(required = false) String icdAll,
			@ApiParam(name = "payCode", value = "支付標準代碼", example = "") @RequestParam(required = false) String payCode,
			@ApiParam(name = "inhCode", value = "院內碼", example = "") @RequestParam(required = false) String inhCode,
			@ApiParam(name = "isLastM", value = "上個月同條件相比", example = "false") @RequestParam(required = false) boolean isLastM,
			@ApiParam(name = "isLastY", value = "去年同期時段相比", example = "false") @RequestParam(required = false) boolean isLastY)
			throws ParseException {

		
		return ResponseEntity.ok(dbService.getDeductedNoteQueryCondition(year, month, dataFormats, funcTypes, medNames, icdAll, payCode, inhCode, isLastM, isLastY));
	}

}
