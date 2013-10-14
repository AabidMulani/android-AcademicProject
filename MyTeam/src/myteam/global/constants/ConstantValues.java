package myteam.global.constants;

public class ConstantValues {
	private final static int Mobile_No_Length = 10;
	public final static String TaskTypeArray[] = new String[] { "MEETING", "TRAINING","PRESENTATION", "SUBMIT REPORT", "OTHER" };
	
	public static String[] getTaskTypeArray() {
		return TaskTypeArray;
	}

	public static int getMobile_No_Length() {
		return Mobile_No_Length;
	}

}
