package myteam.global.constants;

import java.util.Properties;

import javax.mail.Session;
import javax.mail.Store;

import android.os.AsyncTask;

public class GmailLoginThread extends AsyncTask<Void, Void, String>{
	private String user=null,pass=null;
	
	public GmailLoginThread(String user,String pass) {
		this.user=user;
		this.pass=pass;
	}
	@Override
	protected String doInBackground(Void... arg0) {
		try {
			String host = "smtp.gmail.com";
			Properties props = System.getProperties();
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.user", user);
			props.put("mail.smtp.password", pass);
			props.put("mail.smtp.port", "465");
			props.put("mail.smtp.auth", "true");
			Session session = Session.getDefaultInstance(props, null);
			Store store = session.getStore("imaps");
			store.connect(host, user, pass);
			if (store.isConnected()) {
				store.close();
				return "DONE";
			}else{
				return "UNDONE";
			}
		} catch (Exception e) {
			return "UNDONE";
		}
	}

}
