using System.Globalization;
using System.ComponentModel;
using System.Threading;
using System.Windows.Forms;
using System;
using System.IO;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Formatters.Binary;
using System.Runtime.InteropServices;
using WorldWind;
using WorldWind.Renderable;
using WorldWind.PluginEngine;
using System.Net;
using System.Net.Sockets;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using System.Drawing;
using System.Xml;
using System.Data;
using System.Collections;
using Org.Mentalis.Security.Ssl;
namespace GpsTrackerPlugin
{
 [StructLayout(LayoutKind.Sequential)]
 public struct TCPSockets
 {
  public SecureSocket socket;
  public int iDeviceIndex;
  public byte [] byTcpBuffer;
  public string sStream;
 }
 public class GpsTrackerUDPTCP
 {
  GpsTracker m_GpsTracker;
  public GpsTrackerUDPTCP(GpsTracker gpsTracker)
  {
   m_GpsTracker=gpsTracker;
  }
  public void TcpConnectCallback( IAsyncResult ar )
  {
   try
   {
    TCPSockets tcpSockets = (TCPSockets)ar.AsyncState;
    if ( tcpSockets.socket.Connected )
    {
     AsyncCallback recieveData = new AsyncCallback( TcpOnRecievedData );
     tcpSockets.socket.BeginReceive( tcpSockets.byTcpBuffer, 0, tcpSockets.byTcpBuffer.Length, SocketFlags.None, recieveData , tcpSockets );
    }
   }
   catch( Exception)
   {
   }
  }
  public void TcpOnRecievedData( IAsyncResult ar )
  {
   String sGPS;
   TCPSockets tcpSockets = (TCPSockets)ar.AsyncState;
   try
   {
    int nBytesRec = tcpSockets.socket.EndReceive( ar );
    if( nBytesRec > 0)
    {
     sGPS = System.Text.Encoding.ASCII.GetString(tcpSockets.byTcpBuffer,0,nBytesRec);
     try
     {
     if (m_GpsTracker.m_MessageMonitor!=null)
      m_GpsTracker.m_MessageMonitor.AddMessageTCPUDPRaw(sGPS);
     }
     catch (Exception)
     {
      m_GpsTracker.m_MessageMonitor=null;
     }
     tcpSockets.sStream += sGPS;
     int iIndex=-1;
     char [] cEOL = {'\n','\r'};
     string sData="";
     do
     {
      iIndex=tcpSockets.sStream.IndexOfAny(cEOL);
      if (iIndex>=0)
      {
       sData=tcpSockets.sStream.Substring(0,iIndex);
       sData=sData.Trim(cEOL);
       tcpSockets.sStream=tcpSockets.sStream.Remove(0,iIndex+1);
       if (sData!="")
        m_GpsTracker.ShowGPSIcon(sData.ToCharArray(),sData.Length,false,tcpSockets.iDeviceIndex,false,true);
      }
     }
     while(iIndex>=0);
    }
    AsyncCallback recieveData = new AsyncCallback( TcpOnRecievedData );
    tcpSockets.socket.BeginReceive( tcpSockets.byTcpBuffer, 0, tcpSockets.byTcpBuffer.Length, SocketFlags.None, recieveData , tcpSockets );
   }
   catch(Exception)
   {
   }
  }
  public void UdpReceiveData(IAsyncResult iar)
  {
   try
   {
    String sGPS;
    IPEndPoint sender = new IPEndPoint(IPAddress.Any, 0);
    EndPoint tempRemoteEP = (EndPoint)sender;
    TCPSockets tcpSockets = (TCPSockets)iar.AsyncState;
    SecureSocket remote = tcpSockets.socket;
    int nBytesRec = remote.EndReceiveFrom(iar, ref tempRemoteEP);
    if( nBytesRec > 0)
    {
     sGPS = System.Text.Encoding.ASCII.GetString(tcpSockets.byTcpBuffer,0,nBytesRec);
     try
     {
     if (m_GpsTracker.m_MessageMonitor!=null)
      m_GpsTracker.m_MessageMonitor.AddMessageTCPUDPRaw(sGPS);
     }
     catch (Exception)
     {
      m_GpsTracker.m_MessageMonitor=null;
     }
     tcpSockets.sStream += sGPS;
     int iIndex=-1;
     char [] cEOL = {'\n','\r'};
     string sData="";
     do
     {
      iIndex=tcpSockets.sStream.IndexOfAny(cEOL);
      if (iIndex>=0)
      {
       sData=tcpSockets.sStream.Substring(0,iIndex);
       sData=sData.Trim(cEOL);
       tcpSockets.sStream=tcpSockets.sStream.Remove(0,iIndex+1);
       if (sData!="")
        m_GpsTracker.ShowGPSIcon(sData.ToCharArray(),sData.Length,false,tcpSockets.iDeviceIndex,false,true);
      }
     }
     while(iIndex>=0);
    }
    GPSSource gpsSource= (GPSSource)m_GpsTracker.m_gpsSourceList[tcpSockets.iDeviceIndex];
                EndPoint endPoint = new IPEndPoint(IPAddress.Any, Convert.ToInt32(gpsSource.iUDPPort));
    remote.BeginReceiveFrom(tcpSockets.byTcpBuffer, 0, 1024, SocketFlags.None, ref endPoint, new AsyncCallback(UdpReceiveData), tcpSockets);
   }
   catch(Exception)
   {
   }
  }
 }
}
