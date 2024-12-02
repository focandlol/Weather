package focandlol.weather.dto;

import focandlol.weather.error.ErrorCode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
    private ErrorCode code;
    private String message;
}
