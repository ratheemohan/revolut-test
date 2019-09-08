package com.revolut.test.transfersvc.util

import org.apache.log4j.LogManager
import org.apache.log4j.Logger

inline fun<reified T> T.logger(): Logger {
    if(T::class.isCompanion){
        return LogManager.getLogger(T::class.java.enclosingClass)
    }
    return LogManager.getLogger(T::class.java)
}