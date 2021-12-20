package tw.com.leadtek.nhiwidget.service;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class QuartzUtils {

   @Autowired
   private Scheduler scheduler;

   public void addCronJob(Class<? extends Job> jobClass, String taskName, String cron) throws SchedulerException {
      JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(taskName).build();
      CronScheduleBuilder cronScheduler = CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionDoNothing();
      Trigger trigger = TriggerBuilder.newTrigger().startNow().withIdentity(taskName).withSchedule(cronScheduler).build();
      scheduler.scheduleJob(jobDetail, trigger);
   }


   public void modifyCron(String taskName, String cron) throws SchedulerException {
      TriggerKey triggerKey = TriggerKey.triggerKey(taskName);
      CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
      String oldTime = trigger.getCronExpression();
      if (!oldTime.equalsIgnoreCase(cron)) {
         CronScheduleBuilder cronBuilder = CronScheduleBuilder.cronSchedule(cron);
         TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
         triggerBuilder.withIdentity(taskName).startNow().withSchedule(cronBuilder);
         trigger = (CronTrigger) triggerBuilder.build();
         scheduler.rescheduleJob(triggerKey, trigger);
      }
   }


}