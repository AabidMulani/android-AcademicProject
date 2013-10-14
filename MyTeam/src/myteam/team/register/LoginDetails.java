package myteam.team.register;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class LoginDetails extends Activity {
	private RelativeLayout Pass_Layout = null;
	private EditText UserName = null;
	private EditText Password = null;
	private EditText RePassword = null;
	private Button CheckAvail = null;
	private Button Submit_Btn = null;
	private AlertDialog DIALOG_SHOW_MSG;
	private boolean Authenticate = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_details_screen);
		UserName = (EditText) findViewById(R.id.LDS_username_txt);
		UserName.addTextChangedListener(MyTeam.EditTextSound);

		CheckAvail = (Button) findViewById(R.id.LDS_check_availibility);
		Submit_Btn = (Button) findViewById(R.id.LDS_Submit_Btn);
		Password = (EditText) findViewById(R.id.LDS_Password_txt);
		Password.addTextChangedListener(MyTeam.EditTextSound);

		RePassword = (EditText) findViewById(R.id.LDS_RePassword_txt);
		RePassword.addTextChangedListener(MyTeam.EditTextSound);

		Pass_Layout = (RelativeLayout) findViewById(R.id.LDS_password_layout);
		Pass_Layout.setVisibility(8);
		UserName.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				MyTeam.sound_type.start();				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				UnAuthenticate();				
			}
		});
		
		CheckAvail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				if (UserName.getText().toString().trim().length() == 0) {
					UserName.setError("EMPTY FIELD");
				} else {
					CheckUserAvailibilty();
				}
			}
		});

		Submit_Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				if (Password.getText().toString().trim().length() == 0) {
					Password.setError("EMPTY FIELDS");
				} else if (RePassword.getText().toString().trim().length() == 0) {
					RePassword.setError("EMPTY FIELD");
				} else if (!Password.getText().toString()
						.equals(RePassword.getText().toString())) {
					ShowThisMsg(
							"Error",
							"Password does NOT match with the ReEnter Password..",
							false);
				} else {
					EmployeeValues.setUsername(UserName.getText().toString());
					FinalSubmition();
				}
			}
		});
	}

	private void UnAuthenticate() {
		Authenticate = false;
		Pass_Layout.setVisibility(8);
		CheckAvail.setText("Check Availibility");
	}

	private void CheckUserAvailibilty() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
				2);
		nameValuePairs.add(new BasicNameValuePair(ServerLabels
				.getFirstLabel(), ServerLabels
				.getTypeUsernameAvailibility()));
		nameValuePairs.add(new BasicNameValuePair("USERNAME", UserName
				.getText().toString()));
		new PostToServer(nameValuePairs, URL_Strings.getUrlNewUserPage(),"Processing..",1).execute();

	}

		protected void ShowThisMsg(String title, String message, final boolean quit) {
		DIALOG_SHOW_MSG = new AlertDialog.Builder(LoginDetails.this)
				.setIcon(R.drawable.ic_launcher).setTitle(title)
				.setMessage(message)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (quit) {
								Intent in=new Intent(getApplicationContext(),LoginPage.class);
								in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivityForResult(in, 110);
						}
						DIALOG_SHOW_MSG.dismiss();
					}
				}).create();
		DIALOG_SHOW_MSG.show();
	}

	private void FinalSubmition() {
		Log.d("FinalSubmition", Authenticate+"");
		if (Authenticate) {
			AddToServer();
		}else{
			Toast.makeText(getApplicationContext(), "Authentication is NEGATIVE\nTry Again", Toast.LENGTH_LONG).show();
		}
	}

	private void AddToServer() {
				String[] Labels = ServerLabels.getEnteremployeelabels();
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						Labels.length + 1);
				nameValuePairs.add(new BasicNameValuePair(ServerLabels
						.getFirstLabel(), ServerLabels
						.getEnterEmployeeDetails()));
				nameValuePairs.add(new BasicNameValuePair(Labels[0],
						EmployeeValues.getType()));
				nameValuePairs.add(new BasicNameValuePair(Labels[1],
						EmployeeValues.getUsername()));
				nameValuePairs.add(new BasicNameValuePair(Labels[2], Password
						.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair(Labels[3],
						EmployeeValues.getFirst_Name()));
				nameValuePairs.add(new BasicNameValuePair(Labels[4],
						EmployeeValues.getLast_Name()));
				nameValuePairs.add(new BasicNameValuePair(Labels[5],
						EmployeeValues.getGender()));
				nameValuePairs.add(new BasicNameValuePair(Labels[6],
						EmployeeValues.getDesignation()));
				nameValuePairs.add(new BasicNameValuePair(Labels[7],
						EmployeeValues.getDOB()));
				nameValuePairs.add(new BasicNameValuePair(Labels[8],
						EmployeeValues.getMobile_No()));
				nameValuePairs.add(new BasicNameValuePair(Labels[9],
						EmployeeValues.getAddress()));
				nameValuePairs.add(new BasicNameValuePair(Labels[10],
						EmployeeValues.getCity()));
				nameValuePairs.add(new BasicNameValuePair(Labels[11],
						EmployeeValues.getState()));
				nameValuePairs.add(new BasicNameValuePair(Labels[12],
						EmployeeValues.getQuestion()));
				nameValuePairs.add(new BasicNameValuePair(Labels[13],
						EmployeeValues.getAnswer()));
				new PostToServer(nameValuePairs, URL_Strings.getUrlNewUserPage(),"Porcessing..",2).execute();
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
			PG = new ProgressDialog(LoginDetails.this);
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
					if (RESULT_STRING.equalsIgnoreCase("VALID")) {
						Authenticate = true;
						Pass_Layout.setVisibility(0);
						CheckAvail.setText("DONE");
					} else if (RESULT_STRING.equalsIgnoreCase("INVALID")) {
						UserName.setError("USERNAME UNAVAILABLE");
					} else {
						ShowThisMsg("ERROR", RESULT_STRING, false);
					}
					break;
				case 2:
					if (RESULT_STRING.equalsIgnoreCase("SUCCESSFULL")) {
						ShowThisMsg("Done..!",
								"REGISTRATION: Successfull;\n WELCOME TO MyTeam: "
										+ EmployeeValues.getUsername(), true);
					} else if (RESULT_STRING.equalsIgnoreCase("UNSUCCESSFULL")) {
						ShowThisMsg(
								"ERROR",
								"Some Server Side porblem has occur...\nPlease Try Again...\nOR CONTACT ADMINISTRATOR..!",
								false);
					} else {
						ShowThisMsg("ERROR",
								"OYE...!\nTHIS IS AN UNEXPECTED ERROR...."+RESULT_STRING, false);
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
