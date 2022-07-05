package tw.com.leadtek.nhiwidget.controller;

import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import tw.com.leadtek.nhiwidget.dto.PtAnesthesiaFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtAnesthesiaFeePl;
import tw.com.leadtek.nhiwidget.dto.PtBoneMarrowTransFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtBoneMarrowTransFeePl;
import tw.com.leadtek.nhiwidget.dto.PtOthersFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtOthersFeePl;
import tw.com.leadtek.nhiwidget.dto.PtPlasterBandageFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtPlasterBandageFeePl;
import tw.com.leadtek.nhiwidget.dto.PtPsychiatricFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtPsychiatricFeePl;
import tw.com.leadtek.nhiwidget.dto.PtSpecificMedicalFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtSpecificMedicalFeePl;
import tw.com.leadtek.nhiwidget.service.PaymentTermsService;
import tw.com.leadtek.nhiwidget.service.PtAnesthesiaFeeService;
import tw.com.leadtek.nhiwidget.service.PtBoneMarrowTransFeeService;
import tw.com.leadtek.nhiwidget.service.PtOthersFeeService;
import tw.com.leadtek.nhiwidget.service.PtPlasterBandageFeeService;
import tw.com.leadtek.nhiwidget.service.PtPsychiatricFeeService;
import tw.com.leadtek.nhiwidget.service.PtSpecificMedicalFeeService;


