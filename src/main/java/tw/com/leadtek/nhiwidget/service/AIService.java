package tw.com.leadtek.nhiwidget.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.model.rdb.MR;

@Service
public class AIService {

	@Autowired
	private MRDao mrDao;
	@Autowired
	private ParametersService parametersService;
	@Autowired
	private IntelligentService intelligentService;
	/**
	 * 計算門診費用差異
	 * @param InhMrId
	 * @return
	 */
	public Map<String, Object> clinicCostDiff() {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			String msg = "";
			List<Map<String, Object>> listClinicMap = mrDao.clinic();
			// 設定檔上限
			int costDiffUl = Integer
					.parseInt(parametersService.getOneValueByName("INTELLIGENT_CONFIG", "COST_DIFF_UL"));
			// 設定檔下限
			int costDiffll = Integer
					.parseInt(parametersService.getOneValueByName("INTELLIGENT_CONFIG", "COST_DIFF_LL"));
			for(Map<String,Object> clinicMap : listClinicMap) {
				MR mr = mrDao.getMrByID(clinicMap.get("ID").toString());
				// 門診上下限
				float clinicUp = (Float.parseFloat(mr.getTotalDot().toString())
						/ Float.parseFloat(clinicMap.get("up").toString())) * 100;
				float clinicDown = (Float.parseFloat(mr.getTotalDot().toString())
						/ Float.parseFloat(clinicMap.get("down").toString())) * 100;
				double dUp = (double) clinicMap.get("up");
				double dDown = (double) clinicMap.get("down");
				int iUp = (int)dUp;
				int iDown = (int)dDown;
				if (clinicUp > costDiffUl) {
					msg = retMsg(COSTTYPE.UP.name(), mr.getInhMrId(), mr.getIcdcm1(), iUp,
							clinicUp);
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(), mr.getIcdcm1().toString(), msg, true);
				} else if (clinicDown > costDiffll) {
					msg = retMsg(COSTTYPE.DOWN.name(), mr.getInhMrId(), mr.getIcdcm1(),
							iDown, clinicUp);
					intelligentService.insertIntelligent(mr, INTELLIGENT_REASON.COST_DIFF.value(), mr.getIcdcm1().toString(), msg, true);
				}
			}

//			data.put("mr", mr);
//			data.put("clinicMap", clinicMap);
//			data.put("costDiffUl", costDiffUl);
			data.put("msg", msg);
		} catch (Exception e) {
			e.printStackTrace();
			data.put("msg", "該病歷查無門診資料");
		}
		return data;
	}
    /**
     * 計算住院費用差異
     * @param InhMrId
     * @return
     */
	public Map<String, Object> hospitalCostDiff() {
		Map<String, Object> data = new HashMap<String, Object>();

		try {
			String msg = "";
//			Map<String, Object> mr = mrDao.queryByInhMrID(InhMrId);
			List<Map<String, Object>> listHospitalizedMap = mrDao.hospitalized();
			// 設定檔上限
			int costDiffUl = Integer
					.parseInt(parametersService.getOneValueByName("INTELLIGENT_CONFIG", "COST_DIFF_UL"));
			// 設定檔下限
			int costDiffll = Integer
					.parseInt(parametersService.getOneValueByName("INTELLIGENT_CONFIG", "COST_DIFF_LL"));
			for(Map<String, Object> hospitalizedMap : listHospitalizedMap) {
				
				MR mm = mrDao.getMrByID(hospitalizedMap.get("MR_ID").toString());
				// 住院上下限
				float hospitalizedUp = (Float.parseFloat(mm.getTotalDot().toString())
						/ Float.parseFloat(hospitalizedMap.get("up").toString())) * 100;
				float hospitalizedDown = (Float.parseFloat(mm.getTotalDot().toString())
						/ Float.parseFloat(hospitalizedMap.get("down").toString())) * 100;
				double dUp = (double) hospitalizedMap.get("up");
				double dDown = (double) hospitalizedMap.get("down");
				int iUp = (int) dUp;
				int iDown = (int) dDown;
				if (hospitalizedUp > costDiffUl) {
					msg = retMsg(COSTTYPE.UP.name(), mm.getInhMrId(),mm.getIcdcm1().toString(), iUp,
							hospitalizedUp);
					
					intelligentService.insertIntelligent(mm, INTELLIGENT_REASON.COST_DIFF.value(), mm.getIcdcm1().toString(), msg, true);
				} else if (hospitalizedDown > costDiffll) {
					msg = retMsg(COSTTYPE.DOWN.name(),  mm.getInhMrId(), mm.getIcdcm1().toString(), iDown,
							hospitalizedDown);
					intelligentService.insertIntelligent(mm, INTELLIGENT_REASON.COST_DIFF.value(), mm.getIcdcm1().toString(), msg, true);
				}
			}

//			data.put("mr", mm);
//			data.put("hospitalMap", hospitalizedMap);
//			data.put("costDiffUl", costDiffUl);
			data.put("msg",msg);
		} catch (Exception e) {
			e.printStackTrace();
			data.put("msg", "該病歷無近一年住院資料");
		}
		return data;
	}

	/**
	 * 
	 * @param ctype,   enum帶入要顯示上限還是下限
	 * @param InhMrId, 病歷單號
	 * @param ICDM1,   主診斷號
	 * @param Val1,    病歷點數常數
	 * @param Val2,    對比%
	 * @return
	 */
	public String retMsg(String ctype, String InhMrId, String ICDM1, int Val1, float Val2) {
		String result = "";
	    String paraResult = "";
		switch (ctype) {
		case "UP":
			///取得設定檔的上限word
			paraResult = parametersService.getOneValueByName("INTELLIGENT", "COST_DIFF_UL_WORDING");
			result = String.format(paraResult+"%", InhMrId, ICDM1, Val1, Val2);
			break;
		case "DOWN":
			///取得設定檔的下限word
			paraResult = parametersService.getOneValueByName("INTELLIGENT", "COST_DIFF_LL_WORDING");
			result = String.format(paraResult+"%", InhMrId, ICDM1, Val1, Val2);
			break;
		default:
			result = "";
		}

		return result;
	}

	public enum COSTTYPE {
		UP, DOWN
	}
}
