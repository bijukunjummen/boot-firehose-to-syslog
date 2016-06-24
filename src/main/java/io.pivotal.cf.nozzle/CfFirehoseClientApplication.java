package io.pivotal.cf.nozzle;

import io.pivotal.cf.nozzle.service.FirehoseToSyslogConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class CfFirehoseClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(CfFirehoseClientApplication.class, args);
	}

}

@Component
class FirehoseObserverTrigger implements CommandLineRunner {

	@Autowired
	private FirehoseToSyslogConnector firehoseToSyslogConnector;

	@Override
	public void run(String... args) throws Exception {
		firehoseToSyslogConnector.connect();
	}
}
