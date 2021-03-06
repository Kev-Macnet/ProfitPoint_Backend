package tw.com.leadtek.nhiwidget.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
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
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.media.Content;

import tw.com.leadtek.nhiwidget.dto.PaymentTermsSearchDto;
import tw.com.leadtek.nhiwidget.dto.PaymentTermsSearchPl;
import tw.com.leadtek.nhiwidget.dto.PtInpatientFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtInpatientFeePl;
import tw.com.leadtek.nhiwidget.dto.PtOutpatientFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtOutpatientFeePl;
import tw.com.leadtek.nhiwidget.dto.PtPsychiatricWardFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtPsychiatricWardFeePl;
import tw.com.leadtek.nhiwidget.dto.PtSurgeryFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtSurgeryFeePl;
import tw.com.leadtek.nhiwidget.dto.PtWardFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtWardFeePl;
import tw.com.leadtek.nhiwidget.service.PaymentTermsService;
import tw.com.leadtek.nhiwidget.service.PtInpatientFeeService;
import tw.com.leadtek.nhiwidget.service.PtOutpatientFeeService;
import tw.com.leadtek.nhiwidget.service.PtPsychiatricWardFeeService;
import tw.com.leadtek.nhiwidget.service.PtSurgeryFeeService;
import tw.com.leadtek.nhiwidget.service.PtWardFeeService;
import tw.com.leadtek.tools.Utility;


