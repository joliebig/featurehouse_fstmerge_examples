namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
    using System;
    using Exortech.NetReflector;
 [ReflectorType("userFilter")]
 public class UserFilter : IModificationFilter
 {
        public UserFilter()
        {
            this.UserNames = new string[0];
        }
        [ReflectorProperty("names")]
        public string[] UserNames { get; set; }
  public bool Accept(Modification m)
  {
   return Array.IndexOf(UserNames, m.UserName) >= 0;
  }
        public override string ToString()
        {
            return "UserFilter";
        }
 }
}
