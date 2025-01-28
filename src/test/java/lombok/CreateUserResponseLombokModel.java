package lombok;

@Data
public class CreateUserResponseLombokModel {
    private String name;      // Имя пользователя
    private String job;       // Должность пользователя
    private String id;        // ID пользователя (можно быть созданным)
    private String createdAt; // Время создания
}
