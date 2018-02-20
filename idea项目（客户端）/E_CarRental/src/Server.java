import javax.activation.MimetypesFileTypeMap;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 史书恒 on 2016/10/31.
 */
public class Server {
//    public static String HOST = "http://localhost";//本地服务器
    public static String HOST = "http://www.whathell.top";//网络服务器


    public static String getString(String webpath) {//获取服务器返回数据（JSON形式）
        try {
            URL url = new URL(webpath);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setReadTimeout(20000);
            connection.connect();
            InputStream is = connection.getInputStream();
            return inputStreamToString(is);
        }catch (Exception e) {
            return "failed";
        }
    }

    /*InputStream类转String类*/
    public static String inputStreamToString (InputStream is) {
        StringBuffer sb = new StringBuffer();
        String result = "";
        byte[] buffer = new byte[1024];
        try {
            for(int i = 0; (i = is.read(buffer)) != -1; i++) {
                sb.append(new String(buffer, 0, i));
            }
            result = new String(sb.toString().getBytes(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**仿表单（form）上传图片
     * http://blog.csdn.net/wangpeng047/article/details/38303865
     */
    public static void upload(String filepath) {

        String urlStr = HOST+"/php/uploadimg.php";

        Map<String, String> textMap = new HashMap<String, String>();

        textMap.put("name", "testname");

        Map<String, String> fileMap = new HashMap<String, String>();

        fileMap.put("file", filepath);

        String ret = formUpload(urlStr, textMap, fileMap);

        System.out.println(ret);
    }

    /**
     * 上传图片
     *
     * @param urlStr
     * @param textMap
     * @param fileMap
     * @return
     */
    public static String formUpload(String urlStr, Map<String, String> textMap,
                                    Map<String, String> fileMap) {
        String res = "";
        HttpURLConnection conn = null;
        String BOUNDARY = "---------------------------123821742118716"; //boundary就是request头和上传文件内容的分隔符
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn
                    .setRequestProperty("User-Agent",
                            "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + BOUNDARY);

            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // text
            if (textMap != null) {
                StringBuffer strBuf = new StringBuffer();
                Iterator iter = textMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    String inputValue = (String) entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append(
                            "\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\""
                            + inputName + "\"\r\n\r\n");
                    strBuf.append(inputValue);
                }
                out.write(strBuf.toString().getBytes());
            }

            // file
            if (fileMap != null) {
                Iterator iter = fileMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String inputName = (String) entry.getKey();
                    String inputValue = (String) entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    File file = new File(inputValue);
                    String filename = file.getName();
                    String contentType = new MimetypesFileTypeMap()
                            .getContentType(file);
                    if (filename.endsWith(".png")) {
                        contentType = "image/png";
                    }
                    if (contentType == null || contentType.equals("")) {
                        contentType = "application/octet-stream";
                    }

                    StringBuffer strBuf = new StringBuffer();
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append(
                            "\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\""
                            + inputName + "\"; filename=\"" + filename
                            + "\"\r\n");
                    strBuf.append("Content-Type:" + contentType + "\r\n\r\n");

                    out.write(strBuf.toString().getBytes());

                    DataInputStream in = new DataInputStream(
                            new FileInputStream(file));
                    int bytes = 0;
                    byte[] bufferOut = new byte[1024];
                    while ((bytes = in.read(bufferOut)) != -1) {
                        out.write(bufferOut, 0, bytes);
                    }
                    in.close();
                }
            }

            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();

            // 读取返回数据
            StringBuffer strBuf = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuf.append(line).append("\n");
            }
            res = strBuf.toString();
            reader.close();
            reader = null;
        } catch (Exception e) {
            System.out.println("发送POST请求出错。" + urlStr);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
        return res;
    }

    /**下载网络图片
     *http://blog.csdn.net/csh159/article/details/7310009
     */
    public static boolean saveToFile(String destUrl) {
        String fileName = destUrl.replace(HOST+"php/cars/","");
        File dir = new File("cars");
        if (!(dir.exists() && dir.isDirectory()))
            dir.mkdirs();
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        HttpURLConnection httpUrl = null;
        URL url = null;
        int BUFFER_SIZE = 1024;
        byte[] buf = new byte[BUFFER_SIZE];
        int size = 0;
        try {
            url = new URL(destUrl);
            httpUrl = (HttpURLConnection) url.openConnection();
            httpUrl.connect();
            bis = new BufferedInputStream(httpUrl.getInputStream());
            fos = new FileOutputStream("./cars/"+fileName);
            while ((size = bis.read(buf)) != -1) {
                fos.write(buf, 0, size);
            }
            fos.flush();
        } catch (IOException e) {
            return false;
        } catch (ClassCastException e) {
            return false;
        } finally {
            try {
                fos.close();
                bis.close();
                httpUrl.disconnect();
            } catch (IOException e) {
                return false;
            } catch (NullPointerException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * 加载网络图片
     * http://blog.csdn.net/wxnjob/article/details/6143768
    * */

    public static ImageIcon loadURLImage(String url) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        ImageIcon ii = null;
        try {
            URL iurl = new URL(url);
            Image img = toolkit.createImage(iurl);
            ii = new ImageIcon(img);
        }
        catch (MalformedURLException mue) {
            mue.printStackTrace();
            ii = null;
        }
        finally {
            return ii;
        }
    }
}