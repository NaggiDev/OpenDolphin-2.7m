package open.dolphin.spring.model.domain.communication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kazushi Minagawa.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SMSMessage implements Serializable {

    private List<String> numbers;

    private String message;

    public void addNumber(String number) {
        if (numbers == null) {
            numbers = new ArrayList(1);
        }
        numbers.add(number);
    }
}
