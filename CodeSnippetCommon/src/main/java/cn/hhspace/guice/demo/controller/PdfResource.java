package cn.hhspace.guice.demo.controller;

import cn.hhspace.guice.mapbinder.annotations.LazySingleton;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import org.openqa.selenium.By;
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

            driver.navigate().to("");

            // 创建 AShot 对象
            AShot ashot = new AShot();
            ashot.coordsProvider(new WebDriverCoordsProvider());
            ashot.shootingStrategy(ShootingStrategies.viewportPasting(100));

            // 获取页面高度
            int height = driver.findElement(By.tagName("body")).getSize().getHeight();

            // 将页面分为多个视口截图
            BufferedImage image = ashot.takeScreenshot(driver).getImage();
            BufferedImage result = new BufferedImage(
                    image.getWidth(),
                    height,
                    BufferedImage.TYPE_INT_RGB
            );
            result.getGraphics().drawImage(image, 0, 0, null);

            int scroll = 0;
            while (scroll < height) {
                // 向下滚动一定的距离
                ((RemoteWebDriver) driver).executeScript("window.scrollBy(0, 1000)");
                scroll += 1000;

                // 截图
                image = ashot.takeScreenshot(driver).getImage();
                result.getGraphics().drawImage(
                        image,
                        0,
                        scroll - 1000,
                        null
                );
            }

            // 将截图保存到文件
            ImageIO.write(result, "png", new File("screenshot.png"));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭 WebDriver 对象
            driver.quit();
        }


        Document document = new Document(PageSize.A3, 50, 50, 50, 50);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();
        document.add(Image.getInstance("screenshot.png"));
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
