package tw.com.leadtek.nhiwidget.service;

import java.util.Map;

import org.hibernate.internal.build.AllowSysOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dto.BackupSettingDto;
import tw.com.leadtek.nhiwidget.sql.BackupLogDao;
import tw.com.leadtek.nhiwidget.sql.DbBackupDao;
import tw.com.leadtek.nhiwidget.sql.WebConfigDao;
import tw.com.leadtek.tools.Utility;
import tw.com.leadtek.tools.ZipLib;

@Service
public class DbBackupService {
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DbBackupDao dbBackupDao;
    @Autowired
    private BackupLogDao dbBakupLogDao;
    @Autowired
    private WebConfigDao webConfigDao;

    boolean TRIAL = false;
    String DELIMITER = ",";
    String spliteDELIMITER = ",";
    private String[][] tbName = {
            {"MR", "ID", "UPDATE_AT", "2"},
            {"IP_D", "ID", "UPDATE_AT", "2"},
            {"IP_P", "ID", "UPDATE_AT", "2"},
            {"IP_T", "ID", "UPDATE_AT", "2"},
            {"OP_D", "ID", "UPDATE_AT", "2"},
            {"OP_P", "ID", "UPDATE_AT", "2"},
            {"OP_T", "ID", "UPDATE_AT", "2"},
            {"AP_ADDITIONAL_POINT", "ID", "", "1"},
            {"AP_OUTPATIENT_1", "ID", "", "1"},
            {"AP_OUTPATIENT_1_CATEGORY", "ID,CATEGORY", "", "1"},
            {"AP_OUTPATIENT_2", "ID", "", "1"},
            {"AP_OUTPATIENT_2_CPOE", "id,cpoe", "", "1"},
            {"AP_OUTPATIENT_3", "id,nhi_no", "", "1"},
            {"AP_OUTPATIENT_4", "ID", "", "1"},
            {"AP_OUTPATIENT_4_CATEGORY", "id,category", "", "1"},
            {"AP_OUTPATIENT_4_CPOE", "id,cpoe", "", "1"},
            {"AP_OUTPATIENT_4_TREATMENT", "id,treatment", "", "1"},
            {"AP_OUTPATIENT_5", "id,icd_no,nhi_no", "", "1"},
            {"AP_OUTPATIENT_5_CPOE", "id,cpoe", "", "1"},
            {"AP_OUTPATIENT_6", "ID", "", "1"},
            {"AP_OUTPATIENT_6_CATEGORY", "id,category", "", "1"},
            {"AP_OUTPATIENT_6_CPOE", "id,cpoe", "", "1"},
            {"AP_OUTPATIENT_6_PLAN", "id,plan", "", "1"},
            {"AP_OUTPATIENT_7", "ID", "", "1"},
            {"AP_OUTPATIENT_7_PLAN", "id,plan", "", "1"},
            {"AP_OUTPATIENT_7_TRIAL", "id,trial", "", "1"},
            {"AP_INPATIENT_1", "ID", "", "1"},
            {"AP_INPATIENT_1_CATEGORY", "id,category", "", "1"},
            {"AP_INPATIENT_2", "ID", "", "1"},
            {"AP_INPATIENT_2_CPOE", "id,cpoe", "", "1"},
            {"AP_INPATIENT_3", "id,nhi_no", "", "1"},
            {"AP_INPATIENT_6", "ID", "", "1"},
            {"AP_INPATIENT_6_CATEGORY", "id,category", "", "1"},
            {"AP_INPATIENT_6_CPOE", "id,cpoe", "", "1"},
            {"AP_INPATIENT_6_PLAN", "id,plan", "", "1"},
            //----
            {"PLAN_CONDITION", "ID", "", "1"},
            {"PLAN_ICD_NO", "ID,ICD_NO", "", "1"},
            {"PLAN_LESS_NDAY", "ID,ICD_NO", "", "1"},
            {"PLAN_MORE_TIMES", "ID,ICD_NO", "", "1"},
            {"PLAN_EXP_ICD_NO", "ID,ICD_NO", "", "1"},
            {"PLAN_NO_EXP_ICD_NO", "ID,ICD_NO", "", "1"},
            //----
            {"PT_PAYMENT_TERMS", "ID", "", "1"},
            {"pt_hospital_type", "pt_id,hospital_type", "", "1"},
            {"pt_exclude_nhi_no", "pt_id,nhi_no", "", "1"},
            {"pt_lim_division", "pt_id,division", "", "1"},
            {"pt_not_allow_plan", "pt_id,plan", "", "1"},
            {"pt_coexist_nhi_no", "pt_id,nhi_no", "", "1"},
            {"pt_notify_nhi_no", "pt_id,nhi_no", "", "1"},
            {"pt_include_icd_no", "pt_id,icd_no", "", "1"},
            {"pt_drg_no", "pt_id,drg_no", "", "1"},
            {"pt_outpatient_fee", "pt_id", "", "1"},
            {"pt_inpatient_fee", "pt_id", "", "1"},
            {"pt_ward_fee", "pt_id", "", "1"},
            {"pt_surgery_fee", "pt_id", "", "1"},
            {"pt_psychiatricward_fee", "pt_id", "", "1"},
            {"pt_treatment_fee", "pt_id", "", "1"},
            {"pt_tube_feeding_fee", "pt_id", "", "1"},
            {"pt_nutritional_fee", "pt_id", "", "1"},
            {"pt_adjustment_fee", "pt_id", "", "1"},
            {"pt_medicine_fee", "pt_id", "", "1"},
            {"pt_radiation_fee", "pt_id", "", "1"},
            {"pt_injection_fee", "pt_id", "", "1"},
            {"pt_quality_service", "pt_id", "", "1"},
            {"pt_rehabilitation_fee", "pt_id", "", "1"},
            {"pt_psychiatric_fee", "pt_id", "", "1"},
            {"pt_bone_marrow_trans_fee", "pt_id", "", "1"},
            {"pt_anesthesia_fee", "pt_id", "", "1"},
            {"pt_specific_medical_fee", "pt_id", "", "1"},
            {"pt_others_fee", "pt_id", "", "1"},
            //-----
            {"USER", "ID", "", "1"},
            {"DEPARTMENT", "ID", "", "1"},
            {"USER_DEPARTMENT", "USER_ID,DEPARTMENT_ID", "", "1"},
            {"CODE_CONFLICT", "ID", "", "1"},
            {"ICD10", "ID", "", "1"},
            {"ASSIGNED_POINT", "ID", "", "1"},
            {"PARAMETERS", "ID", "", "1"},
            {"MR_NOTE", "ID", "", "1"},
            {"DEDUCTED_NOTE", "ID", "", "1"},
            {"PAY_CODE", "ID", "UPDATE_AT", "1"},
            {"DRG_CODE", "ID", "", "1"},
            {"ATC", "CODE", "", "1"},
            {"DEDUCTED", "ID", "", "1"},
            {"CODE_THRESHOLD", "ID", "", "1"}
//            {"", "ID", "", "1"},
        };
    
