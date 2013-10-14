package myteam.team.editData;


import myteam.global.constants.ConstantValues;
import myteam.main.forms.MyTeam;
import myteam.main.forms.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class EditContactDetails extends Activity {

	private EditText Addr = null;
	private EditText City = null;
	private EditText MobileNo = null;
	private Spinner State = null;
	private LinearLayout SpinnerLayout = null;
	private Button Back = null;
	private Button Next = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_details_screen);
		Addr = (EditText) findViewById(R.id.CDS_Addr_Txt);
		Addr.setText(ServerMemberData.getAddress());
		Addr.addTextChangedListener(MyTeam.EditTextSound);

		City = (EditText) findViewById(R.id.CDS_City_txt);
		City.setText(ServerMemberData.getCity());
		City.addTextChangedListener(MyTeam.EditTextSound);

		MobileNo = (EditText) findViewById(R.id.CDS_Mobile_txt);
		MobileNo.setText(ServerMemberData.getMobile_No());
		MobileNo.addTextChangedListener(MyTeam.EditTextSound);
		
		State = (Spinner) findViewById(R.id.CDS_StateSpinner);
		int itemId=GetSelectedSpinnerItem();
		if(itemId!=999)
			State.setSelection(itemId);
		SpinnerLayout = (LinearLayout) findViewById(R.id.CDS_state_layout);
		SpinnerLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				State.performClick();
			}
		});
		Back = (Button) findViewById(R.id.CDS_Back_Btn);
		Back.setVisibility(Button.GONE);
		Next = (Button) findViewById(R.id.CDS_Proceed_Btn);
		Next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				if (ValidateIt()) {
					ServerMemberData.setNewAddress(Addr.getText().toString());
					ServerMemberData.setNewCity(City.getText().toString());
					ServerMemberData.setNewState(State.getSelectedItem().toString());
					ServerMemberData.setNewMobile_No(MobileNo.getText().toString());
					onBackPressed();
				}
			}
		});
	}

	private int GetSelectedSpinnerItem() {
		// TODO Auto-generated method stub
		String[] temp=getResources().getStringArray(R.array.state_list); 
		String selected=ServerMemberData.getState().toString();
		for(int i=0;i<temp.length;i++){
			if(selected.equals(temp[i]))
				return i;
		}
		return 999;
	}

	private boolean ValidateIt() {

		boolean Flag = false;
		if (Addr.getText().toString().trim().length() == 0) {
			Addr.setError("EMPTY FIELD");
		} else if (City.getText().toString().trim().length() == 0) {
			City.setError("EMPTY FIELD");
		} else if (State.getSelectedItemId() == 0) {
			State.performClick();
		} else if (MobileNo.getText().toString().trim().length() != ConstantValues
				.getMobile_No_Length()) {
			MobileNo.setError("INVALID DATA");
		} else {
			Flag = true;
		}
		return Flag;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}
}
