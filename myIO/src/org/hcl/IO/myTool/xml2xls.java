/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hcl.IO.myTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

class OnlyExt1 implements FilenameFilter {

    String ext;

    public OnlyExt1(String ext) {
        this.ext = "." + ext;
    }

    public boolean accept(java.io.File dir, String name) {
        return name.endsWith(ext);
    }
}

public class xml2xls {

    private static String file_separator = System.getProperty("file.separator");
    private static String current_directory = System.getProperty("user.dir");
    private static String filetype1 = "xml", filetype2 = "xls";
    private static String output_directory = current_directory + file_separator + "Excels";

    public static void main(String args[]) throws IOException {

        Object[] ob;
        String s1, s2;
        File f1 = new File(current_directory);
        File f2 = new File(output_directory);
        if (!f2.canRead()) {
            f2.mkdir();
        }
        FilenameFilter only = new OnlyExt1(filetype1);
        FilenameFilter xls = new OnlyExt1(filetype2);
        String xlsfile[] = f2.list(xls);
        String sfile[] = f1.list(only);
        ArrayList list = new ArrayList();

        System.out.println(xlsfile.length + "   >>  " + sfile.length);

        if (xlsfile.length > 0) {
            for (int i = 0; i < sfile.length; i++) {
                boolean flagy = false;
                for (int j = 0; j < xlsfile.length; j++) {
                    s2 = xlsfile[j].substring(0, xlsfile[j].indexOf(filetype2));
                    s1 = sfile[i].substring(0, sfile[i].indexOf(filetype1));
                    if (s1.equals(s2)) {
                        flagy = true;
                    }
                }
                if (!flagy) {
                    list.add(sfile[i]);
                }
            }
            ob = list.toArray();
        } else {
            ob = sfile;
        }
        for (int i = 0; i < ob.length; i++) {
            System.out.println(sfile[i]);
        }

        for (int z = 0; z < ob.length; z++) {
            System.out.println(ob[z].toString());
            String file = ob[z].toString();
            int in = ob[z].toString().indexOf(".");
            System.out.println(in);
            String out = file.substring(0, in);
            String output = out + ".xls";
            System.out.println(output);


            String start_attr = "<bfw_toc_d_m>";
            String end_attr = "</bfw_toc_d_m>";
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            String s;
            int i, count = 0, record = 0, repeat = 0, k = 0, k1 = 0; //k1 = attribute_counter
            int flag = 0;
            char ch;
            ArrayList al = new ArrayList();
            String ar[] = {"<i>", "</i>", "<b>", "</b>", "<u>", "</u>", "<sub>", "</sub>", "<sup>", "</sup>", "<sc>", "</sc>", "<para>", "</para>", "<br>", "<url href=", "</url>", "<xref", "</xref>"};

            while ((s = br.readLine()) != null) {
                s = s.trim();
                count++;
                if (s.equals(start_attr)) {

                    record = count;
                    break;
                }
            }
            //System.out.println(record);

            while ((s = br.readLine()) != null) {

                s = s.trim();
                count++;
                if (count > record) {

                    if (s.equals(end_attr)) {

                        break;
                    }

                    int index1 = s.indexOf('<');
                    int index2 = s.indexOf('>');

                    if (index1 == -1 && index2 == -1) {
                        count++;
                        continue;
                    } else if (index1 != -1 && index2 != -1) {

                        String compare = s.substring(index1 + 1, index2);

                        for (i = 0; i < ar.length; i++) {
                            if (ar[i].equals("<" + compare + ">")) {
                                flag = 1;
                                System.out.println("flag " + flag + " " + compare);
                                break;
                            }
                        }
                        if (flag == 1) {
                            flag = 0;
                            count++;
                        } else if (flag == 0) {
                            if (s.charAt(index1 + 1) != '/') {
                                int index3 = compare.indexOf("/");
                                if (index3 == compare.length() - 1) {
                                    k1++;
                                    //System.out.println(compare.substring(0, index3) + " " + k1);
                                    al.add(compare.substring(0, index3));

                                } else {
                                    k1++;
                                    //System.out.println(compare + " " + k1);
                                    al.add(compare);

                                }
                            }
                        }


                    }
                }
            }
            /*while((s = br.readLine()) != null){
            int open=s.indexOf("<");
            int quote=s.indexOf("\"");
            int close=s.lastIndexOf(">");
            int close_quote=s.lastIndexOf("\"");
            System.out.println(s);
            System.out.println(open+" "+quote+" "+close_quote+" "+close);

            if(open!=-1 && close !=-1){

            if(open<quote && close_quote<close){

            k1++;
            System.out.println("filename"+" "+k1);
            al.add("filename");
            break;
            }
            }
            } */
            //System.out.println("k1 " + k1);
            fr.close();
//-------------------------------------------------------------------------------------------------------------
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            count = 0;
            record = 0;
            flag = 0;
            int check = 0;
            while ((s = br.readLine()) != null) {
                s = s.trim();
                if (s.length() != 0) {
                    count++;
                    /*	if(repeat==0){
                    int open=s.indexOf("<");
                    int quote=s.indexOf("\"");
                    int close=s.lastIndexOf(">");
                    int close_quote=s.lastIndexOf("\"");

                    if(open!=-1 && close !=-1){

                    if(open<quote && close_quote<close){

                    k++;
                    System.out.println(s.substring(quote+1,close_quote)+" "+k);
                    al.add(s.substring(quote+1,close_quote));
                    }
                    }
                    }*/
                    if (repeat != 0) {
                        repeat++;
                    }

                    if (s.equals(start_attr)) {
                        record = count;
                        repeat = record;
                    }

                    System.out.println(s);

                    if (repeat > record) {

                        if (s.equals(end_attr)) {
                            repeat = 0;

                        } else {

                            int close = s.indexOf('>');
                            int open = s.indexOf('<');
                            int lastopen = s.lastIndexOf('<');
                            int lastclose = s.lastIndexOf('>');
                            int slash = s.indexOf('/');
                            if (open != lastopen) {
                                String last_tag = s.substring(lastopen + 1, lastclose);

                                for (i = 0; i < ar.length; i++) {
                                    if (ar[i].equals("<" + last_tag + ">")) {
                                        flag = 1;
                                        break;
                                    }
                                }
                                if (flag == 0) {
                                    al.add(s.substring(close + 1, lastopen));
                                    k++;
                                    //System.out.println(s.substring(close + 1, lastopen) + " " + k);
                                }
                            }
                            if (open == lastopen && s.length() - 1 == lastclose && slash != -1) {

                                String last_tag = s.substring(lastopen + 1, lastclose);

                                for (i = 0; i < ar.length; i++) {
                                    if (ar[i].equals("<" + last_tag + ">")) {
                                        flag = 1;
                                        break;
                                    }
                                }
                                if (flag == 0) {

                                    al.add("nullvalue");
                                    k++;
                                    //System.out.println("nullvalue" + " " + k);
                                }
                            }
                            if (flag == 1 || s.length() - 1 != lastclose || (slash == -1 && s.length() - 1 == lastclose)) {

                                String value = s.substring(close + 1, s.length());
                                //System.out.println(value);
                                flag = 0;

                                do {
                                    s = br.readLine();
                                    s = s.trim();
                                    //System.out.println(s);
                                    close = s.indexOf('>');
                                    open = s.indexOf('<');
                                    lastopen = s.lastIndexOf('<');
                                    lastclose = s.lastIndexOf('>');
                                    slash = s.indexOf('/');
                                    //System.out.println(open + " " + close + " " + lastopen + " " + lastclose);
                                    if (open == -1 && close == -1) {
                                        value += " " + s;
                                        //System.out.println(value);
                                        flag = 0;
                                        check = 0;
                                        //System.out.println("loop1");
                                    }
                                    if (open != -1 && close != -1) {
                                        for (i = 0; i < ar.length; i++) {
                                            if (ar[i].equals("<" + s.substring(lastopen + 1, lastclose) + ">")) {
                                                //System.out.println("Hi");
                                                flag = 1;
                                                break;
                                            }
                                        }
                                        if (flag == 1) {
                                            value += " " + s;
                                            //System.out.println(value);
                                            //System.out.println(s.substring(lastopen + 1, lastclose));
                                            //System.out.println("loop2");
                                            flag = 0;
                                            check = 0;
                                        } else {
                                            value += " " + s.substring(0, lastopen);
                                            check = 1;
                                            al.add(value);
                                            k++;
                                            //System.out.println(value + " " + k);
                                            //System.out.println("loop3");
                                        }
                                        //System.out.println("check " + check);
                                    }
                                } while (check != 1);
                            }


                        }
                    }
                }

            }

            fr.close();
//------------------------------------------------------------------------------------------------------------
// Writing into excel sheet
            //System.out.println(k);
            //System.out.println(al.size());
            //System.out.println("k1 " + k1);

            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("xml2xsl");

            Object obj[] = al.toArray();
            int col = 0;
            try {
                for (i = 0; i < al.size(); i++) {
                    // Create a row and put some cells in it. Rows are 0 based.
                    HSSFRow row = sheet.createRow((short) col);
                    col++;
                    //System.out.println("column :b" + col);

                    for (int j = 0; j < k1; j++) {
                        //System.out.println(j + " :" + obj[i].toString());
                        if (obj[i].toString().equals("nullvalue")) {
                            row.createCell((short) j).setCellValue("");
                        } else {
                            row.createCell((short) j).setCellValue(obj[i].toString());
                        }
                        i++;
                    }
                    i--;
                }
            } catch (Exception e) {
                System.out.println("Error :" + e);
                e.printStackTrace();
            }
            // Write the output to a file
            FileOutputStream fileOut = new FileOutputStream(output_directory + file_separator + output);
            wb.write(fileOut);
            fileOut.close();

        }
    }
}
