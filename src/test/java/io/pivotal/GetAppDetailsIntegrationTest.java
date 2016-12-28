package io.pivotal;


import io.pivotal.cf.nozzle.CfFirehoseClientApplication;
import io.pivotal.cf.nozzle.service.AppDetailsCachingService;
import io.pivotal.cf.nozzle.service.AppDetailsService;
import io.pivotal.cf.nozzle.service.FirehoseObserver;
import io.pivotal.junit.category.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CfFirehoseClientApplication.class)
@Category(IntegrationTest.class)
public class GetAppDetailsIntegrationTest {

	@Autowired
	private AppDetailsCachingService appDetailsService;

	@Test
	public void testGetAppDetail() throws Exception {
		System.out.println("appDetailsService = " +
				this.appDetailsService.getApplicationDetail("1191085d-f7aa-46d4-901c-d81e293f2a5f"));

	}
}
