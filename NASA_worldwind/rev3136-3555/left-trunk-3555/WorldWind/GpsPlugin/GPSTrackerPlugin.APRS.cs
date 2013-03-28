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
using System.Text;
namespace GpsTrackerPlugin
{
 public class GpsTrackerAPRS
 {
  GpsTracker m_GpsTracker;
        APRSParser m_APRSParser;
  public GpsTrackerAPRS(GpsTracker gpsTracker)
  {
   m_GpsTracker=gpsTracker;
            m_APRSParser = new APRSParser();
  }
  public bool Parse(string sMsg, int iIndex)
  {
   bool bRet=false;
            if (m_APRSParser.ParseMessage(sMsg) >= 0)
   {
    bRet=false;
    GPSSource gpsSource=(GPSSource)m_GpsTracker.m_gpsSourceList[iIndex];
    for (int i=0; i<gpsSource.sCallSignFilterLines.Length-1; i++)
    {
     string source=m_APRSParser.GetSource();
     string sourceFilter=gpsSource.sCallSignFilterLines[i];
     int iWildIndex=sourceFilter.LastIndexOf('*');
     if (iWildIndex==0)
     {
      bRet=true;
      break;
     }
     else
      if (iWildIndex>=1)
     {
      sourceFilter=sourceFilter.Substring(0,iWildIndex);
      if (source.ToUpper().StartsWith(sourceFilter.ToUpper()))
      {
       bRet=true;
       break;
      }
     }
     else
     if (iWildIndex==-1 && source.ToUpper()==sourceFilter.ToUpper())
     {
      bRet=true;
      break;
     }
    }
    if (gpsSource.sCallSignFilterLines.Length<=1)
     bRet=true;
    if (bRet)
    {
                    gpsSource.GpsPos.m_fLat = Convert.ToSingle(m_APRSParser.GetLatitude());
                    gpsSource.GpsPos.m_fLon = Convert.ToSingle(m_APRSParser.GetLongitude());
                    gpsSource.GpsPos.m_fAlt = m_APRSParser.GetAltitude();
                    gpsSource.GpsPos.m_fSpeed = m_APRSParser.GetSpeedOverGround();
                    gpsSource.GpsPos.m_fHeading= m_APRSParser.GetCourseMadeGood();
                    gpsSource.GpsPos.m_sName = m_APRSParser.GetSource();
                    gpsSource.GpsPos.m_sComment = m_APRSParser.GetComment();
                    gpsSource.GpsPos.m_iAPRSIconCode = Convert.ToInt32(m_APRSParser.GetSymbolCode());
                    gpsSource.GpsPos.m_iAPRSIconTable = Convert.ToInt32(m_APRSParser.GetSymbolTable());
    }
   }
   return bRet;
  }
  public void threadAPRSIS()
  {
   int iIndex= Int32.Parse(Thread.CurrentThread.Name);
   GPSSource gpsSource=(GPSSource)m_GpsTracker.m_gpsSourceList[iIndex];
   string sCallSign=gpsSource.sCallSign;
   int iRefresh=gpsSource.iRefreshRate;
   while (sCallSign != null && sCallSign.Length > 0 && m_GpsTracker.m_fCloseThreads==false)
   {
    try
    {
     HttpWebRequest request=null;
     request = (HttpWebRequest)WebRequest.Create(gpsSource.sAPRSServerURL.Trim() + sCallSign);
     HttpWebResponse response=null;
     response = (HttpWebResponse)request.GetResponse();
     Stream resStream = response.GetResponseStream();
                    resStream.ReadTimeout = 60000;
     int iRead;
     byte[] byChar = new byte[1];
     StringBuilder sbMsg = new StringBuilder("");
     do
     {
      iRead = resStream.ReadByte();
      if (iRead >= 0)
      {
       byChar[0] = Convert.ToByte(iRead);
       if (byChar[0] != '\r' && byChar[0] != '\n')
        sbMsg.Append(Encoding.ASCII.GetString(byChar));
       else
       {
        string sMsg = sbMsg.ToString();
        try
        {
         if (m_GpsTracker.m_MessageMonitor!=null)
          m_GpsTracker.m_MessageMonitor.AddMessageUnfilteredAPRS(sMsg);
        }
        catch (Exception)
        {
         m_GpsTracker.m_MessageMonitor=null;
        }
        char [] cMessage = sMsg.ToCharArray();
        if (!m_GpsTracker.ShowGPSIcon(cMessage,sMsg.Length,false,iIndex,false,true))
        {
         if (sMsg.StartsWith("\"packet_id\"") == false)
         {
          CSVReader csvReader = new CSVReader();
          string [] sMsgField = csvReader.GetCSVLine(sMsg);
          if (sMsgField!=null && sMsgField.Length==14)
          {
           gpsSource.GpsPos.m_sName=sMsgField[1];
           if (sMsgField[2]!="")
            gpsSource.GpsPos.m_fLat=Convert.ToSingle(sMsgField[2]);
           if (sMsgField[3]!="")
            gpsSource.GpsPos.m_fLon=Convert.ToSingle(sMsgField[3]);
           if (sMsgField[4]!="")
            gpsSource.GpsPos.m_fHeading=Convert.ToSingle(sMsgField[4]);
           if (sMsgField[5]!="")
            gpsSource.GpsPos.m_fSpeed=Convert.ToSingle(sMsgField[5]);
           if (sMsgField[6]!="")
            gpsSource.GpsPos.m_fAlt=Convert.ToSingle(sMsgField[6]);
           if (sMsgField[7]!="")
            gpsSource.GpsPos.m_iAPRSIconTable=Convert.ToInt32(Convert.ToChar(sMsgField[7]));
           if (sMsgField[8]!="")
            gpsSource.GpsPos.m_iAPRSIconCode=Convert.ToInt32(Convert.ToChar(sMsgField[8]));
           gpsSource.GpsPos.m_sComment=sMsgField[9];
           if (sMsgField[13]!="")
           {
            DateTime dateAPRS = new DateTime(0);
            dateAPRS = DateTime.Parse(sMsgField[13]);
            gpsSource.GpsPos.m_iYear=dateAPRS.Year;
            gpsSource.GpsPos.m_iMonth=dateAPRS.Month;
            gpsSource.GpsPos.m_iDay=dateAPRS.Day;
            gpsSource.GpsPos.m_iHour=dateAPRS.Hour;
            gpsSource.GpsPos.m_iMin=dateAPRS.Minute;
            gpsSource.GpsPos.m_iSec=Convert.ToSingle(dateAPRS.Second);
           }
           gpsSource.GpsPos.m_fRoll=-1000F;
           gpsSource.GpsPos.m_fPitch=-1000F;
           gpsSource.GpsPos.m_fESpeed=-1000000;
           gpsSource.GpsPos.m_fNSpeed=-1000000;
           gpsSource.GpsPos.m_fVSpeed=-1000000;
           gpsSource.GpsPos.m_fDepth = -1000000;
           gpsSource.GpsPos.m_sAltUnit="m";
           gpsSource.GpsPos.m_sSpeedUnit="km\\h";
           sMsg="APRS:"+sMsg;
           cMessage = sMsg.ToCharArray();
           m_GpsTracker.ShowGPSIcon(cMessage,sMsg.Length,false,iIndex,false,false);
          }
         }
        }
        sbMsg = new StringBuilder("");
       }
      }
     } while (iRead >= 0 && m_GpsTracker.m_fCloseThreads==false);
    }
    catch (Exception)
    {
    }
    for (int iDelay=0; iDelay<(iRefresh*2); iDelay++)
    {
     if (m_GpsTracker.m_fCloseThreads==true)
      break;
     Thread.Sleep(500);
    }
   }
  }
 }
}
