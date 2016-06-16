package io.pivotal;


import io.pivotal.cf.nozzle.CfFirehoseClientApplication;
import io.pivotal.cf.nozzle.service.FirehoseObserver;
import io.pivotal.junit.category.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CfFirehoseClientApplication.class)
@Category(IntegrationTest.class)
public class CfFirehoseClientApplicationIntegration {

	@Autowired
	private FirehoseObserver firehoseObserver;

	@Test
	public void testObserveFirehose() throws Exception {
		firehoseObserver.observeFirehose(0).subscribe(System.out::println);
		Thread.sleep(20000);
	}
}
