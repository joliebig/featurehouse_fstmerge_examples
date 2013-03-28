using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.Core.Security
{
    [ReflectorType("inMemoryCache")]
    public class InMemorySessionCache
        : SessionCacheBase
    {
        public InMemorySessionCache() : this(new SystemClock())
        {
        }
        public InMemorySessionCache(IClock clock) : base(clock)
        {
        }
    }
}
