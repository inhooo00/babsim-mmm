package shop.babsim.babsim.report.discord.domain;

import lombok.Getter;

@Getter
public enum DiscordMessage {
    REPORT("신고완료!")
    ;

    private final String message;

    DiscordMessage(String message) {
        this.message = message;
    }
}
