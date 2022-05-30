import sun.java2d.opengl.WGLSurfaceData;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class EPL2PDF {

    /**
     * 读取txt文件的内容
     * @param file 想要读取的文件对象
     * @return 返回文件内容
     */
    public static List<String> txt2StringArray(File file){
        List<String> list = new ArrayList<String>();
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                list.add(s);
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }

    /**
     *
     * @param file 想要读取的文件对象
     * @return 返回文件内容vo
     */

    public static List<WordsVo> getWordsVo(File file){
        List<String> list = txt2StringArray(file);
        List<WordsVo> result = new ArrayList<WordsVo>();
        for (String ss : list) {
            WordsVo wordsVo = new WordsVo();
            String[] s = ss.split(",");
            if (s[0].startsWith("A")){
                wordsVo.setType("A");
                wordsVo.setX(Integer.parseInt(s[0].substring(1)));
                wordsVo.setY(Integer.parseInt(s[1]));
                wordsVo.setRotation(Integer.parseInt(s[2]));
                wordsVo.setFontSize(Integer.parseInt(s[3]));
                wordsVo.setWide(Integer.parseInt(s[4]));
                wordsVo.setHeight(Integer.parseInt(s[5]));
                wordsVo.setCode(s[6]);
                wordsVo.setData(s[7].replaceAll("\"",""));
                result.add(wordsVo);
            }else if(s[0].startsWith("LO")){
                wordsVo.setType("LO");
                wordsVo.setX(Integer.parseInt(s[0].substring(2)));
                wordsVo.setY(Integer.parseInt(s[1]));
                wordsVo.setWide(Integer.parseInt(s[2]));
                wordsVo.setHeight(Integer.parseInt(s[3]));
                result.add(wordsVo);
            }else if(s[0].startsWith("B")){
                wordsVo.setType("B");
                wordsVo.setX(Integer.parseInt(s[0].substring(2)));
                wordsVo.setY(Integer.parseInt(s[1]));
                wordsVo.setWide(Integer.parseInt(s[2]));
                wordsVo.setHeight(Integer.parseInt(s[3]));
                wordsVo.setData(s[8].replaceAll("\"",""));
                result.add(wordsVo);
            }
        }
        return result;
    }

    public static void main(String[] args){
        File file = new File("D:\\phpstudy_pro\\WWW\\Shipment-Label-1-51937.epl");
        List<WordsVo> list =  getWordsVo(file);
        try {
            /**
             * 得到图片缓冲区
             * INT精确度达到一定,RGB三原色，高度280,宽度360
             */
            BufferedImage bi = new BufferedImage(822, 820, BufferedImage.TYPE_BYTE_GRAY);

            //得到它的绘制环境(这张图片的笔)
            Graphics2D g2 = (Graphics2D) bi.getGraphics();

            //填充一个矩形 左上角坐标(0,0),宽70,高150;填充整张图片
            g2.fillRect(0, 0, 822, 820);
            //设置颜色
            g2.setColor(Color.white);
            //填充整张图片(其实就是设置背景颜色)
            g2.fillRect(0, 0, 360, 280);
            //设置背景颜色
            g2.setColor(Color.black);
            //添加文字 xy坐标位置
            for (WordsVo wordsVo : list) {
                System.out.println(wordsVo);
                //画文字
                if("A".equals(wordsVo.getType())){
                    if(wordsVo.getFontSize() == 1){
                        //设置字体:字体、字号、大小
                        g2.setFont(new Font("微软雅黑", Font.BOLD, 13));
                    }else if(wordsVo.getFontSize() == 4){
                        //设置字体:字体、字号、大小
                        g2.setFont(new Font("微软雅黑", Font.BOLD, 5 * wordsVo.getFontSize() * wordsVo.getWide()));
                    }
                    if(wordsVo.getRotation() == 0) {
                        g2.drawString(wordsVo.getData(), wordsVo.getX() + 3, wordsVo.getY() + wordsVo.getHeight() * 20);
                    }else if(wordsVo.getRotation() == 1){
                        g2.rotate((Math.PI/2),wordsVo.getX(), wordsVo.getY());//旋转
                        g2.drawString(wordsVo.getData(), wordsVo.getX() , wordsVo.getY() + 12);
                        g2.rotate(-(Math.PI/2),wordsVo.getX(), wordsVo.getY());//旋转
                    }
                    //画线
                }else if("LO".equals(wordsVo.getType())){
                    //画边框
                    g2.drawRect(wordsVo.getX(), wordsVo.getY(), wordsVo.getWide(), wordsVo.getHeight());
                }else if("B".equals(wordsVo.getType())){
                    //画条码
                    BufferedImage image = GoogleBarCodeUtils.insertWords(GoogleBarCodeUtils.
                            getBarCode(wordsVo.getData()), wordsVo.getData().replaceAll("\\%",""));
//                    ImageIO.write(image, "jpg", new File("d:\\barcode.jpg"));
                    g2.drawImage(image, wordsVo.getX(), wordsVo.getY(), null);
                }
            }
            //保存图片 JPEG表示保存格式
            ImageIO.write(bi, "JPEG", new FileOutputStream("d:\\b.jpg"));

            System.out.println("成功！");
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
