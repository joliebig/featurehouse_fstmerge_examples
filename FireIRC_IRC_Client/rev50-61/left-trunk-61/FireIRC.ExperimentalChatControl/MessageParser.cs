using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
namespace OVT.FireIRC.ExperimentalChatControl
{
    public class MessageParser
    {
        Color CC0, CC1, CC2, CC3, CC4, CC5, CC6, CC7, CC8, CC9, CC10, CC11, CC12, CC13, CC14, CC15;
        public string ParseToGraphic(string @in)
        {
            string newstring = "";
            newstring += @in.Replace("", "[B]");
            newstring = newstring.Replace("", "[U]");
            newstring = newstring.Replace("", "[O]");
            newstring = newstring.Replace("10", "[CC10,0]");
            newstring = newstring.Replace("11", "[CC11,0]");
            newstring = newstring.Replace("12", "[CC12,0]");
            newstring = newstring.Replace("13", "[CC13,0]");
            newstring = newstring.Replace("14", "[CC14,0]");
            newstring = newstring.Replace("15", "[CC15,0]");
            newstring = newstring.Replace("0", "[CC0,0]");
            newstring = newstring.Replace("1", "[CC1,0]");
            newstring = newstring.Replace("2", "[CC2,0]");
            newstring = newstring.Replace("3", "[CC3,0]");
            newstring = newstring.Replace("4", "[CC4,0]");
            newstring = newstring.Replace("5", "[CC5,0]");
            newstring = newstring.Replace("6", "[CC6,0]");
            newstring = newstring.Replace("7", "[CC7,0]");
            newstring = newstring.Replace("8", "[CC8,0]");
            newstring = newstring.Replace("9", "[CC9 ,0]");
            newstring = newstring.Replace("", "[O]");
            return newstring;
        }
        public Color GetColor(int colorNumber)
        {
            if (colorNumber == 0) { return CC0; }
            else if (colorNumber == 1) { return CC1; }
            else if (colorNumber == 2) { return CC2; }
            else if (colorNumber == 3) { return CC3; }
            else if (colorNumber == 4) { return CC4; }
            else if (colorNumber == 5) { return CC5; }
            else if (colorNumber == 6) { return CC6; }
            else if (colorNumber == 7) { return CC7; }
            else if (colorNumber == 8) { return CC8; }
            else if (colorNumber == 9) { return CC9; }
            else if (colorNumber == 10) { return CC10; }
            else if (colorNumber == 11) { return CC11; }
            else if (colorNumber == 12) { return CC12; }
            else if (colorNumber == 13) { return CC13; }
            else if (colorNumber == 14) { return CC14; }
            else if (colorNumber == 15) { return CC15; }
            else { return CC0; }
        }
        public void SetColor(int colorNumber, Color color)
        {
            if (colorNumber == 0) { CC0 = color; }
            else if (colorNumber == 1) { CC1 = color; }
            else if (colorNumber == 2) { CC2 = color; }
            else if (colorNumber == 3) { CC3 = color; }
            else if (colorNumber == 4) { CC4 = color; }
            else if (colorNumber == 5) { CC5 = color; }
            else if (colorNumber == 6) { CC6 = color; }
            else if (colorNumber == 7) { CC7 = color; }
            else if (colorNumber == 8) { CC8 = color; }
            else if (colorNumber == 9) { CC9 = color; }
            else if (colorNumber == 10) { CC10 = color; }
            else if (colorNumber == 11) { CC11 = color; }
            else if (colorNumber == 12) { CC12 = color; }
            else if (colorNumber == 13) { CC13 = color; }
            else if (colorNumber == 14) { CC14 = color; }
            else if (colorNumber == 15) { CC15 = color; }
        }
    }
}
