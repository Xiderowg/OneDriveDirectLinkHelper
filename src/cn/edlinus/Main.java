package cn.edlinus;

import java.util.*;
import java.io.*;
import java.net.URLDecoder;
import java.net.URL;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;

public class Main {

    public static void main(String[] args) {
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("|                          One Drive Direct Link Helper ver 0.1                          |");
        System.out.println("-----------------------------------------------------------------------------------------");
        BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
        String str = "";
        while (true) {
            // Get user input
            try {
                System.out.println("Please input or paste the link here, type in 'q' will terminate the program:");
                str = scanner.readLine();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (str.equals("q"))
                break;
            // Generate the direct link
            String dLink = "";
            if (str.length() > 0) {
                // Judge the link type
                if (str.contains("1drv.ms")) {
                    // Personal OneDrive link
                    try {
                        dLink = get1drv(str);
                    } catch (Exception e) {
                        System.out.println("Url recognized, but it is doesn't meet the standard, the standard url pattern should be like:");
                        System.out.println("https://1drv.ms/u/s!AnN4R6WWGczehw51hTeeCjrbL_4O");
                        //e.printStackTrace();
                        continue;
                    }
                } else if (str.contains("onedrive.live.com")) {
                    // Personal OneDrive link after redirection
                    try {
                        dLink = getLiveCom(str);
                    } catch (Exception e) {
                        System.out.println("Url recognized, but it is doesn't meet the standard, the standard url pattern should be like:");
                        System.out.println("https://onedrive.live.com/redir.aspx?cid=decc1996a5477873&resid=DECC1996A5477873!723&parId=DECC1996A5477873!700&authkey=!AqWkl_GohQTI94Y&Bsrc=SMIT&ref=button");
                        //e.printStackTrace();
                        continue;
                    }
                } else if (str.contains("sharepoint.com")) {
                    // Office 365 OneDrive link
                    try {
                        dLink = getSharepoint(str);
                    } catch (Exception e) {
                        System.out.println("Url recognized, but it is doesn't meet the standard, the standard url pattern should be like:");
                        System.out.println("https://guanghou-my.sharepoint.com/:v:/g/personal/rj6ayxdzw_get365_pw/EU9uT2CgbqdJrZOmm9FFpXcB6dso8KvS6n6RXfrK_B0e2Q?e=umxSJ4");
                        //e.printStackTrace();
                        continue;
                    }
                } else {
                    System.out.println("Unrecognized url, Please check again.");
                    continue;
                }
                System.out.println("The direct link is: " + dLink);
                setClipboardString(dLink);
                System.out.println("The link has been copy to clipboard.");
            }
        }
        try {
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Link like https://1drv.ms/u/s!AnN4R6WWGczehw51hTeeCjrbL_4O
     * Personal
     *
     * @param link
     * @return Direct Link
     * @throws Exception
     */
    public static String get1drv(String link) throws Exception {
        // Replace 1drv.ms with 1drv.ws
        return link.replaceFirst("1drv.ms", "1drv.ws");
    }

    /**
     * Link like https://onedrive.live.com/redir.aspx?cid=decc1996a5477873&resid=DECC1996A5477873!723&parId=DECC1996A5477873!700&authkey=!AqWkl_GohQTI94Y&Bsrc=SMIT&ref=button
     * Personal
     *
     * @param link
     * @return Direct Link
     * @throws Exception
     */
    public static String getLiveCom(String link) throws Exception {
        // Get resID and authKey first
        String resID, authKey;
        Map<String, String> queryParams = getQueryParams(link);
        resID = queryParams.get("resid");
        authKey = queryParams.get("authkey");
        // Generate the direct link
        StringBuilder sb = new StringBuilder("http://storage.live.com/items/");
        sb.append(resID);
        sb.append("?authkey=");
        sb.append(authKey);
        return sb.toString();
    }

    /**
     * Link like https://xxx-my.sharepoint.com/:b:/g/personal/xx_xxx_onmicrosoft_com/blah-blah
     * Office 365
     *
     * @param link
     * @return
     * @throws Exception
     */
    public static String getSharepoint(String link) throws Exception {
        // Get domain and path
        URL url = new URL(link);
        String path = url.getPath();
        String query = url.getQuery();
        String domain = link.substring(0, link.length() - path.length() - query.length() - 1);
        // Extract user info
        String[] tmp = path.split("/");
        String user_info = tmp[4], resID = tmp[5];
        // Return the direct link
        StringBuilder sb = new StringBuilder(domain);
        sb.append("personal/");
        sb.append(user_info);
        sb.append("/_layouts/15/download.aspx?share=");
        sb.append(resID);
        return sb.toString();
    }

    /**
     * Copy text to Clipboard
     *
     * @param text
     */
    public static void setClipboardString(String text) {
        // Get the system's Clipboard
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // Package the text
        Transferable trans = new StringSelection(text);
        // Set the contents to Clipboard
        clipboard.setContents(trans, null);
    }

    /**
     * Get Query Parameters from a url
     *
     * @param url
     * @return Query Parameters
     */
    public static Map<String, String> getQueryParams(String url) {
        try {
            Map<String, String> params = new HashMap<>();
            String[] urlParts = url.split("\\?");
            if (urlParts.length > 1) {
                String query = urlParts[1];
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    String key = URLDecoder.decode(pair[0], "UTF-8");
                    String value = "";
                    if (pair.length > 1) {
                        value = URLDecoder.decode(pair[1], "UTF-8");
                    }
                    params.put(key, value);
                }
            }

            return params;
        } catch (UnsupportedEncodingException ex) {
            throw new AssertionError(ex);
        }
    }

}