@Api(value = "健保標準給付額 支付條件設定 API", tags = {"10-4 健保標準給付額 支付條件設定"})
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class E2101dControll {
    
    @Autowired
    private PaymentTermsService paymentTermsService;
    @Autowired
    private PtPsychiatricFeeService ptPsychiatricFeeService;
    @Autowired
    private PtBoneMarrowTransFeeService ptBoneMarrowTransFeeService;
    @Autowired
    private PtPlasterBandageFeeService ptPlasterBandageFeeService;
    @Autowired
    private PtAnesthesiaFeeService ptAnesthesiaFeeService;
    @Autowired
    private PtSpecificMedicalFeeService ptSpecificMedicalFeeService;
    @Autowired
    private PtOthersFeeService ptOthersFeeService;

    //==== 精神醫療治療費 (Psychiatric Fee)
    @ApiOperation(value="10-4.01 精神醫療治療費設定(get)", notes="", position=1)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtPsychiatricFeeDto.class)
    })
    @RequestMapping(value = "/payment/psychiatricfee/{pt_id}", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentRadiationfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptPsychiatricFeeService.findPsychiatricFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    
    @ApiOperation(value="10-4.02 精神醫療治療費設定(add)", notes="category = \"精神醫療治療費\"" , position=2)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/psychiatricfee/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPaymentPsychiatricfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @Valid @RequestBody PtPsychiatricFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            long ptId = ptPsychiatricFeeService.addPsychiatricFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(ptId, PtPsychiatricFeeService.Category, params.getActive(), true);
                retMap.put("message", "新增成功。/id="+ptId);
            } else {
                retMap.put("message", "新增失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    
    @ApiOperation(value="10-4.03 精神醫療治療費設定(update)", notes="<b>category 無法變更</b>", position=3)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/psychiatricfee/{pt_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePaymentPsychiatricfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @Valid @RequestBody PtPsychiatricFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            int status = ptPsychiatricFeeService.updatePsychiatricFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(pt_id, PtPsychiatricFeeService.Category, params.getActive(), true);
                retMap.put("message", "修改成功。/id="+pt_id);
            } else {
                retMap.put("message", "修改失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-4.04 精神醫療治療費設定(delete)", notes="", position=4)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/psychiatricfee/{pt_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePaymentPsychiatricfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            paymentTermsService.updateActiveByThread(pt_id, PtPsychiatricFeeService.Category, 0, false);
            int status = ptPsychiatricFeeService.deletePsychiatricFee(pt_id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "刪除成功。/id="+pt_id);
            } else {
                retMap.put("message", "刪除失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    //==== 輸血及骨髓移植費 Bone Marrow Trans Fee, P56
    @ApiOperation(value="10-4.05 輸血及骨髓移植費設定(get)", notes="", position=5)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtBoneMarrowTransFeeDto.class)
    })
    @RequestMapping(value = "/payment/bonemarrowtransfee/{pt_id}", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentBoneMarrowTransFee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptBoneMarrowTransFeeService.findBoneMarrowTransFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-4.06 輸血及骨髓移植費設定(add)", notes="category = \"輸血及骨髓移植費\"", position=6)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/bonemarrowtransfee/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPaymentBoneMarrowTransFee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @Valid @RequestBody PtBoneMarrowTransFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            long ptId = ptBoneMarrowTransFeeService.addBoneMarrowTransFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(ptId, PtBoneMarrowTransFeeService.Category, params.getActive(), true);
                retMap.put("message", "新增成功。/id="+ptId);
            } else {
                retMap.put("message", "新增失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-4.07 輸血及骨髓移植費設定(update)", notes="<b>category 無法變更</b>", position=7)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/bonemarrowtransfee/{pt_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePaymentBoneMarrowTransFee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @Valid @RequestBody PtBoneMarrowTransFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            int status = ptBoneMarrowTransFeeService.updateBoneMarrowTransFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(pt_id, PtBoneMarrowTransFeeService.Category, params.getActive(), true);
                retMap.put("message", "修改成功。/id="+pt_id);
            } else {
                retMap.put("message", "修改失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-4.08 輸血及骨髓移植費設定(delete)", notes="", position=8)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/bonemarrowtransfee/{pt_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePaymentBoneMarrowTransFee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            paymentTermsService.updateActiveByThread(pt_id, PtBoneMarrowTransFeeService.Category, 0, false);
            int status = ptBoneMarrowTransFeeService.deleteBoneMarrowTransFee(pt_id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "刪除成功。/id="+pt_id);
            } else {
                retMap.put("message", "刪除失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }

    
  //==== 石膏繃帶費 (Plaster bandage fee, P57)
    @ApiOperation(value="10-4.09 石膏繃帶費設定(get)", notes="", position=9)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtPlasterBandageFeeDto.class)
    })
    @RequestMapping(value = "/payment/plasterbandagefee/{pt_id}", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentPlasterBandageFee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptPlasterBandageFeeService.findPlasterBandageFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-4.10 石膏繃帶費設定(add)", notes="category = \"石膏繃帶費\"", position=10)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/plasterbandagefee/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPaymentPlasterBandageFee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @Valid @RequestBody PtPlasterBandageFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            long ptId = ptPlasterBandageFeeService.addPlasterBandageFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(ptId, PtPlasterBandageFeeService.Category, params.getActive(), true);
                retMap.put("message", "新增成功。/id="+ptId);
            } else {
                retMap.put("message", "新增失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-4.11 石膏繃帶費設定(update)", notes="<b>category 無法變更</b>", position=11)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/plasterbandagefee/{pt_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePaymentPlasterBandageFee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @Valid @RequestBody PtPlasterBandageFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            int status = ptPlasterBandageFeeService.updatePlasterBandageFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(pt_id, PtPlasterBandageFeeService.Category, params.getActive(), true);
                retMap.put("message", "修改成功。/id="+pt_id);
            } else {
                retMap.put("message", "修改失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-4.12 石膏繃帶費設定(delete)", notes="", position=12)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/plasterbandagefee/{pt_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePaymentPlasterBandageFee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            paymentTermsService.updateActiveByThread(pt_id, PtPlasterBandageFeeService.Category, 0, false);
            int status = ptPlasterBandageFeeService.deletePlasterBandageFee(pt_id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "刪除成功。/id="+pt_id);
            } else {
                retMap.put("message", "刪除失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    
    //==== 麻醉費設定 Anesthesia fee, P58
    @ApiOperation(value="10-4.13 麻醉費設定(get)", notes="", position=13)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtAnesthesiaFeeDto.class)
    })
    @RequestMapping(value = "/payment/anesthesiafee/{pt_id}", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentAnesthesiafee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptAnesthesiaFeeService.findAnesthesiaFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-4.14 麻醉費設定(add)", notes="category = \"麻醉費\"", position=14)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/anesthesiafee/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPaymentAnesthesiafee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @Valid @RequestBody PtAnesthesiaFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            long ptId = ptAnesthesiaFeeService.addAnesthesiaFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(ptId, PtAnesthesiaFeeService.Category, params.getActive(), true);
                retMap.put("message", "新增成功。/id="+ptId);
            } else {
                retMap.put("message", "新增失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-4.15 麻醉費設定(update)", notes="<b>category 無法變更</b>", position=15)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/anesthesiafee/{pt_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePaymentAnesthesiafee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @Valid @RequestBody PtAnesthesiaFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            int status = ptAnesthesiaFeeService.updateAnesthesiaFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(pt_id, PtAnesthesiaFeeService.Category, params.getActive(), true);
                retMap.put("message", "修改成功。/id="+pt_id);
            } else {
                retMap.put("message", "修改失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-4.16 麻醉費設定(delete)", notes="", position=16)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/anesthesiafee/{pt_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePaymentAnesthesiafee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            paymentTermsService.updateActiveByThread(pt_id, PtAnesthesiaFeeService.Category, 0, false);
            int status = ptAnesthesiaFeeService.deleteAnesthesiaFee(pt_id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "刪除成功。/id="+pt_id);
            } else {
                retMap.put("message", "刪除失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    
    //==== 特定診療檢查費 Specific Medical fee, P60
    @ApiOperation(value="10-4.17 特定診療檢查費設定(get)", notes="", position=17)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtSpecificMedicalFeeDto.class)
    })
    @RequestMapping(value = "/payment/specificmedicalfee/{pt_id}", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentSpecificMedicalfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptSpecificMedicalFeeService.findSpecificMedicalFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-4.18 特定診療檢查費設定(add)", notes="category = \"特定診療檢查費\"", position=18)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/specificmedicalfee/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPaymentSpecificMedicalfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @Valid @RequestBody PtSpecificMedicalFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            long ptId = ptSpecificMedicalFeeService.addSpecificMedicalFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(ptId, PtSpecificMedicalFeeService.Category, params.getActive(), true);
                retMap.put("message", "新增成功。/id="+ptId);
            } else {
                retMap.put("message", "新增失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-4.19 特定診療檢查費設定(update)", notes="<b>category 無法變更</b>", position=19)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/specificmedicalfee/{pt_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePaymentSpecificMedicalfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @Valid @RequestBody PtSpecificMedicalFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            int status = ptSpecificMedicalFeeService.updateSpecificMedicalFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(pt_id, PtSpecificMedicalFeeService.Category, params.getActive(), true);
                retMap.put("message", "修改成功。/id="+pt_id);
            } else {
                retMap.put("message", "修改失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-4.20 特定診療檢查費設定(delete)", notes="", position=20)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/specificmedicalfee/{pt_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePaymentSpecificMedicalfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            paymentTermsService.updateActiveByThread(pt_id, PtSpecificMedicalFeeService.Category, 0, false);
            int status = ptSpecificMedicalFeeService.deleteSpecificMedicalFee(pt_id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "刪除成功。/id="+pt_id);
            } else {
                retMap.put("message", "刪除失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    
    //==== 不分類費用設定 Others fee
    @ApiOperation(value="10-4.21 不分類設定(get)", notes="", position=21)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtOthersFeeDto.class)
    })
    @RequestMapping(value = "/payment/othersfee/{pt_id}", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentOthersfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptOthersFeeService.findOthersFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-4.22 不分類設定(add)", notes="category = \"不分類\"", position=22)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/othersfee/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPaymentOthersfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @Valid @RequestBody PtOthersFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            long ptId = ptOthersFeeService.addOthersFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(ptId, PtOthersFeeService.Category, params.getActive(), true);
                retMap.put("message", "新增成功。/id="+ptId);
            } else {
                retMap.put("message", "新增失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-4.23 不分類設定(update)", notes="<b>category 無法變更</b>", position=23)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/othersfee/{pt_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePaymentWardfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @Valid @RequestBody PtOthersFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            if (params.getEnd_date()<=0) {
                params.setEnd_date(4102358400000l); // 2099-12-31
            }
            int status = ptOthersFeeService.updateOthersFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
//                paymentTermsService.correctEndDate(params.getCategory());
                paymentTermsService.correctEndDateByNhiNo(params.getNhi_no(), "");
                paymentTermsService.updateActiveByThread(pt_id, PtOthersFeeService.Category, params.getActive(), true);
                retMap.put("message", "修改成功。/id="+pt_id);
            } else {
                retMap.put("message", "修改失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-4.24 不分類設定(delete)", notes="", position=24)
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/othersfee/{pt_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePaymentOthersfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            paymentTermsService.updateActiveByThread(pt_id, PtOthersFeeService.Category, 0, false);
            int status = ptOthersFeeService.deleteOthersFee(pt_id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "刪除成功。/id="+pt_id);
            } else {
                retMap.put("message", "刪除失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }

}
