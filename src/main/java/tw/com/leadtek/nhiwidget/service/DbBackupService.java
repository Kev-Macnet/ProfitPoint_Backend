package tw.com.leadtek.nhiwidget.service;

import java.io.FileNotFoundException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.sql.DbBackupDao;
import tw.com.leadtek.tools.Utility;

@Service
public class DbBackupService {
//    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DbBackupDao dbBackupDao;
    
    public Map<String, Object> dbBackup(int mode) {
        java.io.File fcurrent = new java.io.File("");
        String currentPath = fcurrent.getAbsolutePath()+"\\";
        String backupPath = currentPath+"backup_data\\";
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
        //------------
        java.util.Date update = Utility.detectDate("2021-08-16");
        String[][] tbName = {
                {"MR", "ID", "UPDATE_AT", "1"},
//                {"MR_CHECKED", "ID", "UPDATE_AT"},
                {"IP_D", "ID", "UPDATE_AT", "2"},
                {"IP_P", "ID", "UPDATE_AT", "2"},
                {"IP_T", "ID", "UPDATE_AT", "2"},
                {"OP_D", "ID", "UPDATE_AT", "2"},
                {"OP_P", "ID", "UPDATE_AT", "2"},
                {"OP_T", "ID", "UPDATE_AT", "2"}
            };
        java.util.List<String> lstFileName = new java.util.ArrayList<String>();
        int ret = 0;
        int tbMode;
        for (int idx=0; idx<tbName.length; idx++) {
            tbMode = Integer.parseInt(tbName[idx][3]);
            if (mode==0 || tbMode==mode) {
                String fileName = backupTable(backupPath, tbName[idx][0],tbName[idx][1], "", update);
                lstFileName.add(fileName);
                ret++;
            }
        }
        
        java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
        retMap.put("zipName", zipName);
        retMap.put("fileNames", lstFileName);
        retMap.put("backupPath", backupPath);
        
        return retMap;
    }
    
    public String backupTable(String path, String tableName, String idName, String updateName, java.util.Date update) {
        int ret = 0;
        long step = 10000;
        String delimiter = ",";
        java.util.List<String> lstData = new java.util.LinkedList<String>();
        java.util.Map<String, Long> mapRange = dbBackupDao.getTableIdRange(tableName, idName);
        System.out.println(mapRange);
        long start = mapRange.get("min_id");
//        mapRange.put("max_id", 50000l); //test! test! test!
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
                }
                for (Map<String, Object>item : lstRow) {
                    ret++;
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
            }
            start += step;
        }
        String fileName = path+tableName+".txt";
        System.out.println("lstData="+lstData.size()+", path="+path);
        Utility.saveToFile(fileName, lstData);
        return (fileName);
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
    
    //------
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
