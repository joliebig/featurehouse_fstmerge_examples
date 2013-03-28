using System.Windows.Forms;
using System.Threading;
namespace GpsTrackerPlugin
{
 public class GPSSource
 {
  public int iStartAltitud;
  public int iNameIndex;
  public int iIndex;
  public string sType;
  public string sDescription;
  public string sComment;
  public string sIconPath;
  public System.Drawing.Color colorTrack;
  public double fLat;
  public double fLon;
  public bool bTrack;
  public bool bSetup;
  public TreeNode treeNode;
  public bool bNeedApply;
        public GPSPositionVariables GpsPos;
  public int iCOMPort;
  public int iBaudRate;
  public int iByteSize;
  public int iSelectedItem;
  public int iParity;
  public int iStopBits;
  public int iFlowControl;
        public SerialPort GpsCOM;
        public string sCOMData;
  public int iUDPPort;
  public string sTCPAddress;
  public int iTCPPort;
  public bool bSecureSocket;
        public TCPSockets tcpSockets;
  public string sFileName;
  public bool bNoDelay;
  public bool bTrackAtOnce;
  public int iPlaySpeed;
  public int iReload;
  public bool bSession;
  public bool bForcePreprocessing;
        public Thread fileThread;
  public int iFilePlaySpeed;
  public string sFileNameSession;
  public int iServerIndex;
  public string sAPRSServerURL;
  public string sCallSign;
  public int iRefreshRate;
  public string sCallSignFilter;
  public string [] sCallSignFilterLines;
        public Thread aprsThread;
  public bool bPOISet;
     public GPSSource()
     {
            GpsPos = new GPSPositionVariables();
     }
 }
}
