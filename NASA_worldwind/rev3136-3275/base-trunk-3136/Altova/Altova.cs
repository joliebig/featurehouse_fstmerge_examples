using System;
namespace Altova
{
 public class AltovaException : Exception
 {
  protected Exception innerException;
  protected string message;
  public AltovaException(string text) : base(text)
  {
   innerException = null;
   message = text;
  }
  public AltovaException(Exception other) : base("", other)
  {
   innerException = other;
   message = other.Message;
  }
  public string GetMessage()
  {
   return message;
  }
  public Exception GetInnerException()
  {
   return innerException;
  }
 }
 public interface TraceTarget
 {
  void WriteTrace(string info);
 }
 public abstract class TraceProvider
 {
  protected TraceTarget traceTarget = null;
  protected void WriteTrace(string info)
  {
   if (traceTarget != null)
    traceTarget.WriteTrace(info);
  }
  public void RegisterTraceTarget(TraceTarget newTraceTarget)
  {
   traceTarget = newTraceTarget;
  }
  public void UnregisterTraceTarget()
  {
   traceTarget = null;
  }
 }
}
