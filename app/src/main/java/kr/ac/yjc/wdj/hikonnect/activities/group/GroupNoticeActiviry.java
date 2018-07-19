package kr.ac.yjc.wdj.hikonnect.activities.group;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import kr.ac.yjc.wdj.hikonnect.Environments;
import kr.ac.yjc.wdj.hikonnect.R;
import kr.ac.yjc.wdj.hikonnect.activities.LoadingDialog;
import kr.ac.yjc.wdj.hikonnect.activities.groupDetail.TabsActivity;
import kr.ac.yjc.wdj.hikonnect.activities.group_list.GroupMenuActivity;
import kr.ac.yjc.wdj.hikonnect.activities.group_list.groups_list_main;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * The Activity used when make group notice
 * @author  Areum Lee (leear5799@gmail.com)
 * @since   2018-06-14
 */

public class GroupNoticeActiviry extends AppCompatActivity {
    // UI 변수
    private Button          findImgFileBtn,         // 이미지 파일 찾기 버튼
                            okBtn,                  // 확인 버튼
                            cancelBtn;              // 취소 버튼
    private EditText        noticeTitle,            // 공지사항 제목
                            noticeContents,         // 공지사항 내용
                            noticeImgFileName;      // 공지사항 첨부파일명(Img)
    private LoadingDialog   loadingDialog;          // 로딩 화면

    // 데이터 담을 변수
    private String          userId;             // 사용자 id
    private Uri             ImageCaptureUri;    // 업로드 할 이미지 uri
    private Bitmap          ImageBitmap;        // 이미지 bitmap
    private String          ImagePath;          // 이미지 경로

    // 이미지 파일 업로드 관련 변수
    private static final MediaType      MEDIA_TYPE_PNG           = MediaType.parse("image/png");
    private static final int            PICK_FROM_CAMERA         = 1;        // 카메라 촬영으로 사진 가져오기
    private static final int            PICK_FROM_GALLERY        = 2;        // 휴대폰 갤러리에서 사진 가져오기
    private static int                  columnPath;

    private static final String    TAG                      = "GroupNoticeActivity";

