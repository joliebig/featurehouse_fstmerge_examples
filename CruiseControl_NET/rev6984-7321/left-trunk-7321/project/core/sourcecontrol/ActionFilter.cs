using System;
using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
 [ReflectorType("actionFilter")]
 public class ActionFilter : IModificationFilter
 {
        public ActionFilter()
        {
            this.Actions = new string[0];
        }
        [ReflectorProperty("actions")]
        public string[] Actions { get; set; }
  public bool Accept(Modification m)
  {
   return Array.IndexOf(Actions, m.Type) >= 0;
  }
        public override string ToString()
        {
            return "ActionFilter";
        }
 }
}
