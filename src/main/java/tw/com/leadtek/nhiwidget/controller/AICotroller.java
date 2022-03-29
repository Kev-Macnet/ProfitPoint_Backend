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
	@RequestMapping(value = "/getClinicCostDiffData", method = { RequestMethod.GET, RequestMethod.POST })
	public Object getClinicCostDiffData() {
		return aiService.clinicCostDiff();
	}
	
	@ResponseBody
	@RequestMapping(value = "/getHospitalCostDiffData", method = { RequestMethod.GET, RequestMethod.POST })
	public Object getHospitalCostDiffData() {
		return aiService.hospitalCostDiff();
	}
}
