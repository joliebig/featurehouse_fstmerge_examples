
package net.sourceforge.pmd.cpd;

import java.util.Properties;

public class LanguageFactory {

   public static String[] supportedLanguages = new String[]{"java", "jsp", "cpp", "c", "php", "ruby","fortran", "ecmascript" };
   private static final String SUFFIX = "Language";
   public static final String EXTENSION = "extension";
   public static final String BY_EXTENSION = "by_extension";
   private static final String PACKAGE = "net.sourceforge.pmd.cpd.";

    public Language createLanguage(String language) {
        return createLanguage(language, new Properties());
    }

   public Language createLanguage(String language, Properties properties)
   {
     language = this.languageAliases(language);
     
     Language implementation;
     try {
       implementation = this.dynamicLanguageImplementationLoad(this.languageConventionSyntax(language));
       if ( implementation == null )
       {
         
         implementation = this.dynamicLanguageImplementationLoad(language.toUpperCase());
         
         
         if ( implementation == null )
         {
           
           
           return new AnyLanguage(language);
         }
       }
       return implementation;
     } catch (InstantiationException e) {
       e.printStackTrace();
     } catch (IllegalAccessException e) {
       e.printStackTrace();
     }
     return null;
   }

     private String languageAliases(String language)
     {
       
       if ( "c".equals(language) ) {
         return "cpp";
       }
       return language;
     }

    private Language dynamicLanguageImplementationLoad(String language) throws InstantiationException, IllegalAccessException
    {
        try {
            return (Language) this.getClass().getClassLoader().loadClass(
                PACKAGE + language + SUFFIX).newInstance();
        } catch (ClassNotFoundException e) {
            
            
            return null;
        } catch (NoClassDefFoundError e) {
            
            
            
            
            return null;
        }
    }

   
   private String languageConventionSyntax(String language) {
       return Character.toUpperCase(language.charAt(0)) + language.substring(1, language.length()).toLowerCase();
    }
}
