/**
 * 
 */
package it.tooly.shared.util;

import java.util.concurrent.TimeUnit;

/**
 * @author R504570
 * 
 */
public class StopWatch {

	private long startTime = System.nanoTime();

	/**
	 * Returns the number of nano seconds (1 billionth of a second) that has elapsed since this method was last called (or the object was created).
	 * 
	 * @return the elapsed time
	 */
	public long getElapsedTime() {
		long currentTime = System.nanoTime();
		long result = currentTime - startTime;
		startTime = currentTime;

		return result;
	}

	/**
     * 
     */
	public String getElapsedTimeAsString() {
		return getTimeString(getElapsedTime(), 2);
	}

	protected static final long MICROSECOND = 1000;
	protected static final long MILISECOND = MICROSECOND * 1000;
	protected static final long SECOND = MILISECOND * 1000;
	protected static final long MINUTE = SECOND * 60;
	protected static final long HOUR = MINUTE * 60;

	protected String getTimeString(long nanoSeconds, int levelsDeep) {

		levelsDeep--;
		if (levelsDeep >= 0) {

			long hours = TimeUnit.NANOSECONDS.toHours(nanoSeconds);
			long minutes = TimeUnit.NANOSECONDS.toMinutes(nanoSeconds) % 60;
			long seconds = TimeUnit.NANOSECONDS.toSeconds(nanoSeconds) % 60;
			long millis = TimeUnit.NANOSECONDS.toMillis(nanoSeconds);
			long micros = TimeUnit.NANOSECONDS.toMicros(nanoSeconds);

			StringBuffer str = new StringBuffer();
			if (hours > 0) {
				str.append(hours).append(" hours ");
				long rest = nanoSeconds - (hours * HOUR);
				str.append(getTimeString(rest, levelsDeep));
			} else if (minutes > 0) {
				str.append(minutes).append(" minutes ");
				long rest = nanoSeconds - (minutes * MINUTE);
				str.append(getTimeString(rest, levelsDeep));
			} else if (seconds > 0) {
				str.append(seconds).append(" seconds ");
				long rest = nanoSeconds - (seconds * SECOND);
				str.append(getTimeString(rest, levelsDeep));
			} else if (millis > 0) {
				str.append(millis).append(" milliseconds ");
				long rest = nanoSeconds - (millis * MILISECOND);
				str.append(getTimeString(rest, levelsDeep));

			} else if (micros > 0) {
				str.append(micros).append(" microseconds ");
				long rest = nanoSeconds - (micros * MICROSECOND);
				str.append(getTimeString(rest, levelsDeep));

			} else if (nanoSeconds > 0) {
				str.append(nanoSeconds).append(" nanoseconds ");
			}

			return str.toString();

		}

		return "";

	}
}
