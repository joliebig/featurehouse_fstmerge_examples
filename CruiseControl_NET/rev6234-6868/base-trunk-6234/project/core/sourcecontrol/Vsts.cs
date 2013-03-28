using System;
using System.Collections.Generic;
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
        private const string VS2008_32_REGISTRY_PATH = @"Software\Microsoft\VisualStudio\9.0";
        private const string VS2005_32_REGISTRY_PATH = @"Software\Microsoft\VisualStudio\8.0";
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
        public string Server;
        [ReflectorProperty("executable", Required = false)]
        public string Executable
        {
            get
            {
                if (executable == null)
                    executable = ReadTFFromRegistry();
                return executable;
            }
            set { executable = value; }
        }
        [ReflectorProperty("project")]
        public string ProjectPath;
        [ReflectorProperty("applyLabel", Required = false)]
        public bool ApplyLabel = false;
        [ReflectorProperty("autoGetSource", Required = false)]
        public bool AutoGetSource = false;
        [ReflectorProperty("username", Required = false)]
        public string Username = String.Empty;
        [ReflectorProperty("password", Required = false)]
        public string Password = String.Empty;
        [ReflectorProperty("domain", Required = false)]
        public string Domain = String.Empty;
        [ReflectorProperty("workingDirectory", Required = false)]
        public string WorkingDirectory = String.Empty;
        [ReflectorProperty("cleanCopy", Required = false)]
        public bool CleanCopy = false;
        [ReflectorProperty("force", Required = false)]
        public bool Force = false;
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
                return workspaceName;
            }
            set
            {
                workspaceName = value;
            }
        }
        [ReflectorProperty("deleteWorkspace", Required = false)]
        public bool DeleteWorkspace = false;
        public override Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to)
        {
            ProcessResult result = null;
            try
            {
                if ( projectExists( from_ ) )
                {
                    Log.Debug("Checking Team Foundation Server for Modifications");
                    Log.Debug("from_: " + from_.StartTime + " - To: " + to.StartTime);
                    List<Modification> modifications = new List<Modification>();
                    result = executor.Execute(NewHistoryProcessInfo(from_, to));
                    lookForErrorReturns(result);
                }
                else
                {
                    Log.Error(String.Format("Project {0} is not valid on {1} TFS server", ProjectPath, Server));
                    throw new Exception("Project Name is not valid on this TFS server");
                }
            }
            catch (Exception)
            {
                throw;
            }
            return ParseModifications(result, from_.StartTime, to.StartTime);
        }
        public override void LabelSourceControl(IIntegrationResult result)
        {
            try
            {
                if (ApplyLabel && result.Succeeded)
                {
                    lookForErrorReturns(executor.Execute(NewLabelProcessInfo(result)));
                }
            }
            catch (Exception)
            {
                throw;
            }
        }
        public override void GetSource(IIntegrationResult result)
        {
            if (AutoGetSource && projectExists(result))
            {
                try
                {
                    this.WorkingDirectory = result.BaseFromWorkingDirectory(this.WorkingDirectory);
                    if (CleanCopy)
                    {
                        Log.Debug("Deleting " + this.WorkingDirectory);
                        this.deleteDirectory(this.WorkingDirectory);
                    }
                    if (workspaceExists(result))
                    {
                        if (DeleteWorkspace)
                        {
                            Log.Debug("Removing existing workspace " + Workspace);
                            lookForErrorReturns(executor.Execute(DeleteWorkSpaceProcessInfo(result)));
                            Log.Debug("Creating New Workspace " + Workspace);
                            lookForErrorReturns(executor.Execute(CreateWorkSpaceProcessInfo(result)));
                            Log.Debug(string.Format("Mapping Workspace {0} to {1}", Workspace, WorkingDirectory));
                            lookForErrorReturns(executor.Execute(MapWorkSpaceProcessInfo(result)));
                        }
                    }
                    else
                    {
                        Log.Debug("Creating New Workspace " + Workspace);
                        lookForErrorReturns(executor.Execute(CreateWorkSpaceProcessInfo(result)));
                        Log.Debug(string.Format("Mapping Workspace {0} to {1}", Workspace, WorkingDirectory));
                        lookForErrorReturns(executor.Execute(MapWorkSpaceProcessInfo(result)));
                    }
                    if (!workspaceIsMappedCorrectly(result))
                    {
                        Log.Debug(string.Format("Mapping Workspace {0} to {1}", Workspace, WorkingDirectory));
                        lookForErrorReturns(executor.Execute(MapWorkSpaceProcessInfo(result)));
                    }
                    Log.Debug("Getting Files in " + Workspace);
                    ProcessInfo pi = GetWorkSpaceProcessInfo(result);
                    pi.TimeOut = 3600000;
                    lookForErrorReturns(executor.Execute(pi));
                }
                catch (Exception)
                {
                    throw;
                }
            }
        }
        private bool projectExists(IIntegrationResult result)
        {
            try
            {
                Log.Debug(String.Format("Checking if Project {0} exists", ProjectPath));
                ProcessResult pr = executor.Execute(CheckProjectProcessInfo(result));
                lookForErrorReturns(pr);
                string failedMessage = "No items match";
                return (!pr.StandardOutput.Contains(failedMessage));
            }
            catch (Exception)
            {
                throw;
            }
        }
        private bool workspaceIsMappedCorrectly(IIntegrationResult result)
        {
            try
            {
                Log.Debug(String.Format("Checking if Workspace {0} exists", Workspace));
                ProcessResult pr = executor.Execute(CheckWorkSpaceProcessInfo(result));
                lookForErrorReturns(pr);
                string expected = ProjectPath + ": " + WorkingDirectory;
                return (pr.StandardOutput.Contains(expected));
            }
            catch (Exception)
            {
                throw;
            }
        }
        private void lookForErrorReturns(ProcessResult pr)
        {
            if (pr.HasErrorOutput && pr.Failed)
            {
                Log.Error(pr.StandardError);
                throw new Exception(pr.StandardError);
            }
        }
        private void deleteDirectory(string path)
        {
            if (Directory.Exists(path))
            {
                this.MarkAllFilesReadWrite(path);
                Directory.Delete(path, true);
            }
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
        private bool workspaceExists(IIntegrationResult result)
        {
            Log.Debug(String.Format("Checking if Workspace {0} exists", Workspace));
            ProcessResult pr = executor.Execute(CheckWorkSpaceProcessInfo(result));
            lookForErrorReturns(pr);
            return !(pr.StandardOutput.Contains("No workspace matching"));
        }
        private ProcessInfo CheckProjectProcessInfo(IIntegrationResult result)
        {
            ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
            buffer.AddArgument("dir");
            buffer.AddArgument("/folders");
            buffer.AppendArgument(string.Format("/server:{0}", Server));
            buffer.AppendArgument(String.Format("\"{0}\"", ProjectPath));
            return NewProcessInfo(buffer.ToString(), result);
        }
        private ProcessInfo MapWorkSpaceProcessInfo(IIntegrationResult result)
        {
            ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
            buffer.AddArgument("workfold");
            buffer.AddArgument("/map");
            buffer.AppendArgument(String.Format("\"{0}\"", ProjectPath ));
            buffer.AppendArgument(String.Format("\"{0}\"", WorkingDirectory));
            buffer.AppendArgument(string.Format("/server:{0}", Server));
            buffer.AppendArgument(String.Format("/workspace:{0}", Workspace));
            return NewProcessInfo(buffer.ToString(), result);
        }
        private ProcessInfo GetWorkSpaceProcessInfo(IIntegrationResult result)
        {
            ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
            buffer.AddArgument("get");
            buffer.AppendArgument("/force");
            buffer.AppendArgument("/recursive");
            buffer.AppendArgument("/noprompt");
            buffer.AppendArgument(String.Format("\"{0}\"", WorkingDirectory));
            return NewProcessInfo(buffer.ToString(), result);
        }
        private ProcessInfo CreateWorkSpaceProcessInfo(IIntegrationResult result)
        {
            ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
            buffer.AddArgument("workspace");
            buffer.AppendArgument("/new");
            buffer.AppendArgument(string.Format("/computer:{0}", Environment.MachineName));
            buffer.AppendArgument(string.Format("/comment:\"{0}\"", DEFAULT_WORKSPACE_COMMENT));
            buffer.AppendArgument(string.Format("/server:{0}", Server));
            buffer.AppendArgument(String.Format("\"{0}\"", Workspace));
            return NewProcessInfo(buffer.ToString(), result);
        }
        private ProcessInfo DeleteWorkSpaceProcessInfo(IIntegrationResult result)
        {
            ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
            buffer.AddArgument("workspace");
            buffer.AppendArgument("/delete");
            buffer.AppendArgument(string.Format("/computer:{0}", Environment.MachineName));
            buffer.AppendArgument(string.Format("-server:{0}", Server));
            buffer.AppendArgument(String.Format("\"{0}\"", Workspace));
            return NewProcessInfo(buffer.ToString(), result);
        }
        private ProcessInfo CheckWorkSpaceProcessInfo(IIntegrationResult result)
        {
            ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
            buffer.AddArgument("workspaces");
            buffer.AppendArgument(string.Format("/computer:{0}", Environment.MachineName));
            buffer.AppendArgument(string.Format("-server:{0}", Server));
            buffer.AppendArgument("/format:detailed");
            buffer.AppendArgument(String.Format("\"{0}\"", Workspace));
            return NewProcessInfo(buffer.ToString(), result);
        }
        private ProcessInfo NewLabelProcessInfo(IIntegrationResult result)
        {
            ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
            buffer.AddArgument("label");
            buffer.AppendArgument(string.Format("/server:{0}", Server));
            buffer.AppendArgument(result.Label);
            buffer.AppendArgument(String.Format("\"{0}\"", WorkingDirectory));
            buffer.AppendArgument("/recursive");
            return NewProcessInfo(buffer.ToString(), result);
        }
        private ProcessInfo NewHistoryProcessInfo(IIntegrationResult from_, IIntegrationResult to)
        {
            ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
            buffer.AddArgument("history");
            buffer.AppendArgument("-noprompt");
            buffer.AppendArgument(String.Format("-server:{0}", Server));
            buffer.AppendArgument(String.Format("\"{0}\"", ProjectPath));
            buffer.AppendArgument(String.Format("-version:D{0}~D{1}", FormatCommandDate(from_.StartTime), FormatCommandDate(to.StartTime)));
            buffer.AppendArgument("-recursive");
            buffer.AppendArgument("-format:detailed");
            if (!string.IsNullOrEmpty(Username) && !string.IsNullOrEmpty(Password))
            {
                if (!string.IsNullOrEmpty(Domain))
                {
                    Username = Domain + @"\" + Username;
                }
                buffer.AppendArgument(String.Format("-login:{0},{1}",
                    Username,
                    ProcessArgumentBuilder.HideArgument(Password)));
            }
            return NewProcessInfo(buffer.ToString(), to);
        }
        private string FormatCommandDate(DateTime date)
        {
            return date.ToUniversalTime().ToString(UtcXmlDateFormat, CultureInfo.InvariantCulture);
        }
        private ProcessInfo NewProcessInfo(string args, IIntegrationResult result)
        {
            string workingDirectory = Path.GetFullPath(result.BaseFromWorkingDirectory(WorkingDirectory));
            if (!Directory.Exists(workingDirectory)) Directory.CreateDirectory(workingDirectory);
            ProcessInfo processInfo = new ProcessInfo(Executable, args, workingDirectory);
            processInfo.StreamEncoding = Encoding.UTF8;
            return processInfo;
        }
        private string ReadTFFromRegistry()
        {
            string registryValue = null;
            registryValue = registry.GetLocalMachineSubKeyValue(VS2008_64_REGISTRY_PATH, VS_REGISTRY_KEY);
            if (registryValue == null)
            {
                registryValue = registry.GetLocalMachineSubKeyValue(VS2005_64_REGISTRY_PATH, VS_REGISTRY_KEY);
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
                Log.Debug("Unable to find TF.exe and it was not defined in Executable Parameter");
                throw new Exception("Unable to find TF.exe and it was not defined in Executable Parameter");
            }
            return Path.Combine(registryValue, TF_EXE);
        }
    }
}