    public String[] parsePrimaryKey(String tableName) {
        String ret="";
        for (int a=0; a<tbName.length; a++) {
            if (tbName[a][0].toUpperCase().equals(tableName.toUpperCase())) {
                ret= tbName[a][1].toUpperCase();
            }
        }
        return ret.split(",");
    }
    
    public int arrayIndexOf(String key, String arr[]) {
        int ret=-1;
        key = key.toUpperCase();
        for (int m=0; m<arr.length; m++) {
            if (key.equals(arr[m].toUpperCase())) {
                ret = m;
               break;
            }
        }
      return (ret);
    }

    
    public java.util.List<Map<String, Object>> findAll(long id, String userName) {
        BasicJsonParser linkJsonParser = new BasicJsonParser();
        java.util.List<Map<String, Object>> lst = dbBakupLogDao.findAllById(id, userName);
        System.out.println("query :" + lst.size());
        for (Map<String, Object> item: lst) {
//            item.remove("filename");
            String description = item.get("description").toString();
            if (description.length()>7) {
                item.put("description", linkJsonParser.parseList(description));
            }
        }
        return lst;
    }
    
    
    public int deleteRow(long id) {
        java.util.Map<String, Object> mapBackup = dbBakupLogDao.findOne(id);
        if (!mapBackup.isEmpty()) {
            String fileName = getBackupPath()+mapBackup.get("filename").toString();
            java.io.File f = new java.io.File(fileName);
            if (f.exists()) {
                Utility.deleteFile(fileName);
            }
        }
        int ret = dbBakupLogDao.delete(id);
        return ret;
    }
    
