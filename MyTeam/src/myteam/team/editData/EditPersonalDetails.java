package myteam.team.editData;

//TODO here i have a problem with on RESUME...Also if another details to be added or NOT
import java.util.Calendar;

import myteam.main.forms.MyTeam;
import myteam.main.forms.R;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class EditPersonalDetails extends Activity {
	private static final int DATE_DIALOG_ID = 0;
	private Spinner gender = null;
	private TextView DOB_Txt = null;
	private Button Date_Btn = null;
	private Button Next_Btn = null;
	private EditText FirstName_Txt = null;
	private EditText LastName_Txt = null;
	private EditText Designation_Txt = null;
	private int mYear;
	private int mMonth;
	private int mDay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_details_screen);
		FirstName_Txt = (EditText) findViewById(R.id.PDS_firstname_txt);
		FirstName_Txt.setText(ServerMemberData.getFirst_Name());
		FirstName_Txt.addTextChangedListener(MyTeam.EditTextSound);

		LastName_Txt = (EditText) findViewById(R.id.PDS_lastname_txt);
		LastName_Txt.setText(ServerMemberData.getLast_Name());
		LastName_Txt.addTextChangedListener(MyTeam.EditTextSound);

		DOB_Txt = (TextView) findViewById(R.id.PDS_DOB_txt);
		DOB_Txt.setText(ServerMemberData.getDOB());
		Designation_Txt = (EditText) findViewById(R.id.PDS_Designation_txt);
		Designation_Txt.setText(ServerMemberData.getDesignation());
		Designation_Txt.addTextChangedListener(MyTeam.EditTextSound);

		gender = (Spinner) findViewById(R.id.PDS_GenderSpinner);
		gender.setVisibility(8);
		Date_Btn = (Button) findViewById(R.id.PDS_DOB_Btn);
		
		Next_Btn = (Button) findViewById(R.id.PDS_Proceed_Btn);

		Date_Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				final Calendar c = Calendar.getInstance(); // Date Instance
				mYear = c.get(Calendar.YEAR);
				mMonth = c.get(Calendar.MONTH);
				mDay = c.get(Calendar.DAY_OF_MONTH);
				showDialog(DATE_DIALOG_ID);
			}
		});

		Next_Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				if (ValidateIt()) {
					ServerMemberData.setNewFirst_Name(FirstName_Txt.getText()
							.toString());
					ServerMemberData.setNewLast_Name(LastName_Txt.getText()
							.toString());
					ServerMemberData.setNewGender(gender.getSelectedItem()
							.toString());
					ServerMemberData.setNewDOB(DOB_Txt.getText().toString());
					ServerMemberData.setNewDesignation(Designation_Txt.getText()
							.toString());
					onBackPressed();
				}
			}
		});
	}
	public void showDatePicker(View view){
		final Calendar c = Calendar.getInstance(); // Date Instance
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		showDialog(DATE_DIALOG_ID);
	}
	private boolean ValidateIt() {
		boolean Flag = false;
		if (FirstName_Txt.getText().toString().trim().length() == 0) {
			FirstName_Txt.setError("Enter FIRST NAEM");
		} else if (LastName_Txt.getText().toString().trim().length() == 0) {
			LastName_Txt.setError("Enter LAST NAME");
		} else if (DOB_Txt.getText().toString().equals("[----/--/--]")) {
			Date_Btn.performClick();
		} else if (Designation_Txt.getText().toString().trim().length() == 0) {
			Designation_Txt.setError("Enter DESIGNATION");
		} else {
			Flag = true;
		}
		return Flag;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DATE_DIALOG_ID:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		}
	}

	private void updateDisplay() {
		DOB_Txt.setText(new StringBuilder()
				.append(padding(mYear)).append("-").append(padding(mMonth + 1))
				.append("-").append(mDay).append(" "));
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

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}
}