@Api(value = "????????????????????? ?????????????????? API", tags = {"10-1 ????????????????????? ??????????????????"})
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class E2101aControll {
    @Value("${springfox.documentation.swagger.use-model-v3:true}")
    boolean useModelV3;
    
    @Autowired
    private PaymentTermsService paymentTermsService;
    @Autowired
    private PtOutpatientFeeService ptOutpatientFeeService;
    @Autowired
    private PtWardFeeService ptWardFeeService;
    @Autowired
    private PtInpatientFeeService ptInpatientFeeService;
    @Autowired
    private PtPsychiatricWardFeeService ptPsychiatricWardFeeService;
    @Autowired
    private PtSurgeryFeeService ptSurgeryFeeService;

    // PPT Page38
    @ApiOperation(value="10-1.01 ????????????????????????(??????)", notes="", position=1)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="", response=PaymentTermsSearchDto.class)
    })
    @RequestMapping(value = "/payment/terms/search", method = RequestMethod.POST)
    public ResponseEntity<?> paymentTermsSearh(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @Valid @RequestBody PaymentTermsSearchPl params) throws Exception {
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
//            paymentTermsService.correctEndDate("");
            java.util.Date da1 = Utility.detectDate(params.getStart_date());
            java.util.Date da2 = Utility.detectDate(params.getEnd_date());
            String feeNo = (params.getFeeNo()==null) ? "" : params.getFeeNo();
            String nhiNo = (params.getNhiNo()==null) ? "" : params.getNhiNo();
            String category = (params.getCategory()==null) ? "" : params.getCategory();
            String sortField = params.getSort_field();
            String sortDirection = params.getSort_direction(); // ASC|DESC
            if ((sortField==null)||(sortField.length()==0)) {
                sortField = "ID";
            }
            if ((sortDirection==null)||(sortDirection.length()==0)) {
                sortDirection = "ASC";
            }
            if (java.util.Arrays.asList(new String[] {"ID","FEE_NO","NHI_NO","CATEGORY","START_DATE","END_DATE"}).indexOf(sortField.toUpperCase())<0) {
                sortField = "ID";
            }
            if (java.util.Arrays.asList(new String[] {"ASC","DESC"}).indexOf(sortDirection.toUpperCase())<0) {
                sortField = "ASC";
            }
            java.util.Map<String, Object> retMap = paymentTermsService.searchPaymentTerms(feeNo, nhiNo, category, da1, da2, 
                    params.getPageSize(), params.getPageIndex(), sortField.toUpperCase(), sortDirection.toUpperCase());
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-1.02 ????????????????????????", notes="", position=2)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="[{...}, {...} ...]") 
    })
    @RequestMapping(value = "/payment/terms/category", method = RequestMethod.POST)
    public ResponseEntity<?> paymentTermsCategory(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt) throws Exception {
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            String[][] arrCategory = {
                {"???????????????","/payment/outpatientfee/"},
                {"???????????????","/payment/inpatientfee/"},
                {"?????????","/payment/wardfee/"},
                {"?????????????????????","/payment/psychiatricwardfee/"},
                {"?????????","/payment/surgeryfee/"},
                
                {"???????????????","/payment/treatmentfee/"},
//                {"???????????????","/payment/tubefeedingfee/"},
                {"?????????????????????????????????","/payment/nutritionalfee/"}, //???????????????
                {"?????????","/payment/adjustmentfee/"},
                {"??????","/payment/medicinefee/"},
                
                {"??????????????????","/payment/radiationfee/"},
                {"??????","/payment/injectionfee/"},
                {"??????????????????","/payment/qualityservice/"},
                {"??????????????????","/payment/inpatientcare/"},
                {"???????????????","/payment/rehabilitationfee/"},
                
                {"?????????????????????","/payment/psychiatricfee/"},
                {"????????????????????????","/payment/bonemarrowtransfee/"},
                {"???????????????","/payment/plasterbandagefee/"},
                {"?????????","/payment/anesthesiafee/"},
                {"?????????????????????","/payment/specificmedicalfee/"},
                
                {"?????????","/payment/othersfee/"}
            };

            java.util.List<Map<String, Object>> retList = new java.util.ArrayList<Map<String, Object>>();
            for (int a=0; a<arrCategory.length; a++) {
                java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
                map.put("category", arrCategory[a][0]);
                map.put("url_prefix", arrCategory[a][1]);
                retList.add(map);
            }
            return new ResponseEntity<>(retList, HttpStatus.OK);
        }
    }
    
    
    @ApiOperation(value="10-1.03 ?????????????????????????????????", notes="", position=3)
    @ApiResponses(value={
            @ApiResponse(code = 200, message="{status:1.????????????)/else.???????????? }")
    })
    @ApiImplicitParams({
        @ApiImplicitParam(name="Authorization", value="token", example="", dataType="String", paramType="header", required=true),
        @ApiImplicitParam(name="id", value="??????", dataType="String", paramType="path", required=true),
        @ApiImplicitParam(name="category", value="????????????", dataType="String", paramType="query", required=true),
        @ApiImplicitParam(name="state", value="0.??????/1.??????", dataType="String", paramType="query", required=true)
     })
    @RequestMapping(value = "/payment/terms/setactive/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> paymentTermsSetActive(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long id,
        @RequestParam(required=true, defaultValue="") String category,
        @RequestParam(required=true, defaultValue="") int state) throws Exception {
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = paymentTermsService.updateActive(id, category, state);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>=1) {
                retMap.put("message", "???????????????");
            } else {
                retMap.put("message", "??????????????????");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }

    
    @ApiOperation(value="10-1.04 ?????????????????????(get)", notes="", position=4)
    @ApiResponses({
        @ApiResponse(code = 200, message="", response=PtOutpatientFeeDto.class)
    })
    @RequestMapping(value = "/payment/outpatientfee/{pt_id}", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentOutpatientfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptOutpatientFeeService.findOutpatientFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }

    
    @ApiOperation(value="10-1.05 ?????????????????????(add)", notes="category = \"???????????????\"" , position=5)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/outpatientfee/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPaymentOutpatientfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @RequestBody PtOutpatientFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            long ptId = ptOutpatientFeeService.addOutpatientFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                retMap.put("message", "???????????????/id="+ptId);
            } else {
                retMap.put("message", "????????????!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-1.06 ?????????????????????(update)", notes="<b>category ????????????</b>", position=6)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/outpatientfee/{pt_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePaymentOutpatientfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @Valid @RequestBody PtOutpatientFeePl params) throws Exception {
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            int status = ptOutpatientFeeService.updateOutpatientFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                retMap.put("message", "???????????????/id="+pt_id);
            } else {
                retMap.put("message", "????????????!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-1.07 ?????????????????????(delete)", notes="", position=7)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/outpatientfee/{pt_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePaymentOutpatientfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptOutpatientFeeService.deleteOutpatientFee(pt_id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "???????????????/id="+pt_id);
            } else {
                retMap.put("message", "????????????!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    //==== ??????????????? inpatient fee
    @ApiOperation(value="10-1.08 ?????????????????????(get)", notes="", position=8)
    @ApiResponses({
        @ApiResponse(code = 200, message="", response=PtInpatientFeeDto.class)
    })
    @RequestMapping(value = "/payment/inpatientfee/{pt_id}", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentInpatientfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptInpatientFeeService.findInpatientFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-1.09 ?????????????????????(add)", notes="category = \"???????????????\"", position=9)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/inpatientfee/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPaymentInpatientfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @Valid @RequestBody PtInpatientFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            long ptId = ptInpatientFeeService.addInpatientFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                retMap.put("message", "???????????????/id="+ptId);
            } else {
                retMap.put("message", "????????????!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    
    @ApiOperation(value="10-1.10 ?????????????????????(update)", notes="<b>category ????????????</b>", position=10)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/inpatientfee/{pt_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePaymentInpatientfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @Valid @RequestBody PtInpatientFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            int status = ptInpatientFeeService.updateInpatientFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "???????????????/id="+pt_id);
            } else {
                retMap.put("message", "????????????!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    
    @ApiOperation(value="10-1.11 ?????????????????????(delete)", notes="", position=11)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/inpatientfee/{pt_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePaymentInpatientfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptInpatientFeeService.deleteInpatientFee(pt_id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "???????????????/id="+pt_id);
            } else {
                retMap.put("message", "????????????!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    //==== ??????????????? Ward fee
    @ApiOperation(value="10-1.12 ???????????????(get)", notes="", position=12)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtWardFeeDto.class)
    })
    @RequestMapping(value = "/payment/wardfee/{pt_id}", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentWardfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptWardFeeService.findWardFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-1.13 ???????????????(add)", notes="category = \"?????????\"", position=13)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/wardfee/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPaymentWardfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @Valid @RequestBody PtWardFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            long ptId = ptWardFeeService.addWardFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                retMap.put("message", "???????????????/id="+ptId);
            } else {
                retMap.put("message", "????????????!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-1.14 ???????????????(update)", notes="<b>category ????????????</b>", position=14)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/wardfee/{pt_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePaymentWardfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @Valid @RequestBody PtWardFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            int status = ptWardFeeService.updateWardFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                retMap.put("message", "???????????????/id="+pt_id);
            } else {
                retMap.put("message", "????????????!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-1.15 ???????????????(delete)", notes="", position=15)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/wardfee/{pt_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePaymentWardfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptWardFeeService.deleteWardFee(pt_id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "???????????????/id="+pt_id);
            } else {
                retMap.put("message", "????????????!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    //==== ????????????????????? Psychiatric Ward Fee
    @ApiOperation(value="10-1.16 ?????????????????????(get)", notes="", position=16)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtPsychiatricWardFeeDto.class)
    })
    @RequestMapping(value = "/payment/psychiatricwardfee/{pt_id}", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentPsychiatricWardfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptPsychiatricWardFeeService.findPsychiatricWardFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    
    @ApiOperation(value="10-1.17 ?????????????????????(add)", notes="category = \"?????????????????????\"", position=17)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/psychiatricwardfee/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPaymentPsychiatricWardfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @Valid @RequestBody PtPsychiatricWardFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            long ptId = ptPsychiatricWardFeeService.addPsychiatricWardFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                retMap.put("message", "???????????????/id="+ptId);
            } else {
                retMap.put("message", "????????????!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-1.18 ?????????????????????(update)", notes="<b>category ????????????</b>", position=18)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/psychiatricwardfee/{pt_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePaymentPsychiatricWardfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @Valid @RequestBody PtPsychiatricWardFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            int status = ptPsychiatricWardFeeService.updatePsychiatricWardFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                retMap.put("message", "???????????????/id="+pt_id);
            } else {
                retMap.put("message", "????????????!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    
    @ApiOperation(value="10-1.19 ?????????????????????(delete)", notes="", position=19)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/psychiatricwardfee/{pt_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePaymentPsychiatricWardfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptPsychiatricWardFeeService.deletePsychiatricWardFee(pt_id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "???????????????/id="+pt_id);
            } else {
                retMap.put("message", "????????????!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    //==== ??????????????? surgery fee
    @ApiOperation(value="10-1.20 ???????????????(get)", notes="", position=20)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtSurgeryFeeDto.class)
    })
    @RequestMapping(value = "/payment/surgeryfee/{pt_id}", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentSurgeryfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptSurgeryFeeService.findSurgeryFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-1.21 ???????????????(add)", notes="category = \"?????????\"", position=21)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/surgeryfee/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPaymentSurgeryfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @Valid @RequestBody PtSurgeryFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            long ptId = ptSurgeryFeeService.addSurgeryFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                retMap.put("message", "???????????????/id="+ptId);
            } else {
                retMap.put("message", "????????????!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-1.22 ???????????????(update)", notes="<b>category ????????????</b>", position=22)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/surgeryfee/{pt_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePaymentSurgeryfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @Valid @RequestBody PtSurgeryFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            int status = ptSurgeryFeeService.updateSurgeryFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                retMap.put("message", "???????????????/id="+pt_id);
                retMap.put("notify", "????????? category ????????????");
            } else {
                retMap.put("message", "????????????!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-1.23 ???????????????(delete)", notes="", position=23)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/surgeryfee/{pt_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePaymentSurgeryfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptSurgeryFeeService.deleteSurgeryFee(pt_id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "???????????????/id="+pt_id);
            } else {
                retMap.put("message", "????????????!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    //==== ??????????????? surgery fee

}
