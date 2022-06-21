package tw.com.leadtek.nhiwidget.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dao.OP_DDao;
import tw.com.leadtek.nhiwidget.payload.report.CaseStatusAndQuantity;
import tw.com.leadtek.tools.DateTool;

@Service
public class CaseStatusAndQuantityService {
	
	/*
	 * 案件狀態與各別數量
		狀態:
			-3: 疾病分類管理
			-2: 待確認
			-1: 疑問標示
			0: 待處理
			1: 無需變更
			2: 優化完成
			3: 評估不調整
	*/

	private static final Integer DISEASE_CLASSIFICATION_MANAGEMENT = -3;
	private static final Integer CONFIRM = -2;
	private static final Integer QUESTION_MARK = -1;
	private static final Integer BE_PROCESSED = 0;
	private static final Integer NO_NEED_CHANGE = 1;
	private static final Integer OPTIMIZATION_COMPLETE = 2;
	private static final Integer EVALUATION_NOT_ADJUST = 3;
	
	private Logger logger = LogManager.getLogger();
	
	@Autowired
	private OP_DDao opdDao;

	//案件狀態與各別數量數據
	public List<CaseStatusAndQuantity> getData(boolean physical,String status,String sMonth,String eMonth) {
		
		List<String>statusList=Arrays.asList(status.split(" "));
		List<CaseStatusAndQuantity> results=new ArrayList<CaseStatusAndQuantity>();
		
	  	DateFormat format1 = new SimpleDateFormat("yyyy/MM");
	  	DateFormat format2 = new SimpleDateFormat("yyyyMMdd");
		
		try {
			//開始時間
		  	Calendar startCal = Calendar.getInstance();
		  	startCal.setTime(format1.parse(sMonth));
		  	//結束時間
		  	Calendar endCal = Calendar.getInstance();
		  	endCal.setTime(format1.parse(eMonth));
		  	
		  	do {
		  		CaseStatusAndQuantity caseStatusAndQuantity=new CaseStatusAndQuantity();
		  		
		  		Calendar start = Calendar.getInstance();
		  		start.setTime(startCal.getTime());
		  		
		  		startCal.add(Calendar.MONTH, 1);
		  		
		  		Calendar end = Calendar.getInstance();
		  		end.setTime(startCal.getTime());

		  		StringBuilder startStr = new StringBuilder();
		  		StringBuilder endStr = new StringBuilder();
		  		
		  		startStr.append(DateTool.convertToChineseYear(format2.format(start.getTime())));
		  		endStr.append(DateTool.convertToChineseYear(format2.format(end.getTime())));
		  		
  				Map<String, Integer> statusMap=new HashMap<String, Integer>();
  				Map<String, String> physicalMap=new HashMap<String, String>();
		  		
		  		//取得案件狀態與各別數量數據
		  		List<Object[]> objs =opdDao.findTStatusCount(startStr.toString(), endStr.toString());
		  		
		  		for(int i=0;i<objs.size();i++) {
		  			if(objs.get(i)[0]!=null && objs.get(i)[1]!=null) {
		  				Integer statusID=Integer.valueOf(objs.get(i)[0].toString());
		  				Integer value=Integer.valueOf(objs.get(i)[1].toString());
		  				
		  				if(statusID==DISEASE_CLASSIFICATION_MANAGEMENT && statusList.contains("疾病分類管理")) {
		  					statusMap.put("疾病分類管理", value);
		  				}
		  				else if(statusID==CONFIRM && statusList.contains("待確認")) {
		  					statusMap.put("待確認", value);
		  				}
		  				else if(statusID==QUESTION_MARK && statusList.contains("疑問標示")) {
		  					statusMap.put("疑問標示", value);
		  				}
		  				else if(statusID==BE_PROCESSED && statusList.contains("待處理")) {
		  					statusMap.put("待處理", value);
		  				}
		  				else if(statusID==NO_NEED_CHANGE && statusList.contains("無須變更")) {
		  					statusMap.put("無須變更", value);
		  				}
		  				else if(statusID==OPTIMIZATION_COMPLETE && statusList.contains("優化完成")) {
		  					statusMap.put("優化完成", value);
		  				}
		  				else if(statusID==EVALUATION_NOT_ADJUST && statusList.contains("評估不調整")) {
		  					statusMap.put("評估不調整", value);
		  				}
		  			}
		  			
		  		}
		  		
				//包含就醫清單
				if(physical) {
					
				}
				
				caseStatusAndQuantity.setCalculateMonth(format1.format(start.getTime()));
				caseStatusAndQuantity.setStatusMap(statusMap);
				caseStatusAndQuantity.setPhysicalMap(physicalMap);
				results.add(caseStatusAndQuantity);
		  	}
		  	while(!startCal.after(endCal));
		  	
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			logger.info("CaseStatusAndQuantity service error {}",e);
			e.printStackTrace();
		}
		
		return results;
	}
}
