package file;

import java.util.UUID;

public class TestUUID {
    public static void main(String []args) {
        System.out.println(UUID.randomUUID().toString().replace("-", "").toLowerCase());
    }
}
