

package net.sourceforge.squirrel_sql.fw.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectStreamClass;
import java.io.ObjectStreamField;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import net.sourceforge.squirrel_sql.BaseSQuirreLTestCase;


public class FileTest extends BaseSQuirreLTestCase {

    
    private FileWrapper tempDirectory;

    
    private FileWrapper tempFile;

    
    private String slash = File.separator;

    public String fileString = "Test_All_Tests\nTest_java_io_BufferedInputStream\nTest_java_io_BufferedOutputStream\nTest_java_io_ByteArrayInputStream\nTest_java_io_ByteArrayOutputStream\nTest_java_io_DataInputStream\nTest_File\nTest_FileDescriptor\nTest_FileInputStream\nTest_FileNotFoundException\nTest_FileOutputStream\nTest_java_io_FilterInputStream\nTest_java_io_FilterOutputStream\nTest_java_io_InputStream\nTest_java_io_IOException\nTest_java_io_OutputStream\nTest_java_io_PrintStream\nTest_java_io_RandomAccessFile\nTest_java_io_SyncFailedException\nTest_java_lang_AbstractMethodError\nTest_java_lang_ArithmeticException\nTest_java_lang_ArrayIndexOutOfBoundsException\nTest_java_lang_ArrayStoreException\nTest_java_lang_Boolean\nTest_java_lang_Byte\nTest_java_lang_Character\nTest_java_lang_Class\nTest_java_lang_ClassCastException\nTest_java_lang_ClassCircularityError\nTest_java_lang_ClassFormatError\nTest_java_lang_ClassLoader\nTest_java_lang_ClassNotFoundException\nTest_java_lang_CloneNotSupportedException\nTest_java_lang_Double\nTest_java_lang_Error\nTest_java_lang_Exception\nTest_java_lang_ExceptionInInitializerError\nTest_java_lang_Float\nTest_java_lang_IllegalAccessError\nTest_java_lang_IllegalAccessException\nTest_java_lang_IllegalArgumentException\nTest_java_lang_IllegalMonitorStateException\nTest_java_lang_IllegalThreadStateException\nTest_java_lang_IncompatibleClassChangeError\nTest_java_lang_IndexOutOfBoundsException\nTest_java_lang_InstantiationError\nTest_java_lang_InstantiationException\nTest_java_lang_Integer\nTest_java_lang_InternalError\nTest_java_lang_InterruptedException\nTest_java_lang_LinkageError\nTest_java_lang_Long\nTest_java_lang_Math\nTest_java_lang_NegativeArraySizeException\nTest_java_lang_NoClassDefFoundError\nTest_java_lang_NoSuchFieldError\nTest_java_lang_NoSuchMethodError\nTest_java_lang_NullPointerException\nTest_java_lang_Number\nTest_java_lang_NumberFormatException\nTest_java_lang_Object\nTest_java_lang_OutOfMemoryError\nTest_java_lang_RuntimeException\nTest_java_lang_SecurityManager\nTest_java_lang_Short\nTest_java_lang_StackOverflowError\nTest_java_lang_String\nTest_java_lang_StringBuffer\nTest_java_lang_StringIndexOutOfBoundsException\nTest_java_lang_System\nTest_java_lang_Thread\nTest_java_lang_ThreadDeath\nTest_java_lang_ThreadGroup\nTest_java_lang_Throwable\nTest_java_lang_UnknownError\nTest_java_lang_UnsatisfiedLinkError\nTest_java_lang_VerifyError\nTest_java_lang_VirtualMachineError\nTest_java_lang_vm_Image\nTest_java_lang_vm_MemorySegment\nTest_java_lang_vm_ROMStoreException\nTest_java_lang_vm_VM\nTest_java_lang_Void\nTest_java_net_BindException\nTest_java_net_ConnectException\nTest_java_net_DatagramPacket\nTest_java_net_DatagramSocket\nTest_java_net_DatagramSocketImpl\nTest_java_net_InetAddress\nTest_java_net_NoRouteToHostException\nTest_java_net_PlainDatagramSocketImpl\nTest_java_net_PlainSocketImpl\nTest_java_net_Socket\nTest_java_net_SocketException\nTest_java_net_SocketImpl\nTest_java_net_SocketInputStream\nTest_java_net_SocketOutputStream\nTest_java_net_UnknownHostException\nTest_java_util_ArrayEnumerator\nTest_java_util_Date\nTest_java_util_EventObject\nTest_java_util_HashEnumerator\nTest_java_util_Hashtable\nTest_java_util_Properties\nTest_java_util_ResourceBundle\nTest_java_util_tm\nTest_java_util_Vector\n";

    private static String platformId = "JDK"
            + System.getProperty("java.vm.version").replace('.', '-');

    {
        
        FileWrapper tempDir = new FileWrapperImpl(System.getProperty("java.io.tmpdir"));
        String[] files = tempDir.list();
        for (int i = 0; i < files.length; i++) {
            FileWrapper f = new FileWrapperImpl(tempDir, files[i]);
            if (f.isDirectory()) {
                if (files[i].startsWith("hyts_resources")) {
                    deleteTempFolder(f);
                }
            }
            if (files[i].startsWith("hyts_") || files[i].startsWith("hyjar_")) {
                new FileWrapperImpl(tempDir, files[i]).delete();
            }
        }
    }

