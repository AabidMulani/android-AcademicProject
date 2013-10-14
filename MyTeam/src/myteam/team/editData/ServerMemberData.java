package myteam.team.editData;

public class ServerMemberData {
	private static boolean IsDataPresent;
	private static  String First_Name = null;
	private static  String Last_Name = null;
	private static  String Gender = null;
	private static  String Designation = null;
	private static  String DOB = null;
	private static  String Mobile_No = null;
	private static  String Address = null;
	private static  String City = null;
	private static  String State = null;
	private static  String Type = null;

	

	private static String NewFirst_Name = null;
	private static String NewLast_Name = null;
	private static String NewGender = null;
	private static String NewDesignation = null;
	private static String NewDOB = null;
	private static String NewMobile_No = null;
	private static String NewAddress = null;
	private static String NewCity = null;
	private static String NewState = null;
	private static String NewType = null;
	
	public static String getFirst_Name() {
		return First_Name;
	}
	public static String getLast_Name() {
		return Last_Name;
	}
	public static String getGender() {
		return Gender;
	}
	public static String getDesignation() {
		return Designation;
	}
	public static String getDOB() {
		return DOB;
	}
	public static String getMobile_No() {
		return Mobile_No;
	}
	public static String getAddress() {
		return Address;
	}
	public static String getCity() {
		return City;
	}
	public static String getState() {
		return State;
	}
	public static String getType() {
		return Type;
	}
	public static String getNewFirst_Name() {
		return NewFirst_Name;
	}
	public static String getNewLast_Name() {
		return NewLast_Name;
	}
	public static String getNewGender() {
		return NewGender;
	}
	public static String getNewDesignation() {
		return NewDesignation;
	}
	public static String getNewDOB() {
		return NewDOB;
	}
	public static String getNewMobile_No() {
		return NewMobile_No;
	}
	public static String getNewAddress() {
		return NewAddress;
	}
	public static String getNewCity() {
		return NewCity;
	}
	public static String getNewState() {
		return NewState;
	}
	public static String getNewType() {
		return NewType;
	}
	public static void setFirst_Name(String first_Name) {
		First_Name = first_Name;
	}
	public static void setLast_Name(String last_Name) {
		Last_Name = last_Name;
	}
	public static void setGender(String gender) {
		Gender = gender;
	}
	public static void setDesignation(String designation) {
		Designation = designation;
	}
	public static void setDOB(String dOB) {
		DOB = dOB;
	}
	public static void setMobile_No(String mobile_No) {
		Mobile_No = mobile_No;
	}
	public static void setAddress(String address) {
		Address = address;
	}
	public static void setCity(String city) {
		City = city;
	}
	public static void setState(String state) {
		State = state;
	}
	public static void setType(String type) {
		Type = type;
	}
	public static void setNewFirst_Name(String newFirst_Name) {
		NewFirst_Name = newFirst_Name;
	}
	public static void setNewLast_Name(String newLast_Name) {
		NewLast_Name = newLast_Name;
	}
	public static void setNewGender(String newGender) {
		NewGender = newGender;
	}
	public static void setNewDesignation(String newDesignation) {
		NewDesignation = newDesignation;
	}
	public static void setNewDOB(String newDOB) {
		NewDOB = newDOB;
	}
	public static void setNewMobile_No(String newMobile_No) {
		NewMobile_No = newMobile_No;
	}
	public static void setNewAddress(String newAddress) {
		NewAddress = newAddress;
	}
	public static void setNewCity(String newCity) {
		NewCity = newCity;
	}
	public static void setNewState(String newState) {
		NewState = newState;
	}
	public static void setNewType(String newType) {
		NewType = newType;
	}
	protected static boolean CheckIfChanged() {
		
		if(NewFirst_Name == null)
			NewFirst_Name=First_Name;
		if(NewLast_Name == null)
			NewLast_Name=Last_Name;
		if(NewGender == null)
			NewGender=Gender;
		if(NewDesignation == null)
			NewDesignation=Designation;
		if(NewDOB == null)
			NewDOB=DOB;
		if(NewMobile_No == null)
			NewMobile_No=Mobile_No;
		if(NewAddress == null)
			NewAddress=Address;
		if(NewCity == null)
			NewCity=City;
		if(NewState == null)
			NewState=State;
		if(NewType == null)
			NewType=Type;

		
		if (NewFirst_Name.equals(First_Name))
			if (NewLast_Name.equals(Last_Name))
				if (NewGender.equals(Gender))
					if (NewDesignation.equals(Designation))
						if (NewDOB.equals(DOB))
							if (NewMobile_No.equals(Mobile_No))
								if (NewAddress.equals(Address))
									if (NewCity.equals(City))
										if (NewState.equals(State))
											if (NewType.equals(Type))
															return false;
		return true;
	}
	public static boolean isIsDataPresent() {
		return IsDataPresent;
	}
	public static void setIsDataPresent(boolean isDataPresent) {
		IsDataPresent = isDataPresent;
	}

}
