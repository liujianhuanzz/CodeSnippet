package cn.hhspace.guice.modules;

import com.google.inject.Binder;
import com.google.inject.Module;

import java.io.*;
import java.util.List;
import java.util.Properties;

public class PropertiesModule implements Module {

    private final List<String> propertiesFiles;

    public PropertiesModule(List<String> propertiesFiles) {
        this.propertiesFiles = propertiesFiles;
    }

    @Override
    public void configure(Binder binder) {

        Properties props = new Properties();

        Properties systemProps = System.getProperties();
        props.putAll(systemProps);

        for (String propertiesFile: propertiesFiles) {
            try (InputStream stream = openPropertiesFile(propertiesFile, systemProps)) {
                if (stream != null) {
                    try (InputStreamReader in = new InputStreamReader(stream)) {
                        props.load(in);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        binder.bind(Properties.class).toInstance(props);
    }

    private static InputStream openPropertiesFile(String propertiesFile, Properties systemProps) throws FileNotFoundException {

        InputStream stream = ClassLoader.getSystemResourceAsStream(propertiesFile);

        if (stream != null) {
            return stream;
        } else {
            File file = new File(systemProps.getProperty("properties.file", propertiesFile));
            if (file.exists()) {
                return new BufferedInputStream(new FileInputStream(file));
            } else {
                return null;
            }
        }
    }
}
