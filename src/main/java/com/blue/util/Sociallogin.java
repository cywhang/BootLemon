package com.blue.util;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@Slf4j
@Service
public class Sociallogin {

    @Value("${kakao.restapi-key}")
    private String krestapiKey;
    @Value("${kakao.redirect-url}")
    private String kredirectUrl;
    @Value("${naver.restapi-key}")
    private String nrestapiKey;
    @Value("${naver.secret-key}")
    private String nsecretKey;
    @Value("${naver.redirect-url}")
    private String nredirectUrl;

                                                                        /*     카카오 로그인      */
    // 카카오 로그인 url 생성
    public String getKaKaoUrl() {
        String kakaourl = "https://kauth.kakao.com/oauth/authorize?client_id=" + krestapiKey
                        + "&redirect_uri=" + kredirectUrl
                        + "&response_type=code";
        return kakaourl;
    }

    // 카카오 로그인 토큰 얻어오기
    public String getKaKaoAccessToken(String code) {
        String access_Token="";
        String refresh_Token ="";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try{
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=" + krestapiKey);
            sb.append("&redirect_uri=" + kredirectUrl);
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);
            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;
    }


    // 카카오 로그인 access token을 이용한 회원정보 반환받기
    public HashMap<Integer, String> createKakaoUser(String token) {
        String reqURL = "https://kapi.kakao.com/v2/user/me";

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            // boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            // System.out.println(hasEmail);
            //email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();

            // 0. 회원정보 객체 (필수 아이디, 선택 이메일)
            HashMap<Integer, String> map = new HashMap<>();
            // 1. 고유 아이디 설정
            String id = element.getAsJsonObject().get("id").getAsString();
            map.put(1, id);

            // 2. 필수 닉네임 설정
            String name = element.getAsJsonObject().get("properties").getAsJsonObject().get("nickname").getAsString();
            map.put(2, name);

            // 3. 선택 이메일 설정
            String email = "";
            // 선택항목인 이메일여부를 검사하고 값이 있다면 email값 세팅
            if (element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email") != null) {
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
                map.put(3, email);
            } else {
                map.put(3, "정보없음@");
            }


            System.out.println("id : " + id);
            System.out.println("email : " + email);
            System.out.println("nickname : " + name);

            br.close();

            return map;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

                                                                    /*     네이버 로그인      */
    // 네이버 로그인 url 생성
    public String getNaverUrl() {
        String naverurl = "https://nid.naver.com/oauth2.0/authorize?response_type=code"
                        + "&client_id=" + nrestapiKey
                        + "&redirect_uri=" + nredirectUrl;
        return naverurl;
    }


    // 네이버 로그인 토큰 받기
    public String getNaverAccessToken(String code){
        String access_Token="";
        String refresh_Token ="";
        String reqURL = "https://nid.naver.com/oauth2.0/token";
        try{
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=" + nrestapiKey);
            sb.append("&redirect_uri=" + nredirectUrl);
            sb.append("&client_secret=" + nsecretKey);
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);
            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;
    }


    // 네이버 로그인 access token을 이용한 회원정보 반환받기
    public HashMap<Integer, String> createNaverUser(String token) {
        String reqURL = "https://openapi.naver.com/v1/nid/me";

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            // 0. 회원정보 객체 (필수 아이디, 선택 이메일)
            HashMap<Integer, String> map = new HashMap<>();
            // 1. 고유 아이디 설정
            String id = element.getAsJsonObject().get("response").getAsJsonObject().get("id").getAsString();
            map.put(1, id);

            // 2. 필수 닉네임 설정
            String name = element.getAsJsonObject().get("response").getAsJsonObject().get("name").getAsString();
            String decodeString = StringEscapeUtils.unescapeJava(name);
            System.out.println("decoding: " + decodeString);
            map.put(2, decodeString);

            // 3. 선택 이메일 설정
            String email = "";
            // 선택항목인 이메일여부를 검사하고 값이 있다면 email값 세팅
            if (element.getAsJsonObject().get("response").getAsJsonObject().get("email") != null) {
                email = element.getAsJsonObject().get("response").getAsJsonObject().get("email").getAsString();
                map.put(3, email);
            } else {
                map.put(3, "정보없음@");
            }

            System.out.println("id : " + id);
            System.out.println("email : " + email);
            System.out.println("nickname : " + name);

            br.close();

            return map;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
