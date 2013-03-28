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
namespace GpsTrackerPlugin
{
 public class GpsTrackerFile
 {
  GpsTracker m_GpsTracker;
  public GpsTrackerFile(GpsTracker gpsTracker)
  {
   m_GpsTracker=gpsTracker;
  }
  public void PreprocessFile(int iIndex, String sFileName, string sRealName, bool bCheckPre, bool bForePreprocessing)
  {
            GPSSource gpsSource = (GPSSource)m_GpsTracker.m_gpsSourceList[iIndex];
   try
   {
    if( (File.Exists(sFileName + ".TrackAtOnce") || IsTrackAtOnce(sFileName)) &&
     (bForePreprocessing==false || bCheckPre))
    {
     string sTAOFileName;
     if (IsTrackAtOnce(sFileName))
      sTAOFileName=sFileName;
     else
      sTAOFileName=sFileName + ".TrackAtOnce";
     gpsSource.GpsPos.m_gpsTrack=null;
     BinaryReader binReader = new BinaryReader(File.Open(sTAOFileName, FileMode.Open));
     if(binReader.PeekChar() != -1)
     {
      gpsSource.GpsPos.m_gpsTrack=new GPSTrack();
      string sSign="GPSTracker.TrackAtOnce";
      char [] sSignature = binReader.ReadChars(sSign.Length);
      string sSignCompare = new string(sSignature);
      if (sSignCompare==sSign)
      {
       gpsSource.GpsPos.m_gpsTrack.m_uPointCount=binReader.ReadUInt32();
                            gpsSource.GpsPos.m_gpsTrack.SetSize(gpsSource.GpsPos.m_gpsTrack.m_uPointCount);
       byte[] byData = new byte[gpsSource.GpsPos.m_gpsTrack.m_uPointCount*8];
       byData=binReader.ReadBytes((int)gpsSource.GpsPos.m_gpsTrack.m_uPointCount*8);
       System.Buffer.BlockCopy(byData , 0, gpsSource.GpsPos.m_gpsTrack.m_fLat, 0, (int)gpsSource.GpsPos.m_gpsTrack.m_uPointCount*8 );
       byData=binReader.ReadBytes((int)gpsSource.GpsPos.m_gpsTrack.m_uPointCount*8);
       System.Buffer.BlockCopy(byData , 0, gpsSource.GpsPos.m_gpsTrack.m_fLon, 0, (int)gpsSource.GpsPos.m_gpsTrack.m_uPointCount*8 );
       byData=binReader.ReadBytes((int)gpsSource.GpsPos.m_gpsTrack.m_uPointCount*4);
       System.Buffer.BlockCopy(byData , 0, gpsSource.GpsPos.m_gpsTrack.m_fAlt, 0, (int)gpsSource.GpsPos.m_gpsTrack.m_uPointCount*4 );
      }
     }
     binReader.Close();
    }
    else
    {
     uint uProgress;
     uint uMaxProgress;
     StreamReader fsGpsFile = null;
     String sGpsLine;
     byte [] byNMEA;
     char [] cNMEA=null;
     char [] cNMEAMsg = new char[512];
     System.Text.ASCIIEncoding asciiEncoder = new System.Text.ASCIIEncoding();
     if(File.Exists(sFileName) && !IsTrackAtOnce(sFileName))
     {
      FileInfo fInfo = new FileInfo(sFileName);
      uProgress=0;
      uMaxProgress=(uint)fInfo.Length;
      m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("GPSTracker: Processing " + sRealName + ": " + Convert.ToString(uProgress) +" of " + Convert.ToString(uMaxProgress) + " bytes.");
      fsGpsFile = File.OpenText(sFileName);
      gpsSource.GpsPos.m_gpsTrack= new GPSTrack();
                        gpsSource.GpsPos.m_gpsTrack.SetSize(500);
                        uint uResizeArraySize=500;
      while(true)
      {
       sGpsLine=fsGpsFile.ReadLine();
       if (sGpsLine==null)
        break;
       uProgress+=(uint)sGpsLine.Length;
       m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("GPSTracker: Processing " + sRealName + ": " + Convert.ToString(uProgress) +" of " + Convert.ToString(uMaxProgress) + " bytes.");
       byNMEA = asciiEncoder.GetBytes(sGpsLine);
       cNMEA = asciiEncoder.GetChars(byNMEA);
       cNMEA.CopyTo(cNMEAMsg,0);
       if (m_GpsTracker.m_NMEA.ParseGPSMessage(cNMEAMsg,cNMEA.Length,false,iIndex)==true)
        gpsSource.GpsPos.m_gpsTrack.AddPoint(gpsSource.GpsPos.m_fLat,gpsSource.GpsPos.m_fLon,gpsSource.GpsPos.m_fAlt);
                            if (gpsSource.GpsPos.m_gpsTrack.m_uPointCount >= uResizeArraySize)
                            {
                                uResizeArraySize += 100;
                                gpsSource.GpsPos.m_gpsTrack.Resize(uResizeArraySize);
                            }
      }
      uProgress=(uint)fInfo.Length;
      m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("GPSTracker: Processing " + sRealName + ": " + Convert.ToString(uProgress) +" of " + Convert.ToString(uMaxProgress) + " bytes.");
      fsGpsFile.Close();
                        gpsSource.GpsPos.m_gpsTrack.Resize(gpsSource.GpsPos.m_gpsTrack.m_uPointCount);
      m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("GPSTracker: Creating " + sRealName + ".TrackAtOnce");
      using(BinaryWriter binWriter = new BinaryWriter(File.Open(sFileName + ".TrackAtOnce", FileMode.Create)))
      {
       string sSign="GPSTracker.TrackAtOnce";
       char [] sSignature = new char[sSign.Length];
       sSignature = sSign.ToCharArray();
       binWriter.Write(sSignature);
       binWriter.Write(gpsSource.GpsPos.m_gpsTrack.m_uPointCount);
       byte[] byData = new byte[gpsSource.GpsPos.m_gpsTrack.m_uPointCount*8];
       System.Buffer.BlockCopy( gpsSource.GpsPos.m_gpsTrack.m_fLat, 0, byData, 0, (int)gpsSource.GpsPos.m_gpsTrack.m_uPointCount*8 );
       binWriter.Write(byData,0,(int)gpsSource.GpsPos.m_gpsTrack.m_uPointCount*8);
       System.Buffer.BlockCopy( gpsSource.GpsPos.m_gpsTrack.m_fLon, 0, byData, 0, (int)gpsSource.GpsPos.m_gpsTrack.m_uPointCount*8 );
       binWriter.Write(byData,0,(int)gpsSource.GpsPos.m_gpsTrack.m_uPointCount*8);
       System.Buffer.BlockCopy( gpsSource.GpsPos.m_gpsTrack.m_fAlt, 0, byData, 0, (int)gpsSource.GpsPos.m_gpsTrack.m_uPointCount*4 );
       binWriter.Write(byData,0,(int)gpsSource.GpsPos.m_gpsTrack.m_uPointCount*4);
       binWriter.Close();
      }
     }
    }
   }
   catch(Exception)
   {
    m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("");
    m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("GPSTracker: Active");
   }
   m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("");
   m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("GPSTracker: Active");
  }
  public bool IsTrackAtOnce(string sFileName)
  {
   bool bRet=false;
   try
   {
    BinaryReader binReader = new BinaryReader(File.Open(sFileName, FileMode.Open));
    if(binReader.PeekChar() != -1)
    {
     string sSign="GPSTracker.TrackAtOnce";
     char [] sSignature = binReader.ReadChars(sSign.Length);
     string sSignCompare = new string(sSignature);
     if (sSignCompare==sSign)
      bRet=true;
    }
    binReader.Close();
   }
   catch(Exception)
   {
   }
   return bRet;
  }
  public void threadStartFile()
  {
   int iIndex= Int32.Parse(Thread.CurrentThread.Name);
   GPSSource gpsSource = (GPSSource)m_GpsTracker.m_gpsSourceList[iIndex];
   bool bShowError=true;
   while(m_GpsTracker.m_fCloseThreads==false)
   {
    gpsSource.GpsPos.m_gpsTrack = null;
    try
    {
     string sFileName="";
     lock("threadStartFile")
     {
      m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("GPSTracker: Downloading " + gpsSource.sFileName);
      WebClient myWebClient = new WebClient();
                        sFileName = GpsTrackerPlugin.m_sPluginDirectory + "\\DownloadedFile" + Convert.ToString(iIndex) + ".gpsTracker";
      if (File.Exists(sFileName))
       File.Delete(sFileName);
      string sWebReload="";
      if (!File.Exists(gpsSource.sFileName))
       sWebReload="?r=" + System.Guid.NewGuid().ToString();
      myWebClient.DownloadFile(gpsSource.sFileName + sWebReload, sFileName);
     }
     if (File.Exists(sFileName))
     {
      bool bConverted=false;
      lock("threadStartFile")
      {
       bConverted=m_GpsTracker.m_NMEA.CheckConvertGpx(sFileName, gpsSource.sFileName);
       if (!bConverted)
        bConverted=m_GpsTracker.m_NMEA.CheckConvertNasaBaloon(sFileName, gpsSource.sFileName);
      }
      if (bConverted)
       sFileName=sFileName + ".NMEAText";
      if (gpsSource.bTrackAtOnce!=true && !m_GpsTracker.m_File.IsTrackAtOnce(sFileName))
      {
       gpsSource.sFileNameSession=sFileName;
       gpsSource.iFilePlaySpeed=gpsSource.iPlaySpeed;
       threadFile();
      }
      else
      {
       if (gpsSource.iReload>0 && File.Exists(sFileName + ".TrackAtOnce"))
        File.Delete(sFileName + ".TrackAtOnce");
       lock("threadStartFile")
       {
        m_GpsTracker.m_File.PreprocessFile(iIndex,sFileName,gpsSource.sFileName,false,gpsSource.bForcePreprocessing);
       }
       GPSRenderInformation renderInfo = new GPSRenderInformation();
       renderInfo.bPOI=false;
       renderInfo.iIndex=iIndex;
       renderInfo.sDescription=gpsSource.sDescription;
       renderInfo.fFix=false;
       renderInfo.bShowInfo=m_GpsTracker.m_bInfoText;
       renderInfo.bTrackLine=false;
       renderInfo.gpsTrack=gpsSource.GpsPos.m_gpsTrack;
       renderInfo.bRestartTrack=false;
       renderInfo.iDay=gpsSource.GpsPos.m_iDay;
       renderInfo.iMonth=gpsSource.GpsPos.m_iMonth;
       renderInfo.iYear=gpsSource.GpsPos.m_iYear;
       renderInfo.colorTrack=gpsSource.colorTrack;
                            renderInfo.sIcon = gpsSource.sIconPath;
       m_GpsTracker.m_gpsTrackerPlugin.pluginShowOverlay(renderInfo);
       if (gpsSource.bTrack==true)
        m_GpsTracker.m_gpsTrackerPlugin.pluginWorldWindowGotoLatLonHeading(gpsSource.GpsPos.m_gpsTrack.m_fLat[0],gpsSource.GpsPos.m_gpsTrack.m_fLon[0],-1F,gpsSource.iStartAltitud);
       if (gpsSource.iStartAltitud>0)
        gpsSource.iStartAltitud=0;
      }
     }
     if (gpsSource.iReload==0 || m_GpsTracker.m_fCloseThreads==true)
      break;
     else
     {
      for (int i=0; i<gpsSource.iReload && m_GpsTracker.m_fCloseThreads==false; i++)
       Thread.Sleep(1000);
     }
    }
    catch(Exception)
    {
     if (bShowError)
     {
      lock("threadStartFile")
      {
       bShowError=false;
       m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("GPSTracker: Unable to Download " + gpsSource.sFileName + ". Retrying in the background.");
       for (int i=0; i<5 && m_GpsTracker.m_fCloseThreads==false; i++)
        Thread.Sleep(1000);
       m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("");
       m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("GPSTRacker: Active");
      }
      for (int i=0; i<60 && m_GpsTracker.m_fCloseThreads==false; i++)
       Thread.Sleep(1000);
     }
     else
     {
      m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("");
      m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("GPSTRacker: Active");
      for (int i=0; i<60 && m_GpsTracker.m_fCloseThreads==false; i++)
       Thread.Sleep(1000);
     }
    }
   }
  }
  public void threadFile()
  {
   int iIndex= Int32.Parse(Thread.CurrentThread.Name);
   GPSSource gpsSource = (GPSSource)m_GpsTracker.m_gpsSourceList[iIndex];
   int iIndexFile;
   int iIndexNext;
   StreamReader fsGpsFile = null;
   System.Text.ASCIIEncoding asciiEncoder = new System.Text.ASCIIEncoding();
   String sGpsLine=null;
   String sIndex;
   byte [] byNMEA;
   char [] cNMEA=null;
   char [] cNMEAMsg = new char[512];
   DateTime dtCurrent;
   DateTime dtNext = new DateTime(0);
   int iEnd=0;
   bool bRestartTrack=false;
   try
   {
    if(File.Exists(gpsSource.sFileNameSession))
    {
     while(m_GpsTracker.m_fCloseThreads==false)
     {
      bRestartTrack=true;
      fsGpsFile = File.OpenText(gpsSource.sFileNameSession);
      {
       if (m_GpsTracker.m_fPlayback==true)
       {
        m_GpsTracker.LoadSettings(fsGpsFile,false);
        iIndexFile=iIndex+1;
        do
        {
         sIndex=fsGpsFile.ReadLine();
         if (sIndex!=null)
          iEnd=sIndex.IndexOf(",");
         if (sIndex!=null && iEnd>0)
         {
          iIndexFile=Convert.ToInt32(sIndex.Substring(0,iEnd));
          if (iIndexFile==iIndex && sIndex.Length>iEnd+1)
          {
           sGpsLine=sIndex.Substring(iEnd+1);
           break;
          }
         }
         else
          break;
        } while(m_GpsTracker.m_fCloseThreads==false);
       }
       else
       {
        sIndex="dummyindex";
        iIndexFile=iIndex;
        sGpsLine=fsGpsFile.ReadLine();
       }
       if (sGpsLine!=null)
       {
        byNMEA = asciiEncoder.GetBytes(sGpsLine);
        cNMEA = asciiEncoder.GetChars(byNMEA);
        cNMEA.CopyTo(cNMEAMsg,0);
        if (m_GpsTracker.m_iPlaybackSpeed>0)
         dtNext=m_GpsTracker.m_NMEA.GetNMEATime(-1,sGpsLine);
       }
       while(sIndex!=null && sGpsLine!=null && m_GpsTracker.m_fCloseThreads==false && iIndexFile==iIndex)
       {
        if (sGpsLine!="" && sIndex!="")
        {
         try
         {
          if (m_GpsTracker.m_MessageMonitor!=null)
           m_GpsTracker.m_MessageMonitor.AddMessageFileRaw(sGpsLine);
         }
         catch (Exception)
         {
          m_GpsTracker.m_MessageMonitor=null;
         }
         if (m_GpsTracker.ShowGPSIcon(cNMEAMsg,cNMEA.Length,false,iIndex,bRestartTrack,true)==true)
         {
          if (bRestartTrack)
           bRestartTrack=false;
          dtCurrent=dtNext;
          while(m_GpsTracker.m_fCloseThreads==false)
          {
           if (m_GpsTracker.m_fPlayback==true)
           {
            sIndex=fsGpsFile.ReadLine();
            if (sIndex!=null)
            {
             iEnd=sIndex.IndexOf(",");
             if (iEnd>0 && sIndex.Length>iEnd+1)
             {
              sGpsLine=sIndex.Substring(iEnd+1);
              sIndex=sIndex.Substring(0,iEnd);
             }
             else
              sIndex="";
            }
           }
           else
           {
            sIndex=Convert.ToString(iIndex);
            sGpsLine=fsGpsFile.ReadLine();
           }
           if (sGpsLine==null || sIndex==null)
            break;
           else
           if (sGpsLine!="" && sIndex!="")
           {
            iIndexNext=Convert.ToInt32(sIndex);
            byNMEA = asciiEncoder.GetBytes(sGpsLine);
            cNMEA = asciiEncoder.GetChars(byNMEA);
            cNMEA.CopyTo(cNMEAMsg,0);
            if (iIndexNext==iIndex && m_GpsTracker.m_NMEA.ParseGPSMessage(cNMEAMsg,cNMEA.Length,true,iIndex)==true)
            {
             if (m_GpsTracker.m_iPlaybackSpeed>0 && gpsSource.iFilePlaySpeed>0)
             {
              dtNext=m_GpsTracker.m_NMEA.GetNMEATime(-1,sGpsLine);
              if (dtCurrent.Year!=1771 && dtNext.Year!=1771)
              {
               if (dtNext<dtCurrent)
                dtNext=dtNext.AddDays(1);
               TimeSpan tsDelay = dtNext.Subtract(dtCurrent);
               double dDelay=tsDelay.TotalMilliseconds;
               double dSpeed=Convert.ToDouble(gpsSource.iFilePlaySpeed);
               dDelay/=dSpeed;
               dDelay/=m_GpsTracker.m_iPlaybackSpeed;
               Thread.Sleep(Convert.ToInt32(dDelay));
               break;
              }
              else
              {
               Thread.Sleep(1);
               break;
              }
             }
             else
             {
              Thread.Sleep(1);
              break;
             }
            }
            else
             Thread.Sleep(1);
           }
           else
           {
            Thread.Sleep(1);
           }
          }
         }
         else
         {
          sIndex="dummyindex";
          iIndexFile=iIndex;
          sGpsLine=fsGpsFile.ReadLine();
          if (sGpsLine!=null)
          {
           byNMEA = asciiEncoder.GetBytes(sGpsLine);
           cNMEA = asciiEncoder.GetChars(byNMEA);
           cNMEA.CopyTo(cNMEAMsg,0);
           if (m_GpsTracker.m_iPlaybackSpeed>0)
            dtNext=m_GpsTracker.m_NMEA.GetNMEATime(-1,sGpsLine);
          }
          Thread.Sleep(1);
         }
        }
        else
        {
         if (sGpsLine=="")
          sGpsLine=fsGpsFile.ReadLine();
         Thread.Sleep(1);
        }
       }
       if (fsGpsFile!=null)
       {
        fsGpsFile.Close();
        fsGpsFile=null;
       }
       if (gpsSource.iReload==0 && m_GpsTracker.m_fCloseThreads==false)
        Thread.Sleep(3000);
       else
        break;
      }
     }
    }
   }
   catch(Exception)
   {
   }
   if (fsGpsFile!=null)
   {
    fsGpsFile.Close();
    fsGpsFile=null;
   }
  }
 }
}
