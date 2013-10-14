package myteam.team.editData;

import java.util.ArrayList;
import java.util.List;

import myteam.global.constants.BasicDetails;
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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class EditMemberScreen extends Activity {
private Button personalDetails=null;
private Button contactDetails=null;
private Button update_btn=null;
private AlertDialog DIALOG_SHOW_MSG;
private Document doc=null;
private ParseXmlData xmlParser=new ParseXmlData();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_personal_details);
		if(!ServerMemberData.isIsDataPresent()){
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair(ServerLabels
				.getFirstLabel(), "GetData"));
		nameValuePairs.add(new BasicNameValuePair("LeaderID", BasicDetails.getLeaderID()));
		nameValuePairs.add(new BasicNameValuePair("MemberID", BasicDetails.getCurrentID()));
		new PostToServer(nameValuePairs,URL_Strings.getUrlNewUserPage(),"Loading Previous Data..",1).execute();
		}

		personalDetails=(Button) findViewById(R.id.EPD_Personal_details);
		contactDetails=(Button) findViewById(R.id.EPD_Contact_detils);
		update_btn=(Button) findViewById(R.id.EPD_Submit);
		
		personalDetails.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				Intent editId=new Intent(getApplicationContext(),EditPersonalDetails.class);
				startActivityForResult(editId, 110);
			}
		});
		
		
		contactDetails.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				Intent editId=new Intent(getApplicationContext(),EditContactDetails.class);
				startActivityForResult(editId, 110);				
			}
		});
		
		
		update_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				if(ServerMemberData.CheckIfChanged()){
					AddToServer();
				}else{
					ShowThisMsg("No Change Done!", "No Data is Updated... ", true);
				}
			}
		});
	}

	private void GetAllDetails(String xml) {
		try {
			doc = xmlParser.getDomElement(xml);
			NodeList nl = doc.getElementsByTagName("values");
			Element e = (Element) nl.item(0);
			nl.getLength();
			ServerMemberData.setAddress(xmlParser.getValue(e, "Address"));
			ServerMemberData.setCity(xmlParser.getValue(e, "City"));
			ServerMemberData.setDOB(xmlParser.getValue(e, "DOB"));
			ServerMemberData.setDesignation(xmlParser.getValue(e, "Designation"));
			ServerMemberData.setFirst_Name(xmlParser.getValue(e, "FirstName"));
			ServerMemberData.setLast_Name(xmlParser.getValue(e, "LastName"));
			ServerMemberData.setMobile_No(xmlParser.getValue(e, "MobileNo"));
			ServerMemberData.setState(xmlParser.getValue(e, "State"));
			ServerMemberData.setIsDataPresent(true);
		} catch (Exception ex) {
			Toast.makeText(getApplicationContext(), ex.toString(),
					Toast.LENGTH_LONG).show();
				ShowThisMsg("ERROR:", "FAILED TO RECEIVE DATA FROM SERVER...", true);
		}
	}

	protected void ShowThisMsg(String title, String message, final boolean quit) {
		DIALOG_SHOW_MSG = new AlertDialog.Builder(EditMemberScreen.this)
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
	
	private void AddToServer() {
		String[] Labels = ServerLabels.getEnteremployeelabels();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(11);
		nameValuePairs.add(new BasicNameValuePair(ServerLabels
				.getFirstLabel(), "SetData"));
		nameValuePairs.add(new BasicNameValuePair("MemberID",
				BasicDetails.getCurrentID()));
		nameValuePairs.add(new BasicNameValuePair("LeaderID",
				BasicDetails.getLeaderID()));
		nameValuePairs.add(new BasicNameValuePair(Labels[3],
				ServerMemberData.getNewFirst_Name()));
		nameValuePairs.add(new BasicNameValuePair(Labels[4],
				ServerMemberData.getNewLast_Name()));
		nameValuePairs.add(new BasicNameValuePair(Labels[6],
				ServerMemberData.getNewDesignation()));
		nameValuePairs.add(new BasicNameValuePair(Labels[7],
				ServerMemberData.getNewDOB()));
		nameValuePairs.add(new BasicNameValuePair(Labels[8],
				ServerMemberData.getNewMobile_No()));
		nameValuePairs.add(new BasicNameValuePair(Labels[9],
				ServerMemberData.getNewAddress()));
		nameValuePairs.add(new BasicNameValuePair(Labels[10],
				ServerMemberData.getNewCity()));
		nameValuePairs.add(new BasicNameValuePair(Labels[11],
				ServerMemberData.getNewState()));
		new PostToServer(nameValuePairs, URL_Strings.getUrlNewUserPage(),"Updating Data..",2).execute();
}

	@Override
		public void onBackPressed() {
			super.onBackPressed();
			ServerMemberData.setIsDataPresent(false);
			this.finish();
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
			PG = new ProgressDialog(EditMemberScreen.this);
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
				case 1:GetAllDetails(RESULT_STRING);
					break;
				case 2:
					if(RESULT_STRING.equals("SUCCESS")){
						ShowThisMsg("SUCCESS", "DATA IS UPDATED...", true);
					}else{
						ShowThisMsg("ERROR", "SOME SERVER SIDE ERROR", false);
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
