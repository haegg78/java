package mqGrouping;

import java.io.FileOutputStream;
import java.io.IOException;

import com.ibm.mq.*;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.MQHeaderList;
import com.ibm.mq.headers.MQMD; 
public class Browser
{
	public static void main(String[] args) throws MQException, IOException, MQDataException
	{
		FileOutputStream fos = null;
		MQEnvironment.hostname = "mqcmds-cmcc.dkd1.root4.net";
		MQEnvironment.port = 1414;
		MQEnvironment.channel = "CLIENTS.EBRIDGE";
		String queueName = "EBRIDGE.DUMMY";
		//String queueName = "EBRIDGE.DUMMY.REP";
		//String queueName = "SAA2EBRIDGE.FILE.IN.MSG.DEV";
		//String queueName = "SAA2EBRIDGE.FILE.REP.DEV";
		//String queueName = "SAA2EBRIDGE.FILE.STATUS.MSG.DEV";
		String fileNamePrefix = "C:/users/n464210/" + queueName + "_";
		String groupId = "";
		MQQueueManager QMgr = new MQQueueManager("CMDS");
		int loop = 0;
		int openOptions = MQConstants.MQOO_FAIL_IF_QUIESCING |MQConstants.MQOO_INPUT_SHARED |MQConstants.MQOO_BROWSE ;  
		byte messageArray[] = null;
		MQQueue queue = QMgr.accessQueue(queueName, openOptions);
		MQMD md = new MQMD ();
		MQMessage theMessage    = new MQMessage();
		MQGetMessageOptions gmo = new MQGetMessageOptions();
			gmo.options = MQConstants.MQGMO_WAIT |MQConstants.MQGMO_LOGICAL_ORDER |MQConstants.MQGMO_BROWSE_FIRST;
		    gmo.matchOptions = MQConstants.MQMO_NONE;
		    gmo.waitInterval=5000;

		boolean thereAreMessages=true;
		while(thereAreMessages){
		    try{
		        //read the message          
		        queue.get(theMessage,gmo);  
		        //print the text
		        loop++;
		        fos = new FileOutputStream(fileNamePrefix + loop + ".txt");
		        //writer = new BufferedWriter(new FileWriter("C:/users/n464210/imex_testfile_" + loop + ".txt"));
		        groupId = new String(theMessage.groupId);
		        md.copyFrom(theMessage);
		        System.out.println (md + "\n");
		        //System.out.println (md + "\n" + new MQHeaderList (theMessage, true));
		        System.out.println("Group Id: " + groupId);
		        System.out.println("Sequence number: " + theMessage.messageSequenceNumber);
		        //System.out.println("Last message in group: " + theMessage.messageFlags);
		        System.out.println("Message length: " + theMessage.getMessageLength());
		        if ( gmo.groupStatus == MQConstants.MQGS_LAST_MSG_IN_GROUP)
		        {
		        	System.out.println("Last message in group");
		        }
		        else
		        { 
		        	if ( gmo.groupStatus == MQConstants.MQGS_MSG_IN_GROUP)
		        	{
		        		System.out.println("Message in group");
		        	}
		        }
		        messageArray = new byte[theMessage.getMessageLength()];
		        theMessage.readFully(messageArray);
		        fos.write(messageArray);
		        //System.out.println("msg text: "+msgText);

		                 // <--- Solution code Here

		        //move cursor to the next message               
		        gmo.options = MQConstants.MQGMO_WAIT |MQConstants.MQGMO_LOGICAL_ORDER|MQConstants.MQGMO_BROWSE_NEXT;

		    }catch(MQException e){

		        if(e.reasonCode == e.MQRC_NO_MSG_AVAILABLE) {
		            System.out.println("no more message available or retrived");
		        }
		        //e.printStackTrace();
		        thereAreMessages=false;
		    } catch (IOException e) {
		        System.out.println("ERROR: "+e.getMessage());
		    } catch (Exception e) {
		    	System.out.println("ERROR: "+e.getMessage());
		    }
		    finally
		    {
		    	if ( fos != null)
		    	{
		    		fos.close();
		    	}
		    }
		}
	}
}

