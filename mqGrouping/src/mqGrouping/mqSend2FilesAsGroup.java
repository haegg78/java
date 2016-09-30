package mqGrouping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import com.ibm.mq.*;
import com.ibm.mq.constants.MQConstants;

public class mqSend2FilesAsGroup
{
	public static void debugLine(String line)
	{
		Date date = new Date();
		Timestamp ts = new Timestamp(date.getTime());
		System.out.println(ts + "  -  " + line);
	}
	public static void main(String[] args) throws MQException, IOException
	{
		//String fileString = "H:/Sample Files/egmont_bansta.txt";
		//String fileString = "H:/Sample Files/telia_cremul.txt";
		String triggerFile = "H:/MyData/_imex_trigger.xml";
		String payloadFile = "H:/MyData/export.zip";
		FileInputStream fin = null;
		String content = "";
		int splitSize = 1500000;
		int fileLength;
		int maxIterations;
		int numRead;
		MQEnvironment.hostname = "mqcmds-cmcc.dkd1.root4.net";
		MQEnvironment.port = 1414;
		MQEnvironment.channel = "CLIENTS.EBRIDGE";
		MQQueueManager qmgr = null;
		MQQueue queue = null;
		MQPutMessageOptions pmo = new MQPutMessageOptions();
		byte readBuffer[] = new byte[splitSize];
		try 
		{
			debugLine("Before Connect");
			qmgr = new MQQueueManager("CMDS");
			debugLine("After Connect");
			//queue = qmgr.accessQueue("IMX2WV.FILEACT.MSG",MQConstants.MQOO_OUTPUT);
			queue = qmgr.accessQueue("EBRIDGE.DUMMY",MQConstants.MQOO_OUTPUT);
			debugLine("After open queue");
			pmo.options = MQConstants.MQPMO_LOGICAL_ORDER;
			/* Code for reading file and sending it grouped */
			// Access a file and read 
			debugLine("Before trigger");
			File file = new File(triggerFile);
			fin = new FileInputStream(file);
			MQMessage message1 = new MQMessage();
			message1.format = "MQSTR";
			message1.setBooleanProperty("partOfGroup", true);
			numRead = fin.read(readBuffer);
			debugLine("Bytes read: " + numRead);
			content = new String(readBuffer, 0, numRead);
			message1.writeString(content);
			message1.messageFlags = MQConstants.MQMF_MSG_IN_GROUP;
			queue.put(message1, pmo);
			fin.close();
			debugLine("Before payload");
			file = new File(payloadFile);
			fin = new FileInputStream(file);
			MQMessage message2 = new MQMessage();
			message2.format = "";
			message2.setBooleanProperty("partOfGroup", true);
			numRead = fin.read(readBuffer);
			debugLine("Bytes read: " + numRead);
			//content = new String(readBuffer, 0, numRead);
			message2.write(readBuffer, 0, numRead);
			//message2.writeString(content);
			message2.messageFlags = MQConstants.MQMF_LAST_MSG_IN_GROUP;
			queue.put(message2, pmo);
			/*for (int i = 0; i <= maxIterations; i++)
			{
				debugLine("Iteration: " + i);
				MQMessage message = new MQMessage();
				message.format = "MQFMT_NONE";
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
			*/
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