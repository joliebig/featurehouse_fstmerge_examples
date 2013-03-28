namespace ThoughtWorks.CruiseControl.Core.Tasks
{
    using System;
    using System.Collections;
    using System.Collections.Specialized;
    using System.Diagnostics;
    using System.IO;
    using System.Text;
    using System.Text.RegularExpressions;
    using Exortech.NetReflector;
    using ThoughtWorks.CruiseControl.Core.Util;
    [ReflectorType("powershell")]
 public class PowerShellTask
        : TaskBase
 {
  public const int DefaultBuildTimeOut = 600;
        public const string PowerShellExe = "powershell.exe";
        public const string regkeypowershell1 = @"SOFTWARE\Microsoft\PowerShell\1\PowerShellEngine";
        public const string regkeypowershell2 = @"SOFTWARE\Microsoft\PowerShell\2\PowerShellEngine";
        public const string regkeyholder = @"ApplicationBase";
        public static string DefaultScriptsDirectory = System.Environment.GetEnvironmentVariable("USERPROFILE") + @"\Documents\WindowsPowerShell\";
        private string executable;
  private ProcessExecutor executor;
  public PowerShellTask() : this(new Registry(), new ProcessExecutor()) { }
  public PowerShellTask(IRegistry registry, ProcessExecutor executor)
  {
            this.Registry = registry;
   this.executor = executor;
            this.BuildTimeoutSeconds = DefaultBuildTimeOut;
            this.Priority = ProcessPriorityClass.Normal;
            this.ConfiguredScriptsDirectory = DefaultScriptsDirectory;
            this.BuildArgs = string.Empty;
            this.EnvironmentVariables = new EnvironmentVariable[0];
  }
        public IRegistry Registry { get; set; }
        [ReflectorProperty("script", Required = true)]
        public string Script { get; set; }
        [ReflectorProperty("executable", Required = false)]
        public string Executable
        {
            get
   {
    if (executable == null)
    {
     executable = ReadPowerShellFromRegistry();
    }
    return executable;
   }
   set { executable = value; }
  }
        [ReflectorProperty("priority", Required = false)]
        public ProcessPriorityClass Priority { get; set; }
        [ReflectorProperty("scriptsDirectory", Required = false)]
        public string ConfiguredScriptsDirectory { get; set; }
        [ReflectorProperty("buildArgs", Required = false)]
        public string BuildArgs { get; set; }
        [ReflectorProperty("environment", Required = false)]
        public EnvironmentVariable[] EnvironmentVariables { get; set; }
  private int[] successExitCodes = null;
        [ReflectorProperty("successExitCodes", Required = false)]
  public string SuccessExitCodes
  {
   get
            {
                string result = string.Empty;
                if (successExitCodes != null)
                {
                    foreach (int code in successExitCodes)
                    {
                        if (result !=string.Empty)
                            result = result + ",";
                        result = result + code;
                    }
                }
                return result;
            }
   set
   {
                if (!string.IsNullOrEmpty(value))
                {
                    string[] codes = value.Split(',');
                    successExitCodes = new int[codes.Length];
                    for (int i = 0; i < codes.Length; ++i)
                    {
                        successExitCodes[i] = Int32.Parse(codes[i]);
                    }
                }
                else
                {
                    successExitCodes = null;
                }
   }
  }
        [ReflectorProperty("buildTimeoutSeconds", Required = false)]
        public int BuildTimeoutSeconds { get; set; }
        protected override bool Execute(IIntegrationResult result)
  {
            result.BuildProgressInformation.SignalStartRunTask(string.Format("Executing {0}", Executable));
   ProcessInfo processInfo = NewProcessInfoFrom(result);
   ProcessResult processResult = AttemptToExecute(processInfo);
            if (!StringUtil.IsWhitespace(processResult.StandardOutput) || !StringUtil.IsWhitespace(processResult.StandardError))
            {
                ProcessResult newResult = new ProcessResult(
                    MakeBuildResult(processResult.StandardOutput,string.Empty),
                    MakeBuildResult(processResult.StandardError, "Error"),
                    processResult.ExitCode,
                    processResult.TimedOut,
     processResult.Failed);
                processResult = newResult;
            }
            result.AddTaskResult(new ProcessTaskResult(processResult));
   if (processResult.TimedOut)
   {
    throw new BuilderException(this, "Command Line Build timed out (after " + BuildTimeoutSeconds + " seconds)");
   }
            return !processResult.Failed;
  }
  private ProcessInfo NewProcessInfoFrom(IIntegrationResult result)
  {
            ProcessInfo info = new ProcessInfo( executable, Args(result), BaseDirectory(result), this.Priority, successExitCodes);
   info.TimeOut = BuildTimeoutSeconds*1000;
            SetConfiguredEnvironmentVariables(info.EnvironmentVariables, this.EnvironmentVariables);
            IDictionary properties = result.IntegrationProperties;
   foreach (string key in properties.Keys)
   {
    info.EnvironmentVariables[key] = StringUtil.IntegrationPropertyToString(properties[key]);
   }
   return info;
  }
  private string BaseDirectory(IIntegrationResult result)
  {
            return result.BaseFromWorkingDirectory(ConfiguredScriptsDirectory);
  }
  protected ProcessResult AttemptToExecute(ProcessInfo info)
  {
   try
   {
    return executor.Execute(info);
   }
   catch (IOException e)
   {
    throw new BuilderException(this, string.Format("Unable to execute: {0}\n{1}", info, e), e);
   }
  }
  public override string ToString()
  {
   return string.Format(@" BaseDirectory: {0}, PowerShell: {1}", ConfiguredScriptsDirectory, PowerShellExe);
  }
        private static string MakeBuildResult(string input, string msgLevel)
        {
            StringBuilder output = new StringBuilder();
            Regex linePattern = new Regex(@"([^\r\n]+)");
            MatchCollection lines = linePattern.Matches(input);
            if (lines.Count > 0)
            {
                output.Append(System.Environment.NewLine);
                output.Append("<buildresults>");
                output.Append(System.Environment.NewLine);
                foreach (Match line in lines)
                {
                    output.Append("  <message");
                    if (msgLevel !=string.Empty)
                        output.AppendFormat(" level=\"{0}\"", msgLevel);
                    output.Append(">");
                    output.Append(XmlUtil.EncodePCDATA(line.ToString()));
                    output.Append("</message>");
                    output.Append(System.Environment.NewLine);
                }
                output.Append("</buildresults>");
                output.Append(System.Environment.NewLine);
            }
            else
                output.Append(input);
            return output.ToString();
        }
        private static void SetConfiguredEnvironmentVariables(StringDictionary variablePool, EnvironmentVariable[] varsToSet)
        {
            foreach (EnvironmentVariable item in varsToSet)
                variablePool[item.name] = item.value;
        }
  private string ReadPowerShellFromRegistry()
  {
   string registryValue = null;
            registryValue = Registry.GetLocalMachineSubKeyValue(regkeypowershell2, regkeyholder);
   if (registryValue == null)
   {
                registryValue = Registry.GetLocalMachineSubKeyValue(regkeypowershell1, regkeyholder);
   }
            if (registryValue == null)
            {
                Log.Debug("Unable to find PowerShell and it was not defined in Executable Parameter");
                throw new BuilderException(this, "Unable to find PowerShell and it was not defined in Executable Parameter");
            }
   return Path.Combine(registryValue, PowerShellExe);
  }
        private string Args(IIntegrationResult result)
        {
            ProcessArgumentBuilder builder = new ProcessArgumentBuilder();
            if (!string.IsNullOrEmpty(Script))
            {
                if (ConfiguredScriptsDirectory.EndsWith("\\"))
                {
                    builder.AppendArgument(ConfiguredScriptsDirectory + Script);
                }
                else
                {
                    builder.AppendArgument(ConfiguredScriptsDirectory + "\\" + Script);
                }
            }
            if (!string.IsNullOrEmpty(BuildArgs)) builder.AppendArgument(BuildArgs);
            return builder.ToString();
        }
    }
}
