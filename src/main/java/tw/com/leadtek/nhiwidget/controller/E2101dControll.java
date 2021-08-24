package tw.com.leadtek.nhiwidget.controller;

import javax.servlet.http.HttpServletRequest;

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
import tw.com.leadtek.nhiwidget.dto.PtPlasterBandageFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtPlasterBandageFeePl;
import tw.com.leadtek.nhiwidget.dto.PtPsychiatricFeeDto;
import tw.com.leadtek.nhiwidget.dto.PtPsychiatricFeePl;
import tw.com.leadtek.nhiwidget.service.PaymentTermsService;
import tw.com.leadtek.nhiwidget.service.PtAnesthesiaFeeService;
import tw.com.leadtek.nhiwidget.service.PtBoneMarrowTransFeeService;
import tw.com.leadtek.nhiwidget.service.PtPlasterBandageFeeService;
import tw.com.leadtek.nhiwidget.service.PtPsychiatricFeeService;


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

    //==== 精神醫療治療費 (Psychiatric Fee)
    @ApiOperation(value="10-4.01 精神醫療治療費設定(get)", notes="")
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtPsychiatricFeeDto.class)
    })
    @RequestMapping(value = "/payment/psychiatricfee/{pt_id}", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentRadiationfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptPsychiatricFeeService.findPsychiatricFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    
    @ApiOperation(value="10-4.02 精神醫療治療費設定(add)", notes="category = \"放射線診療費\"")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/psychiatricfee/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPaymentPsychiatricfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @RequestBody PtPsychiatricFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            long ptId = ptPsychiatricFeeService.addPsychiatricFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
                retMap.put("message", "新增成功。/id="+ptId);
            } else {
                retMap.put("message", "新增失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    
    @ApiOperation(value="10-4.03 精神醫療治療費設定(update)", notes="<b>category 無法變更</b>")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/psychiatricfee/{pt_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePaymentPsychiatricfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @RequestBody PtPsychiatricFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptPsychiatricFeeService.updatePsychiatricFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "修改成功。/id="+pt_id);
            } else {
                retMap.put("message", "修改失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-4.04 精神醫療治療費設定(delete)", notes="")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/psychiatricfee/{pt_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePaymentPsychiatricfee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
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
    
    //==== 輸血及骨髓移植費 Bone Marrow Trans Fee
    @ApiOperation(value="10-4.05 輸血及骨髓移植費設定(get)", notes="")
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtBoneMarrowTransFeeDto.class)
    })
    @RequestMapping(value = "/payment/bonemarrowtransfee/{pt_id}", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentBoneMarrowTransFee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptBoneMarrowTransFeeService.findBoneMarrowTransFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-4.06 輸血及骨髓移植費設定(add)", notes="category = \"輸血及骨髓移植費\"")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/bonemarrowtransfee/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPaymentBoneMarrowTransFee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @RequestBody PtBoneMarrowTransFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            long ptId = ptBoneMarrowTransFeeService.addBoneMarrowTransFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
                retMap.put("message", "新增成功。/id="+ptId);
            } else {
                retMap.put("message", "新增失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-4.07 輸血及骨髓移植費設定(update)", notes="<b>category 無法變更</b>")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/bonemarrowtransfee/{pt_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePaymentBoneMarrowTransFee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @RequestBody PtBoneMarrowTransFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptBoneMarrowTransFeeService.updateBoneMarrowTransFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "修改成功。/id="+pt_id);
            } else {
                retMap.put("message", "修改失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-4.08 輸血及骨髓移植費設定(delete)", notes="")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/bonemarrowtransfee/{pt_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePaymentBoneMarrowTransFee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
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

    
  //==== 石膏繃帶費 (Plaster bandage fee)
    @ApiOperation(value="10-4.09 石膏繃帶費設定(get)", notes="")
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtPlasterBandageFeeDto.class)
    })
    @RequestMapping(value = "/payment/plasterbandagefee/{pt_id}", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentPlasterBandageFee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = ptPlasterBandageFeeService.findPlasterBandageFee(pt_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-4.10 石膏繃帶費設定(add)", notes="category = \"病房費\"")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/plasterbandagefee/add", method = RequestMethod.POST)
    public ResponseEntity<?> addPaymentPlasterBandageFee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @RequestBody PtPlasterBandageFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            long ptId = ptPlasterBandageFeeService.addPlasterBandageFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
                retMap.put("message", "新增成功。/id="+ptId);
            } else {
                retMap.put("message", "新增失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-4.11 石膏繃帶費設定(update)", notes="<b>category 無法變更</b>")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/plasterbandagefee/{pt_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePaymentPlasterBandageFee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id,
        @RequestBody PtPlasterBandageFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptPlasterBandageFeeService.updatePlasterBandageFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
                retMap.put("message", "修改成功。/id="+pt_id);
            } else {
                retMap.put("message", "修改失敗!");
            }
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="10-4.12 石膏繃帶費設定(delete)", notes="")
    @ApiResponses(value={
        @ApiResponse(code = 200, message="{ status:0 }")
    })
    @RequestMapping(value = "/payment/plasterbandagefee/{pt_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePaymentPlasterBandageFee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
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
    
    
    //==== 麻醉費設定 Anesthesia fee
    @ApiOperation(value="10-4.13 麻醉費設定(get)", notes="", position=13)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=PtAnesthesiaFeeDto.class)
    })
    @RequestMapping(value = "/payment/anesthesiafee/{pt_id}", method = RequestMethod.POST)
    public ResponseEntity<?> getPaymentAnesthesiafee(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt,
        @PathVariable long pt_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
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
        @RequestBody PtAnesthesiaFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = 0;
            long ptId = ptAnesthesiaFeeService.addAnesthesiaFee(params);
            if (ptId<=0) {
                status = -1;
            }
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status==0) {
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
        @RequestBody PtAnesthesiaFeePl params) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = ptAnesthesiaFeeService.updateAnesthesiaFee(pt_id, params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            if (status>0) {
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
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
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

}
