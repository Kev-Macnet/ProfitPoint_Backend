package tw.com.leadtek.nhiwidget.service;

import java.io.File;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.map.HashedMap;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.common.io.Files;

import tw.com.leadtek.nhiwidget.dao.ASSIGNED_POINTDao;
import tw.com.leadtek.nhiwidget.dao.CODE_TABLEDao;
import tw.com.leadtek.nhiwidget.dao.DRG_MONTHLYDao;
import tw.com.leadtek.nhiwidget.dao.DRG_WEEKLYDao;
import tw.com.leadtek.nhiwidget.dao.IP_DDao;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.IP_TDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_DDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dao.OP_TDao;
import tw.com.leadtek.nhiwidget.dao.POINT_MONTHLYDao;
import tw.com.leadtek.nhiwidget.dao.POINT_WEEKLYDao;
import tw.com.leadtek.nhiwidget.dto.ClassCaseCountDto;
import tw.com.leadtek.nhiwidget.dto.ClassDrugDotDto;
import tw.com.leadtek.nhiwidget.dto.ClassDrugFeeDto;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.model.rdb.POINT_MONTHLY;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.report.HealthCareCost;

/*
 * todo list:
 * 	健保藥費概況:
 * 		健保藥費變化優勢
 * */

@Service
public class HealthCareCostService {
	
	  private Logger logger = LogManager.getLogger();
	
	  /**
	   * 全部科別的科別代碼
	   */
	  public static final String FUNC_TYPE_ALL_NAME = "不分科";
	  
	  @Autowired
	  private CODE_TABLEDao code_TABLEDao;
	
	  @Autowired
	  private OP_DDao opdDao;
	
	  @Autowired
	  private OP_TDao optDao;
	
	  @Autowired
	  private IP_TDao iptDao;
	
	  @Autowired
	  private IP_DDao ipdDao;
	
	  @Autowired
	  private IP_PDao ippDao;
	
	  @Autowired
	  private OP_PDao oppDao;
	
	  @Autowired
	  private MRDao mrDao;
	
	  @Autowired
	  private ASSIGNED_POINTDao assignedPointDao;
	
	  @Autowired
	  private POINT_MONTHLYDao pointMonthlyDao;
	
	  @Autowired
	  private CodeTableService codeTableService;
	
	  @Autowired
	  private POINT_WEEKLYDao pointWeeklyDao;
	
	  @Autowired
	  private DRG_MONTHLYDao drgMonthlyDao;
	
	  @Autowired
	  private DRG_WEEKLYDao drgWeeklyDao;
	  
	  public final static String FILE_PATH = "download";
	  private List<CODE_TABLE> codeTableList;
	  
		//門急診/住院 1.病例總點數 2.總藥費 3.藥費佔率 4.案件數
	  private StringBuilder OP_AllDot=new StringBuilder();
	  private StringBuilder OP_AllDrugFee=new StringBuilder();
	  private StringBuilder OP_AllRate=new StringBuilder();
	  private StringBuilder AllCount=new StringBuilder();
		//門急診/住院總藥費差異
	  private StringBuilder FeeDiff=new StringBuilder();
		//門急診 1.病例總點數 2.總藥費 3.藥費佔率 4.案件數
	  private StringBuilder OP_Dot=new StringBuilder();
	  private StringBuilder OP_DrugFee=new StringBuilder();
	  private StringBuilder OP_Rate=new StringBuilder();
	  private StringBuilder OPCount=new StringBuilder();
		//住院 1.病例總點數 2.總藥費 3.藥費佔率 4.案件數
	  private StringBuilder IP_Dot=new StringBuilder();
	  private StringBuilder IP_DrugFee=new StringBuilder();
	  private StringBuilder IP_Rate=new StringBuilder();
	  private StringBuilder IPCount=new StringBuilder();
	  
	  private StringBuilder title=new StringBuilder();
	  
	  private DecimalFormat df = new DecimalFormat("######0.0000");
	  
	  public List<HealthCareCost> getData(String year,String season,List<HealthCareCost> results) {
		  
		  String[] seasons=null;
		  
		  if(season != null && !season.equals("") && !season.equals("null")){
			  seasons=season.split(" ");
		  }
			
			codeTableList=code_TABLEDao.findByCatOrderByCode("FUNC_TYPE");
			
			for(int i=0;i<seasons.length;i++) {
				
				List<String> seasonList=new ArrayList<String>();
				
				if(seasons[i].equals("Q1")) {
					seasonList.add(year+"01");
					seasonList.add(year+"02");
					seasonList.add(year+"03");
					results.add(statisticData(seasons[i],seasonList));
				}
				else if(seasons[i].equals("Q2")) {
					seasonList.add(year+"04");
					seasonList.add(year+"05");
					seasonList.add(year+"06");
					results.add(statisticData(seasons[i],seasonList));
				}
				else if(seasons[i].equals("Q3")) {
					seasonList.add(year+"07");
					seasonList.add(year+"08");
					seasonList.add(year+"09");
					results.add(statisticData(seasons[i],seasonList));
				}
				else if(seasons[i].equals("Q4")) {
					seasonList.add(year+"10");
					seasonList.add(year+"11");
					seasonList.add(year+"12");
					results.add(statisticData(seasons[i],seasonList));
				}
				else {
					  HealthCareCost healthCareCost=new HealthCareCost();
					  healthCareCost.setResult(BaseResponse.ERROR);
					  healthCareCost.setMessage("季度格式不正確");
					  results.add(healthCareCost);
				      return results;
				}
			}
		  
		  return results;
	  }
	  
