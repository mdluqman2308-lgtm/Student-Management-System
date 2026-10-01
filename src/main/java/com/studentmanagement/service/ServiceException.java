package com.studentmanagement.service;

/** An error whose message is safe and friendly enough to show to the user. */
public class ServiceException extends Exception {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
