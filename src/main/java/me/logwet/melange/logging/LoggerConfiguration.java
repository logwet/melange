// package me.logwet.melange.logging;
//
// import java.io.File;
// import me.logwet.melange.config.Config;
// import org.apache.logging.log4j.Level;
// import org.apache.logging.log4j.core.Filter.Result;
// import org.apache.logging.log4j.core.appender.ConsoleAppender.Target;
// import org.apache.logging.log4j.core.config.Configuration;
// import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
// import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
// import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
// import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
// import org.apache.logging.log4j.core.config.builder.api.FilterComponentBuilder;
// import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
// import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
// import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
//
// public class LoggerConfiguration {
//    @SuppressWarnings({"rawtypes", "unchecked"})
//    public static Configuration buildConfiguration() {
//        ConfigurationBuilder<BuiltConfiguration> builder =
//                ConfigurationBuilderFactory.newConfigurationBuilder();
//
//        LayoutComponentBuilder standardLayout = builder.newLayout("PatternLayout");
//        standardLayout.addAttribute(
//                "pattern", "%d{yyyy-MM-dd HH:mm:ss.SSS} %level [%t] [%l] - %msg%n%throwable");
//
//        FilterComponentBuilder infoLevelFilter =
//                builder.newFilter("ThresholdFilter", Result.ACCEPT, Result.NEUTRAL);
//        infoLevelFilter.addAttribute("level", Level.INFO);
//
//        FilterComponentBuilder allLevelFilter =
//                builder.newFilter("ThresholdFilter", Result.ACCEPT, Result.NEUTRAL);
//        allLevelFilter.addAttribute("level", Level.ALL);
//
//        AppenderComponentBuilder stdout = builder.newAppender("stdout", "Console");
//        stdout.addAttribute("target", Target.SYSTEM_OUT);
//        stdout.add(standardLayout);
//        stdout.add(infoLevelFilter);
//
//        builder.add(stdout);
//
//        ComponentBuilder timeBasedTriggeringPolicy =
//                builder.newComponent("TimeBasedTriggeringPolicy");
//        timeBasedTriggeringPolicy.addAttribute("interval", 1);
//
//        ComponentBuilder sizeBasedTriggeringPolicy =
//                builder.newComponent("SizeBasedTriggeringPolicy");
//        sizeBasedTriggeringPolicy.addAttribute("size", "100MB");
//
//        ComponentBuilder triggeringPolicy = builder.newComponent("Policies");
//        triggeringPolicy.addComponent(timeBasedTriggeringPolicy);
//        triggeringPolicy.addComponent(sizeBasedTriggeringPolicy);
//
//        AppenderComponentBuilder rolling = builder.newAppender("rolling", "RollingFile");
//        rolling.addAttribute("fileName", new File(Config.LOG_DIR,
// "melange.log").getAbsolutePath());
//        rolling.addAttribute(
//                "filePattern",
//                new File(Config.LOG_DIR, "melange").getAbsolutePath() + "-%d{yy-MM-dd}.log.gz");
//        rolling.addComponent(triggeringPolicy);
//        rolling.add(standardLayout);
//        rolling.add(allLevelFilter);
//
//        builder.add(rolling);
//
//        RootLoggerComponentBuilder rootLoggerComponentBuilder = builder.newRootLogger(Level.INFO);
//        rootLoggerComponentBuilder.add(builder.newAppenderRef("stdout"));
//        rootLoggerComponentBuilder.add(builder.newAppenderRef("rolling"));
//
//        builder.add(rootLoggerComponentBuilder);
//
//        return builder.build();
//    }
// }
