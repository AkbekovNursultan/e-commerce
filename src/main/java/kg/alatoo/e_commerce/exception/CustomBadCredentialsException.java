package kg.alatoo.e_commerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class CustomBadCredentialsException extends RuntimeException {

    public CustomBadCredentialsException() {
        super();
    }

    public CustomBadCredentialsException(String message) {
        super(message);
    }
}