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
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dto.PtQualityServicePl;
import tw.com.leadtek.nhiwidget.dto.ptNhiNoTimes;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.service.IntelligentService;

@Service
public class PtQualityServiceServiceTask {
	@Autowired
	private MRDao mrDao;

	@Autowired
	private IP_PDao ippDao;
	@Autowired
	private OP_PDao oppDao;

	@Autowired
	private IntelligentService intelligentService;

	public void validQualityServic(PtQualityServicePl params) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		/// 將timestamp轉成date
		java.sql.Timestamp tSdate = new java.sql.Timestamp(params.getStart_date());
		java.sql.Timestamp tEdate = new java.sql.Timestamp(params.getEnd_date());
		Date tsd = new Date(tSdate.getTime());
		Date ted = new Date(tEdate.getTime());
		String sDateStr = sdf.format(tsd);
		String eDateStr = sdf.format(ted);

		/// 該支付準則區間病歷表
		List<MR> mrList = mrDao.getIntelligentMR(sDateStr, eDateStr, params.getNhi_no());
		/// 存放mrID
		List<String> mrIdListStr = new ArrayList<String>();
		/// 提取將該診斷碼之ID
		for (MR mr : mrList) {

			mrIdListStr.add(mr.getId().toString());
		}
		if (params.getHospitalized_type() == 0) {
			for (MR r : mrList) {
				if (r.getDataFormat() == "20") {
					intelligentService.insertIntelligent(r, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s不適用住院就醫方式", params.getNhi_no()), true);
				}
			}
		} else if (params.getOutpatient_type() == 0) {
			for (MR r : mrList) {
				if (r.getDataFormat() == "10") {
					intelligentService.insertIntelligent(r, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s不適用門診就醫方式", params.getNhi_no()), true);
				}
			}
		}

		/// 1.
		/// 限定同患者每次申報此支付標準代碼，與前一次申報日間隔大於等於 日，方可申報
		if (params.getInterval_nday_enable() == 1) {
			List<Map<String, Object>> oppData = oppDao.getRocIdCount(params.getNhi_no(), mrIdListStr);
			List<Map<String, Object>> oppList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> oppList2 = new ArrayList<Map<String, Object>>();
			Map<String, Object> m1 = new HashMap<String, Object>();
			/// 門診
			if (oppData.size() > 0) {
				int count = 0;
				/// 先理出最後2筆資料
				for (Map<String, Object> map : oppData) {

					String rocid = map.get("ROC_ID").toString();
					String sDate = map.get("START_TIME").toString();
					String eDate = map.get("END_TIME").toString();
					String mrid = map.get("MR_ID").toString();

					if (count == 0) {
						m1.put("ROC_ID", rocid);
						m1.put("START_TIME", sDate);
						m1.put("END_TIME", eDate);
						m1.put("MR_ID", mrid);
						oppList.add(m1);
						m1 = new HashMap<String, Object>();
						count++;
					} else if (count == 1) {
						m1.put("ROC_ID", rocid);
						m1.put("START_TIME", sDate);
						m1.put("END_TIME", eDate);
						m1.put("MR_ID", mrid);
						oppList2.add(m1);
						count++;
					} else {
						if (rocid.equals(m1.get("ROC_ID"))) {
							continue;
						} else {
							m1 = new HashMap<String, Object>();
							count = 0;
						}
					}
				}
				/// 相同rocid做日期比對
				for (Map<String, Object> map : oppList) {
					String rocid = map.get("ROC_ID").toString();
					String eDate = map.get("END_TIME").toString();
					String mrid = map.get("MR_ID").toString();
					for (Map<String, Object> map2 : oppList2) {
						String rocid2 = map2.get("ROC_ID").toString();
						String eDate2 = map2.get("END_TIME").toString();
						if (rocid.equals(rocid2)) {
							float f = Float.valueOf(eDate);
							float f2 = Float.valueOf(eDate2);
							float diff = (f - f2) / 10000;
							if (params.getInterval_nday() < diff) {
								MR mr = mrDao.getMrByID(mrid);
								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
										params.getNhi_no(),
										String.format("(醫令代碼)%s與支付準則條件:限定同患者前一次應用與當次應用待申報此支付標準代碼，每次申報間隔大於等於%d日，疑似有出入",
												params.getNhi_no(), params.getInterval_nday()),
										true);
							}

						}
					}
				}
			}

			List<Map<String, Object>> ippData = ippDao.getRocIdCount(params.getNhi_no(), mrIdListStr);
			List<Map<String, Object>> ippList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> ippList2 = new ArrayList<Map<String, Object>>();
			m1 = new HashMap<String, Object>();
			/// 住院
			if (ippData.size() > 0) {
				int count = 0;
				/// 先理出最後2筆資料
				for (Map<String, Object> map : oppData) {

					String rocid = map.get("ROC_ID").toString();
					String sDate = map.get("START_TIME").toString();
					String eDate = map.get("END_TIME").toString();
					String mrid = map.get("MR_ID").toString();

					if (count == 0) {
						m1.put("ROC_ID", rocid);
						m1.put("START_TIME", sDate);
						m1.put("END_TIME", eDate);
						m1.put("MR_ID", mrid);
						ippList.add(m1);
						m1 = new HashMap<String, Object>();
						count++;
					} else if (count == 1) {
						m1.put("ROC_ID", rocid);
						m1.put("START_TIME", sDate);
						m1.put("END_TIME", eDate);
						m1.put("MR_ID", mrid);
						ippList2.add(m1);
						count++;
					} else {
						if (rocid.equals(m1.get("ROC_ID"))) {
							continue;
						} else {
							m1 = new HashMap<String, Object>();
							count = 0;
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
							float f = Float.valueOf(eDate);
							float f2 = Float.valueOf(eDate2);
							float diff = (f - f2) / 10000;
							if (params.getInterval_nday() < diff) {
								MR mr = mrDao.getMrByID(mrid);
								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
										params.getNhi_no(),
										String.format("(醫令代碼)%s與支付準則條件:限定同患者前一次應用與當次應用待申報此支付標準代碼，每次申報間隔大於等於%d日，疑似有出入",
												params.getNhi_no(), params.getInterval_nday()),
										true);
							}

						}
					}
				}
			}
		}

