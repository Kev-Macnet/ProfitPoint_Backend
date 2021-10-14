package tw.com.leadtek.nhiwidget.service;

import java.util.Map;

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
    
    public java.util.List<Map<String, Object>> findAll(long id, String userName) {
        java.util.List<Map<String, Object>> lst = dbBakupLogDao.findAll(id, userName);
        for (Map<String, Object> item: lst) {
            item.remove("filename");
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
//                    retMap.put("description", mapBackup.get("description"));
//                    retMap.put("fileNames", lstFileName);
                    //-------------
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
        backupPath = backupPath+Utility.dateFormat(new java.util.Date(), "HHmmss")+"\\";
        fwork = new java.io.File(backupPath);
        if (!fwork.exists()) { 
            fwork.mkdirs();
        }
        
        java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
        webConfigDao.setConfig("backup_busy", "1", "");
        webConfigDao.setConfig("backup_abort", "0", "");
        java.util.Map<String, Object> mapBackup = backupEntry(backupPath, mode, newest);
        java.util.List<String> lstFileName = (java.util.List)mapBackup.get("fileNames");
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
            dbBakupLogDao.add(username, extractFileName(zipFileName), mode, gson.toJson(mapBackup.get("description")));
            webConfigDao.setConfig("backup_progress", "100.0", "備份進度");
        }
        webConfigDao.setConfig("backup_busy", "0", "");
//        webConfigDao.setConfig("backup_abort", "0", "");
        
        retMap.put("description", mapBackup.get("description"));
        retMap.put("fileNames", lstFileName);
        return retMap;
    }
    
    public Map<String, Object> backupEntry(String backupPath, int mode, boolean newest) {
        //------------
        int abortValue=0;
        java.util.Date update = Utility.detectDate("1990-01-01");
        String[][] tbName = {
                {"MR", "ID", "UPDATE_AT", "2"},
//                {"MR_CHECKED", "ID", "UPDATE_AT", "1"},
                {"IP_D", "ID", "UPDATE_AT", "2"},
                {"IP_P", "ID", "UPDATE_AT", "1"},
                {"IP_T", "ID", "UPDATE_AT", "2"},
                {"OP_D", "ID", "UPDATE_AT", "2"},
                {"OP_P", "ID", "UPDATE_AT", "1"},
                {"OP_T", "ID", "UPDATE_AT", "2"}
            };
        if (newest) {
            String lastDate = webConfigDao.getConfigValue("backup_last_date");
            if (lastDate.length() > 0) {
                update = Utility.detectDate(lastDate);
            } 
        }
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
                        update = new java.util.Date(0l);
                    } 
                    java.util.Map<String, Object> backupResult = backupTable(backupPath, tbName[idx][0],tbName[idx][1], tbName[idx][2], update, 
                            progress, totalProgress);
                    String fileName = backupResult.get("fileName").toString();
                    if (fileName.length()>0) {
                        lstFileName.add(fileName);
                    }
                    java.util.Map<String, Object> mapDescription = new java.util.HashMap<String, Object>();
                    mapDescription.put("table", tbName[idx][0]);
                    mapDescription.put("count", backupResult.get("rowCount"));
