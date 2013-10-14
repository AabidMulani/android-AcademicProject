package myteam.main.forms;

import java.util.ArrayList;
import java.util.List;

import myteam.global.constants.BasicDetails;
import myteam.global.constants.Credits;
import myteam.global.constants.Help;
import myteam.global.constants.ServerLabels;
import myteam.global.constants.URL_Strings;
import myteam.team.editData.EditLoginDetails;
import myteam.team.editData.EditMemberScreen;
import myteam.team.editEmail.EditProfileData;
import myteam.team.management.LeaveGroup;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.Toast;

public class MoreSettings extends Activity {
	private Button ChatButton = null;
	private Button MenuHome = null;
	private Button MenuTeam = null;
	private Button MenuTask = null;
	private Button MenuMore = null;
	private AlertDialog DialogPassword;
	private Button personalBtn=null;
	private Button emailBtn=null;
	private Button loginBtn=null;
	private Button leaveGrup=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_settings_screen);
		MyTeam.currentContext=this;
		((MyTeam)getApplication()).moreSetting=this;
		MenuLayoutInitialization();
		personalBtn=(Button) findViewById(R.id.MSS_edit_personal_details);
		personalBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				ShowPasswordDialog(0);
			}
		});
		emailBtn=(Button) findViewById(R.id.MSS_edit_email_details);
		emailBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				ShowPasswordDialog(1);
			}
		});
		loginBtn=(Button) findViewById(R.id.MSS_edit_login_details);
		loginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				ShowPasswordDialog(2);
			}
		});
		leaveGrup=(Button)findViewById(R.id.MSS_leave_group);
		if(BasicDetails.getTYPE().equals("LEADER"))
			leaveGrup.setVisibility(Button.GONE);
		leaveGrup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				ShowPasswordDialog(3);
			}
		});
	}
	
	protected void ShowPasswordDialog(final int position){
		LayoutInflater minflate = LayoutInflater.from(getApplicationContext());
		final View convertView = minflate.inflate(R.layout.verify_password,
				null);
		final EditText pass=(EditText)convertView.findViewById(R.id.VP_Password_Txt);
		pass.requestFocus();
		DialogPassword = new AlertDialog.Builder(MoreSettings.this)
				.setTitle("ENTER PASSWORD:")
				.setView(convertView)
				.setPositiveButton("PROCEED",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (pass.getText().toString().trim().length()==0) {
									pass.setError("ENTER PASSWORD");
									ShowPasswordDialog(position);
								}else{
									LoginProcedure(pass.getText().toString(),position);
								}
							}
						})
				.setNegativeButton("CANCEL",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).create();
		DialogPassword.show();
	}
	
	protected void ValidLogin(String LoginStatus,int position){
		if (LoginStatus.equals("CORRECT")) {
			switch(position){
			case 0: Intent editdetails=new Intent(getApplicationContext(),EditMemberScreen.class);
					startActivityForResult(editdetails, 110);
				break;
			case 1:	Intent editEmail=new Intent(getApplicationContext(),EditProfileData.class);
			startActivityForResult(editEmail, 110);
				break;
			case 2:Intent login=new Intent(getApplicationContext(),EditLoginDetails.class);
			startActivityForResult(login, 110);
				break;	
			case 3:Intent leave=new Intent(getApplicationContext(),LeaveGroup.class);
			startActivityForResult(leave, 110);
				break;
			}
		} else {
			Toast.makeText(getApplicationContext(), "WRONG PASSWORD\n" +LoginStatus, Toast.LENGTH_LONG).show();
		}
	}
	protected void LoginProcedure(String pass,int position) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair(ServerLabels.getFirstLabel(),
				ServerLabels.getCheckLogin()));
		nameValuePairs.add(new BasicNameValuePair("USERNAME", BasicDetails
				.getCurrentID()));
		nameValuePairs.add(new BasicNameValuePair("PASSWORD", pass));
		new PostToServer(nameValuePairs, URL_Strings.getUrlLoginPage(),"Authenticating..",position).execute();
	}

	private void MenuLayoutInitialization() {
		ChatButton=(Button) findViewById(R.id.MSS_chat_button);
		MenuHome=(Button) findViewById(R.id.MSS_Home_Btn);
		MenuTeam=(Button) findViewById(R.id.MSS_Team_Btn);
		MenuTask=(Button) findViewById(R.id.MSS_Task_Btn);
		MenuMore=(Button) findViewById(R.id.MSS_More_Btn);
		ChatButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				Intent chat = new Intent(getApplicationContext(),
						ChatScreen.class);
				chat.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//				chat.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(chat, 110);
				}
		});
		MenuHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				Intent PageChange=new Intent(getApplicationContext(),HomeScreen.class);
				PageChange.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(PageChange, 110);
			}
		});
		MenuTask.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				Intent PageChange=new Intent(getApplicationContext(),TaskList.class);
				PageChange.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(PageChange, 110);
			}
		});
		MenuTeam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				Intent PageChange=new Intent(getApplicationContext(),TeamList.class);
				PageChange.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(PageChange, 110);
			}
		});
		MenuMore.setEnabled(false);// SET THE SAME SCREEN BUTTON DISABLED..
		MenuMore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				Intent PageChange=new Intent(getApplicationContext(),MoreSettings.class);
				PageChange.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(PageChange, 110);
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
			this.finish();
			((MyTeam)getApplication()).LogOut();
			break;
		case R.id.other_menu_help:
			startActivityForResult(new Intent(getApplicationContext(), Help.class), 110);
			break;
		case R.id.other_menu_credits:
			startActivityForResult(new Intent(getApplicationContext(), Credits.class), 110);
		break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
	}
	
	
	
	class PostToServer extends AsyncTask<String, Void, Boolean>{
		private String ERROR =null;
		ProgressDialog PG =null;
		private String RESULT_STRING;
		String URL;
		List<NameValuePair> nameValuePairs;
		private String Msg;
		private int position;
		public PostToServer(List<NameValuePair> nameValuePairs,String URL,String Msg,int position) {
			this.URL=URL;
			this.nameValuePairs=nameValuePairs;
			this.Msg=Msg;
			this.position=position;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			RESULT_STRING = null;
			PG = new ProgressDialog(MoreSettings.this);
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
					ValidLogin(RESULT_STRING, position);
			}else{
				Toast.makeText(getApplicationContext(), "Error:"+ERROR, Toast.LENGTH_LONG).show();
			}
		}
	}

}
