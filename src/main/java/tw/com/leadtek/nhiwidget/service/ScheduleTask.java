package tw.com.leadtek.nhiwidget.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@EnableScheduling
//@EnableAsync 
@Component
public class ScheduleTask {
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ParametersService paramerterService;
    
    @Autowired
    private SystemService systemService;
        
//    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    private Integer count1 = 1;

    @Scheduled(fixedDelay = 60000*10, initialDelay = 5000)
    public void runCurrentTimeEvery60m() throws InterruptedException {
        userService.checkLoginUser();
    }
    
    @Scheduled(fixedDelay = 60000, initialDelay = 20000)
    public void runCurrentTimeEveryMinute() throws InterruptedException {
      String path = (paramerterService.getParameter("MR_PATH") == null) ? SystemService.FILE_PATH
          : paramerterService.getParameter("MR_PATH");
      ArrayList<File> files = new ArrayList<File>();
      findAllFiles(new File(path), files);
      if (files != null && files.size() > 0) {
        systemService.refreshMRFromFolder(files);
      }
    }
    
    private void findAllFiles(File file, ArrayList<File> files) {
      if (files == null) {
        return;
      }
      if (!file.isDirectory()) {
        checkFileAndAdd(file, files);
      } else if (file.isDirectory()) {
        File[] fileInDir = file.listFiles();
        for (File file2 : fileInDir) {
          if (file2.isFile()) {
            checkFileAndAdd(file2, files);
          } else {
            findAllFiles(file2, files);
          }
        }
      }
    }

    private void checkFileAndAdd(File file, ArrayList<File> files) {
      if (file.getName().indexOf('~') > -1 || !(file.getName().endsWith(".xlsx")
          || file.getName().endsWith(".xml") || file.getName().endsWith(".xls"))) {
        return;
      }
      files.add(file);
    }
    
}