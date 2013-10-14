package myteam.team.management;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import myteam.global.constants.BasicDetails;
import myteam.global.constants.Credits;
import myteam.global.constants.Help;
import myteam.global.constants.ParseXmlData;
import myteam.global.constants.ServerLabels;
import myteam.global.constants.URL_Strings;
import myteam.main.forms.MyTeam;
import myteam.main.forms.R;

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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class JoinGroup extends Activity {
	private Button JoinGroup_Btn = null;
	private ScrollView view = null;
	private LinearLayout layout = null;
	private TextView header = null;
	private String GroupValues[][] = new String[50][4];
	private Document doc = null;
	private String DATE;
	int mYear,mMonth,mDay,mHour,mMin,mSec;
	private Dialog DIALOG_SHOW_MSG;
	private Dialog DIALOG_SHOW_MSG_2;
	private ParseXmlData xmlParser = new ParseXmlData();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.no_group_joined_screen);
		MyTeam.currentContext=this;
		JoinGroup_Btn = (Button) findViewById(R.id.NGJS_Join_Btn);
		view = (ScrollView) findViewById(R.id.NGJS_scrollView);
		layout = (LinearLayout) findViewById(R.id.NGJS_GroupList_linearlayout);
		header = (TextView) findViewById(R.id.NGJS_Header);
		JoinGroup_Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				JoinGroup_Btn.setVisibility(8);
				view.setVisibility(0);
				header.setText("SELECT YOUR TEAM");
				DisplayList();
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.other_page_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.other_menu_logout:
			((MyTeam)getApplication()).LogOut();
			break;
		case R.id.other_menu_credits:
			startActivityForResult(new Intent(getApplicationContext(), Credits.class), 110);
		break;
		case R.id.other_menu_help:
			startActivityForResult(new Intent(getApplicationContext(), Help.class), 110);
		break;
		}
		return true;
	}

	protected void DisplayList() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs
				.add(new BasicNameValuePair(ServerLabels.getFirstLabel(),
						ServerLabels.getAllGroupList()));
		new PostToServer(nameValuePairs,URL_Strings.getUrlGetGroupList(),"Loading List..",1,0).execute();
	}

	private View AddIntoList(final int i) {
		LayoutInflater minflate = LayoutInflater.from(getApplicationContext());
		final View convertView = minflate.inflate(R.layout.group_list_inflater,
				null);
		TextView LeaderID = (TextView) convertView
				.findViewById(R.id.GLI_GroupID);
		TextView MemCount = (TextView) convertView
				.findViewById(R.id.GLI_MemberCount);
		TextView LeaderName = (TextView) convertView
				.findViewById(R.id.GLI_Leader_Txt);
		LeaderID.setText(GroupValues[i][0]);
		MemCount.setText(GroupValues[i][2]);
		LeaderName.setText(GroupValues[i][1]);
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				DIALOG_SHOW_MSG = new AlertDialog.Builder(JoinGroup.this)
						.setIcon(R.drawable.ic_launcher)
						.setTitle("JOIN?")
						.setMessage(
								"Are you sure?\nJOIN " + GroupValues[i][1]
										+ " GROUP")
						.setPositiveButton("Confirm",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {

										List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
												4);
										nameValuePairs.add(new BasicNameValuePair(
												ServerLabels.getFirstLabel(),
												ServerLabels.getJoinGroup()));
										nameValuePairs.add(new BasicNameValuePair(
												"MemberID", BasicDetails
														.getCurrentID()));
										nameValuePairs
												.add(new BasicNameValuePair(
														"LeaderID",
														GroupValues[i][0]));
										GetTodaysDate();
										nameValuePairs
										.add(new BasicNameValuePair(
												"Date",
												DATE));
										new PostToServer(nameValuePairs,URL_Strings.getUrlAddNotification(),"Sending Request..",2,i).execute();
									}
								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										DIALOG_SHOW_MSG.dismiss();
									}
								}).create();
				DIALOG_SHOW_MSG.show();
			}
		});
		return convertView;
	}

	protected void GetTodaysDate(){
		final Calendar c = Calendar.getInstance(); // Date Instance
		 mYear = c.get(Calendar.YEAR);
		 mMonth = c.get(Calendar.MONTH);
		 mDay = c.get(Calendar.DAY_OF_MONTH);
		 DATE=updateDisplay(); 
		 mHour= c.get(Calendar.HOUR_OF_DAY);
		 mMin= c.get(Calendar.MINUTE);
		 mSec=c.get(Calendar.SECOND);
		 DATE =DATE+" "+mHour+":"+mMin+":"+mSec;
	}
	private String updateDisplay() {
		String str=(padding(mYear))+"-"+(padding(mMonth + 1))
				+"-"+mDay;
		return str;
	}

	private String padding(int value) {
		String response;
		if (value < 10)
			response = "0" + value;
		else
			response = value + "";
		return response;
	}
	
	@Override
	public void onBackPressed() {
		Dialog DIALOG_SHOW_MSG = new AlertDialog.Builder(JoinGroup.this)
		.setIcon(R.drawable.ic_launcher).setTitle("BACK:")
		.setMessage("JOINING A GROUP IS A NECESSARY PROCESS...")
		.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				JoinGroup.this.finish();
				((MyTeam)getApplication()).LogOut();
			}
		}).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).create();
		DIALOG_SHOW_MSG.show();
		
	}
	
	
	class PostToServer extends AsyncTask<String, Void, Boolean>{
		private String ERROR =null;
		ProgressDialog PG =null;
		private String RESULT_STRING;
		String URL;
		List<NameValuePair> nameValuePairs;
		private String Msg;
		private int THIS_TYPE;
		private int value;
		public PostToServer(List<NameValuePair> nameValuePairs,String URL,String Msg,int type,int value) {
			this.URL=URL;
			this.nameValuePairs=nameValuePairs;
			this.Msg=Msg;
			this.THIS_TYPE=type;
			this.value=value;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			RESULT_STRING = null;
			PG = new ProgressDialog(JoinGroup.this);
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
				switch(THIS_TYPE){
				case 1:
					doc = xmlParser.getDomElement(RESULT_STRING);
					NodeList nl = doc.getElementsByTagName("values");
					for (int i = 0; i < nl.getLength(); i++) {
						Element e = (Element) nl.item(i);
						GroupValues[i][0] = xmlParser.getValue(e, "ID");
						GroupValues[i][1] = xmlParser.getValue(e, "NAME");
						GroupValues[i][2] = xmlParser.getValue(e, "COUNT");
						GroupValues[i][3] = xmlParser.getValue(e, "EmailID");
						layout.addView(AddIntoList(i));
					}
					break;
				case 2:
					if (RESULT_STRING.equalsIgnoreCase("SUCCESSFULL")) {
						DIALOG_SHOW_MSG.dismiss();
						((MyTeam)getApplication()).SendXMPPmsg(GroupValues[value][3], MyTeam.NotificationText + BasicDetails.getCurrentID());
						DIALOG_SHOW_MSG_2 = new AlertDialog.Builder(
								JoinGroup.this)
								.setIcon(
										R.drawable.ic_launcher)
								.setTitle("THANK YOU:")
								.setMessage(
										"YOUR REQUEST IS SUCCESSUFULLY SENT TO "
												+ GroupValues[value][1]+"\n ON: "+DATE)
								.setPositiveButton(
										"OK",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int whichButton) {
												JoinGroup.this.finish();
												((MyTeam)getApplication()).LogOut();
//												Intent home=new Intent(getApplicationContext(),HomeScreen.class);
//												home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//												startActivityForResult(home, 110);
												DIALOG_SHOW_MSG_2
														.dismiss();
											}
										}).create();
						DIALOG_SHOW_MSG_2.show();
					} else {
						DIALOG_SHOW_MSG.dismiss();
						DIALOG_SHOW_MSG_2 = new AlertDialog.Builder(
								JoinGroup.this)
								.setIcon(
										R.drawable.ic_launcher)
								.setTitle("Error:")
								.setMessage("COULD NOT COMPLETE YOUR REQUEST...\ntry again later..\n"+RESULT_STRING)
								.setPositiveButton(
										"OK",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int whichButton) {
												DIALOG_SHOW_MSG_2
														.dismiss();
											}
										}).create();
						DIALOG_SHOW_MSG_2.show();
					}
					break;
				default:Toast.makeText(getApplicationContext(), "Done"+RESULT_STRING, Toast.LENGTH_LONG).show();
				}
			}else{
				Toast.makeText(getApplicationContext(), "Error:"+ERROR, Toast.LENGTH_LONG).show();
			}
		}
	}

}
