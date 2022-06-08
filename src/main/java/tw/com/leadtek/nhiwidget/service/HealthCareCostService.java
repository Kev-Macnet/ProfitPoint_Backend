package tw.com.leadtek.nhiwidget.service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import tw.com.leadtek.nhiwidget.dto.ClassDrugDotDto;
import tw.com.leadtek.nhiwidget.dto.ClassDrugFeeDto;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
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
	  
	  private List<CODE_TABLE> codeTableList;
	  
		//門急診/住院 1.病例總點數 2.總藥費 3.藥費佔率
	  private StringBuilder OP_AllDot=new StringBuilder();
	  private StringBuilder OP_AllDrugFee=new StringBuilder();
	  private StringBuilder OP_AllRate=new StringBuilder();
		//門急診/住院總藥費差異
	  private StringBuilder FeeDiff=new StringBuilder();
		//門急診 1.病例總點數 2.總藥費 3.藥費佔率
	  private StringBuilder OP_Dot=new StringBuilder();
	  private StringBuilder OP_DrugFee=new StringBuilder();
	  private StringBuilder OP_Rate=new StringBuilder();
		//住院 1.病例總點數 2.總藥費 3.藥費佔率
	  private StringBuilder IP_Dot=new StringBuilder();
	  private StringBuilder IP_DrugFee=new StringBuilder();
	  private StringBuilder IP_Rate=new StringBuilder();
	  
	  private DecimalFormat df = new DecimalFormat("######0.0000");
	  
	  public List<HealthCareCost> getData(String year,String season,List<HealthCareCost> results) {
		  
			String[] seasons=season.split(" ");
			
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
	  
	  public HealthCareCost statisticData(String seasonStr,List<String> seasonList) {
		  
			// 各科別門急診/住院總點數
			List<ClassDrugDotDto> classOP_AllList=initDotList();
			//各科別門急診/住院總藥費差異
			List<ClassDrugFeeDto>classDrugFeeDiff=initFeeList();
			// 各科別門急診總點數
			List<ClassDrugDotDto> classOPList=initDotList();
			// 各科別住院總點數
			List<ClassDrugDotDto> classIPList=initDotList();
			
			OP_AllDot.setLength(0);
			OP_AllDrugFee.setLength(0);
			OP_AllRate.setLength(0);
			FeeDiff.setLength(0);
			OP_Dot.setLength(0);
			OP_DrugFee.setLength(0);
			OP_Rate.setLength(0);
			IP_Dot.setLength(0);
			IP_DrugFee.setLength(0);
			IP_Rate.setLength(0);
		  
			try {
				//門急診/住院 1.病例總點數 2.總藥費 3.藥費佔率
				OP_AllDot.append(opdDao.findTDot(optDao.findByFeeYmListOrderById(seasonList),iptDao.findByFeeYmListOrderById(seasonList)));
				OP_AllDrugFee.append(opdDao.findTDrugFee(optDao.findByFeeYmListOrderById(seasonList),iptDao.findByFeeYmListOrderById(seasonList)));
				OP_AllRate.append(df.format(Double.parseDouble(OP_AllDrugFee.toString())/Double.parseDouble(OP_AllDot.toString())));
				
				//門急診 1.病例總點數 2.總藥費 3.藥費佔率
				OP_Dot.append(opdDao.findOPDot(optDao.findByFeeYmListOrderById(seasonList)));
				OP_DrugFee.append(opdDao.findOPDrugFee(optDao.findByFeeYmListOrderById(seasonList)));
				OP_Rate.append(df.format(Double.parseDouble(OP_DrugFee.toString())/Double.parseDouble(OP_Dot.toString())));
				
				//住院 1.病例總點數 2.總藥費 3.藥費佔率
				IP_Dot.append(ipdDao.findIPDot(iptDao.findByFeeYmListOrderById(seasonList)));
				IP_DrugFee.append(ipdDao.findIPDrugFee(iptDao.findByFeeYmListOrderById(seasonList)));
				IP_Rate.append(df.format(Double.parseDouble(IP_DrugFee.toString())/Double.parseDouble(IP_Dot.toString())));
				
				//取得各科別門急診總藥品點數
				List<Object[]>OP_ClassDrugDot=opdDao.findByOptIdAndGroupByFuncType(optDao.findByFeeYmListOrderById(seasonList));
				//取得各科別住院總藥品點數
				List<Object[]>IP_ClassDrugDot=ipdDao.findByIptIdAndGroupByFuncType(iptDao.findByFeeYmListOrderById(seasonList));
				
				//各科別門急診/住院總藥品點數、各科別門急診總藥品點數
				for(int a=0;a<classOP_AllList.size();a++) {
					String code=classOP_AllList.get(a).getCode();
					int num1=Integer.parseInt(classOP_AllList.get(a).getDot());
					ClassDrugDotDto classDrugDotDto=classOP_AllList.get(a);
					
					for(int b=0;b<OP_ClassDrugDot.size();b++) {
						if(code.equals(OP_ClassDrugDot.get(b)[0].toString())) {
							int num2=Integer.parseInt(OP_ClassDrugDot.get(b)[1].toString());
							
							classDrugDotDto.setDot(String.valueOf(num1+=num2));
							classOP_AllList.set(a, classDrugDotDto);
							
							classDrugDotDto.setDot(String.valueOf(num2));
							classOPList.set(a, classDrugDotDto);
						}
					}
				}
				
				//各科別門急診/住院總藥品點數、各科別住院總藥品點數
				for(int a=0;a<classOP_AllList.size();a++) {
					String code=classOP_AllList.get(a).getCode();
					int num1=Integer.parseInt(classOP_AllList.get(a).getDot());
					ClassDrugDotDto classDrugDotDto=classOP_AllList.get(a);
					
					for(int b=0;b<IP_ClassDrugDot.size();b++) {
						if(code.equals(IP_ClassDrugDot.get(b)[0].toString())) {
							int num2=Integer.parseInt(IP_ClassDrugDot.get(b)[1].toString());
							
							classDrugDotDto.setDot(String.valueOf(num1+=num2));
							classOP_AllList.set(a,classDrugDotDto);
							
							classDrugDotDto.setDot(String.valueOf(num2));
							classIPList.set(a,classDrugDotDto);
						}
					}
				}
				
				//門急診/住院總藥費差異(與前一年同一季相比)
				int currentFee=Integer.valueOf(OP_AllDrugFee.toString());
				int lastFee=Integer.valueOf(opdDao.findTDrugFee(optDao.findByFeeYmListOrderById(getLastSeason(seasonList)),iptDao.findByFeeYmListOrderById(getLastSeason(seasonList))));
				FeeDiff.append(String.valueOf(currentFee-lastFee));
				
				//各科別門急診總藥費(去年同一季)
				List<Object[]>last_OP_ClassDrugFee=opdDao.findByOptIdAndGroupByFuncType(optDao.findByFeeYmListOrderById(getLastSeason(seasonList)));
				//各科別住院總藥費(去年同一季)
				List<Object[]>last_IP_ClassDrugFee=ipdDao.findByIptIdAndGroupByFuncType(iptDao.findByFeeYmListOrderById(getLastSeason(seasonList)));
				
				//各科別門急診/住院總藥費(去年同一季)------------------------------------------------
				List<ClassDrugFeeDto>lastDrugFee=initFeeList();
				
				for(int a=0;a<lastDrugFee.size();a++) {
					String code=lastDrugFee.get(a).getCode();
					int num1=Integer.parseInt(lastDrugFee.get(a).getFee());
					ClassDrugFeeDto classDrugFeeDto=lastDrugFee.get(a);
					
					for(int b=0;b<last_OP_ClassDrugFee.size();b++) {
						if(code.equals(last_OP_ClassDrugFee.get(b)[0].toString())) {
							int num2=Integer.parseInt(last_OP_ClassDrugFee.get(b)[1].toString());
							
							classDrugFeeDto.setFee(String.valueOf(num1+=num2));
							lastDrugFee.set(a, classDrugFeeDto);
						}
					}
				}
				
				for(int a=0;a<lastDrugFee.size();a++) {
					String code=lastDrugFee.get(a).getCode();
					int num1=Integer.parseInt(lastDrugFee.get(a).getFee());
					ClassDrugFeeDto classDrugFeeDto=lastDrugFee.get(a);
					
					for(int b=0;b<last_IP_ClassDrugFee.size();b++) {
						if(code.equals(last_IP_ClassDrugFee.get(b)[0].toString())) {
							int num2=Integer.parseInt(last_IP_ClassDrugFee.get(b)[1].toString());
							
							classDrugFeeDto.setFee(String.valueOf(num1+=num2));
							lastDrugFee.set(a, classDrugFeeDto);
						}
					}
				}
				
				//-----------------------------------------------------------------------------
				
				//各科別門急診/住院總藥費差異(與去年同一季相比)---------------------------------------
				for(int a=0;a<classDrugFeeDiff.size();a++) {
					String code=classDrugFeeDiff.get(a).getCode();
					int num1=Integer.parseInt(classDrugFeeDiff.get(a).getFee());
					ClassDrugFeeDto classDrugFeeDto=classDrugFeeDiff.get(a);
					
					for(int b=0;b<classOP_AllList.size();b++) {
						if(code.equals(classOP_AllList.get(b).getCode())) {
							int num2=Integer.parseInt(classOP_AllList.get(b).getDot());
							classDrugFeeDto.setFee(String.valueOf(num1+=num2));
							classDrugFeeDiff.set(a, classDrugFeeDto);
						}
						
						if(code.equals(lastDrugFee.get(b).getCode())) {
							int num2=Integer.parseInt(lastDrugFee.get(b).getFee());
							classDrugFeeDto.setFee(String.valueOf(num1-=num2));
							classDrugFeeDiff.set(a, classDrugFeeDto);
						}
					}
				}
				
				//----------------------------------------------------------------------------
				
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("health care cost exception {}",e);
//				System.out.println(e.toString());
//				e.printStackTrace();
			}
			
			return setHealthCareCost(seasonStr
					,OP_AllDot.toString(),OP_AllDrugFee.toString(),OP_AllRate.toString(),classOP_AllList
					,classDrugFeeDiff,FeeDiff.toString()
					,OP_Dot.toString(),OP_DrugFee.toString(),OP_Rate.toString(),classOPList
					,IP_Dot.toString(),IP_DrugFee.toString(),IP_Rate.toString(),classIPList);
	  }
	  
	  public HealthCareCost setHealthCareCost(String season,String OP_AllDot, String OP_AllDrugFee,String OP_AllRate,
			  List<ClassDrugDotDto>classOPAll,List<ClassDrugFeeDto> classDrugFeeDiff,String FeeDiff,
			  String OP_Dot,String OP_DrugFee,String OP_Rate,List<ClassDrugDotDto>classOP,
			  String IP_Dot,String IP_DrugFee,String IP_Rate,List<ClassDrugDotDto>classIP){ 
		  	HealthCareCost healthCareCost=new HealthCareCost();
			healthCareCost.setSeason(season);
			healthCareCost.setAllDot(OP_AllDot);
			healthCareCost.setAllDrugFee(OP_AllDrugFee);
			healthCareCost.setAllRate(OP_AllRate);
			healthCareCost.setClassAll(classOPAll);
			healthCareCost.setClassAllFeeDiff(classDrugFeeDiff);
			healthCareCost.setAllFeeDiff(FeeDiff);
			healthCareCost.setOP_Dot(OP_Dot);
			healthCareCost.setOP_DrugFee(OP_DrugFee);
			healthCareCost.setOP_Rate(OP_Rate);
			healthCareCost.setClassOP(classOP);
			healthCareCost.setIP_Dot(IP_Dot);
			healthCareCost.setIP_DrugFee(IP_DrugFee);
			healthCareCost.setIP_Rate(IP_Rate);
			healthCareCost.setClassIP(classIP);
			
			return healthCareCost;
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
