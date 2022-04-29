package hello.rest.entity;

public interface Noticeable {
    Long getTargetObjectId();

    Long getTargetUserId();

    String getFormatMessage(String actionUserName, String format);

    String getFormatUrl();

    String getFormat();
}
