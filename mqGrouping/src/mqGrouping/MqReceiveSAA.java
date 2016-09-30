package mqGrouping;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import com.ibm.mq.*;
import com.ibm.mq.constants.MQConstants;

public class MqReceiveSAA
{
	public static void debugLine(String line, FileOutputStream log) throws Exception
	{
		Date date = new Date();
		Timestamp ts = new Timestamp(date.getTime());
		String logString = ts + "  -  " + line + "\r\n";
		System.out.print(logString);
		log.write(logString.getBytes());
	}
	public static void main(String[] args) throws MQException, IOException, Exception
	{
		String fileString = "";
		FileOutputStream fosPayload = null;
		FileOutputStream fosXmlV2 = null;
		FileOutputStream fosLog = null;
		File filePayload;
		File fileXmlV2;
		String queueString = "SAA2EBRIDGE.FILE.IN.MSG.DEV";
		String qmgrString = "CMDS";
		MQEnvironment.hostname = "mqcmds-cmcc.dkd1.root4.net";
		MQEnvironment.port = 1414;
		MQEnvironment.channel = "CLIENTS.EBRIDGE";			
		byte[] contentInBytes;
		MQQueueManager qmgr = null;
		MQQueue queue = null;
		int iteration = 0;
		MQGetMessageOptions gmo = new MQGetMessageOptions();
		try
		{
			fosLog = new FileOutputStream("C:/temp/saaReceiveLocal/receiveLog.txt");
			debugLine("Before Connect", fosLog);
			qmgr = new MQQueueManager(qmgrString);
			debugLine("After Connect", fosLog);
			queue = qmgr.accessQueue(queueString,MQConstants.MQOO_INPUT_AS_Q_DEF);
			debugLine("After open queue", fosLog);
			gmo.options = MQConstants.MQGMO_LOGICAL_ORDER | MQConstants.MQGMO_ALL_MSGS_AVAILABLE;
			gmo.matchOptions = MQConstants.MQMO_NONE;
			filePayload = new File("C:/temp/saaReceiveLocal/payload.xml");
			fileXmlV2 = new File("C:/temp/saaReceiveLocal/xmlv2.xml");
			fosPayload = new FileOutputStream(filePayload);
			fosXmlV2 = new FileOutputStream(fileXmlV2);
			
			/*if (!file.exists())
			{
				file.createNewFile();
			}*/
			MQMessage message = new MQMessage();
			do 
			{
				iteration++;
				debugLine("Iteration: " + iteration, fosLog);
				queue.get(message, gmo);
				debugLine("After get", fosLog);
				int dataLength = message.getDataLength();
				/*if (iteration == 1) {
					fosXmlV2.write(message.readStringOfByteLength(dataLength).getBytes());
				}
				else {*/
					fosPayload.write(message.readStringOfByteLength(dataLength).getBytes());
				//}
				
			} while (gmo.groupStatus != MQConstants.MQGS_LAST_MSG_IN_GROUP);
		}
		catch(MQException e) {
			if(e.reasonCode == e.MQRC_NO_MSG_AVAILABLE) {
				System.out.println("no more message available or retrived");
			}
			else {
				System.out.println("ReasonCode: " + e.reasonCode + ". CompCode: " + e.completionCode + ".");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			debugLine("#Inside finally#", fosLog);
			if (fosPayload != null)
			{
				debugLine("Close fosPayload", fosLog);
				fosPayload.flush();
				fosPayload.close();
			}
			if (fosXmlV2 != null)
			{
				debugLine("Close fosXmlV2", fosLog);
				fosXmlV2.flush();
				fosXmlV2.close();
			}
			if (queue != null)
			{
				debugLine("Close queue", fosLog);
				queue.close();
			}
			if (qmgr != null)
			{
				debugLine("Disconnect", fosLog);
				qmgr.disconnect();
			}
			if (fosLog != null)
			{
				debugLine("Close fosLog", fosLog);
				fosLog.flush();
				fosLog.close();
			}
		}
	}
}
