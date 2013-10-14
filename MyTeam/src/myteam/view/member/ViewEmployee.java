package myteam.view.member;

import java.util.ArrayList;
import java.util.List;

import myteam.global.constants.BasicDetails;
import myteam.global.constants.Credits;
import myteam.global.constants.Help;
import myteam.global.constants.ParseXmlData;
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
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewEmployee extends Activity {
	private Button Performance=null;
	private Bundle bundleId = null;
	private String ReceivedID = null;
	private Document doc=null;
	private ImageView ProfilePic=null;
	private TextView Name_Txt=null;
	private TextView Designation_Txt=null;
	private TextView DOB_Txt=null;
	private TextView Gender_Txt=null;
	private TextView Addr_Txt=null;
	private TextView Mobile_Txt=null;
	private TextView email_Txt=null;
	private TextView Header=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_employee_details);
		MyTeam.currentContext=this;
		bundleId = getIntent().getExtras();
		if (bundleId == null) {
			Toast.makeText(getApplicationContext(),
					"SYSTEM ERROR...DINT RECEIVE ANY ID...", Toast.LENGTH_LONG)
					.show();
		}
		ReceivedID = bundleId.getString("SELECTEDID");
		Header=(TextView) findViewById(R.id.VED_Header);
		Header.setText("ID: "+ReceivedID);
		ProfilePic=(ImageView) findViewById(R.id.VED_ProfilePic);
		Name_Txt=(TextView) findViewById(R.id.VED_FullName);
		Designation_Txt=(TextView) findViewById(R.id.VED_Designation);
		DOB_Txt=(TextView) findViewById(R.id.VED_DOB);
		Gender_Txt=(TextView) findViewById(R.id.VED_Gender);
		Addr_Txt=(TextView) findViewById(R.id.VED_CompleteAddr);
		Mobile_Txt=(TextView) findViewById(R.id.VED_MobileNo);
		email_Txt=(TextView) findViewById(R.id.VED_EmailAddr);
		Performance=(Button) findViewById(R.id.VED_Performane_Btn);
		if(ReceivedID.equals(BasicDetails.getLeaderID())){
			Performance.setVisibility(8);
			//TODO extra....Check it
		}else{
			Performance.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					((MyTeam)getApplication()).PlayButtonSound();
					Intent perfrmnce=new Intent(getApplicationContext(),Performance.class);
					perfrmnce.putExtra("MEMBERID", ReceivedID);
					perfrmnce.putExtra("NAME",Name_Txt.getText().toString());
					startActivityForResult(perfrmnce, 110);
				}
			});
		}
				GetAndDisplayData();
	}

	private void GetAndDisplayData() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("ID", ReceivedID));
		nameValuePairs.add(new BasicNameValuePair("LeaderID", BasicDetails.getLeaderID()));
		new PostToServer(nameValuePairs,URL_Strings.getUrlMemberData(),"Loading..").execute();
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
			PG = new ProgressDialog(ViewEmployee.this);
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
				ParseXmlData xmlParser = new ParseXmlData();
				doc = xmlParser.getDomElement(RESULT_STRING);
				NodeList nl = doc.getElementsByTagName("values");
				Element e = (Element) nl.item(0);
				Name_Txt.setText(xmlParser.getValue(e, "FULLNAME"));
				String img=xmlParser.getValue(e, "PROFILEPIC");									//ProfilePic.setImageBitmap();
				System.out.println("Img: "+img);
					ProfilePic.setImageBitmap(MyTeam.StringToBitmap(img));
				ProfilePic.setMaxWidth(110);
				ProfilePic.setMinimumWidth(110);
				ProfilePic.setMaxHeight(110);
				ProfilePic.setMinimumHeight(110);
				
				DOB_Txt.setText("DOB: "+xmlParser.getValue(e, "DOB"));
				Gender_Txt.setText(xmlParser.getValue(e, "GENDER"));
				Mobile_Txt.setText("Mobile No: "+xmlParser.getValue(e, "MOBILENO"));
				email_Txt.setText("Email ID: "+xmlParser.getValue(e, "EMAILID"));
				Designation_Txt.setText(xmlParser.getValue(e, "DESIGNATION"));
				Addr_Txt.setText(xmlParser.getValue(e, "ADDRESS")+"\n"+xmlParser.getValue(e, "CITY")+"\n"+xmlParser.getValue(e, "STATE"));	
			}else{
				Toast.makeText(getApplicationContext(), "Error:"+ERROR, Toast.LENGTH_LONG).show();
			}
		}
	}

	
}
