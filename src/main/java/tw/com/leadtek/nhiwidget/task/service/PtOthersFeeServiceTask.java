package tw.com.leadtek.nhiwidget.task.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dto.PtOthersFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.service.IntelligentService;

@Service
public class PtOthersFeeServiceTask extends BasicIntelligentService{
	@Autowired
	private MRDao mrDao;

	@Autowired
	private IP_PDao ippDao;
	@Autowired
	private OP_PDao oppDao;

	@Autowired
	private IntelligentService intelligentService;
	
	private String Category = "不分類";

	@SuppressWarnings("unchecked")
	public void validOthersFee(PtOthersFeePl params) throws ParseException {
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
		/// 不可與  任一，並存單一就醫紀錄一併申報
		if(params.getExclude_nhi_no_enable() == 1) {
			
			this.Exclude_nhi_no_enable(params.getNhi_no(), params.getLst_nhi_no(), isOutpatien, isHospital, mrList);
		}
		
		/// 2.
		///單一就醫紀錄應用數量,限定小於等於 次
		if(params.getMax_inpatient_enable() == 1) {
			
			if(isHospital) {
				List<Map<String, Object>> ippData = ippDao.getListByOrderCodeAndMrid(params.getNhi_no(), mrIdListStr);
				
				if (ippData.size() > 0) {
					
					for (Map<String, Object> map : ippData) {
						float t = Float.parseFloat(map.get("TOTAL").toString());
						/// 如果資料大於限定值
						if (params.getMax_inpatient() < t) {
							MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
							intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
									params.getNhi_no(), String.format("(醫令代碼)%s與支付準則條件:單一就醫紀錄應用數量,限定小於等於%d次，疑似有出入",
											params.getNhi_no(), params.getMax_inpatient()),
									true);
						}
					}
				}
			}
			if(isOutpatien) {
				List<Map<String, Object>> oppData = oppDao.getListByDrugNoAndMrid2(params.getNhi_no(), mrIdListStr);
				if (oppData.size() > 0) {
					for (Map<String, Object> map : oppData) {
						float t = Float.parseFloat(map.get("TOTAL").toString());
						/// 如果資料大於限定值
						if (params.getMax_inpatient() < t) {
							MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
							intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(),
									params.getNhi_no(), String.format("(醫令代碼)%s與支付準則條件:單一就醫紀錄應用數量,限定小於等於%d次，疑似有出入",
											params.getNhi_no(), params.getMax_inpatient()),
									true);
						}
					}
				}
			}
		}
		
		/// 3.
		///每組病歷號碼，每院限一年內，限定申報 次，如有需求另外提出
		if(params.getMax_inpatient_enable() == 1) {
			
			this.Max_inpatient_enable(params.getNhi_no(),params.getMax_times(),isOutpatien, isHospital);
		}
		
		/// 4.
		///限定同患者每次申報此支付標準代碼，與前一次申報日間隔大於等於 日，方可申報
		if(params.getInterval_nday_enable() == 1) {
			
			this.Interval_nday_enable(params.getNhi_no(), params.getInterval_nday(), mrIdListStr, isOutpatien, isHospital);
		}
		
		/// 5.
		///限定同患者每次申報此支付標準代碼， 日內，限定申報小於等於 次
		if(params.getPatient_nday_enable() ==1) {
			
			this.Patient_nday_enable(params.getNhi_no(),params.getPatient_nday_days(), params.getPatient_nday_times(), mrIdListStr, isOutpatien, isHospital);
		}
	}
}
