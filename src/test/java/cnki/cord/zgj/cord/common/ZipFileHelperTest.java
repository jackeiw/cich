package cnki.cord.zgj.cord.common;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class ZipFileHelperTest {

    @Test
    public void testZipFile() {
        String zipPath = "D:\\test\\test\\good.zip";
        String errorZipPath = "D:\\test\\test\\error.zip";
        String targetPath = "D:\\test\\test\\demo\\";
        Assert.assertEquals(true, ZipFileHelper.testZipFile(zipPath));
    }

    @Test
    public void containFiles() {
        try{
            String zipPath = "D:\\test\\test\\good-1.zip";
            String suffixName = ".pdf";
            Assert.assertEquals(true, ZipFileHelper.containFiles(zipPath, suffixName));
        }
        catch (Exception ex){

        }
    }
}