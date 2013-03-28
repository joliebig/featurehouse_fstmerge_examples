




















package wsi.ra.tool;



import java.util.StringTokenizer;
import java.util.Vector;





public class WSITools
{
    

    private WSITools()
    {
    }

    

    
    public static boolean tokenize(Vector vcr, String buf)
    {
        return tokenize(vcr, buf, " \t\n");
    }

    
    public static boolean tokenize(Vector vcr, String buf, String delimstr)
    {
        vcr.clear();
        buf = buf + "\n";

        StringTokenizer st = new StringTokenizer(buf, delimstr);

        while (st.hasMoreTokens())
        {
            vcr.add(st.nextToken());
        }

        return true;
    }

    
    public static boolean tokenize(Vector vcr, String s, String delimstr,
        int limit)
    {
        System.out.println("Warning: tokenize \"" + s + "\"");
        vcr.clear();
        s = s + "\n";

        int endpos = 0;
        int matched = 0;

        StringTokenizer st = new StringTokenizer(s, delimstr);

        while (st.hasMoreTokens())
        {
            String tmp = st.nextToken();
            vcr.add(tmp);

            matched++;

            if (matched == limit)
            {
                endpos = s.lastIndexOf(tmp);
                vcr.add(s.substring(endpos + tmp.length()));

                break;
            }
        }

        return true;
    }
}



