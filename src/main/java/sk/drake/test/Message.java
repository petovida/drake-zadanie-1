package sk.drake.test;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class Message {

  private String id;
  private String type;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate created;

  private BigDecimal amount;
  private Integer vat;
  private BigDecimal amountWithVat;
}
