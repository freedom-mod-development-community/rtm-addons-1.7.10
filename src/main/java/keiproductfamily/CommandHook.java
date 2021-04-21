package keiproductfamily;

import com.google.gson.JsonObject;
import jp.ngt.ngtlib.io.NGTLog;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import scala.util.parsing.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CommandHook extends CommandBase {
    @Override
    public String getCommandName() {
        return "Hook";
    }

    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] arg) {
        StringBuilder builder = new StringBuilder();
        for (String str : arg) {
            builder.append(" ").append(str);
        }
        String message = builder.toString().trim();

        requestWeb(setJsonObj(message, "HookFromMinecraft"), "https://discordapp.com/api/webhooks/822657597923655690/EtxsPyqnDnY17g33ctoAKnoAoj_2A3IXsMOwRTwVsDXoBCoxhfnUKaehVvpc4vdUuwxl");
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "Hook:commandUse.";
    }

    /**
     * JSONオブジェクトを生成する
     * <pre>
     * Discordでよく使う最低限のJSONオブジェクトを生成します。
     * </pre>
     *
     * @param content  : メッセージ
     * @param username ： 送信者名
     *                 //@param avatarUrl : 送信者画像URL
     **/
    String setJsonObj(String content, String username) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("content", content);

        //シリアライズ
        return jsonObject.toString();
    }

    void requestWeb(String json, String url) {

        try {
            //送信先URLを指定してHttpコネクションを作成する
            URL sendUrl = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) sendUrl.openConnection();

            //リクエストヘッダをセット
            con.addRequestProperty("Content-Type", "application/JSON; charset=utf-8");
            con.addRequestProperty("User-Agent", "DiscordBot");
            //URLを出力利用に指示
            con.setDoOutput(true);
            //要求方法にはPOSTを指示
            con.setRequestMethod("POST");

            //要求を送信する
            // POSTデータの長さを設定
            con.setRequestProperty("Content-Length", String.valueOf(json.length()));
            //リクエストのbodyにJSON文字列を書き込む
            OutputStream stream = con.getOutputStream();
            stream.write(json.getBytes(StandardCharsets.UTF_8));
            stream.flush();
            stream.close();

            // HTTPレスポンスコード
            final int status = con.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK
                    && status != HttpURLConnection.HTTP_NO_CONTENT) {
                //異常
                NGTLog.debug("error:" + status);
            }

            //後始末
            con.disconnect();

        } catch (MalformedURLException e) {
            //例外
            e.printStackTrace();
        } catch (IOException e) {
            //例外
            e.printStackTrace();
        }
    }
}