package tw.com.leadtek.nhiwidget.task.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dao.PAY_CODEDao;
import tw.com.leadtek.nhiwidget.dto.PtInpatientFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.model.rdb.PAY_CODE;
import tw.com.leadtek.nhiwidget.service.IntelligentService;

@Service
public class PtInpatientFeeServiceTask {
	@Autowired
	private MRDao mrDao;

	@Autowired
	private PAY_CODEDao payCodeDao;
	@Autowired
	private IP_PDao ippDao;
	@Autowired
	private OP_PDao oppDao;

	@Autowired
	private IntelligentService intelligentService;

	public void validInpatienFee(PtInpatientFeePl params) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		Date dateObj = calendar.getTime();
		String eDateStr = sdf.format(dateObj);
		String sDateStr = minusYear(eDateStr);
		/// 違反案件數
		List<MR> mrList = mrDao.getIntelligentMR(sDateStr, eDateStr);
		List<MR> mrList2 = new ArrayList<MR>();
		List<String> mrIdListStr = new ArrayList<String>();
		/// 判斷支付條件準則日期，如果病歷小於該日，則不顯示
		for (MR mr : mrList) {
			/// 起日
			Date sd = sdf.parse(sDateStr);
			Date mrSd = mr.getMrDate();
			/// 訖日
			Date ed = sdf.parse(eDateStr);
			Date mrEd = mr.getMrEndDate();

			if (sd.before(mrSd) || ed.equals(mrEd)) {
				mrList2.add(mr);
				mrIdListStr.add(mr.getId().toString());
			}
		}

		if (params.getHospitalized_type() == 0) {
			for (MR r : mrList2) {
				if (r.getDataFormat() != "20" && r.getCodeAll().contains(params.getNhi_no())) {
					intelligentService.insertIntelligent(r, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s不適用(住院)就醫方式", params.getNhi_no()), true);
				}
			}
		} else if (params.getOutpatient_type() == 0) {
			for (MR r : mrList2) {
				if (r.getDataFormat() != "10") {
					intelligentService.insertIntelligent(r, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s不適用(門診)就醫方式", params.getNhi_no()), true);
				}
			}
		}

		/// 1.
		/// 單一住院就醫紀錄應用數量,限定小於等於n次
		if (params.getMax_inpatient_enable() == 1) {
			List<Map<String, Object>> dataList = ippDao.getListByOrderCodeAndMrid(params.getNhi_no(), mrIdListStr);
			for (Map<String, Object> map : dataList) {
				float t = Float.parseFloat(map.get("TOTAL").toString());
				/// 如果資料大於限定值
				if (params.getMax_inpatient() < t) {
					MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s與支付準則條件:單一住院就醫紀錄應用數量,限定小於等於%d次，疑似有出入", params.getNhi_no(),
									params.getMax_inpatient()),
							true);
				}
			}
		}

