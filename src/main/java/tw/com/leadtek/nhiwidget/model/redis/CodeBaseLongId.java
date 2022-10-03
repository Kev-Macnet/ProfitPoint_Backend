/**
 * Created on 2021/1/18.
 */
package tw.com.leadtek.nhiwidget.model.redis;

import com.fasterxml.jackson.annotation.JsonInclude;
import tw.com.leadtek.nhiwidget.model.CodeBase;

/**
 * ICD10 匯至 redis 用
 * @author 2268
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CodeBaseLongId extends CodeBase {

  // private static final long serialVersionUID = 3129673102068086430L;

  protected Long id;

  protected String category;

  public CodeBaseLongId() {

  }

  public CodeBaseLongId(long id, String code, String desc, String descEn) {
    this.id = Long.valueOf(id);
    setCode(code);
    setDesc(desc);
    setDescEn(descEn);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

}
