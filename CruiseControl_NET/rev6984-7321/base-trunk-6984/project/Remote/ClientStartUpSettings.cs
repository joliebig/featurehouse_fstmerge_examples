using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.Remote
{
    public class ClientStartUpSettings
    {
        public bool BackwardsCompatable { get; set; }
        public bool UseEncryption { get; set; }
        public bool FetchVersionOnStartUp { get; set; }
    }
}
