package gnu.dtools.ritopt;





public class Utility {

    

    public static boolean contains( String check, String list ) {
	for ( int n = 0; n < list.length(); n++ ) {
	    if ( check.indexOf( list.substring( n, n + 1 ) ) != -1 )
		return true;
	}
	return false;
    }

    

    public static int count( String check, char spec ) {
        int sum = 0;
	for ( int n = 0; n < check.length(); n++ ) {
	    if ( check.charAt( 0 ) == spec ) sum++;
	}
	return sum;
    }

    

    public static boolean contains( char check, String list ) {
	return contains( "" + check, list );
    }

    

    public static boolean isAlpha( String check ) {
	boolean retval = false;
	for ( int n = 0; n < check.length(); n++ ) {
	    retval = isAlphaLower( check.charAt( n ) ) ||
		isAlphaUpper( check.charAt( n ) );
	}
	return retval;
    }

    

    public static boolean isAlphaLower( String check ) {
	boolean retval = false;
	for ( int n = 0; n < check.length(); n++ ) {
	    retval = isAlphaLower( check.charAt( n ) );
	}
	return retval;
    }

    

    public static boolean isAlphaUpper( String check ) {
	boolean retval = false;
	for ( int n = 0; n < check.length(); n++ ) {
	    retval = isAlphaUpper( check.charAt( n ) );
	}
	return retval;
    }

    

    public static boolean isAlpha( char check ) {
	return isAlphaLower( check ) || isAlphaUpper( check );
    }

    

    public static boolean isAlphaLower( char check ) {
	return check >= 'a' && check <= 'z';
    }

    

    public static boolean isAlphaUpper( char check ) {
	return check >= 'A' && check <= 'Z';
    }

    

    public static boolean isAlphaNumeric( char check ) {
	return isAlpha( check ) || isNumeric( check );
    }

    

    public static boolean isNumeric( char check ) {
	return check >= '0' && check <= '9'; 
    }

    

    public static String expandString( String s, int length ) {
	if ( s.length() > length ) s = s.substring( 0, length );
	return s + getSpaces( length - s.length() );
    }

    

    public static String getSpaces( int count ) {
	return repeat( ' ', count );
    }

    

    public static String repeat( char c, int count ) {
	StringBuffer retval = new StringBuffer( count );
	for ( int n = 0; n < count; n++ ) {
	    retval.append( c );
	}
	return retval.toString();
    }

    

    public static String ltrim( String s ) {
	StringBuffer buf = new StringBuffer( s );
	for ( int n = 0; n < buf.length() && buf.charAt( n ) == ' '; ) {
	    buf.delete( 0, 1 );
	}
	return buf.toString();
    }

    

    public static String rtrim( String s ) {
	StringBuffer buf = new StringBuffer( s );
	for ( int k = buf.length() - 1; k >= 0 && buf.charAt( k ) == ' ';
	      k = buf.length() - 1 ) {
	    buf.delete( buf.length() - 1, buf.length() );
	}
	return buf.toString();
    }

    

    public static String trim( String s ) {
	return ltrim( rtrim( s ) );
    }

    

    public static String stripComments( String s, char delim, char comment ) {
	String retval = s;
	boolean q = false;
	for ( int n = 0; n < s.length(); n++ ) {
	    if ( s.charAt( n ) == delim ) {
		q = !q;
	    }
	    else if ( !q && s.charAt( n ) == comment ) {
		retval = s.substring( 0, n );
	    }
	}
	return retval;
    }

    

    public static String repeat( String s, int count ) {
	StringBuffer retval = new StringBuffer( s.length() * count );
	for ( int n = 0; n < count; n++ ) {
	    retval.append( s );
	}
	return retval.toString();
    }
}
