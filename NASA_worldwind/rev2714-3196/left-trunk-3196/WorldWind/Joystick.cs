using Microsoft.DirectX;
using Microsoft.DirectX.DirectInput;
using System.Windows.Forms;
using WorldWind.Renderable;
using WorldWind.Camera;
using WorldWind;
using System.IO;
using System;
using System.Threading;
using System.Reflection;
namespace Mashiharu.Sample
{
 public class Joystick : WorldWind.PluginEngine.Plugin
 {
  DrawArgs drawArgs;
  Device joystick;
  Thread joyThread;
  const double RotationFactor = 5e-4f;
  const double ZoomFactor = 1.2;
  const int AxisRange = 100;
  public override void Load()
  {
   drawArgs = ParentApplication.WorldWindow.DrawArgs;
   DeviceList dl = Manager.GetDevices(DeviceClass.GameControl, EnumDevicesFlags.AttachedOnly);
   dl.MoveNext();
   if(dl.Current==null)
   {
    throw new ApplicationException("No joystick detected.  Please check your connections and verify your device appears in Control panel -> Game Controllers.");
   }
   DeviceInstance di = (DeviceInstance) dl.Current;
   joystick = new Device( di.InstanceGuid );
   joystick.SetDataFormat(DeviceDataFormat.Joystick);
   joystick.SetCooperativeLevel(ParentApplication,
    CooperativeLevelFlags.NonExclusive | CooperativeLevelFlags.Background);
   foreach(DeviceObjectInstance d in joystick.Objects)
   {
    if((d.ObjectId & (int)DeviceObjectTypeFlags.Axis)!=0)
    {
     joystick.Properties.SetRange(ParameterHow.ById, d.ObjectId, new InputRange(-AxisRange, AxisRange));
     joystick.Properties.SetDeadZone(ParameterHow.ById, d.ObjectId, 1000);
    }
   }
   joystick.Acquire();
   joyThread = new Thread( new ThreadStart(JoystickLoop) );
   joyThread.IsBackground = true;
   joyThread.Start();
  }
  public override void Unload()
  {
   if(joyThread != null && joyThread.IsAlive)
    joyThread.Abort();
   joyThread = null;
  }
  void JoystickLoop()
  {
   while( true )
   {
    Thread.Sleep(20);
    try
    {
     joystick.Poll();
     HandleJoystick();
    }
    catch(InputException inputex)
    {
     if((inputex is NotAcquiredException) || (inputex is InputLostException))
     {
      try
      {
       joystick.Acquire();
      }
      catch(InputException)
      {
       Thread.Sleep(1000);
      }
     }
    }
   }
  }
  void HandleJoystick()
  {
   JoystickState jss = joystick.CurrentJoystickState;
   byte[] button = jss.GetButtons();
   bool isButtonDown = false;
   if(button[0]!=0)
   {
    isButtonDown = true;
    drawArgs.WorldCamera.RotationYawPitchRoll(
     Angle.Zero,
     Angle.Zero,
     Angle.FromRadians( -jss.X*RotationFactor ) );
    double altitudeDelta = ZoomFactor * drawArgs.WorldCamera.Altitude * (float)jss.Y/AxisRange;
    drawArgs.WorldCamera.Altitude += altitudeDelta;
   }
   if(button[1]!=0)
   {
    isButtonDown = true;
    drawArgs.WorldCamera.Bank += Angle.FromRadians( jss.X*RotationFactor );
    drawArgs.WorldCamera.Tilt += Angle.FromRadians( jss.Y*RotationFactor );
   }
   if(!isButtonDown)
   {
    double scaling = 0.2 * RotationFactor * drawArgs.WorldCamera.ViewRange.Radians;
    drawArgs.WorldCamera.RotationYawPitchRoll(
     Angle.FromRadians(jss.X*scaling),
     Angle.FromRadians(-jss.Y*scaling),
     Angle.Zero );
   }
  }
 }
}
