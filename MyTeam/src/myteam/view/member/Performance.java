package myteam.view.member;

import java.util.ArrayList;
import java.util.List;

import myteam.global.constants.ParseXmlData;
import myteam.global.constants.URL_Strings;
import myteam.main.forms.MyTeam;
import myteam.main.forms.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Performance extends Activity {
	private Bundle val = null;
	private boolean ShowChart = false;
	private String Name = null, MemID = null;
	private LinearLayout ChartLayout = null;
	private ProgressBar InTimeBar = null;
	private TextView MemberID = null;
	private CategorySeries mSeries = new CategorySeries("");
	private DefaultRenderer mRenderer = new DefaultRenderer();
	private GraphicalView mChartView;
	private ParseXmlData xmlParser = new ParseXmlData();
	private Document doc = null;
	private NodeList nl = null;
	private TextView TotalTask=null,CompletedTask=null;
	private AlertDialog DIALOG_SHOW_MSG;
	private static int[] COLORS = new int[] { Color.GREEN, Color.BLUE,
			Color.MAGENTA, Color.CYAN, Color.RED };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.member_performance_layout);
		val = getIntent().getExtras();
		Name = val.getString("NAME");
		MemID = val.getString("MEMBERID");
		MyTeam.currentContext=this;
		ChartLayout = (LinearLayout) findViewById(R.id.MPL_PieLayout);
		InTimeBar = (ProgressBar) findViewById(R.id.MPL_InTimeBar);
		MemberID = (TextView) findViewById(R.id.MPL_meberName);
		TotalTask=(TextView) findViewById(R.id.MPL_TotalTask);
		CompletedTask=(TextView) findViewById(R.id.MPL_CompletedTask);
		MemberID.setText(Name);
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setStartAngle(90);

		// SEND MEMBER ID

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("MEMBERID", MemID));
		// TODO add provision for PROFILE PIC....
		new PostToServer(nameValuePairs,URL_Strings.getUrlGetPerformance(),"Generating Performance Chart..").execute();
	}

	private void AddIntoPieChart(String str, double x) {
		mSeries.add(str + "(" + x + ")", x);
		SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
		renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
		mRenderer.addSeriesRenderer(renderer);

		if (mChartView != null) {
			mChartView.repaint();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (ShowChart) {
			if (mChartView == null) {
				ChartLayout = (LinearLayout) findViewById(R.id.MPL_PieLayout);
				mChartView = ChartFactory.getPieChartView(this, mSeries,
						mRenderer);
				mRenderer.setClickEnabled(true);
				mRenderer.setSelectableBuffer(10);
				mChartView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						((MyTeam)getApplication()).PlayButtonSound();
						SeriesSelection seriesSelection = mChartView
								.getCurrentSeriesAndPoint();
						if (seriesSelection == null) {
							// DO NOTHING
						} else {
							Toast.makeText(
									Performance.this,
									mSeries.getValue(seriesSelection
											.getPointIndex()) + "",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
				ChartLayout.addView(mChartView, new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			} else {
				mChartView.repaint();
			}
		}
	}
	
	protected void DrowIt(){
		if (ShowChart) {
			if (mChartView == null) {
				ChartLayout = (LinearLayout) findViewById(R.id.MPL_PieLayout);
				mChartView = ChartFactory.getPieChartView(this, mSeries,
						mRenderer);
				mRenderer.setClickEnabled(true);
				mRenderer.setSelectableBuffer(10);
				mChartView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						((MyTeam)getApplication()).PlayButtonSound();
						SeriesSelection seriesSelection = mChartView
								.getCurrentSeriesAndPoint();
						if (seriesSelection == null) {
							// DO NOTHING
						} else {
							Toast.makeText(
									Performance.this,
									mSeries.getValue(seriesSelection
											.getPointIndex()) + "",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
				ChartLayout.addView(mChartView, new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			} else {
				mChartView.repaint();
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	protected void ShowThisMsg(String title, String message, final int type) {
		DIALOG_SHOW_MSG = new AlertDialog.Builder(Performance.this)
				.setIcon(R.drawable.ic_launcher).setTitle(title)
				.setMessage(message)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						DIALOG_SHOW_MSG.dismiss();
					}
				}).create();
		DIALOG_SHOW_MSG.show();
	}

	
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
			PG = new ProgressDialog(Performance.this);
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
				doc = xmlParser.getDomElement(RESULT_STRING);
				nl = doc.getElementsByTagName("values");
				Element e = (Element) nl.item(0);
				
				if (xmlParser.getValue(e, "TOTALTASK")!= null
						&& xmlParser.getValue(e, "INTASK")!= null) {
					// GET THE PERFORMANCE DATA
					ShowChart = true;
					int MaxTask=Integer.parseInt(xmlParser.getValue(e, "TOTALTASK"));
					InTimeBar.setMax(MaxTask);
					TotalTask.setText(MaxTask+"");
					int InTimeTask=Integer.parseInt(xmlParser.getValue(e, "INTIME"));
					InTimeBar.setProgress(InTimeTask);
					CompletedTask.setText(InTimeTask+"");
					AddIntoPieChart("EXCELLENT",
							Integer.parseInt(xmlParser.getValue(e, "FIVE")));
					AddIntoPieChart("VERY GOOD",
							Integer.parseInt(xmlParser.getValue(e, "FOUR")));
					AddIntoPieChart("GOOD", Integer.parseInt(xmlParser.getValue(e, "THREE")));
					AddIntoPieChart("AVERAGE", Integer.parseInt(xmlParser.getValue(e, "TWO")));
					AddIntoPieChart("POOR", Integer.parseInt(xmlParser.getValue(e, "ONE")));
					//
					//
					DrowIt();
				} else {
					ShowChart = false;
					Toast.makeText(getApplicationContext(), "NO DATA AVAILABLE FOR PERFORMANCE GENERATION..", Toast.LENGTH_LONG).show();
				}
			}else{
				Toast.makeText(getApplicationContext(), "Error:"+ERROR, Toast.LENGTH_LONG).show();
			}
		}
	}

	
}
