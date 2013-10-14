package myteam.main.forms;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import myteam.global.constants.BasicDetails;
import myteam.global.constants.MyTeamSettings;
import myteam.team.management.ProfileDetails;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

public class MyTeam extends Application {
	public ChatScreen chatscreen = null;
	public HomeScreen homescreen = null;
	public MoreSettings moreSetting=null;
	public TaskList tasklist=null;
	public TeamList teamlist=null;
	public ProfileDetails profiledetails=null;
	public static Context currentContext = null;
	public static String NotificationText="##NOTIFY##";
	private String typeIntentData = null;
	private String fromIntentData = null;
	private String msgIntentData = null;
    static ProgressDialog progDialog;
    
	public static String[] ChatAvailUserName=null;
	public static String[] ChatAvailEmailID=null;
	public static boolean ChatInFront=false;
	public static boolean HomeInFront=false;
	NotificationManager myNotificationManager = null;
	private static final int NOTIFICATION_HOMESCREEN = 10;
	private static final int NOTIFICATION_NEW_CHAT_ID = 11;
	private static final int NOTIFICATION_INIT_CHAT_ID = 12;

	public static XMPPConnection connection;
	public static Handler mHandler = new Handler();
	Message message;
	String MsgFrom, MsgText;
	static PacketFilter filter;

	public static MediaPlayer sound_screenchange;
	public static MediaPlayer sound_buttonclick;
	public static MediaPlayer sound_type;
	public static MediaPlayer sound_chatMsg;
	public static MediaPlayer sound_chatRequest;
	public static MediaPlayer sound_notification;
	public static MediaPlayer sound_error;
	public static MediaPlayer sound_success;
	public static Typeface CurvedFont;
	public void InitSound() {
		sound_chatMsg = MediaPlayer.create(getApplicationContext(),
				R.raw.myteam_chat_msg);
		sound_chatRequest = MediaPlayer.create(getApplicationContext(),
				R.raw.myteam_chat_request);
		sound_notification = MediaPlayer.create(getApplicationContext(),
				R.raw.myteam_notification_sound);
		sound_error = MediaPlayer.create(getApplicationContext(),
				R.raw.myteam_error_sound2);
		sound_success = MediaPlayer.create(getApplicationContext(),
				R.raw.myteam_success_msg);
		sound_screenchange = MediaPlayer.create(getApplicationContext(),
				R.raw.myteam_screen_change);
		sound_buttonclick = MediaPlayer.create(getApplicationContext(),
				R.raw.myteam_click1);
		sound_type = MediaPlayer.create(getApplicationContext(),
				R.raw.myteam_typying);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		InitSound();
	}

	public String DoLogin(String user,String pass) {
        if(EstablishConnection()){
        	Log.d("DoLogin","Connection Established");
        	if(LoginChat(user, pass)){
        		return "DONE";
        	}
        	else{
        		return "WRONG";
        	}
        }else{
        	return "ERROR";
        }
	}

    private boolean EstablishConnection(){
        String host = "talk.google.com";
        String port = "5222";
        String service = "gmail.com";
        // Create a connection
        ConnectionConfiguration connConfig =
                new ConnectionConfiguration(host, Integer.parseInt(port), service);
        connection = new XMPPConnection(connConfig);
        try {
            connection.connect();
            Log.i("XMPPClient", "[SettingsDialog] Connected to " + connection.getHost());
            return true;
        } catch (XMPPException ex) {
            Log.e("XMPPClient", "[SettingsDialog] Failed to connect to " + connection.getHost());
            Log.e("XMPPClient", ex.toString());
            return false;
        }
}

private boolean LoginChat(String userid,String pass)
{
        try {
            connection.login(userid, pass);
            Log.i("XMPPClient", "Logged in as " + connection.getUser());
            // Set the status to available
            Presence presence = new Presence(Presence.Type.available);
            connection.sendPacket(presence);
            setConnection(connection);
            return true;
        } catch (XMPPException ex) {
            Log.e("XMPPClient", "[SettingsDialog] Failed to log in as " + userid);
            Log.e("XMPPClient", ex.toString());
            return false;
        }
}
	
