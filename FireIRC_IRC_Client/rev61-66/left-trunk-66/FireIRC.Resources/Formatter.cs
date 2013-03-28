using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using OVT.Melissa.PluginSupport;
namespace OVT.FireIRC.Resources
{
    public class Formatter
    {
        public string Format(MessageType t, MessageInfo i, string server, string channel)
        {
            if (channel == null) { channel = "Server"; }
            string ret;
            if (t == MessageType.IrcError) { ret = new DateTimeHeader().Header + " * IRC Error: " + i.Message; }
            else if (t == MessageType.Message) { ret = new DateTimeHeader().Header + " <" + i.Nick + "> " + i.Message; }
            else if (t == MessageType.ActionMessage) { ret = new DateTimeHeader().Header + " ~ " + i.Nick + " " + i.Message; }
            else if (t == MessageType.NoticeMessage) { ret = new DateTimeHeader().Header + " [" + i.Nick + "] " + i.Message; }
            else if (t == MessageType.ServerMessage) { ret = new DateTimeHeader().Header + " * [SERVER]: " + i.Message; }
            else if (t == MessageType.WhoIs) { ret = new DateTimeHeader().Header + " * [Who Is]: " + i.Message; }
            else if (t == MessageType.IrcCommand) { ret = new DateTimeHeader().Header + " * " + i.Message; }
            else if (t == MessageType.MessageOut) { ret = new DateTimeHeader().Header + " --> <" + i.Nick + "> " + i.Message; }
            else if (t == MessageType.MessageOutAction) { ret = new DateTimeHeader().Header + " --> ~ " + i.Nick + " " + i.Message; }
            else { ret = null; }
            if (Directory.Exists(Path.Combine(PropertyService.ConfigDirectory, "Logs")) == false) { Directory.CreateDirectory(Path.Combine(PropertyService.ConfigDirectory, "Logs")); }
            channel = channel.Replace('|', '_');
            channel = channel.Replace('/', '_');
            channel = channel.Replace('\\', '_');
            channel = channel.Replace(':', '_');
            channel = channel.Replace('*', '_');
            channel = channel.Replace('?', '_');
            channel = channel.Replace('"', '_');
            channel = channel.Replace('<', '_');
            channel = channel.Replace('>', '_');
            File.AppendAllText(Path.Combine(PropertyService.ConfigDirectory, Path.Combine("Logs", channel+ "." + server + ".log")), ret + "\r\n");
            return ret;
        }
        public enum MessageType
        {
            Message,
            ActionMessage,
            NoticeMessage,
            ServerMessage,
            IrcError,
            IrcCommand,
            MessageOut,
            MessageOutAction,
            WhoIs,
        }
        public class DateTimeHeader
        {
            public string Date;
            public string Time;
            public string Date_Day;
            public string Date_Month;
            public string Date_Year;
            public string Time_Hours;
            public string Time_Minutes;
            public string Header
            {
                get { return "[" + Date + " " + Time + "]"; }
            }
            public DateTimeHeader()
            {
                DateTime d = DateTime.Now;
                Date_Day = d.Day.ToString();
                Date_Month = d.Month.ToString();
                Date_Year = d.Year.ToString();
                Time_Hours = d.Hour.ToString();
                Time_Minutes = d.Minute.ToString();
                Date = Date_Month + "/" + Date_Day + "/" + Date_Year;
                Time = ToDoubleDigit(Time_Hours) + ":" + ToDoubleDigit(Time_Minutes);
            }
            string ToDoubleDigit(string str)
            {
                string ret = "--";
                if (str == "0") { ret = "00"; }
                else if (str == "1") { ret = "01"; }
                else if (str == "2") { ret = "02"; }
                else if (str == "3") { ret = "03"; }
                else if (str == "4") { ret = "04"; }
                else if (str == "5") { ret = "05"; }
                else if (str == "6") { ret = "06"; }
                else if (str == "7") { ret = "07"; }
                else if (str == "8") { ret = "08"; }
                else if (str == "9") { ret = "09"; }
                else { ret = str; }
                return ret;
            }
        }
    }
    public class MessageInfo
    {
        public string Nick;
        public string Message;
        public MessageInfo(string n, string m)
        {
            Nick = n;
            Message = m;
        }
    }
}
