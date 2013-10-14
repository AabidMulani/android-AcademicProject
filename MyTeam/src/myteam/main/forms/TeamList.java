package myteam.main.forms;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import myteam.global.constants.BasicDetails;
import myteam.global.constants.Credits;
import myteam.global.constants.Help;
import myteam.global.constants.ParseXmlData;
import myteam.global.constants.URL_Strings;
import myteam.view.member.ViewEmployee;

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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TeamList extends Activity {
	private Button ChatButton = null;
	private Button MenuHome = null;
	private Button MenuTeam = null;
	private Button MenuTask = null;
	private Button MenuMore = null;
	private LinearLayout list_layout=null;
	private Map<String, String> TeamDataMap = new HashMap<String, String>();
	private NodeList nl = null;
	private Document doc=null;
	private ParseXmlData xmlParser = new ParseXmlData();
	private AlertDialog DIALOG_SHOW_MSG;
	private Dialog AFTER_DIALOG=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.team_list_view);
		MenuLayoutInitialization();
		list_layout=(LinearLayout) findViewById(R.id.TLV_ListLayout);
		MyTeam.currentContext=this;
		((MyTeam)getApplication()).teamlist=this;
		GetTeam();
	}
	
	private void GetTeam() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("GroupID",BasicDetails.getLeaderID()));
		nameValuePairs.add(new BasicNameValuePair("MemberID",BasicDetails.getCurrentID()));
		new PostToServer(nameValuePairs,URL_Strings.getUrlTeamList(),"Processing..").execute();
		//ShowThisMsg("XML data", xmlData, 0);
	}
	
	protected void AfterGetTeam(String xmlData){
		doc = xmlParser.getDomElement(xmlData);
		nl = doc.getElementsByTagName("values");
		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			TeamDataMap.put("ID", xmlParser.getValue(e, "ID"));
			TeamDataMap.put("NAME", xmlParser.getValue(e, "Name"));
			TeamDataMap.put("PROFILEPIC", xmlParser.getValue(e, "ProfilePic"));
			TeamDataMap.put("MOBILENO", xmlParser.getValue(e, "MobileNo"));
			TeamDataMap.put("EMAILID", xmlParser.getValue(e, "EmailID"));
			TeamDataMap.put("DESIGNATION", xmlParser.getValue(e, "Designation"));
			list_layout.addView(AddThisMember(TeamDataMap));
		}
	}
	
	protected void ShowThisMsg(String title, String message, final int type) {
		DIALOG_SHOW_MSG = new AlertDialog.Builder(TeamList.this)
				.setIcon(R.drawable.ic_launcher).setTitle(title)
				.setMessage(message)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						DIALOG_SHOW_MSG.dismiss();
					}
				}).create();
		DIALOG_SHOW_MSG.show();
	}

	private View AddThisMember(final Map<String, String> TempData) {
		final String TempID=TempData.get("ID");
		final String TempName=TempData.get("NAME");
		final String TempMob=TempData.get("MOBILENO");
		final String TempEmail=TempData.get("EMAILID");
		final String TempDesig=TempData.get("DESIGNATION");
		final String TempPic=TempData.get("PROFILEPIC");
		LayoutInflater minflate = LayoutInflater.from(getApplicationContext());
		final View convertView = minflate.inflate(R.layout.member_inflater_list, null);
		TextView Name = (TextView) convertView.findViewById(R.id.MIL_Name_txt);
		Name.setText(TempData.get("NAME"));
		TextView Desig = (TextView) convertView.findViewById(R.id.MIL_Designation_txt);
		Desig.setText(TempData.get("DESIGNATION"));
		ImageView Image1 = (ImageView) convertView.findViewById(R.id.MIL_image);
		Image1.setImageBitmap(MyTeam.StringToBitmap(TempPic));
		System.out.println("Mil_image:"+TempPic);
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				AfterSelectedDialog(TempID,TempName,TempMob,TempEmail,TempDesig,TempPic);
			}
		});
		return convertView;
	}

	
	
	protected void AfterSelectedDialog(final String ID, String Name,final String Mob, final String Email, String Desig, String Pic) {
		  LayoutInflater factory = LayoutInflater.from(this);
          final View proceedBox = factory.inflate(R.layout.team_proceed_dialog_layout, null);
          TextView mobtxt=(TextView) proceedBox.findViewById(R.id.TPDL_mobile_txt);
          mobtxt.setText(Mob);
          final TextView emailtxt=(TextView) proceedBox.findViewById(R.id.TPDL_mail_txt);
          emailtxt.setText(Email);
          LinearLayout callLayout=(LinearLayout) proceedBox.findViewById(R.id.TPDL_Call_Layout);
          LinearLayout mailLayout=(LinearLayout) proceedBox.findViewById(R.id.TPDL_Email_Layout);
          callLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				try{
				Intent callint= new Intent(Intent.ACTION_CALL);
				callint.setData(Uri.parse("tel:"+Mob));
				startActivity(callint);
				AFTER_DIALOG.dismiss();
				}catch(Exception ex){
					Toast.makeText(getApplicationContext(), "CALL NOT COMPLETE"+ex, Toast.LENGTH_LONG).show();
				}
			}
		});
          mailLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				if(emailtxt.getText().toString().equals("NOT SET")){
					Toast.makeText(getApplicationContext(), "EMAIL NOT SET...", Toast.LENGTH_LONG).show();
				}else{
					Intent intent = new Intent(Intent.ACTION_SEND);
					intent.setType("text/html");
					intent.putExtra(Intent.EXTRA_EMAIL,Email);
//					intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
//					intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.");

					startActivity(Intent.createChooser(intent, "Send Email"));
				}
			}
		});
          
          AFTER_DIALOG= new AlertDialog.Builder(TeamList.this)
              .setTitle(Name)
              .setView(proceedBox)
              .setPositiveButton("VIEW DETAILS", new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int whichButton) {
                	  Intent showDetails= new Intent(getApplicationContext(),ViewEmployee.class);
                	  showDetails.putExtra("SELECTEDID", ID);
                	  startActivityForResult(showDetails, 110);
                  }
              })
              .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int whichButton) {
                	  AFTER_DIALOG.dismiss();
                  }
              })
              .create();
          AFTER_DIALOG.show();
	}

	
	
	private void MenuLayoutInitialization() {
		ChatButton=(Button) findViewById(R.id.TLV_chat_button);
		MenuHome=(Button) findViewById(R.id.TLV_Home_Btn);
		MenuTeam=(Button) findViewById(R.id.TLV_Team_Btn);
		MenuTask=(Button) findViewById(R.id.TLV_Task_Btn);
		MenuMore=(Button) findViewById(R.id.TLV_More_Btn);
		ChatButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				Intent chat = new Intent(getApplicationContext(),
						ChatScreen.class);
				chat.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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
		MenuTeam.setEnabled(false);// SET THE SAME SCREEN BUTTON DISABLED..
		MenuTeam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				Intent PageChange=new Intent(getApplicationContext(),TeamList.class);
				PageChange.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(PageChange, 110);
			}
		});
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
		
		public PostToServer(List<NameValuePair> nameValuePairs,String URL,String Msg) {
			this.URL=URL;
			this.nameValuePairs=nameValuePairs;
			this.Msg=Msg;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			RESULT_STRING = null;
			PG = new ProgressDialog(TeamList.this);
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
				AfterGetTeam(RESULT_STRING);
			}else{
				Toast.makeText(getApplicationContext(), "Error:"+ERROR, Toast.LENGTH_LONG).show();
			}
		}
	}

}
