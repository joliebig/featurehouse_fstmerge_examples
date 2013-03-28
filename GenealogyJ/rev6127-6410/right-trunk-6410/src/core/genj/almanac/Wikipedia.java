package genj.almanac;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormatSymbols;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Wikipedia {
  
  
  private static final String[] 
    DISCLAIMER = {
      "#                                                                       ",
      "# GenJ Almanac (C) 2004 Nils Meier <nils@meiers.net>                    ",
      "#                                                                       ",
      "# This document is licensed under the GNU Free Documentation License. It",
      "# uses material from the Wikipedia article \"{1}\" to \"{2}\" available at",
      "# http://{0}.wikipedia.org/wiki/{1} (and following).                    ",
      "#                                                                       ",
      "# Permission is granted to copy, distribute and/or modify this document ",
      "# under the terms of the GNU Free Documentation License, Version 1.2 or ",
      "# any later version published by the Free Software Foundation; with no  ",
      "# Invariant Sections, with no Front-Cover Texts, and with no Back-Cover ",
      "# Texts.                                                                ",
      "#                                                                       "
    };
  
  
  private static final Charset UTF8 = Charset.forName("UTF-8");
  
  
  private static Pattern 
  
    
    REGEXP_GROUP = Pattern.compile(" *(=+) *([^=]*?)[ =]*"),
    
    
    REGEXP_EVENT = Pattern.compile("\\*+[ ]*\\[\\[(.+?)\\]\\] *[-:] *(.*)"),
    
    
    REGEXP_INTERNALLINK  = Pattern.compile("\\[\\[([^\\[]*\\|)?([^\\[]*?)\\]\\]"),
    REGEXP_EXTERNALLINK  = Pattern.compile("\\[[^\\[]*?\\]"),
    
    
    REGEXP_MONTH = Pattern.compile("[^\\d \\.]+"),
    REGEXP_DAY   = Pattern.compile("[\\d]+");
  
  private static Object[] REGEXPNSUB = {
      
      Pattern.compile("'''(.*?)'''"  ), "<b>$1</b>",
      
      Pattern.compile("''(.*?)''"  ), "<em>$1</em>",
      
      Pattern.compile("(&amp;)"  ), "&",
  };

  
  private final static String 
    URL = "http://{0}.wikipedia.org/wiki/Special:Export/{1}";
  
  
  private String group = null;
  
  
  private static String[] months;
  
  
  private int imported = 0;

  
  public static void main(String[] args) {
    
    
    String lang, cmd;
    String[] ignore;
    PrintWriter out = null;
    int first, last;
    try {
      
      
      cmd   = args[0];
      if (!"read".equals(cmd))
        throw new IllegalArgumentException("unknown command "+cmd);

      
      lang  = args[1];
      months = new DateFormatSymbols(new Locale(lang)).getMonths();
      if (months==null||months.length<12)
        throw new IllegalArgumentException("no month name information for "+lang);
      for (int i=0;i<months.length;i++)
        months[i] = months[i].toLowerCase();
      
      
      first = Integer.parseInt(args[2]);
      last  = Integer.parseInt(args[3]);

      
      if (args.length>=5) {
        StringTokenizer tokens = new StringTokenizer(args[4], "|");
        ignore = new String[tokens.countTokens()];
        for (int i=0;i<ignore.length;i++) 
          ignore[i] = tokens.nextToken().toLowerCase();
      } else {
        ignore = new String[0];
      }
      
      
      if (args.length>=6) 
        out   = getOut(first, last, args[5] , lang);

    } catch (Throwable t) {
      log(true, "java genj.almanac.WikipediaImport read LANGUAGE FIRSTYEAR LASTYEAR IGNORE DIROUT");
      log(false," ("+t.getMessage()+")");
      System.exit(1);
      return;
    }
    
    
    new Wikipedia().read(lang, ignore, first, last, out);
    
    
    if (out!=null) {
      out.flush();
      out.close();
    }
    
    
  }
  
  
  private static void log(boolean system, String msg) {
    if (system)
      System.out.print("*** ");
    System.out.println(msg);
  }
  

  
  private void read(String lang, String[] ignore, int first, int last, PrintWriter out) {

    log(true, "Ignoring: "+Arrays.asList(ignore));
    
    
    if (out!=null) {
      String[] args = new String[]{ lang, ""+first, ""+last };
      for (int i=0;i<DISCLAIMER.length;i++) {
        out.println(new MessageFormat(DISCLAIMER[i]).format(args));
      }
    }
    
    
    for (int year=first;year<=last;year++) {
      
      String yyyy = getYYYY(year);
      if (yyyy==null)
        throw new IllegalArgumentException("can't create yyyy from "+year);
      
      try {
        
        String url = new MessageFormat(URL).format(new String[]{ lang, ""+year});
        
        
        readURL(yyyy, ignore, new URL(url), out);
        
        
      } catch (IOException e) {
        log(true, "IO error on reading "+year);
      }
      
    }
    
    
    log(true, "Read "+imported+" events for '"+lang+"' between "+first+" and "+last);
    
    
  }
  
  
  private static PrintWriter getOut(int first, int last, String dir, String lang) throws IOException {

    
    if (dir==null)
      return null;
    
    
    File file = new File(dir, lang+".wikipedia.zip");
    if (!file.exists())
      file.createNewFile();
    
    if (!file.canWrite())
      throw new IllegalArgumentException("can't write "+file);
    
    log(true, "Writing Wikipedia events into "+file.getAbsolutePath());
    
    
    System.setProperty("line.separator", "\n");
    
    
    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file));
    
    
    out.putNextEntry(new ZipEntry(lang+".wikipedia"));
    
    
    return new PrintWriter(new OutputStreamWriter(out, UTF8));
  }
  
  
  private void readURL(String yyyy, String[] ignore, URL url, PrintWriter out) throws IOException {
    
    
    HttpURLConnection con = (HttpURLConnection)url.openConnection();
    
    InputStream in = con.getInputStream();
    
    
    if (con.getResponseCode()!=HttpURLConnection.HTTP_OK) 
      throw new IOException(con.getResponseMessage());
    
    
    readPage(yyyy, ignore, in, out);
    
    
    con.disconnect();
      
    
  }
  
  
  private void readPage(String yyyy, String[] ignore, InputStream xml, PrintWriter out) throws IOException {
    
    group = null;
    
    BufferedReader in = new BufferedReader(new InputStreamReader(xml, UTF8));
    while (true) {
      String line = in.readLine();
      if (line==null||!readLine(yyyy, ignore, line, out)) 
        break;
    }
    
  }
  
  
  private boolean readLine(String yyyy, String[] ignore, String line, PrintWriter out) {
    
    
    Matcher matcher = REGEXP_GROUP.matcher(line);
    if (matcher.matches()) {

      int    lineLevel = matcher.group(1).length();
      String lineGroup = unformat(unlinkify(matcher.group(2)));

      if (lineLevel<1||lineLevel>3)
        return true;

      
      if (contains(lineGroup, ignore)) {
        group = null;
        return true;
      }

      
      if (contains(lineGroup, months))
        return true;

      
      group = lineGroup;
      
      return true;
    }
    
    
    if (group==null)
      return true;
    
    
    Matcher event = REGEXP_EVENT.matcher(line);
    if (event.matches())
      readEvent(yyyy, ignore, event, out);
    
    
    return true;
  }
  
  
  private void readEvent(String yyyy, String[] ignore, Matcher matcher, PrintWriter out) {
    
    
    String yyyymmdd = getYYYYMMDD(yyyy, matcher.group(1));
    if (yyyymmdd==null)
      return;
    
    
    String text = unformat(unlinkify(matcher.group(2).trim()));
    if (text.length()==0||contains(text, ignore))
      return;
    
    
    String event = yyyymmdd+"\\"+(group!=null?group:"-")+"\\"+text;
    if (out!=null)
      out.println(event);
    else
      log(false,event);
      
    imported++;
    
    
    return;
  }
  
  
  private String getYYYYMMDD(String yyyy, String monthday) {
    
    
    if (monthday.indexOf("-")>=0)
      return null;
    
    
    Matcher month = REGEXP_MONTH.matcher(monthday);
    if (!month.find())
      return null;
    String mm = getMM(month.group(0));
    if (mm==null)
      return null;

    
    Matcher day = REGEXP_DAY.matcher(monthday);
    if (!day.find())
      return null;
    String dd = getDD(day.group(0));
    if (dd==null)
      return null;
    
    
    return yyyy+mm+dd;
    
  }

  
  private String getMM(String month) {
    month = month.toLowerCase();
    for (int i=1;i<=months.length;i++) {
      if (months[i-1].equals(month))
        return i<10 ? "0"+i : ""+i;
    }
    
    return null;
  }
  
  
  private String getDD(String day) {

    try {
      int i = Integer.parseInt(day);
      return i<10 ? "0"+i : ""+i;
    } catch (NumberFormatException e) {
      return null;
    }
  }
  
  
  private String getYYYY(int year) {
    if (year<10)
      return "000"+year;
    if (year<100)
      return "00"+year;
    if (year<1000)
      return "0"+year;
    if (year<10000)
      return ""+year;
    return null;
  }
  
  
  private String unlinkify(String text) {
    
    if (text.length()>0) {
      
      text = REGEXP_INTERNALLINK.matcher(text).replaceAll("$2");
      
      text = REGEXP_EXTERNALLINK.matcher(text).replaceAll("");
    }
    
    
    return text;
  }
  
  
  private String unformat(String text) {
    
    for (int i=0;i<REGEXPNSUB.length;) {
      Matcher matcher = ((Pattern)REGEXPNSUB[i++]).matcher(text);
      text = matcher.replaceAll(REGEXPNSUB[i++].toString());
    }
    
    return text;
  }
  
  
  private boolean contains(String text, String[] subs) {
    text = text.toLowerCase();
    for (int i=0;i<subs.length;i++) {
      
      if (subs[i]==null||subs[i].length()==0)
        continue;
      
      if (text.indexOf(subs[i])>=0)
        return true;
    }
    return false;
  }
  
} 
