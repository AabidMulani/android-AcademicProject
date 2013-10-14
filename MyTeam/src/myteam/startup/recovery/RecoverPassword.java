package myteam.startup.recovery;

import java.util.ArrayList;
import java.util.List;

import myteam.global.constants.ServerLabels;
import myteam.global.constants.URL_Strings;
import myteam.main.forms.MyTeam;
import myteam.main.forms.R;
import myteam.startup.pages.LoginPage;

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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RecoverPassword extends Activity {
	private Button Back = null;
	private Button Proceed = null;
	private EditText UserName = null;
	private EditText Answer = null;
	private TextView Question = null;
	private RelativeLayout layout = null;
	private AlertDialog DIALOG_SHOW_MSG = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forget_password_screen);
		MyTeam.currentContext=this;
		UserName = (EditText) findViewById(R.id.FPS_UserName_Txt);
		UserName.addTextChangedListener(MyTeam.EditTextSound);
		Answer = (EditText) findViewById(R.id.FPS_Answer_Txt);
		Answer.addTextChangedListener(MyTeam.EditTextSound);
		layout = (RelativeLayout) findViewById(R.id.FPS_Question_Relative_layout);
		Question = (TextView) findViewById(R.id.FPS_Question_Txt);
		Back = (Button) findViewById(R.id.FPS_Back_Btn);
		Back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});

		Proceed = (Button) findViewById(R.id.FPS_Proceed_Btn);
		Proceed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (UserName.isEnabled()) {
					if (UserName.getText().toString().trim().length() == 0) {
						UserName.setError("EMPTY FIELD");
					} else {
						GetQuestion();
					}
				} else {
					if (Answer.getText().toString().trim().length() == 0) {
						Answer.setError("EMPTY FIELD");
					} else {
						CheckAnswer();
					}
				}
			}
		});
	}

	protected void GetQuestion() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair(
				ServerLabels.getFirstLabel(), ServerLabels
						.getForgetPassword1()));
		nameValuePairs.add(new BasicNameValuePair("USERNAME", UserName
				.getText().toString()));
		new PostToServer(nameValuePairs,URL_Strings.getUrlForgetPassword(),"Authenticating..",1).execute();
	}

	protected void AfterGetQuestion(String Status){
		if (Status.equalsIgnoreCase("NO USER")) {
			ShowThisMsg(
					"Error:",
					"No Account available with UserName, please re-check the UserName.. ",
					false);
		} else  if (Status.equalsIgnoreCase("ERROR")){
			ShowThisMsg(
					"Error:",
					"THIS IS AN UNEXPECTED ERROR.."+ Status,
					false);
		}else{
			ShowThisMsg(
					"Error:",
					"ERROR NEW: "+Status,
					false);
			layout.setVisibility(0);
			UserName.setEnabled(false);
			Question.setText(Status.toString());
		}
	}
	protected void CheckAnswer() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair(
				ServerLabels.getFirstLabel(), ServerLabels
						.getForgetPassword2()));
		nameValuePairs.add(new BasicNameValuePair("USERNAME", UserName.getText()
				.toString()));
		nameValuePairs.add(new BasicNameValuePair("ANSWER", Answer.getText()
				.toString()));
		new PostToServer(nameValuePairs,URL_Strings.getUrlForgetPassword(),"Processing..",2).execute();
	}

	protected void AfterCheckAnswer(String Status){
		if (Status.equals("WrongAnswer")) {
			ShowThisMsg(
					"Error:",
					"Wrong ANSWER...Try again with correct answer..",
					false);
		} else if (Status.equals("RightAnswer")) {
			ShowThisMsg(
					"Verified..!",
					"Verification successfull...\nPress OK and set NEW PASSWORD.",
					true);
		}else{
			ShowThisMsg(
					"Error:",
					"THIS IS AN UNEXPECTED ERROR.."+ Status,
					false);
		}
	}
	
	protected void ShowThisMsg(String title, String message, final boolean quit) {
		DIALOG_SHOW_MSG = new AlertDialog.Builder(RecoverPassword.this)
				.setIcon(R.drawable.ic_launcher).setTitle(title)
				.setMessage(message)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (quit) {
							Intent passChange = new Intent(
									getApplicationContext(),
									SetNewPassword.class);
							passChange.putExtra("USERNAME", UserName.getText()
									.toString());
							startActivity(passChange);
						}
						DIALOG_SHOW_MSG.dismiss();
					}
				}).create();
		DIALOG_SHOW_MSG.show();
	}

	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent login=new Intent(getApplicationContext(), LoginPage.class);
		login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(login, 110);
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
			PG = new ProgressDialog(RecoverPassword.this);
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
				case 1:AfterGetQuestion(RESULT_STRING);
					break;
				case 2:AfterCheckAnswer(RESULT_STRING);
					break;
				default:Toast.makeText(getApplicationContext(), "Done"+RESULT_STRING, Toast.LENGTH_LONG).show();
				}
			}else{
				Toast.makeText(getApplicationContext(), "Error:"+ERROR, Toast.LENGTH_LONG).show();
			}
		}
	}

	
	
}
