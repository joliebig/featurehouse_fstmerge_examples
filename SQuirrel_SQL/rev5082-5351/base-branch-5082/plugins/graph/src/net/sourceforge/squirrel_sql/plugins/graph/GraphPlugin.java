package net.sourceforge.squirrel_sql.plugins.graph;



import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallbackAdaptor;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.GraphXmlSerializer;


public class GraphPlugin extends DefaultSessionPlugin
{

   private Hashtable<IIdentifier, GraphController[]> _grapControllersBySessionID = 
       new Hashtable<IIdentifier, GraphController[]>();

   
   @SuppressWarnings("unused")
   private static ILogger s_log = LoggerController.createLogger(GraphPlugin.class);

   private PluginResources _resources;

   
   public String getInternalName()
   {
      return "graph";
   }

   
   public String getDescriptiveName()
   {
      return "Graph";
   }

   
   public String getVersion()
   {
      return "1.0";
   }

   
   public String getAuthor()
   {
      return "Gerd Wagner";
   }

   
   public String getChangeLogFileName()
   {
      return "changes.txt";
   }

   
   public String getHelpFileName()
   {
      return "readme.html";
   }

   
   public String getLicenceFileName()
   {
      return "licence.txt";
   }


   
   public synchronized void initialize() throws PluginException
   {
      super.initialize();
      IApplication app = getApplication();

      _resources =
         new PluginResources(
            "net.sourceforge.squirrel_sql.plugins.graph.graph",
            this);



      ActionCollection coll = app.getActionCollection();
      coll.add(new AddToGraphAction(app, _resources, this));
   }

   
   public void unload()
   {
      super.unload();
   }

   
   public PluginSessionCallback sessionStarted(final ISession session)
   {
      GraphXmlSerializer[] serializers  = GraphXmlSerializer.getGraphXmSerializers(this, session);
      GraphController[] controllers = new GraphController[serializers.length];

      for (int i = 0; i < controllers.length; i++)
      {
         controllers[i] = new GraphController(session, this, serializers[i]);
      }


      _grapControllersBySessionID.put(session.getIdentifier(), controllers);


      IObjectTreeAPI api = session.getSessionInternalFrame().getObjectTreeAPI();

      ActionCollection coll = getApplication().getActionCollection();
      api.addToPopup(DatabaseObjectType.TABLE, coll.get(AddToGraphAction.class));

      return new PluginSessionCallbackAdaptor(this);
   }


   public void sessionEnding(ISession session)
   {
      GraphController[] controllers = 
          _grapControllersBySessionID.remove(session.getIdentifier());

      for (int i = 0; i < controllers.length; i++)
      {
         controllers[i].sessionEnding();
      }
   }

   public GraphController[] getGraphControllers(ISession session)
   {
      return _grapControllersBySessionID.get(session.getIdentifier());
   }

   public String patchName(String name, ISession session)
   {

      int postfix = 0;
      if("Objects".equals(name))
      {
         ++postfix;
      }

      if("SQL".equals(name))
      {
         ++postfix;
      }

      GraphController[] controllers = _grapControllersBySessionID.get(session.getIdentifier());

      while(true)
      {
         boolean incremented = false;
         for (int i = 0; i < controllers.length; i++)
         {
            if(0 == postfix)
            {
               if(controllers[i].getTitle().equals(name))
               {
                  ++postfix;
                  incremented = true;
               }
            }
            else
            {
               if(controllers[i].getTitle().equals(name + "_" + postfix))
               {
                  ++postfix;
                  incremented = true;
               }
            }
         }

         if(false == incremented)
         {
            break;
         }
      }

      if(0 == postfix)
      {
         return name;
      }
      else
      {
         return name + "_" + postfix;
      }




   }

   public GraphController createNewGraphControllerForSession(ISession session)
   {
      GraphController[] controllers = _grapControllersBySessionID.get(session.getIdentifier());

      Vector<GraphController> v = new Vector<GraphController>();
      if(null != controllers)
      {
         v.addAll(Arrays.asList(controllers));
      }
      GraphController ret = new GraphController(session, this, null);
      v.add(ret);

      controllers = v.toArray(new GraphController[v.size()]);
      _grapControllersBySessionID.put(session.getIdentifier(), controllers);

      return ret;
   }

   public void removeGraphController(GraphController toRemove, ISession session)
   {
      GraphController[] controllers = _grapControllersBySessionID.get(session.getIdentifier());
      Vector<GraphController> v = new Vector<GraphController>();
      for (int i = 0; i < controllers.length; i++)
      {
         if(false == controllers[i].equals(toRemove))
         {
            v.add(controllers[i]);
         }
      }

      controllers = v.toArray(new GraphController[v.size()]);
      _grapControllersBySessionID.put(session.getIdentifier(), controllers);

   }
}
