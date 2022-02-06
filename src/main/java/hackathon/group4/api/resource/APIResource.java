package hackathon.group4.api.resource;

import hackathon.group4.api.dto.StatsRecord;
import hackathon.group4.api.dto.StatsRecordDTO;
import hackathon.group4.api.service.StatRecordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gateway")
public class APIResource {

    private final StatRecordService apiService;

    public APIResource(StatRecordService apiService) {
        this.apiService = apiService;
    }

    @PostMapping("/record")
    public StatsRecordDTO forwardToDATIM(@RequestBody StatsRecord statsRecord){

        StatsRecordDTO fromDATIM = apiService.forward(statsRecord);

        return fromDATIM;
    }

    @GetMapping("/records/failed")
    public List<StatsRecord> getFailedRequests(){

        return apiService.getFailedRequests();
    }
}
