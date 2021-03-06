package net.sourceforge.squirrel_sql.plugins.hibernate.completion;

import net.sourceforge.squirrel_sql.fw.completion.util.CompletionParser;
import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;
import net.sourceforge.squirrel_sql.fw.completion.CompletionCandidates;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernateConnection;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.PropertyInfo;
import net.sourceforge.squirrel_sql.client.session.ISyntaxHighlightTokenMatcher;
import net.sourceforge.squirrel_sql.client.session.SQLTokenListener;

import java.util.ArrayList;
import java.util.HashMap;

public class HQLCompletionInfoCollection implements MappingInfoProvider
{
   private ArrayList<MappedClassInfo> _mappedClassInfos;
   private ArrayList<SimpleHQLCompletionInfo> _simpleInfos;

   
   private ArrayList<AliasInfo> _currentAliasInfos = new ArrayList<AliasInfo>();

   private HashMap<String, MappedClassInfo> _mappedClassInfoByClassName = new HashMap<String, MappedClassInfo>();
   private HashMap<String, MappedClassInfo> _mappedClassInfoBySimpleClassName = new HashMap<String, MappedClassInfo>();
   private HashMap<String, SimpleHQLCompletionInfo> _simpleInfosByName = new HashMap<String, SimpleHQLCompletionInfo>();
   private HashMap<String, String> _attributeNames = new HashMap<String, String>();
   private HqlSyntaxHighlightTokenMatcher _hqlSyntaxHighlightTokenMatcher = new HqlSyntaxHighlightTokenMatcher(this);

   private MappedClassInfo _lastFoundMappedClassInfo;

   public HQLCompletionInfoCollection(HibernateConnection con)
   {
      _mappedClassInfos = con.getMappedClassInfos();

      for (MappedClassInfo mappedClassInfo : _mappedClassInfos)
      {
         _mappedClassInfoByClassName.put(mappedClassInfo.getClassName(), mappedClassInfo);
         _mappedClassInfoBySimpleClassName.put(mappedClassInfo.getSimpleClassName(), mappedClassInfo);

         for (String  attrName : mappedClassInfo.getAttributeNames())
         {
            _attributeNames.put(attrName, attrName);
         }
      }


      _simpleInfos = new ArrayList<SimpleHQLCompletionInfo>();
      _simpleInfos.addAll(HQLKeywordInfo.createInfos());
      _simpleInfos.addAll(HQLFunctionInfo.createInfos());

      for (SimpleHQLCompletionInfo simpleInfo : _simpleInfos)
      {
         _simpleInfosByName.put(simpleInfo.getCompareString(), simpleInfo);
      }

      _hqlSyntaxHighlightTokenMatcher.addSQLTokenListener(new SQLTokenListener()
      {
         public void tableOrViewFound(String name)
         {
            onTableOrViewFound(name);
         }
      });


      for (MappedClassInfo mappedClassInfo : _mappedClassInfos)
      {
         mappedClassInfo.initAttributesWithClassInfo(this);
      }

   }

   public CompletionCandidates getInfosStartingWith(CompletionParser parser)
   {

      
      
      
      
      
      
      
      
      
      
      
      
      


      ArrayList<CompletionInfo> ciClasses = new ArrayList<CompletionInfo>();
      ArrayList<CompletionInfo> ciAttrs = new ArrayList<CompletionInfo>();


      for (AliasInfo aliasInfo : _currentAliasInfos)
      {
         if(aliasInfo.matches(parser))
         {
            ciClasses.add(aliasInfo);
         }
         ciAttrs.addAll(aliasInfo.getQualifiedMatchingAttributes(parser));

      }

      for (MappedClassInfo mappedClassInfo : _mappedClassInfos)
      {
         if(mappedClassInfo.matches(parser))
         {
            ciClasses.add(mappedClassInfo);
         }

         ciAttrs.addAll(mappedClassInfo.getQualifiedMatchingAttributes(parser));
      }



      ArrayList<CompletionInfo> ret = new ArrayList<CompletionInfo>();
      MappedClassInfo lastFoundBuf = _lastFoundMappedClassInfo;
      if(null != lastFoundBuf)
      {
         if(1 == parser.size())
         {
            ret.addAll(lastFoundBuf.getMatchingAttributes(parser));
         }
         else
         {

            for (PropertyInfo propertyInfo : lastFoundBuf.getAttributes())
            {
               if(propertyInfo.getHibernatePropertyInfo().getPropertyName().equals(parser.getToken(0)))
               {
                  MappedClassInfo mappedClassInfo = propertyInfo.getMappedClassInfo();
                  CompletionParser simpleAttrFakeParser = new CompletionParser(mappedClassInfo.getClassName() + "." + parser.getAllButFirst());
                  ArrayList<PropertyInfo> matchingAttributes = mappedClassInfo.getQualifiedMatchingAttributes(simpleAttrFakeParser);
                  ret.addAll(matchingAttributes);
               }
            }
         }
      }


      int replacementStart;
      String stringToReplace;
      if(0 < ciClasses.size())
      {
         
         
         ret.addAll(ciClasses);
         replacementStart = parser.getReplacementStart();
         stringToReplace = parser.getStringToReplace();
      }
      else
      {
         
         
         ret.addAll(ciAttrs);
         replacementStart = parser.getTextTillCarret().length() - parser.getLastToken().length();
         stringToReplace = parser.getLastToken();
      }



      for (SimpleHQLCompletionInfo simpleInfo : _simpleInfos)
      {
         if(simpleInfo.matches(parser))
         {
            ret.add(simpleInfo);
         }
      }

      return new CompletionCandidates(ret.toArray(new CompletionInfo[ret.size()]), replacementStart, stringToReplace);


   }

