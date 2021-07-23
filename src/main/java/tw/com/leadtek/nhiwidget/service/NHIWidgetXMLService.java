/**
 * Created on 2021/1/25.
 */
package tw.com.leadtek.nhiwidget.service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import tw.com.leadtek.nhiwidget.constant.MR_STATUS;
import tw.com.leadtek.nhiwidget.constant.XMLConstant;
import tw.com.leadtek.nhiwidget.dao.CODE_TABLEDao;
import tw.com.leadtek.nhiwidget.dao.IP_DDao;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.IP_TDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.MR_CHECKEDDao;
import tw.com.leadtek.nhiwidget.dao.OP_DDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dao.OP_TDao;
import tw.com.leadtek.nhiwidget.model.CodeBase;
import tw.com.leadtek.nhiwidget.model.JsonSuggestion;
import tw.com.leadtek.nhiwidget.model.rdb.CODE_TABLE;
import tw.com.leadtek.nhiwidget.model.rdb.DHead;
import tw.com.leadtek.nhiwidget.model.rdb.IP;
import tw.com.leadtek.nhiwidget.model.rdb.IP_D;
import tw.com.leadtek.nhiwidget.model.rdb.IP_DData;
import tw.com.leadtek.nhiwidget.model.rdb.IP_P;
import tw.com.leadtek.nhiwidget.model.rdb.IP_T;
import tw.com.leadtek.nhiwidget.model.rdb.MR;
import tw.com.leadtek.nhiwidget.model.rdb.OP;
import tw.com.leadtek.nhiwidget.model.rdb.OP_D;
import tw.com.leadtek.nhiwidget.model.rdb.OP_DData;
import tw.com.leadtek.nhiwidget.model.rdb.OP_P;
import tw.com.leadtek.nhiwidget.model.rdb.OP_T;
import tw.com.leadtek.nhiwidget.payload.MO;
import tw.com.leadtek.nhiwidget.payload.MRCount;
import tw.com.leadtek.nhiwidget.payload.MRCountResponse;
import tw.com.leadtek.nhiwidget.payload.MRDetail;
import tw.com.leadtek.nhiwidget.payload.MRResponse;
import tw.com.leadtek.nhiwidget.payload.QuickSearchResponse;
import tw.com.leadtek.nhiwidget.payload.SearchReq;
import tw.com.leadtek.tools.DateTool;
import tw.com.leadtek.tools.Utility;

@Service
public class NHIWidgetXMLService {

  private final String DOCTYPE = "<?xml version=\"1.0\" encoding=\"Big5\"?>\n";

  @Autowired
  private OP_PDao oppDao;

  @Autowired
  private OP_DDao opdDao;

  @Autowired
  private OP_TDao optDao;

  @Autowired
  private IP_TDao iptDao;

  @Autowired
  private IP_DDao ipdDao;

  @Autowired
  private IP_PDao ippDao;

  @Autowired
  private CODE_TABLEDao ctDao;

  @Autowired
  private MRDao mrDao;

  @Autowired
  private RedisService redis;

  @Autowired
  private CodeTableService cts;

  @Autowired
  private MR_CHECKEDDao mrCheckedDao;

  @Autowired
  private ParametersService parameters;
  
  @Autowired
  private LogDataService logService;

  public void saveOP(OP op) {
    OP_T opt = optDao.save(op.getTdata());
    List<OP_DData> dDataList = op.getDdata();
    for (OP_DData op_dData : dDataList) {
      OP_D opd = op_dData.getDbody();
      opd.setCaseType(op_dData.getDhead().getCASE_TYPE());
      opd.setSeqNo(op_dData.getDhead().getSEQ_NO());
      opd.setOptId(opt.getId());

      MR mr = new MR(opd);
      mr = mrDao.save(mr);
      CODE_TABLE ct = cts.getCodeTable("INFECTIOUS", opd.getIcdCm1());
      mr.setInfectious((ct == null) ? 0 : 1);
      opd.setMrId(mr.getId());
      opd = opdDao.save(opd);
      List<OP_P> oppList = opd.getPdataList();
      for (OP_P opp : oppList) {
        opp.setOpdId(opd.getId());
        oppDao.save(opp);
      }
    }
  }  

  public void saveIP(IP ip) {
    IP_T ipt = iptDao.save(ip.getTdata());
    Map<String, Object> condition1 = logService.makeCondition(new String[][] {{"ID",Long.toString(ipt.getId())}});
    Map<String, Object> row1 = logService.findOne("IP_T", condition1);
    logService.updateModification("system", "IP_T", condition1, new HashMap<String, Object>(), row1);
    List<IP_DData> dDataList = ip.getDdata();
    for (IP_DData ip_dData : dDataList) {
      IP_D ipd = ip_dData.getDbody();
      ipd.setCaseType(ip_dData.getDhead().getCASE_TYPE());
      ipd.setSeqNo(ip_dData.getDhead().getSEQ_NO());
      ipd.setIptId(ipt.getId());
      MR mr = new MR(ipd);
      mr = mrDao.save(mr);
      CODE_TABLE ct = cts.getCodeTable("INFECTIOUS", ipd.getIcdCm1());
      mr.setInfectious((ct == null) ? 0 : 1);
      ipd.setMrId(mr.getId());
      ipd = ipdDao.save(ipd);
      Map<String, Object> condition2 = logService.makeCondition(new String[][] {{"ID",Long.toString(ipd.getId())}});
      Map<String, Object> row2 = logService.findOne("IP_D", condition2);
      logService.updateModification("system", "IP_D", condition2, new HashMap<String, Object>(), row2);
      mr.setdId(ipd.getId());
      List<IP_P> ippList = ipd.getPdataList();
      for (IP_P ipp : ippList) {
        ipp.setIpdId(ipd.getId());
        ipp.setMrId(mr.getId());
        ippDao.save(ipp);
        Map<String, Object> condition3 = logService.makeCondition(new String[][] {{"ID",Long.toString(ipp.getId())}});
        Map<String, Object> row3 = logService.findOne("IP_P", condition3);
        logService.updateModification("system", "IP_P", condition3, new HashMap<String, Object>(), row3);
      }
      mrDao.save(mr);
    }
  }

