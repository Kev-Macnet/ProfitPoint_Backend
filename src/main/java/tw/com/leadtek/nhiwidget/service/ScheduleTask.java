package tw.com.leadtek.nhiwidget.service;

import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@EnableScheduling
//@EnableAsync 
@Component
public class ScheduleTask {
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private DbBackupService dbBackupService;
    
    @Autowired
    private UserService userService;
        
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Integer count1 = 1;
    

    @Scheduled(fixedDelay = 60000*10, initialDelay = 5000)
    public void runCurrentTimeEvery60m() throws InterruptedException {
        String msg = String.format("(10分鐘)第%d次執行，目前時間：%s",count1++, dateFormat.format(new java.util.Date()));
        System.out.println(msg);
        logger.info(msg);
        dbBackupProcess();
        userService.checkLoginUser();
//        try {
//            scheduleService.calcShareExpiration();
//        } catch(Exception ex) {
//            logger.info("err="+ex.getMessage());
//        }
    }
    
    //==
    public void dbBackupProcess() {
        boolean doIt = dbBackupService.isDoBackup();
        if (doIt==true) {
            System.out.println("Do it.......");
            java.util.Map<String, Object> mapSetting = dbBackupService.loadSetting();
          System.out.println("mapSetting------");
          System.out.println(mapSetting);
//        {every=2, week=1, month=1, time=02:23, mode=2, add=0}
            if (!mapSetting.isEmpty()) {
                int mode = Integer.valueOf(mapSetting.get("mode").toString());
                int add = Integer.valueOf(mapSetting.get("add").toString());
              
                dbBackupService.dbBackupKernel(mode, "schedule task", (add==1) ? true : false);
          }
        } else {
            System.out.println("今天備份過或未到時間.......");
        }
    }

}