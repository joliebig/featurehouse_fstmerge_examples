using System.Diagnostics;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
    [ReflectorType("robocopy")]
 public class RobocopySourceControl : ProcessSourceControl
 {
  private static int[] GenerateExitCodes()
  {
   int[] exitCodes = new int[4];
   exitCodes[0] = 0;
   exitCodes[1] = 1;
   exitCodes[2] = 2;
   exitCodes[3] = 3;
   return exitCodes;
  }
  private static readonly int[] successExitCodes = GenerateExitCodes();
  public RobocopySourceControl() : this(new RobocopyHistoryParser(), new ProcessExecutor())
  {}
        public RobocopySourceControl(IHistoryParser parser, ProcessExecutor executor)
            : base(parser, executor)
        {
            this.Executable = "C:\\Windows\\System32\\robocopy.exe";
            this.AutoGetSource = false;
            this.WorkingDirectory = string.Empty;
            this.AdditionalArguments = string.Empty;
        }
        [ReflectorProperty("executable", Required = false)]
        public string Executable { get; set; }
        [ReflectorProperty("repositoryRoot")]
        public string RepositoryRoot { get; set; }
        [ReflectorProperty("autoGetSource", Required = false)]
        public bool AutoGetSource { get; set; }
        [ReflectorProperty("workingDirectory", Required = false)]
        public string WorkingDirectory { get; set; }
        [ReflectorProperty("additionalArguments", Required = false)]
        public string AdditionalArguments { get; set; }
  public override Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to)
  {
   string destinationDirectory = from_.BaseFromWorkingDirectory(WorkingDirectory);
   ProcessArgumentBuilder builder = new ProcessArgumentBuilder();
   AddStandardArguments(builder, destinationDirectory);
   builder.AddArgument("/L");
   Modification[] modifications = GetModifications(new ProcessInfo(Executable, builder.ToString(), null, ProcessPriorityClass.Normal, successExitCodes), from_.StartTime, to.StartTime);
   return modifications;
  }
  public override void LabelSourceControl(IIntegrationResult result)
  {}
  public override void GetSource(IIntegrationResult result)
  {
   if (AutoGetSource)
   {
    string destinationDirectory = result.BaseFromWorkingDirectory(WorkingDirectory);
    ProcessArgumentBuilder builder = new ProcessArgumentBuilder();
    AddStandardArguments(builder, destinationDirectory);
                Execute(new ProcessInfo(Executable, builder.ToString(), null, ProcessPriorityClass.Normal, successExitCodes));
   }
  }
  private readonly static string standardArguments = " /MIR /NP /X /TS /FP /NDL /NS /NJH /NJS ";
  private void AddStandardArguments(
   ProcessArgumentBuilder builder,
   string destinationDirectory)
  {
   builder.AddArgument(RepositoryRoot);
   builder.AddArgument(destinationDirectory);
   builder.Append(standardArguments);
   builder.Append(AdditionalArguments);
  }
 }
}
