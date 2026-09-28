package com.example.habitforge.exception;

public class DuplicateCompletionException extends RuntimeException {
    public DuplicateCompletionException(String message) { super(message); }
}