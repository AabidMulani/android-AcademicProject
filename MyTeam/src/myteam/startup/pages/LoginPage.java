package myteam.startup.pages;

import java.util.ArrayList;
import java.util.List;

import myteam.global.constants.BasicDetails;
import myteam.global.constants.MyTeamSettings;
import myteam.global.constants.ParseXmlData;
import myteam.global.constants.ServerLabels;
import myteam.global.constants.URL_Strings;
import myteam.main.forms.HomeScreen;
import myteam.main.forms.MyTeam;
import myteam.main.forms.R;
import myteam.startup.recovery.RecoverPassword;
import myteam.team.register.EmployeeType;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginPage extends Activity {
	private int CHECK_CODE = 100;
	private Button NewAccount = null;
	private Button Login = null;
	private CheckBox Remember = null;
	private TextView Forgot = null;
	private EditText UserName = null;
	private EditText Password = null;
	private AlertDialog DIALOG_SHOW_MSG = null;
	private Document doc;
	private String USERNAME, PASSWORD;
	private ParseXmlData xmlParser = new ParseXmlData();
	private MyTeamDB myteamdb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen_activity);
		MyTeam.currentContext=this;

		UserName = (EditText) findViewById(R.id.LSA_UserName_Txt);
		UserName.addTextChangedListener(MyTeam.EditTextSound);
		Password = (EditText) findViewById(R.id.LSA_PassWord_Txt);
		Password.addTextChangedListener(MyTeam.EditTextSound);

		Remember = (CheckBox) findViewById(R.id.LSA_RememberMe_ChkBx);
		Forgot = (TextView) findViewById(R.id.LSA_Forgot_pass);
		NewAccount = (Button) findViewById(R.id.LSA_newAccount_Btn);
		Login = (Button) findViewById(R.id.LSA_Login_Btn);
		Login.requestFocus();////TODO ADITIONAL
		myteamdb=new MyTeamDB(getApplicationContext());
		myteamdb.open();
		if(myteamdb.GetRememberData()){
			Remember.setChecked(true);
			UserName.setText(myteamdb.GetUserName());
			UserName.setEnabled(true);
			Password.setText(myteamdb.GetPassWord());
			Password.setEnabled(true);
		}
		myteamdb.close();
		UpdateMyTeamSettings();
		Toast.makeText(getApplicationContext(), "TESTING CONNECTION....", Toast.LENGTH_LONG).show();
			if(!((MyTeam)getApplication()).IsInternetConnected()){
			ShowThisMsg("NETWORK NOT AVAILABLE", "Your Device Is NOT Connected To Any NetWork...\nPlease Connect And Restart MyTeam...", 1);
			}
			if(!((MyTeam)getApplication()).IsNetworkConnected()){
			ShowThisMsg("NETWORK NOT AVAILABLE", "Your Device Is NOT Connected To Any NetWork...\nPlease Connect And Restart MyTeam...", 1);
			}

		Remember.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				ProcessRememberMe();
			}
		});

		Forgot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				Intent i = new Intent(getApplicationContext(),
						RecoverPassword.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(i, 110);

			}
		});

		NewAccount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				Intent in = new Intent(getApplicationContext(),
						EmployeeType.class);
				startActivityForResult(in, CHECK_CODE);

			}
		});

		Login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				if (UserName.getText().toString().trim().length() == 0) {
					UserName.setError("Empty Field");
				} else if (Password.getText().toString().trim().length() == 0) {
					Password.setError("Empty Field");
				} else {
					ProcessRememberMe();
					LoginProcedure();
				}
			}
		});
	}

	protected void ProcessRememberMe() {
		myteamdb.open();
		if(Remember.isChecked()){
			if(UserName.getText().toString().trim().length()!=0 && Password.getText().toString().trim().length()!=0)
				myteamdb.insertIntoRememberTable(UserName.getText().toString(), Password.getText().toString());
		}else{
			myteamdb.ClearRememberTable();
		}
		myteamdb.close();
	}

	protected void LoginProcedure() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair(ServerLabels.getFirstLabel(),
				ServerLabels.getCheckLogin()));
		USERNAME = UserName.getText().toString();
		nameValuePairs.add(new BasicNameValuePair("USERNAME", USERNAME));
		PASSWORD = Password.getText().toString();
		nameValuePairs.add(new BasicNameValuePair("PASSWORD", PASSWORD));
			new PostToServer(nameValuePairs, URL_Strings.getUrlLoginPage(),"Login Check..",1).execute();
	}

	private void GetBasicDetails() {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair(ServerLabels
					.getFirstLabel(), ServerLabels.getGetBasicDetails()));
			nameValuePairs.add(new BasicNameValuePair("USERNAME", USERNAME));
			new PostToServer(nameValuePairs,URL_Strings.getUrlBasicdetailsPage(),"LOADING BASIC DETAILS..",2).execute();
			//ShowThisMsg("XML", xml, 0);
		}
	
	protected void AfterGetBasicDetails(String xml){
		if (StoreBasicDetails(xml)) {
			Intent in = new Intent(getApplicationContext(),
					HomeScreen.class);
			in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(in, 110);
		} else {
			ShowThisMsg("ERROR:", "ERROR WHILE RECEIVING BASIC DETAILS..", 0);
		}	
	}
	protected boolean StoreBasicDetails(String xml){
			try {
			doc = xmlParser.getDomElement(xml);
			NodeList nl = doc.getElementsByTagName("values");
			Element e = (Element) nl.item(0);
			nl.getLength();
			BasicDetails.setCurrentID(xmlParser.getValue(e, "ID"));
			BasicDetails.setLeaderID(xmlParser.getValue(e, "LeaderID"));
			BasicDetails.setUserName(xmlParser.getValue(e, "UserName").toString());
			BasicDetails.setFirstName(xmlParser.getValue(e, "FirstName").toString());
			BasicDetails.setLastName(xmlParser.getValue(e, "LastName").toString());
			BasicDetails.setGender(xmlParser.getValue(e, "Gender").toString());
			BasicDetails.setTYPE(xmlParser.getValue(e, "EmployeeType").toString());
			BasicDetails.setEmailID(xmlParser.getValue(e, "EmailID").toString()); // NEW
			BasicDetails.setEmailPass(xmlParser.getValue(e, "Password").toString()); // NEW
			Log.d("VALUE:= ", xmlParser.getValue(e, "ProfilePic"));
			BasicDetails.setProfileString(xmlParser.getValue(e, "ProfilePic"));
			BasicDetails.ProfilePic=MyTeam.StringToBitmap(xmlParser.getValue(e, "ProfilePic"));
			return true;
		} catch (Exception ex) {
			Toast.makeText(getApplicationContext(), ex.toString(),
					Toast.LENGTH_LONG).show();
		}
			return false;
	}

	public void ShowThisMsg(String title, String message, final int type) {
		DIALOG_SHOW_MSG = new AlertDialog.Builder(LoginPage.this)
				.setIcon(R.drawable.ic_launcher).setTitle(title)
				.setMessage(message)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (type == 1) {
							System.exit(0);
						}
					}
				}).create();
		DIALOG_SHOW_MSG.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login_page_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.login_menu_exit:
			DIALOG_SHOW_MSG = new AlertDialog.Builder(LoginPage.this)
					.setIcon(R.drawable.ic_launcher)
					.setTitle("EXIT")
					.setMessage("EXITING...THANK YOU FOR USING MyTeam,....")
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									DIALOG_SHOW_MSG.dismiss();
									onBackPressed();
								}
							}).create();
			DIALOG_SHOW_MSG.show();
			break;
		case R.id.login_menu_setting:
			LayoutInflater minflate = LayoutInflater.from(getApplicationContext());
			final View convertView = minflate.inflate(R.layout.settings_layout,
					null);
			final CheckBox soundChk = (CheckBox) convertView
					.findViewById(R.id.SL_sound_chck);
			if(MyTeamSettings.isPlaySound())
				soundChk.setChecked(true);
			else
				soundChk.setChecked(false);
			final CheckBox vibrateChk = (CheckBox) convertView
					.findViewById(R.id.SL_vibrate_chck);
			if(MyTeamSettings.isDoVibrate())
				vibrateChk.setChecked(true);
			else
				vibrateChk.setChecked(false);
					
			DIALOG_SHOW_MSG = new AlertDialog.Builder(LoginPage.this)
					.setIcon(R.drawable.ic_launcher)
					.setTitle("EXIT")
					.setView(convertView)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									String Vibrate,Sound;
									if(vibrateChk.isChecked())
										Vibrate=MyTeamDB.PLAY;
									else
										Vibrate=MyTeamDB.NOT_PLAY;
									if(soundChk.isChecked())
										Sound=MyTeamDB.PLAY;
									else
										Sound=MyTeamDB.NOT_PLAY;
									myteamdb.open();
									myteamdb.UpdateSettingsTable(Sound, Vibrate);
									myteamdb.close();
									UpdateMyTeamSettings();
									DIALOG_SHOW_MSG.dismiss();
								}
							}).create();
			DIALOG_SHOW_MSG.show();
			break;
		}

		return true;
	}

	public void UpdateMyTeamSettings() {
		myteamdb.open();
		if(myteamdb.GetSettingsData()){
				MyTeamSettings.setDoVibrate(myteamdb.GetVibrateSettings());
				MyTeamSettings.setPlaySound(myteamdb.GetSoundSettings());
		}
		myteamdb.close();
	}
	@Override
	public void onBackPressed() {
		Toast.makeText(getApplicationContext(), "Exiting..", Toast.LENGTH_LONG).show();
		this.finish();
		((MyTeam)getApplication()).LogOut();
		
	}
	
	class PostToServer extends AsyncTask<String, Void, Boolean>{
		private String ERROR =null;
		ProgressDialog PG =null;
		private String RESULT_STRING;
		String URL;
		List<NameValuePair> nameValuePairs;
		private String Msg;
		private int THIS_TYPE;
		public PostToServer(List<NameValuePair> nameValuePairs,String URL,String Msg,int type) {
			this.URL=URL;
			this.nameValuePairs=nameValuePairs;
			this.Msg=Msg;
			this.THIS_TYPE=type;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			RESULT_STRING = null;
			PG = new ProgressDialog(LoginPage.this);
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
					if (RESULT_STRING.equalsIgnoreCase("CORRECT")) {
						GetBasicDetails();
					}else{
						ShowThisMsg("ERROR:", "INVALID USERNAME/PASSWORD..", 0);
					}
					break;
				case 2:AfterGetBasicDetails(RESULT_STRING);
					break;
				default:Toast.makeText(getApplicationContext(), "DONE: "+RESULT_STRING, Toast.LENGTH_LONG).show();
				}
			}else{
				Toast.makeText(getApplicationContext(), "Error:"+ERROR, Toast.LENGTH_LONG).show();
			}
		}
	}

	
}
