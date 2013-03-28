using System;
namespace FireIRC.Extenciblility.IRCClasses
{
    public class IrcConstants {
        public const char CtcpChar = '\x1';
        public const char IrcBold = '\x2';
        public const char IrcColor = '\x3';
        public const char IrcReverse = '\x16';
        public const char IrcNormal = '\xf';
        public const char IrcUnderline = '\x1f';
        public const char CtcpQuoteChar = '\x20';
    }
    public enum DccSpeed
    {
        Rfc,
        RfcSendAhead,
        Turbo
    }
    public enum IrcColors {
        White = 0,
        Black = 1,
        Blue = 2,
        Green = 3,
        LightRed = 4,
        Brown = 5,
        Purple = 6,
        Orange = 7,
        Yellow = 8,
        LightGreen = 9,
        Cyan = 10,
        LightCyan = 11,
        LightBlue = 12,
        Pink = 13,
        Grey = 14,
        LightGrey = 15,
        Transparent = 99
    }
}
