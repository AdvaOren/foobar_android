package entities;

public class Friend {
    private String requester;
    private String requested;
    private String requesterName;
    private String status;
    public static final String NOT_FRIENDS = "Add Friend";
    public static final String REQUEST_SENT = "Request Sent";
    public static final String FRIENDS = "We are friends :)";
    public static final String REQUEST_SENT_HIS_SIDE = "Request is sent to you";


    public Friend(String requester, String requested, String status, String requesterName) {
        this.requester = requester;
        this.requested = requested;
        this.status = status;
        this.requesterName = requesterName;
    }

    public Friend(String requester, String requested) {
        this.requester = requester;
        this.requested = requested;
        this.status = "wait";
        this.requesterName = "";
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getRequested() {
        return requested;
    }

    public void setRequested(String requested) {
        this.requested = requested;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
