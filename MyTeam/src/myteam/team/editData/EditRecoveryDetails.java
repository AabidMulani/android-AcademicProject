/*package myteam.team.editData;

import myteam.main.forms.MyTeam;
import myteam.main.forms.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class EditRecoveryDetails extends Activity{
	private Button Next=null;
	private Spinner Question=null;
	private EditText answer=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recovery_details_screen);
		Question=(Spinner) findViewById(R.id.RDS_QuestionSpinner);
		int count=GetSelectedSpinnerItem();
		Question.setSelection(count);
		answer=(EditText) findViewById(R.id.RDS_Answer_txt);
		answer.setText(ServerMemberData.getAnswer());
		answer.addTextChangedListener(MyTeam.EditTextSound);
		Next=(Button) findViewById(R.id.RDS_Proceed_Btn);
		Next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(Question.getSelectedItemPosition()==0){
					Question.performClick();
				}else if(answer.getText().toString().length()==0){
					answer.setError("EMPTY FIELD.");
				}else{
					ServerMemberData.setQuestion(Question.getSelectedItem().toString());
					ServerMemberData.setAnswer(answer.getText().toString());
					onBackPressed();
				}
			}
		});
	}
	
	private int GetSelectedSpinnerItem() {
		// TODO Auto-generated method stub
		String[] temp=getResources().getStringArray(R.array.Question_List); 
		String selected=ServerMemberData.getQuestion().toString();
		for(int i=0;i<temp.length;i++){
			if(selected.equals(temp[i]))
				return i;
		}
		return 999;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}
}
*/