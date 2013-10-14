package myteam.team.management;

import myteam.main.forms.MyTeam;
import myteam.main.forms.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class WaitingPage extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.waiting_page);
		MyTeam.currentContext=this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.hoge_page_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.home_menu_logout:
			this.finish();
			((MyTeam)getApplication()).LogOut();
			break;
		case R.id.home_menu_refresh:
			break;
		}
		return true;
	}
}