	  public void getDataExport(String endDate,String season,List<HealthCareCost> results,HttpServletResponse response) {
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
				
				if(results.size()==1){
					/* 新建工作表
					 *  單季度-各科別藥費占比(門急診/住院、門急診、住院) 
					 *  */
					HSSFSheet drugFeeSheet = workbook.createSheet("單季度-各科別藥費占比(門急診-住院)");
					
					//門急診/住院
					String allDot=results.get(0).getAllDot(); //全院 病歷總點數
					String allDrugFee=results.get(0).getAllDrugFee(); //全院 總藥費
					String allRate=results.get(0).getAllRate();//全院 藥費占率
					String allCount=results.get(0).getAllCount();//全院 案件數
					List<ClassDrugDotDto> classAllDrugFee=results.get(0).getClassAll(); //各科總點數(藥費)
					List<ClassDrugDotDto> classAll_TDot=results.get(0).getClassAll_TDot();//各科別病歷總點數
					List<ClassDrugFeeDto>classAllDrugFeeRate=results.get(0).getClassAllFeeRate();//各科別藥費佔率
					List<ClassCaseCountDto>classAllCaseCount=results.get(0).getClassAllCaseCount();//各科別案件數
					
					//門急診
					String OPDot=results.get(0).getOP_Dot(); //全院 病歷總點數
					String OPDrugFee=results.get(0).getOP_DrugFee(); //全院 總藥費
					String OPRate=results.get(0).getOP_Rate();//全院 藥費占率
					String OPCount=results.get(0).getOPCount();//全院 案件數
					List<ClassDrugDotDto> classOPDrugFee=results.get(0).getClassOP(); //各科總點數(藥費)
					List<ClassDrugDotDto> classOP_TDot=results.get(0).getClassOP_TDot();//各科別病歷總點數
					List<ClassDrugFeeDto>classOPDrugFeeRate=results.get(0).getClassOPFeeRate();//各科別藥費佔率
					List<ClassCaseCountDto>classOPCaseCount=results.get(0).getClassOPCaseCount();//各科別案件數
					
					//住院
					String IPDot=results.get(0).getIP_Dot(); //全院 病歷總點數
					String IPDrugFee=results.get(0).getIP_DrugFee(); //全院 總藥費
					String IPRate=results.get(0).getIP_Rate();//全院 藥費占率
					String IPCount=results.get(0).getIPCount();//全院 案件數
					List<ClassDrugDotDto>classIPDrugFee=results.get(0).getClassIP(); //各科總點數(藥費)
					List<ClassDrugDotDto> classIP_TDot=results.get(0).getClassIP_TDot();//各科別病歷總點數
					List<ClassDrugFeeDto>classIPDrugFeeRate=results.get(0).getClassIPFeeRate();//各科別藥費佔率
					List<ClassCaseCountDto>classIPCaseCount=results.get(0).getClassIPCaseCount();//各科別案件數
					
					for(int i=0;i<20;i++){
							drugFeeSheet.addMergedRegion(new CellRangeAddress(i,i,0,2));
					}
					
					HSSFRow rowDF0 = drugFeeSheet.createRow(0);
					HSSFRow rowDF1 = drugFeeSheet.createRow(1);
					HSSFRow rowDF2 = drugFeeSheet.createRow(2);
					HSSFRow rowDF3 = drugFeeSheet.createRow(3);
					HSSFRow rowDF4 = drugFeeSheet.createRow(4);
					HSSFRow rowDF5 = drugFeeSheet.createRow(5);
					HSSFRow rowDF7 = drugFeeSheet.createRow(7);
					HSSFRow rowDF8 = drugFeeSheet.createRow(8);
					HSSFRow rowDF9 = drugFeeSheet.createRow(9);
					HSSFRow rowDF10 = drugFeeSheet.createRow(10);
					HSSFRow rowDF11 = drugFeeSheet.createRow(11);
					HSSFRow rowDF12 = drugFeeSheet.createRow(12);
					HSSFRow rowDF14 = drugFeeSheet.createRow(14);
					HSSFRow rowDF15 = drugFeeSheet.createRow(15);
					HSSFRow rowDF16 = drugFeeSheet.createRow(16);
					HSSFRow rowDF17 = drugFeeSheet.createRow(17);
					HSSFRow rowDF18 = drugFeeSheet.createRow(18);
					HSSFRow rowDF19 = drugFeeSheet.createRow(19);
					
					//各科別藥費佔比(門急診/住院)
					classDrugFeeTemplate(season,"各科別藥費佔比(門急診/住院)",rowDF0,rowDF1,rowDF2,rowDF3,rowDF4,
							rowDF5,cellStyle_left,allCount,classAllCaseCount,
							allDot,classAll_TDot,allDrugFee,classAllDrugFee,
							allRate,classAllDrugFeeRate);
					
					//各科別藥費佔比(門急診)
					classDrugFeeTemplate(season,"各科別藥費佔比(門急診)",rowDF7,rowDF8,rowDF9,rowDF10,rowDF11,
							rowDF12,cellStyle_left,OPCount,classOPCaseCount,
							OPDot,classOP_TDot,OPDrugFee,classOPDrugFee,
							OPRate,classOPDrugFeeRate);
					
					//各科別藥費佔比(住院)
					classDrugFeeTemplate(season,"各科別藥費佔比(住院)",rowDF14,rowDF15,rowDF16,rowDF17,rowDF18,
							rowDF19,cellStyle_left,IPCount,classIPCaseCount,
							IPDot,classIP_TDot,IPDrugFee,classIPDrugFee,
							IPRate,classIPDrugFeeRate);
					
					/*新建工作表
					 * 單季度-去年同期健保藥費相比差額(門急診/住院)
					 * */
					HSSFSheet drugFeeDiffSheet = workbook.createSheet("單季度-去年同期健保藥費相比差額");
					drugFeeDiffSheet.addMergedRegion(new CellRangeAddress(0,0,0,2));
					drugFeeDiffSheet.addMergedRegion(new CellRangeAddress(1,1,0,1));
					drugFeeDiffSheet.addMergedRegion(new CellRangeAddress(2,2,0,1));
					
					List<ClassDrugFeeDto> classAllFeeDiff=results.get(0).getClassAllFeeDiff();
					String AllFeeDiff=results.get(0).getAllFeeDiff();
					
					// 建立行,行號作為引數傳遞給createRow()方法,第一行從0開始計算
					HSSFRow rowDFD0 = drugFeeDiffSheet.createRow(0);
					HSSFRow rowDFD1 = drugFeeDiffSheet.createRow(1);
					HSSFRow rowDFD2 = drugFeeDiffSheet.createRow(2);
					
					addRowCell(rowDFD0,0,"去年同期健保藥費相比差額",cellStyle_noBorder);
					addRowCell(rowDFD1,0,"",cellStyle);
					addRowCell(rowDFD1,1,"",cellStyle);
					addRowCell(rowDFD1,2,"全院",cellStyle);
					addRowCell(rowDFD2, 0, "藥費總差額",cellStyle);
					addRowCell(rowDFD2, 2, AllFeeDiff,cellStyle);
					
					for(int i=0;i<classAllFeeDiff.size();i++) {
						addRowCell(rowDFD1 ,i+3,classAllFeeDiff.get(i).getDesc_chi(),cellStyle);
						addRowCell(rowDFD2,i+3, classAllFeeDiff.get(i).getFee(),cellStyle);
					}
					
					/*新建工作表 
					 * 單季度-健保藥費排序
					 * */
					HSSFSheet drugFeeSortsheet = workbook.createSheet("單季度-健保藥費排序");
				}
				else {
					
					season="Q1 Q2 Q3 Q4";
					
					/* 新建工作表
					 *  多季度-各科別藥費占比(門急診/住院)
					 *  */
					HSSFSheet drugFeeSheet_All = workbook.createSheet("多季度-各科別藥費占比(門急診-住院)");
					
					for(int i=0;i<24;i++){
						if(i==0 || i==6 || i==12 || i==18) {
							drugFeeSheet_All.addMergedRegion(new CellRangeAddress(i,i,0,4));
						}
						else {
							drugFeeSheet_All.addMergedRegion(new CellRangeAddress(i,i,0,2));
						}
					}
					
					HSSFRow rowDFA0 = drugFeeSheet_All.createRow(0);
					HSSFRow rowDFA1 = drugFeeSheet_All.createRow(1);
					HSSFRow rowDFA2 = drugFeeSheet_All.createRow(2);
					HSSFRow rowDFA3 = drugFeeSheet_All.createRow(3);
					HSSFRow rowDFA4 = drugFeeSheet_All.createRow(4);
					HSSFRow rowDFA5 = drugFeeSheet_All.createRow(5);
					HSSFRow rowDFA6 = drugFeeSheet_All.createRow(6);
					HSSFRow rowDFA7 = drugFeeSheet_All.createRow(7);
					HSSFRow rowDFA8 = drugFeeSheet_All.createRow(8);
					HSSFRow rowDFA9 = drugFeeSheet_All.createRow(9);
					HSSFRow rowDFA10 = drugFeeSheet_All.createRow(10);
					HSSFRow rowDFA11 = drugFeeSheet_All.createRow(11);
					HSSFRow rowDFA12 = drugFeeSheet_All.createRow(12);
					HSSFRow rowDFA13 = drugFeeSheet_All.createRow(13);
					HSSFRow rowDFA14 = drugFeeSheet_All.createRow(14);
					HSSFRow rowDFA15 = drugFeeSheet_All.createRow(15);
					HSSFRow rowDFA16 = drugFeeSheet_All.createRow(16);
					HSSFRow rowDFA17 = drugFeeSheet_All.createRow(17);
					HSSFRow rowDFA18 = drugFeeSheet_All.createRow(18);
					HSSFRow rowDFA19 = drugFeeSheet_All.createRow(19);
					HSSFRow rowDFA20 = drugFeeSheet_All.createRow(20);
					HSSFRow rowDFA21 = drugFeeSheet_All.createRow(21);
					HSSFRow rowDFA22 = drugFeeSheet_All.createRow(22);
					HSSFRow rowDFA23 = drugFeeSheet_All.createRow(23);
					
					/* 新建工作表
					 *  多季度-各科別藥費占比(門急診)
					 *  */
					HSSFSheet drugFeeSheet_OP = workbook.createSheet("多季度-各科別藥費占比(門急診)");
					
					for(int i=0;i<24;i++){
						if(i==0 || i==6 || i==12 || i==18) {
							drugFeeSheet_OP.addMergedRegion(new CellRangeAddress(i,i,0,4));
						}
						else {
							drugFeeSheet_OP.addMergedRegion(new CellRangeAddress(i,i,0,2));
						}
					}
					
					HSSFRow rowDFO0 = drugFeeSheet_OP.createRow(0);
					HSSFRow rowDFO1 = drugFeeSheet_OP.createRow(1);
					HSSFRow rowDFO2 = drugFeeSheet_OP.createRow(2);
					HSSFRow rowDFO3 = drugFeeSheet_OP.createRow(3);
					HSSFRow rowDFO4 = drugFeeSheet_OP.createRow(4);
					HSSFRow rowDFO5 = drugFeeSheet_OP.createRow(5);
					HSSFRow rowDFO6 = drugFeeSheet_OP.createRow(6);
					HSSFRow rowDFO7 = drugFeeSheet_OP.createRow(7);
					HSSFRow rowDFO8 = drugFeeSheet_OP.createRow(8);
					HSSFRow rowDFO9 = drugFeeSheet_OP.createRow(9);
					HSSFRow rowDFO10 = drugFeeSheet_OP.createRow(10);
					HSSFRow rowDFO11 = drugFeeSheet_OP.createRow(11);
					HSSFRow rowDFO12 = drugFeeSheet_OP.createRow(12);
					HSSFRow rowDFO13 = drugFeeSheet_OP.createRow(13);
					HSSFRow rowDFO14 = drugFeeSheet_OP.createRow(14);
					HSSFRow rowDFO15 = drugFeeSheet_OP.createRow(15);
					HSSFRow rowDFO16 = drugFeeSheet_OP.createRow(16);
					HSSFRow rowDFO17 = drugFeeSheet_OP.createRow(17);
					HSSFRow rowDFO18 = drugFeeSheet_OP.createRow(18);
					HSSFRow rowDFO19 = drugFeeSheet_OP.createRow(19);
					HSSFRow rowDFO20 = drugFeeSheet_OP.createRow(20);
					HSSFRow rowDFO21 = drugFeeSheet_OP.createRow(21);
					HSSFRow rowDFO22 = drugFeeSheet_OP.createRow(22);
					HSSFRow rowDFO23 = drugFeeSheet_OP.createRow(23);
					
					
					/* 新建工作表
					 *  多季度-各科別藥費占比(住院)
					 *  */
					HSSFSheet drugFeeSheet_IP = workbook.createSheet("多季度-各科別藥費占比(住院)");
					
					for(int i=0;i<24;i++){
						if(i==0 || i==6 || i==12 || i==18) {
							drugFeeSheet_IP.addMergedRegion(new CellRangeAddress(i,i,0,4));
						}
						else {
							drugFeeSheet_IP.addMergedRegion(new CellRangeAddress(i,i,0,2));
						}
					}
					
					HSSFRow rowDFI0 = drugFeeSheet_IP.createRow(0);
					HSSFRow rowDFI1 = drugFeeSheet_IP.createRow(1);
					HSSFRow rowDFI2 = drugFeeSheet_IP.createRow(2);
					HSSFRow rowDFI3 = drugFeeSheet_IP.createRow(3);
					HSSFRow rowDFI4 = drugFeeSheet_IP.createRow(4);
					HSSFRow rowDFI5 = drugFeeSheet_IP.createRow(5);
					HSSFRow rowDFI6 = drugFeeSheet_IP.createRow(6);
					HSSFRow rowDFI7 = drugFeeSheet_IP.createRow(7);
					HSSFRow rowDFI8 = drugFeeSheet_IP.createRow(8);
					HSSFRow rowDFI9 = drugFeeSheet_IP.createRow(9);
					HSSFRow rowDFI10 = drugFeeSheet_IP.createRow(10);
					HSSFRow rowDFI11 = drugFeeSheet_IP.createRow(11);
					HSSFRow rowDFI12 = drugFeeSheet_IP.createRow(12);
					HSSFRow rowDFI13 = drugFeeSheet_IP.createRow(13);
					HSSFRow rowDFI14 = drugFeeSheet_IP.createRow(14);
					HSSFRow rowDFI15 = drugFeeSheet_IP.createRow(15);
					HSSFRow rowDFI16 = drugFeeSheet_IP.createRow(16);
					HSSFRow rowDFI17 = drugFeeSheet_IP.createRow(17);
					HSSFRow rowDFI18 = drugFeeSheet_IP.createRow(18);
					HSSFRow rowDFI19 = drugFeeSheet_IP.createRow(19);
					HSSFRow rowDFI20 = drugFeeSheet_IP.createRow(20);
					HSSFRow rowDFI21 = drugFeeSheet_IP.createRow(21);
					HSSFRow rowDFI22 = drugFeeSheet_IP.createRow(22);
					HSSFRow rowDFI23 = drugFeeSheet_IP.createRow(23);
							
					/* 新建工作表
					 *  多季度-各科別藥費占比(住院)
					 *  */
					HSSFSheet drugFeeDiffSheet = workbook.createSheet("多季度-去年同期健保藥費相比差額");
					
					for(int i=0;i<12;i++){
						if(i==0 || i==3 || i==6 || i==9) {
							drugFeeDiffSheet.addMergedRegion(new CellRangeAddress(i,i,0,4));
						}
						else {
							drugFeeDiffSheet.addMergedRegion(new CellRangeAddress(i,i,0,2));
						}
					}
					HSSFRow rowD0 = drugFeeDiffSheet.createRow(0);
					HSSFRow rowD1 = drugFeeDiffSheet.createRow(1);
					HSSFRow rowD2 = drugFeeDiffSheet.createRow(2);
					HSSFRow rowD3 = drugFeeDiffSheet.createRow(3);
					HSSFRow rowD4 = drugFeeDiffSheet.createRow(4);
					HSSFRow rowD5 = drugFeeDiffSheet.createRow(5);
					HSSFRow rowD6 = drugFeeDiffSheet.createRow(6);
					HSSFRow rowD7 = drugFeeDiffSheet.createRow(7);
					HSSFRow rowD8 = drugFeeDiffSheet.createRow(8);
					HSSFRow rowD9 = drugFeeDiffSheet.createRow(9);
					HSSFRow rowD10 = drugFeeDiffSheet.createRow(10);
					HSSFRow rowD11 = drugFeeDiffSheet.createRow(11);
					
					for(int i=0;i<results.size();i++) {
						//門急診/住院
						String multi_allDot=results.get(i).getAllDot(); //全院 病歷總點數
						String multi_allDrugFee=results.get(i).getAllDrugFee(); //全院 總藥費
						String multi_allRate=results.get(i).getAllRate();//全院 藥費占率
						String multi_allCount=results.get(i).getAllCount();//全院 案件數
						List<ClassDrugDotDto> multi_classAllDrugFee=results.get(i).getClassAll(); //各科總點數(藥費)
						List<ClassDrugDotDto> multi_classAll_TDot=results.get(i).getClassAll_TDot();//各科別病歷總點數
						List<ClassDrugFeeDto>multi_classAllDrugFeeRate=results.get(i).getClassAllFeeRate();//各科別藥費佔率
						List<ClassCaseCountDto>multi_classAllCaseCount=results.get(i).getClassAllCaseCount();//各科別案件數
						
						//門急診
						String multi_OPDot=results.get(i).getOP_Dot(); //全院 病歷總點數
						String multi_OPDrugFee=results.get(i).getOP_DrugFee(); //全院 總藥費
						String multi_OPRate=results.get(i).getOP_Rate();//全院 藥費占率
						String multi_OPCount=results.get(i).getOPCount();//全院 案件數
						List<ClassDrugDotDto> multi_classOPDrugFee=results.get(i).getClassOP(); //各科總點數(藥費)
						List<ClassDrugDotDto> multi_classOP_TDot=results.get(i).getClassOP_TDot();//各科別病歷總點數
						List<ClassDrugFeeDto>multi_classOPDrugFeeRate=results.get(i).getClassOPFeeRate();//各科別藥費佔率
						List<ClassCaseCountDto>multi_classOPCaseCount=results.get(i).getClassOPCaseCount();//各科別案件數
						
						//住院
						String multi_IPDot=results.get(i).getIP_Dot(); //全院 病歷總點數
						String multi_IPDrugFee=results.get(i).getIP_DrugFee(); //全院 總藥費
						String multi_IPRate=results.get(i).getIP_Rate();//全院 藥費占率
						String multi_IPCount=results.get(i).getIPCount();//全院 案件數
						List<ClassDrugDotDto> multi_classIPDrugFee=results.get(i).getClassIP(); //各科總點數(藥費)
						List<ClassDrugDotDto> multi_classIP_TDot=results.get(i).getClassIP_TDot();//各科別病歷總點數
						List<ClassDrugFeeDto>multi_classIPDrugFeeRate=results.get(i).getClassIPFeeRate();//各科別藥費佔率
						List<ClassCaseCountDto>multi_classIPCaseCount=results.get(i).getClassIPCaseCount();//各科別案件數
						
						//與去年同期健保藥費相比差額
						List<ClassDrugFeeDto> multi_classAllFeeDiff=results.get(i).getClassAllFeeDiff();
						String multi_AllFeeDiff=results.get(i).getAllFeeDiff();
						
						switch (results.get(i).getSeason()) {
							case "Q1":
								classDrugFeeTemplate(results.get(i).getSeason(),"各科別藥費佔比(門急診/住院)",rowDFA0,rowDFA1,rowDFA2,rowDFA3,rowDFA4,
										rowDFA5,cellStyle_left,multi_allCount,multi_classAllCaseCount,
										multi_allDot,multi_classAll_TDot,multi_allDrugFee,multi_classAllDrugFee,
										multi_allRate,multi_classAllDrugFeeRate);
								classDrugFeeTemplate(results.get(i).getSeason(),"各科別藥費佔比(門急診)",rowDFO0,rowDFO1,rowDFO2,rowDFO3,rowDFO4,
										rowDFO5,cellStyle_left,multi_OPCount,multi_classOPCaseCount,
										multi_OPDot,multi_classOP_TDot,multi_OPDrugFee,multi_classOPDrugFee,
										multi_OPRate,multi_classOPDrugFeeRate);
								classDrugFeeTemplate(results.get(i).getSeason(),"各科別藥費佔比(住院)",rowDFI0,rowDFI1,rowDFI2,rowDFI3,rowDFI4,
										rowDFI5,cellStyle_left,multi_IPCount,multi_classIPCaseCount,
										multi_IPDot,multi_classIP_TDot,multi_IPDrugFee,multi_classIPDrugFee,
										multi_IPRate,multi_classIPDrugFeeRate);
								
								classDrugFeeDiffTemplate(results.get(i).getSeason(),"去年同期健保藥費相比差額",rowD0,rowD1,rowD2,
										  cellStyle_left,multi_classAllFeeDiff,multi_AllFeeDiff);
						
								break;
							case "Q2":
								classDrugFeeTemplate(results.get(i).getSeason(),"各科別藥費佔比(門急診/住院)",rowDFA6,rowDFA7,rowDFA8,rowDFA9,rowDFA10,
										rowDFA11,cellStyle_left,multi_allCount,multi_classAllCaseCount,
										multi_allDot,multi_classAll_TDot,multi_allDrugFee,multi_classAllDrugFee,
										multi_allRate,multi_classAllDrugFeeRate);
								classDrugFeeTemplate(results.get(i).getSeason(),"各科別藥費佔比(門急診)",rowDFO6,rowDFO7,rowDFO8,rowDFO9,rowDFO10,
										rowDFO11,cellStyle_left,multi_OPCount,multi_classOPCaseCount,
										multi_OPDot,multi_classOP_TDot,multi_OPDrugFee,multi_classOPDrugFee,
										multi_OPRate,multi_classOPDrugFeeRate);
								classDrugFeeTemplate(results.get(i).getSeason(),"各科別藥費佔比(住院)",rowDFI6,rowDFI7,rowDFI8,rowDFI9,rowDFI10,
										rowDFI11,cellStyle_left,multi_IPCount,multi_classIPCaseCount,
										multi_IPDot,multi_classIP_TDot,multi_IPDrugFee,multi_classIPDrugFee,
										multi_IPRate,multi_classIPDrugFeeRate);
								
								classDrugFeeDiffTemplate(results.get(i).getSeason(),"去年同期健保藥費相比差額",rowD3,rowD4,rowD5,
										  cellStyle_left,multi_classAllFeeDiff,multi_AllFeeDiff);
								break;
							case "Q3":
								classDrugFeeTemplate(results.get(i).getSeason(),"各科別藥費佔比(門急診/住院)",rowDFA12,rowDFA13,rowDFA14,rowDFA15,rowDFA16,
										rowDFA17,cellStyle_left,multi_allCount,multi_classAllCaseCount,
										multi_allDot,multi_classAll_TDot,multi_allDrugFee,multi_classAllDrugFee,
										multi_allRate,multi_classAllDrugFeeRate);
								classDrugFeeTemplate(results.get(i).getSeason(),"各科別藥費佔比(門急診)",rowDFO12,rowDFO13,rowDFO14,rowDFO15,rowDFO16,
										rowDFO17,cellStyle_left,multi_OPCount,multi_classOPCaseCount,
										multi_OPDot,multi_classOP_TDot,multi_OPDrugFee,multi_classOPDrugFee,
										multi_OPRate,multi_classOPDrugFeeRate);
								classDrugFeeTemplate(results.get(i).getSeason(),"各科別藥費佔比(住院)",rowDFI12,rowDFI13,rowDFI14,rowDFI15,rowDFI16,
										rowDFI17,cellStyle_left,multi_IPCount,multi_classIPCaseCount,
										multi_IPDot,multi_classIP_TDot,multi_IPDrugFee,multi_classIPDrugFee,
										multi_IPRate,multi_classIPDrugFeeRate);
								
								classDrugFeeDiffTemplate(results.get(i).getSeason(),"去年同期健保藥費相比差額",rowD6,rowD7,rowD8,
										  cellStyle_left,multi_classAllFeeDiff,multi_AllFeeDiff);
								break;
							case "Q4":
								classDrugFeeTemplate(results.get(i).getSeason(),"各科別藥費佔比(門急診/住院)",rowDFA18,rowDFA19,rowDFA20,rowDFA21,rowDFA22,
										rowDFA23,cellStyle_left,multi_allCount,multi_classAllCaseCount,
										multi_allDot,multi_classAll_TDot,multi_allDrugFee,multi_classAllDrugFee,
										multi_allRate,multi_classAllDrugFeeRate);
								classDrugFeeTemplate(results.get(i).getSeason(),"各科別藥費佔比(門急診)",rowDFO18,rowDFO19,rowDFO20,rowDFO21,rowDFO22,
										rowDFO23,cellStyle_left,multi_OPCount,multi_classOPCaseCount,
										multi_OPDot,multi_classOP_TDot,multi_OPDrugFee,multi_classOPDrugFee,
										multi_OPRate,multi_classOPDrugFeeRate);
								classDrugFeeTemplate(results.get(i).getSeason(),"各科別藥費佔比(住院)",rowDFI18,rowDFI19,rowDFI20,rowDFI21,rowDFI22,
										rowDFI23,cellStyle_left,multi_IPCount,multi_classIPCaseCount,
										multi_IPDot,multi_classIP_TDot,multi_IPDrugFee,multi_classIPDrugFee,
										multi_IPRate,multi_classIPDrugFeeRate);
								
								classDrugFeeDiffTemplate(results.get(i).getSeason(),"去年同期健保藥費相比差額",rowD9,rowD10,rowD11,
										  cellStyle_left,multi_classAllFeeDiff,multi_AllFeeDiff);
								break;
							default:
								break;
						}
					}
					
			  }
		  
			  //產生報表
				String fileNameStr = "健保藥費概況" + "_" + endDate+"_"+season.replaceAll("\\s", "_");
				String fileName = URLEncoder.encode(fileNameStr, "UTF-8");
				String filepath = (System.getProperty("os.name").toLowerCase().startsWith("windows"))
						? FILE_PATH + "\\" + fileName
						: FILE_PATH + "/" + fileName;
				File file = new File(filepath);
				response.reset();
				response.setHeader("Content-Disposition",
						"attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + ".csv");
				response.setContentType("application/vnd.ms-excel;charset=utf8");

				workbook.write(response.getOutputStream());
				workbook.close();
		  } catch (Exception e) {
				// TODO: handle exception
			  logger.info("健保藥費概況產生報表錯誤: {}",e);
			  System.out.println(e);
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
	  
	  public void classDrugFeeDiffTemplate(String season,String str,HSSFRow row0,HSSFRow row1,HSSFRow row2,
			  HSSFCellStyle cellStyle_left,List<ClassDrugFeeDto> multi_classAllFeeDiff,String multi_AllFeeDiff) {
		  
		  	title.setLength(0);
		  	title.append(season+str);
		  	
			addRowCell(row0,0,title.toString(),null);
			addRowCell(row1,0,"",cellStyle_left);
			addRowCell(row1,1,"",cellStyle_left);
			addRowCell(row1,2,"",cellStyle_left);
			addRowCell(row1,3,"全院",cellStyle_left);
			addRowCell(row2,0, "藥費總差額",cellStyle_left);
			addRowCell(row2,1,"",cellStyle_left);
			addRowCell(row2,2,"",cellStyle_left);
			addRowCell(row2,3, multi_AllFeeDiff,cellStyle_left);
			
			for(int x=0;x<multi_classAllFeeDiff.size();x++) {
				addRowCell(row1 ,x+4,multi_classAllFeeDiff.get(x).getDesc_chi(),cellStyle_left);
				addRowCell(row2,x+4, multi_classAllFeeDiff.get(x).getFee(),cellStyle_left);
			}
	  }
	  
	  public void classDrugFeeTemplate(String season,String str,HSSFRow row0,HSSFRow row1,HSSFRow row2,HSSFRow row3,HSSFRow row4,
			  HSSFRow row5,HSSFCellStyle cellStyle_left,String caseCount,List<ClassCaseCountDto>classCaseCount,
			  String dot,List<ClassDrugDotDto>classTDot,String drugFee,List<ClassDrugDotDto>classDrugFee,
			  String drugFeeRate,List<ClassDrugFeeDto>classDrugFeeRate) {
		    
		  	title.setLength(0);
		  	title.append(season+str);
		  
			addRowCell(row0,0,title.toString(),null);
			addRowCell(row1, 3, "全院", cellStyle_left);
			for(int i=0;i<3;i++) {
				addRowCell(row1, i, "", cellStyle_left);
			}
			
			addRowCell(row2,0,"案件數",cellStyle_left);
			for(int i=1;i<3;i++) {
				addRowCell(row2, i, "", cellStyle_left);
			}
			addRowCell(row2,3,caseCount,cellStyle_left);
			for(int i=0;i<classCaseCount.size();i++) {
				addRowCell(row2,i+4,classCaseCount.get(i).getCaseCount(),cellStyle_left);
			}
			
			addRowCell(row3,0,"病歷總點數(不含自費)",cellStyle_left);
			for(int i=1;i<3;i++) {
				addRowCell(row3, i, "", cellStyle_left);
			}
			addRowCell(row3,3,dot,cellStyle_left);
			for(int i=0;i<classTDot.size();i++) {
				addRowCell(row3,i+4,classTDot.get(i).getDot(),cellStyle_left);
			}
			
			addRowCell(row4,0,"總藥費(不含自費)",cellStyle_left);
			for(int i=1;i<3;i++) {
				addRowCell(row4, i, "", cellStyle_left);
			}
			addRowCell(row4, 3, drugFee, cellStyle_left);
			for(int i=0;i<classDrugFee.size();i++) {
				addRowCell(row1,i+4,classDrugFee.get(i).getDesc_chi(),cellStyle_left);
				addRowCell(row4,i+4,classDrugFee.get(i).getDot(),cellStyle_left);
			}
			
			addRowCell(row5,0,"藥費佔率",cellStyle_left);
			for(int i=1;i<3;i++) {
				addRowCell(row5, i, "", cellStyle_left);
			}
			for(int i=0;i<classDrugFeeRate.size();i++) {
				addRowCell(row5,i+4,classDrugFeeRate.get(i).getFee(),cellStyle_left);
			}
			addRowCell(row5,3,drugFeeRate,cellStyle_left);
	  }
	  
	  public HealthCareCost statisticData(String seasonStr,List<String> seasonList) {
		  
			// 各科別門急診/住院總藥品點數(總藥費)
			List<ClassDrugDotDto> classOP_AllList=initDotList();
			//各科別門急診/住院總藥費(去年同一季)
			List<ClassDrugFeeDto>lastDrugFee=initFeeList();
			//各科別門急診/住院總藥費差異
			List<ClassDrugFeeDto>classDrugFeeDiff=initFeeList();
			// 各科別門急診總藥品點數(總藥費)
			List<ClassDrugDotDto> classOPList=initDotList();
			// 各科別住院總藥品點數(總藥費)
			List<ClassDrugDotDto> classIPList=initDotList();
			//各科別門急診/住院總病歷點數
			List<ClassDrugDotDto> classAll_TDot=initDotList();
			//各科別門急診病例總點數
			List<ClassDrugDotDto> classOP_TDot=initDotList();
			//各科別住院總病歷點數
			List<ClassDrugDotDto> classIP_TDot=initDotList();
			//各科別門急診/住院藥費佔率
			List<ClassDrugFeeDto> classAllDrugFeeRate=initFeeList();
			//各科別門急診藥費佔率
			List<ClassDrugFeeDto> classOPDrugFeeRate=initFeeList();
			//各科別住院藥費佔率
			List<ClassDrugFeeDto> classIPDrugFeeRate=initFeeList();
			//各科別門急診/住院案件
			List<ClassCaseCountDto> classAllCaseCount=initCaseList();
			//各科別門急診案件
			List<ClassCaseCountDto> classOPCaseCount=initCaseList();
			//各科別住院案件
			List<ClassCaseCountDto> classIPCaseCount=initCaseList();
			
			OP_AllDot.setLength(0);
			AllCount.setLength(0);
			OP_AllDrugFee.setLength(0);
			OP_AllRate.setLength(0);
			FeeDiff.setLength(0);
			OP_Dot.setLength(0);
			OPCount.setLength(0);
			OP_DrugFee.setLength(0);
			OP_Rate.setLength(0);
			IP_Dot.setLength(0);
			IPCount.setLength(0);
			IP_DrugFee.setLength(0);
			IP_Rate.setLength(0);
		  
			try {
				//門急診/住院 1.病例總點數 2.總藥費 3.藥費佔率 4.案件數
				String allD=opdDao.findTDot(optDao.findByFeeYmListOrderById(seasonList),iptDao.findByFeeYmListOrderById(seasonList));
				if(allD!=null){
					OP_AllDot.setLength(0);
					OP_AllDot.append(allD);
				}
				else {
					OP_AllDot.setLength(0);
					OP_AllDot.append("0");
				}
				String tDrugFee=opdDao.findTDrugFee(optDao.findByFeeYmListOrderById(seasonList),iptDao.findByFeeYmListOrderById(seasonList));
				if(tDrugFee!=null) {
					OP_AllDrugFee.setLength(0);
					OP_AllDrugFee.append(tDrugFee);
				}
				else {
					OP_AllDrugFee.setLength(0);
					OP_AllDrugFee.append("0");
				}
				OP_AllRate.append(df.format(checkDoubleNull(OP_AllDrugFee.toString())/checkDoubleNull(OP_AllDot.toString())));
				AllCount.append(opdDao.findTCount(optDao.findByFeeYmListOrderById(seasonList),iptDao.findByFeeYmListOrderById(seasonList)));
				
				//門急診 1.病例總點數 2.總藥費 3.藥費佔率 4.案件數
				String opD=opdDao.findOPDot(optDao.findByFeeYmListOrderById(seasonList));
				if(opD!=null) {
					OP_Dot.setLength(0);
					OP_Dot.append(opD);
				}
				else {
					OP_Dot.setLength(0);
					OP_Dot.append("0");
				}
				String opDrugFee=opdDao.findOPDrugFee(optDao.findByFeeYmListOrderById(seasonList));
				if(opDrugFee!=null) {
					OP_DrugFee.setLength(0);
					OP_DrugFee.append(opDrugFee);
				}
				else {
					OP_DrugFee.setLength(0);
					OP_DrugFee.append("0");
				}
				OP_Rate.append(df.format(checkDoubleNull(OP_DrugFee.toString())/checkDoubleNull(OP_Dot.toString())));
				OPCount.append(opdDao.findOPCount(optDao.findByFeeYmListOrderById(seasonList)));
				
				//住院 1.病例總點數 2.總藥費 3.藥費佔率 4.案件數
				String ipD=ipdDao.findIPDot(iptDao.findByFeeYmListOrderById(seasonList));
				if(ipD!=null) {
					IP_Dot.setLength(0);
					IP_Dot.append(ipD);
				}
				else {
					IP_Dot.setLength(0);
					IP_Dot.append("0");
				}
				String ipDrugFee=ipdDao.findIPDrugFee(iptDao.findByFeeYmListOrderById(seasonList));
				if(ipDrugFee!=null) {
					IP_DrugFee.setLength(0);
					IP_DrugFee.append(ipDrugFee);
				}
				else {
					IP_DrugFee.setLength(0);
					IP_DrugFee.append("0");
				}
				IP_Rate.append(df.format(checkDoubleNull(IP_DrugFee.toString())/checkDoubleNull(IP_Dot.toString())));
				IPCount.append(ipdDao.findIPCount(iptDao.findByFeeYmListOrderById(seasonList)));
				
				//各科別門急診總病歷點數
				List<Object[]>OP_ClassTDot=opdDao.findClassOP_TDot(optDao.findByFeeYmListOrderById(seasonList));
				//各科別住院總病歷點數
				List<Object[]>IP_ClassTDot=ipdDao.findClassIP_TDot(iptDao.findByFeeYmListOrderById(seasonList));
				
				for(int a=0;a<classOP_TDot.size();a++) {
					String code=classOP_TDot.get(a).getCode();
					ClassDrugDotDto classDrugDotDto=classOP_TDot.get(a);
					
					for(int b=0;b<OP_ClassTDot.size();b++) {
						if(code.equals(OP_ClassTDot.get(b)[0].toString())) {
							if(OP_ClassTDot.get(b)[1]!=null) {
								classDrugDotDto.setDot(OP_ClassTDot.get(b)[1].toString());
							}
							classOP_TDot.set(a, classDrugDotDto);
						}
					}
				}
				
				for(int a=0;a<classIP_TDot.size();a++) {
					String code=classIP_TDot.get(a).getCode();
					ClassDrugDotDto classDrugDotDto=classIP_TDot.get(a);
					
					for(int b=0;b<IP_ClassTDot.size();b++) {
						if(code.equals(IP_ClassTDot.get(b)[0].toString())) {
							if(IP_ClassTDot.get(b)[1]!=null) {
								classDrugDotDto.setDot(IP_ClassTDot.get(b)[1].toString());
							}
							classIP_TDot.set(a, classDrugDotDto);
						}
					}
				}
				
				//各科別門急診/住院總病歷點數
				for(int a=0;a<classAll_TDot.size();a++) {
					String code=classAll_TDot.get(a).getCode();
					int num1=checkIntgerNull(classAll_TDot.get(a).getDot());
					ClassDrugDotDto classDrugDotDto=classAll_TDot.get(a);
					
					for(int b=0;b<OP_ClassTDot.size();b++) {
						if(code.equals(OP_ClassTDot.get(b)[0].toString())) {
							if(OP_ClassTDot.get(b)[1]!=null) {
								int num2=checkIntgerNull(OP_ClassTDot.get(b)[1].toString());
								int num3=num1+num2;
								classDrugDotDto.setDot(String.valueOf(num3));
							}
							classAll_TDot.set(a,classDrugDotDto);
						}
					}
				}
				
				for(int a=0;a<classAll_TDot.size();a++) {
					String code=classAll_TDot.get(a).getCode();
					int num1=checkIntgerNull(classAll_TDot.get(a).getDot());
					ClassDrugDotDto classDrugDotDto=classAll_TDot.get(a);
					
					for(int b=0;b<IP_ClassTDot.size();b++) {
						if(code.equals(IP_ClassTDot.get(b)[0].toString())) {
							if(IP_ClassTDot.get(b)[1]!=null) {
								int num2=checkIntgerNull(IP_ClassTDot.get(b)[1].toString());
								int num3=num1+num2;
								classDrugDotDto.setDot(String.valueOf(num3));
							}
							classAll_TDot.set(a,classDrugDotDto);
						}
					}
				}
				
				//取得各科別門急診總藥品點數
				List<Object[]>OP_ClassDrugDot=opdDao.findByOptIdAndGroupByFuncType(optDao.findByFeeYmListOrderById(seasonList));
				//取得各科別住院總藥品點數
				List<Object[]>IP_ClassDrugDot=ipdDao.findByIptIdAndGroupByFuncType(iptDao.findByFeeYmListOrderById(seasonList));
				
				//各科別門急診/住院總藥品點數
				for(int a=0;a<classOP_AllList.size();a++) {
					String code=classOP_AllList.get(a).getCode();
					int num1=checkIntgerNull(classOP_AllList.get(a).getDot());
					ClassDrugDotDto classDrugDotDto=classOP_AllList.get(a);
					
					for(int b=0;b<OP_ClassDrugDot.size();b++) {
						if(code.equals(OP_ClassDrugDot.get(b)[0].toString())) {
							if(OP_ClassDrugDot.get(b)[1]!=null) {
								int num2=checkIntgerNull(OP_ClassDrugDot.get(b)[1].toString());
								int num3=num1+num2;
								classDrugDotDto.setDot(String.valueOf(num3));
							}
							classOP_AllList.set(a, classDrugDotDto);
						}
					}
				}
				
				//各科別門急診總藥品點數
				for(int a=0;a<classOPList.size();a++) {
					String code=classOPList.get(a).getCode();
					ClassDrugDotDto classDrugDotDto=classOPList.get(a);
					
					for(int b=0;b<OP_ClassDrugDot.size();b++) {
						if(code.equals(OP_ClassDrugDot.get(b)[0].toString())) {	
							if(OP_ClassDrugDot.get(b)[1]!=null) {
								classDrugDotDto.setDot(OP_ClassDrugDot.get(b)[1].toString());
							}
							classOPList.set(a, classDrugDotDto);
						}
					}
				}
				
				//各科別門急診/住院總藥品點數
				for(int a=0;a<classOP_AllList.size();a++) {
					String code=classOP_AllList.get(a).getCode();
					int num1=checkIntgerNull(classOP_AllList.get(a).getDot());
					ClassDrugDotDto classDrugDotDto=classOP_AllList.get(a);
					
					for(int b=0;b<IP_ClassDrugDot.size();b++) {
						if(code.equals(IP_ClassDrugDot.get(b)[0].toString())) {
							if(IP_ClassDrugDot.get(b)[1]!=null) {
								int num2=checkIntgerNull(IP_ClassDrugDot.get(b)[1].toString());
								int num3=num1+num2;
								classDrugDotDto.setDot(String.valueOf(num3));
							}
							classOP_AllList.set(a,classDrugDotDto);
							
						}
					}
				}
				
				//各科別住院總藥品點數
				for(int a=0;a<classIPList.size();a++) {
					String code=classIPList.get(a).getCode();
					ClassDrugDotDto classDrugDotDto=classIPList.get(a);
					
					for(int b=0;b<IP_ClassDrugDot.size();b++) {
						if(code.equals(IP_ClassDrugDot.get(b)[0].toString())) {
							if(IP_ClassDrugDot.get(b)[1]!=null) {
								classDrugDotDto.setDot(IP_ClassDrugDot.get(b)[1].toString());
							}
							classIPList.set(a,classDrugDotDto);
							
						}
					}
				}
				
				//門急診/住院總藥費差異(與去年同一季相比)
				int currentFee=checkIntgerNull(OP_AllDrugFee.toString());
				int lastFee=checkIntgerNull(opdDao.findTDrugFee(optDao.findByFeeYmListOrderById(getLastSeason(seasonList)),iptDao.findByFeeYmListOrderById(getLastSeason(seasonList))));
				FeeDiff.append(String.valueOf(currentFee-lastFee));
				
				//各科別門急診總藥費(去年同一季)
				List<Object[]>last_OP_ClassDrugFee=opdDao.findByOptIdAndGroupByFuncType(optDao.findByFeeYmListOrderById(getLastSeason(seasonList)));
				//各科別住院總藥費(去年同一季)
				List<Object[]>last_IP_ClassDrugFee=ipdDao.findByIptIdAndGroupByFuncType(iptDao.findByFeeYmListOrderById(getLastSeason(seasonList)));
				
				//各科別門急診/住院總藥費(去年同一季)------------------------------------------------
				
				for(int a=0;a<lastDrugFee.size();a++) {
					String code=lastDrugFee.get(a).getCode();
					int num1=checkIntgerNull(lastDrugFee.get(a).getFee());
					ClassDrugFeeDto classDrugFeeDto=lastDrugFee.get(a);
					
					for(int b=0;b<last_OP_ClassDrugFee.size();b++) {
						if(code.equals(last_OP_ClassDrugFee.get(b)[0].toString())) {
							if(last_OP_ClassDrugFee.get(b)[1]!=null) {
								int num2=checkIntgerNull(last_OP_ClassDrugFee.get(b)[1].toString());
								int num3=num1+num2;
								classDrugFeeDto.setFee(String.valueOf(num3));
							}
							lastDrugFee.set(a, classDrugFeeDto);
						}
					}
				}
				
				for(int a=0;a<lastDrugFee.size();a++) {
					String code=lastDrugFee.get(a).getCode();
					int num1=checkIntgerNull(lastDrugFee.get(a).getFee());
					ClassDrugFeeDto classDrugFeeDto=lastDrugFee.get(a);
					
					for(int b=0;b<last_IP_ClassDrugFee.size();b++) {
						if(code.equals(last_IP_ClassDrugFee.get(b)[0].toString())) {
							if(last_IP_ClassDrugFee.get(b)[1]!=null) {
								int num2=checkIntgerNull(last_IP_ClassDrugFee.get(b)[1].toString());
								int num3=num1+num2;
								classDrugFeeDto.setFee(String.valueOf(num3));
							}
							lastDrugFee.set(a, classDrugFeeDto);
						}
					}
				}
				
				//-----------------------------------------------------------------------------
				
				//各科別門急診/住院總藥費差異(與去年同一季相比)---------------------------------------
				for(int a=0;a<classDrugFeeDiff.size();a++) {
					String code=classDrugFeeDiff.get(a).getCode();
					ClassDrugFeeDto classDrugFeeDto=classDrugFeeDiff.get(a);
					
					for(int b=0;b<classOP_AllList.size();b++) {
						if(code.equals(classOP_AllList.get(b).getCode())) {
							int num2=checkIntgerNull(classOP_AllList.get(b).getDot());
							classDrugFeeDto.setFee(String.valueOf(num2));
							classDrugFeeDiff.set(a, classDrugFeeDto);
						}
					}
				}

				for(int a=0;a<classDrugFeeDiff.size();a++) {
					String code=classDrugFeeDiff.get(a).getCode();
					int num1=checkIntgerNull(classDrugFeeDiff.get(a).getFee());
					ClassDrugFeeDto classDrugFeeDto=classDrugFeeDiff.get(a);
					
					for(int b=0;b<lastDrugFee.size();b++) {
						if(code.equals(lastDrugFee.get(b).getCode())) {
							int num2=checkIntgerNull(lastDrugFee.get(b).getFee());
							int num3=num1-num2;
							classDrugFeeDto.setFee(String.valueOf(num3));
							classDrugFeeDiff.set(a, classDrugFeeDto);
						}
					}
				}
				
				//----------------------------------------------------------------------------
				
				//各科別門急診/住院藥費佔率
				for(int a=0;a<classAllDrugFeeRate.size();a++) {
					String desc_chi=classAllDrugFeeRate.get(a).getDesc_chi();
					String code=classAllDrugFeeRate.get(a).getCode();
					
					for(int b=0;b<classOP_AllList.size();b++) {
						if(code.equals(classOP_AllList.get(b).getCode())) {
								classAllDrugFeeRate.set(a, new ClassDrugFeeDto(desc_chi,code,classOP_AllList.get(b).getDot()));
						}
					}
				}
				
				for(int a=0;a<classAllDrugFeeRate.size();a++) {
					String desc_chi=classAllDrugFeeRate.get(a).getDesc_chi();
					String code=classAllDrugFeeRate.get(a).getCode();
					double num1=checkDoubleNull(classAllDrugFeeRate.get(a).getFee());
					
					for(int b=0;b<classAll_TDot.size();b++) {
						if(code.equals(classAll_TDot.get(b).getCode())) {
							double num2=checkDoubleNull(classAll_TDot.get(b).getDot());
							if((int)num1==0 || (int)num2==0) {
								classAllDrugFeeRate.set(a, new ClassDrugFeeDto(desc_chi,code,df.format(0)));
							}
							else {
								classAllDrugFeeRate.set(a, new ClassDrugFeeDto(desc_chi,code,df.format(num1/num2)));
							}
						}
					}
				}
				
				//各科別門急診藥費佔率
				for(int a=0;a<classOPDrugFeeRate.size();a++) {
					String desc_chi=classOPDrugFeeRate.get(a).getDesc_chi();
					String code=classOPDrugFeeRate.get(a).getCode();
					
					for(int b=0;b<classOPList.size();b++) {
						if(code.equals(classOPList.get(b).getCode())) {
							classOPDrugFeeRate.set(a,new ClassDrugFeeDto(desc_chi, code, classOPList.get(b).getDot()));
						}
					}
				}
				for(int a=0;a<classOPDrugFeeRate.size();a++) {
					String desc_chi=classOPDrugFeeRate.get(a).getDesc_chi();
					String code=classOPDrugFeeRate.get(a).getCode();
					double num1=checkDoubleNull(classOPDrugFeeRate.get(a).getFee());
					
					for(int b=0;b<classOP_TDot.size();b++) {
						if(code.equals(classOP_TDot.get(b).getCode())) {
							double num2=checkDoubleNull(classOP_TDot.get(b).getDot());
							if((int)num1==0 || (int)num2==0) {
								classOPDrugFeeRate.set(a, new ClassDrugFeeDto(desc_chi,code,df.format(0)));
							}
							else {
								classOPDrugFeeRate.set(a, new ClassDrugFeeDto(desc_chi,code,df.format(num1/num2)));
							}
						}
					}
				}
				
				//各科別住院藥費佔率
				for(int a=0;a<classIPDrugFeeRate.size();a++) {
					String desc_chi=classIPDrugFeeRate.get(a).getDesc_chi();
					String code=classIPDrugFeeRate.get(a).getCode();
					
					for(int b=0;b<classIPList.size();b++) {
						if(code.equals(classIPList.get(b).getCode())) {
							classIPDrugFeeRate.set(a,new ClassDrugFeeDto(desc_chi, code, classIPList.get(b).getDot()));
						}
					}
				}
				for(int a=0;a<classIPDrugFeeRate.size();a++) {
					String desc_chi=classIPDrugFeeRate.get(a).getDesc_chi();
					String code=classIPDrugFeeRate.get(a).getCode();
					double num1=checkDoubleNull(classIPDrugFeeRate.get(a).getFee());
					
					for(int b=0;b<classIP_TDot.size();b++) {
						if(code.equals(classIP_TDot.get(b).getCode())) {
							double num2=checkDoubleNull(classIP_TDot.get(b).getDot());
							if((int)num1==0 || (int)num2==0) {
								classIPDrugFeeRate.set(a, new ClassDrugFeeDto(desc_chi,code,df.format(0)));
							}
							else {
								classIPDrugFeeRate.set(a, new ClassDrugFeeDto(desc_chi,code,df.format(num1/num2)));
							}
						}
					}
				}
				
				
				//各科別門急診案件數
				List<Object[]>class_op_case_count=opdDao.findClassOPCount(optDao.findByFeeYmListOrderById(seasonList));
				//各科別住院案件數
				List<Object[]>class_ip_case_count=ipdDao.findClassIPCount(iptDao.findByFeeYmListOrderById(seasonList));
				
				for(int a=0;a<classOPCaseCount.size();a++) {
					String code=classOPCaseCount.get(a).getCode();
					String desc_chi=classOPCaseCount.get(a).getDesc_chi();
					
					for(int b=0;b<class_op_case_count.size();b++) {
						if(code.equals(class_op_case_count.get(b)[0].toString())) {
							if(class_op_case_count.get(b)[1]!=null) {
								classOPCaseCount.set(a,new ClassCaseCountDto(desc_chi, code, class_op_case_count.get(b)[1].toString()));
							}
						}
					}
				}
				
				for(int a=0;a<classIPCaseCount.size();a++) {
					String code=classIPCaseCount.get(a).getCode();
					String desc_chi=classIPCaseCount.get(a).getDesc_chi();
					
					for(int b=0;b<class_ip_case_count.size();b++) {
						if(code.equals(class_ip_case_count.get(b)[0].toString())) {
							if(class_ip_case_count.get(b)[1]!=null) {
								classIPCaseCount.set(a,new ClassCaseCountDto(desc_chi, code, class_ip_case_count.get(b)[1].toString()));
							}
						}
					}
				}
				
				//各科別門急診/住院案件數
				for(int a=0;a<classAllCaseCount.size();a++) {
					String code=classAllCaseCount.get(a).getCode();
					String desc_chi=classAllCaseCount.get(a).getDesc_chi();
					
					for(int b=0;b<classOPCaseCount.size();b++) {
						if(code.equals(classOPCaseCount.get(b).getCode())) {
							classAllCaseCount.set(a, new ClassCaseCountDto(desc_chi, code, classOPCaseCount.get(b).getCaseCount()));
						}
					}
				}
				
				for(int a=0;a<classAllCaseCount.size();a++) {
					String code=classAllCaseCount.get(a).getCode();
					String desc_chi=classAllCaseCount.get(a).getDesc_chi();
					int num1=checkIntgerNull(classAllCaseCount.get(a).getCaseCount());
					
					for(int b=0;b<classIPCaseCount.size();b++) {
						if(code.equals(classIPCaseCount.get(b).getCode())) {
							int num2=checkIntgerNull(classIPCaseCount.get(b).getCaseCount());
							int num3=num1+num2;
							classAllCaseCount.set(a, new ClassCaseCountDto(desc_chi, code,String.valueOf(num3)));
						}
					}
				}
				
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("health care cost exception {}",e);
//				e.printStackTrace();
			}
			
			return setHealthCareCost(seasonStr
					,OP_AllDot.toString(),OP_AllDrugFee.toString(),OP_AllRate.toString(),AllCount.toString(),classOP_AllList
					,classDrugFeeDiff,FeeDiff.toString()
					,OP_Dot.toString(),OP_DrugFee.toString(),OP_Rate.toString(),OPCount.toString(),classOPList
					,IP_Dot.toString(),IP_DrugFee.toString(),IP_Rate.toString(),IPCount.toString(),classIPList
					,classOP_TDot,classIP_TDot,classAll_TDot
					,classOPDrugFeeRate,classIPDrugFeeRate,classAllDrugFeeRate
					,classOPCaseCount,classIPCaseCount,classAllCaseCount);
	  }
	  
	  public HealthCareCost setHealthCareCost(String season,String OP_AllDot, String OP_AllDrugFee,String OP_AllRate,String AllCount,
			  List<ClassDrugDotDto>classOPAll,List<ClassDrugFeeDto> classDrugFeeDiff,String FeeDiff,
			  String OP_Dot,String OP_DrugFee,String OP_Rate,String OPCount,List<ClassDrugDotDto>classOP,
			  String IP_Dot,String IP_DrugFee,String IP_Rate,String IPCount,List<ClassDrugDotDto>classIP,
			  List<ClassDrugDotDto> classOP_TDot,List<ClassDrugDotDto> classIP_TDot,List<ClassDrugDotDto>classAll_TDot,
			  List<ClassDrugFeeDto>classOPDrugFeeRate,List<ClassDrugFeeDto>classIPDrugFeeRate,List<ClassDrugFeeDto>classAllDrugFeeRate,
			  List<ClassCaseCountDto> classOPCaseCount,List<ClassCaseCountDto> classIPCaseCount,List<ClassCaseCountDto> classAllCaseCount){ 
		  	HealthCareCost healthCareCost=new HealthCareCost();
			healthCareCost.setSeason(season);
			healthCareCost.setAllDot(OP_AllDot);
			healthCareCost.setAllDrugFee(OP_AllDrugFee);
			healthCareCost.setAllRate(OP_AllRate);
			healthCareCost.setAllCount(AllCount);
			healthCareCost.setClassAll(classOPAll);
			healthCareCost.setClassAll_TDot(classAll_TDot);
			healthCareCost.setClassAllFeeRate(classAllDrugFeeRate);
			healthCareCost.setClassAllCaseCount(classAllCaseCount);
			healthCareCost.setClassAllFeeDiff(classDrugFeeDiff);
			healthCareCost.setAllFeeDiff(FeeDiff);
			
			healthCareCost.setOP_Dot(OP_Dot);
			healthCareCost.setOP_DrugFee(OP_DrugFee);
			healthCareCost.setOP_Rate(OP_Rate);
			healthCareCost.setOPCount(OPCount);
			healthCareCost.setClassOP(classOP);
			healthCareCost.setClassOP_TDot(classOP_TDot);
			healthCareCost.setClassOPFeeRate(classOPDrugFeeRate);
			healthCareCost.setClassOPCaseCount(classOPCaseCount);
			
			healthCareCost.setIP_Dot(IP_Dot);
			healthCareCost.setIP_DrugFee(IP_DrugFee);
			healthCareCost.setIP_Rate(IP_Rate);
			healthCareCost.setIPCount(IPCount);
			healthCareCost.setClassIP(classIP);
			healthCareCost.setClassIP_TDot(classIP_TDot);
			healthCareCost.setClassIPFeeRate(classIPDrugFeeRate);
			healthCareCost.setClassIPCaseCount(classIPCaseCount);
			
			return healthCareCost;
		}
	  
	  public int checkIntgerNull(String str) {
		  Integer num =null;
		  if(str != null && !str.equals("") && !str.equals("null")){
			  num = Integer.parseInt(str);
		  }
		  else {
			  return 0;
		  }
		  
		  return num;
	  }
	  
	  public double checkDoubleNull(String str) {
		  Double num =null;
		  if(str != null && !str.equals("") && !str.equals("null")){
			  num = Double.parseDouble(str);
		  }
		  else {
			  return 0;
		  }
		  
		  return num;
	  }
	  
	  public List<ClassDrugDotDto> initDotList(){
		  List<ClassDrugDotDto> list =new ArrayList<ClassDrugDotDto>();
		  for(int i=0;i<codeTableList.size();i++) {
			  list.add(new ClassDrugDotDto(codeTableList.get(i).getDescChi(), codeTableList.get(i).getCode(), "0"));
		  }
		  return list;
	  }
	  
	  public List<ClassDrugFeeDto> initFeeList(){
		  List<ClassDrugFeeDto> list=new ArrayList<ClassDrugFeeDto>();
		  for(int i=0;i<codeTableList.size();i++) {
			  list.add(new ClassDrugFeeDto(codeTableList.get(i).getDescChi(), codeTableList.get(i).getCode(), "0"));
		  }
		  return list;
	  }
	  
	  public List<ClassCaseCountDto> initCaseList(){
		  List<ClassCaseCountDto> list=new ArrayList<ClassCaseCountDto>();
		  for(int i=0;i<codeTableList.size();i++) {
			  list.add(new ClassCaseCountDto(codeTableList.get(i).getDescChi(), codeTableList.get(i).getCode(), "0"));
		  }
		  return list;
	  }
	  
	  //得到去年同一季
	  public List<String> getLastSeason(List<String> current){
		  
		  List<String>last=new ArrayList<String>();
		  StringBuilder day=new StringBuilder();
		  
		  for(int i=0;i<current.size();i++) {
			  Date date;
			  String lastYear="";
			  day.setLength(0);
			  
				try {
					date = new SimpleDateFormat("MM/dd/yyyy").parse("01/01/"+current.get(i).substring(0,3));
				    Calendar instance = Calendar.getInstance();
					instance.setTime(date);
					instance.add(Calendar.YEAR, -1);
					lastYear = String.valueOf(instance.get(Calendar.YEAR));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				day.append(lastYear+current.get(i).substring(3));
				last.add(day.toString());
				
		  }
		  
//		  System.out.println(last.toString());
		  return last;
	  }
}
