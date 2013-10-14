package myteam.team.register;
import myteam.main.forms.MyTeam;
import myteam.main.forms.R;
import myteam.startup.pages.LoginPage;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class EmployeeType extends Activity {

	private RelativeLayout leader = null;
	private RelativeLayout member = null;
	private Intent i;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.employee_type);
		leader = (RelativeLayout) findViewById(R.id.ET_leader_relative_layout);
		member = (RelativeLayout) findViewById(R.id.ET_member_relative_layout);

		leader.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				EmployeeValues.setType("LEADER");
				i = new Intent(getApplicationContext(), PersonalDetails.class);
				startActivityForResult(i, 100);
			}
		});

		member.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				((MyTeam)getApplication()).PlayButtonSound();
				EmployeeValues.setType("MEMBER");
				i = new Intent(getApplicationContext(), PersonalDetails.class);
				startActivityForResult(i, 100);
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent login=new Intent(getApplicationContext(), LoginPage.class);
		login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(login, 110);
	}
}
