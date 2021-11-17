package tw.com.leadtek.nhiwidget.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dto.AdditionalConditionPl;
import tw.com.leadtek.nhiwidget.dto.AdditionalContent1Pl;
import tw.com.leadtek.nhiwidget.dto.AdditionalContent2ListPl;
import tw.com.leadtek.nhiwidget.dto.AdditionalContent2Pl;
import tw.com.leadtek.nhiwidget.dto.AdditionalContent3Pl;
import tw.com.leadtek.nhiwidget.dto.AdditionalContent4ListPl;
import tw.com.leadtek.nhiwidget.dto.AdditionalContent4Pl;
import tw.com.leadtek.nhiwidget.dto.AdditionalContent5ListPl;
import tw.com.leadtek.nhiwidget.dto.AdditionalContent5Pl;
import tw.com.leadtek.nhiwidget.dto.AdditionalContent6ListPl;
import tw.com.leadtek.nhiwidget.dto.AdditionalContent6Pl;
import tw.com.leadtek.nhiwidget.dto.AdditionalContent7ListPl;
import tw.com.leadtek.nhiwidget.dto.AdditionalContent7Pl;
import tw.com.leadtek.nhiwidget.sql.AdditionalPointDao;
import tw.com.leadtek.tools.Utility;

@Service
public class AdditionalPointService {
//    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AdditionalPointDao additionalPointDao;
    
    public java.util.Map<String, Object> findList(java.util.Date startDate, java.util.Date endDate, int pageSize, int pageIndex) {
        java.util.List<Map<String, Object>> lst = additionalPointDao.searchAdditionalPoint(0, startDate, endDate);
        if (lst.size()==0) {
            lst = additionalPointDao.searchAdditionalPointByDateRange(0, startDate, endDate);
        }
        java.util.List<Map<String, Object>> data = new java.util.ArrayList<Map<String, Object>>();
        long totalCount = lst.size();
        int start = pageSize*pageIndex;
        for (int a=start; a<start+pageSize; a++) {
            if (a<totalCount) {
                data.add(lst.get(a));
            } else {
                break;
            }
        }
        java.util.Map<String, Object> retMap = new java.util.HashMap<String, Object>();
        retMap.put("total", totalCount);
        retMap.put("data", data);
        return retMap;
    }
    
