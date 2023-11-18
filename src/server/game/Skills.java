package server.game;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Skills {

	private static String[] SKILLS = {
		"Attack",
		"Strength",
		"Defence",
		"Range",
		"Magic",
		"Hitpoints"
		
	};
	
	public static int getSkillID(String skill) {
		int skillId = -1;
		for (int i = 0; i <= SKILLS.length; i++) {
			if (SKILLS[i].equalsIgnoreCase(skill)) {
				skillId = i;
			}
		}
		
		return skillId;
	}
	
	public static String getSkillName(int skillId) {
		return SKILLS[skillId];
	}
	
	/*
	 * Custom method - saves looping round to find the Exp for a level
	 */
	public static int getExpForLevel(int level) {
		return (int) ((Math.pow((double) 2, (((double)level/7)-2))) * (level+300));
	}
	
	/*
	 * Have to use standard method as it's impossible to calculate otherwise.
	 */
	public static int getLevelForExp(int exp) {
		for(int i = 1; i <= 99; i++) {
			if(getExpForLevel(i) >= exp)
				return i;
		}
		return 0;
	}

	public static int getTotalExp(int skills[]) {
		int exp = 0;
		for (int i = 0; i < skills.length; i++) {
			exp += skills[i];
		}
		return exp;
	}
	
	public static int getExpMulitplier() {
		return isBonusWeekend() ? 2 : 1;
	}

	private static boolean isBonusWeekend() {
		final byte[] BONUS_EXP_DAYS = {
			Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY
		};
		Calendar cal = new GregorianCalendar();
		int d = cal.get(Calendar.DAY_OF_WEEK);
		for (byte b = 0; b < BONUS_EXP_DAYS.length; b++) {
			if (BONUS_EXP_DAYS[b] == d) {
				return true;
			}
		}
		return false;
	}
	
}