	public void setConnection(XMPPConnection connection) {
		MyTeam.connection = connection;
		if (connection != null) {
			// Add a packet listener to get messages sent to us
			PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
			connection.addPacketListener(new PacketListener() {
				public void processPacket(Packet packet) {
					Message message = (Message) packet;
					if (message.getBody() != null) {
						String fromName = StringUtils.parseBareAddress(message
								.getFrom());
						MsgFrom = fromName;
						MsgText = message.getBody();
						mHandler.post(new Runnable() {
							public void run() {
								try {
									if (MsgText.substring(0, 10).equals(
											NotificationText)) {
										if (HomeInFront) {
											sound_notification.start();
											homescreen.RefreshNotifications();
										} else {
											GenerateHomeScreenNotification(
													"NEW NOTIFICATION",
													NOTIFICATION_HOMESCREEN);
											Toast.makeText(
													currentContext,
													"NEW NOTIFICATION FROM: "
															+ MsgText
																	.substring(20)
															+ "\nPRESS REFRESH...!",
													Toast.LENGTH_LONG).show();
										}
									} else {
										if (chatscreen != null) {
											if (ChatInFront) {
												
												chatscreen.FilterInComingMsg(
														MsgText.substring(0, 8),
														MsgFrom,
														MsgText.substring(
																8,
																MsgText.length() - 8));
											} else {
												GenerateChatNotification(
														"NEW CHAT MSG",
														NOTIFICATION_NEW_CHAT_ID);
												chatscreen.FilterInComingMsg(
														MsgText.substring(0, 8),
														MsgFrom,
														MsgText.substring(
																8,
																MsgText.length() - 8));
											}
										} else {
											NotifyInitializeChat(
													MsgText.substring(0, 8),
													MsgFrom,
													MsgText.substring(
															8,
															MsgText.length() - 8));
										}
									}
								} catch (Exception ex) {
								}
							}
						});
					}
				}
			}, filter);
		}
	}

	protected void NotifyInitializeChat(String type, String from, String msg) {
		BasicDetails.setParseChatIntent(true);
		myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		CharSequence NotificationTicket = new Date(System.currentTimeMillis())
				.toLocaleString();
		long when = System.currentTimeMillis();
		Notification notification = new Notification(
				R.drawable.chat_icon1_selected, NotificationTicket, when);
		Intent notificationIntent = new Intent(this, ChatScreen.class);
		StoreIntentData(type, from, msg);
		PendingIntent contentIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, notificationIntent, 0);
		notification.setLatestEventInfo(getApplicationContext(), "CHAT",
				"START CHATTING", contentIntent);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		myNotificationManager.notify(NOTIFICATION_INIT_CHAT_ID, notification);
		sound_notification.start();
		VibrateDevice();
	}

	public void SendXMPPmsg(String msgTO, String MsgBody) {
		try{
		Message msg = new Message(msgTO, Message.Type.chat);
		msg.setBody(MsgBody);
		connection.sendPacket(msg);
		}catch(Exception ex){
			Toast.makeText(getApplicationContext(), "CONNECTION LOST..\nPlease Restart Application!", Toast.LENGTH_LONG).show();
		}
	}

	public void GenerateChatNotification(CharSequence NotificationContent,
			int ID) {
		myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		CharSequence NotificationTicket = new Date(System.currentTimeMillis())
				.toLocaleString();
		long when = System.currentTimeMillis();
		Notification notification = new Notification(
				R.drawable.ic_launcher, NotificationTicket, when);
		Intent notificationIntent = new Intent(this, ChatScreen.class);
		PendingIntent contentIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, notificationIntent, 0);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		notification.setLatestEventInfo(getApplicationContext(), "CHAT",
				NotificationContent, contentIntent);
		myNotificationManager.notify(ID, notification);
		sound_notification.start();
		VibrateDevice();
	}

	public void GenerateHomeScreenNotification(CharSequence NotificationContent,
			int ID) {
		myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		CharSequence NotificationTicket = new Date(System.currentTimeMillis())
				.toLocaleString();
		long when = System.currentTimeMillis();
		Notification notification = new Notification(
				R.drawable.ic_launcher, NotificationTicket, when);
		Intent notificationIntent = new Intent(this, HomeScreen.class);
		PendingIntent contentIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, notificationIntent, 0);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		notification.setLatestEventInfo(getApplicationContext(), "CHAT",
				NotificationContent, contentIntent);
		myNotificationManager.notify(ID, notification);
		sound_notification.start();
		VibrateDevice();
	}

	
//	public String[] friendslist() {
//		String[][] TeamChatList=GetTeamList();
//		Roster roster = connection.getRoster();
//		Collection<RosterEntry> entries = roster.getEntries();
//		System.out.println("\n\n" + entries.size() + " buddy(ies):");
//		String name[] = new String[entries.size()];
//		String email[] = new String[entries.size()];
//		int i = 0;
//		for (RosterEntry r : entries) {
//			for(int x=0;x<TeamChatList[1].length;x++){
//				if(TeamChatList[1][x].equals(r.getUser())){
////					Presence presence = roster.getPresence(r.getUser());
////					if (presence.getType() == Presence.Type.available) {
////					   // Tom is online...
////					}
//					email[i] = TeamChatList[x][0];
//					name[i++] = r.getUser();
//				}
//			}
//		}
//		ChatAvailUserName=new String[i];
//		ChatAvailUserName=name;
//		ChatAvailEmailID=new String[i];
//		ChatAvailEmailID=email;
//		return ChatAvailUserName;
//	}

