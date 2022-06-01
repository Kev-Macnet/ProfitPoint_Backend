package tw.com.leadtek.nhiwidget.task.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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
import tw.com.leadtek.nhiwidget.dto.PtMedicineFeePl;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.service.IntelligentService;

@Service
public class PtMedicineFeeServiceTask extends BasicIntelligentService{
	@Autowired
	private MRDao mrDao;
	@Autowired
	private IP_PDao ippDao;
	@Autowired
	private OP_PDao oppDao;

	@Autowired
	private IntelligentService intelligentService;
	
	private String Category = "藥費";

	@SuppressWarnings("unchecked")
	public void validMedicineFee(PtMedicineFeePl params) throws ParseException {
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
		///每件給藥日數不得超過
		if(params.getMax_nday_enable() ==1) {
		
			///如果住院
			if(isHospital ) {
				
				List<Map<String,Object>> ippData = ippDao.getListByDaysAndCodeAndMrid(params.getMax_nday(), params.getNhi_no(), mrIdListStr);
				if(ippData.size() > 0) { 
					for(Map<String,Object> map: ippData) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:每件給藥日數不得超過%d日，疑似有出入", params.getNhi_no(),params.getMax_nday()), true);
					}
				}
			}
			///如果門診
			if(isOutpatien) {
				
				List<Map<String,Object>> oppData = oppDao.getListByDaysAndCodeAndMrid(params.getMax_nday(), params.getNhi_no(), mrIdListStr);
				if(oppData.size() > 0) {
					for(Map<String,Object> map: oppData) {
						MR mr = mrDao.getMrByID(map.get("MR_ID").toString());
						intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.VIOLATE.value(), params.getNhi_no(),
								String.format("(醫令代碼)%s與支付準則條件:每件給藥日數不得超過%d日，疑似有出入", params.getNhi_no(),params.getMax_nday()), true);
					}
				}
			}
			
		}
	}
}
