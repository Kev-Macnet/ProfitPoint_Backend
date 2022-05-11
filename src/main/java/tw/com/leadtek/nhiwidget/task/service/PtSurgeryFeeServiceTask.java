package tw.com.leadtek.nhiwidget.task.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dto.PtSurgeryFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.MR;

@Service
public class PtSurgeryFeeServiceTask extends BasicIntelligentService{
	
	private String Category = "手術費";

	@SuppressWarnings("unchecked")
	public void validSurgeryFee(PtSurgeryFeePl params) throws ParseException {
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
		/// 不可與 支付代碼任一，並存單一就醫紀錄一併申報
		if(params.getExclude_nhi_no_enable() == 1) {
			
			this.Exclude_nhi_no_enable(params.getNhi_no(), params.getLst_nhi_no(), isOutpatien, isHospital, mrList);
		}
		
		/// 2.
		///患者限定年紀小於等於 歲，方可進行申報
		if(params.getLim_age_enable() == 1) {
			
			this.Lim_age_enable(params.getNhi_no(), params.getLim_age(), mrIdListStr, isOutpatien, isHospital);
			
		}
		
		/// 3.
		/// 限定特定科別應用
		if (params.getLim_division_enable() == 1) {
			this.Lim_division_enable(params.getNhi_no(), params.getLst_division(), mrIdListStr, isOutpatien, isHospital);

		}

	}
}
