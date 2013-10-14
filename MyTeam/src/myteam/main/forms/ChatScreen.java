package myteam.main.forms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import myteam.global.constants.BasicDetails;
import myteam.global.constants.ParseXmlData;
import myteam.global.constants.URL_Strings;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatScreen extends Activity {
	private String[] FriendsName=null;
	private String[] DisplayName=null;
	private String[] FriendsEmail=null;
	private final String NEW_CHAT_MSG = "##1111##";
	private final String NEW_CHAT_REQUEST = "##1119##";
	private final String NEW_CHAT_ACCEPTED = "##1118##";
	private final String NEW_CHAT_REJECTED = "##1117##";
	private final String CLOSE_CHAT_MSG = "##1116##";
	private final int CHAT_MSG = 1111;
	private final int CHAT_REQUEST = 1119;
	private final int CHAT_ACCEPTED = 1118;
	private final int CHAT_REJECTED = 1117;
	private final int CHAT_CLOSE = 1116;
	int TempType = 0;
	LinearLayout ArrayChat[] = new LinearLayout[20];
	private final int START_NEW_CHAT = 2;
	private final int ACCEPT_NEW_CHAT = 3;
	String RECEIVED_REQUEST_NAME;
	String RECEIVED_REQUEST_NUMBER;
	String RECEIVED_MESSAGE_BODY;
	String RECEIVED_MESSAGE_TIMING;

	Dialog DIALOG_UR_NAME;
	Dialog DIALOG_NEW_CHAT;
	Dialog DIALOG_ACCEPT_CHAT;
	boolean FirstUse = true;
	String MY_CHAT_NAME = BasicDetails.getUserName();

	String RcvEmailID[] = new String[20];
	String RcvUserName[] = new String[20];
	Button LabelBtn[] = new Button[20];
	LinearLayout btnLayout;
	FrameLayout frame;
	EditText MsgBody;
	Button SendChatBtn;
	ScrollView MainScroll;
	HorizontalScrollView hs=null;
	TextView status;
	boolean IsNewEmail;
	int MAX_COUNT, CURRENT_COUNT, TEMP_COUNT;
	private Dialog DialogListMember;

	private AlertDialog DIALOG_SHOW_MSG;
	private Animation mAnimation;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_Light);
		setContentView(R.layout.main_layout);
		((MyTeam) getApplication()).chatscreen = this;
		MyTeam.currentContext = this;
		//TODO
		mAnimation = new AlphaAnimation(1, 0);
	    mAnimation.setDuration(800);
	    mAnimation.setInterpolator(new LinearInterpolator());
	    mAnimation.setRepeatCount(Animation.INFINITE);
	    mAnimation.setRepeatMode(Animation.REVERSE); 
		try {
			((MyTeam) getApplication()).CancelNotification(11);
		} catch (Exception ex) {}
		
		if(!BasicDetails.isChatPageInit()){
			InitialzeComponents();
			MAX_COUNT=0;
			BasicDetails.setChatPageInit(true);
			}
		if (BasicDetails.isParseChatIntent()) {
			BasicDetails.setParseChatIntent(false);
			((MyTeam) getApplication()).CancelNotification(12);
			((MyTeam) getApplication()).CallIntentData();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyTeam.ChatInFront=true;
		try {
			((MyTeam) getApplication()).CancelNotification(11);
		} catch (Exception ex) {
		}
	}


	@Override
	protected void onRestart() {
		super.onRestart();
		MyTeam.ChatInFront=true;
	}
	@Override
	protected void onPause() {
		super.onPause();
		MyTeam.ChatInFront=true;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		MyTeam.ChatInFront=false;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyTeam.ChatInFront=false;
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	protected void FilterOutGoingMsg(int type, String Name, String email,
			String TxtMsg) {
		switch (type) {
		case CHAT_MSG:
			RECEIVED_MESSAGE_BODY = TxtMsg;
			RECEIVED_MESSAGE_TIMING = GetCurrentTime();
			TempType = 0;
			AddNewChatMsg(CURRENT_COUNT);
			PutThisView(CURRENT_COUNT);
			SendTEXT(email, NEW_CHAT_MSG + TxtMsg + NEW_CHAT_MSG);
			break;
		case CHAT_REQUEST:
			SendTEXT(email, NEW_CHAT_REQUEST + MY_CHAT_NAME + NEW_CHAT_REQUEST);
			break;
		case CHAT_ACCEPTED:
			SendTEXT(email, NEW_CHAT_ACCEPTED + MY_CHAT_NAME
					+ NEW_CHAT_ACCEPTED);
			break;
		case CHAT_REJECTED:
			SendTEXT(email, NEW_CHAT_REJECTED + MY_CHAT_NAME
					+ NEW_CHAT_REJECTED);
			break;
		case CHAT_CLOSE:
			SendTEXT(email, CLOSE_CHAT_MSG + MY_CHAT_NAME + CLOSE_CHAT_MSG);
			break;
		}
	}

	private void SendTEXT(String email, String msg) {
		((MyTeam) getApplication()).SendXMPPmsg(email, msg);
	}

	protected boolean FilterInComingMsg(String type, String SenderEmail,
			String MainMsg) {
		
		boolean SkipMsg = false;
		switch (GetMsgType(type)) {
		case CHAT_MSG:
			((MyTeam)getApplication()).MsgSound();
			((MyTeam)getApplication()).VibrateDevice();
			TEMP_COUNT = GetCurrentCount(SenderEmail);
			if (TEMP_COUNT == -1) {
				Toast.makeText(getApplicationContext(),
						"KHATARNAK ERROR HAI BE....!", Toast.LENGTH_LONG)
						.show();
			} else {
				RECEIVED_MESSAGE_BODY = MainMsg;
				RECEIVED_MESSAGE_TIMING = GetCurrentTime();
				TempType = 1;
//					LabelBtn[TEMP_COUNT].setTextColor();//TODO
				if(CURRENT_COUNT!=TEMP_COUNT)
					LabelBtn[TEMP_COUNT].startAnimation(mAnimation);
				AddNewChatMsg(TEMP_COUNT);
			}
			break;
		case CHAT_REQUEST:
			MyTeam.sound_notification.start();
			((MyTeam)getApplication()).VibrateDevice();
			RECEIVED_REQUEST_NAME = MainMsg;
			RECEIVED_REQUEST_NUMBER = SenderEmail;
			showDialog(ACCEPT_NEW_CHAT);
			break;
		case CHAT_ACCEPTED:
			((MyTeam)getApplication()).MsgSound();
			((MyTeam)getApplication()).VibrateDevice();
			AddNewTab(MAX_COUNT, MainMsg, SenderEmail);
			MAX_COUNT++;
			break;
		case CHAT_REJECTED:
			((MyTeam)getApplication()).MsgSound();
			((MyTeam)getApplication()).VibrateDevice();
			Toast.makeText(getApplicationContext(),
					MainMsg + " REJECTED your Chat Request", Toast.LENGTH_LONG)
					.show();
			break;
		case CHAT_CLOSE:
			((MyTeam)getApplication()).MsgSound();
			((MyTeam)getApplication()).VibrateDevice();
			Toast.makeText(getApplicationContext(),
					MainMsg + " has STOPPED chatting with YOU.",
					Toast.LENGTH_LONG).show();
			TEMP_COUNT = GetCurrentCount(SenderEmail);
			if (TEMP_COUNT == -1) {
				Toast.makeText(getApplicationContext(),
						"KHATARNAK ERROR HAI BE....!", Toast.LENGTH_LONG)
						.show();
			} else {
				DeleteThisMember(TEMP_COUNT);
			}
			break;
		default:
			Toast.makeText(getApplicationContext(), "UNKNOWN",
					Toast.LENGTH_LONG).show();
		}
		return SkipMsg;
	}

	private int GetMsgType(String type) {
		if (type.equals(NEW_CHAT_MSG))
			return CHAT_MSG;
		if (type.equals(NEW_CHAT_REQUEST))
			return CHAT_REQUEST;
		if (type.equals(NEW_CHAT_ACCEPTED))
			return CHAT_ACCEPTED;
		if (type.equals(NEW_CHAT_REJECTED))
			return CHAT_REJECTED;
		if (type.equals(CLOSE_CHAT_MSG))
			return CHAT_CLOSE;
		return 0;
	}

	protected void InitialzeComponents() {
		btnLayout = (LinearLayout) findViewById(R.id.ML_btn_LinearLayout);
		MsgBody = (EditText) findViewById(R.id.ML_msgbodytxt);
		MsgBody.addTextChangedListener(MyTeam.EditTextSound);
		SendChatBtn = (Button) findViewById(R.id.ML_sendchatbtn);
		SendChatBtn.requestFocus();////TODO ADITIONAL
		frame = (FrameLayout) findViewById(R.id.ML_chatframelayout);
		MainScroll = (ScrollView) findViewById(R.id.ML_chatscrollView);
		status = (TextView) findViewById(R.id.ML_StatusTextView);
		SendChatBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((MyTeam)getApplication()).PlayButtonSound();
				if (MAX_COUNT == 0) {
					Toast.makeText(getApplicationContext(),
							"No Receiver Added", Toast.LENGTH_LONG).show();
				} else {
					if(MsgBody.getText().toString().trim().length()!=0){
					FilterOutGoingMsg(CHAT_MSG, RcvUserName[CURRENT_COUNT],
							RcvEmailID[CURRENT_COUNT], MsgBody.getText()
									.toString());
					}
					MsgBody.setText("");
				}
				onBackPressed();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater i = getMenuInflater();
		i.inflate(R.menu.chat_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem i) {
		switch (i.getItemId()) {
		case R.id.chat_memberList:
			//showDialog(START_NEW_CHAT);// Need to change
			GetTeamList();
			break;
		case R.id.chat_closethischat:
			FilterOutGoingMsg(CHAT_CLOSE, RcvUserName[CURRENT_COUNT],
					RcvEmailID[CURRENT_COUNT], MY_CHAT_NAME);
			DeleteThisMember(CURRENT_COUNT);
			break;
		case R.id.chat_exitchat:
			DeleteAllChat();
			BasicDetails.setChatPageInit(false);
			finish();
			Intent homeIntent=new Intent(getApplicationContext(),
					HomeScreen.class);
			homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(homeIntent, 110);
			break;
		case R.id.chat_home:
			Intent homeIntent1=new Intent(getApplicationContext(),
					HomeScreen.class);
			homeIntent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(homeIntent1, 110);
			break;
		}
		return false;
	}

	protected void DeleteAllChat() {
		int MaxLoop = MAX_COUNT;
		try {
			for (int x = 0; x < MaxLoop; x++) {
				FilterOutGoingMsg(CHAT_CLOSE, RcvUserName[MAX_COUNT - 1],
						RcvEmailID[MAX_COUNT - 1], MY_CHAT_NAME);
				DeleteThisMember(MAX_COUNT - 1);
			}
		} catch (Exception ex) {
		}		
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {

		case START_NEW_CHAT:
			LayoutInflater factory1 = LayoutInflater.from(this);
			final View textEntryView1 = factory1.inflate(
					R.layout.new_chat_number, null);
			DIALOG_NEW_CHAT = new AlertDialog.Builder(ChatScreen.this)
					.setTitle("New Chat Detalis...")
					.setView(textEntryView1)
					.setPositiveButton("Start Chat",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									EditText Tempemail = (EditText) DIALOG_NEW_CHAT
											.findViewById(R.id.Number_txt_view);
									Tempemail
											.addTextChangedListener(MyTeam.EditTextSound);
									if (Tempemail.getText().toString() == null) {
										Tempemail.setError("Enter Name..");
									} else {
										IsNewEmail = true;
										for (int i = 0; i < MAX_COUNT - 1; i++) {
											if ((Tempemail.getText())
													.equals(RcvEmailID[i]))
												IsNewEmail = false;

										}
										if (IsNewEmail) {
											FilterOutGoingMsg(CHAT_REQUEST,
													null, Tempemail.getText()
															.toString(), null);

										} else
											Toast.makeText(
													getApplicationContext(),
													Tempemail.getText()
															+ " is AVAILABLE in the CHAT MEMBER'S",
													Toast.LENGTH_LONG).show();
										Tempemail.setText("");
									}
								}
							})
					.setNegativeButton("Go Back",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).create();
			return DIALOG_NEW_CHAT;
		case ACCEPT_NEW_CHAT:
			DIALOG_ACCEPT_CHAT = new AlertDialog.Builder(ChatScreen.this)
					.setTitle("New Chat Detalis...")
					.setMessage(
							"Name: " + RECEIVED_REQUEST_NAME + "\nNumber :"
									+ RECEIVED_REQUEST_NUMBER
									+ "\n Do you wish to ACCEPT ?")
					.setPositiveButton("Start Chat",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									AddNewTab(MAX_COUNT, RECEIVED_REQUEST_NAME,
											RECEIVED_REQUEST_NUMBER);
									MAX_COUNT++;
									FilterOutGoingMsg(CHAT_ACCEPTED,
											RECEIVED_REQUEST_NAME,
											RECEIVED_REQUEST_NUMBER,
											MY_CHAT_NAME);
								}
							})
					.setNegativeButton("Go Back",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									FilterOutGoingMsg(CHAT_REJECTED,
											RECEIVED_REQUEST_NAME,
											RECEIVED_REQUEST_NUMBER,
											MY_CHAT_NAME);
								}
							}).create();
			return DIALOG_ACCEPT_CHAT;
		}
		return null;
	}
	
	
	
	protected void ChatListMember() {
		try{
		DialogListMember = new AlertDialog.Builder(ChatScreen.this)
		.setTitle("Start Chatting:")
		.setSingleChoiceItems(DisplayName, 0,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0,
							int arg1) {
						Log.d("Clicked",arg1+"");
						IsNewEmail = true;
						for (int i = 0; i < MAX_COUNT; i++) {
							if (FriendsEmail[arg1].equalsIgnoreCase(RcvEmailID[i])){
								IsNewEmail = false;
								break;
							}
						}
						if (IsNewEmail) {
							FilterOutGoingMsg(CHAT_REQUEST,
									null, FriendsEmail[arg1], null);

						} else
							Toast.makeText(
									getApplicationContext(),
									FriendsEmail[arg1]
											+ " is AVAILABLE in the CHAT MEMBER'S",
									Toast.LENGTH_LONG).show();
//						FilterOutGoingMsg(CHAT_REQUEST,
//								null, FriendsEmail[arg1], null);
//						Toast.makeText(getApplicationContext(), "CHAT REQUEST SENT..", Toast.LENGTH_LONG).show();
						DialogListMember.dismiss();
					}
				})
		.setNegativeButton("Back",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						DialogListMember.dismiss();
					}
				}).create();
		DialogListMember.show();
		}catch(Exception ex){
			Log.d("Reached Here11",ex.toString());
		}
	}

	private void GetTeamList() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("GroupID",BasicDetails.getLeaderID()));
		nameValuePairs.add(new BasicNameValuePair("MemberID",BasicDetails.getCurrentID()));
		new PostToServer(nameValuePairs,URL_Strings.getUrlChatList(),"LOADING MEMBERS LIST..").execute();
	}
	
	private void AfterServerPost(String xmlData){	
		int tmp=0;
		ParseXmlData parser=new ParseXmlData();
		Document doc = parser.getDomElement(xmlData);
		NodeList nl = doc.getElementsByTagName("values");
		FriendsName = new String[nl.getLength()];
		FriendsEmail=new String[nl.getLength()];
		DisplayName=new String[nl.getLength()];
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			if(((MyTeam)getApplication()).CheckIfPresent(parser.getValue(e, "EmailID")))
				DisplayName[tmp]=parser.getValue(e, "Name") + "(AVAILABLE)";
			else
				DisplayName[tmp]=parser.getValue(e, "Name") + "(UNAVAILABLE)";
				FriendsName[tmp]=parser.getValue(e, "Name");
				FriendsEmail[tmp++]=parser.getValue(e, "EmailID");
		}
		ChatListMember();
	}
	
	public void ShowThisMsg(String title, String message, final int type) {
		DIALOG_SHOW_MSG = new AlertDialog.Builder(ChatScreen.this)
				.setIcon(R.drawable.ic_launcher).setTitle(title)
				.setMessage(message)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						((MyTeam)getApplication()).PlayButtonSound();
					}
				}).create();
		DIALOG_SHOW_MSG.show();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////
