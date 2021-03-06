




















package net.sf.jabref.export.layout.format;




public class CreateDocBookEditors extends CreateDocBookAuthors
{
    

    public String format(String fieldText)
    {
        

        int index = 0;
        int oldPos = 0;
        String author;
        StringBuffer sb = new StringBuffer(100);
        

        if (fieldText.indexOf(" and ") == -1)
        {
          sb.append("<editor>");
          singleAuthor(sb, fieldText);
          sb.append("</editor>");
        }
        else
        {
            String[] names = fieldText.split(" and ");
            for (int i=0; i<names.length; i++)
            {
              sb.append("<editor>");
              singleAuthor(sb, names[i]);
              sb.append("</editor>");
              if (i < names.length -1)
                sb.append("\n       ");
            }
        }

        fieldText = sb.toString();

        return fieldText;
    }

}



