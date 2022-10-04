package tw.com.leadtek.nhiwidget.controller;

import java.io.InputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import tw.com.leadtek.nhiwidget.annotation.LogDefender;
import tw.com.leadtek.nhiwidget.constant.LogType;
import tw.com.leadtek.nhiwidget.dto.LogActionDto;
import tw.com.leadtek.nhiwidget.dto.LogExportDto;
import tw.com.leadtek.nhiwidget.dto.LogForgotPwdDto;
import tw.com.leadtek.nhiwidget.dto.LogImportDto;
import tw.com.leadtek.nhiwidget.dto.LogMrDto;
import tw.com.leadtek.nhiwidget.dto.LogSigninDto;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.log.LogOperateResponse;
import tw.com.leadtek.nhiwidget.security.service.UserDetailsImpl;
import tw.com.leadtek.nhiwidget.service.LogOperateService;
import tw.com.leadtek.tools.DateUtil;
import tw.com.leadtek.tools.StringUtility;
import tw.com.leadtek.tools.VaildateUtil;

@Api(tags = "使用者行為匯出API", value = "使用者行為匯出API")
@CrossOrigin(origins = "*", maxAge = 36000)
@RestController
@RequestMapping(value = "/logOperate", produces = "application/json; charset=utf-8")
public class LogOperateController extends BaseController{
	
	@Autowired
	private LogOperateService logOperateService;
	
	@ApiOperation(value = "取得使用者行為匯出清單", notes = "取得使用者行為匯出清單")
	@GetMapping("/query")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<?> query(
			@ApiParam(name = "sdate", value = "起始日期，格式 yyyy/MM/dd",
			example = "2021/03/15") @RequestParam(required = false) String sdate,
			@ApiParam(name = "edate", value = "結束日期，格式 yyyy/MM/dd",
			example = "2021/03/18") @RequestParam(required = false) String edate,
			@ApiParam(name = "showType", value = "資料顯示方式，如：R(RANGE)，D(DAY)",
			example = "R") @RequestParam(required = true) String showType,
//			@ApiParam(name = "showInhClinicId", value = "列出就醫紀錄編號清單資訊",
//			example = "Y") @RequestParam(required = true) boolean showInhClinicId,
			@ApiParam(name = "actor", value = "角色行為項目，如：P(PRINCIPAL)，D(MEDICAL_STAFF)",
			example = "P") @RequestParam(required = true) String actor,
			@ApiParam(name = "pCondition", value = "負責人限縮條件，如：A(疾病分類 / 申報全體)，UN(負責人員帳號)，DN(負責人員姓名)",
			example = "A") @RequestParam(required = false) String pCondition,
			@ApiParam(name = "pUserNames", value = "負責人員帳號，若為多筆資料，用空格隔開",
			example = "leadtek FED_C") @RequestParam(required = false) String pUserNames,
			@ApiParam(name = "pDisplayNames", value = "負責人員姓名，若為多筆資料，用空格隔開",
			example = "王小明 蔡依依") @RequestParam(required = false) String pDisplayNames,
			@ApiParam(name = "msCondition", value = "醫護行為限縮條件，如：A(醫護全體)，D(科別)，DN(醫護姓名)",
			example = "A") @RequestParam(required = false) String msCondition,
			@ApiParam(name = "msDepts", value = "負責人員姓名，若為多筆資料，用空格隔開",
			example = "35 400") @RequestParam(required = false) String msDepts,
			@ApiParam(name = "msDisplayNames", value = "負責人員姓名，若為多筆資料，用空格隔開",
			example = "王小明 蔡依依") @RequestParam(required = false) String msDisplayNames,
			@ApiParam(name = "showLogTypes", value = "顯示列表，\nSG=登出入時間/系統登入總時數、"              + 
			                                                "\nIP=資料匯入時間、       "                +
			                                                "\nEP=資料匯出筆數/時間、      "              +
			                                                "\nFG=申請密碼清單/累計次數、"                 +
			                                                "\nAC=使用者操作紀錄log、"                   +
			                                                "\nCW=比對警示待確認案件數、"                  +
			                                                "\nDM=疑問標示案件通知數/時間(現有疑問標示總案件數)、"+
			                                                "\nEC=評估不調整案件數(評估不調整總案件數)、"      +
			                                                "\nOF=優化完成案件數(疑問優化完成總案件數)、"      +
			                                                "\nUR=未讀取次數紀錄、"                      +
			                                                "\nBN=被通知次數紀錄，若為多筆資料，"            +
			                                                "\n用空格隔開",
			example = "SG BN") @RequestParam(required = true) String showLogTypes) throws Exception {
		
		ResponseEntity<?> errorResponse = this.validateForQuery(sdate, edate, showType, actor, pCondition, pUserNames, pDisplayNames, msCondition, msDepts, msDisplayNames, showLogTypes);
		
		if(null != errorResponse) {
			
			return errorResponse;
		}
		
	     Map<String, Object> result = logOperateService.query(sdate, edate, showType, actor, pCondition, pUserNames, pDisplayNames, msCondition, msDepts, msDisplayNames, showLogTypes);
	    
	    
		return ResponseEntity.ok(this.toBody(result));
	}
	