    public String getFilename(long backupId) {
        String ret = "";
        java.util.Map<String, Object> mapBackup = dbBakupLogDao.findOne(backupId);
        if (!mapBackup.isEmpty()) {
            String fileName = getBackupPath()+mapBackup.get("filename").toString();
            java.io.File f = new java.io.File(fileName);
            if (f.exists()) {
                ret = fileName; 
            }
        }
        return ret;
    }
    
    public int setBackupAbort() {
        int ret = -1;
        String busy = webConfigDao.getConfigValue("backup_busy");
        if (busy.equals("1")) {
            webConfigDao.setConfig("backup_abort", "1", "");
            ret = 0;
        }
        return ret;
    }
    
    public int setRestoreAbort() {
        int ret = -1;
        String busy = webConfigDao.getConfigValue("restore_busy");
        if (busy.equals("1")) {
            webConfigDao.setConfig("restore_abort", "1", "");
            ret = 0;
        }
        return ret;
    }

    public Map<String, Object> dbBackup(int mode, String username) {
        java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
        String busy = webConfigDao.getConfigValue("backup_busy");
        if (!busy.equals("1")) {

            new Thread(() -> {
                logger.info("dbBackup...");
                try {
                    Thread.sleep(100);
                    dbBackupKernel(mode, username, false);
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }).start();

            retMap.put("status", "0"); //busy
            retMap.put("message", "備份資料執行中......");
        } else {
            retMap.put("status", "-1"); //busy
            retMap.put("message", "備份中, 無法再進行備份。"); 
        }
        return retMap;
    }
    
    public Map<String, Object> dbBackupKernel(int mode, String username, boolean newest) {
        String backupPath = getBackupPath();
        String zipName = backupPath+"backup_"+Utility.dateFormat(new java.util.Date(), "yyyyMMdd-HHmmss")+".zip";  //yyyy/MM/dd HH:mm:ss
        java.io.File fwork = new java.io.File(backupPath);
        if (!fwork.exists()) { 
            fwork.mkdirs();
        }
        backupPath = backupPath+Utility.dateFormat(new java.util.Date(), "HHmmss")+"/";
        fwork = new java.io.File(backupPath);
        if (!fwork.exists()) { 
            fwork.mkdirs();
        }
        
        java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
        webConfigDao.setConfig("backup_busy", "1", "");
        webConfigDao.setConfig("backup_abort", "0", "");
        java.util.Map<String, Object> mapBackup = backupEntry(backupPath, mode, newest);
        java.util.List<String> lstFileName = (java.util.List) mapBackup.get("fileNames");
        String zipFileName = zipName; //(String)mapBackup.get("zipName");
        String abort;
        if (lstFileName.size()>0) {
            abort = webConfigDao.getConfigValue("backup_abort");
            if (!abort.equals("1")) {
                ZipLib.zipFiles(zipFileName, lstFileName);
            }
            for (String fname : lstFileName) {
                Utility.deleteFile(fname);
            }
        }
        Utility.deleteFile(backupPath); //((String)mapBackup.get("backupPath"));
        abort = webConfigDao.getConfigValue("backup_abort");
        if (!abort.equals("1")) {
            com.google.gson.Gson gson = new com.google.gson.Gson();
            webConfigDao.setConfig("backup_progress", "100.0", "備份進度");
            if (lstFileName.size()>0) {
                dbBakupLogDao.add(username, extractFileName(zipFileName), mode, gson.toJson(mapBackup.get("description")));
            } else {
                dbBakupLogDao.add(username, "", mode, gson.toJson(mapBackup.get("description")));
            }
        }
        webConfigDao.setConfig("backup_busy", "0", "");
//        webConfigDao.setConfig("backup_abort", "0", "");
        retMap.put("count", mapBackup.get("count"));
        retMap.put("description", mapBackup.get("description"));
        retMap.put("fileNames", lstFileName);
        return retMap;
    }
    
    public Map<String, Object> backupEntry(String backupPath, int mode, boolean newest) {
        //------------
        int abortValue=0;
        java.util.Date startDate = Utility.detectDate("1990-01-01"); //631152000000
        
        if (newest) {
            String lastDate = webConfigDao.getConfigValue("backup_db_last_date");
            if (lastDate.length() > 0) {
                startDate = Utility.detectDate(lastDate);
            } 
        }
        logger.info("startDate="+Utility.dateFormat(startDate, "yyyy/MM/dd HH:mm:ss"));
        int tbMode;
        int progress = 0;
        int totalProgress = 1;
        for (int idx=0; idx<tbName.length; idx++) {
            tbMode = Integer.parseInt(tbName[idx][3]);
            if (mode==0 || tbMode==mode) {
                totalProgress++;
            }
        }

        java.util.List<Map<String, Object>> lstDescription = new java.util.ArrayList<Map<String, Object>>();
        java.util.List<String> lstFileName = new java.util.ArrayList<String>();
        String todayDateStr = Utility.dateFormat(new java.util.Date(), "yyyy-MM-dd");
        String todayTimeStr = Utility.dateFormat(new java.util.Date(), "yyyy-MM-dd HH:mm:ss");
        long rowCount = 0;
        String abort="0";
        webConfigDao.setConfig("backup_progress", "0.0", "備份進度");
        for (int idx=0; idx<tbName.length; idx++) {
            tbMode = Integer.parseInt(tbName[idx][3]);
            if (mode==0 || tbMode==mode) {
                progress++;
                abort = webConfigDao.getConfigValue("backup_abort");
                if (abort.equals("1")) {
                    abortValue = 1;
                    break;
                } else {
                    if (!newest) {
                        startDate = new java.util.Date(0l);
                    } 
                    java.util.Map<String, Object> backupResult;
                    if (tbMode==1) {
                        backupResult = backupSettingTable(backupPath, tbName[idx][0].toUpperCase(),tbName[idx][1]);
                    } else {
                        backupResult = backupDataTable(backupPath, tbName[idx][0].toUpperCase(),tbName[idx][1], tbName[idx][2], startDate, progress, totalProgress);
                    }

                    String fileName = backupResult.get("fileName").toString();
                    if (fileName.length()>0) {
                        lstFileName.add(fileName);
                    }
                    java.util.Map<String, Object> mapDescription = new java.util.HashMap<String, Object>();
                    mapDescription.put(tbName[idx][0], backupResult.get("rowCount"));
                    lstDescription.add(mapDescription);
                    rowCount += (long)backupResult.get("rowCount");
                }
                webConfigDao.setConfig("backup_progress", String.valueOf((100.0*progress)/totalProgress), "備份進度");
            }
        }

        if (!abort.equals("1")) {
            webConfigDao.setConfig("backup_last_date", todayDateStr, "");
            if ((mode==0)||(mode==2)) {
                webConfigDao.setConfig("backup_db_last_date", todayTimeStr, "");
            }
        }
        java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
        retMap.put("fileNames", lstFileName);
        retMap.put("description", lstDescription);
        retMap.put("count", rowCount);
        retMap.put("abort", abortValue);
        return retMap;
    }
    
    public java.util.Map<String, Object> backupDataTable(String path, String tableName, String idField, String updateField, java.util.Date startDate, int indexProgress, int totalProgress) {
        long rowCount = 0;
        long pageSize = 20000;
        String abort;
        double progress1, progress2;
        progress1 = 100.0*(indexProgress-1)/totalProgress;
        java.util.List<String> lstData = new java.util.LinkedList<String>();
        java.util.Map<String, Long> mapRange = dbBackupDao.getTableIdRange(tableName, idField, updateField, startDate);
        // ---------------------------- shunxian test! test! test!
        if (TRIAL) {
            pageSize = 10000;
            mapRange.put("count", 21000l);
        }
     // ---------------------------- shunxian test! test! test!
        long stopIdx = mapRange.get("count");
        long startIdx = 0;
        String fileName = path+tableName+".txt";
        while (startIdx <= stopIdx) {
            java.util.List<Map<String, Object>> lstRow = dbBackupDao.findData(tableName, idField, startIdx, pageSize, updateField, startDate);
            if (lstRow.size() > 0) {
                if (startIdx == 0) { // 處理 Header
                    StringBuffer title = new StringBuffer(); 
                    for (java.util.Map.Entry<String, Object> entry : lstRow.get(0).entrySet()) {
                        title.append(quotedStr(entry.getKey()) + DELIMITER);
                    }
                    lstData.add(title.toString());
                    Utility.saveToFile(fileName, lstData, false);
                    lstData.clear();
                }
                for (Map<String, Object>item : lstRow) {
                    rowCount++;
                    StringBuffer buff = new StringBuffer(); 
                    for (java.util.Map.Entry<String, Object> entry : item.entrySet()) {
                        if (entry.getValue() != null) {
                            buff.append(transEnter(quotedStr(entry.getValue())) + DELIMITER);
                        } else {
                            buff.append("[null]" + DELIMITER);
                        }
                    }
                    lstData.add(buff.toString());
                }
                startIdx += lstRow.size();
                lstRow.clear();
                if (rowCount>0) {
                    Utility.saveToFile(fileName, lstData, true);
                }
                lstData.clear();
                if (totalProgress>0) {
                    progress2 = progress1 + (100.0*startIdx/(stopIdx*totalProgress));
                    webConfigDao.setConfig("backup_progress", String.valueOf(progress2), "備份進度");
                }
            } else {
                break;
            }
            abort = webConfigDao.getConfigValue("backup_abort");
            if (abort.equals("1")) {
                break;
            }
        }
        java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
        retMap.put("rowCount", rowCount);
        if (rowCount>0) {
            Utility.saveToFile(fileName, lstData, true);
            retMap.put("fileName", fileName);
        } else {
            retMap.put("fileName", "");
        }
        return (retMap);
    }
    
    
    public java.util.Map<String, Object> backupSettingTable(String path, String tableName, String idName) {
        long rowCount = 0;
        java.util.List<String> lstData = new java.util.LinkedList<String>();
        String fileName = path+tableName+".txt";
        java.util.List<Map<String, Object>> lstRow = dbBackupDao.findAll(tableName, idName);
        if (lstRow.size() > 0) {
            // 處理 Header
            StringBuffer title = new StringBuffer(); 
            for (java.util.Map.Entry<String, Object> entry : lstRow.get(0).entrySet()) {
                title.append(quotedStr(entry.getKey()) + DELIMITER);
            }
            lstData.add(title.toString());
            Utility.saveToFile(fileName, lstData, false);
            lstData.clear();
            // 處理資料
            for (Map<String, Object>item : lstRow) {
                rowCount++;
                StringBuffer buff = new StringBuffer(); 
                for (java.util.Map.Entry<String, Object> entry : item.entrySet()) {
                    if (entry.getValue() != null) {
                        buff.append(transEnter(quotedStr(entry.getValue())) + DELIMITER);
                    } else {
                        buff.append("[null]" + DELIMITER);
                    }
                }
                lstData.add(buff.toString());
            }
            lstRow.clear();
            if (rowCount>0) {
                Utility.saveToFile(fileName, lstData, true);
            }
            lstData.clear();
        }

        java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
        retMap.put("rowCount", rowCount);
        if (rowCount>0) {
            Utility.saveToFile(fileName, lstData, true);
            retMap.put("fileName", fileName);
        } else {
            retMap.put("fileName", "");
        }
        return (retMap);
    }


    public int saveSetting(BackupSettingDto params) {
        webConfigDao.setConfig("backup_setting", params.toString(), "資料備份參數");
        return 1;
    }
    
    public java.util.Map<String, Object> loadSetting() {
        java.util.Map<String, Object> retMap;
        String backupSetting = webConfigDao.getConfigValue("backup_setting");
        if (backupSetting.length()>0) {
            BasicJsonParser linkJsonParser = new BasicJsonParser();
            retMap = linkJsonParser.parseMap(backupSetting);
        } else {
            retMap = java.util.Collections.emptyMap();
        }
        return (retMap);
    }
    
    public java.util.Map<String, Object> loadBackupProgress() {
        java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
        String busy = webConfigDao.getConfigValue("backup_busy");
        if (busy.equals("1")) {
            retMap.put("status", 0);
        } else {
            retMap.put("status", -1);
        }
        retMap.put("abort", webConfigDao.getConfigValue("backup_abort"));
        String progress = webConfigDao.getConfigValue("backup_progress");
        if (progress.length()>0) {
            retMap.put("progress", Double.parseDouble(progress));
        } else {
            retMap.put("progress", 0);
        }
        
        return (retMap);
    }
    
    public java.util.Map<String, Object> loadRestoreProgress() {
        java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
        String busy = webConfigDao.getConfigValue("restore_busy");
        if (busy.equals("1")) {
            retMap.put("status", 0);
        } else {
            retMap.put("status", -1);
        }
        retMap.put("abort", webConfigDao.getConfigValue("restore_abort"));
        String progress = webConfigDao.getConfigValue("restore_progress");
        if (progress.length()>0) {
            retMap.put("progress", Double.parseDouble(progress));
        } else {
            retMap.put("progress", 0);
        }
        
        return (retMap);
    }
    

    //=== Restore ------
    public java.util.Map<String, Object> restore(long id) {
        java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
        java.util.Map<String, Object> mapBackup = dbBakupLogDao.findOne(id);
        System.out.println("mapBackup.......");
        System.out.println(mapBackup);
        String busy = webConfigDao.getConfigValue("restore_busy");
        if (mapBackup.isEmpty()) {
            retMap.put("status", "-1"); //busy
            retMap.put("message", "無此備份紀錄。");
        } else if (mapBackup.get("filename").toString().length()==0)  {
            retMap.put("status", "-2"); //busy
            retMap.put("message", "此備份紀錄內無資料無須還原。");
        } else if (!busy.equals("1")) {
            new Thread(() -> {
                logger.info("dbRestore...");
                try {
                    Thread.sleep(100);
                    //-------------
                    int retCnt = 0;
//                    java.util.Map<String, Object> mapBackup = dbBakupLogDao.findOne(id);
                    if (!mapBackup.isEmpty()) {
                        String backupPath = getBackupPath();
                        String zipFileName = backupPath+mapBackup.get("filename").toString();
                        String unzipPath = backupPath+"unzip_"+Utility.dateFormat(new java.util.Date(), "HHmmss")+"/";
                        java.io.File fwork = new java.io.File(unzipPath);
                        if (!fwork.exists()) { 
                            fwork.mkdirs();
                        }
                        String csvFullName, tableName;
                        java.util.List<String> csvFiles = ZipLib.unzipFile(zipFileName, unzipPath);
                        String abort="0";
                        int progress = 0;
                        webConfigDao.setConfig("restore_busy", "1", "data還原中...");
                        webConfigDao.setConfig("restore_progress", "0", String.valueOf(csvFiles.size()));
                        webConfigDao.setConfig("restore_abort", "0", "");
                        for (String csvName : csvFiles) {
                            csvFullName = unzipPath+csvName;
                            tableName = csvName.replace(".txt", "");
                            String[] pk = parsePrimaryKey(tableName);
                            java.util.List<String> lstData = Utility.loadFromFile(csvFullName, "UTF-8");
                            retCnt += restoreProcess(tableName, pk, lstData, progress+1, csvFiles.size());
                            Utility.deleteFile(csvFullName);
                            webConfigDao.setConfig("restore_progress", String.valueOf((100.0*progress)/csvFiles.size()), String.valueOf(csvFiles.size()));
                            progress++;
                            abort = webConfigDao.getConfigValue("restore_abort");
                            if (abort.equals("1")) {
                                break;
                            }
                        }
                        Utility.deleteFile(unzipPath);
                        if (!abort.equals("1")) {
                            webConfigDao.setConfig("restore_progress", "100.0", "還原進度");
                        }
                        webConfigDao.setConfig("restore_busy", "0", "");
//                        webConfigDao.setConfig("restore_abort", "0", "");
                        logger.info("Restore Count = "+retCnt);
                    }
                    //-------------
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }).start();

            retMap.put("status", "0");  //busy
            retMap.put("message", "資料還原執行中......");
        } else {
            retMap.put("status", "-3"); //busy
            retMap.put("message", "資料還原中, 無法再進行還原。"); 
        }
        return retMap;
    }
    
    public int restoreProcess(String tableName, String[] pkey, java.util.List<String> lstData, int index, int total) {
        int retCnt = 0;
        int execResult;
        String sql, abort;
        String strHeader = lstData.get(0);
        if (TRIAL) {
            System.out.println("len="+lstData.size()+", "+tableName);
        }
        double progress1, progress2;
        progress1 = 100.0*(index-1)/total;
        long lstSize = lstData.size();
        boolean passHeader = true;
        int idx = 0;
        for (String strRow : lstData) {
            execResult = 0;
            if (passHeader) {
                passHeader=false;
                continue; //跳過 Header
            }
            if (pkey.length>0) {
                sql = generateUpdateSql(tableName, pkey, strHeader, strRow);
                if (sql.length()>0) {
//                    System.out.println("row="+idx+", "+sql);
                    execResult = dbBackupDao.execSql(sql);
                }
            } 
            if (execResult<=0) {
                sql = generateInsertSql(tableName, strHeader, strRow);
                if (sql.length()>0) {
//                    System.out.println("row="+idx+", "+sql);
                    execResult = dbBackupDao.execSql(sql);
                }
            }
            retCnt += execResult;
            if (idx++ >= 500) {
                idx = 0;
                progress2 = progress1 + (100.0*retCnt/(lstSize*total));
                webConfigDao.setConfig("restore_progress", String.valueOf(progress2), 
                        String.valueOf(retCnt)+","+String.valueOf(lstSize));
            }
            
            abort = webConfigDao.getConfigValue("restore_abort");
            if (abort.equals("1")) {
                break;
            }
        }
        return retCnt;
    }
    
    public String generateUpdateSql(String tableName, String[] primary, String headStr, String rowStr) {
        String header, data;
        boolean ispkey;
        String[] arrHead = headStr.split(spliteDELIMITER);
        String[] arrData = rowStr.split(spliteDELIMITER);
        if (arrHead.length!=arrData.length) {
            logger.debug("Restore DB Error:"+tableName+" / "+rowStr);
            if (TRIAL) {
                System.out.println("pass-1.3="+arrHead.length+","+arrData.length+","+primary.length);
                System.out.println("pass-1.4="+rowStr);
            }
            return "";
        } else {
            if (TRIAL) {
                if (tableName.equals("MR")) {
                    tableName="MR3";
                }
            }
            java.util.List<Map<String, String>> primaryVal = new java.util.ArrayList<Map<String, String>>();
            java.util.List<String> sbData = new java.util.ArrayList<String> ();
            for (int a=0; a<arrHead.length; a++) {
                ispkey = false;
                header = arrHead[a];
                data = arrData[a];
                if (header.length()>0) {
                    header = quotedTrim(header).toUpperCase();
                    if (arrayIndexOf(header, primary)>=0) {
                        java.util.Map<String, String> map = new java.util.HashMap<String, String>();
                        map.put("field", header);
                        if (data.equals("[null]")) {
                            map.put("value", "null");
                        } else {
                            map.put("value", quotedReplace(noInjection(arrData[a])));
                        }
                        primaryVal.add(map);
                        ispkey = true;
                    } 
                    
                    if ((ispkey==false)||(arrHead.length==primary.length)) {
                        if (sbData.size()==0) {
                            if (data.equals("[null]")) {
                                sbData.add(String.format(" %s=null", header));
                            } else {
                                sbData.add(String.format(" %s=%s", header, quotedReplace(untransEnter(noInjection(arrData[a])))));
                            }
                        } else {
                            if (data.equals("[null]")) {
                                sbData.add(String.format(",\n    %s=null", header));
                            } else {
                                sbData.add(String.format(",\n    %s=%s", header, quotedReplace(untransEnter(noInjection(arrData[a])))));
                            }
                        }
                    }
                }
            }
            if (primaryVal.size()>0) {
                sbData.add(String.format("\nWhere (1=1)", primary, primaryVal));
                for (Map<String, String> item: primaryVal) {
                    sbData.add(String.format("\n  and (%s=%s)", item.get("field"), item.get("value")));
                }
            }
            StringBuffer strBuf = new StringBuffer();
           for (String str : sbData) {
               strBuf.append(str);
           }
            String ret = String.format("Update %s \nSet", tableName.toUpperCase())+strBuf.toString();
            return ret;
        }
    }
    
    
    public String generateInsertSql(String tableName, String headStr, String rowStr) {
        String header, data;
        String[] arrHead = headStr.split(spliteDELIMITER);
        String[] arrData = rowStr.split(spliteDELIMITER);
        if (arrHead.length!=arrData.length) {
            logger.debug("Restore DB Error:"+tableName+" / "+rowStr);
            if (TRIAL) {
                System.out.println("pass-1.1="+arrHead.length+","+arrData.length);
                System.out.println("pass-1.2="+rowStr);
            }
            return "";
        } else {
            if (TRIAL) {
                if (tableName.equals("MR")) {
                    tableName="MR3";
                }
            }
            StringBuffer sbHead = new StringBuffer();
            StringBuffer sbData = new StringBuffer();
            sbHead.append(tableName+"(");
            sbData.append("Values(");
            for (int a=0; a<arrHead.length; a++) {
                header = arrHead[a];
                data = arrData[a]; 
                if (header.length()>0) {
                    header = quotedTrim(header).toUpperCase();
                    if (a<arrHead.length-1) {
                        sbHead.append(header.toUpperCase()+", ");
                        if (data.equals("[null]")) {
                            sbData.append("null ,");
                        } else {
                            sbData.append(quotedReplace(untransEnter(noInjection(arrData[a])))+", ");
                        }
                    } else {
                        sbHead.append(header.toUpperCase()+")");
                        if (data.equals("[null]")) {
                            sbData.append("null)");
                        } else {
                            sbData.append(quotedReplace(untransEnter(noInjection(arrData[a])))+")");
                        }
                    }
                    
                }
            }
            String ret = "Insert Into\n"+sbHead.toString()+"\n"+sbData.toString();
            return ret;
        }
    }

    
    //===
    public String quotedStr(Object obj) {
        if (obj instanceof String) {
            return ("\"" + obj + "\"");
        } else if (obj instanceof java.util.Date) {
            java.sql.Date tm = (java.sql.Date)obj;
            return ("\"" + tm.getTime() + "\"");
        } else {
            return (obj.toString());
        }
    }
    
    public String quotedTrim(String str) {
        String quoted = "\"";
        if ((str.startsWith(quoted))&&(str.endsWith(quoted))) {
            str = str.substring(quoted.length(), str.length()-quoted.length());
        }
        return str;
    }
    
    public String quotedReplace(String str) {
        String quoted = "\"";
        String quoted2 = "\'";
        if ((str.startsWith(quoted))&&(str.endsWith(quoted))) {
            str = str.substring(quoted.length(), str.length()-quoted.length());
            str = quoted2+str+quoted2;
        }
        return str;
    }
    
    public String noInjection(String str) {
        if (str!=null) {
            return (str.replaceAll("\'", "\'\'"));
        } else {
            return str;
        }
    }
    
    public String transEnter(String str) {
        if (str!=null) {
            str = str.replaceAll(",", "<#44>");
            return (str.replaceAll("\n", "<#13>"));
        } else {
            return str;
        }
    }
    
    public String untransEnter(String str) {
        if (str!=null) {
            str = str.replaceAll("<#44>", ",");
            return (str.replaceAll("<#13>", "\n"));
        } else {
            return str;
        }
    }

    
    public String extractFileName(String fName) {
        java.io.File f = new java.io.File(fName);
        return (f.getName());
    }
    
    public String getBackupPath() {
        java.io.File fcurrent = new java.io.File("");
        String separator = (System.getProperty("os.name").toLowerCase().startsWith("windows")) ? "\\" : "/" ;
        String currentPath = fcurrent.getAbsolutePath()+separator;
        String backupPath = currentPath+"backup_data/";
        return (backupPath);
    }


}
