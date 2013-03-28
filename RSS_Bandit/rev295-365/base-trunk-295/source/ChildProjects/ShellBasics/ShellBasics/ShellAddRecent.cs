using System;
namespace ShellLib
{
 public class ShellAddRecent
 {
  public enum ShellAddRecentDocs
  {
   SHARD_PIDL = 0x00000001,
   SHARD_PATHA = 0x00000002,
   SHARD_PATHW = 0x00000003
  }
  public static void AddToList(String path)
  {
   ShellApi.SHAddToRecentDocs((uint)ShellAddRecentDocs.SHARD_PATHW,path);
  }
  public static void ClearList()
  {
   ShellApi.SHAddToRecentDocs((uint)ShellAddRecentDocs.SHARD_PIDL,IntPtr.Zero);
  }
 }
}
