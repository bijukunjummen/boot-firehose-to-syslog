package io.pivotal.cf.nozzle.web;

import io.pivotal.cf.nozzle.model.AppDetail;
import io.pivotal.cf.nozzle.service.AppDetailsCachingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppDetailController {

    @Autowired
    private AppDetailsCachingService appDetailsCachingService;

    @RequestMapping("/appdetail")
    @ResponseBody
    public AppDetail appDetail() {
        return appDetailsCachingService.getApplicationDetail("dcda8166-9da5-4c39-8787-ebbf7d9b7693");
    }
}
