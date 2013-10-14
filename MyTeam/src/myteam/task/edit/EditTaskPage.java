package myteam.task.edit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import myteam.global.constants.ServerLabels;
import myteam.global.constants.URL_Strings;
import myteam.main.forms.MyTeam;
import myteam.main.forms.R;
import myteam.task.view.ViewSelectedTask;

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
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditTaskPage extends Activity {
	// CONSTANTS
	private final int DATE_DIALOG_ID = 1;
	private final String TempDateString = "[--/--/----]";

	// VARIABLES
	private int mYear, TempStrtYr, TempEndYr;
	private int mMonth, TempStrtMnth, TempEndMnth;
	private int mDay, TempStrtDay, TempEndDay;
	private int TempDateType;
	// BUTTONS
	private Button Start_Date_btn = null;
	private Button End_Date_btn = null;
	private Button Close_Btn = null;
	private Button Proceed_Btn = null;
	// TEXTVIEW
	private TextView Header_txt = null;
	private TextView Start_Date_txt = null;
	private TextView End_Date_txt = null;
	// EDITTEXT
	private EditText Title_Edtxt = null;
	private EditText Subject_Edtxt = null;
	private EditText Desc_Edtxt = null;
	private EditText Location_Edtxt = null;
	private int COLOR_DISABLE=R.color.AliceBlue;    //getResources().getColor(R.color.Cornsilk);
	private Bundle bund=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyTeam.currentContext=this;
		setContentView(R.layout.add_task_layout);
		bund=getIntent().getExtras();
		if(bund==null){
			Toast.makeText(getApplicationContext(), "NO DATA IN BUNDLE..\nSERIOUS ERROR..", Toast.LENGTH_LONG).show();
			onBackPressed();
		}
		
		Header_txt = (TextView) findViewById(R.id.Header_Txt);

		Header_txt.setText(bund.getString("ID"));
		Title_Edtxt = (EditText) findViewById(R.id.ATL_Title_txt);
		Title_Edtxt.addTextChangedListener(MyTeam.EditTextSound);

		Subject_Edtxt = (EditText) findViewById(R.id.ATL_Subject_Txt);
		Subject_Edtxt.addTextChangedListener(MyTeam.EditTextSound);
	
		Subject_Edtxt.setText(bund.getString("Subject"));
		Title_Edtxt.setText(bund.getString("Title"));
		Title_Edtxt.setEnabled(false);
		Title_Edtxt.setBackgroundColor(COLOR_DISABLE);
		Desc_Edtxt = (EditText) findViewById(R.id.ATL_DescTxt);
		Desc_Edtxt.addTextChangedListener(MyTeam.EditTextSound);

		Desc_Edtxt.setText(bund.getString("Desc"));
		Location_Edtxt = (EditText) findViewById(R.id.ATL_Location_Txt);
		Location_Edtxt.addTextChangedListener(MyTeam.EditTextSound);
		Location_Edtxt.setText(bund.getString("Location"));
		Start_Date_txt = (TextView) findViewById(R.id.ATL_Start_Date_View);
		Start_Date_txt.setText(bund.getString("StartDate"));
		End_Date_txt = (TextView) findViewById(R.id.ATL_End_Date_View);
		End_Date_txt.setText(bund.getString("EndDate"));
		Close_Btn = (Button) findViewById(R.id.ATL_CloseBtn);
		Proceed_Btn = (Button) findViewById(R.id.ATL_ProceedBtn);
		Proceed_Btn.setText("SAVE");
		Proceed_Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				Validation();
			}
		});
		Close_Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				onBackPressed();
			}
		});
		Start_Date_btn = (Button) findViewById(R.id.ATL_Start_Date_Btn);
		Start_Date_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				((MyTeam)getApplication()).PlayButtonSound();
				TempDateType = 0;
				showDialog(DATE_DIALOG_ID);
			}
		});
		End_Date_btn = (Button) findViewById(R.id.ATL_End_Date_Btn);
		End_Date_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				((MyTeam)getApplication()).PlayButtonSound();
				TempDateType = 1;
				showDialog(DATE_DIALOG_ID);
			}
		});
	}

	private void AddToServer() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
		nameValuePairs.add(new BasicNameValuePair(ServerLabels.getFirstLabel(),"EditTask"));
		nameValuePairs.add(new BasicNameValuePair("ID",bund.getString("ID")));
			nameValuePairs.add(new BasicNameValuePair("Subject",Subject_Edtxt.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("Description",Desc_Edtxt.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("Location",Location_Edtxt.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("StartDate",Start_Date_txt.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("EndDate",End_Date_txt.getText().toString()));
		//ShowThisMsg("Check It", DisplayMsg,0);
		 new PostToServer(nameValuePairs,URL_Strings.getUrlTaskOperation(),"Updating Task..").execute();
	}

	protected boolean CheckForChanges() {
		if(!Subject_Edtxt.getText().toString().equals(bund.getString("Subject")))
			return true;
		if(!Desc_Edtxt.getText().toString().equals(bund.getString("Desc")))
			return true;
		if(!Location_Edtxt.getText().toString().equals(bund.getString("Location")))
			return true;
		if(!Start_Date_txt.getText().toString().equals(bund.getString("StartDate")))
			return true;
		if(!End_Date_txt.getText().toString().equals(bund.getString("EndDate")))
			return true;
		return false;
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			final Calendar c = Calendar.getInstance(); // Date Instance
			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH);
			mDay = c.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return null;
	}


	private void Validation() {
		if (Title_Edtxt.getText().toString().length() == 0) {
			Title_Edtxt.setError("EMPTY FIELD");
			Title_Edtxt.requestFocus();
		} else if (Subject_Edtxt.getText().toString().trim().length() == 0) {
			Subject_Edtxt.setError("EMPTY FIELD");
			Subject_Edtxt.requestFocus();
		} else if (Desc_Edtxt.getText().toString().trim().length() == 0) {
			Desc_Edtxt.setError("EMPTY FIELD");
			Desc_Edtxt.requestFocus();
		} else if (Start_Date_txt.getText().toString().equals(TempDateString)) {
			Toast.makeText(getApplicationContext(), "SELECT START DATE..",
					Toast.LENGTH_LONG).show();
		} else if (End_Date_txt.getText().toString().equals(TempDateString)) {
			Toast.makeText(getApplicationContext(), "SELECT END DATE..",
					Toast.LENGTH_LONG).show();
		} else if ((TempStrtMnth == TempEndMnth && TempStrtYr == TempEndYr && TempStrtDay > TempEndDay)
				|| (TempStrtMnth > TempEndMnth && TempStrtYr == TempEndYr)
				|| (TempEndYr < TempStrtYr)) {
			Toast.makeText(getApplicationContext(), "END DATE CONFLICTING....",
					Toast.LENGTH_LONG).show();
		} else {
			if(CheckForChanges()){
				AddToServer();
			}else{
				Toast.makeText(getApplicationContext(), "No Changes Detected...", Toast.LENGTH_LONG).show();
			}
		}
	}



	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DATE_DIALOG_ID:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		}
	}

	private void updateDisplay() {
		if (TempDateType == 0) {
			Start_Date_txt.setText(new StringBuilder()
					// Month is 0 based so add 1
					.append(padding(mYear)).append("-")
					.append(padding(mMonth + 1)).append("-").append(mDay)
					.append(" "));
			TempStrtDay = mDay;
			TempStrtMnth = mMonth;
			TempStrtYr = mYear;
		} else if (TempDateType == 1) {
			End_Date_txt.setText(new StringBuilder()
					// Month is 0 based so add 1
					.append(padding(mYear)).append("-")
					.append(padding(mMonth + 1)).append("-").append(mDay)
					.append(" "));
			TempEndDay = mDay;
			TempEndMnth = mMonth;
			TempEndYr = mYear;
		}
	}

	private String padding(int value) {
		String response;
		if (value < 10)
			response = "0" + value;
		else
			response = value + "";
		return response;
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};

	protected void ShowThisMsg(String title, String message,final int type) {
		Dialog DIALOG_SHOW_MSG = new AlertDialog.Builder(EditTaskPage.this)
				.setIcon(R.drawable.ic_launcher).setTitle(title)
				.setMessage(message)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if(type==1){
							onBackPressed();
						}
					}
				}).create();
		DIALOG_SHOW_MSG.show();
	}
	public void onBackPressed() {
		super.onBackPressed();
		Intent taskintent=new Intent(getApplicationContext(), ViewSelectedTask.class);
		taskintent.putExtra("TASKID", bund.getString("ID"));
		startActivityForResult(taskintent, 110);
	};
	
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
			PG = new ProgressDialog(EditTaskPage.this);
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
				if(RESULT_STRING.equals("SUCCESS")){
					 ShowThisMsg("Successfull:", "Task Successfully Edited...!\n", 1);
				 }else{
					 ShowThisMsg("Error:", "Could not complete your request now..\nTry again later..\n"+RESULT_STRING, 1);	//CHANGE TO 0
				 }
			}else{
				Toast.makeText(getApplicationContext(), "Error:"+ERROR, Toast.LENGTH_LONG).show();
			}
		}
	}

	
	
}
