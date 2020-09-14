package org.sunbird;

import akka.actor.ActorRef;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunbird.actor.core.ActorCache;
import org.sunbird.actor.core.ActorService;
import org.sunbird.helper.CassandraConnectionManager;
import org.sunbird.helper.CassandraConnectionMngrFactory;
import org.sunbird.message.IResponseMessage;
import org.sunbird.message.Localizer;
import org.sunbird.message.ResponseCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * this class is used to instantiate the actor system and open saber.
 * @author Amit Kumar
 */
public class Application {
    private final static String actorSystemName = "certActorSystem";
    private static Application instance = new Application();
    private static Localizer localizer = Localizer.getInstance();
    private static Logger logger = LoggerFactory.getLogger(Application.class);


    // private constructor restricted to this class itself
    private Application() {
    }

    // static method to create instance of ActorService class
    public static Application getInstance() {
        return instance;
    }

    // instantiate actor system and actors
    public void init() throws BaseException {
        List<String> actorClassPaths = new ArrayList<>();
        actorClassPaths.add("org.sunbird");
        ActorService.getInstance().init(actorSystemName, actorClassPaths);
        createCassandraConnection(JsonKeys.SUNBIRD);
    }


    /**
     * this method is used to get the reference of actor from in memory cache.
     * @param operation
     * @return
     */
    public ActorRef getActorRef(String operation) {
        return ActorCache.getActorRef(operation);
    }

    /**
     * This method will read the configuration from System variable.
     *
     * @return boolean
     */
    public static boolean createCassandraConnection(String keyspace) throws BaseException {
        boolean response = false;
        String ips = System.getenv(JsonKeys.SUNBIRD_CASSANDRA_IP);
        String envPort = System.getenv(JsonKeys.SUNBIRD_CASSANDRA_PORT);
        CassandraConnectionManager cassandraConnectionManager =
                CassandraConnectionMngrFactory.getObject(JsonKeys.STANDALONE_MODE);

        if (StringUtils.isBlank(ips) || StringUtils.isBlank(envPort)) {
            logger.info("Configuration value is not coming form System variable.");
            return false;
        }
        String[] portList = envPort.split(",");
        String userName = System.getenv(JsonKeys.SUNBIRD_CASSANDRA_USER_NAME);
        String password = System.getenv(JsonKeys.SUNBIRD_CASSANDRA_PASSWORD);
        try {
            boolean result =
                    cassandraConnectionManager.createConnection(ips, portList[0], userName, password, keyspace);
            if (result) {
                response = true;
                logger.info(
                        "CONNECTION CREATED SUCCESSFULLY FOR IP's: " + ips + " : KEYSPACE :" + keyspace);
            } else {
                logger.info(
                        "CONNECTION CREATION FAILED FOR IP: " + ips + " : KEYSPACE :" + keyspace);
            }
        } catch (BaseException ex) {
            logger.error("Application:createCassandraConnection: Exception occurred with message = " + ex.getMessage());
        }
        if (!response) {
            throw new BaseException(
                    IResponseMessage.INVALID_CONFIGURATION,
                    getLocalizedMessage(IResponseMessage.INVALID_CONFIGURATION,null),
                    ResponseCode.SERVER_ERROR.hashCode());
        }
        return response;
    }

    private static String getLocalizedMessage(String key, Locale locale){
        return localizer.getMessage(key, locale);
    }
}
