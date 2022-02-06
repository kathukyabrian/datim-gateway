package hackathon.group4.api.dto;

import lombok.Data;

@Data
public class StatsRecord {
    private Integer id;

    private Integer hospital_id;

    private String hospitalName;

    private Integer newBornNumber;

    private Integer deathNumber;

    private Integer numberOfpatientsServed;

    private Integer coronaCases;
}
