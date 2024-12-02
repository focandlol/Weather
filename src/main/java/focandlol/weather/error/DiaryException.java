package focandlol.weather.error;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class DiaryException extends RuntimeException{
    private final ErrorCode errorCode;
    private final String errorMessage;

    public DiaryException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
