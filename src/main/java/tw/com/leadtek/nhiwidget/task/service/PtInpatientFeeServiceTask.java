package tw.com.leadtek.nhiwidget.task.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dto.PtInpatientFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.service.IntelligentService;

@Service
public class PtInpatientFeeServiceTask extends BasicIntelligentService{
	@Autowired
	private MRDao mrDao;

	@Autowired
	private IP_PDao ippDao;
	@Autowired
	private OP_PDao oppDao;

	@Autowired
	private IntelligentService intelligentService;

	private String Category = "住院診察費";

	@SuppressWarnings("unchecked")
	public void validInpatienFee(PtInpatientFeePl params) throws ParseException {
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

		/// 1.
		/// 單一住院就醫紀錄應用數量,限定小於等於n次
		if (params.getMax_inpatient_enable() == 1) {
			
			List<Map<String, Object>> dataList = ippDao.getListByOrderCodeAndMrid(params.getNhi_no(), mrIdListStr);
			for (Map<String, Object> map : dataList) {
				float t = Float.parseFloat(map.get("TOTAL").toString());
				/// 如果資料大於限定值
				if (params.getMax_inpatient() < t) {
					MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
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
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
							String.format("(醫令代碼)%s與支付準則條件:單一急診就醫紀錄應用數量,限定小於等於%d次，疑似有出入", params.getNhi_no(),
									params.getMax_inpatient()),
							true);
				}
			}
		}

		/// 3.
		/// 不可與 支付代碼任一，並存單一就醫紀錄一併申報
		if (params.getExclude_nhi_no_enable() == 1) {
			
			this.Exclude_nhi_no_enable(params.getNhi_no(), params.getLst_nhi_no(), isOutpatien, isHospital, mrList);
		}

		/// 4.
		/// 每組病歷號碼，每院限申報
		if (params.getMax_patient_no_enable() == 1) {
			///如果門診
			if (isOutpatien) {
				List<Map<String, Object>> oppData = oppDao.getListCountByDrugNoAndMrid(params.getNhi_no(), mrIdListStr);
				if (oppData.size() > 0) {
					for (Map<String, Object> map : oppData) {
						float t = Float.parseFloat(map.get("COUNT").toString());
						if (params.getMax_patient_no() < t) {
							MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
							intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
									params.getNhi_no(), String.format("(醫令代碼)%s與支付準則條件:每組病歷號碼，每院限申報%d次，疑似有出入",
											params.getNhi_no(), params.getMax_patient_no()),
									true);
						}
					}
				}
			}
			///如果住院
			if (isHospital) {
				List<Map<String, Object>> ippData = ippDao.getListCountByOrderCodeAndMrid(params.getNhi_no(),
						mrIdListStr);

				if (ippData.size() > 0) {
					for (Map<String, Object> map : ippData) {
						float t = Float.parseFloat(map.get("COUNT").toString());
						if (params.getMax_patient_no() < t) {
							MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
							intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
									params.getNhi_no(), String.format("(醫令代碼)%s與支付準則條件:每組病歷號碼，每院限申報%d次，疑似有出入",
											params.getNhi_no(), params.getMax_patient_no()),
									true);
						}
					}
				}
			}

		}

		/// 5.
		/// 參與計畫之病患，不得申報
		if (params.getNot_allow_plan_enable() == 1) {
			this.Not_allow_plan_enabl(params.getNhi_no(), params.getLst_allow_plan(), mrList, isOutpatien, isOutpatien);
		}

		/// 6.
		/// 需與以下任一支付標準代碼並存，方可進行申報
		if (params.getCoexist_nhi_no_enable() == 1) {
			
			this.Coexist_nhi_no_enable(params.getNhi_no(), params.getLst_co_nhi_no(), mrList, isOutpatien, isOutpatien);
		}

		/// 7.
		/// 同科別，門急診當次轉住院，單一住院就醫紀錄， 門診診察費或住院診察費支付標準代碼，不可並存
		if (params.getNo_coexist_enable() == 1) {
			/// 如果門診
			if (isOutpatien) {
				List<Map<String, Object>> opData = mrDao.getIdByOPandPaycode(mrIdListStr, "住院診察費");
				for (Map<String, Object> map : opData) {
					MR mr = mrDao.getMrByID(map.get("ID").toString());
					try {
						
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:同科別，門急診當次轉住院，單一住院就醫紀錄， 住院診察費支付標準代碼，不可並存:，疑似有出入",
										params.getNhi_no()),
								true);
					}catch(Exception e) {
						String errStr = e.getMessage();
					}
				}

			}
			/// 如果住院
			if (isHospital) {
				List<Map<String, Object>> ipData = mrDao.getIdByIPandPaycode(mrIdListStr, "門診診察費");
				for (Map<String, Object> map : ipData) {
					MR mr = mrDao.getMrByID(map.get("ID").toString());
					try {
						
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:同科別，門急診當次轉住院，單一住院就醫紀錄， 門診診察費支付標準代碼，不可並存:，疑似有出入",
										params.getNhi_no()),
								true);
					}catch(Exception e) {
						String errStr = e.getMessage();
					}
				}
			}

		}

	}
}
