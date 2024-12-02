package focandlol.weather.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import focandlol.weather.domain.DateWeather;
import focandlol.weather.domain.Diary;
import focandlol.weather.error.DiaryException;
import focandlol.weather.repository.DateWeatherRepository;
import focandlol.weather.repository.DiaryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.time.LocalDate;
import java.util.*;

import static focandlol.weather.error.ErrorCode.DIARY_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {

    @Mock
    DiaryRepository diaryRepository;

    @Mock
    DateWeatherRepository dateWeatherRepository;

    @Spy
    @InjectMocks
    DiaryService diaryService;

    @Test
    @DisplayName("create diary test")
    void createDiary(){
        doReturn(new DateWeather(LocalDate.now(),"Clouds","as",12.1))
                .when(diaryService).getDateWeather(LocalDate.now());

        diaryService.createDiary(LocalDate.now(),"test diary");

        ArgumentCaptor<Diary> captor = ArgumentCaptor.forClass(Diary.class);

        verify(diaryRepository,times(1)).save(captor.capture());

        assertEquals("Clouds",captor.getValue().getWeather());
        assertEquals("as",captor.getValue().getIcon());
        assertEquals(12.1,captor.getValue().getTemperature());
        assertEquals("test diary",captor.getValue().getText());
    }

    @Test
    @DisplayName("read diary test")
    void readDiary(){
        given(diaryRepository.findAllByDate(any()))
                .willReturn(Arrays.asList(
                        Diary.builder()
                                .id(1)
                                .icon("as")
                                .text("test 1")
                                .temperature(13.1)
                                .date(LocalDate.now())
                                .weather("Clouds")
                                .build(),
                        Diary.builder()
                                .id(2)
                                .icon("as")
                                .text("test 2")
                                .temperature(13.1)
                                .date(LocalDate.now())
                                .weather("Clouds")
                                .build()
                ));

        List<Diary> diaries = diaryService.readDiary(LocalDate.now());

        assertEquals(2,diaries.size());
        assertEquals("Clouds",diaries.get(0).getWeather());
        assertEquals("as",diaries.get(0).getIcon());
        assertEquals(13.1,diaries.get(0).getTemperature());
        assertEquals("test 1",diaries.get(0).getText());
        assertEquals("test 2",diaries.get(1).getText());
    }

    @Test
    @DisplayName("read diaries test")
    void readDiaries(){
        given(diaryRepository.findAllByDateBetween(any(),any()))
                .willReturn(Arrays.asList(
                        Diary.builder()
                                .id(1)
                                .icon("as")
                                .text("test 1")
                                .temperature(13.1)
                                .date(LocalDate.of(2024,11,12))
                                .weather("Clouds")
                                .build(),
                        Diary.builder()
                                .id(2)
                                .icon("rn")
                                .text("test 2")
                                .temperature(6.1)
                                .date(LocalDate.of(2024,11,24))
                                .weather("Rainy")
                                .build()
                ));

        List<Diary> diaries = diaryService.readDiaries(LocalDate.now(), LocalDate.now());

        assertEquals(2,diaries.size());
        assertEquals("Clouds",diaries.get(0).getWeather());
        assertEquals("as",diaries.get(0).getIcon());
        assertEquals(13.1,diaries.get(0).getTemperature());
        assertEquals("test 1",diaries.get(0).getText());
        assertEquals(LocalDate.of(2024,11,12),diaries.get(0).getDate());
        assertEquals("Rainy",diaries.get(1).getWeather());
        assertEquals("rn",diaries.get(1).getIcon());
        assertEquals(6.1,diaries.get(1).getTemperature());
        assertEquals("test 2",diaries.get(1).getText());
        assertEquals(LocalDate.of(2024,11,24),diaries.get(1).getDate());
    }

    @Test
    @DisplayName("update diary success")
    void updateDiarySuccess(){
        given(diaryRepository.getFirstByDate(LocalDate.now()))
                .willReturn(Optional.of(Diary.builder()
                        .id(1)
                        .icon("as")
                        .text("test 1")
                        .temperature(13.1)
                        .date(LocalDate.of(2024,11,12))
                        .weather("Clouds")
                        .build()));

        ArgumentCaptor<Diary> captor = ArgumentCaptor.forClass(Diary.class);

        diaryService.updateDiary(LocalDate.now(),"test diary");

        verify(diaryRepository,times(1)).save(captor.capture());

        assertEquals("Clouds",captor.getValue().getWeather());
        assertEquals("as",captor.getValue().getIcon());
        assertEquals(13.1,captor.getValue().getTemperature());
        assertEquals("test diary",captor.getValue().getText());
    }

    @Test
    @DisplayName("update diary failed")
    void updateDiaryFailed(){
        given(diaryRepository.getFirstByDate(LocalDate.now()))
                .willReturn(Optional.empty());

        DiaryException diaryException = assertThrows(DiaryException.class,
                () -> diaryService.updateDiary(LocalDate.now(), "test diary"));

        assertEquals(DIARY_NOT_FOUND, diaryException.getErrorCode());
    }

    @Test
    @DisplayName("delete diary")
    void deleteDiary(){
        diaryService.deleteDiary(LocalDate.now());
        verify(diaryRepository,times(1)).deleteAllByDate(any());
    }

    @Test
    @DisplayName("api로 부터 날씨 정보 가져오기")
    void testGetWeatherStringSuccess() throws Exception {
        HttpURLConnection mockConnection = Mockito.mock(HttpURLConnection.class);

        doReturn(mockConnection).when(diaryService).openHttpConnection(anyString());

        given(mockConnection.getResponseCode()).willReturn(200);
        given(mockConnection.getInputStream()).willReturn(
                new ByteArrayInputStream((
                        "{\n" +
                                "  \"weather\": [{\"main\": \"Clear\", \"icon\": \"01d\"}],\n" +
                                "  \"main\": {\"temp\": 23.1}\n" +
                                "}").getBytes())
        );

        String response = diaryService.getWeatherString();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(
                "{\n" +
                        "  \"weather\": [{\"main\": \"Clear\", \"icon\": \"01d\"}],\n" +
                        "  \"main\": {\"temp\": 23.1}\n" +
                        "}");
        JsonNode jsonNode1 = objectMapper.readTree(response);

        assertEquals(jsonNode, jsonNode1);
    }

    @Test
    @DisplayName("날씨 정보 파싱하기")
    void testParseWeather() {
        String jsonString = "{\n" +
                "  \"weather\": [{\"main\": \"Clear\", \"icon\": \"01d\"}],\n" +
                "  \"main\": {\"temp\": 23.1}\n" +
                "}";

        Map<String, Object> result = diaryService.parseWeather(jsonString);

        assertEquals("Clear", result.get("main"));
        assertEquals("01d", result.get("icon"));
        assertEquals(23.1, result.get("temp"));
    }

    @Test
    @DisplayName("api로 부터 날씨 정보 가져오고 파싱 후 DateWeather 반환")
    void testGetWeatherFromApi() throws Exception {

        HttpURLConnection mockConnection = Mockito.mock(HttpURLConnection.class);
        doReturn(mockConnection).when(diaryService).openHttpConnection(anyString());

        given(mockConnection.getResponseCode()).willReturn(200);
        given(mockConnection.getInputStream()).willReturn(
                new ByteArrayInputStream((
                        "{\n" +
                                "  \"weather\": [{\"main\": \"Clear\", \"icon\": \"01d\"}],\n" +
                                "  \"main\": {\"temp\": 23.1}\n" +
                                "}").getBytes())
        );

        DateWeather result = diaryService.getWeatherFromApi();

        assertEquals(LocalDate.now(), result.getDate());
        assertEquals("Clear", result.getWeather());
        assertEquals(23.1, result.getTemperature());
        assertEquals("01d", result.getIcon());
    }

    @Test
    @DisplayName("해당 날짜의 날씨 정보가 db에 있으면 db에서 날씨 정보 가져오기")
    void testGetWeatherDb(){
        given(dateWeatherRepository.findAllByDate(any()))
                .willReturn(Arrays.asList(
                        new DateWeather(LocalDate.now(),"Clouds","as",12.1)
                ));

        diaryService.getDateWeather(LocalDate.now());

        verify(diaryService,times(0)).getWeatherFromApi();
    }

    @Test
    @DisplayName("해당 날짜의 날씨 정보가 db에 없으면 api로 날씨 정보 가져오기")
    void testGetWeatherApi(){
        given(dateWeatherRepository.findAllByDate(any()))
                .willReturn(new ArrayList<>());

        doReturn(new DateWeather(LocalDate.now(),"Clouds","as",12.1)).when(diaryService).getWeatherFromApi();

        diaryService.getDateWeather(LocalDate.now());

        verify(diaryService,times(1)).getWeatherFromApi();
    }


}