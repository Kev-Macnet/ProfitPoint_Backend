package tw.com.leadtek.nhiwidget.service;

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
        
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Integer count1 = 1;

    @Scheduled(fixedDelay = 60000*10, initialDelay = 5000)
    public void runCurrentTimeEvery60m() throws InterruptedException {
        String msg = String.format("(10分鐘)第%d次執行，目前時間：%s",count1++, dateFormat.format(new java.util.Date()));
        logger.info(msg);
        userService.checkLoginUser();
    }

}