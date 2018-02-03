package org.usfirst.frc.team1502.robot;

public class Timer {
	/*
	 * start new thread
	 * set timeout
	 * set interval
	 * clear timeout
	 * clear interval
	 */
	
	Runnable r;
	Thread _thread;
	
	enum TimerType {
		kTimeout,
		kInterval
	}
	
	public Timer(Runnable r, long millis, TimerType timerType) {
		this.r = r;
		if (timerType == TimerType.kTimeout) {
			Runnable timer = () -> {
				try {
					Thread.sleep(millis);
				} catch (InterruptedException e) {
					
				} finally {
					r.run();
				}
			};
			this._thread = startNewThread(timer);
		} else if (timerType == TimerType.kInterval) {
			Runnable interval = () -> {
				while (true) {
					try {
						Thread.sleep(millis);
					} catch (InterruptedException e) {
						
					} finally {
						r.run();
					}
				}
			};
			this._thread = startNewThread(interval);
		}
	}
	
	public void stop() {
		this._thread.interrupt();
	}
	
	public static Thread startNewThread(Runnable r) {
		Thread t = new Thread(r);
		t.start();
		return t;
	}
}
