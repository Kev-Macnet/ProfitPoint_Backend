/**
 * Created on 2021/4/7.
 */
package tw.com.leadtek.nhiwidget.drg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CallDRGService {

  private StringBuffer sb;

  public CallDRGService() {

  }

  public void mergeOneFile(String folder) {
    int seq = 900001;
    File[] files = new File(folder).listFiles();
    sb = new StringBuffer();
    for (File file : files) {
      if (file.getName().endsWith("A.txt")) {
        seq = readFile(sb, file, seq);
      }
      if (seq > 900009) {
        break;
      }
    }
    writeFile(sb, "C:\\med\\S_DRGService_3412\\data\\202104071825A.txt");
  }

  private int readFile(StringBuffer sb, File file, int seq) {
    int result = seq;
    try {
      BufferedReader br = new BufferedReader(new FileReader(file));
      String s = null;
      while ((s = br.readLine()) != null) {
        String[] ss = s.split(",");
        for (int i = 0; i < ss.length; i++) {
          if (i == 3) {
            sb.append(result);
            result++;
          } else {
            sb.append(ss[i]);
          }
          sb.append(",");
        }
        for (int i = ss.length; i<56 - 2; i++) {
          sb.append(",");
        }
        sb.append("\n");
      }
      br.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  private void writeFile(StringBuffer sb, String filename) {
    try {
      BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)));
      bw.write(sb.toString());
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    CallDRGService call = new CallDRGService();
    call.mergeOneFile("D:\\Users\\2268\\2020\\健保點數申報\\docs_健保點數申報\\RDD-健保署DRG試算程式\\S_DRGServiceText");
  }

}
