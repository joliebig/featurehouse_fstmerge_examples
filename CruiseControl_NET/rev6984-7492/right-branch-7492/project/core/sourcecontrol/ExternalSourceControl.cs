using System;
using System.Collections;
using System.Collections.Specialized;
using System.Globalization;
using System.IO;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.Core.Tasks;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol
{
    [ReflectorType("external")]
    public class ExternalSourceControl : ProcessSourceControl
    {
        public ExternalSourceControl()
            : this(new ExternalSourceControlHistoryParser(), new ProcessExecutor())
        {
        }
        public ExternalSourceControl(ProcessExecutor executor)
            : this(new ExternalSourceControlHistoryParser(), executor)
        {
        }
        public ExternalSourceControl(IHistoryParser parser, ProcessExecutor executor)
            : base(parser, executor)
        {
        }
        [ReflectorProperty("args", Required = false)]
        public string ArgString = string.Empty;
        [ReflectorProperty("autoGetSource", Required = false)]
        public bool AutoGetSource = false;
        [ReflectorArray("environment", Required = false)]
        public EnvironmentVariable[] EnvironmentVariables = new EnvironmentVariable[0];
        [ReflectorProperty("executable", Required = true)]
        public string Executable;
        [ReflectorProperty("labelOnSuccess", Required = false)]
        public bool LabelOnSuccess = false;
        public override Modification[] GetModifications(IIntegrationResult from_, IIntegrationResult to)
        {
            string args = string.Format(@"GETMODS ""{0}"" ""{1}"" {2}",
                FormatCommandDate(to.StartTime),
                FormatCommandDate(from_.StartTime),
                ArgString);
            ProcessInfo command = PrepCommand(Executable, args, from_);
            Modification[] modifications = base.GetModifications(command, from_.StartTime, to.StartTime);
            base.FillIssueUrl(modifications);
            return modifications;
        }
        public override void GetSource(IIntegrationResult result)
        {
            result.BuildProgressInformation.SignalStartRunTask("Getting source from_ External Source Control");
            if (AutoGetSource)
            {
                string args = string.Format(@"GETSOURCE ""{0}"" ""{1}"" {2}",
                    result.WorkingDirectory,
                    FormatCommandDate(result.StartTime),
                    ArgString);
                RunCommand(Executable, args, result);
            }
        }
        public override void LabelSourceControl(IIntegrationResult result)
        {
            if (LabelOnSuccess && result.Succeeded && (result.Label != string.Empty))
            {
                string args = string.Format(@"SETLABEL ""{0}"" ""{1}"" {2}",
                    result.Label,
                    FormatCommandDate(result.StartTime),
                    ArgString);
                RunCommand(Executable, args, result);
            }
        }
        private static string FormatCommandDate(DateTime date)
        {
            return date.ToString("yyyy-MM-dd HH:mm:ss", CultureInfo.InvariantCulture);
        }
        private ProcessInfo PrepCommand(string executable, string args, IIntegrationResult result)
        {
            Log.Debug(string.Format("Preparing to run source control command: {0} {1}", Executable, args));
            ProcessInfo command = new ProcessInfo(executable, args, result.WorkingDirectory);
            SetConfiguredEnvironmentVariables(command.EnvironmentVariables, this.EnvironmentVariables);
            SetCCNetEnvironmentVariables(command.EnvironmentVariables, result.IntegrationProperties);
            return command;
        }
        private ProcessResult RunCommand(string executable, string args, IIntegrationResult result)
        {
            ProcessInfo command = PrepCommand(executable, args, result);
            ProcessResult cmdResults = Execute(command);
            if (cmdResults.Failed)
            {
                Log.Error(string.Format(@"Source control command ""{0} {1}"" failed with RC={2}",
                    Executable, args, cmdResults.ExitCode));
                if ((cmdResults.StandardError != null) && (cmdResults.StandardError !=string.Empty))
                    Log.Error(string.Format("\tError output: {0}", cmdResults.StandardError));
            }
            return cmdResults;
        }
        private static void SetCCNetEnvironmentVariables(StringDictionary variablePool, IDictionary varsToSet)
        {
            foreach (string key in varsToSet.Keys)
            {
                variablePool[key] = (varsToSet[key] == null) ? null : varsToSet[key].ToString();
            }
        }
        private static void SetConfiguredEnvironmentVariables(StringDictionary variablePool, EnvironmentVariable[] varsToSet)
        {
            foreach (EnvironmentVariable item in varsToSet)
                variablePool[item.name] = item.value;
        }
    }
}
