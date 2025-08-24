package com.norton.lms_backend.exception;

public class StorageException extends RuntimeException {

  public StorageException(Exception ex) {
    super(ex);
  }
}
