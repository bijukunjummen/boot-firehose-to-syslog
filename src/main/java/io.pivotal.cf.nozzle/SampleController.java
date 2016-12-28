package io.pivotal.cf.nozzle;

import io.pivotal.cf.nozzle.service.AppDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.pivotal.cf.nozzle.model.AppDetail;
import io.pivotal.cf.nozzle.service.AppDetailsCachingService;

@RestController
public class SampleController {

    @Autowired
    private AppDetailsService appDetailsService;

    @RequestMapping("/sampleAppDetail")
    @ResponseBody
    public AppDetail appDetail() {
        AppDetail appDetail = this.appDetailsService.getApplicationDetail("1191085d-f7aa-46d4-901c-d81e293f2a5f").block();
        return appDetail;
    }
}
