package tw.com.leadtek.nhiwidget.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dto.AdditionalConditionPl;
import tw.com.leadtek.nhiwidget.dto.AdditionalContent1Pl;
import tw.com.leadtek.nhiwidget.dto.AdditionalContent2Pl;
import tw.com.leadtek.nhiwidget.dto.AdditionalContent3Pl;
import tw.com.leadtek.nhiwidget.dto.AdditionalContent4Pl;
import tw.com.leadtek.nhiwidget.dto.AdditionalContent5Pl;
import tw.com.leadtek.nhiwidget.dto.AdditionalContent6Pl;
import tw.com.leadtek.nhiwidget.dto.AdditionalContent7Pl;
import tw.com.leadtek.nhiwidget.sql.AdditionalPointDao;
import tw.com.leadtek.tools.Utility;

@Service
public class AdditionalPointService {
//    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AdditionalPointDao additionalPointDao;
    
    public java.util.List<Map<String, Object>> findList(java.util.Date startDate, java.util.Date endDate) {
        java.util.List<Map<String, Object>> lst = additionalPointDao.searchAdditionalPoint(0, startDate, endDate);
        if (lst.size()==0) {
            lst = additionalPointDao.searchAdditionalPointByDateRange(0, startDate, endDate);
        }
        return lst;
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
                retMap.put("outpatient_2", out2);
                for (Map<String, Object> item : out2) {
                    item.put("cpoe", additionalPointDao.findOutpatientField((long)item.get("id"), 2, "cpoe"));
                    item.remove("id");
                    item.remove("ap_id");
                }
            }
            java.util.List<Map<String, Object>> out3 = additionalPointDao.findOutpatient(ap_id, 3);
            if (out3.size()>0) {
                retMap.put("outpatient_3", out3);
                for (Map<String, Object> item : out3) {
                    item.remove("id");
                    item.remove("ap_id");
                }
            }
            java.util.List<Map<String, Object>> out4 = additionalPointDao.findOutpatient(ap_id, 4);
            if (out4.size()>0) {
                retMap.put("outpatient_4", out4);
                for (Map<String, Object> item : out4) {
                    id = (long)item.get("id");
                    item.put("category", additionalPointDao.findOutpatientField(id, 4, "category"));
                    item.put("cpoe", additionalPointDao.findOutpatientField(id, 4, "cpoe"));
                    item.put("treatment", additionalPointDao.findOutpatientField(id, 4, "treatment"));
                    item.remove("id");
                    item.remove("ap_id");
                }
            }
            java.util.List<Map<String, Object>> out5 = additionalPointDao.findOutpatient(ap_id, 5);
            if (out5.size()>0) {
                retMap.put("outpatient_5", out5);
                for (Map<String, Object> item : out5) {
                    id = (long)item.get("id");
                    item.put("cpoe", additionalPointDao.findOutpatientField(id, 5, "cpoe"));
                    item.remove("id");
                    item.remove("ap_id");
                }
            }
            java.util.List<Map<String, Object>> out6 = additionalPointDao.findOutpatient(ap_id, 6);
            if (out6.size()>0) {
                retMap.put("outpatient_6", out6);
                for (Map<String, Object> item : out6) {
                    id = (long)item.get("id");
                    item.put("category", additionalPointDao.findOutpatientField(id, 6, "category"));
                    item.put("cpoe", additionalPointDao.findOutpatientField(id, 6, "cpoe"));
                    item.put("plan", additionalPointDao.findOutpatientField(id, 6, "plan"));
                    item.remove("id");
                    item.remove("ap_id");
                }
            }
            java.util.List<Map<String, Object>> out7 = additionalPointDao.findOutpatient(ap_id, 7);
            if (out7.size()>0) {
                retMap.put("outpatient_7", out7);
                for (Map<String, Object> item : out7) {
                    id = (long)item.get("id");
                    item.put("trial", additionalPointDao.findOutpatientField(id, 7, "trial"));
                    item.put("plan", additionalPointDao.findOutpatientField(id, 7, "plan"));
                    item.remove("id");
                    item.remove("ap_id");
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
                retMap.put("inpatient_2", inp2);
                for (Map<String, Object> item : inp2) {
                    item.put("cpoe", additionalPointDao.findInpatientField((long)item.get("id"), 2, "cpoe"));
                    item.remove("id");
                    item.remove("ap_id");
                }
            }
            java.util.List<Map<String, Object>> inp3 = additionalPointDao.findInpatient(ap_id, 3);
            if (inp3.size()>0) {
                retMap.put("inpatient_3", inp3);
                for (Map<String, Object> item : inp3) {
                    item.remove("id");
                    item.remove("ap_id");
                }
            }
            java.util.List<Map<String, Object>> inp6 = additionalPointDao.findInpatient(ap_id, 6);
            if (inp6.size()>0) {
                retMap.put("inpatient_6", inp6);
                for (Map<String, Object> item : inp6) {
                    id = (long)item.get("id");
                    item.put("category", additionalPointDao.findInpatientField(id, 6, "category"));
                    item.put("cpoe", additionalPointDao.findInpatientField(id, 6, "cpoe"));
                    item.put("plan", additionalPointDao.findInpatientField(id, 6, "plan"));
                    item.remove("id");
                    item.remove("ap_id");
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
            java.util.List<AdditionalContent2Pl> lstOut2 = params.getOutpatient_2();
            if (lstOut2 != null) {
                for (AdditionalContent2Pl out2 : lstOut2) {
                    additionalPointDao.addOutpatient_2(newId, out2.getEnable(), out2.getNhi_no(), out2.getCpoe());
                }
            }
            java.util.List<AdditionalContent3Pl> lstOut3 = params.getOutpatient_3();
            if (lstOut3 != null) {
                for (AdditionalContent3Pl out3 : lstOut3) {
                    additionalPointDao.addOutpatient_3(newId, out3.getEnable(), out3.getNhi_no());
                }
            }
            java.util.List<AdditionalContent4Pl> lstOut4 = params.getOutpatient_4();
            if (lstOut4 != null) {
                for (AdditionalContent4Pl out4 : lstOut4) {
                    additionalPointDao.addOutpatient_4(newId, out4.getEnable(), out4.getNhi_no(), out4.getCategory(), 
                                out4.getCpoe(), out4.getTreatment());
                }
            }
            java.util.List<AdditionalContent5Pl> lstOut5 = params.getOutpatient_5();
            if (lstOut5 != null) {
                for (AdditionalContent5Pl out5 : lstOut5) {
                    additionalPointDao.addOutpatient_5(newId, out5.getEnable(), out5.getIcd_no(), out5.getNhi_no(), out5.getCpoe());
                }
            }
            java.util.List<AdditionalContent6Pl> lstOut6 = params.getOutpatient_6();
            if (lstOut6 != null) {
                for (AdditionalContent6Pl out6 : lstOut6) {
                    additionalPointDao.addOutpatient_6(newId, out6.getEnable(), out6.getNhi_no(), out6.getCategory(), 
                                out6.getCpoe(), out6.getPlan());
                }
            }
            java.util.List<AdditionalContent7Pl> lstOut7 = params.getOutpatient_7();
            if (lstOut7 != null) {
                for (AdditionalContent7Pl out7 : lstOut7) {
                    additionalPointDao.addOutpatient_7(newId, out7.getEnable(), out7.getNhi_no(), out7.getTrial(), out7.getPlan());
                }
            }
            //--
            AdditionalContent1Pl inp1 = params.getInpatient_1();
            if (inp1 != null) {
              additionalPointDao.addInpatient_1(newId, inp1.getEnable(), inp1.getCategory());
            }
            java.util.List<AdditionalContent2Pl> lstInp2 = params.getInpatient_2();
            if (lstInp2 != null) {
                for (AdditionalContent2Pl inp2 : lstInp2) {
                    additionalPointDao.addInpatient_2(newId, inp2.getEnable(), inp2.getNhi_no(), inp2.getCpoe());
                }
            }
            java.util.List<AdditionalContent3Pl> lstInp3 = params.getInpatient_3();
            if (lstInp3 != null) {
                for (AdditionalContent3Pl inp3 : lstInp3) {
                    additionalPointDao.addInpatient_3(newId, inp3.getEnable(), inp3.getNhi_no());
                }
            }
            java.util.List<AdditionalContent6Pl> lstInp6 = params.getInpatient_6();
            if (lstInp6 != null) {
                for (AdditionalContent6Pl inp6 : lstInp6) {
                    additionalPointDao.addInpatient_6(newId, inp6.getEnable(), inp6.getNhi_no(), inp6.getCategory(), 
                            inp6.getCpoe(), inp6.getPlan());
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
                java.util.List<AdditionalContent2Pl> lstOut2 = params.getOutpatient_2();
                if (lstOut2 != null) {
                    additionalPointDao.delOutpatient_2(id);
                    for (AdditionalContent2Pl out2 : lstOut2) {
                        additionalPointDao.addOutpatient_2(id, out2.getEnable(), out2.getNhi_no(), out2.getCpoe());
                    }
                }
                java.util.List<AdditionalContent3Pl> lstOut3 = params.getOutpatient_3();
                if (lstOut3 != null) {
                    additionalPointDao.delOutpatient_3(id);
                    for (AdditionalContent3Pl out3 : lstOut3) {
                        additionalPointDao.addOutpatient_3(id, out3.getEnable(), out3.getNhi_no());
                    }
                }
                java.util.List<AdditionalContent4Pl> lstOut4 = params.getOutpatient_4();
                if (lstOut4 != null) {
                    additionalPointDao.delOutpatient_4(id);
                    for (AdditionalContent4Pl out4 : lstOut4) {
                        additionalPointDao.addOutpatient_4(id, out4.getEnable(), out4.getNhi_no(), out4.getCategory(), 
                                    out4.getCpoe(), out4.getTreatment());
                    }
                }
                java.util.List<AdditionalContent5Pl> lstOut5 = params.getOutpatient_5();
                if (lstOut5 != null) {
                    additionalPointDao.delOutpatient_5(id);
                    for (AdditionalContent5Pl out5 : lstOut5) {
                        additionalPointDao.addOutpatient_5(id, out5.getEnable(), out5.getNhi_no(), out5.getNhi_no(), out5.getCpoe());
                    }
                }
                java.util.List<AdditionalContent6Pl> lstOut6 = params.getOutpatient_6();
                if (lstOut6 != null) {
                    additionalPointDao.delOutpatient_6(id);
                    for (AdditionalContent6Pl out6 : lstOut6) {
                        additionalPointDao.addOutpatient_6(id, out6.getEnable(), out6.getNhi_no(), out6.getCategory(), 
                                    out6.getCpoe(), out6.getPlan());
                    }
                }
                java.util.List<AdditionalContent7Pl> lstOut7 = params.getOutpatient_7();
                if (lstOut7 != null) {
                    additionalPointDao.delOutpatient_7(id);
                    for (AdditionalContent7Pl out7 : lstOut7) {
                        additionalPointDao.addOutpatient_7(id, out7.getEnable(), out7.getNhi_no(), out7.getTrial(), out7.getPlan());
                    }
                }
                //--
                AdditionalContent1Pl inp1 = params.getInpatient_1();
                if (inp1 != null) {
                    additionalPointDao.delInpatient_1(id);
                    ret +=additionalPointDao.addInpatient_1(id, inp1.getEnable(), inp1.getCategory());
                }
                java.util.List<AdditionalContent2Pl> lstInp2 = params.getInpatient_2();
                if (lstInp2 != null) {
                    additionalPointDao.delInpatient_2(id);
                    for (AdditionalContent2Pl inp2 : lstInp2) {
                        additionalPointDao.addInpatient_2(id, inp2.getEnable(), inp2.getNhi_no(), inp2.getCpoe());
                    }
                }
                java.util.List<AdditionalContent3Pl> lstInp3 = params.getInpatient_3();
                if (lstInp3 != null) {
                    additionalPointDao.delInpatient_3(id);
                    for (AdditionalContent3Pl inp3 : lstInp3) {
                        additionalPointDao.addInpatient_3(id, inp3.getEnable(), inp3.getNhi_no());
                    }
                }
                java.util.List<AdditionalContent6Pl> lstInp6 = params.getInpatient_6();
                if (lstInp6 != null) {
                    additionalPointDao.delInpatient_6(id);
                    for (AdditionalContent6Pl inp6 : lstInp6) {
                        additionalPointDao.addInpatient_6(id, inp6.getEnable(), inp6.getNhi_no(), inp6.getCategory(), 
                                inp6.getCpoe(), inp6.getPlan());
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
