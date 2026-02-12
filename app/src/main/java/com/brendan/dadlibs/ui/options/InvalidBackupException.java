package com.brendan.dadlibs.ui.options;

public class InvalidBackupException extends Exception {
    public InvalidBackupException(String message) {
        super(message);
    }
    public InvalidBackupException(String message, Throwable cause) {
        super(message, cause);
    }
}