package io.nybbles.progcalc.shell;

import io.nybbles.progcalc.shell.contracts.Options;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class DefaultOptions implements Options {
    private static final Logger s_logger = LoggerFactory.getLogger(DefaultOptions.class);
    private Properties _properties = new Properties();
    private String _configPath;

    public DefaultOptions() {
        resetToDefaults();
    }

    @Override
    public boolean parse(String[] args) {
        var configFileOption = Option.builder("c")
                .argName("path")
                .hasArg()
                .required(false)
                .desc("The configuration file location")
                .longOpt("config")
                .build();

        var paramOption = Option.builder("D")
                .required(false)
                .desc("Specify a config override in key=value form")
                .argName("property=value")
                .numberOfArgs(2)
                .valueSeparator()
                .build();

        var options = new org.apache.commons.cli.Options();
        options.addOption(configFileOption);
        options.addOption(paramOption);

        var parser = new DefaultParser();
        try {
            var commandLine = parser.parse(options, args);

            _properties = commandLine.getOptionProperties("D");

            if (commandLine.hasOption("c"))
                _configPath = commandLine.getOptionValue('c');

            return true;
        } catch (ParseException e) {
            s_logger.error("Unable to parse command line arguments: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String getConfigPath() {
        return _configPath;
    }

    @Override
    public void resetToDefaults() {
        _properties.clear();
        _configPath = "shell.properties";
    }

    @Override
    public void removeProperty(String name) {
        _properties.remove(name);
    }

    @Override
    public String getProperty(String name) {
        return _properties.getProperty(name);
    }
}
