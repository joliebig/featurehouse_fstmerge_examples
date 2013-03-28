using System.Net.Sockets;
namespace OVT.FireIRC.Resources.IRC
{
    internal class IrcTcpClient: TcpClient
    {
        public Socket Socket {
            get {
                return Client;
            }
        }
    }
}
