package kr.ac.yjc.wdj.hikonnect.apis.http_request;

import android.content.ContentValues;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * @file        kr.ac.yjc.wdj.hikonnect.apis.HttpRequestConnection.java
 * @author      Beomsu Kwon (rnjs9957@gmail.com)
 * @since       2018-03-26
 * @brief
 */

import android.content.ContentValues;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class HttpRequestConnection {

    public String request(String _url, ContentValues _params) {
        // HttpURLConnection 참조 변수.
        HttpURLConnection urlConn = null;
        // URL 뒤에 붙여서 보낼 파라미터.
        StringBuffer sbParams = new StringBuffer();
        Log.i("파라미터", _params.toString());
        Log.i("url", _url);

        /**
         * 1. StringBuffer에 파라미터 연결
         */
        // 보낼 데이터가 없으면 파라미터를 버린다.
        if (_params == null) {
            Log.v("보낼 데이터 ", "없다");
            sbParams.append("");
        }
        // 보낼 데이터가 있으면 파라미터를 채운다.
        else {
            // 파라미터가 2개 이상이면 파라미터 연결에 &가 필요하므로 스위칭할 변수 생성.
            boolean isAnd = false;
            // 파라미터 키와 값.
            String  key;
            String  value;

            for(Map.Entry<String, Object> parameter : _params.valueSet()) {
                key     = parameter.getKey();
                value   = parameter.getValue().toString();

                // 파라미터가 두개 이상일때, 파라미터 사이에 &를 붙인다.
                if (isAnd)
                    sbParams.append("&");

                sbParams.append(key).append("=").append(value);
                // 파라미터가 2개 이상이면 isAnd를 true로 바꾸고 다음 루프부터 &를 붙인다.
                if (isAnd == false)
                    if (_params.size() >= 2)
                        isAnd = true;
            }
        }


        /**
         * 2. HttpURLConnection을 통해 web의 데이터를 가져온다.
         */
        try{
            URL url = new URL(_url);
            urlConn = (HttpURLConnection) url.openConnection();

            // [2-1] urlConn 설정
            urlConn.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정
            urlConn.setDoInput(true);
            urlConn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
            urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;charset=UTF-8");
            urlConn.connect();
            Log.i("url", "연결");

            // [2-2] parameter 전달 및 데이터 읽어오기.
            String strParams = sbParams.toString(); // sbParams에 정리한 파라미터들을 스트링으로 저장 예) id=id1&pw=123;
            Log.i("strParams", strParams);
            // POST 전송 시 파라미터를 url에 합침.
            OutputStream os = urlConn.getOutputStream();
            Log.i("아웃풋 스트림", "생성함");
            os.write(strParams.getBytes("UTF-8")); // 출력 스트림에 출력.
            Log.i("아웃풋 스트림", "라이트");
            os.flush(); // 출력 스트림을 플러시(비운다)하고 버퍼링 된 모든 출력 바이트를 강제 실행
            Log.i("아웃풋 스트림", "플러시");
            os.close(); // 출력 스트림을 닫고 모든 시스템 자원을 해제.
            Log.i("아웃풋 스트림", "클로즈");

            // [2-3] 연결 요청 확인
            // 실패 시 null을 리턴하고 메서드를 종료.
            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.i("연결 실패", "안타깝...");
                return null;
            }

            // [2-4] 읽어온 결과물 리턴
            // 요청한 URL의 출력물을 BufferReader로 받는다
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));
            Log.i("버퍼리더", "읽음");

            // 출력물의 라인과 그 합에 대한 변수
            String line;
            String page = "";

            // 라인을 받아와 합친다.
            while ((line = reader.readLine()) != null) {
                page += line;
                Log.i("받은거",page);
            }
            return page;

        } catch (MalformedURLException e) { // for URL
            e.printStackTrace();
        } catch (IOException e) { // for openConnection()
            e.printStackTrace();
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }

        return null;
    }
}