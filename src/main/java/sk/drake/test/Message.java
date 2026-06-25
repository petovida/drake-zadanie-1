package sk.drake.test;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@XmlRootElement(name = "Message")
@XmlType(propOrder = {"id", "type", "created", "amount", "vat", "amountWithVat"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Message {

  @XmlElement(name = "Id")
  private String id;

  @XmlElement(name = "Type")
  private String type;

  @XmlElement(name = "Created")
  @JsonFormat(pattern = "yyyy-MM-dd")
  @XmlJavaTypeAdapter(LocalDateAdapter.class)
  private LocalDate created;

  @XmlElement(name = "Amount")
  private BigDecimal amount;

  @XmlElement(name = "Vat")
  private Integer vat;

  @XmlElement(name = "AmountWithVat")
  private BigDecimal amountWithVat;
}
