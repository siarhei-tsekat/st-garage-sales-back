package org.garagesale.payload;

import org.garagesale.exception.Error;

import java.time.LocalDateTime;

public class ApiResponse<T> {
    private LocalDateTime timestamp = LocalDateTime.now();
    private T payload;
    private Error error;

    public ApiResponse() {
    }

    public ApiResponse(T payload) {
        this.payload = payload;
    }

    public static ApiResponse<?> withError(String errorMessage) {
        ApiResponse<Object> resp = new ApiResponse<>();
        Error error = new Error();
        error.setMessage(errorMessage);
        resp.setError(error);
        return resp;
    }

    public static <T> ApiResponse<T> withPayload(T payload) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.setPayload(payload);
        return resp;
    }

    public static ApiResponse<?> withError(Error error) {
        ApiResponse<Object> resp = new ApiResponse<>();
        resp.setError(error);
        return resp;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
