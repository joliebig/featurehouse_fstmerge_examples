using System.Net.Sockets;
namespace FireIRC.Resources.IRC
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
