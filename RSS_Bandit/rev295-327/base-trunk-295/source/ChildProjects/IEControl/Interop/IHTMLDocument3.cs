using System.Runtime.InteropServices;
namespace IEControl
{
 [
 InterfaceType(ComInterfaceType.InterfaceIsDual),
 ComVisible(true),
 Guid(@"3050f485-98b5-11cf-bb82-00aa00bdce0b")
 ]
 public interface IHTMLDocument3 {
  void releaseCapture();
  void recalc(bool fForce);
  [return: MarshalAs(UnmanagedType.Interface)]
  object createTextNode(string text);
  IHTMLElement documentElement();
 }
}
