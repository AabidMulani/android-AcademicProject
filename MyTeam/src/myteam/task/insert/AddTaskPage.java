package myteam.task.insert;

import java.util.Calendar;

import myteam.global.constants.ConstantValues;
import myteam.main.forms.HomeScreen;
import myteam.main.forms.MyTeam;
import myteam.main.forms.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddTaskPage extends Activity {
	// CONSTANTS
	private String TaskTypeArray[] = ConstantValues.getTaskTypeArray();
	private final int ShowTaskType = 0;
	private final int DATE_DIALOG_ID = 1;
	private final int Task_Is_MEETING = 0;
	private final int Task_Is_TRAINING = 1;
	private final int Task_Is_PRESENTATION = 2;
	private final int Task_Is_REPORTING = 3;
	private final int Task_Is_OTHER = 4;
	private final String TempDateString = "[--/--/----]";

	// VARIABLES
	private int mYear, TempStrtYr, TempEndYr;
	private int mMonth, TempStrtMnth, TempEndMnth;
	private int mDay, TempStrtDay, TempEndDay;
	private AlertDialog DialogTaskType;
	private int SELECTED_TASK;
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
	private int COLOR_DISABLE=R.color.Cornsilk;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyTeam.currentContext=this;
		// Typeface CurvedFont=Typeface.createFromAsset(getAssets(),
		// "fonts/**.otf");
		// Header_TextView.setTypeface(CurvedFont);
		showDialog(ShowTaskType);
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
		case ShowTaskType:
			DialogTaskType = new AlertDialog.Builder(AddTaskPage.this)
					.setTitle("Select Task Type:")
					.setSingleChoiceItems(TaskTypeArray, 0,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {

									switch (arg1) {
									case Task_Is_MEETING:
										SELECTED_TASK = Task_Is_MEETING;
										break;
									case Task_Is_TRAINING:
										SELECTED_TASK = Task_Is_TRAINING;
										break;
									case Task_Is_PRESENTATION:
										SELECTED_TASK = Task_Is_PRESENTATION;
										break;
									case Task_Is_REPORTING:
										SELECTED_TASK = Task_Is_REPORTING;
										break;
									case Task_Is_OTHER:
										SELECTED_TASK = Task_Is_OTHER;
										break;
									default:
										Toast.makeText(getApplicationContext(),
												"THIS IS AM ERROR DUDE...!",
												Toast.LENGTH_LONG).show();
										Intent in = new Intent(
												getApplicationContext(),
												HomeScreen.class);
										startActivityForResult(in, 100);
									}
									ShowTaskWindow();
									DialogTaskType.dismiss();
								}
							})
					.setNegativeButton("Back",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									GoToPrevActivity();
								}
							}).create();
			return DialogTaskType;
		}
		return null;
	}

	private void ShowTaskWindow() {
		setContentView(R.layout.add_task_layout);
		//Header_Layout = (LinearLayout) findViewById(R.id.ATL_TaskHeaderLayout);
		//Task_Body_Layout = (LinearLayout) findViewById(R.id.ATL_TaskData_Layout);
		// Header_Layout.setVisibility(0);
		// Task_Body_Layout.setVisibility(0);
		Header_txt = (TextView) findViewById(R.id.Header_Txt);
		Header_txt.setText(TaskTypeArray[SELECTED_TASK]);
		Title_Edtxt = (EditText) findViewById(R.id.ATL_Title_txt);
		Title_Edtxt.addTextChangedListener(MyTeam.EditTextSound);

		Subject_Edtxt = (EditText) findViewById(R.id.ATL_Subject_Txt);
		Subject_Edtxt.addTextChangedListener(MyTeam.EditTextSound);
		int white_color=R.color.White;
		if (SELECTED_TASK != Task_Is_OTHER) {
			Title_Edtxt.setText(TaskTypeArray[SELECTED_TASK]);
			Title_Edtxt.setEnabled(false);
			Title_Edtxt.setTextColor(white_color);
			Title_Edtxt.setBackgroundColor(COLOR_DISABLE);
			Subject_Edtxt.requestFocus();
		} else {
			Title_Edtxt.requestFocus();
		}

		Desc_Edtxt = (EditText) findViewById(R.id.ATL_DescTxt);
		Desc_Edtxt.addTextChangedListener(MyTeam.EditTextSound);
	
		Location_Edtxt = (EditText) findViewById(R.id.ATL_Location_Txt);
		Location_Edtxt.addTextChangedListener(MyTeam.EditTextSound);

		Start_Date_txt = (TextView) findViewById(R.id.ATL_Start_Date_View);
		Start_Date_txt.setText(TempDateString);
		End_Date_txt = (TextView) findViewById(R.id.ATL_End_Date_View);
		End_Date_txt.setText(TempDateString);
		Close_Btn = (Button) findViewById(R.id.ATL_CloseBtn);
		Proceed_Btn = (Button) findViewById(R.id.ATL_ProceedBtn);
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
				GoToPrevActivity();
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

	private void Validation() {
		if (Title_Edtxt.getText().toString().trim().length() == 0) {
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
			Intent task_to_member = new Intent(getApplicationContext(),
					ShowMembersList.class);
			task_to_member.putExtra("Title", Title_Edtxt.getText().toString());
			task_to_member.putExtra("Subject", Subject_Edtxt.getText()
					.toString());
			task_to_member.putExtra("Desc", Desc_Edtxt.getText().toString());
			task_to_member.putExtra("StartDate", Start_Date_txt.getText()
					.toString());
			task_to_member.putExtra("EndDate", End_Date_txt.getText()
					.toString());
			if (Location_Edtxt.getText().toString().trim().length() == 0)
				Location_Edtxt.setText("NOT SET");
			task_to_member.putExtra("Location", Location_Edtxt.getText().toString());
			startActivityForResult(task_to_member, 1001);
		}
	}

	private void GoToPrevActivity() {
		Intent in = new Intent(getApplicationContext(), HomeScreen.class);
		in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(in, 100);
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

	public void onBackPressed() {
		super.onBackPressed();
		GoToPrevActivity();
	};
}
