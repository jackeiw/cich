/**
 * Copyright (C), 1998-2019, 同方知网（北京）技术有限公司
 * FileName: JzwjObject
 * Author:   Vincent
 * Date:     2019/10/5 22:47
 * Description: 卷宗文件实体类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cnki.cord.zgj.cord.entity;

/**
 * 〈一句话功能简述〉<br> 
 * 〈卷宗文件实体类〉
 *
 * @author Vincent
 * @create 2019/10/5
 * @since 1.0.0
 */
public class JzwjObject {
    public String jzbh; //"37000000012274",   #卷宗编号
    public String mlbh; //"1abd8438dd1e40dc982e67d86687b679",  #目录编号
    public String wjxh; //"465dfc057bf1446d8bc9cc2ef9f20c3a",  #文件序号
    public String wjmc; //"fdiuewhfjj4wru43fijdsjfw4eu8rjew",  #文件名称
    public String wjlj; //"\100001\0301\汉东省院起诉受[2018]10000100001号", #文件路径
    public String wjxsmc; //"第 1 页", #文件显示名称
    public String wjhz; //".pdf ",#文件后缀
    public int wjksy; //0, #文件开始页
    public int wjjsy; //0, #文件结束页
    public int wjsxh; //0  #文件顺序号
}