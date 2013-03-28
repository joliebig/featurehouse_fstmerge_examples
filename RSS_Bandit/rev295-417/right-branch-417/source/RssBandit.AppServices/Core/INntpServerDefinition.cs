namespace NewsComponents
{
 public interface INntpServerDefinition
 {
  string Name { get; }
  string DefaultIdentity{ get; }
  bool PreventDownloadOnRefresh{ get; }
  string Server{ get; }
  string AuthUser{ get; }
  System.Byte[] AuthPassword{ get; }
  bool UseSecurePasswordAuthentication{ get; }
  int Port{ get; }
  bool UseSSL{ get; }
  int Timeout{ get; }
 }
}
