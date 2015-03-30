package mqGrouping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import com.ibm.mq.*;
import com.ibm.mq.constants.MQConstants;

public class mqSendGroup
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
		FileInputStream fin = null;
		String content = "";
		String queueString = "";
		String qmgrString = "";
		int splitSize = 4000000;
		int fileLength;
		int maxIterations;
		int numRead;
		if (arg[0] == "CMDS")
		{
			fileString = "H:/Sample Files/telia_cremul.txt";
			queueString = "OPF2EBRIDGE.EBAOUT.REQ";
			qmgrString = "CMDS";
			MQEnvironment.hostname = "mqcmds-cmcc.dkd1.root4.net";
			MQEnvironment.port = 1414;
			MQEnvironment.channel = "CLIENTS.EBRIDGE";			
		}
		else
		{
			fileString = "H:/Sample Files/telia_cremul.txt";
			queueString = "OPF2EBRIDGE.EBAOUT.REQ";
			qmgrString = "";
			MQEnvironment.hostname = "mqcmds-cmcc.dkd1.root4.net";
			MQEnvironment.port = 1414;
			MQEnvironment.channel = "CLIENTS.EBRIDGE";			
		}
		MQQueueManager qmgr = null;
		MQQueue queue = null;
		MQPutMessageOptions pmo = new MQPutMessageOptions();
		byte readBuffer[] = new byte[splitSize];
		try 
		{
			debugLine("Before Connect");
			qmgr = new MQQueueManager(qmgrString);
			debugLine("After Connect");
			queue = qmgr.accessQueue(queueString,MQConstants.MQOO_OUTPUT);
			debugLine("After open queue");
			pmo.options = MQConstants.MQPMO_LOGICAL_ORDER;
			/* Code for reading file and sending it grouped */
			// Access a file and read 
			File file = new File(fileString);
			fin = new FileInputStream(file);
			fileLength = (int)file.length();
			maxIterations = fileLength / splitSize;
			for (int i = 0; i <= maxIterations; i++)
			{
				debugLine("Iteration: " + i);
				MQMessage message = new MQMessage();
				message.format = "MQSTR";
				message.setBooleanProperty("partOfGroup", true);
				numRead = fin.read(readBuffer);
				debugLine("Bytes read: " + numRead);
				content = new String(readBuffer, 0, numRead);
				message.writeString(content);
				if (i < maxIterations)
				{
					message.messageFlags = MQConstants.MQMF_MSG_IN_GROUP;
				}
				else
				{
					message.messageFlags = MQConstants.MQMF_LAST_MSG_IN_GROUP;
				}
				queue.put(message, pmo);
			}
			// End of sending file code
			//
			/* Sample code for sending 5 messages in a group
			for (int i = 1; i <= 5; i++)
			{
				MQMessage message = new MQMessage();
				message.format = "MQSTR";
				message.writeString("Message " + i + "\n");
				if (i < 5)
				{
					message.messageFlags = MQConstants.MQMF_MSG_IN_GROUP;
				}
				else
				{
					message.messageFlags = MQConstants.MQMF_LAST_MSG_IN_GROUP;
				}
				queue.put(message, pmo);
			}
			*/
		}
		finally
		{
			debugLine("#Inside finally#");
			if (fin != null)
			{
				debugLine("Close fin");
				fin.close();
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
			if (fin != null)
			{
				debugLine("Close fin");
				fin.close();
			}
		}
	}
}