package tw.com.leadtek.nhiwidget.task.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dto.PtTreatmentFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.service.IntelligentService;

@Service
public class PtTreatmentFeeServiceTask extends BasicIntelligentService{
	@Autowired
	private MRDao mrDao;

	@Autowired
	private OP_PDao oppDao;

	@Autowired
	private IntelligentService intelligentService;
	
	private String Category = "治療處置費";

	@SuppressWarnings("unchecked")
	public void validTreatmentFee(PtTreatmentFeePl params) throws ParseException {
		boolean isHospital = false;
		boolean isOutpatien = false;
		Map<String,Object> retMap = this.vaidIntelligentTtype(params.getStart_date(), params.getEnd_date(), params.getNhi_no(), params.getOutpatient_type(), params.getHospitalized_type());
		isHospital = (boolean) retMap.get("isHospital");
		isOutpatien = (boolean) retMap.get("isOutpatien");
		/// 存放病例
		List<MR> mrList = new ArrayList<MR>();
		mrList.addAll((Collection<? extends MR>) retMap.get("mrList"));
		/// 存放mrID
		List<String> mrIdListStr = new ArrayList<String>();
		mrIdListStr.addAll((Collection<? extends String>) retMap.get("mrIdListStr"));
		
		String sDateStr, eDateStr;
		sDateStr = (String) retMap.get("sDateStr");
		eDateStr = (String) retMap.get("eDateStr");

		/// 1.
		/// 不可與 任一，並存單一就醫紀錄一併申報
		if (params.getExclude_nhi_no_enable() == 1) {
			
			this.Exclude_nhi_no_enable(params.getNhi_no(), params.getLst_nhi_no(), isOutpatien, isHospital, mrList);
		}
		/// 2.
		/// 需與以下支付標準代碼任一並存，方可進行申報
		if (params.getCoexist_nhi_no_enable() == 1) {
			this.Coexist_nhi_no_enableS(params.getNhi_no(), params.getLst_co_nhi_no(), mrList, isOutpatien, isHospital);
		}

		/// 3.
		/// 單一就醫紀錄應用總數量,限定小於等於
		if (params.getMax_inpatient_enable() == 1) {
			
			this.Max_inpatient_enable(params.getNhi_no(), params.getMax_inpatient(),mrIdListStr, isOutpatien, isOutpatien);
		}

		/// 4.
		/// 單一就醫紀錄上，每日限定應用小於等於
		if (params.getMax_daily_enable() == 1) {
			
			this.Max_daily_enable(params.getNhi_no(), params.getMax_daily(), mrIdListStr, isOutpatien, isHospital);
			
		}

		/// 5.
		/// 單一就醫紀錄上，每 日內，限定應用小於等於 次
		if (params.getEvery_nday_enable() == 1) {
			
			this.Every_nday_enable(params.getNhi_no(), params.getEvery_nday_days(), params.getEvery_nday_times(), mrIdListStr, isOutpatien, isHospital);
			
		}

		/// 6.
		/// 同患者限定每小於等於 日，總申報次數小於等於 次為原則
		if (params.getPatient_nday_enable() == 1) {
			
			this.Patient_nday_enable(params.getNhi_no(),params.getPatient_nday_days(), params.getPatient_nday_times(), mrIdListStr, isOutpatien, isHospital);
		}

		/// 7.
		/// 每組病歷號碼，每院限申報 次
		if (params.getMax_patient_enable() == 1) {
			List<Map<String, Object>> mrDataList = mrDao.getRocListByIdAndCode(mrIdListStr, params.getNhi_no());
			if (mrDataList.size() > 0) {
				for (Map<String, Object> map : mrDataList) {
					int c = Integer.parseInt(map.get("COUNT").toString());

					if (params.getMax_patient() < c) {
						MR mr = mrDao.getMrByRocIdAndCode(map.get("ROC_ID").toString(), params.getNhi_no(),mrIdListStr);
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:每組病歷號碼，每院限申報%d次，疑似有出入", params.getNhi_no(),
										params.getMax_patient()),
								true);
					}
				}
			}
		}

		/// 8.
		/// 單一就醫紀錄上，須包含以下任一ICD診斷碼
		if (params.getInclude_icd_no_enable() == 1) {
			this.Include_icd_no_enable(params.getNhi_no(), params.getLst_icd_no(), mrList, isOutpatien, isHospital);
		}

		/// 9.
		/// 每月申報數量，不可超過門診就診人次之百分之
		if (params.getMax_month_enable() == 1) {

			/// 取該時間區間準則門診總數
			Map<String, Object> oppData = oppDao.getTotalDrugByNo(sDateStr, eDateStr, params.getNhi_no());

			/// 取病例total
			List<Map<String, Object>> oppList = oppDao.getPerMonthByDrugNoAndTotal(sDateStr, eDateStr, params.getNhi_no());
			if (oppList.size() > 0) {
				for(Map<String, Object> listMap : oppList) {
					float count = Float.valueOf(oppData.get("COUNT").toString());
					float total = Float.valueOf(listMap.get("TOTAL").toString());
					float percent = (total / count) * 100;
					///如果超過設定值寫入
					if (params.getMax_month_percentage() < percent) {
						MR mr = mrDao.getMrByRocIdAndCode(listMap.get("ROC_ID").toString(), params.getNhi_no(), mrIdListStr);
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:每月申報數量，不可超過門診就診人次之百分之%d，疑似有出入", params.getNhi_no(),
										params.getMax_month_percentage()),
								true);
					}
				}
			}

		}

		/// 10.
		/// 患者限定年紀小於等於 歲，方可進行申報
		if (params.getMax_age_enable() == 1) {
			this.Lim_age_enable(params.getNhi_no(), params.getMax_age(), mrIdListStr, isOutpatien, isHospital);
		}

		/// 11.
		/// 限定特定科別應用
		if (params.getLim_division_enable() == 1) {
			this.Lim_division_enable(params.getNhi_no(), params.getLst_division(), mrIdListStr, isOutpatien, isHospital);
		}
	}
}
