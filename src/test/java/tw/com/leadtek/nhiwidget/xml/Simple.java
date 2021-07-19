/**
 * Created on 2020/12/25.
 */
package tw.com.leadtek.nhiwidget.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Simple {

  /**
   * 一般案件申請件數
   */
  @JacksonXmlProperty(localName = "tx")
  public int x = 1;
  
  /**
   * 一般案件申請件數
   */
  @JacksonXmlProperty(localName = "ty")
  public int y = 2;
}
