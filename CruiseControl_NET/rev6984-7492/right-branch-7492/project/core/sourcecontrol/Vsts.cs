using System;
using System.Globalization;
using System.IO;
using System.Text;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
    [ReflectorType("vsts")]
    public class Vsts : ProcessSourceControl
    {
        private const string VS2010_32_REGISTRY_PATH = @"Software\Microsoft\VisualStudio\10.0";
        private const string VS2008_32_REGISTRY_PATH = @"Software\Microsoft\VisualStudio\9.0";
        private const string VS2005_32_REGISTRY_PATH = @"Software\Microsoft\VisualStudio\8.0";
        private const string VS2010_64_REGISTRY_PATH = @"Software\Wow6432Node\Microsoft\VisualStudio\10.0";
        private const string VS2008_64_REGISTRY_PATH = @"Software\Wow6432Node\Microsoft\VisualStudio\9.0";
        private const string VS2005_64_REGISTRY_PATH = @"Software\Wow6432Node\Microsoft\VisualStudio\8.0";
        private const string VS_REGISTRY_KEY = @"InstallDir";
        private const string DEFAULT_WORKSPACE_NAME = "CCNET";
        private const string TF_EXE = "TF.exe";
        private const string DEFAULT_WORKSPACE_COMMENT = "Temporary CruiseControl.NET Workspace";
        private const string UtcXmlDateFormat = "yyyy-MM-ddTHH:mm:ssZ";
        private readonly IRegistry registry;
        private VstsHistoryParser parser;
        private string executable;
        private class TfsWorkspaceStatus
        {
            public bool WorkspaceIsMappedCorrectly { get; set; }
            public bool WorkspaceExists { get; set; }
        }
        public Vsts(ProcessExecutor executor, IHistoryParser parser, IRegistry registry)
            : base(parser, executor)
        {
            this.registry = registry;
            this.executor = executor;
            this.parser = parser as VstsHistoryParser;
        }
        public Vsts() :
            this(new ProcessExecutor(), new VstsHistoryParser(), new Registry()) { }
        [ReflectorProperty("server")]
        public string Server { get; set; }
        [ReflectorProperty("executable", Required = false)]
        public string Executable
        {
            get { return executable ?? (executable = ReadTfFromRegistry()); }
            set { executable = value; }
        }
        [ReflectorProperty("project")]
        public string ProjectPath { get; set; }
        [ReflectorProperty("applyLabel", Required = false)]
        public bool ApplyLabel { get; set; }
        [ReflectorProperty("autoGetSource", Required = false)]
        public bool AutoGetSource { get; set; }
        [ReflectorProperty("username", Required = false)]
        public string Username { get; set; }
        [ReflectorProperty("password", typeof(PrivateStringSerialiserFactory), Required = false)]
        public PrivateString Password { get; set; }
        [ReflectorProperty("domain", Required = false)]
        public string Domain { get; set; }
        [ReflectorProperty("workingDirectory", Required = false)]
        public string WorkingDirectory { get; set; }
        [ReflectorProperty("cleanCopy", Required = false)]
        public bool CleanCopy { get; set; }
        [ReflectorProperty("force", Required = false)]
        public bool Force { get; set; }
        private string workspaceName;
        [ReflectorProperty("workspace", Required = false)]
        public string Workspace
        {
            get
            {
                if (workspaceName == null)
                {
                    workspaceName = DEFAULT_WORKSPACE_NAME;
                }
                if (string.IsNullOrEmpty(this.Username))
                {
                    return workspaceName + ";" + BuildTfsUsername();
                }
                return workspaceName;
            }
            set
            {
                workspaceName = value;
            }
        }
        [ReflectorProperty("deleteWorkspace", Required = false)]
        public bool DeleteWorkspace { get; set; }
        public override Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to)
        {
            if (!ProjectExists(from_))
            {
                Log.Error(String.Format("[TFS] Project {0} is not valid on {1} TFS server", ProjectPath, Server));
                throw new Exception("Project Name is not valid on this TFS server");
            }
            Log.Debug("[TFS] Checking Team Foundation Server for Modifications");
            Log.Debug("[TFS] from_: " + from_.StartTime + " - To: " + to.StartTime);
            ProcessResult result = executor.Execute(NewHistoryProcessInfo(from_, to));
            LookForErrorReturns(result);
            return ParseModifications(result, from_.StartTime, to.StartTime);
        }
        public override void LabelSourceControl(IIntegrationResult result)
        {
            if (ApplyLabel && result.Succeeded)
            {
                LookForErrorReturns(executor.Execute(NewLabelProcessInfo(result)));
            }
        }
        public override void GetSource(IIntegrationResult result)
        {
            if (!AutoGetSource || !ProjectExists(result)) return;
            this.WorkingDirectory = result.BaseFromWorkingDirectory(this.WorkingDirectory);
            if (CleanCopy)
            {
                Log.Debug("[TFS] Deleting " + this.WorkingDirectory);
                this.DeleteDirectory(this.WorkingDirectory);
            }
            TfsWorkspaceStatus workspaceStatus = GetWorkspaceStatus(result);
            if (workspaceStatus.WorkspaceExists)
            {
                if (DeleteWorkspace)
                {
                    Log.Debug("[TFS] Removing existing workspace " + Workspace);
                    LookForErrorReturns(executor.Execute(DeleteWorkSpaceProcessInfo(result)));
                    Log.Debug("[TFS] Creating New Workspace " + Workspace);
                    LookForErrorReturns(executor.Execute(CreateWorkSpaceProcessInfo(result)));
                    Log.Debug(string.Format("[TFS] Mapping Workspace {0} to {1}", Workspace, WorkingDirectory));
                    LookForErrorReturns(executor.Execute(MapWorkSpaceProcessInfo(result)));
                }
            }
            else
            {
                Log.Debug("[TFS] Creating New Workspace " + Workspace);
                LookForErrorReturns(executor.Execute(CreateWorkSpaceProcessInfo(result)));
                Log.Debug(string.Format("[TFS] Mapping Workspace {0} to {1}", Workspace, WorkingDirectory));
                LookForErrorReturns(executor.Execute(MapWorkSpaceProcessInfo(result)));
            }
            if (!workspaceStatus.WorkspaceIsMappedCorrectly)
            {
                Log.Debug(string.Format("[TFS] Mapping Workspace {0} to {1}", Workspace, WorkingDirectory));
                LookForErrorReturns(executor.Execute(MapWorkSpaceProcessInfo(result)));
            }
            Log.Debug("[TFS] Getting Files in " + Workspace);
            ProcessInfo pi = GetWorkSpaceProcessInfo(result);
            pi.TimeOut = 3600000;
            LookForErrorReturns(executor.Execute(pi));
        }
        private bool ProjectExists(IIntegrationResult result)
        {
            Log.Debug(String.Format("[TFS] Checking if Project {0} exists", ProjectPath));
            ProcessResult pr = executor.Execute(CheckProjectProcessInfo(result));
            LookForErrorReturns(pr);
            return (!pr.StandardOutput.Contains("No items match"));
        }
        private TfsWorkspaceStatus GetWorkspaceStatus(IIntegrationResult result)
        {
            Log.Debug(String.Format("[TFS] Fetching Workspace {0} details", Workspace));
            ProcessResult pr = executor.Execute(CheckWorkSpaceProcessInfo(result));
            LookForErrorReturns(pr);
            TfsWorkspaceStatus status = new TfsWorkspaceStatus();
            status.WorkspaceIsMappedCorrectly = pr.StandardOutput.Contains(ProjectPath + ": " + WorkingDirectory);
            status.WorkspaceExists = !(pr.StandardOutput.Contains("No workspace matching"));
            return status;
        }
        private static void LookForErrorReturns(ProcessResult pr)
        {
            if (pr.HasErrorOutput && pr.Failed)
            {
                Log.Error(pr.StandardError);
                throw new Exception(pr.StandardError);
            }
        }
        private void DeleteDirectory(string path)
        {
            if (!Directory.Exists(path)) return;
            this.MarkAllFilesReadWrite(path);
            Directory.Delete(path, true);
        }
        private void MarkAllFilesReadWrite(string path)
        {
            DirectoryInfo dirInfo = new DirectoryInfo(path);
            FileInfo[] files = dirInfo.GetFiles();
            foreach (FileInfo file in files)
            {
                file.IsReadOnly = false;
            }
            DirectoryInfo[] dirs = dirInfo.GetDirectories();
            foreach (DirectoryInfo dir in dirs)
            {
                this.MarkAllFilesReadWrite(dir.FullName);
            }
        }
        private ProcessInfo CheckProjectProcessInfo(IIntegrationResult result)
        {
            var buffer = new PrivateArguments("dir", "/folders");
            buffer.Add("/server:", Server);
            buffer.AddQuote(ProjectPath);
            AppendSourceControlAuthentication(buffer);
            return NewProcessInfo(buffer, result);
        }
        private ProcessInfo MapWorkSpaceProcessInfo(IIntegrationResult result)
        {
            var buffer = new PrivateArguments("workfold", "/map");
            buffer.AddQuote(ProjectPath);
            buffer.AddQuote(WorkingDirectory);
            buffer.Add("/server:", Server);
            buffer.Add("/workspace:", this.Workspace);
            AppendSourceControlAuthentication(buffer);
            return NewProcessInfo(buffer, result);
        }
        private ProcessInfo GetWorkSpaceProcessInfo(IIntegrationResult result)
        {
            var buffer = new PrivateArguments(
                "get",
                "/force",
                "/recursive",
                "/noprompt");
            buffer.AddQuote(WorkingDirectory);
            AppendSourceControlAuthentication(buffer);
            return NewProcessInfo(buffer, result);
        }
        private ProcessInfo CreateWorkSpaceProcessInfo(IIntegrationResult result)
        {
            var buffer = new PrivateArguments("workspace", "/new");
            buffer.Add("/computer:", Environment.MachineName);
            buffer.AddQuote("/comment:", DEFAULT_WORKSPACE_COMMENT);
            buffer.Add("/server:", Server);
            buffer.AddQuote(Workspace);
            AppendSourceControlAuthentication(buffer);
            return NewProcessInfo(buffer, result);
        }
        private ProcessInfo DeleteWorkSpaceProcessInfo(IIntegrationResult result)
        {
            var buffer = new PrivateArguments("workspace", "/delete");
            buffer.Add("-server:", Server);
            buffer.AddQuote(Workspace);
            AppendSourceControlAuthentication(buffer);
            return NewProcessInfo(buffer, result);
        }
        private ProcessInfo CheckWorkSpaceProcessInfo(IIntegrationResult result)
        {
            var buffer = new PrivateArguments("workspaces");
            buffer.Add("/computer:", Environment.MachineName);
            buffer.Add("-server:", Server);
            buffer.Add("/format:detailed");
            buffer.AddQuote(Workspace);
            AppendSourceControlAuthentication(buffer);
            return NewProcessInfo(buffer, result);
        }
        private ProcessInfo NewLabelProcessInfo(IIntegrationResult result)
        {
            var buffer = new PrivateArguments("label");
            buffer.Add("/server:", Server);
            buffer.Add(result.Label);
            buffer.AddQuote(WorkingDirectory);
            buffer.Add("/recursive");
            AppendSourceControlAuthentication(buffer);
            return NewProcessInfo(buffer, result);
        }
        private ProcessInfo NewHistoryProcessInfo(IIntegrationResult from_, IIntegrationResult to)
        {
            var buffer = new PrivateArguments("history", "-noprompt");
            buffer.Add("-server:", Server);
            buffer.AddQuote(ProjectPath);
            buffer.Add(String.Format("-version:D{0}~D{1}", FormatCommandDate(from_.StartTime), FormatCommandDate(to.StartTime)));
            buffer.Add("-recursive");
            buffer.Add("-format:detailed");
            AppendSourceControlAuthentication(buffer);
            return NewProcessInfo(buffer, to);
        }
        private void AppendSourceControlAuthentication(PrivateArguments buffer)
        {
            if (!string.IsNullOrEmpty(Username) && !string.IsNullOrEmpty(Password.PrivateValue))
                buffer.Add("/login:" + this.BuildTfsAuthenticationString());
        }
        private string BuildTfsAuthenticationString()
        {
            return BuildTfsUsername() + "," + this.Password.ToString(SecureDataMode.Private);
        }
        private string BuildTfsUsername()
        {
            string username = this.Username;
            if (!string.IsNullOrEmpty(this.Domain))
            {
                return this.Domain + @"\" + username;
            }
            return username;
        }
        private static string FormatCommandDate(DateTime date)
        {
            return date.ToUniversalTime().ToString(UtcXmlDateFormat, CultureInfo.InvariantCulture);
        }
        private ProcessInfo NewProcessInfo(PrivateArguments args, IIntegrationResult result)
        {
            string workingDirectory = Path.GetFullPath(result.BaseFromWorkingDirectory(WorkingDirectory));
            if (!Directory.Exists(workingDirectory)) Directory.CreateDirectory(workingDirectory);
            var processInfo = new ProcessInfo(Executable, args, workingDirectory);
            processInfo.StreamEncoding = Encoding.UTF8;
            processInfo.TimeOut = 600000;
            return processInfo;
        }
        private string ReadTfFromRegistry()
        {
            string registryValue = registry.GetLocalMachineSubKeyValue(VS2010_64_REGISTRY_PATH, VS_REGISTRY_KEY);
            if (registryValue == null)
            {
                registryValue = registry.GetLocalMachineSubKeyValue(VS2008_64_REGISTRY_PATH, VS_REGISTRY_KEY);
            }
            if (registryValue == null)
            {
                registryValue = registry.GetLocalMachineSubKeyValue(VS2005_64_REGISTRY_PATH, VS_REGISTRY_KEY);
            }
            if (registryValue == null)
            {
                registryValue = registry.GetLocalMachineSubKeyValue(VS2010_32_REGISTRY_PATH, VS_REGISTRY_KEY);
            }
            if (registryValue == null)
            {
                registryValue = registry.GetLocalMachineSubKeyValue(VS2008_32_REGISTRY_PATH, VS_REGISTRY_KEY);
            }
            if (registryValue == null)
            {
                registryValue = registry.GetLocalMachineSubKeyValue(VS2005_32_REGISTRY_PATH, VS_REGISTRY_KEY);
            }
            if (registryValue == null)
            {
                Log.Debug("[TFS] Unable to find TF.exe and it was not defined in Executable Parameter");
                throw new Exception("Unable to find TF.exe and it was not defined in Executable Parameter");
            }
            return Path.Combine(registryValue, TF_EXE);
        }
    }
}