    public java.util.Map<String, Object> findOne(long ap_id) {
        java.util.Map<String, Object> retMap = additionalPointDao.findAdditionalPoint(ap_id);
        if (!retMap.isEmpty()) {
            long id;
            java.util.List<Map<String, Object>> out1 = additionalPointDao.findOutpatient(ap_id, 1);
            if (out1.size()>0) {
                java.util.Map<String, Object> mapOut1 = out1.get(0);
                retMap.put("outpatient_1", mapOut1);
                mapOut1.put("category", additionalPointDao.findOutpatientField((long)mapOut1.get("id"), 1, "category"));
                mapOut1.remove("id");
                mapOut1.remove("ap_id");
            }
            java.util.List<Map<String, Object>> out2 = additionalPointDao.findOutpatient(ap_id, 2);
            if (out2.size()>0) {
                java.util.Map<String, Object> mapContent = new java.util.LinkedHashMap<String, Object>();
                retMap.put("outpatient_2", mapContent);
                mapContent.put("enable", out2.get(0).get("enable"));
                mapContent.put("data", out2);
                for (Map<String, Object> item : out2) {
                    item.put("cpoe", additionalPointDao.findOutpatientField((long)item.get("id"), 2, "cpoe"));
                    item.remove("id");
                    item.remove("ap_id");
                    item.remove("enable");
                }
            }
            java.util.List<Map<String, Object>> out3 = additionalPointDao.findOutpatient(ap_id, 3);
            if (out3.size()>0) {
                java.util.List<String> data = new java.util.ArrayList<String>(); 
                java.util.Map<String, Object> mapContent = new java.util.LinkedHashMap<String, Object>();
                retMap.put("outpatient_3", mapContent);
                mapContent.put("enable", out3.get(0).get("enable"));
                mapContent.put("nhi_no", data);
                for (Map<String, Object> item : out3) {
                    data.add(item.get("nhi_no").toString());
                }
            }
            java.util.List<Map<String, Object>> out4 = additionalPointDao.findOutpatient(ap_id, 4);
            if (out4.size()>0) {
                java.util.Map<String, Object> mapContent = new java.util.LinkedHashMap<String, Object>();
                retMap.put("outpatient_4", mapContent);
                mapContent.put("enable", out4.get(0).get("enable"));
                mapContent.put("data", out4);
                for (Map<String, Object> item : out4) {
                    id = (long)item.get("id");
                    item.put("category", additionalPointDao.findOutpatientField(id, 4, "category"));
                    item.put("cpoe", additionalPointDao.findOutpatientField(id, 4, "cpoe"));
                    item.put("treatment", additionalPointDao.findOutpatientField(id, 4, "treatment"));
                    item.remove("id");
                    item.remove("ap_id");
                    item.remove("enable");
                }
            }
            java.util.List<Map<String, Object>> out5 = additionalPointDao.findOutpatient(ap_id, 5);
            if (out5.size()>0) {
                java.util.Map<String, Object> mapContent = new java.util.LinkedHashMap<String, Object>();
                retMap.put("outpatient_5", mapContent);
                mapContent.put("enable", out5.get(0).get("enable"));
                mapContent.put("data", out5);
                for (Map<String, Object> item : out5) {
                    id = (long)item.get("id");
                    item.put("cpoe", additionalPointDao.findOutpatientField(id, 5, "cpoe"));
                    item.remove("id");
                    item.remove("ap_id");
                    item.remove("enable");
                }
            }
            java.util.List<Map<String, Object>> out6 = additionalPointDao.findOutpatient(ap_id, 6);
            if (out6.size()>0) {
                java.util.Map<String, Object> mapContent = new java.util.LinkedHashMap<String, Object>();
                retMap.put("outpatient_6", mapContent);
                mapContent.put("enable", out6.get(0).get("enable"));
                mapContent.put("data", out6);
                for (Map<String, Object> item : out6) {
                    id = (long)item.get("id");
                    item.put("category", additionalPointDao.findOutpatientField(id, 6, "category"));
                    item.put("cpoe", additionalPointDao.findOutpatientField(id, 6, "cpoe"));
                    item.put("plan", additionalPointDao.findOutpatientField(id, 6, "plan"));
                    item.remove("id");
                    item.remove("ap_id");
                    item.remove("enable");
                }
            }
            java.util.List<Map<String, Object>> out7 = additionalPointDao.findOutpatient(ap_id, 7);
            if (out7.size()>0) {
                java.util.Map<String, Object> mapContent = new java.util.LinkedHashMap<String, Object>();
                retMap.put("outpatient_7", mapContent);
                mapContent.put("enable", out7.get(0).get("enable"));
                mapContent.put("data", out7);
                for (Map<String, Object> item : out7) {
                    id = (long)item.get("id");
                    item.put("trial", additionalPointDao.findOutpatientField(id, 7, "trial"));
                    item.put("plan", additionalPointDao.findOutpatientField(id, 7, "plan"));
                    item.remove("id");
                    item.remove("ap_id");
                    item.remove("enable");
                }
            }
            //---
            java.util.List<Map<String, Object>> inp1 = additionalPointDao.findInpatient(ap_id, 1);
            if (inp1.size()>0) {
                java.util.Map<String, Object> mapInp1 = inp1.get(0);
                retMap.put("inpatient_1", mapInp1);
                mapInp1.put("category", additionalPointDao.findInpatientField((long)mapInp1.get("id"), 1, "category"));
                mapInp1.remove("id");
                mapInp1.remove("ap_id");
            }
            java.util.List<Map<String, Object>> inp2 = additionalPointDao.findInpatient(ap_id, 2);
            if (inp2.size()>0) {
                java.util.Map<String, Object> mapContent = new java.util.LinkedHashMap<String, Object>();
                retMap.put("inpatient_2", mapContent);
                mapContent.put("enable", inp2.get(0).get("enable"));
                mapContent.put("data", inp2);
                for (Map<String, Object> item : inp2) {
                    item.put("cpoe", additionalPointDao.findInpatientField((long)item.get("id"), 2, "cpoe"));
                    item.remove("id");
                    item.remove("ap_id");
                    item.remove("enable");
                }
            }
            java.util.List<Map<String, Object>> inp3 = additionalPointDao.findInpatient(ap_id, 3);
            if (inp3.size()>0) {
                java.util.List<String> data = new java.util.ArrayList<String>(); 
                java.util.Map<String, Object> mapContent = new java.util.LinkedHashMap<String, Object>();
                retMap.put("inpatient_3", mapContent);
                mapContent.put("enable", inp3.get(0).get("enable"));
                mapContent.put("nhi_no", data);
                for (Map<String, Object> item : inp3) {
                    data.add(item.get("nhi_no").toString());
                }
            }
            java.util.List<Map<String, Object>> inp6 = additionalPointDao.findInpatient(ap_id, 6);
            if (inp6.size()>0) {
                java.util.Map<String, Object> mapContent = new java.util.LinkedHashMap<String, Object>();
                retMap.put("inpatient_6", mapContent);
                mapContent.put("enable", inp6.get(0).get("enable"));
                mapContent.put("data", inp6);
                for (Map<String, Object> item : inp6) {
                    id = (long)item.get("id");
                    item.put("category", additionalPointDao.findInpatientField(id, 6, "category"));
                    item.put("cpoe", additionalPointDao.findInpatientField(id, 6, "cpoe"));
                    item.put("plan", additionalPointDao.findInpatientField(id, 6, "plan"));
                    item.remove("id");
                    item.remove("ap_id");
                    item.remove("enable");
                }
            }
        }
        return retMap;
    }
    
