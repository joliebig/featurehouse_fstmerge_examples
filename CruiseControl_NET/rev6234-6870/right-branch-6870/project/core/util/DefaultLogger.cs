using System;
namespace ThoughtWorks.CruiseControl.Core.Util
{
    public class DefaultLogger
        : ILogger
    {
        public virtual void Debug(string message, params object[] values)
        {
            if ((values != null) && (values.Length > 0))
            {
                Log.Debug(string.Format(message, values));
            }
            else
            {
                Log.Debug(message);
            }
        }
        public virtual void Info(string message, params object[] values)
        {
            if ((values != null) && (values.Length > 0))
            {
                Log.Info(string.Format(message, values));
            }
            else
            {
                Log.Info(message);
            }
        }
        public virtual void Warning(string message, params object[] values)
        {
            if ((values != null) && (values.Length > 0))
            {
                Log.Warning(string.Format(message, values));
            }
            else
            {
                Log.Warning(message);
            }
        }
        public virtual void Warning(Exception error)
        {
            Log.Warning(error);
        }
        public virtual void Error(string message, params object[] values)
        {
            if ((values != null) && (values.Length > 0))
            {
                Log.Error(string.Format(message, values));
            }
            else
            {
                Log.Error(message);
            }
        }
        public virtual void Error(Exception error)
        {
            Log.Error(error);
        }
    }
}