//                    mapDescription.put("fileName", backupResult.get("fileName").toString());
                    lstDescription.add(mapDescription);
                    rowCount += (long)backupResult.get("rowCount");
                }
                webConfigDao.setConfig("backup_progress", String.valueOf((100.0*progress)/totalProgress), "備份進度");
            }
        }

        if (!abort.equals("1")) {
            String todayStr = Utility.dateFormat(new java.util.Date(), "yyyy-MM-dd");
            webConfigDao.setConfig("backup_last_date", todayStr, "");
        }
        java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
        retMap.put("fileNames", lstFileName);
        retMap.put("description", lstDescription);
        retMap.put("count", rowCount);
        retMap.put("abort", abortValue);
        return retMap;
    }
    
    public java.util.Map<String, Object> backupTable(String path, String tableName, String idName, String updateName, java.util.Date update, 
                                                     int indexProgress, int totalProgress) {
        long rowCount = 0;
        long step = 20000;
        String delimiter = ",";
        String abort;
        double progress1, progress2;
        progress1 = 100.0*(indexProgress-1)/totalProgress;
        java.util.List<String> lstData = new java.util.LinkedList<String>();
        java.util.Map<String, Long> mapRange = dbBackupDao.getTableIdRange(tableName, idName);
        System.out.println(mapRange);
//        mapRange.put("max_id", 30000l); //shunxian test! test! test!
        long minId = mapRange.get("min_id");
        long maxId = mapRange.get("max_id");
        long start = minId;
        String fileName = path+tableName+".txt";
        while (start <= maxId) {
//            lstData.clear();
            java.util.List<Map<String, Object>> lstRow = dbBackupDao.findData(tableName, idName, start, start+step-1, updateName, update);
            if (lstRow.size() > 0) {
                if (start == minId) { // 處理 Header
                    StringBuffer title = new StringBuffer(); 
                    for (java.util.Map.Entry<String, Object> entry : lstRow.get(0).entrySet()) {
                        title.append(quotedStr(entry.getKey()) + delimiter);
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
                            buff.append(quotedStr(entry.getValue()) + delimiter);
                        } else {
                            buff.append("[null]" + delimiter);
                        }
                    }
                    lstData.add(buff.toString());
                }
                lstRow.clear();
                if (rowCount>0) {
                    Utility.saveToFile(fileName, lstData, true);
                }
                lstData.clear();
                if (totalProgress>0) {
                    progress2 = progress1 + (100.0*start/(maxId*totalProgress));
                    webConfigDao.setConfig("backup_progress", String.valueOf(progress2), "備份進度");
                }
            }
            start += step;
            abort = webConfigDao.getConfigValue("backup_abort");
            if (abort.equals("1")) {
                break;
            }
        }
        System.out.println("lstData="+lstData.size()+", path="+path);
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
        webConfigDao.setConfig("backup_setting", params.toString(), "系統資料備份參數");
        return 1;
    }
    
    public java.util.Map<String, Object> loadSetting() {
        java.util.Map<String, Object> retMap;
        String backupSetting = webConfigDao.getConfigValue("backup_setting");
        if (backupSetting.length()>0) {
            BasicJsonParser linkJsonParser = new BasicJsonParser();
            retMap = linkJsonParser.parseMap(backupSetting);
        } else {
            retMap = new java.util.HashMap<String, Object>();
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
    
    public java.util.Date getBackupLastDate() {
        String lastDate = webConfigDao.getConfigValue("backup_last_date");
        if (lastDate.length()==0) {
            lastDate = "1990-01-01";
        }
        return Utility.detectDate(lastDate);
    }
    
    //=== Restore ------
    public java.util.Map<String, Object> restore(long id) {
        java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
        String busy = webConfigDao.getConfigValue("restore_busy");
        if (!busy.equals("1")) {

            new Thread(() -> {
                logger.info("dbRestore...");
                try {
                    Thread.sleep(100);
                    //-------------
                    int retCnt = 0;
                    java.util.Map<String, Object> mapBackup = dbBakupLogDao.findOne(id);
                    if (!mapBackup.isEmpty()) {
//                        System.out.println("mapBackup-----");
//                        System.out.println(mapBackup);
                        String backupPath = getBackupPath();
                        String zipFileName = backupPath+mapBackup.get("filename").toString();
                        String unzipPath = backupPath+"unzip_"+Utility.dateFormat(new java.util.Date(), "HHmmss")+"/";
                        java.io.File fwork = new java.io.File(unzipPath);
                        if (!fwork.exists()) { 
                            fwork.mkdirs();
                        }
//                        System.out.println("backupPath = "+backupPath);
//                        System.out.println("unzipPath = "+unzipPath);
                        String csvFullName, tableName;
                        java.util.List<String> csvFiles = ZipLib.unzipFile(zipFileName, unzipPath);
                        System.out.println("csvFiles-----"+csvFiles.size());
//                        System.out.println(csvFiles);
                        String abort="0";
                        int progress = 0;
                        webConfigDao.setConfig("restore_busy", "1", "data還原中...");
                        webConfigDao.setConfig("restore_progress", "0", String.valueOf(csvFiles.size()));
                        webConfigDao.setConfig("restore_abort", "0", "");
                        for (String csvName : csvFiles) {
                            csvFullName = unzipPath+csvName;
                            tableName = csvName.replace(".txt", "");
                            System.out.println("csvFullName = "+csvName);
                            java.util.List<String> lstData = Utility.loadFromFile(csvFullName);
                            retCnt += restoreProcess(tableName, lstData, progress+1, csvFiles.size());
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
            retMap.put("status", "-1"); //busy
            retMap.put("message", "資料還原中, 無法再進行還原。"); 
        }
        return retMap;
    }
    
    public int restoreProcess(String tableName, java.util.List<String> lstData, int index, int total) {
        int retCnt = 0;
        int execResult;
        String sql, abort;
        String strHeader = lstData.get(0);
//        System.out.println("len="+lstData.size());
//        System.out.println("strHeader="+strHeader);
        double progress1, progress2;
        progress1 = 100.0*(index-1)/total;
        long lstSize = lstData.size();
        boolean passHeader = true;
        int idx = 0;
        for (String strRow : lstData) {
            if (passHeader) {
                passHeader=false;
                continue; //跳過 Header
            }
            sql = generateUpdateSql(tableName, "ID", strHeader, strRow);
            execResult = dbBackupDao.execSql(sql);
            if (execResult==0) {
                sql = generateInsertSql(tableName, "ID", strHeader, strRow);
                execResult = dbBackupDao.execSql(sql);
            }
            retCnt += execResult;
            if (idx++ >= 1000) {
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
    
    public String generateUpdateSql(String tableName, String primary, String headStr, String rowStr) {
        String header, data;
        String primaryVal = "";
        String[] arrHead = headStr.split(",");
        String[] arrData = rowStr.split(",");
        StringBuffer sbData = new StringBuffer();
        for (int a=0; a<arrHead.length; a++) {
            header = arrHead[a];
            data = arrData[a]; 
            if (header.length()>0) {
                header = quotedTrim(header).toUpperCase();
                if (header.equals(primary.toUpperCase())) {
                    if (data.equals("[null]")) {
                        primaryVal = "null";
                    } else {
                        primaryVal = quotedReplace(arrData[a]);
                    }
                } else {
//                    String.format("  %s=%s,", header.toUpperCase(), quotedReplace(arrData[a]));
                    if (a<arrHead.length-1) {
                        if (data.equals("[null]")) {
                            sbData.append(String.format("    %s=null,\n", header));
                        } else {
                            sbData.append(String.format("    %s=%s,\n", header, quotedReplace(arrData[a])));
                        }
                    } else {
                        if (data.equals("[null]")) {
                            sbData.append(String.format("    %s=null\n", header));
                        } else {
                            sbData.append(String.format("    %s=%s\n", header, quotedReplace(arrData[a])));
                        }
                    }
                }
            }
        }
        if (primaryVal.length()>0) {
            sbData.append(String.format("Where (%s=%s)", primary, primaryVal));
        }
        String ret = String.format("Update %s \nSet", tableName.toUpperCase())+sbData.toString();
        return ret; 
    }
    
    
    public String generateInsertSql(String tableName, String primary, String headStr, String rowStr) {
        String header, data;
        String[] arrHead = headStr.split(",");
        String[] arrData = rowStr.split(",");
        StringBuffer sbHead = new StringBuffer();
        StringBuffer sbData = new StringBuffer();
        sbHead.append(tableName+"(");
        sbData.append("Values(");
        for (int a=0; a<arrHead.length; a++) {
            header = arrHead[a];
            data = arrData[a]; 
            if (header.length()>0) {
                header = quotedTrim(header);
                if (a<arrHead.length-1) {
                    sbHead.append(header.toUpperCase()+", ");
                    if (data.equals("[null]")) {
                        sbData.append("null ,");
                    } else {
                        sbData.append(quotedReplace(arrData[a])+", ");
                    }
                } else {
                    sbHead.append(header.toUpperCase()+")");
                    if (data.equals("[null]")) {
                        sbData.append("null)");
                    } else {
                        sbData.append(quotedReplace(arrData[a])+")");
                    }
                }
                
            }
        }
        String ret = "Insert Into\n"+sbHead.toString()+"\n"+sbData.toString();
        return ret; 
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
    
    public String extractFileName(String fName) {
        java.io.File f = new java.io.File(fName);
        return (f.getName());
    }
    
    public String getBackupPath() {
        java.io.File fcurrent = new java.io.File("");
      String currentPath = fcurrent.getAbsolutePath()+"\\";
      String backupPath = currentPath+"backup_data\\";
        return (backupPath);
    }

    //===
    public boolean isDoBackup() {
        boolean doIt = false;
        java.util.Map<String, Object> mapSetting = loadSetting();
//        System.out.println("mapSetting------");
//        System.out.println(mapSetting);
        if (!mapSetting.isEmpty()) {
          //{every=2, week=1, month=1, time=02:23, mode=2, add=0}
            java.util.Date nextOnTime;
            String strToday = Utility.dateFormat(new java.util.Date(), "yyyy-MM-dd");
            String strTime = mapSetting.get("time").toString();
            int every = Integer.valueOf(mapSetting.get("every").toString());
            if (every==1) { //每日
                nextOnTime = Utility.strToDate(strToday+" "+strTime, "yyyy-MM-dd HH:mm");
                if ((nextOnTime.getTime() < new java.util.Date().getTime()) && 
                    (getBackupLastDate().getTime() < Utility.detectDate(strToday).getTime())) {
                    doIt = true;
                } 
            } else if (every==2) { //每周
                int todayWeek = Utility.dayOfWeek(new java.util.Date());
                int week = Integer.valueOf(mapSetting.get("week").toString());
//                System.out.println("todayWeek="+todayWeek+", week="+week);
                if ((week+1) == todayWeek) {
                    nextOnTime = Utility.strToDate(strToday+" "+strTime, "yyyy-MM-dd HH:mm");
//                    System.out.println("nextOnTime="+nextOnTime);
                    if (nextOnTime.getTime() < new java.util.Date().getTime()) {
                        if (getBackupLastDate().getTime() < Utility.detectDate(strToday).getTime()) {
                            doIt = true;
                        }
                    }
                }
            } else if (every==3) { //每月
                int todayDay = Utility.dayOfMonth(new java.util.Date());
                int day = Integer.valueOf(mapSetting.get("month").toString());
//                System.out.println("todayDay="+todayDay+", day="+day);
                if (day == todayDay) {
                    nextOnTime = Utility.strToDate(strToday+" "+strTime, "yyyy-MM-dd HH:mm");
//                    System.out.println("nextOnTime="+nextOnTime);
                    if (nextOnTime.getTime() < new java.util.Date().getTime()) {
                        if (getBackupLastDate().getTime() < Utility.detectDate(strToday).getTime()) {
                            doIt = true;
                        }
                    }
                }
            }
        }
        return doIt;
    }

}
