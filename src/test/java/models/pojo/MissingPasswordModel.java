package models.pojo;

import lombok.Data;

@Data
public class MissingPasswordModel {
    String error;
    public String getError() {
        return error;
    }
}
