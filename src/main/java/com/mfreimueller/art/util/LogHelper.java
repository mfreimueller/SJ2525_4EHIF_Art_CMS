package com.mfreimueller.art.util;

import org.slf4j.Logger;

public final class LogHelper {

    private LogHelper() {}

    public static void logEnter(Logger log) {
        if (log.isDebugEnabled()) {
            var stackTrace = Thread.currentThread().getStackTrace();
            var caller = stackTrace[stackTrace.length - 2];

            log.debug("{}.{} entered", caller.getClassName(), caller.getMethodName());
        }
    }

    public static void logExit(Logger log) {
        if (log.isDebugEnabled()) {
            var stackTrace = Thread.currentThread().getStackTrace();
            var caller = stackTrace[stackTrace.length - 2];

            log.debug("{}.{} exit", caller.getClassName(), caller.getMethodName());
        }
    }

}
