package focandlol.weather.controller;

import focandlol.weather.dto.DiaryDto;
import focandlol.weather.service.DiaryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @ApiOperation(value = "일기 텍스트와 날씨를 이용해서 db에 일기 저장", notes = "This API fetches example data.")
    @PostMapping("/create/diary")
    void createDiary(@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate date,
                     @RequestBody String text){
        diaryService.createDiary(date,text);
    }

    @ApiOperation(value = "선택한 날짜의 모든 일기 데이터 가져옵니다")
    @GetMapping("/read/diary")
    List<DiaryDto> readDiary(@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate date) throws IllegalAccessException {
        return diaryService.readDiary(date).stream()
                .map(diary -> DiaryDto.from(diary))
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "선택한 기간중의 모든 일기 데이터 가져옵니다")
    @GetMapping("/read/diaries")
    List<DiaryDto> readDiaries(@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
                            @Parameter(description = "조회할 기간의 첫번째날", example = "2024-11-15")LocalDate startDate
    ,@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
                            @Parameter(description = "조회할 기간의 마지막날", example = "2024-11-21") LocalDate endDate){
        return diaryService.readDiaries(startDate,endDate).stream()
                .map(diary -> DiaryDto.from(diary))
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "해날 날짜의 첫번째 일기를 업데이트합니다")
    @PutMapping("/update/diary")
    void updateDiary(@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate date,
                     @RequestBody String text){
        diaryService.updateDiary(date,text);
    }

    @ApiOperation(value = "선택한 기간중의 모든 일기 데이터 삭제합니다")
    @DeleteMapping("/delete/diary")
    void deleteDiary(@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate date){
        diaryService.deleteDiary(date);
    }


}
