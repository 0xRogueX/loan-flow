package com.loanflow.event;

import com.loanflow.entity.OverdueTracker;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OverdueAlertEvent {

    private final OverdueTracker tracker;
}
