package focandlol.weather.service;

import focandlol.weather.domain.DateWeather;
import focandlol.weather.domain.Diary;
import focandlol.weather.error.DiaryException;
import focandlol.weather.error.ErrorCode;
import focandlol.weather.repository.DateWeatherRepository;
import focandlol.weather.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;

    @Value("${openweathermap.key}")
    private String apiKey;

    /**
     * 매일 새벽 1시마다 api로 날씨 데이터 받아오는 메서드
     */
    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherDate(){
        dateWeatherRepository.save(getWeatherFromApi());
    }

    /**
     * 해당 날짜에 일기 쓰는 메서드
     */
    @Transactional
    public void createDiary(LocalDate date, String text) {
        //날씨 데이터 가져오기
        DateWeather dateWeather = getDateWeather(date);

        //파싱된 데이터 + 일기 값 db에 넣기
        Diary diary = new Diary();
        diary.dateWeather(dateWeather);
        diary.setText(text);

        diaryRepository.save(diary);
    }

    /**
     * 해당 날짜의 일기 가져오는 메서드
     */
    public List<Diary> readDiary(LocalDate date) {
        return diaryRepository.findAllByDate(date);
    }

    /**
     * 해당 기간의 일기 가져오는 메서드
     */
    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    /**
     * 해당 날짜의 첫 번째 일기글 수정 메서드
     */
    @Transactional
    public void updateDiary(LocalDate date, String text) {
        Diary getDiary = diaryRepository.getFirstByDate(date)
                        .orElseThrow(() -> new DiaryException(ErrorCode.DIARY_NOT_FOUND));
        getDiary.setText(text);

        //테스트를 위해
        diaryRepository.save(getDiary);
    }

    /**
     * 해당 날짜 일기 전부 삭제 메서드
     * @param date
     */
    @Transactional
    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }

    /**
     * api에서 날씨 데이터 갸져오는 메서드
     */
    protected String getWeatherString(){
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;

        try {
            HttpURLConnection con = openHttpConnection(apiUrl);
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }else{
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;
            StringBuilder response = new StringBuilder();
            while((inputLine = br.readLine()) != null){
                response.append(inputLine);
            }
            br.close();

            return response.toString();
        } catch (Exception e) {
            return "failed to get response";
        }
    }

    /**
     * 커넥션 메소드
     */
    protected HttpURLConnection openHttpConnection(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        return (HttpURLConnection) url.openConnection();
    }

    /**
     * getWeatherString() 에서 받아온 날씨 데이터 파싱 메소드
     */
    protected Map<String,Object> parseWeather(String jsonString){
        System.out.println("jsonString = " + jsonString);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject;

        try{
            jsonObject = (JSONObject) parser.parse(jsonString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> resultMap = new HashMap<String, Object>();

        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));

        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp",mainData.get("temp"));

        return resultMap;
    }

    /**
     * api를 통한 날씨 데이터 가져오기
     */
    protected DateWeather getWeatherFromApi(){
        //날씨 데이터 가져오기
        String weatherData = getWeatherString();

        //받아온 날씨 json 파싱하기
        Map<String, Object> parsedWeather = parseWeather(weatherData);

        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(parsedWeather.get("main").toString());
        dateWeather.setTemperature((Double) parsedWeather.get("temp"));
        dateWeather.setIcon(parsedWeather.get("icon").toString());
        return dateWeather;
    }

    /**
     * db에 입력받은 날짜의 날씨가 있을 시 리턴
     * 없을 시 api를 통해 현재 날씨 받아서 리턴
     */
    protected DateWeather getDateWeather(LocalDate date){
        List<DateWeather> dateWeatherListFromDb = dateWeatherRepository.findAllByDate(date);
        if(dateWeatherListFromDb.size() == 0){
            /**
             * 만약 입력받은 날짜 날씨가 없을 시 현재 날짜의 날씨를 리턴
             */
            return getWeatherFromApi();
        }
        return dateWeatherListFromDb.get(0);
    }



}
