using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Text;
using System.Xml;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Config;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
    [ReflectorType("svn")]
    public class Svn : ProcessSourceControl
    {
        public const string DefaultExecutable = "svn";
        public static readonly string UtcXmlDateFormat = "yyyy-MM-ddTHH:mm:ssZ";
  private BuildProgressInformation _buildProgressInformation;
        public Svn(ProcessExecutor executor, IHistoryParser parser, IFileSystem fileSystem)
            : base(parser, executor)
        {
            this.fileSystem = fileSystem;
            this.AuthCaching = AuthCachingMode.None;
            this.Executable = DefaultExecutable;
            this.TagOnSuccess = false;
            this.TagWorkingCopy = false;
            this.DeleteObstructions = false;
            this.AutoGetSource = true;
            this.CheckExternals = false;
            this.CheckExternalsRecursive = true;
            this.CleanCopy = false;
            this.Revert = false;
            this.CleanUp = false;
        }
        public Svn()
            : this(new ProcessExecutor(), new SvnHistoryParser(), new SystemIoFileSystem())
        {
        }
        [ReflectorProperty("webUrlBuilder", InstanceTypeKey = "type", Required = false)]
        public IModificationUrlBuilder UrlBuilder { get; set; }
        [ReflectorProperty("executable", Required = false)]
        public string Executable { get; set; }
        [ReflectorProperty("trunkUrl", Required = false)]
        public string TrunkUrl { get; set; }
        [ReflectorProperty("workingDirectory", Required = false)]
        public string WorkingDirectory { get; set; }
        [ReflectorProperty("tagOnSuccess", Required = false)]
        public bool TagOnSuccess { get; set; }
        [ReflectorProperty("tagWorkingCopy", Required = false)]
        public bool TagWorkingCopy { get; set; }
        [ReflectorProperty("deleteObstructions", Required = false)]
        public bool DeleteObstructions { get; set; }
        [ReflectorProperty("tagBaseUrl", Required = false)]
        public string TagBaseUrl { get; set; }
        [ReflectorProperty("username", Required = false)]
        public string Username { get; set; }
        [ReflectorProperty("password", typeof(PrivateStringSerialiserFactory), Required = false)]
        public PrivateString Password { get; set; }
        [ReflectorProperty("autoGetSource", Required = false)]
        public bool AutoGetSource { get; set; }
        [ReflectorProperty("checkExternals", Required = false)]
        public bool CheckExternals { get; set; }
        [ReflectorProperty("checkExternalsRecursive", Required = false)]
        public bool CheckExternalsRecursive { get; set; }
        [ReflectorProperty("cleanCopy", Required = false)]
        public bool CleanCopy { get; set; }
        [ReflectorProperty("revert", Required = false)]
        public bool Revert { get; set; }
        [ReflectorProperty("cleanUp", Required = false)]
        public bool CleanUp { get; set; }
        [ReflectorProperty("revisionNumbers", Required = false)]
        public bool UseRevsionNumbers { get; set; }
        [ReflectorProperty("authCaching", Required = false)]
        public AuthCachingMode AuthCaching { get; set; }
        [ReflectorProperty("forceUpdate", Required = false)]
        public bool ForceUpdate { get; set; }
        private readonly IFileSystem fileSystem;
        internal Modification[] mods = new Modification[0];
        internal int latestRevision;
        public string FormatCommandDate(DateTime date)
        {
            return date.ToUniversalTime().ToString(UtcXmlDateFormat, CultureInfo.InvariantCulture);
        }
        private bool WorkingFolderIsKnownAsSvnWorkingFolder(string workingDirectory)
        {
            Log.Debug("Checking if {0} is a svn working folder", workingDirectory);
   if (!Directory.Exists(workingDirectory))
    return false;
            return Directory.GetDirectories(workingDirectory, ".svn").Length != 0 ||
                   Directory.GetDirectories(workingDirectory, "_svn").Length != 0;
        }
        private IList<string> ListObstructions(IIntegrationResult result)
        {
            var args = new PrivateArguments("status", "--xml");
            var info = this.NewProcessInfo(args, result);
            var processResult = Execute(info);
            var obstructions = new List<string>();
            var svnData = new XmlDocument();
            svnData.LoadXml(processResult.StandardOutput);
            var nodes = svnData.SelectNodes("//entry[wc-status/@item=\"obstructed\"]");
            foreach (XmlElement node in nodes)
            {
                obstructions.Add(node.GetAttribute("path"));
            }
            return obstructions;
        }
        private void DeleteObstructionsFromWorking(IIntegrationResult result)
        {
            Log.Info("Retrieving obstructions");
            var obstructions = this.ListObstructions(result);
            if (obstructions.Count == 0)
            {
                Log.Info("No obstructions found");
            }
            else
            {
                Log.Info(obstructions.Count.ToString() + " obstruction(s) found - deleting");
                var basePath = Path.GetFullPath(result.BaseFromWorkingDirectory(this.WorkingDirectory)); ;
                foreach (var obstruction in obstructions)
                {
                    var path = Path.Combine(basePath, obstruction);
                    Log.Info("Deleting folder " + path);
                    this.fileSystem.DeleteDirectory(path, true);
                }
            }
        }
        public override Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to)
        {
            var revisionData = NameValuePair.ToDictionary(from_.SourceControlData);
            if (to.LastIntegrationStatus == IntegrationStatus.Unknown)
            {
                ((SvnHistoryParser)historyParser).IntegrationStatusUnknown = true;
            }
            string wd = Path.GetFullPath(to.BaseFromWorkingDirectory(WorkingDirectory));
            if (WorkingFolderIsKnownAsSvnWorkingFolder(wd) )
            {
                if (CleanUp)
                {
                    Execute(CleanupWorkingCopy(to));
                }
                if (Revert)
                {
                    Execute(RevertWorkingCopy(to));
                }
                if (this.DeleteObstructions)
                {
                    this.DeleteObstructionsFromWorking(to);
                }
            }
            else
            {
                Util.Log.Warning(string.Format("{0} is not a svn working folder", wd));
            }
            List<Modification> modifications = new List<Modification>();
            List<string> repositoryUrls = new List<string>();
            repositoryUrls.Add(TrunkUrl);
            if (CheckExternals)
            {
                ProcessResult resultOfSvnPropget = Execute(PropGetProcessInfo(to));
                List<string> externals = ParseExternalsDirectories(resultOfSvnPropget);
                foreach (string external in externals)
                {
                    if (!repositoryUrls.Contains(external)) repositoryUrls.Add(external);
                }
            }
            foreach (string repositoryUrl in repositoryUrls)
            {
                var lastRepositoryRevisionName = "SVN:LastRevision:" + repositoryUrl;
                Modification[] modsInRepository;
                string lastRepositoryRevision = null;
                if (UseRevsionNumbers)
                {
                    lastRepositoryRevision = revisionData.ContainsKey(lastRepositoryRevisionName)
                        ? revisionData[lastRepositoryRevisionName]
                        : null;
                    ProcessResult result = Execute(NewHistoryProcessInfoFromRevision(lastRepositoryRevision, to, repositoryUrl));
                    modsInRepository = ParseModifications(result, lastRepositoryRevision);
                }
                else
                {
                    ProcessResult result = Execute(NewHistoryProcessInfo(from_, to, repositoryUrl));
                    modsInRepository = ParseModifications(result, from_.StartTime, to.StartTime);
                }
                if (modsInRepository != null)
                {
                    lastRepositoryRevision = Modification.GetLastChangeNumber(modsInRepository)
                        ?? lastRepositoryRevision;
                    modifications.AddRange(modsInRepository);
                    revisionData[lastRepositoryRevisionName] = lastRepositoryRevision;
                }
                if (repositoryUrl == TrunkUrl)
                {
                    latestRevision = int.Parse(lastRepositoryRevision ?? "0");
                    revisionData[lastRepositoryRevisionName] = lastRepositoryRevision;
                }
            }
            mods = modifications.ToArray();
            if (UrlBuilder != null)
            {
                UrlBuilder.SetupModification(mods);
            }
            FillIssueUrl(mods);
            to.SourceControlData.Clear();
            NameValuePair.Copy(revisionData, to.SourceControlData);
            return mods;
        }
        public override void LabelSourceControl(IIntegrationResult result)
        {
            if (TagOnSuccess && result.Succeeded)
            {
                Execute(NewLabelProcessInfo(result));
            }
        }
  private BuildProgressInformation GetBuildProgressInformation(IIntegrationResult result)
  {
   if (_buildProgressInformation == null)
                _buildProgressInformation = result.BuildProgressInformation;
   return _buildProgressInformation;
  }
  private void ProcessExecutor_ProcessOutput(object sender, ProcessOutputEventArgs e)
  {
   if (_buildProgressInformation == null)
    return;
   if (e.OutputType == ProcessOutputType.ErrorOutput)
    return;
   _buildProgressInformation.AddTaskInformation(e.Data);
  }
        private ProcessInfo PropGetProcessInfo(IIntegrationResult result)
        {
            var buffer = new PrivateArguments("propget");
            buffer.AddIf(CheckExternalsRecursive, "-R");
            AppendCommonSwitches(buffer);
            buffer.Add("svn:externals");
            buffer.Add(TrunkUrl);
            return NewProcessInfo(buffer, result);
        }
        private ProcessInfo RevertWorkingCopy(IIntegrationResult result)
        {
            var buffer = new PrivateArguments("revert", "--recursive");
            buffer.Add(null, Path.GetFullPath(result.BaseFromWorkingDirectory(WorkingDirectory)), true);
            return NewProcessInfo(buffer, result);
        }
        private ProcessInfo CleanupWorkingCopy(IIntegrationResult result)
        {
            var buffer = new PrivateArguments("cleanup");
   buffer.Add(null, Path.GetFullPath(result.BaseFromWorkingDirectory(WorkingDirectory)), true);
            return NewProcessInfo(buffer, result);
        }
        public override void GetSource(IIntegrationResult result)
        {
            result.BuildProgressInformation.SignalStartRunTask("Getting source from_ SVN");
            if (!AutoGetSource) return;
            if (DoesSvnDirectoryExist(result) && !CleanCopy)
            {
                UpdateSource(result);
            }
            else
            {
                if (CleanCopy)
                {
                    if (WorkingDirectory == null)
                    {
                        DeleteSource(result.WorkingDirectory);
                    }
                    else
                    {
                        DeleteSource(WorkingDirectory);
                    }
                }
                CheckoutSource(result);
            }
        }
        private void DeleteSource(string workingDirectory)
        {
            if (fileSystem.DirectoryExists(workingDirectory))
            {
                new IoService().DeleteIncludingReadOnlyObjects(workingDirectory);
            }
        }
        private void CheckoutSource(IIntegrationResult result)
        {
            if (string.IsNullOrEmpty(TrunkUrl))
                throw new ConfigurationException("<trunkurl> configuration element must be specified in order to automatically checkout source from_ SVN.");
   var bpi = GetBuildProgressInformation(result);
   bpi.SignalStartRunTask("Calling svn checkout ...");
   ProcessExecutor.ProcessOutput += ProcessExecutor_ProcessOutput;
            Execute(NewCheckoutProcessInfo(result));
   ProcessExecutor.ProcessOutput -= ProcessExecutor_ProcessOutput;
        }
        private ProcessInfo NewCheckoutProcessInfo(IIntegrationResult result)
        {
            var buffer = new PrivateArguments("checkout");
            buffer.Add(string.Empty, TrunkUrl, true);
            buffer.Add(null, Path.GetFullPath(result.BaseFromWorkingDirectory(WorkingDirectory)), true);
            AppendCommonSwitches(buffer);
            return NewProcessInfo(buffer, result);
        }
        private void UpdateSource(IIntegrationResult result)
        {
   var bpi = GetBuildProgressInformation(result);
   bpi.SignalStartRunTask("Calling svn update ...");
   ProcessExecutor.ProcessOutput += ProcessExecutor_ProcessOutput;
            Execute(NewGetSourceProcessInfo(result));
   ProcessExecutor.ProcessOutput -= ProcessExecutor_ProcessOutput;
        }
        private bool DoesSvnDirectoryExist(IIntegrationResult result)
        {
            string svnDirectory = Path.Combine(result.BaseFromWorkingDirectory(WorkingDirectory), ".svn");
            string underscoreSvnDirectory = Path.Combine(result.BaseFromWorkingDirectory(WorkingDirectory), "_svn");
            return fileSystem.DirectoryExists(svnDirectory) || fileSystem.DirectoryExists(underscoreSvnDirectory);
        }
        private ProcessInfo NewGetSourceProcessInfo(IIntegrationResult result)
        {
            var buffer = new PrivateArguments("update");
            buffer.Add(null, Path.GetFullPath(result.BaseFromWorkingDirectory(WorkingDirectory)), true);
            AppendRevision(buffer, latestRevision);
            AppendCommonSwitches(buffer);
            if (ForceUpdate) buffer.Add("--force");
            return NewProcessInfo(buffer, result);
        }
        private ProcessInfo NewLabelProcessInfo(IIntegrationResult result)
        {
            var buffer = new PrivateArguments("copy");
            buffer.Add(null, TagMessage(result.Label), true);
            buffer.Add(null, TagSource(result), true);
            buffer.Add(null, TagDestination(result.Label), true);
            if (!TagWorkingCopy)
                AppendRevision(buffer, latestRevision);
            AppendCommonSwitches(buffer);
            return NewProcessInfo(buffer, result);
        }
        private ProcessInfo NewHistoryProcessInfo(IIntegrationResult from_, IIntegrationResult to, string url)
        {
            var buffer = new PrivateArguments("log");
            buffer.Add(null, url, true);
            buffer.Add(string.Format("-r \"{{{0}}}:{{{1}}}\"", FormatCommandDate(from_.StartTime), FormatCommandDate(to.StartTime)));
            buffer.Add("--verbose --xml");
            AppendCommonSwitches(buffer, url != this.TrunkUrl);
            return NewProcessInfo(buffer, to);
        }
        private ProcessInfo NewHistoryProcessInfoFromRevision(string lastRevision, IIntegrationResult to, string url)
        {
            var buffer = new PrivateArguments("log");
            buffer.Add(null, url, true);
            buffer.Add(string.Format("-r {0}:HEAD", string.IsNullOrEmpty(lastRevision) ? "0" : lastRevision));
            buffer.Add("--verbose --xml");
            AppendCommonSwitches(buffer, url != this.TrunkUrl);
            return NewProcessInfo(buffer, to);
        }
        private static List<string> ParseExternalsDirectories(ProcessResult result)
        {
            List<string> externalDirectories = new List<string>();
            using (StringReader reader = new StringReader(result.StandardOutput))
            {
                string externalsDefinition;
                while ((externalsDefinition = reader.ReadLine()) != null)
                {
                    if (!externalsDefinition.Contains("-r") && !externalsDefinition.Equals(string.Empty))
                    {
                        int Pos = GetSubstringPosition(externalsDefinition);
                        if (Pos > 0)
                        {
                            externalsDefinition = externalsDefinition.Substring(Pos);
                        }
                        Pos = externalsDefinition.IndexOf(" ");
                        if (Pos > 0)
                        {
                            externalsDefinition = externalsDefinition.Substring(0, Pos);
                        }
                        if (!externalDirectories.Contains(externalsDefinition))
                        {
                            externalDirectories.Add(externalsDefinition);
                        }
                    }
                }
            }
            return externalDirectories;
        }
        private static int GetSubstringPosition(string externalsDefinition)
        {
            int pos = 0;
            string[] urlTypes = { "file:/", "http:/", "https:/", "svn:/", "svn+ssh:/" };
            foreach (string type in urlTypes)
            {
                int tmp = externalsDefinition.LastIndexOf(type);
                if (tmp > pos) pos = tmp;
            }
            return pos;
        }
        private static string TagMessage(string label)
        {
            return string.Format("-m \"CCNET build {0}\"", label);
        }
        private string TagSource(IIntegrationResult result)
        {
            if ((Modification.GetLastChangeNumber(mods) == null) || TagWorkingCopy)
            {
                return Path.GetFullPath(result.BaseFromWorkingDirectory(WorkingDirectory)).TrimEnd(Path.DirectorySeparatorChar);
            }
            return TrunkUrl;
        }
        private string TagDestination(string label)
        {
            return string.Format("{0}/{1}", TagBaseUrl, label);
        }
        private void AppendCommonSwitches(PrivateArguments buffer)
        {
            this.AppendCommonSwitches(buffer, false);
        }
        private void AppendCommonSwitches(PrivateArguments buffer, bool isExternal)
        {
            if ((this.AuthCaching != AuthCachingMode.Always) && (!isExternal || (this.AuthCaching == AuthCachingMode.None)))
            {
                buffer.AddIf(!string.IsNullOrEmpty(this.Username), "--username ", this.Username, true);
                buffer.AddIf(this.Password != null, "--password ", this.Password, true);
                buffer.Add("--no-auth-cache");
            }
            buffer.Add("--non-interactive");
        }
        private static void AppendRevision(PrivateArguments buffer, int revision)
        {
            buffer.AddIf(revision > 0, "--revision ", revision.ToString());
        }
        private ProcessInfo NewProcessInfo(PrivateArguments args, IIntegrationResult result)
        {
            string workingDirectory = Path.GetFullPath(result.BaseFromWorkingDirectory(WorkingDirectory));
            if (!Directory.Exists(workingDirectory)) Directory.CreateDirectory(workingDirectory);
            ProcessInfo processInfo = new ProcessInfo(Executable, args, workingDirectory);
            processInfo.StreamEncoding = Encoding.UTF8;
            return processInfo;
        }
        public enum AuthCachingMode
        {
            None,
            ExternalsOnly,
            Always,
        }
    }
}
