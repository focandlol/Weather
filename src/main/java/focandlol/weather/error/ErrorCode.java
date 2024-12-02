package focandlol.weather.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    DIARY_NOT_FOUND("해당일에 일기가 없습니다"),
    INTERNAL_SERVER_ERROR("내부 서버 오류입니다"),
    INVALID_FORMAT("잘못된 입력 형식입니다.")
    ;
    private final String description;
}
