package cn.hhspace.guice.demo.controller;

import cn.hhspace.guice.mapbinder.annotations.LazySingleton;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2023/4/13 10:50
 * @Descriptions:
 */
@LazySingleton
@Path("/pdf")
public class PdfResource {
    @GET
    @Path("/itext")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response doGetPdfByItext() throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();
        document.add(new Paragraph("Hello, World!"));
        document.close();

        // 将生成的 PDF 文件读入输入流
        InputStream inputStream = new ByteArrayInputStream(out.toByteArray());

        // 创建 ResponseBuilder 对象
        Response.ResponseBuilder responseBuilder = Response.ok(inputStream);
        responseBuilder.header("Content-Disposition", "attachment; filename=output.pdf");

        // 构建 Response 并返回
        return responseBuilder.build();
    }

    @GET
    @Path("/chrome")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response doGetPdfByChrome() throws DocumentException, IOException {
        System.setProperty("webdriver.chrome.driver", getClass().getResource("/chromedriver").getPath());

        // 创建 ChromeOptions 对象，设置启动参数
        ChromeOptions options = new ChromeOptions();
        // 无头模式
        options.addArguments("--headless");
        // 禁用 GPU 硬件加速
        options.addArguments("--disable-gpu");
        // 禁用沙盒模式
        options.addArguments("--no-sandbox");
        // 禁用临时文件存储
        options.addArguments("--disable-dev-shm-usage");
        // 设置窗口大小
        options.addArguments("--window-size=1920,1080");

        // 创建 WebDriver 对象，启动 Chrome 浏览器
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get("https://www.baidu.com");

            Map<String, Object> prefs = new HashMap<>();
            prefs.put("plugins.plugins_disabled", new String[] {"Chrome PDF Viewer"});
            prefs.put("plugins.always_open_pdf_externally", true);

            ChromeOptions pdfOptions = new ChromeOptions();
            pdfOptions.setExperimentalOption("prefs", prefs);
            pdfOptions.addArguments("--headless");
            pdfOptions.addArguments("--disable-gpu");
            pdfOptions.addArguments("--no-sandbox");
            pdfOptions.addArguments("--print-to-pdf");

            driver.navigate().to("");
            driver.findElement(By.cssSelector("body"))
                    .sendKeys(Keys.CONTROL, Keys.PAUSE);

            Thread.sleep(5000);

            try (OutputStream outputStream = new FileOutputStream("output.png")) {
                byte[] pdfBytes = driver.findElement(By.tagName("body")).getScreenshotAs(OutputType.BYTES);
                outputStream.write(pdfBytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭 WebDriver 对象
            driver.quit();
        }

        Rectangle pageSize = new Rectangle(2020, 1180);

        Document document = new Document(pageSize, 50, 50, 50, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();
        document.add(Image.getInstance("output.png"));
        document.close();

        // 将生成的 PDF 文件读入输入流
        InputStream inputStream = new ByteArrayInputStream(out.toByteArray());

        // 创建 ResponseBuilder 对象
        Response.ResponseBuilder responseBuilder = Response.ok(inputStream);
        responseBuilder.header("Content-Disposition", "attachment; filename=output.pdf");

        // 构建 Response 并返回
        return responseBuilder.build();
    }

    @GET
    @Path("/scroll")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response doGetPdfByChromeScroll() throws DocumentException, IOException {
        System.setProperty("webdriver.chrome.driver", getClass().getResource("/chromedriver").getPath());

        ChromeOptions options = new ChromeOptions();
        //ssl证书支持
        options.setCapability("acceptSslCerts", true);
        //截屏支持
        options.setCapability("takesScreenshot", true);
        //css搜索支持
        options.setCapability("cssSelectorsEnabled", true);
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-dev-shm-usage");
        options.setHeadless(true);

        ChromeDriver driver = new ChromeDriver(options);
        //设置超时，避免有些内容加载过慢导致截不到图
        driver.manage().timeouts().pageLoadTimeout(1, TimeUnit.MINUTES);
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.MINUTES);
        driver.manage().timeouts().setScriptTimeout(1, TimeUnit.MINUTES);

        Long width = 2500L;
        Long height = 3500L;
        try {
            //设置需要访问的地址
            driver.get("");
            Thread.sleep(5000);

            //设置窗口宽高，设置后才能截全
            driver.manage().window().setSize(new Dimension(width.intValue(), height.intValue()));
            //这里需要模拟滑动，有些是滑动的时候才加在的
            long temp_height = 0;
            while (true) {
                //每次滚动500个像素，因为懒加载所以每次等待2S 具体时间可以根据具体业务场景去设置
                Thread.sleep(2000);
                driver.executeScript("window.scrollBy(0,500)");
                temp_height += 1000;
                if(temp_height>=height){
                    break;
                }
            }

            //设置截图文件保存的路径
            String screenshotPath = "output.png";
            File srcFile = driver.getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(srcFile, new File(screenshotPath));
        } catch (Exception e){
            throw new RuntimeException("截图失败",e);
        } finally {
            driver.quit();
        }

        Document document = new Document(new Rectangle(width.intValue()*2, height.intValue()), 0, 0, 0, 0);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();
        document.add(Image.getInstance("output.png"));
        document.close();

        // 将生成的 PDF 文件读入输入流
        InputStream inputStream = new ByteArrayInputStream(out.toByteArray());

        // 创建 ResponseBuilder 对象
        Response.ResponseBuilder responseBuilder = Response.ok(inputStream);
        responseBuilder.header("Content-Disposition", "attachment; filename=output.pdf");

        // 构建 Response 并返回
        return responseBuilder.build();
    }


}
