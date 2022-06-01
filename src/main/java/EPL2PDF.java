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

    public static List<List<WordsVo>> getWordsVo(File file){
        List<String> list = txt2StringArray(file);
        List<List<WordsVo>> result = new ArrayList<List<WordsVo>>();
        List<WordsVo> resultWordsList = new ArrayList<WordsVo>();
        List<String> nn = new ArrayList<String>();
        for (String ss : list) {
            if("N".equals(ss)){
                nn.add("N");
            }
            //出现一对N为一个面单
            if(nn.size() == 2){
                result.add(resultWordsList);
                nn = new ArrayList<String>();
                resultWordsList = new ArrayList<WordsVo>();
                continue;
            }
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
                resultWordsList.add(wordsVo);
            }else if(s[0].startsWith("LO")){
                wordsVo.setType("LO");
                wordsVo.setX(Integer.parseInt(s[0].substring(2)));
                wordsVo.setY(Integer.parseInt(s[1]));
                wordsVo.setWide(Integer.parseInt(s[2]));
                wordsVo.setHeight(Integer.parseInt(s[3]));
                resultWordsList.add(wordsVo);
            }else if(s[0].startsWith("B")){
                wordsVo.setType("B");
                wordsVo.setX(Integer.parseInt(s[0].substring(2)));
                wordsVo.setY(Integer.parseInt(s[1]));
                wordsVo.setWide(Integer.parseInt(s[2]));
                wordsVo.setHeight(Integer.parseInt(s[3]));
                wordsVo.setData(s[8].replaceAll("\"",""));
                resultWordsList.add(wordsVo);
            }
        }
        return result;
    }

    public static void EPL2PDF(String path){
        File file = new File(path);
        List<List<WordsVo>> list =  getWordsVo(file);
        try {
            //添加文字 xy坐标位置
            for (List<WordsVo> listWords : list) {
                /**
                 * 得到图片缓冲区
                 * INT精确度达到一定,RGB三原色，高度280,宽度360
                 */
                BufferedImage bi = new BufferedImage(822, 822, BufferedImage.TYPE_BYTE_GRAY);

                //得到它的绘制环境(这张图片的笔)
                Graphics2D g2 = (Graphics2D) bi.getGraphics();

                //填充一个矩形 左上角坐标(0,0),宽70,高150;填充整张图片
                g2.fillRect(0, 0, 822, 822);
                //设置颜色
                g2.setColor(Color.white);
                //填充整张图片(其实就是设置背景颜色)
                g2.fillRect(0, 0, 360, 280);
                //设置背景颜色
                g2.setColor(Color.black);
                String trackingNo = "";
                for (WordsVo wordsVo : listWords) {
                    System.out.println(wordsVo);
                    if ("A".equals(wordsVo.getType())) {
                        //判断字号
                        if (wordsVo.getFontSize() == 1) {
                            //设置字体:字体、字号、大小
                            g2.setFont(new Font("微软雅黑", Font.PLAIN, 15));
                        } else if (wordsVo.getFontSize() == 4) {
                            //设置字体:字体、字号、大小
                            g2.setFont(new Font("微软雅黑", Font.BOLD, 17 * wordsVo.getHeight()));
                        } else {
                            //设置字体:字体、字号、大小
                            g2.setFont(new Font("微软雅黑", Font.PLAIN, 15));
                        }
                        //画文字
                        if (wordsVo.getRotation() == 0) {
                            if (wordsVo.getData().startsWith(" ") && !wordsVo.getData().contains("NEXT")) {
                                g2.drawString(wordsVo.getData(), wordsVo.getX() + 100, wordsVo.getY() + wordsVo.getHeight() * 16);
                            } else {
                                g2.drawString(wordsVo.getData(), wordsVo.getX() + 5, wordsVo.getY() + wordsVo.getHeight() * 16);
                            }
                        } else if (wordsVo.getRotation() == 1) {
                            g2.rotate((Math.PI / 2), wordsVo.getX(), wordsVo.getY());//旋转
                            g2.drawString(wordsVo.getData(), wordsVo.getX(), wordsVo.getY() + 10);
                            g2.rotate(-(Math.PI / 2), wordsVo.getX(), wordsVo.getY());//旋转
                        }
                        //画线
                    } else if ("LO".equals(wordsVo.getType())) {
                        //画边框
                        g2.drawRect(wordsVo.getX(), wordsVo.getY(), wordsVo.getWide(), wordsVo.getHeight());
                    } else if ("B".equals(wordsVo.getType())) {
                        //画条码
                        BufferedImage image = GoogleBarCodeUtils.
                                getBarCode(wordsVo.getData());
                        //                    ImageIO.write(image, "jpg", new File("d:\\barcode.jpg"));
                        g2.drawImage(image, wordsVo.getX(), wordsVo.getY(), null);
                        trackingNo = wordsVo.getData();
                    }
                }
                //保存图片 JPEG表示保存格式
                ImageIO.write(bi, "JPEG", new FileOutputStream("d:\\data\\" + trackingNo + ".jpg"));

                System.out.println("成功！");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        EPL2PDF.EPL2PDF("D:\\data\\test.epl");

    }
}
