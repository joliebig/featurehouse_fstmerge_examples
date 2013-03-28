namespace RssBandit.AppServices.Core
{
 public interface ISharedProperty
 {
  string maxitemage { get; set; }
  int refreshrate { get; set; }
  bool refreshrateSpecified { get; set; }
  bool downloadenclosures { get; set; }
  bool downloadenclosuresSpecified { get; set; }
  bool enclosurealert { get; set; }
  bool enclosurealertSpecified { get; set; }
  string enclosurefolder { get; set; }
  string listviewlayout { get; set; }
  string stylesheet { get; set; }
  bool markitemsreadonexit { get; set; }
  bool markitemsreadonexitSpecified { get; set; }
 }
}
