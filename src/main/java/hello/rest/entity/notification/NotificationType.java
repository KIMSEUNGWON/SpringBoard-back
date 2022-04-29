package hello.rest.entity.notification;

//https://velog.io/@youmakemesmile/JPA-Enum-Type-%EC%A0%81%EC%9A%A9%EA%B8%B0
public enum NotificationType implements CodeValue {
    Notice("N", "공지사항", "notice"),
    Comment("C", "댓글", "board"),
    Reply("R", "답글", "board"),
    Like("L", "좋아요", "board");

    //api/board/{boardName}/post/{postId}

    private String code;
    private String value;
    private String url;

    NotificationType(String code, String value, String url) {
        this.code = code;
        this.value = value;
        this.url = url;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getUrl() {
        return url;
    }
}
