using System;
using log4net;
namespace ThoughtWorks.CruiseControl.Core.Util.Log4NetTrace
{
 public interface ITraceLog : ILog
 {
  void Trace(object message);
  void Trace(object message, Exception t);
  void TraceFormat(string format, params object[] args);
  bool IsTraceEnabled { get; }
 }
}
