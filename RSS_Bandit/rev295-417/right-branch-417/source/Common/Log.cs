using System;
using log4net;
using log4net.Config;
using System.IO;
using System.Reflection;
namespace RssBandit.Common.Logging {
 internal class Log {
  private static ILog Logger;
  private static string LOGCONFIG = "RssBandit.exe.log4net.config";
  static Log() {
   if( File.Exists(Log4NetConfigFile)) {
    if (Environment.OSVersion.Platform == PlatformID.Win32NT) {
     XmlConfigurator.ConfigureAndWatch(new FileInfo(Log4NetConfigFile));
    }
    else {
     XmlConfigurator.Configure(new FileInfo(Log4NetConfigFile));
    }
   }
   else {
    BasicConfigurator.Configure();
   }
   Logger = GetLogger(typeof(Log));
  }
  public static string Log4NetConfigFile {
   get {
    return Path.Combine(Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location) , LOGCONFIG);
   }
  }
  public static void Error(string message,Exception exception) {
   try {
    Logger.Error(message,exception);
   }
   catch(Exception e) {
    throw new ApplicationException(String.Format("Logger.Error() failed on logging '{0}'.", message +"::"+ exception.Message), e);
   }
  }
  public static void Error(string message) {
   try {
    Logger.Error(message);
   }
   catch(Exception e) {
    throw new ApplicationException(String.Format("Logger.Error() failed on logging '{0}'.", message ), e);
   }
  }
  public static void Warning(string message,Exception exception) {
   try {
    Logger.Warn(message,exception);
   }
   catch(Exception e) {
    throw new ApplicationException(String.Format("Logger.Warn() failed on logging '{0}'.", message +"::"+ exception.Message), e);
   }
  }
  public static void Warning(string message) {
   try {
    Logger.Warn(message);
   }
   catch(Exception e) {
    throw new ApplicationException(String.Format("Logger.Warn() failed on logging '{0}'.", message), e);
   }
  }
  public static void Fatal(string message,Exception exception) {
   try {
    Logger.Fatal(message,exception);
   }
   catch(Exception e) {
    throw new ApplicationException(String.Format("Logger.Fatal() failed on logging '{0}'.", message +"::"+ exception.Message), e);
   }
  }
  public static void Fatal(string message) {
   try {
    Logger.Fatal(message);
   }
   catch(Exception e) {
    throw new ApplicationException(String.Format("Logger.Fatal() failed on logging '{0}'.", message), e);
   }
  }
  public static void Info(string message,Exception exception) {
   try {
    Logger.Info(message,exception);
   }
   catch(Exception e) {
    throw new ApplicationException(String.Format("Logger.Info() failed on logging '{0}'.", message +"::"+ exception.Message), e);
   }
  }
  public static void Info(string message) {
   try {
    Logger.Info(message);
   }
   catch(Exception e) {
    throw new ApplicationException(String.Format("Logger.Info() failed on logging '{0}'.", message), e);
   }
  }
  public static void Trace(string message) {
   Log.Info(message);
  }
  public static void Trace(string message, Exception exception) {
   Log.Info(message, exception);
  }
  public static void Debug(string message,Exception exception) {
   try {
    Logger.Debug(message,exception);
   }
   catch(Exception e) {
    throw new ApplicationException(String.Format("Logger.Debug() failed on logging '{0}'.", message +"::"+ exception.Message), e);
   }
  }
  public static void Debug(string message) {
   try {
    Logger.Debug(message);
   }
   catch(Exception e) {
    throw new ApplicationException(String.Format("Logger.Debug() failed on logging '{0}'.", message), e);
   }
  }
  public static ILog GetLogger(System.Type type) {
   return LogManager.GetLogger(type);
  }
 }
}
