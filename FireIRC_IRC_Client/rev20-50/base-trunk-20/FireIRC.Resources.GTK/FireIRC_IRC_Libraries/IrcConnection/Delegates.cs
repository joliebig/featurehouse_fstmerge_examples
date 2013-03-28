namespace FireIRC.Resources.IRC
{
    public delegate void ReadLineEventHandler(object sender, ReadLineEventArgs e);
    public delegate void WriteLineEventHandler(object sender, WriteLineEventArgs e);
    public delegate void AutoConnectErrorEventHandler(object sender, AutoConnectErrorEventArgs e);
}
