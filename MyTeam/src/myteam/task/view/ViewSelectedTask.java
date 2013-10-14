package myteam.task.view;

import java.util.ArrayList;
import java.util.List;

import myteam.global.constants.BasicDetails;
import myteam.global.constants.ParseXmlData;
import myteam.global.constants.URL_Strings;
import myteam.main.forms.MyTeam;
import myteam.main.forms.R;
import myteam.task.edit.EditTaskPage;

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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class ViewSelectedTask extends Activity{

	private Bundle intntData=null;
	private String ReceivedID=null;
	private TextView ID =null;
	private TextView CompleteStatus=null;
	private TextView PercentStatus=null;
	private TextView Title=null;
	private TextView Subject=null;
	private TextView Desc=null;
	private TextView StartDt=null;
	private TextView EndDt=null;
	private TextView Location=null;
	private TextView Members=null;
	private Button Comment_Btn=null;
	private Button Status_Btn=null;
	private Button Edit_Btn=null;
	private AlertDialog DIALOG_SHOW_MSG;
	private String TaskType=null;
	private ParseXmlData xmlParser = new ParseXmlData();
	private Document doc=null;
	private NodeList nl=null;
	private AlertDialog DialogUpdateStatus;
	private int PercentComplete;
	private int MemCount;
	public static String[] NotifyEmail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_specific_task);
		MyTeam.currentContext=this;
		intntData=getIntent().getExtras();
		if(intntData==null){
			Toast.makeText(getApplicationContext(), "Khatarnaak Error hai be...", Toast.LENGTH_LONG).show();
		}
		ReceivedID = intntData.getString("TASKID");
		ID = (TextView) findViewById(R.id.VST_Header_Task_ID);
		CompleteStatus = (TextView) findViewById(R.id.VST_Header_Status);
		PercentStatus = (TextView) findViewById(R.id.VST_Percent_Txt);
		Title = (TextView) findViewById(R.id.VST_TaskType);
		Subject = (TextView) findViewById(R.id.VST_Subject_Txt);
		Subject.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				ShowThisMsg("SUBJECT", Subject.getText().toString(), 0);
			}
		});
		Desc = (TextView) findViewById(R.id.VST_Desc_Txt);
		Desc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				ShowThisMsg("DESCRIPTION", Desc.getText().toString(), 0);
			}
		});
		StartDt = (TextView) findViewById(R.id.VST_Start_Date);
		EndDt = (TextView) findViewById(R.id.VST_End_Date);
		Location = (TextView) findViewById(R.id.VST_Display_location);
		Members = (TextView) findViewById(R.id.VST_Member_List_Txt);
		Members.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				ShowThisMsg("MEMBERS ("+TaskType+")", Members.getText().toString(), 0);
			}
		});
		Comment_Btn=(Button) findViewById(R.id.VST_COMMENTS_Btn);
		Comment_Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				CommentWindow();
			}
		});
		Status_Btn=(Button) findViewById(R.id.VST_STATUS_Btn);
		if(BasicDetails.getTYPE().equals("LEADER")){
			Status_Btn.setVisibility(8);
		}
		Status_Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				StatusWindow();
			}
		});
		GetAndDisplayData();
		Edit_Btn=(Button) findViewById(R.id.VST_EDIT_Btn);
		
		Edit_Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				finish();
				GotoEditTask();
			}
		});
		
	}

	@Override
	protected void onResume() {
		super.onRestart();
		GetAndDisplayData();
	}
	protected void GotoEditTask() {
		Intent taskEdit = new Intent(getApplicationContext(),EditTaskPage.class);
		taskEdit.putExtra("ID", ID.getText().toString());
		taskEdit.putExtra("Title", Title.getText().toString());
		taskEdit.putExtra("Subject", Subject.getText()
				.toString());
		taskEdit.putExtra("Desc", Desc.getText().toString());
		taskEdit.putExtra("StartDate", StartDt.getText()
				.toString());
		taskEdit.putExtra("EndDate", EndDt.getText()
				.toString());
		taskEdit.putExtra("Location", Location.getText().toString().substring(10));
		startActivityForResult(taskEdit, 110);
	}

	private void CommentWindow() {
		Intent cmmnt=new Intent(getApplicationContext(),CommentScreen.class);
		cmmnt.putExtra("TASKID", ReceivedID);
		startActivityForResult(cmmnt, 110);
	}
	

	private void StatusWindow() {
			LayoutInflater minflate = LayoutInflater.from(getApplicationContext());
			final View convertView = minflate.inflate(R.layout.insert_update_comment, null);
			final EditText Comnt_Text= (EditText) convertView.findViewById(R.id.IUC_Commnt_textbox);
			Comnt_Text.addTextChangedListener(MyTeam.EditTextSound);

			final TextView value_Text=(TextView) convertView.findViewById(R.id.IUC_seekbar_value);
			final SeekBar statusBar=(SeekBar) convertView.findViewById(R.id.IUC_statusBar);
			statusBar.setVisibility(0);
			value_Text.setVisibility(0);
			value_Text.setText(PercentComplete+"");
			statusBar.setMax(100);
			statusBar.setProgress(PercentComplete);
			statusBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(SeekBar arg0) {
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar arg0) {
				}
				
				@Override
				public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
					if(statusBar.getProgress()<PercentComplete){
						statusBar.setProgress(PercentComplete);
					}
					value_Text.setText(statusBar.getProgress()+"%");
				}
			});
			DialogUpdateStatus=new AlertDialog.Builder(ViewSelectedTask.this)
	        .setTitle("UPDATE STATUS: ("+statusBar.getProgress()+")")
	        .setView(convertView)
	        .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            if(statusBar.getProgress()<=PercentComplete){
	            	ShowThisMsg("STATUS NOT CHANGED:", "THE CURRENT STATUS AND UPDATE STATUS ARE SAME....", 1);
	            }else if(Comnt_Text.getText().toString().trim().length()==0){
	            	ShowThisMsg("COMMENT FIELD BLANK:", "PLEASE COMMENT ON THIS STATUS UPDATE...", 1);
	            	}else{
	            		String hdrMsg="(UPDATED FROM :"+PercentComplete+"% to "+statusBar.getProgress()+")\n";
	            		Toast.makeText(getApplicationContext(), statusBar.getProgress()+"..."+PercentComplete, Toast.LENGTH_LONG).show();
	            		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
	            		nameValuePairs.add(new BasicNameValuePair("TASKID", ReceivedID));
	            		nameValuePairs.add(new BasicNameValuePair("STATUS", statusBar.getProgress()+""));
	            		nameValuePairs.add(new BasicNameValuePair("SENDERID", BasicDetails.getCurrentID()));
	            		nameValuePairs.add(new BasicNameValuePair("COMMENT",hdrMsg+ Comnt_Text.getText().toString()));
	            		new PostToServer(nameValuePairs,URL_Strings.getUrlUpdateStatus(),"Updating..",1).execute();
	            	}
	            }
	        })
	        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	DialogUpdateStatus.dismiss();
	            }
	        })
	        .create();
			DialogUpdateStatus.show();
	}

	private void GetAndDisplayData() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("TASKID", ReceivedID));
		nameValuePairs.add(new BasicNameValuePair("LeaderID", BasicDetails.getLeaderID()));
		new PostToServer(nameValuePairs,URL_Strings.getUrlViewTask(),"Loading..",2).execute();
	}	
		

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.view_task_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.ViewTask_menu_report:
			GenerateReport();
			break;
		}
		return true;
	}
	
	private void GenerateReport() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("TASKID", ReceivedID));
		new PostToServer(nameValuePairs,URL_Strings.getUrlGenerateReport(),"Constructing Report...",3).execute();
	}

	public void ShowThisMsg(String title, String message, final int type) {
		DIALOG_SHOW_MSG = new AlertDialog.Builder(ViewSelectedTask.this)
				.setIcon(R.drawable.ic_launcher).setTitle(title)
				.setMessage(message)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (type == 1) {
							DialogUpdateStatus.show();
						}
						if(type==2){
							Toast.makeText(getApplicationContext(), "Refreshing", Toast.LENGTH_LONG).show();
							GetAndDisplayData();
						}
						if(type==3){
							String url =URL_Strings.getReportPath()+ReceivedID+".pdf";
							Intent i = new Intent(Intent.ACTION_VIEW);
							i.setData(Uri.parse(url));
							startActivity(i);
						}
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
			PG = new ProgressDialog(ViewSelectedTask.this);
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
//					if(RESULT_STRING.equals("DONE")){
            			for(int i=0;i<NotifyEmail.length;i++){
            				((MyTeam)getApplication()).SendXMPPmsg(NotifyEmail[i], MyTeam.NotificationText+BasicDetails.getCurrentID());
            			}
            			ShowThisMsg("Done!", "Status successfully updated...", 2);
//            			for(int i=0;i<NotifyEmail.length;i++){
//            				((MyTeam)getApplication()).SendXMPPmsg(NotifyEmail[i], MyTeam.NotificationText+BasicDetails.getCurrentID());
//            			}
//					}else{
//						//TODO this can create error....
//						Toast.makeText(getApplicationContext(), "#######Error:"+ERROR, Toast.LENGTH_LONG).show();
//					}
					break;
				case 2:
					doc = xmlParser.getDomElement(RESULT_STRING);
					nl = doc.getElementsByTagName("values");
					Element e = (Element) nl.item(0);
					ID.setText(xmlParser.getValue(e, "ID"));
					CompleteStatus.setText(xmlParser.getValue(e, "TaskComplete"));
					PercentComplete=Integer.parseInt(xmlParser.getValue(e, "StatusPercent"));
					PercentStatus.setText(PercentComplete+"%");
					Title.setText(xmlParser.getValue(e, "Title"));
					Subject.setText(xmlParser.getValue(e, "Subject"));
					Desc.setText(xmlParser.getValue(e, "Description"));
					StartDt.setText(xmlParser.getValue(e, "StartDate"));
					EndDt.setText(xmlParser.getValue(e, "EndDate"));
					Location.setText("Location :"+xmlParser.getValue(e, "Location"));
					Members.setText(xmlParser.getValue(e, "Member"));
					TaskType=xmlParser.getValue(e, "Type");
					MemCount=Integer.parseInt(xmlParser.getValue(e, "MemberCount"));
					NotifyEmail=new String[MemCount+1];
					for(int i=0;i<=MemCount;i++){
						NotifyEmail[i]=xmlParser.getValue(e, "EmailID"+i);
					}
					if(BasicDetails.getTYPE().equals("LEADER")){
						if(PercentStatus.getText().toString().equals("100%")){
							Edit_Btn.setVisibility(8);
						}else{
							Edit_Btn.setVisibility(0);
						}
					}else{
						Edit_Btn.setVisibility(8);
					}
					break;
				case 3:ShowThisMsg("REPORT: ","REPORT GENERATION COMPLETE...\nPress ok to start downloading...", 3);
					break;
				default:Toast.makeText(getApplicationContext(), "Done"+RESULT_STRING, Toast.LENGTH_LONG).show();
				}
			}else{
				Toast.makeText(getApplicationContext(), "Error:"+ERROR, Toast.LENGTH_LONG).show();
			}
		}
	}

	
}
