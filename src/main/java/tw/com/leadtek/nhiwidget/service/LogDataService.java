package tw.com.leadtek.nhiwidget.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.sql.LogDataDao;
import tw.com.leadtek.tools.Utility;

// swagger: http://127.0.0.1:8081/swagger-ui/index.html
@Service
public class LogDataService {
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Value("${server.port}")
    private String serverPort;


    @Autowired
    private LogDataDao logDataDao;

    public java.util.Map<String, Object> calcDrgSource(String idCard, String in_date) {
        int appl_dot;
        String card_seq_no="";
        long sn=0;
        String gender =  idCard.substring(1, 2);
        if (gender.equals("1")) {
            gender="M";
        } else {
            gender="F";
        }
        java.util.Set<Map<String, Object>> icdcmSet = new java.util.HashSet<Map<String, Object>>();
        
        java.util.List<Map<String, Object>> lstIPD = logDataDao.find_IP_D(idCard, in_date.trim());
        for (Map<String, Object> item : lstIPD) {
            if (item.get("APPL_DOT")==null) {
                appl_dot = 0;
            } else {
                appl_dot = (int)item.get("APPL_DOT");
            }
            if (item.get("CARD_SEQ_NO")!=null && card_seq_no.length()==0) {
                card_seq_no = (String)item.get("CARD_SEQ_NO");
                sn= (long)item.get("sn")-900000;
            }
            
            for (int a=1; a<=5; a++) {
                if (item.get("ICD_CM_"+a)!=null) {
                    String icd_cm_original = item.get("ICD_CM_"+a).toString();
                    String icd_cm_str = icd_cm_original.replaceAll("\\.", "");
                    java.util.Map<String, Object> ele = new java.util.HashMap<String, Object>();
                    ele.put("icd_cm", icd_cm_str);
                    ele.put("icd_cm_original", icd_cm_original);
                    ele.put("appl_dot", appl_dot);
                    icdcmSet.add(ele);
                }
            }
        }
        //醫事機構代號    費用年月    身分證號    流水號    性別    
        //入院日期    出生日期    
        //主診斷代碼    次診斷代碼一    次診斷代碼二    次診斷代碼三    次診斷代碼四    
        //主手術代碼    次手術代碼一    次手術代碼二    次手術代碼三    次手術代碼四    
        //轉歸代碼    出院日期    醫療費用    權重別    
        //1.DRG    併發症註記    MDC    錯誤註記    次診斷代碼五    次診斷代碼六    次診斷代碼七    次診斷代碼八    次診斷代碼九    次診斷代碼十    次診斷代碼十一
        //2.次診斷代碼十二    次診斷代碼十三    次診斷代碼十四    次診斷代碼十五    次診斷代碼十六    次診斷代碼十七    次診斷代碼十八    次診斷代碼十九    次手術代碼五    次手術代碼六    
        //3.次手術代碼七    次手術代碼八    次手術代碼九    次手術代碼十    次手術代碼十一    次手術代碼十二    次手術代碼十三    次手術代碼十四    次手術代碼十五    次手術代碼十六    
        //4.次手術代碼十七    次手術代碼十八    次手術代碼十九    檔案格式錯誤備註
        String field[][]={{"HOSP_ID"},{"FEE_YM","ROC"},{"ROC_ID"},{"SN"},{"gender"},
                 {"IN_DATE","ROC"},{"ID_BIRTH_YMD","ROC"},
                 {"icd_cm"},{"ICD_CM_2"},{"ICD_CM_3"},{"ICD_CM_4"},{"ICD_CM_5"},
                 {""},{""},{""},{""},{""},
                 {"TRAN_CODE"},{"OUT_DATE","ROC"},{"appl_dot"},{"QWEIGHT"}};
        java.util.List<String> drgList = new java.util.LinkedList<String>();
        java.util.Map<String, Object> rowData;
        for (Map<String, Object> item : icdcmSet) {
            rowData = lstIPD.get(0);
            calcICD_CM_2345(item.get("icd_cm").toString(), icdcmSet, rowData);
            StringBuffer strBuffer = new StringBuffer(); 
            for (int a=0; a<field.length; a++) {
//                System.out.println(field[a][0]+"-------->");
                if (field[a][0].equals("gender")) {
                    strBuffer.append(gender+",");
                } else if (field[a][0].equals("appl_dot")) {
                    int price = (int) item.get("appl_dot");
                    if (price>0) {
                        strBuffer.append("+"+item.get("appl_dot").toString()+",");
                    } else {
                        strBuffer.append(item.get("appl_dot").toString()+",");
                    }
                    
                } else if (field[a][0].equals("icd_cm")) {
                    strBuffer.append(item.get("icd_cm").toString()+",");
                } else if (field[a][0].length()>0) {
                    if (field[a].length==2) {
                        if (field[a][1].equals("ROC")) {
                            strBuffer.append(rocDateToDate(rowData.get(field[a][0]).toString())+",");
                        } else {
                            strBuffer.append(rowData.get(field[a][0]).toString()+",");
                        }
                    } else {
                        if (rowData.get(field[a][0]) != null) {
                            strBuffer.append(rowData.get(field[a][0]).toString()+",");
                        } else {
                            strBuffer.append(",");
                        }
                    }
                } else {
                    strBuffer.append(",");
                }
            }
            for (int a=0; a<33; a++) {
                strBuffer.append(",");
            }
            drgList.add(strBuffer.toString());
        }
        
        java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
        retMap.put("card_seq_no", card_seq_no);
        retMap.put("sn", sn);
        retMap.put("icd_cm", icdcmSet);
        retMap.put("drg_list", drgList);
        return retMap;
    }
    
    
    public String rocDateToDate(String rocDate) {
        String ret;
        String yy, mm, dd;
        int yearAdd = 1911;
        if (serverPort.equals("8081")) {
//            yearAdd += 2; //測試資料才可以過
        }
        if (rocDate.length()==5) {
            yy = rocDate.substring(0, 3);
            mm = rocDate.substring(3, 5);
            ret = Integer.toString(Integer.parseInt(yy)+yearAdd)+mm;
        } else if (rocDate.length()==6) {
            yy = rocDate.substring(0, 2);
            mm = rocDate.substring(2, 4);
            dd = rocDate.substring(4, 6);
            ret = Integer.toString(Integer.parseInt(yy)+yearAdd)+mm+dd;
        } else if (rocDate.length()==7) {
            yy = rocDate.substring(0, 3);
            mm = rocDate.substring(3, 5);
            dd = rocDate.substring(5, 7);
            ret = Integer.toString(Integer.parseInt(yy)+yearAdd)+mm+dd;
        } else {
            yy =  mm = dd ="";
            ret ="";
        }
        return ret;
    }
    
