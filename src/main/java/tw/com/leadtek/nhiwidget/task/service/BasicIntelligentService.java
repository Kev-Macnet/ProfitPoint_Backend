package tw.com.leadtek.nhiwidget.task.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.dao.IP_DDao;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_DDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dto.PtOutpatientFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.IP_D;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.model.rdb.OP_D;
import tw.com.leadtek.nhiwidget.service.IntelligentService;
import tw.com.leadtek.tools.DateTool;
/**
 * 支付準則共用模組
 *
 */
@Service
public class BasicIntelligentService {
	@Autowired
	private MRDao mrDao;
	@Autowired
	private IP_PDao ippDao;
	@Autowired
	private OP_PDao oppDao;
	@Autowired
	private IP_DDao ipdDao;
	@Autowired
	private OP_DDao opdDao;
	@Autowired
	private IntelligentService intelligentService;

	/**
	 * 檢查支付準則是否時間內且是門診|住院
	 * @param start_date(起始時間)
	 * @param end_date(結束時間)
	 * @param nhi_no(支付準則)
	 * @param outpatient_type(是否為門診)
	 * @param hospitalized_type(是否為住院)
	 * @return map
	 * @throws ParseException
	 */
	public Map<String, Object> vaidIntelligentTtype(long start_date, long end_date, String nhi_no, int outpatient_type, int hospitalized_type)
			throws ParseException {
		Map<String, Object> retMap = new HashMap<String, Object>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		/// 將timestamp轉成date
		java.sql.Timestamp tSdate = new java.sql.Timestamp(start_date);
		java.sql.Timestamp tEdate = new java.sql.Timestamp(end_date);
		Date tsd = new Date(tSdate.getTime());
		Date ted = new Date(tEdate.getTime());
		String sDateStr = sdf.format(tsd);
		String eDateStr = sdf.format(ted);
		boolean isHospital = false;
		boolean isOutpatien = false;
		if(hospitalized_type == 0) {isHospital = false;} else {isHospital = true;}
		if(outpatient_type == 0) {isOutpatien = false;} else {isOutpatien = true;}
		
		List<MR> mrList = new ArrayList<MR>();
		if(isHospital && isOutpatien) {
			
			/// 該支付準則區間病歷表,全部
			mrList = mrDao.getIntelligentMR(sDateStr, eDateStr,  "%," + nhi_no + ",%");
		}
		else if(!isOutpatien || !isHospital) {
			if(isOutpatien) {
				/// 該支付準則區間病歷表,門診
				mrList = mrDao.getIntelligentMRO(sDateStr, eDateStr, "%," + nhi_no + ",%");
			}
			else if(isHospital) {
				/// 該支付準則區間病歷表,住院
				mrList = mrDao.getIntelligentMRH(sDateStr, eDateStr, "%," + nhi_no + ",%");
			}
		}
		/// 存放mrID
		List<String> mrIdListStr = new ArrayList<String>();
		/// 提取將該診斷碼之ID
		for (MR mr : mrList) {

			mrIdListStr.add(mr.getId().toString());
		}
		if (hospitalized_type == 0) {
			for (MR r : mrList) {
				if (r.getDataFormat().equals("20")) {
					intelligentService.insertIntelligent(r, INTELLIGENT_REASON.VIOLATE.value(), nhi_no,
							String.format("(醫令代碼)%s不適用住院就醫方式", nhi_no), true);
				}
			}
		} else if (outpatient_type == 0) {
			for (MR r : mrList) {
				if (r.getDataFormat().equals("10")) {
					intelligentService.insertIntelligent(r, INTELLIGENT_REASON.VIOLATE.value(), nhi_no,
							String.format("(醫令代碼)%s不適用門診就醫方式", nhi_no), true);
				}
			}
		}
		
		retMap.put("isHospital", isHospital);
		retMap.put("isOutpatien", isOutpatien);
		retMap.put("mrList", mrList);
		retMap.put("mrIdListStr", mrIdListStr);
		retMap.put("sDateStr", sDateStr);
		retMap.put("eDateStr", eDateStr);
		return retMap;

	}
	/**
	 * 不可與  任一，並存單一就醫紀錄一併申報
	 * @param nhi_no(支付準則)
	 * @param lst_nhi_no(支付準則list)
	 * @param isOutpatien(是否門診)
	 * @param isHospital(是否住院)
	 * @param mrList(mr資料列)
	 */
	public void Exclude_nhi_no_enable(String nhi_no, List<String> lst_nhi_no, boolean isOutpatien, boolean isHospital, List<MR> mrList ) {
		List<String> nhiNoList = lst_nhi_no;
		int count = 0;
		/// 如果門診
		if (isHospital) {
			for (MR mr : mrList) {
				for (String nhiNo : nhiNoList) {
					if (mr.getCodeAll().contains(nhiNo) && count == 0) {
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
								nhi_no,
								String.format("(醫令代碼)%s與支付準則條件:不可與%s任一，並存單一就醫紀錄一併申報，疑似有出入",
										nhi_no, nhiNoList.toString()),
								true);
						count++;
					} else if (mr.getCodeAll().contains(nhiNo) && count > 0) {
						continue;
					}
				}
				count = 0;
			}
		}
		/// 如果住院
		if (isOutpatien) {
			for (MR mr : mrList) {
				for (String nhiNo : nhiNoList) {
					if (mr.getCodeAll().contains(nhiNo) && count == 0) {
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
								nhi_no,
								String.format("(醫令代碼)%s與支付準則條件:不可與%s任一，並存單一就醫紀錄一併申報，疑似有出入",
										nhi_no, nhiNoList.toString()),
								true);
						count++;
					} else if (mr.getCodeAll().contains(nhiNo) && count > 0) {
						continue;
					}
				}
				count = 0;
			}
		}
	}
	/**
	 * 每組病歷號碼，每院限一年內，限定申報 次，如有需求另外提出
	 * @param nhi_no(支付準則)
	 * @param max_times(申報次數)
	 * @param isOutpatien(是否門診)
	 * @param isHospital(是否住院)
	 * @throws ParseException
	 */
	public void Max_inpatient_enable(String nhi_no, int max_times,boolean isOutpatien, boolean isHospital) throws ParseException {
		///取得最新病例
		List<Map<String, Object>> mrData = mrDao.getRocLastDayListByIdAndCode(nhi_no);
		if (mrData.size() > 0) {
			for (Map<String, Object> map : mrData) {
				String endDate = map.get("MR_DATE").toString();
				String startDate = minusYear(endDate);
				Map<String, Object> mrData2 = mrDao.getRocCountListByCodeAndDate(nhi_no, map.get("ROC_ID").toString(),
						startDate, endDate);
				
				if(mrData2.size() >0) {
					float count = Float.valueOf(mrData2.get("COUNT").toString());
		          			
				    if(max_times < count) {
				    	
				    	List<MR> mrModelList = mrDao.getAllByCodeAndRocid(nhi_no, map.get("ROC_ID").toString());
				    	if(mrModelList.size() > 0) {
				    		
				    		MR mr = mrModelList.get(0);
				    		if(isOutpatien && mr.getDataFormat().equals("10")) {
				    			intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
				    					nhi_no,
				    					String.format("(醫令代碼)%s與支付準則條件:每組病歷號碼，每院限一年內，限定申報%d次，如有需求另外提出，疑似有出入",
				    							nhi_no, max_times),
				    					true);
				    		}
				    		if(isHospital && mr.getDataFormat().equals("20")) {
				    			intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
				    					nhi_no,
				    					String.format("(醫令代碼)%s與支付準則條件:每組病歷號碼，每院限一年內，限定申報%d次，如有需求另外提出，疑似有出入",
				    							nhi_no, max_times),
				    					true);
				    		}
				    	}
				    	
				    }
				}
			}
		}
	}
	/**
	 * 限定同患者每次申報此支付標準代碼，與前一次申報日間隔大於等於 日，方可申報
	 * @param nhi_no(支付準則)
	 * @param interval_nday(申報間隔日數)
	 * @param mrIdListStr(mrId)
	 * @param isOutpatien(是否門診)
	 * @param isHospital(是否住院)
	 * @throws ParseException 
	 */
	public void Interval_nday_enable(String nhi_no, int interval_nday, List<String> mrIdListStr,boolean isOutpatien, boolean isHospital ) throws ParseException {
		Map<String, Object> m1 = new HashMap<String, Object>();
		if(isOutpatien) {
			/// 取得該區間支付準則病例
			List<Map<String, Object>> oppData = oppDao.getRocIdCount(nhi_no, mrIdListStr);
			List<Map<String, Object>> oppList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> oppList2 = new ArrayList<Map<String, Object>>();
			/// 門診
			if (oppData.size() > 0) {
				int count = 0;
				/// 將相同身分證號理出日期最新2筆資料
				for (Map<String, Object> map : oppData) {
					if(map.get("START_TIME") != null && map.get("END_TIME") != null) {
						
						String rocid = map.get("ROC_ID").toString();
						String sDate = map.get("START_TIME").toString();
						String eDate = map.get("END_TIME").toString();
						String mrid = map.get("MR_ID").toString();
						
						if (count == 0) {
							m1.put("ROC_ID", rocid);
							m1.put("START_TIME", sDate);
							m1.put("END_TIME", eDate);
							m1.put("MR_ID", mrid);
							/// 裝最新一筆資料
							oppList.add(m1);
							m1 = new HashMap<String, Object>();
							count++;
						} else if (count == 1) {
							m1.put("ROC_ID", rocid);
							m1.put("START_TIME", sDate);
							m1.put("END_TIME", eDate);
							m1.put("MR_ID", mrid);
							/// 裝第二新一筆資料
							oppList2.add(m1);
							count++;
						} else {
							if (rocid.equals(m1.get("ROC_ID"))) {
								continue;
							} else {
								m1 = new HashMap<String, Object>();
								m1.put("ROC_ID", rocid);
								m1.put("START_TIME", sDate);
								m1.put("END_TIME", eDate);
								m1.put("MR_ID", mrid);
								/// 裝最新一筆資料
								oppList.add(m1);
								m1 = new HashMap<String, Object>();
								count = 1;
							}
						}
					}
				}
				/// 2個array做相同rocid日期比對
				for (Map<String, Object> map : oppList) {
					String rocid = map.get("ROC_ID").toString();
					String eDate = map.get("END_TIME").toString();
					String mrid = map.get("MR_ID").toString();
					for (Map<String, Object> map2 : oppList2) {
						String rocid2 = map2.get("ROC_ID").toString();
						String eDate2 = map2.get("END_TIME").toString();
						if (rocid.equals(rocid2)) {
							Date firstDate =  DateTool.convertChineseToYears(eDate, null);
							Date secondDate =  DateTool.convertChineseToYears(eDate2, null);
							///日期比對相減取得相差天數
							long diff = this.dayBetween(secondDate,firstDate);
					
							/// 如果小於設定數值則寫入
							if (interval_nday > diff) {
								MR mr = mrDao.getMrByID(mrid);
								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
										nhi_no,
										String.format("(醫令代碼)%s與支付準則條件:限定同患者前一次應用與當次應用待申報此支付標準代碼，每次申報間隔大於等於%d日，疑似有出入",
												nhi_no, interval_nday),
										true);
							}
							
						}
					}
				}
			}
		}
		if(isHospital) {
			/// 取得該區間支付準則病例
			List<Map<String, Object>> ippData = ippDao.getRocIdCount(nhi_no, mrIdListStr);
			List<Map<String, Object>> ippList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> ippList2 = new ArrayList<Map<String, Object>>();
			m1 = new HashMap<String, Object>();
			/// 住院
			if (ippData.size() > 0) {
				int count = 0;
				/// 將相同身分證號理出日期最新2筆資料
				for (Map<String, Object> map : ippData) {
					if(map.get("START_TIME") != null && map.get("END_TIME") != null) {
						
						String rocid = map.get("ROC_ID").toString();
						String sDate = map.get("START_TIME").toString();
						String eDate = map.get("END_TIME").toString();
						String mrid = map.get("MR_ID").toString();
						
						if (count == 0) {
							m1.put("ROC_ID", rocid);
							m1.put("START_TIME", sDate);
							m1.put("END_TIME", eDate);
							m1.put("MR_ID", mrid);
							/// 裝最新一筆資料
							ippList.add(m1);
							m1 = new HashMap<String, Object>();
							count++;
						} else if (count == 1) {
							m1.put("ROC_ID", rocid);
							m1.put("START_TIME", sDate);
							m1.put("END_TIME", eDate);
							m1.put("MR_ID", mrid);
							/// 裝第二新一筆資料
							ippList2.add(m1);
							count++;
						} else {
							if (rocid.equals(m1.get("ROC_ID"))) {
								continue;
							} else {
								m1 = new HashMap<String, Object>();
								m1.put("ROC_ID", rocid);
								m1.put("START_TIME", sDate);
								m1.put("END_TIME", eDate);
								m1.put("MR_ID", mrid);
								/// 裝最新一筆資料
								ippList.add(m1);
								m1 = new HashMap<String, Object>();
								count = 1;
							}
						}
					}
				}
				/// 相同rocid做日期比對
				for (Map<String, Object> map : ippList) {
					String rocid = map.get("ROC_ID").toString();
					String eDate = map.get("END_TIME").toString();
					String mrid = map.get("MR_ID").toString();
					for (Map<String, Object> map2 : ippList2) {
						String rocid2 = map2.get("ROC_ID").toString();
						String eDate2 = map2.get("END_TIME").toString();
						if (rocid.equals(rocid2)) {
							Date firstDate =  DateTool.convertChineseToYears(eDate, null);
							Date secondDate =  DateTool.convertChineseToYears(eDate2, null);
							///日期比對相減取得相差天數
							long diff = this.dayBetween(secondDate,firstDate);
							/// 如果小於設定數值則寫入
							if (interval_nday > diff) {
								MR mr = mrDao.getMrByID(mrid);
								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
										nhi_no,
										String.format("(醫令代碼)%s與支付準則條件:限定同患者前一次應用與當次應用待申報此支付標準代碼，每次申報間隔大於等於%d日，疑似有出入",
												nhi_no, interval_nday),
										true);
							}
							
						}
					}
				}
			}
		}
	}
	/**
	 * 限定同患者每次申報此支付標準代碼， 日內，限定申報小於等於 次
	 * @param nhi_no(支付準則)
	 * @param patient_nday_days(日數)
	 * @param patient_nday_times(次數)
	 * @param mrIdListStr(mrId)
	 * @param isOutpatien(是否門診)
	 * @param isHospital(是否住院)
	 */
	public void Patient_nday_enable(String nhi_no, int patient_nday_days, int patient_nday_times, List<String> mrIdListStr,boolean isOutpatien, boolean isHospital ) {
		if(isHospital) {
			List<Map<String, Object>> ippData = ippDao.getListRocIdByOrderCodeAndMridAndDays(patient_nday_days,nhi_no, mrIdListStr);
			
			if(ippData.size() > 0) {
				for (Map<String, Object> map : ippData) {
					float t = Float.parseFloat(map.get("TOTAL").toString());
					/// 如果資料大於限定值
					if (patient_nday_times < t) {
						MR mr = mrDao.getMrByRocIdAndCode(map.get("ROC_ID").toString(), nhi_no,mrIdListStr);
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), nhi_no,
								String.format("(醫令代碼)%s與支付準則條件:同患者限定每小於等於%d日，總申報次數小於等於%d次為原則，疑似有出入", nhi_no,
										patient_nday_days,patient_nday_times),
								true);
					}
				}
			}
		}
		if(isOutpatien) {
			List<Map<String, Object>> oppData = oppDao.getListRocIdByDrugNoAndMridAndDays(patient_nday_days,nhi_no, mrIdListStr);
			if(oppData.size() > 0) {
				for (Map<String, Object> map : oppData) {
					float t = Float.parseFloat(map.get("TOTAL").toString());
					/// 如果資料大於限定值
					if (patient_nday_times < t) {
						MR mr = mrDao.getMrByRocIdAndCode(map.get("ROC_ID").toString(), nhi_no,mrIdListStr);
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), nhi_no,
								String.format("(醫令代碼)%s與支付準則條件:同患者限定每小於等於%d日，總申報次數小於等於%d次為原則，疑似有出入", nhi_no,
										patient_nday_days,patient_nday_times),
								true);
					}
				}
			}
		}
	}
	/**
	 * 限定特定科別應用
	 * @param nhi_no(支付準則)
	 * @param lst_division(清單)
	 * @param mrIdListStr(mrId)
	 * @param isOutpatien(是否門診)
	 * @param isHospital(是否住院)
	 */
	public void Lim_division_enable(String nhi_no, List<String> lst_division, List<String> mrIdListStr,boolean isOutpatien, boolean isHospital) {
		List<String> funList = lst_division;
		List<String> funcAppend = new ArrayList<String>();

		for (String func : funList) {
			funcAppend.add(func);
		}

		List<MR> mrDataList = mrDao.getIntelligentMrByFuncName(mrIdListStr, funcAppend);
		/// 如果有非指定funcName資料
		if (mrDataList.size() > 0) {
			for (MR mr : mrDataList) {
				///如果門診
				if(isOutpatien && mr.getDataFormat().equals("10")) {
					
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), nhi_no,
							String.format("(醫令代碼)%s與支付準則條件:限定特定科%s別應用，疑似有出入", nhi_no, funcAppend), true);
				}
				///如果住院
				if(isHospital && mr.getDataFormat().equals("20")) {
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), nhi_no,
							String.format("(醫令代碼)%s與支付準則條件:限定特定科%s別應用，疑似有出入", nhi_no, funcAppend), true);

				}

			}
		}
	}
	/**
	 * 需與任一，並存單一就醫紀錄一併申報時
	 * @param nhi_no(支付準則)
	 * @param lst_co_nhi_no(清單)
	 * @param mrList(mr資料)
	 * @param isOutpatien(是否門診)
	 * @param isHospital(是否住院)
	 */
	public void Coexist_nhi_no_enable(String nhi_no, List<String> lst_co_nhi_no, List<MR> mrList,boolean isOutpatien, boolean isHospital) {
		List<String> coList = lst_co_nhi_no;
		int count = 0;
		for (MR mr : mrList) {
			for (String s : coList) {
				
				/// 再判斷沒有符合
				if (!mr.getCodeAll().contains(s)) {
					if (count == 0) {
						if(isOutpatien && mr.getDataFormat().equals("10")) {
							intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
									nhi_no,
									String.format("(醫令代碼)%s與支付準則條件:需與[%s]任一，並存單一就醫紀錄一併申報時，疑似有出入",
											nhi_no, coList.toString()),
									true);
						}
						if(isHospital && mr.getDataFormat().equals("20")) {
							intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
									nhi_no,
									String.format("(醫令代碼)%s與支付準則條件:需與[%s]任一，並存單一就醫紀錄一併申報時，疑似有出入",
											nhi_no, coList.toString()),
									true);
						}
						
					}
					count++;
				}
			}
			count = 0;
			
		}
	}
	/**
	 * 同患者限定每小於等於 日，僅能待申報次(可累績申報)
	 * @param nhi_no(支付準則)
	 * @param patient_nday_days(日數)
	 * @param patient_nday_times(次數)
	 * @param mrIdListStr(mrId)
	 * @param isOutpatien(是否門診)
	 * @param isHospital(是否住院)
	 */
	public void Patient_nday_enableT(String nhi_no, int patient_nday_days, int patient_nday_times, List<String> mrIdListStr,boolean isOutpatien, boolean isHospital) {
		if(isHospital) {
			
			List<Map<String, Object>> ippData = ippDao.getListRocIdByOrderCodeAndMridAndDays(patient_nday_days,nhi_no, mrIdListStr);
			if(ippData.size() > 0) {
				for (Map<String, Object> map : ippData) {
					float t = Float.parseFloat(map.get("TOTAL").toString());
					/// 如果資料大於限定值
					if (patient_nday_times < t) {
						MR mr = mrDao.getMrByRocIdAndCode(map.get("ROC_ID").toString(), nhi_no,mrIdListStr);
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), nhi_no,
								String.format("(醫令代碼)%s與支付準則條件:同患者限定每小於等於%d日，僅能待申報%d次(可累績申報)，疑似有出入", nhi_no,
										patient_nday_days,patient_nday_times),
								true);
					}
				}
			}
		}
		if(isOutpatien) {
			List<Map<String, Object>> oppData = oppDao.getListRocIdByDrugNoAndMridAndDays(patient_nday_days,nhi_no, mrIdListStr);
			if(oppData.size() > 0) {
				for (Map<String, Object> map : oppData) {
					float t = Float.parseFloat(map.get("TOTAL").toString());
					/// 如果資料大於限定值
					if (patient_nday_times < t) {
						MR mr = mrDao.getMrByRocIdAndCode(map.get("ROC_ID").toString(), nhi_no,mrIdListStr);
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), nhi_no,
								String.format("(醫令代碼)%s與支付準則條件:同患者限定每小於等於%d日，僅能待申報%d次(可累績申報)，疑似有出入", nhi_no,
										patient_nday_days,patient_nday_times),
								true);
					}
				}
			}
		}
	}
	/**
	 * 單一就醫紀錄應用數量,限定小於等於 次
	 * @param nhi_no(支付準則)
	 * @param max_inpatient(次數)
	 * @param mrIdListStr(mrId)
	 * @param isOutpatien(是否門診)
	 * @param isHospital(是否住院)
	 */
	public void Max_inpatient_enable(String nhi_no, int max_inpatient, List<String> mrIdListStr,boolean isOutpatien, boolean isHospital) {
		if(isHospital) {
			List<Map<String, Object>> ippData = ippDao.getListByOrderCodeAndMrid(nhi_no, mrIdListStr);
			
			if(ippData.size() > 0) {
				
				for (Map<String, Object> map : ippData) {
					float t = Float.parseFloat(map.get("TOTAL").toString());
					/// 如果資料大於限定值
					if (max_inpatient < t) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), nhi_no,
								String.format("(醫令代碼)%s與支付準則條件:單一就醫紀錄應用總數量,限定小於等於%d次，疑似有出入", nhi_no,
										max_inpatient),
								true);
					}
				}
			}
		}
		if(isOutpatien) {
			List<Map<String, Object>> oppData = oppDao.getListByDrugNoAndMrid2(nhi_no, mrIdListStr);
			if(oppData.size() > 0) {
				for (Map<String, Object> map : oppData) {
					float t = Float.parseFloat(map.get("TOTAL").toString());
					/// 如果資料大於限定值
					if (max_inpatient < t) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), nhi_no,
								String.format("(醫令代碼)%s與支付準則條件:單一就醫紀錄應用總數量,限定小於等於%d次，疑似有出入", nhi_no,
										max_inpatient),
								true);
					}
				}
			}
		}
	}
	
	/**
	 * 單一就醫紀錄上，須包含以下任一ICD診斷碼
	 * @param nhi_no(支付準則)
	 * @param lst_icd_no(清單)
	 * @param mrList(mr資料)
	 * @param isOutpatien(是否門診)
	 * @param isHospital(是否住院)
	 */
	public void Include_icd_no_enable(String nhi_no, List<String> lst_icd_no, List<MR> mrList,boolean isOutpatien, boolean isHospital) {
		List<String> icdList = lst_icd_no;
		int count = 0;
		for (MR mr : mrList) {
			for (String s : icdList) {
				if (mr.getCodeAll().contains(nhi_no) && !mr.getIcdAll().contains(s)) {

					if (count == 0) {
						if (isOutpatien && mr.getDataFormat().equals("10")) {
							intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
									nhi_no,
									String.format("(醫令代碼)%s與支付準則條件:單一就醫紀錄上，須包含以下任一ICD診斷碼[%s]，疑似有出入",
											nhi_no, icdList.toString()),
									true);
						}
						if (isHospital && mr.getDataFormat().equals("20")) {
							intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
									nhi_no,
									String.format("(醫令代碼)%s與支付準則條件:單一就醫紀錄上，須包含以下任一ICD診斷碼[%s]，疑似有出入",
											nhi_no, icdList.toString()),
									true);
						}

					}
					count++;
				}
			}
			count = 0;
		}
	}
	/**
	 * 需與任一，並存於單一就醫紀錄，方可申報
	 * @param nhi_no(支付準則)
	 * @param lst_co_nhi_no(清單)
	 * @param mrList(mr資料)
	 * @param isOutpatien(是否門診)
	 * @param isHospital(是否住院)
	 */
	public void Coexist_nhi_no_enableS(String nhi_no, List<String> lst_co_nhi_no, List<MR> mrList,boolean isOutpatien, boolean isHospital) {
		List<String> coList = lst_co_nhi_no;
		int count = 0;
		
		for (MR mr : mrList) {
			for (String s : coList) {
				
				/// 再判斷沒有符合
				if (!mr.getCodeAll().contains(s)) {
					if (count == 0) {
						///如果住院
						if(isHospital && mr.getDataFormat().equals("20")) {
							intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
									nhi_no, String.format("(醫令代碼)%s與支付準則條件:需與%s以下支付標準代碼任一並存，方可進行申報，疑似有出入",
											nhi_no, coList.toString()),
									true);
						}
						///如果門診
						if(isOutpatien && mr.getDataFormat().equals("10")) {
							intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
									nhi_no, String.format("(醫令代碼)%s與支付準則條件:需與%s以下支付標準代碼任一並存，方可進行申報，疑似有出入",
											nhi_no, coList.toString()),
									true);
						}
						
					}
					count++;
				}
			}
			count = 0;
			
		}
	}
	/**
	 * 單一就醫紀錄上，每日限定應用小於等於
	 * @param nhi_no(支付準則)
	 * @param max_inpatient(次數)
	 * @param mrIdListStr(mrId)
	 * @param isOutpatien(是否門診)
	 * @param isHospital(是否住院)
	 */
	public void Max_daily_enable(String nhi_no, int max_daily, List<String> mrIdListStr,boolean isOutpatien, boolean isHospital) {
		///如果住院
		if(isHospital) {
			List<Map<String, Object>> ippData = ippDao.getListOneDayByOrderCodeAndMrid(nhi_no, mrIdListStr);
			
			if (ippData.size() > 0) {
				for (Map<String, Object> map : ippData) {
					float t = Float.parseFloat(map.get("TOTAL").toString());
					/// 如果資料大於限定值
					if (max_daily < t) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), nhi_no,
								String.format("(醫令代碼)%s與支付準則條件:單一住院就醫紀錄應用數量,限定小於等於%d次，疑似有出入", nhi_no,
										max_daily),
								true);
					}
				}
			}
		}
		///如果門診
		if(isOutpatien) {
			List<Map<String, Object>> oppData = oppDao.getListOneDayByDrugNoAndMrid(nhi_no, mrIdListStr);
			
			if (oppData.size() > 0) {
				for (Map<String, Object> map : oppData) {
					float t = Float.parseFloat(map.get("TOTAL").toString());
					/// 如果資料大於限定值
					if (max_daily < t) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), nhi_no,
								String.format("(醫令代碼)%s與支付準則條件:單一門診就醫紀錄應用數量,限定小於等於%d次，疑似有出入", nhi_no,
										max_daily),
								true);
					}
				}
			}
		}
	}
	/**
	 * 單一就醫紀錄上，每 日內，限定應用小於等於 次
	 * @param nhi_no(支付準則)
	 * @param every_nday_days(天數)
	 * @param every_nday_times(次數)
	 * @param mrIdListStr(mrId)
	 * @param isOutpatien(是否門診)
	 * @param isHospital(是否住院)
	 */
	public void Every_nday_enable(String nhi_no,int every_nday_days, int every_nday_times, List<String> mrIdListStr,boolean isOutpatien, boolean isHospital) {
		///如果住院
		if(isHospital) {
			List<Map<String, Object>> ippData = ippDao.getListByOrderCodeAndMridAndDays(every_nday_days,
					nhi_no, mrIdListStr);
			
			if (ippData.size() > 0) {
				for (Map<String, Object> map : ippData) {
					float t = Float.parseFloat(map.get("TOTAL").toString());
					/// 如果資料大於限定值
					if (every_nday_times < t) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), nhi_no,
								String.format("(醫令代碼)%s與支付準則條件:單一就醫紀錄上，每%d日內，限定應用小於等於%d次，疑似有出入", nhi_no,
										every_nday_days, every_nday_times),
								true);
					}
				}
			}
		}
		///如果門診
		if(isOutpatien) {
			List<Map<String, Object>> oppData = oppDao.getListByDrugNoAndMridAndDays(every_nday_days,
					nhi_no, mrIdListStr);
			if (oppData.size() > 0) {
				for (Map<String, Object> map : oppData) {
					float t = Float.parseFloat(map.get("TOTAL").toString());
					/// 如果資料大於限定值
					if (every_nday_times < t) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), nhi_no,
								String.format("(醫令代碼)%s與支付準則條件:單一就醫紀錄上，每%d日內，限定應用小於等於%d次，疑似有出入", nhi_no,
										every_nday_days, every_nday_times),
								true);
					}
				}
			}
		}
	}
	/**
	 * 患者限定年紀小於等於 歲，方可進行申報
	 * @param nhi_no(支付準則)
	 * @param lim_age(年紀)
	 * @param mrIdListStr(mrId)
	 * @param isOutpatien(是否門診)
	 * @param isHospital(是否住院)
	 * @throws ParseException
	 */
	public void Lim_age_enable(String nhi_no,int lim_age, List<String> mrIdListStr,boolean isOutpatien, boolean isHospital) throws ParseException {
		if(isOutpatien) {
	    	
	    	List<OP_D> opData = opdDao.getDataListByMrId(mrIdListStr);
	    	if(opData.size() > 0) {
				for (OP_D model : opData) {
					String rocBirth = "";
					if(model.getNbBirthday() != null && !model.getNbBirthday().isEmpty()) {
						rocBirth = model.getNbBirthday();
					}
					else {
						rocBirth = model.getIdBirthYmd();
					}
					Date d = DateTool.convertChineseToYear(rocBirth);
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
					Date currentDate = new Date();
					String rocStr = sdf2.format(d);
					String currentStr = sdf2.format(currentDate);
					int diffY = yearsBetween(rocStr, currentStr);
					if(lim_age > diffY) {
						MR mr = mrDao.getMrByID(model.getId().toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
								nhi_no,
								String.format("(醫令代碼)%s與支付準則條件:患者限定年紀小於等於%d歲，方可進行申報，疑似有出入",
										nhi_no, lim_age),
								true);
					}
				}
			} 
	    }
	    if(isHospital ) {
	    	List<IP_D> ipData = ipdDao.getDataListByMrId(mrIdListStr);
	    	
	    	if (ipData.size() > 0) {
	    		for (IP_D model : ipData) {
	    			String rocBirth = "";
					if(model.getNbBirthday() != null && !model.getNbBirthday().isEmpty()) {
						rocBirth = model.getNbBirthday();
					}
					else {
						rocBirth = model.getIdBirthYmd();
					}
	    			Date d = DateTool.convertChineseToYear(rocBirth);
	    			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
	    			Date currentDate = new Date();
	    			String rocStr = sdf2.format(d);
	    			String currentStr = sdf2.format(currentDate);
	    			int diffY = yearsBetween(rocStr, currentStr);
	    			if(lim_age > diffY) {
	    				MR mr = mrDao.getMrByID(model.getId().toString());
	    				intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
	    						nhi_no,
	    						String.format("(醫令代碼)%s與支付準則條件:患者限定年紀小於等於%d歲，方可進行申報，疑似有出入",
	    								nhi_no, lim_age),
	    						true);
	    			}
	    		}
	    	}
	    }
	}
	/**
	 * 參與計畫之病患，不得申報
	 * @param nhi_no(支付準則)
	 * @param lst_allow_plan(支付準則計畫)
	 * @param mrIdListStr(mrId)
	 * @param isOutpatien(是否門診)
	 * @param isHospital(是否住院)
	 */
	public void Not_allow_plan_enabl(String nhi_no,List<String> lst_allow_plan, List<MR> mrList,boolean isOutpatien, boolean isHospital) {
		List<String> planList = lst_allow_plan;
		int count = 0;
		for (MR mr : mrList) {
			/// 如果門診
			if (isOutpatien && mr.getDataFormat().equals("10")) {
				for (String s : planList) {
					if (mr.getCodeAll().contains(s)) {

						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
								nhi_no, String.format("(醫令代碼)%s與支付準則條件:參與[%s]計畫之病患，不得申報，疑似有出入",
										nhi_no, planList.toString()),
								true);
						count++;
					} else if (mr.getCodeAll().contains(s) && count > 0) {
						continue;
					}
				}
			}
			/// 如果住院
			if (isHospital && mr.getDataFormat().equals("20")) {
				for (String s : planList) {
					if (mr.getCodeAll().contains(s)) {

						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
								nhi_no, String.format("(醫令代碼)%s與支付準則條件:參與[%s]計畫之病患，不得申報，疑似有出入",
										nhi_no, planList.toString()),
								true);
						count++;
					} else if (mr.getCodeAll().contains(s) && count > 0) {
						continue;
					}
				}
			}

			count++;
		}
	}

	/**
	 * 帶入日期並減一年
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	protected String minusYear(String date) throws ParseException {
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date d = sdf.parse(date);
		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(d);
		currentDate.add(Calendar.YEAR, -1);
		Date d2 = currentDate.getTime();
		result = sdf.format(d2);

		return result;
	}

	/**
	 * 計算兩個時間相差多少個年
	 * 
	 * @param early
	 * @param late
	 * @return
	 * @throws ParseException
	 */
	protected int yearsBetween(String start, String end) throws ParseException {
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		startDate.setTime(sdf.parse(start));
		endDate.setTime(sdf.parse(end));
		return (endDate.get(Calendar.YEAR) - startDate.get(Calendar.YEAR));
	}
	
	/**
	 * 計算兩個時間相差多少小時
	 * 
	 * @param start
	 * @param end
	 * @return
	 * @throws ParseException
	 */
	protected long hourBetween(Date start, Date end) throws ParseException {
		long diff = end.getTime() - start.getTime();

		TimeUnit time = TimeUnit.DAYS;
		long diffrence = time.convert(diff, TimeUnit.MILLISECONDS);
		long hour = diffrence * 24;
		return hour;
	}
	
	/**
	 * 計算兩個時間相差多少天
	 * 
	 * @param start
	 * @param end
	 * @return
	 * @throws ParseException
	 */
	protected long dayBetween(Date start, Date end) throws ParseException {
		long diff = end.getTime() - start.getTime();

		TimeUnit time = TimeUnit.DAYS;
		long diffrence = time.convert(diff, TimeUnit.MILLISECONDS);

		return diffrence;
	}

	// Convert Date to Calendar
	protected Calendar dateToCalendar(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;

	}

	// Convert Calendar to Date
	private Date calendarToDate(Calendar calendar) {
		return calendar.getTime();
	}

}

