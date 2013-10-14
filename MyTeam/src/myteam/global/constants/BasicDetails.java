package myteam.global.constants;

import android.graphics.Bitmap;

public class BasicDetails {
	
	private static String FirstName="NOT SET";
	private static String LastName="NOT SET";
	private static String Gender="NOT SET";
	private static String TYPE="NOT SET";
	private static String CurrentID="NOT SET";
	private static String LeaderID="NOT SET";
	private static String UserName="NOT SET";
	private static String EmailID="NOT SET";
	private static String EmailPass="NOT SET";
	private static boolean LoginDone=false;
	private static boolean ChatPageInit=false;
	private static boolean HomePageInit=false;
	private static boolean ParseChatIntent=false;
	public static Bitmap ProfilePic=null;
	private static String ProfileString=null;
	
	public static void clearBasicDetails(){
		FirstName="NOT SET";
		LastName="NOT SET";
		Gender="NOT SET";
		TYPE="NOT SET";
		CurrentID="NOT SET";
		LeaderID="NOT SET";
		UserName="NOT SET";
		EmailID="NOT SET";
		EmailPass="NOT SET";
		LoginDone=false;
		ChatPageInit=false;
		HomePageInit=false;
		ParseChatIntent=false;
		ProfilePic=null;
	}
	public static String getFirstName() {
		return FirstName;
	}
	public static String getLastName() {
		return LastName;
	}
	public static String getGender() {
		return Gender;
	}
	public static String getTYPE() {
		return TYPE;
	}
	public static String getCurrentID() {
		return CurrentID;
	}
	public static String getLeaderID() {
		return LeaderID;
	}
	public static void setFirstName(String firstName) {
		FirstName = firstName;
	}
	public static void setLastName(String lastName) {
		LastName = lastName;
	}
	public static void setGender(String gender) {
		Gender = gender;
	}
	public static void setTYPE(String tYPE) {
		TYPE = tYPE;
	}
	public static void setCurrentID(String iD) {
		CurrentID = iD;
	}
	public static void setLeaderID(String leaderID) {
		LeaderID = leaderID;
	}
	public static String getUserName() {
		return UserName;
	}
	public static void setUserName(String userName) {
		UserName = userName;
	}
	
	
	public static String getEmailID() {
		return EmailID;
	}
	public static void setEmailID(String emailID) {
		EmailID = emailID;
	}
	
	public String getEmailPass() {
		return EmailPass;
	}
	public static void setEmailPass(String emailPass) {
		EmailPass = emailPass;
	}
	public static boolean isLoginDone() {
		return LoginDone;
	}
	public static void setLoginDone(boolean loginDone) {
		LoginDone = loginDone;
	}
	public static boolean isChatPageInit() {
		return ChatPageInit;
	}
	public static void setChatPageInit(boolean loginPageInit) {
		ChatPageInit = loginPageInit;
	}
	public static boolean isHomePageInit() {
		return HomePageInit;
	}
	public static void setHomePageInit(boolean homePageInit) {
		HomePageInit = homePageInit;
	}
	public static boolean isParseChatIntent() {
		return ParseChatIntent;
	}
	public static void setParseChatIntent(boolean parseChatIntent) {
		ParseChatIntent = parseChatIntent;
	}
	public static Bitmap getProfilePic() {
		return ProfilePic;
	}
	public static String getProfileString() {
		return ProfileString;
	}
	public static void setProfileString(String profileString) {
		ProfileString = profileString;
	}

	
}
