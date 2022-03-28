package tw.com.leadtek.nhiwidget.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import tw.com.leadtek.nhiwidget.service.AIService;

@RestController
@RequestMapping("/auth")
public class AICotroller {
	
	@Autowired
	private AIService aiService;

	@ResponseBody
	@RequestMapping(value = "/getClinicCostDiffData/{InhMrId}", method = { RequestMethod.GET, RequestMethod.POST })
	public Object getClinicCostDiffData(@PathVariable String InhMrId) {
		return aiService.clinicCostDiff(InhMrId);
	}
	
	@ResponseBody
	@RequestMapping(value = "/getHospitalCostDiffData/{InhMrId}", method = { RequestMethod.GET, RequestMethod.POST })
	public Object getHospitalCostDiffData(@PathVariable String InhMrId) {
		return aiService.hospitalCostDiff(InhMrId);
	}
}
