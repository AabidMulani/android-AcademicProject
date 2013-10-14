package myteam.main.forms;

import java.util.ArrayList;
import java.util.List;

import myteam.global.constants.BasicDetails;
import myteam.global.constants.Credits;
import myteam.global.constants.Help;
import myteam.global.constants.NotificationData;
import myteam.global.constants.ParseXmlData;
import myteam.global.constants.ServerLabels;
import myteam.global.constants.URL_Strings;
import myteam.startup.pages.LoginPage;
import myteam.task.insert.AddTaskPage;
import myteam.task.view.ViewSelectedTask;
import myteam.team.management.JoinGroup;
import myteam.team.management.ProfileDetails;

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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class HomeScreen extends Activity {
	private final int LOGOUT_APPLICATION = 2;
	private final int SET_EMAIL = 3;
	private final int GROUP_JOIN = 4;
	public TextView InnerHeader=null;
	private Button ChatButton = null;
	private Button MenuHome = null;
	private Button MenuTeam = null;
	private Button MenuTask = null;
	private Button MenuMore = null;
	private Button AddTask = null;
	private TextView Welcome_Msg = null;
	private TextView Type_Msg = null;
	private AlertDialog DIALOG_SHOW_MSG;
	private String xmlData1 = null;
	private Document doc = null;
	private ParseXmlData xmlParser = new ParseXmlData();
	private NodeList nl = null;
	private Element e = null;
	private LinearLayout notificationBoard = null;
	public int temp = 0;
	final int NORMAL_CHECK=0;
	final int REFRESH_TYPE=1;
	final int JOIN_GROUP_TYPE=2;
	final int ADD_SCORE_TYPE=3;
	final int CHECK_GROUP_TYPE=4;
	final int CHECK_IF_REQUEST_TYPE=5;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyTeam.HomeInFront=true;
		((MyTeam) getApplication()).homescreen = this;
		MyTeam.currentContext=this;
		if (!BasicDetails.isLoginDone()){
			CheckEmailDetails();
		}
		if (BasicDetails.isLoginDone()){
				ProceedFurther();
		}
	}

	private void ProceedFurther() {
		CheckGroupDetails();
		setContentView(R.layout.home_screen);
		MenuLayoutInitialization();
		Welcome_Msg = (TextView) findViewById(R.id.HS_Header_show_Welcome);
		Type_Msg = (TextView) findViewById(R.id.HS_Header_showType);
		InnerHeader=(TextView) findViewById(R.id.HS_inner_header);
		Welcome_Msg.setText("Welcome, " + BasicDetails.getFirstName());
		Type_Msg.setText(BasicDetails.getTYPE());
		try {
			((MyTeam) getApplication()).CancelNotification(10);
		} catch (Exception ex) {}
		
		notificationBoard = (LinearLayout) findViewById(R.id.HS_inner_LinearLayout);
		AddTask = (Button) findViewById(R.id.HS_Add_newTask_Btn);
		if (BasicDetails.getTYPE().equals("MEMBER")) {
			AddTask.setVisibility(8);
		} else {
			AddTask.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					((MyTeam)getApplication()).PlayButtonSound();
					Intent addtask = new Intent(
							getApplicationContext(), AddTaskPage.class);
					startActivityForResult(addtask, 110);
				}
			});
		}
		
		RefreshNotifications();
		
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		MyTeam.HomeInFront=true;
		RefreshNotifications();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyTeam.HomeInFront=true;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		MyTeam.HomeInFront=false;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyTeam.HomeInFront=false;
	}
	
	private void CheckEmailDetails() {
		String pass = new BasicDetails().getEmailPass();
		if (BasicDetails.getEmailID().equals("NOT SET")
				&& (pass.equals("NOT SET"))) {
			ShowThisMsg(
					"LOGIN NOT SET:",
					"EMAIL DETAILS ARE NOT SET..\nPLEASE ENTER THE EMAIL DETAILS...",
					SET_EMAIL);
		} else {
			new GmailLoginThread(BasicDetails.getEmailID(), pass).execute();
		}
	}

	@Override
	public void onBackPressed() {
	}

	protected void RefreshNotifications() {
		
			// NotificationData.ClearAllNotification();
			notificationBoard.removeAllViews();
			InnerHeader.setTextColor(getResources().getColor(R.color.Yellow));
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair(ServerLabels
					.getFirstLabel(), ServerLabels.getRefreshNotification()));
			nameValuePairs.add(new BasicNameValuePair("UserID", BasicDetails
					.getCurrentID()));

			new PostToServer(nameValuePairs,URL_Strings.getUrlRefreshNotification(),"REFRESHING NOTIFICATION..",REFRESH_TYPE,"NULL").execute();
			// ShowThisMsg("DATA", xmlData, 0);
		}
		protected void AfterServerRefreshNotification(String xmlData){
			try {
			nl = doc.getElementsByTagName("values");
			if (nl.getLength() == 0) {
				notificationBoard.addView(NoNewNotification());
			} else {
				for (int i = 0; i < nl.getLength(); i++) {
					e = (Element) nl.item(i);
					temp = i;
					switch (Integer.parseInt(xmlParser.getValue(e, "CODE"))) {
					case NotificationData.TYPE_JOIN_GROUP:
						notificationBoard
								.addView(DisplayJOIN_GROUP_Notification(i,
										NotificationData.JOIN_GROUP_MSG));
						break;
					case NotificationData.TYPE_NEW_TASK:
						notificationBoard.addView(DisplayNEW_TASK_Notification(
								i, NotificationData.NEW_TASK_MSG));
						break;
					case NotificationData.TYPE_NEW_COMMENT:
						notificationBoard
								.addView(DisplayNEW_COMMENT_Notification(i,
										NotificationData.NEW_COMMENT_MSG));
						break;
					case NotificationData.TYPE_STATUS_UPDATE:
						notificationBoard
								.addView(DisplayTASK_UPDATE_Notification(i,
										NotificationData.STATUS_UPDATE_MSG));
						break;
					case NotificationData.TYPE_TASK_COMPLETE:
						notificationBoard
								.addView(DisplayTASK_COMPLETE_Notification(i,
										NotificationData.TASK_COMPLETE_MSG));
						break;
					}
				}
			}
		} catch (Exception ex) {
			Log.e(getClass().getName(), temp + "");
			Log.e(getClass().getName(), ex.toString());
		}
	}

	// NEW JOIN GROUP
	private View DisplayJOIN_GROUP_Notification(final int count, String Msg) {
		final String NotificationValue = xmlParser.getValue(e, "NOTIFICATIONID");
		final String CodeValue = xmlParser.getValue(e, "CODE");
		final String SenderValue = xmlParser.getValue(e, "SENDER");
		final String DateValue = xmlParser.getValue(e, "DATE");
		final String NameValue = xmlParser.getValue(e, "NAME");
		final String DesignValue = xmlParser.getValue(e, "DESIGNATION");
		final String MobValue = xmlParser.getValue(e, "MOBILENO");
		LayoutInflater minflate = LayoutInflater.from(getApplicationContext());
		final View convertView = minflate.inflate(
				R.layout.notification_inflater, null);
		TextView msgNote = (TextView) convertView
				.findViewById(R.id.Notification_Text);
		msgNote.setText(Msg);
		msgNote.setSelected(true);
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				JoinGroupDialog(NotificationValue, CodeValue, SenderValue,
						DateValue, NameValue, DesignValue, MobValue);
				notificationBoard.removeView(convertView);

			}
		});
		return convertView;
	}

	protected void JoinGroupDialog(final String notificationValue,
			final String codeValue, final String senderValue, String dateValue,
			String nameValue, String designValue, String mobValue) {
		String showMsg = "You have received a new Join Request from:\n"
				+ nameValue + "\n" + designValue + "\n" + mobValue
				+ "\n...plz respond!";
		DIALOG_SHOW_MSG = new AlertDialog.Builder(HomeScreen.this)
				.setIcon(R.drawable.ic_launcher)
				.setTitle("JOIN GROUP")
				.setMessage(showMsg)
				.setPositiveButton("ACCEPT",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								((MyTeam)getApplication()).PlayButtonSound();
								SendResponse_JOINGROUP("ACCEPTED", notificationValue,
										senderValue, codeValue);
								
								DIALOG_SHOW_MSG.dismiss();
							}
						})
				.setNegativeButton("REJECT",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								((MyTeam)getApplication()).PlayButtonSound();
								SendResponse_JOINGROUP("REJECTED", notificationValue,
										senderValue, codeValue);
								DIALOG_SHOW_MSG.dismiss();

							}
						}).create();
		DIALOG_SHOW_MSG.show();

	}

	protected void SendResponse_JOINGROUP(String str,
			String notificationValue, String senderValue, String codeValue) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
		nameValuePairs.add(new BasicNameValuePair("NOTIFICATIONID",
				notificationValue));
		nameValuePairs.add(new BasicNameValuePair("MEMBERID", senderValue));
		nameValuePairs.add(new BasicNameValuePair("LEADERID", BasicDetails
				.getCurrentID()));
		nameValuePairs.add(new BasicNameValuePair("CODE", codeValue));
		nameValuePairs.add(new BasicNameValuePair("STATUS", str));
		new PostToServer(nameValuePairs,URL_Strings.getUrlProcessNotification(),"Processing...",JOIN_GROUP_TYPE,"NULL").execute();
	}

	// NEW TASK NOTIFICATION

	private View DisplayNEW_TASK_Notification(final int count, String Msg) {
		final String NotificationValue = xmlParser.getValue(e, "NOTIFICATIONID");
		final String CodeValue = xmlParser.getValue(e, "CODE");
		final String SenderValue = xmlParser.getValue(e, "SENDER");
		final String DateValue = xmlParser.getValue(e, "DATE");
		final String TaskIDValue = xmlParser.getValue(e, "TASKID");
		final String StatusValue = xmlParser.getValue(e, "STATUS");
		LayoutInflater minflate = LayoutInflater.from(getApplicationContext());
		final View convertView = minflate.inflate(
				R.layout.notification_inflater, null);
		TextView msgNote = (TextView) convertView
				.findViewById(R.id.Notification_Text);
		msgNote.setText(Msg);
		msgNote.setSelected(true);
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				NewTaskDialog(NotificationValue, CodeValue, SenderValue,
						DateValue, TaskIDValue, StatusValue);
				notificationBoard.removeView(convertView);

			}
		});
		return convertView;
	}

	protected void NewTaskDialog(String notificationValue, String codeValue,
			String senderValue, String dateValue, final String taskIDValue,
			String statusValue) {
		String showMsg = "A NEW TASK IS ALLOCATED TO YOU BY:\n" + senderValue
				+ "\n on " + dateValue + "\n with TASK ID : " + taskIDValue
				+ ".\n!";
		DIALOG_SHOW_MSG = new AlertDialog.Builder(HomeScreen.this)
				.setIcon(R.drawable.ic_launcher)
				.setTitle("NEW TASK")
				.setMessage(showMsg)
				.setPositiveButton("VIEW",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								((MyTeam)getApplication()).PlayButtonSound();
								Intent taskintent = new Intent(
										getApplicationContext(),
										ViewSelectedTask.class);
								taskintent.putExtra("TASKID", taskIDValue);
								startActivityForResult(taskintent, 110);
								DIALOG_SHOW_MSG.dismiss();
							}
						})
				.setNegativeButton("BACK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								((MyTeam)getApplication()).PlayButtonSound();
								DIALOG_SHOW_MSG.dismiss();
							}
						}).create();
		DeleteThisNotification(notificationValue);
		DIALOG_SHOW_MSG.show();

	}

	// NEW COMMENT
	private View DisplayNEW_COMMENT_Notification(final int count, String Msg) {
		final String NotificationValue = xmlParser.getValue(e, "NOTIFICATIONID");
		final String CodeValue = xmlParser.getValue(e, "CODE");
		final String SenderValue = xmlParser.getValue(e, "SENDER");
		final String DateValue = xmlParser.getValue(e, "DATE");
		final String TaskIDValue = xmlParser.getValue(e, "TASKID");
		final String StatusValue = xmlParser.getValue(e, "STATUS");
		LayoutInflater minflate = LayoutInflater.from(getApplicationContext());
		final View convertView = minflate.inflate(
				R.layout.notification_inflater, null);
		TextView msgNote = (TextView) convertView
				.findViewById(R.id.Notification_Text);
		msgNote.setText(Msg);
		msgNote.setSelected(true);
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				NewCommentDialog(NotificationValue, CodeValue, SenderValue,
						DateValue, TaskIDValue, StatusValue);
				notificationBoard.removeView(convertView);

			}
		});
		return convertView;
	}

	protected void NewCommentDialog(String notificationValue, String codeValue,
			String senderValue, String dateValue, final String taskIDValue,
			String statusValue) {
		String showMsg = "A NEW COMMENT IS ADDED TO TASK ID:\n" + taskIDValue
				+ "BY :\n" + senderValue + "\n on " + dateValue;
		DIALOG_SHOW_MSG = new AlertDialog.Builder(HomeScreen.this)
				.setIcon(R.drawable.ic_launcher)
				.setTitle("NEW COMMENT")
				.setMessage(showMsg)
				.setPositiveButton("VIEW",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								((MyTeam)getApplication()).PlayButtonSound();
								Intent taskintent = new Intent(
										getApplicationContext(),
										ViewSelectedTask.class);
								taskintent.putExtra("TASKID", taskIDValue);
								startActivityForResult(taskintent, 110);
								DIALOG_SHOW_MSG.dismiss();
							}
						})
				.setNegativeButton("BACK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								((MyTeam)getApplication()).PlayButtonSound();
								DIALOG_SHOW_MSG.dismiss();
							}
						}).create();
		DeleteThisNotification(notificationValue);
		DIALOG_SHOW_MSG.show();

	}

	// NEW TASK UPDATE
	private View DisplayTASK_UPDATE_Notification(final int count, String Msg) {
		final String NotificationValue = xmlParser.getValue(e, "NOTIFICATIONID");
		final String CodeValue = xmlParser.getValue(e, "CODE");
		final String SenderValue = xmlParser.getValue(e, "SENDER");
		final String DateValue = xmlParser.getValue(e, "DATE");
		final String TaskIDValue = xmlParser.getValue(e, "TASKID");
		final String StatusValue = xmlParser.getValue(e, "STATUS");
		LayoutInflater minflate = LayoutInflater.from(getApplicationContext());
		final View convertView = minflate.inflate(
				R.layout.notification_inflater, null);
		TextView msgNote = (TextView) convertView
				.findViewById(R.id.Notification_Text);
		msgNote.setText(Msg);
		msgNote.setSelected(true);
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				TaskUpdateDialog(NotificationValue, CodeValue, SenderValue,
						DateValue, TaskIDValue, StatusValue);
				notificationBoard.removeView(convertView);

			}
		});
		return convertView;
	}

	protected void TaskUpdateDialog(String notificationValue, String codeValue,
			String senderValue, String dateValue, final String taskIDValue,
			String statusValue) {
		String showMsg = "STATUS OF TASK ID:\n" + taskIDValue
				+ "\nis updated to " + statusValue + "\n on " + dateValue
				+ "\n by " + senderValue;
		DIALOG_SHOW_MSG = new AlertDialog.Builder(HomeScreen.this)
				.setIcon(R.drawable.ic_launcher)
				.setTitle("STATUS UPDATED")
				.setMessage(showMsg)
				.setPositiveButton("VIEW",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								((MyTeam)getApplication()).PlayButtonSound();
								Intent taskintent = new Intent(
										getApplicationContext(),
										ViewSelectedTask.class);
								taskintent.putExtra("TASKID", taskIDValue);
								startActivityForResult(taskintent, 110);
								DIALOG_SHOW_MSG.dismiss();
							}
						})
				.setNegativeButton("BACK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								((MyTeam)getApplication()).PlayButtonSound();
								DIALOG_SHOW_MSG.dismiss();
							}
						}).create();
		DeleteThisNotification(notificationValue);
		DIALOG_SHOW_MSG.show();

	}

	// TASK COMPLETE...
	private View DisplayTASK_COMPLETE_Notification(final int count, String Msg) {
		final String NotificationValue = xmlParser.getValue(e, "NOTIFICATIONID");
		final String CodeValue = xmlParser.getValue(e, "CODE");
		final String SenderValue = xmlParser.getValue(e, "SENDER");
		final String DateValue = xmlParser.getValue(e, "DATE");
		final String TaskIDValue = xmlParser.getValue(e, "TASKID");
		final String StatusValue = xmlParser.getValue(e, "STATUS");
		LayoutInflater minflate = LayoutInflater.from(getApplicationContext());
		final View convertView = minflate.inflate(
				R.layout.notification_inflater, null);
		TextView msgNote = (TextView) convertView
				.findViewById(R.id.Notification_Text);
		msgNote.setText(Msg);
		msgNote.setSelected(true);
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				TaskCompleteDialog(NotificationValue, CodeValue, SenderValue,
						DateValue, TaskIDValue, StatusValue);
				notificationBoard.removeView(convertView);

			}
		});
		return convertView;
	}

	protected void TaskCompleteDialog(final String notificationValue,
			String codeValue, final String senderValue, String dateValue,
			final String taskIDValue, String statusValue) {
		LayoutInflater minflate = LayoutInflater.from(getApplicationContext());
		final View convertView = minflate.inflate(R.layout.give_scores, null);
		TextView Taskid_Txt = (TextView) convertView
				.findViewById(R.id.GS_TaskID);
		final RatingBar rateBar = (RatingBar) convertView
				.findViewById(R.id.GS_ratingBar);
		Taskid_Txt.setText(taskIDValue);
		rateBar.setProgress(6);
		DIALOG_SHOW_MSG = new AlertDialog.Builder(HomeScreen.this)
				.setTitle(senderValue)
				.setView(convertView)
				.setPositiveButton("SUBMIT",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								((MyTeam)getApplication()).PlayButtonSound();
								Toast.makeText(
										getApplicationContext(),
										"Score Given: " + rateBar.getProgress(),
										Toast.LENGTH_LONG).show();
								List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
										3);
								nameValuePairs.add(new BasicNameValuePair(
										"MEMBERID", senderValue));
								nameValuePairs.add(new BasicNameValuePair(
										"TASKID", taskIDValue));
								nameValuePairs.add(new BasicNameValuePair(
										"SCORE", rateBar.getProgress() + ""));
								new PostToServer(nameValuePairs,URL_Strings.getUrlAddScore(),"Processing..",ADD_SCORE_TYPE,notificationValue).execute();
								
							}
						}).create();
		DIALOG_SHOW_MSG.show();
		//DeleteThisNotification(notificationValue);
		//TODO if problem un comment above line
	}

	private View NoNewNotification() {
		LayoutInflater minflate = LayoutInflater.from(getApplicationContext());
		final View convertView = minflate.inflate(R.layout.no_notification,
				null);
		return convertView;
	}

	private void MenuLayoutInitialization() {
		ChatButton = (Button) findViewById(R.id.HS_chat_button);
		MenuHome = (Button) findViewById(R.id.HS_Home_Btn);
		MenuTeam = (Button) findViewById(R.id.HS_Team_Btn);
		MenuTask = (Button) findViewById(R.id.HS_Task_Btn);
		MenuMore = (Button) findViewById(R.id.HS_More_Btn);
		ChatButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO....
				((MyTeam)getApplication()).PlayButtonSound();
				Intent chat = new Intent(getApplicationContext(),
						ChatScreen.class);
				chat.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivityForResult(chat, 110);
			}
		});
		
		MenuHome.setEnabled(false);// SET THE SAME SCREEN BUTTON DISABLED..
		MenuHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				Intent PageChange = new Intent(getApplicationContext(),
						HomeScreen.class);
				PageChange.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(PageChange, 110);
			}
		});
		MenuTask.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				Intent PageChange = new Intent(getApplicationContext(),
						TaskList.class);
				PageChange.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(PageChange, 110);
			}
		});
		MenuTeam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				Intent PageChange = new Intent(getApplicationContext(),
						TeamList.class);
				PageChange.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(PageChange, 110);
			}
		});
		MenuMore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				Intent PageChange = new Intent(getApplicationContext(),
						MoreSettings.class);
				PageChange.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(PageChange, 110);
			}
		});
	}


	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.hoge_page_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.home_menu_logout:
			this.finish();
			((MyTeam) getApplication()).LogOut();
			break;
		case R.id.home_menu_help:
				startActivityForResult(new Intent(getApplicationContext(), Help.class), 110);
			break;
		case R.id.home_menu_refresh:
			RefreshNotifications();
			break;
		case R.id.home_menu_credits:
			startActivityForResult(new Intent(getApplicationContext(), Credits.class), 110);
		break;
		}
		return true;
	}

	private void CheckGroupDetails() {
		
		if (BasicDetails.getLeaderID().equals("NOT SET")) {
			Log.d(getClass().getName(),"Leader is NOT SET");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair(ServerLabels
						.getFirstLabel(), ServerLabels.getPendingNotification()));
				nameValuePairs.add(new BasicNameValuePair("USERID", BasicDetails
						.getCurrentID()));
				new PostToServer(nameValuePairs,
						URL_Strings.getUrlPendingNotification(),"Processing..",CHECK_IF_REQUEST_TYPE,"NULL").execute();
		}
	}
	public void AfterServerCheckIfRequest(String xmlData1){
		final String NotificationID;
		String Receiver, date;
				if (xmlData1.equalsIgnoreCase("invalid")) {
					Log.d(getClass().getName(),"NO GROUP JOINT");
					ShowThisMsg("NO GROUP JOINT",
							"PLEASE JOIN A GROUP NOW....!", GROUP_JOIN);
				} else {
					Toast.makeText(getApplicationContext(),
							"NOTIFICATION PRESENT", Toast.LENGTH_LONG).show();
					doc = xmlParser.getDomElement(xmlData1);
					NodeList nl = doc.getElementsByTagName("values");
					Element e = (Element) nl.item(0);
					nl.getLength();
					NotificationID = xmlParser.getValue(e, "NOTIFICATIONID");
					Receiver = xmlParser.getValue(e, "RECEIVER");
					date = xmlParser.getValue(e, "DATE").toString();

					DIALOG_SHOW_MSG = new AlertDialog.Builder(HomeScreen.this)
							.setIcon(R.drawable.ic_launcher)
							.setTitle("Request Pending:")
							.setMessage(
									"You have already sent a request to:\n "
											+ Receiver + "\non " + date)
							.setPositiveButton("WAIT",
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											((MyTeam)getApplication()).PlayButtonSound();
											Toast.makeText(
													getApplicationContext(),
													"MyTeam is exiting..\nPlease login after sometime..",
													Toast.LENGTH_LONG).show();
											Intent logout = new Intent(
													getApplicationContext(),
													LoginPage.class);
											logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
											startActivityForResult(logout, 110);
										}
									})
							.setNegativeButton("CANCEL REQUEST",
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											((MyTeam)getApplication()).PlayButtonSound();
											List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
													2);
											nameValuePairs
													.add(new BasicNameValuePair(
															ServerLabels
																	.getFirstLabel(),
															ServerLabels
																	.getDeleteNotification()));
											nameValuePairs
													.add(new BasicNameValuePair(
															"NOTIFICATIONID",
															NotificationID));
											new PostToServer(nameValuePairs,URL_Strings.getUrlPendingNotification(),"Deleting request..",CHECK_GROUP_TYPE,"NULL").execute();
										}
									}).create();
					DIALOG_SHOW_MSG.show();

			} 
		}
	protected void ShowThisMsg(String title, String message, final int type) {
		DIALOG_SHOW_MSG = new AlertDialog.Builder(HomeScreen.this)
				.setIcon(R.drawable.ic_launcher).setTitle(title)
				.setMessage(message)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						((MyTeam)getApplication()).PlayButtonSound();
						switch (type) {
						case LOGOUT_APPLICATION:
							((MyTeam) getApplication()).LogOut();
							break;
						case SET_EMAIL:
							Intent setmail = new Intent(
									getApplicationContext(),
									ProfileDetails.class);
							startActivityForResult(setmail, 110);
							break;
						case GROUP_JOIN:
							Intent grup = new Intent(getApplicationContext(),
									JoinGroup.class);
							startActivityForResult(grup, 110);
							break;
						}
						DIALOG_SHOW_MSG.dismiss();
					}
				}).create();
		DIALOG_SHOW_MSG.show();
	}

	public void DeleteThisNotification(String id) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("NOTIFICATIONID", id));
		new PostToServer(nameValuePairs,URL_Strings.getUrlDeleteNotification(),"",NORMAL_CHECK,"NULL").execute();
		Log.d(getClass().getName(),"DeleteNotifiaction :"+id);
	}

	
	class PostToServer extends AsyncTask<String, Void, Boolean>{
		private String ERROR =null;
		ProgressDialog PG =null;
		private String RESULT_STRING;
		String URL;
		List<NameValuePair> nameValuePairs;
		private String Msg;
		private String notificationId;
		private int POST_TYPE;
		
		public PostToServer(List<NameValuePair> nameValuePairs,String URL,String Msg,int POST_TYPE,String notificationId) {
			this.URL=URL;
			this.nameValuePairs=nameValuePairs;
			this.Msg=Msg;
			this.POST_TYPE=POST_TYPE;
			this.notificationId=notificationId;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			RESULT_STRING = null;
			PG = new ProgressDialog(HomeScreen.this);
			PG.setCancelable(false);
			PG.setMessage(Msg);
			PG.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			//PG.show();
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
			//if(PG != null){ if(PG.isShowing()){ PG.dismiss();}}
			if(result){
				switch(POST_TYPE){
				case REFRESH_TYPE:AfterServerRefreshNotification(RESULT_STRING);					
					break;
				case JOIN_GROUP_TYPE:
					if (RESULT_STRING.equals("SUCCESSFULL")) {
						ShowThisMsg("DONE..","YOUR REQUEST WAS PROCESSED", 0);
					} else {
						ShowThisMsg(
								"Error:","UNEXPECTED ERROR...\nThe notification is deleted..\nnotification was NOT PROCESSED\n"
										+ RESULT_STRING, 0);
					}
					break;
				case ADD_SCORE_TYPE:
					if (RESULT_STRING.equals("SUCCESS")) {
					DeleteThisNotification(notificationId);
					ShowThisMsg("DONE!",
							"SCORES HAS BEEN SUBMITTED...", 0);
				} else {
					ShowThisMsg(
							"Error:",
							"COULD NOT COMPLETE THIS REQUEST....\nTRY AGAIN LATER...",
							0);
				}
					break;
				case CHECK_GROUP_TYPE:
					if (RESULT_STRING
							.equalsIgnoreCase("successfull")) {
						Intent grup = new Intent(
								getApplicationContext(),
								JoinGroup.class);
						startActivityForResult(grup,
								110);
					} else {
						ShowThisMsg("Error:", xmlData1,
								0);
					}
					break;
				case CHECK_IF_REQUEST_TYPE:
					AfterServerCheckIfRequest(RESULT_STRING);
					break;
					default:Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
				}
			}else{
				Toast.makeText(getApplicationContext(), "Error:"+ERROR, Toast.LENGTH_LONG).show();
			}
		}
	}

	
	public class GmailLoginThread extends AsyncTask<Void, Void, Boolean>{
		private String user=null,pass=null;
		ProgressDialog PG =null;
		String URL;
		List<NameValuePair> nameValuePairs;
		private String Msg="EMAIL LOGIN ";
		private String RESULT_STRING=null,ERROR=null;
		public GmailLoginThread(String user,String pass) {
			this.user=user;
			this.pass=pass;
			Msg+=":\n"+user;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			PG = new ProgressDialog(HomeScreen.this);
			PG.setCancelable(false);
			PG.setMessage(Msg);
			PG.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			PG.show();
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			try{
				RESULT_STRING=((MyTeam)getApplication()).DoLogin(user, pass);
				return true;
			}catch(Exception ex){
				ERROR=ex.toString();
				return false;
			}
		}
		protected void onPostExecute(Boolean login) {
			super.onPostExecute(login);
			if(PG != null){ if(PG.isShowing()){ PG.dismiss();}}
			if(login){
			if (RESULT_STRING.equals("DONE")) {
				BasicDetails.setLoginDone(true);
				ProceedFurther();
			} else if (RESULT_STRING.equals("WRONG")) {
				ShowThisMsg(
						"LOGIN UNSUCCESSFULL:",
						"EMAIL DETAILS ARE WRONG..\nPlease Enter Valid Details...",
						SET_EMAIL);
			}else if(RESULT_STRING.equals("ERROR")){
				ShowThisMsg("UNEXPECTED PROBLEM:",
						"Application Will Quit Now..\nNetwork Connection unavailable or slow..\n",
						LOGOUT_APPLICATION);
			}
			}else {
				ShowThisMsg("UNEXPECTED PROBLEM:",
						"UNEXPECTED ERROR...\nApplication Will Quit Now..\n"+ERROR,
						LOGOUT_APPLICATION);
			}
		}
	}
}
