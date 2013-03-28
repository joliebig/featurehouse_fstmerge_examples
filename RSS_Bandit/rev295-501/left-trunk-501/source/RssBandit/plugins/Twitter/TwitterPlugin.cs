using System;
using System.IO;
using System.Security;
using System.Xml.XPath;
using Syndication.Extensibility;
using System.Windows.Forms;
using System.Text;
using System.Xml;
using System.Xml.Serialization;
using System.Security.Cryptography;
using System.Net;
namespace BlogExtension.Twitter
{
 public class TwitterPlugin : IBlogExtension
 {
  string configFile;
  twitter configInfo;
  TripleDESCryptoServiceProvider cryptoProvider;
  XmlSerializer serializer;
  public TwitterPlugin()
  {
   string assemblyUri = this.GetType().Assembly.CodeBase;
   string assemblyPath = new Uri(assemblyUri).LocalPath;
   string assemblyDir = Path.GetDirectoryName(assemblyPath);
   configFile = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData), @"RssBandit\twitter.xml");
   try
   {
    if(!File.Exists(configFile) && File.Exists(Path.Combine(assemblyDir, "twitter.xml")))
    {
     string oldConfigPath = Path.Combine(assemblyDir, "twitter.xml");
     File.Copy(oldConfigPath, configFile, false);
     File.Delete(oldConfigPath);
    }
   }
   catch(IOException)
   {
   }
   catch(SecurityException)
   {
   }
   cryptoProvider = new TripleDESCryptoServiceProvider();
   cryptoProvider.Key = CalculateHash();
   cryptoProvider.Mode = CipherMode.ECB;
   serializer = new XmlSerializer(typeof(twitter));
  }
  public string DisplayName { get { return Resource.Manager["RES_MenuTwitterCaption"]; } }
  public bool HasConfiguration { get { return true; } }
  public bool HasEditingGUI { get { return true; } }
  public void Configure(IWin32Window parent)
  {
   this.LoadConfig();
   using (TwitterPluginConfigurationForm configForm = new TwitterPluginConfigurationForm(configInfo.username, this.Decrypt(configInfo.password), configInfo.apiurl))
   {
    if (configForm.ShowDialog(parent) == DialogResult.OK)
    {
     configInfo.apiurl = configForm.textUri.Text;
     configInfo.username = configForm.textUser.Text;
     configInfo.password = this.Encrypt(configForm.textPwd.Text);
     XmlTextWriter writer = new XmlTextWriter(configFile, Encoding.UTF8);
     serializer.Serialize(writer, configInfo);
    }
   }
  }
  public void BlogItem(IXPathNavigable rssFragment, bool edited) {
   HttpWebRequest request = null;
   HttpWebResponse response = null;
   try{
    this.LoadConfig();
    string url = rssFragment.CreateNavigator().Evaluate("string(//item/link/text())").ToString(), message = null;
                if((bool) rssFragment.CreateNavigator().Evaluate("starts-with(string(/rss/channel/link), 'http://twitter.com')")){
                    message = rssFragment.CreateNavigator().Evaluate("string(//item/title/text())").ToString();
                    int index = message.IndexOf(':');
                    if (index != -1) {
                        message = "@" + message.Substring(0, index);
                    }
                } else {
                    message = GetTinyUrl(url);
                }
    using (TwitterPostForm postForm = new TwitterPostForm(message)) {
     if (postForm.ShowDialog() == DialogResult.OK) {
      message = postForm.textPost.Text;
      Uri postUrl = new Uri(configInfo.apiurl + "?source=rssbandit&status=" + Uri.EscapeDataString(message));
      request = (HttpWebRequest) WebRequest.Create(postUrl);
      request.UserAgent = "twitterIBlogExtensionPlugin/1.0";
                        request.Method = "POST";
      NetworkCredential nc = new NetworkCredential(configInfo.username, this.Decrypt(configInfo.password));
      request.Credentials = nc;
      response = (HttpWebResponse) request.GetResponse();
      if(response.StatusCode != HttpStatusCode.OK){
       throw new Exception(new System.IO.StreamReader(response.GetResponseStream()).ReadToEnd());
      }
     }
    }
   }catch(Exception e){
    MessageBox.Show(e.Message, e.GetType().Name,MessageBoxButtons.OK, MessageBoxIcon.Error);
   }finally{
    if(response != null){
     response.Close();
    }
   }
  }
  private void LoadConfig(){
   if(configInfo == null){
    if(File.Exists(configFile)){
     XmlTextReader reader = new XmlTextReader(configFile);
     configInfo = (twitter) serializer.Deserialize(reader);
     reader.Close();
    }else{
     configInfo = new twitter();
                    configInfo.apiurl = "http://twitter.com/statuses/update.xml";
    }
   }
  }
        private static string GetTinyUrl(string url) {
            try {
                HttpWebRequest req = HttpWebRequest.Create("http://tinyurl.com/api-create.php?url=" + url) as HttpWebRequest;
                StreamReader r = new StreamReader(req.GetResponse().GetResponseStream());
                url = r.ReadToEnd();
                r.Close();
            } catch (Exception) {
            }
            return url;
        }
  private static byte[] CalculateHash() {
   string salt = "TwitterPlugin.4711";
   byte[] b = Encoding.Unicode.GetBytes(salt);
   int bLen = b.GetLength(0);
   Random r = new Random(1500450271);
   byte[] res = new Byte[500];
   int i = 0;
   for (i = 0; i < bLen && i < 500; i++)
    res[i] = (byte)(b[i] ^ r.Next(30, 127));
   while (i < 500) {
    res[i] = (byte)r.Next(30, 127);
    i++;
   }
   MD5CryptoServiceProvider csp = new MD5CryptoServiceProvider();
   return csp.ComputeHash(res);
  }
  private byte[] Encrypt(string str) {
   byte[] inBytes;
   byte[] ret;
   if (str == null)
    ret = null;
   else {
    if (str.Length == 0)
     ret = null;
    else {
     try {
      inBytes = Encoding.Unicode.GetBytes(str);
      ret = cryptoProvider.CreateEncryptor().TransformFinalBlock(inBytes, 0, inBytes.GetLength(0));
     }
     catch (Exception e) {
      MessageBox.Show("Exception in Encrypt: "+e.ToString(), "CryptHelper");
      ret = null;
     }
    }
   }
   return ret;
  }
  private string Decrypt(byte[] bytes) {
   byte[] tmp;
   string ret;
   if ((bytes == null) || (bytes.GetLength(0) == 0))
    ret = String.Empty;
   else {
    try {
     tmp = cryptoProvider.CreateDecryptor().TransformFinalBlock(bytes, 0, bytes.GetLength(0));
     ret = Encoding.Unicode.GetString(tmp);
    }
    catch (Exception e) {
     MessageBox.Show("Exception in Decrypt: "+e.ToString(), "CryptHelper");
     ret = String.Empty;
    }
   }
   return ret;
  }
 }
 [System.Xml.Serialization.XmlRootAttribute("twitter", Namespace="", IsNullable=false)]
 public class twitter {
  [System.Xml.Serialization.XmlElementAttribute("api-url")]
  public string apiurl;
  public string username;
  public byte[] password;
 }
}
