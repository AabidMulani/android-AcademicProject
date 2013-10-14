package myteam.startup.pages;

import myteam.main.forms.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Logo extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_logo_screen);
		Animation mAnimation = new AlphaAnimation(1, 0);
	    mAnimation.setDuration(400);
	    mAnimation.setInterpolator(new LinearInterpolator());
	    mAnimation.setRepeatCount(Animation.INFINITE);
	    mAnimation.setRepeatMode(Animation.REVERSE); 
	    RelativeLayout layout=(RelativeLayout) findViewById(R.id.myteam_mainLayout);
	    TextView text=(TextView) findViewById(R.id.logo_text);
	    text.startAnimation(mAnimation);
	    layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivityForResult(new Intent(getApplicationContext(),LoginPage.class), 110);
			}
		});
	    
	}
}
