using System.Diagnostics;
namespace ThoughtWorks.CruiseControl.CCTrayLib.Presentation
{
 public class AudioPlayer : IAudioPlayer
 {
  public void Play(string filename)
  {
   Debug.WriteLine("Playing: " + filename);
   Audio.PlaySound(filename, false, true);
  }
 }
}
