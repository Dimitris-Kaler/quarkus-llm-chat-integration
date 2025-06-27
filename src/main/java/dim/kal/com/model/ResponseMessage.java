package dim.kal.com.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ResponseMessage {


    private String message;

    public ResponseMessage() {//create default constructor fro jackson
    }

    public ResponseMessage(String message){
        this.message = message;

    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
