package myteam.task.view;

import java.util.ArrayList;
import java.util.List;

import myteam.global.constants.BasicDetails;
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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CommentScreen extends Activity {
	private String TaskID = null;
	private TextView Header = null;
	private Button AddComment = null;
	private Button BackBtn = null;
	private LinearLayout CommentList = null;
	private AlertDialog DialogAddComment;
	private ParseXmlData xmlParser = new ParseXmlData();
	private NodeList nl=null;
	private Document doc=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bndl = getIntent().getExtras();
		MyTeam.currentContext=this;
		if (bndl == null) {
			Toast.makeText(getApplicationContext(), "No DATA in bundle",
					Toast.LENGTH_LONG).show();
		}
		TaskID = bndl.getString("TASKID");
		setContentView(R.layout.comment_screen);
		Header = (TextView) findViewById(R.id.CS_Title);
		Header.setText(TaskID);
		AddComment = (Button) findViewById(R.id.CS_AddComment_Btn);
		AddComment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				AddNewComment();
			}
		});
		BackBtn = (Button) findViewById(R.id.CS_Back_Btn);
		BackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				onBackPressed();
			}
		});
		CommentList = (LinearLayout) findViewById(R.id.CS_Comment_List);
		GetAllComments();
	}

	private void GetAllComments() {
		try {
			CommentList.removeAllViews();
		} catch (Exception e) {}
			
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("TASKID", TaskID));
		new PostToServer(nameValuePairs,URL_Strings.getUrlViewComment(),"Loading Comments...",1).execute();
		//ShowThisMsg("xmlData",xmlData, 0);
	}

	protected void AddNewComment() {
		LayoutInflater minflate = LayoutInflater.from(getApplicationContext());
		final View convertView = minflate.inflate(R.layout.insert_update_comment, null);
		final EditText Comnt_Text= (EditText) convertView.findViewById(R.id.IUC_Commnt_textbox);
		Comnt_Text.addTextChangedListener(MyTeam.EditTextSound);

		DialogAddComment=new AlertDialog.Builder(CommentScreen.this)
        .setTitle("ADD COMMENT:")
        .setView(convertView)
        .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	if(Comnt_Text.getText().toString().trim().length()==0){
            		DialogAddComment.show();
            	}else{
            		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            		nameValuePairs.add(new BasicNameValuePair("TASKID", TaskID));
            		nameValuePairs.add(new BasicNameValuePair("SENDERID", BasicDetails.getCurrentID()));
            		nameValuePairs.add(new BasicNameValuePair("COMMENT", Comnt_Text.getText().toString()));
            		new PostToServer(nameValuePairs,URL_Strings.getUrlAddComment(),"Submitting..",2).execute();
            	}
            }
        })
        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	DialogAddComment.dismiss();
            }
        })
        .create();
		DialogAddComment.show();
	}

	private View AddCommentToList(String sndr,String time,String commnt){
		LayoutInflater minflate = LayoutInflater.from(getApplicationContext());
		final View convertView = minflate.inflate(R.layout.comment_inflater, null);
		TextView Comnt_Text= (TextView) convertView.findViewById(R.id.CI_CommentText);
		TextView Sndr_Text= (TextView) convertView.findViewById(R.id.CI_Member_ID);
		TextView Time_Text= (TextView) convertView.findViewById(R.id.CI_DateTime);
		Comnt_Text.setText(commnt);
		Sndr_Text.setText(sndr);
		Time_Text.setText(time);
		return convertView;
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
			PG = new ProgressDialog(CommentScreen.this);
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
					for(int i=0;i<nl.getLength();i++){
					Element e = (Element) nl.item(i);
					if(BasicDetails.getCurrentID().equals(xmlParser.getValue(e, "SENDERID")))
						CommentList.addView(AddCommentToList("YOU", xmlParser.getValue(e, "DATE"), xmlParser.getValue(e, "COMMENT")));
					else
						CommentList.addView(AddCommentToList(xmlParser.getValue(e, "SENDERID"), xmlParser.getValue(e, "DATE"), xmlParser.getValue(e, "COMMENT")));
					}
					break;
				case 2:
					if(RESULT_STRING.equals("SUCCESS")){
            			//TODO this is added
            			for(int i=0;i<ViewSelectedTask.NotifyEmail.length;i++){
            				((MyTeam)getApplication()).SendXMPPmsg(ViewSelectedTask.NotifyEmail[i], MyTeam.NotificationText+BasicDetails.getCurrentID());
            			}
            			GetAllComments();
            		}else{
            			Toast.makeText(getApplicationContext(), RESULT_STRING, Toast.LENGTH_LONG).show();
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
