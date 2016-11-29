package com.shenhua.account.ui;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

class SnackbarManager {
	private static final int MSG_TIMEOUT = 0;
	private static final int SHORT_DURATION_MS = 1500;
	private static final int LONG_DURATION_MS = 2750;
	private static SnackbarManager sSnackbarManager;

	static SnackbarManager getInstance() {
		if (sSnackbarManager == null) {
			sSnackbarManager = new SnackbarManager();
		}
		return sSnackbarManager;
	}

	private final Object mLock;
	private final Handler mHandler;
	private SnackbarRecord mCurrentSnackbar;
	private SnackbarRecord mNextSnackbar;

	private SnackbarManager() {
		mLock = new Object();
		mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
			@Override
			public boolean handleMessage(Message message) {
				switch (message.what) {
				case MSG_TIMEOUT:
					handleTimeout((SnackbarRecord) message.obj);
					return true;
				}
				return false;
			}
		});
	}

	interface Callback {
		void show();

		void dismiss(int event);
	}

	public void show(int duration, Callback callback) {
		synchronized (mLock) {
			if (isCurrentSnackbar(callback)) {
				mCurrentSnackbar.duration = duration;
				mHandler.removeCallbacksAndMessages(mCurrentSnackbar);
				scheduleTimeoutLocked(mCurrentSnackbar);
				return;
			} else if (isNextSnackbar(callback)) {
				mNextSnackbar.duration = duration;
			} else {
				mNextSnackbar = new SnackbarRecord(duration, callback);
			}
			if (mCurrentSnackbar != null
					&& cancelSnackbarLocked(mCurrentSnackbar,
							TSnackbar.Callback.DISMISS_EVENT_CONSECUTIVE)) {
				return;
			} else {
				mCurrentSnackbar = null;
				showNextSnackbarLocked();
			}
		}
	}

	public void dismiss(Callback callback, int event) {
		synchronized (mLock) {
			if (isCurrentSnackbar(callback)) {
				cancelSnackbarLocked(mCurrentSnackbar, event);
			} else if (isNextSnackbar(callback)) {
				cancelSnackbarLocked(mNextSnackbar, event);
			}
		}
	}

	public void onDismissed(Callback callback) {
		synchronized (mLock) {
			if (isCurrentSnackbar(callback)) {
				mCurrentSnackbar = null;
				if (mNextSnackbar != null) {
					showNextSnackbarLocked();
				}
			}
		}
	}

	public void onShown(Callback callback) {
		synchronized (mLock) {
			if (isCurrentSnackbar(callback)) {
				scheduleTimeoutLocked(mCurrentSnackbar);
			}
		}
	}

	public void cancelTimeout(Callback callback) {
		synchronized (mLock) {
			if (isCurrentSnackbar(callback)) {
				mHandler.removeCallbacksAndMessages(mCurrentSnackbar);
			}
		}
	}

	public void restoreTimeout(Callback callback) {
		synchronized (mLock) {
			if (isCurrentSnackbar(callback)) {
				scheduleTimeoutLocked(mCurrentSnackbar);
			}
		}
	}

	private static class SnackbarRecord {
		private final WeakReference<Callback> callback;
		private int duration;

		SnackbarRecord(int duration, Callback callback) {
			this.callback = new WeakReference<>(callback);
			this.duration = duration;
		}

		boolean isSnackbar(Callback callback) {
			return callback != null && this.callback.get() == callback;
		}
	}

	private void showNextSnackbarLocked() {
		if (mNextSnackbar != null) {
			mCurrentSnackbar = mNextSnackbar;
			mNextSnackbar = null;
			final Callback callback = mCurrentSnackbar.callback.get();
			if (callback != null) {
				callback.show();
			} else {
				mCurrentSnackbar = null;
			}
		}
	}

	private boolean cancelSnackbarLocked(SnackbarRecord record, int event) {
		final Callback callback = record.callback.get();
		if (callback != null) {
			callback.dismiss(event);
			return true;
		}
		return false;
	}

	private boolean isCurrentSnackbar(Callback callback) {
		return mCurrentSnackbar != null
				&& mCurrentSnackbar.isSnackbar(callback);
	}

	private boolean isNextSnackbar(Callback callback) {
		return mNextSnackbar != null && mNextSnackbar.isSnackbar(callback);
	}

	private void scheduleTimeoutLocked(SnackbarRecord r) {
		if (r.duration == TSnackbar.LENGTH_INDEFINITE) {
			return;
		}
		int durationMs = LONG_DURATION_MS;
		if (r.duration > 0) {
			durationMs = r.duration;
		} else if (r.duration == TSnackbar.LENGTH_SHORT) {
			durationMs = SHORT_DURATION_MS;
		}
		mHandler.removeCallbacksAndMessages(r);
		mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_TIMEOUT, r),
				durationMs);
	}

	private void handleTimeout(SnackbarRecord record) {
		synchronized (mLock) {
			if (mCurrentSnackbar == record || mNextSnackbar == record) {
				cancelSnackbarLocked(record,
						TSnackbar.Callback.DISMISS_EVENT_TIMEOUT);
			}
		}
	}
}