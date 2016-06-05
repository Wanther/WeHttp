package cn.wanther.http;

import android.test.InstrumentationTestCase;
import android.util.Log;

import cn.wanther.http.Logger;

public class LoggerTest extends InstrumentationTestCase {

    private static final String TAG = "LoggerTest";

    public void testSplitChar() throws Exception {
        String str1 = "阿AAAAAAAAA";
        String str2 = "波BBBBBBBBB";
        String str3 = "呲CCC";

        String[] result = Logger.splitString(str1 + str2 + str3, 10);

        assertEquals(result[0], str1);
        assertEquals(result[1], str2);
        assertEquals(result[2], str3);
    }

    public void testLog() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int count = i < 3 ? 4000 : 10;

            for (int j = 0; j < count; j++) {
                sb.append((char)('A' + i));
            }
        }

        Logger.log(Log.INFO, TAG, sb.toString());

        // none assert
    }
}
