package tw.com.leadtek.nhiwidget.task.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.dto.PtBoneMarrowTransFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.service.IntelligentService;

@Service
public class PtBoneMarrowTransFeeServiceTask extends BasicIntelligentService{
	@Autowired
	private IntelligentService intelligentService;
	
	private String Category = "輸血及骨髓移植費";

	@SuppressWarnings("unchecked")
	public void validBoneMarrowTransFee(PtBoneMarrowTransFeePl params) throws ParseException {
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
		///需與 任一，並存單一就醫紀錄一併申報時 
        if(params.getCoexist_nhi_no_enable() == 1) {
        	
			this.Coexist_nhi_no_enable(params.getNhi_no(), params.getLst_co_nhi_no(), mrList, isOutpatien, isOutpatien);
        }
        
        ///2.
        ///參與計畫之病患，不得申報
        if(params.getNot_allow_plan_enable() == 1) {
        	
        	this.Not_allow_plan_enabl(params.getNhi_no(), params.getLst_allow_plan(), mrList, isOutpatien, isOutpatien);
        	
        }
        
        ///3.
        ///限定特定科別應用
        if(params.getLim_division_enable() == 1) {
        	
        	this.Lim_division_enable(params.getNhi_no(), params.getLst_division(), mrIdListStr, isOutpatien, isHospital);
        }
	}
}
