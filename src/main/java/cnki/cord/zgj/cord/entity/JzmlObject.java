/**
 * Copyright (C), 1998-2019, 同方知网（北京）技术有限公司
 * FileName: JzmlObject
 * Author:   Vincent
 * Date:     2019/10/5 22:42
 * Description: 卷宗目录实体类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cnki.cord.zgj.cord.entity;

/**
 * 〈一句话功能简述〉<br> 
 * 〈卷宗目录实体类〉
 *
 * @author Vincent
 * @create 2019/10/5
 * @since 1.0.0
 */
public class JzmlObject {
    public String jzbh; //"37000000012274",   #卷宗编号
    public String mlbh; //"dd84939fefbc443a8320f33e492ce78a",  #目录编号
    public int mllx; //1,  #目录类型
    public String fmlbh; //"", #父目录编号
    public String mlxsmc; //"文书卷", #目录显示名称
    public String mlxx; //"", #目录详细信息
    public int mlsxh; //1 #目录顺序号
}