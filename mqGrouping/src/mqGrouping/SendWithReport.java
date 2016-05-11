package mqGrouping;

import java.io.FileInputStream;
import java.sql.Timestamp;
import java.util.Date;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;

public class SendWithReport {

	public static void debugLine(String line)
	{
		Date date = new Date();
		Timestamp ts = new Timestamp(date.getTime());
		System.out.println(ts + "  -  " + line);
	}
	public static void main(String[] args) throws Exception {
		String file = "C:/Users/n464210/Documents/connectionTest.txt";
		String content = "";
		FileInputStream fin = null;
		MQEnvironment.hostname = "mqcmds-cmcc.dkd1.root4.net";
		MQEnvironment.port = 1414;
		MQEnvironment.channel = "CLIENTS.EBRIDGE";
		MQQueueManager qmgr = null;
		MQQueue queue = null;
		MQPutMessageOptions pmo = new MQPutMessageOptions();
		byte readBuffer[] = new byte[8192];
		try {
			debugLine("Before Connect");
			qmgr = new MQQueueManager("CMDS");
			debugLine("After Connect");
			queue = qmgr.accessQueue("EBRIDGE.DUMMY",MQConstants.MQOO_OUTPUT);
			debugLine("After open queue");
			fin = new FileInputStream(file);
			MQMessage message = new MQMessage();
			message.format = "MQSTR";
			message.report = MQConstants.MQRO_COA;
			message.replyToQueueManagerName = "DUMMY";
			message.replyToQueueName = "EBRIDGE.DUMMY.REP";
			int numRead = fin.read(readBuffer);
			debugLine("Bytes read: " + numRead);
			content = new String(readBuffer, 0, numRead);
			message.writeString(content);
			queue.put(message, pmo);
		}
		finally {
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
		}
	}

}
