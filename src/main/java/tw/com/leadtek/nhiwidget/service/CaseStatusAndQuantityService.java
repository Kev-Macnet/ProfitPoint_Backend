package tw.com.leadtek.nhiwidget.service;

import java.io.File;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dao.IP_DDao;
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
	
	@Autowired
	private IP_DDao ipdDao;
	
	public final static String FILE_PATH = "download";

	//案件狀態與各別數量數據
	public CaseStatusAndQuantity getData(boolean physical,String status,String sDate,String eDate) {
		
		List<String>statusList=Arrays.asList(status.split(" "));
		CaseStatusAndQuantity caseStatusAndQuantity=new CaseStatusAndQuantity();
	  	
		try {
			
			DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
			
		  	//開始時間
		  	Date startDate=format1.parse(sDate);
		  	//結束時間
		  	Date endDate=format1.parse(eDate);
		  	
		  	System.out.println("開始時間(西元): "+sDate+" 結束時間(西元): "+eDate);

//	  		StringBuilder startStr = new StringBuilder();
//	  		StringBuilder endStr = new StringBuilder();
//	  		
//	  		//西元轉民國
//	  		startStr.append(DateTool.convertToChineseYear(format1.format(startCal.getTime())));
//	  		endStr.append(DateTool.convertToChineseYear(format1.format(endCal.getTime())));
//	  		
//	  		System.out.println("開始時間(民國): "+startStr.toString()+" 結束時間(民國): "+endStr.toString()+"\n");
	  		
			Map<String, Integer> statusMap=new HashMap<String, Integer>();
			Map<String, List<String>> physicalMap=new HashMap<String, List<String>>();
	  		
	  		//取得案件狀態與各別數量數據
	  		List<Object[]> objs =opdDao.findAllStatusCount(startDate,endDate);
	  		
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
				
				for(int i=0;i<statusList.size();i++) {
					physicalMap.put(statusList.get(i),new ArrayList<String>());
				}
				
				List<Object[]> physicalAll=opdDao.findAllPhysical(startDate, endDate);
				
		  		for(int i=0;i<physicalAll.size();i++) {
		  			if(physicalAll.get(i)[0]!=null && physicalAll.get(i)[1]!=null) {
		  				Integer statusID=Integer.valueOf(physicalAll.get(i)[0].toString());
		  				String INH_CLINIC_ID=physicalAll.get(i)[1].toString();
		  				String statusCHI="";
		  				
		  				if(statusID==DISEASE_CLASSIFICATION_MANAGEMENT && statusList.contains("疾病分類管理")) {
		  					statusCHI="疾病分類管理";
		  				}
		  				else if(statusID==CONFIRM && statusList.contains("待確認")) {
		  					statusCHI="待確認";
		  				}
		  				else if(statusID==QUESTION_MARK && statusList.contains("疑問標示")) {
		  					statusCHI="疑問標示";
		  				}
		  				else if(statusID==BE_PROCESSED && statusList.contains("待處理")) {
		  					statusCHI="待處理";
		  				}
		  				else if(statusID==NO_NEED_CHANGE && statusList.contains("無須變更")) {
		  					statusCHI="無須變更";
		  				}
		  				else if(statusID==OPTIMIZATION_COMPLETE && statusList.contains("優化完成")) {
		  					statusCHI="優化完成";
		  				}
		  				else if(statusID==EVALUATION_NOT_ADJUST && statusList.contains("評估不調整")) {
		  					statusCHI="評估不調整";
		  				}
		  				
		  				if(!statusCHI.equals("")) {
			  				List<String>clinic_idList= physicalMap.get(statusCHI);
			  				clinic_idList.add(INH_CLINIC_ID);
			  				physicalMap.put(statusCHI,clinic_idList);
		  				}
		  			}
		  		}
			}
			
			caseStatusAndQuantity.setStartDate(format1.format(startDate));
			caseStatusAndQuantity.setEndDate(format1.format(endDate));
			caseStatusAndQuantity.setStatusMap(statusMap);
			caseStatusAndQuantity.setPhysicalMap(physicalMap);
		  	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("CaseStatusAndQuantity service error {}",e);
			e.printStackTrace();
		}
		
		return caseStatusAndQuantity;
	}
	
	//案件狀態與各別數量 - 匯出
	public void getDataExport(boolean physical,CaseStatusAndQuantity result,String startDate,String endDate,HttpServletResponse response) {
		
		try {
			
			// 建立新工作簿
			HSSFWorkbook workbook = new HSSFWorkbook();
		  
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			
			HSSFCellStyle cellStyle_left = workbook.createCellStyle();
			cellStyle_left.setAlignment(HorizontalAlignment.LEFT);
			cellStyle_left.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle_left.setBorderBottom(BorderStyle.THIN);
			cellStyle_left.setBorderLeft(BorderStyle.THIN);
			cellStyle_left.setBorderRight(BorderStyle.THIN);
			cellStyle_left.setBorderTop(BorderStyle.THIN);
			
			HSSFCellStyle cellStyle_noBorder = workbook.createCellStyle();
			cellStyle_noBorder.setAlignment(HorizontalAlignment.CENTER);
			cellStyle_noBorder.setVerticalAlignment(VerticalAlignment.CENTER);

			Font font = workbook.createFont();
			font.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
			
			/* 新建工作表
			 *  案件狀態與各別數量
			 *  */
			HSSFSheet statusAndQuantitySheet = workbook.createSheet("案件狀態與各別數量");	
			
			StringBuffer calculateDate=new StringBuffer();
			calculateDate.append(startDate);
			calculateDate.append("至");
			calculateDate.append(endDate);
			
			//title1
			HSSFRow row_title = statusAndQuantitySheet.createRow(0);
			statusAndQuantitySheet.addMergedRegion(new CellRangeAddress(0,0,0,1));
			statusAndQuantitySheet.addMergedRegion(new CellRangeAddress(0,0,2,4));
			addRowCell(row_title, 0, "統計日期區間", cellStyle);
			addRowCell(row_title, 1, "", cellStyle);
			addRowCell(row_title, 2, calculateDate.toString(), cellStyle);
			addRowCell(row_title, 3, "", cellStyle);
			addRowCell(row_title, 4, "", cellStyle);
			
			//title2
			statusAndQuantitySheet.addMergedRegion(new CellRangeAddress(2,2,0,2));
			HSSFRow row_title2 = statusAndQuantitySheet.createRow(2);
			addRowCell(row_title2, 0, "案件狀態與各別數量(可複選)", cellStyle);
			addRowCell(row_title2, 1, "", cellStyle);
			addRowCell(row_title2, 2, "", cellStyle);
			
			int rowIndex=3;
			
			if(physical) {
				
				statusAndQuantitySheet.addMergedRegion(new CellRangeAddress(0,0,5,6));
				addRowCell(row_title, 5, "列出就醫清單", cellStyle);
				addRowCell(row_title, 6, "", cellStyle);
				
				Map<String, Integer> statusMap=result.getStatusMap();
				Map<String, List<String>> physicalMap=result.getPhysicalMap();
				
				//案件狀態與數量table
				HSSFRow row_statusTitle = statusAndQuantitySheet.createRow(rowIndex);
				HSSFRow row_statusValue = statusAndQuantitySheet.createRow(rowIndex+1);
				
				statusAndQuantitySheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,0,2));
				statusAndQuantitySheet.addMergedRegion(new CellRangeAddress(rowIndex+1,rowIndex+1,0,2));
				addRowCell(row_statusTitle, 0, "統計日期區間", cellStyle);
				addRowCell(row_statusTitle, 1, "", cellStyle);
				addRowCell(row_statusValue, 0, calculateDate.toString(), cellStyle);
				addRowCell(row_statusValue, 1, "", cellStyle);
				addRowCell(row_statusValue, 2, "", cellStyle);
				
				int cellIndex=3;
				for(Map.Entry<String, Integer> entry : statusMap.entrySet()) {
					statusAndQuantitySheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,cellIndex,cellIndex+1));
					statusAndQuantitySheet.addMergedRegion(new CellRangeAddress(rowIndex+1,rowIndex+1,cellIndex,cellIndex+1));
					
					addRowCell(row_statusTitle, cellIndex, entry.getKey(), cellStyle);
					addRowCell(row_statusTitle, cellIndex+1, "", cellStyle);
					
					addRowCell(row_statusValue, cellIndex, addThousandths(Long.valueOf(entry.getValue())), cellStyle);
					addRowCell(row_statusValue, cellIndex+1, "", cellStyle);
					
					cellIndex+=2;
				}
				
				//title3
				HSSFRow row_title3 = statusAndQuantitySheet.createRow(rowIndex+3);
				statusAndQuantitySheet.addMergedRegion(new CellRangeAddress(rowIndex+3,rowIndex+3,0,1));
				addRowCell(row_title3, 0, "案件狀態與編號", cellStyle);
				addRowCell(row_title3, 1, "", cellStyle);
				
				//title4
				HSSFRow row_title4 = statusAndQuantitySheet.createRow(rowIndex+4);
				statusAndQuantitySheet.addMergedRegion(new CellRangeAddress(rowIndex+4,rowIndex+4,0,1));
				addRowCell(row_title4, 0, "案件狀態(就醫清單)", cellStyle);
				addRowCell(row_title4, 1, "", cellStyle);
				statusAndQuantitySheet.addMergedRegion(new CellRangeAddress(rowIndex+4,rowIndex+4,2,14));
				for(int j=2;j<15;j++) {
					if(j==2) {
						addRowCell(row_title4, 2, "就醫編號紀錄", cellStyle);
					}
					else {
						addRowCell(row_title4, j, "", cellStyle);
					}
				}
				
				//就醫紀錄編號table
				int cellIndex2=5;
				for(Map.Entry<String, List<String>> entry : physicalMap.entrySet()) {
					
					List<String>pList=entry.getValue();
					StringBuilder physicals=new StringBuilder();
					for(int j=0;j<pList.size();j++) {
						if(j==pList.size()-1) {
							physicals.append(pList.get(j));
						}
						else {
							physicals.append(pList.get(j));
							physicals.append(",");
						}
					}
					
					statusAndQuantitySheet.addMergedRegion(new CellRangeAddress(rowIndex+cellIndex2,rowIndex+cellIndex2,0,1));
					statusAndQuantitySheet.addMergedRegion(new CellRangeAddress(rowIndex+cellIndex2,rowIndex+cellIndex2,2,14));
					
					HSSFRow row_physical = statusAndQuantitySheet.createRow(rowIndex+cellIndex2);
					
					addRowCell(row_physical, 0, entry.getKey(), cellStyle);
					addRowCell(row_physical, 1, "", cellStyle);
					addRowCell(row_physical, 2, physicals.toString(), cellStyle_left);
					for(int j=3;j<15;j++) {
						addRowCell(row_physical, j, "", cellStyle);
					}
					
					cellIndex2++;
				}
			}
			else {
					Map<String, Integer> statusMap=result.getStatusMap();
					
					//案件狀態與數量table
					HSSFRow row_statusTitle = statusAndQuantitySheet.createRow(rowIndex);
					HSSFRow row_statusValue = statusAndQuantitySheet.createRow(rowIndex+1);
					
					statusAndQuantitySheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,0,2));
					statusAndQuantitySheet.addMergedRegion(new CellRangeAddress(rowIndex+1,rowIndex+1,0,2));
					addRowCell(row_statusTitle, 0, "統計日期區間", cellStyle);
					addRowCell(row_statusTitle, 1, "", cellStyle);
					addRowCell(row_statusValue, 0, calculateDate.toString(), cellStyle);
					addRowCell(row_statusValue, 1, "", cellStyle);
					addRowCell(row_statusValue, 2, "", cellStyle);
					
					int cellIndex=3;
					for(Map.Entry<String, Integer> entry : statusMap.entrySet()) {
						statusAndQuantitySheet.addMergedRegion(new CellRangeAddress(rowIndex,rowIndex,cellIndex,cellIndex+1));
						statusAndQuantitySheet.addMergedRegion(new CellRangeAddress(rowIndex+1,rowIndex+1,cellIndex,cellIndex+1));
						
						addRowCell(row_statusTitle, cellIndex, entry.getKey(), cellStyle);
						addRowCell(row_statusTitle, cellIndex+1, "", cellStyle);
						
						addRowCell(row_statusValue, cellIndex, addThousandths(Long.valueOf(entry.getValue())), cellStyle);
						addRowCell(row_statusValue, cellIndex+1, "", cellStyle);
						
						cellIndex+=2;
					}
			}
			
		  //產生報表
			String fileNameStr = "案件狀態與各別數量" + "_" + calculateDate.toString();
			String fileName = URLEncoder.encode(fileNameStr, "UTF-8");
			String filepath = (System.getProperty("os.name").toLowerCase().startsWith("windows"))
					? FILE_PATH + "\\" + fileName
					: FILE_PATH + "/" + fileName;
			File file = new File(filepath);
			response.reset();
		    response.setHeader("Access-Control-Allow-Origin", "*");
		    response.setHeader("Access-Control-Allow-Methods", "*");
		    response.setHeader("Content-Disposition",
				"attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + ".csv");
		    response.setContentType("application/octet-stream;charset=utf8");

			workbook.write(response.getOutputStream());
			workbook.close();
			
	  } catch (Exception e) {
			// TODO: handle exception
		  logger.info("案件狀態與各別數量產生報表錯誤: {}",e);
		  e.printStackTrace();
	  }
	}
	
	  public void addRowCell(HSSFRow row,int num,String value,HSSFCellStyle cellStyle) {
			// 建立單元格,row已經確定了行號,列號作為引數傳遞給createCell(),第一列從0開始計算
			HSSFCell cell = row.createCell(num);
			// 設定單元格的值,即A1的值(第一行,第一列)
			cell.setCellValue(value);
		  if(cellStyle!=null) {
			  cell.setCellStyle(cellStyle);
		  }
	  }
	  
	//千分位
	public String addThousandths(Long num) {
		DecimalFormat df=new DecimalFormat("#,###");
		return df.format(num);
	}
}
