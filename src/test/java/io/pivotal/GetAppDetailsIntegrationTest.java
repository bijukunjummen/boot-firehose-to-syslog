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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CfFirehoseClientApplication.class)
@Category(IntegrationTest.class)
public class GetAppDetailsIntegrationTest {

	@Autowired
	private AppDetailsCachingService appDetailsService;

	@Test
	public void testGetAppDetail() throws Exception {
		System.out.println("appDetailsService = " +
				this.appDetailsService.getApplicationDetail("dcda8166-9da5-4c39-8787-ebbf7d9b7693"));
	}
}
