package myteam.global.constants;


public class NotificationData {
//	public static Map<String, String> MapedData = new HashMap<String, String>(50);
//	public static List<Map<String, String>> NotificationArray = new ArrayList<Map<String, String>>(50);

	public static final int TYPE_JOIN_GROUP = 999;
	public static final String JOIN_GROUP_ARRAY[] = { "NOTIFICATIONID","CODE", "SENDER",
			"STATUS", "DATE", "NAME", "DESIGNATION", "MOBILENO" };
	public static final int JOIN_GROUP_LENGTH = JOIN_GROUP_ARRAY.length;
	public static final String JOIN_GROUP_MSG = "NEW 'JOIN GROUP' REQUEST..";

	
	public static final int TYPE_NEW_TASK = 998;
	public static final String NEW_TASK_ARRAY[] = { "NOTIFICATIONID","CODE", "SENDER",
			"DATE", "TASKID","STATUS" };
	public static final int NEW_TASK_LENGTH = NEW_TASK_ARRAY.length;
	public static final String NEW_TASK_MSG = "NEW 'TASK' ALLOCATED..";
	
	public static final int TYPE_STATUS_UPDATE = 997;
	public static final String STATUS_UPDATE_ARRAY[] = { "NOTIFICATIONID","CODE", "SENDER",
			"DATE", "TASKID","STATUS" };
	public static final int STATUS_UPDATE_LENGTH = STATUS_UPDATE_ARRAY.length;
	public static final String STATUS_UPDATE_MSG = "TASK STATUS HAS BEEN UPDATED...";
	
	public static final int TYPE_NEW_COMMENT = 996;
	public static final String NEW_COMMENT_ARRAY[] = { "NOTIFICATIONID","CODE", "SENDER",
			"DATE", "TASKID" };
	public static final int NEW_COMMENT_LENGTH = NEW_COMMENT_ARRAY.length;
	public static final String NEW_COMMENT_MSG = "NEW 'COMMENT' IS ADDED...";
	
	public static final int TYPE_TASK_COMPLETE = 995;
	public static final String TASK_COMPLETE_ARRAY[] = { "NOTIFICATIONID","CODE", "SENDER",
			"DATE", "TASKID" };
	public static final int TASK_COMPLETE_LENGTH = TASK_COMPLETE_ARRAY.length;
	public static final String TASK_COMPLETE_MSG = "[Task Complete] Please score the member's performance..";
	
	
	
//	public static void ClearAllNotification() {
//		MapedData.clear();
//		NotificationArray.clear();
//	}
}
