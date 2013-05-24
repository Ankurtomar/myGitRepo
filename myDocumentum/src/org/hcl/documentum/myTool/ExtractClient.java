package org.hcl.documentum.myTool;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Console of Extracter.
 * @author Ankur_Tomar
 */
public class ExtractClient {

    static ArrayList locationlist;
    static Pattern[] p;
    static Matcher m;
    static DctmClient c;
    static int count = 0;
    static Properties prop;

    public static void main(String[] a) throws IOException {

        prop = new Properties();
        prop.load(new FileInputStream("login.properties"));
        locationlist = new ArrayList();
        try {
            c = DctmClient.getInstance();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        final String query1 = prop.getProperty("Query_part1"), query2 = prop.getProperty("Query_part2");
        String[] allowedPath = prop.getProperty("Allowed_paths").split(";");
        p = new Pattern[allowedPath.length];
        for (int i = 0; i < allowedPath.length; i++) {
            p[i] = Pattern.compile(allowedPath[i]);
        }

        Thread inputBuffer = new Thread("Input Buffer") {

            String[] temp;

            public void run() {
                String input;
                String query;
                boolean validPath = false;
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        System.in));
                try {
                    while (true) {
                        System.out.println("Enter Query or Extract location : ");
                        input = br.readLine();
                        if (!input.equals(null)) {
                            if (input.startsWith("select")) {
                                ExtractThread t1 = new ExtractThread(c, input, "Query Result");
                            } else {
                                for (int i = 0; i < p.length; i++) {
                                    m = p[i].matcher(input);
                                    if (m.find()) {
                                        validPath = true;
                                        locationlist.add(count, input);
                                        temp = input.split("/");
                                        query = query1 + input + query2;
                                        System.out.println(query);
                                        ExtractThread t1 = new ExtractThread(c, query, temp[temp.length - 1]);
                                    }
                                }
                                if (!validPath) {
                                    System.out.println("........Invalid path entered.........");
                                }
                            }
                        }
                        Thread.sleep(2000);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        };
        inputBuffer.start();
    }
}
