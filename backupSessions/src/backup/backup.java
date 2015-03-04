package backup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import backup.findFile.Finder;

public class backup
{
	public static void main(String[] args)
		throws IOException, InterruptedException
	{
		if (args.length != 3)
		{
			System.out.println("Proper Usage is: java -cp backupSessions.jar backup.backup <Ora Developer Search Path> <WinSCP Ini file> <Destination Path>");
	        System.exit(1);
		}
		String timeStamp = new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date());
		System.out.println(timeStamp + "\r\n---- Start of backup ----");
		String oracleDeveloperSearchPath = args[0];
		String backupPath = args[2];
		String pattern = "connections.xml";
		File srcFile = null;
		File destFile = null;
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
			srcFile = new File(iterator.next());
			destFile = new File(backupPath + "/" + srcFile.getName() + "_" + timeStamp);
			findFile.FileCopy(srcFile,destFile);
		}
		//
		// Backup Putty sessions using regedit
		String puttyFile = backupPath + "/putty.reg_" + timeStamp;
		findFile.backupPutty(puttyFile);
		//
		// Backup WinSCP ini file
		String winscpFile = args[1];
		String winscpBackupFile = backupPath + "/WinSCP.ini_" + timeStamp;
		srcFile = new File(winscpFile);
		destFile = new File(winscpBackupFile);
		findFile.FileCopy(srcFile,destFile);
	}
}