    private void deleteTempFolder(FileWrapper dir) {
        String files[] = dir.list();
        for (int i = 0; i < files.length; i++) {
            FileWrapper f = new FileWrapperImpl(dir, files[i]);
            if (f.isDirectory()) {
                deleteTempFolder(f);
            } else {
                f.delete();
            }
        }
        dir.delete();
    }

    
    public void test_ConstructorLjava_io_FileLjava_lang_String() {
        String dirName = System.getProperty("user.dir");
        FileWrapper d = new FileWrapperImpl(dirName);
        FileWrapper f = new FileWrapperImpl(d, "input.tst");
        if (!dirName.regionMatches((dirName.length() - 1), slash, 0, 1)) {
            dirName += slash;
        }
        dirName += "input.tst";
        assertEquals("Test 1: Created Incorrect File ", dirName, f.getPath());

        String fileName = null;
        try {
            f = new FileWrapperImpl(d, fileName);
            fail("NullPointerException Not Thrown.");
        } catch (NullPointerException e) {
        }

        d = null;
        f = new FileWrapperImpl(d, "input.tst");
        assertEquals("Test 2: Created Incorrect File",
                     dirName, f.getAbsolutePath());

        
        FileWrapper s = null;
        f = new FileWrapperImpl("/abc");
        d = new FileWrapperImpl(s, "/abc");
        assertEquals("Test3: Created Incorrect File",
                     d.getAbsolutePath(), f.getAbsolutePath());

        
        FileWrapper path = new FileWrapperImpl("/dir/file");
        FileWrapper root = new FileWrapperImpl("/");
        FileWrapper file = new FileWrapperImpl(root, "/dir/file");
        assertEquals("Assert 1: wrong path result ", path.getPath(), file
                .getPath());
        if (File.separatorChar == '\\') {
            assertTrue("Assert 1.1: path not absolute ", new FileWrapperImpl("\\\\\\a\b")
                       .isAbsolute());
        } else {
            assertFalse("Assert 1.1: path absolute ", new FileWrapperImpl("\\\\\\a\b")
                       .isAbsolute());
        }

        
        dirName = System.getProperty("user.dir");
        fileName = "input.tst";

        
        d = new FileWrapperImpl(dirName);
        f = new FileWrapperImpl(d, fileName);
        if (!dirName
                .regionMatches((dirName.length() - 1), File.separator, 0, 1)) {
            dirName += File.separator;
        }
        dirName += fileName;
        assertEquals("Assert 2: Created incorrect file ",
                     dirName, f.getPath());

        
        try {
            f = new FileWrapperImpl(d, null);
            fail("Assert 3: NullPointerException not thrown.");
        } catch (NullPointerException e) {
            
        }

        f = new FileWrapperImpl((FileWrapper) null, fileName);
        assertTrue("Assert 4: Created incorrect file " + f.getPath(), f
                .getAbsolutePath().equals(dirName));

        
        FileWrapper f1 = new FileWrapperImpl("a");
        FileWrapper f2 = new FileWrapperImpl("a/");
        assertEquals("Assert 5: Trailing slash file name is incorrect", f1, f2);
    }

    
    public void test_ConstructorLjava_lang_String() {
        String fileName = null;
        try {
            new FileWrapperImpl(fileName);
            fail("NullPointerException Not Thrown.");
        } catch (NullPointerException e) {
            
        }

        fileName = System.getProperty("user.dir");
        if (!fileName.regionMatches((fileName.length() - 1), slash, 0, 1)) {
            fileName += slash;
        }
        fileName += "input.tst";

        FileWrapper f = new FileWrapperImpl(fileName);
        assertEquals("Created incorrect File", fileName, f.getPath());
    }

    
    public void test_ConstructorLjava_lang_StringLjava_lang_String() {
        String dirName = null;
        String fileName = "input.tst";
        FileWrapper f = new FileWrapperImpl(dirName, fileName);
        String userDir = System.getProperty("user.dir");
        if (!userDir.regionMatches((userDir.length() - 1), slash, 0, 1)) {
            userDir += slash;
        }
        userDir += "input.tst";
        assertEquals("Test 1: Created Incorrect File.",
                     userDir, f.getAbsolutePath());

        dirName = System.getProperty("user.dir");
        fileName = null;
        try {
            f = new FileWrapperImpl(dirName, fileName);
            fail("NullPointerException Not Thrown.");
        } catch (NullPointerException e) {
            
        }

        fileName = "input.tst";
        f = new FileWrapperImpl(dirName, fileName);
        assertEquals("Test 2: Created Incorrect File", userDir, f.getPath());

        
        String s = null;
        f = new FileWrapperImpl("/abc");
        FileWrapper d = new FileWrapperImpl(s, "/abc");
        assertEquals("Test3: Created Incorrect File", d.getAbsolutePath(), f
                .getAbsolutePath());
    }

    
    public void test_Constructor_String_String_112270() {
        FileWrapper ref1 = new FileWrapperImpl("/dir1/file1");

        FileWrapper file1 = new FileWrapperImpl("/", "/dir1/file1");
        assertEquals("wrong result 1", ref1.getPath(), file1.getPath());
        FileWrapper file2 = new FileWrapperImpl("/", "//dir1/file1");
        assertEquals("wrong result 2", ref1.getPath(), file2.getPath());

        if (File.separatorChar == '\\') {
            FileWrapper file3 = new FileWrapperImpl("\\", "\\dir1\\file1");
            assertEquals("wrong result 3", ref1.getPath(), file3.getPath());
            FileWrapper file4 = new FileWrapperImpl("\\", "\\\\dir1\\file1");
            assertEquals("wrong result 4", ref1.getPath(), file4.getPath());
        }

        FileWrapper ref2 = new FileWrapperImpl("/lib/content-types.properties");
        FileWrapper file5 = new FileWrapperImpl("/", "lib/content-types.properties");
        assertEquals("wrong result 5", ref2.getPath(), file5.getPath());
    }

    
    public void test_Constructor_File_String_112270() {
        FileWrapper ref1 = new FileWrapperImpl("/dir1/file1");

        FileWrapper root = new FileWrapperImpl("/");
        FileWrapper file1 = new FileWrapperImpl(root, "/dir1/file1");
        assertEquals("wrong result 1", ref1.getPath(), file1.getPath());
        FileWrapper file2 = new FileWrapperImpl(root, "//dir1/file1");
        assertEquals("wrong result 2", ref1.getPath(), file2.getPath());

        if (File.separatorChar == '\\') {
            FileWrapper file3 = new FileWrapperImpl(root, "\\dir1\\file1");
            assertEquals("wrong result 3", ref1.getPath(), file3.getPath());
            FileWrapper file4 = new FileWrapperImpl(root, "\\\\dir1\\file1");
            assertEquals("wrong result 4", ref1.getPath(), file4.getPath());
        }

        FileWrapper ref2 = new FileWrapperImpl("/lib/content-types.properties");
        FileWrapper file5 = new FileWrapperImpl(root, "lib/content-types.properties");
        assertEquals("wrong result 5", ref2.getPath(), file5.getPath());
    }

    
    public void test_ConstructorLjava_net_URI() throws URISyntaxException {
        URI uri = null;
        try {
            new FileWrapperImpl(uri);
            fail("NullPointerException Not Thrown.");
        } catch (NullPointerException e) {
            
        }

        
        String[] uris = new String[] { "mailto:user@domain.com", 
                
                "ftp:///path", 
                "//host/path/", 
                "file://host/path", 
                "file:///path?query", 
                "file:///path#fragment", 
                "file:///path?", "file:///path#" };

        for (int i = 0; i < uris.length; i++) {
            uri = new URI(uris[i]);
            try {
                new FileWrapperImpl(uri);
                fail("Expected IllegalArgumentException for new FileWrapperImpl(" + uri
                        + ")");
            } catch (IllegalArgumentException e) {
                
            }
        }

        
        FileWrapper f = new FileWrapperImpl(new URI("file:///pa%20th/another\u/pa%25th"));
        assertTrue("Created incorrect File " + f.getPath(), f.getPath().equals(
                slash + "pa th" + slash + "another\u" + slash + "pa%th"));
    }

    
    public void test_canRead() throws IOException {
        
        FileWrapper f = new FileWrapperImpl(System.getProperty("java.io.tmpdir"), platformId
                + "canRead.tst");
        try {
            FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
            fos.close();
            assertTrue("canRead returned false", f.canRead());
            f.delete();
        } finally {
            f.delete();
        }
    }

    
    public void test_canWrite() throws IOException {
        
        FileWrapper f = new FileWrapperImpl(System.getProperty("java.io.tmpdir"), platformId
                + "canWrite.tst");
        try {
            FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
            fos.close();
            assertTrue("canWrite returned false", f.canWrite());
        } finally {
            f.delete();
        }
    }

    
    public void test_compareToLjava_io_File() {
        FileWrapperImpl f1 = new FileWrapperImpl("thisFile.file");
        FileWrapperImpl f2 = new FileWrapperImpl("thisFile.file");
        FileWrapperImpl f3 = new FileWrapperImpl("thatFile.file");
        assertEquals("Equal files did not answer zero for compareTo", 0, f1
                .compareTo(f2));
        assertTrue("f3.compareTo(f1) did not result in value < 0", f3
                .compareTo(f1) < 0);
        assertTrue("f1.compareTo(f3) did not result in value > 0", f1
                .compareTo(f3) > 0);
    }

    
    public void test_createNewFile_EmptyString() {
        FileWrapper f = new FileWrapperImpl("");
        try {
            f.createNewFile();
            fail("should throw IOException");
        } catch (IOException e) {
            
        }
    }

    
    public void test_createNewFile() throws IOException {
        String base = System.getProperty("java.io.tmpdir");
        boolean dirExists = true;
        int numDir = 1;
        FileWrapper dir = new FileWrapperImpl(base, String.valueOf(numDir));
        
        while (dirExists) {
            
            
            if (dir.exists()) {
                numDir++;
                dir = new FileWrapperImpl(base, String.valueOf(numDir));
            } else {
                dirExists = false;
            }
        }

        
        
        try {
            
            FileWrapper f1 = new FileWrapperImpl(dir, "tempfile.tst");
            f1.createNewFile();
            fail("IOException not thrown");
        } catch (IOException e) {
            
        }

        dir.mkdir();

        FileWrapper f1 = new FileWrapperImpl(dir, "tempfile.tst");
        FileWrapper f2 = new FileWrapperImpl(dir, "tempfile.tst");
        f1.deleteOnExit();
        f2.deleteOnExit();
        dir.deleteOnExit();
        assertFalse("File Should Not Exist", f1.isFile());
        f1.createNewFile();
        assertTrue("File Should Exist.", f1.isFile());
        assertTrue("File Should Exist.", f2.isFile());
        String dirName = f1.getParent();
        if (!dirName.endsWith(slash)) {
            dirName += slash;
        }
        assertEquals("File Saved To Wrong Directory.",
                     dir.getPath() + slash, dirName);
        assertEquals("File Saved With Incorrect Name.", "tempfile.tst",
                     f1.getName());

        
        assertFalse("File Already Exists, createNewFile Should Return False.",
                f2.createNewFile());

        
        String sep = File.separator;
        f1 = new FileWrapperImpl(sep + "..");
        try {
            f1.createNewFile();
            fail("should throw IOE");
        } catch (IOException e) {
            
        }
        f1 = new FileWrapperImpl(sep + "a" + sep + ".." + sep + ".." + sep);
        try {
            f1.createNewFile();
            fail("should throw IOE");
        } catch (IOException e) {
            
        }

        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
    }

    
    public void test_createTempFileLjava_lang_StringLjava_lang_String()
            throws IOException {
        
        FileWrapper f1 = null;
        FileWrapper f2 = null;
        try {
            f1 = new FileWrapperImpl(File.createTempFile("hyts_abc", ".tmp"));
            f2 = new FileWrapperImpl(File.createTempFile("hyts_tf", null));
            String fileLocation = f1.getParent();
            if (!fileLocation.endsWith(slash)) {
                fileLocation += slash;
            }
            String tempDir = System.getProperty("java.io.tmpdir");
            if (!tempDir.endsWith(slash)) {
                tempDir += slash;
            }
            assertEquals(
                    "File did not save to the default temporary-file location.",
                    tempDir, fileLocation);

            
            FileWrapper currentFile;
            String fileName;
            
            for (int i = 0; i < 2; i++) {
                currentFile = i == 0 ? f1 : f2;
                fileName = currentFile.getPath();
                assertTrue("File Created With Incorrect Suffix.", fileName
                        .endsWith(".tmp"));
            }

            
            
            fileName = f1.getName();
            assertTrue("Test 1: File Created With Incorrect Prefix.", fileName
                    .startsWith("hyts_abc"));
            fileName = f2.getName();
            assertTrue("Test 2: File Created With Incorrect Prefix.", fileName
                    .startsWith("hyts_tf"));

            
            
            try {
                FileWrapper f3 = new FileWrapperImpl(File.createTempFile("ab", ".tst"));
                f3.delete();
                fail("IllegalArgumentException Not Thrown.");
            } catch (IllegalArgumentException e) {
                
            }
            try {
                FileWrapper f3 = new FileWrapperImpl(File.createTempFile("a", ".tst"));
                f3.delete();
                fail("IllegalArgumentException Not Thrown.");
            } catch (IllegalArgumentException e) {
                
            }
            try {
                FileWrapper f3 = new FileWrapperImpl(File.createTempFile("", ".tst"));
                f3.delete();
                fail("IllegalArgumentException Not Thrown.");
            } catch (IllegalArgumentException e) {
                
            }
        } finally {
            if (f1 != null) {
                f1.delete();
            }
            if (f2 != null) {
                f2.delete();
            }
        }
    }

    
    public void test_createTempFileLjava_lang_StringLjava_lang_StringLjava_io_File()
            throws IOException {
        FileWrapper f1 = null;
        FileWrapper f2 = null;
        String base = System.getProperty("java.io.tmpdir");
        try {
            
            
            f1 = new FileWrapperImpl(File.createTempFile("hyts_tf", null, null));
            FileWrapperImpl dir = new FileWrapperImpl(base);
            f2 = FileWrapperImpl.createTempFile("hyts_tf", ".tmp", dir);
            FileWrapper currentFile;
            String fileLocation;
            String fileName;
            for (int i = 0; i < 2; i++) {
                currentFile = i == 0 ? f1 : f2;
                fileLocation = currentFile.getParent();
                if (!fileLocation.endsWith(slash)) {
                    fileLocation += slash;
                }
                if (!base.endsWith(slash)) {
                    base += slash;
                }
                assertEquals(
                        "File not created in the default temporary-file location.",
                        base, fileLocation);
                fileName = currentFile.getName();
                assertTrue("File created with incorrect suffix.", fileName
                        .endsWith(".tmp"));
                assertTrue("File created with incorrect prefix.", fileName
                        .startsWith("hyts_tf"));
                currentFile.delete();
            }

            
            int dirNumber = 1;
            boolean dirExists = true;
            
            
            dir = new FileWrapperImpl(base, String.valueOf(dirNumber));
            
            while (dirExists) {
                
                
                
                if (dir.exists()) {
                    dirNumber++;
                    dir = new FileWrapperImpl(base, String.valueOf(dirNumber));
                } else {
                    dirExists = false;
                }
            }
            try {
                
                FileWrapper f3 = FileWrapperImpl.createTempFile("hyts_tf", null, dir);
                f3.delete();
                fail("IOException not thrown");
            } catch (IOException e) {
                
            }
            dir.delete();

            
            
            try {
                FileWrapper f4 = new FileWrapperImpl(File.createTempFile("ab", null, null));
                f4.delete();
                fail("IllegalArgumentException not thrown.");
            } catch (IllegalArgumentException e) {
                
            }
            try {
                FileWrapper f4 = FileWrapperImpl.createTempFile("a", null, null);
                f4.delete();
                fail("IllegalArgumentException not thrown.");
            } catch (IllegalArgumentException e) {
                
            }
            try {
                FileWrapper f4 = FileWrapperImpl.createTempFile("", null, null);
                f4.delete();
                fail("IllegalArgumentException not thrown.");
            } catch (IllegalArgumentException e) {
                
            }
        } finally {
            if (f1 != null) {
                f1.delete();
            }
            if (f2 != null) {
                f1.delete();
            }
        }
    }

    
    public void test_delete() throws IOException {
        FileWrapper dir = new FileWrapperImpl(System.getProperty("user.dir"), platformId
                + "filechk");
        dir.mkdir();
        assertTrue("Directory does not exist", dir.exists());
        assertTrue("Directory is not directory", dir.isDirectory());
        FileWrapper f = new FileWrapperImpl(dir, "filechk.tst");
        FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
        fos.close();
        assertTrue("Error Creating File For Delete Test", f.exists());
        dir.delete();
        assertTrue("Directory Should Not Have Been Deleted.", dir.exists());
        f.delete();
        assertTrue("File Was Not Deleted", !f.exists());
        dir.delete();
        assertTrue("Directory Was Not Deleted", !dir.exists());
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    
    public void test_equalsLjava_lang_Object() throws IOException {
        FileWrapper f1 = new FileWrapperImpl("filechk.tst");
        FileWrapper f2 = new FileWrapperImpl("filechk.tst");
        FileWrapper f3 = new FileWrapperImpl("xxxx");

        assertTrue("Equality test failed", f1.equals(f2));
        assertTrue("Files Should Not Return Equal.", !f1.equals(f3));

        f3 = new FileWrapperImpl("FiLeChK.tst");
        boolean onWindows = File.separatorChar == '\\';
        boolean onUnix = File.separatorChar == '/';
        if (onWindows) {
            assertTrue("Files Should Return Equal.", f1.equals(f3));
        } else if (onUnix) {
            assertTrue("Files Should NOT Return Equal.", !f1.equals(f3));
        }

        f1 = new FileWrapperImpl(System.getProperty("java.io.tmpdir"), "casetest.tmp");
        f2 = new FileWrapperImpl(System.getProperty("java.io.tmpdir"), "CaseTest.tmp");
        new FileOutputStream(f1.getAbsolutePath()).close(); 
        if (f1.equals(f2)) {
            try {
                FileInputStream fis = new FileInputStream(f2.getAbsolutePath());
                fis.close();
            } catch (IOException e) {
                fail("File system is case sensitive");
            }
        } else {
            boolean exception = false;
            try {
                FileInputStream fis = new FileInputStream(f2.getAbsolutePath());
                fis.close();
            } catch (IOException e) {
                exception = true;
            }
            assertTrue("File system is case insensitive", exception);
        }
        f1.delete();
    }

    
    public void test_exists() throws IOException {
        FileWrapper f = new FileWrapperImpl(System.getProperty("user.dir"), platformId
                + "exists.tst");
        assertTrue("Exists returned true for non-existent file", !f.exists());
        FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
        fos.close();
        assertTrue("Exists returned false file", f.exists());
        f.delete();
    }

    
    public void test_getAbsoluteFile() {
        String base = System.getProperty("user.dir");
        if (!base.endsWith(slash)) {
            base += slash;
        }
        FileWrapperImpl f = new FileWrapperImpl(base, "temp.tst");
        FileWrapperImpl f2 = (FileWrapperImpl)f.getAbsoluteFile();
        assertEquals("Test 1: Incorrect File Returned.", 0, f2.compareTo((FileWrapperImpl)f
                .getAbsoluteFile()));
        f = new FileWrapperImpl(base + "Temp" + slash + slash + "temp.tst");
        f2 = (FileWrapperImpl)f.getAbsoluteFile();
        assertEquals("Test 2: Incorrect File Returned.", 0, f2.compareTo((FileWrapperImpl)f
                .getAbsoluteFile()));
        f = new FileWrapperImpl(base + slash + ".." + slash + "temp.tst");
        f2 = (FileWrapperImpl)f.getAbsoluteFile();
        assertEquals("Test 3: Incorrect File Returned.", 0, f2.compareTo((FileWrapperImpl)f
                .getAbsoluteFile()));
        f.delete();
        f2.delete();
    }

    
    public void test_getAbsolutePath() {
        String base = System.getProperty("user.dir");
        if (!base.regionMatches((base.length() - 1), slash, 0, 1)) {
            base += slash;
        }
        FileWrapper f = new FileWrapperImpl(base, "temp.tst");
        assertEquals("Test 1: Incorrect Path Returned.",
                     base + "temp.tst", f.getAbsolutePath());

        f = new FileWrapperImpl(base + "Temp" + slash + slash + slash + "Testing" + slash
                + "temp.tst");
        assertEquals("Test 2: Incorrect Path Returned.",
		     base + "Temp" + slash + "Testing" + slash + "temp.tst",
                     f.getAbsolutePath());

        f = new FileWrapperImpl(base + "a" + slash + slash + ".." + slash + "temp.tst");
        assertEquals("Test 3: Incorrect Path Returned.",
                     base + "a" + slash + ".." + slash + "temp.tst",
                     f.getAbsolutePath());
        f.delete();
    }

    
    public void test_getCanonicalFile() throws IOException {
        String base = System.getProperty("user.dir");
        if (!base.endsWith(slash)) {
            base += slash;
        }
        FileWrapperImpl f = new FileWrapperImpl(base, "temp.tst");
        FileWrapperImpl f2 = (FileWrapperImpl)f.getCanonicalFile();
        assertEquals("Test 1: Incorrect File Returned.", 0, ((FileWrapperImpl)f2
                .getCanonicalFile()).compareTo((FileWrapperImpl)f.getCanonicalFile()));
        f = new FileWrapperImpl(base + "Temp" + slash + slash + "temp.tst");
        f2 = (FileWrapperImpl)f.getCanonicalFile();
        assertEquals("Test 2: Incorrect File Returned.", 0, ((FileWrapperImpl)f2
                .getCanonicalFile()).compareTo((FileWrapperImpl)f.getCanonicalFile()));
        f = new FileWrapperImpl(base + "Temp" + slash + slash + ".." + slash + "temp.tst");
        f2 = (FileWrapperImpl)f.getCanonicalFile();
        assertEquals("Test 3: Incorrect File Returned.", 0, ((FileWrapperImpl)f2
                .getCanonicalFile()).compareTo((FileWrapperImpl)f.getCanonicalFile()));

        
        boolean onWindows = File.separatorChar == '\\';
        if (onWindows) {
            FileWrapper testdir = new FileWrapperImpl(base, "long-" + platformId);
            testdir.mkdir();
            FileWrapper dir = new FileWrapperImpl(testdir, "longdirectory" + platformId);
            try {
                dir.mkdir();
                f = new FileWrapperImpl(dir, "longfilename.tst");
                f2 = (FileWrapperImpl)f.getCanonicalFile();
                assertEquals("Test 4: Incorrect File Returned.", 0, ((FileWrapperImpl)f2
                        .getCanonicalFile()).compareTo((FileWrapperImpl)f.getCanonicalFile()));
                FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
                fos.close();
                f2 = new FileWrapperImpl(testdir + slash + "longdi~1" + slash
                        + "longfi~1.tst");
                FileWrapperImpl canonicalf2 = (FileWrapperImpl)f2.getCanonicalFile();
                
                if (canonicalf2.exists()) {
                    assertTrue("Test 5: Incorrect File Returned: "
                            + canonicalf2, canonicalf2.compareTo(((FileWrapperImpl)f
                            .getCanonicalFile())) == 0);
                }
            } finally {
                f.delete();
                f2.delete();
                dir.delete();
                testdir.delete();
            }
        }
    }

    
    public void test_getCanonicalPath() throws IOException {
        
        String dots = "..";
        String base = new FileWrapperImpl(System.getProperty("user.dir"))
                .getCanonicalPath();
        if (!base.regionMatches((base.length() - 1), slash, 0, 1)) {
            base += slash;
        }
        FileWrapper f = new FileWrapperImpl(base, "temp.tst");
        assertEquals("Test 1: Incorrect Path Returned.", base + "temp.tst", f
                .getCanonicalPath());
        f = new FileWrapperImpl(base + "Temp" + slash + dots + slash + "temp.tst");
        assertEquals("Test 2: Incorrect Path Returned.", base + "temp.tst", f
                .getCanonicalPath());

        
        
        
        
        int dirNumber = 1;
        boolean dirExists = true;
        FileWrapper dir1 = new FileWrapperImpl(base, String.valueOf(dirNumber));
        while (dirExists) {
            if (dir1.exists()) {
                dirNumber++;
                dir1 = new FileWrapperImpl(base, String.valueOf(dirNumber));
            } else {
                dirExists = false;
            }
        }
        f = new FileWrapperImpl(base + dirNumber + slash + dots + slash + dirNumber
                + slash + "temp.tst");
        assertEquals("Test 3: Incorrect Path Returned.", base + dirNumber
                + slash + "temp.tst", f.getCanonicalPath());
        f = new FileWrapperImpl(base + dirNumber + slash + "Temp" + slash + dots + slash
                + "Test" + slash + "temp.tst");
        assertEquals("Test 4: Incorrect Path Returned.", base + dirNumber
                + slash + "Test" + slash + "temp.tst", f.getCanonicalPath());

        f = new FileWrapperImpl("1234.567");
        assertEquals("Test 5: Incorrect Path Returned.", base + "1234.567", f
                .getCanonicalPath());

        
        boolean onWindows = (File.separatorChar == '\\');
        if (onWindows) {
            FileWrapper testdir = new FileWrapperImpl(base, "long-" + platformId);
            testdir.mkdir();
            FileWrapper f1 = new FileWrapperImpl(testdir, "longfilename" + platformId + ".tst");
            FileOutputStream fos = new FileOutputStream(f1.getAbsolutePath());
            FileWrapper f2 = null, f3 = null, dir2 = null;
            try {
                fos.close();
                String dirName1 = f1.getCanonicalPath();
                FileWrapper f4 = new FileWrapperImpl(testdir, "longfi~1.tst");
                
                if (f4.exists()) {
                    String dirName2 = f4.getCanonicalPath();
                    assertEquals("Test 6: Incorrect Path Returned.", dirName1,
                            dirName2);
                    dir2 = new FileWrapperImpl(testdir, "longdirectory" + platformId);
                    if (!dir2.exists()) {
                        assertTrue("Could not create dir: " + dir2, dir2
                                .mkdir());
                    }
                    f2 = new FileWrapperImpl(testdir.getPath() + slash + "longdirectory"
                            + platformId + slash + "Test" + slash + dots
                            + slash + "longfilename.tst");
                    FileOutputStream fos2 = new FileOutputStream(f2.getAbsolutePath());
                    fos2.close();
                    dirName1 = f2.getCanonicalPath();
                    f3 = new FileWrapperImpl(testdir.getPath() + slash + "longdi~1"
                            + slash + "Test" + slash + dots + slash
                            + "longfi~1.tst");
                    dirName2 = f3.getCanonicalPath();
                    assertEquals("Test 7: Incorrect Path Returned.", dirName1,
                            dirName2);
                }
            } finally {
                f1.delete();
                if (f2 != null) {
                    f2.delete();
                }
                if (dir2 != null) {
                    dir2.delete();
                }
                testdir.delete();
            }
        }
    }

    
    public void test_getName() {
        FileWrapper f = new FileWrapperImpl("name.tst");
        assertEquals("Test 1: Returned incorrect name", "name.tst", f.getName());

        f = new FileWrapperImpl("");
        assertEquals("Test 2: Returned incorrect name", "", f.getName());

        f.delete();
    }

    
    public void test_getParent() {
        FileWrapper f = new FileWrapperImpl("p.tst");
        assertNull("Incorrect path returned", f.getParent());
        f = new FileWrapperImpl(System.getProperty("user.home"), "p.tst");
        assertEquals("Incorrect path returned",
                     System.getProperty("user.home"), f.getParent());
        f.delete();

        FileWrapper f1 = new FileWrapperImpl("/directory");
        assertEquals("Wrong parent test 1", slash, f1.getParent());
        f1 = new FileWrapperImpl("/directory/file");
        assertEquals("Wrong parent test 2",
                     slash + "directory", f1.getParent());
        f1 = new FileWrapperImpl("directory/file");
        assertEquals("Wrong parent test 3", "directory", f1.getParent());
        f1 = new FileWrapperImpl("/");
        assertNull("Wrong parent test 4", f1.getParent());
        f1 = new FileWrapperImpl("directory");
        assertNull("Wrong parent test 5", f1.getParent());

        if (File.separatorChar == '\\' && new FileWrapperImpl("d:/").isAbsolute()) {
            f1 = new FileWrapperImpl("d:/directory");
            assertEquals("Wrong parent test 1a", "d:" + slash, f1.getParent());
            f1 = new FileWrapperImpl("d:/directory/file");
            assertEquals("Wrong parent test 2a",
                         "d:" + slash + "directory", f1.getParent());
            f1 = new FileWrapperImpl("d:directory/file");
            assertEquals("Wrong parent test 3a", "d:directory", f1.getParent());
            f1 = new FileWrapperImpl("d:/");
            assertNull("Wrong parent test 4a", f1.getParent());
            f1 = new FileWrapperImpl("d:directory");
            assertEquals("Wrong parent test 5a", "d:", f1.getParent());
        }
    }

    
    public void test_getParentFile() {
        FileWrapperImpl f = new FileWrapperImpl("tempfile.tst");
        assertNull("Incorrect path returned", f.getParentFile());
        f = new FileWrapperImpl(System.getProperty("user.dir"), "tempfile1.tmp");
        FileWrapper f2 = new FileWrapperImpl(System.getProperty("user.dir"), "tempfile2.tmp");
        FileWrapper f3 = new FileWrapperImpl(System.getProperty("user.dir"), "/a/tempfile.tmp");
        assertEquals("Incorrect File Returned", 0, ((FileWrapperImpl)f.getParentFile()).compareTo(
      	  (FileWrapperImpl)f2.getParentFile()));
        assertTrue("Incorrect File Returned", ((FileWrapperImpl)f.getParentFile()).compareTo(
      	  (FileWrapperImpl)f3.getParentFile()) != 0);
        f.delete();
        f2.delete();
        f3.delete();
    }

    
    public void test_getPath() {
        String base = System.getProperty("user.home");
        String fname;
        FileWrapperImpl f1;
        if (!base.regionMatches((base.length() - 1), slash, 0, 1)) {
            base += slash;
        }
        fname = base + "filechk.tst";
        f1 = new FileWrapperImpl(base, "filechk.tst");
        FileWrapperImpl f2 = new FileWrapperImpl("filechk.tst");
        FileWrapperImpl f3 = new FileWrapperImpl("c:");
        FileWrapperImpl f4 = new FileWrapperImpl(base + "a" + slash + slash + ".." + slash
                + "filechk.tst");
        assertEquals("getPath returned incorrect path(f1)",
                     fname, f1.getPath());
        assertEquals("getPath returned incorrect path(f2)",
                     "filechk.tst", f2.getPath());
        assertEquals("getPath returned incorrect path(f3)","c:", f3.getPath());
        assertEquals("getPath returned incorrect path(f4)",
                     base + "a" + slash + ".." + slash + "filechk.tst",
                     f4.getPath());
        f1.delete();
        f2.delete();
        f3.delete();
        f4.delete();

        
        FileWrapperImpl file;
        String separator = File.separator;

        file = new FileWrapperImpl((FileWrapper) null, "x/y/z");
        assertEquals("x" + separator + "y" + separator + "z", file.getPath());

        file = new FileWrapperImpl((String) null, "x/y/z");
        assertEquals("x" + separator + "y" + separator + "z", file.getPath());

        
        String f1ParentName = "01";
        f1 = new FileWrapperImpl(f1ParentName, "");
        assertEquals(f1ParentName, f1.getPath());

        String f2ParentName = "0";
        f2 = new FileWrapperImpl(f2ParentName, "");

        assertEquals(-1, f2.compareTo(f1));
        assertEquals(1, f1.compareTo(f2));

        FileWrapper parent = new FileWrapperImpl(System.getProperty("user.dir"));
        f3 = new FileWrapperImpl(parent, "");

        assertEquals(parent.getPath(), f3.getPath());

        
        FileWrapper file1 = new FileWrapperImpl("", "");
        assertEquals(File.separator, file1.getPath());

        FileWrapper file2 = new FileWrapperImpl(new FileWrapperImpl(""), "");
        assertEquals(File.separator, file2.getPath());
    }

    
    public void test_hashCode() {
        
        String mixedFname = "SoMe FiLeNaMe";
        FileWrapper mfile = new FileWrapperImpl(mixedFname);
        FileWrapper lfile = new FileWrapperImpl(mixedFname.toLowerCase());

        if (mfile.equals(lfile)) {
            assertTrue("Assert 0: wrong hashcode", mfile.hashCode() == lfile
                    .hashCode());
        } else {
            assertFalse("Assert 1: wrong hashcode", mfile.hashCode() == lfile
                    .hashCode());
        }
    }

    
    public void test_isAbsolute() {
        if (File.separatorChar == '\\') {
            FileWrapper f = new FileWrapperImpl("c:\\test");
            FileWrapper f1 = new FileWrapperImpl("\\test");
            
            assertTrue("Absolute returned false", (f.isAbsolute() && !f1
                    .isAbsolute())
                    || (!f.isAbsolute() && f1.isAbsolute()));
        } else {
            FileWrapper f = new FileWrapperImpl("/test");
            FileWrapper f1 = new FileWrapperImpl("\\test");
            assertTrue("Absolute returned false", f.isAbsolute());
            assertFalse("Absolute returned true", f1.isAbsolute());
        }
        assertTrue("Non-Absolute returned true", !new FileWrapperImpl("../test")
                .isAbsolute());
    }

    
    public void test_isDirectory() {
        String base = System.getProperty("user.dir");
        if (!base.regionMatches((base.length() - 1), slash, 0, 1)) {
            base += slash;
        }
        FileWrapper f = new FileWrapperImpl(base);
        assertTrue("Test 1: Directory Returned False", f.isDirectory());
        f = new FileWrapperImpl(base + "zxzxzxz" + platformId);
        assertTrue("Test 2: (Not Created) Directory Returned True.", !f
                .isDirectory());
        f.mkdir();
        try {
            assertTrue("Test 3: Directory Returned False.", f.isDirectory());
        } finally {
            f.delete();
        }
    }

    
    public void test_isFile() throws IOException {
        String base = System.getProperty("user.dir");
        FileWrapper f = new FileWrapperImpl(base);
        assertTrue("Directory Returned True As Being A File.", !f.isFile());
        if (!base.regionMatches((base.length() - 1), slash, 0, 1)) {
            base += slash;
        }
        f = new FileWrapperImpl(base, platformId + "amiafile");
        assertTrue("Non-existent File Returned True", !f.isFile());
        FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
        fos.close();
        assertTrue("File returned false", f.isFile());
        f.delete();
    }

    
    public void test_isHidden() throws IOException, InterruptedException {
        boolean onUnix = File.separatorChar == '/';
        FileWrapper f = FileWrapperImpl.createTempFile("hyts_", ".tmp");
        
        
        if (onUnix) {
            FileWrapper f2 = new FileWrapperImpl(".test.tst" + platformId);
            FileOutputStream fos2 = new FileOutputStream(f2.getAbsolutePath());
            fos2.close();
            assertTrue("File returned hidden on Unix", !f.isHidden());
            assertTrue("File returned visible on Unix", f2.isHidden());
            assertTrue("File did not delete.", f2.delete());
        } else {
            
            
            Runtime r = Runtime.getRuntime();
            assertTrue("File returned hidden", !f.isHidden());
            Process p = r.exec("attrib +h \"" + f.getAbsolutePath() + "\"");
            p.waitFor();
            assertTrue("File returned visible", f.isHidden());
            p = r.exec("attrib -h \"" + f.getAbsolutePath() + "\"");
            p.waitFor();
            assertTrue("File returned hidden", !f.isHidden());
        }
        f.delete();
    }

    
    public void test_lastModified() throws IOException {
        FileWrapper f = new FileWrapperImpl(System.getProperty("java.io.tmpdir"), platformId
                + "lModTest.tst");
        f.delete();
        long lastModifiedTime = f.lastModified();
        assertEquals("LastModified Time Should Have Returned 0.", 0,
                lastModifiedTime);
        FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
        fos.close();
        f.setLastModified(315550800000L);
        lastModifiedTime = f.lastModified();
        assertEquals("LastModified Time Incorrect",
                     315550800000L, lastModifiedTime);
        f.delete();

        
        f = new FileWrapperImpl("/../");
        assertTrue(f.lastModified() > 0);
    }

    
    public void test_length() throws IOException {
        FileWrapper f = new FileWrapperImpl(System.getProperty("user.dir"), platformId
                + "input.tst");
        assertEquals("File Length Should Have Returned 0.", 0, f.length());
        FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
        fos.write(fileString.getBytes());
        fos.close();
        assertEquals("Incorrect file length returned",
		     fileString.length(), f.length());
        f.delete();

        
        f = FileWrapperImpl.createTempFile("test", "tmp");
        f.deleteOnExit();
        RandomAccessFile raf = new RandomAccessFile(new File(f.getAbsolutePath()), "rwd");
        raf.write(0x41);
        assertEquals(1, f.length());
    }

    
    public void test_list() throws IOException {
        String base = System.getProperty("user.dir");
        
        
        
        int dirNumber = 1;
        boolean dirExists = true;
        FileWrapper dir = null;
        dir = new FileWrapperImpl(base, platformId + String.valueOf(dirNumber));
        while (dirExists) {
            if (dir.exists()) {
                dirNumber++;
                dir = new FileWrapperImpl(base, String.valueOf(dirNumber));
            } else {
                dirExists = false;
            }
        }

        String[] flist = dir.list();

        assertNull("Method list() Should Have Returned null.", flist);

        assertTrue("Could not create parent directory for list test", dir
                .mkdir());

        String[] files = { "mtzz1.xx", "mtzz2.xx", "mtzz3.yy", "mtzz4.yy" };
        try {
            assertEquals(
                    "Method list() Should Have Returned An Array Of Length 0.",
                    0, dir.list().length);

            FileWrapper file = new FileWrapperImpl(dir, "notADir.tst");
            try {
                FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
                fos.close();
                assertNull(
                        "listFiles Should Have Returned Null When Used On A File Instead Of A Directory.",
                        file.list());
            } finally {
                file.delete();
            }

            for (int i = 0; i < files.length; i++) {
                FileWrapper f = new FileWrapperImpl(dir, files[i]);
                FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
                fos.close();
            }

            flist = dir.list();
            if (flist.length != files.length) {
                fail("Incorrect list returned");
            }

            
            
            boolean[] check = new boolean[flist.length];
            for (int i = 0; i < check.length; i++) {
                check[i] = false;
            }
            for (int i = 0; i < files.length; i++) {
                for (int j = 0; j < flist.length; j++) {
                    if (flist[j].equals(files[i])) {
                        check[i] = true;
                        break;
                    }
                }
            }
            int checkCount = 0;
            for (int i = 0; i < check.length; i++) {
                if (check[i] == false) {
                    checkCount++;
                }
            }
            assertEquals("Invalid file returned in listing", 0, checkCount);

            for (int i = 0; i < files.length; i++) {
                FileWrapper f = new FileWrapperImpl(dir, files[i]);
                f.delete();
            }

            assertTrue("Could not delete parent directory for list test.", dir
                    .delete());
        } finally {
            for (int i = 0; i < files.length; i++) {
                FileWrapper f = new FileWrapperImpl(dir, files[i]);
                f.delete();
            }
            dir.delete();
        }
    }

    
    public void test_listFiles() throws IOException, InterruptedException {
        String base = System.getProperty("user.dir");
        
        int dirNumber = 1;
        boolean dirExists = true;
        FileWrapper dir = new FileWrapperImpl(base, platformId + String.valueOf(dirNumber));
        
        while (dirExists) {
            
            
            if (dir.exists()) {
                dirNumber++;
                dir = new FileWrapperImpl(base, String.valueOf(dirNumber));
            } else {
                dirExists = false;
            }
        }
        
        assertNull("listFiles Should Return Null.", dir.listFiles());

        assertTrue("Failed To Create Parent Directory.", dir.mkdir());

        String[] files = { "1.tst", "2.tst", "3.tst", "" };
        try {
            assertEquals("listFiles Should Return An Array Of Length 0.", 0,
                    dir.listFiles().length);

            FileWrapperImpl file = new FileWrapperImpl(dir, "notADir.tst");
            try {
                FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
                fos.close();
                assertNull(
                        "listFiles Should Have Returned Null When Used On A File Instead Of A Directory.",
                        file.listFiles());
            } finally {
                file.delete();
            }

            for (int i = 0; i < (files.length - 1); i++) {
                FileWrapper f = new FileWrapperImpl(dir, files[i]);
                FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
                fos.close();
            }

            new FileWrapperImpl(dir, "doesNotExist.tst");
            FileWrapper[] flist = dir.listFiles();

            
            
            assertEquals("Incorrect Number Of Files Returned.", 3, flist.length);

            
            boolean onUnix = File.separatorChar == '/';
            boolean onWindows = File.separatorChar == '\\';
            if (onWindows) {
                files[3] = "4.tst";
                FileWrapper f = new FileWrapperImpl(dir, "4.tst");
                FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
                fos.close();
                Runtime r = Runtime.getRuntime();
                Process p = r.exec("attrib +h \"" + f.getPath() + "\"");
                p.waitFor();
            }
            if (onUnix) {
                files[3] = ".4.tst";
                FileWrapper f = new FileWrapperImpl(dir, ".4.tst");
                FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
                fos.close();
            }
            flist = dir.listFiles();
            assertEquals("Incorrect Number Of Files Returned.", 4, flist.length);

            
            
            boolean[] check = new boolean[flist.length];
            for (int i = 0; i < check.length; i++) {
                check[i] = false;
            }
            for (int i = 0; i < files.length; i++) {
                for (int j = 0; j < flist.length; j++) {
                    if (flist[j].getName().equals(files[i])) {
                        check[i] = true;
                        break;
                    }
                }
            }
            int checkCount = 0;
            for (int i = 0; i < check.length; i++) {
                if (check[i] == false) {
                    checkCount++;
                }
            }
            assertEquals("Invalid file returned in listing", 0, checkCount);

            if (onWindows) {
                Runtime r = Runtime.getRuntime();
                Process p = r.exec("attrib -h \""
                        + new FileWrapperImpl(dir, files[3]).getPath() + "\"");
                p.waitFor();
            }

            for (int i = 0; i < files.length; i++) {
                FileWrapper f = new FileWrapperImpl(dir, files[i]);
                f.delete();
            }
            assertTrue("Parent Directory Not Deleted.", dir.delete());
        } finally {
            for (int i = 0; i < files.length; i++) {
                FileWrapper f = new FileWrapperImpl(dir, files[i]);
                f.delete();
            }
            dir.delete();
        }
    }

    
    public void test_listFilesLjava_io_FileFilter() throws IOException {
        String base = System.getProperty("java.io.tmpdir");
        
        int dirNumber = 1;
        boolean dirExists = true;
        FileWrapper baseDir = new FileWrapperImpl(base, platformId + String.valueOf(dirNumber));
        
        while (dirExists) {
            
            
            if (baseDir.exists()) {
                dirNumber++;
                baseDir = new FileWrapperImpl(base, String.valueOf(dirNumber));
            } else {
                dirExists = false;
            }
        }

        
        FileFilter dirFilter = new FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory();
            }
        };

