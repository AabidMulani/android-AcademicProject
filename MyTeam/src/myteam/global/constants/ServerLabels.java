package myteam.global.constants;

public class ServerLabels {
	private final static String FIRST_LABEL = "TYPE";
	private final static String CHECK_LOGIN = "CheckLogin";
	private final static String GET_BASIC_DETAILS = "BasicDetails";
	private final static String ENTER_EMPLOYEE_DETAILS = "EnterEmployeeValue";
	private final static String TYPE_USERNAME_AVAILIBILITY = "CheckUserName";
	private final static String FORGET_PASSWORD_1="GetQuestion";
	private final static String FORGET_PASSWORD_2="CheckAnswer";
	private final static String FORGET_PASSWORD_3="ChangePassWord";
	private final static String ALL_GROUP_LIST="GetGroupList";
	private final static String JOIN_GROUP="AddMember";
	private final static String REFRESH_NOTIFICATION="Refresh";
	private final static String PENDING_NOTIFICATION="Check";
	private final static String DELETE_NOTIFICATION="Delete";
	
	
	
	public final static String[] EnterEmployeelabels = new String[] {
			"EmployeeType", "UserName", "PassWord", "FirstName", "LastName",
			"Gender", "Designation", "DOB", "MobileNo", "Address", "City",
			"State", "SecurityQ", "SecurityA" };



	public static String getFirstLabel() {
		return FIRST_LABEL;
	}



	public static String getCheckLogin() {
		return CHECK_LOGIN;
	}



	public static String getGetBasicDetails() {
		return GET_BASIC_DETAILS;
	}



	public static String getEnterEmployeeDetails() {
		return ENTER_EMPLOYEE_DETAILS;
	}



	public static String getTypeUsernameAvailibility() {
		return TYPE_USERNAME_AVAILIBILITY;
	}



	public static String getForgetPassword1() {
		return FORGET_PASSWORD_1;
	}



	public static String getForgetPassword2() {
		return FORGET_PASSWORD_2;
	}



	public static String getForgetPassword3() {
		return FORGET_PASSWORD_3;
	}



	public static String getAllGroupList() {
		return ALL_GROUP_LIST;
	}



	public static String getJoinGroup() {
		return JOIN_GROUP;
	}



	public static String getRefreshNotification() {
		return REFRESH_NOTIFICATION;
	}



	public static String getPendingNotification() {
		return PENDING_NOTIFICATION;
	}



	public static String getDeleteNotification() {
		return DELETE_NOTIFICATION;
	}



	public static String[] getEnteremployeelabels() {
		return EnterEmployeelabels;
	}


}
