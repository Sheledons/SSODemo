package cn.sheledon.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author sheledon
 */
@Setter
@Getter
@ToString
@Builder
public class ResultInfo {
    private boolean success;
    private String message;
    private Object object;
}
