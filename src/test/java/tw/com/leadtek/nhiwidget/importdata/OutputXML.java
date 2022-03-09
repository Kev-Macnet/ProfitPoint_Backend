/**
 * Created on 2022/2/21.
 */
package tw.com.leadtek.nhiwidget.importdata;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import tw.com.leadtek.nhiwidget.NHIWidget;
import tw.com.leadtek.nhiwidget.constant.XMLConstant;
import tw.com.leadtek.nhiwidget.dao.IP_DDao;
import tw.com.leadtek.nhiwidget.dao.IP_PDao;
import tw.com.leadtek.nhiwidget.dao.IP_TDao;
import tw.com.leadtek.nhiwidget.dao.MRDao;
import tw.com.leadtek.nhiwidget.dao.OP_DDao;
import tw.com.leadtek.nhiwidget.dao.OP_PDao;
import tw.com.leadtek.nhiwidget.dao.OP_TDao;
import tw.com.leadtek.nhiwidget.model.rdb.DHead;
import tw.com.leadtek.nhiwidget.model.rdb.IP_D;
import tw.com.leadtek.nhiwidget.model.rdb.IP_P;
import tw.com.leadtek.nhiwidget.model.rdb.IP_T;
import tw.com.leadtek.nhiwidget.model.rdb.OP_D;
import tw.com.leadtek.nhiwidget.model.rdb.OP_P;
import tw.com.leadtek.nhiwidget.model.rdb.OP_T;
import tw.com.leadtek.nhiwidget.model.xml.InPatient;
import tw.com.leadtek.nhiwidget.model.xml.InPatientDData;
import tw.com.leadtek.nhiwidget.model.xml.OutPatient;
import tw.com.leadtek.nhiwidget.model.xml.OutPatientDData;
import tw.com.leadtek.nhiwidget.service.NHIWidgetXMLService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NHIWidget.class)
@WebAppConfiguration
public class OutputXML {

  @Autowired
  private MRDao mrDao;
  
  @Autowired
  private OP_TDao optDao;
  
  @Autowired
  private OP_DDao opdDao;
  
  @Autowired
  private OP_PDao oppDao;
  
  @Autowired
  private IP_TDao iptDao;
  
  @Autowired
  private IP_DDao ipdDao;
  
  @Autowired
  private IP_PDao ippDao;
  
  @Autowired
  private NHIWidgetXMLService xmlService;
  
  @Test
  public void testOutput() {
    long time = System.currentTimeMillis();
    outputXML(XMLConstant.DATA_FORMAT_OP, "11001");
    long usedTime = System.currentTimeMillis() - time;
    System.out.println("op 11101 time:" + usedTime);
    time = System.currentTimeMillis();
    outputXML(XMLConstant.DATA_FORMAT_IP, "11001");
    usedTime = System.currentTimeMillis() - time;
    System.out.println("ip 11101 time:" + usedTime);
  }
  
  public void outputXML(String dataFormat, String applYm) {
    if (XMLConstant.DATA_FORMAT_OP.equals(dataFormat)) {
      outputOPXML(applYm);
    } else if (XMLConstant.DATA_FORMAT_IP.equals(dataFormat)) {
      outputIPXML(applYm);
    }
  }
  