	@ApiOperation(value = "匯出CSV檔", notes = "匯出CSV檔")
	@GetMapping("/exportCSV")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<?> exportCSV(
			@ApiParam(name = "sdate", value = "起始日期，格式 yyyy/MM/dd",
			example = "2021/03/15") @RequestParam(required = false) String sdate,
			@ApiParam(name = "edate", value = "結束日期，格式 yyyy/MM/dd",
			example = "2021/03/18") @RequestParam(required = false) String edate,
			@ApiParam(name = "showType", value = "資料顯示方式，如：R(RANGE)，D(DAY)",
			example = "R") @RequestParam(required = true) String showType,
			@ApiParam(name = "showInhClinicId", value = "列出就醫紀錄編號清單資訊",
			example = "Y") @RequestParam(required = true) boolean showInhClinicId,
			@ApiParam(name = "actor", value = "角色行為項目，如：P(PRINCIPAL)，D(MEDICAL_STAFF)",
			example = "P") @RequestParam(required = true) String actor,
			@ApiParam(name = "pCondition", value = "負責人限縮條件，如：A(疾病分類 / 申報全體)，UN(負責人員帳號)，DN(負責人員姓名)",
			example = "A") @RequestParam(required = false) String pCondition,
			@ApiParam(name = "pUserNames", value = "負責人員帳號，若為多筆資料，用空格隔開",
			example = "leadtek FED_C") @RequestParam(required = false) String pUserNames,
			@ApiParam(name = "pDisplayNames", value = "負責人員姓名，若為多筆資料，用空格隔開",
			example = "王小明 蔡依依") @RequestParam(required = false) String pDisplayNames,
			@ApiParam(name = "msCondition", value = "醫護行為限縮條件，如：A(醫護全體)，D(科別)，DN(醫護姓名)",
			example = "A") @RequestParam(required = false) String msCondition,
			@ApiParam(name = "msDepts", value = "負責人員姓名，若為多筆資料，用空格隔開",
			example = "35 400") @RequestParam(required = false) String msDepts,
			@ApiParam(name = "msDisplayNames", value = "負責人員姓名，若為多筆資料，用空格隔開",
			example = "王小明 蔡依依") @RequestParam(required = false) String msDisplayNames,
			@ApiParam(name = "showLogTypes", value = "顯示列表，\nSG=登出入時間/系統登入總時數、"              + 
			                                                "\nIP=資料匯入時間、       "                +
			                                                "\nEP=資料匯出筆數/時間、      "              +
			                                                "\nFG=申請密碼清單/累計次數、"                 +
			                                                "\nAC=使用者操作紀錄log、"                   +
			                                                "\nCW=比對警示待確認案件數、"                  +
			                                                "\nDM=疑問標示案件通知數/時間(現有疑問標示總案件數)、"+
			                                                "\nEC=評估不調整案件數(評估不調整總案件數)、"      +
			                                                "\nOF=優化完成案件數(疑問優化完成總案件數)、"      +
			                                                "\nUR=未讀取次數紀錄、"                      +
			                                                "\nBN=被通知次數紀錄，若為多筆資料，"            +
			                                                "\n用空格隔開",
			example = "SG BN") @RequestParam(required = true) String showLogTypes) throws Exception {
		
		
		ResponseEntity<?> errorResponse = this.validateForQuery(sdate, edate, showType, actor, pCondition, pUserNames, pDisplayNames, msCondition, msDepts, msDisplayNames, showLogTypes);
		
		if(null != errorResponse) {
			
			return errorResponse;
		}
		
	     Map<String, Object> result = logOperateService.query(sdate, edate, showType, actor, pCondition, pUserNames, pDisplayNames, msCondition, msDepts, msDisplayNames, showLogTypes);
	     
	     final String fileName = "UserReport.zip";
	     
	     try(InputStream in = logOperateService.exportCSV(result, fileName, showInhClinicId)){
	    	 
	    	 InputStreamResource resource = new InputStreamResource(in);

		     return ResponseEntity.ok()
		    		 .contentType(MediaType.APPLICATION_OCTET_STREAM)
		             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") +"\"")
		             .body(resource);
	     }
	}
	
	private ResponseEntity<?> validateForQuery(String sdate         , String edate      , String showType  , 
			                                   String actor         , String pCondition , String pUserNames, 
			                                   String pDisplayNames , String msCondition, String msDepts   , 
			                                   String msDisplayNames, String showLogTypes) throws ParseException {
		
		LogOperateResponse result = new LogOperateResponse();
		result.setResult(BaseResponse.ERROR);
		
		UserDetailsImpl user = super.getUserDetails();
	    
	    if (user == null) {
	    	result.setMessage("無法取得登入狀態");
	    	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
	    }
		
	    if((StringUtils.isNotBlank(sdate) && !VaildateUtil.isDate(sdate))||
	       (StringUtils.isNotBlank(edate) && !VaildateUtil.isDate(edate))) {
	    	
	    	result.setMessage("日期格式有誤");
    		return ResponseEntity.badRequest().body(result);
    		
	    }else if((StringUtils.isNotBlank(sdate) && VaildateUtil.isDate(sdate))&&
	 	         (StringUtils.isNotBlank(edate) && VaildateUtil.isDate(edate))){
	    	
	    	Date sDate = DateUtil.yyyy$MM$dd.convertToDate(sdate);
	    	
	    	Date eDate = DateUtil.yyyy$MM$dd.convertToDate(edate);
	    	
	    	if(sDate.getTime() > eDate.getTime()) {
	    		
	    		result.setMessage("啟始日不可大於結束日");
	    		return ResponseEntity.badRequest().body(result);
	    	}
	    }
	    
	    if("R".equalsIgnoreCase(showType)) {
	    	
	    }else if("D".equalsIgnoreCase(showType)) {
	    	
	    }else {
	    	result.setMessage("Invalid showType value ('"+showType+"')");
    		return ResponseEntity.badRequest().body(result);
	    }
	    
	    
	    if("P".equalsIgnoreCase(actor)) {
	    	
	    	
	    	if("A".equalsIgnoreCase(pCondition)) {
	    		
	    		
	    	}else if("DN".equalsIgnoreCase(pCondition)) {
	    		
	    		String[] displayNames = StringUtility.splitBySpace(pDisplayNames);
	    		
	    		if( 0 == displayNames.length) {
	    			
	    			result.setMessage("負責人員帳號不可為空");
		    		return ResponseEntity.badRequest().body(result);
	    		}
	    		
	    	}else if("UN".equalsIgnoreCase(pCondition)) {
	    		
	    		String[] userNames = StringUtility.splitBySpace(pUserNames);
	    		
	    		if( 0 == userNames.length) {
	    			
	    			result.setMessage("負責人員姓名不可為空");
		    		return ResponseEntity.badRequest().body(result);
	    		}
	    		
	    	}else {
	    		
	    		result.setMessage("Invalid pCondition value ('"+pCondition+"')");
	    		return ResponseEntity.badRequest().body(result);
	    	}
	    	
	    }else if("D".equalsIgnoreCase(actor)) {
	    	
	    	
	    	if("A".equalsIgnoreCase(msCondition)) {
	    		
	    		
	    	}else if("D".equalsIgnoreCase(msCondition)) {
	    		
	    		String[] depts = StringUtility.splitBySpace(msDepts);
	    		
	    		if( 0 == depts.length) {
	    			
	    			result.setMessage("科別不可為空");
		    		return ResponseEntity.badRequest().body(result);
	    		}
	    		
	    	}else if("DN".equalsIgnoreCase(msCondition)) {
	    		
	    		String[] displayNames = StringUtility.splitBySpace(msDisplayNames);
	    		
	    		if( 0 == displayNames.length) {
	    			
	    			result.setMessage("醫護姓名不可為空");
		    		return ResponseEntity.badRequest().body(result);
	    		}
	    		
	    	}else {
	    		
	    		result.setMessage("Invalid msCondition value ('"+msCondition+"')");
	    		return ResponseEntity.badRequest().body(result);
	    	}
	    	
	    }else {
	    	
	    	result.setMessage("Invalid actor value ('"+actor+"')");
    		return ResponseEntity.badRequest().body(result);
	    }
	    
	    {
	    	if( 0 ==StringUtility.splitBySpace(showLogTypes).length) {
	    		result.setMessage("請勾選需要顯示的報表種類");
	    		return ResponseEntity.badRequest().body(result);
	    	}
	    	
	    	
	    }
	    
	    return null;
	}

	@SuppressWarnings("all")
	private LogOperateResponse toBody(Map<String, Object> map) {
		
		LogOperateResponse result = new LogOperateResponse();
		
		 map.forEach((key, value) -> {
			 
			 if("SG".equalsIgnoreCase(key)) {
				 
				 result.setSgList((List<LogSigninDto>) value);
			 }
			 
			 if("FG".equalsIgnoreCase(key)) {
				 
				 result.setFgList((List<LogForgotPwdDto>) value);
			 }
			 
			 if("CW".equalsIgnoreCase(key)) {
				 
				 result.setCwList((List<LogMrDto>) value);
			 }
			 
			 if("DM".equalsIgnoreCase(key)) {
				 
				 result.setDmList((List<LogMrDto>) value);
			 }
			 
			 if("EC".equalsIgnoreCase(key)) {
				 
				 result.setEcList((List<LogMrDto>) value);
			 }
			 
			 if("OF".equalsIgnoreCase(key)) {
				 
				 result.setOfList((List<LogMrDto>) value);
			 }
			 
			 if("UR".equalsIgnoreCase(key)) {
				 
				 result.setUrList((List<LogMrDto>) value);
			 }
			 
			 if("BN".equalsIgnoreCase(key)) {
				 
				 result.setBnList((List<LogMrDto>) value);
			 }
			 
			 if("AC".equalsIgnoreCase(key)) {
				 
				 result.setAcList((List<LogActionDto>) value);
			 }
			 
			 if("IP".equalsIgnoreCase(key)) {
				 
				 result.setIpList((List<LogImportDto>) value);
			 }
			 
			 if("EP".equalsIgnoreCase(key)) {
				 
				 result.setEpList((List<LogExportDto>) value);
			 }
			 
		 });
		 
		return result;
	}
	
}
