package tw.com.leadtek.nhiwidget.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.swagger.annotations.ApiModelProperty;
import tw.com.leadtek.nhiwidget.dto.PlanConditionPl;
import tw.com.leadtek.nhiwidget.dto.PlanIcdNoPl;
import tw.com.leadtek.nhiwidget.dto.PlanLessNDayPl;
import tw.com.leadtek.nhiwidget.dto.PlanMoreTimesPl;
import tw.com.leadtek.nhiwidget.sql.PlanConditionDao;
import tw.com.leadtek.tools.Utility;

@Service
public class PlanConditionService {
//    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PlanConditionDao planConditionDao;
    
    public java.util.List<Map<String, Object>> findList(String searchName) {
        return planConditionDao.findList(searchName);
    }
    
    public java.util.Map<String, Object> findOne(long id) {
        java.util.Map<String, Object> retMap = planConditionDao.findOne(id);
        if (!retMap.isEmpty()) {
            retMap.put("icd_no", planConditionDao.findIcdNo(id));
            retMap.put("less_nday", planConditionDao.findLessNDay(id));
            retMap.put("more_times", planConditionDao.findMoreTimes(id));
        }
        
        return retMap;
    }

    //PlanIcdNoPl, PlanLessNdayPl, PlanMoreTimesPl
    public int addPlanCondition(PlanConditionPl params) {
        int ret = planConditionDao.addPlanCondition(params.getName(), params.getDivision(), params.getActive(), 
                params.getExp_icd_no_enable(), params.getExp_icd_no(), params.getNo_exp_icd_no_enable(), params.getNo_exp_icd_no(), 
                params.getExclude_psychiatric_enable(), 
                params.getMedicine_times_enable(), params.getMedicine_times(), params.getMedicine_times_division(), 
                params.getExclude_plan_nday_enable(), params.getExclude_plan_nday(), 
                params.getExclude_join_enable(), params.getExclude_join());
        java.util.Map<String, Object> mapPlan = planConditionDao.findOne(params.getName(), params.getDivision());
        if (mapPlan != null) {
            long id = (long)mapPlan.get("id");
            if (params.getIcd_no()!=null) {
                planConditionDao.delIcdNo(id);
                for (PlanIcdNoPl pl : params.getIcd_no()) {
                    ret += planConditionDao.addIcdNo(id, pl.getEnable(), pl.getIcd_no());
                }
            }
            if (params.getLess_nday()!=null) {
                planConditionDao.delLessNDay(id);
                for (PlanLessNDayPl pl : params.getLess_nday()) {
                    ret += planConditionDao.addLessNDay(id, pl.getEnable(), pl.getIcd_no(), pl.getNday());
                }
            }
            if (params.getMore_times()!=null) {
                planConditionDao.delMoreTimes(id);
                for (PlanMoreTimesPl pl : params.getMore_times()) {
                    ret += planConditionDao.addMoreTimes(id, pl.getEnable(), pl.getIcd_no(), pl.getTimes());
                }
            }
        }

        return ret;
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
                for (PlanIcdNoPl pl : params.getIcd_no()) {
                    ret += planConditionDao.addIcdNo(id, pl.getEnable(), pl.getIcd_no());
                }
            }
            if (params.getLess_nday()!=null) {
                planConditionDao.delLessNDay(id);
                for (PlanLessNDayPl pl : params.getLess_nday()) {
                    ret += planConditionDao.addLessNDay(id, pl.getEnable(), pl.getIcd_no(), pl.getNday());
                }
            }
            if (params.getMore_times()!=null) {
                planConditionDao.delMoreTimes(id);
                for (PlanMoreTimesPl pl : params.getMore_times()) {
                    ret += planConditionDao.addMoreTimes(id, pl.getEnable(), pl.getIcd_no(), pl.getTimes());
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
}