        assertNull("listFiles Should Return Null.", baseDir
                .listFiles(dirFilter));

        assertTrue("Failed To Create Parent Directory.", baseDir.mkdir());

        FileWrapper dir1 = null;
        String[] files = { "1.tst", "2.tst", "3.tst" };
        try {
            assertEquals("listFiles Should Return An Array Of Length 0.", 0,
                    baseDir.listFiles(dirFilter).length);

            FileWrapper file = new FileWrapperImpl(baseDir, "notADir.tst");
            try {
                FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
                fos.close();
                assertNull(
                        "listFiles Should Have Returned Null When Used On A File Instead Of A Directory.",
                        file.listFiles(dirFilter));
            } finally {
                file.delete();
            }

            for (int i = 0; i < files.length; i++) {
                FileWrapper f = new FileWrapperImpl(baseDir, files[i]);
                FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
                fos.close();
            }
            dir1 = new FileWrapperImpl(baseDir, "Temp1");
            dir1.mkdir();

            
            FileFilter fileFilter = new FileFilter() {
                public boolean accept(File f) {
                    return f.isFile();
                }
            };

            
            FileWrapper[] directories = baseDir.listFiles(dirFilter);
            assertEquals("Incorrect Number Of Directories Returned.", 1,
                    directories.length);

            
            assertEquals("Incorrect Directory Returned.", 0, ((FileWrapperImpl)directories[0])
                    .compareTo((FileWrapperImpl)dir1));

            
            FileWrapper[] flist = baseDir.listFiles(fileFilter);
            assertEquals("Incorrect Number Of Files Returned.",
                         files.length, flist.length);

            
            
            boolean[] check = new boolean[flist.length];
            for (int i = 0; i < check.length; i++) {
                check[i] = false;
            }
            for (int i = 0; i < files.length; i++) {
                for (int j = 0; j < flist.length; j++) {
                    if (flist[j].getName().equals(files[i])) {
                        check[i] = true;
                        break;
                    }
                }
            }
            int checkCount = 0;
            for (int i = 0; i < check.length; i++) {
                if (check[i] == false) {
                    checkCount++;
                }
            }
            assertEquals("Invalid file returned in listing", 0, checkCount);

            for (int i = 0; i < files.length; i++) {
                FileWrapper f = new FileWrapperImpl(baseDir, files[i]);
                f.delete();
            }
            dir1.delete();
            assertTrue("Parent Directory Not Deleted.", baseDir.delete());
        } finally {
            for (int i = 0; i < files.length; i++) {
                FileWrapper f = new FileWrapperImpl(baseDir, files[i]);
                f.delete();
            }
            if (dir1 != null) {
                dir1.delete();
            }
            baseDir.delete();
        }
    }

    
    public void test_listFilesLjava_io_FilenameFilter() throws IOException {
        String base = System.getProperty("java.io.tmpdir");
        
        int dirNumber = 1;
        boolean dirExists = true;
        FileWrapper dir = new FileWrapperImpl(base, platformId + String.valueOf(dirNumber));
        
        while (dirExists) {
            
            
            if (dir.exists()) {
                dirNumber++;
                dir = new FileWrapperImpl(base, platformId + String.valueOf(dirNumber));
            } else {
                dirExists = false;
            }
        }

        
        FilenameFilter tstFilter = new FilenameFilter() {
            public boolean accept(File f, String fileName) {
                return fileName.endsWith(".tst");
            }
        };

        assertNull("listFiles Should Return Null.", dir.listFiles(tstFilter));

        assertTrue("Failed To Create Parent Directory.", dir.mkdir());

        String[] files = { "1.tst", "2.tst", "3.tmp" };
        try {
            assertEquals("listFiles Should Return An Array Of Length 0.", 0,
                    dir.listFiles(tstFilter).length);

            FileWrapper file = new FileWrapperImpl(dir, "notADir.tst");
            try {
                FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
                fos.close();
                assertNull(
                        "listFiles Should Have Returned Null When Used On A File Instead Of A Directory.",
                        file.listFiles(tstFilter));
            } finally {
                file.delete();
            }

            for (int i = 0; i < files.length; i++) {
                FileWrapper f = new FileWrapperImpl(dir, files[i]);
                FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
                fos.close();
            }

            
            FilenameFilter tmpFilter = new FilenameFilter() {
                public boolean accept(File f, String fileName) {
                    
                    if (fileName.endsWith(".tmp")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            };

            
            FileWrapper[] flist = dir.listFiles(tstFilter);
            assertEquals("Incorrect Number Of Files Passed Through tstFilter.",
                    2, flist.length);
            for (int i = 0; i < flist.length; i++) {
                assertTrue("File Should Not Have Passed The tstFilter.",
                        flist[i].getPath().endsWith(".tst"));
            }

            flist = dir.listFiles(tmpFilter);
            assertEquals("Incorrect Number Of Files Passed Through tmpFilter.",
                    1, flist.length);
            assertTrue("FileWrapper Should Not Have Passed The tmpFilter.", flist[0]
                    .getPath().endsWith(".tmp"));

            for (int i = 0; i < files.length; i++) {
                FileWrapper f = new FileWrapperImpl(dir, files[i]);
                f.delete();
            }
            assertTrue("Parent Directory Not Deleted.", dir.delete());
        } finally {
            for (int i = 0; i < files.length; i++) {
                FileWrapper f = new FileWrapperImpl(dir, files[i]);
                f.delete();
            }
            dir.delete();
        }
    }

    
    public void test_listLjava_io_FilenameFilter() throws IOException {
        String base = System.getProperty("user.dir");
        
        
        
        int dirNumber = 1;
        boolean dirExists = true;
        FileWrapper dir = new FileWrapperImpl(base, platformId + String.valueOf(dirNumber));
        while (dirExists) {
            if (dir.exists()) {
                dirNumber++;
                dir = new FileWrapperImpl(base, String.valueOf(dirNumber));
            } else {
                dirExists = false;
            }
        }

        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return !name.equals("mtzz1.xx");
            }
        };

        String[] flist = dir.list(filter);
        assertNull("Method list(FilenameFilter) Should Have Returned Null.",
                flist);

        assertTrue("Could not create parent directory for test", dir.mkdir());

        String[] files = { "mtzz1.xx", "mtzz2.xx", "mtzz3.yy", "mtzz4.yy" };
        try {
            
            

            flist = dir.list(filter);
            assertEquals("Array Of Length 0 Should Have Returned.", 0,
                    flist.length);

            for (int i = 0; i < files.length; i++) {
                FileWrapper f = new FileWrapperImpl(dir, files[i]);
                FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
                fos.close();
            }

            flist = dir.list(filter);

            assertEquals("Incorrect list returned", flist.length,
                    files.length - 1);

            
            
            boolean[] check = new boolean[flist.length];
            for (int i = 0; i < check.length; i++) {
                check[i] = false;
            }
            String[] wantedFiles = { "mtzz2.xx", "mtzz3.yy", "mtzz4.yy" };
            for (int i = 0; i < wantedFiles.length; i++) {
                for (int j = 0; j < flist.length; j++) {
                    if (flist[j].equals(wantedFiles[i])) {
                        check[i] = true;
                        break;
                    }
                }
            }
            int checkCount = 0;
            for (int i = 0; i < check.length; i++) {
                if (check[i] == false) {
                    checkCount++;
                }
            }
            assertEquals("Invalid file returned in listing", 0, checkCount);

            for (int i = 0; i < files.length; i++) {
                FileWrapper f = new FileWrapperImpl(dir, files[i]);
                f.delete();
            }
            assertTrue("Could not delete parent directory for test.", dir
                    .delete());
        } finally {
            for (int i = 0; i < files.length; i++) {
                FileWrapper f = new FileWrapperImpl(dir, files[i]);
                f.delete();
            }
            dir.delete();
        }
    }

    
    public void test_listRoots() {
        File[] roots = File.listRoots();
        boolean onUnix = File.separatorChar == '/';
        boolean onWindows = File.separatorChar == '\\';
        if (onUnix) {
            assertEquals("Incorrect Number Of Root Directories.", 1,
                    roots.length);
            String fileLoc = roots[0].getPath();
            assertTrue("Incorrect Root Directory Returned.", fileLoc
                    .startsWith(slash));
        } else if (onWindows) {
            
            assertTrue("Incorrect Number Of Root Directories.",
                    roots.length > 0);
        }
    }

    
    public void test_mkdir() throws IOException {
        String base = System.getProperty("user.dir");
        
        
        
        int dirNumber = 1;
        boolean dirExists = true;
        FileWrapper dir = new FileWrapperImpl(base, String.valueOf(dirNumber));
        while (dirExists) {
            if (dir.exists()) {
                dirNumber++;
                dir = new FileWrapperImpl(base, String.valueOf(dirNumber));
            } else {
                dirExists = false;
            }
        }

        assertTrue("mkdir failed", dir.mkdir());
	assertTrue("mkdir worked but exists check failed", dir.exists());
        dir.deleteOnExit();

        String longDirName = "abcdefghijklmnopqrstuvwx";
        String newbase = new String(dir + File.separator);
        StringBuilder sb = new StringBuilder(dir + File.separator);
        StringBuilder sb2 = new StringBuilder(dir + File.separator);

        
        while (dir.getCanonicalPath().length() < 256 - longDirName.length()) {
            sb.append(longDirName + File.separator);
            dir = new FileWrapperImpl(sb.toString());
            assertTrue("mkdir failed", dir.mkdir());
	    assertTrue("mkdir worked but exists check failed", dir.exists());
            dir.deleteOnExit();
        }

        while (dir.getCanonicalPath().length() < 256) {
            sb.append(0);
            dir = new FileWrapperImpl(sb.toString());
            assertTrue("mkdir " + dir.getCanonicalPath().length() + " failed",
                    dir.mkdir());
            assertTrue("mkdir " + dir.getCanonicalPath().length()
                       + " worked but exists check failed", dir.exists());
            dir.deleteOnExit();
        }
        dir = new FileWrapperImpl(sb2.toString());
        
        while (dir.getCanonicalPath().length() < 256) {
            sb2.append(0);
            dir = new FileWrapperImpl(sb2.toString());
            assertTrue("mkdir " + dir.getCanonicalPath().length() + " failed",
                    dir.mkdir());
            assertTrue("mkdir " + dir.getCanonicalPath().length()
                       + " worked but exists check failed", dir.exists());
            dir.deleteOnExit();
        }

        
        String[] ss = { "dir\u", "abc", "abc@123", "!@#$%^&",
                "~\u!\u@\u$", "\u\u\u",
                "\u\u\u" };
        for (int i = 0; i < ss.length; i++) {
            dir = new FileWrapperImpl(newbase, ss[i]);
            assertTrue("mkdir " + dir.getCanonicalPath() + " failed",
                       dir.mkdir());
            assertTrue("mkdir " + dir.getCanonicalPath()
                       + " worked but exists check failed",
                       dir.exists());
            dir.deleteOnExit();
        }
    }

    
    public void test_mkdirs() {
        String userHome = System.getProperty("user.dir");
        if (!userHome.endsWith(slash)) {
            userHome += slash;
        }
        FileWrapper f = new FileWrapperImpl(userHome + "mdtest" + platformId + slash + "mdtest2",
                "p.tst");
        FileWrapper g = new FileWrapperImpl(userHome + "mdtest" + platformId + slash + "mdtest2");
        FileWrapper h = new FileWrapperImpl(userHome + "mdtest" + platformId);
        f.mkdirs();
        try {
            assertTrue("Base Directory not created", h.exists());
            assertTrue("Directories not created", g.exists());
            assertTrue("File not created", f.exists());
        } finally {
            f.delete();
            g.delete();
            h.delete();
        }
    }

    
    public void test_renameToLjava_io_File() throws IOException {
        String base = System.getProperty("user.dir");
        FileWrapper dir = new FileWrapperImpl(base, platformId);
        dir.mkdir();
        FileWrapper f = new FileWrapperImpl(dir, "xxx.xxx");
        FileWrapper rfile = new FileWrapperImpl(dir, "yyy.yyy");
        FileWrapper f2 = new FileWrapperImpl(dir, "zzz.zzz");
        try {
            FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
            fos.write(fileString.getBytes());
            fos.close();
            long lengthOfFile = f.length();

            rfile.delete(); 

            assertTrue("Test 1: File Rename Failed", f.renameTo(rfile));
            assertTrue("Test 2: File Rename Failed.", rfile.exists());
            assertEquals("Test 3: Size Of File Changed.",
                         lengthOfFile, rfile.length());

            fos = new FileOutputStream(rfile.getAbsolutePath());
            fos.close();

            f2.delete(); 
            assertTrue("Test 4: File Rename Failed", rfile.renameTo(f2));
            assertTrue("Test 5: File Rename Failed.", f2.exists());
        } finally {
            f.delete();
            rfile.delete();
            f2.delete();
            dir.delete();
        }
    }

    
    public void test_setLastModifiedJ() throws IOException {
        FileWrapper f1 = null;
        try {
            f1 = new FileWrapperImpl(Support_PlatformFile.getNewPlatformFile(
                    "hyts_tf_slm", ".tmp"));
            f1.createNewFile();
            long orgTime = f1.lastModified();
            
            f1.setLastModified(orgTime - 100000);
            long lastModified = f1.lastModified();
            assertEquals("Test 1: LastModifed time incorrect",
                         orgTime - 100000, lastModified);
            
            f1.setLastModified(orgTime - 10000000);
            lastModified = f1.lastModified();
            assertEquals("Test 2: LastModifed time incorrect",
                         orgTime - 10000000, lastModified);
            
            f1.setLastModified(orgTime + 100000);
            lastModified = f1.lastModified();
            assertEquals("Test 3: LastModifed time incorrect",
                         orgTime + 100000, lastModified);
            
            f1.setLastModified(orgTime + 10000000);
            lastModified = f1.lastModified();
            assertEquals("Test 4: LastModifed time incorrect",
                         orgTime + 10000000, lastModified);
            
            f1.setLastModified(315550800000L);
            lastModified = f1.lastModified();
            assertEquals("Test 5: LastModified time incorrect",
                         315550800000L, lastModified);
            String osName = System.getProperty("os.name", "unknown");
            if (osName.equals("Windows 2000") || osName.equals("Windows NT")) {
                
                boolean result = f1.setLastModified(4354837199000L);
                long next = f1.lastModified();
                
                
                if (result) {
                    assertEquals("Test 6: LastModified time incorrect",
                                 4354837199000L, next);
                }
            }
            
            try {
                f1.setLastModified(-25);
                fail("IllegalArgumentException Not Thrown.");
            } catch (IllegalArgumentException e) {
            }
        } finally {
            if (f1 != null) {
                f1.delete();
            }
        }
    }

    
    public void test_setReadOnly() throws IOException, InterruptedException {
        FileWrapper f1 = null;
        FileWrapper f2 = null;
        try {
            f1 = FileWrapperImpl.createTempFile("hyts_tf", ".tmp");
            f2 = FileWrapperImpl.createTempFile("hyts_tf", ".tmp");
            
            
            f1.setReadOnly();
            
            
            try {
                
                new FileOutputStream(f1.getAbsolutePath());
                fail("IOException not thrown.");
            } catch (IOException e) {
                
            }
            Runtime r = Runtime.getRuntime();
            Process p;
            boolean onUnix = File.separatorChar == '/';
            if (onUnix) {
                p = r.exec("chmod +w " + f1.getAbsolutePath());
            } else {
                p = r.exec("attrib -r \"" + f1.getAbsolutePath() + "\"");
            }
            p.waitFor();
            
            
            FileOutputStream fos = new FileOutputStream(f1.getAbsolutePath());
            fos.write(fileString.getBytes());
            fos.close();
            assertTrue("File Was Not Able To Be Written To.",
                    f1.length() == fileString.length());
            assertTrue("File f1 Did Not Delete", f1.delete());

            
            
            fos = new FileOutputStream(f2.getAbsolutePath());
            
            fos.write(fileString.getBytes());
            fos.close();
            f2.setReadOnly();
            
            
            try {
                
                
                
                fos = new FileOutputStream(f2.getAbsolutePath());
                fail("IOException not thrown.");
            } catch (IOException e) {
                
            }
            r = Runtime.getRuntime();
            if (onUnix) {
                p = r.exec("chmod +w " + f2.getAbsolutePath());
            } else {
                p = r.exec("attrib -r \"" + f2.getAbsolutePath() + "\"");
            }
            p.waitFor();
            assertTrue("File f2 Is Set To ReadOnly.", f2.canWrite());
            fos = new FileOutputStream(f2.getAbsolutePath());
            fos.write(fileString.getBytes());
            fos.close();
            f2.setReadOnly();
            assertTrue("FileWrapper f2 Did Not Delete", f2.delete());
            
            f2 = new FileWrapperImpl(System.getProperty("user.dir"), "deltestdir");
            f2.mkdir();
            f2.setReadOnly();
            assertTrue("Directory f2 Did Not Delete", f2.delete());
            assertTrue("Directory f2 Did Not Delete", !f2.exists());
        } finally {
            if (f1 != null) {
                f1.delete();
            }
            if (f2 != null) {
                f2.delete();
            }
        }
    }

    
    public void test_toString() {
        String fileName = System.getProperty("user.home") + slash + "input.tst";
        FileWrapper f = new FileWrapperImpl(fileName);
        assertEquals("Incorrect string returned", fileName, f.toString());

        if (File.separatorChar == '\\') {
            String result = new FileWrapperImpl("c:\\").toString();
            assertEquals("Removed backslash", "c:\\", result);
        }
    }

    
    public void test_toURI() throws URISyntaxException {
        
        FileWrapper dir = new FileWrapperImpl(System.getProperty("user.dir"));

        
        String newURIPath = dir.getAbsolutePath();
        newURIPath = newURIPath.replace(File.separatorChar, '/');
        if (!newURIPath.startsWith("/")) {
            newURIPath = "/" + newURIPath;
        }
        if (!newURIPath.endsWith("/")) {
            newURIPath += '/';
        }

        URI uri = dir.toURI();
        assertEquals("Test 1A: Incorrect URI Returned.", dir.getAbsoluteFile(), new FileWrapperImpl(uri));
        assertEquals("Test 1B: Incorrect URI Returned.",
                     new URI("file", null, newURIPath, null, null), uri);

        
        FileWrapper f = new FileWrapperImpl(dir, "te% \u st.tst");
        newURIPath = f.getAbsolutePath();
        newURIPath = newURIPath.replace(File.separatorChar, '/');
        if (!newURIPath.startsWith("/")) {
            newURIPath = "/" + newURIPath;
        }

        uri = f.toURI();
        assertEquals("Test 2A: Incorrect URI Returned.",
                     f.getAbsoluteFile(), new FileWrapperImpl(uri));
        assertEquals("Test 2B: Incorrect URI Returned.",
                     new URI("file", null, newURIPath, null, null), uri);

        
        dir = new FileWrapperImpl(""); 
        uri = dir.toURI();
        assertTrue("Test current dir: URI does not end with slash.", uri
                .toString().endsWith("/"));
    }

    
    public void test_toURL() throws MalformedURLException {
        
        FileWrapper dir = new FileWrapperImpl(System.getProperty("user.dir"));

        
        String newDirURL = dir.getAbsolutePath();
        newDirURL = newDirURL.replace(File.separatorChar, '/');
        if (newDirURL.startsWith("/")) {
            newDirURL = "file:" + newDirURL;
        } else {
            newDirURL = "file:/" + newDirURL;
        }
        if (!newDirURL.endsWith("/")) {
            newDirURL += '/';
        }
        assertEquals("Test 1: Incorrect URL Returned.",
                     dir.toURL().toString(), newDirURL);

        
        FileWrapper f = new FileWrapperImpl(dir, "test.tst");
        String newURL = f.getAbsolutePath();
        newURL = newURL.replace(File.separatorChar, '/');
        if (newURL.startsWith("/")) {
            newURL = "file:" + newURL;
        } else {
            newURL = "file:/" + newURL;
        }
        assertEquals("Test 2: Incorrect URL Returned.",
                     f.toURL().toString(), newURL);

        
        dir = new FileWrapperImpl(""); 
        newDirURL = dir.toURL().toString();
        assertTrue("Test current dir: URL does not end with slash.", newDirURL
                .endsWith("/"));
    }

    
    public void test_toURI2() throws URISyntaxException {
        FileWrapper f = new FileWrapperImpl(System.getProperty("user.dir"), "a/b/c/../d/e/./f");

        String path = f.getAbsolutePath();
        path = path.replace(File.separatorChar, '/');
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        URI uri1 = new URI("file", null, path, null);
        URI uri2 = f.toURI();
        assertEquals("uris not equal", uri1, uri2);
    }

    
    public void test_toURL2() throws MalformedURLException {
        FileWrapper f = new FileWrapperImpl(System.getProperty("user.dir"), "a/b/c/../d/e/./f");

        String path = f.getAbsolutePath();
        path = path.replace(File.separatorChar, '/');
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        URL url1 = new URL("file", "", path);
        URL url2 = f.toURL();
        assertEquals("urls not equal", url1, url2);
    }

    
    public void test_deleteOnExit() throws IOException, InterruptedException {
        FileWrapper dir = new FileWrapperImpl("dir4filetest");
        dir.mkdir();
        assertTrue(dir.exists());
        FileWrapper subDir = new FileWrapperImpl("dir4filetest/subdir");
        subDir.mkdir();
        assertTrue(subDir.exists());

        Support_Exec.execJava(new String[] {
                "net.sourceforge.squirrel_sql.fw.util.Support_DeleteOnExit",
                dir.getAbsolutePath(), subDir.getAbsolutePath() },
                new String[] {}, false);
        assertFalse(dir.exists());
        assertFalse(subDir.exists());
    }

    
    public void test_objectStreamClass_getFields() throws Exception {
        
        ObjectStreamClass objectStreamClass = ObjectStreamClass
                .lookup(File.class);
        ObjectStreamField[] objectStreamFields = objectStreamClass.getFields();
        assertEquals(1, objectStreamFields.length);
        ObjectStreamField objectStreamField = objectStreamFields[0];
        assertEquals("path", objectStreamField.getName());
        assertEquals(String.class, objectStreamField.getType());
    }

    
    public void test_list_withUnicodeFileName() throws Exception {
        FileWrapper rootDir = new FileWrapperImpl("P");
        if (!rootDir.exists()) {
            rootDir.mkdir();
            rootDir.deleteOnExit();
        }

        String dirName = new String("src\u");
        FileWrapper dir = new FileWrapperImpl(rootDir, dirName);
        if (!dir.exists()) {
            dir.mkdir();
            dir.deleteOnExit();
        }
        boolean exist = false;
        String[] fileNames = rootDir.list();
        for (String fileName : fileNames) {
            if (dirName.equals(fileName)) {
                exist = true;
                break;
            }
        }
        assertTrue(exist);
    }

    
    protected void setUp() throws IOException {
        
        String userDir = System.getProperty("user.dir");
        if (userDir == null) {
            userDir = "j:\\jcl-builddir\\temp\\source";
        }
        if (!userDir.regionMatches((userDir.length() - 1), slash, 0, 1)) {
            userDir += slash;
        }
        tempDirectory = new FileWrapperImpl(userDir + "tempDir"
                + String.valueOf(System.currentTimeMillis()));
        if (!tempDirectory.mkdir()) {
            System.out.println("Setup for FileTest failed.");
        }

        
        tempFile = new FileWrapperImpl(tempDirectory, "tempfile");
        FileOutputStream tempStream = new FileOutputStream(tempFile.getPath(),
                false);
        tempStream.close();
    }

    
    protected void tearDown() {
        tempFile.delete();
        tempDirectory.delete();
    }
}
