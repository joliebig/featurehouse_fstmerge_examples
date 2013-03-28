using System;
using System.Collections.Generic;
using System.Text;
namespace OVT.FireIRC.Resources
{
    public static class MessageParser
    {
        public static string ParseToGraphic(string @in)
        {
            string newstring = "";
            newstring += @in.Replace("", "");
            newstring = newstring.Replace("", "");
            newstring = newstring.Replace("", "");
            newstring = newstring.Replace("10", "");
            newstring = newstring.Replace("11", "");
            newstring = newstring.Replace("12", "");
            newstring = newstring.Replace("13", "");
            newstring = newstring.Replace("14", "");
            newstring = newstring.Replace("15", "");
            newstring = newstring.Replace("0", "");
            newstring = newstring.Replace("1", "");
            newstring = newstring.Replace("2", "");
            newstring = newstring.Replace("3", "");
            newstring = newstring.Replace("4", "");
            newstring = newstring.Replace("5", "");
            newstring = newstring.Replace("6", "");
            newstring = newstring.Replace("7", "");
            newstring = newstring.Replace("8", "");
            newstring = newstring.Replace("9", "");
            newstring = newstring.Replace("", "");
            return CreateRTFGroup(newstring);
        }
        private static string CreateRTFGroup(string input)
        {
            string[] output = input.Split('');
            return String.Join("", output);
        }
    }
}
