package tw.com.leadtek.nhiwidget.controller;

import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import tw.com.leadtek.nhiwidget.service.LogDataService;
import tw.com.leadtek.nhiwidget.service.pt.PaymentTermsService;


@Api(value = "DRG-API", tags = {"11 DRG API"})
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DrgRestController {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private LogDataService logDataService;
    @Autowired
    private PaymentTermsService paymentTermsService;


    @ApiOperation(value="11.1 DRG Initiate", notes="")
    @ApiResponses({
        @ApiResponse(code = 200, message="{...}")
    })
    @ApiImplicitParams({
       @ApiImplicitParam(name = "drg_path", value = "DRG程式路徑", example="c:\\med\\S_DRGService_3412" , dataType = "String", paramType = "query", required = true),
       @ApiImplicitParam(name = "drg_exe", value = "DRG程式", example="DRGICD10.exe" , dataType = "String", paramType = "query", required = true),
    })
    @RequestMapping(value = "/api/drg/initiate", method = RequestMethod.POST)
    public ResponseEntity<?> initiateDrg(@RequestHeader("Authorization") String jwt,
            @RequestParam(required=false, defaultValue="") String drg_path,
            @RequestParam(required=false, defaultValue="") String drg_exe) throws Exception {
//        java.util.Map<String, Object> retMap = ipdService.drgProcess(id_card, in_date);
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            String drgPath = drg_path;
            if (drgPath==null) {
                drgPath = "c:\\med\\S_DRGService_3412";
            }
            String drgExe = drg_exe;
            if (drgExe==null) {
                drgExe = "DRGICD10.exe";
            }
            System.out.println("drgPath123 = "+drgPath+";"+drgExe);
            int status = logDataService.createDrgBatchFile(drgPath, drgExe);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    //===
    @ApiOperation(value="11.2 DRG 計算", notes="")
    @ApiResponses({
        @ApiResponse(code = 200, message="{...}")
    })
    @ApiImplicitParams({
       @ApiImplicitParam(name="id_card", value="身分證號", example="C120***370", dataType="String", paramType="path", required=true),
       @ApiImplicitParam(name="in_date", value="住院日(民國年)", example="1100112", dataType="String", paramType="path", required=true)
    })
    @RequestMapping(value = "/api/drg/{id_card}/{in_date}", method = RequestMethod.POST)
    public ResponseEntity<?> calculateDrg(HttpServletRequest request,
        @RequestHeader("Authorization") String jwt, @PathVariable String id_card,
        @PathVariable String in_date) throws Exception {
      // java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
      // if ((int)jwtValidation.get("status") != 200) {
      // return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
      // } else {
      java.util.Map<String, Object> retMap = logDataService.drgProcess(id_card, in_date);
      return new ResponseEntity<>(retMap, HttpStatus.OK);
      // }
    }
    
    //===
//    @ApiOperation(value="11.3 Data Log Test", notes="")
//    @ApiResponses({
//        @ApiResponse(code = 200, message="{...}")
//    })
//    @ApiImplicitParams({
//       @ApiImplicitParam(name="table_name", value="Table Name", example="IP_D", dataType="String", paramType="path", required=true)
//    })
//    @RequestMapping(value = "/api/data_log/{table_name}", method = RequestMethod.POST)
//    public java.util.Set<Map<String, Object>> data_log(HttpServletRequest request,
//        @PathVariable String table_name,
//        @ApiParam(value="LogDataPl", required=true)
//        @RequestBody java.util.List<LogDataPl> params) throws Exception {
//        java.util.Set<Map<String, Object>> retSet = logDataService.testModifyData("Samuel", table_name, params);
//        return retSet; 
//    }
    
}

/*
{
  "mr_id": 31005,
  "card_seq_no": "0002",
  "fee_ym": "202601",
  "roc_id": "C120***370",
  "in_date": "20260112",
  "out_date": "20260115",
  "data": [
    {
      "icd_cm_1": "B99.9",
      "icd_cm_2": "E11.00",
      "icd_cm_3": "",
      "icd_cm_4": "",
      "icd_cm_5": "",
      "appl_dot": "+91268719",
      "drg_code": "42303",
      "error_code": "0000000000000000000000000000000000000000",
      "error_message": []
    },
    {
      "icd_cm_1": "E11.00",
      "icd_cm_2": "B99.9",
      "icd_cm_3": "",
      "icd_cm_4": "",
      "icd_cm_5": "",
      "appl_dot": "+91268719",
      "drg_code": "29402",
      "error_code": "0000000000000000000000000000000000000000",
      "error_message": []
    }
  ]
} 
 */



