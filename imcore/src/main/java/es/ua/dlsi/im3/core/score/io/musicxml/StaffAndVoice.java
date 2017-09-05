package es.ua.dlsi.im3.core.score.io.musicxml;

import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.Time;

public class StaffAndVoice implements Comparable<StaffAndVoice> {
	String staffNumber;
	String voiceNumber;
	Time currentTime;
	public StaffAndVoice(String staffNumber, String voiceNumber) {
		super();
		if (staffNumber == null || voiceNumber == null) {
			throw new IM3RuntimeException("staffNumber or voiceNumber are null");
		}
		this.staffNumber = staffNumber;
		this.voiceNumber = voiceNumber;
		this.currentTime = Time.TIME_ZERO;
	}
	
	public void addTime(Time time) {
		currentTime = currentTime.add(time);
	}

	public void substractTime(Time time) {
		currentTime = currentTime.substract(time);
	}
	
	public final String getStaffNumber() {
		return staffNumber;
	}

	public final String getVoiceNumber() {
		return voiceNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((staffNumber == null) ? 0 : staffNumber.hashCode());
		result = prime * result + ((voiceNumber == null) ? 0 : voiceNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StaffAndVoice other = (StaffAndVoice) obj;
		if (staffNumber == null) {
			if (other.staffNumber != null)
				return false;
		} else if (!staffNumber.equals(other.staffNumber))
			return false;
		if (voiceNumber == null) {
			if (other.voiceNumber != null)
				return false;
		} else if (!voiceNumber.equals(other.voiceNumber))
			return false;
		return true;
	}

	public Time getTime() {
		return currentTime;
	}

	public void setTime(Time time) {
		this.currentTime = time;
		
	}

	@Override
	public int compareTo(StaffAndVoice o) {
		int diff = staffNumber.compareTo(o.staffNumber);
		if (diff == 0) {
			diff = voiceNumber.compareTo(o.voiceNumber);
		}
		return diff;
	}

	@Override
	public String toString() {
		return "StaffAndVoice [staffNumber=" + staffNumber + ", voiceNumber=" + voiceNumber + ", currentTime="
				+ currentTime + "]";
	}
	
	
}
