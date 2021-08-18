package epam.az.worker.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.Arrays;
import java.util.Map;

@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = NotFoundException.class)
    public Message handleNotFoundException(NotFoundException ex) {

        String message = "Entity not found exception";
        log.error("Not found exception: {}", message, ex);

        return Message.builder()
                .message(message)
                .errorsMap(Map.of(ex.getErrorCode().toString(), Arrays.toString(ex.getValues())))
                .build();
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = DateFormatException.class)
    public Message handleDateFormatException(DateFormatException ex) {

        String message = "Date should be in format dd/MM/yyyy";
        log.error("Date format exception: {}", message, ex);

        return Message.builder()
                .message(message)
                .errorsMap(Map.of(ex.getErrorCode().toString(), Arrays.toString(ex.getValues())))
                .build();
    }



 }
