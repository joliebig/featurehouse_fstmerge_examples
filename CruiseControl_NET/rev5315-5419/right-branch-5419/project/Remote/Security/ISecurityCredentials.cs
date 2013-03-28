using System;
using System.Collections.Generic;
using System.Text;
namespace ThoughtWorks.CruiseControl.Remote.Security
{
    public interface ISecurityCredentials
    {
        string Identifier { get; }
        string this[string credential] { get; set; }
        string Serialise();
        void Deserialise(string credentials);
        string[] Credentials { get; }
    }
}