   private void onTableOrViewFound(String name)
   {
      MappedClassInfo mappedClassInfo = _mappedClassInfoBySimpleClassName.get(name);

      if(null != mappedClassInfo)
      {
         _lastFoundMappedClassInfo = mappedClassInfo;
      }
      else
      {
         _lastFoundMappedClassInfo = _mappedClassInfoByClassName.get(name);
      }
   }


   public void setCurrentAliasInfos(ArrayList<AliasInfo> aliasInfos)
   {
      _currentAliasInfos = aliasInfos;
   }

   public MappedClassInfo getMappedClassInfoFor(String token)
   {
      
      
      

      CompletionParser cp = new CompletionParser(token);

      if(2 > cp.size())
      {
         return getMappedClassInfoForNonAliasedToken(cp);
      }

      String aliasCandidate = cp.getToken(0);

      
      

      ArrayList<AliasInfo> buf = _currentAliasInfos;

      for (AliasInfo currentAliasInfo : buf)
      {
         if(currentAliasInfo.getCompareString().equals(aliasCandidate))
         {
            ArrayList<PropertyInfo> matchingAttributes = currentAliasInfo.getQualifiedMatchingAttributes(cp);
            for (PropertyInfo matchingAttribute : matchingAttributes)
            {
               if(matchingAttribute.getHibernatePropertyInfo().getPropertyName().equals(cp.getLastToken()))
               {
                  return matchingAttribute.getMappedClassInfo();
               }
            }
         }
      }

      return getMappedClassInfoForNonAliasedToken(cp);
   }

   private MappedClassInfo getMappedClassInfoForNonAliasedToken(CompletionParser cp)
   {
      for (MappedClassInfo mappedClassInfo : _mappedClassInfos)
      {
         if(mappedClassInfo.matches(cp))
         {
            return mappedClassInfo;
         }

         if(
              cp.getStringToParse().startsWith(mappedClassInfo.getClassName()) ||
              cp.getStringToParse().startsWith(mappedClassInfo.getSimpleClassName())
           )
         {
            ArrayList<PropertyInfo> matchingAttributes = mappedClassInfo.getQualifiedMatchingAttributes(cp);
            for (PropertyInfo matchingAttribute : matchingAttributes)
            {
               if(matchingAttribute.getHibernatePropertyInfo().getPropertyName().equals(cp.getLastToken()))
               {
                  return matchingAttribute.getMappedClassInfo();
               }
            }
         }
      }

      return null;
   }

   public boolean mayBeClassOrAliasName(String token)
   {
      if(0 == token.length())
      {
         return false;
      }

      if(false == Character.isJavaIdentifierStart(token.charAt(0)))
      {
         return false;
      }

      if(_simpleInfosByName.containsKey(token))
      {
         return false;
      }

      for (int i = 1; i < token.length(); i++)
      {
         char c = token.charAt(i);
         if(false == Character.isJavaIdentifierPart(c) && '.' != c)
         {
            return false;
         }
      }

      return true;


   }

   public ISyntaxHighlightTokenMatcher getHqlSyntaxHighlightTokenMatcher()
   {
      return _hqlSyntaxHighlightTokenMatcher;
   }

   public boolean isMappeadClass(String name)
   {
      return _mappedClassInfoByClassName.containsKey(name) || _mappedClassInfoBySimpleClassName.containsKey(name);
   }

   public boolean isFunction(String name)
   {
      SimpleHQLCompletionInfo completionInfo = _simpleInfosByName.get(name);
      return null != completionInfo && completionInfo instanceof HQLFunctionInfo;
   }

   public boolean isKeyword(String name)
   {
      SimpleHQLCompletionInfo completionInfo = _simpleInfosByName.get(name);
      return null != completionInfo && completionInfo instanceof HQLKeywordInfo;
   }

   public boolean isMappedAttribute(String name)
   {
      return _attributeNames.containsKey(name);
   }
}
