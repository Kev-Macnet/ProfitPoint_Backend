/**
 * Created on 2021/11/19.
 */
package tw.com.leadtek.nhiwidget.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tw.com.leadtek.nhiwidget.constant.INTELLIGENT_REASON;
import tw.com.leadtek.nhiwidget.constant.MR_STATUS;
import tw.com.leadtek.nhiwidget.constant.ROLE_TYPE;
import tw.com.leadtek.nhiwidget.dao.INTELLIGENTDao;
import tw.com.leadtek.nhiwidget.model.rdb.INTELLIGENT;
import tw.com.leadtek.nhiwidget.payload.intelligent.IntelligentRecord;
import tw.com.leadtek.nhiwidget.payload.intelligent.IntelligentResponse;
import tw.com.leadtek.nhiwidget.security.service.UserDetailsImpl;
import tw.com.leadtek.tools.Utility;

@Service
public class IntelligentService {

  private Logger logger = LogManager.getLogger();
  
  @Autowired
  private INTELLIGENTDao intelligentDao;
  
  public IntelligentResponse getIntelligent(UserDetailsImpl user, String orderBy, Boolean asc,
      int perPage, int page) {
    IntelligentResponse result = new IntelligentResponse();

    Specification<INTELLIGENT> spec = new Specification<INTELLIGENT>() {

      private static final long serialVersionUID = 1011L;

      public Predicate toPredicate(Root<INTELLIGENT> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (ROLE_TYPE.DOCTOR.getRole().equals(user.getRole())) {
          predicate.add(cb.like(root.get("noticeName"), "%" + user.getUsername() + "%"));
        }
        predicate.add(cb.equal(root.get("status"), MR_STATUS.QUESTION_MARK.value()));
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));

        List<Order> orderList = new ArrayList<Order>();
        if (orderBy != null && asc != null) {
          if (asc.booleanValue()) {
            orderList.add(cb.asc(root.get(orderBy)));
          } else {
            orderList.add(cb.desc(root.get(orderBy)));
          }
        } else {
          orderList.add(cb.desc(root.get("startDate")));
        }
        query.orderBy(orderList);
        return query.getRestriction();
      }
    };
    result.setCount((int) intelligentDao.count(spec));
    Page<INTELLIGENT> pages = intelligentDao.findAll(spec, PageRequest.of(page, perPage));
    List<IntelligentRecord> list = new ArrayList<IntelligentRecord>();
    if (pages != null && pages.getSize() > 0) {
      for (INTELLIGENT p : pages) {
        list.add(new IntelligentRecord(p));
      }
    }
    result.setTotalPage(Utility.getTotalPage(result.getCount(), perPage));
    result.setData(list);
    
    List<Object[]> groupByList = intelligentDao.countGroupByConditionCode();
    for (Object[] obj : groupByList) {
      if(((Integer) obj[0]).intValue() == INTELLIGENT_REASON.HIGH_RATIO.value()){
        result.setHighRatio(((BigInteger) obj[1]).intValue());
      } else if(((Integer) obj[0]).intValue() == INTELLIGENT_REASON.HIGH_RISK.value()){
        result.setHighRisk(((BigInteger) obj[1]).intValue());
      }  else if(((Integer) obj[0]).intValue() == INTELLIGENT_REASON.INFECTIOUS.value()){
        result.setInfectious(((BigInteger) obj[1]).intValue());
      } else if(((Integer) obj[0]).intValue() == INTELLIGENT_REASON.INH_OWN_EXIST.value()){
        result.setInhOwnExist(((BigInteger) obj[1]).intValue());
      } else if(((Integer) obj[0]).intValue() == INTELLIGENT_REASON.MATERIAL.value()){
        result.setMaterial(((BigInteger) obj[1]).intValue());
      } else if(((Integer) obj[0]).intValue() == INTELLIGENT_REASON.OVER_AMOUNT.value()){
        result.setOverAmount(((BigInteger) obj[1]).intValue());
      } else if(((Integer) obj[0]).intValue() == INTELLIGENT_REASON.PILOT_PROJECT.value()){
        result.setPilotProject(((BigInteger) obj[1]).intValue());
      } else if(((Integer) obj[0]).intValue() == INTELLIGENT_REASON.RARE_ICD.value()){
        result.setRareIcd(((BigInteger) obj[1]).intValue());
      } else if(((Integer) obj[0]).intValue() == INTELLIGENT_REASON.SAME_ATC.value()){
        result.setSameAtc(((BigInteger) obj[1]).intValue());
      } else if(((Integer) obj[0]).intValue() == INTELLIGENT_REASON.VIOLATE.value()){
        result.setViolate(((BigInteger) obj[1]).intValue());
      }
    }
    return result;
  }
}
