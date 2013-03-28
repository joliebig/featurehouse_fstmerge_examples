package net.sourceforge.squirrel_sql.client;




public class ApplicationManager {
	
    static {
        initApplication();
    }
    
    
	public static void initApplication() {
        ApplicationArguments.reset();
		ApplicationArguments.initialize(new String[0]);
		ApplicationArguments.getInstance();
	}
}
