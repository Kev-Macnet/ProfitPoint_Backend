package tw.com.leadtek.nhiwidget.service;

import java.io.File;
import java.text.SimpleDateFormat;
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
      File[] files = new File(path).listFiles();
      if (files != null && files.length > 0) {
        systemService.refreshMRFromFolder(files);
      }
    }

}