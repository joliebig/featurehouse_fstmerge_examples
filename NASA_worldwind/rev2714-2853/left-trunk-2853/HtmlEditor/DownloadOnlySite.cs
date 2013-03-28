using System;
using System.Runtime.InteropServices;
using System.Diagnostics;
namespace onlyconnect
{
 public class DownloadOnlySite: IOleClientSite
 {
  public DownloadOnlySite()
  {
  }
  [DispId(dispids.DISPID_AMBIENT_DLCONTROL)]
  public int Idispatch_Invoke_Handler()
  {
   Console.WriteLine("SetFlags Silent called");
   return (int) constants.DLCTL_NO_SCRIPTS |
                (int)constants.DLCTL_NO_JAVA |
                (int)constants.DLCTL_NO_DLACTIVEXCTLS
                | (int)constants.DLCTL_NO_RUNACTIVEXCTLS |
                (int)constants.DLCTL_DOWNLOADONLY |
                (int)constants.DLCTL_SILENT |
                (int)constants.DLCTL_DLIMAGES | 0;
  }
  public int SaveObject()
  {
   return HRESULT.S_OK;
  }
  public int GetMoniker(uint dwAssign, uint dwWhichMoniker, out Object ppmk)
  {
   ppmk = null;
   return HRESULT.E_NOTIMPL;
  }
  public int GetContainer(out IOleContainer ppContainer)
  {
   ppContainer = null;
   return HRESULT.E_NOINTERFACE;
  }
  public int ShowObject()
  {
   return HRESULT.S_OK;
  }
  public int OnShowWindow(int fShow)
  {
   return HRESULT.S_OK;
  }
  public int RequestNewObjectLayout()
  {
   return HRESULT.S_OK;
  }
 }
}