		/// 2.
		/// 單一急診就醫紀錄應用數量,限定小於等於
		if (params.getMax_emergency_enable() == 1) {
			List<Map<String, Object>> dataList = oppDao.getListByDrugNoAndMrid(params.getNhi_no(), mrIdListStr);
			for (Map<String, Object> map : dataList) {
				float t = Float.parseFloat(map.get("TOTAL").toString());
				/// 如果資料大於限定值
				if (params.getMax_emergency() < t) {
					MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s與支付準則條件:單一急診就醫紀錄應用數量,限定小於等於%d次，疑似有出入", params.getNhi_no(),
									params.getMax_inpatient()),
							true);
				}
			}
		}

		/// 3.
		/// 不可與 支付代碼任一，並存單一就醫紀錄一併申報
		if (params.getExclude_nhi_no_enable() == 1) {
			List<String> nhiNoList = params.getLst_nhi_no();
			int count = 0;
			for (MR mr : mrList2) {
				for (String nhiNo : nhiNoList) {
					if (mr.getCodeAll().contains(nhiNo) && count == 0) {
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(),
								params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:不可與%s(輸入支付標準代碼)%s任一，並存單一就醫紀錄一併申報，疑似有出入",
										params.getNhi_no(), nhiNo),
								true);
						count++;
					} else if (mr.getCodeAll().contains(nhiNo) && count > 0) {
						continue;
					}
				}
			}

		}

		/// 4.
		/// 每組病歷號碼，每院限申報
		if (params.getMax_patient_no_enable() == 1) {
			List<Map<String, Object>> ippData = ippDao.getListCountByOrderCodeAndMrid(params.getNhi_no(), mrIdListStr);
			List<Map<String, Object>> oppData = oppDao.getListCountByDrugNoAndMrid(params.getNhi_no(), mrIdListStr);

			if (ippData.size() > 0) {
				for (Map<String, Object> map : ippData) {
					float t = Float.parseFloat(map.get("COUNT").toString());
					if (params.getMax_patient_no() < t) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(),
								params.getNhi_no(), String.format("(醫令代碼)%s與支付準則條件:每組病歷號碼，每院限申報%d次，疑似有出入",
										params.getNhi_no(), params.getMax_patient_no()),
								true);
					}
				}
			}
			if (oppData.size() > 0) {
				for (Map<String, Object> map : oppData) {
					float t = Float.parseFloat(map.get("COUNT").toString());
					if (params.getMax_patient_no() < t) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(),
								params.getNhi_no(), String.format("(醫令代碼)%s與支付準則條件:每組病歷號碼，每院限申報%d次，疑似有出入",
										params.getNhi_no(), params.getMax_patient_no()),
								true);
					}
				}
			}
		}

		/// 5.
		/// 參與計畫之病患，不得申報
		if (params.getNot_allow_plan_enable() == 1) {
			List<String> planList = params.getLst_allow_plan();
			for (MR mr : mrList2) {
				if (mr.getCodeAll().contains(params.getNhi_no())) {
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s與支付準則條件:參與[%s]計畫之病患，不得申報，疑似有出入", params.getNhi_no(),
									planList.toString()),
							true);
				}
			}
		}

		/// 6.
		/// 需與以下任一支付標準代碼並存，方可進行申報
		if (params.getCoexist_nhi_no_enable() == 1) {
			List<String> coList = params.getLst_co_nhi_no();
			int count = 0;
			for (MR mr : mrList2) {
				for (String s : coList) {
					/// 先判斷有相同支付標準
					if (mr.getCodeAll().contains(params.getNhi_no())) {
						/// 再判斷沒有符合
						if (!mr.getCodeAll().contains(s)) {
							if (count == 0) {

								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(),
										params.getNhi_no(), String.format("(醫令代碼)%s與支付準則條件:需與[%s]任一，並存單一就醫紀錄一併申報，疑似有出入",
												params.getNhi_no(), coList.toString()),
										true);
							}
							count++;
						}
					}
					count = 0;
				}
			}
		}

		/// 7.
		/// 同科別，門急診當次轉住院，單一住院就醫紀錄， 門診診察費或住院診察費支付標準代碼，不可並存
		if (params.getNo_coexist_enable() == 1) {
			for (MR mr : mrList2) {
				/// 先判斷有相同支付標準
				if (mr.getCodeAll().contains(params.getNhi_no()) && mr.getDataFormat().equals("10")) {
					String[] split = mr.getCodeAll().toString().split(",");
					for (String s : split) {
						if (s.equals(params.getNhi_no())) {

							PAY_CODE pcModel = payCodeDao.findDataByCode(params.getNhi_no());
							if (pcModel.getCodeType().equals("門診診察費")) {
								intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(),
										params.getNhi_no(),
										String.format(
												"(醫令代碼)%s與支付準則條件:同科別，門急診當次轉住院，單一住院就醫紀錄， 門診診察費或住院診察費支付標準代碼，不可並存:，疑似有出入",
												params.getNhi_no()),
										true);
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
	 * @param early
	 * @param late
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
