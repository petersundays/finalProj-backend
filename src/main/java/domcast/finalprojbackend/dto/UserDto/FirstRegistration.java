package domcast.finalprojbackend.dto.UserDto;

import domcast.finalprojbackend.enums.TypeOfUserEnum;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FirstRegistration {
    @XmlElement
    private String email;
    @XmlElement
    private String password;
    @XmlElement
    private final TypeOfUserEnum typeOfUser = TypeOfUserEnum.NOT_CONFIRMED;

    public FirstRegistration() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public TypeOfUserEnum getTypeOfUser() {
        return typeOfUser;
    }
}
