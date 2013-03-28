using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.CCTrayLib.X10
{
    public class Cm11LowLevelDriverError : System.EventArgs
    {
        public Cm11LowLevelDriverError(string message)
        {
            this.message = message;
        }
        private string message;
        public string Message
        {
            get { return message; }
            set { message = value; }
        }
    }
}
