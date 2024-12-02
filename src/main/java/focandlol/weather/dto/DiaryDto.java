package focandlol.weather.dto;

import focandlol.weather.domain.Diary;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaryDto {
    private String weather;
    private String icon;
    private double temperature;
    private String text;
    private LocalDate date;

    public static DiaryDto from(Diary diary) {
        return DiaryDto.builder()
                .weather(diary.getWeather())
                .icon(diary.getIcon())
                .temperature(diary.getTemperature())
                .text(diary.getText())
                .date(diary.getDate())
                .build();
    }
}
