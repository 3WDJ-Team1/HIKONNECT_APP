package kr.ac.yjc.wdj.hikonnect.APIs.HttpRequest;

import android.content.ContentValues;
import android.net.UrlQuerySanitizer;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

/**
 * Created by Kwon on 3/26/2018.
 */

public class HttpRequestConnection {

    public static String postRequest(String _url, ContentValues _params) {

        HttpURLConnection httpConn = null;
        BufferedReader reader = null;

        try{
            URL urlConn = new URL(_url);

            //HttpURLConnection 참조 변수.
            httpConn = (HttpURLConnection)urlConn.openConnection();

            // req 바디에 붙여 보낼 파라미터
            JSONObject json = new JSONObject();

            if (_params != null) {
                String key;
                String value;

                for(Map.Entry<String, Object> parameter : _params.valueSet()) {
                    key     = parameter.getKey();
                    value   = parameter.getValue().toString();

                    json.accumulate(key, value);
                }
            }

            httpConn.setDefaultUseCaches(false);
            httpConn.setRequestProperty("Accept", "application/json");
            httpConn.setRequestProperty("Content-type", "application/json");
            httpConn.setRequestMethod("POST");

            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);

            httpConn.setChunkedStreamingMode(0);

//            httpConn.connect();

            OutputStream os = httpConn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));

            writer.write(json.toString());
            writer.flush();
            writer.close();

            InputStream is = httpConn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(httpConn != null)
                httpConn.disconnect();
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getRequest(String _url) {

        // HttpURLConnection 참조 변수.
        HttpURLConnection urlConn = null;
        // URL 뒤에 붙여서 보낼 파라미터.
        StringBuffer sbParams = new StringBuffer();

        /**
         * 2. HttpURLConnection을 통해 web의 데이터를 가져온다.
         */
        try{
            URL url = new URL(_url);
            urlConn = (HttpURLConnection) url.openConnection();

            // [2-1] urlConn 설정
            urlConn.setRequestMethod("GET"); // URL 요청에 대한 메소드 설정
            urlConn.setDoInput(true);
            urlConn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.

            // [2-2] parameter 전달 및 데이터 읽어오기.
            String strParams = sbParams.toString(); // sbParams에 정리한 파라미터들을 스트링으로 저장 예) id=id1&pw=123;

            // [2-3] 연결 요청 확인
            // 실패 시 null을 리턴하고 메서드를 종료.
            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            // [2-4] 읽어온 결과물 리턴
            // 요청한 URL의 출력물을 BufferReader로 받는다
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

            // 출력물의 라인과 그 합에 대한 변수
            String line;
            String page = "";

            // 라인을 받아와 합친다.
            while ((line = reader.readLine()) != null) {
                page += line;
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
