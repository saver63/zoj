
import java.util.ArrayList;
import java.util.List;

/**
 * 无线占用空间(浪费系统内存）
 */
public class Main {
    public static void main(String[] args)  {
        //不能定义对象,JVM会回收
        List<byte[]> bytes = new ArrayList<>();
        while (true){
            bytes.add(new byte[10000]);
        }

    }
}