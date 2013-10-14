package myteam.global.constants;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;



public class ServerThread extends AsyncTask<Void, String, String>{
	Context context;
	URL url;
	HttpURLConnection conn;
	String xml;
	String URL;
	List<NameValuePair> nameValuePairs;
	public String ERROR =null;
	ProgressDialog PG =null;
	
	
	public ServerThread() {}

	public ServerThread(String URL,List<NameValuePair> nameValuePairs,Context context) {
		this.URL=URL;
		this.nameValuePairs=nameValuePairs;
		this.context=context;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		PG = new ProgressDialog(context);
		PG.setCancelable(false);
		PG.setMessage("Uploading...");
		PG.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		PG.show();
	}

	
	@Override
	protected String doInBackground(Void... params) {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(URL);
			try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity httpEntity= response.getEntity();
				xml=EntityUtils.toString(httpEntity);
				return xml;
			} catch (Exception e) {
				return "exception1 "+e.toString();
			}
		} catch (Exception ex) {
			return "exception2"+ex.toString();
		}
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if(PG != null){ if(PG.isShowing()){ PG.dismiss();}}
		
	}
	
	
}


/*public void convert(){
		//convert sdcard image to base64
		Bitmap bm = BitmapFactory.decodeFile("/sdcard/image.jpg");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object   
		byte[] b = baos.toByteArray(); 
		String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
		
		//convert base64 to bitmap
		byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
		Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
		ImageView img = new ImageView(context);
		img.setImageBitmap(decodedByte);
	}
	*/

