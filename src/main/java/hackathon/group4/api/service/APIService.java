package hackathon.group4.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hackathon.group4.api.dto.PatientRecordDTO;
import hackathon.group4.api.util.HttpUtil;
import lombok.extern.log4j.Log4j2;
import okhttp3.MediaType;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class APIService {

    private final List<PatientRecordDTO> failedRequests = new ArrayList<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    void init() {
        this.executorService.scheduleWithFixedDelay(this::resend, 15, 15, TimeUnit.SECONDS);
    }

    public PatientRecordDTO forward(PatientRecordDTO patientRecordDTO) {

        // make forward the message
        String url = "http://172.16.110.141:5000/";
        String patientRecordStr = null;
        try {
            patientRecordStr = objectMapper.writeValueAsString(patientRecordDTO);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // check for null body -> error checking
        String response = "";
        try {
            response = HttpUtil.post(url, patientRecordStr, MediaType.get("application/json"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response == "") {
            failedRequests.add(patientRecordDTO);
        }

        // catch errors

        // return the response
        PatientRecordDTO fromDATIM = new PatientRecordDTO();
        try {
            fromDATIM = objectMapper.readValue(response, PatientRecordDTO.class);
        } catch (JsonProcessingException e) {
            fromDATIM = new PatientRecordDTO();
        }

        return fromDATIM;

    }

    public List<PatientRecordDTO> getFailedRequests() {
        log.debug("Request to get all failed requests");

        return failedRequests;
    }

    public void resend() {
        log.debug("Resending {} records", this.failedRequests.size());
        if (this.failedRequests.size() > 1) {
            for (PatientRecordDTO patientRecordDTO : this.failedRequests) {
                PatientRecordDTO patientRecordDTO1 = forward(patientRecordDTO);
                if (patientRecordDTO1.getPatientId() != null) {
                    log.info("Succeeded in forwarding so removing from failed requests");
                    this.failedRequests.remove(patientRecordDTO);
                }
            }
        }

    }
}
