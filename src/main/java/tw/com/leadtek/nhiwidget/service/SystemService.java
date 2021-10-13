/**
 * Created on 2021/9/11.
 */
package tw.com.leadtek.nhiwidget.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import tw.com.leadtek.nhiwidget.controller.BaseController;
import tw.com.leadtek.nhiwidget.dao.ATCDao;
import tw.com.leadtek.nhiwidget.dao.PAY_CODEDao;
import tw.com.leadtek.nhiwidget.model.rdb.ATC;
import tw.com.leadtek.nhiwidget.model.rdb.PAY_CODE;
import tw.com.leadtek.nhiwidget.model.redis.CodeBaseLongId;
import tw.com.leadtek.nhiwidget.payload.ATCListResponse;
import tw.com.leadtek.nhiwidget.payload.PayCode;
import tw.com.leadtek.nhiwidget.payload.PayCodeListResponse;

@Service
public class SystemService {

  private Logger logger = LogManager.getLogger();

  @Autowired
  private ATCDao atcDao;

  @Autowired
  private RedisService redisService;

  @Autowired
  private PAY_CODEDao payCodeDao;

  public ATCListResponse getATC(String code, Integer leng, int perPage, int page) {
    List<ATC> codes = new ArrayList<ATC>();
    Specification<ATC> spec = new Specification<ATC>() {

      private static final long serialVersionUID = 1L;

      public Predicate toPredicate(Root<ATC> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicate = new ArrayList<Predicate>();
        if (code != null) {
          predicate.add(cb.like(root.get("code"), code.toUpperCase() + "%"));
        }
        if (leng != null) {
          predicate.add(cb.equal(root.get("leng"), leng));
        }
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        return query.getRestriction();
      }
    };

    ATCListResponse result = new ATCListResponse();
    result.setCount((int) atcDao.count(spec));
    result.setTotalPage(BaseController.countTotalPage(result.getCount(), perPage));
    Page<ATC> pages = atcDao.findAll(spec, PageRequest.of(page, perPage));
    if (pages != null && pages.getSize() > 0) {
      for (ATC atc : pages) {
        codes.add(atc);
      }
    }
    result.setData(codes);
    return result;
  }

  public ATC getATC(String code) {
    if (code == null) {
      return null;
    }
    List<ATC> list = atcDao.findByCode(code);
    if (list == null || list.size() == 0) {
      return null;
    }
    return list.get(0);
  }

