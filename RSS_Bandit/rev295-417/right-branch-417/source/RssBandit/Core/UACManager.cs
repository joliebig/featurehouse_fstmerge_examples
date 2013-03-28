using System.Collections.Generic;
using System.Diagnostics;
using System.Drawing;
using System.Security;
using System.Security.AccessControl;
using Microsoft.Win32;
using Logger = RssBandit.Common.Logging;
namespace RssBandit
{
 internal enum ElevationRequiredAction
 {
  RunBanditAsWindowsUserLogon,
  MakeDefaultAggregator,
 }
 internal class UACManager
 {
  private static readonly log4net.ILog _log = Logger.Log.GetLogger(typeof(UACManager));
  static readonly Dictionary<ElevationRequiredAction, string> _actions = new Dictionary<ElevationRequiredAction, string>(3);
  internal static bool Denied(ElevationRequiredAction action)
  {
   if (_actions.ContainsKey(action))
    return _actions[action] != null;
   if (RssBanditApplication.PortableApplicationMode) {
    switch (action) {
     case ElevationRequiredAction.RunBanditAsWindowsUserLogon:
      _actions.Add(action, "Not applicable in portable mode.");
      return true;
     case ElevationRequiredAction.MakeDefaultAggregator:
      _actions.Add(action, "Not applicable in portable mode.");
      return true;
     default:
      Debug.Assert(false, "Unhandled ElevationRequiredAction: " + action);
      break;
    }
   }
   else
   {
    RegistryKey rk = null;
    switch (action) {
     case ElevationRequiredAction.RunBanditAsWindowsUserLogon:
      try {
       rk = Microsoft.Win32.Registry.CurrentUser.OpenSubKey(@"Software\\Microsoft\\Windows\\CurrentVersion\\Run", RegistryKeyPermissionCheck.ReadWriteSubTree, RegistryRights.CreateSubKey);
       rk.Close();
       _actions.Add(action, null);
      } catch (SecurityException secEx) {
       _log.WarnFormat("ElevationRequiredAction: {0}, {1}", action, secEx);
       _actions.Add(action, secEx.Message);
       return true;
      }
      break;
     case ElevationRequiredAction.MakeDefaultAggregator:
      try {
       rk = Win32.WindowsRegistry.ClassesRootKey(false).OpenSubKey(@"feed", RegistryKeyPermissionCheck.ReadWriteSubTree, RegistryRights.CreateSubKey);
       rk.Close();
       _actions.Add(action, null);
      } catch (SecurityException secEx) {
       _log.WarnFormat("ElevationRequiredAction: {0}, {1}", action, secEx);
       _actions.Add(action, secEx.Message);
       return true;
      }
      break;
     default:
      Debug.Assert(false, "Unhandled ElevationRequiredAction: " + action);
      break;
    }
   }
   return false;
  }
  private UACManager(){}
 }
}
