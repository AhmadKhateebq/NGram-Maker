package com.example.thirdprojectphasetwo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Answer {
    String word;
    Double prob;

    @Override
    public String toString() {
        return
                word +
                ", prob=" + prob
                ;
    }
}
