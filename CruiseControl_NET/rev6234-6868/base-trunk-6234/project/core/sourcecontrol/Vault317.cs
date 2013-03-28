using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Globalization;
using System.Xml;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
 public class Vault317 : Vault3
 {
  private long _folderVersion;
  private long _lastTxID;
  private CultureInfo culture = CultureInfo.CurrentCulture;
  public Vault317(VaultVersionChecker versionCheckerShim) : base(versionCheckerShim)
  {
   _folderVersion = 0;
   _lastTxID = 0;
  }
  public Vault317(VaultVersionChecker versionCheckerShim, IHistoryParser historyParser, ProcessExecutor executor) : base(versionCheckerShim, historyParser, executor)
  {}
  public override Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to)
  {
   if (LookForChangesUsingVersionHistory(from_, to))
    return GetModificationsFromItemHistory(from_, to);
   else
   {
    Modification[] mods = {};
    return mods;
   }
  }
  private Modification[] GetModificationsFromItemHistory(IIntegrationResult from_, IIntegrationResult to)
  {
   Log.Info(string.Format("Retrieving detailed change list for {0} in Vault Repository \"{1}\" between {2} and {3}", _shim.Folder, _shim.Repository, from_.StartTime, to.StartTime));
   ProcessResult result = ExecuteWithRetries(ForHistoryProcessInfo(from_, to));
   Modification[] itemModifications = ParseModifications(result, from_.StartTime, to.StartTime);
   if (itemModifications == null || itemModifications.Length == 0)
    Log.Warning("Item history returned no changes.  Version history is supposed to determine if changes exist.  This is usually caused by clock skew between the CC.NET server and the Vault server.");
            var modList = new List<Modification>(itemModifications.Length);
   foreach (Modification mod in itemModifications)
   {
    if (int.Parse(mod.ChangeNumber) <= _lastTxID)
     modList.Add(mod);
   }
            Modification[] modifications = modList.ToArray();
            base.FillIssueUrl(modifications);
            return modifications;
        }
  private bool LookForChangesUsingVersionHistory(IIntegrationResult from_, IIntegrationResult to)
  {
   Log.Info(string.Format("Checking for modifications to {0} in Vault Repository \"{1}\" between {2} and {3}", _shim.Folder, _shim.Repository, from_.StartTime, to.StartTime));
   bool bFoundChanges = GetFolderVersion(from_, to);
   Log.Debug("The folder has" + (bFoundChanges ? " " : " not ") + "changed.");
   return bFoundChanges;
  }
  public override void GetSource(IIntegrationResult result)
  {
            result.BuildProgressInformation.SignalStartRunTask("Getting source from_ Vault");
   if (!_shim.AutoGetSource) return;
   Debug.Assert(_folderVersion > 0, "_folderVersion <= 0 when attempting to get source.  This shouldn't happen.");
   if (_shim.CleanCopy)
   {
    string cleanCopyWorkingFolder = null;
                if (string.IsNullOrEmpty(_shim.WorkingDirectory))
    {
     cleanCopyWorkingFolder = GetVaultWorkingFolder(result);
                    if (string.IsNullOrEmpty(cleanCopyWorkingFolder))
      throw new VaultException(
       string.Format("Vault user {0} has no working folder set for {1} in repository {2} and no working directory has been specified.",
                     _shim.Username, _shim.Folder, _shim.Repository));
    }
    else
     cleanCopyWorkingFolder = result.BaseFromWorkingDirectory(_shim.WorkingDirectory);
    Log.Debug("Cleaning out source folder: " + cleanCopyWorkingFolder);
    new IoService().EmptyDirectoryIncludingReadOnlyObjects(cleanCopyWorkingFolder);
   }
   Log.Info("Getting source from_ Vault");
   Execute(GetSourceProcessInfo(result));
  }
  public override void LabelSourceControl(IIntegrationResult result)
  {
   if (!_shim.ApplyLabel || result.Status != IntegrationStatus.Success) return;
   Debug.Assert(_folderVersion > 0, "_folderVersion <= 0 when attempting to label.  This shouldn't happen.");
   Log.Info(string.Format("Applying label \"{0}\" to version {1} of {2} in repository {3}.",
                          result.Label, _folderVersion, _shim.Folder, _shim.Repository));
   Execute(LabelProcessInfo(result));
  }
  private ProcessInfo LabelProcessInfo(IIntegrationResult result)
  {
   ProcessArgumentBuilder builder = new ProcessArgumentBuilder();
   builder.AddArgument("label", _shim.Folder);
   builder.AddArgument(result.Label);
   builder.AddArgument(_folderVersion.ToString());
   AddCommonOptionalArguments(builder);
   return ProcessInfoFor(builder.ToString(), result);
  }
  private bool GetFolderVersion(IIntegrationResult from_, IIntegrationResult to)
  {
   bool bFoundChanges = false;
   bool bForceGetLatestVersion = (_folderVersion == 0);
   ProcessResult result = ExecuteWithRetries(VersionHistoryProcessInfo(from_, to, bForceGetLatestVersion));
   string versionHistory = Vault3.ExtractXmlFromOutput(result.StandardOutput);
   XmlDocument versionHistoryXml = new XmlDocument();
   versionHistoryXml.LoadXml(versionHistory);
   XmlNodeList versionNodeList = versionHistoryXml.SelectNodes("/vault/history/item");
   XmlNode folderVersionNode = null;
   if (bForceGetLatestVersion)
   {
    Debug.Assert(versionNodeList.Count == 1, "Attempted to retrieve folder's current version and got no results.");
    folderVersionNode = versionNodeList.Item(0);
   }
   else
   {
    Debug.Assert(versionNodeList.Count == 0 || versionNodeList.Count == 1, "Vault versionhistory -rowlimit 1 returned more than 1 row.");
    if (versionNodeList.Count == 1)
    {
     folderVersionNode = versionNodeList.Item(0);
     bFoundChanges = true;
    }
   }
   if (folderVersionNode != null)
   {
    if (bForceGetLatestVersion)
    {
     XmlAttribute dateAttr = (XmlAttribute) folderVersionNode.Attributes.GetNamedItem("date");
     Debug.Assert(dateAttr != null, "date attribute not found in version history");
     DateTime dtLastChange = DateTime.Parse(dateAttr.Value, culture);
     if (dtLastChange > from_.StartTime)
      bFoundChanges = true;
    }
    XmlAttribute versionAttr = (XmlAttribute) folderVersionNode.Attributes.GetNamedItem("version");
    Debug.Assert(versionAttr != null, "version attribute not found in version history");
    _folderVersion = long.Parse(versionAttr.Value);
    Log.Debug("Most recent folder version: " + _folderVersion);
    XmlAttribute txIdAttr = (XmlAttribute) folderVersionNode.Attributes.GetNamedItem("txid");
    Debug.Assert(txIdAttr != null, "txid attribute not found in version history");
    _lastTxID = long.Parse(txIdAttr.Value);
    Log.Debug("Most recent TxID: " + _lastTxID);
   }
   return bFoundChanges;
  }
  private ProcessInfo VersionHistoryProcessInfo(IIntegrationResult from_, IIntegrationResult to, bool bForceGetLatestVersion)
  {
   ProcessArgumentBuilder builder = new ProcessArgumentBuilder();
   builder.AddArgument("versionhistory", _shim.Folder);
   if (!bForceGetLatestVersion)
   {
    if (_folderVersion != 0)
    {
     builder.AddArgument("-beginversion", (_folderVersion + 1).ToString());
    }
    else
    {
     builder.AddArgument("-begindate", from_.StartTime.ToString("s"));
     builder.AddArgument("-enddate", to.StartTime.ToString("s"));
    }
   }
   builder.AddArgument("-rowlimit", "1");
   AddCommonOptionalArguments(builder);
   return ProcessInfoFor(builder.ToString(), from_);
  }
  private ProcessInfo GetSourceProcessInfo(IIntegrationResult result)
  {
   ProcessArgumentBuilder builder = new ProcessArgumentBuilder();
   builder.AddArgument("getversion", _folderVersion.ToString());
   builder.AddArgument(_shim.Folder);
            if (!string.IsNullOrEmpty(_shim.WorkingDirectory))
   {
    builder.AddArgument(result.BaseFromWorkingDirectory(_shim.WorkingDirectory));
    if (_shim.UseVaultWorkingDirectory)
     builder.AppendArgument("-useworkingfolder");
   }
   builder.AddArgument("-merge", "overwrite");
   builder.AppendArgument("-makewritable");
   builder.AddArgument("-backup", "no");
   builder.AddArgument("-setfiletime", _shim.setFileTime);
   AddCommonOptionalArguments(builder);
   return ProcessInfoFor(builder.ToString(), result);
  }
 }
}
