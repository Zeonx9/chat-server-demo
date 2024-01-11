package com.ade.chat.dtos;

import com.ade.chat.domain.UnreadCounter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link UnreadCounter}
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnreadCounterDto {
    private Long chatId;
    private Integer count;
}