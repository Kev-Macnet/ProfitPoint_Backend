package tw.com.leadtek.nhiwidget.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.http.HttpStatus;
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
import tw.com.leadtek.nhiwidget.constant.ROLE_TYPE;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.my.MyTodoListResponse;
import tw.com.leadtek.nhiwidget.security.service.UserDetailsImpl;
import tw.com.leadtek.nhiwidget.service.ParametersService;
import tw.com.leadtek.tools.DateTool;

@Api(tags = "使用者行為匯出API", value = "使用者行為匯出API")
@CrossOrigin(origins = "*", maxAge = 36000)
@RestController
@RequestMapping(value = "/logOperate", produces = "application/json; charset=utf-8")
public class LogOperateController {
	
	@ApiOperation(value = "取得我的清單-待辦事項", notes = "取得我的清單-待辦事項")
	@GetMapping("/query")
	@LogDefender(value = {LogType.SIGNIN})
	public ResponseEntity<MyTodoListResponse> getMyTodo(
			@ApiParam(name = "sdate", value = "起始日期，格式 yyyy/MM/dd",
			example = "2021/03/15") @RequestParam(required = false) String sdate,
			@ApiParam(name = "edate", value = "結束日期，格式 yyyy/MM/dd",
			example = "2021/03/18") @RequestParam(required = false) String edate,
			@ApiParam(name = "showType", value = "就醫類別為門急診",
			example = "true") @RequestParam(required = false) Boolean isOp,
			@ApiParam(name = "isIp", value = "就醫類別為住院",
			example = "true") @RequestParam(required = false) Boolean isIp,
			@ApiParam(name = "funcType", value = "科別代碼，如：00(不分科)，01(家醫科)，02(內科)，03(外科)...",
			example = "03") @RequestParam(required = false) String funcType,
			@ApiParam(name = "funcTypec", value = "科別名稱，如：不分科、家醫科、內科、外科...",
			example = "家醫科") @RequestParam(required = false) String funcTypec,
			@ApiParam(name = "prsnId", value = "醫護代碼",
			example = "A123456789") @RequestParam(required = false) String prsnId,
			@ApiParam(name = "prsnName", value = "醫護姓名",
			example = "王小明") @RequestParam(required = false) String prsnName,
			@ApiParam(name = "applId", value = "負責人員代碼",
			example = "A123456789") @RequestParam(required = false) String applId,
			@ApiParam(name = "applName", value = "負責人員姓名",
			example = "王小明") @RequestParam(required = false) String applName,
			@ApiParam(value = "病歷狀態，0:待處理，-1:疑問標示",
			example = "0") @RequestParam(required = false) Integer status,
			@ApiParam(name = "orderBy",
			value = "排序欄位名稱，status:資料狀態，sdate:就醫日期-起，edate:就醫日期-訖，inhMrId:病歷號碼，name:患者姓名，"
					+ "inhClinicId:就醫記錄編號，funcType:科別代碼，funcTypec:科別，prsnId:醫護代碼，prsnName:醫護姓名，"
					+ "totalDot:病歷點數，applId:負責人員代碼，applName:負責人員",
					example = "sdate") @RequestParam(required = false) String orderBy,
			@ApiParam(name = "asc", value = "排序方式，true:由小至大，false:由大至小，空值表示不排序",
			example = "true") @RequestParam(required = false) Boolean asc,
			@ApiParam(name = "perPage", value = "每頁顯示筆數",
			example = "20") @RequestParam(required = false) Integer perPage,
			@ApiParam(name = "page", value = "頁碼，第一頁值為0，第二頁值為1…",
			example = "0") @RequestParam(required = false, defaultValue = "0") Integer page) {
//		int perPageInt = (perPage == null) ? parameters.getIntParameter(ParametersService.PAGE_COUNT)
//				: perPage.intValue();
//		
//		String column = orderBy;
//		if (column != null) {
//			if (column.equals("sdate")) {
//				column = "startDate";
//			} else if (column.equals("edate")) {
//				column = "endDate";
//			} else if (column.equals("totalDot")) {
//				column = "tDot";
//			} else if (!findDeclaredMethod("tw.com.leadtek.nhiwidget.model.rdb.MY_MR", column)) {
//				MyTodoListResponse result = new MyTodoListResponse();
//				result.setMessage("orderBy無此欄位：" + column);
//				result.setResult("failed");
//				return ResponseEntity.badRequest().body(result);
//			}
//		}
//		UserDetailsImpl user = getUserDetails();
//		if (user == null) {
//			MyTodoListResponse result = new MyTodoListResponse();
//			result.setMessage("無法取得登入狀態");
//			result.setResult(BaseResponse.ERROR);
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
//		}
//		if (ROLE_TYPE.DOCTOR.getRole().equals(user.getRole())) {
//			MyTodoListResponse result = new MyTodoListResponse();
//			result.setMessage("無權限");
//			result.setResult(BaseResponse.ERROR);
//			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
//		}
//		String dataFormat = null;
//		if (isOp != null && isOp.booleanValue()) {
//			if (isIp != null && isIp) {
//				// dataFormat = null;
//			} else {
//				dataFormat = "10";
//			}
//		} else if (isIp != null && isIp.booleanValue()) {
//			dataFormat = "20";
//		}
//		
//		Date startDate = null;
//		Date endDate = null;
//		
//		if (sdate != null && edate != null) {
//			SimpleDateFormat sdf = new SimpleDateFormat(DateTool.SDF);
//			try {
//				startDate = sdf.parse(sdate);
//				endDate = sdf.parse(edate);
//				if (startDate.after(endDate)) {
//					MyTodoListResponse result = new MyTodoListResponse();
//					result.setMessage("啟始日不可大於結束日");
//					result.setResult("failed");
//					return ResponseEntity.badRequest().body(result);
//				}
//			} catch (ParseException e) {
//				MyTodoListResponse result = new MyTodoListResponse();
//				result.setMessage("日期格式有誤");
//				result.setResult("failed");
//				return ResponseEntity.badRequest().body(result);
//			}
//		}
//		return ResponseEntity.ok(xmlService.getMyTodoList(user, startDate, endDate, dataFormat, 
//				funcType, funcTypec, prsnId, prsnName, applId, applName, status, column, asc, perPageInt, page));
		return null;
	}
}
