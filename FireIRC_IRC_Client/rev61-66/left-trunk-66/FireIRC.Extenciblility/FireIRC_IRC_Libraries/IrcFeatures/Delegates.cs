namespace FireIRC.Extenciblility.IRCClasses
{
    public delegate void ctcpDelagte(CtcpEventArgs eventArgs);
    public delegate void DccConnectionHandler(object sender, DccEventArgs e);
    public delegate void DccChatLineHandler(object sender, DccChatEventArgs e);
    public delegate void DccSendPacketHandler(object sender, DccSendEventArgs e);
    public delegate void DccSendRequestHandler(object sender, DccSendRequestEventArgs e);
}
