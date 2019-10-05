/**
 * Copyright (C), 1998-2019, 同方知网（北京）技术有限公司
 * FileName: DbHelper
 * Author:   Vincent
 * Date:     2019/10/5 18:25
 * Description: 数据库操作类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cnki.cord.zgj.cord.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 〈一句话功能简述〉<br> 
 * 〈数据库操作类〉
 *
 * @author Vincent
 * @create 2019/10/5
 * @since 1.0.0
 */
public class DbHelper {
    private static String Drivde = "org.sqlite.JDBC";
    private static String dbURL = "jdbc:sqlite:db/wsdb.db ";
    public static void connectDB(){
        try{
            Class.forName(Drivde);// 加载驱动,连接sqlite的jdbc
            Connection connection= DriverManager.getConnection(dbURL);//连接数据库wsdb.db,不存在则创建
            Statement statement=connection.createStatement();   //创建连接对象，是Java的一个操作数据库的重要接口
            String sql="create table tables(name varchar(20),pwd varchar(20))";
            statement.executeUpdate("drop table if exists tables");//判断是否有表tables的存在。有则删除
            statement.executeUpdate(sql);            //创建数据库
            statement.executeUpdate("insert into tables values('zhou','156546')");//向数据库中插入数据
            ResultSet rSet=statement.executeQuery("select * from tables");//搜索数据库，将搜索的放入数据集ResultSet中
            while (rSet.next()) {            //遍历这个数据集
                System.out.println("姓名："+rSet.getString(1));//依次输出 也可以这样写 rSet.getString(“name”)
                System.out.println("密码："+rSet.getString("pwd"));
            }
            rSet.close();//关闭数据集
            connection.close();//关闭数据库连接
        }
        catch(Exception e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void testOther() {
        try {
            Class.forName(Drivde);
            Connection conn = DriverManager.getConnection(dbURL);
            Statement stmt = conn.createStatement();

            stmt.executeUpdate("DROP TABLE IF EXISTS person");
            stmt.executeUpdate("CREATE TABLE person(id INTEGER, name STRING)");
            stmt.executeUpdate("INSERT INTO person VALUES(1, 'john')");
            stmt.executeUpdate("INSERT INTO person VALUES(2, 'david')");
            stmt.executeUpdate("INSERT INTO person VALUES(3, 'henry')");
            ResultSet rs = stmt.executeQuery("SELECT * FROM person");
            while (rs.next()) {
                System.out.println("id=>" + rs.getInt("id") + ", name=>" + rs.getString("name"));
            }
            stmt.close();
            conn.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void testData(String sql){
        try {
            Class.forName(Drivde);
            Connection conn = DriverManager.getConnection(dbURL);
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM person");
            while (rs.next()) {
                System.out.println("id=>" + rs.getInt("id") + ", name=>" + rs.getString("name"));
            }
            stmt.close();
            conn.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static ResultSet getData(String sql){
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName(Drivde);
            conn = DriverManager.getConnection(dbURL);
            //conn.setAutoCommit(false);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            /*while (rs.next()) {
                System.out.println("id=>" + rs.getInt("id") + ", name=>" + rs.getString("name"));
            }*/
            //rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {

        }
        return rs;
    }


    public static int excuteSQL(String sql){
        Connection conn = null;
        Statement stmt = null;
        int iRet = -1;
        try {
            Class.forName(Drivde);
            conn = DriverManager.getConnection(dbURL);
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            iRet = stmt.executeUpdate(sql);
            conn.commit();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {

        }
        return iRet;
    }
}