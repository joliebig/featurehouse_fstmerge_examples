namespace ThoughtWorks.CruiseControl.Core.Util
{
 public interface IFileDirectoryDeleter
 {
  void DeleteIncludingReadOnlyObjects(string filename);
 }
}
