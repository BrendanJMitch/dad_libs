package com.brendan.dadlibs.ui.importexport;

public class InvalidBackupException extends Exception {
    public InvalidBackupException(String message) {
        super(message);
    }
    public InvalidBackupException(String message, Throwable cause) {
        super(message, cause);
    }
}