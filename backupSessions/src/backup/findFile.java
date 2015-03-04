package backup;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.*;

import static java.nio.file.FileVisitResult.*;
import static java.nio.file.FileVisitOption.*;

import java.util.*;

public class findFile
{
	public static class Finder 
		extends SimpleFileVisitor<Path>
	{
		ArrayList<String> foundFiles = new ArrayList<String>();
		private final PathMatcher matcher;
		private int numMatches = 0;
		Finder(String pattern)
		{
			matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
		}
		// Compares the glob pattern against
		// the file or directory name.
		void find(Path file)
		{
			Path name = file.getFileName();
			if (name != null && matcher.matches(name)) 
			{
				numMatches++;
				//System.out.println(file);
				foundFiles.add(file.toString());
			}
		}
		// Prints the total number of
		// matches to standard out.
		ArrayList<String> done()
		{
			//System.out.println("Matched: " + numMatches);
			//System.out.println("Size of arrayList: " + foundFiles.size());
			return foundFiles;
		}
		// Invoke the pattern matching
		// method on each file.
		@Override
		public FileVisitResult visitFile(Path file,BasicFileAttributes attrs)
		{
			find(file);
			return CONTINUE;
		}
		// Invoke the pattern matching
		// method on each directory.
		@Override
		public FileVisitResult preVisitDirectory(Path dir,BasicFileAttributes attrs)
		{
			find(dir);
			return CONTINUE;
		}
		@Override
		public FileVisitResult visitFileFailed(Path file,IOException exc)
		{
			System.err.println(exc);
			return CONTINUE;
		}
	}
	public static void main(String[] args)
		throws IOException
	{
		if (args.length < 3 || !args[1].equals("-name"))
		{
			System.err.println("java Find <path>" +
		            " -name \"<glob_pattern>\"");
		}
		Path startingDir = Paths.get(args[0]);
		String pattern = args[2];
		Finder finder = new Finder(pattern);
		System.out.println("After Finder.");
		Files.walkFileTree(startingDir, finder);
		//finder.done();
	}
	public static void backupPutty(String puttyFileString)
		throws IOException, InterruptedException
	{
		File puttyFile = new File(puttyFileString);
		if(!puttyFile.exists()) {
			puttyFile.createNewFile();
	    }
		String puttyRegistryKey = "HKEY_CURRENT_USER\\Software\\SimonTatham";
		String command = "regedit.exe /e " + puttyFileString + " " + puttyRegistryKey; 
		int exitCode = 0;
		Runtime rt = null;
		Process proc = null;
		try
		{
			rt = Runtime.getRuntime();
			proc = rt.exec(command);
			exitCode = proc.waitFor();
			//System.out.println("Exitcode of export:" + exitCode);
			System.out.println("Backup of Putty sessions from " + puttyRegistryKey + " to file " + puttyFileString + " completed.");
		}
		finally
		{
			
		}
	}
	public static void FileCopy(File sourceFile, File destFile) 
		throws IOException
	{
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;

	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	        System.out.println("Backup of file " + sourceFile.toString() + " to " + destFile.toString() + " completed.");
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
}