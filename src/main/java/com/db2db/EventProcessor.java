package com.db2db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.json.JSONObject;

import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventprocessorhost.CloseReason;
import com.microsoft.azure.eventprocessorhost.IEventProcessor;
import com.microsoft.azure.eventprocessorhost.PartitionContext;

public class EventProcessor implements IEventProcessor {
	private int checkpointBatchingCount = 0;

	private String GROUPID = System.getenv("GROUPID");

	public EventProcessor() {

	}

	// @Override
	public void onOpen(PartitionContext context) throws Exception {

		System.out.println("SAMPLE: Partition " + context.getPartitionId() + " is opening");
	}

	// @Override
	public void onClose(PartitionContext context, CloseReason reason) throws Exception {
		System.out.println(
				"SAMPLE: Partition " + context.getPartitionId() + " is closing for reason " + reason.toString());
	}

	// @Override
	public void onError(PartitionContext context, Throwable error) {
		System.out.println("SAMPLE: Partition " + context.getPartitionId() + " onError: " + error.toString());
	}

	public synchronized String nextId(String parId) {
		return this.GROUPID + String.format("%2d", parId) + System.currentTimeMillis();
	}

	// @Override
	public void onEvents(PartitionContext context, Iterable<EventData> messages) throws Exception {
		System.out.println("SAMPLE: Partition " + context.getPartitionId() + " got message batch");
		int messageCount = 0;
		Connection con = null;
		try {
			

			for (EventData data : messages) {
				System.out.println("SAMPLE (" + context.getPartitionId() + "," + data.getSystemProperties().getOffset()
						+ "," + data.getSystemProperties().getSequenceNumber() + "): "
						+ new String(data.getBody(), "UTF8"));
				messageCount++;

				JSONObject msg = null;
				try {
					msg = new JSONObject(new String(data.getBytes()));
				} catch (Exception e) {
					System.out.println("msg format Error: " + e.getMessage() + " -- " + msg);
					msg = new JSONObject("{}");
				}

				if (!GROUPID.equals(msg.optString("from"))) {
					if(con == null)con = PoolManager.getInstance().getConnection();
					PreparedStatement ps = null;
					switch (msg.optString("action")) {
					case "Add":
						try {
							ps = con.prepareStatement("insert into tblTodo(id, item, status) values(?,?,?)");
							ps.setString(1, msg.optString("id"));
							ps.setString(2, msg.optString("item"));
							ps.setString(3, msg.optString("status"));
							ps.executeUpdate();
						}catch(SQLIntegrityConstraintViolationException de) {
							System.out.println("ignore duplicated id - " + msg.opt("id"));
						}catch(Exception e) {
							System.out.println("Insert error handling - " + msg.toString() );
							e.printStackTrace();
						}
						break;
					case "Delete":
						
						ps = con.prepareStatement("update tblTodo set status=? where id=?");
						ps.setString(1, msg.optString("status"));
						ps.setString(2, msg.optString("id"));						
						ps.executeUpdate();
						break;
						
					case "Update":
						
						break;
					default:
						

					}
					if(ps !=null )
						ps.close();
				}

				

				this.checkpointBatchingCount++;
				if ((checkpointBatchingCount % 5) == 0) {
					// System.out.println("SAMPLE: Partition " + context.getPartitionId() + "
					// checkpointing at " +
					// data.getSystemProperties().getOffset() + "," +
					// data.getSystemProperties().getSequenceNumber());
					context.checkpoint(data);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(con!=null)
				try {
					con.close();
				}catch(Exception e) {}
		}
		System.out.println("SAMPLE: Partition " + context.getPartitionId() + " batch size was " + messageCount
				+ " for host " + context.getOwner());
	}
}