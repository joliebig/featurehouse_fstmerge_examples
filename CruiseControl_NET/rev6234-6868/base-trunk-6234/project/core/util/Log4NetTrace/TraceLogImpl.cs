using System;
using System.Globalization;
using log4net.Core;
using log4net.Util;
namespace ThoughtWorks.CruiseControl.Core.Util.Log4NetTrace
{
 public class TraceLogImpl : LogImpl, ITraceLog
 {
  private readonly static Type ThisDeclaringType = typeof(TraceLogImpl);
  private readonly static Level s_defaultLevelTrace = new Level(20000, "TRACE");
  private Level m_levelTrace;
        public TraceLogImpl(log4net.Core.ILogger logger)
            : base(logger)
  {
  }
  protected override void ReloadLevels(log4net.Repository.ILoggerRepository repository)
  {
   base.ReloadLevels(repository);
   m_levelTrace = repository.LevelMap.LookupWithDefault(s_defaultLevelTrace);
  }
  public void Trace(object message)
  {
   Logger.Log(ThisDeclaringType, m_levelTrace, message, null);
  }
  public void Trace(object message, System.Exception t)
  {
   Logger.Log(ThisDeclaringType, m_levelTrace, message, t);
  }
  public void TraceFormat(string format, params object[] args)
  {
   if (IsTraceEnabled)
   {
    Logger.Log(ThisDeclaringType, m_levelTrace, new SystemStringFormat(CultureInfo.InvariantCulture, format, args), null);
   }
  }
  public void TraceFormat(IFormatProvider provider, string format, params object[] args)
  {
   if (IsTraceEnabled)
   {
    Logger.Log(ThisDeclaringType, m_levelTrace, new SystemStringFormat(provider, format, args), null);
   }
  }
  public bool IsTraceEnabled
  {
   get { return Logger.IsEnabledFor(m_levelTrace); }
  }
 }
}
