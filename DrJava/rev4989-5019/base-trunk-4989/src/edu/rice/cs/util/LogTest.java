

package edu.rice.cs.util;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.Random;

import edu.rice.cs.drjava.model.MultiThreadedTestCase;
import edu.rice.cs.plt.io.IOUtil;


public class LogTest extends MultiThreadedTestCase {
  
  public static final int TOL = 2000;
  
  
  
  
  private static class LogTestThread extends Thread {
    
    Log _log;
    int _millis;
    
    public LogTestThread(Log log, int millis) {
      _log = log;
      _millis = millis;
    }
    
    public void run() {
      try { sleep(_millis); }
      catch (Exception e ) {
        e.printStackTrace();
        fail("testConcurrent failed: sleep interrupted");
      }
      _log.log( "Test message" );
    }
  }

  
  private static Date parse(String s) {
    int pos = s.indexOf("GMT: ");
    if (pos == -1) { return null; }
    try {
      return Log.DATE_FORMAT.parse(s.substring(0,pos+3));
    }
    catch(ParseException pe) { return null; }
  }
  
  
  private static String getStringAfterDate(String s) {
    int pos = s.indexOf("GMT: ");
    if (pos==-1) { return null; }
    return s.substring(pos + 5);
  }
  
  
  private static boolean withinTolerance(Date earlier, Date time0, Date now) {
    return (time0.getTime() - earlier.getTime() < TOL) && (now.getTime() - time0.getTime() < TOL);
  }
  
  
  public void testLog() throws IOException {

    File file1 = IOUtil.createAndMarkTempFile("logtest001",".txt");
    
    
    Date earlier = new Date();
    
    Log log1 = new Log(file1, true);
    log1.log("Message 1");
    log1.log("Message 2");
    log1.log("Message 3");
    
    BufferedReader fin = new BufferedReader(new FileReader(file1));
    Date now = new Date();
    
    String s0 = fin.readLine();
    Date time0 = parse(s0);
    assertTrue("Log not opened after 'earlier' and before 'now'", withinTolerance(earlier, time0, now));
    
    String log1OpenMsg = "Log '" + file1.getName() + "' opened: ";
    assertEquals("Incorrect log open message", log1OpenMsg , getStringAfterDate(s0).substring(0, log1OpenMsg.length()));
    
    String s1 = fin.readLine();
    Date time1 = parse(s1);
    assertTrue("Date of message 1 not after 'earlier' and before 'now'", withinTolerance(earlier, time1, now));
    assertTrue("Date of message 1 not after 'log opened' and before 'now'", withinTolerance(time0, time1, now));
    assertEquals("Log message 1", "Message 1", getStringAfterDate(s1));
    
    String s2 = fin.readLine();
    Date time2 = parse(s2);
    assertTrue("Date of message 2 not after 'earlier' and before 'now'", withinTolerance(earlier, time2, now));
    assertTrue("Date of message 2 not after 'message 1' and before 'now'", withinTolerance(time1, time2, now));
    assertEquals("Log message 2", "Message 2", getStringAfterDate(s2));
    
    String s3 = fin.readLine();
    Date time3 = parse(s3);
    assertTrue("Date of message 3 not after 'earlier' and before 'now'", withinTolerance(earlier, time3, now));
    assertTrue("Date of message 3 not after 'message 2' and before 'now'", withinTolerance(time2, time3, now));
    assertEquals("Log message 3", "Message 3", getStringAfterDate(s3));
    
    assertEquals("End of log expected", null, fin.readLine());
    fin.close();

  }

  
  public void testExceptionPrinting() throws IOException {

    
    File file2 = IOUtil.createAndMarkTempFile("logtest002",".txt");
    
    
    Date earlier = new Date();
    
    Log log2 = new Log(file2, true);
    

    
    
    try { throw new ArrayIndexOutOfBoundsException(); }
    catch (ArrayIndexOutOfBoundsException e) {
      
      log2.log("Message 1", e);
    }
    
    String method = null;
    try { throw new NullPointerException(); }
    catch (NullPointerException e) {
      
      StackTraceElement[] stes = e.getStackTrace();
      method = "\tat "+stes[0].toString();
      log2.log("Message 2", stes);
    }
    
    BufferedReader fin = new BufferedReader(new FileReader(file2));
    Date now = new Date();
    
    String s0 = fin.readLine();
    Date time0 = parse(s0);
    assertTrue("Log not opened after 'earlier' and before 'now'", withinTolerance(earlier, time0, now));
    
    String log2OpenMsg = "Log '" + file2.getName() + "' opened: ";
    assertEquals("Incorrect log open message", log2OpenMsg , getStringAfterDate(s0).substring(0, log2OpenMsg.length()));
    
    String s1 = fin.readLine();
    Date time1 = parse(s1);
    assertTrue("Date of message 1 not after 'earlier' and before 'now'", withinTolerance(earlier, time1, now));
    assertTrue("Date of message 1 not after 'log opened' and before 'now'", withinTolerance(time0, time1, now));
    assertEquals("Log exception 1", "java.lang.ArrayIndexOutOfBoundsException", fin.readLine());
    
    
    String s2;
    Date time2;
    do {
      s2 = fin.readLine();
      time2 = parse(s2);  
    }
    while (time2 == null); 
    

    
    assertTrue("Date of message 2 not after 'earlier' and before 'now'", withinTolerance(earlier, time2, now));
    assertTrue("Date of message 2 not after 'message 1' and before 'now'", withinTolerance(time1, time2, now)); 
    assertEquals("Log message 2", "Message 2", getStringAfterDate(s2));
    assertEquals("Log exception 2 (trace line 1)", method, fin.readLine());
    
    fin.close();

  }   
  
  private static final int NUM_THREADS = 50;
  private static final int DELAY = 100;
  
  
  
  public void testConcurrentWrites() throws IOException, InterruptedException {

    
    File file3 = IOUtil.createAndMarkTempFile("logtest003",".txt");
    
    
    Date earlier = new Date();
    
    Log log3 = new Log(file3, true);
    Random r = new Random();
    Thread[] threads = new Thread[NUM_THREADS];
    for (int i = 0; i < NUM_THREADS; i++) threads[i] = new LogTestThread(log3, r.nextInt(DELAY));
    for (int i = 0; i < NUM_THREADS; i++) threads[i].start();
    for (int i = 0; i < NUM_THREADS; i++) threads[i].join();
    log3.close();
    
    BufferedReader fin = new BufferedReader(new FileReader(file3));
    Date now = new Date();
    String s0 = fin.readLine();
    Date time0 = parse(s0);
    
    
    
    assertTrue("Log not opened after 'earlier' and before 'now'", withinTolerance(earlier, time0, now));
    
    String log3OpenMsg = "Log '" + file3.getName() + "' opened: ";
    assertEquals("Incorrect log open message", log3OpenMsg , getStringAfterDate(s0).substring(0, log3OpenMsg.length()));
    
    for (int i = 0; i < NUM_THREADS; i++) {
      String s1 = fin.readLine();
      Date time1 = parse(s1);
      assertTrue("Date of message not after 'earlier' and before 'now'", withinTolerance(earlier, time1, now));
      assertTrue("Date of message not after 'previous time' and before 'now'", withinTolerance(time0, time1, now));
      assertEquals("Log message", "Test message", getStringAfterDate(s1));
      time0 = time1;
    } 
    
    fin.close();

  }
}

