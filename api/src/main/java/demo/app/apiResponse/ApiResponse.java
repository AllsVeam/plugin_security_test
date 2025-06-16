package demo.app.apiResponse;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private int status;
    private String msg;
    private T object;
    private boolean success;
    private String message;
    private T data;


    public ApiResponse() {}

    public ApiResponse(int status, String msg, T object) {
        this.status = status;
        this.msg = msg;
        this.object = object;
    }


}
