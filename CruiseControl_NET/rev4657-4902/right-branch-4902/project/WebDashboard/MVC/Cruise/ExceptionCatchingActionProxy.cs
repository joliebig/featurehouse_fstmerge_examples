using System;
using System.Collections;
using ThoughtWorks.CruiseControl.WebDashboard.MVC.View;
using ThoughtWorks.CruiseControl.Core.Security;
namespace ThoughtWorks.CruiseControl.WebDashboard.MVC.Cruise
{
 public class ExceptionCatchingActionProxy : IAction
 {
  private readonly IAction proxiedAction;
  private readonly IVelocityViewGenerator velocityViewGenerator;
  public ExceptionCatchingActionProxy(IAction proxiedAction, IVelocityViewGenerator velocityViewGenerator)
  {
   this.proxiedAction = proxiedAction;
   this.velocityViewGenerator = velocityViewGenerator;
  }
  public IResponse Execute(IRequest request)
  {
            try
            {
                return proxiedAction.Execute(request);
            }
            catch (PermissionDeniedException pde)
            {
                Hashtable velocityContext = new Hashtable();
                velocityContext["exception"] = pde;
                velocityContext["type"] = "PermissionDeniedException";
                return velocityViewGenerator.GenerateView(@"ActionException.vm", velocityContext);
            }
            catch (Exception e)
            {
                Hashtable velocityContext = new Hashtable();
                velocityContext["exception"] = e;
                velocityContext["type"] = "unknown";
                return velocityViewGenerator.GenerateView(@"ActionException.vm", velocityContext);
            }
  }
 }
}
