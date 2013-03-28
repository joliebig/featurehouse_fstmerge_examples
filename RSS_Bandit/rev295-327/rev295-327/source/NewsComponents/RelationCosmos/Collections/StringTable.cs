using System; 
using System.Runtime.InteropServices; 
using System.Xml; namespace  NewsComponents.Collections {
	
 [ComVisible(false)] 
 public class  StringTable  : XmlNameTable {
		class  Entry {
			
   internal  string str;
 
   internal  int hashCode;
 
   internal  Entry next;
 
   internal  Entry( string str, int hashCode, Entry next ) {
    this.str = str;
    this.hashCode = hashCode;
    this.next = next;
   }

		}
		
  Entry[] entries;
 
  int count;
 
  int mask;
 
  int hashCodeRandomizer;
 
  public  StringTable() {
   entries = new Entry[32];
   hashCodeRandomizer = Environment.TickCount;
  }
 
  public override  string Add( string key ) {
   if ( key == null ) {
    throw new ArgumentNullException( "key" );
   }
   int len = key.Length;
   if ( len == 0 ) {
    return string.Empty;
   }
   int hashCode = len + hashCodeRandomizer;
   for ( int i = 0; i < key.Length; i++ ) {
    hashCode += ( hashCode << 7 ) ^ key[i];
   }
   hashCode -= hashCode >> 17;
   hashCode -= hashCode >> 11;
   hashCode -= hashCode >> 5;
   Entry[] entries = this.entries;
   for ( Entry e = entries[hashCode & (entries.Length-1)];
    e != null;
    e = e.next ) {
    if ( e.hashCode == hashCode && e.str.Equals( key ) ) {
     return e.str;
    }
   }
   return AddEntry( key, hashCode );
  }
 
  public override  string Add( char[] key, int start, int len ) {
   if ( len == 0 ) {
    return string.Empty;
   }
   int hashCode = len + hashCodeRandomizer;
   hashCode += ( hashCode << 7 ) ^ key[start];
   int end = start+len;
   for ( int i = start + 1; i < end; i++) {
    hashCode += ( hashCode << 7 ) ^ key[i];
   }
   hashCode -= hashCode >> 17;
   hashCode -= hashCode >> 11;
   hashCode -= hashCode >> 5;
   Entry[] entries = this.entries;
   for ( Entry e = entries[hashCode & (entries.Length-1)];
    e != null;
    e = e.next ) {
    if ( e.hashCode == hashCode && TextEquals( e.str, key, start ) ) {
     return e.str;
    }
   }
   return AddEntry( new string( key, start, len ), hashCode );
  }
 
  public override  string Get( string value ) {
   if ( value == null ) {
    throw new ArgumentNullException("value");
   }
   if ( value.Length == 0 ) {
    return string.Empty;
   }
   int len = value.Length + hashCodeRandomizer;
   int hashCode = len;
   for ( int i = 0; i < value.Length; i++ ) {
    hashCode += ( hashCode << 7 ) ^ value[i];
   }
   hashCode -= hashCode >> 17;
   hashCode -= hashCode >> 11;
   hashCode -= hashCode >> 5;
   Entry[] entries = this.entries;
   for ( Entry e = entries[hashCode & (entries.Length-1)];
    e != null;
    e = e.next ) {
    if ( e.hashCode == hashCode && e.str.Equals( value ) ) {
     return e.str;
    }
   }
   return null;
  }
 
  public override  string Get( char[] key, int start, int len ) {
   if ( len == 0 ) {
    return string.Empty;
   }
   int hashCode = len + hashCodeRandomizer;
   hashCode += ( hashCode << 7 ) ^ key[start];
   int end = start+len;
   for ( int i = start + 1; i < end; i++) {
    hashCode += ( hashCode << 7 ) ^ key[i];
   }
   hashCode -= hashCode >> 17;
   hashCode -= hashCode >> 11;
   hashCode -= hashCode >> 5;
   Entry[] entries = this.entries;
   for ( Entry e = entries[hashCode & (entries.Length-1)];
    e != null;
    e = e.next ) {
    if ( e.hashCode == hashCode && TextEquals( e.str, key, start ) ) {
     return e.str;
    }
   }
   return null;
  }
 
  private  string AddEntry( string str, int hashCode ) {
   Entry e;
   lock (this) {
    Entry[] entries = this.entries;
    int index = hashCode & entries.Length-1;
    for ( e = entries[index]; e != null; e = e.next ) {
     if ( e.hashCode == hashCode && e.str.Equals( str ) ) {
      return e.str;
     }
    }
    e = new Entry( str, hashCode, entries[index] );
    entries[index] = e;
    if ( count++ == mask ) {
     Grow();
    }
   }
   return e.str;
  }
 
  private  void Grow() {
   int newMask = mask * 2 + 1;
   Entry[] oldEntries = entries;
   Entry[] newEntries = new Entry[newMask+1];
   for ( int i = 0; i < oldEntries.Length; i++ ) {
    Entry e = oldEntries[i];
    while ( e != null ) {
     int newIndex = e.hashCode & newMask;
     Entry tmp = e.next;
     e.next = newEntries[newIndex];
     newEntries[newIndex] = e;
     e = tmp;
    }
   }
   entries = newEntries;
   mask = newMask;
  }
 
  private static  bool TextEquals( string array, char[] text, int start ) {
   for ( int i = 0; i < array.Length; i++ ) {
    if ( array[i] != text[start+i] ) {
     return false;
    }
   }
   return true;
  }

	}

}