    public void calcICD_CM_2345(String cur_icd_cm, java.util.Set<Map<String, Object>> icdSet, java.util.Map<String, Object> rowData) {
        int idx=2;
        String icd_cm; 
        for (Map<String, Object> item : icdSet) {
            icd_cm = item.get("icd_cm").toString();
            if (!cur_icd_cm.equals(icd_cm)) {
                rowData.put("ICD_CM_"+idx, icd_cm);
                idx++;
                if (idx>5) {
                    break;
                }
            } 
        }
    }
    
    /*
    public boolean deleteFile(String fName) {
        java.io.File f = new java.io.File(fName);
        if (f.exists()) {
            f.delete();
            return (true);
        } else {
            return (false);
        } 
    }

    public boolean saveToFile(String fileName, java.util.List<String> lstData) {
        try {
            java.io.File fp = new java.io.File(fileName);
             java.io.BufferedWriter bwr = new java.io.BufferedWriter(new java.io.FileWriter(fp));
             for (String str : lstData) {
                 bwr.write(str+"\r\n");
             }
             bwr.flush();
             bwr.close();
             return true;
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    

    public java.util.List<String> loadFromFile(String fileName) {
      java.util.List<String> buffer = new java.util.LinkedList<String>();
      try {
          java.io.FileInputStream fis = new java.io.FileInputStream(fileName);
          java.io.BufferedReader isReader = new java.io.BufferedReader(new java.io.InputStreamReader(fis, "big5"));
          String str;
          while((str = isReader.readLine()) != null) {
              buffer.add(str);
          }
          isReader.close();
          fis.close();
      } catch(java.io.IOException e) {
          e.printStackTrace();
      }
      return buffer;
    }
    */

