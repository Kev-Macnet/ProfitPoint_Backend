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
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import tw.com.leadtek.nhiwidget.dto.BackupSettingDto;
import tw.com.leadtek.nhiwidget.service.DbBackupService; 
import tw.com.leadtek.nhiwidget.service.PaymentTermsService;
import tw.com.leadtek.nhiwidget.sql.WebConfigDao;


@Api(value = "資料備份與還原 API", tags = {"12 資料備份與還原"})
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DbBackupControll {
    
    @Autowired
    private PaymentTermsService paymentTermsService;
    @Autowired
    private DbBackupService dbBackupService;
    @Autowired
    private WebConfigDao webConfigDao;

    //==== 
    @ApiOperation(value="12.01 資料備份紀錄", notes="", position=1)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }") //, response=PtTreatmentFeeDto.class)
    })
    @RequestMapping(value = "/dbbackup/log", method = RequestMethod.POST)
    public ResponseEntity<?> dbBackupLog(@RequestHeader("Authorization") String jwt) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.List<Map<String, Object>> retMap = dbBackupService.findAll(0, "");
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="12.02 資料備份", notes="", position=2)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }") //, response=PtTreatmentFeeDto.class)
    })
    @ApiImplicitParams({
        @ApiImplicitParam(name="mode", example="0", value="0.完整備份/1.系統參數備份/2.資料備份", dataType="Integer", paramType="path", required=true)
    })
    @RequestMapping(value = "/dbbackup/all/{mode}", method = RequestMethod.POST)
    public ResponseEntity<?> dbBackupAll(@RequestHeader("Authorization") String jwt,
        @PathVariable int mode) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> mapBackup = dbBackupService.dbBackup(mode, jwtValidation.get("userName").toString());
            return new ResponseEntity<>(mapBackup, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="12.03 取得資料備份進度", notes="", position=3)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=BackupSettingDto.class)
    })
    @RequestMapping(value = "/dbbackup/progress", method = RequestMethod.POST)
    public ResponseEntity<?> dbBackupProgress(@RequestHeader("Authorization") String jwt) throws Exception {

        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = dbBackupService.loadBackupProgress();
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="12.04 放棄資料備份", notes="", position=4)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }") //, response=PtTreatmentFeeDto.class)
    })
    @RequestMapping(value = "/dbbackup/abort", method = RequestMethod.POST)
    public ResponseEntity<?> dbBackupAbort(@RequestHeader("Authorization") String jwt) throws Exception {

        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = dbBackupService.setBackupAbort();
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    
    @ApiOperation(value="12.05 刪除資料備份紀錄", notes="", position=5)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }") //, response=PtTreatmentFeeDto.class)
    })
    @RequestMapping(value = "/dbbackup/log/{backup_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> dbBackupDelete(@RequestHeader("Authorization") String jwt,
        @PathVariable int backup_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = dbBackupService.deleteRow(backup_id);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="12.06 寫入資料備份設定", notes="", position=6)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }") //, response=PtTreatmentFeeDto.class)
    })
    @RequestMapping(value = "/dbbackup/setting", method = RequestMethod.PUT)
    public ResponseEntity<?> dbBackupSettingSave(@RequestHeader("Authorization") String jwt,
            @RequestBody BackupSettingDto params) throws Exception {

        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = dbBackupService.saveSetting(params);
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    //===
    @ApiOperation(value="12.07 讀取資料備份設定", notes="", position=7)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }", response=BackupSettingDto.class)
    })
    @RequestMapping(value = "/dbbackup/setting", method = RequestMethod.POST)
    public ResponseEntity<?> dbBackupSettingRead(@RequestHeader("Authorization") String jwt) throws Exception {

        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = dbBackupService.loadSetting();
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    //===
    @ApiOperation(value="12.08 還原資料備份檔", notes="", position=8)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }") //, response=PtTreatmentFeeDto.class)
    })
    @RequestMapping(value = "/dbbackup/restore/{backup_id}", method = RequestMethod.POST)
    public ResponseEntity<?> dbBackupRestore(@RequestHeader("Authorization") String jwt,
        @PathVariable int backup_id) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = dbBackupService.restore(backup_id);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="12.09 取得資料還原進度", notes="", position=9)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }")
    })
    @RequestMapping(value = "/dbbackup/restore/progress", method = RequestMethod.POST)
    public ResponseEntity<?> dbBackupRestoreProgress(@RequestHeader("Authorization") String jwt) throws Exception {

        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            java.util.Map<String, Object> retMap = dbBackupService.loadRestoreProgress();
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    
    @ApiOperation(value="12.10 放棄資料還原process", notes="", position=10)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }")
    })
    @RequestMapping(value = "/dbbackup/restore/abort", method = RequestMethod.POST)
    public ResponseEntity<?> dbBackupRestoreAbort(@RequestHeader("Authorization") String jwt) throws Exception {

        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            int status = dbBackupService.setRestoreAbort();
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", status);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }
    //===
    @ApiOperation(value="12.11 Backup Initiate", notes="回復全部狀態值", position=99)
    @ApiResponses({
        @ApiResponse(code = 200, message="{ ... }") //, response=PtTreatmentFeeDto.class)
    })
    @RequestMapping(value = "/dbbackup/initiate", method = RequestMethod.POST)
    public ResponseEntity<?> dbBackupTest(@RequestHeader("Authorization") String jwt) throws Exception {
        
        java.util.Map<String, Object> jwtValidation = paymentTermsService.jwtValidate(jwt, 4);
        if ((int)jwtValidation.get("status") != 200) {
            return new ResponseEntity<>(jwtValidation, HttpStatus.UNAUTHORIZED);
        } else {
            webConfigDao.setConfig("backup_busy", "0", "");
            webConfigDao.setConfig("backup_abort", "0", "");
            webConfigDao.setConfig("backup_progress", "0", "");
            webConfigDao.setConfig("restore_busy", "0", "");
            webConfigDao.setConfig("restore_abort", "0", "");
            webConfigDao.setConfig("restore_progress", "0", "");
            java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
            retMap.put("status", 0);
            return new ResponseEntity<>(retMap, HttpStatus.OK);
        }
    }


}
