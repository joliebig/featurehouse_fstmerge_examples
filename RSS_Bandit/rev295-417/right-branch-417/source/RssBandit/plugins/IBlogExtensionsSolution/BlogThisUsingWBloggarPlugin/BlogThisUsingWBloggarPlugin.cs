using System;
using System.Reflection;
using System.Windows.Forms;
using System.Xml;
using System.IO;
using System.Xml.XPath;
using System.Xml.Xsl;
using Syndication.Extensibility;
using System.Diagnostics;
using Microsoft.Win32;
namespace Haack.Rss.BlogExtensions
{
 public sealed class BlogThisUsingWbloggarPlugin : IBlogExtension
 {
  RegistryKey _registryKey = null;
  BlogThisType _blogType = BlogThisType.None;
  public bool HasConfiguration { get {return true; } }
  public bool HasEditingGUI{ get {return true; } }
  public void Configure(IWin32Window parent)
  {
   using(ConfigurationForm form = new ConfigurationForm())
   {
    form.BlogType = this.BlogType;
    DialogResult result = form.ShowDialog(parent);
    if(result == DialogResult.OK)
    {
     this.BlogType = form.BlogType;
    }
   }
  }
  public string DisplayName { get { return "Blog This Using w.bloggar..."; } }
  public void BlogItem(System.Xml.XPath.IXPathNavigable rssFragment, bool edited)
  {
   if(!IsBloggarInstalled)
   {
    throw new ApplicationException("No registry setting for w.bloggar located. Please install w.bloggar from_ http://www.wbloggar.com to get this feature to work");
   }
   string wbloggarPath = ((string)BloggarRegistryKey.GetValue("InstallPath"));
   XslTransform transform = new XslTransform();
   transform.Load(new XmlTextReader(XsltStream), null, null);
   string tempfile = Path.GetTempFileName();
   transform.Transform(rssFragment, null, new StreamWriter(tempfile), null);
   Process.Start(wbloggarPath + @"\wbloggar.exe", tempfile);
  }
  Stream XsltStream
  {
   get
   {
    string resourceName = "Haack.Rss.BlogExtensions.Resources." + this.BlogType.ToString() + ".xslt";
    return Assembly.GetExecutingAssembly().GetManifestResourceStream(resourceName);
   }
  }
  bool IsBloggarInstalled
  {
   get
   {
    return BloggarRegistryKey != null;
   }
  }
  RegistryKey BloggarRegistryKey
  {
   get
   {
    if(_registryKey == null)
     _registryKey = Registry.CurrentUser.OpenSubKey(@"Software\VB and VBA Program Settings\Bloggar");
    return _registryKey;
   }
  }
  BlogThisType BlogType
  {
   get
   {
    if(_blogType != BlogThisType.None)
     return _blogType;
    try
    {
     XmlDocument doc = null;
     if(File.Exists(ConfigurationPath))
     {
      doc = new XmlDocument();
      doc.Load(ConfigurationPath);
      XPathNavigator navigator = doc.CreateNavigator();
      _blogType = (BlogThisType)Enum.Parse(typeof(BlogThisType), ((string)navigator.Evaluate("string(//blogType/text())")).Trim());
      return _blogType;
     }
    }
    catch(Exception)
    {
    }
    return BlogThisType.LinkOnly;
   }
   set
   {
    try
    {
     if(!Directory.Exists(Path.GetDirectoryName(ConfigurationPath)))
      Directory.CreateDirectory(Path.GetDirectoryName(ConfigurationPath));
     bool append = true;
     using(StreamWriter writer = new StreamWriter(ConfigurationPath, !append))
     {
      writer.Write("<?xml version=\"1.0\" standalone=\"yes\" ?><blogThisUsingWBloggarPluginSettings><blogType>" + value.ToString() + "</blogType></blogThisUsingWBloggarPluginSettings>");
     }
    }
    finally
    {
     _blogType = value;
    }
   }
  }
  string ConfigurationPath
  {
   get
   {
    string configPath = System.Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData);
    return Path.Combine(configPath, @"BlogThisUsingWBloggarPlugin\wBloggarSettings.xml");
   }
  }
  internal enum BlogThisType
  {
   None,
   LinkOnly,
   LinkWithAuthor,
   Full
  }
 }
}
