package tw.com.leadtek.nhiwidget.task.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dto.PtRehabilitationFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.service.IntelligentService;

@Service
public class PtRehabilitationFeeServiceTask {
	@Autowired
	private MRDao mrDao;

	@Autowired
	private IP_PDao ippDao;
	@Autowired
	private OP_PDao oppDao;

	@Autowired
	private IntelligentService intelligentService;

	private String Category = "復健治療費";

	public void validRehabilitationFee(PtRehabilitationFeePl params) throws ParseException {
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

		/// .1
		/// 不可與 任一，並存單一就醫紀錄一併申報
		if (params.getExclude_nhi_no_enable() == 1) {
			List<String> nhiNoList = params.getLst_nhi_no();
			int count = 0;
			/// 如果門診
			if (params.getHospitalized_type() == 1) {
				for (MR mr : mrList) {
					for (String nhiNo : nhiNoList) {
						if (mr.getCodeAll().contains(nhiNo) && count == 0) {
							intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
									params.getNhi_no(), String.format("(醫令代碼)%s與支付準則條件:不可與%s任一，並存單一就醫紀錄一併申報，疑似有出入",
											params.getNhi_no(), nhiNoList.toString()),
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
			if (params.getOutpatient_type() == 1) {
				for (MR mr : mrList) {
					for (String nhiNo : nhiNoList) {
						if (mr.getCodeAll().contains(nhiNo) && count == 0) {
							intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
									params.getNhi_no(), String.format("(醫令代碼)%s與支付準則條件:不可與%s任一，並存單一就醫紀錄一併申報，疑似有出入",
											params.getNhi_no(), nhiNoList.toString()),
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

		/// 2.
		/// 同患者限定每小於等於 日，僅能待申報 次(可累績申報)
		if (params.getPatient_nday_enable() == 1) {
			if (params.getHospitalized_type() == 1) {
				List<Map<String, Object>> ippData = ippDao.getListRocIdByOrderCodeAndMridAndDays(
						params.getPatient_nday_days(), params.getNhi_no(), mrIdListStr);

				if (ippData.size() > 0) {
					for (Map<String, Object> map : ippData) {
						float t = Float.parseFloat(map.get("TOTAL").toString());
						/// 如果資料大於限定值
						if (params.getPatient_nday_times() < t) {
							MR mr = mrDao.getMrByRocIdAndCode(map.get("ROC_ID").toString(), params.getNhi_no(),mrIdListStr);
							intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
									params.getNhi_no(),
									String.format("(醫令代碼)%s與支付準則條件:同患者限定每小於等於%d日，僅能待申報%d次(可累績申報)，疑似有出入",
											params.getNhi_no(), params.getPatient_nday_days(),
											params.getPatient_nday_times()),
									true);
						}
					}
				}
			}
			if (params.getOutpatient_type() == 1) {
				List<Map<String, Object>> oppData = oppDao.getListRocIdByDrugNoAndMridAndDays(
						params.getPatient_nday_days(), params.getNhi_no(), mrIdListStr);

				if (oppData.size() > 0) {
					for (Map<String, Object> map : oppData) {
						float t = Float.parseFloat(map.get("TOTAL").toString());
						/// 如果資料大於限定值
						if (params.getPatient_nday_times() < t) {
							MR mr = mrDao.getMrByRocIdAndCode(map.get("ROC_ID").toString(), params.getNhi_no(),mrIdListStr);
							intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
									params.getNhi_no(),
									String.format("(醫令代碼)%s與支付準則條件:同患者限定每小於等於%d日，僅能待申報%d次(可累績申報)，疑似有出入",
											params.getNhi_no(), params.getPatient_nday_days(),
											params.getPatient_nday_times()),
									true);
						}
					}
				}
			}
		}

		/// 3.
		/// 單一就醫紀錄上，須包含以下任一ICD診斷碼
		if (params.getInclude_icd_no_enable() == 1) {
			List<String> icdList = params.getLst_icd_no();
			int count = 0;
			for (MR mr : mrList) {
				for (String s : icdList) {
					if (mr.getCodeAll().contains(params.getNhi_no()) && !mr.getIcdAll().contains(s)) {

						if (count == 0) {
							if (params.getOutpatient_type() == 1 && mr.getDataFormat().equals("10")) {
								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
										params.getNhi_no(),
										String.format("(醫令代碼)%s與支付準則條件:單一就醫紀錄上，須包含以下任一ICD診斷碼[%s]，疑似有出入",
												params.getNhi_no(), icdList.toString()),
										true);
							}
							if (params.getHospitalized_type() == 1 && mr.getDataFormat().equals("20")) {
								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
										params.getNhi_no(),
										String.format("(醫令代碼)%s與支付準則條件:單一就醫紀錄上，須包含以下任一ICD診斷碼[%s]，疑似有出入",
												params.getNhi_no(), icdList.toString()),
										true);
							}

						}
						count++;
					}
				}
				count = 0;
			}
		}

		/// 4.
		/// 限定同患者執行過 ，並在小於等於，並在小於等於 日內，申報指支付標準代碼
		if (params.getCoexist_nhi_no_enable() == 1) {
			List<String> coList = params.getLst_co_nhi_no();
			List<MR> mrAppendList = new ArrayList<MR>();
			List<MR> mrAppendList2 = new ArrayList<MR>();
			int count = 0;
			for (MR mr : mrList) {
				for (String s : coList) {
					/// 先判斷有相同支付標準
					if (mr.getCodeAll().contains(params.getNhi_no())) {
						/// 再判斷沒有符合
						if (mr.getCodeAll().contains(s)) {

							if (count == 0) {
								mrAppendList.add(mr);
								count++;

							} else if (count == 1) {
								mrAppendList2.add(mr);
							}

						}
					}
					count = 0;
				}
			}
			count = 0;
			if (mrAppendList.size() > 0) {
				for (MR mr : mrAppendList) {
					for (MR mr2 : mrAppendList2) {
						if (mr.getRocId() == mr2.getRocId()) {

							Date d1 = mr.getMrDate();
							Date d2 = mr2.getMrDate();
							/// 當次與上次日期相減
							long diff = dayBetween(d1, d2);
							/// 如果日期大於指定天數
							if (params.getMin_coexist() < diff) {
								if (params.getOutpatient_type() == 1 && mr.getDataFormat().equals("10")) {
									intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
											params.getNhi_no(),
											String.format("(醫令代碼)%s與支付準則條件:限定同患者執行過[%s]，並在小於等於%d日內，申報指支付標準代碼，疑似有出入",
													params.getNhi_no(), coList.toString(), params.getMin_coexist()),
											true);
								}
								if (params.getHospitalized_type() == 1 && mr.getDataFormat().equals("20")) {
									intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
											params.getNhi_no(),
											String.format("(醫令代碼)%s與支付準則條件:限定同患者執行過[%s]，並在小於等於%d日內，申報指支付標準代碼，疑似有出入",
													params.getNhi_no(), coList.toString(), params.getMin_coexist()),
											true);
								}

							}

						}
					}
				}
			}
		}

		/// 5.
		/// 限定特定科別應用
		if (params.getLim_division_enable() == 1) {
			List<String> funList = params.getLst_division();
			List<String> funcAppend = new ArrayList<String>();

			for (String func : funList) {
				funcAppend.add(func);
			}

			List<MR> mrDataList = mrDao.getIntelligentMrByFuncName(mrIdListStr, funcAppend);
			/// 如果有非指定funcName資料
			if (mrDataList.size() > 0) {
				for (MR mr : mrDataList) {
					/// 如果門診
					if (params.getOutpatient_type() == 1 && mr.getDataFormat().equals("10")) {

						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:限定特定科%s別應用，疑似有出入", params.getNhi_no(), funcAppend),
								true);
					}
					/// 如果住院
					if (params.getHospitalized_type() == 1 && mr.getDataFormat().equals("20")) {
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:限定特定科%s別應用，疑似有出入", params.getNhi_no(), funcAppend),
								true);

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
	 * 計算兩個時間相差多少天
	 * 
	 * @param start
	 * @param end
	 * @return
	 * @throws ParseException
	 */
	private long dayBetween(Date start, Date end) throws ParseException {
		long diff = end.getTime() - start.getTime();

		TimeUnit time = TimeUnit.DAYS;
		long diffrence = time.convert(diff, TimeUnit.MILLISECONDS);

		return diffrence;
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
