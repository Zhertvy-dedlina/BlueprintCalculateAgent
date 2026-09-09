package ru.vsu.zhertvydedlina.aiservice.common.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
