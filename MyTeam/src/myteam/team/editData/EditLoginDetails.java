package myteam.team.editData;

import java.util.ArrayList;
import java.util.List;

import myteam.global.constants.BasicDetails;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditLoginDetails extends Activity {
	private Button Back = null;
	private Button Submit = null;
	private EditText Pass = null;
	private EditText PassRe = null;
	private AlertDialog DIALOG_SHOW_MSG;
	private String UserName = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter_new_password_screen);
		MyTeam.currentContext=this;

		UserName = BasicDetails.getCurrentID();
		Pass = (EditText) findViewById(R.id.ENPS_Password_Txt);
		Pass.addTextChangedListener(MyTeam.EditTextSound);
		PassRe = (EditText) findViewById(R.id.ENPS_RePassword_Txt);
		PassRe.addTextChangedListener(MyTeam.EditTextSound);
		Back = (Button) findViewById(R.id.ENPS_Back_Btn);
		Back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				onBackPressed();
			}
		});
		Submit = (Button) findViewById(R.id.ENPS_Submit_Btn);
		Submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				if (Pass.getText().toString().trim().length() == 0) {
					Pass.setError("EMPTY FIELD");
				} else if (PassRe.getText().toString().trim().length() == 0) {
					PassRe.setError("EMPTY FIELD");
				} else if (!Pass.getText().toString()
						.equals(PassRe.getText().toString())) {
					ShowThisMsg("Error:	",
							"Password and Re-Entered Password do NOT match",
							false);
				} else {
					StoreNewPassword();
				}
			}
		});
	}

	private void StoreNewPassword() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair(ServerLabels.getFirstLabel(),
				ServerLabels.getForgetPassword3()));
		nameValuePairs.add(new BasicNameValuePair("USERNAME", UserName));
		nameValuePairs.add(new BasicNameValuePair("PASSWORD", Pass.getText()
				.toString()));
		new PostToServer(nameValuePairs, URL_Strings.getUrlForgetPassword(),"Updating..").execute();
	}

	protected void ShowThisMsg(String title, String message, final boolean quit) {
		DIALOG_SHOW_MSG = new AlertDialog.Builder(EditLoginDetails.this)
				.setIcon(R.drawable.ic_launcher).setTitle(title)
				.setMessage(message)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (quit) {
							onBackPressed();
						}
						DIALOG_SHOW_MSG.dismiss();
					}
				}).create();
		DIALOG_SHOW_MSG.show();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
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
			PG = new ProgressDialog(EditLoginDetails.this);
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
				if (RESULT_STRING.equalsIgnoreCase("Done")) {
					ShowThisMsg("Successfull",
							"NEW PASSWORD HAS BEEN SET..", true);
				} else {
					ShowThisMsg("Error:	", "Some Error has OCCURE: "
							+ RESULT_STRING, false);
				}
			}else{
				Toast.makeText(getApplicationContext(), "Error:"+ERROR, Toast.LENGTH_LONG).show();
			}
		}
	}

	
}
