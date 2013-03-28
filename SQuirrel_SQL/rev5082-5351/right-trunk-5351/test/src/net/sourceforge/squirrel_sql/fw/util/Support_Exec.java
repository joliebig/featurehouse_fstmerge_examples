

package net.sourceforge.squirrel_sql.fw.util;


import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Support_Exec
{

	
	public static String execJava(String[] args, String[] classpath, boolean displayOutput)
		throws IOException, InterruptedException
	{
		Object[] arr = execJavaCommon(args, classpath, null, displayOutput, true);

		return getProcessOutput(arr, displayOutput);
	}

	
	public static String execJava(String[] args, String[] classpath, String[] envp, boolean displayOutput)
		throws IOException, InterruptedException
	{
		Object[] arr = execJavaCommon(args, classpath, envp, displayOutput, false);

		return getProcessOutput(arr, displayOutput);
	}

	private static String getProcessOutput(Object[] arr, boolean displayOutput) throws IOException,
		InterruptedException
	{
		Process proc = (Process) arr[0];
		StringBuilder output = new StringBuilder();
		InputStream in = proc.getInputStream();
		int result;
		byte[] bytes = new byte[1024];

		while ((result = in.read(bytes)) != -1)
		{
			output.append(new String(bytes, 0, result));

			if (displayOutput)
			{
				System.out.write(bytes, 0, result);
			}
		}

		in.close();
		proc.waitFor();
		checkStderr(arr);
		proc.destroy();

		return output.toString();
	}

	public static void checkStderr(Object[] execArgs)
	{
		StringBuilder errBuf = (StringBuilder) execArgs[1];

		synchronized (errBuf)
		{
			if (errBuf.length() > 0)
			{
				fail(errBuf.toString());
			}
		}
	}

	public static Object[] execJava2(String[] args, String[] classpath, boolean displayOutput)
		throws IOException, InterruptedException
	{
		return execJavaCommon(args, classpath, null, displayOutput, true);
	}

	private static Object[] execJavaCommon(String[] args, String[] classpath, String[] envp,
		boolean displayOutput, boolean appendToSystemClassPath) throws IOException, InterruptedException
	{
		
		ArrayList<String> execArgs = null;
		StringBuilder classPathString = new StringBuilder();

		String executable;
		String testVMArgs;
		StringTokenizer st;

		execArgs = new ArrayList<String>(3 + args.length);

		
		executable = System.getProperty("java.home");
		if (!executable.endsWith(File.separator))
		{
			executable += File.separator;
		}
		executable += "bin" + File.separator;
		execArgs.add(executable + "java");

		
		if (classpath != null)
		{
			for (String element : classpath)
			{
				classPathString.append(File.pathSeparator);
				classPathString.append(element);
			}
		}
		if (appendToSystemClassPath)
		{
			execArgs.add("-cp");
			execArgs.add(System.getProperty("java.class.path") + classPathString);
		}
		else
		{
			if (classpath != null)
			{
				execArgs.add("-cp");
				execArgs.add(classPathString.toString());
			}
		}

		
		testVMArgs = System.getProperty("hy.test.vmargs");
		if (testVMArgs != null)
		{
			st = new StringTokenizer(testVMArgs, " ");

			while (st.hasMoreTokens())
			{
				execArgs.add(st.nextToken());
			}
		}

		
		for (String arg : args)
		{
			execArgs.add(arg);
		}

		
		
		
		
		
		
		
		
		
		

		
		final Process proc = Runtime.getRuntime().exec(execArgs.toArray(new String[execArgs.size()]), envp);
		final StringBuilder errBuf = new StringBuilder();
		Thread errThread = new Thread(new Runnable()
		{
			public void run()
			{
				synchronized (errBuf)
				{
					InputStream err;
					int result;
					byte[] bytes = new byte[1024];

					synchronized (proc)
					{
						proc.notifyAll();
					}

					err = proc.getErrorStream();
					try
					{
						while ((result = err.read(bytes)) != -1)
						{
							System.err.write(bytes, 0, result);
							errBuf.append(new String(bytes));
						}
						err.close();
					}
					catch (IOException e)
					{
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						PrintStream printer = new PrintStream(out);

						e.printStackTrace();
						e.printStackTrace(printer);
						printer.close();
						errBuf.append(new String(out.toByteArray()));
					}
				}
			}
		});

		synchronized (proc)
		{
			errThread.start();
			
			int count = 0;
			boolean isFinished = false;
			while (!isFinished)
			{
				try
				{
					proc.wait();
					isFinished = true;
				}
				catch (InterruptedException e)
				{
					if (++count == 2) { throw e; }
				}
			}
			if (count > 0)
			{
				Thread.currentThread().interrupt();
			}
		}

		return new Object[] { proc, errBuf };
	}

}
