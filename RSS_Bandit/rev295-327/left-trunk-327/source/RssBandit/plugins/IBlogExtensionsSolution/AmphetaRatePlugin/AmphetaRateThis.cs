using System;
using System.IO;
using System.Net;
using System.Web;
using System.Windows.Forms;
using System.Xml;
using System.Xml.XPath;
using Syndication.Extensibility;
namespace AmphetaRatePlugin
{
 public sealed class AmphetaRateThis : IBlogExtension
 {
  string _userID = null;
  public void BlogItem(System.Xml.XPath.IXPathNavigable rssFragment, bool edited)
  {
   if(this.UserID == null)
   {
    MessageBox.Show("You cannot submit a rating without a valid AmphetaRate user id. Please configure one.");
    return;
   }
   XPathNavigator navigator = rssFragment.CreateNavigator();
   string title = (string)navigator.Evaluate("string(//item/title/text())");
   string link = (string)navigator.Evaluate("string(//item/link/text())");
   using(RatingForm form = new RatingForm())
   {
    if(form.ShowDialog() == DialogResult.OK)
    {
     string url = String.Format("http://amphetarate.sourceforge.net/dinka-add-rating.php?rating={0}&title={1}&xmlurl={2}&desc={3}&link={4}&guid={5}&uid={6}&encoding={7}", form.SelectedRating, Encode(title), Encode(link), "", "", "", Encode(this.UserID), "unicode-entities");
     HttpWebRequest request = WebRequest.Create(url) as HttpWebRequest;
     request.Method = "GET";
     HttpWebResponse response = (HttpWebResponse)request.GetResponse();
     if(response.StatusCode != HttpStatusCode.OK)
     {
      MessageBox.Show("An error occurred while submitting the rating. The server responded with " + response.StatusDescription + ".");
     }
    }
   }
  }
  string Encode(string s)
  {
   return HttpUtility.UrlEncodeUnicode(s).Replace("+", "&20");
  }
  public bool HasEditingGUI
  {
   get
   {
    return false;
   }
  }
  public string DisplayName
  {
   get
   {
    return "AmphetaRate This...";
   }
  }
  public void Configure(System.Windows.Forms.IWin32Window parent)
  {
   string amphetaID = this.UserID;
   using(ConfigurationForm form = new ConfigurationForm())
   {
    form.AmphetaRateID = (amphetaID == null ? "" : amphetaID);
    DialogResult result = form.ShowDialog(parent);
    if(result == DialogResult.OK)
    {
     this.UserID = form.AmphetaRateID;
    }
   }
  }
  string UserID
  {
   get
   {
    if(_userID == null)
    {
     XmlDocument doc = null;
     if(File.Exists(ConfigurationPath))
     {
      doc = new XmlDocument();
      doc.Load(ConfigurationPath);
      XPathNavigator navigator = doc.CreateNavigator();
      _userID = ((string)navigator.Evaluate("string(//ID/text())")).Trim();
     }
    }
    return _userID;
   }
   set
   {
    if(value != null && value != _userID)
    {
     if(!Directory.Exists(Path.GetDirectoryName(ConfigurationPath)))
      Directory.CreateDirectory(Path.GetDirectoryName(ConfigurationPath));
     using(StreamWriter writer = new StreamWriter(ConfigurationPath, false))
     {
      writer.Write("<?xml version=\"1.0\" standalone=\"yes\" ?><AmphetaRateSettings><ID>" + value + "</ID></AmphetaRateSettings>");
     }
     _userID = value;
    }
   }
  }
  string ConfigurationPath
  {
   get
   {
    string configPath = System.Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData);
    return Path.Combine(configPath, @"AmphetaRate\AmphetaSettings.xml");
   }
  }
  public bool HasConfiguration
  {
   get
   {
    return true;
   }
  }
 }
}