    public long addAdditionalCondition(AdditionalConditionPl params) {
        java.util.Date startDate = Utility.detectDate(params.getStart_date());
        java.util.Date endDate = Utility.detectDate(params.getEnd_date());
        
        long newId = additionalPointDao.addAdditionalPoint(params.getActive(), params.getSyear(), startDate, endDate);
        if (newId>0) {
            AdditionalContent1Pl out1 = params.getOutpatient_1();
            if (out1 != null) {
              additionalPointDao.addOutpatient_1(newId, out1.getEnable(), out1.getCategory());
            }
            AdditionalContent2Pl out2 = params.getOutpatient_2();
            if (out2 != null) {
                for (AdditionalContent2ListPl item : out2.getData()) {
                    additionalPointDao.addOutpatient_2(newId, out2.getEnable(), item.getNhi_no(), item.getCpoe());
                }
            }
            AdditionalContent3Pl out3 = params.getOutpatient_3();
            if (out3 != null) {
                for (String nhi_no : out3.getNhi_no()) {
                    additionalPointDao.addOutpatient_3(newId, out3.getEnable(), nhi_no);
                }
            }

            AdditionalContent4Pl out4 = params.getOutpatient_4();
            if (out4 != null) {
                for (AdditionalContent4ListPl item : out4.getData()) {
                    additionalPointDao.addOutpatient_4(newId, out4.getEnable(), item.getNhi_no(), item.getCategory(), 
                            item.getCpoe(), item.getTreatment());
                }
            }

            AdditionalContent5Pl out5 = params.getOutpatient_5();
            if (out5 != null) {
                for (AdditionalContent5ListPl item : out5.getData()) {
                    additionalPointDao.addOutpatient_5(newId, out5.getEnable(), item.getIcd_no(), item.getNhi_no(), item.getCpoe());
                }
            }

            AdditionalContent6Pl out6 = params.getOutpatient_6();
            if (out6 != null) {
                for (AdditionalContent6ListPl item : out6.getData()) {
                    additionalPointDao.addOutpatient_6(newId, out6.getEnable(), item.getNhi_no(), item.getCategory(), 
                            item.getCpoe(), item.getPlan());
                }
            }

            AdditionalContent7Pl out7 = params.getOutpatient_7();
            if (out7 != null) {
                for (AdditionalContent7ListPl item : out7.getData()) {
                    additionalPointDao.addOutpatient_7(newId, out7.getEnable(), item.getNhi_no(), item.getTrial(), item.getPlan());
                }
            }
            //--
            AdditionalContent1Pl inp1 = params.getInpatient_1();
            if (inp1 != null) {
              additionalPointDao.addInpatient_1(newId, inp1.getEnable(), inp1.getCategory());
            }
            
            AdditionalContent2Pl inp2 = params.getInpatient_2();
            if (inp2 != null) {
                for (AdditionalContent2ListPl item : inp2.getData()) {
                    additionalPointDao.addInpatient_2(newId, inp2.getEnable(), item.getNhi_no(), item.getCpoe());
                }
            }
            
            AdditionalContent3Pl inp3 = params.getInpatient_3();
            if (inp3 != null) {
                for (String nhi_no : inp3.getNhi_no()) {
                    additionalPointDao.addInpatient_3(newId, inp3.getEnable(), nhi_no);
                }
            }
            
            AdditionalContent6Pl inp6 = params.getInpatient_6();
            if (inp6 != null) {
                for (AdditionalContent6ListPl item : inp6.getData()) {
                    additionalPointDao.addInpatient_6(newId, inp6.getEnable(), item.getNhi_no(), item.getCategory(), 
                            item.getCpoe(), item.getPlan());
                }
            }
        }
        return newId;
    }
    
