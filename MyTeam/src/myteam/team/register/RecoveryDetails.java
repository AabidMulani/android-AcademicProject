package myteam.team.register;

import myteam.main.forms.MyTeam;
import myteam.main.forms.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class RecoveryDetails extends Activity{
	private Button Next=null;
	private Spinner Question=null;
	private EditText answer=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recovery_details_screen);
		Question=(Spinner) findViewById(R.id.RDS_QuestionSpinner);
		answer=(EditText) findViewById(R.id.RDS_Answer_txt);
		answer.addTextChangedListener(MyTeam.EditTextSound);

		Next=(Button) findViewById(R.id.RDS_Proceed_Btn);
		Next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				if(Question.getSelectedItemPosition()==0){
					Question.performClick();
				}else if(answer.getText().toString().trim().length()==0){
					answer.setError("EMPTY FIELD.");
				}else{
					EmployeeValues.setQuestion(Question.getSelectedItem().toString());
					EmployeeValues.setAnswer(answer.getText().toString());
					Intent i= new Intent(getApplicationContext(),LoginDetails.class);
					startActivityForResult(i, 110);
				}
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