    public String execBatch(String pyCommand) {
        String[] arrCommand = pyCommand.split(" ");
        String[] arguments = new String[arrCommand.length];
        for (int a=0; a<arrCommand.length; a++) {
            arguments[a] = arrCommand[a];
        }
        StringBuffer sBuffer = new StringBuffer();
        try {
          Process process = Runtime.getRuntime().exec(arguments); 
          int exitCode = process.waitFor();
          logger.info("execBatch status="+exitCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sBuffer.toString();
    }

    public String getErrorMessage(String err) {
        String errCode[][] = {
                {"Z","無效的代碼"},
                {"A","違反編碼原則(V00-Y99外因碼不得為主診斷)"},
                {"X","健保不給付（若出現於主診斷，才會出現錯誤訊息，無法繼續分類；若出現於次診斷照常編審；若出現於任一手術碼，不論是否為唯一手術碼，該碼忽略後列T，仍可以健保給付之手術碼繼續分類，惟醫院應將健保不給付部分費用扣除後申報）"},
                {"H","限新生兒（入院年月減出生年月<12個月)"},
                {"I","限0-17歲"},
                {"K","限14歲以上"},
                {"U","限早產兒<3個月"},
                {"W","限早產兒>=3個月"},
                {"M","限男性"},
                {"F","限女性"},
                {"S","性別不確定"},
                {"O","不允許為主診斷"},
                {"N","不能為主診斷"},
                {"V","無次診斷時,此碼不可為主診斷"},
                {"E","主診斷不應空白"},
                {"R","有次手術,主手術不應空白(不影響編審)"}
        };
        String message = "";
        for (int a=0; a<errCode.length; a++) {
            if (err.toUpperCase().equals(errCode[a][0])) {
                message = errCode[a][1];
            }
        }
        return message;
    }
    
    public java.util.Set<String> parseErrorMessage(String err) {
        String code, msg;
        java.util.Set<String> retSet = new java.util.HashSet<String>();
        for (int a=0; a<err.length()-2; a++) {
            code =  err.substring(a, a+1);
            msg = getErrorMessage(code);
            if (msg.length()>0) {
                retSet.add(msg);
            }
        }
        return retSet;
    }
    
    
    public java.util.Map<String, Object> parseDrgResult(long sn, String card_seq_no, java.util.List<String> lstData, java.util.List<?> lst_icd_cm) {
        java.util.Map<String, Object> retMap = new java.util.LinkedHashMap<String, Object>();
        retMap.put("mr_id", sn);
        retMap.put("card_seq_no", card_seq_no);
        String masterField[][]= {{"fee_ym","1","費用年月"},
                {"roc_id","2","身分證號"},
                {"in_date","5","入院日期"},
                {"out_date","18","出院日期"}};
        String detailField[][]= {
                {"icd_cm_1","7","主診斷代碼"},
                {"icd_cm_2","8","次診斷代碼1"},
                {"icd_cm_3","9","次診斷代碼2"},
                {"icd_cm_4","10","次診斷代碼3"},
                {"icd_cm_5","11","次診斷代碼4"},
                {"appl_dot","19","費用"},
                {"drg_code","21","DRG 碼"},
                {"complication","22","併發症註記"},
                {"mdc_code","23","MDC"},
                {"error_code","24","錯誤碼"}
            };
        if (lstData.size()>1) {
            String masterRow[] = lstData.get(1).split(",");
            for (int a=0; a<masterField.length; a++) {
                retMap.put(masterField[a][0], masterRow[Integer.parseInt(masterField[a][1])]);
            }
            java.util.List<Map<String, Object>> lstIcd = new java.util.ArrayList<Map<String, Object>>(); 
            retMap.put("data", lstIcd);
            
            int idx=0;
            for (String row : lstData) {
                idx++;
                if (idx<=1) {
                    continue;
                }
                String ele[] = row.split(",");
//                for (int a=0; a<ele.length; a++) {
//                    System.out.println(a+"/"+ele[a]);
//                    logger.info(a+"/"+ele[a]);
//                }
                java.util.Map<String, Object> map = new java.util.LinkedHashMap<String, Object>();
                for (int a=0; a<detailField.length; a++) {
                    if (detailField[a][0].indexOf("icd_cm_")>=0) {
                        String icd = ele[Integer.parseInt(detailField[a][1])];
                        if (icd.length()>0) {
                            java.util.Map<String, Object> mapIcd =  findMapData(lst_icd_cm, "icd_cm", icd);
                            if (mapIcd != null) {
                                icd = mapIcd.get("icd_cm_original").toString();
                            }
                        }
                        map.put(detailField[a][0], icd);
                    } else {
                        map.put(detailField[a][0], ele[Integer.parseInt(detailField[a][1])]);
                    }
                }
                if (ele.length>=56) {//55
                    java.util.List<String> lst = new java.util.ArrayList<String>();
                    lst.add(ele[55].trim());
                    map.put("error_message", lst);
                } else {
                    map.put("error_message", parseErrorMessage(map.get("error_code").toString()));
                }
                map.put("rw", logDataDao.getDrgRw(map.get("drg_code").toString(), retMap.get("in_date").toString(), retMap.get("out_date").toString()));
                lstIcd.add(map);
            }
        }
        return retMap;
    }


    public java.util.Map<String, Object> drgProcess(String idCard, String in_date) {
        java.util.Map<String, Object> retMap;
        java.util.Map<String, Object> drgSource = calcDrgSource(idCard, in_date);
        
        java.util.List<String> drg_list = (java.util.List<String>)drgSource.get("drg_list");
        String card_seq_no = (String) drgSource.get("card_seq_no");
        long sn = (long) drgSource.get("sn");
        //java.util.Set<Map<String, Object>> icdcmSet = new java.util.HashSet<Map<String, Object>>
        //java.util.List<Map<String, Object>> icd_cm = (java.util.Set) drgSource.get("icd_cm");
        java.util.List<Map> lst_icd_cm = setToList((java.util.Set<Map>)drgSource.get("icd_cm"));
        if (drg_list.size()>0) {
            String path = "drg_data";
            long fname = new java.util.Date().getTime();
            String sourceName = path+"/"+fname+".txt";
            String targetName = path+"/"+fname+"-b.txt";
            Utility.saveToFile(sourceName, drg_list, false);
            String pyCommand = path+"/DRG.BAT "+fname+".txt"+" "+fname+"-b.txt";
//            String pyCommand = path+"/DRG.BAT 20210405030645A-Test.txt"+" "+fname+"-b.txt";   //for Test
            System.out.println("exec="+pyCommand);
            execBatch(pyCommand);
            java.util.List<String>lstResult = Utility.loadFromFile(targetName);
            retMap = parseDrgResult(sn, card_seq_no, lstResult, lst_icd_cm);
            int wrCnt = writeDrgResult(retMap);
            Utility.deleteFile(sourceName);
            Utility.deleteFile(targetName);
        } else {
            retMap = new java.util.HashMap<String, Object>(); 
        }
        return retMap;
    }
    
    public int writeDrgResult(java.util.Map<String, Object> drgResult) {
        int ret = 0;
        String drg_code, error;
        long mr_id = (long)drgResult.get("mr_id");
        long appl_dot;
        logDataDao.del_DRG_CAL(mr_id);
        java.util.List<Map<String, Object>> lstDrgData = (java.util.List<Map<String, Object>>)drgResult.get("data");
        for (Map<String, Object> item: lstDrgData) {
            if (item.get("appl_dot")!=null) {
                appl_dot = strToLong(item.get("appl_dot").toString());
            } else {
                appl_dot = 0;
            }
            drg_code = item.get("drg_code").toString();
            error = item.get("error_code").toString().replaceAll("00", "");
            if ((drg_code.length()==0)&&(error.length()==0)) {
                error= "0(AP過期)";
            }

            ret += logDataDao.add_DRG_CAL(mr_id, item.get("icd_cm_1").toString(), appl_dot, 
                    (double)item.get("rw"), drg_code, 
                    item.get("complication").toString(), 
                    item.get("mdc_code").toString(), error, "",  0,  0);
        }
        return ret;
    }

    public int createDrgBatchFile(String drgPath, String drgExe) {
        int ret = -1;
        String arrDrgPath[]=drgPath.split(":");
        if (arrDrgPath.length==2) {
            java.io.File fcurrent = new java.io.File("");
            String currentPath = fcurrent.getAbsolutePath()+"\\";
            String workPath = currentPath+"drg_data\\";
            java.io.File fwork = new java.io.File(workPath);
            if (!fwork.exists()) { 
                fwork.mkdirs();
            }
            java.util.List<String> lstData = new java.util.ArrayList<String>();
            lstData.add("@echo off");
            lstData.add(arrDrgPath[0]+":");
            lstData.add(String.format("cd \"%s\"", arrDrgPath[1]));
            String cmdStr=String.format("%s \"%s%%1\" \"%s%%2\" \"y\"", drgExe, workPath, workPath);
            lstData.add(cmdStr);
            String arrWorkPath[]=workPath.split(":");
            lstData.add(arrWorkPath[0]+":");
            lstData.add(String.format("cd \"%s\"", arrWorkPath[1]));
            Utility.saveToFile(workPath+"DRG.BAT", lstData, false);
            ret = 0;
        }

        return ret;
    }
    
    
    private java.util.List<Map> setToList(java.util.Set<Map> setData) {
        java.util.List<Map> lst = new java.util.LinkedList<>();
        for (Map item: setData) {
            lst.add(item);
        }
        return lst;
    }
    
    private java.util.Map<String, Object> findMapData(java.util.List<?> list, String key, String value) {
        java.util.Map<String, Object> retMap = null;
        String v; 
        for (Map<String, Object> temp: (java.util.List<Map<String, Object>>)list) {
            if (temp.get(key)!=null) {
                v=temp.get(key).toString();
                if (value.equals(v)) {
                    retMap=temp;
                    break;
                }
            }  else {
                //System.out.println(temp);
            }
        }
        return (retMap);
    }

    private long strToLong(String str) {
        if (str==null)
            return (0); 
        else {
            try {
                long ret =0;
                if (str.length()>0) {
                    ret = Long.parseLong(str);
                }
                return (ret);
            } catch (Exception e) {
                return (0);
            }
        }
    }
    
    //== log ===
    public String getMapStr(java.util.Map<String, Object> map, String key) {
        String ret = null;
        if (map.get(key)!=null) {
            ret =  map.get(key).toString();
        }
        return (ret);
    }

    
    private java.util.Set<Map<String, Object>> compareData(java.util.Map<String, Object> row1, java.util.Map<String, Object> row2) {
        java.util.Set<Map<String, Object>> retSet = new java.util.HashSet<Map<String, Object>>();
        String key, val, val2;
        int modifyCnt = 0;
        if (!row1.isEmpty()) {
            for (java.util.Map.Entry<String, Object> entry : row1.entrySet()) {
                key = entry.getKey();
                if (entry.getValue()!=null) {
                    val = entry.getValue().toString();
                } else {
                    val = null;
                }
                if (row2.get(key)!=null) {
                    val2 = row2.get(key).toString();
                } else {
                    val2 = null;
                }
//                System.out.println("key, "+key+"="+val+"/"+val2);
                int equal;
                if ((val==null)&&(val2==null)) {
                    equal = 1;
                } else if(val==null || val2==null) {
                    equal = 0;
                    modifyCnt++;
                } else if(val.equals(val2)) {
                    equal = 1;
                } else {
                    equal = 0;
                    modifyCnt++;
                }
                java.util.Map<String, Object> map = new java.util.LinkedHashMap<String, Object>();
                map.put("field", key);
                map.put("source", val);
                map.put("modify", val2);
                map.put("equal", equal);
                retSet.add(map);
            }
        }
        //---
        if (!row2.isEmpty()) {
            for (java.util.Map.Entry<String, Object> entry : row2.entrySet()) {
                key = entry.getKey();
                if (entry.getValue()!=null) {
                    val2 = entry.getValue().toString();
                } else {
                    val2 = null;
                }
                if (row1.get(key)!=null) {
                    val = row1.get(key).toString();
                } else {
                    val = null;
                }
//                System.out.println("key2, "+key+"="+val+"/"+val2);
                int equal;
                if ((val==null)&&(val2==null)) {
                    equal = 1;
                } else if(val==null || val2==null) {
                    equal = 0;
                    modifyCnt++;
                } else if(val.equals(val2)) {
                    equal = 1;
                } else {
                    equal = 0;
                    modifyCnt++;
                }
                java.util.Map<String, Object> map = new java.util.LinkedHashMap<String, Object>();
                map.put("field", key);
                map.put("source", val);
                map.put("modify", val2);
                map.put("equal", equal);
                retSet.add(map);
            }
        }
        if (modifyCnt==0) {
            retSet.clear();
        }
//        System.out.println(retSet.size());
        return (retSet);
    }
    
    public java.util.Map<String, Object> makeCondition(String params[][]) { //{"key","val"},{"key","val"}
        java.util.Map<String, Object> condition = new java.util.HashMap<String, Object>();
        for (int a=0; a<params.length; a++) {
            condition.put(params[a][0], params[a][1]);
        }
        return condition;
    }
    
    public java.util.Map<String, Object> findOne(String table, Map<String, Object> condition) {
        return logDataDao.findOne(table, condition);
    }
    
    private java.util.Map<String, Object> conditionString(java.util.Map<String, Object> condition) { //{"key","val"},{"key","val"}
        java.util.Map<String, Object> kvMap = new java.util.HashMap<String, Object>();
        String fields = "";
        String values = "";
        for (java.util.Map.Entry<String, Object> entry : condition.entrySet()) {
            fields += entry.getKey()+";";
            values += entry.getValue()+";";
        }

        if (fields.length()>1) {
            fields = fields.substring(0, fields.length()-1);
        }
        if (values.length()>1) {
            values = values.substring(0, values.length()-1);
        }
        kvMap.put("fields", fields);
        kvMap.put("values", values);
        return kvMap;
    }
    
    public long updateModification(String user, String table, 
            java.util.Map<String, Object> condition,
            java.util.Map<String, Object> row1, java.util.Map<String, Object> row2) {
        java.util.Map<String, Object> condiMap  = conditionString(condition);
//        System.out.println("condiMap----");
//        System.out.println(condiMap);
        java.util.Set<Map<String, Object>> diffSet = compareData(row1, row2);
//        System.out.println(diffSet);
        long newId = 0;
        if (diffSet.size()>0) {
            int mode;
            if (row1.isEmpty()) {
                mode = 1; 
            } else if (row2.isEmpty()) {
                mode = 3;
            } else {
                mode = 2;
            }
            
            newId = logDataDao.addLogData(table, condiMap.get("fields").toString(), condiMap.get("values").toString(), user, mode);
            if (mode!=3 && newId>0) {
                for (Map<String, Object> item : diffSet) {
                    String field, source, modify;
                    field = getMapStr(item, "field");
                    source = getMapStr(item, "source"); 
                    modify = getMapStr(item, "modify");
                    logDataDao.addLogDataDetail(newId, field, source, modify, (int)item.get("equal"));
                }
            }
        }
        return newId;
    }
    
    public int setLogin(String jwt) {
        int ret = -1;
        if (jwt.length()>20) {
            jwt = jwt.replace("Bearer", "");
            String arrJwt[] = jwt.split("\\.");
            String jwtBody = "";
            byte[] jwtBytes = java.util.Base64.getDecoder().decode(arrJwt[1]);
            try {
                jwtBody = new String(jwtBytes, "UTF-8");
            } catch (java.io.UnsupportedEncodingException ex) {
                jwtBody = new String(jwtBytes);
            }
            BasicJsonParser linkJsonParser = new BasicJsonParser();
            Map<String, Object> jwtMap = linkJsonParser.parseMap(jwtBody);
            logDataDao.addSignin(jwtMap.get("sub").toString(), jwt);
            //{ "sub": "test", "uid": 2, "exp": 1627378586 }
            ret = 0;
        }
        return ret;
    }
    
    public int setLogout(String jwt) {
        int ret = -1;
        if (jwt.length()>20) {
            ret = logDataDao.updateSignout(jwt);
        }
        return ret;
    }
    
    // http://127.0.0.1:8081/nhixml/mr?allMatch=Y&sdate=2021%2F07%2F23&edate=2021%2F07%2F23&minPoints=68&maxPoints=90&dataFormat=10,20&funcType=AC&prsnName=&drg=&drgSection=B1&icdAll=&status=3&page=0&perPage=20
    public long updateLogSearch(String user, String allMatch, String startDate, String endDate,
            Integer minPoints, Integer maxPoints, String dataFormat, String funcType, String prsnId,
            String prsnName, String applId, String applName, String inhMrId, String inhClinicId,
            String drg, String drgSection, String orderCode, String inhCode, String drugUse,
            String inhCodeDrugUse, String icdAll, String icdCMMajor, String icdCMSecondary, String icdPCS,
            String qrObject, String qrSdate, String qrEdate, String status, String deductedCode,
            String deductedOrder) {
        long m_id = logDataDao.addLogSearch(user);
        if (m_id>0) {
            logDataDao.addLogSearchDetail(m_id, "allMatch", allMatch);
            logDataDao.addLogSearchDetail(m_id, "startDate", startDate);
            logDataDao.addLogSearchDetail(m_id, "endDate", endDate);
            if (minPoints>0) {
                logDataDao.addLogSearchDetail(m_id, "minPoints", Integer.toString(minPoints));
            }
            if (maxPoints>minPoints) {
                logDataDao.addLogSearchDetail(m_id, "maxPoints", Integer.toString(maxPoints));
            }
            logDataDao.addLogSearchDetail(m_id, "dataFormat", dataFormat);
            logDataDao.addLogSearchDetail(m_id, "funcType", funcType);
            logDataDao.addLogSearchDetail(m_id, "prsnId", prsnId);
            logDataDao.addLogSearchDetail(m_id, "prsnName", prsnName);
            logDataDao.addLogSearchDetail(m_id, "applId", applId);
            logDataDao.addLogSearchDetail(m_id, "applName", applName);
            logDataDao.addLogSearchDetail(m_id, "inhMrId", inhMrId);
            logDataDao.addLogSearchDetail(m_id, "inhClinicId", inhClinicId);
            logDataDao.addLogSearchDetail(m_id, "drg", drg);
            logDataDao.addLogSearchDetail(m_id, "drgSection", drgSection);
            logDataDao.addLogSearchDetail(m_id, "orderCode", orderCode);
            logDataDao.addLogSearchDetail(m_id, "inhCode", inhCode);
            logDataDao.addLogSearchDetail(m_id, "drugUse", drugUse);
            logDataDao.addLogSearchDetail(m_id, "inhCodeDrugUse", inhCodeDrugUse);
            logDataDao.addLogSearchDetail(m_id, "icdAll", icdAll);
            logDataDao.addLogSearchDetail(m_id, "icdCMMajor", icdCMMajor);
            logDataDao.addLogSearchDetail(m_id, "icdCMSecondary", icdCMSecondary);
            logDataDao.addLogSearchDetail(m_id, "icdPCS", icdPCS);
            logDataDao.addLogSearchDetail(m_id, "qrObject", qrObject);
            logDataDao.addLogSearchDetail(m_id, "qrSdate", qrSdate);
            logDataDao.addLogSearchDetail(m_id, "qrEdate", qrEdate);
            logDataDao.addLogSearchDetail(m_id, "status", status);
            logDataDao.addLogSearchDetail(m_id, "deductedCode", deductedCode);
            logDataDao.addLogSearchDetail(m_id, "deductedOrder", deductedOrder);
        }
        return (m_id);
    }
    //====================================
    /*
    public java.util.Set<Map<String, Object>> testModifyData(String user, String table, java.util.List<LogDataPl> params) {
        String[][] arr = new String[params.size()][2];
        int idx=0;
        for (LogDataPl item : params) {
            arr[idx][0] = item.getField();
            arr[idx][1] = item.getValue();
        }
        java.util.Map<String, Object> condition = makeCondition(arr);
        java.util.Map<String, Object> row1 = findOne(table, condition);
        logDataDao.testTest("23");
        java.util.Map<String, Object> row2 = findOne(table, condition);
        java.util.Set<Map<String, Object>> diffSet = updateModification(user, table, condition, row1, row2);
        
        return diffSet;
    }
    */
    
}
