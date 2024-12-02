package focandlol.weather.controller;

import focandlol.weather.dto.DiaryDto;
import focandlol.weather.service.DiaryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
    @ApiResponses({
            @ApiResponse(code = 200, message = "일기 생성 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청 형식: DiaryException, ConversionFailedException 등"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @PostMapping("/create/diary")
    void createDiary(@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) @Parameter(description = "날짜", example = "2024-11-15")LocalDate date,
                     @RequestBody String text){
        diaryService.createDiary(date,text);
    }

    @ApiOperation(value = "선택한 날짜의 모든 일기 데이터 가져옵니다")
    @ApiResponses({
            @ApiResponse(code = 200, message = "일기 조회 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청 형식: DiaryException, ConversionFailedException 등"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @GetMapping("/read/diary")
    List<DiaryDto> readDiary(@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) @Parameter(description = "날짜", example = "2024-11-15")LocalDate date) throws IllegalAccessException {
        return diaryService.readDiary(date).stream()
                .map(diary -> DiaryDto.from(diary))
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "선택한 기간중의 모든 일기 데이터 가져옵니다")
    @ApiResponses({
            @ApiResponse(code = 200, message = "일기 조회 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청 형식: DiaryException, ConversionFailedException 등"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
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
    @ApiResponses({
            @ApiResponse(code = 200, message = "일기 업데이트 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청 형식: DiaryException, ConversionFailedException 등"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @PutMapping("/update/diary")
    void updateDiary(@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) @Parameter(description = "날짜", example = "2024-11-15")LocalDate date,
                     @RequestBody String text){
        diaryService.updateDiary(date,text);
    }

    @ApiOperation(value = "선택한 기간중의 모든 일기 데이터 삭제합니다")
    @ApiResponses({
            @ApiResponse(code = 200, message = "일기 삭제 성공"),
            @ApiResponse(code = 400, message = "잘못된 요청 형식: DiaryException, ConversionFailedException 등"),
            @ApiResponse(code = 500, message = "서버 내부 오류")
    })
    @DeleteMapping("/delete/diary")
    void deleteDiary(@RequestParam @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) @Parameter(description = "날짜", example = "2024-11-15")LocalDate date){
        diaryService.deleteDiary(date);
    }


}
