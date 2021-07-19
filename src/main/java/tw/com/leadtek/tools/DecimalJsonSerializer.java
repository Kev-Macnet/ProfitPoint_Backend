/**
 * Created on 2021/1/28.
 */
package tw.com.leadtek.tools;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Float object 轉成 xml 時，會有 .0 狀況，因此用此 class 刪掉 .0
 * @author 2268
 *
 */
public class DecimalJsonSerializer extends JsonSerializer<Float> {
  @Override
  public void serialize(Float value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException, JsonProcessingException {
    String s = String.format("%.1f", value);
    if (s.endsWith(".0")) {
      s = s.substring(0, s.length() - 2);
    }
    jgen.writeString(s);
  }
}
