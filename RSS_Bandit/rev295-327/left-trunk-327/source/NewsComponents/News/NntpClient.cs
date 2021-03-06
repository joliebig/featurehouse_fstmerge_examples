using System;
using System.Collections;
using System.Collections.Specialized;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Text.RegularExpressions;
using NewsComponents.Utils;
namespace NewsComponents.News {
 internal class NntpMessage{
  private readonly NameValueCollection headers;
  private string id;
  private string body;
  internal NntpMessage(){
   headers = new NameValueCollection();
   id = body = String.Empty;
  }
  internal NameValueCollection Headers{
   get{
    return headers;
   }
  }
  internal string Id{
   get{
    return id;
   }
  }
  internal string Body{
   get{
    return body;
   }
  }
  internal void SetBody(string[] sLines){
   string sStat = sLines[0];
   sStat = sStat.Substring(sStat.IndexOf(" ") + 1);
   sStat = sStat.Substring(sStat.IndexOf(" ") + 1);
   sStat = sStat.Substring(sStat.IndexOf(" ") + 1);
   id = sStat;
   StringBuilder sBody = new StringBuilder();
   for(int i = 1; i < sLines.Length - 2; i++) {
    sBody.Append(sLines[i]);
    sBody.Append(Environment.NewLine);
   }
   body = sBody.ToString();
  }
  internal void SetHeaders(string[] sLines) {
   string sStat;
   string sTmp;
   sStat = sLines[0];
   sStat = sStat.Substring(sStat.IndexOf(" ") + 1);
   sStat = sStat.Substring(sStat.IndexOf(" ") + 1);
   sStat = sStat.Substring(sStat.IndexOf(" ") + 1);
   id = sStat;
   for(int i = 1; i < sLines.Length - 2; i++) {
    sTmp = sLines[i];
    if(sTmp == ".") {
     break;
    }
    int colonPos = sTmp.IndexOf(":");
    if(colonPos > 0) {
     headers.Add(sTmp.Substring(0, colonPos).Trim(),
      sTmp.Substring(colonPos + 1).Trim());
    }
   }
  }
  internal void SetXHeader(string pseudoId, string header, string value) {
   id = pseudoId;
   headers.Add(header, value);
  }
 }
 internal class NntpMessages
  : ReadOnlyCollectionBase{
  internal NntpMessages(){;}
  internal void Add(NntpMessage oMsg){
   InnerList.Add(oMsg);
  }
 }
 [Serializable]
 public class NntpWebException: WebException
 {
  public NntpWebException()
  {}
  public NntpWebException(string message): base(message) {}
  public NntpWebException(string message, Exception innerException): base(message, innerException) {}
 }
    internal class NntpClient: IDisposable{
  static readonly Regex xheaderResult = new Regex(@"(?<1>\d+)\s(?<2>.*)");
  private static readonly log4net.ILog _log = RssBandit.Common.Logging.Log.GetLogger(typeof(NntpClient));
  internal NntpClient(string Server, int Port) {
   socket = new MyTcpClient(Server, Port);
   socket.NoDelay = true;
   connectResp = GetData(false);
  }
  internal NntpClient(string Server): this(Server, 119){;}
        internal string ConnectResponse{
            get{
                return connectResp;
            }
        }
  internal int Timeout{
   set {
     socket.SendTimeout = value;
     socket.ReceiveTimeout = value;
   }
  }
        internal int FirstMsg{
            get{
                return firstMsg;
            }
        }
        internal int LastMsg{
            get{
                return lastMsg;
            }
        }
        public void Dispose(){
            if( socket != null){
                Send("QUIT");
                GetData(false);
                socket.Close();
            }
            socket = null;
        }
        internal bool AuthInfo(string userName, string password){
            int responseCode = SendAndParseResponse("AUTHINFO USER " + userName);
   if(responseCode == 281){
                return true;
            }else if(responseCode == 381){
                responseCode = SendAndParseResponse("AUTHINFO PASS " + password);
    if(responseCode < 300){
                    return true;
                }
            }
            return false;
        }
        private int SendAndParseResponse(string command)
        {
            try
            {
                Send(command);
                string response = GetData(false);
                if(response.Length > 3)
                {
                    return Convert.ToInt32(response.Substring(0,3));
                }
                return 999;
            }
            catch
            {
                return 999;
            }
        }
        internal void Groups(TextWriter writer)
        {
            string response;
   bool firstTimeAround = true;
            Send("LIST");
   do{
                response = GetData(true);
    if(firstTimeAround){
     if ((response.Length < 3) || response.Substring( 0, 3) != "215") {
      throw new NntpWebException(response);
     }
     firstTimeAround = false;
    }
    writer.Write(response);
            }while((response.EndsWith(".\r\n")!= true) && (response.Length > 0));
        }
        internal delegate void StreamCreator(string fileName, out Stream stream);
  internal void Post(string message) {
   Send("POST");
   string response = GetData(false);
   if (response.Substring( 0, 3) != "340") {
    throw new NntpWebException(response);
   }
   Send(message);
   response = GetData(false);
   if (response.Substring( 0, 3) != "240") {
    throw new NntpWebException(response);
   }
  }
        internal void Decode(NntpMessage message, StreamCreator streamCreator)
        {
            NntpMessage[] parts = new NntpMessage[1];
            parts[0] = message;
            DecodeMultiPart(parts, streamCreator);
        }
        internal void DecodeMultiPart(NntpMessage[] parts, StreamCreator streamCreator)
        {
            Regex split = new Regex(Environment.NewLine);
            Regex uuencodeFilePattern = new Regex(@"^begin\s\d\d\d\s+(?<1>.+)\s*");
            Regex yencodeFilePattern = new Regex(@"^\=ybegin .*name=(?<1>.+)$");
            bool bLookingForBegin = true;
            bool bIsYenc = false;
            Stream fileStream = null;
            try
            {
                for(int i = 0; i < parts.Length; i++)
                {
                    if(parts[i] == null)
                    {
                        return;
                    }
                }
                for(int i = 0; i < parts.Length; i++)
                {
                    NntpMessage msg = parts[i];
                    EnsureBody(msg);
                    string body = msg.Body;
                    string[] lines = split.Split(body);
                    for(int j = 0; j < lines.Length; j++)
                    {
                        string line = lines[j];
                        if(bLookingForBegin)
                        {
                            Match m = uuencodeFilePattern.Match(line);
                            if(m.Success)
                            {
                                bLookingForBegin = false;
                                string fileName = CleanFileName(m.Groups[1].Value);
                                streamCreator(fileName, out fileStream);
                                if(fileStream == null)
                                {
                                    return;
                                }
                            }
                            m = yencodeFilePattern.Match(line);
                            if(m.Success)
                            {
                                bIsYenc = true;
                                bLookingForBegin = false;
                                string fileName = CleanFileName(m.Groups[1].Value);
                                streamCreator(fileName, out fileStream);
                                if(fileStream == null)
                                {
                                    return;
                                }
                            }
                        }
                        else
                        {
                            if(bIsYenc)
                            {
                                if(line.StartsWith("=y"))
                                {
                                    continue;
                                }
                            {
                                char[] lineBufChars = line.ToCharArray();
                                byte[] lineBuf = new byte[lineBufChars.Length];
                                for(int k = 0; k < lineBuf.Length; k++)
                                {
                                    lineBuf[k] = (byte) lineBufChars[k];
                                }
                                byte[] decoded = new byte[lineBuf.Length];
                                int length = yydecode(decoded, 0, lineBuf, 0, lineBuf.Length);
                                fileStream.Write(decoded,0,length);
                            }
                            }
                            else
                            {
                                if(line.Length == 0 || line == "end")
                                {
                                    break;
                                }
                                byte[] lineBuf = Encoding.ASCII.GetBytes(line);
                                byte[] decoded = new byte[lineBuf.Length];
                                int length = uudecode(decoded, 0, lineBuf, 0, lineBuf.Length);
                                fileStream.Write(decoded,0,length);
                            }
                        }
                    }
                }
            }
            finally
            {
                if(fileStream != null)
                {
                    fileStream.Close();
                }
            }
        }
        internal void EnsureBody(NntpMessage msg)
        {
            if(msg.Body == "")
            {
                Send("BODY " + msg.Id);
                string sData = GetData(true);
                if(sData.Length > 1 && sData[0] == '2')
                {
                    msg.SetBody(Split.Split(sData));
                }
            }
        }
        private static byte uudecode(byte c)
        {
            return (byte) ((c - ' ') & 0x3f);
        }
        private static int uudecode(byte[] decoded, int decodedStart, byte[] encoded, int encodedStart, int encodedLength)
        {
            if(encodedLength < 1)
            {
                return 0;
            }
            int n = uudecode(encoded[encodedStart]);
            int expected = ((n+2)/3)*4;
            if( expected > (encodedLength-1))
            {
                byte[] newEncoded = new byte[expected+1];
                Array.Copy(encoded,encodedStart,newEncoded,0,encodedLength);
                for(int i = encodedLength; i <= expected;i++)
                {
                    newEncoded[i] = (byte) ' ';
                }
                return uudecode(decoded, decodedStart, newEncoded, 0, newEncoded.Length);
            }
            int e = encodedStart + 1;
            int d = decodedStart;
            int c = n;
            while(c > 0)
            {
                byte s0 = uudecode(encoded[e]);
                byte s1 = uudecode(encoded[e+1]);
                byte s2 = uudecode(encoded[e+2]);
                byte s3 = uudecode(encoded[e+3]);
                byte d0 = (byte) ((s0 << 2) | (0x03 & (s1 >> 4)));
                byte d1 = (byte) ((s1 << 4) | (0x0f & (s2 >> 2)));
                byte d2 = (byte) ((s2 << 6) | s3);
                decoded[d] = d0;
                if(c>1)
                {
                    decoded[d+1] = d1;
                }
                if(c>2)
                {
                    decoded[d+2] = d2;
                }
                e += 4;
                d += 3;
                c -= 3;
            }
            return n;
        }
        private static byte yydecode(byte c)
        {
            return (byte) ((c - 42) & 0xff);
        }
        private static int yydecode(byte[] decoded, int decodedStart, byte[] encoded, int encodedStart, int encodedLength)
        {
            if(encodedLength < 1)
            {
                return 0;
            }
            int e = encodedStart;
            int n = encodedLength;
            int d = decodedStart;
            while(n > 0)
            {
                byte c = encoded[e++];
                --n;
                if(c == '=')
                {
                    if(n > 0)
                    {
                        c = (byte)(encoded[e++]-64);
                        --n;
                    }
                }
                decoded[d++] = yydecode(c);
            }
            return d-decodedStart;
        }
        internal static void GroupFiles(NntpMessages messages, SortedList multiFiles)
        {
            Regex[] multipartPatterns = new Regex[2];
            multipartPatterns[0] = new Regex(@"(?<1>.*)\s*\((?<2>\d+)/(?<3>\d+)\)");
            multipartPatterns[1] = new Regex(@"(?<1>.*)\s*\[(?<2>\d+)/(?<3>\d+)\]");
            foreach(NntpMessage oMsg in messages)
            {
                string subject = oMsg.Headers["subject"];
                if(subject != null)
                {
                    foreach(Regex multipart in multipartPatterns)
                    {
                        Match m = multipart.Match(subject);
                        if(m.Success)
                        {
                            string file = m.Groups[1].Value;
                            int part = Convert.ToInt32(m.Groups[2].Value);
                            int whole = Convert.ToInt32(m.Groups[3].Value);
                            if(part <= 0 || part > whole)
                            {
                                continue;
                            }
                            NntpMessage[] v;
                            if( multiFiles.Contains(file) )
                            {
                                v = (NntpMessage[]) multiFiles[file];
                            }
                            else
                            {
                                v = new NntpMessage[whole];
                                multiFiles.Add(file,v);
                            }
                            if(v[part-1] != null)
                            {
                                NntpMessage old = v[part-1];
                                string oldSubject = old.Headers["subject"];
                                if(! oldSubject.StartsWith("Re:"))
                                {
                                    continue;
                                }
                            }
                            v[part-1] = oMsg;
                            goto nextPart;
                        }
                    }
                {
                    NntpMessage[] v = new NntpMessage[1];
                    v[0] = oMsg;
                    multiFiles.Add(oMsg.Id,v);
                }
                nextPart:;
                }
            }
        }
        private readonly Encoding ASCII = Encoding.ASCII;
        private readonly Regex Split = new Regex("\r\n");
        private MyTcpClient socket;
        private readonly String connectResp = "";
        private String CurrGroup = "";
  private int firstMsg;
        private int lastMsg;
        private readonly byte[] bRecv = new byte[4096];
        private readonly char[] bRecvChars = new Char[4096];
        private readonly StringBuilder sb = new StringBuilder();
        private class MyTcpClient : TcpClient
        {
            internal MyTcpClient(string Server, int Port)
            : base(Server, Port)
            {
            }
            internal int Receive(byte[] bytes, int offset, int length)
            {
                return Client.Receive(bytes, offset, length,0);
            }
        }
        private void Send(string sData)
        {
            byte[] bSend = ASCII.GetBytes(sData + Environment.NewLine);
            socket.GetStream().Write(bSend, 0, bSend.Length);
        }
  private string GetData(bool expectLongResponse){
   sb.Length = 0;
   StringWriter sw = new StringWriter(sb);
   GetData(expectLongResponse, sw);
   sw.Flush();
   sw.Close();
   return sb.ToString();
  }
        private void GetData(bool expectLongResponse, TextWriter writer)
        {
            int iBytes;
            sb.Length = 0;
            bool bFirstLine = true;
            bool bLookForDotEnd = false;
            bool bError = false;
            do
            {
                try
                {
                    iBytes = socket.Receive(bRecv, 0, bRecv.Length);
                    for(int i = 0; i < iBytes; i++)
                    {
                        bRecvChars[i] = (char) bRecv[i];
                    }
     writer.Write(bRecvChars,0,iBytes);
     if(bFirstLine)
                    {
                        if(sb.Length >= 3)
                        {
                            string codeString = sb.ToString(0,3);
                            int code = Convert.ToInt32(codeString);
                            bFirstLine = false;
                            if(code < 299)
                            {
                                if(expectLongResponse)
                                {
                                    bLookForDotEnd = true;
                                }
                            }
                            else
                            {
                                bError = true;
                            }
                        }
                    }
                    if(bLookForDotEnd && sb.Length >= 5 && sb.ToString(sb.Length-5, 5) == "\r\n.\r\n")
                    {
                        break;
                    }
                    else if((bError || !expectLongResponse) && sb.Length >= 2 && sb.ToString(sb.Length-2,2) == "\r\n")
                    {
                        break;
                    }
                }
                catch(Exception ex)
                {
                    throw new NntpWebException(ex.Message, ex);
                }
            }
            while(iBytes > 0);
        }
        internal void SelectGroup(string GroupName)
        {
            string sData;
            Send("GROUP " + GroupName);
            sData = GetData(false);
            if( sData.Substring(0,3) == "411")
            {
                throw new NntpWebException("No such group: " + GroupName);
   }
   else if (sData.Substring(0, 3) == "550")
   {
    throw new NntpWebException("Invalid newsgroup: " + GroupName);
   }
   string[] messageNumbers = sData.Split(' ');
   firstMsg = Convert.ToInt32( messageNumbers[2] );
   lastMsg = Convert.ToInt32( messageNumbers[3] );
   CurrGroup = GroupName;
        }
  internal void GetNntpMessages(DateTime since, int downloadCount, TextWriter sw){
   IDictionary capabilities = this.GetCapabilities(null);
   string sData;
   since = since.ToUniversalTime();
   Send(String.Format("NEWNEWS {0} {1} GMT", this.CurrGroup, since.ToString("yyMMdd HHmmss")));
   sData = GetData(true);
   if(sData.Length > 0 && sData[0] == '2' && sData.IndexOf("empty") == -1 &&
      (capabilities.Count == 0 || capabilities.Contains("NEWNEWS")))
   {
    string sDump = sData;
    StringCollection messageIds = new StringCollection();
    while((sData.EndsWith(".\r\n")!= true) && (sData.Length > 0)){
     sData = GetData(true);
     sDump = sDump + sData;
    }
    string[] sList = Split.Split(sDump);
    for(int i = 1 ; i <= sList.Length - 3; i++) {
     messageIds.Add(sList[i]);
    }
    GetNntpMessages(messageIds, downloadCount, sw);
   }else{
    int last = this.LastMsg;
    int first = Math.Max(last - downloadCount, this.FirstMsg);
    GetNntpMessages(first, last, sw);
   }
   sw.Flush();
  }
        internal void GetNntpMessage(string id, TextWriter sw){
   try{
    string sDump = String.Empty, sData;
    Send("ARTICLE " + id);
    do{
     sData = GetData(true);
     sDump = sDump + sData;
     if(sData.StartsWith("423") || sData.StartsWith("430")){
      break;
     }
    }while((sData.EndsWith(".\r\n")!= true) && (sData.Length > 0));
    if(sData.Length > 0 && sData[0] == '2'){
     sw.Write(sData);
    }
   }catch(Exception e){_log.Error("GetNntpMessage() failed", e);}
        }
        internal void GetNntpMessages(StringCollection oMsgs, int downloadCount, TextWriter sw)
        {
   int downloaded = 0;
   for(int i = oMsgs.Count; i-->0;){
    string sId = oMsgs[i];
    GetNntpMessage(sId, sw);
    downloaded++;
    if(downloaded == downloadCount){
     break;
    }
            }
  }
        internal void GetNntpMessages(int first, int last, TextWriter sw)
        {
            for(int i = first; i <= last; i++){
                GetNntpMessage(i.ToString(), sw);
            }
        }
        internal NntpMessages GetNntpMessages(int first, int last, string header)
        {
            NntpMessages oList = new NntpMessages();
            string result = SendCommand("XHDR " + header + " " + first + "-" + last, true);
            if(result.Length > 0 && result[0] == '2')
            {
                string[] lines = this.Split.Split(result);
                for(int i = 1; i < lines.Length - 2; i++)
                {
                    string line = lines[i];
                    Match m = xheaderResult.Match(line);
                    if(m.Success)
                    {
                        NntpMessage oMsg = new NntpMessage();
                        oMsg.SetXHeader(m.Groups[1].Value, header, m.Groups[2].Value);
                        oList.Add(oMsg);
                    }
                }
            }
            return oList;
        }
     internal IDictionary GetCapabilities(string additionalExtension) {
      HybridDictionary dict = new HybridDictionary();
      string cmd = "CAPABILITIES";
      if (!string.IsNullOrEmpty(additionalExtension))
       cmd += String.Format(" {0}", additionalExtension);
      string result = SendCommand(cmd, false);
   if(result.Length > 0 && result[0] == '1') {
    string[] lines = this.Split.Split(result);
    for(int i = 1; i < lines.Length - 2; i++) {
     dict.Add(lines[i], null);
    }
   }
      return dict;
     }
        internal string SendCommand(string NNTPCommand, bool expectLongResponse)
        {
            Send(NNTPCommand);
            return GetData(true);
        }
        private static string CleanFileName(string fileName)
        {
            return Path.GetFileName(fileName);
        }
    }
}
