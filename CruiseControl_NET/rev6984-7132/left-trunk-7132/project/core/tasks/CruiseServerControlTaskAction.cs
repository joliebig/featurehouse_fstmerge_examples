using System;
using System.Collections.Generic;
using System.Text;
using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    [ReflectorType("controlAction")]
    public class CruiseServerControlTaskAction
    {
        [ReflectorProperty("project", Required = false)]
        public string Project { get; set; }
        [ReflectorProperty("type")]
        public CruiseServerControlTaskActionType Type { get; set; }
    }
}
