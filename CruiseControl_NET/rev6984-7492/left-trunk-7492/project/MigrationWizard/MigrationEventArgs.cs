using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.MigrationWizard
{
    public class MigrationEventArgs
        : EventArgs
    {
        public MigrationEventArgs(string message, MigrationEventType type)
        {
            Message = message;
            Type = type;
            Time = DateTime.Now;
        }
        public string Message { get; private set; }
        public MigrationEventType Type { get; private set; }
        public DateTime Time { get; private set; }
    }
}
