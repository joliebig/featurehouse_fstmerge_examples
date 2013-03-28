using System.Windows.Forms;
using ThoughtWorks.CruiseControl.CCTrayLib.X10;
namespace ThoughtWorks.CruiseControl.CCTrayLib.X10
{
 public interface IX10LowLevelDriver
 {
  void ControlDevice(int deviceCode, Function deviceCommand, int lightLevel);
        void ResetStatus(Label statusLabel);
 }
}