    // Session
    private SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_notice_app_bar);

        // 사용자 ID
        preferences         = getSharedPreferences("loginData", MODE_PRIVATE);
        userId              = preferences.getString("user_id", "");

        // 변수 초기화
        // UI 변수
        loadingDialog       = new LoadingDialog(this);

        findImgFileBtn      = (Button) findViewById(R.id.fileFindBtn);
        okBtn               = (Button) findViewById(R.id.okBtn);
        cancelBtn           = (Button) findViewById(R.id.cancelBtn);

        noticeTitle         = (EditText) findViewById(R.id.noticeTitle);
        noticeContents      = (EditText) findViewById(R.id.noticeContents);
        noticeImgFileName   = (EditText) findViewById(R.id.noticeImgFileName);

        // 각 버튼에 클릭 리스너 달기
        setBtnClickListner();
    }

    // 버튼별 클릭 리스너
    private void setBtnClickListner() {
        // 파일 찾기
        findImgFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 사진촬영 선택 시
                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 카메라에서 사진 촬영
                        doTakePhotoAction();
                    }
                };

                // 앨범선택 선택 시
                DialogInterface.OnClickListener galleryListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 휴대폰 갤러리 호출
                        getGallery();
                    }
                };

                // 취소 선택 시
                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                };

                // 이미지 업로드 방법 선택 할 dialog
                new AlertDialog.Builder(GroupNoticeActiviry.this)
                        .setTitle("업로드할 이미지를 선택하세요.")
                        .setPositiveButton("사진촬영", cameraListener)
                        .setNeutralButton("앨범선택", galleryListener)
                        .setNegativeButton("취소", cancelListener)
                        .show();
            }
        });

        // 확인
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 선택된 사진이 있는지 확인
                // 선택된 사진이 O => 인터넷 연결 상태 확인 후, 서버 업로드
                // 선택된 사진이 X => 경고창
                if (!TextUtils.isEmpty(ImagePath)) {
                    // 인터넷 연결 상태 확인
                    // 연결되어 있지 X => 경고창
                    // 연결되어 O => 서버 업로드
                    if (NetworkHelper.isConnectedToInternet(getBaseContext()) == false) {
                    Toast.makeText(getApplicationContext(), "인터넷 연결 상태를 확인하세요.",
                            Toast.LENGTH_LONG).show();
                    } else {
                        new UploadNoticeText().execute();     // 공지사항 Text 업로드 (제목, 내용)
                        new UploadNoticeImgFile().execute();  // 공지사항 첨부파일 업로드 (Image)
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "업로드할 파일을 선택하세요.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 취소
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이전 화면으로 돌아가기
                GroupNoticeActiviry.super.onBackPressed();
            }
        });
    }

    // 직접 휴대폰으로 사진 촬영
    private void doTakePhotoAction() {
        //
    }

    // 사진 선택을 위해 갤러리 호출
    private void getGallery() {
        // ACTION_PICK은 안드로이드 KitKat(level 19)부터 이용 가능
        Log.d("select gallery", "갤러리 선택");
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);

        //final Intent chooserIntent = Intent.createChooser(galleryIntent, "이미지 선택");
        startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // URI 정보를 이용하여 사진 정보 얻어 옴
        super.onActivityResult(requestCode, resultCode, data);
        /*if (resultCode == RESULT_OK && requestCode == 1010) {
            Uri         selectedImageUri    = data.getData();
            String[]    filaPathColumn      = {MediaStore.Images.Media.DATA};

            Cursor      cursor              = getContentResolver().query(selectedImageUri, filaPathColumn,
                                                                null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();

                int coulumnIndex    = cursor.getColumnIndex(filaPathColumn[0]);
                ImagePath           = cursor.getString(coulumnIndex);

                noticeImgFileName.setText(ImagePath + ".png");
            } else {
                Toast.makeText(getApplicationContext(), "업로드 된 이미지가 없습니다.", Toast.LENGTH_LONG);
            }
        }*/
        if (resultCode == RESULT_OK && requestCode == PICK_FROM_GALLERY) {
            if (data != null) {
                Uri imageFile = data.getData();

                getImagePathToUri(imageFile);
            }
        }
    }

    // URI 정보를 이용하여 사진 정보를 호출
    private void getImagePathToUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();

        //Cursor cursor = managedQuery(uri, projection, null, null, null);
        //startManagingCursor(cursor);

        columnPath      = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        ImagePath       = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));

        Log.d("공지사항 이미지 경로", cursor.getString(columnPath));
        Log.d("공지사항 이미지 경로2", ImagePath);
        noticeImgFileName.setText(ImagePath);
    }

    // 공지사항 TEXT 서버 업로드 (제목, 내용)
    private class UploadNoticeText extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();

                RequestBody body = new FormBody.Builder()
                        .add("writer", userId)
                        .add("uuid", TabsActivity.groupId)
                        .add("title", noticeTitle.getText().toString())
                        .add("content", noticeContents.getText().toString())
                        .add("picture", "")
                        .build();

                Request request = new Request.Builder()
                        .url(Environments.LARAVEL_HIKONNECT_IP + "/api/notice")
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().toString();
            } catch (IOException ie) {
                ie.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, s.toString());

            if (s == "false") {
                Toast.makeText(
                        getBaseContext(),
                        "공지사항 작성에 실패했습니다.",
                        Toast.LENGTH_SHORT
                ).show();
                loadingDialog.dismiss();
            } else {
                Toast.makeText(
                        getBaseContext(),
                        "공지사항이 작성되었습니다.",
                        Toast.LENGTH_SHORT
                ).show();
                loadingDialog.dismiss();
            }
        }
    }

    // 공지사항 첨부파일 업로드 (Image)
    /*private void UploadNoticeImageFile(String param, String filaName) {
        try {
            FileInputStream fileInputStream = new FileInputStream(filaName);
            URL url = new URL(Environments.NODE_HIKONNECT_IP + "/image/announce");
            Log.d("Test", "fileInputStream is " + fileInputStream);

            // open connection
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data");

            // write data
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private class UploadNoticeImgFile extends AsyncTask<String, Integer, Integer>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                JSONObject jsonObject = ImageUploadJSONParser.uploadImage(ImagePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            Log.d(TAG, result.toString());

            if (result.toString() == "false") {
                Toast.makeText(
                        getBaseContext(),
                        "공지사항 이미지 등록에 실패했습니다.",
                        Toast.LENGTH_SHORT
                ).show();
                loadingDialog.dismiss();

                Intent intent = new Intent(getBaseContext(), TabsActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(
                        getBaseContext(),
                        "공지사항 이미지가 등록되었습니다.",
                        Toast.LENGTH_SHORT
                ).show();
                loadingDialog.dismiss();

                Intent intent = new Intent(getBaseContext(), TabsActivity.class);
                startActivity(intent);
            }
        }
    }
}