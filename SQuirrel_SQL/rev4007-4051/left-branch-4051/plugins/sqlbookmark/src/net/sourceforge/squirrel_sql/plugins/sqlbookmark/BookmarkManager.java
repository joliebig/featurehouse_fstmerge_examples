

package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import net.sourceforge.squirrel_sql.fw.completion.CompletionCandidates;
import net.sourceforge.squirrel_sql.fw.completion.ICompletorModel;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;


public class BookmarkManager implements ICompletorModel
{

   
   private File bookmarkFile;

   
   private ArrayList<Bookmark> bookmarks = new ArrayList<Bookmark>();

   
   private HashMap<String, Integer> bookmarkIdx = new HashMap<String, Integer>();
   private SQLBookmarkPlugin _plugin;

   public BookmarkManager(SQLBookmarkPlugin plugin)
   {
      try
      {
         _plugin = plugin;
         bookmarkFile = new File(_plugin.getPluginUserSettingsFolder(), "bookmarks.xml");
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   
   protected boolean add(Bookmark bookmark)
   {
      Integer idxInt = bookmarkIdx.get(bookmark.getName());
      if (idxInt != null)
      {
         bookmarks.set(idxInt.intValue(), bookmark);
         return true;
      }
      else
      {
         bookmarks.add(bookmark);
         idxInt = bookmarks.size() - 1;
         bookmarkIdx.put(bookmark.getName(), idxInt);
         return false;
      }
   }

   
   protected Bookmark get(String name)
   {
      Integer idxInt = bookmarkIdx.get(name);
      if (idxInt != null) {
         return bookmarks.get(idxInt.intValue());
      }
      return null;
   }

   
   protected void load() throws IOException
   {

      try
      {
         XMLBeanReader xmlin = new XMLBeanReader();

         if (bookmarkFile.exists())
         {
            xmlin.load(bookmarkFile, getClass().getClassLoader());
            for (Iterator<?> i = xmlin.iterator(); i.hasNext();)
            {
               Object bean = i.next();
               if (bean instanceof Bookmark)
               {
                  add((Bookmark) bean);
               }
            }
         }
      }
      catch (XMLException e)
      {
         throw new RuntimeException(e);
      }
   }

   
   protected void save()
   {
      try
      {
         XMLBeanWriter xmlout = new XMLBeanWriter();

         for (Iterator<Bookmark> i = bookmarks.iterator(); i.hasNext();)
         {
            Bookmark bookmark = i.next();

            xmlout.addToRoot(bookmark);
         }

         xmlout.save(bookmarkFile);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   protected Iterator<Bookmark> iterator()
   {
      return bookmarks.iterator();
   }

   public CompletionCandidates getCompletionCandidates(String bookmarkNameBegin)
   {
      Vector<BookmarkCompletionInfo> ret = new Vector<BookmarkCompletionInfo>();

      int maxNameLen = 0;
      for (int i = 0; i < bookmarks.size(); i++)
      {
         Bookmark bookmark = bookmarks.get(i);
         if (bookmark.getName().startsWith(bookmarkNameBegin))
         {
            ret.add(new BookmarkCompletionInfo(bookmark));
            maxNameLen = Math.max(maxNameLen, bookmark.getName().length());
         }
      }

      String defaultMarksInPopup =
         _plugin.getBookmarkProperties().getProperty(SQLBookmarkPlugin.BOOKMARK_PROP_DEFAULT_MARKS_IN_POPUP, "" + false);

      if(Boolean.valueOf(defaultMarksInPopup).booleanValue())
      {
         Bookmark[] defaultBookmarks = DefaultBookmarksFactory.getDefaultBookmarks();

         for (int i = 0; i < defaultBookmarks.length; i++)
         {
            if (defaultBookmarks[i].getName().startsWith(bookmarkNameBegin))
            {
               ret.add(new BookmarkCompletionInfo(defaultBookmarks[i]));
               maxNameLen = Math.max(maxNameLen, defaultBookmarks[i].getName().length());
            }
         }
      }





      BookmarkCompletionInfo[] candidates = ret.toArray(new BookmarkCompletionInfo[ret.size()]);

      for (int i = 0; i < candidates.length; i++)
      {
         candidates[i].setMaxCandidateNameLen(maxNameLen);
      }

      return new CompletionCandidates(candidates);
   }

   public void removeAll()
   {
      bookmarks = new ArrayList<Bookmark>();
      bookmarkIdx = new HashMap<String, Integer>();
   }
}
