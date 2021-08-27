package tw.com.leadtek.nhiwidget.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import tw.com.leadtek.nhiwidget.service.DbBackupService; 
import tw.com.leadtek.nhiwidget.service.PaymentTermsService;
import tw.com.leadtek.tools.Utility;

@Api(value = "系統備份與還原 API", tags = {"12 系統備份與還原"})
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DbBackupControll {
    
    @Autowired
    private PaymentTermsService paymentTermsService;
    @Autowired
    private DbBackupService dbBackupService;

    //==== 
    @ApiOperation(value="12.01 系統備份", notes="")
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }") //, response=PtTreatmentFeeDto.class)
    })
    @ApiImplicitParams({
        @ApiImplicitParam(name="mode", example="0", value="0.完整備份/1.系統備份/2.資料備份", dataType="Integer", paramType="path", required=true)
    })
    @RequestMapping(value = "/dbbackup/all/{mode}", method = RequestMethod.POST)
    public ResponseEntity<?> dbBackupAll(@RequestHeader("Authorization") String jwt,
        @PathVariable int mode) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> mapBackup = dbBackupService.dbBackup(mode);
            java.util.List<String> lstFileName = (java.util.List)mapBackup.get("fileNames");
            String zipFileName = (String)mapBackup.get("zipName");
            if (lstFileName.size()>0) {
                dbBackupService.zipFiles(zipFileName, lstFileName);
                for (String fname : lstFileName) {
                    Utility.deleteFile(fname);
                }
            }
            Utility.deleteFile((String)mapBackup.get("backupPath"));
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("count", lstFileName.size());
            retMap.put("fileNames", lstFileName);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    //===
    @ApiOperation(value="12.99 Test", notes="")
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }") //, response=PtTreatmentFeeDto.class)
    })
    @RequestMapping(value = "/dbbackup/test", method = RequestMethod.POST)
    public ResponseEntity<?> dbBackupTest(@RequestHeader("Authorization") String jwt) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.List<String> lst = new java.util.ArrayList<String>();
            java.util.List<String> lst2 = new java.util.ArrayList<String>();
            lst.add("12-AAA\n");
            lst.add("34-BBB\n");
            lst2.add("56-婉轉\n");
            lst2.add("78-天真\n");
            String fileName = "d:/temp/123.txt";
            Utility.saveToFile(fileName, lst);
            Utility.saveToFile(fileName, lst2);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("count", 1234);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }


}
