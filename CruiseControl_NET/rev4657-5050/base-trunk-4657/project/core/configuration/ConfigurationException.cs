using System;
namespace ThoughtWorks.CruiseControl.Core.Config
{
 [Serializable]
 public class ConfigurationException : CruiseControlException
 {
  public ConfigurationException(string s) : base(s) {}
  public ConfigurationException(string s, Exception e) : base(s, e) {}
 }
 [Serializable]
 public class ConfigurationFileMissingException : ConfigurationException
 {
  public ConfigurationFileMissingException(string s) : base(s) {}
  public ConfigurationFileMissingException(string s, Exception e) : base(s, e) {}
 }
}
