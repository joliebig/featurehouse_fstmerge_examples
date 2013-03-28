using System;
using System.Globalization;
using System.IO;
using System.Text.RegularExpressions;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Config;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
 [ReflectorType("cvs")]
 public class Cvs : ProcessSourceControl
 {
  public const string DefaultCvsExecutable = "cvs";
  public const string COMMAND_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss 'GMT'";
  private readonly IFileSystem fileSystem;
        private BuildProgressInformation _buildProgressInformation;
  public Cvs() : this(new CvsHistoryParser(), new ProcessExecutor(), new SystemIoFileSystem())
  {
  }
  public Cvs(IHistoryParser parser, ProcessExecutor executor, IFileSystem fileSystem) : base(parser, executor)
  {
   this.fileSystem = fileSystem;
  }
        [ReflectorProperty("executable", Required = false)]
  public string Executable = DefaultCvsExecutable;
  [ReflectorProperty("cvsroot")]
  public string CvsRoot = string.Empty;
  [ReflectorProperty("module")]
  public string Module;
  [ReflectorProperty("workingDirectory", Required=false)]
  public string WorkingDirectory = string.Empty;
  [ReflectorProperty("labelOnSuccess", Required=false)]
  public bool LabelOnSuccess = false;
  [ReflectorProperty("restrictLogins", Required=false)]
  public string RestrictLogins = string.Empty;
  [ReflectorProperty("webUrlBuilder", InstanceTypeKey="type", Required=false)]
  public IModificationUrlBuilder UrlBuilder = new NullUrlBuilder();
  [ReflectorProperty("autoGetSource", Required = false)]
  public bool AutoGetSource = true;
  [ReflectorProperty("cleanCopy", Required = false)]
  public bool CleanCopy = true;
        [ReflectorProperty("forceCheckout", Required = false)]
        public bool ForceCheckout = false;
  [ReflectorProperty("branch", Required=false)]
  public string Branch = string.Empty;
  [ReflectorProperty("tagPrefix", Required=false)]
  public string TagPrefix = "ver-";
  [ReflectorProperty("suppressRevisionHeader", Required=false)]
  public bool SuppressRevisionHeader;
  public string FormatCommandDate(DateTime date)
  {
   return date.ToUniversalTime().ToString(COMMAND_DATE_FORMAT, CultureInfo.InvariantCulture);
  }
  public override Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to)
  {
   Modification[] modifications = GetModifications(CreateLogProcessInfo(from_), from_.StartTime, to.StartTime);
   StripRepositoryRootFromModificationFolderNames(modifications);
   UrlBuilder.SetupModification(modifications);
            base.FillIssueUrl(modifications);
            return modifications;
        }
  public override void LabelSourceControl(IIntegrationResult result)
  {
   if (LabelOnSuccess && result.Succeeded)
   {
    Execute(NewLabelProcessInfo(result));
   }
  }
  public override void GetSource(IIntegrationResult result)
  {
            GetBuildProgressInformation(result).SignalStartRunTask("Getting source from_ CVS");
   if (!AutoGetSource) return;
   if (!ForceCheckout && DoesCvsDirectoryExist(result))
   {
    UpdateSource(result);
   }
   else
   {
    CheckoutSource(result);
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
  private bool DoesCvsDirectoryExist(IIntegrationResult result)
  {
   string cvsDirectory = Path.Combine(result.BaseFromWorkingDirectory(WorkingDirectory), "CVS");
   return fileSystem.DirectoryExists(cvsDirectory);
  }
  private void CheckoutSource(IIntegrationResult result)
  {
            if (string.IsNullOrEmpty(CvsRoot))
    throw new ConfigurationException("<cvsroot> configuration element must be specified in order to automatically checkout source from_ CVS.");
            ProcessExecutor.ProcessOutput += ProcessExecutor_ProcessOutput;
   Execute(NewCheckoutProcessInfo(result));
            ProcessExecutor.ProcessOutput -= ProcessExecutor_ProcessOutput;
  }
  private ProcessInfo NewCheckoutProcessInfo(IIntegrationResult result)
  {
   ProcessArgumentBuilder builder = new ProcessArgumentBuilder();
   AppendCvsRoot(builder);
   builder.AddArgument("-q");
   builder.AddArgument("checkout");
   builder.AddArgument("-R");
   builder.AddArgument("-P");
   builder.AddArgument("-r", Branch);
   builder.AddArgument("-d", StringUtil.AutoDoubleQuoteString(result.BaseFromWorkingDirectory(WorkingDirectory)));
   builder.AddArgument(Module);
   return NewProcessInfoWithArgs(result, builder.ToString());
  }
  private void UpdateSource(IIntegrationResult result)
  {
            ProcessExecutor.ProcessOutput += ProcessExecutor_ProcessOutput;
   Execute(NewGetSourceProcessInfo(result));
            ProcessExecutor.ProcessOutput -= ProcessExecutor_ProcessOutput;
  }
  private ProcessInfo NewGetSourceProcessInfo(IIntegrationResult result)
  {
   ProcessArgumentBuilder builder = new ProcessArgumentBuilder();
   AppendCvsRoot(builder);
   builder.AppendArgument("-q update -d -P");
   builder.AppendIf(CleanCopy, "-C");
   builder.AddArgument("-r", Branch);
   return NewProcessInfoWithArgs(result, builder.ToString());
  }
  private ProcessInfo CreateLogProcessInfo(IIntegrationResult from_)
  {
   return NewProcessInfoWithArgs(from_, BuildLogProcessInfoArgs(from_.StartTime));
  }
  private ProcessInfo NewLabelProcessInfo(IIntegrationResult result)
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   AppendCvsRoot(buffer);
   buffer.AppendArgument(string.Format("tag {0}{1}", TagPrefix, ConvertIllegalCharactersInLabel(result)));
   return NewProcessInfoWithArgs(result, buffer.ToString());
  }
  private string ConvertIllegalCharactersInLabel(IIntegrationResult result)
  {
   return Regex.Replace(result.Label, @"\.", "_");
  }
  private ProcessInfo NewProcessInfoWithArgs(IIntegrationResult result, string args)
  {
   return new ProcessInfo(Executable, args, result.BaseFromWorkingDirectory(WorkingDirectory));
  }
  private string BuildLogProcessInfoArgs(DateTime from_)
  {
   ProcessArgumentBuilder buffer = new ProcessArgumentBuilder();
   AppendCvsRoot(buffer);
   buffer.AddArgument("-q");
   buffer.AddArgument("rlog");
   buffer.AddArgument("-N");
   buffer.AppendIf(SuppressRevisionHeader, "-S");
            if (string.IsNullOrEmpty(Branch))
   {
    buffer.AddArgument("-b");
   }
   else
   {
    buffer.AppendArgument("-r{0}", Branch);
   }
   buffer.AppendArgument(@"""-d>{0}""", FormatCommandDate(from_));
            if (!string.IsNullOrEmpty(RestrictLogins))
   {
    foreach (string login in RestrictLogins.Split(','))
    {
     buffer.AppendArgument("-w{0}", login.Trim());
    }
   }
   buffer.AddArgument(Module);
   return buffer.ToString();
  }
  private void AppendCvsRoot(ProcessArgumentBuilder buffer)
  {
   buffer.AddArgument("-d", CvsRoot);
  }
  private void StripRepositoryRootFromModificationFolderNames(Modification[] modifications)
  {
   foreach (Modification modification in modifications)
   {
    modification.FolderName = StripRepositoryFolder(modification.FolderName);
   }
  }
  private const string LocalCvsProtocolString = ":local:";
  private string StripRepositoryFolder(string rcsFilePath)
  {
   string repositoryFolder = GetRepositoryFolder();
   if (rcsFilePath.StartsWith(repositoryFolder))
   {
    return rcsFilePath.Remove(0, repositoryFolder.Length);
   }
   return rcsFilePath;
  }
  private string GetRepositoryFolder()
  {
   string modulePath = '/' + Module + '/';
   if (CvsRoot.StartsWith(LocalCvsProtocolString))
    return CvsRoot.Substring(LocalCvsProtocolString.Length) + modulePath;
   return CvsRoot.Substring(CvsRoot.LastIndexOf(':') + 1) + modulePath;
  }
 }
}