public boolean CheckIfPresent(String email){
	try{
	Roster roster = connection.getRoster();
	Presence presence = roster.getPresence(email);
	if (presence.getType() == Presence.Type.available) 
		return true;
	return false;
	}catch(Exception ex){
		return false;
	}
}
	
	
	private void StoreIntentData(String type, String from, String msg) {
		typeIntentData = type;
		fromIntentData = from;
		msgIntentData = msg;
	}

	public void CallIntentData() {
		chatscreen.FilterInComingMsg(typeIntentData, fromIntentData,
				msgIntentData);
	}

	public void UnKnowChatMsg(String msg) {
		Toast.makeText(getApplicationContext(), "UNKNOWN CHAT MSG:\n" + msg,
				Toast.LENGTH_LONG).show();
	}

	public void LogOut() {

		
			if (chatscreen != null){
				chatscreen.DeleteAllChat();
				chatscreen.finish();
			}
			try {
			if (connection != null && connection.isConnected()) {
				Toast.makeText(currentContext, "Logging Out", Toast.LENGTH_LONG)
						.show();
				connection.disconnect();
			}
			} catch (Exception ex) {
			}
			if (homescreen != null)
				homescreen.finish();
			if (moreSetting != null)
				moreSetting.finish();
			if (teamlist != null)
				teamlist.finish();
			if (tasklist != null)
				tasklist.finish();
			if (profiledetails != null)
				profiledetails.finish();
			
		BasicDetails.clearBasicDetails();
//		
		onTerminate();
	}

	public void CancelNotification(int id) {
		myNotificationManager.cancel(id);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
//		Intent intent = new Intent(Intent.ACTION_MAIN);
//		intent.addCategory(Intent.CATEGORY_HOME);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(intent);
		System.exit(0);
	}


	public void VibrateDevice(){
		if(MyTeamSettings.isDoVibrate()){
			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(300);
		}

	}
	
	public void PlayButtonSound(){
		if(MyTeamSettings.isPlaySound())
			sound_buttonclick.start();
	}
	public static TextWatcher EditTextSound = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			if(MyTeamSettings.isPlaySound())
				sound_type.start();
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
		}

		@Override
		public void afterTextChanged(Editable arg0) {
		}
	};

	public static String BitmapToString(Bitmap image)
	{
	    Bitmap immagex=image;
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	    immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
	    byte[] b = baos.toByteArray();
	    String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);

	    Log.e("LOOK", imageEncoded);
	    return imageEncoded;
	}
	
	public static Bitmap StringToBitmap(String imgString) {
		Bitmap bmp = null;
		try{

			if (imgString == null || imgString.equalsIgnoreCase("NOT SET")
					|| imgString.equalsIgnoreCase("NULL")) {
				Log.d("StringToBitmap", "IF PART");
				bmp = BitmapFactory.decodeResource(
						currentContext.getResources(),
						R.drawable.default_profile_pic);
			} else {
				Log.d("IMAGE CONVERT", "I M HERE...");
				Log.d("StringToBitmap", "ELSE PART");
				byte[] decodedString = Base64.decode(imgString, Base64.DEFAULT);
				Bitmap decodedByte = BitmapFactory.decodeByteArray(
						decodedString, 0, decodedString.length);
				bmp = decodedByte;
			}
		}catch(Exception ex){
			bmp = BitmapFactory.decodeResource(currentContext.getResources(),R.drawable.default_profile_pic);
			Log.d("PROFILE PIC", "ERROR: "+ex.toString());
		}
		return bmp;
	}

	public boolean IsNetworkConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) currentContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		boolean isConnectedToNetwork = (networkInfo != null && networkInfo
				.isConnected());
		return isConnectedToNetwork;
	}

	public boolean IsInternetConnected() {
		boolean isFound = false;
		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conMgr.getActiveNetworkInfo() != null
				&& conMgr.getActiveNetworkInfo().isAvailable()
				&& conMgr.getActiveNetworkInfo().isConnected()) {
			Log.d("Internet Connection  Present", "");
			isFound = true;
		} else {
			Log.d("Internet Connection Not Present", "");
			isFound = false;
		}
		return isFound;
	}
	public void MsgSound(){
		if(MyTeamSettings.isPlaySound())
			sound_chatMsg.start();
	}
	

}