		/// 2.
		/// 限定同患者執行過 任一，大於等於 次，方可申報
		if (params.getCoexist_nhi_no_enable() == 1) {
			List<ptNhiNoTimes> coList = params.getLst_co_nhi_no();
			int count = 0;
			List<String> mridList = new ArrayList<String>();
			for (MR mr : mrList) {
				if (mr.getCodeAll().contains(params.getNhi_no()))
					mridList.add(mr.getId().toString());
			}
			if(params.getHospitalized_type() == 1) {
				
				/// 住院
				for (ptNhiNoTimes model : coList) {
					List<Map<String, Object>> ippData = ippDao.getRocidTotalByDrugNoandMrid(model.getNhi_no(), mridList);
					if (count == 0) {
						if (ippData.size() > 0) {
							for (Map<String, Object> map : ippData) {
								int total = Integer.parseInt(map.get("TOTAL").toString());
								
								if (model.getTimes() < total) {
									MR mr = mrDao.getMrByRocIdAndCode(map.get("ROC_ID").toString(), model.getNhi_no());
									intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
											params.getNhi_no(),
											String.format("(醫令代碼)%s與支付準則條件:限定同患者執行過%s任一，大於等於%d次，方可申報，疑似有出入",
													params.getNhi_no(), model.getNhi_no(), model.getTimes()),
											true);
								}
							}
							count++;
						}
					}
				}
			}
			if(params.getOutpatient_type() == 1) {
				
				/// 門診
				for (ptNhiNoTimes model : coList) {
					List<Map<String, Object>> oppData = oppDao.getRocidTotalByDrugNoandMrid(model.getNhi_no(), mridList);
					/// 進入條件為or，
					if (count == 0) {
						
						if (oppData.size() > 0) {
							for (Map<String, Object> map : oppData) {
								int total = Integer.parseInt(map.get("TOTAL").toString());
								
								if (model.getTimes() < total) {
									MR mr = mrDao.getMrByRocIdAndCode(map.get("ROC_ID").toString(), model.getNhi_no());
									intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
											params.getNhi_no(),
											String.format("(醫令代碼)%s與支付準則條件:限定同患者執行過%s任一，大於等於%d次，方可申報，疑似有出入",
													params.getNhi_no(), model.getNhi_no(), model.getTimes()),
											true);
								}
							}
							count++;
						}
						
					}
					
				}
			}

		}

		/// 3.
		/// 限定同患者累積申報此支付標準代碼， 天內小於等於 次，方可申報
		if (params.getEvery_nday_enable() == 1) {
			List<Map<String, Object>> oppData = oppDao.getRocIdCount(params.getNhi_no(), mrIdListStr);
			List<Map<String, Object>> oppList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> oppList2 = new ArrayList<Map<String, Object>>();
			Map<String, Object> m1 = new HashMap<String, Object>();
			/// 門診
			if (oppData.size() > 0) {
				int count = 0;
				/// 先理出最後2筆資料
				for (Map<String, Object> map : oppData) {

					String rocid = map.get("ROC_ID").toString();
					String sDate = map.get("START_TIME").toString();
					String eDate = map.get("END_TIME").toString();
					String mrid = map.get("MR_ID").toString();
					String total = map.get("TOTAL_Q").toString();

					if (count == 0) {
						m1.put("ROC_ID", rocid);
						m1.put("START_TIME", sDate);
						m1.put("END_TIME", eDate);
						m1.put("MR_ID", mrid);
						m1.put("TOTAL_Q", total);
						oppList.add(m1);
						m1 = new HashMap<String, Object>();
						count++;
					} else if (count == 1) {
						m1.put("ROC_ID", rocid);
						m1.put("START_TIME", sDate);
						m1.put("END_TIME", eDate);
						m1.put("TOTAL_Q", total);
						oppList2.add(m1);
						count++;
					} else {
						if (rocid.equals(m1.get("ROC_ID"))) {
							continue;
						} else {
							m1 = new HashMap<String, Object>();
							count = 0;
						}
					}
				}
				/// 相同rocid做日期比對
				for (Map<String, Object> map : oppList) {
					String rocid = map.get("ROC_ID").toString();
					String eDate = map.get("END_TIME").toString();
					String mrid = map.get("MR_ID").toString();
					String total = map.get("TOTAL_Q").toString();
					for (Map<String, Object> map2 : oppList2) {
						String rocid2 = map2.get("ROC_ID").toString();
						String eDate2 = map2.get("END_TIME").toString();
						String total2 = map2.get("TOTAL_Q").toString();
						if (rocid.equals(rocid2)) {
							float f = Float.valueOf(eDate);
							float f2 = Float.valueOf(eDate2);
							float diff = (f - f2) / 10000;
							///先判斷為日期內
							if (params.getEvery_nday_days() <= diff) {
								int tCount = Integer.parseInt(total) + Integer.getInteger(total2);
								///再判斷加總是否有超過條件
								if(params.getEvery_nday_times() < tCount) {
									
									MR mr = mrDao.getMrByID(mrid);
									intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
											params.getNhi_no(),
											String.format("(醫令代碼)%s與支付準則條件:限定同患者累積申報此支付標準代碼%d天內小於等於%d次，方可申報，疑似有出入",
													params.getNhi_no(), params.getEvery_nday_days(), params.getEvery_nday_times()),
											true);
								}
								
							}

						}
					}
				}
			}

			List<Map<String, Object>> ippData = ippDao.getRocIdCount(params.getNhi_no(), mrIdListStr);
			List<Map<String, Object>> ippList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> ippList2 = new ArrayList<Map<String, Object>>();
			m1 = new HashMap<String, Object>();
			/// 住院
			if (ippData.size() > 0) {
				int count = 0;
				/// 先理出最後2筆資料
				for (Map<String, Object> map : oppData) {

					String rocid = map.get("ROC_ID").toString();
					String sDate = map.get("START_TIME").toString();
					String eDate = map.get("END_TIME").toString();
					String mrid = map.get("MR_ID").toString();
					String total = map.get("TOTAL_Q").toString();
					if (count == 0) {
						m1.put("ROC_ID", rocid);
						m1.put("START_TIME", sDate);
						m1.put("END_TIME", eDate);
						m1.put("MR_ID", mrid);
						ippList.add(m1);
						m1 = new HashMap<String, Object>();
						count++;
					} else if (count == 1) {
						m1.put("ROC_ID", rocid);
						m1.put("START_TIME", sDate);
						m1.put("END_TIME", eDate);
						m1.put("MR_ID", mrid);
						ippList2.add(m1);
						count++;
					} else {
						if (rocid.equals(m1.get("ROC_ID"))) {
							continue;
						} else {
							m1 = new HashMap<String, Object>();
							count = 0;
						}
					}
				}
				/// 相同rocid做日期比對
				for (Map<String, Object> map : ippList) {
					String rocid = map.get("ROC_ID").toString();
					String eDate = map.get("END_TIME").toString();
					String mrid = map.get("MR_ID").toString();
					String total = map.get("TOTAL_Q").toString();
					for (Map<String, Object> map2 : ippList2) {
						String rocid2 = map2.get("ROC_ID").toString();
						String eDate2 = map2.get("END_TIME").toString();
						String total2 = map2.get("TOTAL_Q").toString();
						if (rocid.equals(rocid2)) {
							float f = Float.valueOf(eDate);
							float f2 = Float.valueOf(eDate2);
							float diff = (f - f2) / 10000;
							///先判斷為日期內
							if (params.getEvery_nday_days() <= diff) {
								int tCount = Integer.parseInt(total) + Integer.getInteger(total2);
								///再判斷加總是否有超過條件
								if(params.getEvery_nday_times() < tCount) {
									
									MR mr = mrDao.getMrByID(mrid);
									intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
											params.getNhi_no(),
											String.format("(醫令代碼)%s與支付準則條件:限定同患者累積申報此支付標準代碼%d天內小於等於%d次，方可申報，疑似有出入",
													params.getNhi_no(), params.getEvery_nday_days(), params.getEvery_nday_times()),
											true);
								}
							}

						}
					}
				}
			}
            
		}
	}

	/**
	 * 帶入日期並減一年
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	private String minusYear(String date) throws ParseException {
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
	 * @param start
	 * @param end
	 * @return
	 * @throws ParseException
	 */
	private int yearsBetween(String start, String end) throws ParseException {
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
	private long hourBetween(Date start, Date end) throws ParseException {
		long diff = end.getTime() - start.getTime();

		TimeUnit time = TimeUnit.DAYS;
		long diffrence = time.convert(diff, TimeUnit.MILLISECONDS);
		long hour = diffrence * 24;
		return hour;
	}

	// Convert Date to Calendar
	private Calendar dateToCalendar(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;

	}

	// Convert Calendar to Date
	private Date calendarToDate(Calendar calendar) {
		return calendar.getTime();
	}
}
