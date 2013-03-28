using System;
using System.Text.RegularExpressions;
using System.Threading;
using System.Xml;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
 public class Vault3 : ProcessSourceControl
 {
  private static readonly Regex MatchVaultElements = new Regex("<vault>(?:.|\n)*</vault>", RegexOptions.IgnoreCase);
  private bool _labelApplied = false;
  protected VaultVersionChecker _shim;
  public Vault3(VaultVersionChecker versionCheckerShim) : base(new VaultHistoryParser())
  {
   _shim = versionCheckerShim;
   this.Timeout = _shim.Timeout;
  }
  public Vault3(VaultVersionChecker versionCheckerShim, IHistoryParser historyParser, ProcessExecutor executor) : base(historyParser, executor)
  {
   this._shim = versionCheckerShim;
   this.Timeout = _shim.Timeout;
  }
  public override Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to)
  {
   _labelApplied = false;
   Log.Info(string.Format("Checking for modifications to {0} in Vault Repository \"{1}\" between {2} and {3}", _shim.Folder, _shim.Repository, from_.StartTime, to.StartTime));
   ProcessResult result = ExecuteWithRetries(ForHistoryProcessInfo(from_, to));
            Modification[] modifications = ParseModifications(result, from_.StartTime, to.StartTime);
            base.FillIssueUrl(modifications);
            return modifications;
        }
  public override void LabelSourceControl(IIntegrationResult result)
  {
   if (! _shim.ApplyLabel) return;
   if (_shim.AutoGetSource)
   {
    if (result.Status != IntegrationStatus.Success)
    {
     if (_labelApplied)
     {
      Log.Info(string.Format(
       "Integration failed.  Removing label \"{0}\" from_ {1} in repository {2}.", result.Label, _shim.Folder, _shim.Repository));
      Execute(RemoveLabelProcessInfo(result));
     }
     else
      Log.Debug(string.Format(
       "Integration failed, but a label was never successfully applied to {0} in repository {1}, so skipping removal.",
       _shim.Folder, _shim.Repository));
    }
   }
   else
   {
    Log.Info(string.Format("Applying label \"{0}\" to {1} in repository {2}.", result.Label, _shim.Folder, _shim.Repository));
    Execute(LabelProcessInfo(result));
   }
  }
  public override void GetSource(IIntegrationResult result)
  {
   if (!_shim.AutoGetSource) return;
   _labelApplied = false;
            if (string.IsNullOrEmpty(_shim.WorkingDirectory) && !(!_shim.ApplyLabel && _shim.UseVaultWorkingDirectory && !_shim.CleanCopy))
   {
    _shim.WorkingDirectory = GetVaultWorkingFolder(result);
                if (string.IsNullOrEmpty(_shim.WorkingDirectory))
     throw new VaultException(
      string.Format("Vault user {0} has no working folder set for {1} in repository {2} and no working directory has been specified.",
                    _shim.Username, _shim.Folder, _shim.Repository));
   }
   if (_shim.ApplyLabel)
   {
    Log.Info(string.Format("Applying label \"{0}\" to {1} in repository {2}.", result.Label, _shim.Folder, _shim.Repository));
    Execute(LabelProcessInfo(result));
    _labelApplied = true;
   }
            if (_shim.CleanCopy && !string.IsNullOrEmpty(this._shim.WorkingDirectory))
   {
    Log.Debug("Cleaning out source folder: " + result.BaseFromWorkingDirectory(_shim.WorkingDirectory));
    new IoService().EmptyDirectoryIncludingReadOnlyObjects(result.BaseFromWorkingDirectory(_shim.WorkingDirectory));
   }
   Log.Info("Getting source from_ Vault");
   Execute(GetSourceProcessInfo(result, _shim.ApplyLabel));
  }
  public static string ExtractXmlFromOutput(string output)
  {
   string value = MatchVaultElements.Match(output).Value;
   if (value.Length == 0)
   {
    throw new VaultException(string.Format("The output does not contain the expected <vault> element: {0}", output));
   }
   return value;
  }
  private ProcessInfo GetSourceProcessInfo(IIntegrationResult result, bool getByLabel)
  {
   ProcessArgumentBuilder builder = new ProcessArgumentBuilder();
   if (getByLabel)
   {
    builder.AddArgument("getlabel", _shim.Folder);
    builder.AddArgument(result.Label);
    if (_shim.UseVaultWorkingDirectory)
     builder.AddArgument("-labelworkingfolder", result.BaseFromWorkingDirectory(_shim.WorkingDirectory));
    else
     builder.AddArgument("-destpath", result.BaseFromWorkingDirectory(_shim.WorkingDirectory));
   }
   else
   {
    builder.AddArgument("get", _shim.Folder);
    if (_shim.UseVaultWorkingDirectory)
     builder.AppendArgument("-performdeletions removeworkingcopy");
    else
     builder.AddArgument("-destpath", result.BaseFromWorkingDirectory(_shim.WorkingDirectory));
   }
   builder.AddArgument("-merge", "overwrite");
   builder.AppendArgument("-makewritable");
   builder.AddArgument("-setfiletime", _shim.setFileTime);
   AddCommonOptionalArguments(builder);
   return ProcessInfoFor(builder.ToString(), result);
  }
  private ProcessInfo LabelProcessInfo(IIntegrationResult result)
  {
   ProcessArgumentBuilder builder = new ProcessArgumentBuilder();
   builder.AddArgument("label", _shim.Folder);
   builder.AddArgument(result.Label);
   AddCommonOptionalArguments(builder);
   return ProcessInfoFor(builder.ToString(), result);
  }
  private ProcessInfo RemoveLabelProcessInfo(IIntegrationResult result)
  {
   ProcessArgumentBuilder builder = new ProcessArgumentBuilder();
   builder.AddArgument("deletelabel", _shim.Folder);
   builder.AddArgument(result.Label);
   AddCommonOptionalArguments(builder);
   return ProcessInfoFor(builder.ToString(), result);
  }
  protected ProcessInfo ForHistoryProcessInfo(IIntegrationResult from_, IIntegrationResult to)
  {
   ProcessInfo info = ProcessInfoFor(BuildHistoryProcessArgs(from_.StartTime, to.StartTime), from_);
   Log.Debug("Vault History command: " + info.ToString());
   return info;
  }
  protected ProcessInfo ProcessInfoFor(string args, IIntegrationResult result)
  {
   return new ProcessInfo(_shim.Executable, args, result.BaseFromWorkingDirectory(_shim.WorkingDirectory));
  }
  private string BuildHistoryProcessArgs(DateTime from_, DateTime to)
  {
   ProcessArgumentBuilder builder = new ProcessArgumentBuilder();
   builder.AddArgument("history", _shim.Folder);
   builder.AppendArgument(_shim.HistoryArgs);
   builder.AddArgument("-begindate", from_.ToString("s"));
   builder.AddArgument("-enddate", to.ToString("s"));
   AddCommonOptionalArguments(builder);
   return builder.ToString();
  }
  protected void AddCommonOptionalArguments(ProcessArgumentBuilder builder)
  {
   builder.AddArgument("-host", _shim.Host);
   builder.AddArgument("-user", _shim.Username);
   builder.AddHiddenArgument("-password", _shim.Password);
   builder.AddArgument("-repository", _shim.Repository);
   builder.AppendIf(_shim.Ssl, "-ssl");
   builder.AddArgument("-proxyserver", _shim.proxyServer);
   builder.AddArgument("-proxyport", _shim.proxyPort);
   builder.AddArgument("-proxyuser", _shim.proxyUser);
   builder.AddHiddenArgument("-proxypassword", _shim.proxyPassword);
   builder.AddArgument("-proxydomain", _shim.proxyDomain);
   builder.AppendArgument(_shim.otherVaultArguments);
  }
  protected string GetVaultWorkingFolder(IIntegrationResult result)
  {
   ProcessArgumentBuilder builder = new ProcessArgumentBuilder();
   builder.AddArgument("listworkingfolders");
   AddCommonOptionalArguments(builder);
   ProcessInfo processInfo = ProcessInfoFor(builder.ToString(), result);
   ProcessResult processResult = Execute(processInfo);
   XmlDocument xml = GetVaultResponse(processResult, processInfo);
   XmlNodeList workingFolderNodes = xml.SelectNodes("/vault/listworkingfolders/workingfolder");
   XmlAttribute repositoryFolderAtt;
   XmlAttribute localFolderAtt;
   foreach (XmlNode workingFolderNode in workingFolderNodes)
   {
    repositoryFolderAtt = workingFolderNode.Attributes["reposfolder"];
    localFolderAtt = workingFolderNode.Attributes["localfolder"];
    if (repositoryFolderAtt != null && localFolderAtt != null)
     if (repositoryFolderAtt.InnerText == _shim.Folder)
     {
      return localFolderAtt.InnerText;
     }
   }
   return null;
  }
  private XmlDocument GetVaultResponse(ProcessResult result, ProcessInfo info)
  {
   XmlDocument xml = new XmlDocument();
   try
   {
    xml.LoadXml(ExtractXmlFromOutput(result.StandardOutput));
   }
   catch (XmlException)
   {
    throw new VaultException(string.Format(
     "Unable to parse vault XML output for vault command: [{0}].  Vault Output: [{1}]", info.SafeArguments, result.StandardOutput));
   }
   return xml;
  }
  protected ProcessResult ExecuteWithRetries(ProcessInfo processInfo)
  {
   ProcessResult result = null;
   for(int i=0; i < _shim.pollRetryAttempts; i++)
   {
    try
    {
     result = Execute(processInfo);
     return result;
    }
    catch(CruiseControlException e)
    {
     if (i+1 == _shim.pollRetryAttempts)
      throw;
     else
     {
      Log.Warning(string.Format("Attempt {0} of {1}: {2}", i+1, _shim.pollRetryAttempts, e.ToString()));
      Log.Debug(string.Format("Sleeping {0} seconds", _shim.pollRetryWait));
      Thread.Sleep(_shim.pollRetryWait * 1000);
     }
    }
   }
   throw new CruiseControlException("This should never happen.  Failed to execute within the loop, there's probably an off-by-one error above.");
  }
  public class VaultException : CruiseControlException
  {
   public VaultException(string message) : base(message)
   {}
  }
 }
}
