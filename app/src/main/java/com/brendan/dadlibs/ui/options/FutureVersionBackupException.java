package com.brendan.dadlibs.ui.options;

public class FutureVersionBackupException extends Exception{

    public final String currentVersion;
    public final String backupVersion;


    public FutureVersionBackupException(String message, String currentVersion, String backupVersion){
        super(message);
        this.backupVersion = backupVersion;
        this.currentVersion = currentVersion;
    }

    public FutureVersionBackupException(String message, Throwable cause, String currentVersion, String backupVersion){
        super(message, cause);
        this.backupVersion = backupVersion;
        this.currentVersion = currentVersion;
    }
}
