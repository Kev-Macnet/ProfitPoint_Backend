package tw.com.leadtek.nhiwidget.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.leadtek.nhiwidget.dto.PlanConditionPl;
import tw.com.leadtek.nhiwidget.dto.PlanLessNDayPl;
import tw.com.leadtek.nhiwidget.dto.PlanMoreTimesPl;
import tw.com.leadtek.nhiwidget.sql.PlanConditionDao;

@Service
public class PlanConditionService {
//    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PlanConditionDao planConditionDao;
    
    public java.util.Map<String, Object> findList(String searchName, int pageSize, int pageIndex) {
        long totalCount = planConditionDao.findListCount(searchName);
        int start = pageSize*pageIndex;
        if (start>totalCount) {
            start = (int)totalCount;
        } else if (start<0) {
            start = 0;
        }
        java.util.List<Map<String, Object>> lst = planConditionDao.findList(searchName, start, pageSize);
        
        java.util.Map<String, Object> retMap = new java.util.LinkedHashMap<String, Object>();
        retMap.put("total", totalCount);
        retMap.put("pages", (int)totalCount/pageSize + ((totalCount%pageSize)>0 ? 1: 0));
        retMap.put("pageIndex", pageIndex);
        retMap.put("pageSize", pageSize);
        retMap.put("data", lst);
        return retMap;
    }
    
    public java.util.Map<String, Object> findOne(long id) {
        java.util.Map<String, Object> retMap = planConditionDao.findOne(id);
        if (!retMap.isEmpty()) {
            //
            java.util.List<Map<String, Object>> icd_no = planConditionDao.findIcdNo(id);
            if (icd_no.size()>0) {
                java.util.Map<String, Object> map = icd_no.get(0);
                retMap.put("icd_no_enable", map.get("enable"));
                retMap.put("icd_no", extractListStr(icd_no, "icd_no"));
            }
            java.util.List<Map<String, Object>> lessNDay = planConditionDao.findLessNDay(id);
            if (lessNDay.size()>0) {
                java.util.Map<String, Object> map = lessNDay.get(0);
                retMap.put("less_nday_enable", map.get("enable"));
                retMap.put("less_nday", extractListMap(lessNDay, new String[] {"icd_no","nday"}));
            }
            java.util.List<Map<String, Object>> moreTimes = planConditionDao.findMoreTimes(id);
            if (lessNDay.size()>0) {
                java.util.Map<String, Object> map = moreTimes.get(0);
                retMap.put("more_times_enable", map.get("enable"));
                retMap.put("more_times", extractListMap(moreTimes, new String[] {"icd_no","times"}));
            }
        }
        return retMap;
    }
    

    //PlanIcdNoPl, PlanLessNdayPl, PlanMoreTimesPl
    public long addPlanCondition(PlanConditionPl params) {
        long new_id = planConditionDao.addPlanCondition(params.getName(), params.getDivision(), params.getActive(), 
                params.getExp_icd_no_enable(), params.getExp_icd_no(), params.getNo_exp_icd_no_enable(), params.getNo_exp_icd_no(), 
                params.getExclude_psychiatric_enable(), 
                params.getMedicine_times_enable(), params.getMedicine_times(), params.getMedicine_times_division(), 
                params.getExclude_plan_nday_enable(), params.getExclude_plan_nday(), 
                params.getExclude_join_enable(), params.getExclude_join());
//        java.util.Map<String, Object> mapPlan = planConditionDao.findOne(params.getName(), params.getDivision());
        if (new_id > 0) {
            int cnt=1;
            if (params.getIcd_no()!=null) {
                planConditionDao.delIcdNo(new_id);
                for (String icd_no : params.getIcd_no()) {
                    cnt += planConditionDao.addIcdNo(new_id, params.getIcd_no_enable(), icd_no);
                }
            }
            if (params.getLess_nday()!=null) {
                planConditionDao.delLessNDay(new_id);
                for (PlanLessNDayPl pl : params.getLess_nday()) {
                    cnt += planConditionDao.addLessNDay(new_id, params.getLess_nday_enable(), pl.getIcd_no(), pl.getNday());
                }
            }
            if (params.getMore_times()!=null) {
                planConditionDao.delMoreTimes(new_id);
                for (PlanMoreTimesPl pl : params.getMore_times()) {
                    cnt += planConditionDao.addMoreTimes(new_id, params.getMore_times_enable(), pl.getIcd_no(), pl.getTimes());
                }
            }
        }
        return new_id;
    }
    
    
    public int updatePlanCondition(long id, PlanConditionPl params) {
        int ret=0;
        if (id > 0) {
            ret += planConditionDao.updatePlanCondition(id, params.getName(), params.getDivision(), params.getActive(), 
                    params.getExp_icd_no_enable(), params.getExp_icd_no(), params.getNo_exp_icd_no_enable(), params.getNo_exp_icd_no(), 
                    params.getExclude_psychiatric_enable(), 
                    params.getMedicine_times_enable(), params.getMedicine_times(), params.getMedicine_times_division(), 
                    params.getExclude_plan_nday_enable(), params.getExclude_plan_nday(), 
                    params.getExclude_join_enable(), params.getExclude_join());
            if (params.getIcd_no()!=null) {
                planConditionDao.delIcdNo(id);
                for (String icd_no : params.getIcd_no()) {
                    ret += planConditionDao.addIcdNo(id, params.getIcd_no_enable(), icd_no);
                }
            }
            if (params.getLess_nday()!=null) {
                planConditionDao.delLessNDay(id);
                for (PlanLessNDayPl pl : params.getLess_nday()) {
                    ret += planConditionDao.addLessNDay(id, params.getLess_nday_enable(), pl.getIcd_no(), pl.getNday());
                }
            }
            if (params.getMore_times()!=null) {
                planConditionDao.delMoreTimes(id);
                for (PlanMoreTimesPl pl : params.getMore_times()) {
                    ret += planConditionDao.addMoreTimes(id, params.getMore_times_enable(), pl.getIcd_no(), pl.getTimes());
                }
            }
        }
        return ret;
    }
    
    
    public int deletePlanCondition(long id) {
        int ret=0;
        if (id > 0) {
            ret += planConditionDao.delIcdNo(id);
            ret += planConditionDao.delLessNDay(id);
            ret += planConditionDao.delMoreTimes(id);
            ret += planConditionDao.delPlanCondition(id);
        }
        return ret;
    }
    
    private java.util.List<String> extractListStr(java.util.List<Map<String, Object>> lstData, String field) {
        java.util.List<String> retList = new java.util.ArrayList<String>();
        if (lstData.size()>0) {
            for (Map<String, Object> item : lstData) {
                retList.add(item.get(field).toString());
            }
        }
        return retList;
    }
    
    private java.util.List<Map<String, Object>> extractListMap(java.util.List<Map<String, Object>> lstData, String fields[]) {
        java.util.List<Map<String, Object>> retList = new java.util.ArrayList<Map<String, Object>>();
        if (lstData.size()>0) {
            for (Map<String, Object> item : lstData) {
                Map<String, Object> map = new java.util.HashMap<String, Object>();
                for (int a=0; a<fields.length; a++) {
                    map.put(fields[a], item.get(fields[a]));
                }
                retList.add(map);
            }
        }
        return retList;
    }
}
