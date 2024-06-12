
import java.security.Permission;

public class MySecurityManager extends SecurityManager {


    //默认开所有权限
    @Override
    public void checkPermission(Permission perm) {
//        //默认禁用所有权限
//        super.checkPermission(perm);
    }

    //检测程序是否可执行文件
    @Override
    public void checkExec(String cmd) {
        throw new SecurityException("checkExec 权限异常" + cmd);
    }

    //检测程序是否可读文件
    @Override
    public void checkRead(String file) {
        //放行对象
        if (file.contains("hutool")){
            return;
        }
        throw new SecurityException("checkRead 权限异常" + file);
    }

    //检测程序是否允许写文件
    @Override
    public void checkWrite(String file) {
//        throw new SecurityException("checkWrite 权限异常" + file);
    }

    //检测程序是否允许删除文件
    @Override
    public void checkDelete(String file) {
//        throw new SecurityException("checkDelete 权限异常" + file);
    }

    //检测程序是否允许打开网络连接
    @Override
    public void checkConnect(String host, int port) {
//        throw new SecurityException("checkConnect 权限异常" + host+":"+port);
    }
}