  /**
   * 下載表單資料為 Excel 檔案.
   * 
   * @param name
   * @param outputStream
   * @return
   * @throws IOException
   */
  public int getNHIXMLFile(String ym, String dataFormat, OutputStream outputStream)
      throws IOException {

    XmlMapper xmlMapper = new XmlMapper();
    // 若值為 null 不會輸出到 String
    xmlMapper.setSerializationInclusion(Include.NON_NULL);
    Object data = null;
    if ("IP".equals(dataFormat)) {
      data = getIP(ym);
    } else if ("OP".equals(dataFormat)) {
      data = getOP(ym);
    }
    // xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
    String xml = xmlMapper.writeValueAsString(data);

    try {
      outputStream.write(DOCTYPE.getBytes());
      outputStream.write(xml.getBytes(Charset.forName("BIG5")));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return -1;

  }

  /**
   * 下載表單資料為 Excel 檔案.
   * 
   * @param name
   * @param outputStream
   * @return
   * @throws IOException
   */
  public int getNHIXMLFileBetween(String sdate, String edate, String dataFormat,
      OutputStream outputStream) throws IOException {

    XmlMapper xmlMapper = new XmlMapper();
    // 若值為 null 不會輸出到 String
    xmlMapper.setSerializationInclusion(Include.NON_NULL);
    xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
    Object data = null;
    int result = 0;
    if ("20".equals(dataFormat)) {
      data = getIP(sdate, edate);
      if (data != null) {
        result = ((IP) data).getDdata().size();
      }
    } else if ("10".equals(dataFormat)) {
      data = getOP(sdate, edate);
      if (data != null) {
        result = ((OP) data).getDdata().size();
      }
    }    // xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
    String xml = xmlMapper.writeValueAsString(data);
    // System.out.println("xml=" + xml);

    try {
      outputStream.write(DOCTYPE.getBytes());
      outputStream.write(xml.getBytes(Charset.forName("BIG5")));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * 取得住院申報資料
   * 
   * @param ym
   * @return
   */
  private IP getIP(String ym) {
    IP result = new IP();
    List<IP_T> iptList = iptDao.findByFeeYmOrderById(ym);
    if (iptList != null && iptList.size() > 0) {
      IP_T ipt = iptList.get(0);
      result.setTdata(ipt);
      List<IP_DData> ip_DDataList = new ArrayList<IP_DData>();
      result.setDdata(ip_DDataList);
      List<IP_D> ipdList = ipdDao.findByIptId(ipt.getId());
      for (IP_D ip_D : ipdList) {
        IP_DData ip_Ddata = new IP_DData();
        DHead dHead = new DHead();
        dHead.setCASE_TYPE(ip_D.getCaseType());
        dHead.setSEQ_NO(ip_D.getSeqNo());
        ip_Ddata.setDhead(dHead);
        ip_Ddata.setDbody(ip_D);
        ip_DDataList.add(ip_Ddata);

        ip_D.setPdataList(ippDao.findByIpdId(ip_D.getId()));
      }
    }
    return result;
  }

  /**
   * 取得門診申報資料
   * 
   * @param ym
   * @return
   */
  private OP getOP(String ym) {
    OP result = new OP();
    List<OP_T> optList = optDao.findByFeeYmOrderById(ym);
    if (optList != null && optList.size() > 0) {
      OP_T opt = optList.get(0);
      result.setTdata(opt);
      List<OP_DData> op_DDataList = new ArrayList<OP_DData>();
      result.setDdata(op_DDataList);
      List<OP_D> opdList = opdDao.findByOptId(opt.getId());
      for (OP_D op_D : opdList) {
        OP_DData op_Ddata = new OP_DData();
        DHead dHead = new DHead();
        dHead.setCASE_TYPE(op_D.getCaseType());
        dHead.setSEQ_NO(op_D.getSeqNo());
        op_Ddata.setDhead(dHead);
        op_Ddata.setDbody(op_D);
        op_DDataList.add(op_Ddata);

        op_D.setPdataList(oppDao.findByOpdId(op_D.getId()));
      }
    }
    return result;
  }

  /**
   * 取得住院申報資料
   * 
   * @param ym
   * @return
   */
  private IP getIP(String sdate, String edate) {
    IP result = new IP();
    IP_T ipt = new IP_T();
    result.setTdata(ipt);
    List<IP_DData> ip_DDataList = new ArrayList<IP_DData>();
    result.setDdata(ip_DDataList);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    try {
      Date sDate = (sdate == null) ? null : new Date(sdf.parse(sdate).getTime());
      Date eDate = (edate == null) ? null : new Date(sdf.parse(edate).getTime());

      List<IP_D> ipdList = ipdDao.findByIDFromMR("10", sDate, eDate);
      if (ipdList != null && ipdList.size() > 0) {
        List<IP_P> ippList = ippDao.findByIpdIDFromMR("10", sDate, eDate);

        for (IP_D ip_D : ipdList) {
          IP_DData ip_Ddata = new IP_DData();
          DHead dHead = new DHead();
          dHead.setCASE_TYPE(ip_D.getCaseType());
          dHead.setSEQ_NO(ip_D.getSeqNo());
          ip_Ddata.setDhead(dHead);
          ip_Ddata.setDbody(ip_D);
          ip_DDataList.add(ip_Ddata);
          ip_D.setPdataList(searchIppByIpdId(ippList, ip_D.getId()));
          ip_DDataList.add(ip_Ddata);
        }
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * 取得門診申報資料
   * 
   * @param ym
   * @return
   */
  private OP getOP(String sdate, String edate) {
    OP result = new OP();
    OP_T opt = new OP_T();
    result.setTdata(opt);
    List<OP_DData> op_DDataList = new ArrayList<OP_DData>();
    result.setDdata(op_DDataList);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    try {
      Date sDate = (sdate == null) ? null : new Date(sdf.parse(sdate).getTime());
      Date eDate = (edate == null) ? null : new Date(sdf.parse(edate).getTime());

      List<OP_D> opdList = opdDao.findByIDFromMR("10", sDate, eDate);
      if (opdList != null && opdList.size() > 0) {
        List<OP_P> oppList = oppDao.findByOpdIDFromMR("10", sDate, eDate);

        for (OP_D op_D : opdList) {
          OP_DData op_Ddata = new OP_DData();
          DHead dHead = new DHead();
          dHead.setCASE_TYPE(op_D.getCaseType());
          dHead.setSEQ_NO(op_D.getSeqNo());
          op_Ddata.setDhead(dHead);
          op_Ddata.setDbody(op_D);
          op_D.setPdataList(searchOppByOpdId(oppList, op_D.getId()));
          op_DDataList.add(op_Ddata);
        }
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return result;
  } 

  private List<OP_P> searchOppByOpdId(List<OP_P> oppList, Long opdId) {
    List<OP_P> result = new ArrayList<OP_P>();
    for (int i = oppList.size() - 1; i >= 0; i--) {
      OP_P opP = oppList.get(i);
      if (opP.getOpdId().longValue() == opdId.longValue()) {
        result.add(oppList.remove(i));
      }
    }
    return result;
  }

  private List<IP_P> searchIppByIpdId(List<IP_P> ippList, Long ipdId) {
    List<IP_P> result = new ArrayList<IP_P>();
    for (int i = ippList.size() - 1; i >= 0; i--) {
      IP_P ipP = ippList.get(i);
      if (ipP.getIpdId().longValue() == ipdId.longValue()) {
        result.add(ippList.remove(i));
      }
    }
    return result;
  }

  public List<CODE_TABLE> getCodeTable(String cat) {
    List<CODE_TABLE> list = "ALL".equals(cat) ? ctDao.findAll() : ctDao.findByCat(cat);
    if (list == null || list.size() == 0) {
      return new ArrayList<CODE_TABLE>();
    }
    filterCat(list, cat);
    return list;
  }

  private void filterCat(List<CODE_TABLE> list, String cat) {
    for (CODE_TABLE code_TABLE : list) {
      if (code_TABLE.getRemark() != null && code_TABLE.getRemark().length() == 0) {
        code_TABLE.setRemark(null);
      }
    }
    if ("ALL".equals(cat)) {
      return;
    }
    for (CODE_TABLE code_TABLE : list) {
      code_TABLE.setCat(null);
    }
  }

  public String search(SearchReq req) {
    List<IP_D> ls = null;
    if (req.getAll() != null) {
      ls = fullSearchIP_D(null, PageRequest.of(0, 10), req.getAll());
    } else {
      IP_D ipd = new IP_D();
      if (req.getCaseType() != null) {
        ipd.setCaseType(req.getCaseType());
      }
      if (req.getApplyPoints() != null) {
        ipd.setMedDot(req.getApplyPoints().intValue());
      }
      if (req.getDrugFee() != null) {
        ipd.setDrugDot(req.getDrugFee().intValue());
      }
      if (req.getFuncDate() != null) {
        ipd.setApplStartDate(req.getFuncDate());
      }
      if (req.getFuncType() != null) {
        ipd.setFuncType(req.getFuncType());
      }
      if (req.getIcdCM() != null) {
        ipd.setIcdCm1(req.getIcdCM());
      }
      if (req.getIcdOP() != null) {
        ipd.setIcdOpCode1(req.getIcdOP());
      }
      if (req.getName() != null) {
        ipd.setName(req.getName());
      }
      if (req.getPrsnID() != null) {
        ipd.setPrsnId(req.getPrsnID());
      }
      if (req.getRocID() != null) {
        ipd.setRocId(req.getRocID());
      }
      if (req.getYm() != null) {
        ipd.setApplStartDate(req.getYm());
      }
      // in
      // List<String> strList=new ArrayList<>();
      // strList.add("20");
      // strList.add("24");
      // predicatesList.add(exp.in(strList));

      ExampleMatcher matcher = ExampleMatcher.matching() // 構建物件
          .withStringMatcher(StringMatcher.CONTAINING) // 改變預設字串匹配方式：模糊查詢
          .withIgnoreCase(true) // 改變預設大小寫忽略方式：忽略大小寫
          .withMatcher("APPL_START_DATE", GenericPropertyMatchers.startsWith());
      // .withIgnorePaths("focus"); // 忽略屬性：是否關注。因為是基本型別，需要忽略掉

      Example<IP_D> ex = Example.of(ipd, matcher);

      // 查詢
      ls = ipdDao.findAll(ex);
    }

    ObjectMapper objectMapper = new ObjectMapper();
    // 若值為 null 不會輸出到 String
    objectMapper.setSerializationInclusion(Include.NON_NULL);
    try {
      String json = objectMapper.writeValueAsString(ls);
      return json;
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    if (req.getOrder() != null) {
      // 醫令代碼
    }
    return "";
  }

  private List<IP_D> fullSearchIP_D(IP_D ipd, PageRequest page, String q) {
    // PageRequest pageRequest = new PageRequest(0,10);
    Page<IP_D> pages = ipdDao.findAll(new Specification<IP_D>() {

      public Predicate toPredicate(Root<IP_D> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicate = new ArrayList<Predicate>();

        // 1.混合條件查詢
        Path<String> pathPART_CODE = root.get("partNo");
        Path<String> pathFUNC_TYPE = root.get("funcType");
        Path<String> pathPRSN_ID = root.get("prsnId");
        Path<String> pathTW_DRG_CODE = root.get("twDrgCode");
        predicate.add(cb.or(cb.like(root.get("rocId"), q + "%"), cb.like(pathPART_CODE, q + "%"),
            cb.like(pathFUNC_TYPE, q + "%"), cb.like(pathPRSN_ID, q + "%"),
            cb.like(pathTW_DRG_CODE, q + "%")));

        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        return query.getRestriction();
      }

    }, page);

    List<IP_D> result = new ArrayList<IP_D>();

    if (pages != null && pages.getSize() > 0) {
      for (IP_D ip_D : pages) {
        result.add(ip_D);
      }
    }
    return result;
  }

  private Map<String, Object> fullSearchMR(int page, int perPage, String q, Date sDate,
      Date eDate) {
    // PageRequest pageRequest = new PageRequest(0,10);
    Specification<MR> spec = new Specification<MR>() {

      private static final long serialVersionUID = 1L;

      public Predicate toPredicate(Root<MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicate = new ArrayList<Predicate>();

        // 1.混合條件查詢
        Path<String> pathInhMrId = root.get("inhMrId");
        Path<String> pathFuncType = root.get("funcType");
        Path<String> pathRocId = root.get("rocId");
        Path<String> pathPrsnId = root.get("prsnId");
        Path<String> pathApplId = root.get("applId");
        Path<String> pathRemark = root.get("remark");
        Path<String> pathInhClinicId = root.get("inhClinicId");
        predicate.add(cb.between(root.get("mrDate"), sDate, eDate));
        predicate.add(cb.or(cb.like(pathRocId, q + "%"), cb.like(pathInhMrId, q + "%"),
            cb.like(pathFuncType, q + "%"), cb.like(pathPrsnId, q + "%"),
            cb.like(pathApplId, q + "%"), cb.like(pathRemark, q + "%"),
            cb.like(pathInhClinicId, q + "%")));

        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        return query.getRestriction();
      }
    };
    long total = mrDao.count(spec);
    Page<MR> pages = mrDao.findAll(spec, PageRequest.of(page, perPage));

    List<MRResponse> mrList = new ArrayList<MRResponse>();

    if (pages != null && pages.getSize() > 0) {
      for (MR mrDb : pages) {
        mrList.add(new MRResponse(mrDb, cts));
      }
    }
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("totalPage", Utility.getTotalPage((int) total, perPage));
    result.put("mr", mrList);
    return result;
  }

  public Map<String, Object> getMR(String allMatch, String startDate, String endDate,
      Integer minPoints, Integer maxPoints, String dataFormat, String funcType, String prsnId,
      String prsnName, String applId, String applName, String inhMrId, String inhClinicId,
      String drg, String drgSection, String orderCode, String inhCode, String drugUse,
      String inhCodeDrugUse, String icdAll, String icdCMMajor, String icdCMSecondary, String icdPCS,
      String qrObject, String qrSdate, String qrEdate, String status, String deductedCode,
      String deductedOrder, String all, int perPage, int page) {

    Map<String, Object> result = new HashMap<String, Object>();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    try {
      Date sDate = (startDate == null || startDate.length() == 0) ? null
          : new Date(sdf.parse(startDate).getTime());
      Date eDate = (endDate == null || endDate.length() == 0) ? null
          : new Date(sdf.parse(endDate).getTime());

      if (all != null) {
        result = fullSearchMR(page, perPage, all, sDate, eDate);
      } else {
        boolean isAnd = false;
        if (allMatch != null && allMatch.toUpperCase().equals("Y")) {
          isAnd = true;
        }
        // TODO 缺drg, drgSection, orderCode, drugUse, icdAll, icdCMMajor, icdCMSecondary, icdPCS
        // qrObject, qrSdate, qrEdate, deductedCode, deductedOrder, inhCode, inhCodeDrugUse
        result = getMR(page, perPage, isAnd, sDate, eDate, minPoints, maxPoints, dataFormat,
            funcType, prsnId, prsnName, applId, applName, inhMrId, inhClinicId, drg, drgSection,
            status, deductedCode);
      }
      return result;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return new HashMap<String, Object>();
  }

  // List<MR>
  private Map<String, Object> getMR(int page, int perPage, boolean isAnd, Date sDate, Date eDate,
      Integer minPoints, Integer maxPoints, String dataFormat, String funcType, String prsnId,
      String prsnName, String applId, String applName, String inhMrId, String inhClinicId,
      String drg, String drgSection, String status, String deductedCode) {

    Specification<MR> spec = new Specification<MR>() {

      private static final long serialVersionUID = 1L;

      public Predicate toPredicate(Root<MR> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicate = new ArrayList<Predicate>();
        predicate.add(cb.between(root.get("mrDate"), sDate, eDate));
        if (minPoints != null && maxPoints != null) {
          predicate.add(cb.between(root.get("totalDot"), minPoints, maxPoints));
        }
        addPredicate(root, predicate, cb, "dataFormat", dataFormat, false, false);
        if (!"00".equals(funcType)) {
          addPredicate(root, predicate, cb, "funcType", funcType, false, false);
        }
        addPredicate(root, predicate, cb, "prsnId", prsnId, false, false);
        addPredicate(root, predicate, cb, "prsnName", prsnId, false, false);
        addPredicate(root, predicate, cb, "applId", applId, false, false);
        addPredicate(root, predicate, cb, "applName", applId, false, false);
        addPredicate(root, predicate, cb, "inhMrId", inhMrId, false, false);
        addPredicate(root, predicate, cb, "inhClinicId", inhClinicId, false, false);
        addPredicate(root, predicate, cb, "drgCode", drg, false, false);
        addPredicate(root, predicate, cb, "drgSection", drgSection, false, false);
        addPredicate(root, predicate, cb, "status", status, false, true);
        if (deductedCode != null && deductedCode.length() > 0) {
          predicate.add(cb.greaterThan(root.get("deductedDot"), 0));
        }

        Predicate[] pre = new Predicate[predicate.size()];
        query.where(predicate.toArray(pre));
        return query.getRestriction();
      }
    };
    long total = mrDao.count(spec);
    Page<MR> pages = mrDao.findAll(spec, PageRequest.of(page, perPage));

    ObjectMapper objectMapper = new ObjectMapper();
    // 若值為 null 不會輸出到 String
    //objectMapper.setSerializationInclusion(Include.NON_NULL);
   
    
    List<MRResponse> mrList = new ArrayList<MRResponse>();
    if (pages != null && pages.getSize() > 0) {
      for (MR mrDb : pages) {
        MRResponse mrr = new MRResponse(mrDb, cts); 
        mrList.add(mrr);
        try {
          String json = objectMapper.writeValueAsString(mrr);
        } catch (JsonProcessingException e) {
          e.printStackTrace();
        }
      }
    }
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("totalPage", Utility.getTotalPage((int) total, perPage));
    result.put("mr", mrList);
    return result;
  }

  public Map<String, Object> dwSearch(String sdate, String edate, String dataFormat,
      String funcType, String prsnId, String prsnName, String applId, String applName,
      String status, Integer perPage, Integer page) {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    try {
      Date sDate = (sdate == null) ? null : new Date(sdf.parse(sdate).getTime());
      Date eDate = (edate == null) ? null : new Date(sdf.parse(edate).getTime());

      // TODO 缺drg, drgSection, orderCode, drugUse, icdAll, icdCMMajor, icdCMSecondary, icdPCS
      // qrObject, qrSdate, qrEdate, deductedCode, deductedOrder, inhCode, inhCodeDrugUse
      return getMR(page, perPage, false, sDate, eDate, null, null, dataFormat, funcType, prsnId,
          prsnName, applId, applName, null, null, null, null, null, null);

    } catch (ParseException e) {
      e.printStackTrace();
    }

    return new HashMap<String, Object>();
  }

  public Map<String, Object> quickSearch(int perPage, int page, String startDate, String endDate, Integer minPoints,
      Integer maxPoints, String dataFormat, String funcType, String orderCode, String inhCode,
      String icdCMMajor) {

    List<QuickSearchResponse> qsList = new ArrayList<QuickSearchResponse>();
    Map<String, Object> result = new HashMap<String, Object>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    try {
      Date sDate = (startDate == null || startDate.length() == 0) ? null
          : new Date(sdf.parse(startDate).getTime());
      Date eDate = (endDate == null || endDate.length() == 0) ? null
          : new Date(sdf.parse(endDate).getTime());


      Map<String, Object> mrMap = getMR(page, perPage, false, sDate, eDate, minPoints, maxPoints,
          dataFormat, funcType, null, null, null, null, null, null, null, null, null, null);

      if (mrMap == null || mrMap.get("mr") == null) {
        return new HashMap<String, Object>();
      }
      List<Long> ids = new ArrayList<Long>();
      for (MR mr : (List<MR>) mrMap.get("mr")) {
        qsList.add(new QuickSearchResponse(mr, cts.getDesc("FUNC_TYPE", mr.getFuncType())));
        ids.add(mr.getId());
      }
      List<Map<String, Object>> reasons = mrCheckedDao.queryMRChecked(ids);
      for (Map<String, Object> map : reasons) {
        Long id = (Long) map.get("MRID");
        String reason = (String) map.get("REASON");
        String detail = (String) map.get("DETAIL");
        for (QuickSearchResponse qsr : qsList) {
          if (qsr.getId().longValue() == id.longValue()) {
            qsr.setDetail(detail);
            qsr.setReason(reason);
            break;
          }
        }
      }
      result.put("totalPage", mrMap.get("totalPage"));
      result.put("qs", qsList);
      return result;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return result;
  }

  private void addPredicate(Root<MR> root, List<Predicate> predicate, CriteriaBuilder cb,
      String paramName, String params, boolean isAnd, boolean isInteger) {
    if (params == null || params.length() == 0) {
      return;
    }
    if (params.indexOf(',') < 0) {
      if (isInteger) {
        predicate.add(cb.equal(root.get(paramName), params));
      } else {
        predicate.add(cb.like(root.get(paramName), params + "%"));
      }
    } else {
      String[] ss = params.split(",");
      List<Predicate> predicates = new ArrayList<Predicate>();
      for (int i = 0; i < ss.length; i++) {
        if (isInteger) {
          predicates.add(cb.equal(root.get(paramName), ss[i]));
        } else {
          predicates.add(cb.like(root.get(paramName), ss[i] + "%"));
        }
      }
      Predicate[] pre = new Predicate[predicates.size()];
      if (isAnd) {
        predicate.add(cb.and(predicates.toArray(pre)));
      } else {
        predicate.add(cb.or(predicates.toArray(pre)));
      }
    }
  }

  public List<MR> getMRByMrDateBetween(String startDate, String endDate, Integer status) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    try {
      Date sDate = new Date(sdf.parse(startDate).getTime());
      Date eDate = new Date(sdf.parse(endDate).getTime());

      List<MR> list = null;
      if (status != null) {
        list = mrDao.findByStatusAndMrDateBetween(status, sDate, eDate);
      } else {
        list = mrDao.findByMrDateBetween(sDate, eDate);
      }
      return list;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return new ArrayList<MR>();
  }

  public MRCountResponse getMRCount(String startDate, String endDate, String dataFormat,
      String funcType, String prsnId, String applId) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    MRCount result = new MRCount();
    try {
      Date sDate = new Date(sdf.parse(startDate).getTime());
      Date eDate = new Date(sdf.parse(endDate).getTime());
      result.setDayCount((int) ((eDate.getTime() - sDate.getTime()) / 86400000L) + 1);

      List<String> dataFormatList = new ArrayList<String>();
      if ("10".equals(dataFormat)) {
        // 門急診
        dataFormatList.add("10");
      } else if ("20".equals(dataFormat)) {
        // 住院
        dataFormatList.add("20");
      } else {
        dataFormatList.add("10");
        dataFormatList.add("20");
      }
      // 取得指定日期區間的病歷數, 申請病歷數及申請總點數.
      List<Map<String, Object>> list = null;
      // 取得指定日期區間的各個病歷確認狀態總數
      List<Map<String, Object>> statusCount = null;
      if (prsnId == null && applId == null) {
        if (funcType == null || "00".equals(funcType)) {
          list = mrDao.queryMRRecordCount(sDate, eDate, sDate, eDate);
          statusCount = mrDao.queryMRStatusCount(dataFormatList, sDate, eDate);
        } else {
          list = mrDao.queryMRRecordCountByFuncType(sDate, eDate, funcType, sDate, eDate, funcType);
          statusCount = mrDao.queryMRStatusCount(sDate, eDate, funcType);
        }
        result.updateValues(list.get(0));
        result.updateMrStatusCount(statusCount);
      } else {
        Map<String, Object> map = getMR(0, 1000000, false, sDate, eDate, null, null, dataFormat,
            funcType, prsnId, null, applId, null, null, null, null, null, null, null);
        if (map.get("mr") != null) {
          List<MR> mrList = (List<MR>) map.get("mr");
          result.setTotalMr(mrList.size());
          for (MR mr : mrList) {
            if (mr.getStatus() == MR_STATUS.CLASSIFIED.value()) {
              result.setClassified(result.getClassified() + 1);
            } else if (mr.getStatus() == MR_STATUS.DONT_CHANGE.value()) {
              result.setDontChange(result.getDontChange() + 1);
            } else if (mr.getStatus() == MR_STATUS.NO_CHANGE.value()) {
              result.setNoChange(result.getNoChange() + 1);
            } else if (mr.getStatus() == MR_STATUS.OPTIMIZED.value()) {
              result.setOptimized(result.getOptimized() + 1);
            } else if (mr.getStatus() == MR_STATUS.QUESTION_MARK.value()) {
              result.setQuestionMark(result.getQuestionMark() + 1);
            } else if (mr.getStatus() == MR_STATUS.WAIT_CONFIRM.value()) {
              result.setWaitConfirm(result.getWaitConfirm() + 1);
            } else if (mr.getStatus() == MR_STATUS.WAIT_PROCESS.value()) {
              result.setWaitProcess(result.getWaitProcess() + 1);
            }

            if (mr.getApplDot() != null && mr.getApplDot().intValue() > 0) {
              result.setApplSum(result.getApplSum() + 1);
              result.setApplDot(result.getApplDot().intValue() + mr.getApplDot().intValue());
            }
            if (mr.getDrgCode() != null && mr.getDrgCode().length() > 0) {
              result.setDrg(result.getDrg().intValue() + 1);
            }
          }
        }

      }

    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  public MRCount getMRCountByApplId(String startDate, String endDate, String applId) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    MRCount result = new MRCount();
    try {
      Date sDate = new Date(sdf.parse(startDate).getTime());
      Date eDate = new Date(sdf.parse(endDate).getTime());

      // 取得指定日期區間的各個病歷確認狀態總數
      List<Map<String, Object>> statusCount = null;
      if (applId == null) {
        statusCount = mrDao.queryMRStatusCount(sDate, eDate, null);
      } else {
        statusCount = mrDao.queryMRStatusCountByApplId(sDate, eDate, splitStringToList(applId));
      }
      result.updateMrStatusCount(statusCount);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return result;
  }

  public List<String> splitStringToList(String s) {
    List<String> result = new ArrayList<String>();
    if (s.indexOf(',') < 0) {
      result.add(s.trim());
      return result;
    }
    String[] ss = s.split(",");
    for (String string : ss) {
      result.add(string.trim());
    }
    return result;
  }

  public MRDetail getMRDetail(String id) {
    MRDetail result = null;
    MR mr = mrDao.getOne(Long.parseLong(id));
    if (mr != null) {
      result = new MRDetail(mr);
      System.out.println("getMRDetail " + id + " funcType:" + result.getFuncType());
      if (XMLConstant.DATA_FORMAT_OP.equals(result.getDataFormat())) {
        OP_D opD = opdDao.getOne(result.getdId());
        result.setOPDData(opD, cts);

        List<OP_P> oppList = oppDao.findByOpdId(opD.getId());
        List<MO> moList = new ArrayList<MO>();
        for (OP_P op_P : oppList) {
          MO mo = new MO();
          mo.setOPPData(op_P, cts);
          moList.add(mo);
        }
        result.setMos(moList);
      } else if (XMLConstant.DATA_FORMAT_IP.equals(result.getDataFormat())) {
        IP_D ipD = ipdDao.getOne(result.getdId());
        result.setIPDData(ipD, cts);
        // @TODO
      }
    }
    return result;
  }

  public MRDetail updateMRDetail(MRDetail mrDetail) {
    MRDetail result = null;
    MR mr = mrDao.getOne(mrDetail.getId());
    if (mr == null) {
      return null;
    }
    mr.updateMR(mrDetail);
    if (XMLConstant.DATA_FORMAT_OP.equals(mrDetail.getDataFormat())) {
      OP_D opD = opdDao.getOne(mr.getdId());
      opD.setFuncType(mr.getFuncType());
      updateOPDByMrDetail(opD, mrDetail);
      opdDao.save(opD);
//      List<OP_P> oppList = oppDao.findByOpdId(opD.getId());
//      List<MO> moList = new ArrayList<MO>();
//      for (OP_P op_P : oppList) {
//        MO mo = new MO();
//        mo.setOPPData(op_P, cts);
//        moList.add(mo);
//      }
//      result.setMos(moList);
    } else if (XMLConstant.DATA_FORMAT_IP.equals(mrDetail.getDataFormat())) {
      IP_D ipD = ipdDao.getOne(mr.getdId());
      Map<String, Object> row1;
      if (ipD.getId()==null) {
          row1 = new HashMap<String, Object>();
      } else {
          Map<String, Object> condition1 = logService.makeCondition(new String[][] {{"ID",Long.toString(ipD.getId())}});
          row1 = logService.findOne("IP_D", condition1);
      }
      ipD.setFuncType(mr.getFuncType());
      ipD.setRocId(mrDetail.getRocId());
      ipD.setName(mrDetail.getName());
      ipD.setPrsnId(mrDetail.getPrsnId());
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      ipD.setInDate(DateTool.convertToChineseYear(sdf.format(mrDetail.getMrDate())));
      ipD.setApplDot(mrDetail.getApplDot());
      ipD.setNonApplDot(mrDetail.getOwnExpense());
      ipdDao.save(ipD);
      Map<String, Object> condition2 = logService.makeCondition(new String[][] {{"ID",Long.toString(ipD.getId())}});
      Map<String, Object> row2 = logService.findOne("IP_D", condition2);
      logService.updateModification("system", "IP_D", condition2, row1, row2);
    }
    System.out.println("new FuncType:" + mr.getFuncType());
    mr = mrDao.save(mr);
    result = new MRDetail(mr);
    System.out.println("after save new FuncType:" + result.getFuncType());
    return result;
  }
  
  private void updateOPDByMrDetail(OP_D opD, MRDetail mrDetail) {
    opD.setRocId(mrDetail.getRocId());
    opD.setName(mrDetail.getName());
    opD.setPrsnId(mrDetail.getPrsnId());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    opD.setFuncDate(DateTool.convertToChineseYear(sdf.format(mrDetail.getMrDate())));
    opD.setTotalDot(mrDetail.getTotalDot());
    opD.setTotalApplDot(mrDetail.getApplDot());
    opD.setIdBirthYmd(mrDetail.getBirthday());
    opD.setPayType(removeDash(mrDetail.getPayType()));
    if (mrDetail.getIcdCM() != null) {
      if (mrDetail.getIcdCM().size() >= 1) {
        opD.setIcdCm1(mrDetail.getIcdCM().get(0).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 2) {
        opD.setIcdCm2(mrDetail.getIcdCM().get(1).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 3) {
        opD.setIcdCm3(mrDetail.getIcdCM().get(2).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 4) {
        opD.setIcdCm4(mrDetail.getIcdCM().get(3).getCode());
      }
      if (mrDetail.getIcdCM().size() >= 5) {
        opD.setIcdCm5(mrDetail.getIcdCM().get(4).getCode());
      }
    }
    if (mrDetail.getCureItems() != null) {
      if (mrDetail.getCureItems().size() >= 1) {
        opD.setCureItemNo1(mrDetail.getCureItems().get(0).getCode());
      }
      if (mrDetail.getCureItems().size() >= 2) {
        opD.setCureItemNo1(mrDetail.getCureItems().get(1).getCode());
      }
      if (mrDetail.getCureItems().size() >= 3) {
        opD.setCureItemNo1(mrDetail.getCureItems().get(2).getCode());
      }
      if (mrDetail.getCureItems().size() >= 4) {
        opD.setCureItemNo1(mrDetail.getCureItems().get(3).getCode());
      }
    }
    if (mrDetail.getIcdOP() != null) {
      if (mrDetail.getIcdOP().size() >= 1) {
        opD.setIcdOpCode1(mrDetail.getIcdOP().get(0).getCode());
      }
      if (mrDetail.getCureItems().size() >= 2) {
        opD.setIcdOpCode2(mrDetail.getIcdOP().get(1).getCode());
      }
      if (mrDetail.getCureItems().size() >= 3) {
        opD.setIcdOpCode3(mrDetail.getIcdOP().get(2).getCode());
      }
    }
    opD.setApplCauseMark(mrDetail.getApplCauseMark());
  }

  public void initMRDetailValueByOpd(MRDetail detail, OP_D opD) {
    detail.setBirthday(opD.getIdBirthYmd());
    detail.setPayType(opD.getPayType());
    List<CodeBase> cureItems = new ArrayList<CodeBase>();
    addCodeBaseByCode(cureItems, "ICD10-PCS", opD.getCureItemNo1());
    addCodeBaseByCode(cureItems, "ICD10-PCS", opD.getCureItemNo2());
    addCodeBaseByCode(cureItems, "ICD10-PCS", opD.getCureItemNo3());
    addCodeBaseByCode(cureItems, "ICD10-PCS", opD.getCureItemNo4());
    if (cureItems.size() > 0) {
      detail.setCureItems(cureItems);
    }

    List<CodeBase> icdCM = new ArrayList<CodeBase>();
    addCodeBaseByCode(icdCM, "ICD10-CM", opD.getIcdCm1());
    addCodeBaseByCode(icdCM, "ICD10-CM", opD.getIcdCm2());
    addCodeBaseByCode(icdCM, "ICD10-CM", opD.getIcdCm3());
    addCodeBaseByCode(icdCM, "ICD10-CM", opD.getIcdCm4());
    addCodeBaseByCode(icdCM, "ICD10-CM", opD.getIcdCm5());
    if (icdCM.size() > 0) {
      detail.setIcdCM(icdCM);
    }

    List<CodeBase> icdOp = new ArrayList<CodeBase>();
    addCodeBaseByCode(icdCM, "ICD10-PCS", opD.getIcdOpCode1());
    addCodeBaseByCode(icdCM, "ICD10-PCS", opD.getIcdOpCode2());
    addCodeBaseByCode(icdCM, "ICD10-PCS", opD.getIcdOpCode3());
    if (icdOp.size() > 0) {
      detail.setIcdOP(icdOp);
    }

    detail.setApplCauseMark(opD.getApplCauseMark());
  }

  private void addCodeBaseByCode(List<CodeBase> list, String category, String code) {
    if (code == null || code.length() == 0) {
      return;
    }
    List<JsonSuggestion> query = redis.query(category, code);
    for (JsonSuggestion jsonSuggestion : query) {
      String[] ss = jsonSuggestion.getId().split(":");
      if (ss.length > 1 && ss[1].equals(code)) {
        CodeBase cb = new CodeBase();
        cb.setCode(code);
        cb.setDesc(jsonSuggestion.getValue());
        list.add(cb);
        break;
      }
    }
  }
  
  public String removeDash(String s) {
    if (s != null && s.indexOf('-') <= 2) {
      return s.substring(0, s.indexOf('-'));
    }
    return s;
  }
}
