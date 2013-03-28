using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.Remote.Messages;
using System.Security.Cryptography;
using System.IO;
namespace ThoughtWorks.CruiseControl.Remote
{
    public class EncryptingConnection
        : ServerConnectionBase, IServerConnection, IDisposable
    {
        private IServerConnection innerConnection;
        private byte[] cryptoKey = new byte[0];
        private byte[] cryptoIv = new byte[0];
        public EncryptingConnection(IServerConnection innerConnection)
        {
            this.innerConnection = innerConnection;
            innerConnection.SendMessageCompleted += PassOnSendMessageCompleted;
            innerConnection.RequestSending += PassOnRequestSending;
            innerConnection.ResponseReceived += PassOnResponseReceived;
        }
        public string Type
        {
            get { return innerConnection.Type; }
        }
        public virtual string Address
        {
            get { return innerConnection.Address; }
        }
        public string ServerName
        {
            get { return innerConnection.ServerName; }
        }
        public bool IsBusy
        {
            get { return innerConnection.IsBusy; }
        }
        public Response SendMessage(string action, ServerRequest request)
        {
            if ((cryptoKey.Length == 0) || (cryptoIv.Length == 0)) InitialisePassword();
            var encryptedRequest = new EncryptedRequest();
            encryptedRequest.Action = action;
            var crypto = new RijndaelManaged();
            crypto.Key = cryptoKey;
            crypto.IV = cryptoIv;
            encryptedRequest.EncryptedData = EncryptMessage(crypto, request.ToString());
            var response = innerConnection.SendMessage("ProcessSecureRequest", encryptedRequest);
            var encryptedResponse = response as EncryptedResponse;
            if ((response.Result == ResponseResult.Success) && (encryptedResponse != null))
            {
                var data = DecryptMessage(crypto, encryptedResponse.EncryptedData);
                response = XmlConversionUtil.ProcessResponse(data);
            }
            return response;
        }
        public void SendMessageAsync(string action, ServerRequest request)
        {
            this.SendMessageAsync(action, request, null);
        }
        public void SendMessageAsync(string action, ServerRequest request, object userState)
        {
            throw new NotImplementedException();
        }
        public void CancelAsync()
        {
            this.CancelAsync(null);
        }
        public void CancelAsync(object userState)
        {
            throw new NotImplementedException();
        }
        public virtual void Dispose()
        {
            var disposable = innerConnection as IDisposable;
            if (disposable != null)
            {
                disposable.Dispose();
            }
        }
        public event EventHandler<MessageReceivedEventArgs> SendMessageCompleted;
        private void InitialisePassword()
        {
            try
            {
                var publicKeyRequest = new ServerRequest();
                var publicKeyResponse = innerConnection.SendMessage("RetrievePublicKey", publicKeyRequest);
                if (publicKeyResponse.Result == ResponseResult.Failure)
                {
                    throw new CommunicationsException("Server does not export a public key: " + publicKeyResponse.ConcatenateErrors());
                }
                var crypto = new RijndaelManaged();
                crypto.KeySize = 128;
                crypto.GenerateKey();
                crypto.GenerateIV();
                cryptoKey = crypto.Key;
                cryptoIv = crypto.IV;
                var passwordKey = Convert.ToBase64String(cryptoKey);
                var passwordIv = Convert.ToBase64String(cryptoIv);
                var provider = new RSACryptoServiceProvider();
                provider.FromXmlString((publicKeyResponse as DataResponse).Data);
                var encryptedPasswordKey = Convert.ToBase64String(
                    provider.Encrypt(
                        UTF8Encoding.UTF8.GetBytes(passwordKey), false));
                var encryptedPasswordIv = Convert.ToBase64String(
                    provider.Encrypt(
                        UTF8Encoding.UTF8.GetBytes(passwordIv), false));
                var loginRequest = new LoginRequest(encryptedPasswordKey);
                loginRequest.AddCredential(LoginRequest.PasswordCredential, encryptedPasswordIv);
                var loginResponse = innerConnection.SendMessage("InitialiseSecureConnection", loginRequest);
                if (loginResponse.Result == ResponseResult.Failure)
                {
                    throw new CommunicationsException("Server did not allow the connection to be secured: " + loginResponse.ConcatenateErrors());
                }
            }
            catch
            {
                cryptoIv = new byte[0];
                cryptoKey = new byte[0];
                throw;
            }
        }
        private static string EncryptMessage(RijndaelManaged crypto, string message)
        {
            var encryptStream = new MemoryStream();
            var encrypt = new CryptoStream(encryptStream,
                crypto.CreateEncryptor(),
                CryptoStreamMode.Write);
            var dataToEncrypt = Encoding.UTF8.GetBytes(message);
            encrypt.Write(dataToEncrypt, 0, dataToEncrypt.Length);
            encrypt.FlushFinalBlock();
            encrypt.Close();
            var data = Convert.ToBase64String(encryptStream.ToArray());
            return data;
        }
        private static string DecryptMessage(RijndaelManaged crypto, string message)
        {
            var inputStream = new MemoryStream(Convert.FromBase64String(message));
            string data;
            using (var decryptionStream = new CryptoStream(inputStream,
                crypto.CreateDecryptor(),
                CryptoStreamMode.Read))
            {
                using (var reader = new StreamReader(decryptionStream))
                {
                    data = reader.ReadToEnd();
                }
            }
            return data;
        }
        private void PassOnSendMessageCompleted(object sender, MessageReceivedEventArgs args)
        {
            if (SendMessageCompleted != null)
            {
                SendMessageCompleted(this, args);
            }
        }
        private void PassOnRequestSending(object sender, CommunicationsEventArgs args)
        {
            FireRequestSending(args.Action, args.Message as ServerRequest);
        }
        private void PassOnResponseReceived(object sender, CommunicationsEventArgs args)
        {
            FireResponseReceived(args.Action, args.Message as Response);
        }
    }
}
