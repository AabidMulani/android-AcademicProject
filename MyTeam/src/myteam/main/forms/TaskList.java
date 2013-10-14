package myteam.main.forms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import myteam.global.constants.BasicDetails;
import myteam.global.constants.ConstantValues;
import myteam.global.constants.Credits;
import myteam.global.constants.Help;
import myteam.global.constants.ParseXmlData;
import myteam.global.constants.ServerLabels;
import myteam.global.constants.URL_Strings;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TaskList extends Activity {
	private String TaskTypeArray[] = ConstantValues.getTaskTypeArray();
	private String NameArray[] = null;
	private String IDArray[] = new String[100];
	private String TypeArray[] = new String[100];
	
	private final int REQUEST_ALL_TASK = 0;
	private final int REQUEST_SORT_TYPE = 1;
	private final int REQUEST_SORT_DATE = 2;
	private final int REQUEST_SORT_MEMBER = 3;
	private final String TempDateString = "[--/--/----]";

	private int TempDateType;
	private int LIST_TYPE;
	private String SortByTypeVALUE = null;
	private String SortByDATE_Start = null;
	private String SortByDATE_End = null;
	private String SortByMEMBER_VAULE = null;

	private Button ChatButton = null;
	private Button MenuHome = null;
	private Button MenuTeam = null;
	private Button MenuTask = null;
	private Button MenuMore = null;
	private Button SortBtn = null;
	private LinearLayout List = null;
	private Button MoreBtn = null;
	private int CurrentCount;
	private NodeList nl = null;
	private Element e = null;
	private Document doc = null;
	private ParseXmlData xmlParser = new ParseXmlData();
	private AlertDialog DIALOG_SHOW_MSG;
	private Dialog SortDialog;
	private Dialog DialogSortType;
	private Dialog DialogSortDate;
	private Dialog DialogSortMember;
	private TextView Start_Date_Txt = null;
	private TextView End_Date_Txt = null;
	private Button Start_Date_Btn = null;
	private Button End_Date_Btn = null;
	private int TempEndDay;
	private int TempEndMnth;
	private int TempEndYr;
	private int mDay;
	private int mMonth;
	private int mYear;
	private int TempStrtDay;
	private int TempStrtMnth;
	private int TempStrtYr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.task_list_screen);
		MyTeam.currentContext=this;
		((MyTeam)getApplication()).tasklist=this;
		MenuLayoutInitialization();
		LIST_TYPE = 0;
		CurrentCount = 1;
		List = (LinearLayout) findViewById(R.id.TLS_Scroll_Layout);

		SortBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				ShowSortDialog();
			}
		});

		MoreBtn = (Button) findViewById(R.id.TLS_MoreBtn);
		MoreBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				switch (LIST_TYPE) {
				case REQUEST_ALL_TASK:
					List<NameValuePair> nameValuePairs0 = new ArrayList<NameValuePair>(
							4);
					nameValuePairs0.add(new BasicNameValuePair(ServerLabels
							.getFirstLabel(), "ALL TASK"));
					nameValuePairs0.add(new BasicNameValuePair("GroupID",
							BasicDetails.getLeaderID()));
					nameValuePairs0.add(new BasicNameValuePair("MemberID",
							BasicDetails.getCurrentID()));
					nameValuePairs0.add(new BasicNameValuePair("Count",
							CurrentCount + ""));
					new PostToServer(nameValuePairs0,URL_Strings.getUrlTaskList(),"Updating List..",1).execute();
					break;
				case REQUEST_SORT_TYPE:
					List<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>(
							5);
					nameValuePairs1.add(new BasicNameValuePair(ServerLabels
							.getFirstLabel(), "SORT BY TYPE"));
					nameValuePairs1.add(new BasicNameValuePair("TaskType",
							SortByTypeVALUE));
					nameValuePairs1.add(new BasicNameValuePair("GroupID",
							BasicDetails.getLeaderID()));
					nameValuePairs1.add(new BasicNameValuePair("MemberID",
							BasicDetails.getCurrentID()));
					nameValuePairs1.add(new BasicNameValuePair("Count",
							CurrentCount + ""));
					new PostToServer(nameValuePairs1,URL_Strings.getUrlTaskList(),"Updating List..",1).execute();
					break;
				case REQUEST_SORT_DATE:
					List<NameValuePair> nameValuePairs2 = new ArrayList<NameValuePair>(
							6);
					nameValuePairs2.add(new BasicNameValuePair(ServerLabels
							.getFirstLabel(), "SORT BY DATE"));
					nameValuePairs2.add(new BasicNameValuePair("StartDate",
							SortByDATE_Start));
					nameValuePairs2.add(new BasicNameValuePair("EndDate",
							SortByDATE_End));
					nameValuePairs2.add(new BasicNameValuePair("GroupID",
							BasicDetails.getLeaderID()));
					nameValuePairs2.add(new BasicNameValuePair("MemberID",
							BasicDetails.getCurrentID()));
					nameValuePairs2.add(new BasicNameValuePair("Count",
							CurrentCount + ""));
					new PostToServer(nameValuePairs2,URL_Strings.getUrlTaskList(),"Updating List..",1).execute();
					break;
				case REQUEST_SORT_MEMBER:
					List<NameValuePair> nameValuePairs3 = new ArrayList<NameValuePair>(
							3);
					nameValuePairs3.add(new BasicNameValuePair(ServerLabels
							.getFirstLabel(), "SORT BY MEMBER"));
					nameValuePairs3.add(new BasicNameValuePair("GroupID",
							BasicDetails.getLeaderID()));
					nameValuePairs3.add(new BasicNameValuePair("MemberID",
							SortByMEMBER_VAULE));
					nameValuePairs3.add(new BasicNameValuePair("Count",
							CurrentCount + ""));
					new PostToServer(nameValuePairs3,URL_Strings.getUrlTaskList(),"Updating List..",1).execute();
					break;

				}
			}
		});
		MoreBtn.performClick();
	}

	private void UpdateTaskList() {
		if (nl.getLength() == 0) {
			MoreBtn.setEnabled(false);
			Toast.makeText(getApplicationContext(), "NO MORE TASK AVAILABLE",
					Toast.LENGTH_LONG).show();
		} else {
			CurrentCount++;
			for (int i = 0; i < nl.getLength(); i++) {
				e = (Element) nl.item(i);
				List.addView(AddThisToList(xmlParser.getValue(e, "ID"),
						xmlParser.getValue(e, "Title"), xmlParser.getValue(e, "Subject"),
						xmlParser.getValue(e, "StartDate"),
						xmlParser.getValue(e, "EndDate"), xmlParser.getValue(e, "Status")));//TODO status
			}
		}
	}

	protected void ShowSortDialog() {
		if(BasicDetails.getTYPE().equals("LEADER")){
			String TextArray[]=new String[]{"ALL TASK","SORT BY TYPE","SORT BY DATE","SORT BY MEMBER"};
			TypeArray=TextArray;
		}else{
			String TextArray[]=new String[]{"ALL TASK","SORT BY TYPE","SORT BY DATE"};
			TypeArray=TextArray;
		}
			SortDialog = new AlertDialog.Builder(TaskList.this)
		.setTitle("Select Sort Type:")
		.setSingleChoiceItems(TypeArray, 0,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0,
							int arg1) {

						switch (arg1) {
						case REQUEST_ALL_TASK:
							LIST_TYPE = REQUEST_ALL_TASK;
							CurrentCount=1;
							List.removeAllViews();
							MoreBtn.performClick();
							MoreBtn.setEnabled(true);
							break;
						case REQUEST_SORT_TYPE:
							ShowSortByTypeDialog();
							break;
						case REQUEST_SORT_DATE:
							ShowSortByDateDialog();
							break;
						case REQUEST_SORT_MEMBER:
							GetAllMembersArray();
							break;
						default:
						}
						
						SortDialog.dismiss();
					}
				})
		.setNegativeButton("Back",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						SortDialog.dismiss();
					}
				}).create();
			SortDialog.show();
	}

	protected void ShowSortByMember() {
		try{
			Log.d("Reached Here","Done..");
		DialogSortMember = new AlertDialog.Builder(TaskList.this)
		.setTitle("Select Task Type:")
		.setSingleChoiceItems(NameArray, 0,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0,
							int arg1) {
						Log.d("Clicked",arg1+"");
						DialogSortMember.dismiss();
						SortByMEMBER_VAULE = IDArray[arg1];
						DialogSortMember.dismiss();
						LIST_TYPE = REQUEST_SORT_MEMBER;
						CurrentCount = 1;
						List.removeAllViews();
						MoreBtn.performClick();
						MoreBtn.setEnabled(true);
					}
				})
		.setNegativeButton("Back",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						DialogSortMember.dismiss();
					}
				}).create();
		Log.d("Reached Here11","Done..");
		DialogSortMember.show();
		Log.d("Reached Here11","Done..");
		}catch(Exception ex){
			Log.d("Reached Here11",ex.toString());
		}
	}

	private void GetAllMembersArray() {
		SortDialog.dismiss();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair(ServerLabels.getFirstLabel(),
				"AllMembers"));
		nameValuePairs.add(new BasicNameValuePair("LeaderID", BasicDetails
				.getCurrentID()));
		new PostToServer(nameValuePairs,URL_Strings.getUrlTaskList(),"MEMBERS LIST..",2).execute();
		//ShowThisMsg("Members", xmlData, 0);
	}
	
	public void AfterGetAllMembersArray(String xmlData){
		doc = xmlParser.getDomElement(xmlData);
		nl = doc.getElementsByTagName("values");
		String[] TempArray=new String[nl.getLength()];
		for(int i=0;i<nl.getLength();i++){
			e = (Element) nl.item(i);
			IDArray[i]=xmlParser.getValue(e, "ID");
			TempArray[i]=xmlParser.getValue(e, "Name").toString(); 
		}
		NameArray=TempArray;
		ShowSortByMember();
	}

	protected void ShowSortByDateDialog() {
		LayoutInflater minflate = LayoutInflater.from(getApplicationContext());
		final View convertView = minflate.inflate(R.layout.sort_by_date_layout,
				null);
		Start_Date_Txt = (TextView) convertView
				.findViewById(R.id.SBDL_Start_Date_View);
		Start_Date_Btn = (Button) convertView
				.findViewById(R.id.SBDL_Start_Date_Btn);
		Start_Date_Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				TempDateType = 0;
				showDialog(0);
			}
		});
		End_Date_Txt = (TextView) convertView
				.findViewById(R.id.SBDL_End_Date_View);
		End_Date_Btn = (Button) convertView
				.findViewById(R.id.SBDL_End_Date_Btn);
		End_Date_Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				TempDateType = 1;
				showDialog(0);
			}
		});
		DialogSortDate = new AlertDialog.Builder(TaskList.this)
				.setTitle("SORT BY DATE")
				.setView(convertView)
				.setPositiveButton("VIEW",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (Start_Date_Txt.getText().toString()
										.equals(TempDateString)) {
									Toast.makeText(getApplicationContext(),
											"SELECT START DATE..",
											Toast.LENGTH_LONG).show();
									ShowSortByDateDialog();
								} else if (End_Date_Txt.getText().toString()
										.equals(TempDateString)) {
									Toast.makeText(getApplicationContext(),
											"SELECT END DATE..",
											Toast.LENGTH_LONG).show();
									ShowSortByDateDialog();
								} else if ((TempStrtMnth == TempEndMnth
										&& TempStrtYr == TempEndYr && TempStrtDay > TempEndDay)
										|| (TempStrtMnth > TempEndMnth && TempStrtYr == TempEndYr)
										|| (TempEndYr < TempStrtYr)) {
									Toast.makeText(getApplicationContext(),
											"END DATE CONFLICTING....",
											Toast.LENGTH_LONG).show();
									ShowSortByDateDialog();
								} else {
									SortByDATE_Start = Start_Date_Txt.getText()
											.toString();
									SortByDATE_End = End_Date_Txt.getText()
											.toString();
									DialogSortDate.dismiss();
									LIST_TYPE = REQUEST_SORT_DATE;
									CurrentCount = 1;
									List.removeAllViews();
									MoreBtn.performClick();
									MoreBtn.setEnabled(true);
								}
							}
						})
				.setNegativeButton("CANCEL",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								DialogSortDate.dismiss();
							}
						}).create();
		DialogSortDate.show();

	}

	protected void ShowSortByTypeDialog() {
		DialogSortType = new AlertDialog.Builder(TaskList.this)
				.setTitle("Select Task Type:")
				.setSingleChoiceItems(TaskTypeArray, 0,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								SortByTypeVALUE = TaskTypeArray[arg1];
								DialogSortType.dismiss();
								LIST_TYPE = REQUEST_SORT_TYPE;
								CurrentCount = 1;
								List.removeAllViews();
								MoreBtn.performClick();
								MoreBtn.setEnabled(true);
							}
						}).create();
		DialogSortType.show();
	}

	public View AddThisToList(final String ID, String Title, String Subj,
			String StartDt, String EndDt, String Status) {
		LayoutInflater minflate = LayoutInflater.from(getApplicationContext());
		final View convertView = minflate.inflate(R.layout.task_view_layout,
				null);
		if (Status.equals("COMPLETE"))
			convertView.setBackgroundDrawable(getResources().getDrawable(R.drawable.task_list_bk_complete));
		else
			convertView.setBackgroundDrawable(getResources().getDrawable(R.drawable.task_list_bk_incomplete));
		TextView TaskID_Txt = (TextView) convertView
				.findViewById(R.id.TVL_TaskID);
		TextView Title_Txt = (TextView) convertView
				.findViewById(R.id.TVL_TaskTitle);
		TextView Subj_Txt = (TextView) convertView
				.findViewById(R.id.TVL_TaskSubject);
		TextView Start_Txt = (TextView) convertView
				.findViewById(R.id.TVL_StartDate);
		TextView End_Txt = (TextView) convertView
				.findViewById(R.id.TVL_EndDate);
		// ImageView Image = (ImageView)
		// convertView.findViewById(R.id.MIL_image);
		TaskID_Txt.setText(ID);
		Title_Txt.setText(Title);
		Subj_Txt.setText(Subj);
		Start_Txt.setText(StartDt);
		End_Txt.setText(EndDt);
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				// IF YOU WANT SHOW A DIALOG BOX HERE
				Intent taskintent = new Intent(getApplicationContext(),
						ViewSelectedTask.class);
				taskintent.putExtra("TASKID", ID);
				startActivityForResult(taskintent, 110);
			}
		});
		return convertView;

	}

	private void MenuLayoutInitialization() {
		ChatButton = (Button) findViewById(R.id.TLS_chat_button);
		MenuHome = (Button) findViewById(R.id.TLS_Home_Btn);
		MenuTeam = (Button) findViewById(R.id.TLS_Team_Btn);
		MenuTask = (Button) findViewById(R.id.TLS_Task_Btn);
		MenuMore = (Button) findViewById(R.id.TLS_More_Btn);
		SortBtn = (Button) findViewById(R.id.TLS_SortBtn);
		ChatButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				// TODO CALL THE CHAT SCREEN..
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
				Intent PageChange = new Intent(getApplicationContext(),
						HomeScreen.class);
				PageChange.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(PageChange, 110);
			}
		});
		MenuTask.setEnabled(false);// SET THE SAME SCREEN BUTTON DISABLED..
		MenuTask.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				Intent PageChange = new Intent(getApplicationContext(),
						TaskList.class);
				PageChange.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(PageChange, 110);
			}
		});
		MenuTeam.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				Intent PageChange = new Intent(getApplicationContext(),
						TeamList.class);
				PageChange.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(PageChange, 110);
			}
		});
		MenuMore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				Intent PageChange = new Intent(getApplicationContext(),
						MoreSettings.class);
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

	protected void ShowThisMsg(String title, String message, final int type) {
		DIALOG_SHOW_MSG = new AlertDialog.Builder(TaskList.this)
				.setIcon(R.drawable.ic_launcher).setTitle(title)
				.setMessage(message)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						DIALOG_SHOW_MSG.dismiss();
					}
				}).create();
		DIALOG_SHOW_MSG.show();
	}

	private void upDateDisplay() {
		if (TempDateType == 0) {
			Start_Date_Txt.setText(new StringBuilder()
					// Month is 0 based so add 1
					.append(padding(mYear)).append("-")
					.append(padding(mMonth + 1)).append("-").append(mDay)
					.append(" "));
			TempStrtDay = mDay;
			TempStrtMnth = mMonth;
			TempStrtYr = mYear;
		} else if (TempDateType == 1) {
			End_Date_Txt.setText(new StringBuilder()
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
			upDateDisplay();
		}
	};

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			final Calendar c = Calendar.getInstance(); // Date Instance
			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH);
			mDay = c.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return null;
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
			PG = new ProgressDialog(TaskList.this);
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
					UpdateTaskList();
					break;
				case 2:AfterGetAllMembersArray(RESULT_STRING);
					break;
				default:
					Toast.makeText(getApplicationContext(), "DONE : "+RESULT_STRING, Toast.LENGTH_LONG).show();
				}
			}else{
				Toast.makeText(getApplicationContext(), "Error:"+ERROR, Toast.LENGTH_LONG).show();
			}
		}
	}

} 
