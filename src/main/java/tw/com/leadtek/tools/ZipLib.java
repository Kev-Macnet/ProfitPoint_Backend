package tw.com.leadtek.tools;

import java.io.FileNotFoundException;

public class ZipLib {
    
 // using: zipFiles("c:/temp/123.zip", List<String>);
    public static int zipFiles(String fileName, java.util.List<String> srcFiles) {
        int ret = 0;
//        List<String> srcFiles = Arrays.asList("test1.txt", "test2.txt");
        try {
            java.io.FileOutputStream fos = new java.io.FileOutputStream(fileName);
            java.util.zip.ZipOutputStream zipOut = new java.util.zip.ZipOutputStream(fos);
            for (String srcFile : srcFiles) {
                java.io.File fileToZip = new java.io.File(srcFile);
                java.io.FileInputStream fis = new java.io.FileInputStream(fileToZip);
                java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(fileToZip.getName());
                zipOut.putNextEntry(zipEntry);
    
                byte[] bytes = new byte[4096];
                int length;
                while((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                fis.close();
                ret++;
            }
            zipOut.close();
            fos.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } 
        catch(java.io.IOException e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    //===
    // using: zipDirectory("c:/temp/123.zip", "c:/temp/files");
    public static int zipDirectory(String fileName, String sourcePath) {
        int ret = 0;
        try {
            java.io.FileOutputStream fos = new java.io.FileOutputStream(fileName);
            java.util.zip.ZipOutputStream zipOut = new java.util.zip.ZipOutputStream(fos);
            java.io.File fileToZip = new java.io.File(sourcePath);

            ret = zipFileEntry(fileToZip, fileToZip.getName(), zipOut);
            zipOut.close();
            fos.close();
        } catch(java.io.IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    //-----Unzip an Archive
    // using: unzipFile("c:/temp/files", "c:/temp/123.zip");
    public static java.util.List<String> unzipFile(String zipName, String destPath) {
        java.util.List<String> retList = new java.util.ArrayList<String>();
        try {
            java.io.File destDir = new java.io.File(destPath);
            byte[] buffer = new byte[4096];
            java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(new java.io.FileInputStream(zipName));
            java.util.zip.ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                if (!zipEntry.getName().endsWith("/")) {
                    retList.add(zipEntry.getName());
                }
                java.io.File newFile = newUnzipEntry(destDir, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new java.io.IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // fix for Windows-created archives
                    java.io.File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new java.io.IOException("Failed to create directory " + parent);
                    }
                    // write file content
                    java.io.FileOutputStream fos = new java.io.FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
            zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        } catch(java.io.IOException e) {
            e.printStackTrace();
        }
        return retList;
    }
    
    //===
    private static java.io.File newUnzipEntry(java.io.File destinationDir, java.util.zip.ZipEntry zipEntry) throws java.io.IOException {
        java.io.File destFile = new java.io.File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + java.io.File.separator)) {
            throw new java.io.IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }
    
    //===
    private static int zipFileEntry(java.io.File fileToZip, String fileName, java.util.zip.ZipOutputStream zipOut) throws java.io.IOException {
        int ret = 0;
        if (fileToZip.isHidden()) {
            return ret;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new java.util.zip.ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new java.util.zip.ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            java.io.File[] children = fileToZip.listFiles();
            for (java.io.File childFile : children) {
                ret += zipFileEntry(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return ret;
        }
        java.io.FileInputStream fis = new java.io.FileInputStream(fileToZip);
        java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[4096];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
        ret++;
        return ret;
    }

}
