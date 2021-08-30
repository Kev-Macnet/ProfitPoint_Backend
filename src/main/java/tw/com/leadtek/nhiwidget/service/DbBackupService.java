package tw.com.leadtek.nhiwidget.service;

import java.io.FileNotFoundException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dto.BackupSettingDto;
import tw.com.leadtek.nhiwidget.sql.BackupLogDao;
import tw.com.leadtek.nhiwidget.sql.DbBackupDao;
import tw.com.leadtek.nhiwidget.sql.WebConfigDao;
import tw.com.leadtek.tools.Utility;

@Service
public class DbBackupService {
//    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

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
            String fileName = mapBackup.get("filename").toString();
            java.io.File f = new java.io.File(fileName);
            if (f.exists()) {
                Utility.deleteFile(fileName);
            }
        }
        int ret = dbBakupLogDao.delete(id);
        return ret;
    }
    
    public int abort() {
        int ret = -1;
        String busy = webConfigDao.getConfigValue("backup_busy");
        if (busy.equals("1")) {
            webConfigDao.setConfig("backup_abort", "1", "");
            ret = 0;
        }
        return ret;
    }

    
    public Map<String, Object> dbBackup(int mode, String username) {
        java.io.File fcurrent = new java.io.File("");
        String currentPath = fcurrent.getAbsolutePath()+"\\";
        String backupPath = currentPath+"backup_data\\";
        String zipName = backupPath+"backup_"+Utility.dateFormat(new java.util.Date(), "yyyyMMdd-HHmmss")+".zip";  //yyyy/MM/dd HH:mm:ss
        
        java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
        String busy = webConfigDao.getConfigValue("backup_busy");
        if (!busy.equals("1")) {
            java.io.File fwork = new java.io.File(backupPath);
            if (!fwork.exists()) { 
                fwork.mkdirs();
            }
            backupPath = backupPath+Utility.dateFormat(new java.util.Date(), "HHmmss")+"\\";
            fwork = new java.io.File(backupPath);
            if (!fwork.exists()) { 
                fwork.mkdirs();
            }
            
            webConfigDao.setConfig("backup_busy", "1", "");
            java.util.Map<String, Object> mapBackup = backupEntry(backupPath, mode, false);
            java.util.List<String> lstFileName = (java.util.List)mapBackup.get("fileNames");
            String zipFileName = zipName; //(String)mapBackup.get("zipName");
            String abort;
            if (lstFileName.size()>0) {
                abort = webConfigDao.getConfigValue("backup_abort");
                if (!abort.equals("1")) {
                    zipFiles(zipFileName, lstFileName);
                }
                for (String fname : lstFileName) {
                    Utility.deleteFile(fname);
                }
            }
            Utility.deleteFile(backupPath); //((String)mapBackup.get("backupPath"));
            abort = webConfigDao.getConfigValue("backup_abort");
            if (!abort.equals("1")) {
                com.google.gson.Gson gson = new com.google.gson.Gson();
                dbBakupLogDao.add(username, zipFileName, mode, gson.toJson(mapBackup.get("description")));
            }
            webConfigDao.setConfig("backup_busy", "0", "");
            webConfigDao.setConfig("backup_abort", "0", "");
            
            retMap.put("description", mapBackup.get("description"));
            retMap.put("fileNames", lstFileName);
            if (abort.equals("1")) {
                retMap.put("status", "-2"); //busy
                retMap.put("message", "備份被中斷。");
            } else {
                retMap.put("status", "1");
            }
        } else {
            retMap.put("status", "-1"); //busy
            retMap.put("message", "備份中, 無法再進行備份。"); 
        }
        return retMap;
    }
    
    public Map<String, Object> backupEntry(String backupPath, int mode, boolean newest) {
        //------------
        int abortValue=0;
        java.util.Date update = Utility.detectDate("1990-01-01");
        String[][] tbName = {
                {"MR", "ID", "UPDATE_AT", "1"},
//                {"MR_CHECKED", "ID", "UPDATE_AT", "1"},
                {"IP_D", "ID", "UPDATE_AT", "2"},
                {"IP_P", "ID", "UPDATE_AT", "2"},
                {"IP_T", "ID", "UPDATE_AT", "2"},
                {"OP_D", "ID", "UPDATE_AT", "3"},
                {"OP_P", "ID", "UPDATE_AT", "3"},
                {"OP_T", "ID", "UPDATE_AT", "3"}
            };
        if (newest) {
            String lastDate = webConfigDao.getConfigValue("backup_last_date");
            if (lastDate.length() > 0) {
                update = Utility.detectDate(lastDate);
            } 
        }

        java.util.List<Map<String, Object>> lstDescription = new java.util.ArrayList<Map<String, Object>>();
        java.util.List<String> lstFileName = new java.util.ArrayList<String>();
        long totalCount = 0;
        String abort="";
        int tbMode;
        for (int idx=0; idx<tbName.length; idx++) {
            tbMode = Integer.parseInt(tbName[idx][3]);
            if (mode==0 || tbMode==mode) {
                abort = webConfigDao.getConfigValue("backup_abort");
                if (abort.equals("1")) {
                    abortValue = 1;
                    break;
                } else {
                    if (!newest) {
                        update = new java.util.Date(0l);
                    } 
                    java.util.Map<String, Object> backupResult = backupTable(backupPath, tbName[idx][0],tbName[idx][1], tbName[idx][2], update);
                    String fileName = backupResult.get("fileName").toString();
                    if (fileName.length()>0) {
                        lstFileName.add(fileName);
                    }
                    java.util.Map<String, Object> mapDescription = new java.util.HashMap<String, Object>();
                    mapDescription.put("table", tbName[idx][0]);
                    mapDescription.put("count", backupResult.get("rowCount"));
//                    mapDescription.put("fileName", backupResult.get("fileName").toString());
                    lstDescription.add(mapDescription);
                    totalCount += (long)backupResult.get("rowCount");
                }
            }
        }

        if (newest && !abort.equals("1")) {
            String today = Utility.dateFormat(new java.util.Date(), "yyyy-MM-dd");
            webConfigDao.setConfig("backup_last_date", today, "");
        }
        java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
        retMap.put("fileNames", lstFileName);
        retMap.put("description", lstDescription);
        retMap.put("count", totalCount);
        retMap.put("abort", abortValue);
        return retMap;
    }
    
    public java.util.Map<String, Object> backupTable(String path, String tableName, String idName, String updateName, java.util.Date update) {
        long rowCount = 0;
        long step = 10000;
        String delimiter = ",";
        String abort;
        java.util.List<String> lstData = new java.util.LinkedList<String>();
        java.util.Map<String, Long> mapRange = dbBackupDao.getTableIdRange(tableName, idName);
        System.out.println(mapRange);
        long start = mapRange.get("min_id");
//        mapRange.put("max_id", 20000l); //test! test! test!
        String fileName = path+tableName+".txt";
        while (start<=mapRange.get("max_id")) {
//            lstData.clear();
            java.util.List<Map<String, Object>> lstRow = dbBackupDao.findData(tableName, idName, start, start+step-1, updateName, update);
            if (lstRow.size() > 0) {
                if (start == mapRange.get("min_id")) { // 處理 Header
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
    

    public int zipFiles(String fileName, java.util.List<String> srcFiles) {
        int ret = 0;
//        List<String> srcFiles = Arrays.asList("test1.txt", "test2.txt");
        try {
            java.io.FileOutputStream fos = new java.io.FileOutputStream(fileName);
            java.util.zip.ZipOutputStream zipOut = new java.util.zip.ZipOutputStream(fos);
            for (String srcFile : srcFiles) {
                java.io.File fileToZip = new java.io.File(srcFile);
                java.io.FileInputStream fis = new java.io.FileInputStream(fileToZip);
                java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(fileToZip.getName());
                zipOut.putNextEntry(zipEntry);
    
                byte[] bytes = new byte[4096];
                int length;
                while((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                fis.close();
                ret++;
            }
            zipOut.close();
            fos.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } 
        catch(java.io.IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    public int saveSetting(BackupSettingDto params) {
        webConfigDao.setConfig("backup_setting", params.toString(), "系統資料備份參數");
        return 1;
    }
    
    public java.util.Map<String, Object> readSetting() {
        String backupSetting = webConfigDao.getConfigValue("backup_setting");
        BasicJsonParser linkJsonParser = new BasicJsonParser();
        return (linkJsonParser.parseMap(backupSetting));
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


}
