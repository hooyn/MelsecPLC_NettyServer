package twim.melsecplc.melsec;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Controller
@Slf4j
public class MelSecPlcController {
    public static MelSecPlcHandler plc;
    private final int PORT = 5002;
    //private final String IP_VALUE = "100.100.100.20";
    private final String IP_VALUE = "localhost";

    @PostConstruct
    private void start(){
        new Thread(() -> {
            try{
                log.info("Netty Server Open [IP: {}, Port: {}]", IP_VALUE, PORT);
                plc = new MelSecPlcHandler(IP_VALUE, PORT);
            } catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    @PreDestroy
    private void destroy(){
        log.info("Netty Server Close [IP: {}, Port: {}]", IP_VALUE, PORT);
    }

    @GetMapping("/send")
    public ResponseEntity<?> sendMessage(){
        try{
            plc.sendCommand();
            return new ResponseEntity<>("success", HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
