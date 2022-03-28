package tw.com.leadtek.nhiwidget.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dao.MRDao;

@Service
public class AIService {

	@Autowired
	private MRDao mrDao;
	@Autowired
	private ParametersService parametersService;
	/**
	 * 計算門診費用差異
	 * @param InhMrId
	 * @return
	 */
	public Map<String, Object> clinicCostDiff(String InhMrId) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			String msg = "";
			Map<String, Object> mr = mrDao.queryByInhMrID(InhMrId);
			Map<String, Object> clinicMap = mrDao.clinic(mr.get("ICDCM1").toString());
			// 設定檔上限
			int costDiffUl = Integer
					.parseInt(parametersService.getOneValueByName("INTELLIGENT_CONFIG", "COST_DIFF_UL"));
			// 設定檔下限
			int costDiffll = Integer
					.parseInt(parametersService.getOneValueByName("INTELLIGENT_CONFIG", "COST_DIFF_LL"));

			// 門診上下限
			float clinicUp = (Float.parseFloat(mr.get("T_DOT").toString())
					/ Float.parseFloat(clinicMap.get("up").toString())) * 100;
			float clinicDown = (Float.parseFloat(mr.get("T_DOT").toString())
					/ Float.parseFloat(clinicMap.get("down").toString())) * 100;

			if (clinicUp > costDiffUl) {
				msg = retMsg(COSTTYPE.UP.name(), InhMrId, mr.get("ICDCM1").toString(), clinicMap.get("up").toString(),
						clinicUp);
			} else if (clinicDown > costDiffll) {
				msg = retMsg(COSTTYPE.DOWN.name(), InhMrId, mr.get("ICDCM1").toString(),
						clinicMap.get("down").toString(), clinicUp);
			}

			data.put("mr", mr);
			data.put("clinicMap", clinicMap);
			data.put("costDiffUl", costDiffUl);
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
	public Map<String, Object> hospitalCostDiff(String InhMrId) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			String msg = "";
			Map<String, Object> mr = mrDao.queryByInhMrID(InhMrId);
			Map<String, Object> hospitalizedMap = mrDao.hospitalized(mr.get("ICDCM1").toString());
			// 設定檔上限
			int costDiffUl = Integer
					.parseInt(parametersService.getOneValueByName("INTELLIGENT_CONFIG", "COST_DIFF_UL"));
			// 設定檔下限
			int costDiffll = Integer
					.parseInt(parametersService.getOneValueByName("INTELLIGENT_CONFIG", "COST_DIFF_LL"));
			// 住院上下限
			float hospitalizedUp = (Float.parseFloat(mr.get("T_DOT").toString())
					/ Float.parseFloat(hospitalizedMap.get("up").toString())) * 100;
			float hospitalizedDown = (Float.parseFloat(mr.get("T_DOT").toString())
					/ Float.parseFloat(hospitalizedMap.get("down").toString())) * 100;

			if (hospitalizedUp > costDiffUl) {
				msg = retMsg(COSTTYPE.UP.name(), InhMrId, mr.get("ICDCM1").toString(), hospitalizedMap.get("up").toString(),
						hospitalizedUp);
			} else if (hospitalizedDown > costDiffll) {
				msg = retMsg(COSTTYPE.UP.name(), InhMrId, mr.get("ICDCM1").toString(), hospitalizedMap.get("up").toString(),
						hospitalizedDown);
			}

			data.put("mr", mr);
			data.put("hospitalMap", hospitalizedMap);
			data.put("costDiffUl", costDiffUl);
			data.put("msg", msg);
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
	public String retMsg(String ctype, String InhMrId, String ICDM1, String Val1, float Val2) {
		String result = "";
		switch (ctype) {
		case "UP":
			result = String.format("病歷編號: %s 主診斷: %s，整筆病例點數常態值上限(%s)相比，高出%.2f%%", InhMrId, ICDM1, Val1, Val2);
			break;
		case "DOWN":
			result = String.format("病歷編號: %s 主診斷: %s，整筆病例點數常態值下限(%s)相比，高出%.2f%%", InhMrId, ICDM1, Val1, Val2);
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
