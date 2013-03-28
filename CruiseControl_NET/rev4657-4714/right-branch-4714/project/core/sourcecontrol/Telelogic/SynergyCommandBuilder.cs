using System;
using System.Globalization;
using System.Text;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Sourcecontrol.Telelogic
{
 public class SynergyCommandBuilder
 {
  public static ProcessInfo Heartbeat(SynergyConnectionInfo connection)
  {
   return CreateProcessInfo(connection, "status");
  }
  public static ProcessInfo Start(SynergyConnectionInfo connection, SynergyProjectInfo project)
  {
   const string template = @"start -nogui -q -m -h ""{0}"" -d ""{1}"" -p ""{2}"" -n ""{3}"" -pw ""{4}"" -r ""{5}"" -u ""{6}"" -home ""{7}""";
   string arguments = String.Format(template, connection.Host, connection.Database, project.ProjectSpecification, connection.Username, connection.Password, connection.Role, connection.ClientDatabaseDirectory, connection.HomeDirectory);
   return CreateProcessInfo(connection, arguments);
  }
  public static ProcessInfo Stop(SynergyConnectionInfo connection)
  {
   return CreateProcessInfo(connection, "stop");
  }
  public static ProcessInfo GetDelimiter(SynergyConnectionInfo connection)
  {
   return CreateProcessInfo(connection, "delimiter");
  }
  public static ProcessInfo GetDcmDelimiter(SynergyConnectionInfo connection)
  {
   return CreateProcessInfo(connection, "dcm /show /delimiter");
  }
  public static ProcessInfo GetDcmSettings(SynergyConnectionInfo connection)
  {
   return CreateProcessInfo(connection, "dcm /show /settings");
  }
  public static ProcessInfo GetProjectFullName(SynergyConnectionInfo connection, SynergyProjectInfo project)
  {
   const string template = @"properties /format ""%objectname"" /p ""{0}""";
   string arguments = String.Format(template, project.ProjectSpecification);
   return CreateProcessInfo(connection, arguments);
  }
  public static ProcessInfo GetSubProjects(SynergyConnectionInfo connection, SynergyProjectInfo project)
  {
   const string template = @"query hierarchy_project_members('{0}', 'none')";
   string arguments = String.Format(template, project.ObjectName);
   return CreateProcessInfo(connection, arguments);
  }
  public static ProcessInfo SetProjectRelease(SynergyConnectionInfo connection, SynergyProjectInfo project)
  {
   const string template = @"attribute /m release /v ""{0}"" @ ";
   string arguments = String.Format(template, project.Release);
   return CreateProcessInfo(connection, arguments);
  }
  public static ProcessInfo GetLastReconfigureTime(SynergyConnectionInfo connection, SynergyProjectInfo project)
  {
   const string template = @"attribute /show reconf_time ""{0}""";
   string arguments = String.Format(template, project.ObjectName);
   return CreateProcessInfo(connection, arguments);
  }
  public static ProcessInfo UseReconfigureTemplate(SynergyConnectionInfo connection, SynergyProjectInfo project)
  {
   const string template = @"reconfigure_properties /reconf_using template /recurse ""{0}""";
   string arguments = String.Format(template, project.ProjectSpecification);
   return CreateProcessInfo(connection, arguments);
  }
  public static ProcessInfo UpdateReconfigureProperites(SynergyConnectionInfo connection, SynergyProjectInfo project)
  {
   const string template = @"reconfigure_properties /refresh /recurse ""{0}""";
   string arguments = String.Format(template, project.ProjectSpecification);
   return CreateProcessInfo(connection, arguments);
  }
  public static ProcessInfo Reconfigure(SynergyConnectionInfo connection, SynergyProjectInfo project)
  {
   const string template = @"reconfigure /recurse /keep_subprojects /project ""{0}""";
   string arguments = String.Format(template, project.ProjectSpecification);
   return CreateProcessInfo(connection, arguments);
  }
  public static ProcessInfo Reconcile(SynergyConnectionInfo connection, SynergyProjectInfo project, string path)
  {
   const string template = @"reconcile /consider_uncontrolled /missing_wa_file /recurse /update_wa ""{0}""";
   string arguments = String.Format(template, path);
   return CreateProcessInfo(connection, arguments);
  }
  public static ProcessInfo GetWorkArea(SynergyConnectionInfo connection, SynergyProjectInfo project)
  {
   const string template = @"info /format ""%wa_path\%name"" /project ""{0}""";
   string arguments = String.Format(template, project.ProjectSpecification);
   return CreateProcessInfo(connection, arguments);
  }
  public static ProcessInfo GetNewTasks(SynergyConnectionInfo connection, SynergyProjectInfo project, DateTime startDate)
  {
   const string template = @"query /type task /format " + @"""%displayname #### %task_number #### %completion_date #### %resolver #### %task_synopsis #### "" " + @"/nf /u /no_sort """ + @"status != 'task_automatic' and status != 'excluded' and " + @"completion_date >= time('{2}') and " + @"not ( is_task_in_folder_of(folder('{1}')) or " + @"is_task_in_folder_of(is_folder_in_rp_of(is_baseline_project_of('{0}'))) or " + @"is_task_in_rp_of(is_baseline_project_of('{0}')) ) and " + @"(is_task_in_folder_of(is_folder_in_rp_of('{0}')) or is_task_in_rp_of('{0}'))""";
   string arguments = String.Format(template, project.ObjectName, project.TaskFolder, FormatCommandDate(startDate));
   return CreateProcessInfo(connection, arguments);
  }
  public static string FormatCommandDate(DateTime startDate)
  {
   return startDate.ToString("yyyy/MM/dd HH:mm:ss", CultureInfo.InvariantCulture);
  }
  public static ProcessInfo GetTaskObjects(SynergyConnectionInfo connection, SynergyProjectInfo project)
  {
   const string template = @"task /show objects /no_sort /u @";
   return CreateProcessInfo(connection, template);
  }
  public static ProcessInfo GetObjectPaths(SynergyConnectionInfo connection, SynergyProjectInfo project)
  {
   const string template = @"finduse @";
   return CreateProcessInfo(connection, template);
  }
  public static ProcessInfo AddTasksToFolder(SynergyConnectionInfo connection, SynergyProjectInfo project, IIntegrationResult result)
  {
   const string template = @"folder /modify /add_tasks ""{0}"" /y ""{1}""";
   string tasks = GetTaskList(result.Modifications);
   string arguments = String.Format(template, tasks, project.TaskFolder);
   return CreateProcessInfo(connection, arguments);
  }
  public static ProcessInfo AddLabelToTaskComment(SynergyConnectionInfo connection, SynergyProjectInfo project, IIntegrationResult result)
  {
   const string template = @"task /modify /description ""Integrated Successfully with CruiseControl.NET project '{0}' build '{1}' on {2}"" ""{3}""";
   string tasks = GetTaskList(result.Modifications);
   string arguments = String.Format(template, result.ProjectName, result.Label, result.StartTime, tasks);
   return CreateProcessInfo(connection, arguments);
  }
  public static ProcessInfo CreateBaseline(SynergyConnectionInfo connection, SynergyProjectInfo project, IIntegrationResult result)
  {
   const string template = @"baseline /create ""{5:yyyyMMdd} CCNET build {1} "" /description ""Integrated Successfully with CruiseControl.NET project '{0}' build '{1}' on {5}"" /release ""{3}"" /purpose ""{4}"" /p ""{2}"" /subprojects";
   string arguments = String.Format(template, result.ProjectName, result.Label, project.ProjectSpecification, project.Release, project.Purpose, result.StartTime);
   return CreateProcessInfo(connection, arguments);
  }
  private static ProcessInfo CreateProcessInfo(SynergyConnectionInfo connectionInfo, string arguments)
  {
   return new ProcessInfo(connectionInfo.Executable, arguments, connectionInfo.WorkingDirectory);
  }
  public static string GetTaskList(Modification[] modifications)
  {
   if (null == modifications || 0 == modifications.Length)
    throw(new CruiseControlException("Invalid Argument: The Synergy task list cannot be empty"));
   int length = modifications.Length;
   int[] taskList = new int[length];
   StringBuilder retVal = new StringBuilder(10*length);
   int j = 0;
   foreach (Modification task in modifications)
   {
    bool exists = false;
    for (int i = 0; i < length; i++)
    {
     if (taskList[i] == task.ChangeNumber)
     {
      exists = true;
      break;
     }
    }
    if (! exists)
    {
     if (j > 0)
     {
      retVal.Append(',');
     }
     retVal.Append(task.ChangeNumber);
     taskList[j++] = task.ChangeNumber;
    }
   }
   return (retVal.ToString());
  }
 }
}
