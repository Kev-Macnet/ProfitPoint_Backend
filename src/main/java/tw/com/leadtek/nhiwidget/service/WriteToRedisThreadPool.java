/**
 * Created on 2021/1/18.
 */
package tw.com.leadtek.nhiwidget.service;

import java.util.Vector;

public class WriteToRedisThreadPool implements Runnable {

  private final static int MAX = 100;

  private Vector<WriteToRedisThread> pps;

  //private WriteToRedis wtr;

  private static int inProcess = 0;
  
  private static int total = 0;
  
  private static boolean isFinished = false;

  public WriteToRedisThreadPool() {
    //this.wtr = wtr;
    pps = new Vector<WriteToRedisThread>(MAX);
  }

  @Override
  public void run() {
    System.out.println("===================================== start pool ===============================");
    int maxWaitTime = 2000;
    try {
      Thread.sleep(4000);
      do {
        if (!startProcess()) {
          for (int j = 0; j < maxWaitTime; j++) {
            Thread.sleep(100);
            if (startProcess()) {
              break;
            }
          }
        }
      } while (pps.size() > 0 || !isFinished);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("total=" + total);
  }
  
  public synchronized void addThread(WriteToRedisThread thread) {
    pps.add(thread);
  }

  private synchronized boolean startProcess() {
    if (inProcess < MAX && pps.size() > 0) {
      pps.remove(0).run();
      inProcess++;
      return true;
    }
    return false;
  }

  public synchronized void decrease() {
    inProcess--;
  }
  
  public synchronized int getThreadCount() {
    return pps.size();
  }
  
  public synchronized boolean isRunning() {
    return pps.size() > 0 || inProcess > 0;
  }

  public synchronized void addTotal() {
    total++;
  }

  public static boolean isFinished() {
    return isFinished;
  }

  public void setFinished(boolean isFinished) {
    WriteToRedisThreadPool.isFinished = isFinished;
  }
 
}
