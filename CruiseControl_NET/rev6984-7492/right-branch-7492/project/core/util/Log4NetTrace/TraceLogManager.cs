using System;
using System.Reflection;
using System.Collections;
using log4net;
using log4net.Core;
using log4net.Repository;
using log4net.Repository.Hierarchy;
namespace ThoughtWorks.CruiseControl.Core.Util.Log4NetTrace
{
 public class TraceLogManager
 {
  private static readonly WrapperMap s_wrapperMap = new WrapperMap(new WrapperCreationHandler(WrapperCreationHandler));
  private TraceLogManager() { }
  public static ITraceLog Exists(string name)
  {
   return Exists(Assembly.GetCallingAssembly(), name);
  }
  public static ITraceLog Exists(string domain, string name)
  {
   return WrapLogger(LoggerManager.Exists(domain, name));
  }
  public static ITraceLog Exists(Assembly assembly, string name)
  {
   return WrapLogger(LoggerManager.Exists(assembly, name));
  }
  public static ITraceLog[] GetCurrentLoggers()
  {
   return GetCurrentLoggers(Assembly.GetCallingAssembly());
  }
  public static ITraceLog[] GetCurrentLoggers(string domain)
  {
   return WrapLoggers(LoggerManager.GetCurrentLoggers(domain));
  }
  public static ITraceLog[] GetCurrentLoggers(Assembly assembly)
  {
   return WrapLoggers(LoggerManager.GetCurrentLoggers(assembly));
  }
  public static ITraceLog GetLogger(string name)
  {
   return GetLogger(Assembly.GetCallingAssembly(), name);
  }
  public static ITraceLog GetLogger(string domain, string name)
  {
   return WrapLogger(LoggerManager.GetLogger(domain, name));
  }
  public static ITraceLog GetLogger(Assembly assembly, string name)
  {
   return WrapLogger(LoggerManager.GetLogger(assembly, name));
  }
  public static ITraceLog GetLogger(Type type)
  {
   return GetLogger(Assembly.GetCallingAssembly(), type.FullName);
  }
  public static ITraceLog GetLogger(string domain, Type type)
  {
   return WrapLogger(LoggerManager.GetLogger(domain, type));
  }
  public static ITraceLog GetLogger(Assembly assembly, Type type)
  {
   return WrapLogger(LoggerManager.GetLogger(assembly, type));
  }
        private static ITraceLog WrapLogger(log4net.Core.ILogger logger)
  {
   return (ITraceLog)s_wrapperMap.GetWrapper(logger);
  }
        private static ITraceLog[] WrapLoggers(log4net.Core.ILogger[] loggers)
  {
   ITraceLog[] results = new ITraceLog[loggers.Length];
   for(int i=0; i<loggers.Length; i++)
   {
    results[i] = WrapLogger(loggers[i]);
   }
   return results;
  }
  private static ILoggerWrapper WrapperCreationHandler(log4net.Core.ILogger logger)
  {
   return new TraceLogImpl(logger);
  }
 }
}
