package mqGrouping;

import java.io.IOException;

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.MQMD;

public class BrowseBISWay {

	public static void main(String[] args)  throws MQException, IOException, MQDataException
	{
		// TODO Auto-generated method stub
		MQEnvironment.hostname = "mqcmds-cmcc.dkd1.root4.net";
		MQEnvironment.port = 1414;
		MQEnvironment.channel = "CLIENTS.EBRIDGE";
		//String queueName = "EBRIDGE.DUMMY";
		String queueName = "SAA2EBRIDGE.FILE.IN.MSG.DEV";
		String groupId = "";
		MQQueueManager QMgr = new MQQueueManager("CMDS");
		int loop = 0;
		int openOptions = MQConstants.MQOO_FAIL_IF_QUIESCING |MQConstants.MQOO_INPUT_SHARED |MQConstants.MQOO_BROWSE ;  
		MQQueue queue = QMgr.accessQueue(queueName, openOptions);
		MQMD md = new MQMD ();
		MQMessage theMessage    = new MQMessage();
		MQGetMessageOptions gmo = new MQGetMessageOptions();
			gmo.options = MQConstants.MQGMO_ALL_MSGS_AVAILABLE |MQConstants.MQGMO_ALL_SEGMENTS_AVAILABLE;
			gmo.matchOptions = MQConstants.MQMO_NONE;
			gmo.waitInterval=5000;
		boolean thereAreMessages=true;
		while (thereAreMessages) {
			
		}
	}

}
