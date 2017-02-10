package cn.zhouchenxi.xxqp.controller;

/**
 * Created by zhouchenxi on 2017/1/5.
 */
public class CheckResult {
    private String validateMessagesShowId;


    private Boolean status;

    private int httpstatus;

    private data data;

    private Object messages;

    public String getValidateMessagesShowId() {
        return validateMessagesShowId;
    }

    public void setValidateMessagesShowId(String validateMessagesShowId) {
        this.validateMessagesShowId = validateMessagesShowId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public int getHttpstatus() {
        return httpstatus;
    }

    public void setHttpstatus(int httpstatus) {
        this.httpstatus = httpstatus;
    }

    public CheckResult.data getData() {
        return data;
    }

    public void setData(CheckResult.data data) {
        this.data = data;
    }

    public Object getMessages() {
        return messages;
    }

    public void setMessages(Object messages) {
        this.messages = messages;
    }

    public Object getValidateMessages() {
        return validateMessages;
    }

    public void setValidateMessages(Object validateMessages) {
        this.validateMessages = validateMessages;
    }

    private Object validateMessages;


    public class data{
        private int result;

        private String msg;

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

}


