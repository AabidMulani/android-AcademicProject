package myteam.team.management;

import java.util.ArrayList;
import java.util.List;

import myteam.global.constants.BasicDetails;
import myteam.global.constants.URL_Strings;
import myteam.main.forms.HomeScreen;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class LeaveGroup extends Activity {
private Button confirm=null;
private AlertDialog DIALOG_SHOW_MSG;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leave_group);
		confirm=(Button) findViewById(R.id.LG_CONFIRM);
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				RemoveFromGroup();
			}
		});
	}
	
	protected void RemoveFromGroup() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("MEMBERID", BasicDetails.getCurrentID()));
		new PostToServer(nameValuePairs, URL_Strings.getUrlLeaveGroup(),"Processing..").execute();
		
	}

	protected void ShowThisMsg(String title, String message,final int type) {
		DIALOG_SHOW_MSG = new AlertDialog.Builder(LeaveGroup.this)
				.setIcon(R.drawable.ic_launcher).setTitle(title)
				.setMessage(message)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if(type==1){
							BasicDetails.setLeaderID("NOT SET");
							Intent home=new Intent(getApplicationContext(),HomeScreen.class);
							home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivityForResult(home, 110);
						}
						DIALOG_SHOW_MSG.dismiss();
					}
				}).create();
		DIALOG_SHOW_MSG.show();
	}

	@Override
		public void onBackPressed() {
			super.onBackPressed();
		}
	
	class PostToServer extends AsyncTask<String, Void, Boolean>{
		private String ERROR =null;
		ProgressDialog PG =null;
		private String RESULT_STRING;
		String URL;
		List<NameValuePair> nameValuePairs;
		private String Msg;
		public PostToServer(List<NameValuePair> nameValuePairs,String URL,String Msg) {
			this.URL=URL;
			this.nameValuePairs=nameValuePairs;
			this.Msg=Msg;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			RESULT_STRING = null;
			PG = new ProgressDialog(LeaveGroup.this);
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
				if(RESULT_STRING.equals("SUCCESS")){
					ShowThisMsg("DONE", "Request Completed...\nMyTeam will exit now, please restart and JOIN ANOTHER GROUP", 1);
				}else{
					ShowThisMsg("ERROR", "COULD NOT COMPLETE THE REQUEST AT THIS TIME...\nTRY AGAIN AFTER SOME TIME", 0);
				}
			}else{
				Toast.makeText(getApplicationContext(), "Error:"+ERROR, Toast.LENGTH_LONG).show();
			}
		}
	}

	
	
}
