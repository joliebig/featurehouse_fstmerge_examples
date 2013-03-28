using System;
using System.Collections;
using System.ComponentModel;
using System.Data;
using System.Threading;
using System.Runtime.InteropServices;
namespace GpsTrackerPlugin
{
 public class SerialPort
 {
  private bool m_bClose=true;
  private int m_iIndex;
  private Thread m_ThreadRx;
  private GpsTracker m_GpsTracker;
  public bool IsOpen;
  private const uint GENERIC_READ = 0x80000000;
  private const uint GENERIC_WRITE = 0x40000000;
  private const uint OPEN_EXISTING = 3;
  private const uint INVALID_HANDLE_VALUE = 0xFFFFFFFF;
  private const uint PURGE_RXCLEAR = 0x0008;
  private const uint ERROR_OPERATION_ABORTED = 995;
  private uint m_hCOMPort = INVALID_HANDLE_VALUE;
  private uint m_iCOMPort;
  private uint m_iBaudRate;
  private byte m_byByteSize;
  private byte m_byParity;
  private byte m_byStopBits;
  private uint m_iFlowControl;
  [StructLayout(LayoutKind.Sequential)]
   public struct DCB
  {
   public uint DCBlength;
   public uint BaudRate;
   public uint flags;
   public ushort wReserved;
   public ushort XonLim;
   public ushort XoffLim;
   public byte ByteSize;
   public byte Parity;
   public byte StopBits;
   public char XonChar;
   public char XoffChar;
   public char ErrorChar;
   public char EofChar;
   public char EvtChar;
   public ushort wReserved1;
  }
  [StructLayout(LayoutKind.Sequential)]
   private struct COMMTIMEOUTS
  {
   public uint ReadIntervalTimeout;
   public uint ReadTotalTimeoutMultiplier;
   public uint ReadTotalTimeoutConstant;
   public uint WriteTotalTimeoutMultiplier;
   public uint WriteTotalTimeoutConstant;
  }
  [StructLayout(LayoutKind.Sequential)]
   private struct COMSTAT
  {
   public uint fFlags;
   public uint cbInQue;
   public uint cbOutQue;
  }
  [DllImport("kernel32.dll")]
  private static extern uint CreateFile(
   string lpFileName,
   uint dwDesiredAccess,
   int dwShareMode,
   uint lpSecurityAttributes,
   uint dwCreationDisposition,
   uint dwFlagsAndAttributes,
   uint hTemplateFile
   );
  [DllImport("kernel32.dll")]
  private static extern bool ReadFile(
   uint hFile,
   byte[] lpBuffer,
   uint nNumberOfBytesToRead,
   ref uint lpNumberOfBytesRead,
   System.IntPtr lpOverlapped
   );
  [DllImport("kernel32.dll")]
  private static extern bool GetCommState(uint hFile,ref DCB lpDCB);
  [DllImport("kernel32.dll")]
  private static extern bool SetCommState(uint hFile,ref DCB lpDCB);
  [DllImport("kernel32.dll")]
  private static extern bool GetCommTimeouts(uint hFile,ref COMMTIMEOUTS lpCommTimeouts);
  [DllImport("kernel32.dll")]
  private static extern bool SetCommTimeouts(uint hFile,ref COMMTIMEOUTS lpCommTimeouts);
  [DllImport("kernel32.dll")]
  private static extern bool CloseHandle(uint hObject);
  [DllImport("kernel32.dll")]
  private static extern uint GetLastError();
  [DllImport("kernel32.dll")]
  private static extern bool ClearCommError(uint hFile, ref uint lpErrors,ref COMSTAT lpStat);
  [DllImport("kernel32.dll")]
  private static extern bool PurgeComm(uint hFile, uint dwFlags);
  public SerialPort(GpsTracker gpsTracker, int iIndex, uint iCOMPort, uint iBaudRate, byte byByteSize, byte byParity, byte byStopBits, uint iFlowControl)
  {
   m_iIndex=iIndex;
   m_iCOMPort=iCOMPort;
   m_iBaudRate=iBaudRate;
   m_byByteSize=byByteSize;
   m_byParity=byParity;
   m_byStopBits=byStopBits;
   m_iFlowControl=iFlowControl;
   m_ThreadRx=null;
   m_hCOMPort=INVALID_HANDLE_VALUE;
   m_bClose=true;
   m_GpsTracker=gpsTracker;
   IsOpen=false;
  }
  public bool Open()
  {
   bool fRet=false;
   DCB dcbCommPort = new DCB();
   COMMTIMEOUTS ctoCommPort = new COMMTIMEOUTS();
   Close();
   m_hCOMPort = CreateFile("\\\\.\\COM" + m_iCOMPort.ToString().Trim() ,GENERIC_READ,0, 0,OPEN_EXISTING,0,0);
   if(m_hCOMPort != INVALID_HANDLE_VALUE)
   {
    uint uxErrors=0;
    COMSTAT xstat = new COMSTAT();
    ClearCommError (m_hCOMPort,ref uxErrors, ref xstat);
    PurgeComm(m_hCOMPort,PURGE_RXCLEAR);
    GetCommTimeouts(m_hCOMPort,ref ctoCommPort);
    ctoCommPort.ReadIntervalTimeout=0;
    ctoCommPort.ReadTotalTimeoutConstant = 100;
    ctoCommPort.ReadTotalTimeoutMultiplier = 0;
    SetCommTimeouts(m_hCOMPort,ref ctoCommPort);
    dcbCommPort.DCBlength=(uint)Marshal.SizeOf(dcbCommPort);
    GetCommState(m_hCOMPort, ref dcbCommPort);
    dcbCommPort.DCBlength=(uint)Marshal.SizeOf(dcbCommPort);
    dcbCommPort.BaudRate=m_iBaudRate;
    dcbCommPort.Parity=m_byParity;
    dcbCommPort.ByteSize=m_byByteSize;
    dcbCommPort.StopBits=m_byStopBits;
    dcbCommPort.flags=1;
    switch (m_iFlowControl)
    {
     case 0:
      dcbCommPort.flags=0x1091;
      break;
     case 1:
      dcbCommPort.flags=0x2095;
      break;
     case 2:
      dcbCommPort.flags=0x1391;
      break;
    }
    dcbCommPort.XonLim=0x50;
    dcbCommPort.XoffLim=0xC8;
    dcbCommPort.XonChar=Convert.ToChar(0x11);
    dcbCommPort.XoffChar=Convert.ToChar(0x13);
    dcbCommPort.wReserved=0;
    dcbCommPort.wReserved1=0;
    dcbCommPort.ErrorChar=Convert.ToChar(0);
    dcbCommPort.EvtChar=Convert.ToChar(0);
    dcbCommPort.EofChar=Convert.ToChar(0);
    if (SetCommState(m_hCOMPort, ref dcbCommPort))
     fRet=true;
   }
   IsOpen=fRet;
   m_bClose=!fRet;
   return fRet;
  }
  public bool Close()
  {
   bool bRet=false;
   if(m_hCOMPort != INVALID_HANDLE_VALUE)
   {
    IsOpen=false;
    m_bClose=true;
    if (m_ThreadRx!=null)
     m_ThreadRx.Join(500);
    bRet=CloseHandle(m_hCOMPort);
    m_hCOMPort = INVALID_HANDLE_VALUE;
   }
   return bRet;
  }
  public bool StartRx()
  {
   bool bRet=false;
   if(m_hCOMPort != INVALID_HANDLE_VALUE)
   {
    m_ThreadRx = new Thread(new ThreadStart(threadRead));
    m_ThreadRx.Priority = ThreadPriority.AboveNormal;
    m_ThreadRx.Name = "COMReaderThread";
    m_ThreadRx.Start();
   }
   return bRet;
  }
  public void threadRead()
  {
   uint uBytesRead=0;
   byte [] byData = new byte[100];
   System.Text.ASCIIEncoding asciiEncoder = new System.Text.ASCIIEncoding();
   try
   {
    PurgeComm(m_hCOMPort,PURGE_RXCLEAR);
    while (!m_bClose)
    {
     if (ReadFile(m_hCOMPort,byData,20,ref uBytesRead,IntPtr.Zero))
     {
      if (uBytesRead>0 && m_bClose==false)
      {
       string sData = asciiEncoder.GetString(byData,0,(int)uBytesRead);
       m_GpsTracker.COMCallback(m_iIndex,sData);
       try
       {
       if(m_GpsTracker.m_MessageMonitor!=null)
        m_GpsTracker.m_MessageMonitor.AddMessageCOMRaw(sData);
      }
       catch (Exception)
       {
        m_GpsTracker.m_MessageMonitor=null;
       }
      }
     }
     else
     {
      Thread.Sleep(50);
      if (GetLastError()==ERROR_OPERATION_ABORTED)
      {
       uint uxErrors=0;
       COMSTAT xstat = new COMSTAT();
       ClearCommError (m_hCOMPort,ref uxErrors, ref xstat);
       PurgeComm(m_hCOMPort,PURGE_RXCLEAR);
      }
     }
    }
   }
   catch(Exception)
   {
   }
  }
 }
}
