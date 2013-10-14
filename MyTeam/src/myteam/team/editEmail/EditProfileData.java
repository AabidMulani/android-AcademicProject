package myteam.team.editEmail;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import myteam.global.constants.BasicDetails;
import myteam.global.constants.Credits;
import myteam.global.constants.GmailLoginThread;
import myteam.global.constants.Help;
import myteam.global.constants.ServerLabels;
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
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class EditProfileData extends Activity {
	private Button Back_Btn = null;
	private Button Submit_Btn = null;
	private EditText email_Id = null;
	private EditText password = null;
	private ImageView ProfilePic = null;
	private String imageString=null;
	private AlertDialog DIALOG_SHOW_MSG;
	private String email,pass;
	private BasicDetails basicdetails= new BasicDetails();
	private String OldUser,OldPass;
	private boolean DoRestart=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.email_details_screen);
		MyTeam.currentContext=this;
		email_Id = (EditText) findViewById(R.id.EDS_username_txt);
		OldUser=BasicDetails.getEmailID();
		email_Id.setText(OldUser);
		email_Id.addTextChangedListener(MyTeam.EditTextSound);
		password = (EditText) findViewById(R.id.EDS_PASSWORD_txt);
		OldPass=basicdetails.getEmailPass();
		password.setText(OldPass);
		password.addTextChangedListener(MyTeam.EditTextSound);

		Back_Btn = (Button) findViewById(R.id.EDS_Back_Btn);
		Submit_Btn = (Button) findViewById(R.id.EDS_Proceed_Btn);
		ProfilePic = (ImageView) findViewById(R.id.EDS_porfile_image);
		ProfilePic.setImageBitmap(BasicDetails.getProfilePic());
		ProfilePic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				openGallery();
			}
		});

		Back_Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				if (BasicDetails.isLoginDone()) {
					onBackPressed();
				} else {
					ShowThisMsg("Exiting..", "MyTeam will exit now...", 2);
				}
			}
		});

		Submit_Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				if (email_Id.getText().toString().trim().length() == 0) {
					email_Id.setError("EMPTY FIELD");
				} else if (password.getText().toString().trim().length() == 0) {
					password.setError("EMPTY FIELD");
				} else {
					CheckLogin(email_Id.getText().toString(), password
							.getText().toString());
				}
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
		case R.id.other_menu_credits:
			startActivityForResult(new Intent(getApplicationContext(), Credits.class), 110);
		break;
		case R.id.other_menu_help:
			startActivityForResult(new Intent(getApplicationContext(), Help.class), 110);
		break;
		}
		return true;
	}

	private void CheckLogin(String user, String pass) {
		if(imageString==null){
			imageString=BasicDetails.getProfileString();
		}
		if(user.equals(OldUser) && pass.equals(OldPass)){
			DoRestart=false;
			ShowThisMsg("Note:", "Update Profile Pic Only...", 3);
		}else{
			DoRestart=true;
//			new GmailCheck().execute();
			DoCheckLogin(user, pass);
		}
	}
	
	private void DoCheckLogin(String user, String pass) {
		GmailLoginThread gmailLogin=new GmailLoginThread(user, pass);
		try{
			if (gmailLogin.execute().get().equals("DONE")) {
				ShowThisMsg("DONE..!", "Login Successfull..", 3);
			}else{
				ShowThisMsg("Error", "UNABLE TO LOGIN..../n", 0);
		}}catch(Exception ex){
			ShowThisMsg("Error", "UNABLE TO LOGIN..../n"+ ex, 0);
		}

	}

	protected void ShowThisMsg(String title, String message, final int type) {
		DIALOG_SHOW_MSG = new AlertDialog.Builder(EditProfileData.this)
				.setIcon(R.drawable.ic_launcher).setTitle(title)
				.setMessage(message)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						switch (type) {
						case 1:
							DIALOG_SHOW_MSG.dismiss();
							if(BasicDetails.isLoginDone()){
								onBackPressed();
								//TODO.... changed....Please check
							}else{
								Intent home=new Intent(getApplicationContext(),HomeScreen.class);
								home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivityForResult(home, 110);
							}
								
							break;
						case 2:
							Toast.makeText(getApplicationContext(),
									"Now MyTeam will exit...",
									Toast.LENGTH_LONG).show();
							EditProfileData.this.finish();
							((MyTeam) getApplication()).LogOut();
							break;
						case 3:
							List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
									5);
							nameValuePairs.add(new BasicNameValuePair(
									ServerLabels.getFirstLabel(), "UPDATE"));
							email = email_Id.getText().toString();
							pass = password.getText().toString();
							nameValuePairs.add(new BasicNameValuePair("ID",
									BasicDetails.getCurrentID()));
							nameValuePairs.add(new BasicNameValuePair(
									"EMAILID", email));
							nameValuePairs.add(new BasicNameValuePair(
									"PASSWORD", pass));
							if(imageString==null){
								nameValuePairs.add(new BasicNameValuePair(
										"PROFILEPIC", "NULL"));
										System.out.println("GOING TO SERVER:NULL");
							}else{
								nameValuePairs.add(new BasicNameValuePair(
										"PROFILEPIC", imageString));
								System.out.println("GOING TO SERVER:"+imageString);
								
							}
							BasicDetails.ProfilePic=MyTeam.StringToBitmap(imageString);
							new PostToServer(nameValuePairs,URL_Strings.getUrlAddEmail(),"Updating..",DoRestart).execute();
							break;
						default:
							DIALOG_SHOW_MSG.dismiss();
						}
					}
				}).create();
		DIALOG_SHOW_MSG.show();
	}

	public void openGallery(){
    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
//        intent.putExtra("crop", "true");
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        intent.putExtra("outputX", 100);
//        intent.putExtra("outputY", 100);

        try {
                intent.putExtra("return-data", true);
                startActivityForResult(intent, 1008);
        } catch (ActivityNotFoundException e) {
        	Log.e(this.getClass().getName(), "Exception while image intent called");
        }
   }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	 
    	if (resultCode == RESULT_OK && requestCode == 1008) {
            Bundle extras = data.getExtras();
            Bitmap thePic = extras.getParcelable("data");
            ProfilePic.setImageBitmap(thePic);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            thePic.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            imageString = Base64.encodeToString(b, Base64.DEFAULT);

    }
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
		boolean restart;
		public PostToServer(List<NameValuePair> nameValuePairs,String URL,String Msg,boolean restart) {
			this.URL=URL;
			this.nameValuePairs=nameValuePairs;
			this.Msg=Msg;
			this.restart=restart;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			RESULT_STRING = null;
			PG = new ProgressDialog(EditProfileData.this);
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
				if (RESULT_STRING.equalsIgnoreCase("CORRECT")) {
					if(restart){
					BasicDetails.setLoginDone(false);
					BasicDetails.setEmailID(email);
					BasicDetails.setEmailPass(pass);
					ShowThisMsg(
							"INFORMATION..",
							"Application will restart now..",
							1);
					}
					else{
						BasicDetails.setEmailID(email);
						BasicDetails.setEmailPass(pass);
						ShowThisMsg(
								"INFORMATION..",
								"Profile Pic Updated..",
								1);
					}
				} else {
					Toast.makeText(
							getApplicationContext(),
							"PROBLEM WHILE STORING ON SERVER..."
									+ RESULT_STRING, Toast.LENGTH_LONG)
							.show();
					Back_Btn.performClick();
				}
			}else{
				Toast.makeText(getApplicationContext(), "Error:"+ERROR, Toast.LENGTH_LONG).show();
			}
		}
	}
	
	class GmailCheck extends AsyncTask<String, Void, Boolean>{
		private String ERROR =null;
		ProgressDialog PG =null;
		private String RESULT_STRING;
		String URL;
		List<NameValuePair> nameValuePairs;
		private String Msg;
		
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			RESULT_STRING = null;
			PG = new ProgressDialog(EditProfileData.this);
			PG.setCancelable(false);
			PG.setMessage(Msg);
			PG.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			PG.show();
		}

		@Override
		protected Boolean doInBackground(String... arg0) {
			try {
				GmailLoginThread gmailLogin = new GmailLoginThread(email, pass);
				RESULT_STRING=gmailLogin.execute().get();
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
				try {
					if (RESULT_STRING.equals("DONE")) {
						ShowThisMsg("DONE..!", "Login Successfull..", 3);
					} else {
						ShowThisMsg("Error", "UNABLE TO LOGIN..../n", 0);
					}
				} catch (Exception ex) {
					
				}
			}else{
				ShowThisMsg("Error", "UNABLE TO LOGIN..../n" + ERROR, 0);
			}
		}
	}

}
