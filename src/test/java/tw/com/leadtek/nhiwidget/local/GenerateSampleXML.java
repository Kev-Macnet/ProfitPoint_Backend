/**
 * Created on 2022/3/22.
 */
package tw.com.leadtek.nhiwidget.local;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import tw.com.leadtek.nhiwidget.NHIWidget;
import tw.com.leadtek.nhiwidget.dao.IP_DDao;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.IP_TDao;
import tw.com.leadtek.nhiwidget.dao.OP_DDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dao.OP_TDao;
import tw.com.leadtek.nhiwidget.model.rdb.DHead;
import tw.com.leadtek.nhiwidget.model.rdb.IP;
import tw.com.leadtek.nhiwidget.model.rdb.IP_D;
import tw.com.leadtek.nhiwidget.model.rdb.IP_DData;
import tw.com.leadtek.nhiwidget.model.rdb.IP_T;
import tw.com.leadtek.nhiwidget.model.rdb.OP;
import tw.com.leadtek.nhiwidget.model.rdb.OP_D;
import tw.com.leadtek.nhiwidget.model.rdb.OP_DData;
import tw.com.leadtek.nhiwidget.model.rdb.OP_T;
import tw.com.leadtek.nhiwidget.service.NHIWidgetXMLService;
import tw.com.leadtek.nhiwidget.service.ParametersService;
import tw.com.leadtek.nhiwidget.service.ReportService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class GenerateSampleXML {

  private final static String FILE_PATH = "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\Test\\to廠商\\";
  
  /**
   * 每天匯出各科別最多幾筆
   */
  public final static int COUNT_BY_FUNC_TYPE = 4;
  
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
  private ParametersService parametersService;
  
  @Autowired
  private ReportService reportService;
  
  @Ignore
  @Test
  public void outputSample() {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -10);
    Calendar calMin = parametersService.getMinMaxCalendar(cal.getTime(), true);
    Calendar calMax = parametersService.getMinMaxCalendar(new Date(), false);
    
    do {
      int chineseYM = calMin.get(Calendar.YEAR) * 100 + calMin.get(Calendar.MONTH) + 1 - 191100;
      try {
        getNHIXMLFile(String.valueOf(chineseYM), "IP");
        getNHIXMLFile(String.valueOf(chineseYM), "OP");
      } catch (IOException e) {
        e.printStackTrace();
      }
      System.out.println(chineseYM);
      calMin.add(Calendar.MONTH, 1);
    } while (calMin.before(calMax));
  }
  
  public int getNHIXMLFile(String ym, String dataFormat)
      throws IOException {

    String filename = FILE_PATH + ym + ("OP".equals(dataFormat) ? "-0" : "-1") + ".xml";
    BufferedWriter bw =
        new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "BIG5"));
 
    XmlMapper xmlMapper = new XmlMapper();
    // 若值為 null 不會輸出到 String
    xmlMapper.setSerializationInclusion(Include.NON_NULL);
    xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
    Object data = null;
    if ("IP".equals(dataFormat)) {
      data = getIP(ym);
    } else if ("OP".equals(dataFormat)) {
      data = getOP(ym);
    }
    // xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
    String xml = xmlMapper.writeValueAsString(data);

    try {
      bw.write(NHIWidgetXMLService.DOCTYPE);
      bw.write(xml);
      bw.flush();
      bw.close();
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return -1;

  }
  
  /**
   * 取得住院申報資料
   * 
   * @param ym
   * @return
   */
  private IP getIP(String ym) {
    int MAX = COUNT_BY_FUNC_TYPE / 2;
    HashMap<String, HashMap<String, Integer>> dayCount = new HashMap<String, HashMap<String, Integer>>();
    IP result = new IP();
    List<IP_T> iptList = iptDao.findByFeeYmOrderById(ym);
    if (iptList != null && iptList.size() > 0) {
      IP_T ipt = iptList.get(0);
      result.setTdata(ipt);
      List<IP_DData> ip_DDataList = new ArrayList<IP_DData>();
      result.setDdata(ip_DDataList);
      List<IP_D> ipdList = ipdDao.findByIptId(ipt.getId());
      for (IP_D ip_D : ipdList) {
        HashMap<String, Integer> funcCount = dayCount.get(ip_D.getApplEndDate());
        if (funcCount == null) {
          funcCount = new HashMap<String, Integer>();
          dayCount.put(ip_D.getApplEndDate(), funcCount);
        }
        Integer count = funcCount.get(ip_D.getFuncType());
        if (count == null) {
          count = new Integer(0);
        }
        if (count.intValue() >= MAX) {
          continue;
        }
        funcCount.put(ip_D.getFuncType(), new Integer(count.intValue() + 1));
        
        IP_DData ip_Ddata = new IP_DData();
        DHead dHead = new DHead();
        dHead.setCASE_TYPE(ip_D.getCaseType());
        dHead.setSEQ_NO(ip_D.getSeqNo());
        ip_Ddata.setDhead(dHead);
        ip_D.setName(ip_D.getName().charAt(0) + "**");
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
    int MAX = COUNT_BY_FUNC_TYPE;
    HashMap<String, HashMap<String, Integer>> dayCount = new HashMap<String, HashMap<String, Integer>>();
    
    OP result = new OP();
    List<OP_T> optList = optDao.findByFeeYmOrderById(ym);
    if (optList != null && optList.size() > 0) {
      OP_T opt = optList.get(0);
      result.setTdata(opt);
      List<OP_DData> op_DDataList = new ArrayList<OP_DData>();
      result.setDdata(op_DDataList);
      List<OP_D> opdList = opdDao.findByOptId(opt.getId());
      for (OP_D op_D : opdList) {
        String date = (op_D.getFuncEndDate() == null) ? op_D.getFuncDate() : op_D.getFuncEndDate();
        HashMap<String, Integer> funcCount = dayCount.get(date);
        if (funcCount == null) {
          funcCount = new HashMap<String, Integer>();
          dayCount.put(date, funcCount);
        }
        Integer count = funcCount.get(op_D.getFuncType());
        if (count == null) {
          count = new Integer(0);
        }
        if (count.intValue() >= MAX) {
          continue;
        }
        funcCount.put(op_D.getFuncType(), new Integer(count.intValue() + 1));
        OP_DData op_Ddata = new OP_DData();
        DHead dHead = new DHead();
        dHead.setCASE_TYPE(op_D.getCaseType());
        dHead.setSEQ_NO(op_D.getSeqNo());
        op_Ddata.setDhead(dHead);
        op_D.setName(op_D.getName().charAt(0) + "**");
        op_Ddata.setDbody(op_D);
        op_DDataList.add(op_Ddata);

        op_D.setPdataList(oppDao.findByOpdIdOrderByOrderSeqNo(op_D.getId()));
      }
    }
    return result;
  }
  
  @Ignore
  @Test
  public void runMonthlyReport() {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -10);
    Calendar calMin = parametersService.getMinMaxCalendar(cal.getTime(), true);
    Calendar calMax = parametersService.getMinMaxCalendar(new Date(), false);

    do {
      int chineseYM = calMin.get(Calendar.YEAR) * 100 + calMin.get(Calendar.MONTH) + 1 - 191100;
      reportService.calculateDRGMonthly(String.valueOf(chineseYM));
      System.out.println(chineseYM);
      calMin.add(Calendar.MONTH, 1);
    } while (calMin.before(calMax));
  }
}
