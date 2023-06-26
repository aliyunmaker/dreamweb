package cc.landingzone.dreamweb.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 作者：珈贺
 * 描述：
 */
public class FileUtil {
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static void main(String[] args) {
//        fileToString("/terraform/oss.tf");
        System.out.println(fileToString("src/main/resources/terraform/oss.tf"));
    }


    public static String fileToString(String filePath) {

        try{
            Path path = Paths.get(filePath);
            Stream<String> lines = Files.lines(path);
            String content = lines.collect(Collectors.joining(System.lineSeparator()));
            lines.close();
            return content;
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

}
