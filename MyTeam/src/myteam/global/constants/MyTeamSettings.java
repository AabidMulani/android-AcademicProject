package myteam.global.constants;

public class MyTeamSettings {

	private static boolean PlaySound=true;
	private static boolean DoVibrate=true;

	public static boolean isPlaySound() {
		return PlaySound;
	}

	public static void setPlaySound(boolean playSound) {
		PlaySound = playSound;
	}

	public static boolean isDoVibrate() {
		return DoVibrate;
	}

	public static void setDoVibrate(boolean doVibrate) {
		DoVibrate = doVibrate;
	}
}
