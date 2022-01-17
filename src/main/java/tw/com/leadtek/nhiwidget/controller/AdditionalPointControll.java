package tw.com.leadtek.nhiwidget.controller;


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
import tw.com.leadtek.nhiwidget.dto.AdditionalSearchListDto;
import tw.com.leadtek.nhiwidget.dto.AdditionalConditionPl;
import tw.com.leadtek.nhiwidget.dto.AdditionalSearchPl;
import tw.com.leadtek.nhiwidget.service.AdditionalPointService;
import tw.com.leadtek.nhiwidget.service.PaymentTermsService;
import tw.com.leadtek.tools.Utility;

@Api(value = "參數設定-總額外點數條件 API", tags = {"14 參數設定-總額外點數條件"})
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdditionalPointControll {
    
    @Autowired
    private PaymentTermsService paymentTermsService;
    @Autowired
    private AdditionalPointService additionalPointService;
    
    //==== 
    @ApiOperation(value="14.01 總額外點數條件清單", notes="", position=1)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=AdditionalSearchListDto.class) //, responseContainer = "List"
    })
    @RequestMapping(value = "/additional/list", method = RequestMethod.POST)
    public ResponseEntity<?> additionalConditionList(@RequestHeader("Authorization") String jwt,
            @RequestBody AdditionalSearchPl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            //shunxian 2022
            additionalPointService.fillStartAndEndNull(new java.util.Date());
            
            java.util.Date da1 = Utility.detectDate(params.getStart_date());
            java.util.Date da2 = Utility.detectDate(params.getEnd_date());
            String sortField = params.getSort_field();
            String sortDirection = params.getSort_direction(); // ASC|DESC
            if ((sortField==null)||(sortField.length()==0)) {
                sortField = "ID";
            }
            if ((sortDirection==null)||(sortDirection.length()==0)) {
                sortDirection = "ASC";
            }
            if (java.util.Arrays.asList(new String[] {"ID","START_DATE","END_DATE"}).indexOf(sortField.toUpperCase())<0) {
                sortField = "ID";
            }
            if (java.util.Arrays.asList(new String[] {"ASC","DESC"}).indexOf(sortDirection.toUpperCase())<0) {
                sortField = "ASC";
            }

            java.util.Map<String, Object> retMap = additionalPointService.findList(da1, da2, params.getPageSize(), params.getPageIndex(),
                    sortField.toUpperCase(), sortDirection.toUpperCase());
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    //===
    @ApiOperation(value="14.02 新增總額外點數條件", notes="", position=2)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }")
    })
    @RequestMapping(value = "/additional/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPlanCondition(@RequestHeader("Authorization") String jwt,
            @RequestBody AdditionalConditionPl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            long newId = additionalPointService.addAdditionalCondition(params);
            additionalPointService.fillStartAndEndNull(new java.util.Date());
            additionalPointService.correctEndDate(Utility.detectDate(params.getStart_date()));
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            if (newId>0) {
                retMap.put("status", 0);
            } else {
                retMap.put("status", -1);
            }
            retMap.put("new_id", newId);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    
    @ApiOperation(value="14.03 更新總額外點數條件", notes="", position=3)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }")
    })
    @RequestMapping(value = "/additional/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePlanCondition(@RequestHeader("Authorization") String jwt,
            @PathVariable long id,
            @RequestBody AdditionalConditionPl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = additionalPointService.updateAdditionalCondition(id, params);
            additionalPointService.fillStartAndEndNull(new java.util.Date());
            additionalPointService.correctEndDate(Utility.detectDate(params.getStart_date()));
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    
    @ApiOperation(value="14.04 刪除總額外點數條件", notes="", position=4)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }")
    })
    @RequestMapping(value = "/additional/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePlanCondition(@RequestHeader("Authorization") String jwt,
            @PathVariable long id) throws Exception {
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = additionalPointService.deleteAdditionalCondition(id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            retMap.put("id", id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }

    @ApiOperation(value="14.05 取得總額外點數條件", notes="", position=5)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=AdditionalConditionDto.class)
    })
    @RequestMapping(value = "/additional/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> obtainPlanCondition(@RequestHeader("Authorization") String jwt,
            @PathVariable long id) throws Exception {
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = additionalPointService.findOne(id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="14.06 總額外點數條件狀態設定", notes="", position=6)
    @ApiResponses({
        @ApiResponse(code = 200, message="{status:1.設定成功)/else.設定失敗 }")
    })
    @ApiImplicitParams({
        @ApiImplicitParam(name="Authorization", value="token", example="", dataType="String", paramType="header", required=true),
        @ApiImplicitParam(name="id", value="單號", dataType="String", paramType="path", required=true),
        @ApiImplicitParam(name="state", value="0.未啟動/1.使用中/2.鎖定", dataType="String", paramType="query", required=true)
     })
    @RequestMapping(value = "/additional/setactive/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> additionalSetActive(@RequestHeader("Authorization") String jwt,
            @PathVariable long id,
            @RequestParam(required=true, defaultValue="") int state) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = additionalPointService.updateActive(id, state);
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
