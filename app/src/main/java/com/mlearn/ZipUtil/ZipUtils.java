package com.mlearn.ZipUtil;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


/**
 * @author: AIRCode
 * @description: 解压ZIP文件
 * @date: 2017-04-11
 * @time: 09:22
 */
public class ZipUtils {
    public static final String TAG = "ZIP";

    public ZipUtils() {
    }

    /**
     * 解压zip到指定的路径
     *
     * @param zipFileString ZIP的名称
     * @param outPathString 要解压缩路径
     * @throws Exception
     */
    public static void UnZipFolder(String zipFileString, String outPathString) throws Exception {
        ZipUtils.DeleteAllFileInFolder(outPathString);
        Log.e(TAG, "UnZipFolder: " + zipFileString);
        ZipFile inZip = new ZipFile(zipFileString);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            inZip = new ZipFile(zipFileString, Charset.forName("GBK"));
        }
        Enumeration<ZipEntry> inZips = (Enumeration<ZipEntry>) inZip.entries();
        ZipEntry zipEntry;
        String szName = "";
        File outPathFloder = new File(outPathString);
        if (!outPathFloder.exists()) {
            outPathFloder.mkdirs();
        }
        while (inZips.hasMoreElements()) {
            zipEntry = inZips.nextElement();
            szName = zipEntry.getName();
            Log.e(TAG, "UnZipFolder: " + szName);
            if (zipEntry.isDirectory()) { //获取部件的文件夹名
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {
                File file = new File(outPathString + File.separator + szName);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                } // 获取文件的输出流
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                BufferedInputStream in = new BufferedInputStream(inZip.getInputStream(zipEntry));
                int len;
                byte[] buffer = new byte[1024]; // 读取（字节）字节到缓冲区
                while ((len = in.read(buffer)) != -1) { // 从缓冲区（0）位置写入（字节）字节
                    out.write(buffer, 0, len);
                    out.flush();
                }
                in.close();
                out.close();
            }
        }
        inZip.close();
    }

    /**
     * 解压zip到本地
     *
     * @param zipFileString ZIP的名称
     * @return outPathString 解压目录
     * @throws Exception
     */
    public static String UnZipFolder(String zipFileString) throws Exception {
        String outPathString = zipFileString.substring(0, zipFileString.lastIndexOf("."));
        ZipFile inZip = new ZipFile(zipFileString);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            inZip = new ZipFile(zipFileString, Charset.forName("GBK"));
        }
        Enumeration<ZipEntry> inZips = (Enumeration<ZipEntry>) inZip.entries();
        ZipEntry zipEntry;
        String szName = "";
        File outPathFloder = new File(outPathString);
        if (!outPathFloder.exists()) {
            outPathFloder.mkdirs();
        }
        while (inZips.hasMoreElements()) {
            zipEntry = inZips.nextElement();
            szName = zipEntry.getName();
            Log.e(TAG, "UnZipFolder: " + szName);
            if (zipEntry.isDirectory()) { //获取部件的文件夹名
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {
                File file = new File(outPathString + File.separator + szName);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                } // 获取文件的输出流
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                BufferedInputStream in = new BufferedInputStream(inZip.getInputStream(zipEntry));
                int len;
                byte[] buffer = new byte[1024]; // 读取（字节）字节到缓冲区
                while ((len = in.read(buffer)) != -1) { // 从缓冲区（0）位置写入（字节）字节
                    out.write(buffer, 0, len);
                    out.flush();
                }
                in.close();
                out.close();
            }
        }
        inZip.close();
        return outPathString;
    }

    /**
     * 压缩文件和文件夹
     *
     * @param srcFileString 要压缩的文件或文件夹
     * @param zipFileString 解压完成的Zip路径
     * @throws Exception
     */
    public static void ZipFolder(String srcFileString, String zipFileString) throws Exception { //创建ZIP
        ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFileString)); //创建文件
        File file = new File(srcFileString); //压缩
        ZipFiles(file.getParent() + File.separator, file.getName(), outZip); //完成和关闭
        outZip.finish();
        outZip.close();
    }

    /**
     * 返回zip的文件输入流
     *
     * @param zipFileString zip的名称
     * @param fileString    ZIP的文件名
     * @return InputStream
     * @throws Exception
     */
    public static InputStream UpZip(String zipFileString, String fileString) throws Exception {
        ZipFile zipFile = new ZipFile(zipFileString);
        ZipEntry zipEntry = zipFile.getEntry(fileString);
        return zipFile.getInputStream(zipEntry);
    }

    /**
     * 压缩文件
     *
     * @param folderString
     * @param fileString
     * @param zipOutputSteam
     * @throws Exception
     */
    private static void ZipFiles(String folderString, String
            fileString, ZipOutputStream zipOutputSteam) throws Exception {
        if (zipOutputSteam == null) return;
        File file = new File(folderString + fileString);
        if (file.isFile()) {
            ZipEntry zipEntry = new ZipEntry(fileString);
            FileInputStream inputStream = new FileInputStream(file);
            zipOutputSteam.putNextEntry(zipEntry);
            int len;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }
            zipOutputSteam.closeEntry();
        } else { //文件夹
            String fileList[] = file.list(); //没有子文件和压缩
            if (fileList.length <= 0) {
                ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
                zipOutputSteam.putNextEntry(zipEntry);
                zipOutputSteam.closeEntry();
            } //子文件和递归
            for (int i = 0; i < fileList.length; i++) {
                ZipFiles(folderString, fileString + File.separator + fileList[i], zipOutputSteam);
            }
        }
    }

    /**
     * 返回ZIP中的文件列表（文件和文件夹）
     *
     * @param zipFileString  ZIP的名称
     * @param bContainFolder 是否包含文件夹
     * @param bContainFile   是否包含文件
     * @return
     * @throws Exception
     */
    public static List<File> GetZipFileList(String zipFileString,
                                            boolean bContainFolder, boolean bContainFile) throws Exception {
        List<File> fileList = new ArrayList<File>();
        ZipFile inZip = new ZipFile(zipFileString);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            inZip = new ZipFile(zipFileString, Charset.forName("GBK"));
        }
        Enumeration<ZipEntry> inZips = (Enumeration<ZipEntry>) inZip.entries();
        ZipEntry zipEntry;
        String szName = "";
        while (inZips.hasMoreElements()) {
            zipEntry = inZips.nextElement();
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(szName);
                if (bContainFolder) {
                    fileList.add(folder);
                }
            } else {
                File file = new File(szName);
                if (bContainFile) {
                    fileList.add(file);
                }
            }
        }
        inZip.close();
        return fileList;
    }

    public static boolean DeleteFolder(String folderPath) {
        try {
            File myFilePath = new File(folderPath);
            if (!myFilePath.exists()) {
                return true;
            }
            DeleteAllFileInFolder(folderPath); //删除完里面所有内容
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean DeleteFile(String filePath) {
        try {
            File myFilePath = new File(filePath);
            Log.e(TAG, "DeleteFile: "+filePath);
            if (!myFilePath.exists()) {
                return true;
            }
            myFilePath.delete(); //删除文件
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean DeleteAllFileInFolder(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                DeleteAllFileInFolder(path + "/" + tempList[i]);//先删除文件夹里面的文件
                DeleteFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }
}