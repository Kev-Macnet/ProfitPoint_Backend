/**
 * Created on 2021/11/5.
 */
package tw.com.leadtek.nhiwidget.payload.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("名稱陣列 + 值陣列+ 值2陣列，名稱與值及值2為1對1")
public class NameValueList3 implements Serializable {

	private static final long serialVersionUID = 6134412943825576967L;

	@ApiModelProperty(value = "名稱陣列", required = true)
	protected List<String> names;

	@ApiModelProperty(value = "值陣列", required = true)
	protected List<Long> values;

	@ApiModelProperty(value = "值陣列2", required = true)
	protected List<Long> values2;

	public NameValueList3() {
		names = new ArrayList<String>();
		values = new ArrayList<Long>();
		values2 = new ArrayList<Long>();
	}

	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}

	public List<Long> getValues() {
		return values;
	}

	public void setValues(List<Long> values) {
		this.values = values;
	}

	public List<Long> getValues2() {
		return values2;
	}

	public void setValues2(List<Long> values2) {
		this.values2 = values2;
	}

	public void append(String name, long value, long value2) {
		names.add(names.size(), name);
		values.add(values.size(), value);
		values2.add(values.size(), value2);
	}

	public void add(String name, long value, long value2) {
		names.add(0, name);
		values.add(0, value);
		values2.add(0, value2);
	}

}
