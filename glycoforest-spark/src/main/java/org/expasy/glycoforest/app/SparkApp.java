package org.expasy.glycoforest.app;

import org.apache.spark.SparkConf;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class SparkApp {
    protected static void loadProperties(SparkConf conf) {

        try {

            final Properties properties = new Properties();
            final Reader reader = new FileReader("./spark-args.properties");
            properties.load(reader);

            for(Object obj : properties.keySet()){

                String key = obj.toString();
                conf.set(key, properties.getProperty(key));
            }
        } catch (IOException e) {

            throw new IllegalStateException(e);
        }
    }
}