  public void outputOPXML(String applYm) {
    List<OP_T> optList = optDao.findByFeeYmOrderById(applYm);
    OP_T opt = null;
    if (optList == null || optList.size() == 0) {
      opt = xmlService.createOPT(applYm);
    } else {
      opt = optList.get(0);
    }
    List<OP_D> opdList = opdDao.findByApplYM(applYm);
    List<OP_P> oppList = oppDao.findByApplYM(applYm);
    
    OutPatient op = new OutPatient();
    op.setTdata(opt);
    
    List<OutPatientDData> ddata = new ArrayList<OutPatientDData>();
    for (OP_D opd : opdList) {
      OutPatientDData opData = new OutPatientDData();
      DHead dhead = new DHead();
      dhead.setCASE_TYPE(opd.getCaseType());
      dhead.setSEQ_NO(opd.getSeqNo());
      opData.setDhead(dhead);
      
      List<OP_P> pdata = new ArrayList<OP_P>();
      for(int i=oppList.size() - 1; i>=0; i--) {
        OP_P opp = oppList.get(i);
        if (opp.getOpdId().longValue() == opd.getId().longValue()) {
          pdata.add(opp);
          oppList.remove(i);
        }
      }
      opd.setPdataList(pdata);
      opData.setDbody(opd);
      ddata.add(opData);
    }
    op.setDdata(ddata);
    //outputFile(op, applYm + "-0.xml");
    outputFileJAXB(op, applYm + "-0.xml");
    if (opdList.size() > 0 && oppList.size() > 0) {
      optDao.save(opt);
    }
  }
  
  public void outputIPXML(String applYm) {
    List<IP_T> iptList = iptDao.findByFeeYmOrderById(applYm);
    IP_T ipt = null;
    if (iptList == null || iptList.size() == 0) {
      ipt = xmlService.createIPT(applYm);
    } else {
      ipt = iptList.get(0);
    }
    List<IP_D> ipdList = ipdDao.findByApplYM(applYm);
    List<IP_P> ippList = ippDao.findByApplYM(applYm);
    
    InPatient ip = new InPatient();
    ip.setTdata(ipt);
    
    List<InPatientDData> ddata = new ArrayList<InPatientDData>();
    for (IP_D ipd : ipdList) {
      InPatientDData ipData = new InPatientDData();
      DHead dhead = new DHead();
      dhead.setCASE_TYPE(ipd.getCaseType());
      dhead.setSEQ_NO(ipd.getSeqNo());
      ipData.setDhead(dhead);
      
      List<IP_P> pdata = new ArrayList<IP_P>();
      for(int i=ippList.size() - 1; i>=0; i--) {
        IP_P ipp = ippList.get(i);
        if (ipp.getIpdId().longValue() == ipd.getId().longValue()) {
          pdata.add(ipp);
          ippList.remove(i);
        }
      }
      ipd.setPdataList(pdata);
      ipData.setDbody(ipd);
      ddata.add(ipData);
    }
    ip.setDdata(ddata);
    outputFileJAXB(ip, applYm + "-1.xml");
    if (ipdList.size() > 0 && ippList.size() > 0) {
      iptDao.save(ipt);
    }
  }
  
//  private void outputFile(Object obj, String filename) {
//    StringWriter stringWriter = new StringWriter();
//    XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
//    try {
//      XMLStreamWriter sw = xmlOutputFactory.createXMLStreamWriter(stringWriter);
//      XmlMapper xmlMapper = new XmlMapper();
//      // 空的 tag 不輸出
//      xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
//      xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
//      sw.writeStartDocument("Big5", "1.0");
//      xmlMapper.writeValue(sw, obj);
//      sw.writeEndDocument();
//      String xml = stringWriter.toString();
//
//      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
//          "D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\xml\\"
//              + filename),
//          "UTF-8"));
//      bw.write(xml);
//      bw.close();
//    } catch (UnsupportedEncodingException e) {
//      e.printStackTrace();
//    } catch (FileNotFoundException e) {
//      e.printStackTrace();
//    } catch (XMLStreamException e) {
//      e.printStackTrace();
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }
  
  private void outputFileJAXB(Object obj, String filename) {
    try {
      JacksonXmlModule xmlModule = new JacksonXmlModule();
      xmlModule.setDefaultUseWrapper(false);
      ObjectMapper objectMapper = new XmlMapper(xmlModule);
      objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
      objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
      String xml = objectMapper.writeValueAsString(obj);

      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\xml\\" + filename),
          "BIG5"));
      bw.write("<?xml version=\"1.0\" encoding=\"Big5\"?>");
      bw.newLine();
      bw.write(xml);
      bw.close();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
