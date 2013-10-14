package myteam.global.constants;

import myteam.main.forms.MyTeam;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.os.AsyncTask;
import android.util.Log;

public class XmppLoginThread extends AsyncTask<Void, Void, String>{
private String user;
private String pass;
public XMPPConnection connection;

	public XmppLoginThread(String user,String pass) {
		this.user=user;
		this.pass=pass;
	}

	@Override
	protected String doInBackground(Void... params) {
		try {
			if (EstablishConnection()) {
				Log.d(getClass().getName(), "ESTABLISHED CONNECTION..");
				if (LoginChat(user, pass)) {
					Log.d(getClass().getName(), "LOGIN SUCCESSFULL..");
					BasicDetails.setLoginDone(true);
					MyTeam.connection = this.connection;
					return "DONE";
				} else {
					Log.e(getClass().getName(), "FAILED TO LOGIN CHAT");
					return "UNDONE";
				}
			} else {
				Log.e(getClass().getName(), "COUDNT ESTABLISH CONNECTION..");
				return "UNDONE";
			}
		} catch (Exception ex) {
			return "UNDONE";
		}
	}

		private boolean EstablishConnection() {
			String host = "talk.google.com";
			String port = "5222";
			String service = "gmail.com";
			// Create a connection
			ConnectionConfiguration connConfig = new ConnectionConfiguration(host,
					Integer.parseInt(port), service);
			connection = new XMPPConnection(connConfig);
			try {
				connection.connect();
				Log.i("XMPPClient",
						"[SettingsDialog] Connected to " + connection.getHost());
				return true;
			} catch (XMPPException ex) {
				Log.e("XMPPClient", "[SettingsDialog] Failed to connect to "
						+ connection.getHost());
				Log.e("XMPPClient", ex.toString());
				return false;
			}
		}

		private boolean LoginChat(String userid, String pass) {
			try {
				connection.login(userid, pass);
				Log.i("XMPPClient", "Logged in as " + connection.getUser());
				// Set the status to available
				Presence presence = new Presence(Presence.Type.available);
	            presence.setStatus("available");
	            presence.setPriority(40);
	            presence.setMode(Presence.Mode.available);
				connection.sendPacket(presence);
				return true;
			} catch (XMPPException ex) {
				Log.e("XMPPClient", "[SettingsDialog] Failed to log in as "
						+ userid);
				Log.e("XMPPClient", ex.toString());
				return false;
			}
		}


}