  public void saveATC(ATC atc, boolean isCreate) {
    CodeBaseLongId cb = new CodeBaseLongId(0, atc.getCode(), null, null);
    cb.setCategory("ATC");
    if (atc.getNote().indexOf("（") > 0) {
      // 齲齒預防藥（Caries prophylactic agents）
      cb.setDesc(atc.getNote().substring(0, atc.getNote().indexOf("（")));
      cb.setDescEn(
          atc.getNote().substring(atc.getNote().indexOf("（") + 1, atc.getNote().length() - 1));
    } else {
      cb.setDesc(atc.getNote());
    }

    if (isCreate) {
      int id = redisService.getMaxId() + 1;
      cb.setId((long) id);
      atc.setRedisId(id);
      redisService.addIndexToRedisIndex("ICD10-index", String.valueOf(cb.getId()),
          cb.getCode().toLowerCase());
    } else {
      cb.setId((long) atc.getRedisId());
    }
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setSerializationInclusion(Include.NON_NULL);
      String json = objectMapper.writeValueAsString(cb);
      // 1. save to data
      redisService.putHash("ICD10-data", String.valueOf(cb.getId()), json);
      // 2. save code to index for search
    } catch (JsonProcessingException e) {
      logger.error("saveATC", e);
    }
    atcDao.save(atc);
  }

  public void deleteATC(ATC atc) {
    atcDao.delete(atc);
    redisService.deleteHash("ICD10-data", atc.getRedisId().toString());
    redisService.removeIndexToRedisIndex("ICD10-index", atc.getCode(), atc.getRedisId());
  }

  public PayCodeListResponse getPayCode(String startDay, String endDay, String atc, String codeType,
      String code, String inhCode, String name, String inhName, String orderBy, Boolean asc,
      int perPage, int page) {

    Specification<PAY_CODE> spec = new Specification<PAY_CODE>() {

      private static final long serialVersionUID = 1L;

      public Predicate toPredicate(Root<PAY_CODE> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date sd = null;
        Date ed = null;
        try {
          if (startDay != null && startDay.length() > 0) {
            sd = sdf.parse(startDay);
          }
          if (endDay != null && endDay.length() > 0) {
            ed = sdf.parse(endDay);
          }
        } catch (ParseException e) {
          e.printStackTrace();
        }

        List<Predicate> predicate = new ArrayList<Predicate>();
        if (sd != null) {
          predicate.add(cb.lessThanOrEqualTo(root.get("startDay"), sd));
        }
        if (ed != null) {
          predicate.add(cb.greaterThan(root.get("endDay"), ed));
        }
        if (atc != null) {
          predicate.add(cb.equal(root.get("atc"), atc));
        }
        if (codeType != null) {
          predicate.add(cb.equal(root.get("codeType"), codeType));
        }
        if (code != null && code.length() > 1) {
          predicate.add(cb.like(root.get("code"), code + "%"));
        }
        if (inhCode != null && inhCode.length() > 1) {
          predicate.add(cb.like(root.get("inhCode"), inhCode + "%"));
        }
        if (name != null && name.length() > 1) {
          predicate.add(cb.like(root.get("name"), name + "%"));
        }
        if (inhName != null && inhName.length() > 1) {
          predicate.add(cb.like(root.get("inhName"), inhName + "%"));
        }
        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));

        if (orderBy != null && asc != null) {
          List<Order> orderList = new ArrayList<Order>();
          if (asc.booleanValue()) {
            orderList.add(cb.asc(root.get(orderBy)));
          } else {
            orderList.add(cb.desc(root.get(orderBy)));
          }
          query.orderBy(orderList);
        }
        return query.getRestriction();
      }
    };

    PayCodeListResponse result = new PayCodeListResponse();
    result.setCount((int) payCodeDao.count(spec));
    result.setTotalPage(BaseController.countTotalPage(result.getCount(), perPage));

    List<PayCode> codes = new ArrayList<PayCode>();
    Page<PAY_CODE> pages = payCodeDao.findAll(spec, PageRequest.of(page, perPage));
    if (pages != null && pages.getSize() > 0) {
      for (PAY_CODE pc : pages) {
        codes.add(new PayCode(pc));
      }
    }
    result.setData(codes);
    return result;
  }

  public PAY_CODE getPayCode(PAY_CODE pc) {
    if (pc == null) {
      return null;
    }
    if (pc.getId() != null && pc.getId() > 0) {
      Optional<PAY_CODE> optional = payCodeDao.findById(pc.getId());
      if (optional.isPresent()) {
        return optional.get();
      }
      return null;
    }
    List<PAY_CODE> list = null;
    if (pc.getCode() != null) {
      list = payCodeDao.findByCode(pc.getCode());
    } else if (pc.getInhCode() != null) {
      list = payCodeDao.findByInhCode(pc.getInhCode());
    }
    if (list == null || list.size() == 0) {
      return null;
    }
    return checkPayCodeStartAndEndDay(list, pc);
  }

  private PAY_CODE checkPayCodeStartAndEndDay(List<PAY_CODE> list, PAY_CODE pc) {
    for (PAY_CODE payCode : list) {
      if (payCode.getStartDate().getTime() == pc.getStartDate().getTime() && payCode.getEndDate().getTime() == pc.getEndDate().getTime()) {
        return payCode;
      }
    }
    return null;
  }

  public void savePayCode(PAY_CODE pc, boolean isCreate) {
    CodeBaseLongId cb = new CodeBaseLongId(0, pc.getCode(), null, null);
    cb.setCategory("ORDER");
    if (pc.getInhName() == null) {
      cb.setDesc(pc.getName());
    } else {
      cb.setDesc(pc.getInhName());
    }

    if (isCreate) {
      int id = redisService.getMaxId() + 1;
      cb.setId((long) id);
      pc.setRedisId(id);
      redisService.addIndexToRedisIndex("ICD10-index", String.valueOf(cb.getId()),
          cb.getCode().toLowerCase());
    } else {
      cb.setId((long) pc.getRedisId());
    }
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setSerializationInclusion(Include.NON_NULL);
      String json = objectMapper.writeValueAsString(cb);
      // 1. save to data
      redisService.putHash("ICD10-data", String.valueOf(cb.getId()), json);
      // 2. save code to index for search
    } catch (JsonProcessingException e) {
      logger.error("saveATC", e);
    }
    payCodeDao.save(pc);
  }
  
  public void deletePayCode(PAY_CODE pc) {
    payCodeDao.delete(pc);
    redisService.deleteHash("ICD10-data", pc.getRedisId().toString());
    redisService.removeIndexToRedisIndex("ICD10-index", pc.getCode(), pc.getRedisId());
  }
}
