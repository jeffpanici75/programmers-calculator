package io.nybbles.progcalc.shell;

import io.nybbles.progcalc.shell.contracts.Configuration;
import io.nybbles.progcalc.shell.contracts.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.Properties;

public class DefaultConfiguration implements Configuration {
    private static final Logger s_logger = LoggerFactory.getLogger(DefaultConfiguration.class);
    private Properties _properties = new Properties();
    private Options _options;

    public DefaultConfiguration(Options options) {
        _options = options;
    }

    private String getPropertyOverrideOrDefault(final String name) {
        String value = _options.getProperty(name);
        if (value != null && value.length() > 0)
            return value;
        value = _properties.getProperty(name);
        if (value == null)
            return "";
        return value;
    }

    @Override
    public void load() {
        load(null);
    }

    @Override
    public void load(InputStream inputStream) {
        try {
            if (inputStream == null) {
                URL url = DefaultConfiguration.class
                        .getClassLoader()
                        .getResource(_options.getConfigPath());
                if (url != null) {
                    inputStream = new FileInputStream(url.getPath());
                    _properties.load(inputStream);
                } else {
                    s_logger.error("Unable to find: {}", _options.getConfigPath());
                }
            }
        } catch (Exception e) {
            s_logger.error("Unhandled exception in DefaultConfiguration.load", e);
        }
    }

    @Override
    public void save() {
        save(null);
    }

    @Override
    public void clearProperties() {
        _properties.clear();
    }

    @Override
    public void save(OutputStream outputStream) {
        try {
            if (outputStream == null) {
                URL url = DefaultConfiguration.class
                        .getClassLoader()
                        .getResource(_options.getConfigPath());
                if (url != null) {
                    outputStream = new FileOutputStream(url.getPath());
                }
            }
            if (outputStream != null) {
                _properties.store(outputStream, "Programmer's Calculator Configuration");
                outputStream.flush();
                outputStream.close();
            }
        } catch (Exception e) {
            s_logger.error("Unhandled exception in DefaultConfiguration.save", e);
        }
    }
}
