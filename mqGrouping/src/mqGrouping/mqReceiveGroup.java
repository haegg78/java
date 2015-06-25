package mqGrouping;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import com.ibm.mq.*;
import com.ibm.mq.constants.MQConstants;

public class mqReceiveGroup
{
	public static void debugLine(String line)
	{
		Date date = new Date();
		Timestamp ts = new Timestamp(date.getTime());
		System.out.println(ts + "  -  " + line);
	}
	public static void main(String[] args) throws MQException, IOException
	{
		String fileString = "";
		FileOutputStream fos = null;
		File file;
		String queueString = "";
		String qmgrString = "";
		byte[] contentInBytes;
		if (args[0] == "CMDS")
		{
			fileString = "H:/Sample Files/telia_cremul.txt_AfterWMQ";
			queueString = "OPF2EBRIDGE.EBAOUT.REQ";
			qmgrString = "CMDS";
			MQEnvironment.hostname = "mqcmds-cmcc.dkd1.root4.net";
			MQEnvironment.port = 1414;
			MQEnvironment.channel = "CLIENTS.EBRIDGE";			
		}
		else
		{
			fileString = "H:/Sample Files/telia_cremul.txt_AfterWMQ";
			queueString = "OPF2EBRIDGE.EBAOUT.REQ";
			qmgrString = "";
			MQEnvironment.hostname = "localhost";
			MQEnvironment.port = 1414;
			MQEnvironment.channel = "";			
		}
		MQQueueManager qmgr = null;
		MQQueue queue = null;
		int iteration = 0;
		MQGetMessageOptions gmo = new MQGetMessageOptions();
		try
		{
			debugLine("Before Connect");
			qmgr = new MQQueueManager(qmgrString);
			debugLine("After Connect");
			queue = qmgr.accessQueue(queueString,MQConstants.MQOO_INPUT_AS_Q_DEF);
			debugLine("After open queue");
			gmo.options = MQConstants.MQGMO_LOGICAL_ORDER | MQConstants.MQGMO_ALL_MSGS_AVAILABLE;
			gmo.matchOptions = MQConstants.MQMO_NONE;
			file = new File(fileString);
			fos = new FileOutputStream(file);
			if (!file.exists())
			{
				file.createNewFile();
			}
			MQMessage message = new MQMessage();
			do 
			{
				iteration++;
				debugLine("Iteration: " + iteration);
				queue.get(message, gmo);
				debugLine("After get");
				int dataLength = message.getDataLength();
				fos.write(message.readStringOfByteLength(dataLength).getBytes());
			} while (gmo.groupStatus != MQConstants.MQGS_LAST_MSG_IN_GROUP);
			fos.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			debugLine("#Inside finally#");
			if (fos != null)
			{
				debugLine("Close fos");
				fos.close();
			}
			if (queue != null)
			{
				debugLine("Close queue");
				queue.close();
			}
			if (qmgr != null)
			{
				debugLine("Disconnect");
				qmgr.disconnect();
			}
		}
	}
}
