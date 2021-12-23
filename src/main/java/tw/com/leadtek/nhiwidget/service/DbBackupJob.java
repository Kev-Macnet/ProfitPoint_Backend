package tw.com.leadtek.nhiwidget.service;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import tw.com.leadtek.tools.Utility;


@Component
@DisallowConcurrentExecution
public class DbBackupJob extends QuartzJobBean {
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private DbBackupService dbBackupService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    	java.util.Map<String, Object> mapSetting = dbBackupService.loadSetting(); 
        if (!mapSetting.isEmpty()) {
            int every = Utility.getMapInt(mapSetting, "every");
            String[] time = mapSetting.get("time").toString().split(":");
            if ((every>0)&&(time.length==2)) {
                logger.info("  -->定時備份任務跑起來");
//                System.out.println("  -->定時備份任務跑起來"+Utility.dateFormat(new java.util.Date(), "HH:mm:ss"));
                int mode = Integer.valueOf(mapSetting.get("mode").toString());
                int add = Integer.valueOf(mapSetting.get("add").toString());
                dbBackupService.dbBackupKernel(mode, "schedule task", (add==1) ? true : false);
            }
        }
    }
}
