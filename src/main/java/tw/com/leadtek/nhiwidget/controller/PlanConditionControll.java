package tw.com.leadtek.nhiwidget.controller;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import tw.com.leadtek.nhiwidget.dto.PlanConditionDto;
import tw.com.leadtek.nhiwidget.dto.PlanConditionPl;
import tw.com.leadtek.nhiwidget.service.PaymentTermsService;
import tw.com.leadtek.nhiwidget.service.PlanConditionService;

@Api(value = "參數設定-計畫可收案病例條件 API", tags = {"13 參數設定-計畫可收案病例條件"})
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PlanConditionControll {
    
    @Autowired
    private PaymentTermsService paymentTermsService;
    @Autowired
    private PlanConditionService planConditionService;
    
    //==== 
    @ApiOperation(value="13.01 計畫計畫可收案病例條件清單", notes="", position=1)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PlanConditionDto.class)
    })
    @RequestMapping(value = "/plan/list", method = RequestMethod.POST)
    public ResponseEntity<?> planConditionList(@RequestHeader("Authorization") String jwt,
            @RequestParam(required=false, defaultValue="") String searchName) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            if (searchName==null) {
                searchName= "";
            }
            java.util.List<Map<String, Object>> retMap = planConditionService.findList(searchName);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="13.02 新增計畫可收案病例條件", notes="", position=2)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }")
    })
    @RequestMapping(value = "/plan/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPlanCondition(@RequestHeader("Authorization") String jwt,
            @RequestBody PlanConditionPl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = planConditionService.addPlanCondition(params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    
    @ApiOperation(value="13.03 更新計畫可收案病例條件", notes="", position=3)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }")
    })
    @RequestMapping(value = "/plan/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<?> updatePlanCondition(@RequestHeader("Authorization") String jwt,
            @PathVariable long id,
            @RequestBody PlanConditionPl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = planConditionService.updatePlanCondition(id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    
    @ApiOperation(value="13.04 刪除計畫可收案病例條件", notes="", position=4)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }")
    })
    @RequestMapping(value = "/plan/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePlanCondition(@RequestHeader("Authorization") String jwt,
            @PathVariable long id) throws Exception {
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = planConditionService.deletePlanCondition(id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }

    @ApiOperation(value="13.05 計畫可收案病例條件", notes="", position=5)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }")
    })
    @RequestMapping(value = "/plan/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> obtainPlanCondition(@RequestHeader("Authorization") String jwt,
            @PathVariable long id) throws Exception {
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = planConditionService.findOne(id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }

}
