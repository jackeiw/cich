package cnki.cord.zgj.cord.portal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rocketmqTest")
public class RocketmqTestController {
    @GetMapping("sendCommon")
    public String sendAsc() {
        return null;
    }
}