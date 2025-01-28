package models.lombok;

import lombok.Data;

@Data
public class UpdateUserResponseLombokModel {
    private String name;      // Имя пользователя
    private String job;       // Должность пользователя
    private String updatedAt; // Время обновления
}
