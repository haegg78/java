package backup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import backup.findFile.Finder;

public class backup
{
	public static void main(String[] args)
		throws IOException, InterruptedException
	{
		if (args.length != 2)
		{
			System.out.println("Proper Usage is: java -cp backupSessions.jar backup.backup <Ora Developer Search Path> <Destination Path>");
	        System.exit(1);
		}
		String oracleDeveloperSearchPath = args[0];
		String backupPath = args[1];
		String pattern = "connections.xml";
		// Finding the file in variable pattern and copying to backup
		Path startingDir = Paths.get(oracleDeveloperSearchPath);
		Finder finder = new Finder(pattern);
		//Path foundPath = Files.walkFileTree(startingDir, finder);
		Files.walkFileTree(startingDir, finder);
		ArrayList<String> filesFound = finder.done();
		System.out.println("Files found based on pattern '" + pattern + "': " + filesFound.size() + " in base directory: " + oracleDeveloperSearchPath);
		Iterator<String> iterator = filesFound.iterator();
		while (iterator.hasNext())
		{
			//System.out.println(iterator.next());
			File srcFile = new File(iterator.next());
			File destFile = new File(backupPath + "/" + srcFile.getName());
			findFile.FileCopy(srcFile,destFile);
		}
		//
		// Backup Putty sessions using regedit
		String puttyFile = backupPath + "/putty.reg";
		findFile.backupPutty(puttyFile);
	}
}
