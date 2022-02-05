package hackathon.group4.api.resource;

import hackathon.group4.api.dto.PatientRecordDTO;
import hackathon.group4.api.service.APIService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gateway")
public class APIResource {

    private final APIService apiService;

    public APIResource(APIService apiService) {
        this.apiService = apiService;
    }

    @PostMapping("/record")
    public PatientRecordDTO forwardToDATIM(@RequestBody PatientRecordDTO patientRecordDTO){

        PatientRecordDTO fromDATIM = apiService.forward(patientRecordDTO);

        return fromDATIM;
    }

    @GetMapping("/records/failed")
    public List<PatientRecordDTO> getFailedRequests(){

        return apiService.getFailedRequests();
    }
}
