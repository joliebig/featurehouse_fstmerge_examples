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
 public class GpsTrackerNMEA
 {
  GpsTracker m_GpsTracker;
  GpsTrackerAPRS m_Aprs;
  CSVReader m_CSVParser;
  enum SupportedMessages
  {
   msgNotSupported,
   msgGPRMC,
   msgGPGGA,
   msgAPRS,
   mspAPRSWebSession,
  }
  public GpsTrackerNMEA(GpsTracker gpsTracker)
  {
   m_GpsTracker=gpsTracker;
   m_Aprs = new GpsTrackerAPRS(gpsTracker);
   m_CSVParser = new CSVReader();
  }
  public bool ParseGPSMessage(char [] cNMEAMessage, int iMsgLength, bool fCheck, int iIndex)
  {
   bool fLocked=false;
   SupportedMessages supportedMsg=SupportedMessages.msgNotSupported;
   GPSSource gpsSource = (GPSSource)m_GpsTracker.m_gpsSourceList[iIndex];
   string [] sMsgField;
   String sGpsLat="";
   String sGpsLatMin="";
   String sGpsLon="";
   String sGpsLonMin="";
   String sNS="";
   String sEW="";
   String sGpsSpeed="";
   String sGpsAlt="";
   String sGpsAltUnit="";
   String sGpsHeading="";
   String sGpsDate="";
   String sCallSign="";
   int iIndex1;
            gpsSource.GpsPos.m_sName = "";
            gpsSource.GpsPos.m_sComment = "";
            gpsSource.GpsPos.m_iAPRSIconCode = -1;
            gpsSource.GpsPos.m_iAPRSIconTable = -1;
   try
   {
    char [] trimChars = { ' ', '\t', '\r', '\n' };
    String sGPRMC = new String(cNMEAMessage,0,iMsgLength);
    sGPRMC = sGPRMC.TrimStart(trimChars);
    sGPRMC = sGPRMC.TrimEnd(trimChars);
    if (iMsgLength > 11)
    {
     if (sGPRMC.StartsWith("$GPRMC")==true)
      supportedMsg=SupportedMessages.msgGPRMC;
     else
      if (sGPRMC.StartsWith("$GPGGA")==true)
      supportedMsg=SupportedMessages.msgGPGGA;
     else
      if (sGPRMC.StartsWith("APRS:")==true)
      supportedMsg=SupportedMessages.mspAPRSWebSession;
     else
      if (m_Aprs.Parse(sGPRMC,iIndex)==true)
       supportedMsg=SupportedMessages.msgAPRS;
     else
     {
      int iIndexGPRMC=sGPRMC.IndexOf("$GPRMC");
      int iIndexGPGGA=sGPRMC.IndexOf("$GPGGA");
      int iIndexCallSign=sGPRMC.IndexOf(">");
      int iIndexNMEA=-1;
      if (iIndexGPRMC>0)
      {
       supportedMsg=SupportedMessages.msgGPRMC;
       iIndexNMEA=iIndexGPRMC;
      }
      else
      if (iIndexGPGGA>0)
      {
       supportedMsg=SupportedMessages.msgGPGGA;
       iIndexNMEA=iIndexGPGGA;
      }
      if (iIndexNMEA>0 && iIndexCallSign>0 && iIndexNMEA>iIndexCallSign)
      {
       sCallSign=sGPRMC.Substring(0,iIndexCallSign);
       sGPRMC=sGPRMC.Substring(iIndexNMEA);
      }
      else
       supportedMsg=SupportedMessages.msgNotSupported;
      if (supportedMsg!=SupportedMessages.msgNotSupported)
      {
       bool bRet=false;
       if (gpsSource.sCallSignFilterLines.Length<=1)
        bRet=true;
       else
        for (int i=0; i<gpsSource.sCallSignFilterLines.Length-1; i++)
        {
         string source=sCallSign;
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
       if (!bRet)
        supportedMsg=SupportedMessages.msgNotSupported;
       else
        gpsSource.sDescription=sCallSign;
      }
     }
    }
    if (supportedMsg!=SupportedMessages.msgNotSupported)
    {
     if (fCheck)
      fLocked=true;
     else
     {
      switch (supportedMsg)
      {
       case SupportedMessages.msgGPRMC:
        sMsgField = m_CSVParser.GetCSVLine(sGPRMC);
        if (sMsgField[2].ToUpper()=="A" || gpsSource.bBabelNMEA==true)
        {
         sGpsLat = sMsgField[3];
         iIndex1=sGpsLat.IndexOf('.',0);
         sGpsLatMin = sGpsLat.Substring(iIndex1-2);
         sGpsLat = sGpsLat.Substring(0,iIndex1-2);
         sNS = sMsgField[4];
         sGpsLon = sMsgField[5];
         iIndex1=sGpsLon.IndexOf('.',0);
         sGpsLonMin = sGpsLon.Substring(iIndex1-2);
         sGpsLon = sGpsLon.Substring(0,iIndex1-2);
         sEW = sMsgField[6];
         sGpsSpeed = sMsgField[7];
         sGpsHeading = sMsgField[8];
         sGpsDate = sMsgField[9];
         sGpsAlt="NA";
         GetNMEATime(iIndex,sGPRMC);
         fLocked=true;
        }
        break;
       case SupportedMessages.msgGPGGA:
        sMsgField = m_CSVParser.GetCSVLine(sGPRMC);
        if (sMsgField[6]=="1" || sMsgField[6]=="2" || gpsSource.bBabelNMEA==true)
        {
         sGpsLat= sMsgField[2];
         iIndex1=sGpsLat.IndexOf('.',0);
         sGpsLatMin = sGpsLat.Substring(iIndex1-2);
         sGpsLat = sGpsLat.Substring(0,iIndex1-2);
         sNS= sMsgField[3];
         sGpsLon= sMsgField[4];
         iIndex1=sGpsLon.IndexOf('.',0);
         sGpsLonMin = sGpsLon.Substring(iIndex1-2);
         sGpsLon = sGpsLon.Substring(0,iIndex1-2);
         sEW= sMsgField[5];
         sGpsAlt = sMsgField[9];
         sGpsAltUnit = sMsgField[10];
         sGpsDate = "NA";
         sGpsSpeed = "NA";
         sGpsHeading = "NA";
         GetNMEATime(iIndex,sGPRMC);
                                    fLocked=true;
        }
        break;
       case SupportedMessages.msgAPRS:
        gpsSource.GpsPos.m_fRoll=-1000F;
        gpsSource.GpsPos.m_fPitch=-1000F;
        gpsSource.GpsPos.m_fESpeed=-1000000;
        gpsSource.GpsPos.m_fNSpeed=-1000000;
        gpsSource.GpsPos.m_fVSpeed=-1000000;
        gpsSource.GpsPos.m_fDepth = -1000000;
        gpsSource.GpsPos.m_sAltUnit="m";
        gpsSource.GpsPos.m_sSpeedUnit="km\\h";
        if (gpsSource.GpsPos.m_fLat!=-1000000F && gpsSource.GpsPos.m_fLon!=-1000000F)
         fLocked=true;
        break;
       case SupportedMessages.mspAPRSWebSession:
        sGPRMC=sGPRMC.Remove(0,"APRS:".Length);
        CSVReader csvReader = new CSVReader();
        sMsgField = csvReader.GetCSVLine(sGPRMC);
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
        gpsSource.GpsPos.m_fRoll=-1000F;
        gpsSource.GpsPos.m_fPitch=-1000F;
        gpsSource.GpsPos.m_fESpeed=-1000000;
        gpsSource.GpsPos.m_fNSpeed=-1000000;
        gpsSource.GpsPos.m_fVSpeed=-1000000;
        gpsSource.GpsPos.m_fDepth = -1000000;
        gpsSource.GpsPos.m_sAltUnit="m";
        gpsSource.GpsPos.m_sSpeedUnit="km\\h";
        fLocked=true;
        break;
      }
      if (fLocked)
      {
       switch (supportedMsg)
       {
        case SupportedMessages.msgGPRMC:
        case SupportedMessages.msgGPGGA:
         sGpsLatMin=sGpsLatMin.Replace('.', Convert.ToChar(CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator));
         gpsSource.GpsPos.m_fLat = System.Convert.ToDouble(sGpsLatMin)/(float)60;
         gpsSource.GpsPos.m_fLat = System.Convert.ToDouble(sGpsLat) + gpsSource.GpsPos.m_fLat;
         if (sNS.ToUpper()=="S")
          gpsSource.GpsPos.m_fLat*=(float)-1;
         sGpsLonMin=sGpsLonMin.Replace('.', Convert.ToChar(CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator));
         gpsSource.GpsPos.m_fLon = System.Convert.ToDouble(sGpsLonMin)/(float)60;
         gpsSource.GpsPos.m_fLon = System.Convert.ToDouble(sGpsLon) + gpsSource.GpsPos.m_fLon;
         if (sEW.ToUpper()=="W")
          gpsSource.GpsPos.m_fLon*=(float)-1;
         if (sGpsSpeed!="NA")
         {
          sGpsSpeed=sGpsSpeed.Replace('.', Convert.ToChar(CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator));
          gpsSource.GpsPos.m_fSpeed = System.Convert.ToSingle(sGpsSpeed);
          gpsSource.GpsPos.m_sSpeedUnit="knots";
         }
         if (sGpsAlt!="NA")
         {
          sGpsAlt=sGpsAlt.Replace('.', Convert.ToChar(CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator));
          gpsSource.GpsPos.m_fAlt = System.Convert.ToSingle(sGpsAlt);
          gpsSource.GpsPos.m_sAltUnit = sGpsAltUnit;
         }
         if (sGpsHeading!="NA")
         {
          sGpsHeading=sGpsHeading.Replace('.', Convert.ToChar(CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator));
          gpsSource.GpsPos.m_fHeading = System.Convert.ToSingle(sGpsHeading);
         }
         if (sGpsDate!="NA")
         {
          gpsSource.GpsPos.m_iDay=Convert.ToInt32(sGpsDate.Substring(0,2));
          gpsSource.GpsPos.m_iMonth=Convert.ToInt32(sGpsDate.Substring(2,2));
          gpsSource.GpsPos.m_iYear=Convert.ToInt32(sGpsDate.Substring(4,2));
         }
         if (gpsSource.datePosition.Year!=1771 && gpsSource.datePosition.Month!=6 &&
          gpsSource.datePosition.Month!=9)
         {
          gpsSource.GpsPos.m_iDay=gpsSource.datePosition.Day;
          gpsSource.GpsPos.m_iMonth=gpsSource.datePosition.Month;
          gpsSource.GpsPos.m_iYear=gpsSource.datePosition.Year;
          gpsSource.GpsPos.m_iHour=gpsSource.datePosition.Hour;
          gpsSource.GpsPos.m_iMin=gpsSource.datePosition.Minute;
          gpsSource.GpsPos.m_iSec=gpsSource.datePosition.Second;
         }
         gpsSource.GpsPos.m_fRoll=-1000F;
         gpsSource.GpsPos.m_fPitch=-1000F;
         gpsSource.GpsPos.m_fESpeed=-1000000;
         gpsSource.GpsPos.m_fNSpeed=-1000000;
         gpsSource.GpsPos.m_fVSpeed=-1000000;
         gpsSource.GpsPos.m_fDepth = -1000000;
         break;
       }
      }
     }
    }
   }
   catch (Exception)
   {
    fLocked=false;
   }
   return fLocked;
  }
  public DateTime GetNMEATime(int iGPSIndex, string sNMEA)
  {
   string [] sMsgField;
   string sGPSTime;
   string sHour;
   string sMinute;
   string sSecond;
   SupportedMessages supportedMsg=SupportedMessages.msgNotSupported;
            GPSSource gpsSource=null;
            if (iGPSIndex>=0)
                gpsSource = (GPSSource)m_GpsTracker.m_gpsSourceList[iGPSIndex];
   DateTime dtGPSTime = new DateTime(1771,6,9);
   try
   {
    if (sNMEA.StartsWith("$GPRMC")==true)
     supportedMsg=SupportedMessages.msgGPRMC;
    else
    if (sNMEA.StartsWith("$GPGGA")==true)
     supportedMsg=SupportedMessages.msgGPGGA;
    else
    if (sNMEA.StartsWith("$PIXSE,TIME__")==true)
     supportedMsg=SupportedMessages.msgNotSupported;
    if (iGPSIndex>=0)
    {
     gpsSource.GpsPos.m_iHour=-1;
     gpsSource.GpsPos.m_iMin=-1;
     gpsSource.GpsPos.m_iSec=-1;
    }
    switch (supportedMsg)
    {
     case SupportedMessages.msgGPRMC:
     case SupportedMessages.msgGPGGA:
      sMsgField = m_CSVParser.GetCSVLine(sNMEA);
      sGPSTime=sMsgField[1];
      sHour=sGPSTime.Substring(0,2);
      sMinute=sGPSTime.Substring(2,2);
      sSecond=sGPSTime.Substring(4,2);
      dtGPSTime = new DateTime(2001,1,1,Convert.ToInt32(sHour),Convert.ToInt32(sMinute),Convert.ToInt32(sSecond));
      if (iGPSIndex>=0)
      {
       gpsSource.GpsPos.m_iHour=Convert.ToInt32(sHour);
       gpsSource.GpsPos.m_iMin=Convert.ToInt32(sMinute);
       gpsSource.GpsPos.m_iSec=Convert.ToSingle(sSecond);
      }
      break;
    }
   }
   catch(Exception)
   {
    dtGPSTime = new DateTime(1771,6,9);
   }
   return dtGPSTime;
  }
  public bool CheckConvertGpx(string sFileName, string sRealName)
  {
   bool bRet=false;
   XmlTextReader reader=null;
   uint uProgress=0;
   uint uMaxProgress=0;
   try
   {
    reader = new XmlTextReader(sFileName);
    reader.WhitespaceHandling = WhitespaceHandling.None;
    XmlDocument xmlDoc = new XmlDocument();
    xmlDoc.Load(reader);
    reader.Close();
    XmlNode xnod = xmlDoc.DocumentElement;
    ArrayList arrayLat = new ArrayList();
    ArrayList arrayLon = new ArrayList();
    ArrayList arrayEle = new ArrayList();
    ArrayList arrayTime = new ArrayList();
    arrayLat.Clear();
    arrayLon.Clear();
    arrayEle.Clear();
    arrayTime.Clear();
    ParsingGPXChildren(xnod,arrayLat,arrayLon,arrayEle,arrayTime);
    if (arrayLat.Count>0)
    {
     uProgress=0;
     uMaxProgress=(uint)arrayLat.Count;
     m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("GPSTracker: Converting " + sRealName + ": " + Convert.ToString(uProgress) +" of " + Convert.ToString(uMaxProgress) + " gpx entries.");
     if(File.Exists(sFileName + ".NMEAText"))
      File.Delete(sFileName + ".NMEAText");
     StreamWriter sw=null;
     sw = File.CreateText(sFileName + ".NMEAText");
     for (int i=0; i<arrayLat.Count && sw!=null; i++)
     {
      int iIndex1=0;
      int iIndex2=arrayTime[i].ToString().IndexOf('T',iIndex1);
      String sDate=arrayTime[i].ToString().Substring(iIndex1,iIndex2-iIndex1);
      iIndex1=iIndex2+1;
      iIndex2=arrayTime[i].ToString().IndexOf('Z',iIndex1);
      String sTime=arrayTime[i].ToString().Substring(iIndex1,iIndex2-iIndex1);
      String sLat=arrayLat[i].ToString().Trim();
      String sLon=arrayLon[i].ToString().Trim();
      String sAlt=arrayEle[i].ToString().Trim();
      String sSpeed="0";
      String sCourse="0";
      String sNMEADate=sDate.Substring(8,2) + sDate.Substring(5,2) + sDate.Substring(2,2);
      String sNMEATime=sTime.Substring(0,2) + sTime.Substring(3,2) + sTime.Substring(6,2);
      String sNMEALat;
      String sNMEALatMin;
      String sNMEALatNS;
      iIndex1=sLat.IndexOf('.');
      if (sLat.StartsWith("-"))
      {
       sNMEALatNS="S";
       sNMEALat=sLat.Substring(1,iIndex1-1);
      }
      else
      {
       sNMEALatNS="N";
       sNMEALat=sLat.Substring(0,iIndex1);
      }
      if (sNMEALat.Length==1)
       sNMEALat="0"+sNMEALat;
      else
       if (sNMEALat.Length==0)
       sNMEALat="00";
      sLat=sLat.Replace('.', Convert.ToChar(CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator));
      double dLatMin=Convert.ToDouble(sLat.Substring(iIndex1))*60;
      sNMEALatMin=Convert.ToString(decimal.Round(Convert.ToDecimal(dLatMin),3));
      iIndex1=sNMEALatMin.IndexOf('.');
      if (iIndex1==-1)
       sNMEALatMin=sNMEALatMin+".0";
      iIndex1=sNMEALatMin.IndexOf('.');
      if (iIndex1==1)
       sNMEALatMin="0"+sNMEALatMin;
      sNMEALatMin=sNMEALatMin.Replace(Convert.ToChar(CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator),'.');
      sNMEALat+=sNMEALatMin;
      String sNMEALon;
      String sNMEALonMin;
      String sNMEALonEW;
      iIndex1=sLon.IndexOf('.');
      if (sLon.StartsWith("-"))
      {
       sNMEALonEW="W";
       sNMEALon=sLon.Substring(1,iIndex1-1);
      }
      else
      {
       sNMEALonEW="E";
       sNMEALon=sLon.Substring(0,iIndex1);
      }
      if (sNMEALon.Length==1)
       sNMEALon="00"+sNMEALon;
      else
       if (sNMEALon.Length==2)
       sNMEALon="0"+sNMEALon;
      else
       if (sNMEALon.Length==0)
       sNMEALon="000";
      sLon=sLon.Replace('.', Convert.ToChar(CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator));
      double dLonMin=Convert.ToDouble(sLon.Substring(iIndex1))*60;
      sNMEALonMin=Convert.ToString(decimal.Round(Convert.ToDecimal(dLonMin),3));
      iIndex1=sNMEALonMin.IndexOf('.');
      if (iIndex1==-1)
       sNMEALonMin=sNMEALonMin+".0";
      iIndex1=sNMEALonMin.IndexOf('.');
      if (iIndex1==1)
       sNMEALonMin="0"+sNMEALonMin;
      sNMEALonMin=sNMEALonMin.Replace(Convert.ToChar(CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator),'.');
      sNMEALon+=sNMEALonMin;
      String sNMEAAlt;
      sNMEAAlt = sAlt;
      String sNMEASpeed = sSpeed;
      String sNMEACourse = sCourse;
      string sConvertedGPRMC = "$GPRMC," + sNMEATime + ",A," + sNMEALat + "," + sNMEALatNS + "," + sNMEALon + "," + sNMEALonEW + "," + sNMEASpeed + "," + sNMEACourse + ".0," + sNMEADate + ",004.2,W,*70";
      string sConvertedGPGGA = "$GPGGA," + sNMEATime + "," + sNMEALat + "," + sNMEALatNS + "," + sNMEALon + "," + sNMEALonEW + ",1,05,1.5," + sNMEAAlt + ",M,,,*70";
      sw.WriteLine(sConvertedGPRMC);
      sw.WriteLine(sConvertedGPGGA);
      uProgress=(uint)i;
      m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("GPSTracker: Converting " + sRealName + ": " + Convert.ToString(uProgress) +" of " + Convert.ToString(uMaxProgress) + " gpx entries.");
     }
     uProgress=uMaxProgress;
     m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("GPSTracker: Converting " + sRealName + ": " + Convert.ToString(uProgress) +" of " + Convert.ToString(uMaxProgress) + " gpx entries.");
     if (sw!=null)
     {
      sw.Close();
      bRet=true;
     }
    }
   }
   catch(Exception)
   {
    if (reader!=null)
     reader.Close();
    m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("");
    m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("GPSTracker: Active");
   }
   if (reader!=null)
    reader.Close();
   m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("");
   m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("GPSTracker: Active");
   return bRet;
  }
  private void ParsingGPXChildren(XmlNode xnod, ArrayList arrayLat, ArrayList arrayLon, ArrayList arrayEle, ArrayList arrayTime)
  {
   try
  {
   XmlNode xnodWorking;
   if (xnod.NodeType == XmlNodeType.Element)
   {
    if (xnod.Attributes["lat"]!=null && xnod.Attributes["lon"]!=null && (xnod.Name.ToLower()=="trkpt" || xnod.Name.ToLower()=="rtept"))
    {
     double lat = Double.Parse(xnod.Attributes["lat"].Value);
     double lon = Double.Parse(xnod.Attributes["lon"].Value);
     double ele = Double.Parse(xnod.FirstChild.FirstChild.Value);
     string time = xnod.FirstChild.NextSibling.FirstChild.Value;
     arrayLat.Add((double)lat);
     arrayLon.Add((double)lon);
     arrayEle.Add((double)ele);
     arrayTime.Add((string)time);
    }
    else
    {
     if(xnod.HasChildNodes)
     {
      xnodWorking = xnod.FirstChild;
      while (xnodWorking != null)
      {
       ParsingGPXChildren(xnodWorking,arrayLat,arrayLon,arrayEle,arrayTime);
       xnodWorking = xnodWorking.NextSibling;
      }
     }
    }
   }
  }
   catch(Exception)
   {
   }
  }
  public bool CheckConvertNasaBaloon(string sFileName, string sRealName)
  {
   uint uProgress;
   uint uMaxProgress;
   bool bRet=false;
   long lLine=0;
   try
   {
    if(File.Exists(sFileName + ".NMEAText"))
     File.Delete(sFileName + ".NMEAText");
    StreamWriter sw=null;
    if(File.Exists(sFileName))
    {
     StreamReader fsGpsFile = File.OpenText(sFileName);
     if (fsGpsFile!=null)
     {
      FileInfo fi = new FileInfo(sFileName);
      uProgress=0;
      uMaxProgress=(uint)fi.Length;
      m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("GPSTracker: Converting " + sRealName + ": " + Convert.ToString(uProgress) +" of " + Convert.ToString(uMaxProgress) + " bytes.");
      string sGPSMsg=null;
      do
      {
       sGPSMsg=fsGpsFile.ReadLine();
       lLine++;
       if (lLine>50 && sw==null)
        break;
       if (sGPSMsg!=null && sGPSMsg.Length>18 &&
        sGPSMsg.StartsWith("$")==false &&
        sGPSMsg.Substring(2,1)=="/" && sGPSMsg.Substring(5,1)=="/" && sGPSMsg.Substring(8,1)=="," &&
        sGPSMsg.Substring(11,1)==":" && sGPSMsg.Substring(14,1)==":" && sGPSMsg.Substring(17,1)=="," )
       {
        int iIndex1=0;
        int iIndex2=sGPSMsg.IndexOf(',',iIndex1);
        String sDate=sGPSMsg.Substring(iIndex1,iIndex2-iIndex1);
        iIndex1=iIndex2+1;
        iIndex2=sGPSMsg.IndexOf(',',iIndex1);
        String sTime=sGPSMsg.Substring(iIndex1,iIndex2-iIndex1);
        iIndex1=iIndex2+1;
        iIndex2=sGPSMsg.IndexOf(',',iIndex1);
        if (iIndex2==iIndex1)
         continue;
        String sLat=sGPSMsg.Substring(iIndex1,iIndex2-iIndex1);
        iIndex1=iIndex2+1;
        iIndex2=sGPSMsg.IndexOf(',',iIndex1);
        if (iIndex2==iIndex1)
         continue;
        String sLon=sGPSMsg.Substring(iIndex1,iIndex2-iIndex1);
        iIndex1=iIndex2+1;
        iIndex2=sGPSMsg.IndexOf(',',iIndex1);
        if (iIndex2==iIndex1)
         continue;
        String sAlt=sGPSMsg.Substring(iIndex1,iIndex2-iIndex1);
        iIndex1=iIndex2+1;
        iIndex2=sGPSMsg.IndexOf(',',iIndex1);
        if (iIndex2==iIndex1)
         continue;
        String sSpeed=sGPSMsg.Substring(iIndex1,iIndex2-iIndex1);
        iIndex1=iIndex2+1;
        String sCourse=sGPSMsg.Substring(iIndex1);
        String sNMEADate=sDate.Substring(3,2) + sDate.Substring(0,2) + sDate.Substring(6,2);
        String sNMEATime=sTime.Substring(0,2) + sTime.Substring(3,2) + sTime.Substring(6,2);
        String sNMEALat;
        String sNMEALatMin;
        String sNMEALatNS;
        iIndex1=sLat.IndexOf('.');
        if (sLat.StartsWith("-"))
        {
         sNMEALatNS="S";
         sNMEALat=sLat.Substring(1,iIndex1-1);
        }
        else
        {
         sNMEALatNS="N";
         sNMEALat=sLat.Substring(0,iIndex1);
        }
        if (sNMEALat.Length==1)
         sNMEALat="0"+sNMEALat;
        else
         if (sNMEALat.Length==0)
         sNMEALat="00";
        sLat=sLat.Replace('.', Convert.ToChar(CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator));
        double dLatMin=Convert.ToDouble(sLat.Substring(iIndex1))*60;
        sNMEALatMin=Convert.ToString(decimal.Round(Convert.ToDecimal(dLatMin),3));
        iIndex1=sNMEALatMin.IndexOf('.');
        if (iIndex1==-1)
         sNMEALatMin=sNMEALatMin+".0";
        iIndex1=sNMEALatMin.IndexOf('.');
        if (iIndex1==1)
         sNMEALatMin="0"+sNMEALatMin;
        sNMEALatMin=sNMEALatMin.Replace(Convert.ToChar(CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator),'.');
        sNMEALat+=sNMEALatMin;
        String sNMEALon;
        String sNMEALonMin;
        String sNMEALonEW;
        iIndex1=sLon.IndexOf('.');
        if (sLon.StartsWith("-"))
        {
         sNMEALonEW="W";
         sNMEALon=sLon.Substring(1,iIndex1-1);
        }
        else
        {
         sNMEALonEW="E";
         sNMEALon=sLon.Substring(0,iIndex1);
        }
        if (sNMEALon.Length==1)
         sNMEALon="00"+sNMEALon;
        else
         if (sNMEALon.Length==2)
         sNMEALon="0"+sNMEALon;
        else
         if (sNMEALon.Length==0)
         sNMEALon="000";
        sLon=sLon.Replace('.', Convert.ToChar(CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator));
        double dLonMin=Convert.ToDouble(sLon.Substring(iIndex1))*60;
        sNMEALonMin=Convert.ToString(decimal.Round(Convert.ToDecimal(dLonMin),3));
        iIndex1=sNMEALonMin.IndexOf('.');
        if (iIndex1==-1)
         sNMEALonMin=sNMEALonMin+".0";
        iIndex1=sNMEALonMin.IndexOf('.');
        if (iIndex1==1)
         sNMEALonMin="0"+sNMEALonMin;
        sNMEALonMin=sNMEALonMin.Replace(Convert.ToChar(CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator),'.');
        sNMEALon+=sNMEALonMin;
        String sNMEAAlt;
        double dFeetToMeter=Convert.ToDouble(3048)/Convert.ToDouble(10000);
        double dAlt = Convert.ToDouble(sAlt) * dFeetToMeter;
        sNMEAAlt = Convert.ToString(dAlt);
        String sNMEASpeed = sSpeed;
        String sNMEACourse = sCourse;
        string sConvertedGPRMC = "$GPRMC," + sNMEATime + ",A," + sNMEALat + "," + sNMEALatNS + "," + sNMEALon + "," + sNMEALonEW + "," + sNMEASpeed + "," + sNMEACourse + ".0," + sNMEADate + ",004.2,W,*70";
        string sConvertedGPGGA = "$GPGGA," + sNMEATime + "," + sNMEALat + "," + sNMEALatNS + "," + sNMEALon + "," + sNMEALonEW + ",1,05,1.5," + sNMEAAlt + ",M,,,*70";
        if (sw==null)
        {
         if (File.Exists(sFileName + ".NMEAText"))
          File.Delete(sFileName + ".NMEAText");
         sw = File.CreateText(sFileName + ".NMEAText");
        }
        sw.WriteLine(sConvertedGPRMC);
        sw.WriteLine(sConvertedGPGGA);
       }
       if (sGPSMsg==null)
       {
        uProgress=uMaxProgress;
        m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("GPSTracker: Converting " + sRealName + ": " + Convert.ToString(uProgress) +" of " + Convert.ToString(uMaxProgress) + " bytes.");
        break;
       }
       else
       {
        if (uProgress+(uint)sGPSMsg.Length>uMaxProgress)
         uProgress=uMaxProgress;
        else
         uProgress+=(uint)sGPSMsg.Length;
        m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("GPSTracker: Converting " + sRealName + ": " + Convert.ToString(uProgress) +" of " + Convert.ToString(uMaxProgress) + " bytes.");
       }
      } while(true);
      uProgress=uMaxProgress;
      m_GpsTracker.m_gpsTrackerPlugin.pluginShowFixInfo("GPSTracker: Converting " + sRealName + ": " + Convert.ToString(uProgress) +" of " + Convert.ToString(uMaxProgress) + " bytes.");
      fsGpsFile.Close();
     }
     if (sw!=null)
     {
      sw.Close();
      bRet=true;
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
   return bRet;
  }
        public string ConvertToGPGGA(GPSPositionVariables gpsPos)
        {
            string sGPGGA = "";
            try
            {
                sGPGGA = "$GPGGA,";
                sGPGGA += String.Format("{0:00}", gpsPos.m_iHour) + String.Format("{0:00}", gpsPos.m_iMin) + String.Format("{0:00}", gpsPos.m_iSec) + ",";
                String sLat = Convert.ToString(gpsPos.m_fLat);
                String sNMEALat;
                String sNMEALatMin;
                String sNMEALatNS;
                int iIndex1 = sLat.IndexOf(CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator);
                if (iIndex1 == -1)
                    sLat = sLat + CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator + "0";
                iIndex1 = sLat.IndexOf(CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator);
                if (sLat.StartsWith("-"))
                {
                    sNMEALatNS = "S";
                    sNMEALat = sLat.Substring(1, iIndex1 - 1);
                }
                else
                {
                    sNMEALatNS = "N";
                    sNMEALat = sLat.Substring(0, iIndex1);
                }
                if (sNMEALat.Length == 1)
                    sNMEALat = "0" + sNMEALat;
                else
                    if (sNMEALat.Length == 0)
                        sNMEALat = "00";
                double dLatMin = Convert.ToDouble(sLat.Substring(iIndex1)) * 60;
                sNMEALatMin = Convert.ToString(decimal.Round(Convert.ToDecimal(dLatMin), 3));
                iIndex1 = sNMEALatMin.IndexOf(CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator);
                if (iIndex1 == -1)
                    sNMEALatMin = sNMEALatMin + ".0";
                iIndex1 = sNMEALatMin.IndexOf(CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator);
                if (iIndex1 == 1)
                    sNMEALatMin = "0" + sNMEALatMin;
                sNMEALatMin = sNMEALatMin.Replace(Convert.ToChar(CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator), '.');
                sNMEALat += sNMEALatMin;
                sGPGGA += sNMEALat + "," + sNMEALatNS + ",";
                String sLon = Convert.ToString(gpsPos.m_fLon);
                String sNMEALon;
                String sNMEALonMin;
                String sNMEALonEW;
                iIndex1 = sLon.IndexOf(CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator);
                if (iIndex1 == -1)
                    sLon = sLon + CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator + "0";
                iIndex1 = sLon.IndexOf(CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator);
                if (sLon.StartsWith("-"))
                {
                    sNMEALonEW = "W";
                    sNMEALon = sLon.Substring(1, iIndex1 - 1);
                }
                else
                {
                    sNMEALonEW = "E";
                    sNMEALon = sLon.Substring(0, iIndex1);
                }
                if (sNMEALon.Length == 1)
                    sNMEALon = "00" + sNMEALon;
                else
                    if (sNMEALon.Length == 2)
                        sNMEALon = "0" + sNMEALon;
                    else
                        if (sNMEALon.Length == 0)
                            sNMEALon = "000";
                double dLonMin = Convert.ToDouble(sLon.Substring(iIndex1)) * 60;
                sNMEALonMin = Convert.ToString(decimal.Round(Convert.ToDecimal(dLonMin), 3));
                iIndex1 = sNMEALonMin.IndexOf(CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator);
                if (iIndex1 == -1)
                    sNMEALonMin = sNMEALonMin + ".0";
                iIndex1 = sNMEALonMin.IndexOf(CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator);
                if (iIndex1 == 1)
                    sNMEALonMin = "0" + sNMEALonMin;
                sNMEALonMin = sNMEALonMin.Replace(Convert.ToChar(CultureInfo.CurrentCulture.NumberFormat.NumberDecimalSeparator), '.');
                sNMEALon += sNMEALonMin;
                sGPGGA += sNMEALon + "," + sNMEALonEW + ",";
                sGPGGA += "1,";
                sGPGGA += "12,";
                sGPGGA += "0.5,";
                sGPGGA += String.Format("{0:0000.0}", gpsPos.m_fAlt) + ",M,";
                sGPGGA += "0,M,";
                sGPGGA += ",,,";
                int iChecksum = Convert.ToByte(sGPGGA[1]);
                for (int iGPGGA = 2; iGPGGA < sGPGGA.Length; iGPGGA++)
                    iChecksum ^= Convert.ToByte(sGPGGA[iGPGGA]);
                sGPGGA += "*" + iChecksum.ToString("X2");
            }
            catch (Exception)
            {
                sGPGGA = "";
            }
            return sGPGGA;
        }
 }
}
