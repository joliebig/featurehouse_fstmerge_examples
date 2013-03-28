package tests.net.sf.jabref.testutils;

import java.security.Permission;

import net.sf.jabref.JabRef;


public class TestUtils {

	public static final String PATH_TO_TEST_BIBTEX = "src/tests/net/sf/jabref/bibtexFiles/test.bib";	

	
	public static JabRef getInitializedJabRef() {
		disableSystemExit();
		try {
			String[] args = { "-p", " ", PATH_TO_TEST_BIBTEX };
			JabRef.main(args);
		} catch (ExitException e) {
		} finally {
			enableSystemExit();
		}
		JabRef jabref = JabRef.singleton;
		return jabref;
	}

	
	public static void closeJabRef() {
		JabRef jabref = JabRef.singleton;
		if (jabref != null) {
			jabref.jrf.dispose();
		}
	}

	@SuppressWarnings("serial")
	private static class ExitException extends SecurityException {
	}

	private static void disableSystemExit() {
		final SecurityManager securityManager = new SecurityManager() {
			public void checkPermission(Permission permission) {
				if (permission.getName().contains("exitVM")) {
					throw new ExitException();
				}
			}
		};
		System.setSecurityManager(securityManager);
	}

	private static void enableSystemExit() {
		System.setSecurityManager(null);
	}

}
