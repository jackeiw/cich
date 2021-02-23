package cnki.cord.zgj.cord;

import com.tyyw.lcba.messagesystem.api.config.EnableMQ;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
// TODO: 正式发布需要打开这里
@EnableMQ
public class MqasApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqasApplication.class, args);
    }

}
