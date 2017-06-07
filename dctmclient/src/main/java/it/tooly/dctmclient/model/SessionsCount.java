/**
 *
 */
package it.tooly.dctmclient.model;

/**
 * Information about the number of sessions on a content server
 *
 * @author M.E. de Boer
 *
 */
public class SessionsCount {
	final public static String A_CONCURRENT_SESSIONS = "concurrent_sessions";
	final public static String A_HOT_LIST_SIZE = "hot_list_size";
	final public static String A_WARM_LIST_SIZE = "warm_list_size";
	final public static String A_COLD_LIST_SIZE = "cold_list_size";
	final public static String DQL_COUNT_SESSIONS = "execute count_sessions";

	private int concurrent;
	private int hotSize;
	private int warmSize;
	private int coldSize;

	public SessionsCount(int concurrent, int hotSize, int warmSize, int coldSize) {
		setAll(concurrent, hotSize, warmSize, coldSize);
	}

	public void setAll(int concurrent, int hotSize, int warmSize, int coldSize) {
		this.concurrent = concurrent;
		this.hotSize = hotSize;
		this.warmSize = warmSize;
		this.coldSize = coldSize;
	}

	public int getHotSize() {
		return this.hotSize;
	}
	public void setHotSize(int hotSize) {
		this.hotSize = hotSize;
	}

	public int getWarmSize() {
		return this.warmSize;
	}
	public void setWarmSize(int warmSize) {
		this.warmSize = warmSize;
	}

	public int getColdSize() {
		return this.coldSize;
	}
	public void setColdSize(int coldSize) {
		this.coldSize = coldSize;
	}

	public int getConcurrent() {
		return this.concurrent;
	}

	public void setConcurrent(int concurrent) {
		this.concurrent = concurrent;
	}

}
