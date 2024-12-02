package focandlol.weather.controller;

import focandlol.weather.domain.Diary;
import focandlol.weather.service.DiaryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DiaryController.class)
class DiaryControllerTest {

    @MockBean
    private DiaryService diaryService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("일기 생성 성공")
    void createDiary() throws Exception {
        LocalDate date = LocalDate.now();
        String text = "create diary test";

        mockMvc.perform(post("/create/diary")
                        .param("date", date.toString())
                        .content(text)
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("일기 생성 실패")
    void createDiaryFailed() throws Exception {
        String text = "create diary test";

        mockMvc.perform(post("/create/diary")
                        .param("date", "2024-11-21-12-12")
                        .content(text)
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_FORMAT"))
                .andExpect(result -> {
                    Exception exception = result.getResolvedException();
                    assertTrue(exception instanceof MethodArgumentTypeMismatchException);
                });
    }

    @Test
    @DisplayName("해당 날짜의 일기 조회")
    void readDiary() throws Exception {
        LocalDate date = LocalDate.now();

        given(diaryService.readDiary(any()))
                .willReturn(Arrays.asList(
                        getDiary(1,"cloud","as",23.1,"first diary",date),
                        getDiary(2,"cloud","as",23.1,"sec diary",date)
                ));

        mockMvc.perform(get("/read/diary").param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].weather").value("cloud"))
                .andExpect(jsonPath("$[0].icon").value("as"))
                .andExpect(jsonPath("$[0].temperature").value(23.1))
                .andExpect(jsonPath("$[0].text").value("first diary"))
                .andExpect(jsonPath("$[0].date").value(date.toString()));

    }

    @Test
    @DisplayName("해당 기간 내 일기 조회")
    void readDiaries() throws Exception {
        LocalDate date = LocalDate.now();

        given(diaryService.readDiaries(any(),any()))
                .willReturn(Arrays.asList(
                        getDiary(1,"cloud","as",23.1,"first diary",date),
                        getDiary(2,"cloud","as",23.1,"sec diary",date)
                ));

        mockMvc.perform(get("/read/diaries")
                        .param("startDate", "2024-11-21")
                        .param("endDate", "2024-11-29"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].weather").value("cloud"))
                .andExpect(jsonPath("$[0].icon").value("as"))
                .andExpect(jsonPath("$[0].temperature").value(23.1))
                .andExpect(jsonPath("$[0].text").value("first diary"))
                .andExpect(jsonPath("$[0].date").value(date.toString()));

    }

    @Test
    @DisplayName("해당 날짜의 첫 번째 일기 수정")
    void updateDiary() throws Exception {
        LocalDate date = LocalDate.now();
        String text = "update diary test";

        mockMvc.perform(put("/update/diary")
                        .param("date", date.toString())
                        .content(text)
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("해당 날짜의 일기 삭제")
    void deleteDiary() throws Exception {
        LocalDate date = LocalDate.now();
        String text = "delete diary test";

        mockMvc.perform(delete("/delete/diary")
                        .param("date", date.toString())
                        .content(text)
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk());
    }

    private Diary getDiary(int id,String weather, String icon, double temperature, String text, LocalDate date){
        return Diary.builder()
                .id(id)
                .weather(weather)
                .icon(icon)
                .temperature(temperature)
                .text(text)
                .date(date).build();
    }

}