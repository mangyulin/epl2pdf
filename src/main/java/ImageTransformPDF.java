import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 使用时，需要修改 ImageTransformPDF()函数中的输入图片和输出pdf
 */
public class ImageTransformPDF {
    public static String ImageTransformPDF(){
        new ImageTransformPDF().imgOfPdf("输出pdf的路径和文件名（带后缀）", "需要转化成pdf的文件的路径");
        return "200!ok";
    }
    //为终极函数做铺垫
    public static File Pdf(ArrayList<String> imageUrllist, String mOutputPdfFileName) {
        Document doc = new Document(PageSize.A4, 0, 0, 0, 0); //new一个pdf文档
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(mOutputPdfFileName)); //pdf写入
            doc.open();//打开文档
            for (int i = 0; i < imageUrllist.size(); i++) {  //循环图片List，将图片加入到pdf中
                doc.newPage();  //在pdf创建一页
                Image png1 = Image.getInstance(imageUrllist.get(i)); //通过文件路径获取image
                float heigth = png1.getHeight();
                float width = png1.getWidth();
                int percent = getPercent2(heigth, width);
                png1.setAlignment(Image.MIDDLE);
                png1.scalePercent(percent + 3);// 表示是原来图像的比例;
                doc.add(png1);
            }
            doc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File mOutputPdfFile = new File(mOutputPdfFileName);  //输出流
        if (!mOutputPdfFile.exists()) {
            mOutputPdfFile.deleteOnExit();
            return null;
        }
        return mOutputPdfFile; //反回文件输出流
    }

    public static int getPercent(float h, float w) {
        int p = 0;
        float p2 = 0.0f;
        if (h > w) {
            p2 = 297 / h * 100;
        } else {
            p2 = 210 / w * 100;
        }
        p = Math.round(p2);
        return p;
    }

    public static int getPercent2(float h, float w) {
        int p = 0;
        float p2 = 0.0f;
        p2 = 530 / w * 100;
        p = Math.round(p2);
        return p;
    }

    /**
     * @Description: 通过图片路径及生成pdf路径，将图片转成pdf
     * @Author: zd
     * @Date: 2019/9/29
     */
    public static void imgOfPdf(String filepath, String imgUrl) {
        try {
            ArrayList<String> imageUrllist = new ArrayList<String>(); //图片list集合
            String[] imgUrls = imgUrl.split(",");
            for (int i=0; i<imgUrls.length; i++) {
                imageUrllist.add(imgUrls[i]);
            }
            String pdfUrl =  filepath;  //输出pdf文件路径
            File file = Pdf(imageUrllist, pdfUrl);//生成pdf
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ImageTransformPDF.imgOfPdf("d:\\data\\aaa.pdf","d:\\data\\b.jpg");
    }
}