    public int updateAdditionalCondition(long id, AdditionalConditionPl params) {
        java.util.Date startDate = Utility.detectDate(params.getStart_date());
        java.util.Date endDate = Utility.detectDate(params.getEnd_date());
        
        int ret=0;
        if (id > 0) {
            ret = additionalPointDao.updateAdditionalPoint(id, params.getActive(), params.getSyear(), startDate, endDate);
//            System.out.println("update-ret="+ret);
            if (ret > 0) {
                AdditionalContent1Pl out1 = params.getOutpatient_1();
                if (out1 != null) {
                    additionalPointDao.delOutpatient_1(id);
                    ret +=additionalPointDao.addOutpatient_1(id, out1.getEnable(), out1.getCategory());
                }
                
                AdditionalContent2Pl out2 = params.getOutpatient_2();
                if (out2 != null) {
                    additionalPointDao.delOutpatient_2(id);
                    for (AdditionalContent2ListPl item : out2.getData()) {
                        additionalPointDao.addOutpatient_2(id, out2.getEnable(), item.getNhi_no(), item.getCpoe());
                    }
                }
                
                AdditionalContent3Pl out3 = params.getOutpatient_3();
                if (out3 != null) {
                    additionalPointDao.delOutpatient_3(id);
                    for (String nhi_no : out3.getNhi_no()) {
                        additionalPointDao.addOutpatient_3(id, out3.getEnable(), nhi_no);
                    }
                }
                
                AdditionalContent4Pl out4 = params.getOutpatient_4();
                if (out4 != null) {
                    additionalPointDao.delOutpatient_4(id);
                    for (AdditionalContent4ListPl item : out4.getData()) {
                        additionalPointDao.addOutpatient_4(id, out4.getEnable(), item.getNhi_no(), item.getCategory(), 
                                item.getCpoe(), item.getTreatment());
                    }
                }

                AdditionalContent5Pl out5 = params.getOutpatient_5();
                if (out5 != null) {
                    additionalPointDao.delOutpatient_5(id);
                    for (AdditionalContent5ListPl item : out5.getData()) {
                        additionalPointDao.addOutpatient_5(id, out5.getEnable(), item.getNhi_no(), item.getNhi_no(), item.getCpoe());
                    }
                }
                
                AdditionalContent6Pl out6 = params.getOutpatient_6();
                if (out6 != null) {
                    additionalPointDao.delOutpatient_6(id);
                    for (AdditionalContent6ListPl item : out6.getData()) {
                        additionalPointDao.addOutpatient_6(id, out6.getEnable(), item.getNhi_no(), item.getCategory(), 
                                item.getCpoe(), item.getPlan());
                    }
                }

                AdditionalContent7Pl out7 = params.getOutpatient_7();
                if (out7 != null) {
                    additionalPointDao.delOutpatient_7(id);
                    for (AdditionalContent7ListPl item : out7.getData()) {
                        additionalPointDao.addOutpatient_7(id, out7.getEnable(), item.getNhi_no(), item.getTrial(), item.getPlan());
                    }
                }
                //--
                AdditionalContent1Pl inp1 = params.getInpatient_1();
                if (inp1 != null) {
                    additionalPointDao.delInpatient_1(id);
                    ret +=additionalPointDao.addInpatient_1(id, inp1.getEnable(), inp1.getCategory());
                }
                
                AdditionalContent2Pl inp2 = params.getInpatient_2();
                if (inp2 != null) {
                    additionalPointDao.delInpatient_2(id);
                    for (AdditionalContent2ListPl item : inp2.getData()) {
                        additionalPointDao.addInpatient_2(id, inp2.getEnable(), item.getNhi_no(), item.getCpoe());
                    }
                }

                AdditionalContent3Pl inp3 = params.getInpatient_3();
                if (inp3 != null) {
                    additionalPointDao.delInpatient_3(id);
                    for (String nhi_no : inp3.getNhi_no()) {
                        additionalPointDao.addInpatient_3(id, inp3.getEnable(), nhi_no);
                    }
                }
                
                AdditionalContent6Pl inp6 = params.getInpatient_6();
                if (inp6 != null) {
                    additionalPointDao.delInpatient_6(id);
                    for (AdditionalContent6ListPl item : inp6.getData()) {
                        additionalPointDao.addInpatient_6(id, inp6.getEnable(), item.getNhi_no(), item.getCategory(), 
                                item.getCpoe(), item.getPlan());
                    }
                }
            }
        }
        return ret;
    }


    public int deleteAdditionalCondition(long id) {
        int ret=0;
        if (id > 0) {
            ret += additionalPointDao.delOutpatient_1(id);
            ret += additionalPointDao.delOutpatient_2(id);
            ret += additionalPointDao.delOutpatient_3(id);
            ret += additionalPointDao.delOutpatient_4(id);
            ret += additionalPointDao.delOutpatient_5(id);
            ret += additionalPointDao.delOutpatient_6(id);
            ret += additionalPointDao.delOutpatient_7(id);
            ret += additionalPointDao.delInpatient_1(id);
            ret += additionalPointDao.delInpatient_2(id);
            ret += additionalPointDao.delInpatient_3(id);
            ret += additionalPointDao.delInpatient_6(id);
            ret += additionalPointDao.deleteAdditionalPoint(id);
        }
        return ret;
    }
}
