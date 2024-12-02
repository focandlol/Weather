package focandlol.weather.error;

import focandlol.weather.WeatherApplication;
import focandlol.weather.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static focandlol.weather.error.ErrorCode.*;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DiaryException.class)
    public ErrorResponse diaryException(DiaryException e){
        logger.error("error 발생 " + e.getMessage());
        return new ErrorResponse(e.getErrorCode(),e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ConversionFailedException.class,IllegalArgumentException.class,
            MethodArgumentTypeMismatchException.class})
    public ErrorResponse illegalException(Exception e){
        logger.error("error 발생 " + e.getMessage());
        return new ErrorResponse(INVALID_FORMAT,INVALID_FORMAT.getDescription());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        logger.error("error 발생 " + e.getMessage());
        return new ErrorResponse(INTERNAL_SERVER_ERROR,INTERNAL_SERVER_ERROR.getDescription());
    }
}
