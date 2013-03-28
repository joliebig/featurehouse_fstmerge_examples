namespace ThoughtWorks.CruiseControl.Remote
{
    public class WebClientFactory<TClient>
        : IWebClientFactory
        where TClient : IWebClient, new()
    {
        public IWebClient Generate()
        {
            var client = new TClient();
            return client;
        }
    }
}
