using System;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
    [ReflectorType("surround")]
 public class Surround : ProcessSourceControl
 {
  public const string TO_SSCM_DATE_FORMAT = "yyyyMMddHHmmss";
  private const string DefaultServerConnection = "127.0.0.1:4900";
  private const string DefaultServerLogin = "Administrator:";
        public Surround()
            : base(new SurroundHistoryParser(), new ProcessExecutor())
        {
            this.Executable = "sscm";
            this.ServerConnect = DefaultServerConnection;
            this.ServerLogin = DefaultServerLogin;
            this.SearchRegExp = 0;
            this.Recursive = 0;
        }
        [ReflectorProperty("executable")]
        public string Executable { get; set; }
        [ReflectorProperty("branch")]
        public string Branch { get; set; }
        [ReflectorProperty("repository")]
        public string Repository { get; set; }
        [ReflectorProperty("file", Required = false)]
        public string File { get; set; }
        [ReflectorProperty("workingDirectory")]
        public string WorkingDirectory { get; set; }
        [ReflectorProperty("serverconnect", Required = false)]
        public string ServerConnect { get; set; }
        [ReflectorProperty("serverlogin", Required = false)]
        public string ServerLogin { get; set; }
        [ReflectorProperty("searchregexp", Required = false)]
        public int SearchRegExp { get; set; }
        [ReflectorProperty("recursive", Required = false)]
        public int Recursive { get; set; }
  public override Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to)
  {
   string command = String.Format("cc {0} -d{1}:{2} {3} -b{4} -p{5} {6} -z{7} -y{8}",
                                  File,
                                  from_.StartTime.ToString(TO_SSCM_DATE_FORMAT),
                                  to.StartTime.ToString(TO_SSCM_DATE_FORMAT),
                                  (Recursive == 0) ?string.Empty : "-r",
                                  Branch,
                                  Repository,
                                  (SearchRegExp == 0) ? "-x-" : "-x",
                                  ServerConnect,
                                  ServerLogin);
            Modification[] modifications = GetModifications(CreateSSCMProcessInfo(command), from_.StartTime, to.StartTime);
            base.FillIssueUrl(modifications);
            return modifications;
        }
  private ProcessInfo CreateSSCMProcessInfo(string command)
  {
   return new ProcessInfo(Executable, command);
  }
  public override void LabelSourceControl(IIntegrationResult result)
  {}
  public override void Initialize(IProject project)
  {
   Execute(CreateSSCMProcessInfo("workdir " + WorkingDirectory + " " + Repository + " -z" + ServerConnect + " -y" + ServerLogin));
  }
  public override void GetSource(IIntegrationResult result)
  {
   Log.Info("Getting source from_ Surround SCM");
            result.BuildProgressInformation.SignalStartRunTask("Getting source from_ Surround SCM");
   string command = String.Format("get * -q -tcheckin -wreplace {0} -d{1} -b{2} -p{3} -z{4} -y{5}",
                                  (Recursive == 0) ?string.Empty : "-r",
                                  WorkingDirectory,
             Branch,
                                  Repository,
                                  ServerConnect,
                                  ServerLogin);
   Execute(CreateSSCMProcessInfo(command));
  }
 }
}
