using System.Net.Sockets;
namespace FireIRC.Extenciblility.IRCClasses
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
