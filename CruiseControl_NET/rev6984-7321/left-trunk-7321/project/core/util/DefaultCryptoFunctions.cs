namespace ThoughtWorks.CruiseControl.Core.Util
{
    using System;
    using System.Security.Cryptography;
    using System.Text;
    public class DefaultCryptoFunctions
        : ICryptoFunctions
    {
        public string GenerateHash(string value)
        {
            var sha = new SHA512Managed();
            var data = Encoding.UTF8.GetBytes(value);
            var hashData = sha.ComputeHash(data);
            var hash = Convert.ToBase64String(hashData);
            return hash;
        }
    }
}
