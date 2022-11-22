package twim.melsecplc.Melsec;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
public class MelsecController {
    public static MelsecPlcHandler plc;

    @PostConstruct
    private void start(){
        new Thread(() -> {
            try{
                log.info("Start McProtocol TCP");
                plc = new MelsecPlcHandler("localhost", 5000);
            } catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    @GetMapping("/send")
    public ResponseEntity<?> sendMessage(HttpServletRequest request){
        try{
            String data = "Hello PLC";

            plc.sendCommand(data);
            return new ResponseEntity<>("success", HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreDestroy
    private void destroy(){
        log.info("Destroy McProtocol TCP { Port: 5000 }");
    }
}
