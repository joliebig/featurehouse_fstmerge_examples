namespace FireIRC.Resources.IRC
{
    public delegate void IrcEventHandler(object sender, IrcEventArgs e);
    public delegate void CtcpEventHandler(object sender, CtcpEventArgs e);
    public delegate void ActionEventHandler(object sender, ActionEventArgs e);
    public delegate void ErrorEventHandler(object sender, ErrorEventArgs e);
    public delegate void PingEventHandler(object sender, PingEventArgs e);
    public delegate void KickEventHandler(object sender, KickEventArgs e);
    public delegate void JoinEventHandler(object sender, JoinEventArgs e);
    public delegate void NamesEventHandler(object sender, NamesEventArgs e);
    public delegate void PartEventHandler(object sender, PartEventArgs e);
    public delegate void InviteEventHandler(object sender, InviteEventArgs e);
    public delegate void OpEventHandler(object sender, OpEventArgs e);
    public delegate void DeopEventHandler(object sender, DeopEventArgs e);
    public delegate void HalfopEventHandler(object sender, HalfopEventArgs e);
    public delegate void DehalfopEventHandler(object sender, DehalfopEventArgs e);
    public delegate void VoiceEventHandler(object sender, VoiceEventArgs e);
    public delegate void DevoiceEventHandler(object sender, DevoiceEventArgs e);
    public delegate void BanEventHandler(object sender, BanEventArgs e);
    public delegate void UnbanEventHandler(object sender, UnbanEventArgs e);
    public delegate void TopicEventHandler(object sender, TopicEventArgs e);
    public delegate void TopicChangeEventHandler(object sender, TopicChangeEventArgs e);
    public delegate void NickChangeEventHandler(object sender, NickChangeEventArgs e);
    public delegate void QuitEventHandler(object sender, QuitEventArgs e);
    public delegate void AwayEventHandler(object sender, AwayEventArgs e);
    public delegate void WhoEventHandler(object sender, WhoEventArgs e);
    public delegate void MotdEventHandler(object sender, MotdEventArgs e);
    public delegate void PongEventHandler(object sender, PongEventArgs e);
}
