using System;
using Exortech.NetReflector;
using System.Xml.Serialization;
namespace ThoughtWorks.CruiseControl.Remote
{
 [ReflectorType("externalLink")]
 [Serializable]
    [XmlRoot("externalLink")]
 public class ExternalLink
 {
  private string name;
  private string url;
  public ExternalLink() : this (string.Empty, string.Empty) { }
  public ExternalLink(string name, string url)
  {
   this.name = name;
   this.url = url;
  }
  [ReflectorProperty("name")]
        [XmlAttribute("name")]
  public string Name
  {
   get { return name; }
   set { name = value; }
  }
  [ReflectorProperty("url")]
        [XmlAttribute("url")]
  public string Url
  {
   get { return url; }
   set { url = value; }
  }
 }
}
