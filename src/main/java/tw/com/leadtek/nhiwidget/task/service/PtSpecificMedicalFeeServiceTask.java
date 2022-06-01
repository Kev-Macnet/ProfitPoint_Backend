package tw.com.leadtek.nhiwidget.task.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dto.PtSpecificMedicalFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.MR;

@Service
public class PtSpecificMedicalFeeServiceTask extends BasicIntelligentService{

	private String Category = "特定診療檢查費";

	@SuppressWarnings("unchecked")
	public void validSpecificMedicalFee(PtSpecificMedicalFeePl params) throws ParseException {

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
		/// 不可與任一，並存單一就醫紀錄一併申報
		if (params.getExclude_nhi_no_enable() == 1) {
			
			this.Exclude_nhi_no_enable(params.getNhi_no(), params.getLst_nhi_no(), isOutpatien, isHospital, mrList);
		}

		/// 2.
		/// 限定同患者每次申報此支付標準代碼，與前一次申報日間隔大於等於 日，方可申報
		if (params.getInterval_nday_enable() == 1) {
			
			this.Interval_nday_enable(params.getNhi_no(), params.getInterval_nday(), mrIdListStr, isOutpatien, isHospital);

		}

		/// 3.
		/// 每組病歷號碼，每院限一年內，限定申報 次，如有需求另外提出
		if (params.getMax_times_enable() == 1) {
			
			this.Max_inpatient_enable(params.getNhi_no(),params.getMax_times(),isOutpatien, isHospital);
		}
	}
}
