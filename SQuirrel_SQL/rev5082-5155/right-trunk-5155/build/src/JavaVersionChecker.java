import javax.swing.JOptionPane;




public class JavaVersionChecker
{
	 
	private static final String JAVA_HOME_PROPERTY = "java.home";
	
	
	private static final String JAVA_VERSION_PROPERTY = "java.version";

	
	public static void main(String[] args)
	{
		if (args.length == 0) {
			System.err.println("JavaVersionChecker: Must specify one or more minimum JVM versions");
			System.exit(1);
		}
				
		String jvmVersion = System.getProperty(JAVA_VERSION_PROPERTY);
		if (!checkVersion(jvmVersion, args)) {
			String javaHome = System.getProperty(JAVA_HOME_PROPERTY);
			JOptionPane.showMessageDialog(null, 
				"Your Java Virtual Machine must be at least "+args[0]+" to run SQuirreL 3.x and above\n" +				
				"  JVM Version used: "+jvmVersion+ "\n" +
				"  JVM Location: "+javaHome);
			System.exit(1);
		}
		System.exit(0);
	}
	
	
	private static boolean checkVersion(String jvmVersion, String[] minimumJavaVersions) {
		
		if (jvmVersion == null) {
			System.err.println("jvm version could not be determined. The "+JAVA_VERSION_PROPERTY+
				" system property is null");
		}
		
		boolean result = false;
		for (int i = 0; i < minimumJavaVersions.length; i++) {
			if (jvmVersion.startsWith(minimumJavaVersions[i])) {
				result = true;
			}
		}
		return result;
	}

}
