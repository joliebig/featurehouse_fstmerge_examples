
package genj.search;

import java.util.List;

public interface WorkerListener {

  public void started();
  
  public void more(List<Hit> hits);
  
  public void stopped();
  
}
