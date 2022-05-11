package tw.com.leadtek.nhiwidget.task.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dto.PtPsychiatricFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.MR;

@Service
public class PtPsychiatricFeeServiceTask extends BasicIntelligentService{
	
	private String Category = "精神醫療治療費";

	@SuppressWarnings("unchecked")
	public void validPsychiatricFee(PtPsychiatricFeePl params) throws ParseException {
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
		///不可與 任一，並存單一就醫紀錄一併申報
		if(params.getExclude_nhi_no_enable() == 1) {
			
			this.Exclude_nhi_no_enable(params.getNhi_no(), params.getLst_nhi_no(), isOutpatien, isHospital, mrList);
		}
		
		/// 2.
		///同患者限定每小於等於 日，僅能待申報次(可累績申報)
		if(params.getPatient_nday_enable() == 1) {
			
			this.Patient_nday_enableT(params.getNhi_no(), params.getPatient_nday_days(), params.getPatient_nday_times(), mrIdListStr, isOutpatien, isHospital);
			
		}
		
		/// 3.
		///單一就醫紀錄應用數量,限定小於等於 次
		if(params.getMax_inpatient_enable() ==1 ) {
			
			this.Max_inpatient_enable(params.getNhi_no(), params.getMax_inpatient(),mrIdListStr, isOutpatien, isOutpatien);
		}
		
		/// 4.
		///限定特定科別應用
		if(params.getLim_division_enable() == 1) {
			this.Lim_division_enable(params.getNhi_no(), params.getLst_division(), mrIdListStr, isOutpatien, isHospital);
		}
	}
}
