package myteam.task.insert;

import java.util.ArrayList;
import java.util.List;

import myteam.global.constants.BasicDetails;
import myteam.global.constants.ParseXmlData;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ShowMembersList extends Activity {
	Dialog DIALOG_SHOW_MSG;
	private static final int TaskMode = 0;
	private String TaskModeArray[] = new String[] { "Individual Task",
			"Group Task" };
	String[] EnterTaskDetails = new String[] { "Title", "Subject", "Type",
			"Description", "Location", "StartDate", "EndDate", "MemberCount" };
	String value[] = new String[50];
	private String Title=null;
	private String Subject=null;
	private String Desc=null;
	private String StartDt=null;
	private String EndDt=null;
	private String Locatn=null;
	
	private int TaskModeValue;
	private String CHECK = "SELECT ALL";
	private String UNCHECK = "CLEAR SELECTION";
	CheckBox MemberCheckBox[] = new CheckBox[50];
	boolean Is_Member_Selected[] = new boolean[50];
	private String MemberID[] = new String[50];
	private String EmailID[]=new String [50];
	int Count;
	int Length;
	int SelectedMemberCount;

	private TextView Header = null;
	private Button Back_Btn = null;
	private Button Next_Btn = null;
	private Button SelectAll_Btn = null;
	private LinearLayout ScrollLayout = null;
	private Dialog SelectMode = null;
	private Bundle receivedData;
	private Document doc=null;
	private NodeList nl=null;
	private ParseXmlData xmlParser = new ParseXmlData();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_allocated_members_list);
		MyTeam.currentContext=this;
		receivedData = getIntent().getExtras();
		if (receivedData == null) {
			Toast.makeText(this, "KHATARNAAK ERROR HAI BE....",
					Toast.LENGTH_SHORT).show();
			return;
		}
		Title = receivedData.getString("Title");
		Subject = receivedData.getString("Subject");
		Desc= receivedData.getString("Desc");
		Locatn= receivedData.getString("Location");
		StartDt = receivedData.getString("StartDate");
		EndDt = receivedData.getString("EndDate");
		//ShowThisMsg("Bundle", Title+"\n"+Subject+"\n"+Desc+"\n"+Locatn+"\n"+StartDt+"\n"+EndDt, 0);///COMENT IT
		SelectedMemberCount = 0;
		Header = (TextView) findViewById(R.id.SAML_Heading);
		Header.setText("Select Members");
		Back_Btn = (Button) findViewById(R.id.SAML_BackBtn);
		Back_Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				onBackPressed();
			}
		});

		Next_Btn = (Button) findViewById(R.id.SAML_NextBtn);
		Next_Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				if (SelectedMemberCount == 0) {
					Toast.makeText(getApplicationContext(),
							"NO MEMBERS SELECTED....",
							Toast.LENGTH_SHORT).show();
				} else {
					if (SelectedMemberCount == 1) {
						TaskModeValue = 0;
						Bundle_All_Values();
					} else {
						showDialog(TaskMode);
					}
				}
			}
		});

		SelectAll_Btn = (Button) findViewById(R.id.SAML_SelectAllBtn);
		SelectAll_Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				if (SelectAll_Btn.getText().toString().equalsIgnoreCase(CHECK)) {
					Do_SelectAll();
					SelectAll_Btn.setText(UNCHECK);
				} else {
					Do_UnselectAll();
					SelectAll_Btn.setText(CHECK);
				}

			}
		});
		ScrollLayout = (LinearLayout) findViewById(R.id.SAML_Scroll_Layout);
		GetMembersList();

	}


	private void GetMembersList() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("ID",BasicDetails.getCurrentID()));
		new PostToServer(nameValuePairs,URL_Strings.getUrlAllocateMemberList(),"Updating Members List..",1).execute();
	}


	protected void Do_UnselectAll() {
		for (int a = 0; a < Length; a++) {
			MemberCheckBox[a].setSelected(false);
			if (MemberCheckBox[a].isChecked())
				MemberCheckBox[a].performClick();
			Is_Member_Selected[a] = false;
		}
		SelectedMemberCount = 0;
	}

	protected void Do_SelectAll() {
		for (int a = 0; a < Length; a++) {
			MemberCheckBox[a].setSelected(true);
			Is_Member_Selected[a] = true;
			if (!MemberCheckBox[a].isChecked())
				MemberCheckBox[a].performClick();
		}
		SelectedMemberCount = Length;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		TaskModeValue = 0;
		switch (id) {
		case TaskMode:
			SelectMode = new AlertDialog.Builder(ShowMembersList.this)
					.setTitle("Task Type")
					.setSingleChoiceItems(TaskModeArray, 0,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									TaskModeValue = whichButton;
								}
							})
					.setPositiveButton("Finish",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									/* User clicked Yes so do some stuff */
									if (TaskModeValue != 2) {
										Bundle_All_Values();
									} else {
										Toast.makeText(getApplicationContext(),
												"Select a Type...",
												Toast.LENGTH_SHORT).show();
									}
								}
							})
					.setNegativeButton("Back",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									/* User clicked No so do some stuff */
									SelectMode.dismiss();
								}
							}).create();
			return SelectMode;
		}
		return null;
	}

	protected void Bundle_All_Values() {
		String DisplayMsg = "";
		boolean flag=false;
		Toast.makeText(getApplicationContext(), "Now In Bundle Mode",
				Toast.LENGTH_SHORT).show();
		int tmp = 0;

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(10+SelectedMemberCount);
		nameValuePairs.add(new BasicNameValuePair(ServerLabels.getFirstLabel(),"AddTask"));
		nameValuePairs.add(new BasicNameValuePair("LeaderID",BasicDetails.getCurrentID()));
			nameValuePairs.add(new BasicNameValuePair("Title",Title));
			nameValuePairs.add(new BasicNameValuePair("Subject",Subject));
			if(TaskModeValue==0){
				nameValuePairs.add(new BasicNameValuePair("Type","Individual"));
			}else{
				nameValuePairs.add(new BasicNameValuePair("Type","Group"));
			}
			nameValuePairs.add(new BasicNameValuePair("Description",Desc));
			nameValuePairs.add(new BasicNameValuePair("Location",Locatn));
			nameValuePairs.add(new BasicNameValuePair("StartDate",StartDt));
			nameValuePairs.add(new BasicNameValuePair("EndDate",EndDt));
			nameValuePairs.add(new BasicNameValuePair("MemberCount",SelectedMemberCount+""));
			
			//DisplayMsg = DisplayMsg + EnterTaskDetails[i]+": "+value[i] + "\n";
		
			DisplayMsg=SelectedMemberCount+"";
			
			
			for (int i = 0; i < SelectedMemberCount; i++) {
			flag=false;
			while (tmp < nl.getLength()) {
				if (Is_Member_Selected[tmp] == true){
					flag=true;
					break;
				}
				tmp++;
			}
			if(flag){
				nameValuePairs.add(new BasicNameValuePair("Member" + i, MemberID[tmp]));
				DisplayMsg = DisplayMsg +"\nMember" + i+": "+ MemberID[tmp]+ "\n";
			}
			tmp++;
		}

		//ShowThisMsg("Check It", DisplayMsg,0);
		 new PostToServer(nameValuePairs,URL_Strings.getUrlTaskOperation(),"Processing...",2).execute();
	}

	protected View ViewThisTaskLayout(final int Cnt, final String ID,String Name, String Designation,String ProfilePic) {
		LayoutInflater minflate = LayoutInflater.from(getApplicationContext());
		final View convertView = minflate.inflate(
				R.layout.member_checklist_layout, null);
		TextView MemberName = (TextView) convertView
				.findViewById(R.id.MCL_memberNameTxt);
		TextView MemberDesignation = (TextView) convertView
				.findViewById(R.id.MCL_memberDesignTxt);
		ImageView Pic=(ImageView) convertView.findViewById(R.id.MCL_memberImage);
		if(ProfilePic=="NULL")
			Pic.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.default_profile_pic));
		else
			Pic.setImageBitmap(MyTeam.StringToBitmap(ProfilePic));////////////////////////////////
		MemberCheckBox[Cnt] = (CheckBox) convertView
				.findViewById(R.id.MCL_membercheckBox);
		MemberName.setText(Name);
		MemberDesignation.setText(Designation);
		MemberCheckBox[Cnt].setSelected(false);
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				if (MemberCheckBox[Cnt].isSelected()) { // Is Selected..then
														// Uncheck
					MemberCheckBox[Cnt].setSelected(false);
					Is_Member_Selected[Cnt] = false;
					SelectedMemberCount--;
					if (SelectAll_Btn.getText().toString().equals(UNCHECK))
						SelectAll_Btn.setText(CHECK);
				} else {
					MemberCheckBox[Cnt].setSelected(true);
					Is_Member_Selected[Cnt] = true;
					SelectedMemberCount++;
					if (SelectedMemberCount == Length)
						SelectAll_Btn.setText(UNCHECK);
				}
				MemberCheckBox[Cnt].performClick();
			}
		});
		return convertView;
	}

	protected void ShowThisMsg(String title, String message,final int type) {
		DIALOG_SHOW_MSG = new AlertDialog.Builder(ShowMembersList.this)
				.setIcon(R.drawable.ic_launcher).setTitle(title)
				.setMessage(message)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if(type==1){
							Intent home=new Intent(getApplicationContext(), HomeScreen.class);
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
			PG = new ProgressDialog(ShowMembersList.this);
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
					doc = xmlParser.getDomElement(RESULT_STRING);
					nl = doc.getElementsByTagName("values");
					for (int i = 0; i < nl.getLength(); i++) {
						Element e = (Element) nl.item(i);
						MemberID[i]= xmlParser.getValue(e, "ID");
						String name=xmlParser.getValue(e, "NAME");
						String pic=xmlParser.getValue(e, "PROFILEPIC");
						String design=xmlParser.getValue(e, "DESIGNATION");
						EmailID[i]=xmlParser.getValue(e, "EmailID");
						Log.d("Storing Value",EmailID[i]);
						Log.d("i=",i+"");
						ScrollLayout.addView(ViewThisTaskLayout(i, MemberID[i],name,design,pic));
					}
					break;
				case 2:
					int tmp;
					boolean flag;
					 if(RESULT_STRING.equals("SUCCESS")){
						 tmp=0;
						 for (int i = 0; i < SelectedMemberCount; i++) {
								flag=false;
								while (tmp < nl.getLength()) {
									if (Is_Member_Selected[tmp] == true){
										flag=true;
										break;
									}
									tmp++;
								}
								if(flag){
									if(EmailID[tmp]!=null)
									((MyTeam)getApplication()).SendXMPPmsg(EmailID[tmp], MyTeam.NotificationText+BasicDetails.getCurrentID());
								}
								tmp++;
							}

						 ShowThisMsg("Successfull:", "Task allocation done...!\n", 1);
					 }else{
						 ShowThisMsg("Error:", "Could not complete your request now..\nTry again later..\n"+RESULT_STRING, 1);	//CHANGE TO 0
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
