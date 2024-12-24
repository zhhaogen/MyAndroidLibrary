package cn.zhg.basic.bug;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.zhg.basic.R;

public class BugReporterActivity extends Activity {
    public static final String EXTRA_PATH = " cn.zhg.basic.bug.EXTRA.PATH";
    public static final String EXTRA_AUTO = " cn.zhg.basic.bug.EXTRA.AUTO";
    public static final String EXTRA_URL = " cn.zhg.basic.bug.EXTRA.URL";
    private File bugsFile;
    private AlertDialog dialog;
    private String url;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            Log.d(BugReporter.TAG, "no happen ");
            finish();
            return;
        }
        String bugsFilePath = intent.getStringExtra(EXTRA_PATH);
        if (bugsFilePath == null) {
            Log.d(BugReporter.TAG, "无反馈信息 ");
            finish();
            return;
        }
        bugsFile = new File(bugsFilePath);
        boolean isAuto = intent.getBooleanExtra(EXTRA_AUTO, false);
        url = intent.getStringExtra(EXTRA_URL);
        if (isAuto) {
            sendCrash();
            return;
        }
        createDialog();
    }

    private void createDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.basic_label_bug_title);
        dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        dialogBuilder.setMessage(R.string.basic_label_bug_msg);
        dialogBuilder.setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                exit();
            }
        });
        dialogBuilder.setNegativeButton(R.string.basic_action_share, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                sendShare();
            }
        });
        if (url != null) {
            dialogBuilder.setNeutralButton(R.string.basic_action_bug_send, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    sendCrash();
                }
            });
        }
        dialog = dialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }

    private void exit() {
        Log.i(BugReporter.TAG, "退出程序");
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }


    public void onBackPressed() {
        exit();
        super.onBackPressed();
    }

    private void sendShare() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, getBugs());
            // intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(bugsFile));
            intent.setType("text/plain");
            Intent oIntent = Intent.createChooser(intent, getString(R.string.basic_action_share));
            oIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(oIntent);
        } catch (Throwable igr) {
            igr.printStackTrace();
            Toast.makeText(this, igr.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void sendCrash() {
        if (!bugsFile.exists()||url==null) {
            return;
        }
        new Thread() {
            public void run() {
                Log.i(BugReporter.TAG, "反馈地址:" +url);
                HttpURLConnection uc = null;
                try {

                    uc = (HttpURLConnection) new URL(url)
                            .openConnection();
                    uc.setDoInput(true);
                    uc.setDoOutput(true);
                    OutputStream os = uc.getOutputStream();
                    InputStream is = new FileInputStream(bugsFile);
                    byte[] buff = new byte[1024 * 1024];
                    int len = 0;
                    while ((len = is.read(buff)) != -1) {
                        os.write(buff, 0, len);
                    }
                    is.close();
                    os.close();
                    uc.disconnect();
                    Log.i(BugReporter.TAG, "发送成功:");
                } catch (Throwable e) {
                    Log.w(BugReporter.TAG, "发送反馈信息失败");
                    e.printStackTrace();
                } finally {
                    if (uc != null) {
                        uc.disconnect();
                    }
                    exit();
                }
            }
        }.start();
    }

    private String getBugs() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        InputStream is = new FileInputStream(bugsFile);
        byte[] buff = new byte[1024 * 1024];
        int len = 0;
        while ((len = is.read(buff)) != -1) {
            os.write(buff, 0, len);
        }
        is.close();

        return new String(os.toByteArray());
    }
}
