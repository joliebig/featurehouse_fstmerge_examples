using System;
namespace ThoughtWorks.CruiseControl.Core.Util
{
    public interface ILogger
    {
        void Debug(string message, params object[] values);
        void Info(string message, params object[] values);
        void Warning(string message, params object[] values);
        void Warning(Exception error);
        void Error(string message, params object[] values);
        void Error(Exception error);
    }
}
