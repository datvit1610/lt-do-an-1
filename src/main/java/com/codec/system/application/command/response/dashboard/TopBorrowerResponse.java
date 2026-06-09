package com.codec.system.application.command.response.dashboard;

import jakarta.persistence.Tuple;
import lombok.Data;

@Data
public class TopBorrowerResponse {
  String borrowerId;
  String fullName;
  String roleName;
  Long totalLoans;

  public TopBorrowerResponse (Tuple tuple) {
    this.borrowerId = tuple.get("borrowerId", String.class);
    this.fullName = tuple.get("fullName", String.class);
    this.roleName = tuple.get("roleName", String.class);
    this.totalLoans = tuple.get("totalLoans", Long.class);
  }
}
