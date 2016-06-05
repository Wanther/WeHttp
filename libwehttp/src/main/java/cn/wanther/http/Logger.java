package cn.wanther.http;

import android.util.Log;

public abstract class Logger {

    private static Logger sInstance = new AndroidLogImpl(2048);

    public static void log(int level, String tag, String message) {
        sInstance.doLog(level, tag, message);
    }

    public static void log(int level, String tag, String message, Throwable t){
        sInstance.doLog(level, tag, message, t);
    }

    protected abstract void doLog(int level, String tag, String message);
    protected abstract void doLog(int level, String tag, String message, Throwable t);

    private static class AndroidLogImpl extends Logger{
        private final int splitCharCount;

        public AndroidLogImpl(int splitCharCount) {
            this.splitCharCount = splitCharCount;
        }

        @Override
        protected void doLog(int level, String tag, String message) {
            if (message.length() <= splitCharCount) {
                write(level, tag, message);
            } else {
                String[] split = splitString(message, splitCharCount);
                for (int i = 0; i < split.length; i++) {
                    write(level, tag + (i + 1), split[i]);
                }
            }
        }

        @Override
        protected void doLog(int level, String tag, String message, Throwable t) {
            if (message.length() <= splitCharCount) {
                write(level, tag, message, t);
            } else {
                String[] split = splitString(message, splitCharCount);
                for (int i = 0; i < split.length; i++) {
                    if (i < split.length - 1) {
                        write(level, tag + (i + 1), split[i]);
                    } else {    // 把异常写在最后一个
                        write(level, tag + (i + 1), split[i], t);
                    }
                }
            }
        }

        private void write(int level, String tag, String message) {
            if (level == Log.VERBOSE) {
                Log.v(tag, message);
            } else if (level == Log.DEBUG) {
                Log.d(tag, message);
            } else if (level == Log.INFO) {
                Log.i(tag, message);
            } else if (level == Log.WARN) {
                Log.w(tag, message);
            } else if (level == Log.ERROR) {
                Log.e(tag, message);
            } else {
                Log.w(tag, message);
            }
        }

        private void write(int level, String tag, String message, Throwable t) {
            if (level == Log.VERBOSE) {
                Log.v(tag, message, t);
            } else if (level == Log.DEBUG) {
                Log.d(tag, message, t);
            } else if (level == Log.INFO) {
                Log.i(tag, message, t);
            } else if (level == Log.WARN) {
                Log.w(tag, message, t);
            } else if (level == Log.ERROR) {
                Log.e(tag, message, t);
            } else {
                Log.w(tag, message, t);
            }
        }
    }

    public static String[] splitString(String str, int limit) {
        if (str == null || limit <= 0) {
            return null;
        }

        int len = str.length();

        String[] result = new String[len / limit + (len % limit == 0 ? 0 : 1)];

        for(int i = 0; i < result.length; i++) {
            result[i] = str.substring(i * limit, i == result.length - 1 ? len : ((i + 1) * limit));
        }

        return result;
    }

}