//	protected void DeleteThisMember(int count) {
//		if (MAX_COUNT != 0) {
//			for (int i = count; i < MAX_COUNT-1; i++) {//I HAVE CHANGED tHE LOOP...please check...
//				ArrayChat[i] = ArrayChat[i + 1];
//				LabelBtn[i] = LabelBtn[i + 1];
//				LabelBtnStatus[i] = LabelBtnStatus[i + 1];
//				RcvUserName[i] = RcvUserName[i + 1];
//				RcvEmailID[i] = RcvEmailID[i + 1];
//				PutThisView(i);
//			}
//			try{
//			ArrayChat[MAX_COUNT - 1].setVisibility(8);
//			LabelBtn[MAX_COUNT - 1].setVisibility(8);// //////////////////////88888888888888888888888888888
//			MAX_COUNT--;
//			if (MAX_COUNT == 0) {
//				status.setText("No Member's Added...");
//			}
//			CURRENT_COUNT = MAX_COUNT - 1;
//			PutThisView(CURRENT_COUNT);
//			}catch(Exception ex){
//				Log.d("DeleteThisMember", ex.toString());
//			}
//		} else {
//			Toast.makeText(getApplicationContext(), "No Member's Available..",
//					Toast.LENGTH_LONG).show();
//		}
//	}

	protected void DeleteThisMember(int count){
		if(MAX_COUNT!=0){
			for(int i=count;i<MAX_COUNT-1;i++){
				ArrayChat[i]=ArrayChat[i+1];
				LabelBtn[i]=LabelBtn[i+1];
				RcvUserName[i]=RcvUserName[i+1];
				RcvEmailID[i]=RcvEmailID[i+1];
				PutThisView(i);
			}
			ArrayChat[MAX_COUNT-1].setVisibility(8);
			LabelBtn[MAX_COUNT-1].setVisibility(8);////////////////////////88888888888888888888888888888
			MAX_COUNT--;
			if(MAX_COUNT==0){
				status.setText("No Member's Added...");
			}
			CURRENT_COUNT=MAX_COUNT-1;
		}
		else{
			Toast.makeText(getApplicationContext(), "No Member's Available..", Toast.LENGTH_LONG).show();
		}
	}
	
	// NOT NECESSARY may be...
	protected boolean CheckIfPresent(int x) {
		if (RcvUserName[x].equalsIgnoreCase(MsgBody.getText().toString()))
			return true;
		return false;
	}

	protected void AddNewTab(final int cnt, String name, String numbr) {
		RcvUserName[cnt] = name;
		RcvEmailID[cnt] = numbr;
		LabelBtn[cnt] = AddNewLabelButton(name);
		btnLayout.addView(LabelBtn[cnt]);
		ArrayChat[cnt] = AddNewLinearLayout();
		PutThisView(cnt);
		CURRENT_COUNT = cnt;
		LabelBtn[cnt].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((MyTeam)getApplication()).PlayButtonSound();
				CURRENT_COUNT = cnt;
				PutThisView(CURRENT_COUNT);
				try{
					LabelBtn[CURRENT_COUNT].clearAnimation();//TODO
				}catch(Exception ex){}
			}
		});
	}

	private void PutThisView(int cnt) {
		frame.removeAllViews();
		frame.addView(ArrayChat[cnt]);
		ArrayChat[cnt].setOverScrollMode(1);// ...........Extra MAY BE
		MainScroll.scrollTo(0, MainScroll.getBottom());
		for(int i=0;i<MAX_COUNT;i++){					//
			LabelBtn[i].setSelected(false);				//
			LabelBtn[i].setEnabled(true);
		}		
		LabelBtn[cnt].setSelected(true);				//TODO
		LabelBtn[cnt].setEnabled(false);
		status.setText(RcvUserName[cnt]);
	}
	
	
	private LinearLayout AddNewLinearLayout() {
		LinearLayout ll = new LinearLayout(getApplicationContext());
		ll.setOrientation(1);
		ll.setVisibility(0);
		ll.setId(1000 + MAX_COUNT);
		return ll;
	}

	protected Button AddNewLabelButton(String name) {
		Button LabelButton=new Button(getApplicationContext());
		LabelButton.setId(MAX_COUNT);
		LabelButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_bk_theme));
		LabelButton.setText(R.color.White);
		LabelButton.setText(name);
		LabelButton.setVisibility(0);
		return LabelButton;
	}

	protected String GetCurrentTime() {
		final Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH);
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		int mHour = c.get(Calendar.HOUR_OF_DAY);
		int mMinute = c.get(Calendar.MINUTE);
		return "[" + mDay + " / " + mMonth + " / " + mYear + " ]  [ " + mHour
				+ " : " + mMinute + " ] ";
	}

	private int GetCurrentCount(String senderNo) {
		int i;
		for (i = 0; i < 20; i++)
			if (RcvEmailID[i].equals(senderNo))
				return i;
		return -1;
	}

	protected void AddNewChatMsg(int count) {
		ArrayChat[count].addView(GetdNewChatMsg());
		ArrayChat[count].setOverScrollMode(1);// ///////.................XTRA
												// SURE
		MainScroll.scrollTo(0, MainScroll.getBottom());
	}

	protected View GetdNewChatMsg() {
		View convertView = null;
		LayoutInflater minflate = LayoutInflater.from(getApplicationContext());
		if (TempType == 0)
			convertView = minflate.inflate(R.layout.chat_msg_layout, null);
		else
			convertView = minflate.inflate(R.layout.in_chat_layout, null);
		TextView Msgtext = (TextView) convertView.findViewById(R.id.MsgTxtView);
		TextView Timetext = (TextView) convertView
				.findViewById(R.id.TimeTxtView);
		Msgtext.setText(RECEIVED_MESSAGE_BODY);
		Timetext.setText(RECEIVED_MESSAGE_TIMING);

		return convertView;
	}
	
	@Override
	public void onBackPressed() {
		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
			
	}

	
	class PostToServer extends AsyncTask<String, Void, Boolean>{
		private String ERROR =null;
		ProgressDialog PG =null;
		private String RESULT_STRING;
		String URL;
		List<NameValuePair> nameValuePairs;
		String Msg;
		
		public PostToServer(List<NameValuePair> nameValuePairs,String URL,String Msg) {
			this.URL=URL;
			this.nameValuePairs=nameValuePairs;
			this.Msg=Msg;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			RESULT_STRING = null;
			PG = new ProgressDialog(ChatScreen.this);
			PG.setCancelable(false);
			PG.setMessage(Msg);
			PG.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			PG.show();
		}

		@Override
		protected Boolean doInBackground(String... arg0) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(URL);
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity httpEntity= response.getEntity();
				RESULT_STRING=EntityUtils.toString(httpEntity);
				return true;
			} catch (Exception ex) {
				ex.printStackTrace();
				ERROR = ex.getMessage();
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(PG != null){ if(PG.isShowing()){ PG.dismiss();}}
			if(result){
				AfterServerPost(RESULT_STRING);
			}else{
				Toast.makeText(getApplicationContext(), "Error:"+ERROR, Toast.LENGTH_LONG).show();
			}
		}
	}

	
	
}
