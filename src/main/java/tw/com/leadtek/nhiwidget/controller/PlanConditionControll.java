package tw.com.leadtek.nhiwidget.controller;


import java.util.Map;

import javax.validation.Valid;

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
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import tw.com.leadtek.nhiwidget.dto.AdditionalConditionDto;
import tw.com.leadtek.nhiwidget.dto.AdditionalSearchPl;
import tw.com.leadtek.nhiwidget.dto.PlanConditionDto;
import tw.com.leadtek.nhiwidget.dto.PlanConditionPl;
import tw.com.leadtek.nhiwidget.dto.PlanSearchListDto;
import tw.com.leadtek.nhiwidget.dto.PlanSearchPl;
import tw.com.leadtek.nhiwidget.service.PlanConditionService;
import tw.com.leadtek.nhiwidget.service.pt.PaymentTermsService;

@Api(value = "參數設定-計畫可收案病例條件 API", tags = {"13 參數設定-計畫可收案病例條件"})
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PlanConditionControll {
    
    @Autowired
    private PaymentTermsService paymentTermsService;
    @Autowired
    private PlanConditionService planConditionService;
    
    //==== 
    @ApiOperation(value="13.01 計畫可收案病例條件清單", notes="", position=1)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PlanSearchListDto.class) //, responseContainer = "List"
    })
    @RequestMapping(value = "/plan/list", method = RequestMethod.POST)
    public ResponseEntity<?> planConditionList(@RequestHeader("Authorization") String jwt,
            @Valid @RequestBody PlanSearchPl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            if (params.getSearchName()==null) {
                params.setSearchName("");
            }
            String sortField = params.getSort_field();
            String sortDirection = params.getSort_direction(); // ASC|DESC
            if ((sortField==null)||(sortField.length()==0)) {
                sortField = "ID";
            }
            if ((sortDirection==null)||(sortDirection.length()==0)) {
                sortDirection = "ASC";
            }
            if (java.util.Arrays.asList(new String[] {"ID","DIVISION","PLAN_NAME"}).indexOf(sortField.toUpperCase())<0) {
                sortField = "ID";
            }
            if (java.util.Arrays.asList(new String[] {"ASC","DESC"}).indexOf(sortDirection.toUpperCase())<0) {
                sortField = "ASC";
            }
            java.util.Map<String, Object> retMap = planConditionService.findList(params.getSearchName(), params.getPageSize(), params.getPageIndex(),
                    sortField.toUpperCase(), sortDirection.toUpperCase());
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="13.02 新增計畫可收案病例條件", notes="", position=2)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }")
    })
    @RequestMapping(value = "/plan/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPlanCondition(@RequestHeader("Authorization") String jwt,
            @Valid @RequestBody PlanConditionPl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            long planId = planConditionService.addPlanCondition(params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", 0);
            retMap.put("new_id", planId);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="13.03 更新計畫可收案病例條件", notes="", position=3)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }")
    })
    @RequestMapping(value = "/plan/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePlanCondition(@RequestHeader("Authorization") String jwt,
            @PathVariable long id,
            @Valid @RequestBody PlanConditionPl params) throws Exception {
        
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

    @ApiOperation(value="13.05 取得計畫可收案病例條件", notes="", position=5)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PlanConditionDto.class)
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
    
    @ApiOperation(value="13.06 計畫可收案病例條件狀態設定", notes="", position=6)
    @ApiResponses({
        @ApiResponse(code = 200, message="{status:1.設定成功)/else.設定失敗 }")
    })
    @ApiImplicitParams({
        @ApiImplicitParam(name="Authorization", value="token", example="", dataType="String", paramType="header", required=true),
        @ApiImplicitParam(name="id", value="單號", dataType="String", paramType="path", required=true),
        @ApiImplicitParam(name="state", value="0.未啟動/1.使用中/2.鎖定", dataType="String", paramType="query", required=true)
     })
    @RequestMapping(value = "/plan/setactive/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> planConditionSetActive(@RequestHeader("Authorization") String jwt,
            @PathVariable long id,
            @RequestParam(required=true, defaultValue="") int state) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = planConditionService.updatePlanConditionActive(id, state);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>=1) {
                retMap.put("message", "設定完成。");
            } else {
                retMap.put("message", "單號不存在。");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }

}
