package tw.com.leadtek.nhiwidget.service;


import java.io.File;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import org.springframework.stereotype.Service;

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
import tw.com.leadtek.nhiwidget.dto.CaseDotFeeDto;
import tw.com.leadtek.nhiwidget.dto.ClassCaseCountDto;
import tw.com.leadtek.nhiwidget.dto.ClassDoctorDto;
import tw.com.leadtek.nhiwidget.dto.ClassDoctorDto_weekly;
import tw.com.leadtek.nhiwidget.dto.ClassDoctorWeeklyDto;
import tw.com.leadtek.nhiwidget.dto.ClassDrugDotDto;
import tw.com.leadtek.nhiwidget.dto.ClassDrugFeeDto;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.payload.BaseResponse;
import tw.com.leadtek.nhiwidget.payload.report.HealthCareCost;
import tw.com.leadtek.tools.DateTool;

/*
 * 	??????????????????
 * */

@Service
public class HealthCareCostService {
	
	  private Logger logger = LogManager.getLogger();
	
	  /**
	   * ???????????????????????????
	   */
	  public static final String FUNC_TYPE_ALL_NAME = "?????????";
	  
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
	  
	  private DecimalFormat df = new DecimalFormat("######0.0000");
	  
	//??????????????????????????????
	  public List<HealthCareCost> getData(String year,String season,List<HealthCareCost> results) {
		  
			  String[] seasons=new String[4];
		  
			  if(season != null && !season.equals("") && !season.equals("null")){
				  seasons=season.split(" ");
			  }
			
			codeTableList=code_TABLEDao.findByCatOrderByCode("FUNC_TYPE");
			
			//??????????????????
			String weekDate="";
			for(int i=0;i<seasons.length;i++) {
				if(seasons[i].equals("Q1")) {
					weekDate="0331";
				}
				else if(seasons[i].equals("Q2")) {
					weekDate="0630";
				}
				else if(seasons[i].equals("Q3")) {
					weekDate="0930";
				}
				else if(seasons[i].equals("Q4")) {
					weekDate="1231";
				}
			}
			
			String chineseYear = DateTool.convertToChineseYear(year);
			
			for(int i=0;i<seasons.length;i++) {
				
				List<String> seasonList=new ArrayList<String>();
				StringBuilder monthA=new StringBuilder();
				StringBuilder monthB=new StringBuilder();
				StringBuilder monthC=new StringBuilder();
				
				if(seasons[i].equals("Q1")) {
					monthA.append(chineseYear);
					monthA.append("01");
					monthB.append(chineseYear);
					monthB.append("02");
					monthC.append(chineseYear);
					monthC.append("03");
					
					seasonList.add(monthA.toString());
					seasonList.add(monthB.toString());
					seasonList.add(monthC.toString());
					results.add(statisticData(seasons[i],seasonList,year,weekDate));
				}
				else if(seasons[i].equals("Q2")) {
					monthA.append(chineseYear);
					monthA.append("04");
					monthB.append(chineseYear);
					monthB.append("05");
					monthC.append(chineseYear);
					monthC.append("06");
					
					seasonList.add(monthA.toString());
					seasonList.add(monthB.toString());
					seasonList.add(monthC.toString());
					results.add(statisticData(seasons[i],seasonList,year,weekDate));
				}
				else if(seasons[i].equals("Q3")) {
					monthA.append(chineseYear);
					monthA.append("07");
					monthB.append(chineseYear);
					monthB.append("08");
					monthC.append(chineseYear);
					monthC.append("09");
					
					seasonList.add(monthA.toString());
					seasonList.add(monthB.toString());
					seasonList.add(monthC.toString());
					results.add(statisticData(seasons[i],seasonList,year,weekDate));
				}
				else if(seasons[i].equals("Q4")) {
					monthA.append(chineseYear);
					monthA.append("10");
					monthB.append(chineseYear);
					monthB.append("11");
					monthC.append(chineseYear);
					monthC.append("12");
					
					seasonList.add(monthA.toString());
					seasonList.add(monthB.toString());
					seasonList.add(monthC.toString());
					results.add(statisticData(seasons[i],seasonList,year,weekDate));
				}
				else {
					  HealthCareCost healthCareCost=new HealthCareCost();
					  healthCareCost.setResult(BaseResponse.ERROR);
					  healthCareCost.setMessage("?????????????????????");
					  results.add(healthCareCost);
				      return results;
				}
			}
		  
		  return results;
	  }
	  
	  
	  //??????????????????-??????
	  public void getDataExport(String year,String season,List<HealthCareCost> results,HttpServletResponse response) {
			try {
				
				String chineseYear = DateTool.convertToChineseYear(year);
				
				// ??????????????????
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
					/* ???????????????
					 *  ?????????-?????????????????????(?????????/???????????????????????????) 
					 *  */
					HSSFSheet drugFeeSheet = workbook.createSheet("?????????-?????????????????????(?????????-??????)");
					
					//?????????/??????
					String allDot=results.get(0).getAllDot(); //?????? ???????????????
					String allDrugFee=results.get(0).getAllDrugFee(); //?????? ?????????
					String allRate=results.get(0).getAllRate();//?????? ????????????
					String allCount=results.get(0).getAllCount();//?????? ?????????
					List<ClassDrugDotDto> classAllDrugFee=results.get(0).getClassAll(); //???????????????(??????)
					List<ClassDrugDotDto> classAll_TDot=results.get(0).getClassAll_TDot();//????????????????????????
					List<ClassDrugFeeDto>classAllDrugFeeRate=results.get(0).getClassAllFeeRate();//?????????????????????
					List<ClassCaseCountDto>classAllCaseCount=results.get(0).getClassAllCaseCount();//??????????????????
					
					//?????????
					String OPDot=results.get(0).getOP_Dot(); //?????? ???????????????
					String OPDrugFee=results.get(0).getOP_DrugFee(); //?????? ?????????
					String OPRate=results.get(0).getOP_Rate();//?????? ????????????
					String OPCount=results.get(0).getOPCount();//?????? ?????????
					List<ClassDrugDotDto> classOPDrugFee=results.get(0).getClassOP(); //???????????????(??????)
					List<ClassDrugDotDto> classOP_TDot=results.get(0).getClassOP_TDot();//????????????????????????
					List<ClassDrugFeeDto>classOPDrugFeeRate=results.get(0).getClassOPFeeRate();//?????????????????????
					List<ClassCaseCountDto>classOPCaseCount=results.get(0).getClassOPCaseCount();//??????????????????
					
					//??????
					String IPDot=results.get(0).getIP_Dot(); //?????? ???????????????
					String IPDrugFee=results.get(0).getIP_DrugFee(); //?????? ?????????
					String IPRate=results.get(0).getIP_Rate();//?????? ????????????
					String IPCount=results.get(0).getIPCount();//?????? ?????????
					List<ClassDrugDotDto>classIPDrugFee=results.get(0).getClassIP(); //???????????????(??????)
					List<ClassDrugDotDto> classIP_TDot=results.get(0).getClassIP_TDot();//????????????????????????
					List<ClassDrugFeeDto>classIPDrugFeeRate=results.get(0).getClassIPFeeRate();//?????????????????????
					List<ClassCaseCountDto>classIPCaseCount=results.get(0).getClassIPCaseCount();//??????????????????
					
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
					
					//?????????????????????(?????????/??????)
					classDrugFeeTemplate(season,"?????????????????????(?????????/??????)",rowDF0,rowDF1,rowDF2,rowDF3,rowDF4,
							rowDF5,cellStyle_left,allCount,classAllCaseCount,
							allDot,classAll_TDot,allDrugFee,classAllDrugFee,
							allRate,classAllDrugFeeRate);
					
					//?????????????????????(?????????)
					classDrugFeeTemplate(season,"?????????????????????(?????????)",rowDF7,rowDF8,rowDF9,rowDF10,rowDF11,
							rowDF12,cellStyle_left,OPCount,classOPCaseCount,
							OPDot,classOP_TDot,OPDrugFee,classOPDrugFee,
							OPRate,classOPDrugFeeRate);
					
					//?????????????????????(??????)
					classDrugFeeTemplate(season,"?????????????????????(??????)",rowDF14,rowDF15,rowDF16,rowDF17,rowDF18,
							rowDF19,cellStyle_left,IPCount,classIPCaseCount,
							IPDot,classIP_TDot,IPDrugFee,classIPDrugFee,
							IPRate,classIPDrugFeeRate);
					
					/*???????????????
					 * ?????????-????????????????????????????????????(?????????/??????)
					 * */
					HSSFSheet drugFeeDiffSheet = workbook.createSheet("?????????-????????????????????????????????????");
					drugFeeDiffSheet.addMergedRegion(new CellRangeAddress(0,0,0,2));
					drugFeeDiffSheet.addMergedRegion(new CellRangeAddress(1,1,0,1));
					drugFeeDiffSheet.addMergedRegion(new CellRangeAddress(2,2,0,1));
					
					List<ClassDrugFeeDto> classAllFeeDiff=results.get(0).getClassAllFeeDiff();
					String AllFeeDiff=results.get(0).getAllFeeDiff();
					
					// ?????????,???????????????????????????createRow()??????,????????????0????????????
					HSSFRow rowDFD0 = drugFeeDiffSheet.createRow(0);
					HSSFRow rowDFD1 = drugFeeDiffSheet.createRow(1);
					HSSFRow rowDFD2 = drugFeeDiffSheet.createRow(2);
					
					addRowCell(rowDFD0,0,"????????????????????????????????????",cellStyle_noBorder);
					addRowCell(rowDFD1,0,"",cellStyle_left);
					addRowCell(rowDFD1,1,"",cellStyle_left);
					addRowCell(rowDFD1,2,"??????",cellStyle_left);
					addRowCell(rowDFD2, 0, "???????????????",cellStyle_left);
					addRowCell(rowDFD2, 2, AllFeeDiff,cellStyle_left);
					
					for(int i=0;i<classAllFeeDiff.size();i++) {
						addRowCell(rowDFD1 ,i+3,classAllFeeDiff.get(i).getDesc_chi(),cellStyle_left);
						addRowCell(rowDFD2,i+3, classAllFeeDiff.get(i).getFee(),cellStyle_left);
					}
					
					/*??????????????? 
					 * ?????????-??????????????????
					 * */
					HSSFSheet drugFeeSortsheet = workbook.createSheet("?????????-??????????????????");
					
					HSSFRow rowDFS0 = drugFeeSortsheet.createRow(0);
					addRowCell(rowDFS0,0, "",cellStyle_left);
					addRowCell(rowDFS0,1, "?????????",cellStyle_left);
					addRowCell(rowDFS0,2, "???????????????",cellStyle_left);
					addRowCell(rowDFS0,3, "?????????",cellStyle_left);
					
					List<ClassDoctorDto> classDoctorAll=results.get(0).getClassDoctorAll();
					int index=1;
					
					for(int i=1;i<classDoctorAll.size();i++) {
						String desc_chi=classDoctorAll.get(i).getDesc_chi();
						Map<String,CaseDotFeeDto> doctors=classDoctorAll.get(i).getDoctors();
						
						HSSFRow row = drugFeeSortsheet.createRow(index);
						
						//??????????????????
						addRowCell(row, 0,  desc_chi, cellStyle_left);
						addRowCell(row, 1,  "", cellStyle_left);
						addRowCell(row, 2,  "", cellStyle_left);
						addRowCell(row, 3,  "", cellStyle_left);
						
						index++;
						
						for (Map.Entry<String, CaseDotFeeDto> entry : doctors.entrySet()) {
							String doctorName=entry.getKey();
							Integer caseCount=entry.getValue().getCaseCount();
							Integer dot=entry.getValue().getDot();
							Integer drugFee=entry.getValue().getDrugFee();
							
							HSSFRow row_element = drugFeeSortsheet.createRow(index);
							
							//???????????????????????????
							addRowCell(row_element, 0, doctorName, cellStyle_left);
							addRowCell(row_element, 1,  caseCount.toString(), cellStyle_left);
							addRowCell(row_element, 2, dot.toString(), cellStyle_left);
							addRowCell(row_element, 3, drugFee.toString(), cellStyle_left);
							
							index++;
						}
					}
					
					
				}
				else {
					
					/* ???????????????
					 *  ?????????-?????????????????????(?????????/??????)
					 *  */
					HSSFSheet drugFeeSheet_All = workbook.createSheet("?????????-?????????????????????(?????????-??????)");					
					/* ???????????????
					 *  ?????????-?????????????????????(?????????)
					 *  */
					HSSFSheet drugFeeSheet_OP = workbook.createSheet("?????????-?????????????????????(?????????)");				
					/* ???????????????
					 *  ?????????-?????????????????????(??????)
					 *  */
					HSSFSheet drugFeeSheet_IP = workbook.createSheet("?????????-?????????????????????(??????)");						
					/* ???????????????
					 *  ?????????-?????????????????????(??????)
					 *  */
					HSSFSheet drugFeeDiffSheet = workbook.createSheet("?????????-????????????????????????????????????");
					
					int cellIndex=0;
					int cellIndex2=0;
					
					for(int i=0;i<results.size();i++) {
						
						if(i!=0) {
							cellIndex+=6;
							cellIndex2+=3;
						}
						
						//?????????/??????
						String multi_allDot=results.get(i).getAllDot(); //?????? ???????????????
						String multi_allDrugFee=results.get(i).getAllDrugFee(); //?????? ?????????
						String multi_allRate=results.get(i).getAllRate();//?????? ????????????
						String multi_allCount=results.get(i).getAllCount();//?????? ?????????
						List<ClassDrugDotDto> multi_classAllDrugFee=results.get(i).getClassAll(); //???????????????(??????)
						List<ClassDrugDotDto> multi_classAll_TDot=results.get(i).getClassAll_TDot();//????????????????????????
						List<ClassDrugFeeDto>multi_classAllDrugFeeRate=results.get(i).getClassAllFeeRate();//?????????????????????
						List<ClassCaseCountDto>multi_classAllCaseCount=results.get(i).getClassAllCaseCount();//??????????????????
						
						//?????????
						String multi_OPDot=results.get(i).getOP_Dot(); //?????? ???????????????
						String multi_OPDrugFee=results.get(i).getOP_DrugFee(); //?????? ?????????
						String multi_OPRate=results.get(i).getOP_Rate();//?????? ????????????
						String multi_OPCount=results.get(i).getOPCount();//?????? ?????????
						List<ClassDrugDotDto> multi_classOPDrugFee=results.get(i).getClassOP(); //???????????????(??????)
						List<ClassDrugDotDto> multi_classOP_TDot=results.get(i).getClassOP_TDot();//????????????????????????
						List<ClassDrugFeeDto>multi_classOPDrugFeeRate=results.get(i).getClassOPFeeRate();//?????????????????????
						List<ClassCaseCountDto>multi_classOPCaseCount=results.get(i).getClassOPCaseCount();//??????????????????
						
						//??????
						String multi_IPDot=results.get(i).getIP_Dot(); //?????? ???????????????
						String multi_IPDrugFee=results.get(i).getIP_DrugFee(); //?????? ?????????
						String multi_IPRate=results.get(i).getIP_Rate();//?????? ????????????
						String multi_IPCount=results.get(i).getIPCount();//?????? ?????????
						List<ClassDrugDotDto> multi_classIPDrugFee=results.get(i).getClassIP(); //???????????????(??????)
						List<ClassDrugDotDto> multi_classIP_TDot=results.get(i).getClassIP_TDot();//????????????????????????
						List<ClassDrugFeeDto>multi_classIPDrugFeeRate=results.get(i).getClassIPFeeRate();//?????????????????????
						List<ClassCaseCountDto>multi_classIPCaseCount=results.get(i).getClassIPCaseCount();//??????????????????
						
						//???????????????????????????????????????
						List<ClassDrugFeeDto> multi_classAllFeeDiff=results.get(i).getClassAllFeeDiff();
						String multi_AllFeeDiff=results.get(i).getAllFeeDiff();
						
						
						//?????????????????????(?????????/??????) table
						drugFeeSheet_All.addMergedRegion(new CellRangeAddress(cellIndex,cellIndex,0,2));
						drugFeeSheet_All.addMergedRegion(new CellRangeAddress(cellIndex+1,cellIndex+1,0,2));
						drugFeeSheet_All.addMergedRegion(new CellRangeAddress(cellIndex+2,cellIndex+2,0,2));
						drugFeeSheet_All.addMergedRegion(new CellRangeAddress(cellIndex+3,cellIndex+3,0,2));
						drugFeeSheet_All.addMergedRegion(new CellRangeAddress(cellIndex+4,cellIndex+4,0,2));
						drugFeeSheet_All.addMergedRegion(new CellRangeAddress(cellIndex+5,cellIndex+5,0,2));
						
						HSSFRow row1 = drugFeeSheet_All.createRow(cellIndex);
						HSSFRow row2 = drugFeeSheet_All.createRow(cellIndex+1);
						HSSFRow row3 = drugFeeSheet_All.createRow(cellIndex+2);
						HSSFRow row4 = drugFeeSheet_All.createRow(cellIndex+3);
						HSSFRow row5 = drugFeeSheet_All.createRow(cellIndex+4);
						HSSFRow row6 = drugFeeSheet_All.createRow(cellIndex+5);
						
						StringBuilder titleName=new StringBuilder();
						titleName.append(results.get(i).getSeason());
						titleName.append("?????????????????????(?????????/??????)");
						
						addRowCell(row1, 0,titleName.toString() , cellStyle_noBorder);
						
						for(int j=0;j<3;j++) {
							addRowCell(row2, j,"" , cellStyle_left);
						}
						addRowCell(row2, 3,"??????" , cellStyle_left);
						
						addRowCell(row3, 0,"?????????" , cellStyle_left);
						for(int j=1;j<3;j++) {
							addRowCell(row3, j,"" , cellStyle_left);
						}
						addRowCell(row3, 3, multi_allCount, cellStyle_left);
						
						for(int j=0;j<multi_classAllCaseCount.size();j++) {
							addRowCell(row2, j+4, multi_classAllCaseCount.get(j).getDesc_chi(), cellStyle_left);
							addRowCell(row3, j+4, multi_classAllCaseCount.get(j).getCaseCount(), cellStyle_left);
						}
						
						addRowCell(row4, 0,"???????????????(????????????)" , cellStyle_left);
						for(int j=1;j<3;j++) {
							addRowCell(row4, j,"" , cellStyle_left);
						}
						addRowCell(row4, 3, multi_allDot, cellStyle_left);
						
						for(int j=0;j<multi_classAll_TDot.size();j++) {
							addRowCell(row4, j+4, multi_classAll_TDot.get(j).getDot(), cellStyle_left);
						}
						
						addRowCell(row5, 0,"?????????(????????????)" , cellStyle_left);
						for(int j=1;j<3;j++) {
							addRowCell(row5, j,"" , cellStyle_left);
						}
						addRowCell(row5, 3, multi_allDrugFee, cellStyle_left);
						
						for(int j=0;j<multi_classAllDrugFee.size();j++) {
							addRowCell(row5, j+4, multi_classAllDrugFee.get(j).getDot(), cellStyle_left);
						}
						
						addRowCell(row6, 0,"????????????" , cellStyle_left);
						for(int j=1;j<3;j++) {
							addRowCell(row6, j,"" , cellStyle_left);
						}
						addRowCell(row6, 3, multi_allRate, cellStyle_left);
						
						for(int j=0;j<multi_classAllDrugFeeRate.size();j++) {
							addRowCell(row6, j+4, multi_classAllDrugFeeRate.get(j).getFee(), cellStyle_left);
						}
						
						//?????????????????????(?????????) table
						drugFeeSheet_OP.addMergedRegion(new CellRangeAddress(cellIndex,cellIndex,0,2));
						drugFeeSheet_OP.addMergedRegion(new CellRangeAddress(cellIndex+1,cellIndex+1,0,2));
						drugFeeSheet_OP.addMergedRegion(new CellRangeAddress(cellIndex+2,cellIndex+2,0,2));
						drugFeeSheet_OP.addMergedRegion(new CellRangeAddress(cellIndex+3,cellIndex+3,0,2));
						drugFeeSheet_OP.addMergedRegion(new CellRangeAddress(cellIndex+4,cellIndex+4,0,2));
						drugFeeSheet_OP.addMergedRegion(new CellRangeAddress(cellIndex+5,cellIndex+5,0,2));
						
						HSSFRow rowA = drugFeeSheet_OP.createRow(cellIndex);
						HSSFRow rowB = drugFeeSheet_OP.createRow(cellIndex+1);
						HSSFRow rowC = drugFeeSheet_OP.createRow(cellIndex+2);
						HSSFRow rowD = drugFeeSheet_OP.createRow(cellIndex+3);
						HSSFRow rowE = drugFeeSheet_OP.createRow(cellIndex+4);
						HSSFRow rowF = drugFeeSheet_OP.createRow(cellIndex+5);
						
						titleName.setLength(0);
						titleName.append(results.get(i).getSeason());
						titleName.append("?????????????????????(?????????)");
						
						addRowCell(rowA, 0,titleName.toString() , cellStyle_noBorder);
						
						for(int j=0;j<3;j++) {
							addRowCell(rowB, j,"" , cellStyle_left);
						}
						addRowCell(rowB, 3,"??????" , cellStyle_left);
						
						addRowCell(rowC, 0,"?????????" , cellStyle_left);
						for(int j=1;j<3;j++) {
							addRowCell(rowC, j,"" , cellStyle_left);
						}
						addRowCell(rowC, 3, multi_OPCount, cellStyle_left);
						
						for(int j=0;j<multi_classOPCaseCount.size();j++) {
							addRowCell(rowB, j+4, multi_classOPCaseCount.get(j).getDesc_chi(), cellStyle_left);
							addRowCell(rowC, j+4, multi_classOPCaseCount.get(j).getCaseCount(), cellStyle_left);
						}
						
						addRowCell(rowD, 0,"???????????????(????????????)" , cellStyle_left);
						for(int j=1;j<3;j++) {
							addRowCell(rowD, j,"" , cellStyle_left);
						}
						addRowCell(rowD, 3, multi_OPDot, cellStyle_left);
						
						for(int j=0;j<multi_classOP_TDot.size();j++) {
							addRowCell(rowD, j+4, multi_classOP_TDot.get(j).getDot(), cellStyle_left);
						}
						
						addRowCell(rowE, 0,"?????????(????????????)" , cellStyle_left);
						for(int j=1;j<3;j++) {
							addRowCell(rowE, j,"" , cellStyle_left);
						}
						addRowCell(rowE, 3, multi_OPDrugFee, cellStyle_left);
						
						for(int j=0;j<multi_classOPDrugFee.size();j++) {
							addRowCell(rowE, j+4, multi_classOPDrugFee.get(j).getDot(), cellStyle_left);
						}
						
						addRowCell(rowF, 0,"????????????" , cellStyle_left);
						for(int j=1;j<3;j++) {
							addRowCell(rowF, j,"" , cellStyle_left);
						}
						addRowCell(rowF, 3, multi_OPRate, cellStyle_left);
						
						for(int j=0;j<multi_classOPDrugFeeRate.size();j++) {
							addRowCell(rowF, j+4, multi_classOPDrugFeeRate.get(j).getFee(), cellStyle_left);
						}
						
						//?????????????????????(??????) table
						drugFeeSheet_IP.addMergedRegion(new CellRangeAddress(cellIndex,cellIndex,0,2));
						drugFeeSheet_IP.addMergedRegion(new CellRangeAddress(cellIndex+1,cellIndex+1,0,2));
						drugFeeSheet_IP.addMergedRegion(new CellRangeAddress(cellIndex+2,cellIndex+2,0,2));
						drugFeeSheet_IP.addMergedRegion(new CellRangeAddress(cellIndex+3,cellIndex+3,0,2));
						drugFeeSheet_IP.addMergedRegion(new CellRangeAddress(cellIndex+4,cellIndex+4,0,2));
						drugFeeSheet_IP.addMergedRegion(new CellRangeAddress(cellIndex+5,cellIndex+5,0,2));
						
						HSSFRow rowAA = drugFeeSheet_IP.createRow(cellIndex);
						HSSFRow rowBB = drugFeeSheet_IP.createRow(cellIndex+1);
						HSSFRow rowCC = drugFeeSheet_IP.createRow(cellIndex+2);
						HSSFRow rowDD = drugFeeSheet_IP.createRow(cellIndex+3);
						HSSFRow rowEE = drugFeeSheet_IP.createRow(cellIndex+4);
						HSSFRow rowFF = drugFeeSheet_IP.createRow(cellIndex+5);
						
						titleName.setLength(0);
						titleName.append(results.get(i).getSeason());
						titleName.append("?????????????????????(??????)");
						
						addRowCell(rowAA, 0,titleName.toString() , cellStyle_noBorder);
						
						for(int j=0;j<3;j++) {
							addRowCell(rowBB, j,"" , cellStyle_left);
						}
						addRowCell(rowBB, 3,"??????" , cellStyle_left);
						
						addRowCell(rowCC, 0,"?????????" , cellStyle_left);
						for(int j=1;j<3;j++) {
							addRowCell(rowCC, j,"" , cellStyle_left);
						}
						addRowCell(rowCC, 3, multi_IPCount, cellStyle_left);
						
						for(int j=0;j<multi_classIPCaseCount.size();j++) {
							addRowCell(rowBB, j+4, multi_classIPCaseCount.get(j).getDesc_chi(), cellStyle_left);
							addRowCell(rowCC, j+4, multi_classIPCaseCount.get(j).getCaseCount(), cellStyle_left);
						}
						
						addRowCell(rowDD, 0,"???????????????(????????????)" , cellStyle_left);
						for(int j=1;j<3;j++) {
							addRowCell(rowDD, j,"" , cellStyle_left);
						}
						addRowCell(rowDD, 3, multi_IPDot, cellStyle_left);
						
						for(int j=0;j<multi_classIP_TDot.size();j++) {
							addRowCell(rowDD, j+4, multi_classIP_TDot.get(j).getDot(), cellStyle_left);
						}
						
						addRowCell(rowEE, 0,"?????????(????????????)" , cellStyle_left);
						for(int j=1;j<3;j++) {
							addRowCell(rowEE, j,"" , cellStyle_left);
						}
						addRowCell(rowEE, 3, multi_IPDrugFee, cellStyle_left);
						
						for(int j=0;j<multi_classIPDrugFee.size();j++) {
							addRowCell(rowEE, j+4, multi_classIPDrugFee.get(j).getDot(), cellStyle_left);
						}
						
						addRowCell(rowFF, 0,"????????????" , cellStyle_left);
						for(int j=1;j<3;j++) {
							addRowCell(rowFF, j,"" , cellStyle_left);
						}
						addRowCell(rowFF, 3, multi_IPRate, cellStyle_left);
						
						for(int j=0;j<multi_classIPDrugFeeRate.size();j++) {
							addRowCell(rowFF, j+4, multi_classIPDrugFeeRate.get(j).getFee(), cellStyle_left);
						}
						
						
						//???????????????????????????????????????
						HSSFRow rowa = drugFeeDiffSheet.createRow(cellIndex2);
						HSSFRow rowb = drugFeeDiffSheet.createRow(cellIndex2+1);
						HSSFRow rowc = drugFeeDiffSheet.createRow(cellIndex2+2);
						
						drugFeeDiffSheet.addMergedRegion(new CellRangeAddress(cellIndex2,cellIndex2,0,2));
						drugFeeDiffSheet.addMergedRegion(new CellRangeAddress(cellIndex2+1,cellIndex2+1,0,2));
						drugFeeDiffSheet.addMergedRegion(new CellRangeAddress(cellIndex2+2,cellIndex2+2,0,2));
						
						titleName.setLength(0);
						titleName.append(results.get(i).getSeason());
						titleName.append("????????????????????????????????????");
						
						addRowCell(rowa,0,titleName.toString(),null);
						
						for(int j=0;j<3;j++) {
							addRowCell(rowb,j,"",cellStyle_left);
						}
						addRowCell(rowb,3,"??????",cellStyle_left);
						
						addRowCell(rowc,0, "???????????????",cellStyle_left);
						addRowCell(rowc,1,"",cellStyle_left);
						addRowCell(rowc,2,"",cellStyle_left);
						addRowCell(rowc,3, multi_AllFeeDiff,cellStyle_left);
						
						for(int x=0;x<multi_classAllFeeDiff.size();x++) {
							addRowCell(rowb ,x+4,multi_classAllFeeDiff.get(x).getDesc_chi(),cellStyle_left);
							addRowCell(rowc,x+4, multi_classAllFeeDiff.get(x).getFee(),cellStyle_left);
						}
					}
					
					/*??????????????? 
					 * ?????????-????????????????????????(????????????)
					 * */
					HSSFSheet classDrugFeeSheet_weekly = workbook.createSheet("?????????-????????????????????????");
					
					String[]seasonList=season.split(" ");
					List<Integer>seasonSort=new ArrayList<Integer>();
					for(int i=0;i<seasonList.length;i++) {
						 String[]s=seasonList[i].split("Q");
						 seasonSort.add(Integer.valueOf(s[1]));
					}
					int targetSeason=Collections.max(seasonSort);
					
					//????????????????????????
					int title=0;
					
					List<ClassDoctorWeeklyDto>classDoctorWeeklyList=results.get(seasonSort.indexOf(targetSeason)).getClassDoctorAllWeekly();
					List<String>weekly=results.get(seasonSort.indexOf(targetSeason)).getWeekly();
					
					for(int i=0;i<classDoctorWeeklyList.size();i++) {
						List<ClassDoctorDto_weekly> classDoctors=classDoctorWeeklyList.get(i).getClassDoctors();
						
						if(i!=0) {
							title=title+weekly.size()+4;
						}
						
						for(int j=0;j<codeTableList.size();j++) {
							if(classDoctorWeeklyList.get(i).getCode().equals(codeTableList.get(j).getCode()))
							{
								String desc_chi=codeTableList.get(j).getDescChi();
								HSSFRow rowHead = classDrugFeeSheet_weekly.createRow(title);
								addRowCell(rowHead, 0,desc_chi , cellStyle_left);
								
								HSSFRow row1 = classDrugFeeSheet_weekly.createRow(title+1);
								HSSFRow row2 = classDrugFeeSheet_weekly.createRow(title+2);
								
								//???????????????????????????????????????
								int weekIndex=0;
								int drugFeeIndex=1;
								int caseCountweekIndex=2;
								
								for(int x=0;x<classDoctors.size();x++) {
									//?????????
									String doctor=classDoctors.get(x).getDoctorName();
									//??????????????????
									List<CaseDotFeeDto> weeklyInfo=classDoctors.get(x).getCaseDotFeeWeekly();
									
									addRowCell(row1, weekIndex+1,doctor, cellStyle_noBorder);
									addRowCell(row2, weekIndex,"??????" , cellStyle_left);
									addRowCell(row2, drugFeeIndex, "????????????", cellStyle_left);
									addRowCell(row2, caseCountweekIndex,"?????????" , cellStyle_left);
									
									if(x==0) {
										for(int y=0;y<weekly.size();y++) {
											HSSFRow row = classDrugFeeSheet_weekly.createRow(title+3+y);
											addRowCell(row, weekIndex, weekly.get(y) , cellStyle_left);
											addRowCell(row, drugFeeIndex, "0" , cellStyle_left);
											addRowCell(row, caseCountweekIndex, "0"  , cellStyle_left);
										}
									}
									else {
										for(int y=0;y<weekly.size();y++) {
											HSSFRow row = classDrugFeeSheet_weekly.getRow(title+3+y);
											addRowCell(row, weekIndex, weekly.get(y) , cellStyle_left);
											addRowCell(row, drugFeeIndex, "0" , cellStyle_left);
											addRowCell(row, caseCountweekIndex, "0"  , cellStyle_left);
										}
									}
									
									for(int y=0;y<weekly.size();y++) {
										String week=weekly.get(y);
										for(int k=0;k<weeklyInfo.size();k++) {
											String week2=weeklyInfo.get(k).getWeek();
											Integer caseCount=weeklyInfo.get(k).getCaseCount();
											Integer drugFee=weeklyInfo.get(k).getDrugFee();
											
											if(week.equals(week2)) {
												HSSFRow row = classDrugFeeSheet_weekly.getRow(title+3+y);
												addRowCell(row, drugFeeIndex, drugFee.toString() , cellStyle_left);
												addRowCell(row, caseCountweekIndex,  caseCount.toString() , cellStyle_left);
											}
										}
										
									}
									
									caseCountweekIndex+=3;
									drugFeeIndex+=3;
									weekIndex+=3;
								}
								
							}
						}
					}	
			  }
		  
			  //????????????
				String fileNameStr = "??????????????????" + "_" + chineseYear+"_"+season.replaceAll("\\s", "_");
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
			  logger.info("????????????????????????????????????: {}",e);
//			  e.printStackTrace();
//			  System.out.println(e);
		  }
	  }
	  
	  public void addRowCell(HSSFRow row,int num,String value,HSSFCellStyle cellStyle) {
			// ???????????????,row?????????????????????,???????????????????????????createCell(),????????????0????????????
			HSSFCell cell = row.createCell(num);
			// ?????????????????????,???A1??????(?????????,?????????)
			cell.setCellValue(value);
		  if(cellStyle!=null) {
			  cell.setCellStyle(cellStyle);
		  }
	  }
	  
	  public void classDrugFeeDiffTemplate(String season,String str,HSSFRow row0,HSSFRow row1,HSSFRow row2,
			  HSSFCellStyle cellStyle_left,List<ClassDrugFeeDto> multi_classAllFeeDiff,String multi_AllFeeDiff) {
		  
		  	StringBuilder title=new StringBuilder();
		  	title.append(season+str);
		  	
			addRowCell(row0,0,title.toString(),null);
			addRowCell(row1,0,"",cellStyle_left);
			addRowCell(row1,1,"",cellStyle_left);
			addRowCell(row1,2,"",cellStyle_left);
			addRowCell(row1,3,"??????",cellStyle_left);
			addRowCell(row2,0, "???????????????",cellStyle_left);
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
		    
		  	StringBuilder title=new StringBuilder();
		  	title.append(season+str);
		  
			addRowCell(row0,0,title.toString(),null);
			addRowCell(row1, 3, "??????", cellStyle_left);
			for(int i=0;i<3;i++) {
				addRowCell(row1, i, "", cellStyle_left);
			}
			
			addRowCell(row2,0,"?????????",cellStyle_left);
			for(int i=1;i<3;i++) {
				addRowCell(row2, i, "", cellStyle_left);
			}
			addRowCell(row2,3,caseCount,cellStyle_left);
			for(int i=0;i<classCaseCount.size();i++) {
				addRowCell(row2,i+4,classCaseCount.get(i).getCaseCount(),cellStyle_left);
			}
			
			addRowCell(row3,0,"???????????????(????????????)",cellStyle_left);
			for(int i=1;i<3;i++) {
				addRowCell(row3, i, "", cellStyle_left);
			}
			addRowCell(row3,3,dot,cellStyle_left);
			for(int i=0;i<classTDot.size();i++) {
				addRowCell(row3,i+4,classTDot.get(i).getDot(),cellStyle_left);
			}
			
			addRowCell(row4,0,"?????????(????????????)",cellStyle_left);
			for(int i=1;i<3;i++) {
				addRowCell(row4, i, "", cellStyle_left);
			}
			addRowCell(row4, 3, drugFee, cellStyle_left);
			for(int i=0;i<classDrugFee.size();i++) {
				addRowCell(row1,i+4,classDrugFee.get(i).getDesc_chi(),cellStyle_left);
				addRowCell(row4,i+4,classDrugFee.get(i).getDot(),cellStyle_left);
			}
			
			addRowCell(row5,0,"????????????",cellStyle_left);
			for(int i=1;i<3;i++) {
				addRowCell(row5, i, "", cellStyle_left);
			}
			for(int i=0;i<classDrugFeeRate.size();i++) {
				addRowCell(row5,i+4,classDrugFeeRate.get(i).getFee(),cellStyle_left);
			}
			addRowCell(row5,3,drugFeeRate,cellStyle_left);
	  }
	  
	 @SuppressWarnings("unchecked")
	public HealthCareCost statisticData(String seasonStr,List<String> seasonList,String year,String weekDate) {
		  
			//?????????/?????? 1.??????????????? 2.????????? 3.???????????? 4.?????????
		  	  StringBuilder OP_AllDot=new StringBuilder();
			  StringBuilder OP_AllDrugFee=new StringBuilder();
			  StringBuilder OP_AllRate=new StringBuilder();
			  StringBuilder AllCount=new StringBuilder();
			  //?????????/?????????????????????
			  StringBuilder FeeDiff=new StringBuilder();
				//????????? 1.??????????????? 2.????????? 3.???????????? 4.?????????
			  StringBuilder OP_Dot=new StringBuilder();
			  StringBuilder OP_DrugFee=new StringBuilder();
			  StringBuilder OP_Rate=new StringBuilder();
			  StringBuilder OPCount=new StringBuilder();
				//?????? 1.??????????????? 2.????????? 3.???????????? 4.?????????
			  StringBuilder IP_Dot=new StringBuilder();
			  StringBuilder IP_DrugFee=new StringBuilder();
			  StringBuilder IP_Rate=new StringBuilder();
			  StringBuilder IPCount=new StringBuilder();
		  
			// ??????????????????/?????????????????????(?????????)
			List<ClassDrugDotDto> classOP_AllList=initDotList();
			//??????????????????/???????????????(???????????????)
			List<ClassDrugFeeDto>lastDrugFee=initFeeList();
			//????????????????????????????????????????????????????????????(?????????) ?????????
			List<ClassDoctorDto>classDoctorAll=initDoctorDotList();
			//??????????????????????????????????????????????????????????????????(?????????) ?????????
			List<ClassDoctorWeeklyDto> classDoctorWeekly=new ArrayList<ClassDoctorWeeklyDto>();
			//??????????????????/?????????????????????
			List<ClassDrugFeeDto>classDrugFeeDiff=initFeeList();
			// ?????????????????????????????????(?????????)
			List<ClassDrugDotDto> classOPList=initDotList();
			// ??????????????????????????????(?????????)
			List<ClassDrugDotDto> classIPList=initDotList();
			//??????????????????/?????????????????????
			List<ClassDrugDotDto> classAll_TDot=initDotList();
			//?????????????????????????????????
			List<ClassDrugDotDto> classOP_TDot=initDotList();
			//??????????????????????????????
			List<ClassDrugDotDto> classIP_TDot=initDotList();
			//??????????????????/??????????????????
			List<ClassDrugFeeDto> classAllDrugFeeRate=initFeeList();
			//??????????????????????????????
			List<ClassDrugFeeDto> classOPDrugFeeRate=initFeeList();
			//???????????????????????????
			List<ClassDrugFeeDto> classIPDrugFeeRate=initFeeList();
			//??????????????????/????????????
			List<ClassCaseCountDto> classAllCaseCount=initCaseList();
			//????????????????????????
			List<ClassCaseCountDto> classOPCaseCount=initCaseList();
			//?????????????????????
			List<ClassCaseCountDto> classIPCaseCount=initCaseList();
			//??????
			List<String>weekly=new ArrayList<String>();
			Object[] objects=new Object[2];
		  
			try {
				
				objects=calculateFeeWeekly(classDoctorWeekly,year,weekDate);
				
				if(objects[0]!=null) {
					classDoctorWeekly=(List<ClassDoctorWeeklyDto>) objects[0];
				}
				if(objects[1]!=null) {
					weekly=(List<String>) objects[1];
				}
				
				//?????????/?????? 1.??????????????? 2.????????? 3.???????????? 4.?????????
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
				
				//????????? 1.??????????????? 2.????????? 3.???????????? 4.?????????
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
				
				//?????? 1.??????????????? 2.????????? 3.???????????? 4.?????????
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
				
				//?????????????????????????????????
				List<Object[]>OP_ClassTDot=opdDao.findClassOP_TDot(optDao.findByFeeYmListOrderById(seasonList));
				//??????????????????????????????
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
				
				//??????????????????/?????????????????????
				for(int a=0;a<classAll_TDot.size();a++) {
					String code=classAll_TDot.get(a).getCode();
					int num1=checkIntgerNull(classAll_TDot.get(a).getDot());
					ClassDrugDotDto classDrugDotDto=classAll_TDot.get(a);
					
					for(int b=0;b<OP_ClassTDot.size();b++) {
						if(code.equals(OP_ClassTDot.get(b)[0].toString())) {
							if(OP_ClassTDot.get(b)[1]!=null) {
								int num2=checkIntgerNull(OP_ClassTDot.get(b)[1]);
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
								int num2=checkIntgerNull(IP_ClassTDot.get(b)[1]);
								int num3=num1+num2;
								classDrugDotDto.setDot(String.valueOf(num3));
							}
							classAll_TDot.set(a,classDrugDotDto);
						}
					}
				}
				
				//???????????????????????????????????????
				List<Object[]>OP_ClassDrugDot=opdDao.findByOptIdAndGroupByFuncType(optDao.findByFeeYmListOrderById(seasonList));
				//????????????????????????????????????
				List<Object[]>IP_ClassDrugDot=ipdDao.findByIptIdAndGroupByFuncType(iptDao.findByFeeYmListOrderById(seasonList));
				
				//??????????????????/?????????????????????
				for(int a=0;a<classOP_AllList.size();a++) {
					String code=classOP_AllList.get(a).getCode();
					int num1=checkIntgerNull(classOP_AllList.get(a).getDot());
					ClassDrugDotDto classDrugDotDto=classOP_AllList.get(a);
					
					for(int b=0;b<OP_ClassDrugDot.size();b++) {
						if(code.equals(OP_ClassDrugDot.get(b)[0].toString())) {
							if(OP_ClassDrugDot.get(b)[1]!=null) {
								int num2=checkIntgerNull(OP_ClassDrugDot.get(b)[1]);
								int num3=num1+num2;
								classDrugDotDto.setDot(String.valueOf(num3));
							}
							classOP_AllList.set(a, classDrugDotDto);
						}
					}
				}
				
				//?????????????????????????????????
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
				
				//??????????????????/?????????????????????
				for(int a=0;a<classOP_AllList.size();a++) {
					String code=classOP_AllList.get(a).getCode();
					int num1=checkIntgerNull(classOP_AllList.get(a).getDot());
					ClassDrugDotDto classDrugDotDto=classOP_AllList.get(a);
					
					for(int b=0;b<IP_ClassDrugDot.size();b++) {
						if(code.equals(IP_ClassDrugDot.get(b)[0].toString())) {
							if(IP_ClassDrugDot.get(b)[1]!=null) {
								int num2=checkIntgerNull(IP_ClassDrugDot.get(b)[1]);
								int num3=num1+num2;
								classDrugDotDto.setDot(String.valueOf(num3));
							}
							classOP_AllList.set(a,classDrugDotDto);
							
						}
					}
				}
				
				//??????????????????????????????
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
				
				//?????????/?????????????????????(????????????????????????)
				int currentFee=checkIntgerNull(OP_AllDrugFee.toString());
				int lastFee=checkIntgerNull(opdDao.findTDrugFee(optDao.findByFeeYmListOrderById(getLastSeason(seasonList)),iptDao.findByFeeYmListOrderById(getLastSeason(seasonList))));
				FeeDiff.append(String.valueOf(currentFee-lastFee));
				
				//???????????????????????????(???????????????)
				List<Object[]>last_OP_ClassDrugFee=opdDao.findByOptIdAndGroupByFuncType(optDao.findByFeeYmListOrderById(getLastSeason(seasonList)));
				//????????????????????????(???????????????)
				List<Object[]>last_IP_ClassDrugFee=ipdDao.findByIptIdAndGroupByFuncType(iptDao.findByFeeYmListOrderById(getLastSeason(seasonList)));
				
				//??????????????????/???????????????(???????????????)------------------------------------------------
				
				for(int a=0;a<lastDrugFee.size();a++) {
					String code=lastDrugFee.get(a).getCode();
					int num1=checkIntgerNull(lastDrugFee.get(a).getFee());
					ClassDrugFeeDto classDrugFeeDto=lastDrugFee.get(a);
					
					for(int b=0;b<last_OP_ClassDrugFee.size();b++) {
						if(code.equals(last_OP_ClassDrugFee.get(b)[0].toString())) {
							if(last_OP_ClassDrugFee.get(b)[1]!=null) {
								int num2=checkIntgerNull(last_OP_ClassDrugFee.get(b)[1]);
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
								int num2=checkIntgerNull(last_IP_ClassDrugFee.get(b)[1]);
								int num3=num1+num2;
								classDrugFeeDto.setFee(String.valueOf(num3));
							}
							lastDrugFee.set(a, classDrugFeeDto);
						}
					}
				}
				
				//-----------------------------------------------------------------------------
				
				//??????????????????/?????????????????????(????????????????????????)---------------------------------------
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
				
				//??????????????????/??????????????????
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
				
				//??????????????????????????????
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
				
				//???????????????????????????
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
				
				
				//???????????????????????????
				List<Object[]>class_op_case_count=opdDao.findClassOPCount(optDao.findByFeeYmListOrderById(seasonList));
				//????????????????????????
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
				
				//??????????????????/???????????????
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
				
				//?????????????????????????????????????????????????????????????????????(?????????)
				List<Object[]>classDoctorOP=opdDao.findOPClassDoctor(optDao.findByFeeYmListOrderById(seasonList));
				//??????????????????????????????????????????????????????????????????(?????????)
				List<Object[]>classDoctorIP=ipdDao.findIPClassDoctor(iptDao.findByFeeYmListOrderById(seasonList));
				
				//???????????????????????????/????????????????????????????????????????????????(?????????)
				for(int a=0;a<classDoctorAll.size();a++) {
					String desc_chi=classDoctorAll.get(a).getDesc_chi();
					String code=classDoctorAll.get(a).getCode();
					Map<String,CaseDotFeeDto> doctors=classDoctorAll.get(a).getDoctors();
					
					for(int b=0;b<classDoctorOP.size();b++) {
						if(code.equals(classDoctorOP.get(b)[0].toString())) {
								if(classDoctorOP.get(b)[1]!=null) {
									String doctorID=classDoctorOP.get(b)[1].toString();
									int caseCount=checkIntgerNull(classDoctorOP.get(b)[2]);
									int dot=checkIntgerNull(classDoctorOP.get(b)[3]);
									int fee=checkIntgerNull(classDoctorOP.get(b)[4]);
									doctors.put(doctorID,new CaseDotFeeDto("",caseCount,dot,fee));
									classDoctorAll.set(a,new ClassDoctorDto(desc_chi,code,doctors));
								}
						}
					}
				}
				
				for(int a=0;a<classDoctorAll.size();a++) {
					String desc_chi=classDoctorAll.get(a).getDesc_chi();
					String code=classDoctorAll.get(a).getCode();
					Map<String,CaseDotFeeDto> doctors=classDoctorAll.get(a).getDoctors();
					
					for(int b=0;b<classDoctorIP.size();b++) {
						if(code.equals(classDoctorIP.get(b)[0].toString()) && classDoctorIP.get(b)[1]!=null){
							if(doctors.containsKey(classDoctorIP.get(b)[1].toString())==true) {
								String doctorID=classDoctorIP.get(b)[1].toString();
								int caseCount=checkIntgerNull(classDoctorIP.get(b)[2]);
								int dot=checkIntgerNull(classDoctorIP.get(b)[3]);
								int fee=checkIntgerNull(classDoctorIP.get(b)[4]);
								
								int caseCountAll=doctors.get(doctorID).getCaseCount();
								int dotAll=doctors.get(doctorID).getDot();
								int feeAll=doctors.get(doctorID).getDrugFee();
								
								doctors.replace(doctorID,new CaseDotFeeDto("",caseCount+caseCountAll,
										dot+dotAll, fee+feeAll));
								classDoctorAll.set(a,new ClassDoctorDto(desc_chi,code,doctors));
							}
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
					,classOPCaseCount,classIPCaseCount,classAllCaseCount,classDoctorAll
					,classDoctorWeekly,weekly);
	  }
	  
	  public HealthCareCost setHealthCareCost(String season,String OP_AllDot, String OP_AllDrugFee,String OP_AllRate,String AllCount,
			  List<ClassDrugDotDto>classOPAll,List<ClassDrugFeeDto> classDrugFeeDiff,String FeeDiff,
			  String OP_Dot,String OP_DrugFee,String OP_Rate,String OPCount,List<ClassDrugDotDto>classOP,
			  String IP_Dot,String IP_DrugFee,String IP_Rate,String IPCount,List<ClassDrugDotDto>classIP,
			  List<ClassDrugDotDto> classOP_TDot,List<ClassDrugDotDto> classIP_TDot,List<ClassDrugDotDto>classAll_TDot,
			  List<ClassDrugFeeDto>classOPDrugFeeRate,List<ClassDrugFeeDto>classIPDrugFeeRate,List<ClassDrugFeeDto>classAllDrugFeeRate,
			  List<ClassCaseCountDto> classOPCaseCount,List<ClassCaseCountDto> classIPCaseCount,List<ClassCaseCountDto> classAllCaseCount,
			  List<ClassDoctorDto>classDoctorAll,List<ClassDoctorWeeklyDto> classDoctorWeekly,List<String>weekly){ 
		  	HealthCareCost healthCareCost=new HealthCareCost();
			healthCareCost.setSeason(season);
			healthCareCost.setAllDot(OP_AllDot);
			healthCareCost.setAllDrugFee(OP_AllDrugFee);
			healthCareCost.setAllRate(OP_AllRate);
			healthCareCost.setAllCount(AllCount);
			healthCareCost.setClassAll(classOPAll);
			healthCareCost.setClassDoctorAll(classDoctorAll);
			healthCareCost.setClassAll_TDot(classAll_TDot);
			healthCareCost.setClassAllFeeRate(classAllDrugFeeRate);
			healthCareCost.setClassAllCaseCount(classAllCaseCount);
			healthCareCost.setClassDoctorAllWeekly(classDoctorWeekly);
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
			
			healthCareCost.setWeekly(weekly);
			
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
	  
	  public int checkIntgerNull(Object obj) {
		  Integer num =null;
		  if(obj != null){
			  num = Integer.parseInt(obj.toString());
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
	  
	  public Object[] calculateFeeWeekly(List<ClassDoctorWeeklyDto> classDoctorWeekly,String year,String day) {
		  
		  List<String> weekly=new ArrayList<String>();
		  
		  try {
			  	DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			  
			  	StringBuilder yearDay=new StringBuilder();
			  	yearDay.append(year);
			  	yearDay.append(day);
			  
			  	//????????????
			  	Date startDate=dateFormat.parse(yearDay.toString());
			  	Calendar startCal = getLastYearDay(startDate);
			  
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, startCal.get(Calendar.YEAR));
				cal.set(Calendar.MONTH, startCal.get(Calendar.MONTH));
				cal.set(Calendar.DAY_OF_YEAR, startCal.get(Calendar.DAY_OF_YEAR));
	
				if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
					cal.add(Calendar.DAY_OF_YEAR, Calendar.SUNDAY - cal.get(Calendar.DAY_OF_WEEK));
				}
				//????????????
			  	Date endDate=dateFormat.parse(yearDay.toString());
			  	Calendar calMax = Calendar.getInstance();
			  	calMax.setTime(endDate);
			  	
				List<String> codes = new ArrayList<String>();
				List<String> doctors = new ArrayList<String>();
				
				do {
					Date start = cal.getTime();
					cal.add(Calendar.DAY_OF_YEAR, 6);
					Date end = cal.getTime();
					
					//????????????????????????????????????????????????????????????????????????????????????  
					String weekStart = DateTool.convertToChineseYear(dateFormat.format(start));
					String weekEnd= DateTool.convertToChineseYear(dateFormat.format(end));
					
					//?????????????????????????????????????????????
					List<Object[]>classDoctorOP=opdDao.findOPClassDoctorWeekly(weekStart, weekEnd);
					//??????????????????????????????????????????
					List<Object[]>classDoctorIP=ipdDao.findIPClassDoctorWeekly(weekStart, weekEnd);
				  	
					//??????/???
					StringBuilder week=new StringBuilder();
					week.append(cal.get(Calendar.YEAR));
					week.append("w");
					week.append(cal.get(Calendar.WEEK_OF_YEAR));
					
					weekly.add(week.toString());
					
					//???????????????????????????/????????????????????????
					for(int i=0;i<classDoctorOP.size();i++) {
						if(classDoctorOP.get(i)!=null) {
							String code="";
							String doctorName="";
							Integer caseCount=0;
							Integer dot=0;
							Integer drugFee=0;
							if(classDoctorOP.get(i)[0]!=null) {
								code=classDoctorOP.get(i)[0].toString();
							}
							if(classDoctorOP.get(i)[1]!=null) {
								doctorName=classDoctorOP.get(i)[1].toString();
							}
							if(classDoctorOP.get(i)[2]!=null) {
								caseCount=Integer.parseInt(classDoctorOP.get(i)[2].toString());
							}
							if(classDoctorOP.get(i)[3]!=null) {
								dot=Integer.parseInt(classDoctorOP.get(i)[3].toString());
							}
							if(classDoctorOP.get(i)[4]!=null) {
								drugFee=Integer.parseInt(classDoctorOP.get(i)[4].toString());
							}
							
							if(codes.contains(code)) {
								if(doctors.contains(doctorName)) {
									
									for(int j=0;j<classDoctorWeekly.size();j++) {
										ClassDoctorWeeklyDto cw=classDoctorWeekly.get(j);
										if(code.equals(cw.getCode())) {
											List<ClassDoctorDto_weekly> classDoctorDtos_weekly=cw.getClassDoctors();
											for(int k=0;k<classDoctorDtos_weekly.size();k++) {
												if(doctorName.equals(classDoctorDtos_weekly.get(k).getDoctorName())) {
													List<CaseDotFeeDto>caseDotFeeDtos=classDoctorDtos_weekly.get(k).getCaseDotFeeWeekly();
													caseDotFeeDtos.add(new CaseDotFeeDto(week.toString(),caseCount,dot,drugFee));
													classDoctorDtos_weekly.set(k,new ClassDoctorDto_weekly(doctorName,caseDotFeeDtos));
												}
											}
											classDoctorWeekly.set(j,new ClassDoctorWeeklyDto(code,classDoctorDtos_weekly));
										}
									}
									
								}
								else {
									for(int j=0;j<classDoctorWeekly.size();j++) {
										ClassDoctorWeeklyDto cw=classDoctorWeekly.get(j);
										if(code.equals(cw.getCode())) {
											List<ClassDoctorDto_weekly> classDoctorDtos_weekly=cw.getClassDoctors();
											List<CaseDotFeeDto>caseDotFeeDtos=new ArrayList<CaseDotFeeDto>();
											caseDotFeeDtos.add(new CaseDotFeeDto(week.toString(),caseCount,dot,drugFee));
											classDoctorDtos_weekly.add(new ClassDoctorDto_weekly(doctorName,caseDotFeeDtos));
											classDoctorWeekly.set(j,new ClassDoctorWeeklyDto(code,classDoctorDtos_weekly));
										}
									}
									doctors.add(doctorName);
								}
							}
							else {
								List<ClassDoctorDto_weekly> classDoctorDtos_weekly=new ArrayList<ClassDoctorDto_weekly>();
								List<CaseDotFeeDto>caseDotFeeDtos=new ArrayList<CaseDotFeeDto>();
								
								caseDotFeeDtos.add(new CaseDotFeeDto(week.toString(),caseCount,dot,drugFee));
								classDoctorDtos_weekly.add(new ClassDoctorDto_weekly(doctorName,caseDotFeeDtos));
								classDoctorWeekly.add(new ClassDoctorWeeklyDto(code,classDoctorDtos_weekly));
								
								codes.add(code);
								doctors.add(doctorName);
							}
						}
					}
					
					for(int i=0;i<classDoctorIP.size();i++) {
						if(classDoctorIP.get(i)!=null) {
							String code="";
							String doctorName="";
							Integer caseCount=0;
							Integer dot=0;
							Integer drugFee=0;
							if(classDoctorIP.get(i)[0]!=null) {
								code=classDoctorIP.get(i)[0].toString();
							}
							if(classDoctorIP.get(i)[1]!=null) {
								doctorName=classDoctorIP.get(i)[1].toString();
							}
							if(classDoctorIP.get(i)[2]!=null) {
								caseCount=Integer.parseInt(classDoctorIP.get(i)[2].toString());
							}
							if(classDoctorIP.get(i)[3]!=null) {
								dot=Integer.parseInt(classDoctorIP.get(i)[3].toString());
							}
							if(classDoctorIP.get(i)[4]!=null) {
								drugFee=Integer.parseInt(classDoctorIP.get(i)[4].toString());
							}
							
							if(codes.contains(code)) {
								if(doctors.contains(doctorName)) {
									
									for(int j=0;j<classDoctorWeekly.size();j++) {
										ClassDoctorWeeklyDto cw=classDoctorWeekly.get(j);
										if(code.equals(cw.getCode())) {
											List<ClassDoctorDto_weekly> classDoctorDtos_weekly=cw.getClassDoctors();
											for(int k=0;k<classDoctorDtos_weekly.size();k++) {
												if(doctorName.equals(classDoctorDtos_weekly.get(k).getDoctorName())) {
													List<CaseDotFeeDto>caseDotFeeDtos=classDoctorDtos_weekly.get(k).getCaseDotFeeWeekly();
													for(int z=0;z<caseDotFeeDtos.size();z++) {
														String weekStr=caseDotFeeDtos.get(z).getWeek();
														if(week.toString().equals(weekStr)) {
															int oldCaseCount=caseDotFeeDtos.get(z).getCaseCount();
															int oldDot=caseDotFeeDtos.get(z).getDot();
															int oldDrugFee=caseDotFeeDtos.get(z).getDrugFee();
															caseDotFeeDtos.get(z).setCaseCount(oldCaseCount + caseCount);
															caseDotFeeDtos.get(z).setDot(oldDot + dot);
															caseDotFeeDtos.get(z).setDrugFee(oldDrugFee + drugFee);
														}
													}
													classDoctorDtos_weekly.set(k,new ClassDoctorDto_weekly(doctorName,caseDotFeeDtos));
												}
											}
											classDoctorWeekly.set(j,new ClassDoctorWeeklyDto(code,classDoctorDtos_weekly));
										}
									}
									
								}
								else {
									for(int j=0;j<classDoctorWeekly.size();j++) {
										ClassDoctorWeeklyDto cw=classDoctorWeekly.get(j);
										if(code.equals(cw.getCode())) {
											List<ClassDoctorDto_weekly> classDoctorDtos_weekly=cw.getClassDoctors();
											List<CaseDotFeeDto>caseDotFeeDtos=new ArrayList<CaseDotFeeDto>();
											caseDotFeeDtos.add(new CaseDotFeeDto(week.toString(),caseCount,dot,drugFee));
											classDoctorDtos_weekly.add(new ClassDoctorDto_weekly(doctorName,caseDotFeeDtos));
											classDoctorWeekly.set(j,new ClassDoctorWeeklyDto(code,classDoctorDtos_weekly));
										}
									}
									doctors.add(doctorName);
								}
							}
							else {
								List<ClassDoctorDto_weekly> classDoctorDtos_weekly=new ArrayList<ClassDoctorDto_weekly>();
								List<CaseDotFeeDto>caseDotFeeDtos=new ArrayList<CaseDotFeeDto>();
								
								caseDotFeeDtos.add(new CaseDotFeeDto(week.toString(),caseCount,dot,drugFee));
								classDoctorDtos_weekly.add(new ClassDoctorDto_weekly(doctorName,caseDotFeeDtos));
								classDoctorWeekly.add(new ClassDoctorWeeklyDto(code,classDoctorDtos_weekly));
								
								codes.add(code);
								doctors.add(doctorName);
							}
						}
					}
					
					cal.add(Calendar.DAY_OF_YEAR, 1);
					
				} while (cal.before(calMax));
				logger.info("??????????????????-????????????????????????????????? done");
				
		  }
		  catch (Exception e) {
//				  e.printStackTrace();
				  logger.info("calculateFeeWeekly error {}",e);
		  }
		  
		  Object[]objects={classDoctorWeekly,weekly};
		  
		  return objects;
	  }
	  
	  public List<ClassDoctorDto> initDoctorDotList(){
		  List<ClassDoctorDto>list=new ArrayList<ClassDoctorDto>();
		  for(int i=0;i<codeTableList.size();i++) {
			  list.add(new ClassDoctorDto(codeTableList.get(i).getDescChi(),codeTableList.get(i).getCode(),new HashedMap<String, CaseDotFeeDto>()));
		  }
		  return list;
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
	  
	  //?????????????????????
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
					logger.info("getLastSeason error {}",e);
//					e.printStackTrace();
				}
				
				day.append(lastYear+current.get(i).substring(3));
				last.add(day.toString());
				
		  }

		  return last;
	  }
	  
	//?????????????????????
	  public Calendar getLastYearDay(Date day){
		  Calendar instance = null;
			try {
			    instance = Calendar.getInstance();
				instance.setTime(day);
				instance.add(Calendar.YEAR, -1);
			} catch (Exception e) {
//				e.printStackTrace();
				logger.info("getLastYearDay error {}",e);
			}
			
		return instance;
	  }
}
