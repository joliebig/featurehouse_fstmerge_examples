using System;
using System.Globalization;
using System.Text;
using System.Web;
using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol.Telelogic
{
 [ReflectorType("changeSynergy")]
 public class ChangeSynergyUrlBuilder : IModificationUrlBuilder
 {
  private string username;
  private string obfuscatedPassword;
  [ReflectorProperty("database", Required=false)]
  public string Database;
        [ReflectorProperty("username", Required = false)]
  public string Username
  {
   get { return username; }
   set
   {
    if (null == value)
    {
     username = null;
    }
    else
    {
     username = Environment.ExpandEnvironmentVariables(value);
    }
   }
  }
        [ReflectorProperty("password", Required = false)]
  public string Password
  {
   get { return obfuscatedPassword; }
   set
   {
    if (null == value)
    {
     obfuscatedPassword = null;
    }
    else
    {
     obfuscatedPassword = Environment.ExpandEnvironmentVariables(value);
     obfuscatedPassword = ObfuscatePassword(obfuscatedPassword);
    }
   }
  }
        [ReflectorProperty("role", Required = false)]
  public string Role;
        [ReflectorProperty("url")]
  public string Url;
  public void SetCredentials(SynergyConnectionInfo connection)
  {
   if (null == Database || 0 == Database.Length)
    Database = connection.Database;
  }
  public void SetupModification(Modification[] modifications)
  {
   const string trustedUrl = @"{0}/servlet/com.continuus.webpt.servlet.PTweb?ACTION_FLAG=frameset_form&TEMPLATE_FLAG=TaskDetailsView" + @"&task_number={{0}}&role={1}&database={2}&user={3}&generic_data={4}";
   const string loginUrl = @"{0}/servlet/com.continuus.webpt.servlet.PTweb?" + @"ACTION_FLAG=tokenless_form&TEMPLATE_FLAG=ConfirmLogin" + @"&role={1}&database={2}&context=";
   const string loginQuery = @"/servlet/com.continuus.webpt.servlet.PTweb?ACTION_FLAG=frameset_form&TEMPLATE_FLAG=TaskDetailsView" + @"&role={0}&database={1}&task_number=";
   string taskUrl;
   string taskQuery;
   if (null != username && 0 != username.Length)
   {
    taskUrl = String.Format(trustedUrl, Url, HttpUtility.UrlEncode(Role), HttpUtility.UrlEncode(Database), HttpUtility.UrlEncode(username), HttpUtility.UrlEncode(obfuscatedPassword));
   }
   else
   {
    taskQuery = String.Format(loginQuery, HttpUtility.UrlEncode(Role), HttpUtility.UrlEncode(Database));
    taskQuery = HttpUtility.UrlEncode(taskQuery);
    taskUrl = String.Format(loginUrl, Url, HttpUtility.UrlEncode(Role), HttpUtility.UrlEncode(Database));
    taskUrl = String.Concat(taskUrl, taskQuery, "{0}");
   }
   foreach (Modification modification in modifications)
   {
    modification.Url = String.Format(taskUrl, modification.ChangeNumber);
   }
  }
  public string ObfuscatePassword(int seed, string password)
  {
   StringBuilder obfuscatedValue;
   int asciiCode;
   if (null == password)
   {
    return (null);
   }
   obfuscatedValue = new StringBuilder(password.Length*4);
   obfuscatedValue.Append(seed.ToString(CultureInfo.InvariantCulture));
   obfuscatedValue.Append(":");
   foreach (char c in password)
   {
    asciiCode = ((int) c)*seed;
    obfuscatedValue.Append(asciiCode.ToString(CultureInfo.InvariantCulture));
    obfuscatedValue.Append(",");
   }
   obfuscatedValue.Remove(obfuscatedValue.Length - 1, 1);
   return (obfuscatedValue.ToString());
  }
  private string ObfuscatePassword(string password)
  {
   Random r = new Random();
   int seed = r.Next(0, 1000);
   return (ObfuscatePassword(seed, password));
  }
 }
}
