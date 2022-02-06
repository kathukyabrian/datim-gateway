package hackathon.group4.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hackathon.group4.api.dto.StatsRecord;
import hackathon.group4.api.dto.StatsRecordDTO;
import hackathon.group4.api.util.HttpUtil;
import lombok.extern.log4j.Log4j2;
import okhttp3.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class StatRecordService {

    private final List<StatsRecord> failedRequests = new ArrayList<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public StatsRecordDTO forward(StatsRecord statsRecord) {

        log.info("Sending to DATIM");

        // make forward the message
        String url = "http://172.16.110.141:5000/";
        String statsRecordStr = null;
        try {
            statsRecordStr = objectMapper.writeValueAsString(statsRecord);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // check for null body -> error checking
        String response = "";
        try {
            response = HttpUtil.post(url, statsRecordStr, MediaType.get("application/json"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response == "") {
            failedRequests.add(statsRecord);
        }

        log.debug("Received response : {}",response);
        // catch errors

        // return the response
        StatsRecordDTO fromDATIM = new StatsRecordDTO();
        try {
            fromDATIM = objectMapper.readValue(response, StatsRecordDTO.class);
        } catch (JsonProcessingException e) {
            fromDATIM = new StatsRecordDTO();
        }

        return fromDATIM;

    }

    public List<StatsRecord> getFailedRequests() {
        log.debug("Request to get all failed requests");

        return failedRequests;
    }

//    public void resend() {
//        log.debug("Resending {} records", this.failedRequests.size());
//        if (this.failedRequests.size() > 0) {
//            for (StatsRecord patientRecordDTO : this.failedRequests) {
//                StatsRecord patientRecordDTO1 = forward(patientRecordDTO);
//                if (patientRecordDTO1.getPatientId() != null) {
//                    log.info("Succeeded in forwarding so removing from failed requests");
//                    int index = failedRequests.indexOf(patientRecordDTO);
//                    log.info("Index  : {}",index);
//                    this.failedRequests.remove(index);
//                }
//            }
//        }
//    }
}
